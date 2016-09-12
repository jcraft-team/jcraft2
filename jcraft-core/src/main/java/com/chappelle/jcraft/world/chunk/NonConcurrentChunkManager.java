package com.chappelle.jcraft.world.chunk;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.util.Context;

public class NonConcurrentChunkManager extends AbstractChunkManager
{
	public NonConcurrentChunkManager(Context context)
	{
		super(context);
	}
	
	@Override
	public void update()
	{
		Vector2Int playerChunk = getPlayerChunk();
		Region2Int newRelevantRegion = getRelevantRegion(playerChunk);
		if(currentRelevantRegion != null && !newRelevantRegion.equals(currentRelevantRegion))//Means we have stuff to load and stuff to unload
		{
			unloadChunksWhenNecessary(newRelevantRegion);
		}
		for(Vector2Int chunkLocation : newRelevantRegion)
		{
			Chunk chunk = nearCache.get(chunkLocation);
			if(chunk == null)
			{
				chunk = loadOrCreateChunk(chunkLocation);
				chunk.addToScene(world.node);
				markNeighborChunksDirty(chunk);
				world.getLightManager().propagateLight();
				generateChunkMesh(chunk);
				chunk.setMeshFromPending();
				nearCache.put(chunkLocation, chunk);
			}
			else if(chunk.isDirty())
			{
				world.getLightManager().propagateLight();
				generateChunkMesh(chunk);
				chunk.setMeshFromPending();
			}
		}
		currentRelevantRegion = newRelevantRegion;
	}

	@Override
	protected void unloadChunk(final Vector2Int chunkLocation)
	{
		Chunk chunk = nearCache.remove(chunkLocation);//May need to persist before removing
		if(chunk != null)
		{
			chunk.removeFromScene();
		}
	}

	@Override
	public void initializeChunks(ProgressMonitor progress, double playerX, double playerZ, int radius)
	{
		update();
		world.spawnPlayer(world.getPlayer());
	}
}
