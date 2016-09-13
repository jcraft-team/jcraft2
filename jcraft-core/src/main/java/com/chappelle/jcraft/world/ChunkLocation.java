package com.chappelle.jcraft.world;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.world.chunk.Chunk;

public class ChunkLocation
{
	private Chunk chunk;
	private Vector3Int localBlockLocation;

	public ChunkLocation(Chunk chunk, Vector3Int localBlockLocation)
	{
		this.chunk = chunk;
		this.localBlockLocation = localBlockLocation;
	}
	
	public Chunk getChunk()
	{
		return chunk;
	}

	public Vector3Int getLocalBlockLocation()
	{
		return localBlockLocation;
	}

	public Block getBlock()
	{
		return chunk.getBlock(localBlockLocation);
	}

	public void setBlock(Block block)
	{
		chunk.setBlock(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z, block);
	}

	public void removeBlock()
	{
		chunk.removeBlock(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z);
	}
}
