package com.chappelle.jcraft.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple object pool custom taylored for AABBs. Used to cut down on object creation
 */
public class AABBPool
{
	private final int maxNumCleans;
	private final int numEntriesToRemove;

	private int nextIndex;
	private int cleanCount;
	private int maxIndex;

	private final List<AABB> entries = new ArrayList<AABB>();

	public AABBPool(int maxNumCleans, int numEntriesToRemove)
	{
		this.maxNumCleans = maxNumCleans;
		this.numEntriesToRemove = numEntriesToRemove;
	}

	public AABB getAABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		AABB result;

		if(this.nextIndex >= this.entries.size())
		{
			result = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
			this.entries.add(result);
		}
		else
		{
			result = this.entries.get(this.nextIndex);
			result.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
		}

		++this.nextIndex;
		return result;
	}

	public void cleanPool()
	{
		if(this.nextIndex > this.maxIndex)
		{
			this.maxIndex = this.nextIndex;
		}

		if(this.cleanCount++ == this.maxNumCleans)
		{
			int i = Math.max(this.maxIndex, this.entries.size() - this.numEntriesToRemove);

			while(this.entries.size() > i)
			{
				this.entries.remove(i);
			}

			this.maxIndex = 0;
			this.cleanCount = 0;
		}

		this.nextIndex = 0;
	}

	public void clearPool()
	{
		this.nextIndex = 0;
		this.entries.clear();
	}

	public int getlistAABBsize()
	{
		return this.entries.size();
	}

	public int getnextPoolIndex()
	{
		return this.nextIndex;
	}
}
