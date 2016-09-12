package com.chappelle.jcraft;

import java.util.*;

import com.chappelle.jcraft.world.chunk.Feature;

public class ChunkGeneratorImpl implements ChunkGenerator
{
	private final List<Feature> features;
	private final long seed;
	
	public ChunkGeneratorImpl(long seed, List<Feature> features)
	{
		this.seed = seed;
		this.features = features;
	}
	
	@Override
	public void generate(int x, int z, byte[][][] data)
	{
		for(Feature feature : features)
		{
			feature.setSeed(seed);
			feature.generate(x, z, data);
		}
	}
}