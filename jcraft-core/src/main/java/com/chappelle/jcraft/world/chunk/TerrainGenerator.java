package com.chappelle.jcraft.world.chunk;

import java.util.ArrayList;
import java.util.List;

import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.util.MathUtils;
import com.chappelle.jcraft.world.World;
import com.jamonapi.*;

public class TerrainGenerator
{
	private ChunkManager chunkManager;
	private World world;
	
	private List<Feature> features = new ArrayList<Feature>();
	private GeneratorThread gen;
	
	public TerrainGenerator(World world, ChunkManager chunkManager)
	{
		this.world = world;
		this.chunkManager = chunkManager;
	}
	
	public void generateTerrainAroundPlayer(double playerX, double playerZ, int radius)
	{
		int chunkX = MathUtils.floor_double(playerX)/16;
		int chunkZ = MathUtils.floor_double(playerZ)/16;
		Chunk playerChunk = world.getChunkFromChunkCoordinates(chunkX, chunkZ);
		if(playerChunk == null)
		{
			generateTerrain(chunkX, chunkZ);
		}
		else
		{
//			for(Vector3Int location : playerChunk.getMissingChunkNeighborhoodLocations(radius))
//			{
//				generateTerrain(location.x, location.z);
//			}
////
			if(gen == null || !gen.running)
			{
				new Thread(gen = new GeneratorThread(playerChunk, radius), "TerrainGen").start();
			}
		}
	}
	
	public void generateTerrain(int x, int z)
	{
		byte[][][] blockTypes = new byte[16][256][16];
		for(Feature gen : features)
		{
			Monitor mon = MonitorFactory.start("Gen Feature " + gen.getClass().getSimpleName());
			gen.generate(x, z, blockTypes);
			mon.stop();
		}
		Chunk chunk = new Chunk(world, x, z, blockTypes);
		chunkManager.addChunk(chunk);
	}

	private class GeneratorThread implements Runnable
	{
		public boolean running;
		private Chunk playerChunk;
		
		private int radius;
		public GeneratorThread(Chunk playerChunk, int radius)
		{
			this.playerChunk = playerChunk;
			this.radius = radius;
		}
		
		@Override
		public void run()
		{
			running = true;
			try
			{
				for(Vector3Int location : playerChunk.getMissingChunkNeighborhoodLocations(radius))
				{
					generateTerrain(location.x, location.z);
				}
			}
			finally
			{
				running = false;
			}
		}
	}
	
	public TerrainGenerator addFeature(Feature featureGenerator)
	{
		this.features.add(featureGenerator);
		return this;
	}
}