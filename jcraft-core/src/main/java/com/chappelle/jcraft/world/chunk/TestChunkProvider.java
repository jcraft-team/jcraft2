package com.chappelle.jcraft.world.chunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.World;

public class TestChunkProvider implements ChunkProvider
{
	private Map<Long, Chunk> chunks = new HashMap<Long, Chunk>();
	
	private World world;
	
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
		setBlockArea(chunk, new Vector3Int(16, 10, 16), Block.grass);
		
		chunk.setBlock(6, 10, 4, Block.grass);
		chunk.setBlock(7, 10, 4, Block.grass);
		chunk.setBlock(7, 10, 5, Block.grass);
		chunk.setBlock(7, 10, 6, Block.grass);
		chunk.setBlock(7, 10, 7, Block.grass);
		chunk.setBlock(7, 10, 8, Block.grass);
		chunk.setBlock(7, 11, 4, Block.grass);
		chunk.setBlock(7, 11, 5, Block.grass);
		chunk.setBlock(7, 11, 5, Block.grass);
		chunk.setBlock(7, 12, 4, Block.grass);
		chunk.setBlock(7, 11, 6, Block.grass);
		chunk.setBlock(7, 13, 4, Block.grass);
		chunk.setBlock(7, 13, 5, Block.grass);
		chunk.setBlock(7, 13, 6, Block.grass);
		chunk.setBlock(7, 13, 7, Block.grass);
		chunk.setBlock(7, 12, 5, Block.grass);
		chunk.setBlock(7, 13, 6, Block.grass);
		chunk.setBlock(7, 13, 7, Block.grass);
		chunk.setBlock(7, 12, 5, Block.grass);
		chunk.setBlock(7, 12, 6, Block.grass);
		chunk.setBlock(7, 12, 7, Block.grass);
		chunk.setBlock(7, 11, 7, Block.grass);
		chunk.setBlock(7, 11, 8, Block.grass);
		chunk.setBlock(7, 12, 8, Block.grass);
		chunk.setBlock(7, 13, 8, Block.grass);
		chunk.setBlock(6, 13, 8, Block.grass);
		chunk.setBlock(6, 13, 7, Block.grass);
		chunk.setBlock(6, 13, 6, Block.grass);
		chunk.setBlock(6, 13, 5, Block.grass);
		chunk.setBlock(6, 13, 4, Block.grass);
		chunk.setBlock(6, 12, 4, Block.grass);
		chunk.setBlock(6, 11, 4, Block.grass);
		chunk.setBlock(6, 12, 8, Block.grass);
		chunk.setBlock(6, 11, 8, Block.grass);
		chunk.setBlock(6, 10, 8, Block.grass);
		chunk.setBlock(5, 13, 8, Block.grass);
		chunk.setBlock(5, 13, 7, Block.grass);
		chunk.setBlock(5, 13, 6, Block.grass);
		chunk.setBlock(5, 13, 5, Block.grass);
		chunk.setBlock(5, 13, 4, Block.grass);
		chunk.setBlock(5, 12, 4, Block.grass);
		chunk.setBlock(5, 11, 4, Block.grass);
		chunk.setBlock(5, 10, 4, Block.grass);
		chunk.setBlock(5, 10, 8, Block.grass);
		chunk.setBlock(5, 11, 8, Block.grass);
		chunk.setBlock(5, 12, 8, Block.grass);
		chunk.setBlock(4, 13, 4, Block.grass);
		chunk.setBlock(4, 12, 4, Block.grass);
		chunk.setBlock(4, 11, 4, Block.grass);
		chunk.setBlock(4, 10, 4, Block.grass);
		chunk.setBlock(4, 13, 5, Block.grass);
		chunk.setBlock(4, 13, 6, Block.grass);
		chunk.setBlock(4, 13, 7, Block.grass);
		chunk.setBlock(4, 13, 8, Block.grass);
		chunk.setBlock(4, 12, 8, Block.grass);
		chunk.setBlock(4, 11, 8, Block.grass);
		chunk.setBlock(4, 10, 8, Block.grass);
		chunk.setBlock(4, 12, 7, Block.grass);
		chunk.setBlock(4, 11, 7, Block.grass);
		chunk.setBlock(4, 10, 7, Block.grass);
		chunk.setBlock(4, 10, 5, Block.grass);
		chunk.setBlock(4, 11, 5, Block.grass);
		chunk.setBlock(4, 12, 5, Block.grass);
		
