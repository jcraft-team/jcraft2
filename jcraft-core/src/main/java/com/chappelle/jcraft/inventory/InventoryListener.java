package com.chappelle.jcraft.inventory;

public interface InventoryListener 
{
    void onInventoryChanged();
    
    void onSelectionChanged(int oldIndex, int newIndex);
}
