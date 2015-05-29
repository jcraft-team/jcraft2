package com.chappelle.jcraft;

import com.cubes.Vector3Int;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * Control that adds a wire frame box around the block the cursor is pointed at.
 */
public class BlockCursorControl extends NodeControl
{
	private Geometry blockCursor;
	private float halfBlockSize;
	private float blockSize;
	private BlockHelper blockHelper;
	private AssetManager assetManager;
	private Vector3Int prevCursorLocation;
	
	public BlockCursorControl(BlockHelper blockHelper, AssetManager assetManager, float blockSize)
	{
		this.blockHelper = blockHelper;
		this.assetManager = assetManager;
		this.blockSize = blockSize;
		this.halfBlockSize = blockSize/2;
	}
	
    @Override
    public void setNode(Node node)
    {
        blockCursor = createWireBox(halfBlockSize, ColorRGBA.Yellow);
        blockCursor.setCullHint(Spatial.CullHint.Always);

        node.attachChild(blockCursor);
    }
    
	@Override
	protected void controlUpdate(float tpf)
	{
		if(isEnabled())
		{
			Vector3Int newCursorLocation = blockHelper.getPointedBlockLocationInChunkSpace(false);
			if(newCursorLocation == null)
			{
				blockCursor.setCullHint(Spatial.CullHint.Always);
			}
			else
			{
				if(!newCursorLocation.equals(prevCursorLocation))
				{
					blockCursor.setCullHint(Spatial.CullHint.Never);
					Vector3f pos = newCursorLocation.mult((int)blockSize).toVector3f().addLocal(halfBlockSize, halfBlockSize, halfBlockSize);
					blockCursor.setLocalTranslation(pos);
				}
			}
			prevCursorLocation = newCursorLocation;
		}
	}

    private Geometry createWireBox(float size, ColorRGBA color)
    {
        Geometry g = new Geometry("wireframe cube", new Box(size + 0.01f, size + 0.01f, size + 0.01f));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        return g;
    }

}