package com.chappelle.jcraft.world.chunk;

import com.chappelle.jcraft.util.math.Vector2Int;

public class ShutdownChunkTask implements ChunkTask
{

	@Override
	public String getName()
	{
		return "Shutdown";
	}

	@Override
	public void run()
	{
	}

	@Override
	public boolean isTerminateSignal()
	{
		return true;
	}

	@Override
	public Vector2Int getPosition()
	{
		return Vector2Int.zero();
	}

}
