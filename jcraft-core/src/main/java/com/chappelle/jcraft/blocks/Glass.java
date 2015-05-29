package com.chappelle.jcraft.blocks;

import com.cubes.Block;
import com.cubes.BlockSkin;
import com.cubes.BlockSkin_TextureLocation;
import com.cubes.World;

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