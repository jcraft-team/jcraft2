package com.chappelle.jcraft.blocks;

import java.util.Map;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.util.math.*;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.google.common.collect.Maps;

import gnu.trove.list.*;

public class MeshGenContextImpl implements MeshGenContext
{
	public Vector3fPool vec3Pool = new Vector3fPool();
	public final World world;
	public final Chunk chunk;

	public MeshData meshData;
	public Vector3Int location;
	public Block block;
	
	private Map<Direction, Chunk> neighborCache = Maps.newHashMap();
	
	public MeshGenContextImpl(World world, Chunk chunk)
	{
		this.world = world;
		this.chunk = chunk;
	}

	public void setBlock(Block block)
	{
		this.block = block;
	}
	
	public Chunk getChunkNeighbor(Direction dir)
	{
		Chunk neighbor = neighborCache.get(dir);
		if(neighbor == null)
		{
			neighbor = world.getChunkNeighbor(chunk, dir);
			neighborCache.put(dir, neighbor);
		}
		return neighbor;
	}

	@Override
	public Vector3Int getLocation()
	{
		return location;
	}

	@Override
	public Chunk getChunk()
	{
		return chunk;
	}

	@Override
	public Block getBlock()
	{
		return block;
	}

	@Override
	public Block getBlock(int x, int y, int z)
	{
		return world.getBlock(x, y, z);
	}

	@Override
	public TFloatList getColorList()
	{
		return meshData.colorList;
	}

	@Override
	public TFloatList getPositions()
	{
		return meshData.positionsList;
	}

	@Override
	public TShortList getIndices()
	{
		return meshData.indicesList;
	}

	@Override
	public TFloatList getNormals()
	{
		return meshData.normalsList;
	}

	@Override
	public TFloatList getTextureCoordinates()
	{
		return meshData.textureCoordinatesList;
	}

	@Override
	public Vector3fPool getVector3fPool()
	{
		return meshData.vec3Pool;
	}

	@Override
	public int getTexturesCountX()
	{
		return CubesSettings.getInstance().getTexturesCountX();
	}

	@Override
	public int getTexturesCountY()
	{
		return CubesSettings.getInstance().getTexturesCountY();
	}

	@Override
	public boolean isOpaqueBlockPresent(int x, int y, int z)
	{
		return world.isOpaqueBlockPresent(x, y, z);
	}
}
