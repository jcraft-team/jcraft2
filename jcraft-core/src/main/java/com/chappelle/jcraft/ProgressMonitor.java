package com.chappelle.jcraft;

public interface ProgressMonitor
{
	void setNote(String note);
	String getNote();

	float getPercentCompleted();
	
	void setPercentCompleted(float percent);
}
