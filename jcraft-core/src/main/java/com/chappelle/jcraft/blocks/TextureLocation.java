package com.chappelle.jcraft.blocks;

public class TextureLocation
{
	private int row;
	private int column;

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
}
