package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.Chunk;
import com.chappelle.jcraft.CubesSettings;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.World;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.AppSettings;

public class HUDControl extends AbstractControl
{
	private Node node;
	private Node guiNode;
	private BitmapText crosshairs;
	private AssetManager assetManager;
	private JCraft app;
	private AppSettings settings;
	private BitmapFont guiFont;
	private BitmapText playerLocationLabel;
	private BitmapText blockLocationLabel;
	private BitmapText chunkLocationLabel;
	private BitmapText lightLevelLabel;
	private PlayerControl player;
	private World world;
	
	public HUDControl(JCraft app, AppSettings appSettings, PlayerControl player)
	{
		this.node = new Node();
		this.world = app.world;
		this.app = app;
		this.guiNode = app.getGuiNode();
		this.assetManager = app.getAssetManager();
		this.settings = appSettings;
		this.player = player;
	}
	
	@Override
	public void setSpatial(Spatial spatial) 
	{
        if (spatial instanceof Node)
        {
            Node parentNode = (Node) spatial;
            parentNode.attachChild(node);

            guiNode.detachAllChildren();
            
            guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
            app.setGuiFont(guiFont);
            crosshairs = new BitmapText(guiFont, false);
            crosshairs.setSize(guiFont.getCharSet().getRenderedSize() * 2);
            crosshairs.setText("+");
            float x = settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2;
            float y = settings.getHeight() / 2 + crosshairs.getLineHeight() / 2;
            crosshairs.setLocalTranslation(x, y, 0);
            app.getGuiNode().attachChild(crosshairs);

            playerLocationLabel = new BitmapText(guiFont, false);
            playerLocationLabel.setSize(guiFont.getCharSet().getRenderedSize());
            playerLocationLabel.setText("Player location: ");
            x = 10;
            y = settings.getHeight() - 100;
            playerLocationLabel.setLocalTranslation(x, y, 0);
            app.getGuiNode().attachChild(playerLocationLabel);

            blockLocationLabel = new BitmapText(guiFont, false);
            blockLocationLabel.setSize(guiFont.getCharSet().getRenderedSize());
            blockLocationLabel.setText("Block location: ");
            x = 10;
            y-= 25;
            blockLocationLabel.setLocalTranslation(x, y, 0);
            app.getGuiNode().attachChild(blockLocationLabel);

            chunkLocationLabel = new BitmapText(guiFont, false);
            chunkLocationLabel.setSize(guiFont.getCharSet().getRenderedSize());
            chunkLocationLabel.setText("Chunk location: ");
            x = 10;
            y-= 25;
            chunkLocationLabel.setLocalTranslation(x, y, 0);
            app.getGuiNode().attachChild(chunkLocationLabel);

            lightLevelLabel = new BitmapText(guiFont, false);
            lightLevelLabel.setSize(guiFont.getCharSet().getRenderedSize());
            lightLevelLabel.setText("Light Level: ");
            x = 10;
            y-= 25;
            lightLevelLabel.setLocalTranslation(x, y, 0);
            app.getGuiNode().attachChild(lightLevelLabel);
        }	
	}
	
	@Override
	protected void controlUpdate(float tpf)
	{
		playerLocationLabel.setText("Player location: " + player.getLocalTranslation());
		
		Vector3Int blockLoc = Vector3Int.fromVector3f(player.getLocalTranslation().divide(CubesSettings.getInstance().getBlockSize()));
		blockLocationLabel.setText("Block location: " + blockLoc);//TODO: replace with cubeSettings.getBlockSize()
		if(blockLoc != null)
		{
			lightLevelLabel.setText("Light Level: " + world.getLight(blockLoc.subtract(0, 1, 0)));
			Chunk chunk = world.getChunk(blockLoc);
			if(chunk != null)
			{
				chunkLocationLabel.setText("Chunk location: " + chunk.location);
			}
		}
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp)
	{
	}
}