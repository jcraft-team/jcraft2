package com.chappelle.jcraft.profiler;

public final class ProfilerResult implements Comparable<ProfilerResult>
{
	public double maxTime;
	public double elapsedTime;
	public String section;

	public ProfilerResult(String section, double maxTime, double elapsedTime)
	{
		this.section = section;
		this.maxTime = maxTime;
		this.elapsedTime = elapsedTime;
	}

	public int compareTo(ProfilerResult other)
	{
		return other.maxTime < this.maxTime ? -1 : (other.maxTime > this.maxTime ? 1 : other.section.compareTo(this.section));
	}
}
