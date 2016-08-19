package com.chappelle.jcraft.jme3;

import java.util.concurrent.*;

import com.chappelle.jcraft.ProgressMonitor;
import com.jme3.app.*;
import com.jme3.app.state.*;
import com.simsilica.lemur.*;

public class LoadingAppState extends BaseAppState
{
	private SimpleApplication application;
	private Container progressContainer;
	private ProgressBar progressBar;
	private AppState nextAppState;
	private final ProgressMonitor progressMonitor;
	private Future<Void> future;
	private ExecutorService pool;
	private final Callable<Void> loader;

	public LoadingAppState(AppState nextAppState, Callable<Void> loader, ProgressMonitor progressMonitor)
	{
		this.nextAppState = nextAppState;
		this.loader = loader;
		this.progressMonitor = progressMonitor;
	}
	
	@Override
	protected void initialize(Application app)
	{
		if(!(app instanceof SimpleApplication))
		{
			throw new IllegalArgumentException("app must extend SimpleApplication");
		}
		application = (SimpleApplication)app;
		
		progressContainer = new Container();
		progressBar = new ProgressBar();
		progressContainer.setLocalTranslation(300, 300, 0);
		progressContainer.addChild(progressBar);
		progressBar.setProgressPercent(0.5);
		progressBar.setMessage("Initializing");
	}

	@Override
	public void update(float tpf)
	{
		if(future == null)
		{
			future = pool.submit(loader);
		}
		else
		{
			if(future.isDone())
			{
				getStateManager().detach(this);
			}
			else
			{
				progressBar.setMessage(progressMonitor.getNote());
				progressBar.setProgressPercent(progressMonitor.getPercentCompleted());
			}
		}
	}
	
	@Override
	protected void cleanup(Application app)
	{
	}

	@Override
	protected void onEnable()
	{
		pool = Executors.newFixedThreadPool(1);
		application.getGuiNode().attachChild(progressContainer);
	}

	@Override
	protected void onDisable()
	{
		application.getGuiNode().detachChild(progressContainer);
		pool.shutdown();
		getStateManager().attach(nextAppState);
	}
}
