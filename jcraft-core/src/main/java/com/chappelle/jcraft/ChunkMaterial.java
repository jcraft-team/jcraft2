package com.chappelle.jcraft;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.texture.Texture;

public class ChunkMaterial extends Material
{
    public static ChunkMaterial makeUnshadedMaterial(AssetManager assetManager, String blockTextureFilePath)
    {
    	ChunkMaterial result = new ChunkMaterial("MatDefs/Blocks.j3md", assetManager, blockTextureFilePath, true);
    	result.setTexture("ColorMap", makeTexture(assetManager, blockTextureFilePath));
    	result.setBoolean("VertexColor", true);
    	setupMaterial(result);
    	return result;
    }

    public static ChunkMaterial makeLightingMaterial(AssetManager assetManager, String blockTextureFilePath)
    {
    	ChunkMaterial result = new ChunkMaterial("MatDefs/Lighting.j3md", assetManager, blockTextureFilePath, true);
    	result.setTexture("DiffuseMap", makeTexture(assetManager, blockTextureFilePath));
    	result.setBoolean("UseMaterialColors", true);
    	setupMaterial(result);
    	return result;
    }

    private static Texture makeTexture(AssetManager assetManager, String blockTextureFilePath)
    {
    	Texture texture = assetManager.loadTexture(blockTextureFilePath);
    	texture.setMagFilter(Texture.MagFilter.Nearest);
    	texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
    	return texture;
    }

    private static void setupMaterial(ChunkMaterial material)
    {
    	material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
    	material.setFloat("AlphaDiscardThreshold", 0.1f);
    }
    
    private ChunkMaterial(String matDef, AssetManager assetManager, String blockTextureFilePath, boolean useVertextColor)
    {
    	super(assetManager, matDef);
    }
}
