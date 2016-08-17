package com.chappelle.jcraft.jme3;

import java.util.logging.*;

import com.chappelle.jcraft.*;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.*;

public class JCraftApplication extends BlockApplication
{
	private static final Level LOG_LEVEL = Level.INFO;

	private static JCraftApplication jcraft;

	public JCraftApplication()
	{
		jcraft = this;
	}
	
	@Override
	public void simpleInitApp()
	{
		super.simpleInitApp();
		
		initializeNiftyGUI();
		if(GameSettings.skyEnabled)
		{
			Float timeOfDay = (Float)voxelWorldSave.getGameData("timeOfDay");
			AdvancedSkyAppState sky = new AdvancedSkyAppState(timeOfDay == null ? 6 : timeOfDay);
			stateManager.attach(sky);
			world.setTimeOfDayProvider(sky);
		}
		initControls();

//		rootNode.addControl(hud = new HotBarControl(this, settings, player));
		rootNode.addControl(new CrosshairsControl(this, settings, player));
		rootNode.addControl(new HighlightSelectedBlockControl(world, player, assetManager));
//		nifty.fromXml("Interface/hud.xml", "hud", hud);

//		this.inventoryAppState = new InventoryAppState();
//		stateManager.attach(inventoryAppState);

	}

	private void initializeNiftyGUI()
	{
//		Camera niftyCamera = new Camera(cam.getWidth(), cam.getHeight());
//		// Nifty gets it's own view port so we can still write to gui node		
//		ViewPort niftyViewPort = renderManager.createPostView("Nifty View", niftyCamera);
//		niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, niftyViewPort);
//		nifty = niftyDisplay.getNifty();
//
//		niftyViewPort.addProcessor(niftyDisplay);
//		nifty.addXml("Interface/hud.xml");
//		nifty.addXml("Interface/inventory.xml");
	}

	private void initControls()
	{
		addMapping("e", new KeyTrigger(KeyInput.KEY_E));
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
		super.onAction(name, isPressed, lastTimePerFrame);
		
		if("e".equals(name) && !isPressed)
		{
			showInventory();
		}
	}

	private void showInventory()
	{
//		nifty.fromXml("Interface/inventory.xml", "inventoryScreen", inventoryAppState);
//		inputManager.setCursorVisible(true);
//		flyCam.setEnabled(false);
	}

	public EntityPlayer getPlayer()
	{
		return player;
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
