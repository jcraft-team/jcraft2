package com.chappelle.jcraft.world.chunk;

import java.util.*;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.blocks.MeshGenerator;
import com.chappelle.jcraft.serialization.VoxelWorldSave;
import com.chappelle.jcraft.util.Context;
import com.chappelle.jcraft.world.World;
import com.google.common.collect.Maps;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;

public abstract class AbstractChunkManager implements ChunkManager
{
	protected static final int UNLOAD_LEEWAY = 1;
	private static final int UNLOAD_PER_FRAME = 64;
	protected Map<Vector2Int, Chunk> nearCache = Maps.newConcurrentMap();
	protected final World world;
	protected Region2Int currentRelevantRegion;
	protected VoxelWorldSave voxelWorldSave;
	protected ChunkGenerator chunkGenerator;
	protected Context context;
	protected abstract void unloadChunk(final Vector2Int chunkLocation);
	
	public AbstractChunkManager(Context context)
	{
		this.world = context.get(World.class);
		this.voxelWorldSave = context.get(VoxelWorldSave.class);
		this.chunkGenerator = context.get(ChunkGenerator.class);
	}
	
	@Override
	public Chunk getChunk(int chunkX, int chunkZ)
	{
		return nearCache.get(new Vector2Int(chunkX, chunkZ));
	}

	@Override
	public Collection<Chunk> getLoadedChunks()
	{
		return nearCache.values();
	}

	@Override
	public void rebuildChunks()
	{
		for(Chunk chunk : nearCache.values())
		{
			chunk.markDirty();
		}
	}

	@Override
	public void destroy()
	{
	}

	protected Region2Int getRelevantRegion(Vector2Int playerChunk)
	{
		return Region2Int.createFromCenterExtents(playerChunk, GameSettings.chunkRenderDistance);
	}

	protected Vector2Int getPlayerChunk()
	{
		EntityPlayer player = world.getPlayer();
		return toChunkPosition(player.posX, player.posZ);
	}

	protected Vector2Int toChunkPosition(double posX, double posZ)
	{
		return new Vector2Int((int)(Math.floor(posX)/16.0), (int)(Math.floor(posZ)/16.0));
	}
	
	protected Vector2Int toChunkPosition(Vector3f pos)
	{
		return toChunkPosition(pos.x, pos.z);
	}

	protected void unloadChunksWhenNecessary(Region2Int newRelevantRegion)
	{
		int unloaded = 0;
		Region2Int expandedRegion = newRelevantRegion.expand(UNLOAD_LEEWAY);
		for(Vector2Int chunkLocation : nearCache.keySet())
		{
			if(unloaded < UNLOAD_PER_FRAME && !expandedRegion.encompasses(chunkLocation))
			{
				unloadChunk(chunkLocation);
				unloaded++;
			}
		}
	}
	
	protected boolean isRelevantRegionChanged(Region2Int newRelevantRegion)
	{
		return currentRelevantRegion != null && !newRelevantRegion.equals(currentRelevantRegion);
	}

	protected void generateChunkMesh(Chunk chunk)
	{
		final Mesh opaque = MeshGenerator.generateOptimizedMesh(chunk, false);
		final Mesh transparent = MeshGenerator.generateOptimizedMesh(chunk, true);
		chunk.setPendingMesh(opaque, transparent);
	}
	
	protected Chunk generateChunk(int x, int z)
	{
		byte[][][] blockTypes = new byte[16][256][16];
		chunkGenerator.generate(x, z, blockTypes);
		return new Chunk(world, x, z, blockTypes);
	}

	protected Chunk loadChunkFromDisk(int chunkX, int chunkZ)
	{
		int[][][] chunkDataFromDisk = voxelWorldSave.readChunk(chunkX, chunkZ);
		if(chunkDataFromDisk != null)
		{
			return new Chunk(world, chunkX, chunkZ, chunkDataFromDisk);
		}
		else
		{
			return null;
		}
	}

	protected Chunk loadOrCreateChunk(Vector2Int location)
	{
		Chunk chunk = loadChunkFromDisk(location.x, location.z);
		if(chunk == null)
		{
			chunk = generateChunk(location.x, location.z);
		}
		return chunk;
	}
	
	public void markNeighborChunksDirty(Chunk chunk)
	{
		for(Direction dir : Direction.values())
		{
			Chunk neighbor = chunk.getChunkNeighbor(dir);
			if(neighbor != null)
			{
				neighbor.markDirty();
			}
		}
	}
}
