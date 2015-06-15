package com.chappelle.jcraft.world.chunk.gen;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.chunk.ChunkFeatureGenerator;

public class FlatFeatureGenerator implements ChunkFeatureGenerator
{
	private int height;
	private Block block;
	
	public FlatFeatureGenerator(Block block, int height)
	{
		this.height = height;
		this.block = block;
	}
	
	@Override
	public void addFeatures(int[][][] blockTypes, boolean[][][] blocks_IsOnSurface)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < height; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					if(y == 0)
					{
						blockTypes[x][y][z] = Block.bedrock.blockId;
					}
					else
					{
						blockTypes[x][y][z] = block.blockId;
					}
					if(y == height - 1)
					{
						blocks_IsOnSurface[x][y][z] = true;
					}
				}
			}
		}
	}

}
