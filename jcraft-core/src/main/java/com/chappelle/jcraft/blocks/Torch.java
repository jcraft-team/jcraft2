package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.BlockSkin;
import com.chappelle.jcraft.BlockSkin_TextureLocation;
import com.chappelle.jcraft.BlockState;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.World;
import com.chappelle.jcraft.shapes.BlockShape_Torch;
import com.jme3.math.Vector3f;

public class Torch extends Block
{
    /**
     * BlockState key that represents the contact normal of the block
     * clicked when placing the torch
     */
    public static final Short VAR_ORIENTATION = 1;
    public static final Short VAR_ATTACHED_BLOCK = 2;//TODO: could probably use just this one and remove the VAR_ORIENTATION in the future
	
    public Torch(World blockTerrainManager)
    {
    	super(blockTerrainManager, new BlockSkin(new BlockSkin_TextureLocation(0, 5), true));
    	
    	setShapes(new BlockShape_Torch());
    }

    @Override
    public void onBlockPlaced(Vector3Int location, Vector3f contactNormal, Vector3f cameraDirectionUnitVector)
    {
        BlockState blockState = terrainMgr.getBlockState(location);
        blockState.put(VAR_ORIENTATION, contactNormal);
        blockState.put(VAR_ATTACHED_BLOCK, location.subtract(Vector3Int.fromVector3f(contactNormal)));        
    }
    
    @Override
    public void onNeighborRemoved(Vector3Int removedBlockLocation, Vector3Int myLocation)
    {
        BlockState state = terrainMgr.getBlockState(myLocation);
        Vector3Int attachedLocation = (Vector3Int)state.get(VAR_ATTACHED_BLOCK);
        if(removedBlockLocation.equals(attachedLocation))
        {
            terrainMgr.removeBlock(myLocation);
        }
    }
    
    @Override
    public boolean isValidPlacementFace(Block.Face face)
    {
        return face != Block.Face.Bottom;
    }

    @Override
    public boolean smothersBottomBlock()
    {
        return false;
    }
    
    @Override
    public boolean isSolid()
    {
    	return false;
    }
    
    @Override
	public int getBlockLightValue()
	{
		return 14;
	}

}
