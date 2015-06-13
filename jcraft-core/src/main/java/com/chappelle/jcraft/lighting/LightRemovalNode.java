package com.chappelle.jcraft.lighting;

import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.world.chunk.Chunk;

public class LightRemovalNode
{
	public Chunk chunk;
	public short val;
	public int x;
	public int y;
	public int z;
	
	public LightRemovalNode(Vector3Int location, short val, Chunk chunk)
	{
		this.chunk = chunk;
		this.val = val;
		this.x = location.x;
		this.y = location.y;
		this.z = location.z;
	}
}
