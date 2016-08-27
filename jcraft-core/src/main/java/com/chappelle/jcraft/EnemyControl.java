package com.chappelle.jcraft;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

public class EnemyControl extends NodeControl
{
	private Geometry cube1Geo;
	private Enemy enemy;
	private Vector3f position = new Vector3f();
	private AssetManager assetManager;
	
	public EnemyControl(AssetManager assetManager, Enemy enemy)
	{
		this.assetManager = assetManager;
		this.enemy = enemy;
	}
	
	@Override
	protected void attach()
	{
		Box cube1Mesh = new Box( 0.5f, 1f, 0.5f);
//		enemy.setSize(0.6F, 1.8F);
		enemy.setSize(4F, 5F);
		cube1Geo = new Geometry("My Textured Box", cube1Mesh);
		Material cube1Mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		cube1Mat.setColor("Color", ColorRGBA.Green);
		cube1Geo.setMaterial(cube1Mat);
		getNode().attachChild(cube1Geo);
	}

	@Override
	protected void detach()
	{
		getNode().detachChild(cube1Geo);
	}

	@Override
	protected void controlUpdate(float tpf)
	{
		if(isEnabled())
		{
			enemy.update(tpf);
			
			position.set((float)enemy.posX, (float)enemy.posY, (float)enemy.posZ);
			cube1Geo.setLocalTranslation(position);
		}
	}
}