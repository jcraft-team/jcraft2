package com.chappelle.jcraft;

import java.util.List;

import com.chappelle.jcraft.lighting.LightMap;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public abstract class BlockShape
{
	private boolean isTransparent;
	protected List<Vector3f> positions;
	protected List<Short> indices;
	protected List<Float> normals;
	protected List<Float> colors;
	protected List<Vector2f> textureCoordinates;

	public void prepare(boolean isTransparent, MeshData meshData)
	{
		this.positions = meshData.positionsList;
		this.indices = meshData.indicesList;
		this.normals = meshData.normalsList;
		this.textureCoordinates = meshData.textureCoordinatesList;
		this.colors = meshData.colorList;
		this.isTransparent = isTransparent;
	}

	public abstract void addTo(Chunk chunk, Block block, Vector3Int blockLocation);

	protected boolean shouldFaceBeAdded(Chunk chunk, Vector3Int blockLocation, Block.Face face)
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
				Vector3Int neighborBlockLocation = BlockNavigator.getNeighborBlockLocalLocation(blockLocation, face);
				Block neighborBlock = chunk.getBlock(neighborBlockLocation);
				if(neighborBlock != null)
				{
					BlockSkin neighborBlockSkin = neighborBlock.getSkin(chunk, blockLocation, face);
					if(blockSkin.isTransparent() != neighborBlockSkin.isTransparent())
					{
						return true;
					}
					BlockShape neighborShape = neighborBlock.getShape(chunk, neighborBlockLocation);
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
	
    protected void addLighting(Chunk chunk, Vector3Int location, Block.Face face)
    {
    	float light = 1.0f;
    	if(chunk != null)
    	{
    		Block block = chunk.getBlock(location);
    		if(block.getBlockLightValue() > 0)
    		{
    			light = (float)block.getBlockLightValue()/LightMap.MAX_LIGHT;
    		}
    		else
    		{
    			LightMap lights = chunk.getLights();
    			if(face == Block.Face.Top)
    			{
    				int neighborY = location.y+1;
    				if(neighborY < 32)
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
    				if(neighborZ < 32)
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
    						chunk.getLights().getNormalizedLight(location.x, location.y, 31);
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
    						chunk.getLights().getNormalizedLight(31, location.y, location.z);
    					}
    				}
    			}
    			else if(face == Block.Face.Right)
    			{
    				int neighborX = location.x + 1;
    				if(neighborX < 32)
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
