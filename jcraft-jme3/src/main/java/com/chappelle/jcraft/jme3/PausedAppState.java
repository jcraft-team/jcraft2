package com.chappelle.jcraft.jme3;

import com.jme3.app.*;
import com.jme3.app.state.*;
import com.simsilica.lemur.*;

public class PausedAppState extends BaseAppState
{
	boolean isGuiShowing = false;
	private Container pauseOptionsContainer;
	private SimpleApplication application;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initialize(Application app)
	{
		this.application = (SimpleApplication)app;
		
		pauseOptionsContainer = new Container();
		pauseOptionsContainer.setLocalTranslation(300, 300, 0);
		pauseOptionsContainer.addChild(new Label("Game menu"));
		Button startGame = pauseOptionsContainer.addChild(new Button("Back to Game"));
		startGame.addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				AppStateManager stateManager = application.getStateManager();
				stateManager.getState(GameRunningAppState.class).setEnabled(true);
				PausedAppState.this.setEnabled(false);
			}
		});
		Button options = pauseOptionsContainer.addChild(new Button("Options"));
		options.addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				//TODO
			}
		});
		Button exitGame = pauseOptionsContainer.addChild(new Button("Save and Quit to Title"));
		exitGame.addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				AppStateManager stateManager = application.getStateManager();
				GameRunningAppState gameRunningAppState = stateManager.getState(GameRunningAppState.class);
				stateManager.detach(PausedAppState.this);
				stateManager.detach(gameRunningAppState);
				BeginningAppState beginningAppState = stateManager.getState(BeginningAppState.class);
				beginningAppState.setEnabled(true);
			}
		});
		System.out.println("initialize");
	}

	@Override
	protected void cleanup(Application app)
	{
		this.application = null;
	}

	@Override
	protected void onEnable()
	{
		application.getGuiNode().attachChild(pauseOptionsContainer);
	}

	@Override
	protected void onDisable()
	{
		application.getGuiNode().detachChild(pauseOptionsContainer);
	}
}