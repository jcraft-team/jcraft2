package com.chappelle.jcraft.world.gen;

public interface ChunkGenerator
{
	void generate(int x, int z, byte[][][] data);
}
