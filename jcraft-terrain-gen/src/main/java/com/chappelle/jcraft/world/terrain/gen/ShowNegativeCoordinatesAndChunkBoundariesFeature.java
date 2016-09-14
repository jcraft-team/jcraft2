package com.chappelle.jcraft.world.terrain.gen;

import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.world.chunk.*;
import com.chappelle.jcraft.world.gen.AbstractFeature;

public class ShowNegativeCoordinatesAndChunkBoundariesFeature extends AbstractFeature
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
					byte block = Blocks.woolWhite.blockId;
					if(z == 15 || z == -15 || z == 0 || x == 0 || x == 15 || x == -15)
					{
						block = Blocks.woolBlack.blockId;
					}
					else if(chunkX < 0 || chunkZ < 0)
					{
						block = Blocks.woolBlue.blockId;
					}
					blockTypes[x][y][z] = block;
				}
			}
		}
	}

}
