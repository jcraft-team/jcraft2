package com.chappelle.jcraft.world.chunk.gen;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.chunk.Feature;

public class ShowNegativeCoordinatesAndChunkBoundariesFeature implements Feature
{
	
	public ShowNegativeCoordinatesAndChunkBoundariesFeature()
	{
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, byte[][][] blockTypes)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < 16; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					byte block = Block.woolWhite.blockId;
					if(z == 15 || z == -15 || z == 0 || x == 0 || x == 15 || x == -15)
					{
						block = Block.woolBlack.blockId;
					}
					else if(chunkX < 0 || chunkZ < 0)
					{
						block = Block.woolBlue.blockId;
					}
					blockTypes[x][y][z] = block;
				}
			}
		}
	}

}
