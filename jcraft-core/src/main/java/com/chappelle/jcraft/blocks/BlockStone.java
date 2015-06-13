package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.world.World;
import com.jme3.math.Vector3f;

public class BlockStone extends Block
{
	public BlockStone(int blockId)
	{
		super(blockId, new BlockSkin[]{new BlockSkin(new BlockSkin_TextureLocation(0, 1), false)});
	}

	@Override
	public void onBlockPlaced(World world, Vector3Int location, Block.Face face,Vector3f cameraDirectionAsUnitVector)
	{
		world.playSound(SoundConstants.DIG_STONE, 4);
	}

	@Override
	public void onBlockRemoved(World world, Vector3Int location)
	{
		world.playSound(SoundConstants.DIG_STONE, 4);
	}
}
