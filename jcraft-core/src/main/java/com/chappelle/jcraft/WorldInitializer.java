package com.chappelle.jcraft;

import com.chappelle.jcraft.world.World;

public interface WorldInitializer
{
	/**
	 * Called right after the world has been constructed and has not yet been added to the scene
	 * @param world
	 */
	void configureWorld(World world);
}