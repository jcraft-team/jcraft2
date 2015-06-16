package com.chappelle.jcraft.jme3;

import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chappelle.jcraft.CubesSettings;
import com.chappelle.jcraft.EntityPlayer;
import com.chappelle.jcraft.GameSettings;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.profiler.Profiler;
import com.chappelle.jcraft.profiler.ProfilerResult;
import com.chappelle.jcraft.util.AABB;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.ChunkProvider;
import com.chappelle.jcraft.world.chunk.SimpleChunkProvider;
import com.chappelle.jcraft.world.chunk.gen.*;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.font.BitmapFont;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import de.lessvoid.nifty.Nifty;

public class JCraft extends SimpleApplication implements ActionListener
{
	private static final int JUMP_TIME_INTERVAL = 200;
	private final Vector3Int terrainSize = new Vector3Int(16, 10, 16);
	private int terrainIndex = terrainSize.x/16;

	private static JCraft jcraft;
	private Nifty nifty;
	private NiftyJmeDisplay niftyDisplay;
	public boolean debugEnabled = false;
	private GameSettings gameSettings;
	private CubesSettings cubesSettings;
	private BlockTerrainControl blockTerrain;
	private Node terrainNode = new Node("terrain");
	private InventoryAppState inventoryAppState;
	private EntityPlayer player;
	public World world;
	private Profiler profiler;
	private HUDControl hud;
	public ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
	
	/**
	 * Used for enabling flying by double pressing space
	 */
	private long lastJumpPressed;
	
	public JCraft(GameSettings gameSettings)
	{
		jcraft = this;
		
		this.gameSettings = gameSettings;
		debugEnabled = gameSettings.debugEnabled;
		this.profiler = new Profiler();
		profiler.profilingEnabled = gameSettings.profilingEnabled;
		settings = new AppSettings(true);
		settings.setWidth(gameSettings.screenWidth);
		settings.setHeight(gameSettings.screenHeight);
		settings.setTitle("JCraft");
		settings.setFrameRate(gameSettings.frameRate);
	}

	public AppSettings getAppSettings()
	{
		return settings;
	}
	
	public Nifty getNifty()
	{
		return nifty;
	}
	
	public EntityPlayer getPlayer()
	{
		return player;
	}
	
	public Profiler getProfiler()
	{
		return profiler;
	}
	
	public static JCraft getInstance()
	{
		return jcraft;
	}
	
	@Override
	public void simpleInitApp()
	{
		initializeGUI();

		cam.setFrustumPerspective(45f, (float)cam.getWidth()/cam.getHeight(), 0.01f, 500f);
		initControls();
		initBlockTerrain();
		cam.lookAtDirection(new Vector3f(1, 0, 1), Vector3f.UNIT_Y);
		
		//Setup sky
		viewPort.setBackgroundColor(new ColorRGBA((float)128/255, (float)173/255, (float)254/255, 1));

		//Setup player
		player = new EntityPlayer(world, cam);
		hud = makeHUD2(player);
		player.preparePlayerToSpawn();
		player.moveEntity(1000, 0, 1000);
		world.generateNearbyChunks();
		rootNode.addControl(new PlayerControl(this, player));

		rootNode.addControl(hud);
		rootNode.addControl(new BlockCursorControl(world, player, assetManager));

		profiler.startSection("root");
		updateStatsView();
		
		System.out.println("****************************************************************************");
		System.out.println("Press F1 for fullscreen, F3 to toggle debug, F4 to toggle profiler.");
		System.out.println("See key bindings in JCraft class for other controls");
		System.out.println("****************************************************************************");
		System.out.println("\r\n\r\n");
		
		
		this.inventoryAppState = new InventoryAppState();
		stateManager.attach(inventoryAppState);
		nifty.fromXml("Interface/hud.xml", "hud", hud);
	}

	private void initializeGUI()
	{
		Camera niftyCamera = new Camera(cam.getWidth(), cam.getHeight());
		ViewPort niftyViewPort = renderManager.createPostView("Nifty View", niftyCamera);//Nifty gets it's own view port so we can still write to gui node
		niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, niftyViewPort);
		nifty = niftyDisplay.getNifty();

