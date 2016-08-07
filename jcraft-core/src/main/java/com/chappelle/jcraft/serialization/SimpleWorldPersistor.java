package com.chappelle.jcraft.serialization;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.*;

import org.apache.commons.io.FileUtils;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.*;

public class SimpleWorldPersistor implements WorldPersistor
{
	private static final Logger log = Logger.getLogger(SimpleWorldPersistor.class.getName());

	@Override
	public void save(World world)
	{
    	File worldSaveDir = new File(GameFiles.getSaveDir(), world.getName());
    	if(!worldSaveDir.exists())
    	{
    		worldSaveDir.mkdirs();
    	}
    	for(Chunk chunk : world.getChunkManager().getLoadedChunks())
    	{
    		writeChunkToFile(chunk, worldSaveDir);
    	}
    	
    	File worldDataFile = new File(worldSaveDir, "World.dat");
    	try
		{
			FileUtils.writeStringToFile(worldDataFile, world.getTerrainGenerator().getSeed() + "", Charset.defaultCharset());
		}
		catch(IOException e)
		{
			throw new RuntimeException("Unable to save world data for '" + world.getName() + "'", e);
		}
	}

	@Override
	public World loadWorld(BlockApplication app, String name)
	{
		File worldSaveDir = new File(GameFiles.getSaveDir(), name);
		if(!worldSaveDir.exists())
		{
			return null;
		}
		long seed = 0;
		try
		{
			String seedStr = FileUtils.readFileToString(new File(worldSaveDir, "World.dat"), Charset.defaultCharset());
			seed = Long.parseLong(seedStr);
		}
		catch(IOException e)
		{
			throw new RuntimeException("Unable to load world '" + name + "'", e);
		}
		World world = new World(app, CubesSettings.getInstance(), app.getAssetManager(), app.getCamera(), name, seed);
		ChunkManager chunkManager = world.getChunkManager();
		List<Chunk> chunks = new ArrayList<>();
		for(File file : worldSaveDir.listFiles(new ChunkFilenameFilter()))
		{
			chunks.add(loadChunkFromFile(world, file));
		}
		for(Chunk chunk : chunks)//Need to do a separate loop so weird things don't happen on chunk boundaries
		{
			chunkManager.addChunk(chunk);
		}
		return world;
	}
	
	private Chunk loadChunkFromFile(World world, File file)
	{
		try
		{
			String name = file.getName();
			name = name.replace("chunk[", "").replace("]", "").replace(".dat", "");
			String[] parts = name.split(",");
			
			Chunk chunk = new Chunk(world, Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
			BitInputStream bitInputStream = new BitInputStream(new FileInputStream(file));
			chunk.read(bitInputStream);
			return chunk;
		}
		catch(IOException e)
		{
			throw new RuntimeException("Unable to load chunk from file: " + file.getName());
		}
	}

	private static class ChunkFilenameFilter implements FilenameFilter
	{
		@Override
		public boolean accept(File dir, String name)
		{
			return name.startsWith("chunk");
		}
	}

    private static void writeChunkToFile(Chunk chunk, File saveDir)
	{
    	try
    	{
    		File file = new File(saveDir, String.format("chunk[%d,%d].dat", chunk.location.x, chunk.location.z));
    		BitOutputStream bitOutputStream = new BitOutputStream(new FileOutputStream(file));
    		chunk.write(bitOutputStream);
    		bitOutputStream.close();
    	}
    	catch(IOException e)
    	{
    		log.log(Level.SEVERE, "Unable to save chunk " + chunk.location, e);
    	}
	}
}
