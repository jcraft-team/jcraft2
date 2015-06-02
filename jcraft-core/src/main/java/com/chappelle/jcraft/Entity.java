package com.chappelle.jcraft;

import com.chappelle.jcraft.util.AABB;
import com.jme3.math.Vector3f;

public class Entity
{
	public float yAcceleration = -9.8f;
	public float yVelocity = 0;

	private boolean gravityEnabled = true;
	
	public double prevPosX;
	public double prevPosY;
	public double prevPosZ;
	/** Entity position X */
	public double posX;

	/** Entity position Y */
	public double posY;

	/** Entity position Z */
	public double posZ;


	public Vector3f motionVector = new Vector3f();
//	public Vector3f pos = new Vector3f();
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
	
	public Entity(World world)
	{
		this.world = world;
		this.boundingBox = AABB.getBoundingBox(0, 0, 0, 0, 0, 0);
	}

	public void update(float tpf)
	{
//		previousPos = pos.clone();
		motionVector.set(0, 0, 0);
		if(gravityEnabled)
		{
			yVelocity = yVelocity + tpf*yAcceleration;
			double oldY = posY;
			double newY = (oldY + tpf*yVelocity);
			double yDiff = Math.max(-0.9f, newY - oldY);
			motionVector.y += yDiff;
		}
	}

	public void setPosition(double x, double y, double z)
	{
		posX = x;
		posY = y;
		posZ = z;
//		this.pos.set(x, y, z);

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
		motionVector.set(0,0,0);
	}

	public void toggleGravity()
	{
		gravityEnabled = !gravityEnabled;
	}
}
