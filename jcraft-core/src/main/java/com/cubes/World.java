package com.cubes;

public interface World
{
	BlockState getBlockState(Vector3Int location);
	Chunk getChunk(Vector3Int blockLocation);
	Vector3Int getLocalBlockLocation(Vector3Int blockLocation, Chunk chunk);
	Block getBlock(int x, int y, int z);
	Block getBlock(Vector3Int location);
	CubesSettings getSettings();
	Chunk getChunkNeighbor(Chunk chunk, Direction direction);
	void setBlock(Vector3Int topLocation, Block block);
	void removeBlock(Vector3Int location);
	void removeBlock(Block door, Vector3Int location);
}
