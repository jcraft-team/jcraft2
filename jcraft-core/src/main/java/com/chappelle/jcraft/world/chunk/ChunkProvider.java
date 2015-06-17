package com.chappelle.jcraft.world.chunk;

import java.util.Collection;

import com.chappelle.jcraft.world.World;

public interface ChunkProvider
{
	Chunk getChunk(int x, int z);
	Chunk generateChunk(int x, int z);
	void setWorld(World world);
	
	Collection<Chunk> getLoadedChunks();
	void removeChunk(int x, int z);
}
