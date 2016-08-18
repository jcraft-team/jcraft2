package com.chappelle.jcraft;

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
    protected Node getNode() {
        return (Node)getSpatial();
    }

    @Override
    public void setSpatial( Spatial s ) {
        if( s != null && !(s instanceof Node) )
            throw new RuntimeException( "Node controls must be associated with Nodes or Node subclasses." );

        if( getSpatial() != null ) {
            detach();
        }
        super.setSpatial(s);
        if( getSpatial() != null ) {
            attach();
        }
    }

    protected abstract void attach();
    protected abstract void detach();

    @Override
    protected void controlUpdate( float tpf ) {
    }

    @Override
    protected void controlRender( RenderManager rm, ViewPort vp ) {
    }
}
