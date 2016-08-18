package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.GameSettings;
import com.jme3.app.*;
import com.jme3.app.state.*;
import com.simsilica.lemur.*;
import com.simsilica.lemur.style.BaseStyles;

public class BeginningAppState extends BaseAppState
{
	boolean isGuiShowing = false;
	private Container beginningOptionsContainer;
	private SimpleApplication application;
	private GameRunningAppState gameRunningAppState;
	
	public BeginningAppState(GameRunningAppState gameRunningAppState)
	{
		this.gameRunningAppState = gameRunningAppState;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initialize(Application app)
	{
		this.application = (SimpleApplication)app;
		
		GuiGlobals.initialize(app);
		BaseStyles.loadGlassStyle();
		GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

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
				AppStateManager stateManager = application.getStateManager();
				BeginningAppState.this.setEnabled(false);
				stateManager.attach(gameRunningAppState);
				
			}
		});
		Button exitGame = beginningOptionsContainer.addChild(new Button("Quit Game"));
		exitGame.addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				application.stop();
			}
		});
		System.out.println("initialize");
	}

	@Override
	protected void cleanup(Application app)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void onEnable()
	{
		getState(StatsAppState.class).setEnabled(false);
		application.getGuiNode().attachChild(beginningOptionsContainer);
	}

	@Override
	protected void onDisable()
	{
		getState(StatsAppState.class).setEnabled(GameSettings.debugEnabled);
		application.getGuiNode().detachChild(beginningOptionsContainer);
	}
}