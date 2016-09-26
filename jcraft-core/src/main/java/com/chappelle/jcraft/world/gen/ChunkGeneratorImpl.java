package com.chappelle.jcraft.world.gen;

import java.util.List;

import com.chappelle.jcraft.world.chunk.Chunk;

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
	public Chunk generate(int x, int z)
	{
		byte[][][] data = new byte[16][256][16];
		for(Feature feature : features)
		{
			feature.setSeed(seed);
			feature.generate(x, z, data);
		}
		return new Chunk(x, z, data);
	}

	@Override
	public void initialize()
	{
	}
}