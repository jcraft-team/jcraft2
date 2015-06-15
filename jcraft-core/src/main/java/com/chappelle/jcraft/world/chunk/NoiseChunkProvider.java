package com.chappelle.jcraft.world.chunk;

import java.util.Random;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.Noise;

public class NoiseChunkProvider extends BaseChunkProvider
{
	private final Random rand;
	private final float roughness;
	private int flatChunkHeight;
	
	public NoiseChunkProvider(long seed, float roughness, int flatChunkHeight)
	{
		this.roughness = roughness;
		this.rand = new Random(seed);
		this.flatChunkHeight = flatChunkHeight;
	}
	
	@Override
	protected void doFillChunkWithBlocks(int[][][] blockTypes, boolean[][][] blocks_IsOnSurface, Block block)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < flatChunkHeight; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					blockTypes[x][y][z] = block.blockId;
					if(y == flatChunkHeight - 1)
					{
						blocks_IsOnSurface[x][y][z] = true;
					}
				}
			}
		}

		Noise noise = new Noise(rand, roughness, 16, 16);
		noise.initialise();
		float gridMinimum = noise.getMinimum();
		float gridLargestDifference = (noise.getMaximum() - gridMinimum);
		float[][] grid = noise.getGrid();
		for(int x = 0; x < grid.length; x++)
		{
			float[] row = grid[x];
			for(int z = 0; z < row.length; z++)
			{
				/*---Calculation of block height has been summarized to minimize the java heap---
				float gridGroundHeight = (row[z] - gridMinimum);
				float blockHeightInPercents = ((gridGroundHeight * 100) / gridLargestDifference);
				int blockHeight = ((int) ((blockHeightInPercents / 100) * size.getY())) + 1;
				---*/
				float gridGroundHeight = (row[z] - gridMinimum);
				int blockHeight = flatChunkHeight + (((int) ((((gridGroundHeight * 100) / gridLargestDifference) / 100) * 5)) + 1);
				for(int y = 0; y < blockHeight; y++)
				{
					blockTypes[x][y][z] = block.blockId;
					if(y == blockHeight - 1)
					{
						blocks_IsOnSurface[x][y][z] = true;
					}
				}
			}
		}
	}
}
