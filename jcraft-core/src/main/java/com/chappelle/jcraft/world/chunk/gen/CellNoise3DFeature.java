package com.chappelle.jcraft.world.chunk.gen;

import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.world.chunk.Feature;

public class CellNoise3DFeature implements Feature
{
    private final double simplexScale; // range from around 0.015 to around 0.001  The higher the number the more rugged and extreme the terain.
    private final float persistence;
	private final int iterations; // Use a value of 1 to get very smooth rolling hills.  No need to go higher than 4.
    private CellNoise cellNoise;

	public CellNoise3DFeature(long seed)
	{
		this(seed, 0.007f, 0.5f, 4);
	}
	
	public CellNoise3DFeature(long seed, float simplexScale, float persistence, int iterations)
	{
		
		short method = 1;
		this.cellNoise = new CellNoise(seed, method);
		this.simplexScale = simplexScale;
		this.persistence = persistence;
		this.iterations = iterations;
	}

	
	@Override
	public void generate(int chunkX, int chunkZ, byte[][][] blockTypes)
	{
		int xOffset = chunkX*16;
		int zOffset = chunkZ*16;
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				blockTypes[x][0][z] = Block.bedrock.blockId;
				for (int y = 1; y < 100; y++)
				{
	                Double c = sumOctave(iterations, x+xOffset, y, z+zOffset, persistence, simplexScale);
					blockTypes[x][y][z] = getBlock(c);
                }
			}
		}
	}
	
    private double sumOctave(int num_iterations, double x, double y, double z, double persistence, double scale)
    {
        double maxAmp = 0;
        double amp = 1;
        double noise = 0;

        //#add successively smaller, higher-frequency terms
        for(int i = 0; i < num_iterations; ++i)
        {
            noise += cellNoise.noise(x, y, z, scale) * amp;
            maxAmp += amp;
            amp *= persistence;
            scale *= 2;
        }

        //take the average value of the iterations
        noise /= maxAmp;

        return noise;
    }

    private byte getBlock(Double c)
    {
    	c = Math.abs(c);
    	byte block = 0;
        if(c>.28)
        {
            block = Block.woolRed.blockId;
        }
        if(c>.30)
        {
            block = Block.woolBlue.blockId;
        }
        if(c>.35)
        {
            block = Block.woolOrange.blockId;
        }
        if(c>.40)
        {
            block = Block.woolYellow.blockId;
        }
        if(c>.45)
        {
            block = Block.woolGreen.blockId;
        }
        if(c>.50)
        {
            block = Block.woolBrown.blockId;
        }
        if(c>.55)
        {
            block = Block.woolPink.blockId;
        }
        if(c>.60)
        {
            block = Block.woolMagenta.blockId;
        }
        if(c>.65 )
        {
        	block = Block.woolBlack.blockId;
        }
        if(c>.70 )
        {
        	block = 0;
        }
        return (byte)block;
    }
}
