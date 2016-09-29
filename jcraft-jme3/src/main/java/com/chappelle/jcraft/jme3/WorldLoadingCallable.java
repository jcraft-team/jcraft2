package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.world.World;
import com.jme3.app.state.AppState;
import com.jme3.system.AppSettings;

public class WorldLoadingCallable implements LoadingCallable
{
	private World world;
	private EntityPlayer player;
	private ProgressMonitor progressMonitor;
	private AppSettings settings;
	
	public WorldLoadingCallable(World world, EntityPlayer player, AppSettings settings)
	{
		this.world = world;
		this.player = player;
		this.progressMonitor = new DefaultProgressMonitor();
		this.settings = settings;
	}

	@Override
	public Void call() throws Exception
	{
		int chunkRenderDistance = GameSettings.chunkRenderDistance;
		GameSettings.chunkRenderDistance = Math.min(chunkRenderDistance, 6);
		world.getChunkManager().initializeChunks(progressMonitor, player.posX, player.posZ, GameSettings.chunkRenderDistance);
		GameSettings.chunkRenderDistance = chunkRenderDistance;
		return null;
	}

	@Override
	public AppState getNextAppState()
	{
		return new GameRunningAppState(player, world, settings);
	}

	@Override
	public ProgressMonitor getProgressMonitor()
	{
		return progressMonitor;
	}

}
