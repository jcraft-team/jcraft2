package com.chappelle.jcraft.world.gen;

import java.util.List;

import com.chappelle.jcraft.world.chunk.Feature;

public interface FeatureProvider
{
	String getId();
	List<Feature> getFeatures();
	void setSeed(long seed);
}