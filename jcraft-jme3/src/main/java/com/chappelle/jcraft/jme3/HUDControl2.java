package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.EntityPlayer;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.blocks.Sprite;
import com.chappelle.jcraft.inventory.Inventory;
import com.chappelle.jcraft.inventory.InventoryListener;
import com.chappelle.jcraft.inventory.ItemStack;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
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
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class HUDControl2 extends NodeControl implements ScreenController, InventoryListener
{
    private Node node;
    private Node guiNode;
    private BitmapText crosshairs;
    private AssetManager assetManager;
    private JCraft app;
    private AppSettings settings;
    private EntityPlayer player;
    private boolean showDebug = true;
    private Nifty nifty;
    private PanelBuilder panelBuilder;
    private ControlBuilder slotBuilder;
    private ControlBuilder itemBuilder;
    private Screen screen;
    private boolean inventoryDirty = true;
    private float secondCounter;
    private float frameCounter;

    public HUDControl2(JCraft app, EntityPlayer player)
    {
        this.node = new Node();
        this.assetManager = app.getAssetManager();
        this.app = app;
        this.player = player;
        this.settings = app.getAppSettings();
        this.guiNode = app.getGuiNode();
    }

    @Override
    public void setNode(Node parentNode)
    {
        parentNode.attachChild(node);

        guiNode.detachAllChildren();

        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        app.setGuiFont(guiFont);

        crosshairs = new BitmapText(guiFont, false);
        crosshairs.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        crosshairs.setText("+");
        float x = settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2;
        float y = settings.getHeight() / 2 + crosshairs.getLineHeight() / 2;
        crosshairs.setLocalTranslation(x, y, 0);
        app.getGuiNode().attachChild(crosshairs);

        this.nifty = this.app.getNifty();
        player.getInventory().addListener(this);
    }

    private String toString(Vector3f vector)
    {
        return "[" + (int) vector.x + "," + (int) vector.y + "," + (int) vector.z + "]";
    }

    private void updateHUDElement(String id, String label, String value)
    {
        Element health = nifty.getCurrentScreen().findElementByName(id);
        health.getRenderer(TextRenderer.class).setText(label + ": " + value);
    }

    @Override
    protected void controlUpdate(float tpf)
    {
        if ("hud".equals(nifty.getCurrentScreen().getScreenId()))
        {
            if (showDebug)
            {
//                updateHUDElement("location", "Location", toString(player.getWorldLocation()) + " - " + player.getChunkLocation());
//                updateHUDElement("direction", "Direction", toString(this.app.getCamera().getDirection()));
//                updateHUDElement("chunks", "Chunks", Integer.toString(terrain.getChunkLocations().size()));
//
//                Vector3Int pointedLocation = player.getPointedBlockLocationInWorldSpace();
//                if (pointedLocation != null)
//                {
//                	Vector3Int lookingAt = pointedLocation.divide((int)GameProperties.CUBE_SIZE);
//                	updateHUDElement("light", "Light", Integer.toString(terrain.getLight(pointedLocation)));
//					updateHUDElement("pointed", "Looking at", lookingAt.toString());
//                }
//                else
//                {
//                	updateHUDElement("light", "Light", "");
//                    updateHUDElement("pointed", "Looking at", "");
//                }
            }

//            calculateFPS(tpf);
            populateInventoryItems();
        }
    }

	private void calculateFPS(float tpf)
	{
        secondCounter += app.getTimer().getTimePerFrame();
        frameCounter ++;
        if (secondCounter >= 1.0f) {
            int fps = (int) (frameCounter / secondCounter);
            updateHUDElement("fps", "FPS", Integer.toString(fps));
            secondCounter = 0.0f;
            frameCounter = 0;
        }          		
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

    public void toggleDebug()
    {
        showDebug = !showDebug;
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
