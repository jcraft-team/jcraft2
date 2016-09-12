package com.chappelle.jcraft.serialization;

import java.io.Serializable;

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
public interface VoxelWorldSave
{
	/**
	 * Flushs all enquened save operations in the voxel world save.
	 */
	void flushSave();

	/**
	 * Checks if the chunk for the given position exists in the save file.
	 * 
	 * @param chunkX
	 * @param chunkY
	 * @param chunkZ
	 * @return
	 */
	boolean hasChunk(int chunkX, int chunkZ);

	/**
	 * Reads the chunk for the given position from the save file. Returns null
	 * if the chunk is not found.
	 * 
	 * @param chunkX
	 * @param chunkY
	 * @param chunkZ
	 * @return
	 */
	int[][][] readChunk(int chunkX, int chunkZ);

	/**
	 * Writes the chunk for the given position to the save file. If the chunk
	 * already exists in the file it will get overwritten, otherwise it will get
	 * added.
	 * 
	 * @param chunkX
	 * @param chunkZ
	 * @return
	 */
	void writeChunk(int chunkX, int chunkZ, int[][][] data);
	
	void putGameData(String key, Serializable value);

	Serializable getGameData(String key);
	
	void closeDB();
}