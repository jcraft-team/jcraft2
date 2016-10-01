package com.chappelle.jcraft.blocks;

import java.util.*;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.lighting.LightType;
import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.*;

import gnu.trove.list.TFloatList;

public abstract class BlockShape
{
	protected Map<Face, Boolean> fullSide = new HashMap<>();
	
	public BlockShape()
	{
		for(Face face : Face.values())
		{
			fullSide.put(face, Boolean.TRUE);
		}
	}
	
    protected void addPositions(TFloatList positions, Vector3f positionsVec3f)
    {
    	positions.add(positionsVec3f.x);
    	positions.add(positionsVec3f.y);
    	positions.add(positionsVec3f.z);
    }

	public abstract void addTo(MeshGenContext meshGenContext, boolean isTransparent);

	/**
	 * Determines if a Shape's face should be added to the mesh. Checks neighbor blocks and across chunk boundaries if necessary.
	 * @param chunk The chunk containing this BlockShape
	 * @param blockLocation The location of the block
	 * @param face The block face in question
	 * @param isTransparent Flag indicating whether this is the transparent geometry or not
	 * @return true if the face should be added, false otherwise
	 */
//	protected boolean shouldFaceBeAdded(Chunk chunk, Vector3Int blockLocation, Block.Face face, boolean isTransparent)
	protected boolean shouldFaceBeAdded(MeshGenContext gen, Face face, boolean isTransparent)
	{
		Chunk chunk = gen.getChunk();
		Vector3Int blockLocation = gen.getLocation();
		if(chunk == null)
		{
			return true;
		}
		else
		{
			Block block = chunk.getBlock(blockLocation);
			Skin blockSkin = block.getSkin(chunk, blockLocation, face);
			if(blockSkin.isTransparent() == isTransparent)
			{
				Chunk neighborChunk = null;
				Vector3Int neighborBlockLocation = Face.getNeighborBlockLocalLocation(blockLocation, face);
				Block neighborBlock = chunk.getBlock(neighborBlockLocation);
				if(neighborBlock == null)//Check neighboring chunks
				{
					if(neighborBlockLocation.x < 0)
					{
						neighborChunk = gen.getChunkNeighbor(Direction.LEFT);
						if(neighborChunk != null)
						{
							neighborBlock = neighborChunk.getBlock(neighborBlockLocation.setX(15));
						}
					}
					else if(neighborBlockLocation.x > 15)
					{
						neighborChunk = gen.getChunkNeighbor(Direction.RIGHT);
						if(neighborChunk != null)
						{
							neighborBlock = neighborChunk.getBlock(neighborBlockLocation.setX(0));
						}
					}
					else if(neighborBlockLocation.z < 0)
					{
						neighborChunk = gen.getChunkNeighbor(Direction.BACK);
						if(neighborChunk != null)
						{
							neighborBlock = neighborChunk.getBlock(neighborBlockLocation.setZ(15));
						}
					}
					else if(neighborBlockLocation.z > 15)
					{
						neighborChunk = gen.getChunkNeighbor(Direction.FRONT);
						if(neighborChunk != null)
						{
							neighborBlock = neighborChunk.getBlock(neighborBlockLocation.setZ(0));
						}
					}
				}
				
				if(neighborBlock == null)
				{
					return true;
				}
				else
				{
					if(neighborChunk == null)
					{
						neighborChunk = chunk;
					}
					if(neighborBlock.isLiquid && block.isLiquid)
					{
						return false;
					}
					Skin neighborBlockSkin = neighborBlock.getSkin(neighborChunk, blockLocation, face);
					if(blockSkin.isTransparent() != neighborBlockSkin.isTransparent())
					{
						return true;
					}
					BlockShape neighborShape = neighborBlock.getShape(neighborChunk, neighborBlockLocation);
					return !isFullSide(face) || !neighborShape.isFullSide(Face.getOppositeFace(face));
				}
			}
			return false;
		}
	}

	protected boolean isFullSide(Face face)
	{
		return fullSide.get(face);
	}
	
	protected Vector2f getTextureCoordinates(Chunk chunk, TextureLocation textureLocation, float xUnitsToAdd, float yUnitsToAdd)
	{
		float textureUnitX = (1f / CubesSettings.getInstance().getTexturesCountX());
		float textureUnitY = (1f / CubesSettings.getInstance().getTexturesCountY());
		float x = (((textureLocation.getColumn() + xUnitsToAdd) * textureUnitX));
		float y = ((((-1 * textureLocation.getRow()) + (yUnitsToAdd - 1)) * textureUnitY) + 1);
		return new Vector2f(x, y);
	}

