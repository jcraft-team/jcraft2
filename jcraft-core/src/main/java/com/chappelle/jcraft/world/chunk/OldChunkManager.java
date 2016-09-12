package com.chappelle.jcraft.world.chunk;

import java.util.*;
import java.util.concurrent.*;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.blocks.MeshGenerator;
import com.chappelle.jcraft.util.*;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jme3.scene.Mesh;

public class OldChunkManager extends AbstractChunkManager
{
	private Map<Long, Chunk> chunks = new ConcurrentHashMap<Long, Chunk>();
	
	/**Chunks added to this queue need their mesh generated for the first time and added to the scene graph*/
	private Queue<Chunk> addedQueue = new ConcurrentLinkedQueue<Chunk>();
	
	private static final int THREAD_COUNT = 1;
	public ScheduledThreadPoolExecutor chunkUpdaters = new ScheduledThreadPoolExecutor(THREAD_COUNT, new ThreadFactoryBuilder().setNameFormat("ChunkUpdater-%d").build());
	public ScheduledThreadPoolExecutor chunkLoaders = new ScheduledThreadPoolExecutor(THREAD_COUNT, new ThreadFactoryBuilder().setNameFormat("ChunkLoader-%d").build());
	
	public OldChunkManager(Context context)
	{
		super(context);
	}

	public void update()
	{
		if(chunkUpdaters.getActiveCount() == 0)
		{
			chunkUpdaters.submit(new Callable<Void>()
			{
				@Override
				public Void call() throws Exception
				{
					updateNow();
					return null;
				}
			});
		}
		EntityPlayer player = world.getPlayer();
		loadChunksAroundPlayer(player.posX, player.posZ, GameSettings.chunkRenderDistance);
	}

	@Override
	protected void unloadChunk(Vector2Int chunkLocation)
	{
	}

	private void loadChunksAroundPlayer(double playerX, double playerZ, int radius)
	{
		int chunkX = MathUtils.floor_double(playerX)/16;
		int chunkZ = MathUtils.floor_double(playerZ)/16;
		Chunk playerChunk = getChunk(chunkX, chunkZ);
		if(playerChunk == null)
		{
			playerChunk = loadOrCreateChunk(new Vector2Int(chunkX, chunkZ));
			addChunk(playerChunk);
		}
		else
		{
			if(chunkLoaders.getActiveCount() == 0)
			{
				chunkLoaders.submit(new ChunkLoaderThread(playerChunk, radius));
			}
		}
	}
	
	public void initializeChunks(ProgressMonitor progress, double playerX, double playerZ, int radius)
	{
		progress.setNote("Generating terrain");
		int finishedItems = 0;
		int chunksToLoad = (int)Math.pow(2*radius+1, 2);//Moore neighborhood
		int chunkX = MathUtils.floor_double(playerX)/16;
		int chunkZ = MathUtils.floor_double(playerZ)/16;
		List<Chunk> initialChunks = new ArrayList<>();
		Chunk playerChunk = loadChunkFromDisk(chunkX, chunkZ);
		if(playerChunk == null)
		{
			playerChunk = generateChunk(chunkX, chunkZ);
		}
		initialChunks.add(playerChunk);
		chunks.put(playerChunk.id, playerChunk);
		finishedItems++;
		progress.setPercentCompleted(finishedItems/(float)chunksToLoad);
		for(Vector3Int location : getMissingChunkNeighborhoodLocations(playerChunk.location.x, playerChunk.location.z, radius))
		{
			Chunk chunk = loadChunkFromDisk(location.x, location.z);
			if(chunk == null)
			{
				chunk = generateChunk(location.x, location.z);
			}
			initialChunks.add(chunk);
			chunks.put(chunk.id, chunk);
			finishedItems++;
			progress.setPercentCompleted(finishedItems/(float)chunksToLoad);
		}
		world.getLightManager().propagateLight();
		finishedItems = 0;
		progress.setNote("Generating chunk mesh");
		for(Chunk chunk : initialChunks)
		{
			final Mesh opaque = MeshGenerator.generateOptimizedMesh(chunk, false);
			final Mesh transparent = MeshGenerator.generateOptimizedMesh(chunk, true);
			world.enqueue(new ChunkMeshUpdater(chunk, opaque, transparent));
			finishedItems++;
			progress.setPercentCompleted(finishedItems/(float)initialChunks.size());
		}
	}
	
    private List<Vector3Int> getMissingChunkNeighborhoodLocations(int centerX, int centerZ, int r)
    {
    	List<Vector3Int> result = new ArrayList<Vector3Int>();
    	//Iterates starting in top left corner, then down, then across to the right
    	int x = centerX - r;
    	int z = centerZ + r;
    	int gridWidth = r*2 + 1;
    	for(int xMod = 0; xMod < gridWidth; xMod++)
    	{
    		for(int zMod = 0; zMod < gridWidth; zMod++)
    		{
    			int chunkX = x+xMod;
    			int chunkZ = z-zMod;
    			if(!(chunkX == centerX && chunkZ == centerZ))//Exclude this chunk from result
    			{
    				Chunk chunk = world.getChunkFromChunkCoordinates(chunkX, chunkZ);
    				if(chunk == null)
    				{
    					result.add(new Vector3Int(chunkX, 0, chunkZ));
    				}
    			}
    		}
    	}
    	return result;
    }
    
	private class ChunkLoaderThread implements Callable<Void>
	{
		private Chunk playerChunk;
		
		private int radius;
		public ChunkLoaderThread(Chunk playerChunk, int radius)
		{
			this.playerChunk = playerChunk;
			this.radius = radius;
		}
		
		@Override
		public Void call() throws Exception
		{
			for(Vector3Int location : getMissingChunkNeighborhoodLocations(playerChunk.location.x, playerChunk.location.z, radius))
			{
				Chunk chunk = loadChunkFromDisk(location.x, location.z);
				if(chunk == null)
				{
					chunk = generateChunk(location.x, location.z);
				}
				addChunk(chunk);
			}
			return null;
		}
	}

	private void updateNow()
	{
		addNextChunk();
		for(Chunk chunk : getLoadedChunks())
		{
			if(chunk.isDirty() && chunk.isLoaded)
			{
				world.getLightManager().propagateLight();
				final Mesh opaque = MeshGenerator.generateOptimizedMesh(chunk, false);
				final Mesh transparent = MeshGenerator.generateOptimizedMesh(chunk, true);
				world.enqueue(new ChunkMeshUpdater(chunk, opaque, transparent));
			}
		}
	}

	private void addNextChunk()
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
	
	private void addChunk(Chunk chunk)
	{
		addedQueue.add(chunk);
		chunks.put(chunk.id, chunk);
	}

	public void destroy()
	{
		chunkUpdaters.shutdownNow();
		chunkLoaders.shutdownNow();
	}
}
