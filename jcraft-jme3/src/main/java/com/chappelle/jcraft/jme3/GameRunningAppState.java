package com.chappelle.jcraft.jme3;

import java.util.Timer;
import java.util.logging.Logger;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.debug.*;
import com.chappelle.jcraft.serialization.*;
import com.chappelle.jcraft.util.AABB;
import com.chappelle.jcraft.world.World;
import com.jme3.app.*;
import com.jme3.app.state.*;
import com.jme3.asset.AssetManager;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.*;
import com.jme3.scene.*;
import com.jme3.system.AppSettings;

public class GameRunningAppState extends BaseAppState implements ActionListener
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
	private SimpleApplication application;
	private ViewPort viewPort;
	private AppStateManager stateManager;
	private AssetManager assetManager;
	private Node rootNode;
	private AppSettings settings;
	private PausedAppState pausedAppState;
	
	public GameRunningAppState(EntityPlayer player, World world, VoxelWorldSave voxelWorldSave, AppSettings settings)
	{
		this.settings = settings;
		this.player = player;
		this.world = world;
		this.voxelWorldSave = voxelWorldSave;
	}
	
	@Override
	protected void initialize(Application app)
	{
		if(!(app instanceof SimpleApplication))
		{
			throw new IllegalArgumentException("app must extend SimpleApplication");
		}
		application = (SimpleApplication)app;
		inputManager = app.getInputManager();
		viewPort = app.getViewPort();
		stateManager = app.getStateManager();
		rootNode = application.getRootNode();
		assetManager = app.getAssetManager();
		debugEnabled = GameSettings.debugEnabled;
		pausedAppState = new PausedAppState();
		
		initControls();

		stateManager.attach(new DebugAppState(settings, new DebugDataProvider(player, world)));
		rootNode.addControl(new PlayerControl(application, player));
		
		log.info("****************************************************************************");
		log.info("Press F1 for fullscreen, F3 to toggle debug, F4 to toggle profiler.");
		log.info("See key bindings in JCraft class for other controls");
		log.info("****************************************************************************");
		log.info("\r\n\r\n");

		worldSaveTask = new WorldSaveTask(world, voxelWorldSave);
		worldSaveTimer = new Timer("WorldSave");
		worldSaveTimer.scheduleAtFixedRate(worldSaveTask, 1*1000, GameSettings.worldSaveInterval);
		
		if(GameSettings.skyEnabled)
		{
			Float timeOfDay = (Float) voxelWorldSave.getGameData("timeOfDay");
			AdvancedSkyAppState sky = new AdvancedSkyAppState(timeOfDay == null ? 6 : timeOfDay);
			stateManager.attach(sky);
			world.setTimeOfDayProvider(sky);
		}
		else
		{
			viewPort.setBackgroundColor(new ColorRGBA((float) 128 / 255, (float) 173 / 255, (float) 254 / 255, 1));
		}
		initControls();

		rootNode.addControl(new CrosshairsControl(application, settings, player));
		rootNode.addControl(new HighlightSelectedBlockControl(world, player, assetManager));
		
		world.addToScene(rootNode);
	}

	
	@Override
	protected void cleanup(Application app)
	{
		AdvancedSkyAppState sky = stateManager.getState(AdvancedSkyAppState.class);
		if(stateManager.hasState(sky))
		{
			stateManager.detach(sky);
		}
		viewPort.setBackgroundColor(ColorRGBA.Black);
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

		this.application = null;
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
		inputManager.setCursorVisible(false);
		stateManager.getState(FlyCamAppState.class).setEnabled(true);
	}

	@Override
	protected void onDisable()
	{
		inputManager.setCursorVisible(true);
		stateManager.getState(FlyCamAppState.class).setEnabled(false);
	}

	@Override
	public void update(float tpf)
	{
		AABB.getAABBPool().cleanPool();
		world.update(tpf);
	}

	private void addMapping(String action, Trigger trigger)
	{
		inputManager.addMapping(action, trigger);
		inputManager.addListener(this, action);
	}


	private void initControls()
	{
		addMapping("f1", new KeyTrigger(KeyInput.KEY_F1));
		addMapping("f3", new KeyTrigger(KeyInput.KEY_F3));
		addMapping("f5", new KeyTrigger(KeyInput.KEY_F5));
		addMapping("ToggleAmbientOcclusion", new KeyTrigger(KeyInput.KEY_F9));
		addMapping("RebuildChunks", new KeyTrigger(KeyInput.KEY_F10));
		
		addMapping("e", new KeyTrigger(KeyInput.KEY_E));
		addMapping("save", new KeyTrigger(KeyInput.KEY_F7));
		addMapping("guitoggle", new KeyTrigger(KeyInput.KEY_F8));
		
		inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
		addMapping("pause", new KeyTrigger(KeyInput.KEY_ESCAPE));
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
			world.rebuildChunks();
		}
		else if("ToggleAmbientOcclusion".equals(name) && !isPressed)
		{
			GameSettings.ambientOcclusionEnabled = !GameSettings.ambientOcclusionEnabled;
			world.rebuildChunks();
		}
		else if("pause".equals(name) && !isPressed)
		{
			GameRunningAppState.this.setEnabled(false);
			if(!stateManager.hasState(pausedAppState))
			{
				stateManager.attach(pausedAppState);
			}
			pausedAppState.setEnabled(true);
		}
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