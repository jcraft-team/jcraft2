package com.chappelle.jcraft.world.terrain.gen;

import java.util.Random;

import com.chappelle.jcraft.blocks.Blocks;
import com.chappelle.jcraft.world.chunk.*;

public class WaterFeature extends AbstractFeature
{
	private int waterLevel;
	private Random rand;
	
	public WaterFeature(long seed, int waterLevel)
	{
		this.waterLevel = waterLevel;
		this.rand = new Random(seed + 3);
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, byte[][][] blockTypes)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y <= waterLevel; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					if(blockTypes[x][y][z] == 0)
					{
						blockTypes[x][y][z] = Blocks.water.blockId;
					}
				}
			}
		}
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y <= waterLevel; y++)
			{
				for(int z = 0; z < 16; z++)
				{
					addSand(blockTypes, x, y, z, 1.0f);
				}
			}
		}
	}

	private void addSand(byte[][][] blockTypes, int x, int y, int z, float probability)
	{
		if(rand.nextFloat() < probability)
		{
			if(blockTypes[x][y][z] != Blocks.water.blockId)
			{
				blockTypes[x][y][z] = Blocks.sand.blockId;
			}
			
			//THIS IS REALLY SLOW
//			if(blockTypes[(x+1)&15][y][z] != Block.water.blockId)
//			{
//				addSand(blockTypes, (x+1)&15, y, z, probability*0.7f);
//			}
//			if(x > 0 && blockTypes[x-1][y][z] != Block.water.blockId)
//			{
//				addSand(blockTypes, x-1, y, z, probability*0.7f);
//			}
//			if(blockTypes[x][y][(z+1)&15] != Block.water.blockId)
//			{
//				addSand(blockTypes, x, y, (z+1)&15, probability*0.7f);
//			}
//			if(z > 0 && blockTypes[x][y][z-1] != Block.water.blockId)
//			{
//				addSand(blockTypes, x, y, z-1, probability*0.7f);
//			}
		}
	}
}