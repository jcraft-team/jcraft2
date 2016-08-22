package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.EntityPlayer;
import com.jme3.app.SimpleApplication;
import com.jme3.renderer.*;
import com.jme3.scene.*;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

public class CrosshairsControl extends AbstractControl
{
	private Node debugNode;
	private Node guiNode;
	private AppSettings settings;
	private float screenWidth;
	private float screenHeight;
	private Picture plus;
	
	private static final int WIDTH = 30;
	private static final int HEIGHT = 30;
	
	public CrosshairsControl(SimpleApplication app, AppSettings appSettings, EntityPlayer player)
	{
		this.debugNode = new Node("crosshairs");
		this.guiNode = app.getGuiNode();
		this.settings = appSettings;
		plus = new Picture("icons");
		plus.setWidth(WIDTH);
		plus.setHeight(HEIGHT);
		plus.setImage(app.getAssetManager(), "Textures/gui/crosshairs.png", true);
		
		screenWidth = settings.getWidth();
		screenHeight = settings.getHeight();
	}
	
	@Override
	public void setSpatial(Spatial spatial) 
	{
		if(spatial == null)
		{
			guiNode.detachChild(plus);
			guiNode.detachChild(debugNode);
		}
		else if (spatial instanceof Node)
        {
            guiNode.attachChild(debugNode);//TODO: Investigate what this node is for
            guiNode.attachChild(plus);
            positionElements();
        }	
	}

	public void positionElements()
	{
        float x = screenWidth / 2 - (WIDTH/2);
        float y = screenHeight / 2 - (HEIGHT/2);
        plus.setLocalTranslation(x, y, 0);
	}
	
	@Override
	protected void controlUpdate(float tpf)
	{
		if(settings.getWidth() != screenWidth || settings.getHeight() != screenHeight)//May have toggled fullscreen mode
		{
			screenWidth = settings.getWidth();
			screenHeight = settings.getHeight();

			positionElements();
		}
	}
	
	@Override
	protected void controlRender(RenderManager rm, ViewPort vp)
	{
	}
}