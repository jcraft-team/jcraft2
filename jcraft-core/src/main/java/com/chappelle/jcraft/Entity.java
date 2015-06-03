package com.chappelle.jcraft;

import com.chappelle.jcraft.util.AABB;
import com.jme3.math.Vector3f;

public class Entity
{
	public float yAcceleration = -9.8f;

	public boolean isAirBorne;
	private boolean gravityEnabled = true;
	
	public double prevPosX;
	public double prevPosY;
	public double prevPosZ;
	
	/** Entity motion X */
	public double motionX;

	/** Entity motion Y */
	public double motionY;

	/** Entity motion Z */
	public double motionZ;
	
	/** Entity position X */
	public double posX;

	/** Entity position Y */
	public double posY;

	/** Entity position Z */
	public double posZ;

	public Vector3f previousPos = new Vector3f();

	public final AABB boundingBox;
	
	public float yOffset;
	public float ySize;

	/** How wide this entity is considered to be */
	public float width;

	/** How high this entity is considered to be */
	public float height;

	protected final World world;
	protected boolean onGround;
	
	/**
	 * The distance that has to be exceeded in order to triger a new step sound
	 * and an onEntityWalking event on a block
	 */
	protected int nextStepDistance = 1;
	
	/** The distance walked multiplied by 0.6 */
	public float distanceWalkedModified;
	public float distanceWalkedOnStepModified;
	
	public Entity(World world)
	{
		this.world = world;
		this.boundingBox = AABB.getBoundingBox(0, 0, 0, 0, 0, 0);
	}

	public void update(float tpf)
	{
		//Dampening
		this.motionX *= 0.98D;
		this.motionY *= 0.98D;
		this.motionZ *= 0.98D;
		if (Math.abs(this.motionX) < 0.005D)
		{
			this.motionX = 0.0D;
		}

		if (Math.abs(this.motionY) < 0.005D)
		{
			this.motionY = 0.0D;
		}

		if (Math.abs(this.motionZ) < 0.005D)
		{
			this.motionZ = 0.0D;
		}
		
		//Gravity
		this.motionY -= 0.008D;
		
		//Update previous position before movement happens
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
	}

	public void setPosition(double x, double y, double z)
	{
		posX = x;
		posY = y;
		posZ = z;

		float halfWidth = this.width / 2.0F;
		double minX = x - (double) halfWidth;
		double minY = y - (double) this.yOffset + (double) this.ySize;
		double minZ = z - (double) halfWidth;
		double maxX = x + (double) halfWidth;
		double maxY = y - (double) this.yOffset + (double) this.ySize + (double) this.height;
		double maxZ = z + (double) halfWidth;
		this.boundingBox.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
	}

	/**
	 * Sets the size of this Entity and updates the bounding box accordingly
	 * @param width
	 * @param height
	 */
	protected void setSize(float width, float height)
	{
		if (width != this.width || height != this.height)
		{
			this.width = width;
			this.height = height;
			this.boundingBox.maxX = this.boundingBox.minX + (double) this.width;
			this.boundingBox.maxZ = this.boundingBox.minZ + (double) this.width;
			this.boundingBox.maxY = this.boundingBox.minY + (double) this.height;
		}
	}
	
	protected void playStepSound(int x, int y, int z, Block block)
	{
		if(block.stepSound != null)
		{
			world.playSound(block.stepSound);
		}
	}
	
	protected void preparePlayerToSpawn()
	{
		while (posY < 256)
		{
			this.setPosition(posX, posY, posZ);

			if (world.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty())
			{
				break;
			}

			++posY;
		}
		setVelocity(0, 0, 0);
	}

	public void addVelocity(double par1, double par3, double par5)
	{
		this.motionX += par1;
		this.motionY += par3;
		this.motionZ += par5;
		this.isAirBorne = true;
	}

	public void setVelocity(double par1, double par3, double par5)
	{
		this.motionX = par1;
		this.motionY = par3;
		this.motionZ = par5;
	}

	public void toggleGravity()
	{
		gravityEnabled = !gravityEnabled;
	}
}
