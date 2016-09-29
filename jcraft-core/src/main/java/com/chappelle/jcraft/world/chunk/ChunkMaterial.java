package com.chappelle.jcraft.world.chunk;

import com.chappelle.jcraft.GameSettings;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
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
    	
    	//TODO: Need to update if someone changes render distance in game
    	int fogStart = (GameSettings.chunkRenderDistance-1)*16;
		setFloat("FogStart", fogStart);
    	setFloat("FogEnd", fogStart+24);
		setColor("FogColor", GameSettings.defaultSkyColor);
    	setBoolean("VertexColor", true);
    	getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
    	setFloat("dayNightLighting", 1.0f);
    	setFloat("AlphaDiscardThreshold", 0.1f);
    }
    
    public void setFogColor(ColorRGBA color)
    {
    	setColor("FogColor", color);
    }
}
