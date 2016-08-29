package com.chappelle.jcraft.lighting;

import java.util.*;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.Chunk;

public class FloodFillLightManager implements LightManager
{
	private World world;
	private Queue<LightNode> lightAdditionQueue;
	private Queue<LightRemovalNode> lightRemovalQueue;
	private Queue<LightNode> sunlightAdditionQueue;
	private Queue<LightRemovalNode> sunlightRemovalQueue;
	
	public FloodFillLightManager(World world)
	{
		this.world = world;
		lightAdditionQueue = new LinkedList<>();
		lightRemovalQueue = new LinkedList<>();
		sunlightAdditionQueue = new LinkedList<>();
		sunlightRemovalQueue = new LinkedList<>();
//		initSunlight();
	}
	
	@Override
	public void propagateLight()
	{
		do
		{
			propagateRemovedBlockLights();
			propagateAddedBlockLights();
			
			propagateRemovedSunlight();
			propagateAddedSunlight();
			
		}while(stillWorkToDo());
	}
	
	private boolean stillWorkToDo()
	{
		return !lightAdditionQueue.isEmpty() && !lightRemovalQueue.isEmpty() && !sunlightAdditionQueue.isEmpty() && !sunlightRemovalQueue.isEmpty();
	}

	private void propagateRemovedBlockLights()
	{
		LightRemovalNode node = lightRemovalQueue.poll();
		while(node != null)
		{
			Chunk chunk = node.chunk;
			int x = node.x;
			int y = node.y; 
			int z = node.z;
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
				propagateRemovedBlockLights(world.getChunkNeighbor(chunk, Direction.LEFT), lightLevel, location.setX(15));
			}
			
			//Positive X neighbor
			location.x = x + 1;
			location.y = y;
			location.z = z;
			if(location.x < 16)//Check chunk boundary 
			{
				propagateRemovedBlockLights(chunk, lightLevel, location);
			}
			else
			{
				propagateRemovedBlockLights(world.getChunkNeighbor(chunk, Direction.RIGHT), lightLevel, location.setX(0));
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
			if(location.y < 256)//Check chunk boundary 
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
				propagateRemovedBlockLights(world.getChunkNeighbor(chunk, Direction.BACK), lightLevel, location.setZ(15));
			}
			
			//Positive Z neighbor
			location.x = x;
			location.y = y;
			location.z = z + 1;
			if(location.z < 16)//Check chunk boundary 
			{
				propagateRemovedBlockLights(chunk, lightLevel, location);
			}
			else
			{
				propagateRemovedBlockLights(world.getChunkNeighbor(chunk, Direction.FRONT), lightLevel, location.setZ(0));
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
				lightRemovalQueue.add(new LightRemovalNode(location, (short)neighborLevel, chunk));
			}
			else if(neighborLevel >= lightLevel)
			{
				lightAdditionQueue.add(new LightNode(location, chunk));
			}
		}
	}
	
	private void propagateAddedBlockLights()
	{
		LightNode node = lightAdditionQueue.poll();
		while(node != null)
		{
			Chunk chunk = node.chunk;
			int x = node.x;
			int y = node.y; 
			int z = node.z;
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
				propagateAddedBlockLights(world.getChunkNeighbor(chunk, Direction.LEFT), location.setX(15), lightLevel);
			}
			

			//Positive X neighbor
			location.x = x + 1;
			location.y = y;
			location.z = z;
			if(location.x < 16)//Check chunk boundary 
			{
				propagateAddedBlockLights(chunk, location, lightLevel);
			}
			else
			{
				propagateAddedBlockLights(world.getChunkNeighbor(chunk, Direction.RIGHT), location.setX(0), lightLevel);
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
			if(location.y < 256)//Check chunk boundary 
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
				propagateAddedBlockLights(world.getChunkNeighbor(chunk, Direction.BACK), location.setZ(15), lightLevel);
			}
			
			//Positive Z neighbor
			location.x = x;
			location.y = y;
			location.z = z + 1;
			if(location.z < 16)//Check chunk boundary 
			{
				propagateAddedBlockLights(chunk, location, lightLevel);
			}
			else
			{
				propagateAddedBlockLights(world.getChunkNeighbor(chunk, Direction.FRONT), location.setZ(0), lightLevel);
			}
			node = lightAdditionQueue.poll();	
		}
	}

	private void propagateAddedBlockLights(Chunk chunk, Vector3Int location, int lightLevel)
	{
		if(needsLightUpdated(chunk, location, LightType.BLOCK, lightLevel))
		{
			chunk.setLight(location.x, location.y, location.z, LightType.BLOCK, lightLevel - 1);
			lightAdditionQueue.add(new LightNode(location, chunk));
		}
	}

	private void propagateAddedSunlight()
	{
		LightNode node = sunlightAdditionQueue.poll();
		while(node != null)
		{
			Chunk chunk = node.chunk;
			int x = node.x;
			int y = node.y; 
			int z = node.z;
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
				propagateAddedSunlight(world.getChunkNeighbor(chunk, Direction.LEFT), location.setX(15), lightLevel);
			}

			//Positive X neighbor
			location.x = x + 1;
			location.y = y;
			location.z = z;
			if(location.x < 16)//Check chunk boundary 
			{
				propagateAddedSunlight(chunk, location, lightLevel);
			}
			else
			{
				propagateAddedSunlight(world.getChunkNeighbor(chunk, Direction.RIGHT), location.setX(0), lightLevel);
			}
			
			
			//Negative Y neighbor
			location.x = x;
			location.y = y - 1;
			location.z = z;
			if(location.y >= 0)//Check chunk boundary 
			{
				if(needsLightUpdated(chunk, location, LightType.SKY, lightLevel))
				{
					if(lightLevel == 15)
					{
						chunk.setLight(location.x, location.y, location.z, LightType.SKY, lightLevel);
					}
					else
					{
						chunk.setLight(location.x, location.y, location.z, LightType.SKY, lightLevel - 1);
					}
					sunlightAdditionQueue.add(new LightNode(location, chunk));
				}
			}

			//Positive Y neighbor
			location.x = x;
			location.y = y + 1;
			location.z = z;
			if(location.y < 256)//Check chunk boundary 
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
				propagateAddedSunlight(world.getChunkNeighbor(chunk, Direction.BACK), location.setZ(15), lightLevel);
			}
			
			//Positive Z neighbor
			location.x = x;
			location.y = y;
			location.z = z + 1;
			if(location.z < 16)//Check chunk boundary 
			{
				propagateAddedSunlight(chunk, location, lightLevel);
			}
			else
			{
				propagateAddedSunlight(world.getChunkNeighbor(chunk, Direction.FRONT), location.setZ(0), lightLevel);
			}
			
			node = sunlightAdditionQueue.poll();
		}
	}

	private void propagateAddedSunlight(Chunk chunk, Vector3Int location, int lightLevel)
	{
		if(needsLightUpdated(chunk, location, LightType.SKY, lightLevel))
		{
			chunk.setLight(location.x, location.y, location.z, LightType.SKY, lightLevel - 1);
			sunlightAdditionQueue.add(new LightNode(location, chunk));
		}
	}
	
	private void propagateRemovedSunlight()
	{
		LightRemovalNode node = sunlightRemovalQueue.poll();
		while(node != null)
		{
			Chunk chunk = node.chunk;
			int x = node.x;
			int y = node.y; 
			int z = node.z;
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
				propagateRemovedSunlight(world.getChunkNeighbor(chunk, Direction.LEFT), lightLevel, location.setX(15));
			}

			//Positive X neighbor
			location.x = x + 1;
			location.y = y;
			location.z = z;
			if(location.x < 16)//Check chunk boundary 
			{
				propagateRemovedSunlight(chunk, lightLevel, location);
			}
			else
			{
				propagateRemovedSunlight(world.getChunkNeighbor(chunk, Direction.RIGHT), lightLevel, location.setX(0));
			}
			
			//Negative Y neighbor
			location.x = x;
			location.y = y - 1;
			location.z = z;
			if(location.y >= 0)//Check chunk boundary 
			{
				neighborLevel = chunk.getLight(location.x, location.y, location.z, LightType.SKY);
				if(lightLevel == 15 || (neighborLevel != 0 && neighborLevel < lightLevel))
				{
					chunk.setLight(location.x, location.y, location.z, LightType.SKY, 0);
					sunlightRemovalQueue.add(new LightRemovalNode(location, (short)neighborLevel, chunk));
				}
				else if(neighborLevel >= lightLevel)
				{
					sunlightAdditionQueue.add(new LightNode(location, chunk));
				}
			}

			//Positive Y neighbor
			location.x = x;
			location.y = y + 1;
			location.z = z;
			if(location.y < 256)//Check chunk boundary 
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
				propagateRemovedSunlight(world.getChunkNeighbor(chunk, Direction.BACK), lightLevel, location.setZ(15));
			}
			
			//Positive Z neighbor
			location.x = x;
			location.y = y;
			location.z = z + 1;
			if(location.z < 16)//Check chunk boundary 
			{
				propagateRemovedSunlight(chunk, lightLevel, location);
			}
			else
			{
				propagateRemovedSunlight(world.getChunkNeighbor(chunk, Direction.FRONT), lightLevel, location.setZ(0));
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
				sunlightRemovalQueue.add(new LightRemovalNode(location, (short)neighborLevel, chunk));
			}
			else if(neighborLevel >= lightLevel)
			{
				sunlightAdditionQueue.add(new LightNode(location, chunk));
			}
		}
	}
	

	private boolean needsLightUpdated(Chunk chunk, Vector3Int location, LightType lightType, int lightLevel)
	{
		return chunk != null && isNotOpaque(chunk.getBlock(location)) && chunk.getLight(location.x, location.y, location.z, lightType) + 2 <= lightLevel;
	}
	
	private boolean isNotOpaque(Block block)
	{
		return block == null || block.isTransparent;
	}

	@Override
	public void addSunlight(Chunk chunk, Vector3Int localBlockLocation)
	{
		chunk.setLight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z, LightType.SKY, 15);
		
		sunlightAdditionQueue.add(new LightNode(localBlockLocation, chunk));
	}

	@Override
	public void setBlockLight(Chunk chunk, Vector3Int localBlockLocation, int light)
	{
		chunk.setLight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z, LightType.BLOCK, light);
		
		lightAdditionQueue.add(new LightNode(localBlockLocation, chunk));
	}
	
	@Override
	public void removeBlockLight(Chunk chunk, Vector3Int localBlockLocation)
	{
		short val = (short)chunk.getLight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z, LightType.BLOCK);
		
		lightRemovalQueue.add(new LightRemovalNode(localBlockLocation, val, chunk));
		
		chunk.setLight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z, LightType.BLOCK, 0);
	}

	@Override
	public void removeSunlight(Chunk chunk, Vector3Int localBlockLocation)
	{
		short val = (short)chunk.getLight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z, LightType.SKY);
		
		sunlightRemovalQueue.add(new LightRemovalNode(localBlockLocation, val, chunk));
		
		chunk.setLight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z, LightType.SKY, 0);
	}

	public void restoreSunlight(Chunk chunk, Vector3Int localBlockLocation)
	{
		int x = localBlockLocation.x;
		int y = localBlockLocation.y;
		int z = localBlockLocation.z;

		if(chunk.isBlockExposedToDirectSunlight(localBlockLocation.x, localBlockLocation.y, localBlockLocation.z))
		{
			for(int by = localBlockLocation.y; by >= 0; by--)
			{
				Block block = chunk.getBlock(x, by, z);
				if(block != null && !block.isTransparent)
				{
					break;
				}
				chunk.setLight(x, by, z, LightType.SKY, 15);
				sunlightAdditionQueue.add(new LightNode(x,by,z, chunk));
			}
		}
		else
		{
			Chunk neighbor = null;
			int light = 0;
			if(x-1 < 0)
			{
				neighbor = world.getChunkNeighbor(chunk, Direction.LEFT);
				if(neighbor != null)
				{
					light = Math.max(light, neighbor.getLight(15,y,z, LightType.SKY));
				}
			}
			else
			{
				light = Math.max(light, chunk.getLight(x-1,y,z, LightType.SKY));
			}
			
			if(x+1 > 15)
			{
				neighbor = world.getChunkNeighbor(chunk, Direction.RIGHT);
				if(neighbor != null)
				{
					light = Math.max(light, neighbor.getLight(0,y,z, LightType.SKY));
				}
			}
			else
			{
				light = Math.max(light, chunk.getLight(x+1,y,z, LightType.SKY));
			}
			
			
			if(z-1 < 0)
			{
				neighbor = world.getChunkNeighbor(chunk, Direction.BACK);
				if(neighbor != null)
				{
					light = Math.max(light, neighbor.getLight(x,y,15, LightType.SKY));
				}
			}
			else
			{
				light = Math.max(light, chunk.getLight(x,y,z-1, LightType.SKY));
			}
			
			
			if(z+1 > 15)
			{
				neighbor = world.getChunkNeighbor(chunk, Direction.FRONT);
				if(neighbor != null)
				{
					light = Math.max(light, neighbor.getLight(x,y,0, LightType.SKY));
				}
			}
			else
			{
				light = Math.max(light, chunk.getLight(x,y,z+1, LightType.SKY));
			}
			
			if(y+1 < 255)
			{
				light = Math.max(light, chunk.getLight(x,y+1,z, LightType.SKY));
			}
			
			chunk.setLight(x, y, z, LightType.SKY, Math.max(light-1, 0));
			sunlightAdditionQueue.add(new LightNode(x,y,z, chunk));
		}
	}
	
	public void initSunlight(Chunk chunk)
	{
		int y = 255;
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				Block block = chunk.getBlock(x, y, z);
				if(block == null || block.isTransparent)
				{
					chunk.setLight(x, y, z, LightType.SKY, 15);
					sunlightAdditionQueue.add(new LightNode(x, y, z, chunk));
				}
			}
		}
		propagateLight();
	}
}