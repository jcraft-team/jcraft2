package com.chappelle.jcraft;

import com.chappelle.jcraft.util.AABB;
import com.jme3.math.Vector3f;

public class Entity
{
	public float yAcceleration = -50.0f;
	public float yVelocity = 0;

	private boolean gravityEnabled = true;
	
	public double prevPosX;
	public double prevPosY;
	public double prevPosZ;

	public Vector3f pos = new Vector3f();

	public final AABB boundingBox;
	
	public float yOffset;
	public float ySize;

	/** How wide this entity is considered to be */
	public float width;

	/** How high this entity is considered to be */
	public float height;

	protected final World world;
	
	public Entity(World world)
	{
		this.world = world;
		this.boundingBox = AABB.getBoundingBox(0, 0, 0, 0, 0, 0);
	}
	
	public void setPosition(float x, float y, float z)
	{
		this.pos.set(x, y, z);

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

	public void update(float tpf)
	{
		if(gravityEnabled)
		{
			yVelocity = yVelocity + tpf*yAcceleration;
			float oldY = pos.y;
			float newY = (oldY + tpf*yVelocity);
			float yDiff = Math.max(-2.9f, newY - oldY);
			pos.addLocal(0, yDiff, 0);
		}
		
	}
	
	public void toggleGravity()
	{
		gravityEnabled = !gravityEnabled;
	}
}
