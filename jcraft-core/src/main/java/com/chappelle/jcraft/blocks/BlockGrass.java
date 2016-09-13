package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.util.math.Vector3Int;
import com.chappelle.jcraft.world.World;
import com.chappelle.jcraft.world.chunk.Chunk;
import com.jme3.math.Vector3f;

public class BlockGrass extends Block
{
	public BlockGrass(int blockId)
	{
		super(blockId, new BlockSkin[] { new BlockSkin(new BlockSkin_TextureLocation(0, 0), false),
				new BlockSkin(new BlockSkin_TextureLocation(3, 0), false),
				new BlockSkin(new BlockSkin_TextureLocation(2, 0), false) });
	}

	public void onBlockPlaced(World world, Vector3Int location, Vector3f contactNormal, Vector3f cameraDirectionAsUnitVector)
	{
		world.playSound(SoundConstants.DIG_GRASS, 4);
	}

	public void onBlockRemoved(World world, Vector3Int location)
	{
		world.playSound(SoundConstants.DIG_GRASS, 4);
	}

	public void onEntityWalking(World world, Vector3Int location)
	{
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