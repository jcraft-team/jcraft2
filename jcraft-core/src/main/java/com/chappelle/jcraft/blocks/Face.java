package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.util.math.Vector3Int;
import com.jme3.math.Vector3f;

public enum Face
{
	Top(Vector3f.UNIT_Y), Bottom(Vector3f.UNIT_Y.negate()), Left(Vector3f.UNIT_X.negate()), Right(Vector3f.UNIT_X), Front(Vector3f.UNIT_Z), Back(Vector3f.UNIT_Z.negate());

	public Vector3f normal;
	public Vector3f oppositeNormal;
	
	private Face(Vector3f normal)
	{
		this.normal = normal;
		this.oppositeNormal = normal.negate();
	}
	
	public Vector3f getNormal()
	{
		return normal;
	}
	
	public static Face fromNormal(Vector3f normal)
	{
		return fromNormal(Vector3Int.fromVector3f(normal));
	}

	public static Face fromNormal(Vector3Int normal)
	{
		int x = normal.getX();
		int y = normal.getY();
		int z = normal.getZ();
		if(x != 0)
		{
			if(x > 0)
			{
				return Face.Right;
			}
			else
			{
				return Face.Left;
			}
		}
		else if(y != 0)
		{
			if(y > 0)
			{
				return Face.Top;
			}
			else
			{
				return Face.Bottom;
			}
		}
		else if(z != 0)
		{
			if(z > 0)
			{
				return Face.Front;
			}
			else
			{
				return Face.Back;
			}
		}
		return null;
	}

    public static Face getOppositeFace(Face face)
    {
        switch(face){
            case Top:       return Face.Bottom;
            case Bottom:    return Face.Top;
            case Left:      return Face.Right;
            case Right:     return Face.Left;
            case Front:     return Face.Back;
            case Back:      return Face.Front;
        }
        return null;
    }
    
    public static Vector3Int getNeighborBlockLocalLocation(Vector3Int location, Face face)
    {
        Vector3Int neighborLocation = getNeighborBlockLocation_Relative(face);
        neighborLocation.addLocal(location);
        return neighborLocation;
    }
    
    public static Vector3Int getNeighborBlockLocation_Relative(Face face)
    {
        Vector3Int neighborLocation = new Vector3Int();
        switch(face)
        {
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
    
}