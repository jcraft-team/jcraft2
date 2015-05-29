package com.chappelle.jcraft.lighting;

import java.util.LinkedList;
import java.util.Queue;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.Chunk;
import com.chappelle.jcraft.Direction;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.World;

public class FloodFillLightManager implements LightManager
{
	private final int chunkWidth = 32;
	private boolean sunlightEnabled = true;
	
	private World terrain;
	private Queue<LightNode> lightAdditionQueue;
	private Queue<LightRemovalNode> lightRemovalQueue;
	private Queue<LightNode> sunlightAdditionQueue;
	private Queue<LightRemovalNode> sunlightRemovalQueue;
	
	public FloodFillLightManager(World terrain)
	{
		this.terrain = terrain;
		lightAdditionQueue = new LinkedList<>();
		lightRemovalQueue = new LinkedList<>();
		sunlightAdditionQueue = new LinkedList<>();
		sunlightRemovalQueue = new LinkedList<>();
	}
	
	@Override
	public void calculateLight()
	{
		propagateRemovedBlockLights();
		propagateAddedBlockLights();
		
		if(sunlightEnabled)
		{
			propagateRemovedSunlight();
			propagateAddedSunlight();
		}
		else
		{
			sunlightAdditionQueue.clear();
			sunlightRemovalQueue.clear();
		}
	
	}

	private void propagateRemovedBlockLights()
	{
		LightRemovalNode node = lightRemovalQueue.poll();
		while(node != null)
		{
			Chunk chunk = node.chunk;
			short index = node.index;
			int x = index % chunkWidth;
			int y = index / (chunkWidth * chunkWidth); 
			int z = (index % (chunkWidth * chunkWidth) ) / chunkWidth;
			int lightLevel = node.val;
			
			Vector3Int location = new Vector3Int(x, y, z);
			//Negative X neighbor
			location.x = x - 1;
			if(location.x >= 0)//Check chunk boundary 
			{
				propagateRemovedBlockLights(chunk, lightLevel, location);
			}
			else
			{
				propagateRemovedBlockLights(terrain.getChunkNeighbor(chunk, Direction.LEFT), lightLevel, location.setX(chunkWidth-1));
			}
			

			//Positive X neighbor
			location.x = x + 1;
			location.y = y;
			location.z = z;
			if(location.x < chunkWidth)//Check chunk boundary 
			{
				propagateRemovedBlockLights(chunk, lightLevel, location);
			}
			else
			{
				propagateRemovedBlockLights(terrain.getChunkNeighbor(chunk, Direction.RIGHT), lightLevel, location.setX(0));
			}
			
			//Negative Y neighbor
			location.x = x;
			location.y = y - 1;
			location.z = z;
			if(location.y >= 0)//Check chunk boundary 
			{
				propagateRemovedBlockLights(chunk, lightLevel, location);
			}

			//Positive Y neighbor
			location.x = x;
			location.y = y + 1;
			location.z = z;
			if(location.y < chunkWidth)//Check chunk boundary 
			{
				propagateRemovedBlockLights(chunk, lightLevel, location);
			}
			
			//Negative Z neighbor
			location.x = x;
			location.y = y;
			location.z = z - 1;
			if(location.z >= 0)//Check chunk boundary 
			{
				propagateRemovedBlockLights(chunk, lightLevel, location);
			}
			else
			{
				propagateRemovedBlockLights(terrain.getChunkNeighbor(chunk, Direction.BACK), lightLevel, location.setZ(chunkWidth-1));
			}
			
			//Positive Z neighbor
			location.x = x;
			location.y = y;
			location.z = z + 1;
			if(location.z < chunkWidth)//Check chunk boundary 
			{
				propagateRemovedBlockLights(chunk, lightLevel, location);
			}
			else
			{
				propagateRemovedBlockLights(terrain.getChunkNeighbor(chunk, Direction.FRONT), lightLevel, location.setZ(0));
			}
			node = lightRemovalQueue.poll();
		}
	}

	private void propagateRemovedBlockLights(Chunk chunk, int lightLevel, Vector3Int location)
	{
		if(chunk != null)
		{
			int neighborLevel = chunk.getLight(location.x, location.y, location.z, LightType.BLOCK);
			if(neighborLevel != 0 && neighborLevel < lightLevel)
			{
				chunk.setLight(location.x, location.y, location.z, LightType.BLOCK, 0);
				lightRemovalQueue.add(new LightRemovalNode(index(location), (short)neighborLevel, chunk));
			}
			else if(neighborLevel >= lightLevel)
			{
				lightAdditionQueue.add(new LightNode(index(location), chunk));
			}
		}
	}
	
