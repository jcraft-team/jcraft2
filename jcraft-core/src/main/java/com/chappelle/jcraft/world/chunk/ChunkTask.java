package com.chappelle.jcraft.world.chunk;

import com.chappelle.jcraft.util.concurrency.Task;
import com.chappelle.jcraft.util.math.Vector2Int;

public interface ChunkTask extends Task
{
	Vector2Int getPosition();
}