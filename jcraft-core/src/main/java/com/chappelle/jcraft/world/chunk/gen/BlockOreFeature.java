package com.chappelle.jcraft.world.chunk.gen;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.chunk.Feature;

public class BlockOreFeature implements Feature
{
	private Random rand;
	private int height;
	private Map<Integer, BlockGenConfig> blockGenConfigs = new HashMap<Integer, BlockGenConfig>();
	
	public BlockOreFeature(long seed, int height)
	{
		this.height = height;
		this.rand = new Random(seed);
		
		BlockGenConfig coalConfig = new BlockGenConfig();
		coalConfig.setMinCluster((int)(height*0.05)).setMaxCluster((int)(height*0.2)).setInitialClusterProbability(0.25f).setClusterProbabilityDropOff(0.05f);
		blockGenConfigs.put(Block.coal.blockId, coalConfig);

		BlockGenConfig gravelConfig = new BlockGenConfig();
		gravelConfig.setMinCluster((int)(height*0.05)).setMaxCluster((int)(height*0.2)).setInitialClusterProbability(0.25f).setClusterProbabilityDropOff(0.05f);
		blockGenConfigs.put(Block.gravel.blockId, gravelConfig);
		
		BlockGenConfig smoothStoneConfig = new BlockGenConfig();
		smoothStoneConfig.setMinCluster((int)(height*0.2)).setMaxCluster((int)(height*0.5)).setInitialClusterProbability(0.4f).setClusterProbabilityDropOff(0.6f);
		blockGenConfigs.put(Block.smoothStone.blockId, smoothStoneConfig);

		BlockGenConfig goldConfig = new BlockGenConfig();
		goldConfig.setMinCluster((int)(height*0.01)).setMaxCluster((int)(height*0.1)).setInitialClusterProbability(0.2f).setClusterProbabilityDropOff(0.05f);
		blockGenConfigs.put(Block.gold.blockId, goldConfig);

		BlockGenConfig diamondConfig = new BlockGenConfig();
		diamondConfig.setMinCluster((int)(height*0.01)).setMaxCluster((int)(height*0.05)).setInitialClusterProbability(0.1f).setClusterProbabilityDropOff(0.05f);
		blockGenConfigs.put(Block.diamond.blockId, diamondConfig);

		BlockGenConfig ironConfig = new BlockGenConfig();
		ironConfig.setMinCluster((int)(height*0.05)).setMaxCluster((int)(height*0.3)).setInitialClusterProbability(0.4f).setClusterProbabilityDropOff(0.05f);
		blockGenConfigs.put(Block.iron.blockId, ironConfig);

		BlockGenConfig redstoneConfig = new BlockGenConfig();
		redstoneConfig.setMinCluster((int)(height*0.05)).setMaxCluster((int)(height*0.15)).setInitialClusterProbability(0.3f).setClusterProbabilityDropOff(0.05f);
		blockGenConfigs.put(Block.redstone.blockId, redstoneConfig);
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, int[][][] blockTypes, boolean[][][] blocks_IsOnSurface)
	{
		for(Map.Entry<Integer, BlockGenConfig> entry : blockGenConfigs.entrySet())
		{
			int blockId = entry.getKey();
			BlockGenConfig config = entry.getValue();
			
			int clusterCount = rand.nextInt(config.getMaxCluster() - config.getMinCluster()) + config.getMinCluster();
			for(int i = 0; i < clusterCount; i++)
			{
				int x = rand.nextInt(16);
				int y = rand.nextInt(height-1 - config.getSurfaceOffset());
				int z = rand.nextInt(16);
				
				blockTypes[x][y][z] = blockId;
				addCluster(x, y, z, blockTypes, config.getInitialClusterProbability(), config.getClusterProbabilityDropOff(), blockId, config.getSurfaceOffset());
			}
		}
	}
	
	private void addCluster(int x, int y, int z, int[][][] blockTypes, float probability, float dropOff, int blockId, int surfaceOffset)
	{
		int newX = (x+1)&15;
		int newY = y;
		int newZ = z;
		blockTypes[newX][newY][newZ] = blockId;
		if(rand.nextFloat() < probability)
		{
			addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId, surfaceOffset);
		}

		newX = x;
		newY = (y+1)&(height-1-surfaceOffset);
		newZ = z;
		blockTypes[newX][newY][newZ] = blockId;
		if(rand.nextFloat() < probability)
		{
			addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId, surfaceOffset);
		}

		newX = x;
		newY = y;
		newZ = (z+1)&15;
		blockTypes[newX][newY][newZ] = blockId;
		if(rand.nextFloat() < probability)
		{
			addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId, surfaceOffset);
		}

		newX = (x-1)&15;
		newY = y;
		newZ = z;
		blockTypes[newX][newY][newZ] = blockId;
		if(rand.nextFloat() < probability)
		{
			addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId, surfaceOffset);
		}

		newX = x;
		newY = (y-1) < 0 ? 0 : (y-1);
		newZ = z;
		blockTypes[newX][newY][newZ] = blockId;
		if(rand.nextFloat() < probability)
		{
			addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId, surfaceOffset);
		}
		
		newX = x;
		newY = y;
		newZ = (z-1)&15;
		blockTypes[newX][newY][newZ] = Block.coal.blockId;
		if(rand.nextFloat() < probability)
		{
			addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId, surfaceOffset);
		}
	}

}