		chunk.setBlock(9, 9, 0, Block.glass);
		chunk.setBlock(9, 9, 1, Block.glass);
		chunk.setBlock(9, 9, 2, Block.glass);
		chunk.setBlock(9, 9, 3, Block.glass);
		chunk.setBlock(8, 9, 3, Block.glass);
		chunk.setBlock(8, 9, 2, Block.glass);
		chunk.setBlock(8, 9, 1, Block.glass);
		chunk.setBlock(8, 9, 0, Block.glass);
		chunk.setBlock(7, 9, 0, Block.glass);
		chunk.setBlock(7, 9, 1, Block.glass);
		chunk.setBlock(7, 9, 2, Block.glass);
		chunk.setBlock(7, 9, 3, Block.glass);
		chunk.setBlock(6, 9, 3, Block.glass);
		chunk.setBlock(6, 9, 2, Block.glass);
		chunk.setBlock(6, 9, 1, Block.glass);
		chunk.setBlock(6, 9, 0, Block.glass);
		chunk.setBlock(5, 9, 3, Block.glass);
		chunk.setBlock(5, 9, 2, Block.glass);
		chunk.setBlock(5, 9, 1, Block.glass);
		chunk.setBlock(5, 9, 0, Block.glass);
		chunk.setBlock(4, 9, 0, Block.glass);
		chunk.setBlock(4, 9, 1, Block.glass);
		chunk.setBlock(4, 9, 2, Block.glass);
		chunk.setBlock(4, 9, 3, Block.glass);
		
		
		chunk.setBlock(3, 9, 9, Block.smoothStone);
		chunk.setBlock(2, 9, 9, Block.smoothStone);
		chunk.setBlock(1, 9, 9, Block.smoothStone);
		chunk.setBlock(0, 9, 9, Block.smoothStone);
		chunk.setBlock(0, 9, 8, Block.smoothStone);
		chunk.setBlock(1, 9, 8, Block.smoothStone);
		chunk.setBlock(2, 9, 8, Block.smoothStone);
		chunk.setBlock(3, 9, 8, Block.smoothStone);
		chunk.setBlock(3, 9, 7, Block.smoothStone);
		chunk.setBlock(3, 9, 6, Block.smoothStone);
		chunk.setBlock(3, 9, 5, Block.smoothStone);
		chunk.setBlock(3, 9, 4, Block.smoothStone);
		chunk.setBlock(2, 9, 7, Block.smoothStone);
		chunk.setBlock(2, 9, 6, Block.smoothStone);
		chunk.setBlock(2, 9, 5, Block.smoothStone);
		chunk.setBlock(2, 9, 4, Block.smoothStone);
		chunk.setBlock(1, 9, 7, Block.smoothStone);
		chunk.setBlock(1, 9, 6, Block.smoothStone);
		chunk.setBlock(1, 9, 5, Block.smoothStone);
		chunk.setBlock(1, 9, 4, Block.smoothStone);
		chunk.setBlock(0, 9, 7, Block.smoothStone);
		chunk.setBlock(0, 9, 6, Block.smoothStone);
		chunk.setBlock(0, 9, 5, Block.smoothStone);
		chunk.setBlock(0, 9, 4, Block.smoothStone);

		chunk.setBlock(8, 9, 9, Block.ice);
		chunk.setBlock(8, 9, 8, Block.ice);
		chunk.setBlock(8, 9, 7, Block.ice);
		chunk.setBlock(8, 9, 6, Block.ice);
		chunk.setBlock(8, 9, 5, Block.ice);
		chunk.setBlock(8, 9, 4, Block.ice);
		chunk.setBlock(9, 9, 4, Block.ice);
		chunk.setBlock(10, 9, 4, Block.ice);
		chunk.setBlock(11, 9, 4, Block.ice);
		chunk.setBlock(13, 9, 4, Block.ice);
		chunk.setBlock(12, 9, 4, Block.ice);
		chunk.setBlock(14, 9, 4, Block.ice);
		chunk.setBlock(15, 9, 4, Block.ice);
		chunk.setBlock(9, 9, 5, Block.ice);
		chunk.setBlock(10, 9, 5, Block.ice);
		chunk.setBlock(11, 9, 5, Block.ice);
		chunk.setBlock(12, 9, 5, Block.ice);
		chunk.setBlock(13, 9, 5, Block.ice);
		chunk.setBlock(14, 9, 5, Block.ice);
		chunk.setBlock(15, 9, 5, Block.ice);
		chunk.setBlock(9, 9, 6, Block.ice);
		chunk.setBlock(10, 9, 6, Block.ice);
		chunk.setBlock(11, 9, 6, Block.ice);
		chunk.setBlock(12, 9, 6, Block.ice);
		chunk.setBlock(13, 9, 6, Block.ice);
		chunk.setBlock(9, 9, 7, Block.ice);
		chunk.setBlock(10, 9, 7, Block.ice);
		chunk.setBlock(11, 9, 7, Block.ice);
		chunk.setBlock(12, 9, 7, Block.ice);
		chunk.setBlock(13, 9, 7, Block.ice);
		chunk.setBlock(9, 9, 8, Block.ice);
		chunk.setBlock(10, 9, 8, Block.ice);
		chunk.setBlock(11, 9, 8, Block.ice);
		chunk.setBlock(12, 9, 8, Block.ice);
		chunk.setBlock(13, 9, 8, Block.ice);
		chunk.setBlock(9, 9, 9, Block.ice);
		chunk.setBlock(10, 9, 9, Block.ice);
		chunk.setBlock(11, 9, 9, Block.ice);
		chunk.setBlock(12, 9, 9, Block.ice);
		chunk.setBlock(13, 9, 9, Block.ice);
		chunk.setBlock(14, 9, 6, Block.ice);
		chunk.setBlock(14, 9, 7, Block.ice);
		chunk.setBlock(14, 9, 8, Block.ice);
		chunk.setBlock(14, 9, 9, Block.ice);
		chunk.setBlock(15, 9, 9, Block.ice);
		chunk.setBlock(15, 9, 8, Block.ice);
		chunk.setBlock(15, 9, 7, Block.ice);
		chunk.setBlock(15, 9, 6, Block.ice);
		
		chunk.setBlock(6, 10, 9, Block.grass);
		chunk.setBlock(6, 10, 10, Block.grass);
		chunk.setBlock(6, 10, 11, Block.grass);
		chunk.setBlock(6, 11, 9, Block.grass);
		chunk.setBlock(6, 11, 10, Block.grass);
		chunk.setBlock(6, 12, 9, Block.grass);

		chunk.setBlock(0, 10, 0, Block.plantRed);
		chunk.setBlock(1, 10, 0, Block.plantYellow);
		chunk.setBlock(0, 10, 1, Block.mushroomBrown);
		chunk.setBlock(0, 10, 2, Block.mushroomRed);
		return chunk;
	}
}
