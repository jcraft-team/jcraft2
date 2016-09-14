package com.chappelle.jcraft.world.terrain.gen;

import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.world.chunk.*;
import com.chappelle.jcraft.world.gen.AbstractFeature;

public class ChessBoardFeature extends AbstractFeature
{
	
	public ChessBoardFeature()
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
			        byte block = Blocks.woolBlack.blockId;
			        if(z %2 == 0)
			        {
			            if(x % 2 == 0) block = Blocks.woolWhite.blockId;
			        }
			        else
			        {
			            if(x % 2 == 1) block = Blocks.woolWhite.blockId;
			        }
					blockTypes[x][y][z] = block;
					
				}
			}
		}
	}

}
