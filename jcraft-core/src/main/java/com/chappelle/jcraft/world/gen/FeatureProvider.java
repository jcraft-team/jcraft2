package com.chappelle.jcraft.world.gen;

import java.util.List;

public interface FeatureProvider
{
	String getId();
	List<Feature> getFeatures();
	void setSeed(long seed);
}