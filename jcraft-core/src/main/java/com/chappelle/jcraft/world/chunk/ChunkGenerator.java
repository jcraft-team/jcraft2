package com.chappelle.jcraft.world.chunk;

public interface ChunkGenerator
{
	void generate(int x, int z, byte[][][] data);
}
