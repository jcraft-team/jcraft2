package com.chappelle.jcraft.jme3;

import java.util.concurrent.*;

import com.chappelle.jcraft.*;
import com.jme3.app.*;
import com.jme3.app.state.*;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.*;

public class LoadingAppState extends BaseAppState
{
	private SimpleApplication application;
	private Container progressContainer;
	private ProgressBar progressBar;
	private final ProgressMonitor progressMonitor;
	private Future<Void> future;
	private ExecutorService pool;
	private final LoadingCallable loader;

	public LoadingAppState(LoadingCallable loader)
	{
		this.loader = loader;
		this.progressMonitor = loader.getProgressMonitor();
	}
	
	@Override
	protected void initialize(Application app)
	{
		if(!(app instanceof SimpleApplication))
		{
			throw new IllegalArgumentException("app must extend SimpleApplication");
		}
		application = (SimpleApplication)app;
		
		progressBar = new ProgressBar();
		progressBar.setProgressPercent(0.5);
		progressBar.setMessage("Initializing");
		progressBar.getLabel().setInsets(new Insets3f(5, 5, 5, 5));
		progressBar.getLabel().setTextHAlignment(HAlignment.Center);
		progressBar.getLabel().setTextVAlignment(VAlignment.Center);
		progressBar.setPreferredSize(new Vector3f(500, 35, 0));

		progressContainer = new Container();
		progressContainer.addChild(progressBar);
		progressContainer.setLocalTranslation(GameSettings.screenWidth/2 - progressContainer.getPreferredSize().x/2, GameSettings.screenHeight/2 + progressContainer.getPreferredSize().y/2, 0);
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
		future = null;
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
		getStateManager().attach(loader.getNextAppState());
	}
}
