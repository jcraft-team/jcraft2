package com.chappelle.jcraft.lighting;

import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.world.chunk.Chunk;

public interface LightManager
{
	void propagateLight();

	void setBlockLight(Chunk chunk, Vector3Int location, int light);
	void removeBlockLight(Chunk chunk, Vector3Int location);
	
	void addSunlight(Chunk chunk, Vector3Int location);
	void removeSunlight(Chunk chunk, Vector3Int location);

	void restoreSunlight(Chunk chunk, Vector3Int localBlockLocation);
	
	void initSunlight(Chunk chunk);
}
