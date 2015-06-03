package com.chappelle.jcraft.lighting;

import com.chappelle.jcraft.Chunk;
import com.chappelle.jcraft.Vector3Int;

public class LightNode
{
	public Chunk chunk;
	public int x;
	public int y;
	public int z;
	
	public LightNode(Vector3Int location, Chunk chunk)
	{
		this.chunk = chunk;
		this.x = location.x;
		this.y = location.y;
		this.z = location.z;
	}
}
