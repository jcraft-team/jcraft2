package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.BlockSkin;
import com.chappelle.jcraft.BlockSkin_TextureLocation;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.World;
import com.jme3.math.Vector3f;

public class BlockIce extends Block
{
	public BlockIce(int blockId)
	{
		super(blockId, new BlockSkin[]{new BlockSkin(new BlockSkin_TextureLocation(3, 4), true)});
		this.slipperiness = 0.98F;
	}

	@Override
	public void onBlockPlaced(World world, Vector3Int location, Vector3f contactNormal,	Vector3f cameraDirectionAsUnitVector)
	{
		world.playSound(SoundConstants.STEP_STONE_4);
	}

	@Override
	public void onBlockRemoved(World world, Vector3Int location)
	{
		world.playSound(SoundConstants.MISC_GLASS, 3);
	}

	@Override
	public boolean isTransparent()
	{
		return true;
	}
	
}
