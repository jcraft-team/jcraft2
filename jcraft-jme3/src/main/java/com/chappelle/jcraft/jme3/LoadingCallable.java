package com.chappelle.jcraft.jme3;

import java.util.concurrent.Callable;

import com.chappelle.jcraft.ProgressMonitor;
import com.jme3.app.state.AppState;

public interface LoadingCallable extends Callable<Void>
{
	AppState getNextAppState();
	ProgressMonitor getProgressMonitor();
}
