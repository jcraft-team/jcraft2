package com.chappelle.jcraft.debug;

import java.text.*;
import java.util.*;

import org.apache.commons.lang3.ArrayUtils;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.lighting.LightType;
import com.chappelle.jcraft.util.*;
import com.chappelle.jcraft.world.*;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector3f;

public class DebugDataProvider
{
	private String[] labels = new String[]
			{
				"Player Location", "Player AABB", "Player Selection", "Player Direction",
				"Ray Trace", "Walking On", "Chunk", "Loaded Chunks", "Light", "Ambient Occlusion",
				"Time"
			};
	
	private static final NumberFormat NUMBER_FORMAT_SMALL = new DecimalFormat("#0.0");
	private static final NumberFormat NUMBER_FORMAT_LARGE = new DecimalFormat("#0.000");
	
	private Map<String, String> debugData = new HashMap<>();
	private final EntityPlayer player;
	private final World world;
	private StringBuilder temp = new StringBuilder();
	
	public DebugDataProvider(BlockApplication app)
	{
		this.player = app.getPlayer();
		this.world = app.world;
		
		//Do this to make the labels appear in the order as they do above
		ArrayUtils.reverse(labels);
	}
	
	public String[] getLabels()
	{
		return labels;
	}
	
	public Map<String, String> getData()
	{
		debugData.clear();
		
		debugData.put("Player Location", toString(player.posX, player.posY, player.posZ));
		debugData.put("Player AABB", toString(player.boundingBox));
		debugData.put("Player Selection", toString(player.getSelectedBlock()));
		debugData.put("Player Direction", toString(player.cam.getDirection()));
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
				debugData.put("Chunk", "(" + chunk.location.x + ", " + chunk.location.z + ")");
			}
		}
		debugData.put("Ray Trace", getRayTraceData());
		
		return debugData;
	}
	
	private String toString(Vector3f direction)
	{
		return "(" + NUMBER_FORMAT_LARGE.format(direction.x) + ", " + NUMBER_FORMAT_LARGE.format(direction.y) + ", " + NUMBER_FORMAT_LARGE.format(direction.z) + ")";
	}

	private String getRayTraceData()
	{
		temp.setLength(0);
		RayTrace rayTrace = player.pickBlock();
		if(rayTrace != null)
		{
			Block block = world.getBlock(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
			temp.append(block == null ? "Air" : block);
			temp.append(" (" + rayTrace.blockX + "," + rayTrace.blockY + "," + rayTrace.blockZ + "|" + rayTrace.sideHit + ")");
			if(block != null)
			{
				ChunkLocation localBlockState = world.getLocalBlockState(new Vector3Int(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ));
				if(localBlockState != null)
				{
					Vector3Int localBlockLocation = localBlockState.getLocalBlockLocation();
					temp.append(" --> (" + localBlockLocation.x + "," + localBlockLocation.y + "," + localBlockLocation.z + ")");
				}
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

	private String toString(double x, double y, double z)
	{
		return "(" + NUMBER_FORMAT_SMALL.format(x) + "," + NUMBER_FORMAT_SMALL.format(y) + ", " + NUMBER_FORMAT_SMALL.format(z) + ")";
	}

	private String toString(AABB aabb)
	{
		if(aabb == null)
		{
			return "";
		}
		else
		{
			return "[" + NUMBER_FORMAT_SMALL.format(aabb.minX) + "," + NUMBER_FORMAT_SMALL.format(aabb.minY) + "," + NUMBER_FORMAT_SMALL.format(aabb.minZ) + " -> " + NUMBER_FORMAT_SMALL.format(aabb.maxX) + "," + NUMBER_FORMAT_SMALL.format(aabb.maxY) + "," + NUMBER_FORMAT_SMALL.format(aabb.maxZ) + "]";
		}
	}
	
	private String toString(Block block)
	{
		return block == null ? "Air" : block.getClass().getSimpleName();
	}
}