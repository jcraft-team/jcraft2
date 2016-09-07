package com.chappelle.world.terrain.gen;

import com.chappelle.jcraft.WorldInitializer;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.ChunkManager;
import com.chappelle.jcraft.world.terrain.gen.*;

public class TerasologyWorldInitializer implements WorldInitializer
{

	@Override
	public void configureWorld(World world)
	{
		ChunkManager terrainGenerator = world.getChunkManager();

		long seed = world.getSeed();

		int seaLevel = 45;
		terrainGenerator.addFeature(new TerasologyFeature(seaLevel, seed));
		terrainGenerator.addFeature(new BlockOreFeature(seed));
		terrainGenerator.addFeature(new WaterFeature(seed, seaLevel));
		terrainGenerator.addFeature(new PlantFeature(seed));
		terrainGenerator.addFeature(new TreeFeature(seed));

	}

}
