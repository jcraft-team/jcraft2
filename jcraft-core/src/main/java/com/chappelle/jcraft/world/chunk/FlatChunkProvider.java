package com.chappelle.jcraft.world.chunk;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.World;

public class FlatChunkProvider implements ChunkProvider
{
	private Map<Long, Chunk> chunks = new HashMap<Long, Chunk>();
	
	private Random rand = new Random();
	private World world;
	
	private int height;
	
	public FlatChunkProvider(int height)
	{
		this.height = height;
	}
	
	@Override
	public Chunk getChunk(int x, int z)
	{
		return chunks.get(ChunkCoordIntPair.chunkXZ2Int(x, z));
	}

	public void setWorld(World world)
	{
		this.world = world;
	}
	
	private void setBedrock(int[][][] blockTypes)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				blockTypes[x][0][z] = Block.bedrock.blockId;
			}
		}
	}
	private void fillChunkWithBlocks(int[][][] blockTypes, boolean[][][] blocks_IsOnSurface, Block block)
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
		addOres(blockTypes);
	}

	private void addOres(int[][][] blockTypes)
	{
		//Coal
		for(int i = 0; i < 6; i++)
		{
			int x = rand.nextInt(16);
			int y = rand.nextInt(height-1);
			int z = rand.nextInt(16);
			
			blockTypes[x][y][z] = Block.coal.blockId;
			addCluster(x, y, z, blockTypes, 0.25f, 0.05f, Block.coal.blockId);
		}

		//Smooth stone
		for(int i = 0; i < 10; i++)
		{
			int x = rand.nextInt(16);
			int y = rand.nextInt(height-1);
			int z = rand.nextInt(16);
			
			blockTypes[x][y][z] = Block.smoothStone.blockId;
			addCluster(x, y, z, blockTypes, 0.2f, 0.6f, Block.smoothStone.blockId);
		}

		//Gold
		for(int i = 0; i < 1; i++)
		{
			int x = rand.nextInt(16);
			int y = rand.nextInt(height-1);
			int z = rand.nextInt(16);
			
			blockTypes[x][y][z] = Block.gold.blockId;
			addCluster(x, y, z, blockTypes, 0.2f, 0.05f, Block.gold.blockId);
		}

		//Diamond
		for(int i = 0; i < 1; i++)
		{
			int x = rand.nextInt(16);
			int y = rand.nextInt(height-1);
			int z = rand.nextInt(16);
			
			blockTypes[x][y][z] = Block.diamond.blockId;
			addCluster(x, y, z, blockTypes, 0.2f, 0.05f, Block.diamond.blockId);
		}

		//Iron
		for(int i = 0; i < 5; i++)
		{
			int x = rand.nextInt(16);
			int y = rand.nextInt(height-1);
			int z = rand.nextInt(16);
			
			blockTypes[x][y][z] = Block.iron.blockId;
			addCluster(x, y, z, blockTypes, 0.3f, 0.05f, Block.iron.blockId);
		}

		//Redstone
		for(int i = 0; i < 2; i++)
		{
			int x = rand.nextInt(16);
			int y = rand.nextInt(height-1);
			int z = rand.nextInt(16);
			
			blockTypes[x][y][z] = Block.redstone.blockId;
			addCluster(x, y, z, blockTypes, 0.3f, 0.05f, Block.redstone.blockId);
		}
	}
	
	private void addCluster(int x, int y, int z, int[][][] blockTypes, float probability, float dropOff, int blockId)
	{
		int newX = (x+1)&15;
		int newY = y;
		int newZ = z;
		
		blockTypes[newX][newY][newZ] = blockId;
		if(rand.nextFloat() < probability)
		{
			addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId);
		}
		newX = x;
		newY = (y+1)&(height-1);
		newZ = z;
		
		blockTypes[newX][newY][newZ] = blockId;
		if(rand.nextFloat() < probability)
		{
			addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId);
		}

		newX = x;
		newY = y;
		newZ = (z+1)&15;
		
		blockTypes[newX][newY][newZ] = blockId;
		if(rand.nextFloat() < probability)
		{
			addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId);
		}

		newX = (x-1)&15;
		newY = y;
		newZ = z;
		
		blockTypes[newX][newY][newZ] = blockId;
		if(rand.nextFloat() < probability)
		{
			addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId);
		}
		newX = x;
		newY = (y-1)&(height-1);
		newZ = z;
		
		blockTypes[newX][newY][newZ] = blockId;
		if(rand.nextFloat() < probability)
		{
			addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId);
		}
		
		newX = x;
		newY = y;
		newZ = (z-1)&15;
		
		blockTypes[newX][newY][newZ] = Block.coal.blockId;
		if(rand.nextFloat() < probability)
		{
			addCluster(newX, newY, newZ, blockTypes, probability*dropOff, dropOff, blockId);
		}
	}

	@Override
	public Chunk generateChunk(int x, int z)
	{
		world.profiler.startSection("ChunkGen");
		int[][][] blockTypes = new int[16][256][16];
		boolean[][][] blocks_IsOnSurface = new boolean[16][256][16];
		fillChunkWithBlocks(blockTypes, blocks_IsOnSurface, Block.grass);
		setBedrock(blockTypes);
		Chunk chunk = new Chunk(world, x, z, blockTypes, blocks_IsOnSurface);
		chunks.put(ChunkCoordIntPair.chunkXZ2Int(x, z), chunk);
		world.profiler.endSection();
		return chunk;
	}

}
