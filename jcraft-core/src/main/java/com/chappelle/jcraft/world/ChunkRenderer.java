package com.chappelle.jcraft.world;

import com.chappelle.jcraft.world.chunk.Chunk;

public interface ChunkRenderer
{
	void addDirtyChunk(Chunk chunk);
}
