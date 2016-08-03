package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.world.World;
import com.jme3.math.Vector3f;

public class BlockIce extends Block
{
	public BlockIce(int blockId)
	{
		super(blockId, new BlockSkin[]{new BlockSkin(new BlockSkin_TextureLocation(3, 4), true)});
		this.slipperiness = 0.98F;
		this.isTransparent = true;
	}

	@Override
	public void onBlockPlaced(World world, Vector3Int location, Block.Face face,Vector3f cameraDirectionAsUnitVector)
	{
		world.playSound(SoundConstants.STEP_STONE_4);
	}

	@Override
	public void onBlockRemoved(World world, Vector3Int location)
	{
		world.playSound(SoundConstants.MISC_GLASS, 3);
	}

}
