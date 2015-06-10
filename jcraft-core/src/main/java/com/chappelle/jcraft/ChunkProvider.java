package com.chappelle.jcraft;

public interface ChunkProvider
{
	Chunk getChunk(int x, int z);
	Chunk generateChunk(int x, int z);
	void setWorld(World world);
}
