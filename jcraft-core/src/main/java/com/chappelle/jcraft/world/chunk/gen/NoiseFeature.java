package com.chappelle.jcraft.world.chunk.gen;

import java.util.Random;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.Noise;
import com.chappelle.jcraft.world.chunk.Feature;

public class NoiseFeature implements Feature
{
	private final Random rand;
	private final float roughness;
	private int flatChunkHeight;
	
	public NoiseFeature(long seed, float roughness, int flatChunkHeight)
	{
		this.roughness = roughness;
		this.rand = new Random(seed);
		this.flatChunkHeight = flatChunkHeight;
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, int[][][] blockTypes, boolean[][][] blocks_IsOnSurface)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < flatChunkHeight; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					blockTypes[x][y][z] = Block.grass.blockId;
					if(y == flatChunkHeight - 1)
					{
						blocks_IsOnSurface[x][y][z] = true;
					}
				}
			}
		}

		int magnitude = 4;
		generateFromNoise(blockTypes, blocks_IsOnSurface, Block.grass, 0, 0, 16, 16, magnitude);
		
//		if(rand.nextInt(5) == 0)
//		{
//			magnitude = 5;
//			generateFromNoise(blockTypes, blocks_IsOnSurface, Block.smoothStone, rand.nextInt(10), rand.nextInt(10), rand.nextInt(8) + 8, rand.nextInt(8) + 8, magnitude);
//		}
	}

	private void generateFromNoise(int[][][] blockTypes, boolean[][][] blocks_IsOnSurface, Block block, int xMin, int zMin, int xMax, int zMax, int magnitude)
	{
		Noise noise = new Noise(rand, roughness, xMax, zMax);
		noise.initialise();
		float gridMinimum = noise.getMinimum();
		float gridLargestDifference = (noise.getMaximum() - gridMinimum);
		float[][] grid = noise.getGrid();
		for(int x = xMin; x < grid.length; x++)
		{
			float[] row = grid[x];
			for(int z = zMin; z < row.length; z++)
			{
				/*---Calculation of block height has been summarized to minimize the java heap---
				float gridGroundHeight = (row[z] - gridMinimum);
				float blockHeightInPercents = ((gridGroundHeight * 100) / gridLargestDifference);
				int blockHeight = ((int) ((blockHeightInPercents / 100) * size.getY())) + 1;
				---*/
				float gridGroundHeight = (row[z] - gridMinimum);
				int blockHeight = flatChunkHeight + (((int) ((((gridGroundHeight * 100) / gridLargestDifference) / 100) * magnitude)) + 1);
				for(int y = 0; y < blockHeight; y++)
				{
					if(block.blockId == Block.smoothStone.blockId)
					{
						int randVal = rand.nextInt(8);
						if(randVal == 0)
						{
							blockTypes[x][y][z] = Block.coal.blockId;
						}
						else if(randVal == 1)
						{
							blockTypes[x][y][z] = Block.gravel.blockId;
						}
						else
						{
							blockTypes[x][y][z] = block.blockId;
						}
					}
					else
					{
						blockTypes[x][y][z] = block.blockId;
					}
					if(y == blockHeight - 1)
					{
						blocks_IsOnSurface[x][y][z] = true;
					}
				}
			}
		}
	}
}