	private void propagateAddedBlockLights()
	{
		LightNode node = lightAdditionQueue.poll();
		while(node != null)
		{
			Chunk chunk = node.chunk;
			short index = node.index;
			int x = index % chunkWidth;
			int y = index / (chunkWidth * chunkWidth); 
			int z = (index % (chunkWidth * chunkWidth) ) / chunkWidth;
			Vector3Int location = new Vector3Int(x, y, z);
			
			int lightLevel = chunk.getLight(location.x, location.y, location.z, LightType.BLOCK);
			//Negative X neighbor
			location.x = x - 1;
			if(location.x >= 0)//Check chunk boundary 
			{
				propagateAddedBlockLights(chunk, location, lightLevel);
			}
			else
			{
				propagateAddedBlockLights(terrain.getChunkNeighbor(chunk, Direction.LEFT), location.setX(chunkWidth-1), lightLevel);
			}
			

			//Positive X neighbor
			location.x = x + 1;
			location.y = y;
			location.z = z;
			if(location.x < chunkWidth)//Check chunk boundary 
			{
				propagateAddedBlockLights(chunk, location, lightLevel);
			}
			else
			{
				propagateAddedBlockLights(terrain.getChunkNeighbor(chunk, Direction.RIGHT), location.setX(0), lightLevel);
			}
			
			//Negative Y neighbor
			location.x = x;
			location.y = y - 1;
			location.z = z;
			if(location.y >= 0)//Check chunk boundary 
			{
				propagateAddedBlockLights(chunk, location, lightLevel);
			}

			//Positive Y neighbor
			location.x = x;
			location.y = y + 1;
			location.z = z;
			if(location.y < chunkWidth)//Check chunk boundary 
			{
				propagateAddedBlockLights(chunk, location, lightLevel);
			}
			
			//Negative Z neighbor
			location.x = x;
			location.y = y;
			location.z = z - 1;
			if(location.z >= 0)//Check chunk boundary 
			{
				propagateAddedBlockLights(chunk, location, lightLevel);
			}
			else
			{
				propagateAddedBlockLights(terrain.getChunkNeighbor(chunk, Direction.BACK), location.setZ(chunkWidth-1), lightLevel);
			}
			
			//Positive Z neighbor
			location.x = x;
			location.y = y;
			location.z = z + 1;
			if(location.z < chunkWidth)//Check chunk boundary 
			{
				propagateAddedBlockLights(chunk, location, lightLevel);
			}
			else
			{
				propagateAddedBlockLights(terrain.getChunkNeighbor(chunk, Direction.FRONT), location.setZ(0), lightLevel);
			}
			node = lightAdditionQueue.poll();	
		}
	}

	private void propagateAddedBlockLights(Chunk chunk, Vector3Int location, int lightLevel)
	{
		if(needsLightUpdated(chunk, location, LightType.BLOCK, lightLevel))
		{
			chunk.setLight(location.x, location.y, location.z, LightType.BLOCK, lightLevel - 1);
			lightAdditionQueue.add(new LightNode(index(location), chunk));
		}
	}

