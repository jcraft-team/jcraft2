package com.chappelle.jcraft.blocks;

import java.util.List;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.lighting.LightType;
import com.chappelle.jcraft.util.BlockNavigator;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector2f;

public abstract class BlockShape
{
	public abstract void addTo(MeshData meshData, Chunk chunk, Block block, Vector3Int blockLocation, boolean isTransparent);

	/**
	 * Determines if a Shape's face should be added to the mesh. Checks neighbor blocks and across chunk boundaries if necessary.
	 * @param chunk The chunk containing this BlockShape
	 * @param blockLocation The location of the block
	 * @param face The block face in question
	 * @param isTransparent Flag indicating whether this is the transparent geometry or not
	 * @return true if the face should be added, false otherwise
	 */
	protected boolean shouldFaceBeAdded(Chunk chunk, Vector3Int blockLocation, Block.Face face, boolean isTransparent)
	{
		if(chunk == null)
		{
			return true;
		}
		else
		{
			Block block = chunk.getBlock(blockLocation);
			BlockSkin blockSkin = block.getSkin(chunk, blockLocation, face);
			if(blockSkin.isTransparent() == isTransparent)
			{
				Chunk neighborChunk = null;
				Block neighborBlock = null;
				Vector3Int neighborBlockLocation = BlockNavigator.getNeighborBlockLocalLocation(blockLocation, face);
				neighborBlock = chunk.getBlock(neighborBlockLocation);
				if(neighborBlock == null)//Check neighboring chunks
				{
					if(neighborBlockLocation.x < 0)
					{
						neighborChunk = chunk.getChunkNeighbor(Direction.LEFT);
						if(neighborChunk != null)
						{
							neighborBlock = neighborChunk.getBlock(neighborBlockLocation.setX(15));
						}
					}
					else if(neighborBlockLocation.x > 15)
					{
						neighborChunk = chunk.getChunkNeighbor(Direction.RIGHT);
						if(neighborChunk != null)
						{
							neighborBlock = neighborChunk.getBlock(neighborBlockLocation.setX(0));
						}
					}
					else if(neighborBlockLocation.z < 0)
					{
						neighborChunk = chunk.getChunkNeighbor(Direction.BACK);
						if(neighborChunk != null)
						{
							neighborBlock = neighborChunk.getBlock(neighborBlockLocation.setZ(15));
						}
					}
					else if(neighborBlockLocation.z > 15)
					{
						neighborChunk = chunk.getChunkNeighbor(Direction.FRONT);
						if(neighborChunk != null)
						{
							neighborBlock = neighborChunk.getBlock(neighborBlockLocation.setZ(0));
						}
					}
				}
				
				if(neighborBlock != null)
				{
					if(neighborChunk == null)
					{
						neighborChunk = chunk;
					}
					BlockSkin neighborBlockSkin = neighborBlock.getSkin(neighborChunk, blockLocation, face);
					if(blockSkin.isTransparent() != neighborBlockSkin.isTransparent())
					{
						return true;
					}
					BlockShape neighborShape = neighborBlock.getShape(neighborChunk, neighborBlockLocation);
					return (!(canBeMerged(face) && neighborShape.canBeMerged(BlockNavigator.getOppositeFace(face))));
				}
				return true;
			}
			return false;
		}
	}

	protected abstract boolean canBeMerged(Block.Face face);

	protected Vector2f getTextureCoordinates(Chunk chunk, BlockSkin_TextureLocation textureLocation, float xUnitsToAdd, float yUnitsToAdd)
	{
		float textureUnitX = (1f / CubesSettings.getInstance().getTexturesCountX());
		float textureUnitY = (1f / CubesSettings.getInstance().getTexturesCountY());
		float x = (((textureLocation.getColumn() + xUnitsToAdd) * textureUnitX));
		float y = ((((-1 * textureLocation.getRow()) + (yUnitsToAdd - 1)) * textureUnitY) + 1);
		return new Vector2f(x, y);
	}
	
