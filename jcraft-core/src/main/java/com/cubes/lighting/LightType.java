package com.cubes.lighting;

public enum LightType
{
	SKY(15), BLOCK(0);
	
	public final int defaultLightValue;

	private LightType(int defaultLightValue)
	{
		this.defaultLightValue = defaultLightValue;
	}
}
