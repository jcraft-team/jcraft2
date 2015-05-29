package com.chappelle.jcraft.blocks;

import com.cubes.Block;
import com.cubes.Vector3Int;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;

/**
 * Object created when a block is picked. Contains all sorts of useful information about the type of block and 
 * where the block was picked.
 */
public class PickedBlock
{
    private Block block;
    private Vector3f collisionLocation;
    private Vector3Int blockLocation;
    private Vector3f contactNormal;
    private Vector3f cameraDirection;

    public PickedBlock(Block block, Vector3Int blockLocation, CollisionResults collisionResults, Vector3f cameraDirection)
    {
        this.block = block;
        Vector3f collisionContactPoint = collisionResults.getClosestCollision().getContactPoint();
        this.collisionLocation = com.cubes.Util.compensateFloatRoundingErrors(collisionContactPoint);
        this.blockLocation = blockLocation;
        this.contactNormal = collisionResults.getClosestCollision().getContactNormal();
        this.cameraDirection = cameraDirection;
    }

    public void setBlock(Block block)
    {
        this.block = block;
    }
    
    public Block getBlock()
    {
        return block;
    }

    public Vector3f getCollisionLocation()
    {
        return collisionLocation;
    }

    public Vector3Int getBlockLocation()
    {
        return blockLocation;
    }

    public Vector3f getContactNormal()
    {
        return contactNormal;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("Block: ");
        result.append(block.getClass().getSimpleName());
        result.append("\r\n");
        result.append("Collision: ");
        result.append(collisionLocation);
        result.append("\r\n");
        result.append("Block Location: ");
        result.append(blockLocation);
        result.append("\r\n");
        return result.toString();
    }

	public Vector3f getCameraDirection()
	{
		return cameraDirection;
	}
}
