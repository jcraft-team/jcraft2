package com.chappelle.jcraft.world.terrain.gen;

import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.world.chunk.Feature;

public class AOTestFeature implements Feature
{
	private int height;
	
	public AOTestFeature(int height)
	{
		this.height = height;
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, byte[][][] blockTypes)
	{
		if(chunkX == 0 && chunkZ == 0)
		{
			blockTypes[5][height][5] = Blocks.grass.blockId;
			blockTypes[5][height+1][5] = Blocks.grass.blockId;
			blockTypes[4][height][5] = Blocks.grass.blockId;
			blockTypes[5][height][4] = Blocks.grass.blockId;
		}
	}
}