package com.chappelle.jcraft.world;

public class FixedTimeOfDayProvider implements TimeOfDayProvider
{
	private float timeOfDay;
	
	public  FixedTimeOfDayProvider(float timeOfDay)
	{
		this.timeOfDay = timeOfDay;
	}
	
	@Override
	public float getTimeOfDay()
	{
		return timeOfDay;
	}

	@Override
	public void setTimeOfDay(float timeOfDay)
	{
		this.timeOfDay = timeOfDay;
	}

}
