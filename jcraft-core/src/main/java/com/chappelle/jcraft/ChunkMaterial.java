package com.chappelle.jcraft;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.texture.Texture;

public class ChunkMaterial extends Material
{
    public ChunkMaterial(AssetManager assetManager, String blockTextureFilePath)
    {
    	super(assetManager, "MatDefs/Blocks.j3md");
    	
    	Texture texture = assetManager.loadTexture(blockTextureFilePath);
    	texture.setMagFilter(Texture.MagFilter.Nearest);
    	texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
    	setTexture("ColorMap", texture);

    	setBoolean("VertexColor", true);
    	getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
    	setFloat("dayNightLighting", 1.0f);
    	setFloat("AlphaDiscardThreshold", 0.1f);
    }
}
