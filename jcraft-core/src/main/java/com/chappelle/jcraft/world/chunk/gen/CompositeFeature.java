package com.chappelle.jcraft.world.chunk.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.chappelle.jcraft.world.chunk.Feature;

public class CompositeFeature implements Feature
{
	private List<Feature> features = new ArrayList<Feature>();
	
	private Random rand;
	
	public CompositeFeature()
	{
		this.rand = new Random();
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, byte[][][] blockTypes)
	{
		if(features.size() > 0)
		{
			features.get(rand.nextInt(features.size())).generate(chunkX, chunkZ, blockTypes);
		}
	}

	public CompositeFeature addFeature(Feature feature)
	{
		features.add(feature);
		return this;
	}
}
