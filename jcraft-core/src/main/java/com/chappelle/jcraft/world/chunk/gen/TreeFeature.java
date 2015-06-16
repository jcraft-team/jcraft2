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
	public void generate(int[][][] blockTypes, boolean[][][] blocks_IsOnSurface)
	{
		for(int x = 3; x < 13; x++)
		{
			for(int y = 0; y < 240; y++)
			{
				for(int z = 3; z < 13; z++)
				{
					if(blocks_IsOnSurface[x][y][z] && blockTypes[x][y][z] == Block.grass.blockId)
					{
						if(rand.nextInt(100) == 50)
						{
							blocks_IsOnSurface[x][y][z] = false;
							generateTree(blockTypes, x, y, z);
						}
					}
				}
			}
		}
	}

	private void generateTree(int[][][] blockTypes, int x, int y, int z)
	{
		int treeHeightMin = 4;
		int treeHeightMax = 8;
		int treeHeight = rand.nextInt(treeHeightMax - treeHeightMin) + treeHeightMin;
		for(int i = 1; i <= treeHeight; i++)
		{
			blockTypes[x][y+i][z] = Block.wood.blockId;
		}
		int treeTop = y+treeHeight;
		blockTypes[x][treeTop+1][z] = Block.leaves.blockId;
		
		addLeaves(blockTypes, x, treeTop+1, z);
		addLeaves(blockTypes, x, treeTop, z);
		addLeaves(blockTypes, x, treeTop-1, z);
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
