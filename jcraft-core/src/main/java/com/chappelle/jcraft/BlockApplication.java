package com.chappelle.jcraft;

import java.util.*;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.chappelle.jcraft.debug.*;
import com.chappelle.jcraft.serialization.*;
import com.chappelle.jcraft.util.AABB;
import com.chappelle.jcraft.world.World;
import com.jme3.app.*;
import com.jme3.font.BitmapFont;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.*;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.system.AppSettings;

/**
 * Base class for all block applications. Initializes the block terrain, world, player and the camera. Provides
 * basic debug features like toggleDebug and toggleWireframe.
 */
public class BlockApplication extends SimpleApplication implements ActionListener
{
	private final static Logger log = Logger.getLogger(BlockApplication.class.getName()); 
	
	private static BlockApplication jcraft;
	private CubesSettings cubesSettings;
	protected EntityPlayer player;
	public World world;
	public boolean debugEnabled = false;
	private boolean wireframe;
	private List<WorldInitializer> worldInitializers = new ArrayList<>();
	private List<GameInitializer> gameInitializers = new ArrayList<>();
	public WorldPersistor worldPersistor;
	
	public BlockApplication()
	{
		jcraft = this;

		addInitializers(worldInitializers, WorldInitializer.class);
		addInitializers(gameInitializers, GameInitializer.class);
		
		debugEnabled = GameSettings.debugEnabled;
		settings = new AppSettings(true);
		settings.setWidth(GameSettings.screenWidth);
		settings.setHeight(GameSettings.screenHeight);
		settings.setTitle("JCraft");
		settings.setFrameRate(GameSettings.frameRate);
//		settings.setFullscreen(true);
	}
	
	@Override
	public void simpleInitApp()
	{
		worldPersistor = new SimpleWorldPersistor();
		initControls();
		initBlockTerrain();
		cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 500f);
		cam.lookAtDirection(new Vector3f(1, 0, 1), Vector3f.UNIT_Y);

		// Setup sky
		viewPort.setBackgroundColor(new ColorRGBA((float) 128 / 255, (float) 173 / 255, (float) 254 / 255, 1));

		// Setup player
		player = new EntityPlayer(world, cam);
		world.getChunkManager().loadChunksAroundPlayer(player.posX, player.posZ, 3);
		world.update(0);
		player.preparePlayerToSpawn();
		

		//AppStates and Controls
		stateManager.attach(new DebugAppState());
		rootNode.addControl(new PlayerControl(this, player));
		
		log.info("****************************************************************************");
		log.info("Press F1 for fullscreen, F3 to toggle debug, F4 to toggle profiler.");
		log.info("See key bindings in JCraft class for other controls");
		log.info("****************************************************************************");
		log.info("\r\n\r\n");

		initializeGame();
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
		cubesSettings = new CubesSettings(this);
		cubesSettings.setDefaultBlockMaterial("Textures/FaithfulBlocks.png");

		long seed = getSeed();
		log.info("Using world seed: " + seed);
		String worldToLoad = System.getProperty("world");
		if(StringUtils.isNotBlank(worldToLoad))
		{
			log.info("Loading world " + worldToLoad);
//			world = worldPersistor.loadWorld(this, worldToLoad);
//			if(world == null)
//			{
//				log.warning("No world with name " + worldToLoad + " was found");
//			}
//			else
//			{
//				log.info("Finished loading world " + worldToLoad);
//			}
		}
		if(world == null)
		{
			log.info("Creating new world...get ready!");
			world = new World(this, cubesSettings, assetManager, cam, "JCraftWorld", seed);
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

	private void initializeGame()
	{
		for(GameInitializer gi : gameInitializers)
		{
			gi.initialize(this);
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

	@Override
	public void simpleUpdate(float tpf)
	{
		AABB.getAABBPool().cleanPool();
		world.update(tpf);
	}

	private void initControls()
	{
		addMapping("f1", new KeyTrigger(KeyInput.KEY_F1));
		addMapping("f3", new KeyTrigger(KeyInput.KEY_F3));
		addMapping("f5", new KeyTrigger(KeyInput.KEY_F5));
		addMapping("ToggleAmbientOcclusion", new KeyTrigger(KeyInput.KEY_F9));
		addMapping("RebuildChunks", new KeyTrigger(KeyInput.KEY_F10));
		addMapping("save", new KeyTrigger(KeyInput.KEY_F7));
	}

	private void addMapping(String action, Trigger trigger)
	{
		inputManager.addMapping(action, trigger);
		inputManager.addListener(this, action);
	}

	@Override
	public void onAction(String name, boolean isPressed, float lastTimePerFrame)
	{
		if("f1".equals(name) && !isPressed)
		{
			toggleToFullscreen();
		}
		else if("f3".equals(name) && !isPressed)
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
		}
		else if("save".equals(name) && !isPressed)
		{
			worldPersistor.save(world);
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

	@Override
	public void destroy()
	{
		super.destroy();
		world.destroy();
		GameSettings.save();
	}

	public void toggleToFullscreen()
	{
		settings.setFullscreen(!settings.isFullscreen());
		restart(); // restart the context to apply changes
	}

	public AppSettings getAppSettings()
	{
		return settings;
	}

	public EntityPlayer getPlayer()
	{
		return player;
	}

	public DebugDataProvider getDebugDataProvider()
	{
		return new DebugDataProvider(this);
	}
	
	public static BlockApplication getInstance()
	{
		return jcraft;
	}

	public void setGuiFont(BitmapFont guiFont)
	{
		this.guiFont = guiFont;
	}
	
	public BitmapFont getGuiFont()
	{
		return this.guiFont;
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