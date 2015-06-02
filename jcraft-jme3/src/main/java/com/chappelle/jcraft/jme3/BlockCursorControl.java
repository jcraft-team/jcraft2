package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.BlockHelper;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.World;
import com.chappelle.jcraft.util.AABB;
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
	private BlockHelper blockHelper;
	private AssetManager assetManager;
	private Vector3Int prevCursorLocation;
	private World world;
	private Box box;
	
	public BlockCursorControl(World world, BlockHelper blockHelper, AssetManager assetManager)
	{
		this.world = world;
		this.blockHelper = blockHelper;
		this.assetManager = assetManager;
	}
	
    @Override
    public void setNode(Node node)
    {
    	box = new Box(1,1,1);
        blockCursor = new Geometry("wireframe cube", box);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Yellow);
        blockCursor.setMaterial(mat);

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
					Block block = world.getBlock(newCursorLocation);
					if(block != null)
					{
						blockCursor.setCullHint(Spatial.CullHint.Never);
						
						AABB bb = block.getCollisionBoundingBox(world, newCursorLocation.x, newCursorLocation.y, newCursorLocation.z);
						Vector3f center = new Vector3f((float)Math.abs(bb.minX-bb.maxX)/2.0f, (float)Math.abs(bb.minY-bb.maxY)/2.0f, (float)Math.abs(bb.minZ-bb.maxZ)/2.0f);
						box.updateGeometry(center, center.x, center.y, center.z);
						blockCursor.setLocalTranslation(newCursorLocation.toVector3f());
					}
					else
					{
						blockCursor.setCullHint(Spatial.CullHint.Always);
					}
				}
			}
			prevCursorLocation = newCursorLocation;
		}
	}
}