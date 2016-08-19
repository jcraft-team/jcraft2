package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.GameSettings;
import com.chappelle.jcraft.jme3.appstate.BaseInputAppState;
import com.jme3.app.*;
import com.jme3.app.state.*;
import com.simsilica.lemur.*;

public class BeginningAppState extends BaseInputAppState<JCraftApplication>
{
	boolean isGuiShowing = false;
	private Container beginningOptionsContainer;
	private AppState loadingAppState;
	
	public BeginningAppState(AppState loadingAppState)
	{
		this.loadingAppState = loadingAppState;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initialize(Application app)
	{
		super.initialize(app);
		
		app.getStateManager().attach(new OptionPanelState());

		beginningOptionsContainer = new Container();
		beginningOptionsContainer.setLocalTranslation(300, 300, 0);
		beginningOptionsContainer.addChild(new Label("JCraft"));
		Button startGame = beginningOptionsContainer.addChild(new Button("Start Game"));
		startGame.addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				AppStateManager stateManager = getMyApplication().getStateManager();
				BeginningAppState.this.setEnabled(false);
				stateManager.attach(loadingAppState);
				
			}
		});
		Button exitGame = beginningOptionsContainer.addChild(new Button("Quit Game"));
		exitGame.addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				getMyApplication().stop();
			}
		});
	}

	@Override
	protected void onEnable()
	{
		super.onEnable();
		
		getState(StatsAppState.class).setEnabled(false);
		getMyApplication().getGuiNode().attachChild(beginningOptionsContainer);
	}

	@Override
	protected void onDisable()
	{
		super.onDisable();
		
		getState(StatsAppState.class).setEnabled(GameSettings.debugEnabled);
		getMyApplication().getGuiNode().detachChild(beginningOptionsContainer);
	}
}