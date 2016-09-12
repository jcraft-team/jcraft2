package com.chappelle.jcraft.world.terrain.gen;

import java.util.Random;

import com.chappelle.jcraft.blocks.*;
import com.chappelle.jcraft.world.chunk.*;

public class Simplex2DFeature extends AbstractFeature
{
    private double simplexScale;
    private float persistence;
	private int iterations;
	private int height;
    private int waterLevel;
    private byte[] blockIds;
    private int blockCount;
    private Random rand;

	/**
	 * Creates a Feature that generates terrain based on the Simplex2D noise algorithm with default values
	 * @param seed The world seed
	 */
	public Simplex2DFeature(long seed, byte... blockIds)
	{
		this(seed, 0.009f, 0.33f, 4, 80, blockIds);
	}
	
	/**
	 * Creates a Feature that generates terrain based on the Simplex2D noise algorithm
	 * @param seed The world seed
	 * @param simplexScale Range from around 0.015 to around 0.001  The higher the number the more rugged and extreme the terain.
	 * @param persistence Persistence value used in the Simplex2D noise algorithm
	 * @param iterations Use a value of 1 to get very smooth rolling hills.  No need to go higher than 4.
	 * @param height The max height of the terrain
	 */
	public Simplex2DFeature(long seed, float simplexScale, float persistence, int iterations, int height, byte... blockIds)
	{
		this.simplexScale = simplexScale;
		this.persistence = persistence;
		this.iterations = iterations;
		this.height = height;
		this.waterLevel = (int) (height * .3f);
		this.blockIds = blockIds;
		if(this.blockIds == null || this.blockIds.length == 0)
		{
			this.blockIds = new byte[]{Blocks.grass.blockId};
		}
		this.blockCount = this.blockIds.length;
		this.rand = new Random(seed);
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
				blockTypes[x][0][z] = Blocks.bedrock.blockId;
				Double c = sumOctave(iterations, x+xOffset, z+zOffset, persistence, simplexScale);
				c = normalize(c, 1, height);
				for (int y = 1; y < c; y++)
				{
                   byte blockToPlace = blockIds[rand.nextInt(blockCount)];
                   blockTypes[x][y][z] = blockToPlace;
				}
                if(c<waterLevel) // water
                {
                    for(int y=c.intValue()+1; y<=waterLevel; y++)
                    {
                    	blockTypes[x][y][z] = Blocks.water.blockId;
                    }
                }
			}
		}
	}
	
    private double sumOctave(int num_iterations, double x, double z, double persistence, double scale)
    {
        double maxAmp = 0;
        double amp = 1;
        double noise = 0;

        //#add successively smaller, higher-frequency terms
        for(int i = 0; i < num_iterations; ++i)
        {
            noise += SimplexNoise.noise2D(x, z, scale) * amp;
            maxAmp += amp;
            amp *= persistence;
            scale *= 2;
        }

        //take the average value of the iterations
        noise /= maxAmp;

        return noise;
    }

    private Double normalize(double value, double low, double high)
    {
        return value * (high - low) / 2 + (high + low) / 2;
    }

    /**
     * SimplexScale Range from around 0.015 to around 0.001  The higher the number the more rugged and extreme the terain.
     */
	public Simplex2DFeature setSimplexScale(double simplexScale)
	{
		this.simplexScale = simplexScale;
		return this;
	}

	public Simplex2DFeature setPersistence(float persistence)
	{
		this.persistence = persistence;
		return this;
	}

	/**
	 * Iterations Use a value of 1 to get very smooth rolling hills.  No need to go higher than 4.
	 */
	public Simplex2DFeature setIterations(int iterations)
	{
		this.iterations = iterations;
		return this;
	}

	public Simplex2DFeature setHeight(int height)
	{
		this.height = height;
		this.waterLevel = (int)0.5f*height;
		return this;
	}
	
}