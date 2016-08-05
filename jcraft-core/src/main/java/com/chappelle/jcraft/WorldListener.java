package com.chappelle.jcraft;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.chunk.Chunk;

public interface WorldListener
{
	void onBlockAdded(Block block, Chunk chunk, int x, int y, int z);

	void onBlockRemoved(Block block, Chunk chunk, int x, int y, int z);
}