	private void propagateAddedSunlight()
	{
		LightNode node = sunlightAdditionQueue.poll();
		while(node != null)
		{
			Chunk chunk = node.chunk;
			short index = node.index;
			int x = index % chunkWidth;
			int y = index / (chunkWidth * chunkWidth); 
			int z = (index % (chunkWidth * chunkWidth) ) / chunkWidth;
			Vector3Int location = new Vector3Int(x, y, z);
			
			int lightLevel = chunk.getLight(location.x, location.y, location.z, LightType.SKY);
			//Negative X neighbor
			location.x = x - 1;
			if(location.x >= 0)//Check chunk boundary 
			{
				propagateAddedSunlight(chunk, location, lightLevel);
			}
			else
			{
				propagateAddedSunlight(terrain.getChunkNeighbor(chunk, Direction.LEFT), location.setX(chunkWidth-1), lightLevel);
			}

			//Positive X neighbor
			location.x = x + 1;
			location.y = y;
			location.z = z;
			if(location.x < chunkWidth)//Check chunk boundary 
			{
				propagateAddedSunlight(chunk, location, lightLevel);
			}
			else
			{
				propagateAddedSunlight(terrain.getChunkNeighbor(chunk, Direction.RIGHT), location.setX(0), lightLevel);
			}
			
			
			//Negative Y neighbor
			location.x = x;
			location.y = y - 1;
			location.z = z;
			if(location.y >= 0)//Check chunk boundary 
			{
				if(needsLightUpdated(chunk, location, LightType.SKY, lightLevel))
				{
					if(lightLevel == LightMap.MAX_LIGHT)
					{
						chunk.setLight(location.x, location.y, location.z, LightType.SKY, lightLevel);
					}
					else
					{
						chunk.setLight(location.x, location.y, location.z, LightType.SKY, lightLevel - 1);
					}
					sunlightAdditionQueue.add(new LightNode(index(location), chunk));
				}
			}

			//Positive Y neighbor
			location.x = x;
			location.y = y + 1;
			location.z = z;
			if(location.y < chunkWidth)//Check chunk boundary 
			{
				propagateAddedSunlight(chunk, location, lightLevel);
			}

			//Negative Z neighbor
			location.x = x;
			location.y = y;
			location.z = z - 1;
			if(location.z >= 0)//Check chunk boundary 
			{
				propagateAddedSunlight(chunk, location, lightLevel);
			}
			else
			{
				propagateAddedSunlight(terrain.getChunkNeighbor(chunk, Direction.BACK), location.setZ(chunkWidth-1), lightLevel);
			}
			
			//Positive Z neighbor
			location.x = x;
			location.y = y;
			location.z = z + 1;
			if(location.z < chunkWidth)//Check chunk boundary 
			{
				propagateAddedSunlight(chunk, location, lightLevel);
			}
			else
			{
				propagateAddedSunlight(terrain.getChunkNeighbor(chunk, Direction.FRONT), location.setZ(0), lightLevel);
			}
			
			node = sunlightAdditionQueue.poll();
		}
	}

	private void propagateAddedSunlight(Chunk chunk, Vector3Int location, int lightLevel)
	{
		if(needsLightUpdated(chunk, location, LightType.SKY, lightLevel))
		{
			chunk.setLight(location.x, location.y, location.z, LightType.SKY, lightLevel - 1);
			sunlightAdditionQueue.add(new LightNode(index(location), chunk));
		}
	}
	
	private void propagateRemovedSunlight()
	{
		LightRemovalNode node = sunlightRemovalQueue.poll();
		while(node != null)
		{
			Chunk chunk = node.chunk;
			short index = node.index;
			int x = index % chunkWidth;
			int y = index / (chunkWidth * chunkWidth); 
			int z = (index % (chunkWidth * chunkWidth) ) / chunkWidth;
			int lightLevel = node.val;
			
			Vector3Int location = new Vector3Int(x, y, z);
			//Negative X neighbor
			location.x = x - 1;
			int neighborLevel = 0;
			if(location.x >= 0)//Check chunk boundary 
			{
				propagateRemovedSunlight(chunk, lightLevel, location);
			}
			else
			{
				propagateRemovedSunlight(terrain.getChunkNeighbor(chunk, Direction.LEFT), lightLevel, location.setX(chunkWidth-1));
			}

			//Positive X neighbor
			location.x = x + 1;
			location.y = y;
			location.z = z;
			if(location.x < chunkWidth)//Check chunk boundary 
			{
				propagateRemovedSunlight(chunk, lightLevel, location);
			}
			else
			{
				propagateRemovedSunlight(terrain.getChunkNeighbor(chunk, Direction.RIGHT), lightLevel, location.setX(0));
			}
			
			//Negative Y neighbor
			location.x = x;
			location.y = y - 1;
			location.z = z;
			if(location.y >= 0)//Check chunk boundary 
			{
				neighborLevel = chunk.getLight(location.x, location.y, location.z, LightType.SKY);
				if(lightLevel == LightMap.MAX_LIGHT || (neighborLevel != 0 && neighborLevel < lightLevel))
				{
					chunk.setLight(location.x, location.y, location.z, LightType.SKY, 0);
					sunlightRemovalQueue.add(new LightRemovalNode(index(location), (short)neighborLevel, chunk));
				}
				else if(neighborLevel >= lightLevel)
				{
					sunlightAdditionQueue.add(new LightNode(index(location), chunk));
				}
			}

			//Positive Y neighbor
			location.x = x;
			location.y = y + 1;
			location.z = z;
			if(location.y < chunkWidth)//Check chunk boundary 
			{
				propagateRemovedSunlight(chunk, lightLevel, location);
			}
			
			//Negative Z neighbor
			location.x = x;
			location.y = y;
			location.z = z - 1;
			if(location.z >= 0)//Check chunk boundary 
			{
				propagateRemovedSunlight(chunk, lightLevel, location);
			}
			else
			{
				propagateRemovedSunlight(terrain.getChunkNeighbor(chunk, Direction.BACK), lightLevel, location.setZ(chunkWidth-1));
			}
			
			//Positive Z neighbor
			location.x = x;
			location.y = y;
			location.z = z + 1;
			if(location.z < chunkWidth)//Check chunk boundary 
			{
				propagateRemovedSunlight(chunk, lightLevel, location);
			}
			else
			{
				propagateRemovedSunlight(terrain.getChunkNeighbor(chunk, Direction.FRONT), lightLevel, location.setZ(0));
			}
			node = sunlightRemovalQueue.poll();
		}
	}

