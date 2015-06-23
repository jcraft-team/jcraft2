package com.chappelle.jcraft.world.chunk.gen;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.chunk.Feature;

public class FlatFeature implements Feature
{
	private int height;
	private Block block;
	
	public FlatFeature(Block block, int height)
	{
		this.height = height;
		this.block = block;
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, int[][][] blockTypes, int[][] heightMap)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < height; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					heightMap[x][z] = height-1;
					if(y == 0)
					{
						blockTypes[x][y][z] = Block.bedrock.blockId;
					}
					else
					{
						blockTypes[x][y][z] = block.blockId;
					}
				}
			}
		}
	}

}
