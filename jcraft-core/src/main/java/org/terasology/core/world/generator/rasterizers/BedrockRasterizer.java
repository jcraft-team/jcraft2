package org.terasology.core.world.generator.rasterizers;

import org.terasology.world.generation.*;

import com.chappelle.jcraft.blocks.Blocks;
import com.chappelle.jcraft.world.chunk.Chunk;

public class BedrockRasterizer implements WorldRasterizer
{

	@Override
	public void initialize()
	{

	}

	@Override
	public void generateChunk(Chunk chunk, Region chunkRegion)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				chunk.setBlock(x, 0, z, Blocks.bedrock);
			}
		}
	}

}
