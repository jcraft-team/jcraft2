package com.chappelle.jcraft.world.chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.World;

public abstract class BaseChunkProvider implements ChunkProvider
{
	private Map<Long, Chunk> chunks = new HashMap<Long, Chunk>();
	
	private World world;
	
	private List<ChunkFeatureGenerator> featureGenerators = new ArrayList<ChunkFeatureGenerator>();
	
	@Override
	public Chunk getChunk(int x, int z)
	{
		return chunks.get(ChunkCoordIntPair.chunkXZ2Int(x, z));
	}

	public void setWorld(World world)
	{
		this.world = world;
	}
	
	protected void setBedrock(int[][][] blockTypes)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				blockTypes[x][0][z] = Block.bedrock.blockId;
			}
		}
	}
	
	protected abstract void doFillChunkWithBlocks(int[][][] blockTypes, boolean[][][] blocks_IsOnSurface, Block block);
	
	protected void fillChunkWithBlocks(int[][][] blockTypes, boolean[][][] blocks_IsOnSurface, Block block)
	{
		doFillChunkWithBlocks(blockTypes, blocks_IsOnSurface, block);

		for(ChunkFeatureGenerator gen : featureGenerators)
		{
			gen.addFeatures(blockTypes);
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

	public ChunkProvider addFeatureGenerator(ChunkFeatureGenerator featureGenerator)
	{
		this.featureGenerators.add(featureGenerator);
		return this;
	}
}
