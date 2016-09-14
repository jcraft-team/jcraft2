package com.chappelle.world.terrain.gen;

import java.util.*;

import com.chappelle.jcraft.world.gen.*;
import com.chappelle.jcraft.world.terrain.gen.*;

public class TerasologyFeatureProvider implements FeatureProvider
{
	private long seed;
	
	@Override
	public List<Feature> getFeatures()
	{
		List<Feature> features = new ArrayList<>();
		int seaLevel = 45;
		features.add(new TerasologyFeature(seaLevel, seed));
		features.add(new BlockOreFeature(seed));
		features.add(new WaterFeature(seed, seaLevel));
		features.add(new PlantFeature(seed));
		features.add(new TreeFeature(seed));

		return features;
	}

	@Override
	public void setSeed(long seed)
	{
		this.seed = seed;
	}

	@Override
	public String getId()
	{
		return "terasology:default";
	}
}