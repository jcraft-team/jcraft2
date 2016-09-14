package com.chappelle.jcraft.world.gen;

import java.util.*;

public class ChunkGenContext
{
	private Map<String, Object> contextData = new HashMap<String, Object>();
	
	public void putData(String key, Object data)
	{
		contextData.put(key, data);
	}
	
	public Object getData(String key)
	{
		return contextData.get(key);
	}

	public byte[][] getDataAs2DByteArray(String key)
	{
		return (byte[][])contextData.get(key);
	}

	public byte[][][] getDataAs3DByteArray(String key)
	{
		return (byte[][][])contextData.get(key);
	}

	public float[][][] getDataAs2DFloatArray(String key)
	{
		return (float[][][])contextData.get(key);
	}

	public float[][][] getDataAs3DFloatArray(String key)
	{
		return (float[][][])contextData.get(key);
	}
}
