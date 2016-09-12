package com.chappelle.jcraft;

import java.util.List;

import com.chappelle.jcraft.world.chunk.Feature;

public interface FeatureProvider
{
	List<Feature> getFeatures();
	void setSeed(long seed);
}