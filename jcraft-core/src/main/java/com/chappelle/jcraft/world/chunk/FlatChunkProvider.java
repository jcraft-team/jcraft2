package com.chappelle.jcraft.world.chunk;

import com.chappelle.jcraft.blocks.Block;

public class FlatChunkProvider extends BaseChunkProvider
{
	private int height;

	public FlatChunkProvider(int height)
	{
		this.height = height;
	}

	@Override
	public void doFillChunkWithBlocks(int[][][] blockTypes, boolean[][][] blocks_IsOnSurface, Block block)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < height; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					blockTypes[x][y][z] = block.blockId;
					if(y == height - 1)
					{
						blocks_IsOnSurface[x][y][z] = true;
					}
				}
			}
		}
	}
}
