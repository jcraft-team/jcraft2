package com.chappelle.jcraft.world.gen;

public abstract class AbstractFeature implements Feature
{
	protected long seed;

	@Override
	public void setSeed(long seed)
	{
		this.seed = seed;
	}
}
