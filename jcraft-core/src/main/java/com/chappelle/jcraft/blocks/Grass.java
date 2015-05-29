package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.Block;
import com.chappelle.jcraft.BlockSkin;
import com.chappelle.jcraft.BlockSkin_TextureLocation;
import com.chappelle.jcraft.Chunk;
import com.chappelle.jcraft.Vector3Int;
import com.chappelle.jcraft.World;

public class Grass extends Block
{
	public Grass(World blockTerrainManager)
	{
		super(blockTerrainManager, new BlockSkin[] { new BlockSkin(new BlockSkin_TextureLocation(0, 0), false),
				new BlockSkin(new BlockSkin_TextureLocation(3, 0), false),
				new BlockSkin(new BlockSkin_TextureLocation(2, 0), false) });
	}

	@Override
	protected int getSkinIndex(Chunk chunk, Vector3Int location, Block.Face face)
	{
		if(chunk == null || chunk.isBlockOnSurface(location))
		{
			switch(face)
			{
				case Top:
					return 0;
				case Bottom:
					return 2;
				default: 
					return 1;
			}
		}
		return 2;
	}
}