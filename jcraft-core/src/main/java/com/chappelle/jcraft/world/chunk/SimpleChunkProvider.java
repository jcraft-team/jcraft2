package com.chappelle.jcraft.world.chunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.chappelle.jcraft.world.World;

public class SimpleChunkProvider implements ChunkProvider
{
	private Map<Long, Chunk> chunks = new ConcurrentHashMap<Long, Chunk>();
	
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
		Chunk chunk = chunks.remove(ChunkCoordIntPair.chunkXZ2Int(x, z));
		if(chunk != null)
		{
			chunk.destroy();
		}
	}

	public void setWorld(World world)
	{
		this.world = world;
	}
	
	@Override
	public Chunk generateChunk(int x, int z)
	{
		world.profiler.startSection("ChunkGen");
		byte[][][] blockTypes = new byte[16][256][16];
		for(Feature gen : features)
		{
			gen.generate(x, z, blockTypes);
		}
		Chunk chunk = new Chunk(world, x, z, blockTypes);
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
