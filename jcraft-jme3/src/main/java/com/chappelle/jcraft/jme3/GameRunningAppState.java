package com.chappelle.jcraft.jme3;

import java.util.Timer;
import java.util.logging.Logger;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.commands.SimpleCommandHandler;
import com.chappelle.jcraft.debug.*;
import com.chappelle.jcraft.jme3.appstate.BaseInputAppState;
import com.chappelle.jcraft.serialization.*;
import com.chappelle.jcraft.util.physics.AABB;
import com.chappelle.jcraft.world.World;
import com.jme3.app.*;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.*;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.*;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;

public class GameRunningAppState extends BaseInputAppState<JCraftApplication>
{
	private final static Logger log = Logger.getLogger(GameRunningAppState.class.getName()); 

	//General game stuff
	public World world;
	protected EntityPlayer player;
	
	//Debug settings
	public boolean debugEnabled = false;
	private boolean wireframe;
	
	//World save objects
	public VoxelWorldSave voxelWorldSave;
	private WorldSaveTask worldSaveTask;
	private Timer worldSaveTimer;
	private InputManager inputManager;
	private ViewPort viewPort;
	private AppStateManager stateManager;
	private AssetManager assetManager;
	private Node rootNode;
	private AppSettings settings;
	private PausedAppState pausedAppState;
	private CommandLineAppState commandLineAppState;
	private PlayerControl playerControl;
	
	public GameRunningAppState(EntityPlayer player, World world, AppSettings settings)
	{
		this.settings = settings;
		this.player = player;
		this.world = world;
		this.voxelWorldSave = world.getVoxelWorldSave();
	}
	
	@Override
	protected void initialize(Application app)
	{
		super.initialize(app);
		
		inputManager = app.getInputManager();
		viewPort = app.getViewPort();
		stateManager = app.getStateManager();
		rootNode = getMyApplication().getRootNode();
		assetManager = app.getAssetManager();
		debugEnabled = GameSettings.debugEnabled;
		pausedAppState = new PausedAppState();
		commandLineAppState = new CommandLineAppState(new SimpleCommandHandler(player, world));
		
		initInputMappings();

		stateManager.attach(new DebugAppState(settings, new DebugDataProvider(player, world)));
		rootNode.addControl(playerControl = new PlayerControl(getMyApplication(), player));
		
		log.info("****************************************************************************");
		log.info("Press F1 for fullscreen, F3 to toggle debug, F4 to toggle profiler.");
		log.info("See key bindings in JCraft class for other controls");
		log.info("****************************************************************************");
		log.info("\r\n\r\n");

		worldSaveTask = new WorldSaveTask(world, voxelWorldSave);
		worldSaveTimer = new Timer("WorldSave");
		long autoSaveInterval = GameSettings.autoSaveInterval;
		if(autoSaveInterval > 0)
		{
			worldSaveTimer.scheduleAtFixedRate(worldSaveTask, 1*1000, GameSettings.autoSaveInterval);
		}
		
		if(GameSettings.skyEnabled)
		{
			Float timeOfDay = (Float) voxelWorldSave.getGameData("timeOfDay");
			AdvancedSkyAppState sky = new AdvancedSkyAppState(timeOfDay == null ? 6 : timeOfDay);
			stateManager.attach(sky);
			world.setTimeOfDayProvider(sky);
		}
		else
		{
			viewPort.setBackgroundColor(GameSettings.defaultSkyColor);
		}
		initInputMappings();

		rootNode.addControl(new CrosshairsControl(getMyApplication(), settings, player));
		rootNode.addControl(new HighlightSelectedBlockControl(world, player, assetManager));
		
		world.addToScene(rootNode);
	}

	
	@Override
	protected void cleanup(Application app)
	{
		super.cleanup(app);
		
		AdvancedSkyAppState sky = stateManager.getState(AdvancedSkyAppState.class);
		if(stateManager.hasState(sky))
		{
			stateManager.detach(sky);
		}
		DebugAppState debugAppState = stateManager.getState(DebugAppState.class);
		if(stateManager.hasState(debugAppState))
		{
			stateManager.detach(debugAppState);
		}
		rootNode.removeControl(CrosshairsControl.class);
		rootNode.removeControl(PlayerControl.class);
		rootNode.removeControl(HighlightSelectedBlockControl.class);

		//Cancel auto save thread and flush any modified chunks to disk
		worldSaveTimer.cancel();
		voxelWorldSave.flushSave();

		world.destroy();
		GameSettings.save();

		inputManager = null;
		viewPort = null;
		stateManager = null;
		rootNode = null;
		assetManager = null;
		pausedAppState = null;
		
	}

