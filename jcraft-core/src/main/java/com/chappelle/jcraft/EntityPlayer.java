package com.chappelle.jcraft;

import java.util.List;

import com.chappelle.jcraft.blocks.PickedBlock;
import com.chappelle.jcraft.util.AABB;
import com.chappelle.jcraft.util.MathUtils;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class EntityPlayer extends Entity
{
	private boolean[] arrowKeys = new boolean[4];
	private BlockHelper blockHelper;
	private Block selected;

	public Camera cam;
	
	public EntityPlayer(World world, Camera cam, BlockHelper blockHelper)
	{
		super(world);
		
		this.cam = cam;
		this.blockHelper = blockHelper;

		selectBlock(1);
	}
	
	@Override
	public void update(float tpf)
	{
		super.update(tpf);
		
		float playerMoveSpeed = (1.5f * tpf);
		Vector3f camDir = cam.getDirection().mult(playerMoveSpeed);
		Vector3f camLeft = cam.getLeft().mult(playerMoveSpeed);
		
		if(arrowKeys[0])
		{
			addVelocity(camDir.x, 0, camDir.z);
		}
		if(arrowKeys[1])
		{
			Vector3f negate = camLeft.negate();
			addVelocity(negate.x, 0, negate.z);
		}
		if(arrowKeys[2])
		{
			Vector3f negate = camDir.negate();
			addVelocity(negate.x, 0, negate.z);
		}
		if(arrowKeys[3])
		{
			addVelocity(camLeft.x, 0, camLeft.z);
		}
		
		moveEntity(motionX, motionY, motionZ);
		
		float slipperiness = 0.91F;
		Block block = world.getBlock(MathUtils.floor_double(this.posX), MathUtils.floor_double(this.boundingBox.minY) - 1, MathUtils.floor_double(this.posZ));
		if(block != null)
		{
			slipperiness = block.slipperiness;
		}
		this.motionY *= 0.9800000190734863D;
		this.motionX *= (double) slipperiness;
		this.motionZ *= (double) slipperiness;
	}
	
	public void moveEntity(double x, double y, double z)
	{
		List<AABB> boundingBoxes = world.getCollidingBoundingBoxes(this, boundingBox.addCoord(x, y, z));
		for(AABB boundingBox : boundingBoxes)
		{
			y = boundingBox.calculateYOffset(this.boundingBox, y);
		}
		this.boundingBox.offset(0, y, 0);
		for(AABB boundingBox : boundingBoxes)
		{
			x = boundingBox.calculateXOffset(this.boundingBox, x);
		}
		this.boundingBox.offset(x, 0, 0);
		for(AABB boundingBox : boundingBoxes)
		{
			z = boundingBox.calculateZOffset(this.boundingBox, z);
		}
		this.boundingBox.offset(0, 0, z);

		posX = (float)(this.boundingBox.minX + this.boundingBox.maxX) / 2.0f;
		posZ = (float)(this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0f;
		posY = (float)this.boundingBox.minY + this.yOffset - this.ySize;
	}
	

	public void jump()
	{
		this.motionY = 0.41999998688697815D;
	}
	
    public boolean canPlaceBlock()
    {
    	int blockInteractionRange = 5;
        float distanceToDesiredPlacement = new Vector3f((float)posX, (float)posY, (float)posZ).distance(BlockHelper.toVector(blockHelper.getPointedBlockLocationInWorldSpace()));
		return distanceToDesiredPlacement > 1.25 && distanceToDesiredPlacement < blockInteractionRange + 1;
    }

	public void breakBlock()
	{
		PickedBlock pickedBlock = blockHelper.pickBlock();
		if(pickedBlock != null)
		{
			System.out.println("Removing block at " + pickedBlock.getBlockLocation());
			world.removeBlock(pickedBlock.getBlockLocation());
		}
	}
	
	public void placeBlock()
	{
    	PickedBlock pickedBlock = blockHelper.pickBlock();
    	if(pickedBlock != null)
    	{
    		Block block = pickedBlock.getBlock();
			if(block != null && block.isActionBlock())
    		{
    			block.onBlockActivated(world, pickedBlock);
    		}
			else if(canPlaceBlock())
    		{
				pickedBlock = blockHelper.pickNeighborBlock();
    			world.setBlock(pickedBlock, selected);
    			System.out.println("Setting block at " + pickedBlock.getBlockLocation());
//    			System.out.println("blockTerrain.setBlock(" + pickedBlock.getBlockLocation().x + ", " + pickedBlock.getBlockLocation().y + ", " + pickedBlock.getBlockLocation().z + ", Blocks.GRASS);");
    		}
    	}
	}
	
	public void moveUp(boolean isPressed)
	{
		arrowKeys[0] = isPressed;
	}
	
	public void moveRight(boolean isPressed)
	{
		arrowKeys[1] = isPressed;
	}
	
	public void moveDown(boolean isPressed)
	{
		arrowKeys[2] = isPressed;
	}
	
	public void moveLeft(boolean isPressed)
	{
		arrowKeys[3] = isPressed;
	}
	
	public void selectBlock(int index)
	{
		selected = Block.blocksList[index];
		System.out.println("Selected block is " + selected.getClass().getName());
	}
	
	public Block getSelectedBlock()
	{
		return selected;
	}
	
	public void preparePlayerToSpawn()
	{
		this.yOffset = 1.62F;
		this.setSize(0.6F, 1.8F);
		super.preparePlayerToSpawn();
	}

	
	/**
	 * Returns the Block the player is standing on
	 */
	public Block getStandingBlock()
	{
		Vector3Int location = getStandingBlockLocation();
		if(location != null)
		{
			return world.getBlock(location);
		}
		return null;
	}
	
	public Vector3Int getStandingBlockLocation()
	{
		Vector3Int result = new Vector3Int((int)posX, (int)posY, (int)posZ);
		if(result != null)
		{
			result.subtractLocal(0,2,0);
		}
		return result;
	}

}
