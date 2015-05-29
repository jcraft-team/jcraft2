package com.chappelle.jcraft.blocks;

import com.cubes.Block;
import com.cubes.BlockSkin;
import com.cubes.BlockSkin_TextureLocation;
import com.cubes.Chunk;
import com.cubes.Vector3Int;
import com.cubes.World;

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