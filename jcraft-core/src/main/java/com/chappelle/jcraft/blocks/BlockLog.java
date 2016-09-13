package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector3f;

public class BlockLog extends Block
{
	public BlockLog(int blockId)
	{
		super(blockId, new BlockSkin[] { new BlockSkin(new BlockSkin_TextureLocation(4, 1), false), new BlockSkin(new BlockSkin_TextureLocation(5, 1), false) });
	}

	public void onBlockPlaced(World world, Vector3Int location, Vector3f contactNormal, Vector3f cameraDirectionAsUnitVector)
	{
		world.playSound(SoundConstants.DIG_WOOD, 4);
	}

	public void onBlockRemoved(World world, Vector3Int location)
	{
		world.playSound(SoundConstants.DIG_WOOD, 4);
	}

	public void onEntityWalking(World world, Vector3Int location)
	{
		world.playSound(SoundConstants.DIG_WOOD, 4);
	}
	
	@Override
	protected int getSkinIndex(Chunk chunk, Vector3Int location, Block.Face face)
	{
		switch(face)
		{
			case Top:
			case Bottom:
				return 1;
			default: 
				return 0;
		}
	}
}