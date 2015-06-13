package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.BlockSkin;
import com.chappelle.jcraft.BlockSkin_TextureLocation;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.World;
import com.chappelle.jcraft.shapes.BlockShape_Flower;
import com.chappelle.jcraft.util.AABB;
import com.jme3.math.Vector3f;

public class BlockFlower extends Block
{
	public BlockFlower(int blockId)
	{
		super(blockId, new BlockSkin[] { new BlockSkin(new BlockSkin_TextureLocation(12, 0), true) });
		setShapes(new BlockShape_Flower());
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

	@Override
	public AABB getCollisionBoundingBox(World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public boolean isTransparent()
	{
		return true;
	}

	@Override
	public boolean useNeighborLight()
	{
		return false;
	}

	@Override
	public boolean smothersBottomBlock()
	{
		return false;
	}
}