	@Override
	protected void onEnable()
	{
		super.onEnable();
		
		safeSetEnableAppState(getState(StatsAppState.class), GameSettings.debugEnabled);
		setPlayerEnabled(true);
		GuiGlobals.getInstance().setCursorEventsEnabled(false);
	}

	@Override
	public void startListeningForInput()
	{
		super.startListeningForInput();
		playerControl.startListeningForInput();
	}
	
	@Override
	protected void onDisable()
	{
		super.onDisable();
		
		setPlayerEnabled(false);
	}
	
	@Override
	public void stopListeningForInput()
	{
		super.stopListeningForInput();
		playerControl.stopListeningForInput();
	}

	public void setPlayerEnabled(boolean playerEnabled)
	{
		playerControl.setEnabled(playerEnabled);
		inputManager.setCursorVisible(!playerEnabled);
		safeSetEnableAppState(stateManager.getState(FlyCamAppState.class), playerEnabled);
	}

	@Override
	public void update(float tpf)
	{
		super.update(tpf);
		
		AABB.getAABBPool().cleanPool();
		world.update(tpf);
	}

	private void toggleDebug()
	{
		debugEnabled = !debugEnabled;
		DebugAppState debugAppState = stateManager.getState(DebugAppState.class);
		if(debugAppState != null)
		{
			debugAppState.setEnabled(debugEnabled);
		}
	}

	private void initInputMappings()
	{
		addMapping("f1", new KeyTrigger(KeyInput.KEY_F1));
		addMapping("f3", new KeyTrigger(KeyInput.KEY_F3));
		addMapping("f5", new KeyTrigger(KeyInput.KEY_F5));
		addMapping("ToggleAmbientOcclusion", new KeyTrigger(KeyInput.KEY_F9));
		addMapping("RebuildChunks", new KeyTrigger(KeyInput.KEY_F10));
		
		addMapping("e", new KeyTrigger(KeyInput.KEY_E));
		addMapping("save", new KeyTrigger(KeyInput.KEY_F7));
		addMapping("guitoggle", new KeyTrigger(KeyInput.KEY_F8));
		addMapping("commandline", new KeyTrigger(KeyInput.KEY_SLASH));
		
		inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
		addMapping("pause", new KeyTrigger(KeyInput.KEY_ESCAPE));
	}
	
	@Override
	public void onAction(String name, boolean isPressed, float lastTimePerFrame)
	{
		if("f3".equals(name) && !isPressed)
		{
			toggleDebug();
		}
		else if("f5".equals(name) && !isPressed)
		{
			toggleWireframe();
		}
		else if("RebuildChunks".equals(name) && !isPressed)
		{
			world.getChunkManager().rebuildChunks();
		}
		else if("ToggleAmbientOcclusion".equals(name) && !isPressed)
		{
			GameSettings.ambientOcclusionEnabled = !GameSettings.ambientOcclusionEnabled;
			world.getChunkManager().rebuildChunks();
		}
		else if("pause".equals(name) && !isPressed)
		{
			pause();
		}
		else if("commandline".equals(name) && !isPressed)
		{
			showCommandLine();
		}
	}

	private void showCommandLine()
	{
		enableAppState(commandLineAppState);
		playerControl.setEnabled(false);
	}

	private void pause()
	{
		GameRunningAppState.this.setEnabled(false);
		enableAppState(pausedAppState);
	}
	
	private void toggleWireframe()
	{
		wireframe = !wireframe;
		rootNode.depthFirstTraversal(new SceneGraphVisitor()
		{
			public void visit(Spatial spatial)
			{
				if(spatial instanceof Geometry)
				{
					Geometry g = (Geometry)spatial;
					if(!"blockCursor".equals(g.getName()))//Don't toggle the block cursor
					{
						g.getMaterial().getAdditionalRenderState().setWireframe(wireframe);
					}
				}
			}
		});
	}
}