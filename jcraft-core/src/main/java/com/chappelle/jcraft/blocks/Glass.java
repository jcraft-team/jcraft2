package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.BlockSkin;
import com.chappelle.jcraft.BlockSkin_TextureLocation;
import com.chappelle.jcraft.World;

public class Glass extends Block
{
	public Glass(World blockTerrainManager)
	{
		super(blockTerrainManager, new BlockSkin[] { new BlockSkin(new BlockSkin_TextureLocation(1, 3), true)});
	}
	
	@Override
	public boolean isTransparent()
	{
		return true;
	}
}