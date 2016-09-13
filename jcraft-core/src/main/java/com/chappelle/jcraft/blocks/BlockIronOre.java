package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.world.World;
import com.jme3.math.Vector3f;

public class BlockIronOre extends Block
{
	public BlockIronOre(int blockId)
	{
		super(blockId, new BlockSkin[] { new BlockSkin(new BlockSkin_TextureLocation(1, 2), false) });
	}

	public void onBlockPlaced(World world, Vector3Int location, Vector3f contactNormal, Vector3f cameraDirectionAsUnitVector)
	{
	}

	public void onBlockRemoved(World world, Vector3Int location)
	{
	}

	public void onEntityWalking(World world, Vector3Int location)
	{
		world.playSound(SoundConstants.DIG_STONE, 4);
	}
}