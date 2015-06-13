package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.BlockSkin;
import com.chappelle.jcraft.BlockSkin_TextureLocation;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.world.World;
import com.jme3.math.Vector3f;

public class BlockWool extends Block
{
	public BlockWool(int blockId, int textureCol, int textureRow)
	{
		super(blockId, new BlockSkin[] { new BlockSkin(new BlockSkin_TextureLocation(textureCol, textureRow), false) });
	}

	public void onBlockPlaced(World world, Vector3Int location, Vector3f contactNormal, Vector3f cameraDirectionAsUnitVector)
	{
	}

	public void onBlockRemoved(World world, Vector3Int location)
	{
	}

	public void onEntityWalking(World world, Vector3Int location)
	{
	}
}