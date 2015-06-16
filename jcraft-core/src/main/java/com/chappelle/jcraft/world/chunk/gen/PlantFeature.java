package com.chappelle.jcraft.world.chunk.gen;

import java.util.Random;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.chunk.Feature;

public class PlantFeature implements Feature
{
	private Random rand;
	private int[] plants = new int[]{Block.plantRed.blockId, Block.plantYellow.blockId, Block.mushroomBrown.blockId, Block.mushroomRed.blockId, Block.tallGrass.blockId};
	
	public PlantFeature(long seed)
	{
		this.rand = new Random(seed);
	}

	@Override
	public void generate(int[][][] blockTypes, boolean[][][] blocks_IsOnSurface)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < 255; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					if(blocks_IsOnSurface[x][y][z] && blockTypes[x][y][z] == Block.grass.blockId)
					{
						if(rand.nextInt(25) == 20)
						{
							generatePlant(blockTypes, x, y, z);
						}
					}
				}
			}
		}
	}

	private void generatePlant(int[][][] blockTypes, int x, int y, int z)
	{
		blockTypes[x][y+1][z] = plants[rand.nextInt(plants.length)];
	}

}