    protected void addLighting(List<Float> colors, Chunk chunk, Vector3Int location, Block.Face face)
    {
    	int blockLight = 15;
    	int skyLight = 15;
    	Block block = chunk.getBlock(location);
    	Chunk chunkToLight = chunk;
    	boolean noLight = false;
		int x = location.x;
		int y = location.y;
		int z = location.z;
		World world = chunk.world;
		if(block.useNeighborLight())//TODO: Investigate how this is different from transparent?
		{
			if(face == Block.Face.Top)
			{
				y = (location.y+1)&255;
			}
			else if(face == Block.Face.Bottom)
			{
				int neighborY = location.y-1;
				if(neighborY >= 0)
				{
					y = neighborY;
				}
				else
				{
					y = 0;
				}
			}
			else if(face == Block.Face.Front)
			{
				int neighborZ = location.z + 1;
				if(neighborZ < 16)
				{
					z = neighborZ;
				}
				else
				{
					Chunk neighborChunk = world.getChunkNeighbor(chunk, Direction.FRONT);
					if(neighborChunk == null)
					{
						noLight = true;
					}
					else
					{
						chunkToLight = neighborChunk;
						z = 0;
					}
				}
			}
			else if(face == Block.Face.Back)
			{
				int neighborZ = location.z - 1;
				if(neighborZ >= 0)
				{
					z = neighborZ;
				}
				else
				{
					Chunk neighborChunk = world.getChunkNeighbor(chunk, Direction.BACK);
					if(neighborChunk == null)
					{
						noLight = true;
					}
					else
					{
						z = 15;
						chunkToLight = neighborChunk;
					}
				}
			}
			else if(face == Block.Face.Left)
			{
				int neighborX = location.x - 1;
				if(neighborX >= 0)
				{
					x = neighborX;
				}
				else
				{
					Chunk neighborChunk = world.getChunkNeighbor(chunk, Direction.LEFT);
					if(neighborChunk == null)
					{
						noLight = true;
					}
					else
					{
						x = 15;
						chunkToLight = neighborChunk;
					}
				}
			}
			else if(face == Block.Face.Right)
			{
				int neighborX = location.x + 1;
				if(neighborX < 16)
				{
					x = neighborX;
				}
				else
				{
					Chunk neighborChunk = world.getChunkNeighbor(chunk, Direction.RIGHT);
					if(neighborChunk == null)
					{
						noLight = true;
					}
					else
					{
						x = 0;
						chunkToLight = neighborChunk;
					}
				}
			}
    	}

		if(noLight)
		{
			blockLight = 0;
			skyLight = 0;
		}
		else
		{
			if(block.lightValue > 0)
			{
				blockLight = block.lightValue;
			}
			else
			{
				blockLight = chunkToLight.getLight(x, y, z, LightType.BLOCK);
			}
			skyLight = chunkToLight.getLight(x, y, z, LightType.SKY);
		}

		float effectiveBlockLight = Math.max(blockLight, 0.35f);

		if(GameSettings.ambientOcclusionEnabled)
		{
			//start ambient occlusion
			x = location.x;
			y = location.y;
			z = location.z;
			
			int worldX = x + chunk.blockLocation.x;
			int worldZ = z + chunk.blockLocation.z;
			boolean side1 = false;
			boolean side2 = false;
			float cornerOpacity = 0.0f;
			float vertexAO = 0;
			float vertexAOMult = 1.5f;
			
			if(face == Block.Face.Back)
			{
				//bottom left corner
				side1 = world.isOpaqueBlockPresent(worldX+1, y, worldZ-1);
				side2 = world.isOpaqueBlockPresent(worldX, y-1, worldZ-1);
				cornerOpacity = getBlockOpacity(world, worldX+1, y-1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				//bottom right corner
				side1 = world.isOpaqueBlockPresent(worldX-1, y, worldZ-1);
				side2 = world.isOpaqueBlockPresent(worldX, y-1, worldZ-1);
				cornerOpacity = getBlockOpacity(world, worldX-1, y-1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				//Top left corner
				side1 = world.isOpaqueBlockPresent(worldX+1, y+1, worldZ-1);
				side2 = world.isOpaqueBlockPresent(worldX, y+1, worldZ-1);
				cornerOpacity = getBlockOpacity(world, worldX+1, y+1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				//Top right corner
				side1 = world.isOpaqueBlockPresent(worldX, y+1, worldZ-1);
				side2 = world.isOpaqueBlockPresent(worldX-1, y, worldZ-1);
				cornerOpacity = getBlockOpacity(world, worldX-1, y+1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
			}
			else if(face == Block.Face.Front)
			{
				//bottom left
				side1 = world.isOpaqueBlockPresent(worldX-1, y, worldZ+1);
				side2 = world.isOpaqueBlockPresent(worldX, y-1, worldZ+1);
				cornerOpacity = getBlockOpacity(world, worldX-1, y-1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = world.isOpaqueBlockPresent(worldX, y-1, worldZ-1);
				side2 = world.isOpaqueBlockPresent(worldX+1, y, worldZ+1);
				cornerOpacity = getBlockOpacity(world, worldX+1, y-1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = world.isOpaqueBlockPresent(worldX-1, y, worldZ+1);
				side2 = world.isOpaqueBlockPresent(worldX, y+1, worldZ+1);
				cornerOpacity = getBlockOpacity(world, worldX-1, y+1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = world.isOpaqueBlockPresent(worldX+1, y, worldZ+1);
				side2 = world.isOpaqueBlockPresent(worldX, y+1, worldZ+1);
				cornerOpacity = getBlockOpacity(world, worldX+1, y+1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
			}
			else if(face == Block.Face.Left)
			{
				//bottom left
				side1 = world.isOpaqueBlockPresent(worldX-1, y, worldZ-1);
				side2 = world.isOpaqueBlockPresent(worldX-1, y-1, worldZ);
				cornerOpacity = getBlockOpacity(world, worldX-1, y-1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = world.isOpaqueBlockPresent(worldX-1, y, worldZ+1);
				side2 = world.isOpaqueBlockPresent(worldX-1, y-1, worldZ);
				cornerOpacity = getBlockOpacity(world, worldX-1, y-1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = world.isOpaqueBlockPresent(worldX-1, y+1, worldZ);
				side2 = world.isOpaqueBlockPresent(worldX-1, y+1, worldZ-1);
				cornerOpacity = getBlockOpacity(world, worldX-1, y+1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = world.isOpaqueBlockPresent(worldX-1, y+1, worldZ);
				side2 = world.isOpaqueBlockPresent(worldX-1, y+1, worldZ+1);
				cornerOpacity = getBlockOpacity(world, worldX-1, y+1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
			}
			else if(face == Block.Face.Right)
			{
				//bottom left
				side1 = world.isOpaqueBlockPresent(worldX+1, y, worldZ+1);
				side2 = world.isOpaqueBlockPresent(worldX+1, y-1, worldZ);
				cornerOpacity = getBlockOpacity(world, worldX+1, y-1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = world.isOpaqueBlockPresent(worldX+1, y, worldZ-1);
				side2 = world.isOpaqueBlockPresent(worldX+1, y-1, worldZ);
				cornerOpacity = getBlockOpacity(world, worldX+1, y-1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = world.isOpaqueBlockPresent(worldX+1, y, worldZ+1);
				side2 = world.isOpaqueBlockPresent(worldX+1, y+1, worldZ);
				cornerOpacity = getBlockOpacity(world, worldX+1, y+1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = world.isOpaqueBlockPresent(worldX+1, y+1, worldZ);
				side2 = world.isOpaqueBlockPresent(worldX-1, y, worldZ+1);
				cornerOpacity = getBlockOpacity(world, worldX+1, y+1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
			}
			else if(face == Block.Face.Top)
			{
				//bottom left
				side1 = world.isOpaqueBlockPresent(worldX, y+1, worldZ+1);
				side2 = world.isOpaqueBlockPresent(worldX-1, y+1, worldZ);
				cornerOpacity = getBlockOpacity(world, worldX-1, y+1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = world.isOpaqueBlockPresent(worldX+1, y+1, worldZ);
				side2 = world.isOpaqueBlockPresent(worldX, y+1, worldZ+1);
				cornerOpacity = getBlockOpacity(world, worldX+1, y+1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = world.isOpaqueBlockPresent(worldX-1, y+1, worldZ);
				side2 = world.isOpaqueBlockPresent(worldX, y+1, worldZ-1);
				cornerOpacity = getBlockOpacity(world, worldX-1, y+1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = world.isOpaqueBlockPresent(worldX+1, y+1, worldZ);
				side2 = world.isOpaqueBlockPresent(worldX, y+1, worldZ-1);
				cornerOpacity = getBlockOpacity(world, worldX+1, y+1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
			}
			else if(face == Block.Face.Bottom)
			{
				//bottom left
				side1 = world.isOpaqueBlockPresent(worldX, y-1, worldZ+1);
				side2 = world.isOpaqueBlockPresent(worldX-1, y-1, worldZ);
				cornerOpacity = getBlockOpacity(world, worldX+1, y-1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = world.isOpaqueBlockPresent(worldX, y-1, worldZ+1);
				side2 = world.isOpaqueBlockPresent(worldX-1, y-11, worldZ);
				cornerOpacity = getBlockOpacity(world, worldX-1, y-1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = world.isOpaqueBlockPresent(worldX, y-1, worldZ-1);
				side2 = world.isOpaqueBlockPresent(worldX+1, y-1, worldZ);
				cornerOpacity = getBlockOpacity(world, worldX+1, y-1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = world.isOpaqueBlockPresent(worldX-1, y-1, worldZ);
				side2 = world.isOpaqueBlockPresent(worldX, y-1, worldZ-1);
				cornerOpacity = getBlockOpacity(world, worldX-1, y-1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
			}
		}
		else
		{
			colors.add((float)effectiveBlockLight);
			colors.add((float)effectiveBlockLight);
			colors.add((float)effectiveBlockLight);
			colors.add((float)skyLight);

			colors.add((float)effectiveBlockLight);
			colors.add((float)effectiveBlockLight);
			colors.add((float)effectiveBlockLight);
			colors.add((float)skyLight);
			
			colors.add((float)effectiveBlockLight);
			colors.add((float)effectiveBlockLight);
			colors.add((float)effectiveBlockLight);
			colors.add((float)skyLight);
			
			colors.add((float)effectiveBlockLight);
			colors.add((float)effectiveBlockLight);
			colors.add((float)effectiveBlockLight);
			colors.add((float)skyLight);
		}
    }
    
    private float getBlockOpacity(World world, int x, int y, int z)
    {
    	Block block = world.getBlock(x, y, z);
    	if(block != null)
    	{
    		return block.isTransparent ? 0.0f : block.opacity;
    	}
    	return 0.0f;
    }
    
    //Calculate ambient occlusion
    //https://0fps.net/2013/07/03/ambient-occlusion-for-minecraft-like-worlds/
    private float vertexAO(boolean side1, boolean side2, float cornerOpacity) 
    {
    	if(side1 && side2)
    	{
    		return 3;
    	}
		int side1Int = side1 == false ? 0 : 1;
		int side2Int = side2 == false ? 0 : 1;
		return side1Int + side2Int + cornerOpacity;
    }    
}
