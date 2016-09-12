package com.chappelle.jcraft.world.chunk;

import com.chappelle.jcraft.Vector2Int;
import com.chappelle.jcraft.util.concurrency.Task;

public interface ChunkTask extends Task
{
	Vector2Int getPosition();
}