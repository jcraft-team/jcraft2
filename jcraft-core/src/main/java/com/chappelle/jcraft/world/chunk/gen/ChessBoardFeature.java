package com.chappelle.jcraft.world.chunk.gen;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.chunk.Feature;

public class ChessBoardFeature implements Feature
{
	
	public ChessBoardFeature()
	{
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, int[][][] blockTypes)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < 16; y++)
			{
				for(int z = 0; z < 16; z++)
				{
			        int block = Block.woolBlack.blockId;
			        if(z %2 == 0)
			        {
			            if(x % 2 == 0) block = Block.woolWhite.blockId;
			        }
			        else
			        {
			            if(x % 2 == 1) block = Block.woolWhite.blockId;
			        }
					blockTypes[x][y][z] = block;
					
				}
			}
		}
	}

}
