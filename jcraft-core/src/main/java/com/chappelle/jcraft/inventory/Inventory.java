package com.chappelle.jcraft.inventory;

import java.util.ArrayList;
import java.util.List;

import com.chappelle.jcraft.blocks.Block;

public class Inventory 
{
    public static final int INVENTORY_SIZE = 45;
    public static final int EQUIPPED_SIZE = 9;
    
    private static int NEXT_STACK_ID = 1;
    
    private ItemStack[] inventory = new ItemStack[INVENTORY_SIZE + EQUIPPED_SIZE];
    
    private int selectedIndex = -1;

    private List<InventoryListener> listeners = new ArrayList<InventoryListener>();
    
    public ItemStack selectItem(int index)
    {
        index = index - 1;
        if(index >= 0 && index < EQUIPPED_SIZE)
        {
            int oldIndex = selectedIndex;
            selectedIndex = INVENTORY_SIZE + index;
            fireSelectionChanged(oldIndex, selectedIndex);
            return inventory[selectedIndex];
        }
        return null;
    }
    
    public void move(int fromIndex, int toIndex, ItemStack stack)
    {
        if(isValidIndex(toIndex))
        {
            inventory[toIndex] = inventory[fromIndex];
            inventory[fromIndex] = null;
        }
        fireInventoryChanged();        
    }
    
    public void add(Block block, int count)
    {
        for(int i = 0; i < count; i++)
        {
            add(block);
        }
    }
    
    public void add(Block block)
    {
        ItemStack stack = getAvailableItemStack(block);
        if(stack == null)
        {
            int emptySlot = getFirstEmptySlot();
            if(emptySlot >= 0)
            {
                inventory[emptySlot] = new ItemStack(NEXT_STACK_ID++, block);
            }
        }
        else
        {
            stack.add(1);
        }
        fireInventoryChanged();                    
    }
    
    public ItemStack getEquippedSlot(int index)
    {
        return inventory[INVENTORY_SIZE + index - 1];
    }

    public void subtract(ItemStack itemStack)
    {
        itemStack.subtract(1);
        if(itemStack.getCount() <= 0)
        {
            for(int i = 0; i < inventory.length; i++)
            {
                ItemStack item = inventory[i];
                if(item == itemStack)
                {
                    inventory[i] = null;
                }
            }
        }
        fireInventoryChanged();
    }
    
    private int getFirstEmptySlot()
    {
        int result = getFirstEmptyEquippedSlot();
        if(result < 0)
        {
            result = getFirstEmptyInventorySlot();
        }
        return result;
    }

    private int getFirstEmptyInventorySlot()
    {
        return getFirstEmptySlot(0, INVENTORY_SIZE);
    }
    
    private int getFirstEmptyEquippedSlot()
    {
        return getFirstEmptySlot(INVENTORY_SIZE, inventory.length);
    }

    private int getFirstEmptySlot(int start, int end)
    {
        for(int i = start; i < end; i++)
        {
            if(inventory[i] == null)
            {
                return i;
            }
        }
        return -1;        
    }
    
    private ItemStack getAvailableItemStack(Block block)
    {
        for(int i = 0; i < inventory.length; i++)
        {
            ItemStack stack = inventory[i];
            if(stack != null && stack.canAccept(block))
            {
                return stack;
            }
        }
        return null;
    }
    
    public ItemStack[] getItemStacks()
    {
        return inventory;
    }    

    public int getSelectedIndex()
    {
        return selectedIndex;
    }
    
    private boolean isValidIndex(int index)
    {
        return index >= 0 && index < inventory.length;
    }
        
    private void fireInventoryChanged()
    {
        for(InventoryListener l : listeners)
        {
            l.onInventoryChanged();
        }
    }

    private void fireSelectionChanged(int oldIndex, int newIndex)
    {
        for(InventoryListener l : listeners)
        {
            l.onSelectionChanged(oldIndex, newIndex);
        }
    }
    
    public void addListener(InventoryListener listener)
    {
        listeners.add(listener);
    }    
}
