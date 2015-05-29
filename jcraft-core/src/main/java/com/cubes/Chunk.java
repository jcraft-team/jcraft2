package com.cubes;

import com.cubes.lighting.LightMap;
import com.cubes.lighting.LightType;

public interface Chunk
{
	Vector3Int getBlockLocation();
	int getLight(int x, int y, int z, LightType type);
	void setLight(int x, int y, int z, LightType type, int lightVal);
	Object getBlockStateValue(Vector3Int location, Short key);
	BlockState getBlockState(Vector3Int location);
	Block getNeighborBlock_Global(Vector3Int location, Block.Face face);
	Vector3Int getLocation();
	Block getBlock(Vector3Int location);
	void markDirty();
	boolean isBlockOnSurface(Vector3Int location);
	void setBlock(Vector3Int location, Block block);
	void removeBlock(Vector3Int location);
	LightMap getLights();
	World getTerrain();
}
