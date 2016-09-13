package com.chappelle.jcraft.world.terrain.gen;

import java.util.*;

import com.chappelle.jcraft.blocks.Blocks;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.*;
import com.chappelle.jcraft.world.gen.FeatureProvider;

public class DefaultFeatureProvider implements FeatureProvider
{
	private long seed;
	
	@Override
	public List<Feature> getFeatures()
	{
		List<Feature> features = new ArrayList<>();
		SimplexNoise.setSeed(seed);//TODO: Need to make this non-static and allow Simplex2DFeature to set it
		// test commit
//		features.add(new FlatFeature(Blocks.grass, 10));
//		terrainGenerator.addFeature(new AOTestFeature(10));
		
		
//		features.add(new Simplex3DFeature(seed, 0.01f, 0.1f, 4, 70)); 
		features.add(new Simplex2DFeature(seed,Blocks.grass.blockId).setSimplexScale(0.015f).setPersistence(0.2f).setIterations(4).setHeight(60));
		features.add(new Simplex2DFeature(seed).setSimplexScale(0.01f).setPersistence(0.12f).setIterations(4).setHeight(60));
		features.add(new Simplex2DFeature(seed).setSimplexScale(0.001f).setPersistence(0.2f).setIterations(4).setHeight(80));
		features.add(new Simplex2DFeature(seed).setSimplexScale(0.001f).setPersistence(0.09f).setIterations(4).setHeight(100));
		features.add(new BlockOreFeature(seed));
		features.add(new WaterFeature(seed, 45));
		features.add(new PlantFeature(seed));
		features.add(new TreeFeature(seed));
		
		
//		terrainGenerator.addFeature(new ShowNegativeCoordinatesAndChunkBoundariesFeature());
//		terrainGenerator.addFeature(new ChessBoardFeature());
//		terrainGenerator.addFeature(new CellNoiseFlatFeature(seed));
//		terrainGenerator.addFeature(new CellNoise2DFeature(seed));
//		terrainGenerator.addFeature(new CellNoise3DFeature(seed));
		
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
		return "core:default";
	}
}