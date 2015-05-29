package com.cubes.lighting;

import com.cubes.Chunk;

public class LightRemovalNode
{
	public Chunk chunk;
	public short index;
	public short val;
	
	public LightRemovalNode(short index, short val, Chunk chunk)
	{
		this.chunk = chunk;
		this.index = index;
		this.val = val;
	}
}
