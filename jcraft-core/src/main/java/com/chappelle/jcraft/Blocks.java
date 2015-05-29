package com.chappelle.jcraft;

import com.chappelle.jcraft.blocks.Door;
import com.chappelle.jcraft.blocks.Glass;
import com.chappelle.jcraft.blocks.Grass;
import com.chappelle.jcraft.blocks.Torch;
import com.cubes.Block;
import com.cubes.BlockManager;
import com.cubes.World;

public class Blocks
{
	public static Block GRASS;
	public static Block GLASS;
	public static Block DOOR;
	public static Block TORCH;
	public static Block[] blocks;
	
	public static void registerBlocks(World terrainMgr)
	{
		GRASS = new Grass(terrainMgr);
		GLASS = new Glass(terrainMgr);
		DOOR = new Door(terrainMgr);
		TORCH = new Torch(terrainMgr);

		BlockManager.register(GLASS);
		BlockManager.register(GRASS);
		BlockManager.register(DOOR);
		BlockManager.register(TORCH);

		blocks = new Block[]{GRASS, GLASS, DOOR, TORCH};
	}
}
