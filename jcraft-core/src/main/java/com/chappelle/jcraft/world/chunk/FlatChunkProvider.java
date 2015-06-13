package com.chappelle.jcraft.world.chunk;

import java.util.HashMap;
import java.util.Map;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.Vector3Int;
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
	
	private void setBlockArea(Chunk chunk, Vector3Int size, Block block)
	{
		Vector3Int tmpLocation = new Vector3Int();
		for(int x = 0; x < size.getX(); x++)
		{
			for(int y = 0; y < size.getY(); y++)
			{
				for(int z = 0; z < size.getZ(); z++)
				{
					tmpLocation.set(x, y, z);
					chunk.setBlock(tmpLocation, block);
				}
			}
		}
	}

	@Override
	public Chunk generateChunk(int x, int z)
	{
		Chunk chunk = new Chunk(world, x, z);
		chunks.put(ChunkCoordIntPair.chunkXZ2Int(x, z), chunk);
		setBlockArea(chunk, new Vector3Int(16, height, 16), Block.grass);
		setBlockArea(chunk, new Vector3Int(16, 1, 16), Block.bedrock);
		return chunk;
	}

}
