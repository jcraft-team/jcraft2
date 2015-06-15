package com.chappelle.jcraft.blocks;

import java.util.List;

import com.chappelle.jcraft.CubesSettings;
import com.chappelle.jcraft.Direction;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.lighting.LightMap;
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
    	float light = 1.0f;
    	if(chunk != null)
    	{
    		Block block = chunk.getBlock(location);
    		if(block.lightValue > 0)
    		{
    			light = (float)block.lightValue/LightMap.MAX_LIGHT;
    		}
    		else
    		{
    			LightMap lights = chunk.getLights();
    			if(!block.useNeighborLight())
    			{
    				light = lights.getNormalizedLight(location.x, location.y, location.z);
    			}
    			else
    			{
    				if(face == Block.Face.Top)
    				{
    					int neighborY = location.y+1;
    					if(neighborY < 256)
    					{
    						light = lights.getNormalizedLight(location.x, neighborY, location.z);
    					}
    					else
    					{
    						light = 0;
    					}
    				}
    				else if(face == Block.Face.Bottom)
    				{
    					int neighborY = location.y-1;
    					if(neighborY >= 0)
    					{
    						light = lights.getNormalizedLight(location.x, neighborY, location.z);
    					}
    					else
    					{
    						light = 0;
    					}
    				}
    				else if(face == Block.Face.Front)
    				{
    					int neighborZ = location.z + 1;
    					if(neighborZ < 16)
    					{
    						light = lights.getNormalizedLight(location.x, location.y, neighborZ);
    					}
    					else
    					{
    						World terrain = chunk.world;
    						Chunk neighborChunk = terrain.getChunkNeighbor(chunk, Direction.FRONT);
    						if(neighborChunk == null)
    						{
    							light = 0;
    						}
    						else
    						{
    							chunk.getLights().getNormalizedLight(location.x, location.y, 0);
    						}
    					}
    				}
    				else if(face == Block.Face.Back)
    				{
    					int neighborZ = location.z - 1;
    					if(neighborZ >= 0)
    					{
    						light = lights.getNormalizedLight(location.x, location.y, neighborZ);
    					}
    					else
    					{
    						World terrain = chunk.world;
    						Chunk neighborChunk = terrain.getChunkNeighbor(chunk, Direction.BACK);
    						if(neighborChunk == null)
    						{
    							light = 0;
    						}
    						else
    						{
    							chunk.getLights().getNormalizedLight(location.x, location.y, 15);
    						}
    					}
    				}
    				else if(face == Block.Face.Left)
    				{
    					int neighborX = location.x - 1;
    					if(neighborX >= 0)
    					{
    						light = lights.getNormalizedLight(neighborX, location.y, location.z);
    					}
    					else
    					{
    						World terrain = chunk.world;
    						Chunk neighborChunk = terrain.getChunkNeighbor(chunk, Direction.LEFT);
    						if(neighborChunk == null)
    						{
    							light = 0;
    						}
    						else
    						{
    							chunk.getLights().getNormalizedLight(15, location.y, location.z);
    						}
    					}
    				}
    				else if(face == Block.Face.Right)
    				{
    					int neighborX = location.x + 1;
    					if(neighborX < 16)
    					{
    						light = lights.getNormalizedLight(neighborX, location.y, location.z);
    					}
    					else
    					{
    						World terrain = chunk.world;
    						Chunk neighborChunk = terrain.getChunkNeighbor(chunk, Direction.RIGHT);
    						if(neighborChunk == null)
    						{
    							light = 0;
    						}
    						else
    						{
    							chunk.getLights().getNormalizedLight(0, location.y, location.z);
    						}
    					}
    			}
    			}
    		}
    	}
    	colors.add(light);
    	colors.add(light);
    	colors.add(light);
    	colors.add(1.0f);
    	
    	colors.add(light);
    	colors.add(light);
    	colors.add(light);
    	colors.add(1.0f);
    	
    	colors.add(light);
    	colors.add(light);
    	colors.add(light);
    	colors.add(1.0f);
    	
    	colors.add(light);
    	colors.add(light);
    	colors.add(light);
    	colors.add(1.0f);
    }
	
}
