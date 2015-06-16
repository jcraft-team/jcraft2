package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.EntityPlayer;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.blocks.Sprite;
import com.chappelle.jcraft.inventory.Inventory;
import com.chappelle.jcraft.inventory.InventoryListener;
import com.chappelle.jcraft.inventory.ItemStack;
import com.chappelle.jcraft.util.RayTrace;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.Chunk;
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

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.Draggable;
import de.lessvoid.nifty.controls.Droppable;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class HUDControl extends AbstractControl implements ScreenController, InventoryListener
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
    private Nifty nifty;
    private PanelBuilder panelBuilder;
    private ControlBuilder slotBuilder;
    private ControlBuilder itemBuilder;
    private Screen screen;
    private boolean inventoryDirty = true;
	
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
	
	public void positionElements()
	{
        float x = settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2;
        float y = settings.getHeight() / 2 + crosshairs.getLineHeight() / 2;
        crosshairs.setLocalTranslation(x, y, 0);
        
        x = 10;
        y = settings.getHeight() - 10;
        playerLocationLabel.setLocalTranslation(x, y, 0);

        x = 10;
        y -= 25;
        boundingBoxLabel.setLocalTranslation(x, y, 0);

        x = 10;
        y -= 25;
        pointedBoundingBoxLabel.setLocalTranslation(x, y, 0);

        x = 10;
        y-= 25;
        blockLocationLabel.setLocalTranslation(x, y, 0);
        
        x = 10;
        y-= 25;
        chunkLocationLabel.setLocalTranslation(x, y, 0);
        
        x = 10;
        y-= 25;
        lightLevelLabel.setLocalTranslation(x, y, 0);
        
        x = 10;
        y-= 25;
        walkingOnLabel.setLocalTranslation(x, y, 0);
        
        x = 10;
        y-= 25;
        selectedBlockLabel.setLocalTranslation(x, y, 0);
        
        x = 10;
        y-= 25;
        pointedBlockLabel.setLocalTranslation(x, y, 0);
        
        x = 10;
        y-= 25;
        facingLabel.setLocalTranslation(x, y, 0);
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
            guiNode.attachChild(crosshairs);

            playerLocationLabel = new BitmapText(guiFont, false);
            playerLocationLabel.setSize(guiFont.getCharSet().getRenderedSize());
            playerLocationLabel.setText("Player location: ");
            debugNode.attachChild(playerLocationLabel);

            boundingBoxLabel = new BitmapText(guiFont, false);
            boundingBoxLabel.setSize(guiFont.getCharSet().getRenderedSize());
            boundingBoxLabel.setText("Bounding Box: ");
            debugNode.attachChild(boundingBoxLabel);
            
            pointedBoundingBoxLabel = new BitmapText(guiFont, false);
            pointedBoundingBoxLabel.setSize(guiFont.getCharSet().getRenderedSize());
            pointedBoundingBoxLabel.setText("Pointed Bounding Box: ");
            debugNode.attachChild(pointedBoundingBoxLabel);
            
            blockLocationLabel = new BitmapText(guiFont, false);
            blockLocationLabel.setSize(guiFont.getCharSet().getRenderedSize());
            blockLocationLabel.setText("Block location: ");
            debugNode.attachChild(blockLocationLabel);

            chunkLocationLabel = new BitmapText(guiFont, false);
            chunkLocationLabel.setSize(guiFont.getCharSet().getRenderedSize());
            chunkLocationLabel.setText("Chunk location: ");
            debugNode.attachChild(chunkLocationLabel);

            lightLevelLabel = new BitmapText(guiFont, false);
            lightLevelLabel.setSize(guiFont.getCharSet().getRenderedSize());
            lightLevelLabel.setText("Light Level: ");
            debugNode.attachChild(lightLevelLabel);

            walkingOnLabel = new BitmapText(guiFont, false);
            walkingOnLabel.setSize(guiFont.getCharSet().getRenderedSize());
            walkingOnLabel.setText("Walking On: ");
            debugNode.attachChild(walkingOnLabel);

            selectedBlockLabel = new BitmapText(guiFont, false);
            selectedBlockLabel.setSize(guiFont.getCharSet().getRenderedSize());
            selectedBlockLabel.setText("Selected Block: ");
            debugNode.attachChild(selectedBlockLabel);

            pointedBlockLabel = new BitmapText(guiFont, false);
            pointedBlockLabel.setSize(guiFont.getCharSet().getRenderedSize());
            pointedBlockLabel.setText("Pointed Block: ");
            debugNode.attachChild(pointedBlockLabel);
            
            facingLabel = new BitmapText(guiFont, false);
            facingLabel.setSize(guiFont.getCharSet().getRenderedSize());
            facingLabel.setText("Facing: ");
            debugNode.attachChild(facingLabel);
            
            positionElements();
            
            this.nifty = this.app.getNifty();
            player.getInventory().addListener(this);
        }	
	}
	
	@Override
	protected void controlUpdate(float tpf)
	{
		populateInventoryItems();
		if(app.debugEnabled)
		{
			debugNode.setCullHint(CullHint.Never);
			playerLocationLabel.setText("Player location: [" + player.posX + "," + player.posY + ", " + player.posZ + "]");
			boundingBoxLabel.setText("Bounding Box: " + player.boundingBox);
			selectedBlockLabel.setText("Selected Block: " + toString(player.getSelectedBlock()));
			Vector3Int blockLoc = new Vector3Int((int)player.posX, (int)player.posY, (int)player.posZ);
			blockLocationLabel.setText("Block location: " + blockLoc);
			facingLabel.setText("Facing: " + player.cam.getDirection());
			if(blockLoc != null && blockLoc.y < 256)
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

	private void populateInventoryItems()
    {
        if (inventoryDirty)
        {
            ItemStack[] items = player.getInventory().getItemStacks();
            for (int i = Inventory.INVENTORY_SIZE; i < items.length; i++)
            {
                Element itemSlot = screen.findElementByName("itemSlot" + i);
                if (itemSlot != null)
                {
                    Element e = itemSlot.findElementByName("item" + i);
                    ItemStack item = items[i];
                    if (item != null)
                    {
                        if(e == null)
                        {
                            itemBuilder = new ControlBuilder("item");
                            itemBuilder.id("item" + i);
                            itemBuilder.parameter("count", Integer.toString(item.getCount()));
                            e = itemBuilder.build(nifty, screen, itemSlot);
                        }
                        final Sprite sprite = item.getBlock().getSprite();
                        ImageBuilder imageBuilder = new ImageBuilder()
                        {

                            {
                                filename("Textures/FaithfulBlocks.png");
                                imageMode("subImage:" + sprite.getX() + "," + sprite.getY() + "," + sprite.getWidth() + "," + sprite.getHeight());
                                width("50");
                                height("50");

                            }
                        };
                        imageBuilder.build(nifty, screen, e);

                        TextBuilder textBuilder = new TextBuilder();
                        textBuilder.style("nifty-label");
                        textBuilder.font("Interface/Fonts/ArialBlack.fnt");
                        textBuilder.textVAlignBottom();
                        textBuilder.textHAlignRight();
                        textBuilder.text(Integer.toString(item.getCount()));
                        textBuilder.alignCenter();
                        textBuilder.build(nifty, screen, e);
                    }
                    else
                    {
                        Element blockItem = itemSlot.findElementByName("item" + i);
                        if(blockItem != null)
                        {
                            blockItem.setVisible(false);
                            blockItem.markForRemoval();
                        }

                    }
                }
            }
            Element slot = screen.findElementByName("itemSlot" + player.getInventory().getSelectedIndex());
            if(slot != null)
            {
                slot.startEffect(EffectEventId.onCustom);
            }
            inventoryDirty = false;
        }
    }

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp)
	{
	}

    public void bind(Nifty nifty, Screen screen)
    {
        this.nifty = nifty;
        this.screen = screen;
    }

    public void onStartScreen()
    {
        screen = nifty.getCurrentScreen();

        for (int col = Inventory.INVENTORY_SIZE; col < player.getInventory().getItemStacks().length; col++)
        {
            panelBuilder = new PanelBuilder("");
            panelBuilder.id("hudItemColumn" + col);
            panelBuilder.childLayoutVertical();
            Element column = panelBuilder.build(nifty, screen, screen.findElementByName("hudItemSlots"));
            slotBuilder = new ControlBuilder("itemSlot");
            slotBuilder.id("itemSlot" + col);
            slotBuilder.build(nifty, screen, column);
        }
        populateInventoryItems();
    }

    public void onEndScreen()
    {
        inventoryDirty = true;
    }

    public boolean accept(Droppable drpbl, Draggable drgbl, Droppable drpbl1)
    {
        return true;
    }

    public void onInventoryChanged()
    {
        inventoryDirty = true;
    }

    public void onSelectionChanged(int oldIndex, int newIndex)
    {
        if(oldIndex >= 0)
        {
            Element oldSlot = screen.findElementByName("itemSlot" + oldIndex);
            oldSlot.stopEffect(EffectEventId.onCustom);
        }
        Element slot = screen.findElementByName("itemSlot" + newIndex);
        slot.startEffect(EffectEventId.onCustom);
    }

}