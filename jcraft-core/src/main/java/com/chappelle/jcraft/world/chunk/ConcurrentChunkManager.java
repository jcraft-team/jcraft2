package com.chappelle.jcraft.world.chunk;

import java.util.*;
import java.util.concurrent.*;

import org.slf4j.*;

import com.chappelle.jcraft.ProgressMonitor;
import com.chappelle.jcraft.lighting.*;
import com.chappelle.jcraft.util.Context;
import com.chappelle.jcraft.util.concurrency.*;
import com.chappelle.jcraft.util.math.*;
import com.google.common.collect.*;

public class ConcurrentChunkManager extends AbstractChunkManager
{
    private static final Logger logger = LoggerFactory.getLogger(ConcurrentChunkManager.class);

    private static final int NUM_TASK_THREADS = 8;

	private final TaskMaster<ChunkTask> chunkUpdater;
	private final TaskMaster<ChunkTask> chunkLoader;
	
	private final Set<Vector2Int> preparingChunks = Sets.newHashSet();
	private final BlockingQueue<Chunk> readyChunks = Queues.newLinkedBlockingQueue();
	
	public ConcurrentChunkManager(Context context)
	{
		super(context);

		this.chunkUpdater = TaskMaster.createDynamicPriorityTaskMaster("Chunk-Updater", NUM_TASK_THREADS, new ClosestChunkComparator());
		this.chunkLoader = TaskMaster.createDynamicPriorityTaskMaster("Chunk-Loader", NUM_TASK_THREADS, new ClosestChunkComparator());
//		this.chunkLoader = TaskMaster.createFIFOTaskMaster("Chunk-Loader", NUM_TASK_THREADS);
	}

	public void update()
	{
		Vector2Int playerChunk = getPlayerChunk();
		Region2Int newRelevantRegion = getRelevantRegion(playerChunk);
		if(isRelevantRegionChanged(newRelevantRegion))
		{
			unloadChunksWhenNecessary(newRelevantRegion);
		}
		for(Vector2Int chunkLocation : newRelevantRegion)
		{
			Chunk chunk = nearCache.get(chunkLocation);
			if(isEligibleToLoadOrUpdate(chunkLocation))
			{
				if(chunk == null)
				{
					submitLoadChunkTask(chunkLocation);
				}
				else if(chunk.isDirty())
				{
					updateChunk(chunkLocation);
				}
			}
		}
		consumeReadyChunks();
		
		currentRelevantRegion = newRelevantRegion;
	}

	/**
	 * Queues mesh updates to happen on the main game thread
	 */
	private void consumeReadyChunks()
	{
		int maxPerFrame = 1;
		int chunksToProcess = Math.min(readyChunks.size(), maxPerFrame);
		if(chunksToProcess > 0)
		{
			List<Chunk> newReadyChunks = Lists.newArrayListWithExpectedSize(chunksToProcess);
			readyChunks.drainTo(newReadyChunks, chunksToProcess);
			for(Chunk chunk : newReadyChunks)
			{
				world.enqueue(new ChunkMeshUpdater(chunk)); 
			}
		}
	}
	
	private boolean isEligibleToLoadOrUpdate(Vector2Int chunkLocation)
	{
		return !preparingChunks.contains(chunkLocation);
	}
	
	public void initializeChunks(ProgressMonitor progress, double playerX, double playerZ, int radius)
	{
		Vector2Int playerChunk = getPlayerChunk();
		Region2Int newRelevantRegion = getRelevantRegion(playerChunk);
		int finishedItems = 0;
		Vector2Int regionSize = newRelevantRegion.size();
		int chunksToLoad = regionSize.x*regionSize.z;
		for(Vector2Int chunkLocation : newRelevantRegion)
		{
			progress.setNote("Generating chunk " + chunkLocation);
			Chunk chunk = loadOrCreateChunk(chunkLocation);
			world.getLightManager().initSunlight(chunk);
			nearCache.put(chunkLocation, chunk);
			finishedItems++;
			progress.setPercentCompleted(finishedItems/(float)chunksToLoad);
		}
		
		world.getLightManager().propagateLight();
		
		finishedItems = 0;
		progress.setNote("Generating chunk mesh");
		for(Chunk chunk : nearCache.values())
		{
			generateChunkMesh(chunk);
			finishedItems++;
			progress.setPercentCompleted(finishedItems/(float)chunksToLoad);
		}
		
		Future<Boolean> initFuture = world.enqueue(new BulkChunkMeshUpdater(nearCache.values()));
		try
		{
			initFuture.get();
		}
		catch(InterruptedException | ExecutionException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			//Need to wait for init to complete so player doesn't spawn under the world.
			world.spawnPlayer(world.getPlayer());
			currentRelevantRegion = newRelevantRegion;
			initialized = true;
		}
	}
	
	@Override
	public void destroy()
	{
		chunkUpdater.shutdown(new ShutdownChunkTask(), true);
		chunkLoader.shutdown(new ShutdownChunkTask(), true);
		nearCache.clear();
		currentRelevantRegion = null;
		readyChunks.clear();
	}

	@Override
	protected void unloadChunk(final Vector2Int chunkLocation)
	{
//		Chunk chunk = nearCache.remove(chunkLocation);
//		if(chunk != null && chunk.isLoadedAndAddedToScene())
//		{
//			chunk.removeFromScene();
//		}
//
		world.enqueue(new RemoveChunkFromScene(chunkLocation));
	}

