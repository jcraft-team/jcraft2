package com.chappelle.jcraft.jme3;

import java.util.logging.*;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.util.AABB;
import com.chappelle.jcraft.world.World;
import com.jme3.app.*;
import com.jme3.font.BitmapFont;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.*;
import com.jme3.math.*;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.*;
import com.jme3.scene.*;
import com.jme3.system.AppSettings;

import de.lessvoid.nifty.Nifty;

public class JCraft extends SimpleApplication implements ActionListener
{
	private static JCraft jcraft;
	private Nifty nifty;
	private NiftyJmeDisplay niftyDisplay;
	public boolean debugEnabled = false;
	private CubesSettings cubesSettings;
	private InventoryAppState inventoryAppState;
	private EntityPlayer player;
	public World world;
	private HUDControl hud;
	private boolean wireframe;

	public JCraft()
	{
		jcraft = this;

		debugEnabled = GameSettings.debugEnabled;
		settings = new AppSettings(true);
		settings.setWidth(GameSettings.screenWidth);
		settings.setHeight(GameSettings.screenHeight);
		settings.setTitle("JCraft");
		settings.setFrameRate(GameSettings.frameRate);
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

	public static JCraft getInstance()
	{
		return jcraft;
	}

	@Override
	public void simpleInitApp()
	{
		initializeGUI();
		if(GameSettings.skyEnabled)
		{
			stateManager.attach(new EnvironmentAppState());
		}
		initControls();
		initBlockTerrain();
		cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 500f);
		cam.lookAtDirection(new Vector3f(1, 0, 1), Vector3f.UNIT_Y);

		// Setup sky
		viewPort.setBackgroundColor(new ColorRGBA((float) 128 / 255, (float) 173 / 255, (float) 254 / 255, 1));

		// Setup player
		player = new EntityPlayer(world, cam);
		hud = new HUDControl(this, settings, player);
		player.preparePlayerToSpawn();
		rootNode.addControl(new PlayerControl(this, player));

		rootNode.addControl(hud);
		rootNode.addControl(new BlockCursorControl(world, player, assetManager));

		updateStatsView();

		System.out.println("****************************************************************************");
		System.out.println("Press F1 for fullscreen, F3 to toggle debug, F4 to toggle profiler.");
		System.out.println("See key bindings in JCraft class for other controls");
		System.out.println("****************************************************************************");
		System.out.println("\r\n\r\n");

		this.inventoryAppState = new InventoryAppState();
		stateManager.attach(inventoryAppState);
		nifty.fromXml("Interface/hud.xml", "hud", hud);
		
//		world.getNearbyChunks(5);//TODO:
	}

	private void initializeGUI()
	{
		Camera niftyCamera = new Camera(cam.getWidth(), cam.getHeight());
		// Nifty gets it's own view port so we can still write to gui node		
		ViewPort niftyViewPort = renderManager.createPostView("Nifty View", niftyCamera);
		niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, niftyViewPort);
		nifty = niftyDisplay.getNifty();

		niftyViewPort.addProcessor(niftyDisplay);
		nifty.addXml("Interface/hud.xml");
		nifty.addXml("Interface/inventory.xml");
	}

	private void toggleDebug()
	{
		debugEnabled = !debugEnabled;
		updateStatsView();
	}

	private void updateStatsView()
	{
		stateManager.getState(StatsAppState.class).setDisplayStatView(debugEnabled);
		stateManager.getState(StatsAppState.class).setDisplayFps(debugEnabled);
	}

	private void initBlockTerrain()
	{
		cubesSettings = new CubesSettings(this);
		cubesSettings.setDefaultBlockMaterial("Textures/FaithfulBlocks.png");

		long seed = getSeed();
		System.out.println("Using world seed: " + seed);
		world = new World(this, cubesSettings, assetManager, cam, seed);
		world.setTimeOfDayProvider(stateManager.getState(EnvironmentAppState.class));
		world.addToScene(rootNode);
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
		addMapping("e", new KeyTrigger(KeyInput.KEY_E));
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
		else if("e".equals(name) && !isPressed)
		{
			showInventory();
		}
		else if("RebuildChunks".equals(name) && !isPressed)
		{
			world.rebuildChunks();
		}
		else if("ToggleAmbientOcclusion".equals(name) && !isPressed)
		{
			GameSettings.ambientOcclusionEnabled = !GameSettings.ambientOcclusionEnabled;
		}
	}

	private void showInventory()
	{
		nifty.fromXml("Interface/inventory.xml", "inventoryScreen", inventoryAppState);
		inputManager.setCursorVisible(true);
		flyCam.setEnabled(false);
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

	public HUDControl getHUD()
	{
		return hud;
	}

	public void setGuiFont(BitmapFont guiFont)
	{
		this.guiFont = guiFont;
	}

	@Override
	public void destroy()
	{
		super.destroy();
		world.destroy();
		GameSettings.save();
	}

	// WARNING: This may be buggy. See
	// http://hub.jmonkeyengine.org/t/error-switching-to-fullscreen-using-the-documented-code-sample/32750
	public void toggleToFullscreen()
	{
		java.awt.GraphicsDevice device = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
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

		GameSettings.load();

		JCraft app = new JCraft();
		app.setShowSettings(GameSettings.showSettings);
		app.start();
	}
}
