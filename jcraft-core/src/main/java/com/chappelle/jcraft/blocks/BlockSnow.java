package com.chappelle.jcraft.blocks;

public class BlockSnow extends Block
{
	public BlockSnow(int blockId)
	{
		super(blockId, new BlockSkin[]{
				new BlockSkin(new BlockSkin_TextureLocation(2, 4), false),
				new BlockSkin(new BlockSkin_TextureLocation(2, 0), false),
				new BlockSkin(new BlockSkin_TextureLocation(4, 4), false),
				new BlockSkin(new BlockSkin_TextureLocation(4, 4), false),
				new BlockSkin(new BlockSkin_TextureLocation(4, 4), false),
				new BlockSkin(new BlockSkin_TextureLocation(4, 4), false)
				});
	}
}
