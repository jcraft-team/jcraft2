package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.BlockState;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.blocks.shapes.BlockShape_Torch;
import com.chappelle.jcraft.util.AABB;
import com.chappelle.jcraft.util.BlockNavigator;
import com.chappelle.jcraft.util.RayTrace;
import com.chappelle.jcraft.world.World;
import com.jme3.math.Vector3f;

public class BlockTorch extends Block
{
    /**
     * BlockState key that represents the contact normal of the block
     * clicked when placing the torch
     */
    public static final Short VAR_ORIENTATION = 1;
    public static final Short VAR_ATTACHED_BLOCK = 2;//TODO: could probably use just this one and remove the VAR_ORIENTATION in the future
	
    private Vector3Int temp = new Vector3Int();
    
    public BlockTorch(int blockId)
    {
    	super(blockId, new BlockSkin(new BlockSkin_TextureLocation(0, 5), true));
    	
    	setShapes(new BlockShape_Torch());
    	this.isTransparent = true;
    }

    @Override
    public void onBlockPlaced(World world, Vector3Int location, Block.Face face, Vector3f cameraDirectionUnitVector)
    {
    	Vector3Int neighborBlockLocation = BlockNavigator.getNeighborBlockLocalLocation(location, BlockNavigator.getOppositeFace(face));
    	Block attachedBlock = world.getBlock(neighborBlockLocation);
    	if(attachedBlock.blockId == this.blockId)
    	{
    		world.removeBlock(location);
    	}
    	else
    	{
    		BlockState blockState = world.getBlockState(location);
    		blockState.put(VAR_ORIENTATION, face);
    		blockState.put(VAR_ATTACHED_BLOCK, neighborBlockLocation);
    		world.playSound(SoundConstants.DIG_WOOD, 4);
    	}
    }
    
	@Override
	public void setBlockBoundsBasedOnState(World world, int x, int y, int z)
	{
		temp.set(x, y, z);
		BlockState blockState = world.getBlockState(temp);
		Block.Face homeFace = (Block.Face)blockState.get(VAR_ORIENTATION);
		float width = 0.15f;
		float height = 0.6f;
		float xzOffset = 0.35f;
		float yOffset = 0.3f;
		minX = 0.5 - width;
		minY = 0;
		minZ = 0.5 - width;
		maxX = 0.5 + width;
		maxY = height;
		maxZ = 0.5 + width;
		if(homeFace != Block.Face.Top)
		{
			minY += yOffset;
			maxY += yOffset;
		}
		if(homeFace == Block.Face.Front)
		{
			minZ -= xzOffset;
			maxZ -= xzOffset;
		}
		else if(homeFace == Block.Face.Back)
		{
			minZ += xzOffset;
			maxZ += xzOffset;
		}
		else if(homeFace == Block.Face.Left)
		{
			minX += xzOffset;
			maxX += xzOffset;
		}
		else if(homeFace == Block.Face.Right)
		{
			minX -= xzOffset;
			maxX -= xzOffset;
		}
	}
    
	@Override
	public void onBlockRemoved(World world, Vector3Int location)
	{
		world.playSound(SoundConstants.DIG_WOOD, 4);
	}

    @Override
    public void onNeighborRemoved(World world, Vector3Int removedBlockLocation, Vector3Int myLocation)
    {
        BlockState state = world.getBlockState(myLocation);
        Vector3Int attachedLocation = (Vector3Int)state.get(VAR_ATTACHED_BLOCK);
        if(removedBlockLocation.equals(attachedLocation))
        {
            world.removeBlock(myLocation);
        }
    }
    
	public AABB getCollisionBoundingBox(World par1World, int x, int y, int z)
	{
		return null;
	}

    @Override
    public boolean isValidPlacementFace(Block.Face face)
    {
        return face != Block.Face.Bottom;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
    
	@Override
	public AABB getSelectedBoundingBox(World world, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(world, x, y, z);
		AABB selectedBoundingBox = super.getSelectedBoundingBox(world, x, y, z);
		System.out.println(selectedBoundingBox);
		return selectedBoundingBox;
	}

	@Override
	public RayTrace collisionRayTrace(World world, int x, int y, int z, Vector3f startVec, Vector3f endVec)
	{
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.collisionRayTrace(world, x, y, z, startVec, endVec);
	}

}
