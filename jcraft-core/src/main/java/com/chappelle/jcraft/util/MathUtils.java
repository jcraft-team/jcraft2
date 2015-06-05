package com.chappelle.jcraft.util;

import com.jme3.math.Vector3f;

public final class MathUtils
{
	public static int floor_double(double val)
	{
		int intVal = (int) val;
		return val < (double) intVal ? intVal - 1 : intVal;
	}

	/**
	 * Returns a new vector with x value equal to the second parameter, along
	 * the line between this vector and the passed in vector, or null if not
	 * possible.
	 */
	public static Vector3f getIntermediateWithXValue(Vector3f thisVec, Vector3f vec, double xValue)
	{
		double xDiff = vec.x - thisVec.x;
		double yDiff = vec.y - thisVec.y;
		double zDiff = vec.z - thisVec.z;

		if (xDiff * xDiff < 1.0000000116860974E-7D)
		{
			return null;
		} 
		else
		{
			double xAvg = (xValue - thisVec.x) / xDiff;
			return xAvg >= 0.0D && xAvg <= 1.0D ? new Vector3f((float)(thisVec.x + xDiff * xAvg), (float)(thisVec.y + yDiff * xAvg), (float)(thisVec.z + zDiff * xAvg)) : null;
		}
	}

	/**
	 * Returns a new vector with y value equal to the second parameter, along
	 * the line between this vector and the passed in vector, or null if not
	 * possible.
	 */
	public static Vector3f getIntermediateWithYValue(Vector3f thisVec, Vector3f par1Vec3, double par2)
	{
		double d1 = par1Vec3.x - thisVec.x;
		double d2 = par1Vec3.y - thisVec.y;
		double d3 = par1Vec3.z - thisVec.z;

		if (d2 * d2 < 1.0000000116860974E-7D)
		{
			return null;
		} 
		else
		{
			double d4 = (par2 - thisVec.y) / d2;
			return d4 >= 0.0D && d4 <= 1.0D ? new Vector3f((float)(thisVec.x + d1 * d4), (float)(thisVec.y + d2 * d4), (float)(thisVec.z + d3 * d4)) : null;
		}
	}

	/**
	 * Returns a new vector with z value equal to the second parameter, along
	 * the line between this vector and the passed in vector, or null if not
	 * possible.
	 */
	public static Vector3f getIntermediateWithZValue(Vector3f thisVec, Vector3f par1Vec3, double par2)
	{
		double d1 = par1Vec3.x - thisVec.x;
		double d2 = par1Vec3.y - thisVec.y;
		double d3 = par1Vec3.z - thisVec.z;

		if (d3 * d3 < 1.0000000116860974E-7D)
		{
			return null;
		} 
		else
		{
			double d4 = (par2 - thisVec.z) / d3;
			return d4 >= 0.0D && d4 <= 1.0D ? new Vector3f((float)(thisVec.x + d1 * d4), (float)(thisVec.y + d2 * d4), (float)(thisVec.z + d3 * d4)) : null;
		}
	}

	public static double squareDistanceTo(Vector3f from, Vector3f to)
	{
		double d0 = to.x - from.x;
		double d1 = to.y - from.y;
		double d2 = to.z - from.z;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	private MathUtils(){}
}
