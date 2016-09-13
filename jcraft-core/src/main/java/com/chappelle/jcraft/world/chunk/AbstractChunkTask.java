package com.chappelle.jcraft.world.chunk;

import com.chappelle.jcraft.util.math.Vector2Int;

public abstract class AbstractChunkTask implements ChunkTask
{
	private final Vector2Int position;
	private final String name;

	public AbstractChunkTask(String name, Vector2Int position)
	{
		this.position = new Vector2Int(position.x, position.z);
		this.name = name + " " + position;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public Vector2Int getPosition()
	{
		return position;
	}

	@Override
	public boolean isTerminateSignal()
	{
		return false;
	}
}
