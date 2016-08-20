package com.chappelle.jcraft.jme3;

import java.util.*;
import java.util.logging.Logger;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.jme3.appstate.BaseInputAppState;
import com.chappelle.jcraft.world.World;
import com.jme3.app.*;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.SpringGridLayout;

public class BeginningAppState extends BaseInputAppState<JCraftApplication>
{
	private final static Logger log = Logger.getLogger(BeginningAppState.class.getName());

	boolean isGuiShowing = false;
	private Container beginningOptionsContainer;
	
	public World world;
	protected EntityPlayer player;
	private CubesSettings cubesSettings;

	//Plugin api
	private List<WorldInitializer> worldInitializers = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@Override
	protected void initialize(Application app)
	{
		super.initialize(app);
		
		app.getStateManager().attach(new OptionPanelState());

		beginningOptionsContainer = new Container();
		beginningOptionsContainer.setLayout(new SpringGridLayout());
		
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
				startGame();
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

	private void startGame()
	{
		AssetManager assetManager = getApplication().getAssetManager();
		cubesSettings = new CubesSettings(assetManager, new ChunkMaterial(assetManager, "Textures/FaithfulBlocks.png"));
		long seed = getSeed();
		log.info("Using world seed: " + seed);
		log.info("Creating new world...get ready!");
		world = new World(getApplication(), cubesSettings, "JCraftWorld", seed);
		addInitializers(worldInitializers, WorldInitializer.class);

		Camera camera = getMyApplication().getCamera();
		player = new EntityPlayer(world, camera);
		
		configureWorld(world);
		
		AppStateManager stateManager = getMyApplication().getStateManager();
		BeginningAppState.this.setEnabled(false);
		stateManager.attach(new LoadingAppState(new WorldLoadingCallable(world, player, getMyApplication().getSettings())));
	}

	private void configureWorld(World world)
	{
		for(WorldInitializer gi : worldInitializers)
		{
			gi.configureWorld(world);
		}
	}

	private long getSeed()
	{
		long seed = System.currentTimeMillis();
		String seedStr = System.getProperty("seed");
		if(seedStr != null)
		{
			seed = Long.parseLong(seedStr);
		}
		return seed;
	}

	private <T> void addInitializers(List<T> list, Class<T> initializerClass)
	{
		Iterator<T> initializersIterator = ServiceLoader.load(initializerClass).iterator();
		while(initializersIterator.hasNext())
		{
			list.add(initializersIterator.next());
		}
	}

	@Override
	protected void onEnable()
	{
		super.onEnable();
		
		disableAppState(getState(StatsAppState.class));
		
		getMyApplication().getGuiNode().attachChild(beginningOptionsContainer);
	}

	@Override
	protected void onDisable()
	{
		super.onDisable();
		
		safeSetEnableAppState(getState(StatsAppState.class), GameSettings.debugEnabled);
		
		getMyApplication().getGuiNode().detachChild(beginningOptionsContainer);
	}
}