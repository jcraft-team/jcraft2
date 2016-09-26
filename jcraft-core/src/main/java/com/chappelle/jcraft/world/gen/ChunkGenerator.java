package com.chappelle.jcraft.world.gen;

import com.chappelle.jcraft.world.chunk.Chunk;

public interface ChunkGenerator
{
	void initialize();
	Chunk generate(int x, int z);
}
