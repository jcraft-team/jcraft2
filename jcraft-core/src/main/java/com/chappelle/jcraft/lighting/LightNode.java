package com.chappelle.jcraft.lighting;

import com.chappelle.jcraft.Chunk;

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
