package com.chappelle.jcraft.world.chunk;

import java.util.HashMap;
import java.util.Map;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.World;

public class FlatChunkProvider implements ChunkProvider
{
	private Map<Long, Chunk> chunks = new HashMap<Long, Chunk>();
	
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
