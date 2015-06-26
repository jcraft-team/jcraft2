package com.chappelle.jcraft.world.chunk.gen;

import java.util.Random;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.chunk.Feature;

public class BlockOreFeature implements Feature
{
	private Random rand;
	
	public BlockOreFeature(long seed)
	{
		this.rand = new Random(seed);
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, int[][][] blockTypes)
	{
		for(int y = 0; y < 256; y++)
		{
			int x = rand.nextInt(16);
			int z = rand.nextInt(16);
			
			if(y < 12)
			{
				addCluster(x, y, z, blockTypes, 0.1f, 0.05f, Block.diamond.blockId);
			}
			
			if(y < 50)
			{
				x = rand.nextInt(16);
				z = rand.nextInt(16);
				addCluster(x, y, z, blockTypes, 0.2f, 0.05f, Block.iron.blockId);
			}
			if(y < 40)
			{
				x = rand.nextInt(16);
				z = rand.nextInt(16);
				addCluster(x, y, z, blockTypes, 0.3f, 0.05f, Block.redstone.blockId);

				x = rand.nextInt(16);
				z = rand.nextInt(16);
				addCluster(x, y, z, blockTypes, 0.4f, 0.05f, Block.iron.blockId);
			}
			
			x = rand.nextInt(16);
			z = rand.nextInt(16);
			addCluster(x, y, z, blockTypes, 0.4f, 0.08f, Block.smoothStone.blockId);
			
			x = rand.nextInt(16);
			z = rand.nextInt(16);
			addCluster(x, y, z, blockTypes, 0.4f, 0.08f, Block.coal.blockId);

			x = rand.nextInt(16);
			z = rand.nextInt(16);
			addCluster(x, y, z, blockTypes, 0.4f, 0.08f, Block.gravel.blockId);
		}
	}
	
	private void addCluster(int x, int y, int z, int[][][] blockTypes, float probability, float dropOff, int blockId)
	{
		int newX = (x+1)&15;
		int newY = y;
		int newZ = z;
		if(canPlaceOre(blockTypes, newX, newY, newZ))
		{
			blockTypes[newX][newY][newZ] = blockId;
			if(rand.nextFloat() < probability)
			{
				addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId);
			}
		}

		newX = x;
		newY = (y+1)&255;
		newZ = z;
		if(canPlaceOre(blockTypes, newX, newY, newZ))
		{
			blockTypes[newX][newY][newZ] = blockId;
			if(rand.nextFloat() < probability)
			{
				addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId);
			}
		}

		newX = x;
		newY = y;
		newZ = (z+1)&15;
		if(canPlaceOre(blockTypes, newX, newY, newZ))
		{
			blockTypes[newX][newY][newZ] = blockId;
			if(rand.nextFloat() < probability)
			{
				addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId);
			}
		}

		newX = (x-1)&15;
		newY = y;
		newZ = z;
		if(canPlaceOre(blockTypes, newX, newY, newZ))
		{
			blockTypes[newX][newY][newZ] = blockId;
			if(rand.nextFloat() < probability)
			{
				addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId);
			}
		}

		newX = x;
		newY = (y-1) < 0 ? 0 : (y-1);
		newZ = z;
		if(canPlaceOre(blockTypes, newX, newY, newZ))
		{
			blockTypes[newX][newY][newZ] = blockId;
			if(rand.nextFloat() < probability)
			{
				addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId);
			}
		}
		
		newX = x;
		newY = y;
		newZ = (z-1)&15;
		if(canPlaceOre(blockTypes, newX, newY, newZ))
		{
			blockTypes[newX][newY][newZ] = blockId;
			if(rand.nextFloat() < probability)
			{
				addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId);
			}
		}
	}

	private boolean canPlaceOre(int[][][] blockTypes, int x, int y, int z)
	{
		return blockTypes[x][y][z] != 0 && blockTypes[x][y+1][z] != 0 && (blockTypes[x][y][z] == Block.grass.blockId || blockTypes[x][y][z] == Block.smoothStone.blockId || blockTypes[x][y][z] == Block.gravel.blockId);
	}

}
