package com.chappelle.jcraft.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameContext
{
	private static GameContext INSTANCE;
	private Map<String, Object> objects = new ConcurrentHashMap<>();
	
	public static GameContext getInstance()
	{
		if(INSTANCE == null)
		{
			INSTANCE = new GameContext();
		}
		return INSTANCE;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String serviceName, Class<T> serviceClassType)
	{
		return (T)objects.get(serviceName);
	}
	
	public void set(String serviceName, Object obj)
	{
		if(containsObject(serviceName))
		{
			throw new ObjectAlreadyRegisteredException(serviceName);
		}
		objects.put(serviceName, obj);
	}
	
	public boolean containsObject(String serviceName)
	{
		return objects.containsKey(serviceName);
	}
}
