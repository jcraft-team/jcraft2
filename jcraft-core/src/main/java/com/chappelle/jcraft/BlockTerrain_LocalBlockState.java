/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chappelle.jcraft;

import com.chappelle.jcraft.world.chunk.Chunk;

/**
 *
 * @author Carl
 */
public class BlockTerrain_LocalBlockState{

    public BlockTerrain_LocalBlockState(Chunk chunk, Vector3Int localBlockLocation){
        this.chunk = chunk;
        this.localBlockLocation = localBlockLocation;
    }
    private Chunk chunk;
    private Vector3Int localBlockLocation;

    public Chunk getChunk(){
        return chunk;
    }

    public Vector3Int getLocalBlockLocation(){
        return localBlockLocation;
    }

    public Block getBlock(){
        return chunk.getBlock(localBlockLocation);
    }
    
    public void setBlock(Block block){
        chunk.setBlock(localBlockLocation, block);
    }
    
    public void removeBlock(){
        chunk.removeBlock(localBlockLocation);
    }
}
