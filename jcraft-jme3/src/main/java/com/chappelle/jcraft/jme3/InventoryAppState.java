package com.chappelle.jcraft.jme3;

import java.util.HashMap;
import java.util.Map;

import com.chappelle.jcraft.blocks.Sprite;
import com.chappelle.jcraft.inventory.Inventory;
import com.chappelle.jcraft.inventory.ItemStack;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.Draggable;
import de.lessvoid.nifty.controls.Droppable;
import de.lessvoid.nifty.controls.DroppableDropFilter;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class InventoryAppState extends AbstractAppState implements ScreenController, DroppableDropFilter
{
    private Inventory inventory;
    private PanelBuilder panelBuilder;
    private ControlBuilder slotBuilder;
    private ControlBuilder itemBuilder;
    private Nifty nifty;
    private Screen screen;
    private InputManager inputManager;
    private Map<String, ItemStackLocation> locations = new HashMap<String, ItemStackLocation>();
    
    public boolean accept(Droppable drpbl, Draggable drgbl, Droppable drpbl1)
    {
        
        String slotId = drpbl1.getId();
        String itemId = drgbl.getId();
        
        ItemStackLocation location = locations.get(itemId);
        if(location == null)
        {
            return false;
        }
        else
        {            
            int newItemSlot = Integer.parseInt(slotId.replace("itemSlot", ""));
            inventory.move(location.inventoryLocation, newItemSlot, location.itemStack); 
            location.inventoryLocation = newItemSlot;
            rebuildInventoryScreen();
            return true;
        }

    }

    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        
        inventory = JCraft.getInstance().getPlayer().getInventory();
        
        inputManager = app.getInputManager();
    }

    
    public void bind(Nifty nifty, Screen screen)
    {
        this.screen = screen;
        this.nifty = nifty;        
    }

    public void onStartScreen()
    {
        if("inventoryScreen".equals(screen.getScreenId()))
        {
            rebuildInventoryScreen();            
        }
    }

	private void rebuildInventoryScreen()
	{
		locations.clear();
		inputManager.addMapping("close", new KeyTrigger(KeyInput.KEY_F12));        
		inputManager.addListener(new InventoryActionListener(), "close");
		
		int index = 0;
		int numColumns = 9;
		int rowCount = 5;
		for(int col = 0; col < numColumns; col++)
		{           
			String panelId = "inventoryColumn" + col;
			if(screen.findElementByName(panelId) == null)
			{
				panelBuilder = new PanelBuilder("");
				
				panelBuilder.id(panelId);
				panelBuilder.childLayoutVertical();
				Element column = panelBuilder.build(nifty, screen, screen.findElementByName("inventorySlots"));
				for(int j = 0; j < rowCount; j++,index++)
				{
					slotBuilder = new ControlBuilder("itemSlot");
					
					slotBuilder.id("itemSlot" + index);
					Element e = slotBuilder.build(nifty, screen, column);
					
					e.findNiftyControl("itemSlot" + index, Droppable.class).addFilter(InventoryAppState.this);   
				}
				
				Element equippedColumn = panelBuilder.build(nifty, screen, screen.findElementByName("equippedSlots"));
				slotBuilder = new ControlBuilder("itemSlot");
				String equipmentSlotId = "itemSlot" + (col + Inventory.INVENTORY_SIZE);
				slotBuilder.id(equipmentSlotId);
				Element e = slotBuilder.build(nifty, screen, equippedColumn);
				e.findNiftyControl(equipmentSlotId, Droppable.class).addFilter(InventoryAppState.this);   
			}
		}

		ItemStack[] items = inventory.getItemStacks();
		for(int i = 0; i < Inventory.INVENTORY_SIZE; i++)
		{
		    String itemId = "item" + i;
		    String slotId = "itemSlot" + i;                
		                    
		    Element itemSlot = screen.findElementByName(slotId);
		    if(itemSlot != null)
		    {
		        Element e = itemSlot.findElementByName("item" + i);
		        if(e != null)
		        {
		        	e.markForRemoval();
		        }
		        ItemStack item = items[i];
		        locations.put(itemId, new ItemStackLocation(slotId, itemId, i, item));
		        if(item != null)
		        {
//		            if(e == null)
		            {
		                itemBuilder = new ControlBuilder("item");
		                itemBuilder.id(itemId);
		                itemBuilder.visibleToMouse(true);                            
		                e = itemBuilder.build(nifty, screen, itemSlot);
		            }                        
		            final Sprite sprite = item.getBlock().getSprite();                
		            ImageBuilder imageBuilder = new ImageBuilder()
		            {{ 
		                filename("Textures/FaithfulBlocks.png");
		                imageMode("subImage:" + sprite.getX() + "," + sprite.getY() + "," + sprite.getWidth() + "," + sprite.getHeight());
		                width("50");
		                height("50");

		            }};
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
		
		for(int i = Inventory.INVENTORY_SIZE; i < items.length; i++)
		{
		    String slotId = "itemSlot" + i;
		    String itemId = "item" + i;
		    
		    Element itemSlot = screen.findElementByName(slotId);
		    if(itemSlot != null)
		    {
		        Element e = itemSlot.findElementByName("item" + i);
		        ItemStack item = items[i];
		        
		        if(item != null)
		        {
		            if(e == null)
		            {
		                itemBuilder = new ControlBuilder("item");
		                itemBuilder.id(itemId);
		                itemBuilder.visibleToMouse(true);
		                itemBuilder.parameter("count", Integer.toString(item.getCount()));                    
		                e = itemBuilder.build(nifty, screen, itemSlot);                            
		            }
		            locations.put(itemId, new ItemStackLocation(slotId, itemId, i, item));
		            
		            final Sprite sprite = item.getBlock().getSprite();                
		            ImageBuilder imageBuilder = new ImageBuilder()
		            {{ 
		                filename("Textures/FaithfulBlocks.png");
		                imageMode("subImage:" + sprite.getX() + "," + sprite.getY() + "," + sprite.getWidth() + "," + sprite.getHeight());
		                width("50");
		                height("50");

		            }};
		            imageBuilder.build(nifty, screen, e);      
		            
		            TextBuilder textBuilder = new TextBuilder();
		            textBuilder.style("nifty-label");
		            textBuilder.textVAlignBottom();                        
		            textBuilder.textHAlignRight();
		            textBuilder.font("Interface/Fonts/ArialBlack.fnt");
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
	}

    public void onEndScreen()
    {
    }

    private class InventoryActionListener implements ActionListener
    {
        public void onAction(String name, boolean isPressed, float tpf)
        {
            if ("close".equals(name))
            {
                nifty.fromXml("Interface/hud.xml", "hud", JCraft.getInstance().getHUD());
                inputManager.setCursorVisible(false);
//                playerControl.setEnabled(true);
                JCraft.getInstance().getFlyByCamera().setEnabled(true);
                
            }
        }
    }
    
    private class ItemStackLocation
    {
        public int inventoryLocation;
        private ItemStack itemStack;
        
        public ItemStackLocation(String slotId, String itemId, int inventoryLocation, ItemStack itemStack)
        {
            this.inventoryLocation = inventoryLocation;
            this.itemStack = itemStack;
        }
    }
}
