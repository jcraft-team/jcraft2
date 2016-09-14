package com.chappelle.jcraft.jme3;

import java.util.*;
import java.util.logging.Logger;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.jme3.appstate.BaseInputAppState;
import com.chappelle.jcraft.jme3.ui.ClickSoundButton;
import com.chappelle.jcraft.serialization.VoxelWorldSave;
import com.chappelle.jcraft.util.Context;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.*;
import com.chappelle.jcraft.world.gen.*;
import com.jme3.app.*;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.*;
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
	
	private SettingsAppState settingsAppState;
	private Context context;

	public BeginningAppState(Context context)
	{
		this.context = context;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initialize(Application app)
	{
		super.initialize(app);
		
		settingsAppState = new SettingsAppState();
		app.getStateManager().attach(new OptionPanelState());

		beginningOptionsContainer = new Container();
		beginningOptionsContainer.setLayout(new SpringGridLayout());
		
		beginningOptionsContainer.addChild(new Label("JCraft"));
		Button startGame = beginningOptionsContainer.addChild(new ClickSoundButton("Start Game"));
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
		Button settingsButton = beginningOptionsContainer.addChild(new ClickSoundButton("Settings"));
		settingsButton.setInsets(new Insets3f(5, 5, 5, 5));
		settingsButton.setTextHAlignment(HAlignment.Center);
		settingsButton.setTextVAlignment(VAlignment.Center);
		settingsButton.setPreferredSize(new Vector3f(500, 35, 0));
		settingsButton.addClickCommands(new Command<Button>()
		{
			@Override
			public void execute(Button source)
			{
				showSettings();
			}
		});
		Button exitGame = beginningOptionsContainer.addChild(new ClickSoundButton("Quit Game"));
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
	
    protected void showSettings()
	{
    	setEnabled(false);
    	
    	settingsAppState.setDoneAppState(BeginningAppState.class);
    	safeSetEnableAppState(settingsAppState, true);
	}

	protected AudioNode makeAudio(String location)
    {
        AudioNode result = new AudioNode(getApplication().getAssetManager(), location, AudioData.DataType.Buffer);
        result.setReverbEnabled(false);
        result.setVolume(2.0f);
        result.setPositional(false);
        return result;
    }


	private void startGame()
	{
		long seed = getSeed();
		context.put(ChunkGenerator.class, new ChunkGeneratorImpl(seed, getFeatures()));

		log.info("Using world seed: " + seed);
		log.info("Creating new world...get ready!");
		world = new World(getApplication(), context, "JCraftWorld", seed);

		Camera camera = getMyApplication().getCamera();
		player = new EntityPlayer(world, camera);
		// Setup player
		Vector3f playerLocation = (Vector3f)context.get(VoxelWorldSave.class).getGameData("playerLocation");
		if(playerLocation != null)
		{
			player.setPosition(playerLocation.x, playerLocation.y, playerLocation.z);
		}

		
		AppStateManager stateManager = getMyApplication().getStateManager();
		BeginningAppState.this.setEnabled(false);
		stateManager.attach(new LoadingAppState(new WorldLoadingCallable(world, player, getMyApplication().getSettings())));
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

	private List<Feature> getFeatures()
	{
		String featureProviderId = System.getProperty("featureProvider");
		if(featureProviderId == null)
		{
			featureProviderId = "core:default";
		}
		FeatureProvider featureProvider = getFeatureProviders().get(featureProviderId);
		if(featureProvider == null)
		{
			throw new IllegalStateException("No featureProvider with id=" + featureProviderId);
		}
		List<Feature> result = new ArrayList<>();
		result.addAll(featureProvider.getFeatures());
		return result;
	}

	private Map<String, FeatureProvider> getFeatureProviders()
	{
		Map<String, FeatureProvider> result = new HashMap<>();
		Iterator<FeatureProvider> featureProviderIter = ServiceLoader.load(FeatureProvider.class).iterator();
		while(featureProviderIter.hasNext())
		{
			FeatureProvider featureProvider = featureProviderIter.next();
			
			result.put(featureProvider.getId(), featureProvider);
		}		
		return result;
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
		
		getMyApplication().getGuiNode().detachChild(beginningOptionsContainer);
	}
}