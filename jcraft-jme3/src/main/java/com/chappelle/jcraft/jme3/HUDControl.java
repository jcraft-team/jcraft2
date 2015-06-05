package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.Chunk;
import com.chappelle.jcraft.EntityPlayer;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.World;
import com.chappelle.jcraft.util.RayTrace;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.AppSettings;

public class HUDControl extends AbstractControl
{
	private Node debugNode;
	private Node guiNode;
	private BitmapText crosshairs;
	private AssetManager assetManager;
	private JCraft app;
	private AppSettings settings;
	private BitmapFont guiFont;
	private BitmapText playerLocationLabel;
	private BitmapText blockLocationLabel;
	private BitmapText chunkLocationLabel;
	private BitmapText walkingOnLabel;
	private BitmapText selectedBlockLabel;
	private BitmapText lightLevelLabel;
	private BitmapText facingLabel;
	private BitmapText pointedBlockLabel;
	private BitmapText boundingBoxLabel;
	private BitmapText pointedBoundingBoxLabel;
	private EntityPlayer player;
	private World world;
	
	public HUDControl(JCraft app, AppSettings appSettings, EntityPlayer player)
	{
		this.debugNode = new Node("debug");
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
            guiNode.detachAllChildren();
            guiNode.attachChild(debugNode);
            
            guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
            app.setGuiFont(guiFont);
            crosshairs = new BitmapText(guiFont, false);
            crosshairs.setSize(guiFont.getCharSet().getRenderedSize() * 2);
            crosshairs.setText("+");
            float x = settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2;
            float y = settings.getHeight() / 2 + crosshairs.getLineHeight() / 2;
            crosshairs.setLocalTranslation(x, y, 0);
            guiNode.attachChild(crosshairs);

            playerLocationLabel = new BitmapText(guiFont, false);
            playerLocationLabel.setSize(guiFont.getCharSet().getRenderedSize());
            playerLocationLabel.setText("Player location: ");
            x = 10;
            y = settings.getHeight() - 10;
            playerLocationLabel.setLocalTranslation(x, y, 0);
            debugNode.attachChild(playerLocationLabel);

            boundingBoxLabel = new BitmapText(guiFont, false);
            boundingBoxLabel.setSize(guiFont.getCharSet().getRenderedSize());
            boundingBoxLabel.setText("Bounding Box: ");
            x = 10;
            y -= 25;
            boundingBoxLabel.setLocalTranslation(x, y, 0);
            debugNode.attachChild(boundingBoxLabel);
            
            pointedBoundingBoxLabel = new BitmapText(guiFont, false);
            pointedBoundingBoxLabel.setSize(guiFont.getCharSet().getRenderedSize());
            pointedBoundingBoxLabel.setText("Pointed Bounding Box: ");
            x = 10;
            y -= 25;
            pointedBoundingBoxLabel.setLocalTranslation(x, y, 0);
            debugNode.attachChild(pointedBoundingBoxLabel);
            
            blockLocationLabel = new BitmapText(guiFont, false);
            blockLocationLabel.setSize(guiFont.getCharSet().getRenderedSize());
            blockLocationLabel.setText("Block location: ");
            x = 10;
            y-= 25;
            blockLocationLabel.setLocalTranslation(x, y, 0);
            debugNode.attachChild(blockLocationLabel);

            chunkLocationLabel = new BitmapText(guiFont, false);
            chunkLocationLabel.setSize(guiFont.getCharSet().getRenderedSize());
            chunkLocationLabel.setText("Chunk location: ");
            x = 10;
            y-= 25;
            chunkLocationLabel.setLocalTranslation(x, y, 0);
            debugNode.attachChild(chunkLocationLabel);

            lightLevelLabel = new BitmapText(guiFont, false);
            lightLevelLabel.setSize(guiFont.getCharSet().getRenderedSize());
            lightLevelLabel.setText("Light Level: ");
            x = 10;
            y-= 25;
            lightLevelLabel.setLocalTranslation(x, y, 0);
            debugNode.attachChild(lightLevelLabel);

            walkingOnLabel = new BitmapText(guiFont, false);
            walkingOnLabel.setSize(guiFont.getCharSet().getRenderedSize());
            walkingOnLabel.setText("Walking On: ");
            x = 10;
            y-= 25;
            walkingOnLabel.setLocalTranslation(x, y, 0);
            debugNode.attachChild(walkingOnLabel);

            selectedBlockLabel = new BitmapText(guiFont, false);
            selectedBlockLabel.setSize(guiFont.getCharSet().getRenderedSize());
            selectedBlockLabel.setText("Selected Block: ");
            x = 10;
            y-= 25;
            selectedBlockLabel.setLocalTranslation(x, y, 0);
            debugNode.attachChild(selectedBlockLabel);

            pointedBlockLabel = new BitmapText(guiFont, false);
            pointedBlockLabel.setSize(guiFont.getCharSet().getRenderedSize());
            pointedBlockLabel.setText("Pointed Block: ");
            x = 10;
            y-= 25;
            pointedBlockLabel.setLocalTranslation(x, y, 0);
            debugNode.attachChild(pointedBlockLabel);
            
            facingLabel = new BitmapText(guiFont, false);
            facingLabel.setSize(guiFont.getCharSet().getRenderedSize());
            facingLabel.setText("Facing: ");
            x = 10;
            y-= 25;
            facingLabel .setLocalTranslation(x, y, 0);
            debugNode.attachChild(facingLabel);
        }	
	}
	
	@Override
	protected void controlUpdate(float tpf)
	{
		if(app.debugEnabled)
		{
			debugNode.setCullHint(CullHint.Never);
			playerLocationLabel.setText("Player location: [" + player.posX + "," + player.posY + ", " + player.posZ + "]");
			boundingBoxLabel.setText("Bounding Box: " + player.boundingBox);
			selectedBlockLabel.setText("Selected Block: " + toString(player.getSelectedBlock()));
			Vector3Int blockLoc = new Vector3Int((int)player.posX, (int)player.posY, (int)player.posZ);
			blockLocationLabel.setText("Block location: " + blockLoc);
			facingLabel.setText("Facing: " + player.cam.getDirection());
			if(blockLoc != null)
			{
				Vector3Int walkedOnBlockLocation = blockLoc.subtract(0, 2, 0);
				if(walkedOnBlockLocation != null)
				{
					lightLevelLabel.setText("Light Level: " + world.getLight(blockLoc));
					walkingOnLabel.setText("Walking On: " + toString(world.getBlock(walkedOnBlockLocation)));
				}
				Chunk chunk = world.getChunk(blockLoc);
				if(chunk != null)
				{
					chunkLocationLabel.setText("Chunk location: " + chunk.location);
				}
			}
			RayTrace rayTrace = player.pickBlock();
			if(rayTrace != null)
			{
				Block block = world.getBlock(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
				pointedBlockLabel.setText("Pointed Block: " + (block == null ? "Air" : block) + " at [" + rayTrace.blockX + ", " + rayTrace.blockY + ", " + rayTrace.blockZ + "]");
				if(block != null)
				{
					pointedBoundingBoxLabel.setText("Pointed Bounding Box: " + block.getCollisionBoundingBox(world, rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ));
				}
			}
			else
			{
				pointedBlockLabel.setText("Pointed Block: Air");
			}
		}
		else
		{
			debugNode.setCullHint(CullHint.Always);
		}
	}
	
	private String toString(Block block)
	{
		return block == null ? "Air" : block.getClass().getSimpleName();
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp)
	{
	}
}