package com.chappelle.jcraft.world.chunk;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.gen.*;

public class TerrainGeneratorFactory
{
	public static TerrainGenerator makeTerrainGenerator(World world, ChunkManager chunkManager, long seed)
	{
		TerrainGenerator terrainGenerator = new TerrainGenerator(world, chunkManager, seed);
		SimplexNoise.setSeed(seed);//TODO: Need to make this non-static and allow Simplex2DFeature to set it
		// test commit
//		terrainGenerator.addFeature(new FlatFeature(Block.wood, 10));
//		terrainGenerator.addFeature(new AOTestFeature(10));
		
		
		terrainGenerator.addFeature(new Simplex3DFeature(seed, 0.01f, 0.1f, 4, 70)); 
		terrainGenerator.addFeature(new Simplex2DFeature(seed,Block.grass.blockId).setSimplexScale(0.015f).setPersistence(0.2f).setIterations(4).setHeight(60));
		terrainGenerator.addFeature(new Simplex2DFeature(seed).setSimplexScale(0.01f).setPersistence(0.12f).setIterations(4).setHeight(60));
		terrainGenerator.addFeature(new Simplex2DFeature(seed).setSimplexScale(0.001f).setPersistence(0.2f).setIterations(4).setHeight(80));
		terrainGenerator.addFeature(new Simplex2DFeature(seed).setSimplexScale(0.001f).setPersistence(0.09f).setIterations(4).setHeight(100));
		terrainGenerator.addFeature(new BlockOreFeature(seed));
		terrainGenerator.addFeature(new WaterFeature(45));
		terrainGenerator.addFeature(new PlantFeature(seed));
		terrainGenerator.addFeature(new TreeFeature(seed));
		
		
//		terrainGenerator.addFeature(new ShowNegativeCoordinatesAndChunkBoundariesFeature());
//		terrainGenerator.addFeature(new ChessBoardFeature());
//		terrainGenerator.addFeature(new CellNoiseFlatFeature(seed));
//		terrainGenerator.addFeature(new CellNoise2DFeature(seed));
//		terrainGenerator.addFeature(new CellNoise3DFeature(seed));

		return terrainGenerator;
	}
}
