package com.chappelle.jcraft.world.chunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chappelle.jcraft.world.World;

public class SimpleChunkProvider implements ChunkProvider
{
	private Map<Long, Chunk> chunks = new HashMap<Long, Chunk>();
	
	private World world;
	
	private List<Feature> features = new ArrayList<Feature>();
	
	@Override
	public Chunk getChunk(int x, int z)
	{
		return chunks.get(ChunkCoordIntPair.chunkXZ2Int(x, z));
	}

	@Override
	public Collection<Chunk> getLoadedChunks()
	{
		return new ArrayList<Chunk>(chunks.values());
	}

	@Override
	public void removeChunk(int x, int z)
	{
		chunks.remove(ChunkCoordIntPair.chunkXZ2Int(x, z));
	}

	public void setWorld(World world)
	{
		this.world = world;
	}
	
	@Override
	public Chunk generateChunk(int x, int z)
	{
		world.profiler.startSection("ChunkGen");
		int[][][] blockTypes = new int[16][256][16];
		int[][] heightMap = new int[16][16];
		for(Feature gen : features)
		{
			gen.generate(x, z, blockTypes, heightMap);
		}
		Chunk chunk = new Chunk(world, x, z, blockTypes, heightMap);
		chunks.put(ChunkCoordIntPair.chunkXZ2Int(x, z), chunk);
		world.profiler.endSection();
		return chunk;
	}

	public ChunkProvider addFeature(Feature featureGenerator)
	{
		this.features.add(featureGenerator);
		return this;
	}
}
