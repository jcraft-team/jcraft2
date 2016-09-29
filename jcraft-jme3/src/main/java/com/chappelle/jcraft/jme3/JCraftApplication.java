package com.chappelle.jcraft.jme3;

import java.io.File;
import java.util.logging.*;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.serialization.*;
import com.chappelle.jcraft.util.*;
import com.chappelle.jcraft.world.chunk.ChunkMaterial;
import com.jme3.app.*;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;

/**
 * The main application to run. Here are some common VM args when running. Other configurations are in the GameSettings class.
-Dcom.sun.management.jmxremote.port=7091
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
-XX:+UnlockCommercialFeatures
-XX:+FlightRecorder
-Dseed=1470193765295
-DfeatureProvider=terasology:default
-Dworld=JCraftWorld
-Xms256m 
-Xmx8192m
 */
public class JCraftApplication extends SimpleApplication
{
	private static final Level LOG_LEVEL = Level.INFO;

	private static JCraftApplication jcraft;
	private Context rootContext;

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

		rootContext = new ContextImpl();
		rootContext.put(Application.class, this);
		rootContext.put(VoxelWorldSave.class, new VoxelWorldSaveImpl(new File(GameFiles.getSaveDir(), "world.dat")));
		ChunkMaterial chunkMaterial = new ChunkMaterial(assetManager, "Textures/FaithfulBlocks.png");
		rootContext.put(ChunkMaterial.class, chunkMaterial);
		rootContext.put(CubesSettings.class, new CubesSettings(assetManager, chunkMaterial));
		rootContext.put(AppSettings.class, settings);
		stateManager.attach(new BeginningAppState(rootContext));
	}
	
	public Context getRootContext()
	{
		return rootContext;
	}
	
	public AppSettings getSettings()
	{
		return settings;
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
}