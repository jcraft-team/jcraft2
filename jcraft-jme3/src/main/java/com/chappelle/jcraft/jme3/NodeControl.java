package com.chappelle.jcraft.jme3;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * Simple base class for {@code AbstractControl}s that need to attach to a {@code Node}
 */
public abstract class NodeControl extends AbstractControl
{
	protected abstract void setNode(Node node);
	
    @Override
    public final void setSpatial(Spatial spatial)
    {
        super.setSpatial(spatial);

        if (spatial instanceof Node)
        {
        	setNode((Node)spatial);
        }
    }

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp)
	{
	}
}
