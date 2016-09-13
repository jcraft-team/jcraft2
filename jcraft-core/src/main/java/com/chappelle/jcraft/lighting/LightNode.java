package com.chappelle.jcraft.lighting;

import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.world.chunk.Chunk;

public class LightNode
{
	public Chunk chunk;
	public int x;
	public int y;
	public int z;
	
	public LightNode(int x, int y, int z, Chunk chunk)
	{
		this.chunk = chunk;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public LightNode(Vector3Int location, Chunk chunk)
	{
		this.chunk = chunk;
		this.x = location.x;
		this.y = location.y;
		this.z = location.z;
	}
}
