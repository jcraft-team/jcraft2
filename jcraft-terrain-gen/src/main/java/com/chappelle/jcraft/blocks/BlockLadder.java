package com.chappelle.jcraft.blocks;

import org.apache.commons.lang3.BitField;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.blocks.shapes.BlockShape_Ladder;
import com.chappelle.jcraft.util.*;
import com.chappelle.jcraft.world.World;
import com.jme3.math.Vector3f;

public class BlockLadder extends Block
{
	private static final BitField orientationField = new BitField(0x07);//00000111

	public BlockLadder(int blockId)
	{
		super(blockId, new BlockSkin[] { new BlockSkin(new BlockSkin_TextureLocation(3, 5), false)});
		
		setShapes(new BlockShape_Ladder());
		
		this.isClimbable = true;
		this.isTransparent = true;
	}
	
	@Override
	public void onBlockPlaced(World world, Vector3Int location, Block.Face face, Vector3f cameraDirectionAsUnitVector)
	{
        byte blockState = world.getBlockState(location);
        world.setBlockState(location.x, location.y, location.z, (byte)orientationField.setValue(blockState, BlockNavigator.getOppositeFace(face).ordinal()));
	}

	public static Block.Face getOrientation(byte blockState)
	{
		return Block.Face.values()[orientationField.getValue(blockState)];
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
    	Block.Face homeFace = BlockNavigator.getOppositeFace(Block.Face.fromNormal(orientation));
    	if(homeFace == Block.Face.Back)
    	{
    		minX = 0;
    		minY = 0;
    		minZ = 1;
    		maxX = 1;
    		maxY = 1;
    		maxZ = 0.9f;
    	}
    	else if(homeFace == Block.Face.Front)
    	{
    		minX = 0;
    		minY = 0;
    		minZ = 0;
    		maxX = 1;
    		maxY = 1;
    		maxZ = 0.1;
    	}
    	else if(homeFace == Block.Face.Left)
    	{
    		minX = 1;
    		minY = 0;
    		minZ = 0;
    		maxX = 0.9f;
    		maxY = 1;
    		maxZ = 1;
    	}
    	else if(homeFace == Block.Face.Right)
    	{
    		minX = 0;
    		minY = 0;
    		minZ = 0;
    		maxX = 0.1f;
    		maxY = 1;
    		maxZ = 1;
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
    	Block.Face homeFace = BlockNavigator.getOppositeFace(Block.Face.fromNormal(orientation));
    	if(homeFace == Block.Face.Back)
    	{
    		if(myLocation.z + 1 == location.z)
    		{
    			world.removeBlock(myLocation);
    		}
    	}
    	else if(homeFace == Block.Face.Front)
    	{
    		if(myLocation.z - 1 == location.z)
    		{
    			world.removeBlock(myLocation);
    		}
    	}
    	else if(homeFace == Block.Face.Left)
    	{
    		if(myLocation.x + 1 == location.x)
    		{
    			world.removeBlock(myLocation);
    		}
    	}
    	else if(homeFace == Block.Face.Right)
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
		return face != Block.Face.Top && face != Block.Face.Bottom;
	}

	@Override
	public AABB getCollisionBoundingBox(World world, int x, int y, int z)
	{
		return null;
	}
}