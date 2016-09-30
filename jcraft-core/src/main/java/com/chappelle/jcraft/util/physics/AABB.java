package com.chappelle.jcraft.util.physics;

import com.jme3.math.Vector3f;

public class AABB
{
	private static final ThreadLocal<AABBPool> theAABBLocalPool = new AABBLocalPool();

	public static AABBPool getAABBPool()
	{
		return (AABBPool) theAABBLocalPool.get();
	}

	public double minX;
	public double minY;
	public double minZ;
	public double maxX;
	public double maxY;
	public double maxZ;

	/**
	 * Returns a bounding box with the specified bounds
	 */
	public static AABB getBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	/**
	 * Assumes bounds are within a cube of size 1
	 * @param width
	 * @param height
	 * @return
	 */
	public static AABB fromWidthAndHeight(double width, double height)
	{
		return new AABB(width, 0, width, 1 - width, height, 1 - width);
	}
	
	protected AABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	/**
	 * Returns whether the given bounding box intersects with this one.
	 */
	public boolean intersectsWith(AABB other)
	{
		return other.maxX > this.minX && other.minX < this.maxX ? (other.maxY > this.minY && other.minY < this.maxY ? other.maxZ > this.minZ && other.minZ < this.maxZ : false)	: false;
	}

	/**
	 * Sets the bounds of the bounding box
	 */
	public AABB setBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		return this;
	}

	/**
	 * Adds the coordinates to the bounding box extending it if the point lies
	 * outside the current ranges
	 */
	public AABB addCoord(double x, double y, double z)
	{
		double minX = this.minX;
		double minY = this.minY;
		double minZ = this.minZ;
		double maxX = this.maxX;
		double maxY = this.maxY;
		double maxZ = this.maxZ;

		if(x < 0.0D)
		{
			minX += x;
		}

		if(x > 0.0D)
		{
			maxX += x;
		}

		if(y < 0.0D)
		{
			minY += y;
		}

		if(y > 0.0D)
		{
			maxY += y;
		}

		if(z < 0.0D)
		{
			minZ += z;
		}

		if(z > 0.0D)
		{
			maxZ += z;
		}

		return getAABBPool().getAABB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	/**
	 * Returns a bounding box expanded by the specified vector (negative values shrink it)
	 */
	public AABB expand(double par1, double par3, double par5)
	{
		double d3 = this.minX - par1;
		double d4 = this.minY - par3;
		double d5 = this.minZ - par5;
		double d6 = this.maxX + par1;
		double d7 = this.maxY + par3;
		double d8 = this.maxZ + par5;
		return getAABBPool().getAABB(d3, d4, d5, d6, d7, d8);
	}

	/**
	 * Returns a bounding box offset by the specified coordinates (negative values shrink it)
	 */
	public AABB getOffsetBoundingBox(double xOffset, double yOffset, double zOffset)
	{
		return getAABBPool().getAABB(this.minX + xOffset, this.minY + yOffset, this.minZ + zOffset, this.maxX + xOffset, this.maxY + yOffset, this.maxZ + zOffset);
	}

	/**
	 * Offsets the current bounding box by the specified values
	 */
	public AABB offset(double xOffset, double yOffset, double zOffset)
	{
		this.minX += xOffset;
		this.minY += yOffset;
		this.minZ += zOffset;
		this.maxX += xOffset;
		this.maxY += yOffset;
		this.maxZ += zOffset;
		return this;
	}

	public AABB grow(double amt)
	{
		this.minX -= amt;
		this.minY -= amt;
		this.minZ -= amt;
		this.maxX += amt;
		this.maxY += amt;
		this.maxZ += amt;
		return this;
	}
	/**
	 * Returns true if the supplied {@code Vector3f} is completely inside the
	 * bounding box, false otherwise
	 */
	public boolean isVectorInside(Vector3f vector)
	{
		return vector.x > this.minX && vector.x < this.maxX ? (vector.y > this.minY && vector.y < this.maxY ? vector.z > this.minZ && vector.z < this.maxZ : false) : false;
	}

	/**
	 * Returns a copy of the bounding box.
	 */
	public AABB copy()
	{
		return getAABBPool().getAABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	/**
	 * Sets the bounding box to the same bounds as the bounding box passed in.
	 */
	public void set(AABB other)
	{
		this.minX = other.minX;
		this.minY = other.minY;
		this.minZ = other.minZ;
		this.maxX = other.maxX;
		this.maxY = other.maxY;
		this.maxZ = other.maxZ;
	}

	public double calculateYOffset(AABB other, double previousValue)
	{
		if (other.maxX > this.minX && other.minX < this.maxX)
		{
			if (other.maxZ > this.minZ && other.minZ < this.maxZ)
			{
				double d1;

				if (previousValue > 0.0D && other.maxY <= this.minY)
				{
					d1 = this.minY - other.maxY;

					if (d1 < previousValue)
					{
						previousValue = d1;
					}
				}

				if (previousValue < 0.0D && other.minY >= this.maxY)
				{
					d1 = this.maxY - other.minY;

					if (d1 > previousValue)
					{
						previousValue = d1;
					}
				}
				return previousValue;
			} 
			else
			{
				return previousValue;
			}
		} else
		{
			return previousValue;
		}
	}
	
	public double calculateZOffset(AABB other, double previousValue)
	{
		if (other.maxX > this.minX && other.minX < this.maxX)
		{
			if (other.maxY > this.minY && other.minY < this.maxY)
			{
				double d1;

				if (previousValue > 0.0D && other.maxZ <= this.minZ)
				{
					d1 = this.minZ - other.maxZ;

					if (d1 < previousValue)
					{
						previousValue = d1;
					}
				}

				if (previousValue < 0.0D && other.minZ >= this.maxZ)
				{
					d1 = this.maxZ - other.minZ;

					if (d1 > previousValue)
					{
						previousValue = d1;
					}
				}

				return previousValue;
			} 
			else
			{
				return previousValue;
			}
		} 
		else
		{
			return previousValue;
		}
	}
	

	public double calculateXOffset(AABB other, double previousValue)
	{
		if (other.maxY > this.minY && other.minY < this.maxY)
		{
			if (other.maxZ > this.minZ && other.minZ < this.maxZ)
			{
				double diff;

				if (previousValue > 0.0D && other.maxX <= this.minX)
				{
					diff = this.minX - other.maxX;

					if (diff < previousValue)
					{
						previousValue = diff;
					}
				}

				if (previousValue < 0.0D && other.minX >= this.maxX)
				{
					diff = this.maxX - other.minX;

					if (diff > previousValue)
					{
						previousValue = diff;
					}
				}

				return previousValue;
			} 
			else
			{
				return previousValue;
			}
		} 
		else
		{
			return previousValue;
		}
	}

	/**
	 * Checks if a vector is within the Y and Z bounds of the block.
	 */
	public boolean isVecInsideYZBounds(Vector3f v)
	{
		return v == null ? false : v.y >= this.minY && v.y <= this.maxY && v.z >= this.minZ && v.z <= this.maxZ;
	}

	/**
	 * Checks if a vector is within the X and Z bounds of the block.
	 */
	public boolean isVecInsideXZBounds(Vector3f v)
	{
		return v == null ? false : v.x >= this.minX && v.x <= this.maxX && v.z >= this.minZ && v.z <= this.maxZ;
	}

	/**
	 * Checks if a vector is within the X and Y bounds of the block.
	 */
	public boolean isVecInsideXYBounds(Vector3f v)
	{
		return v == null ? false : v.x >= this.minX && v.x <= this.maxX && v.z >= this.minY && v.y <= this.maxY;
	}

	public String toString()
	{
		return "aabb[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", "	+ this.maxZ + "]";
	}
}
