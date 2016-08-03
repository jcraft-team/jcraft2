package com.chappelle.jcraft.world.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.chappelle.jcraft.blocks.MeshGenerator;
import com.chappelle.jcraft.world.World;
import com.jme3.scene.Mesh;

public class ChunkManager
{
	private Map<Long, Chunk> chunks = new ConcurrentHashMap<Long, Chunk>();
	
	/**Chunks added to this queue need their mesh rebuilt. They have already been added to the scene graph*/
	private Queue<Chunk> updateQueue = new ConcurrentLinkedQueue<Chunk>();

	/**Chunks added to this queue need their mesh generated for the first time and added to the scene graph*/
	private Queue<Chunk> addedQueue = new ConcurrentLinkedQueue<Chunk>();
	
	/**Chunks added to this queue need to be removed from the scene graph*/
	private Queue<Chunk> removedQueue = new ConcurrentLinkedQueue<Chunk>();
	
	private World world;
	
	private static final int THREAD_COUNT = 1;
	public ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(THREAD_COUNT);
	
	public ChunkManager(World world)
	{
		this.world = world;
	}
	
	public void updateNow()
	{
		addChunks();
		for(Chunk chunk : getLoadedChunks())
		{
			if(chunk.isDirty() && chunk.isLoaded)
			{
				chunk.lightMgr.propagateLight();
				final Mesh opaque = MeshGenerator.generateOptimizedMesh(chunk, false);
				final Mesh transparent = MeshGenerator.generateOptimizedMesh(chunk, true);
				world.enqueue(new ChunkMeshUpdater(chunk, opaque, transparent));
			}
		}
	}

	private void addChunks()
	{
		Chunk chunk = addedQueue.poll();
		if(chunk != null)
		{
			//No need to propagate light here. it's already initialized
			final Mesh opaque = MeshGenerator.generateOptimizedMesh(chunk, false);
			final Mesh transparent = MeshGenerator.generateOptimizedMesh(chunk, true);
			world.enqueue(new ChunkMeshUpdater(chunk, opaque, transparent));
		}
	}
	
	public void update()
	{
		if(executor.getActiveCount() == 0)
		{
			executor.submit(new Callable<Void>()
			{
				@Override
				public Void call() throws Exception
				{
					updateNow();
					return null;
				}
			});
		}
	}
	
	private class ChunkMeshUpdater implements Callable<Void>
	{
		private Chunk chunk;
		private Mesh opaque;
		private Mesh transparent;
		
		public ChunkMeshUpdater(Chunk chunk, Mesh opaque, Mesh transparent)
		{
			this.chunk = chunk;
			this.opaque = opaque;
			this.transparent = transparent;
		}
		
		@Override
		public Void call() throws Exception
		{
			if(!chunk.isLoaded)
			{
				chunk.addToScene(world.node);
				chunks.put(chunk.id, chunk);
			}
			chunk.setMesh(opaque, transparent);
			return null;
		}
	}
	
	public int getLoadedChunkCount()
	{
		return chunks.size();
	}
	
	public Chunk getChunk(int chunkX, int chunkZ)
	{
		return chunks.get(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ));
	}

	public boolean chunkExists(int x, int z)
	{
		return getChunk(x, z) != null;
	}
	
	public List<Chunk> getLoadedChunks()
	{
		return new ArrayList<Chunk>(chunks.values());
	}
	
	public void addChunk(Chunk chunk)
	{
		addedQueue.add(chunk);
		chunks.put(chunk.id, chunk);
	}
	
	public void removeChunk(Chunk chunk)
	{
		chunks.remove(chunk.id);
	}
	
	public Chunk pollUpdateQueue()
	{
		return updateQueue.poll();
	}

	public Chunk pollAddedQueue()
	{
		return addedQueue.poll();
	}

	public Chunk pollRemovedQueue()
	{
		return removedQueue.poll();
	}
	
	public void destroy()
	{
		executor.shutdownNow();
	}
}
