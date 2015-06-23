package com.chappelle.jcraft;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;

public class CubesSettings
{
	private AssetManager assetManager;
	private Material blockMaterial;
	private int texturesCountX = 16;
	private int texturesCountY = 16;

	private static CubesSettings INSTANCE;
	
	public static CubesSettings getInstance()
	{
		return INSTANCE;
	}
	
	public CubesSettings(Application application)
	{
		assetManager = application.getAssetManager();
		INSTANCE = this;
	}
	
	public AssetManager getAssetManager()
	{
		return assetManager;
	}

	public Material getBlockMaterial()
	{
		return blockMaterial;
	}

	public void setDefaultBlockMaterial(String textureFilePath)
	{
		setBlockMaterial(ChunkMaterial.makeUnshadedMaterial(assetManager, textureFilePath));
	}
	
	public void setBlockMaterial(Material blockMaterial)
	{
		this.blockMaterial = blockMaterial;
	}

	public int getTexturesCountX()
	{
		return texturesCountX;
	}

	public int getTexturesCountY()
	{
		return texturesCountY;
	}

	public void setTexturesCount(int texturesCountX, int texturesCountY)
	{
		this.texturesCountX = texturesCountX;
		this.texturesCountY = texturesCountY;
	}
}
