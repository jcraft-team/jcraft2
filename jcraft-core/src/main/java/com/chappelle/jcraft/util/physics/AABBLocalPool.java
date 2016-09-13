package com.chappelle.jcraft.util.physics;

/**
 * Thread local accessor for the AABBPool
 */
final class AABBLocalPool extends ThreadLocal<AABBPool>
{
	protected AABBPool createNewDefaultPool()
	{
		return new AABBPool(400, 2500);
	}

	protected AABBPool initialValue()
	{
		return this.createNewDefaultPool();
	}
}
