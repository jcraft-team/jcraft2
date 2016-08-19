package com.chappelle.jcraft.jme3;

import java.util.concurrent.Callable;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.world.World;

public class LoadingCallable implements Callable<Void>
{
	private World world;
	private EntityPlayer player;
	private ProgressMonitor progressMonitor;
	
	public LoadingCallable(World world, EntityPlayer player, ProgressMonitor progressMonitor)
	{
		this.world = world;
		this.player = player;
		this.progressMonitor = progressMonitor;
	}

	@Override
	public Void call() throws Exception
	{
		world.getChunkManager().initialize(progressMonitor, player.posX, player.posZ, GameSettings.chunkRenderDistance);
		player.preparePlayerToSpawn();
		return null;
	}

}
