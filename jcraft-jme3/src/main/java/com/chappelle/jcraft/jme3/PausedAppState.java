package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.GameSettings;
import com.jme3.app.*;
import com.jme3.app.state.*;
import com.jme3.math.Vector3f;
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
		startGame.setInsets(new Insets3f(5, 5, 5, 5));
		startGame.setTextHAlignment(HAlignment.Center);
		startGame.setTextVAlignment(VAlignment.Center);
		startGame.setPreferredSize(new Vector3f(500, 35, 0));
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
		options.setInsets(new Insets3f(5, 5, 5, 5));
		options.setTextHAlignment(HAlignment.Center);
		options.setTextVAlignment(VAlignment.Center);
		options.setPreferredSize(new Vector3f(500, 35, 0));

		options.addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				//TODO
			}
		});
		Button exitGame = pauseOptionsContainer.addChild(new Button("Save and Quit to Title"));
		exitGame.setInsets(new Insets3f(5, 5, 5, 5));
		exitGame.setTextHAlignment(HAlignment.Center);
		exitGame.setTextVAlignment(VAlignment.Center);
		exitGame.setPreferredSize(new Vector3f(500, 35, 0));
		
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
		pauseOptionsContainer.setLocalTranslation(GameSettings.screenWidth/2 - pauseOptionsContainer.getPreferredSize().x/2, GameSettings.screenHeight/2 + pauseOptionsContainer.getPreferredSize().y/2, 0);
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