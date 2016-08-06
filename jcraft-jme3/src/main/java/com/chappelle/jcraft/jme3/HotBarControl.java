package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.EntityPlayer;
import com.chappelle.jcraft.blocks.Sprite;
import com.chappelle.jcraft.inventory.*;
import com.jme3.renderer.*;
import com.jme3.scene.*;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.AppSettings;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.*;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.*;

public class HotBarControl extends AbstractControl implements ScreenController, InventoryListener
{
	private Node debugNode;
	private Node guiNode;
	private JCraftApplication app;
	private EntityPlayer player;
    private Nifty nifty;
    private PanelBuilder panelBuilder;
    private ControlBuilder slotBuilder;
    private ControlBuilder itemBuilder;
    private Screen screen;
    private boolean inventoryDirty = true;
	
	public HotBarControl(JCraftApplication app, AppSettings appSettings, EntityPlayer player)
	{
		this.debugNode = new Node("hud");
		this.app = app;
		this.guiNode = app.getGuiNode();
		this.player = player;
	}
	
	@Override
	public void setSpatial(Spatial spatial) 
	{
        if (spatial instanceof Node)
        {
            guiNode.attachChild(debugNode);
            
            this.nifty = this.app.getNifty();
            player.getInventory().addListener(this);
        }	
	}
	
	@Override
	protected void controlUpdate(float tpf)
	{
		populateInventoryItems();
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