package com.chappelle.jcraft.shapes;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * Helper class for manipulating shape vectors
 */
public class ShapeVectors
{
    private Vector3f[] all = new Vector3f[8];
    
    public ShapeVectors(Vector3f bottomTopLeft, Vector3f bottomTopRight, Vector3f bottomBottomLeft, Vector3f bottomBottomRight, Vector3f topTopLeft, Vector3f topTopRight, Vector3f topBottomLeft, Vector3f topBottomRight)
    {
        all[0] = bottomTopLeft;
        all[1] = bottomTopRight;
        all[2] = bottomBottomLeft;
        all[3] = bottomBottomRight;
        all[4] = topTopLeft;
        all[5] = topTopRight;
        all[6] = topBottomLeft;
        all[7] = topBottomRight;
    }
    
    public void zero()
    {
        for(Vector3f v : all)
        {
            v.zero();
        }
    }
    
    public void rotate(Quaternion q)
    {
        for(int i = 0; i < all.length; i++)
        {
            q.multLocal(all[i]);
        }
    }
    
    public void add(Vector3f translation)
    {
        for(Vector3f v : all)
        {
            v.addLocal(translation);
        }
    }

    public void subtract(Vector3f translation)
    {
        for(Vector3f v : all)
        {
            v.subtractLocal(translation);
        }
    }    
}
