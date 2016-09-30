package com.chappelle.jcraft.blocks;

import org.apache.commons.lang3.BitField;

import com.chappelle.jcraft.blocks.shapes.BlockShape_Ladder;
import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.util.physics.AABB;
import com.chappelle.jcraft.world.World;
import com.jme3.math.Vector3f;

public class BlockLadder extends Block
{
	private static final BitField orientationField = new BitField(0x07);//00000111

	public BlockLadder(int blockId)
	{
		super(blockId, new Skin[] { new Skin(new TextureLocation(5, 3), false)});
		
		setShapes(new BlockShape_Ladder());
		
		this.isClimbable = true;
		this.isTransparent = true;
	}
	
	@Override
	public void onBlockPlaced(World world, Vector3Int location, Face face, Vector3f cameraDirectionAsUnitVector)
	{
        byte blockState = world.getBlockState(location);
        world.setBlockState(location.x, location.y, location.z, (byte)orientationField.setValue(blockState, Face.getOppositeFace(face).ordinal()));
	}

	public static Face getOrientation(byte blockState)
	{
		return Face.values()[orientationField.getValue(blockState)];
	}

	public void onBlockRemoved(World world, Vector3Int location)
	{
	}

	public void onEntityWalking(World world, Vector3Int location)
	{
	}

	public void setBlockBoundsBasedOnState(World world, int x, int y, int z)
	{
		byte blockState = world.getBlockState(new Vector3Int(x, y, z));
		Vector3f orientation = getOrientation(blockState).normal;
    	Face homeFace = Face.getOppositeFace(Face.fromNormal(orientation));
    	if(homeFace == Face.Back)
    	{
    		bounds.minX = 0;
    		bounds.minY = 0;
    		bounds.minZ = 1;
    		bounds.maxX = 1;
    		bounds.maxY = 1;
    		bounds.maxZ = 0.9f;
    	}
    	else if(homeFace == Face.Front)
    	{
    		bounds.minX = 0;
    		bounds.minY = 0;
    		bounds.minZ = 0;
    		bounds.maxX = 1;
    		bounds.maxY = 1;
    		bounds.maxZ = 0.1;
    	}
    	else if(homeFace == Face.Left)
    	{
    		bounds.minX = 1;
    		bounds.minY = 0;
    		bounds.minZ = 0;
    		bounds.maxX = 0.9f;
    		bounds.maxY = 1;
    		bounds.maxZ = 1;
    	}
    	else if(homeFace == Face.Right)
    	{
    		bounds.minX = 0;
    		bounds.minY = 0;
    		bounds.minZ = 0;
    		bounds.maxX = 0.1f;
    		bounds.maxY = 1;
    		bounds.maxZ = 1;
    	}
	}
	
	@Override
	public AABB getSelectedBoundingBox(World world, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBox(world, x, y, z);
	}


	@Override
	public void onNeighborRemoved(World world, Vector3Int location, Vector3Int myLocation)
	{
		byte blockState = world.getBlockState(myLocation);
		Vector3f orientation = getOrientation(blockState).normal;
    	Face homeFace = Face.getOppositeFace(Face.fromNormal(orientation));
    	if(homeFace == Face.Back)
    	{
    		if(myLocation.z + 1 == location.z)
    		{
    			world.removeBlock(myLocation);
    		}
    	}
    	else if(homeFace == Face.Front)
    	{
    		if(myLocation.z - 1 == location.z)
    		{
    			world.removeBlock(myLocation);
    		}
    	}
    	else if(homeFace == Face.Left)
    	{
    		if(myLocation.x + 1 == location.x)
    		{
    			world.removeBlock(myLocation);
    		}
    	}
    	else if(homeFace == Face.Right)
    	{
    		if(myLocation.x - 1 == location.x)
    		{
    			world.removeBlock(myLocation);
    		}
    	}
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean isValidPlacementFace(Face face)
	{
		return face != Face.Top && face != Face.Bottom;
	}

	@Override
	public AABB getCollisionBoundingBox(World world, int x, int y, int z)
	{
		return null;
	}
}