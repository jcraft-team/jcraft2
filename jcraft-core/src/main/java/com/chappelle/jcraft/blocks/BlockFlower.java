package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.blocks.shapes.BlockShape_Flower;
import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.util.physics.AABB;
import com.chappelle.jcraft.world.World;
import com.jme3.math.Vector3f;

public class BlockFlower extends Block
{
	public BlockFlower(int blockId, int textureCol, int textureRow)
	{
		super(blockId, new BlockSkin[] { new BlockSkin(new BlockSkin_TextureLocation(textureCol, textureRow), true) });
		setShapes(new BlockShape_Flower());
		bounds.minX = 0.3f;
		bounds.minY = 0;
		bounds.minZ = 0.3f;
		bounds.maxX = 0.6f;
		bounds.maxY = 0.4f;
		bounds.maxZ = 0.6f;
		
		this.isTransparent = true;
	}

	public void onBlockPlaced(World world, Vector3Int location, Vector3f contactNormal, Vector3f cameraDirectionAsUnitVector)
	{
		world.playSound(SoundConstants.DIG_GRASS, 4);
	}

	public void onBlockRemoved(World world, Vector3Int location)
	{
		world.playSound(SoundConstants.DIG_GRASS, 4);
	}

    @Override
    public void onNeighborRemoved(World world, Vector3Int removedBlockLocation, Vector3Int myLocation)
    {
        if(removedBlockLocation.add(0,1,0).equals(myLocation))
        {
            world.removeBlock(myLocation);
        }
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
	public boolean useNeighborLight()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean isValidPlacementFace(Face face)
	{
		return face == Face.Top;
	}
}