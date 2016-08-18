package com.chappelle.jcraft.serialization;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.*;

import org.mapdb.*;
import org.xerial.snappy.Snappy;

import com.chappelle.jcraft.world.chunk.ChunkCoordIntPair;

/**
 * Handles voxel world saving / loading.
 * 
 * If a region got modified the modified chunk data will get compressed and written to the region file.
 * 
 * The saving of the actual world data will get handled in flushSave().
 * So writing data to the world with writeChunk(int,int,in) will enquene data in the save stack.
 * In flushSave they will get written to the disk.
 * 
 * flushSave() should get called in a fixed interval by the server.
 * 
 * This class is completely thread-safe.
 */
public class VoxelWorldSave
{
	private static final Logger log = Logger.getLogger(VoxelWorldSave.class.getName());

	/**
	 * The database connection.
	 */
	private DB db;

	/**
	 * Locks the connection.
	 */
	private Object connectionLockObject = new Object();

	/**
	 * The queue of jobs waiting for writing.
	 */
	private LinkedList<AbstractMap.SimpleEntry<ChunkCoordIntPair, Integer[][][]>> writerQueue;

	private Object writerQueueLock = new Object();

	/**
	 * Gets initialized in constructor. Holds all chunk entry infos.
	 */
	private BTreeMap<ChunkCoordIntPair, byte[]> chunkEntries;

	private BTreeMap<String, Serializable> gameData;
	
	/**
	 * Opens or creates the table and data file.
	 * 
	 * @param worldSaveDir
	 */
	@SuppressWarnings("unchecked")
	public VoxelWorldSave(File worldSaveDir)
	{
		try
		{
			File saveFile = worldSaveDir;
			this.writerQueue = new LinkedList<>();

			// Create db connection
			this.db = DBMaker.fileDB(saveFile).closeOnJvmShutdown().make();

			// Load data
			chunkEntries = (BTreeMap<ChunkCoordIntPair, byte[]>) this.db.treeMap("chunks").createOrOpen();
			gameData = (BTreeMap<String, Serializable>) this.db.treeMap("gameData").createOrOpen();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Flushs all enquened save operations in the voxel world save.
	 */
	public void flushSave()
	{
//		int chunksSaved = 0;
		HashMap<ChunkCoordIntPair, Integer[][][]> updateJobs = new HashMap<>();

		synchronized(this.writerQueueLock)
		{
			while(!this.writerQueue.isEmpty())
			{
				// Get job from queue
				AbstractMap.SimpleEntry<ChunkCoordIntPair, Integer[][][]> queueEntry = this.writerQueue.poll();
				updateJobs.put(queueEntry.getKey(), queueEntry.getValue());
			}
		}

		// Write jobs to database
		synchronized(this.connectionLockObject)
		{
//			long startTime = System.currentTimeMillis();

			// Iterate through every update job.
			for(Entry<ChunkCoordIntPair, Integer[][][]> entry : updateJobs.entrySet())
			{
				// Get chunk position
				int chunkX = entry.getKey().chunkXPos;
				int chunkZ = entry.getKey().chunkZPos;

				// Serialize & compress chunk data
				ByteArrayOutputStream baos = new ByteArrayOutputStream(16 * 256 * 16 * 4);
				// ByteArrayOutputStream baos = new ByteArrayOutputStream();
				BitOutputStream writer = new BitOutputStream(baos);
				Integer[][][] voxelData = entry.getValue();

				for(int x = 0; x < 16; x++)
					for(int y = 0; y < 256; y++)
						for(int z = 0; z < 16; z++)
							writer.writeInteger(voxelData[x][y][z]);

				try
				{
					// Prepare chunk data
					byte[] data = Snappy.compress(baos.toByteArray());

					this.chunkEntries.put(new ChunkCoordIntPair(chunkX, chunkZ), data);
//					chunksSaved++;
				}
				catch(Exception e1)
				{
					log.log(Level.SEVERE, "Error while flushing save jobs", e1);
				}
			}

			// Commit to db
			this.db.commit();

//			System.out.println(String.format("Saved %d chunks in : %d ms", chunksSaved, (System.currentTimeMillis() - startTime)));
		}
	}

	/**
	 * Checks if the chunk for the given position exists in the save file.
	 * 
	 * @param chunkX
	 * @param chunkY
	 * @param chunkZ
	 * @return
	 */
	public boolean hasChunk(int chunkX, int chunkZ)
	{
		synchronized(this.connectionLockObject)
		{
			return this.chunkEntries.containsKey(new ChunkCoordIntPair(chunkX, chunkZ));
		}
	}

	/**
	 * Reads the chunk for the given position from the save file. Returns null
	 * if the chunk is not found.
	 * 
	 * @param chunkX
	 * @param chunkY
	 * @param chunkZ
	 * @return
	 */
	public Integer[][][] readChunk(int chunkX, int chunkZ)
	{
		synchronized(this.connectionLockObject)
		{
			try
			{
				// Get chunk data blob
				byte[] chunkData = this.chunkEntries.get(new ChunkCoordIntPair(chunkX, chunkZ));
				if(chunkData != null)
				{
					// Deserialize data
					byte[] decompressedChunkData = Snappy.uncompress(chunkData);

					// Read data
					BitInputStream reader = new BitInputStream(new ByteArrayInputStream(decompressedChunkData));

					Integer[][][] voxelData = new Integer[16][256][16];
					for(int x = 0; x < 16; x++)
						for(int y = 0; y < 256; y++)
							for(int z = 0; z < 16; z++)
								voxelData[x][y][z] = reader.readInteger();

					return voxelData;
				}
				else
				{
					return null;
				}
			}
			catch(Exception e)
			{
				log.log(Level.SEVERE, "Error reading chunk at (" + chunkX + "," + chunkZ + ")", e);
				return null;
			}
		}
	}

	/**
	 * Writes the chunk for the given position to the save file. If the chunk
	 * already exists in the file it will get overwritten, otherwise it will get
	 * added.
	 * 
	 * @param chunkX
	 * @param chunkZ
	 * @return
	 */
	public void writeChunk(int chunkX, int chunkZ, Integer[][][] data)
	{
		synchronized(this.writerQueueLock)
		{
			// Create Writer entry
			this.writerQueue.add(new AbstractMap.SimpleEntry<>(new ChunkCoordIntPair(chunkX, chunkZ), data));
		}
	}
	
	public void putGameData(String key, Serializable value)
	{
		synchronized(gameData)
		{
			this.gameData.put(key, value);
			db.commit();
		}
	}

	public Serializable getGameData(String key)
	{
		return this.gameData.get(key);
	}
	
	public void closeDB()
	{
		db.close();
	}
}