	private void propagateRemovedSunlight(Chunk chunk, int lightLevel, Vector3Int location)
	{
		if(chunk != null)
		{
			int neighborLevel = chunk.getLight(location.x, location.y, location.z, LightType.SKY);
			if(neighborLevel != 0 && neighborLevel < lightLevel)
			{
				chunk.setLight(location.x, location.y, location.z, LightType.SKY, 0);
				sunlightRemovalQueue.add(new LightRemovalNode(index(location), (short)neighborLevel, chunk));
			}
			else if(neighborLevel >= lightLevel)
			{
				sunlightAdditionQueue.add(new LightNode(index(location), chunk));
			}
		}
	}
	

	private boolean needsLightUpdated(Chunk chunk, Vector3Int location, LightType lightType, int lightLevel)
	{
		return chunk != null && isNotOpaque(chunk.getBlock(location)) && chunk.getLight(location.x, location.y, location.z, lightType) + 2 <= lightLevel;
	}
	
	private boolean isNotOpaque(Block block)
	{
		return block == null || block.isTransparent();
	}

	private short index(Vector3Int location)
	{
		return (short)(location.y * chunkWidth * chunkWidth + location.z * chunkWidth + location.x);
	}
	
	@Override
	public void addSunlight(Vector3Int location)
	{
		Chunk chunk = terrain.getChunk(location);
		Vector3Int localBlockLocation = terrain.getLocalBlockLocation(location, chunk);
		
		chunk.setLight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z, LightType.SKY, LightMap.MAX_LIGHT);
		sunlightAdditionQueue.add(new LightNode(index(localBlockLocation), chunk));
	}

	@Override
	public void setBlockLight(Vector3Int location, int light)
	{
		Chunk chunk = terrain.getChunk(location);
		Vector3Int localBlockLocation = terrain.getLocalBlockLocation(location, chunk);
		chunk.setLight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z, LightType.BLOCK, light);
		lightAdditionQueue.add(new LightNode(index(localBlockLocation), chunk));
	}
	
	@Override
	public void removeBlockLight(Vector3Int location)
	{
		Chunk chunk = terrain.getChunk(location);
		Vector3Int localBlockLocation = terrain.getLocalBlockLocation(location, chunk);
		short val = (short)chunk.getLight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z, LightType.BLOCK);
		
		lightRemovalQueue.add(new LightRemovalNode(index(localBlockLocation), val, chunk));
		
		chunk.setLight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z, LightType.BLOCK, 0);
	}

	@Override
	public void removeSunlight(Vector3Int location)
	{
		Chunk chunk = terrain.getChunk(location);
		Vector3Int localBlockLocation = terrain.getLocalBlockLocation(location, chunk);
		short val = (short)chunk.getLight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z, LightType.SKY);
		
		sunlightRemovalQueue.add(new LightRemovalNode(index(localBlockLocation), val, chunk));
		
		chunk.setLight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z, LightType.SKY, 0);
	}

	@Override
	public void initChunkSunlight(Chunk chunk)
	{
		Vector3Int location = new Vector3Int(0, chunkWidth - 1, 0);
		for(int x = 0; x < chunkWidth; x++)
		{
			location.x = x;
			for(int z = 0; z < chunkWidth; z++)
			{
				location.z = z;
				Block block = chunk.getBlock(location);
				if(block == null || block.isTransparent())
				{
					chunk.setLight(location.x, location.y, location.z, LightType.SKY, LightMap.MAX_LIGHT);
					sunlightAdditionQueue.add(new LightNode(index(location), chunk));
				}
			}
		}
	}

	@Override
	public void rebuildSunlight(Chunk chunk)
	{
		chunk.getLights().clearSunlight();
		initChunkSunlight(chunk);
	}
}