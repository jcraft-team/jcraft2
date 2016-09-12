package com.chappelle.jcraft.world.chunk;

import java.util.*;

import com.chappelle.jcraft.ProgressMonitor;

public interface ChunkManager
{
	void update();
	
	void initializeChunks(ProgressMonitor progress, double playerX, double playerZ, int radius);
	
	Chunk getChunk(int chunkX, int chunkZ);
	Collection<Chunk> getLoadedChunks();
	void destroy();
	void rebuildChunks();
}
