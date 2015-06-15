package com.chappelle.jcraft.world.chunk;

public interface ChunkFeatureGenerator
{
	void addFeatures(int[][][] blockTypes, boolean[][][] blocks_IsOnSurface);
}
