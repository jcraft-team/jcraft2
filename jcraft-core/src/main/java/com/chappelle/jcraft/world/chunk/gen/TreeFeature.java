package com.chappelle.jcraft.world.chunk.gen;

import java.util.Random;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.chunk.Feature;

public class TreeFeature implements Feature
{
	private Random rand;
	
	public TreeFeature(long seed)
	{
		this.rand = new Random(seed);
	}

	@Override
	public void generate(int chunkX, int chunkZ, int[][][] blockTypes, int[][] heightMap)
	{
		for(int x = 3; x < 13; x++)
		{
			for(int y = 0; y < 240; y++)
			{
				for(int z = 3; z < 13; z++)
				{
					if(rand.nextInt(100) == 50)
					{
						int treeHeightMin = 4;
						int treeHeightMax = 8;
						int treeHeight = rand.nextInt(treeHeightMax - treeHeightMin) + treeHeightMin;
						if(canPlaceTree(blockTypes, x, y, z, treeHeight))
						{
							heightMap[x][z] = generateTree(blockTypes, x, y, z, treeHeight);
						}
					}
				}
			}
		}
	}

	private boolean canPlaceTree(int[][][] blockTypes, int x, int y, int z, int treeHeight)
	{
		if(blockTypes[x][y][z] != Block.grass.blockId)
		{
			return false;
		}
		for(int i = y+1; i < y+treeHeight; i++)
		{
			if(blockTypes[x][i][z] != 0)
			{
				return false;
			}
		}
		return true;
	}

	private int generateTree(int[][][] blockTypes, int x, int y, int z, int treeHeight)
	{
		for(int i = 1; i <= treeHeight; i++)
		{
			blockTypes[x][y+i][z] = Block.wood.blockId;
		}
		int treeTop = y+treeHeight;
		blockTypes[x][treeTop+1][z] = Block.leaves.blockId;
		
		addLeaves(blockTypes, x, treeTop+1, z);
		addLeaves(blockTypes, x, treeTop, z);
		addLeaves(blockTypes, x, treeTop-1, z);
		return treeTop+1;
	}
	
	private void addLeaves(int[][][] blockTypes, int x, int y, int z)
	{
		blockTypes[x][y][z+1] = Block.leaves.blockId;
		blockTypes[x+1][y][z] = Block.leaves.blockId;
		blockTypes[x][y][z-1] = Block.leaves.blockId;
		blockTypes[x-1][y][z] = Block.leaves.blockId;
		blockTypes[x+1][y][z+1] = Block.leaves.blockId;
		blockTypes[x-1][y][z-1] = Block.leaves.blockId;
		blockTypes[x+1][y][z-1] = Block.leaves.blockId;
		blockTypes[x-1][y][z+1] = Block.leaves.blockId;
	}

}
