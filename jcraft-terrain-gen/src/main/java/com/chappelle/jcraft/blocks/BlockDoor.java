package com.chappelle.jcraft.blocks;

import org.apache.commons.lang3.BitField;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.blocks.shapes.BlockShape_Door;
import com.chappelle.jcraft.util.*;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector3f;

public class BlockDoor extends Block
{
	private static final BitField orientationField = new BitField(0x07);//00000111
	private static final BitField isOpenField = new BitField(0x8);      //00001000
    private static final BitField isTopField = new BitField(0x10);      //00010000

    private boolean userCanOpen;//Iron doors can't be opened by user
    
    private Vector3Int temp = new Vector3Int();
    
	public BlockDoor(int blockId, boolean userCanOpen)
	{
		super(blockId, new BlockSkin[] { new BlockSkin(new BlockSkin_TextureLocation(1, 5), true), new BlockSkin(new BlockSkin_TextureLocation(1, 6), false)});
		
		this.userCanOpen = userCanOpen;
		
		setShapes(new BlockShape_Door());
		
		this.isTransparent = true;
	}
	
	
    @Override
    public void onBlockPlaced(World world, Vector3Int location, Block.Face face, Vector3f cameraDirectionUnitVector)
    {
    	byte state = world.getBlockState(location);
    	int orientation = Block.Face.fromNormal(cameraDirectionUnitVector).ordinal();
    	state = (byte)orientationField.setValue(state, orientation);
        
        Block bottomBlock = world.getBlock(location.subtract(new Vector3Int(0,1,0)));
        if(this == bottomBlock)
        {
        	state = (byte)isTopField.setBoolean(state, true);
        	world.setBlockState(location.x, location.y, location.z, state);
        }
        else//This is the bottom door, need to create the top
        {
        	state = (byte)isTopField.setBoolean(state, false);
        	world.setBlockState(location.x, location.y, location.z, state);
            Vector3Int topLocation = location.add(0,1,0);
            world.setBlock(topLocation, this);
            onBlockPlaced(world, topLocation, null, cameraDirectionUnitVector);
        }
    }

	@Override
	public void onNeighborRemoved(World world, Vector3Int removedBlockLocation, Vector3Int myLocation)
	{
		byte blockState = world.getBlockState(myLocation);
		boolean top = isTop(blockState);
		if(top && myLocation.subtract(0, 1, 0).equals(removedBlockLocation))
		{
			world.removeBlock(myLocation);
		}
		if(!top && myLocation.add(0, 1, 0).equals(removedBlockLocation))
		{
			world.removeBlock(myLocation);
		}
		
		if(!top && myLocation.subtract(0, 1, 0).equals(removedBlockLocation))//Block underneath the door
		{
			world.removeBlock(myLocation);
		}
	}
	
	
    @Override
    public void onBlockActivated(World world, int x, int y, int z)
    {
    	if(userCanOpen)
    	{
    		Vector3Int location = new Vector3Int(x, y, z);
    		
    		byte blockState = world.getBlockState(location);
    		boolean open = isOpen(blockState);
    		blockState = (byte)isOpenField.setBoolean(blockState, !open);
    		world.setBlockState(x, y, z, blockState);
    		
    		Vector3Int otherDoorLocation = getOtherDoorSection(blockState, location);
			blockState = world.getBlockState(otherDoorLocation);
    		blockState = (byte)isOpenField.setBoolean(blockState, !open);
    		world.setBlockState(otherDoorLocation.x, otherDoorLocation.y, otherDoorLocation.z, blockState);
    		
    		if(open)
    		{
    			world.playSound(SoundConstants.MISC_DOOR_CLOSE);
    		}
    		else
    		{
    			world.playSound(SoundConstants.MISC_DOOR_OPEN);
    		}
    	}
    }

	@Override
	public void onBlockRemoved(World world, Vector3Int location)
	{
		world.playSound(SoundConstants.DIG_WOOD, 4);
	}

    private Vector3Int getOtherDoorSection(byte blockState, Vector3Int blockLocation)
    {
    	if(isTop(blockState))
    	{
    		return blockLocation.subtract(0, 1, 0);
    	}
    	else
    	{
    		return blockLocation.add(0, 1, 0);
    	}
    }

    public static boolean isTop(byte blockState)
    {
    	return isTopField.getValue(blockState) == 1 ? true : false;
    }

    public static boolean isOpen(byte blockState)
    {
    	return isOpenField.getValue(blockState) == 1 ? true : false;
    }
    
    @Override
    protected int getSkinIndex(Chunk chunk, Vector3Int location, Face face)
    {
        byte blockState = chunk.getBlockState(location);
        if (isTop(blockState))
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }
    
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    @Override
    public boolean isActionBlock()
    {
    	return true;
    }

    @Override
	public boolean useNeighborLight()
	{
		return false;
	}


	@Override
	public AABB getCollisionBoundingBox(World world, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBox(world, x, y, z);
	}


	@Override
	public AABB getSelectedBoundingBox(World world, int x, int y, int z)
	{
		return getCollisionBoundingBox(world, x, y, z);
	}


	@Override
	public boolean isValidPlacementFace(Face face)
	{
		return face == Block.Face.Top;
	}


	@Override
	public void setBlockBoundsBasedOnState(World world, int x, int y, int z)
	{
		temp.set(x, y, z);
		byte blockState = world.getBlockState(temp);
		Boolean open = isOpen(blockState);
		Vector3f orientation = getOrientation(blockState).normal;
		Block.Face homeFace = Block.Face.fromNormal(orientation);
		minX = 0;
		maxX = 1.0;
		minZ = 0;
		maxZ = 1.0;
		if(homeFace == Block.Face.Front)
		{
			if(open)
			{
				this.minX = 0.9f;
				this.maxX = 1.0f;
			}
			else
			{
				this.maxZ = 0.1;
			}
		}
		else if(homeFace == Block.Face.Back)
		{
			if(open)
			{
				this.minX = 0f;
				this.maxX = 0.1f;
			}
			else
			{
				this.maxZ = 0.9;
			}
		}
		else if(homeFace == Block.Face.Left)
		{
			if(open)
			{
				this.minZ = 0.9f;
				this.maxZ = 1.0f;
			}
			else
			{
				this.minX = 1.0;
				this.maxX = 0.9f;
			}
		}
		else if(homeFace == Block.Face.Right)
		{
			if(open)
			{
				this.minZ = 0f;
				this.maxZ = 0.1f;
			}
			else
			{
				this.minX = 0;
				this.maxX = 0.1f;
			}
		}
	}


	@Override
	public RayTrace collisionRayTrace(World world, int x, int y, int z, Vector3f startVec, Vector3f endVec)
	{
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.collisionRayTrace(world, x, y, z, startVec, endVec);
	}


	public static Block.Face getOrientation(byte blockState)
	{
		return Block.Face.values()[orientationField.getValue(blockState)];
	}
}