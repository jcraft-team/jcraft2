/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chappelle.jcraft;

import com.jme3.math.Vector3f;

/**
 *
 * @author Carl
 */
public class BlockNavigator{
    
    public static Vector3Int getNeighborBlockLocalLocation(Vector3Int location, Block.Face face){
        Vector3Int neighborLocation = getNeighborBlockLocation_Relative(face);
        neighborLocation.addLocal(location);
        return neighborLocation;
    }
    
    public static Vector3Int getNeighborBlockLocation_Relative(Block.Face face){
        Vector3Int neighborLocation = new Vector3Int();
        switch(face){
            case Top:
                neighborLocation.set(0, 1, 0);
                break;
            
            case Bottom:
                neighborLocation.set(0, -1, 0);
                break;
            
            case Left:
                neighborLocation.set(-1, 0, 0);
                break;
            
            case Right:
                neighborLocation.set(1, 0, 0);
                break;
            
            case Front:
                neighborLocation.set(0, 0, 1);
                break;
            
            case Back:
                neighborLocation.set(0, 0, -1);
                break;
        }
        return neighborLocation;
    }
    
    public static Block.Face getOppositeFace(Block.Face face){
        switch(face){
            case Top:       return Block.Face.Bottom;
            case Bottom:    return Block.Face.Top;
            case Left:      return Block.Face.Right;
            case Right:     return Block.Face.Left;
            case Front:     return Block.Face.Back;
            case Back:      return Block.Face.Front;
        }
        return null;
    }
    
    public static Vector3Int getPointedBlockLocation(World world, Vector3f collisionContactPoint, boolean getNeighborLocation){
        Vector3f collisionLocation = Util.compensateFloatRoundingErrors(collisionContactPoint);
        Vector3Int blockLocation = new Vector3Int(
                (int) (collisionLocation.getX()),
                (int) (collisionLocation.getY()),
                (int) (collisionLocation.getZ()));
        if((world.getBlock(blockLocation) != null) == getNeighborLocation){
            if((collisionLocation.getX() % 1) == 0) {
                blockLocation.subtractLocal(1, 0, 0);
            }
            else if((collisionLocation.getY() % 1) == 0){
                blockLocation.subtractLocal(0, 1, 0);
            }
            else if((collisionLocation.getZ() % 1) == 0){
                blockLocation.subtractLocal(0, 0, 1);
            }
        }
        return blockLocation;
    }
}