	    niftyViewPort.addProcessor(niftyDisplay);
        nifty.addXml("Interface/hud.xml");
        nifty.addXml("Interface/inventory.xml");
	}

	private HUDControl makeHUD2(EntityPlayer player)
	{
		return new HUDControl(this, settings, player);
	}
	
	private void toggleDebug()
	{
		debugEnabled = !debugEnabled;
		updateStatsView();
	}
	
	private void toggleProfiling()
	{
		profiler.profilingEnabled = !profiler.profilingEnabled;
	}
	
	private void updateStatsView()
	{
		stateManager.getState(StatsAppState.class).setDisplayStatView(debugEnabled);
		stateManager.getState(StatsAppState.class).setDisplayFps(debugEnabled);
	}

	private void addMapping(String action, Trigger trigger)
	{
		inputManager.addMapping(action, trigger);
		inputManager.addListener(this, action);
	}
	
	private void initControls()
	{
		addMapping("move_left", new KeyTrigger(KeyInput.KEY_A));
		addMapping("move_right", new KeyTrigger(KeyInput.KEY_D));
		addMapping("move_up", new KeyTrigger(KeyInput.KEY_W));
		addMapping("move_down", new KeyTrigger(KeyInput.KEY_S));
		addMapping("jump", new KeyTrigger(KeyInput.KEY_SPACE));
		addMapping("n", new KeyTrigger(KeyInput.KEY_N));
		addMapping("n", new KeyTrigger(KeyInput.KEY_N));
		addMapping("RightClick", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		addMapping("LeftClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        addMapping("1", new KeyTrigger(KeyInput.KEY_1));
        addMapping("2", new KeyTrigger(KeyInput.KEY_2));
        addMapping("3", new KeyTrigger(KeyInput.KEY_3));
        addMapping("4", new KeyTrigger(KeyInput.KEY_4));
        addMapping("5", new KeyTrigger(KeyInput.KEY_5));
        addMapping("6", new KeyTrigger(KeyInput.KEY_6));
        addMapping("7", new KeyTrigger(KeyInput.KEY_7));
        addMapping("8", new KeyTrigger(KeyInput.KEY_8));
        addMapping("9", new KeyTrigger(KeyInput.KEY_9));
        addMapping("0", new KeyTrigger(KeyInput.KEY_0));
        addMapping("t", new KeyTrigger(KeyInput.KEY_T));
        addMapping("g", new KeyTrigger(KeyInput.KEY_G));
        addMapping("u", new KeyTrigger(KeyInput.KEY_U));
        addMapping("lshift", new KeyTrigger(KeyInput.KEY_LSHIFT));
        addMapping("lctrl", new KeyTrigger(KeyInput.KEY_LCONTROL));
        addMapping("f1", new KeyTrigger(KeyInput.KEY_F1));
        addMapping("f3", new KeyTrigger(KeyInput.KEY_F3));
        addMapping("f4", new KeyTrigger(KeyInput.KEY_F4));
        addMapping("e", new KeyTrigger(KeyInput.KEY_E));
	}

	private void initBlockTerrain()
	{
		cubesSettings = new CubesSettings(this);
		cubesSettings.setDefaultBlockMaterial("Textures/FaithfulBlocks.png");

		
		long seed = System.currentTimeMillis();
		String seedStr = System.getProperty("seed");
		if(seedStr != null)
		{
			seed = Long.parseLong(seedStr);
		}
		System.out.println("Using world seed: " + seed);
		world = new World(makeChunkProvider(seed), profiler, cubesSettings, assetManager, cam, seed);
		blockTerrain = new BlockTerrainControl(this, cubesSettings, world);
		terrainNode.addControl(blockTerrain);
		terrainNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
		rootNode.attachChild(terrainNode);
	}

	private ChunkProvider makeChunkProvider(long seed)
	{
		int height = 230;
		
		SimpleChunkProvider chunkProvider = new SimpleChunkProvider();
		chunkProvider.addFeature(new FlatFeature(Block.grass, height));
		chunkProvider.addFeature(new NoiseFeature(seed, 0.0001f, height));
		chunkProvider.addFeature(new BlockOreFeature(seed, height));
		chunkProvider.addFeature(new TreeFeature(seed));
		chunkProvider.addFeature(new PlantFeature(seed));
//		chunkProvider.addFeatureGenerator(new WaterFeatureGenerator(seed));
		return chunkProvider;
	}
	@Override
	public void simpleUpdate(float tpf)
	{
		AABB.getAABBPool().cleanPool();
	}


	
	@Override
	public void onAction(String name, boolean isPressed, float lastTimePerFrame)
	{
		if(name.equals("move_up"))
		{
			player.moveForward(isPressed);
		}
		else if(name.equals("move_right"))
		{
			player.moveRight(isPressed);
		}
		else if(name.equals("move_left"))
		{
			player.moveLeft(isPressed);
		}
		else if(name.equals("move_down"))
		{
			player.moveBackward(isPressed);
		}
		else if(name.equals("jump"))
		{
			long currentTime = System.currentTimeMillis();
			long timeSinceLastPressed = currentTime - lastJumpPressed;
			if(!isPressed && timeSinceLastPressed > 0 && timeSinceLastPressed < JUMP_TIME_INTERVAL)
			{
				player.toggleFlying();
			}
			else
			{
				player.jump();
			}
			player.moveUp(isPressed);
			if(!isPressed)
			{
				lastJumpPressed = System.currentTimeMillis();
			}
		}
		else if(name.equals("lshift"))
		{
			player.moveDown(isPressed);
		}
		else if(name.equals("lctrl"))
		{
			player.setFastFlying(isPressed);
		}
		else if(name.equals("RightClick") && !isPressed)
		{
			player.placeBlock();
		}
		else if(name.equals("LeftClick") && !isPressed)
		{
			player.breakBlock();
		}
        else if("1".equals(name) && !isPressed)
        {
        	player.selectBlock(1);
        }
        else if("2".equals(name) && !isPressed)
        {
        	player.selectBlock(2);
        }
        else if("3".equals(name) && !isPressed)
        {
        	player.selectBlock(3);
        }
        else if("4".equals(name) && !isPressed)
        {
        	player.selectBlock(4);
        }
        else if("5".equals(name) && !isPressed)
        {
        	player.selectBlock(5);
        }
        else if("6".equals(name) && !isPressed)
        {
        	player.selectBlock(6);
        }
        else if("7".equals(name) && !isPressed)
        {
        	player.selectBlock(7);
        }
        else if("8".equals(name) && !isPressed)
        {
        	player.selectBlock(8);
        }
        else if("9".equals(name) && !isPressed)
        {
        	player.selectBlock(9);
        }
        else if("t".equals(name) && !isPressed)
        {
        	blockTerrain.world.setBlocksFromNoise(new Vector3Int(terrainIndex*16, 0, 0), terrainSize, 0.8f, Block.grass);
        	terrainIndex++;
        }
        else if("f1".equals(name) && !isPressed)
        {
        	toggleToFullscreen();
        }
        else if("e".equals(name) && !isPressed)
        {
            nifty.fromXml("Interface/inventory.xml", "inventoryScreen", inventoryAppState);
            inputManager.setCursorVisible(true);
            
//            player.setEnabled(false);
            flyCam.setEnabled(false);

        }
        else if("g".equals(name) && !isPressed)
        {
        	player.toggleGravity();
        }
        else if("f3".equals(name) && !isPressed)
        {
        	toggleDebug();
        }
        else if("f4".equals(name) && !isPressed)
        {
        	toggleProfiling();
        }
	}
	
	public HUDControl getHUD()
	{
		return hud;
	}
	
    public BitmapFont getGuiFont()
    {
        return guiFont;
    }

    public void setGuiFont(BitmapFont guiFont)
    {
        this.guiFont = guiFont;
    }
    
    public CubesSettings getCubesSettings()
    {
    	return cubesSettings;
    }
    
	@Override
	public void destroy()
	{
		super.destroy();
		executor.shutdown();
		gameSettings.save();

		if(profiler.profilingEnabled)
		{
			printProfilingData();
		}
	}

	private void printProfilingData()
	{
		System.out.println("\r\n\r\n");
		System.out.println("**********************************************");
		System.out.println("************* Profiler results ***************");
		System.out.println("**********************************************");
		profiler.endSection();//needed to close the root profiling section
		for(ProfilerResult result : profiler.getProfilingData("root"))
		{
			System.out.println(result.section + " [" + result.elapsedTime + "," + result.maxTime + "]");
		}
	}

	//WARNING: This may be buggy. See http://hub.jmonkeyengine.org/t/error-switching-to-fullscreen-using-the-documented-code-sample/32750
	public void toggleToFullscreen()
	{
		java.awt.GraphicsDevice device = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		java.awt.DisplayMode[] modes = device.getDisplayModes();

		int i = modes.length - 1;
		settings.setResolution(modes[i].getWidth(), modes[i].getHeight());
		settings.setFrequency(modes[i].getRefreshRate());
		settings.setBitsPerPixel(modes[i].getBitDepth());
		settings.setFullscreen(device.isFullScreenSupported());
		setSettings(settings);
		restart(); // restart the context to apply changes
		
		hud.positionElements();
		
		StatsAppState stats = stateManager.getState(StatsAppState.class);
		stateManager.detach(stats);
		stats = new StatsAppState(guiNode, guiFont);
		stateManager.attach(stats);
	}
	
	public static void main(String[] args)
	{
		Logger.getLogger("").setLevel(Level.SEVERE);

		GameSettings gameSettings = new GameSettings(new File(System.getProperty("user.home")));
		gameSettings.load();

		JCraft app = new JCraft(gameSettings);
		app.setShowSettings(gameSettings.showSettings);
		app.start();
	}
}
