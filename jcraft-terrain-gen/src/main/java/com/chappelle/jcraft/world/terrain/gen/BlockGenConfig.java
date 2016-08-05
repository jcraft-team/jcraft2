package com.chappelle.jcraft.world.terrain.gen;

public class BlockGenConfig
{
	private int minCluster = 10;
	private int maxCluster = 10;
	private float initialClusterProbability = 0.5f;
	private float clusterProbabilityDropOff = 0.05f;
	private int minY;
	private int maxY;
	private int surfaceOffset = 0;

	public int getMinCluster()
	{
		return minCluster;
	}

	public BlockGenConfig setMinCluster(int minCluster)
	{
		this.minCluster = minCluster;
		return this;
	}

	public int getMaxCluster()
	{
		return maxCluster;
	}

	public BlockGenConfig setMaxCluster(int maxCluster)
	{
		this.maxCluster = maxCluster;
		return this;
	}

	public float getInitialClusterProbability()
	{
		return initialClusterProbability;
	}

	public BlockGenConfig setInitialClusterProbability(float initialClusterProbability)
	{
		this.initialClusterProbability = initialClusterProbability;
		return this;
	}

	public float getClusterProbabilityDropOff()
	{
		return clusterProbabilityDropOff;
	}

	public BlockGenConfig setClusterProbabilityDropOff(float clusterProbabilityDropOff)
	{
		this.clusterProbabilityDropOff = clusterProbabilityDropOff;
		return this;
	}

	public int getSurfaceOffset()
	{
		return surfaceOffset;
	}

	public BlockGenConfig setSurfaceOffset(int surfaceOffset)
	{
		this.surfaceOffset = surfaceOffset;
		return this;
	}

	public int getMinY()
	{
		return minY;
	}

	public BlockGenConfig setMinY(int minY)
	{
		this.minY = minY;
		return this;
	}

	public int getMaxY()
	{
		return maxY;
	}

	public BlockGenConfig setMaxY(int maxY)
	{
		this.maxY = maxY;
		return this;
	}

}
