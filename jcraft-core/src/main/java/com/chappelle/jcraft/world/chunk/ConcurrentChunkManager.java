package com.chappelle.jcraft.world.chunk;

import java.util.*;
import java.util.concurrent.*;

import org.slf4j.*;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.lighting.*;
import com.chappelle.jcraft.util.Context;
import com.chappelle.jcraft.util.concurrency.*;
import com.chappelle.jcraft.util.math.*;
import com.google.common.collect.*;

/**
 * This ChunkManager offloads the heavy lifting to other threads to prevent Game lag based on the following algorithm:
 * <ol>
 * <li>Calculates the region around the player based on view distance</li>
 * <li>If that region is different from the last region then unload chunks outside that region</li>
 * <li>Loops through all locations within the view distance, if a chunk is not loaded it queues it for loading</li>
 * <li>If a chunk is dirty, then it queues it for updating</li>
 * <li>All chunks queued to do a task on a separate thread add the chunk back to the readyChunks queue when they are done</li>
 * <li>A certain number of ready chunks get queued to the Main thread for update each frame.</li>
 * <li>The new relevant region gets stored for the next time around.</li>
 * </ol>
 */
public class ConcurrentChunkManager extends AbstractChunkManager
{
    private static final Logger logger = LoggerFactory.getLogger(ConcurrentChunkManager.class);

    /**
     * The maximum number of ready chunks to add to the scene(or update) in one frame. 
     * Updates happen on the Main thread so they have to be limited.
     */
    private static final int MAX_MESH_UPDATES_PER_FRAME = 2;
    private static final int NUM_TASK_THREADS = 4;

	private final TaskMaster<ChunkTask> chunkUpdater;
	private final TaskMaster<ChunkTask> chunkLoader;
	
	private final Set<Vector2Int> preparingChunks = Sets.newHashSet();
	private final DynamicPriorityBlockingQueue<Chunk> readyChunks = new DynamicPriorityBlockingQueue<Chunk>(new ClosestChunkUpdateComparator());
	
	public ConcurrentChunkManager(Context context)
	{
		super(context);

		this.chunkUpdater = TaskMaster.createDynamicPriorityTaskMaster("Chunk-Updater", NUM_TASK_THREADS, new ClosestChunkComparator());
		this.chunkLoader = TaskMaster.createDynamicPriorityTaskMaster("Chunk-Loader", NUM_TASK_THREADS, new ClosestChunkComparator());
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
		int chunksToProcess = Math.min(readyChunks.size(), MAX_MESH_UPDATES_PER_FRAME);
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

	/**
	 * Does the actual updating of the Chunk at the given location. Calculates the mesh
	 * and adds it to the ready chunks. 
	 */
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
			
			generateChunkMesh(chunk);
			addReadyChunk(chunkLocation, chunk);
		}
	}
	
	/**
	 * Does the actual initialization of a Chunk. Checks the disk for the saved chunk. If one is not found
	 * then it builds the mesh and initializes the lighting and adds it to the available chunks map.
	 */
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
//				LightManager lightManager = new RecursiveFloodFillLightManager(world);
				LightManager lightManager = new FloodFillLightManager(world);
				lightManager.initSunlight(chunk);
				nearCache.put(chunkLocation, chunk);
				
				generateChunkMesh(chunk);
				addReadyChunk(chunkLocation, chunk);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds chunk to the scene if it hasn't already been added. Otherwise, it just updates the mesh.
	 * This gets run on the Main thread.
	 */
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
				
				//Slows things down but lighting and other issues get cleared up. May need to do better later.
				markNeighborsDirty(chunk);
			}
			chunk.setMeshFromPending();
			preparingChunks.remove(chunkLocation);
			return null;
		}

		private void markNeighborsDirty(Chunk chunk)
		{
			for(Direction dir : Direction.values())
			{
				Chunk neighbor = world.getChunkNeighbor(chunk, dir);
				if(neighbor != null)
				{
					neighbor.markDirty();
				}
			}
		}
	}

	/**
	 * Adds the given collection of Chunks to the scene. Used during initialization.
	 * This gets run on the Main thread.
	 */
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

	/**
	 * Removes a chunk from the scene and from the map of available chunks.
	 * This gets run on the Main thread.
	 */
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
	
	/**
	 * Sorts ChunkTasks in order of closest Chunk distance to camera
	 */
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

	/**
	 * Sorts Chunks in order of closest Chunk distance to camera
	 */
	private class ClosestChunkUpdateComparator implements Comparator<Chunk>
	{
		@Override
		public int compare(Chunk chunk1, Chunk chunk2)
		{
			Vector2Int chunk1Pos = chunk1.location2i;
			Vector2Int chunk2Pos = chunk2.location2i;
			Vector2Int cameraChunkPosition = toChunkPosition(world.getPlayer().cam.getLocation());
			return cameraChunkPosition.gridDistance(chunk2Pos) - cameraChunkPosition.gridDistance(chunk1Pos);
		}
	}

}