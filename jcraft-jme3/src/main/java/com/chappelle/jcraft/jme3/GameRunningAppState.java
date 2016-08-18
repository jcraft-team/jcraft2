package com.chappelle.jcraft.jme3;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

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
import com.jme3.math.*;
import com.jme3.renderer.*;
import com.jme3.scene.*;
import com.jme3.system.AppSettings;

public class GameRunningAppState extends BaseAppState implements ActionListener
{
	private final static Logger log = Logger.getLogger(GameRunningAppState.class.getName()); 

	//General game stuff
	public World world;
	protected EntityPlayer player;
	private CubesSettings cubesSettings;
	
	//Plugin api
	private List<WorldInitializer> worldInitializers = new ArrayList<>();

	//Debug settings
	public boolean debugEnabled = false;
	private boolean wireframe;
	
	//World save objects
	public VoxelWorldSave voxelWorldSave;
	private WorldSaveTask worldSaveTask;
	private Timer worldSaveTimer = new Timer("WorldSave");
	private InputManager inputManager;
	private SimpleApplication application;
	private Camera cam;
	private ViewPort viewPort;
	private AppStateManager stateManager;
	private AssetManager assetManager;
	private Node rootNode;
	private AppSettings settings;
	private PausedAppState pausedAppState;
	
	public GameRunningAppState(AppSettings settings)
	{
		this.settings = settings;
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
		cam = app.getCamera();
		viewPort = app.getViewPort();
		stateManager = app.getStateManager();
		rootNode = application.getRootNode();
		assetManager = app.getAssetManager();
		debugEnabled = GameSettings.debugEnabled;
		pausedAppState = new PausedAppState();
		
		addInitializers(worldInitializers, WorldInitializer.class);
		
		initControls();
		this.voxelWorldSave = new VoxelWorldSave(new File(GameFiles.getSaveDir(), "world.dat"));
		initBlockTerrain();
		cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 500f);
		Vector3f lookAt = (Vector3f)voxelWorldSave.getGameData("playerLookDirection");
		if(lookAt == null)
		{
			lookAt = new Vector3f(1, 0, 1);
		}
		cam.lookAtDirection(lookAt, Vector3f.UNIT_Y);
		// Setup sky
		viewPort.setBackgroundColor(new ColorRGBA((float) 128 / 255, (float) 173 / 255, (float) 254 / 255, 1));

		// Setup player
		player = new EntityPlayer(world, cam);
		Vector3f playerLocation = (Vector3f)voxelWorldSave.getGameData("playerLocation");
		if(playerLocation != null)
		{
			player.setPosition(playerLocation.x, playerLocation.y, playerLocation.z);
		}
		world.getChunkManager().loadChunksAroundPlayer(player.posX, player.posZ, GameSettings.chunkRenderDistance);
		world.update(0);
		player.preparePlayerToSpawn();

		//AppStates and Controls
		stateManager.attach(new DebugAppState(settings, new DebugDataProvider(player, world)));
		rootNode.addControl(new PlayerControl(application, player));
		
		log.info("****************************************************************************");
		log.info("Press F1 for fullscreen, F3 to toggle debug, F4 to toggle profiler.");
		log.info("See key bindings in JCraft class for other controls");
		log.info("****************************************************************************");
		log.info("\r\n\r\n");

		worldSaveTask = new WorldSaveTask(world, voxelWorldSave);
		worldSaveTimer.scheduleAtFixedRate(worldSaveTask, 1*1000, GameSettings.worldSaveInterval);
		
		if(GameSettings.skyEnabled)
		{
			Float timeOfDay = (Float) voxelWorldSave.getGameData("timeOfDay");
			AdvancedSkyAppState sky = new AdvancedSkyAppState(timeOfDay == null ? 6 : timeOfDay);
			stateManager.attach(sky);
			world.setTimeOfDayProvider(sky);
		}
		initControls();

		rootNode.addControl(new CrosshairsControl(application, settings, player));
		rootNode.addControl(new HighlightSelectedBlockControl(world, player, assetManager));
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
		voxelWorldSave.closeDB();

		world.destroy();
		GameSettings.save();

		this.application = null;
		inputManager = null;
		cam = null;
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

	private void initBlockTerrain()
	{
		cubesSettings = new CubesSettings(assetManager, new ChunkMaterial(assetManager, "Textures/FaithfulBlocks.png"));

		long seed = getSeed();
		log.info("Using world seed: " + seed);
		String worldToLoad = System.getProperty("world");
		if(StringUtils.isNotBlank(worldToLoad))
		{
			log.info("Loading world " + worldToLoad);
			//TODO: Load specified world
		}
		if(world == null)
		{
			log.info("Creating new world...get ready!");
			world = new World(application, cubesSettings, assetManager, cam, "JCraftWorld", seed, voxelWorldSave);
		}

		configureWorld(world);

		world.addToScene(rootNode);
	}

	private void configureWorld(World world)
	{
		for(WorldInitializer gi : worldInitializers)
		{
			gi.configureWorld(world);
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
//		else if("guitoggle".equals(name) && !isPressed)
//		{
//			if(isGuiShowing)
//			{
//				application.getGuiNode().detachChild(myWindow);
//			}
//			else
//			{
//				stateManager.getState(OptionPanelState.class).showError("My error", new RuntimeException("Yep it's an error!"));
////				guiNode.attachChild(myWindow);
//			}
//			isGuiShowing = !isGuiShowing;
//			inputManager.setCursorVisible(isGuiShowing);
//		}
		
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
}