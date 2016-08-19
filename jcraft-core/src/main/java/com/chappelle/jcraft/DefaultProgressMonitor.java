package com.chappelle.jcraft;

public class DefaultProgressMonitor implements ProgressMonitor
{
	private String note;
	private float percentCompleted;
	
	@Override
	public String getNote()
	{
		return note;
	}

	@Override
	public void setNote(String note)
	{
		this.note = note;
	}

	@Override
	public float getPercentCompleted()
	{
		return percentCompleted;
	}

	@Override
	public void setPercentCompleted(float percentCompleted)
	{
		this.percentCompleted = percentCompleted;
	}
}