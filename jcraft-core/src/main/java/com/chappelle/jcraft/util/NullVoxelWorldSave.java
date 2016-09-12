package com.chappelle.jcraft.util;

import java.io.Serializable;

import com.chappelle.jcraft.serialization.VoxelWorldSave;

public class NullVoxelWorldSave implements VoxelWorldSave
{

	@Override
	public void flushSave()
	{

	}

	@Override
	public boolean hasChunk(int chunkX, int chunkZ)
	{
		return false;
	}

	@Override
	public int[][][] readChunk(int chunkX, int chunkZ)
	{
		return null;
	}

	@Override
	public void writeChunk(int chunkX, int chunkZ, int[][][] data)
	{
	}

	@Override
	public void putGameData(String key, Serializable value)
	{
	}

	@Override
	public Serializable getGameData(String key)
	{
		return null;
	}

	@Override
	public void closeDB()
	{
	}

}
