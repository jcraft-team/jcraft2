package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.util.physics.AABB;
import com.chappelle.jcraft.world.World;
import com.jme3.math.Vector3f;

public class BlockWater extends Block
{
	public BlockWater(int blockId)
	{
		super(blockId, new BlockSkin[] { new BlockSkin(new BlockSkin_TextureLocation(13, 12), false) });
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

	@Override
	public AABB getCollisionBoundingBox(World world, int x, int y, int z)
	{
		return null;
	}
}