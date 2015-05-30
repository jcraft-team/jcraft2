package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.BlockSkin;
import com.chappelle.jcraft.BlockSkin_TextureLocation;

public class Glass extends Block
{
	public Glass(int blockId)
	{
		super(blockId, new BlockSkin[] { new BlockSkin(new BlockSkin_TextureLocation(1, 3), true)});
	}
	
	@Override
	public boolean isTransparent()
	{
		return true;
	}
}