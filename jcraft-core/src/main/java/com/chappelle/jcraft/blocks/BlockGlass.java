package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.BlockSkin;
import com.chappelle.jcraft.BlockSkin_TextureLocation;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.World;
import com.jme3.math.Vector3f;

public class BlockGlass extends Block
{
	public BlockGlass(int blockId)
	{
		super(blockId, new BlockSkin[] { new BlockSkin(new BlockSkin_TextureLocation(1, 3), true)});
	}
	
	public void onBlockPlaced(World world, Vector3Int location, Vector3f contactNormal, Vector3f cameraDirectionAsUnitVector)
	{
		world.playSound(SoundConstants.STEP_STONE_4);
	}

	public void onBlockRemoved(World world, Vector3Int location)
	{
		world.playSound(SoundConstants.MISC_GLASS, 3);
	}

	public void onEntityWalking(World world, Vector3Int location)
	{
	}

	@Override
	public boolean isTransparent()
	{
		return true;
	}

	@Override
	public boolean smothersBottomBlock()
	{
		return false;
	}
}