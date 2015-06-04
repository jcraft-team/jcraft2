package com.chappelle.jcraft;

import java.util.List;

import com.chappelle.jcraft.blocks.PickedBlock;
import com.chappelle.jcraft.util.AABB;
import com.chappelle.jcraft.util.MathUtils;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class EntityPlayer extends Entity
{
	private boolean forward;
	private boolean backward;
	private boolean left;
	private boolean right;
	
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
		
		moveAccordingToUserInputs(tpf);
		
		this.motionY *= 0.9800000190734863D;
		addFriction();
		if(isOnLadder())
		{
			moveDownLadder();
		}
		moveEntity(motionX, motionY, motionZ);
	}

	private void moveAccordingToUserInputs(float tpf)
	{
		Vector3f camDir = cam.getDirection().mult(tpf);
		Vector3f camLeft = cam.getLeft().mult(tpf);
		if(forward)
		{
			addVelocity(camDir.x, 0, camDir.z);
		}
		if(right)
		{
			Vector3f negate = camLeft.negate();
			addVelocity(negate.x, 0, negate.z);
		}
		if(backward)
		{
			Vector3f negate = camDir.negate();
			addVelocity(negate.x, 0, negate.z);
		}
		if(left)
		{
			addVelocity(camLeft.x, 0, camLeft.z);
		}
	}

	private void addFriction()
	{
		if(onGround)
		{
			float slipperiness = 0.91F;
			Block block = world.getBlock(MathUtils.floor_double(this.posX), MathUtils.floor_double(this.boundingBox.minY) - 1, MathUtils.floor_double(this.posZ));
			if(block != null)
			{
				slipperiness = block.slipperiness;
			}
			
			this.motionX *= (double) slipperiness;
			this.motionZ *= (double) slipperiness;
		}
		else
		{
			this.motionX *= 0.75D;
			this.motionZ *= 0.75D;
		}
	}

	private void moveDownLadder()
	{
		float ladderMotion = 0.07F;

		if (this.motionX < (double) (-ladderMotion))
		{
			this.motionX = (double) (-ladderMotion);
		}

		if (this.motionX > (double) ladderMotion)
		{
			this.motionX = (double) ladderMotion;
		}

		if (this.motionZ < (double) (-ladderMotion))
		{
			this.motionZ = (double) (-ladderMotion);
		}

		if (this.motionZ > (double) ladderMotion)
		{
			this.motionZ = (double) ladderMotion;
		}

		this.fallDistance = 0.0F;

		if (this.motionY < -ladderMotion)
		{
			this.motionY = -ladderMotion;
		}
	}
	
	public void moveEntity(double x, double y, double z)
	{
		double orgX = x;
		double orgY = y;
		double orgZ = z;
		
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

		this.onGround = orgY != y && orgY < 0.0D;
		this.isCollidedHorizontally = orgX != x || orgZ != z;
		this.isCollidedVertically = orgY != y;
		this.updateFallState(y, this.onGround);
		
		posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0f;
		posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0f;
		posY = this.boundingBox.minY + this.yOffset - this.ySize;
		
		updateWalkDistance();
		
		climbIfOnLadder();
	}

	private void climbIfOnLadder()
	{
		if (this.isCollidedHorizontally && this.isOnLadder())
		{
			this.motionY = 0.05D;
		}
	}

	private void updateWalkDistance()
	{
		//Calculate distance walked and play step sound
		double distX = prevPosX - posX;
		double distY = prevPosY - posY;
		double distZ = prevPosZ - posZ;
		this.distanceWalkedModified = (float) ((double) this.distanceWalkedModified + (double) Math.sqrt(distX * distX + distZ * distZ) * 0.6D);
		this.distanceWalkedOnStepModified = (float) ((double) this.distanceWalkedOnStepModified + (double) Math.sqrt(distX * distX + distY * distY + distZ * distZ) * 0.6D);
		int blockX = MathUtils.floor_double(this.posX);
		int blockY = MathUtils.floor_double(this.boundingBox.minY) - 1;
		int blockZ = MathUtils.floor_double(this.posZ);
		Block block = world.getBlock(blockX, blockY, blockZ);
		if (this.distanceWalkedOnStepModified > (float) this.nextStepDistance && block != null)
		{
			this.nextStepDistance = (int) this.distanceWalkedOnStepModified + 1;

			this.playStepSound(blockX, blockY, blockZ, block);
			block.onEntityWalking(world, blockX, blockY, blockZ);
		}
	}
	

	public void jump()
	{
		if(onGround)
		{
			this.motionY = 0.2D;
		}
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
//    			System.out.println("Setting block at " + pickedBlock.getBlockLocation());
    			System.out.println("world.setBlock(" + pickedBlock.getBlockLocation().x + ", " + pickedBlock.getBlockLocation().y + ", " + pickedBlock.getBlockLocation().z + ", Block." + pickedBlock.getBlock().toString().replace("Block","").toLowerCase() + ");");
    		}
    	}
	}
	
	public void moveUp(boolean isPressed)
	{
		forward = isPressed;
	}
	
	public void moveRight(boolean isPressed)
	{
		right = isPressed;
	}
	
	public void moveDown(boolean isPressed)
	{
		backward = isPressed;
	}
	
	public void moveLeft(boolean isPressed)
	{
		left = isPressed;
	}
	
	public void selectBlock(int index)
	{
		selected = Block.blocksList[index];
		if(selected != null)
		{
			System.out.println("Selected block is " + selected.getClass().getName());
		}
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

	/**
	 * returns true if this entity is by a ladder, false otherwise
	 */
	public boolean isOnLadder()
	{
		int i = MathUtils.floor_double(this.posX);
		int j = MathUtils.floor_double(this.boundingBox.minY);
		int k = MathUtils.floor_double(this.posZ);
		Block block = this.world.getBlock(i, j, k);
		return block != null && block.blockId == Block.ladder.blockId;
	}

}
