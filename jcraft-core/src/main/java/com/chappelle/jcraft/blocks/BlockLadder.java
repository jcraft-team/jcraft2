package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.BlockNavigator;
import com.chappelle.jcraft.BlockSkin;
import com.chappelle.jcraft.BlockSkin_TextureLocation;
import com.chappelle.jcraft.BlockState;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.World;
import com.chappelle.jcraft.shapes.BlockShape_Ladder;
import com.chappelle.jcraft.util.AABB;
import com.jme3.math.Vector3f;

public class BlockLadder extends Block
{
	public static final Short VAR_ORIENTATION = 1;
	
	public BlockLadder(int blockId)
	{
		super(blockId, new BlockSkin[] { new BlockSkin(new BlockSkin_TextureLocation(3, 5), false)});
		
		setShapes(new BlockShape_Ladder());
	}
	
	@Override
	public void onBlockPlaced(World world, Vector3Int location, Block.Face face, Vector3f cameraDirectionAsUnitVector)
	{
        BlockState blockState = world.getBlockState(location);
        blockState.put(VAR_ORIENTATION, face.oppositeNormal);
	}

	public void onBlockRemoved(World world, Vector3Int location)
	{
	}

	public void onEntityWalking(World world, Vector3Int location)
	{
	}

	public void setBlockBoundsBasedOnState(World world, int x, int y, int z)
	{
		BlockState blockState = world.getBlockState(new Vector3Int(x, y, z));
		Vector3f orientation = (Vector3f)blockState.get(VAR_ORIENTATION);
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
		BlockState blockState = world.getBlockState(myLocation);
		Vector3f orientation = (Vector3f)blockState.get(VAR_ORIENTATION);
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
	public boolean isTransparent()
	{
		return true;
	}

	@Override
	public boolean smothersBottomBlock()
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