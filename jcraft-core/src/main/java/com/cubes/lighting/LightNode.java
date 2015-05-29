package com.cubes.lighting;

import com.cubes.Chunk;

public class LightNode
{
	public Chunk chunk;
	public short index;
	
	public LightNode(short index, Chunk chunk)
	{
		this.chunk = chunk;
		this.index = index;
	}
}
