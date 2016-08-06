package com.chappelle.jcraft.jme3;

import java.util.logging.*;

import com.chappelle.jcraft.*;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.*;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.*;

import de.lessvoid.nifty.Nifty;

public class JCraftApplication extends BlockApplication
{
	private static final Level LOG_LEVEL = Level.INFO;

	private static JCraftApplication jcraft;
	private Nifty nifty;
	private NiftyJmeDisplay niftyDisplay;
	private InventoryAppState inventoryAppState;
	private HUDControl hud;

	public JCraftApplication()
	{
		jcraft = this;
	}
	
	@Override
	public void simpleInitApp()
	{
		super.simpleInitApp();
		initializeGUI();
		if(GameSettings.skyEnabled)
		{
			stateManager.attach(new EnvironmentAppState());
		}
		initControls();

		hud = new HUDControl(this, settings, player);
		rootNode.addControl(hud);

		this.inventoryAppState = new InventoryAppState();
		stateManager.attach(inventoryAppState);
		nifty.fromXml("Interface/hud.xml", "hud", hud);

		world.setTimeOfDayProvider(stateManager.getState(EnvironmentAppState.class));
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

	private void initControls()
	{
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
		super.onAction(name, isPressed, lastTimePerFrame);
		
		if("e".equals(name) && !isPressed)
		{
			showInventory();
		}
	}

	private void showInventory()
	{
		nifty.fromXml("Interface/inventory.xml", "inventoryScreen", inventoryAppState);
		inputManager.setCursorVisible(true);
		flyCam.setEnabled(false);
	}

	// http://hub.jmonkeyengine.org/t/error-switching-to-fullscreen-using-the-documented-code-sample/32750
	public void toggleToFullscreen()
	{
		super.toggleToFullscreen();

		hud.positionElements();
	}

	public Nifty getNifty()
	{
		return nifty;
	}

	public EntityPlayer getPlayer()
	{
		return player;
	}

	public static JCraftApplication getInstance()
	{
		return jcraft;
	}

	public HUDControl getHUD()
	{
		return hud;
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
