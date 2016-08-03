package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.BlockState;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.blocks.shapes.BlockShape_Door;
import com.chappelle.jcraft.util.AABB;
import com.chappelle.jcraft.util.RayTrace;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector3f;

public class BlockDoor extends Block
{
    public static final Short VAL_SECTION_TOP = 1;
    public static final Short VAR_SECTION = 2;
    public static final Short VAR_OPEN = 3;
    public static final Short VAR_ORIENTATION = 4;

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
        BlockState blockState = world.getBlockState(location);
        blockState.put(VAR_OPEN, Boolean.FALSE);            
        blockState.put(VAR_ORIENTATION, cameraDirectionUnitVector);            
        
        Block bottomBlock = world.getBlock(location.subtract(new Vector3Int(0,1,0)));
        if(this == bottomBlock)
        {
            blockState.put(VAL_SECTION_TOP, Boolean.TRUE);
        }
        else//This is the bottom door, need to create the top
        {
            blockState.put(VAL_SECTION_TOP, Boolean.FALSE);
            Vector3Int topLocation = location.add(0,1,0);
            world.setBlock(topLocation, this);
            onBlockPlaced(world, topLocation, null, cameraDirectionUnitVector);
        }
    }

	@Override
	public void onNeighborRemoved(World world, Vector3Int removedBlockLocation, Vector3Int myLocation)
	{
		BlockState blockState = world.getBlockState(myLocation);
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
    		BlockState blockState = world.getBlockState(location);
    		Boolean open = (Boolean)blockState.get(VAR_OPEN);
    		blockState.put(VAR_OPEN, !open);
    		
    		blockState = world.getBlockState(getOtherDoorSection(blockState, location));
    		blockState.put(VAR_OPEN, !open);
    		
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

    private Vector3Int getOtherDoorSection(BlockState blockState, Vector3Int blockLocation)
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

    private boolean isTop(BlockState blockState)
    {
        Boolean isTop = (Boolean)blockState.get(VAL_SECTION_TOP);
        return isTop != null && isTop;
    }


    @Override
    protected int getSkinIndex(Chunk chunk, Vector3Int location, Face face)
    {
        BlockState blockState = chunk.getBlockState(location);
        Boolean isTop = (Boolean)blockState.get(VAL_SECTION_TOP);
        if (isTop == null || isTop)
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
		BlockState blockState = world.getBlockState(temp);
		Boolean open = (Boolean)blockState.get(VAR_OPEN);
		Vector3f orientation = (Vector3f)blockState.get(VAR_ORIENTATION);
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
}