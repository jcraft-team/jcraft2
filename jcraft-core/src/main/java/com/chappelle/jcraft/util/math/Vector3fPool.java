package com.chappelle.jcraft.util.math;

import java.util.*;

import com.jme3.math.Vector3f;

/**
 * Simple object pool custom taylored for Vector3fs. Used to cut down on object creation
 */
public class Vector3fPool
{
	private int nextIndex;

	private final List<Vector3f> entries = new ArrayList<>();

	public Vector3f add(Vector3f v, float x, float y, float z)
	{
		return get(x, y, z).addLocal(v);
	}
	
	public Vector3f get(float x, float y, float z)
	{
		Vector3f result;

		if(this.nextIndex >= this.entries.size())
		{
			result = new Vector3f(x, y, z);
			this.entries.add(result);
		}
		else
		{
			result = this.entries.get(this.nextIndex);
			result.set(x, y, z);
		}

		++this.nextIndex;
		return result;
	}

	public void reset()
	{
		this.nextIndex = 0;
	}
	
	public static void main(String[] args)
	{
		Vector3fPool pool = new Vector3fPool();
		Vector3f vector3f = pool.get(10, 5, 10);
		System.out.println(vector3f);
		System.out.println(pool.add(vector3f, 2, 2, 2));
	}
}
