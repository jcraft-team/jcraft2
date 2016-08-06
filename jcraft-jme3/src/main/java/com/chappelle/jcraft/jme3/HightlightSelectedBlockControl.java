package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.*;
import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.util.*;
import com.chappelle.jcraft.world.World;
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
public class HightlightSelectedBlockControl extends NodeControl
{
	private Geometry blockCursor;
	private AssetManager assetManager;
	private World world;
	private Box box;
	private Vector3f minPoint = new Vector3f(0, 0, 0);
	private Vector3f maxPoint = new Vector3f(1, 1, 1);
	private EntityPlayer player;
	private Vector3Int previousLocation = new Vector3Int();
	
	public HightlightSelectedBlockControl(World world, EntityPlayer player, AssetManager assetManager)
	{
		this.world = world;
		this.assetManager = assetManager;
		this.player = player;
	}
	
    @Override
    public void setNode(Node node)
    {
    	box = new Box(minPoint, maxPoint);
        blockCursor = new Geometry("blockCursor", box);
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
			RayTrace rayTrace = player.pickBlock();
			if(rayTrace == null)
			{
				blockCursor.setCullHint(Spatial.CullHint.Always);
				previousLocation.set(-1, -1, -1);//instead of setting to null, set to invalid location
			}
			else
			{
				if(previousLocation.x != rayTrace.blockX || previousLocation.y != rayTrace.blockY || previousLocation.z != rayTrace.blockZ)
				{
					previousLocation.set(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
					
					Block block = world.getBlock(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
					if(block != null)
					{
						blockCursor.setCullHint(Spatial.CullHint.Never);
						
						AABB bb = block.getSelectedBoundingBox(world, rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
						if(bb != null)
						{
							bb.offset(-rayTrace.blockX, -rayTrace.blockY, -rayTrace.blockZ);
							minPoint.set((float)bb.minX, (float)bb.minY, (float)bb.minZ);
							maxPoint.set((float)bb.maxX, (float)bb.maxY, (float)bb.maxZ);
							box.updateGeometry(minPoint, maxPoint);
							blockCursor.setLocalTranslation(rayTrace.blockX, rayTrace.blockY, rayTrace.blockZ);
						}
						else
						{
							blockCursor.setCullHint(Spatial.CullHint.Always);
						}
					}
					else
					{
						blockCursor.setCullHint(Spatial.CullHint.Always);
					}
				}
			}
		}
	}
}