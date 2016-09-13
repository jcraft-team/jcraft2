package com.chappelle.jcraft.serialization;

import java.util.TimerTask;
import java.util.logging.*;

import com.chappelle.jcraft.EntityPlayer;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.*;
import com.jme3.math.Vector3f;

public class WorldSaveTask extends TimerTask
{
	private static final Logger log = Logger.getLogger(WorldSaveTask.class.getName());

	private World world;
	private VoxelWorldSave voxelWorldSave;

	public WorldSaveTask(World world, VoxelWorldSave voxelWorldSave)
	{
		this.world = world;
		this.voxelWorldSave = voxelWorldSave;
	}

	@Override
	public void run()
	{
		if(world.getChunkManager().isInitialized())
		{
			log.log(Level.FINE, "Saving...");
			
			voxelWorldSave.putGameData("timeOfDay", world.getTimeOfDayProvider().getTimeOfDay());
			savePlayerData();
			saveChunkData();
			
			log.log(Level.FINE, "Finished saving");
		}
	}

	private void saveChunkData()
	{
		for(Chunk chunk : world.getChunkManager().getLoadedChunks())
		{
			if(chunk.hasChangedSinceLastSave())
			{
				voxelWorldSave.writeChunk(chunk.location.x, chunk.location.z, chunk.getData());
				chunk.setLastSavedTime(System.currentTimeMillis());
			}
		}
		voxelWorldSave.flushSave();
	}

	private void savePlayerData()
	{
		EntityPlayer player = world.getPlayer();
		voxelWorldSave.putGameData("playerLocation", new Vector3f((float) player.posX, (float) player.posY, (float) player.posZ));
		voxelWorldSave.putGameData("playerLookDirection", player.cam.getDirection());
	}
}
