package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.Direction;
import com.chappelle.jcraft.util.math.*;
import com.chappelle.jcraft.world.chunk.Chunk;

import gnu.trove.list.*;

public interface MeshGenContext
{
	Chunk getChunkNeighbor(Direction dir);
	Vector3Int getLocation();
	
	Chunk getChunk();
	Block getBlock();
	Block getBlock(int x, int y, int z);

	TFloatList getColorList();
	TFloatList getPositions();
	TShortList getIndices();
	TFloatList getNormals();
	TFloatList getTextureCoordinates();
	Vector3fPool getVector3fPool();
	
	int getTexturesCountX();
	int getTexturesCountY();
	boolean isOpaqueBlockPresent(int x, int y, int z);
}
