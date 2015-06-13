package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.BlockSkin;
import com.chappelle.jcraft.BlockSkin_TextureLocation;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.world.World;
import com.jme3.math.Vector3f;

public class BlockCoal extends Block
{
	public BlockCoal(int blockId)
	{
		super(blockId, new BlockSkin[] { new BlockSkin(new BlockSkin_TextureLocation(2, 2), false) });
	}

	public void onBlockPlaced(World world, Vector3Int location, Vector3f contactNormal, Vector3f cameraDirectionAsUnitVector)
	{
		world.playSound(SoundConstants.DIG_STONE, 4);
	}

	public void onBlockRemoved(World world, Vector3Int location)
	{
		world.playSound(SoundConstants.DIG_STONE, 4);
	}

	public void onEntityWalking(World world, Vector3Int location)
	{
		world.playSound(SoundConstants.DIG_STONE, 4);
	}
}