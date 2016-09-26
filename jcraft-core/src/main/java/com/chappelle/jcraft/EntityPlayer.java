package com.chappelle.jcraft;

import java.util.List;
import java.util.logging.Logger;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.inventory.*;
import com.chappelle.jcraft.util.math.*;
import com.chappelle.jcraft.util.physics.*;
import com.chappelle.jcraft.world.World;
import com.jme3.math.*;
import com.jme3.renderer.Camera;

public class EntityPlayer extends Entity
{
	private final static Logger log = Logger.getLogger(EntityPlayer.class.getName()); 

	private static final float WALK_SPEED = 1.0f;
	private static final float NORMAL_FLYSPEED = WALK_SPEED*2;
	private static final float FAST_FLYSPEED = NORMAL_FLYSPEED*2;
	private float flySpeed = NORMAL_FLYSPEED;
	private final float MAX_SPEED = FAST_FLYSPEED;

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
	private RayTrace currentRayTrace;
	
	public EntityPlayer(World world, Camera cam)
	{
		super(world);
		
		this.cam = cam;
		world.setPlayer(this);
	}

	public Inventory getInventory()
	{
		return inventory;
	}
	
	public void initInventory()
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
		currentRayTrace = performRayTrace();
		
		moveAccordingToUserInputs(tpf);
		
		addFriction();
		handleLadderMovement();
		
		
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
//			if(Math.abs(motionX) > MAX_SPEED)
//			{
//				motionX = MAX_SPEED*Math.signum(motionX);
//			}
//			if(Math.abs(motionZ) > MAX_SPEED)
//			{
//				motionZ = MAX_SPEED*Math.signum(motionZ);
//			}
		}
	}

	private void moveAccordingToUserInputs(float tpf)
	{
		float speed = 1.0f *tpf;
		if(isFlying)
		{
			speed = flySpeed*tpf;
		}
		if(speed > MAX_SPEED)
		{
			speed = MAX_SPEED;
		}
		limitCameraRotation();
		
		float yaw = angles[1];
		if(forward)
		{
			motionX += speed * FastMath.sin(yaw);
			motionZ += speed * FastMath.cos(yaw);
		}
		if(right)
		{
			motionX += speed * FastMath.sin(yaw-FastMath.HALF_PI);
			motionZ += speed * FastMath.cos(yaw-FastMath.HALF_PI);
		}
		if(backward)
		{
			motionX += speed * FastMath.sin(yaw+FastMath.PI);
			motionZ += speed * FastMath.cos(yaw+FastMath.PI);
		}
		if(left)
		{
			motionX += speed * FastMath.sin(yaw+FastMath.HALF_PI);
			motionZ += speed * FastMath.cos(yaw+FastMath.HALF_PI);
		}
		if(isFlying)
		{
			if(up)
			{
				addVelocity(0, speed, 0);
			}
			if(down)
			{
				addVelocity(0, -speed, 0);
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
		RayTrace rayTrace = getRayTrace();
		if(rayTrace != null)
		{
			world.removeBlock(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
		}
	}
	
	/**
	 * Lazy initializes the RayTrace.
	 * @return
	 */
	public RayTrace getRayTrace()
	{
		//If this causes problems we can just go back to not caching this.
		if(currentRayTrace == null)
		{
			currentRayTrace = performRayTrace();
		}
		return currentRayTrace;
	}

	private RayTrace performRayTrace()
	{
		float blockReachDistance = 5f;
        Vector3f origin = new Vector3f((float)posX, (float)posY, (float)posZ);
        Vector3f look = cam.getDirection().normalize().mult(blockReachDistance).add(new Vector3f((float)posX, (float)posY, (float)posZ));
        RayTrace rayTrace = world.rayTraceEntities(origin, look);
        if(rayTrace == null)
        {
        	return world.rayTraceBlocks(origin, look);
        }
        else
        {
        	return rayTrace;
        }
	}
	
    public Vector3f getCameraDirectionAsUnitVector()
    {
    	Vector3f cameraDirection = cam.getDirection().normalize();
		float xPos = cameraDirection.angleBetween(Vector3f.UNIT_X);
    	float xNeg = cameraDirection.angleBetween(Vector3f.UNIT_X.negate());
    	float zPos = cameraDirection.angleBetween(Vector3f.UNIT_Z);
    	float zNeg = cameraDirection.angleBetween(Vector3f.UNIT_Z.negate());
    	if(isFirstArgMin(xPos, xNeg, zPos, zNeg))
    	{
    		return Vector3f.UNIT_X;
    	}
    	else if(isFirstArgMin(xNeg, xPos, zPos, zNeg))
    	{
    		return Vector3f.UNIT_X.negate();
    	}
    	else if(isFirstArgMin(zPos, xPos, xNeg, zNeg))
    	{
    		return Vector3f.UNIT_Z;
    	}
		return Vector3f.UNIT_Z.negate();
    }

    private static boolean isFirstArgMin(float a, float b, float c, float d)
    {
    	if(a < b && a < c && a < d)
    	{
    		return true;
    	}
    	return false;
    }
	
	
	public void placeBlock()
	{
		RayTrace rayTrace = getRayTrace();
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
					world.setBlock(rayTrace, getCameraDirectionAsUnitVector(), selected.getBlock());
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
		log.fine("flySpeed=" + flySpeed);
	}
	
	public void selectBlock(int index)
	{
		selected = inventory.selectItem(index);
		if(selected != null)
		{
			log.info("Selected block is " + selected.getBlock().getClass().getName());
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
		return block != null && block.isClimbable;
	}

}
