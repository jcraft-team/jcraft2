package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.GameSettings;
import com.chappelle.jcraft.jme3.appstate.BaseInputAppState;
import com.jme3.app.*;
import com.jme3.app.state.*;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.SpringGridLayout;

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
		beginningOptionsContainer.setLayout(new SpringGridLayout());
//		beginningOptionsContainer.setPreferredSize(new Vector3f(500, 500, 500));
		
		beginningOptionsContainer.addChild(new Label("JCraft"));
		Button startGame = beginningOptionsContainer.addChild(new Button("Start Game"));
		startGame.setInsets(new Insets3f(5, 5, 5, 5));
		startGame.setTextHAlignment(HAlignment.Center);
		startGame.setTextVAlignment(VAlignment.Center);
		startGame.setPreferredSize(new Vector3f(500, 35, 0));
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
		Button settingsButton = beginningOptionsContainer.addChild(new Button("Settings"));
		settingsButton.setInsets(new Insets3f(5, 5, 5, 5));
		settingsButton.setTextHAlignment(HAlignment.Center);
		settingsButton.setTextVAlignment(VAlignment.Center);
		settingsButton.setPreferredSize(new Vector3f(500, 35, 0));
		settingsButton.addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				//TODO:
			}
		});
		Button exitGame = beginningOptionsContainer.addChild(new Button("Quit Game"));
		exitGame.setInsets(new Insets3f(5, 5, 5, 5));
		exitGame.setTextHAlignment(HAlignment.Center);
		exitGame.setTextVAlignment(VAlignment.Center);
		exitGame.setPreferredSize(new Vector3f(500, 35, 0));
		exitGame.addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				getMyApplication().stop();
			}
		});
		beginningOptionsContainer.setLocalTranslation(GameSettings.screenWidth/2 - beginningOptionsContainer.getPreferredSize().x/2, GameSettings.screenHeight/2 + beginningOptionsContainer.getPreferredSize().y/2, 0);		
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