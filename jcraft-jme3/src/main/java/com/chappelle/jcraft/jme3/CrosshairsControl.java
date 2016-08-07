package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.EntityPlayer;
import com.jme3.font.*;
import com.jme3.renderer.*;
import com.jme3.scene.*;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.AppSettings;

public class CrosshairsControl extends AbstractControl
{
	private Node debugNode;
	private Node guiNode;
	private BitmapText crosshairs;
	private AppSettings settings;
	private BitmapFont guiFont;
	private float width;
	private float height;
	
	public CrosshairsControl(JCraftApplication app, AppSettings appSettings, EntityPlayer player)
	{
		this.debugNode = new Node("crosshairs");
		this.guiNode = app.getGuiNode();
		this.settings = appSettings;
		this.guiFont = app.getGuiFont();
		width = settings.getWidth();
		height = settings.getHeight();
	}
	
	@Override
	public void setSpatial(Spatial spatial) 
	{
        if (spatial instanceof Node)
        {
            guiNode.attachChild(debugNode);
            
            crosshairs = new BitmapText(guiFont, false);
            crosshairs.setSize(guiFont.getCharSet().getRenderedSize() * 2);
            crosshairs.setText("+");
            guiNode.attachChild(crosshairs);

        }	
        positionElements();
	}

	public void positionElements()
	{
        float x = settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2;
        float y = settings.getHeight() / 2 + crosshairs.getLineHeight() / 2;
        crosshairs.setLocalTranslation(x, y, 0);
	}
	
	@Override
	protected void controlUpdate(float tpf)
	{
		if(settings.getWidth() != width || settings.getHeight() != height)//May have toggled fullscreen mode
		{
			width = settings.getWidth();
			height = settings.getHeight();

			positionElements();
		}
	}
	
	@Override
	protected void controlRender(RenderManager rm, ViewPort vp)
	{
	}
}