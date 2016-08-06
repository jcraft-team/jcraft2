package com.chappelle.jcraft.debug;

import java.util.*;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.lighting.LightType;
import com.chappelle.jcraft.util.RayTrace;
import com.chappelle.jcraft.world.*;
import com.chappelle.jcraft.world.chunk.Chunk;

public class DebugDataProvider
{
	private String[] labels = new String[]
			{
				"Player Location", "Bounding Box", "Selected Block", "Facing",
				"Time", "Loaded Chunks", "Ambient Occlusion", "Light", "Walking On",
				"Chunk", "Ray Trace"
			};
	
	private Map<String, String> debugData = new HashMap<>();
	private final EntityPlayer player;
	private final World world;
	private StringBuilder temp = new StringBuilder();
	
	public DebugDataProvider(BlockApplication app)
	{
		this.player = app.getPlayer();
		this.world = app.world;
	}
	
	public String[] getLabels()
	{
		return labels;
	}
	
	public Map<String, String> getData()
	{
		debugData.clear();
		
		debugData.put("Player Location", "[" + player.posX + "," + player.posY + ", " + player.posZ + "]");
		debugData.put("Bounding Box", Objects.toString(player.boundingBox));
		debugData.put("Selected Block", toString(player.getSelectedBlock()));
		debugData.put("Facing", Objects.toString(player.cam.getDirection()));
		debugData.put("Time", Objects.toString(world.getTimeOfDayProvider().getTimeOfDay()));
		debugData.put("Loaded Chunks", Integer.toString(world.getLoadedChunkCount()));
		debugData.put("Ambient Occlusion", GameSettings.ambientOcclusionEnabled == true ? "Enabled" : "Disabled");
		
		Vector3Int blockLoc = new Vector3Int((int)player.posX, (int)player.posY, (int)player.posZ);
		if(blockLoc != null && blockLoc.y < 256)
		{
			Vector3Int walkedOnBlockLocation = blockLoc.subtract(0, 2, 0);
			if(walkedOnBlockLocation != null)
			{
				int overallLight = world.getLight(blockLoc);
				int skyLight = world.getLight(blockLoc, LightType.SKY);
				int blockLight = world.getLight(blockLoc, LightType.BLOCK);
				debugData.put("Light", overallLight + " (Sky: " + skyLight + ", Block: " + blockLight + ")");
				debugData.put("Walking On", toString(world.getBlock(walkedOnBlockLocation)));
			}
			Chunk chunk = world.getChunkFromBlockCoordinates(blockLoc.x, blockLoc.z);
			if(chunk != null)
			{
				debugData.put("Chunk", "" + chunk.location);
			}
		}
		debugData.put("Ray Trace", getRayTraceData());
		
		return debugData;
	}
	
	private String getRayTraceData()
	{
		temp.setLength(0);
		
		RayTrace rayTrace = player.pickBlock();
		if(rayTrace != null)
		{
			Block block = world.getBlock(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
			temp.append(block == null ? "Air" : block);
			temp.append(" (" + rayTrace.blockX + "," + rayTrace.blockY + "," + rayTrace.blockZ + "," + rayTrace.sideHit + ")");
			if(block != null)
			{
				ChunkLocation localBlockState = world.getLocalBlockState(new Vector3Int(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ));
				if(localBlockState != null)
				{
					Vector3Int localBlockLocation = localBlockState.getLocalBlockLocation();
					temp.append(" --> (" + localBlockLocation.x + "," + rayTrace.blockY + "," + localBlockLocation.y + "," + localBlockLocation.z + ")");
				}
				temp.append(block.getCollisionBoundingBox(world, rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ));
			}
		}
		else
		{
			temp.append("Air");
		}
		String result = temp.toString();
		temp.setLength(0);
		
		return result;
	}

	private String toString(Block block)
	{
		return block == null ? "Air" : block.getClass().getSimpleName();
	}
}