package com.chappelle.jcraft.serialization;

import com.chappelle.jcraft.BlockApplication;
import com.chappelle.jcraft.world.World;

public interface WorldPersistor
{
	void save(World world);
	
	World loadWorld(BlockApplication app, String name);
	
	void writeChunk(int chunkX, int chunkZ, Integer[][][] data);
	Integer[][][] readChunk(int chunkX, int chunkZ);
}
