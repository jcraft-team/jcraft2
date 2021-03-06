package com.chappelle.jcraft.blocks;

import org.apache.commons.lang3.BitField;

import com.chappelle.jcraft.blocks.shapes.BlockShape_Torch;
import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.util.physics.*;
import com.chappelle.jcraft.world.World;
import com.jme3.math.Vector3f;

public class BlockTorch extends Block
{
	private static final BitField orientationField = new BitField(0x07);//00000111
    /**
     * BlockState key that represents the contact normal of the block
     * clicked when placing the torch
     */
//    public static final Short VAR_ORIENTATION = 1;
//    public static final Short VAR_ATTACHED_BLOCK = 2;//TODO: could probably use just this one and remove the VAR_ORIENTATION in the future
	
    private Vector3Int temp = new Vector3Int();
    
    public BlockTorch(int blockId)
    {
    	super(blockId, new Skin(new TextureLocation(5, 0), true));
    	
    	setShapes(new BlockShape_Torch());
    	this.isTransparent = true;
    }

    @Override
    public void onBlockPlaced(World world, Vector3Int location, Face face, Vector3f cameraDirectionUnitVector)
    {
    	Vector3Int neighborBlockLocation = Face.getNeighborBlockLocalLocation(location, Face.getOppositeFace(face));
    	Block attachedBlock = world.getBlock(neighborBlockLocation);
    	if(attachedBlock.blockId == this.blockId)
    	{
    		world.removeBlock(location);
    	}
    	else
    	{
    		byte blockState = world.getBlockState(location);
    		world.setBlockState(location.x, location.y, location.z, (byte)orientationField.setValue(blockState, face.ordinal()));
//    		blockState.put(VAR_ATTACHED_BLOCK, neighborBlockLocation);
    		world.playSound(SoundConstants.DIG_WOOD, 4);
    	}
    }
    
	@Override
	public void setBlockBoundsBasedOnState(World world, int x, int y, int z)
	{
		temp.set(x, y, z);
		byte blockState = world.getBlockState(temp);
		Face homeFace = getOrientation(blockState);
		float width = 0.15f;
		float height = 0.6f;
		float xzOffset = 0.35f;
		float yOffset = 0.3f;
		bounds.minX = 0.5 - width;
		bounds.minY = 0;
		bounds.minZ = 0.5 - width;
		bounds.maxX = 0.5 + width;
		bounds.maxY = height;
		bounds.maxZ = 0.5 + width;
		if(homeFace != Face.Top)
		{
			bounds.minY += yOffset;
			bounds.maxY += yOffset;
		}
		if(homeFace == Face.Front)
		{
			bounds.minZ -= xzOffset;
			bounds.maxZ -= xzOffset;
		}
		else if(homeFace == Face.Back)
		{
			bounds.minZ += xzOffset;
			bounds.maxZ += xzOffset;
		}
		else if(homeFace == Face.Left)
		{
			bounds.minX += xzOffset;
			bounds.maxX += xzOffset;
		}
		else if(homeFace == Face.Right)
		{
			bounds.minX -= xzOffset;
			bounds.maxX -= xzOffset;
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
        byte state = world.getBlockState(myLocation);
        Face oppositeFace = Face.getOppositeFace(getOrientation(state));
        Vector3Int attachedLocation = myLocation.add(Vector3Int.fromVector3f(oppositeFace.normal));
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
    public boolean isValidPlacementFace(Face face)
    {
        return face != Face.Bottom;
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
		return selectedBoundingBox;
	}

	@Override
	public RayTrace collisionRayTrace(World world, int x, int y, int z, Vector3f startVec, Vector3f endVec)
	{
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.collisionRayTrace(world, x, y, z, startVec, endVec);
	}

	public static Face getOrientation(byte blockState)
	{
		return Face.values()[orientationField.getValue(blockState)];
	}
}
