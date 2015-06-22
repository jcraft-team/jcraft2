package com.chappelle.jcraft;

import java.util.List;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.inventory.Inventory;
import com.chappelle.jcraft.inventory.ItemStack;
import com.chappelle.jcraft.util.AABB;
import com.chappelle.jcraft.util.MathUtils;
import com.chappelle.jcraft.util.RayTrace;
import com.chappelle.jcraft.world.World;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class EntityPlayer extends Entity
{
	private static final float MAX_SPEED = 0.08f;
	private static final float WALK_SPEED = 1.0f;
	private static final float NORMAL_FLYSPEED = 2.0f;
	private static final float FAST_FLYSPEED = 5.0f;
	private float flySpeed = NORMAL_FLYSPEED;

	private boolean up;
	private boolean down;
	private boolean forward;
	private boolean backward;
	private boolean left;
	private boolean right;

	//Temporary quaternion for limiting camera rotation
	private Quaternion tmpQuat = new Quaternion();

	//Temporary array for limiting camera rotation
	private float[] angles = new float[3];

	private ItemStack selected;
	
	public Camera cam;
	private Inventory inventory;
	
	public EntityPlayer(World world, Camera cam)
	{
		super(world);
		
		this.cam = cam;
		world.setPlayer(this);

		initInventory();
	}

	public Inventory getInventory()
	{
		return inventory;
	}
	
	private void initInventory()
	{
		inventory = new Inventory();
		for(int i = 1; i < 250; i++)
		{
			Block block = Block.blocksList[i];
			if(block != null)
			{
				inventory.add(block, 64);
			}
			else
			{
				break;
			}
		}
		selectBlock(1);
	}
	
	@Override
	public void update(float tpf)
	{
		super.update(tpf);
		
		moveAccordingToUserInputs(tpf);
		
		this.motionY *= 0.9800000190734863D;

		addFriction();
		handleLadderMovement();
		
		if (isFlying)
		{
			this.motionY *= 0.6D;
		} 
		
		capSpeed();
		//At this point all movement should be done(ie. altering of the motion vectors)
		moveEntity(motionX, motionY, motionZ);
	}

	private void handleLadderMovement()
	{
		if(isOnLadder())
		{
			moveDownLadder();
		}
		climbIfOnLadder();
	}

	private void capSpeed()
	{
		if(!isFlying)
		{
			if(Math.abs(motionX) > MAX_SPEED)
			{
				motionX = MAX_SPEED*Math.signum(motionX);
			}
			if(Math.abs(motionZ) > MAX_SPEED)
			{
				motionZ = MAX_SPEED*Math.signum(motionZ);
			}
		}
	}

	private void moveAccordingToUserInputs(float tpf)
	{
		float speed = WALK_SPEED;
		if(isFlying)
		{
			speed = flySpeed;
		}
		Vector3f camDir = cam.getDirection().mult(tpf*speed);
		Vector3f camLeft = cam.getLeft().mult(tpf*speed);

		limitCameraRotation();
		
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
		if(isFlying)
		{
			if(up)
			{
				addVelocity(0, flySpeed/10, 0);
			}
			if(down)
			{
				addVelocity(0, -flySpeed/10, 0);
			}
		}
	}

	private void limitCameraRotation()
	{
		cam.getRotation().toAngles(angles);
		if(angles[0] > FastMath.HALF_PI)
		{
			angles[0] = FastMath.HALF_PI;
			cam.setRotation(tmpQuat.fromAngles(angles));
		}
		else if(angles[0] < -FastMath.HALF_PI)
		{
			angles[0] = -FastMath.HALF_PI;
			cam.setRotation(tmpQuat.fromAngles(angles));
		}
	}

	private void addFriction()
	{
		float slipperiness = Block.DEFAULT_SLIPPERINESS;
		if(onGround)
		{
			Block block = world.getBlock(MathUtils.floor_double(this.posX), MathUtils.floor_double(this.boundingBox.minY) - 1, MathUtils.floor_double(this.posZ));
			if(block != null)
			{
				slipperiness = block.slipperiness;
			}
			
		}
		this.motionX *= (double) slipperiness;
		this.motionZ *= (double) slipperiness;
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
		posY = (this.boundingBox.minY + this.yOffset - this.ySize);
		
		updateWalkDistance();
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
	
	public void toggleFlying()
	{
		isFlying = !isFlying;
	}
	
	public void jump()
	{
		if(onGround)
		{
			this.motionY = 0.2D;
		}
	}
	
	public void breakBlock()
	{
		RayTrace rayTrace = pickBlock();
		if(rayTrace != null)
		{
			System.out.println("Removing block at [" + rayTrace.blockX + ", " + rayTrace.blockY + ", " + rayTrace.blockZ + "]");
			world.removeBlock(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
		}
	}
	
	public RayTrace pickBlock()
	{
		float blockReachDistance = 4.5f;
        Vector3f origin = new Vector3f((float)posX, (float)posY, (float)posZ);
        Vector3f look = cam.getDirection().normalize().mult(blockReachDistance).add(new Vector3f((float)posX, (float)posY, (float)posZ));
		return world.rayTraceBlocks(origin, look);
	}
	
	public void placeBlock()
	{
		RayTrace rayTrace = pickBlock();
		if(rayTrace != null)
		{
			Block selectedBlock = world.getBlock(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
			if(selectedBlock != null)
			{
				if(selectedBlock.isActionBlock())
				{
					selectedBlock.onBlockActivated(world, rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
				}
				else
				{
					world.setBlock(rayTrace, selected.getBlock());
				}
			}
		}
	}
	
	public void moveUp(boolean isPressed)
	{
		up = isPressed;
	}

	public void moveDown(boolean isPressed)
	{
		down = isPressed;
	}

	public void moveForward(boolean isPressed)
	{
		forward = isPressed;
	}
	
	public void moveRight(boolean isPressed)
	{
		right = isPressed;
	}
	
	public void moveBackward(boolean isPressed)
	{
		backward = isPressed;
	}
	
	public void moveLeft(boolean isPressed)
	{
		left = isPressed;
	}
	
	public void setFastFlying(boolean isPressed)
	{
		if(isPressed)
		{
			flySpeed = FAST_FLYSPEED;
		}
		else
		{
			flySpeed = NORMAL_FLYSPEED;
		}
		System.out.println("flySpeed=" + flySpeed);
	}
	
	public void selectBlock(int index)
	{
		selected = inventory.selectItem(index);
		if(selected != null)
		{
			System.out.println("Selected block is " + selected.getBlock().getClass().getName());
		}
	}
	
	public Block getSelectedBlock()
	{
		return selected == null ? null : selected.getBlock();
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
