package com.chappelle.jcraft.jme3;

import java.io.File;
import java.util.*;
import java.util.logging.*;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.serialization.VoxelWorldSave;
import com.chappelle.jcraft.world.World;
import com.jme3.app.SimpleApplication;
import com.jme3.math.*;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;

public class JCraftApplication extends SimpleApplication
{
	private final static Logger log = Logger.getLogger(JCraftApplication.class.getName());
	private static final Level LOG_LEVEL = Level.INFO;

	private static JCraftApplication jcraft;

	public World world;
	protected EntityPlayer player;
	private CubesSettings cubesSettings;
	public VoxelWorldSave voxelWorldSave;
	
	//Plugin api
	private List<WorldInitializer> worldInitializers = new ArrayList<>();

	public JCraftApplication()
	{
		jcraft = this;
		
		settings = new AppSettings(true);
		settings.setWidth(GameSettings.screenWidth);
		settings.setHeight(GameSettings.screenHeight);
		settings.setTitle("JCraft");
		settings.setFrameRate(GameSettings.frameRate);
	}

	@Override
	public void simpleInitApp()
	{
		//Initialize Lemur GUI
		GuiGlobals.initialize(this);
		BaseStyles.loadGlassStyle();
		GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

		this.voxelWorldSave = new VoxelWorldSave(new File(GameFiles.getSaveDir(), "world.dat"));
		cubesSettings = new CubesSettings(assetManager, new ChunkMaterial(assetManager, "Textures/FaithfulBlocks.png"));
		long seed = getSeed();
		log.info("Using world seed: " + seed);
		log.info("Creating new world...get ready!");
		world = new World(this, cubesSettings, assetManager, cam, "JCraftWorld", seed, voxelWorldSave);
		player = new EntityPlayer(world, cam);
		
		addInitializers(worldInitializers, WorldInitializer.class);
		configureWorld(world);
		cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 500f);
		Vector3f lookAt = (Vector3f)voxelWorldSave.getGameData("playerLookDirection");
		if(lookAt == null)
		{
			lookAt = new Vector3f(1, 0, 1);
		}
		cam.lookAtDirection(lookAt, Vector3f.UNIT_Y);
		
		// Setup player
		Vector3f playerLocation = (Vector3f)voxelWorldSave.getGameData("playerLocation");
		if(playerLocation != null)
		{
			player.setPosition(playerLocation.x, playerLocation.y, playerLocation.z);
		}
		
		ProgressMonitor progressMonitor = new DefaultProgressMonitor();
		LoadingCallable loadingCallable = new LoadingCallable(world, player, progressMonitor);
		stateManager.attach(new BeginningAppState(new LoadingAppState(new GameRunningAppState(player, world, voxelWorldSave, settings), loadingCallable, progressMonitor)));
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

	public static JCraftApplication getInstance()
	{
		return jcraft;
	}

	public static void main(String[] args)
	{
		Logger.getLogger("").setLevel(LOG_LEVEL);

		GameSettings.load();

		JCraftApplication app = new JCraftApplication();
		app.setShowSettings(GameSettings.showSettings);
		app.start();
	}

	@Override
	public void destroy()
	{
		super.destroy();
		voxelWorldSave.closeDB();
	}
}
