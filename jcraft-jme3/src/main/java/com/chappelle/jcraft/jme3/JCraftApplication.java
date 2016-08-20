package com.chappelle.jcraft.jme3;

import java.util.logging.*;

import com.chappelle.jcraft.GameSettings;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;

public class JCraftApplication extends SimpleApplication
{
	private static final Level LOG_LEVEL = Level.INFO;

	private static JCraftApplication jcraft;

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
		
		stateManager.attach(new BeginningAppState());
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