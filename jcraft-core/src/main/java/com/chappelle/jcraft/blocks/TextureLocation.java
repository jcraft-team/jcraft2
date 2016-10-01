package com.chappelle.jcraft.blocks;

public class TextureLocation
{
	private int row;
	private int column;
	private float xOffset;
	private float yOffset;

	public TextureLocation(int row, int column, float xOffset, float yOffset)
	{
		this(row, column);
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public TextureLocation(int row, int column)
	{
		this.row = row;
		this.column = column;
	}

	public int getColumn()
	{
		return column;
	}

	public int getRow()
	{
		return row;
	}

	public float getxOffset()
	{
		return xOffset;
	}

	public float getyOffset()
	{
		return yOffset;
	}
}
