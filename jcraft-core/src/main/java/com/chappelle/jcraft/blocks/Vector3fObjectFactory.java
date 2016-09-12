package com.chappelle.jcraft.blocks;

import org.apache.commons.pool2.*;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.jme3.math.Vector3f;

public class Vector3fObjectFactory extends BasePooledObjectFactory<Vector3f>
{
	@Override
	public Vector3f create() throws Exception
	{
		return new Vector3f();
	}

	@Override
	public PooledObject<Vector3f> wrap(Vector3f obj)
	{
		return new DefaultPooledObject<Vector3f>(obj);
	}

	@Override
	public void passivateObject(PooledObject<Vector3f> p) throws Exception
	{
		p.getObject().set(0, 0, 0);
	}

}
