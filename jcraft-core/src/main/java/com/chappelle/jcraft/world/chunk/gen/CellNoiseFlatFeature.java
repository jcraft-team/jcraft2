package com.chappelle.jcraft.world.chunk.gen;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.chunk.Feature;

public class CellNoiseFlatFeature implements Feature
{
    private final double simplexScale; // range from around 0.015 to around 0.001  The higher the number the more rugged and extreme the terain.
    private final float persistence;
	private final int iterations; // Use a value of 1 to get very smooth rolling hills.  No need to go higher than 4.
    private CellNoise cellNoise;

	public CellNoiseFlatFeature(long seed)
	{
		this(seed, 0.009f, 0.33f, 4);
	}
	
	public CellNoiseFlatFeature(long seed, float simplexScale, float persistence, int iterations)
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
                Double c = sumOctave(iterations, x+xOffset, z+zOffset, persistence, simplexScale);
				blockTypes[x][0][z] = getBlock(c);
			}
		}
	}
	
	private double sumOctave(int num_iterations, double x, double y, double persistence, double scale)
	{
	    double maxAmp = 0;
	    double amp = 1;
	    double noise = 0;

	    //#add successively smaller, higher-frequency terms
	    for(int i = 0; i < num_iterations; ++i)
	    {
	    	noise += cellNoise.noise(x, y,   scale) * amp;
			maxAmp += amp;
			amp *= persistence;
			scale *= 2;
	    }

	    //take the average value of the iterations
	    noise /= maxAmp;

	    return noise;	
	}
	
	public double normalize(double value, double floor, double ceiling, double minIn, double maxIn)
	{
		return ((ceiling - floor)*(value - minIn)) / (maxIn - minIn) + floor;
	}
	
    private byte getBlock(Double c)
    {
        byte block = Block.woolBlack.blockId;
		c = normalize(c, 0, 14, -1, 1);
		
		byte c2 = c.byteValue();
		block = (byte)(block + c2);
        return block;
    }
}
