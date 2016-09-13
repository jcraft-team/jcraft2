package com.chappelle.jcraft.util.math;

import java.io.Serializable;

import com.chappelle.jcraft.util.Util;
import com.jme3.math.Vector3f;

public class Vector2Int implements Serializable
{
	public int x;
	public int z;

	public Vector2Int(int x, int z)
	{
		this.x = x;
		this.z = z;
	}

	public Vector2Int(Vector2Int v)
	{
		this.x = v.x;
		this.z = v.z;
	}
	
	public Vector2Int(){}

    public static Vector2Int zero() 
    {
        return new Vector2Int(0, 0);
    }

	public int getX()
	{
		return x;
	}

	public Vector2Int setX(int x)
	{
		this.x = x;
		return this;
	}

	public int getZ()
	{
		return z;
	}

	public Vector2Int setZ(int z)
	{
		this.z = z;
		return this;
	}

	public boolean hasNegativeCoordinate()
	{
		return ((x < 0) || (z < 0));
	}

	public Vector2Int set(Vector2Int vector3Int)
	{
		return set(vector3Int.getX(), vector3Int.getZ());
	}

	public Vector2Int set(int x, int z)
	{
		this.x = x;
		this.z = z;
		return this;
	}

	public Vector2Int add(Vector2Int vector2Int)
	{
		return add(vector2Int.getX(), vector2Int.getZ());
	}

	public Vector2Int add(int x, int z)
	{
		return new Vector2Int(this.x + x, this.z + z);
	}

	public Vector2Int addLocal(Vector2Int vector2Int)
	{
		return addLocal(vector2Int.getX(), vector2Int.getZ());
	}

	public Vector2Int addLocal(int x, int z)
	{
		this.x += x;
		this.z += z;
		return this;
	}

	public Vector2Int subtract(Vector2Int vector2Int)
	{
		return subtract(vector2Int.getX(), vector2Int.getZ());
	}

	public Vector2Int subtract(int x, int z)
	{
		return new Vector2Int(this.x - x, this.z - z);
	}

	public Vector2Int subtractLocal(Vector2Int vector3Int)
	{
		return subtractLocal(vector3Int.getX(), vector3Int.getZ());
	}

	public Vector2Int subtractLocal(int x, int z)
	{
		this.x -= x;
		this.z -= z;
		return this;
	}

	public Vector2Int negate()
	{
		return mult(-1);
	}

	public Vector2Int mult(int factor)
	{
		return mult(factor, factor, factor);
	}

	public Vector2Int mult(int x, int y, int z)
	{
		return new Vector2Int(this.x * x, this.z * z);
	}

	public Vector2Int negateLocal()
	{
		return multLocal(-1);
	}

	public Vector2Int multLocal(int factor)
	{
		return multLocal(factor, factor);
	}

	public Vector2Int multLocal(int x, int z)
	{
		this.x *= x;
		this.z *= z;
		return this;
	}

	public static Vector2Int fromVector3f(Vector3f vector)
	{
		vector = Util.compensateFloatRoundingErrors(vector);
		return new Vector2Int((int) vector.x, (int) vector.z);
	}

	public double distanceTo(Vector2Int other)
	{
		return Math.sqrt(distanceSquared(other));
	}
	
	private double distanceSquared(Vector2Int other)
	{
		int xDiff = other.x - x;
		int zDiff = other.z - z;
		return xDiff*xDiff + zDiff*zDiff;
	}
	
    /**
     * @param other the other point
     * @return the grid distance in between (aka 1-Norm, Minkowski or Manhattan distance)
     */
    public int gridDistance(Vector2Int other) 
    {
        return Math.abs(other.getX() - getX()) + Math.abs(other.getZ() - getZ());
    }

	@Override
	public Vector2Int clone()
	{
		return new Vector2Int(x, z);
	}

	@Override
	public String toString()
	{
		return "[Vector2Int x=" + x + " z=" + z + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Vector2Int other = (Vector2Int) obj;
		if(x != other.x)
			return false;
		if(z != other.z)
			return false;
		return true;
	}
}
