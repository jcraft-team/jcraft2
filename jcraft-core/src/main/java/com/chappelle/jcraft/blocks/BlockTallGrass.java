package com.chappelle.jcraft.blocks;


public class BlockTallGrass extends BlockFlower
{
	public BlockTallGrass(int blockId, int textureCol, int textureRow)
	{
		super(blockId, textureCol, textureRow);
		color.g = 1.0f;
		color.r = 0.0f;
		color.b = 0.0f;
	}
}