	private void addReadyChunk(Vector2Int pos, Chunk chunk)
	{
		readyChunks.add(chunk);
		preparingChunks.remove(pos);
	}
	
	private void updateChunk(Vector2Int chunkLocation)
	{
		try
		{
			preparingChunks.add(chunkLocation);
			chunkUpdater.put(new UpdateChunkTask(chunkLocation));
		}
		catch(InterruptedException e)
		{
			logger.error("Failed to enqueue update request for {}", chunkLocation, e);
		}
	}

	private void submitLoadChunkTask(Vector2Int chunkLocation)
	{
		try
		{
			preparingChunks.add(chunkLocation);
			chunkLoader.put(new LoadOrCreateChunkTask(chunkLocation));
		}
		catch(InterruptedException e)
		{
			logger.error("Failed to enqueue load request for {}", chunkLocation, e);
		}
	}

	private class UpdateChunkTask extends AbstractChunkTask
	{
		public UpdateChunkTask(Vector2Int chunkLocation)
		{
			super("Update Chunk", chunkLocation);
		}
		
		@Override
		public void run()
		{
			Vector2Int chunkLocation = getPosition();
			Chunk chunk = nearCache.get(chunkLocation);
			
//			world.getLightManager().propagateLight();
			generateChunkMesh(chunk);
			addReadyChunk(chunkLocation, chunk);
		}
	}

	
	private class LoadOrCreateChunkTask extends AbstractChunkTask
	{
		public LoadOrCreateChunkTask(Vector2Int chunkLocation)
		{
			super("Load or Create Chunk", chunkLocation);
		}
		
		@Override
		public void run()
		{
			try
			{
				
				Vector2Int chunkLocation = getPosition();
				Chunk chunk = loadOrCreateChunk(chunkLocation);
				LightManager lightManager = new RecursiveFloodFillLightManager(world);
				lightManager.initSunlight(chunk);
				nearCache.put(chunkLocation, chunk);
				
//			markNeighborChunksDirty(chunk);
				
				generateChunkMesh(chunk);
				addReadyChunk(chunkLocation, chunk);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private class BulkLoadOrCreateChunkTask extends AbstractTask
	{
		private List<Vector2Int> chunkLocations;
		
		public BulkLoadOrCreateChunkTask(List<Vector2Int> chunkLocations)
		{
			this.chunkLocations = chunkLocations;
		}
		
		@Override
		public void run()
		{
			for(Vector2Int chunkLocation : chunkLocations)
			{
				Chunk chunk = loadOrCreateChunk(chunkLocation);
				LightManager lightManager = new FloodFillLightManager(world);
				lightManager.initSunlight(chunk);
				generateChunkMesh(chunk);
				nearCache.put(chunkLocation, chunk);
				addReadyChunk(chunkLocation, chunk);
			}
//			for(Vector2Int chunkLocation : chunkLocations)
//			{
//				Chunk chunk = nearCache.get(chunkLocation);
//				markNeighborChunksDirty(chunk);
//			}
		}

		@Override
		public String getName()
		{
			return "Load Or Create Chunks";
		}
	}

	private class ChunkMeshUpdater implements Callable<Void>
	{
		private Chunk chunk;

		public ChunkMeshUpdater(Chunk chunk)
		{
			this.chunk = chunk;
		}

		@Override
		public Void call() throws Exception
		{
			Vector2Int chunkLocation = new Vector2Int(chunk.location.x, chunk.location.z);
			if(!chunk.isLoaded)
			{
				chunk.addToScene(world.node);
				nearCache.put(chunkLocation, chunk);
			}
			chunk.setMeshFromPending();
			preparingChunks.remove(chunkLocation);
			return null;
		}
	}

	private class BulkChunkMeshUpdater implements Callable<Boolean>
	{
		private Collection<Chunk> chunks;
		
		public BulkChunkMeshUpdater(Collection<Chunk> chunks)
		{
			this.chunks = chunks;
		}
		
		@Override
		public Boolean call() throws Exception
		{
			for(Chunk chunk : chunks)
			{
				if(!chunk.isLoaded)
				{
					chunk.addToScene(world.node);
					nearCache.put(new Vector2Int(chunk.location.x, chunk.location.z), chunk);
				}
				chunk.setMeshFromPending();
			}
			return true;
		}
	}

	private class RemoveChunkFromScene implements Callable<Void>
	{
		private Vector2Int chunkLocation;
		
		public RemoveChunkFromScene(Vector2Int chunkLocation)
		{
			this.chunkLocation = chunkLocation;
		}
		
		@Override
		public Void call() throws Exception
		{
			Chunk chunk = nearCache.remove(chunkLocation);
			if(chunk != null && chunk.isLoadedAndAddedToScene())
			{
				chunk.removeFromScene();
			}
			return null;
		}
	}
	
	private class ClosestChunkComparator implements Comparator<ChunkTask>
	{
		@Override
		public int compare(ChunkTask chunk1, ChunkTask chunk2)
		{
			Vector2Int chunk1Pos = chunk1.getPosition();
			Vector2Int chunk2Pos = chunk2.getPosition();
			Vector2Int cameraChunkPosition = toChunkPosition(world.getPlayer().cam.getLocation());
			return cameraChunkPosition.gridDistance(chunk1Pos) - cameraChunkPosition.gridDistance(chunk2Pos);
		}
	}

}