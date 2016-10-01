package com.chappelle.jcraft.blocks;

public class Skin
{
	private TextureLocation textureLocation;
	private boolean isTransparent;

	public Skin(int textureRow, int textureColumn, float xOffset, float yOffset, boolean isTransparent)
	{
		this(new TextureLocation(textureRow, textureColumn, xOffset, yOffset), isTransparent);
	}
	
	public Skin(int textureRow, int textureColumn, boolean isTransparent)
	{
		this(new TextureLocation(textureRow, textureColumn), isTransparent);
	}
	public Skin(TextureLocation textureLocation, boolean isTransparent)
	{
		this.textureLocation = textureLocation;
		this.isTransparent = isTransparent;
	}

	public TextureLocation getTextureLocation()
	{
		return textureLocation;
	}

	public boolean isTransparent()
	{
		return isTransparent;
	}
}
