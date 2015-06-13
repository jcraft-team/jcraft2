package com.chappelle.jcraft;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.chappelle.jcraft.world.chunk.Chunk;

public class BlockState implements Serializable
{
    private Map<Short, Object> blockState;

    private transient Chunk chunk;
    
    public BlockState(Chunk chunk)
    {
    	this.chunk = chunk;
    }
    
    public BlockState(){}
    
    public void setChunk(Chunk chunk)
    {
    	this.chunk = chunk;
    }
    
    public void put(Short key, Object value)
    {
        if(blockState == null)
        {
            blockState = new HashMap<>();
        }
        Object current = blockState.get(value);
        
        blockState.put(key, value);
        
        if(valueChanged(current, value))
        {
        	onChange();
        }
    }
    
    private boolean valueChanged(Object v1, Object v2)
    {
    	if(v1 == null && v2 == null)
    	{
    		return false;
    	}
    	else if(v1 == null && v2 != null)
    	{
    		return true;
    	}
    	else if(v1 != null && v2 == null)
    	{
    		return true;
    	}
    	return v1.equals(v2);
    }
    
    protected void onChange()
    {
    	chunk.markDirty();
    }

    public Object get(Short key)
    {
        if(blockState == null)
        {
            return null;
        }
        else
        {
            return blockState.get(key);
        }
    }
}
