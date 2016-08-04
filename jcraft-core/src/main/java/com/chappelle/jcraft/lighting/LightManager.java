package com.chappelle.jcraft.lighting;

import com.chappelle.jcraft.Vector3Int;

public interface LightManager
{
	void propagateLight();

	void setBlockLight(Vector3Int location, int light);
	void removeBlockLight(Vector3Int location);
	
	void addSunlight(Vector3Int location);
	void removeSunlight(Vector3Int location);

	void restoreSunlight(Vector3Int localBlockLocation);
	void rebuildSunlight();
}