	protected float getTextureCoordinatesX(MeshGenContext gen, TextureLocation textureLocation, float xUnitsToAdd, float yUnitsToAdd)
	{
//		float textureUnitX = (1f / CubesSettings.getInstance().getTexturesCountX());
		float textureUnitX = (1f / gen.getTexturesCountX());
		return (((textureLocation.getColumn() + xUnitsToAdd) * textureUnitX));
	}

	protected float getTextureCoordinatesY(MeshGenContext gen, TextureLocation textureLocation, float xUnitsToAdd, float yUnitsToAdd)
	{
//		float textureUnitY = (1f / CubesSettings.getInstance().getTexturesCountY());
		float textureUnitY = (1f / gen.getTexturesCountY());
		return ((((-1 * textureLocation.getRow()) + (yUnitsToAdd - 1)) * textureUnitY) + 1);
	}
	
    protected void addLighting(MeshGenContext gen, Face face)
    {
    	TFloatList colors = gen.getColorList();
    	Chunk chunk = gen.getChunk();
    	Vector3Int location = gen.getLocation();
    	int blockLight = 15;
    	int skyLight = 15;
    	Block block = gen.getBlock();
    	Chunk chunkToLight = chunk;
    	boolean noLight = false;
		int x = location.x;
		int y = location.y;
		int z = location.z;
		if(block.useNeighborLight())//TODO: Investigate how this is different from transparent?
		{
			if(face == Face.Top)
			{
				y = (location.y+1)&255;
			}
			else if(face == Face.Bottom)
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
			else if(face == Face.Front)
			{
				int neighborZ = location.z + 1;
				if(neighborZ < 16)
				{
					z = neighborZ;
				}
				else
				{
					Chunk neighborChunk = gen.getChunkNeighbor(Direction.FRONT);
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
			else if(face == Face.Back)
			{
				int neighborZ = location.z - 1;
				if(neighborZ >= 0)
				{
					z = neighborZ;
				}
				else
				{
					Chunk neighborChunk = gen.getChunkNeighbor(Direction.BACK);
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
			else if(face == Face.Left)
			{
				int neighborX = location.x - 1;
				if(neighborX >= 0)
				{
					x = neighborX;
				}
				else
				{
					Chunk neighborChunk = gen.getChunkNeighbor(Direction.LEFT);
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
			else if(face == Face.Right)
			{
				int neighborX = location.x + 1;
				if(neighborX < 16)
				{
					x = neighborX;
				}
				else
				{
					Chunk neighborChunk = gen.getChunkNeighbor(Direction.RIGHT);
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
			float vertexAOMult = GameSettings.ambientOcclusionIntensity;
			
			if(face == Face.Back)
			{
				//bottom left corner
				side1 = gen.isOpaqueBlockPresent(worldX+1, y, worldZ-1);
				side2 = gen.isOpaqueBlockPresent(worldX, y-1, worldZ-1);
				cornerOpacity = getBlockOpacity(gen, worldX+1, y-1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				//bottom right corner
				side1 = gen.isOpaqueBlockPresent(worldX-1, y, worldZ-1);
				side2 = gen.isOpaqueBlockPresent(worldX, y-1, worldZ-1);
				cornerOpacity = getBlockOpacity(gen, worldX-1, y-1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				//Top left corner
				side1 = gen.isOpaqueBlockPresent(worldX+1, y+1, worldZ-1);
				side2 = gen.isOpaqueBlockPresent(worldX, y+1, worldZ-1);
				cornerOpacity = getBlockOpacity(gen, worldX+1, y+1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				//Top right corner
				side1 = gen.isOpaqueBlockPresent(worldX, y+1, worldZ-1);
				side2 = gen.isOpaqueBlockPresent(worldX-1, y, worldZ-1);
				cornerOpacity = getBlockOpacity(gen, worldX-1, y+1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
			}
			else if(face == Face.Front)
			{
				//bottom left
				side1 = gen.isOpaqueBlockPresent(worldX-1, y, worldZ+1);
				side2 = gen.isOpaqueBlockPresent(worldX, y-1, worldZ+1);
				cornerOpacity = getBlockOpacity(gen, worldX-1, y-1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = gen.isOpaqueBlockPresent(worldX, y-1, worldZ+1);
				side2 = gen.isOpaqueBlockPresent(worldX+1, y, worldZ+1);
				cornerOpacity = getBlockOpacity(gen, worldX+1, y-1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = gen.isOpaqueBlockPresent(worldX-1, y, worldZ+1);
				side2 = gen.isOpaqueBlockPresent(worldX, y+1, worldZ+1);
				cornerOpacity = getBlockOpacity(gen, worldX-1, y+1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = gen.isOpaqueBlockPresent(worldX+1, y, worldZ+1);
				side2 = gen.isOpaqueBlockPresent(worldX, y+1, worldZ+1);
				cornerOpacity = getBlockOpacity(gen, worldX+1, y+1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
			}
			else if(face == Face.Left)
			{
				//bottom left
				side1 = gen.isOpaqueBlockPresent(worldX-1, y, worldZ-1);
				side2 = gen.isOpaqueBlockPresent(worldX-1, y-1, worldZ);
				cornerOpacity = getBlockOpacity(gen, worldX-1, y-1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = gen.isOpaqueBlockPresent(worldX-1, y, worldZ+1);
				side2 = gen.isOpaqueBlockPresent(worldX-1, y-1, worldZ);
				cornerOpacity = getBlockOpacity(gen, worldX-1, y-1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = gen.isOpaqueBlockPresent(worldX-1, y+1, worldZ);
				side2 = gen.isOpaqueBlockPresent(worldX-1, y+1, worldZ-1);
				cornerOpacity = getBlockOpacity(gen, worldX-1, y+1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = gen.isOpaqueBlockPresent(worldX-1, y+1, worldZ);
				side2 = gen.isOpaqueBlockPresent(worldX-1, y+1, worldZ+1);
				cornerOpacity = getBlockOpacity(gen, worldX-1, y+1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
			}
			else if(face == Face.Right)
			{
				//bottom left
				side1 = gen.isOpaqueBlockPresent(worldX+1, y, worldZ+1);
				side2 = gen.isOpaqueBlockPresent(worldX+1, y-1, worldZ);
				cornerOpacity = getBlockOpacity(gen, worldX+1, y-1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = gen.isOpaqueBlockPresent(worldX+1, y, worldZ-1);
				side2 = gen.isOpaqueBlockPresent(worldX+1, y-1, worldZ);
				cornerOpacity = getBlockOpacity(gen, worldX+1, y-1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = gen.isOpaqueBlockPresent(worldX+1, y, worldZ+1);
				side2 = gen.isOpaqueBlockPresent(worldX+1, y+1, worldZ);
				cornerOpacity = getBlockOpacity(gen, worldX+1, y+1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = gen.isOpaqueBlockPresent(worldX+1, y+1, worldZ);
				side2 = gen.isOpaqueBlockPresent(worldX+1, y, worldZ-1);
				cornerOpacity = getBlockOpacity(gen, worldX+1, y+1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
			}
			else if(face == Face.Top)
			{
				//bottom left
				side1 = gen.isOpaqueBlockPresent(worldX, y+1, worldZ+1);
				side2 = gen.isOpaqueBlockPresent(worldX-1, y+1, worldZ);
				cornerOpacity = getBlockOpacity(gen, worldX-1, y+1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = gen.isOpaqueBlockPresent(worldX+1, y+1, worldZ);
				side2 = gen.isOpaqueBlockPresent(worldX, y+1, worldZ+1);
				cornerOpacity = getBlockOpacity(gen, worldX+1, y+1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = gen.isOpaqueBlockPresent(worldX-1, y+1, worldZ);
				side2 = gen.isOpaqueBlockPresent(worldX, y+1, worldZ-1);
				cornerOpacity = getBlockOpacity(gen, worldX-1, y+1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = gen.isOpaqueBlockPresent(worldX+1, y+1, worldZ);
				side2 = gen.isOpaqueBlockPresent(worldX, y+1, worldZ-1);
				cornerOpacity = getBlockOpacity(gen, worldX+1, y+1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
			}
			else if(face == Face.Bottom)
			{
				//bottom left
				side1 = gen.isOpaqueBlockPresent(worldX, y-1, worldZ+1);
				side2 = gen.isOpaqueBlockPresent(worldX+1, y-1, worldZ);
				cornerOpacity = getBlockOpacity(gen, worldX+1, y-1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = gen.isOpaqueBlockPresent(worldX, y-1, worldZ+1);
				side2 = gen.isOpaqueBlockPresent(worldX-1, y-1, worldZ);
				cornerOpacity = getBlockOpacity(gen, worldX-1, y-1, worldZ+1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = gen.isOpaqueBlockPresent(worldX, y-1, worldZ-1);
				side2 = gen.isOpaqueBlockPresent(worldX+1, y-1, worldZ);
				cornerOpacity = getBlockOpacity(gen, worldX+1, y-1, worldZ-1);
				vertexAO = vertexAO(side1, side2, cornerOpacity)*vertexAOMult;
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add((float)effectiveBlockLight);
				colors.add(Math.max(skyLight - vertexAO, 0.0f));
				
				side1 = gen.isOpaqueBlockPresent(worldX-1, y-1, worldZ);
				side2 = gen.isOpaqueBlockPresent(worldX, y-1, worldZ-1);
				cornerOpacity = getBlockOpacity(gen, worldX-1, y-1, worldZ-1);
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
    
    private float getBlockOpacity(MeshGenContext gen, int x, int y, int z)
    {
    	Block block = gen.getBlock(x, y, z);
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
