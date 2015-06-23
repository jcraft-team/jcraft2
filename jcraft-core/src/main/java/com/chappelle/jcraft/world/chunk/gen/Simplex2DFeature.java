package com.chappelle.jcraft.world.chunk.gen;

import java.util.Random;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.util.MathUtils;
import com.chappelle.jcraft.world.chunk.Feature;

public class Simplex2DFeature implements Feature
{
    private final double simplexScale; // range from around 0.015 to around 0.001  The higher the number the more rugged and extreme the terain.
    private final float persistence;
	private final int iterations; // Use a value of 1 to get very smooth rolling hills.  No need to go higher than 4.
    private final int water_level = (int) (256 * .3f);
    private final int stone_level = (int) (256 * .5f);
    private final int snow_level = (int) (256 * .6f);
    private final int ice_level = (int) (256 * .75f);

	private Random rand;
	
	public Simplex2DFeature(long seed)
	{
		this(seed, 0.009f, 0.33f, 4);
	}
	
	public Simplex2DFeature(long seed, float simplexScale, float persistence, int iterations)
	{
		this.rand = new Random(seed);
		this.simplexScale = simplexScale;
		this.persistence = persistence;
		this.iterations = iterations;
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, int[][][] blockTypes, boolean[][][] blocks_IsOnSurface)
	{
		int[][] height = new int[16][16];
		int xOffset = chunkX*16;
		int zOffset = chunkZ*16;
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				blockTypes[x][0][z] = Block.bedrock.blockId;
				Double c = sumOctave(iterations, x+xOffset, z+zOffset, persistence, simplexScale);
				c = normalize(c, 1, 256);
				height[x][z] = MathUtils.floor_double(c);
				for (int y = 1; y < c; y++)
				{
                   int blockToPlace = Block.grass.blockId;
                   double randomNumber = rand.nextDouble() * 5000 ;
                   if(randomNumber < 1000)
                   {
                       blockToPlace = Block.smoothStone.blockId;
                   }
                   if(randomNumber < 500)
                   {
                       blockToPlace = Block.gravel.blockId;
                   }
                   if(randomNumber < 350)
                   {
                       blockToPlace = Block.coal.blockId;
                   }
                   if(randomNumber < 250)
                   {
                       blockToPlace = Block.iron.blockId;
                   }
                   if(randomNumber < 50 && y < 30)
                   {
                       blockToPlace = Block.gold.blockId;
                   }
                   if(randomNumber < 10 && y < 16)
                   {
                       blockToPlace = Block.diamond.blockId;
                   }
                   blockTypes[x][y][z] = blockToPlace;
				}
                if(c<water_level) // water
                {
                    for(int y=c.intValue()+1; y<=water_level; y++)
                    {
                    	blockTypes[x][y][z] = Block.water.blockId;
                    }
                }
                for(int y = c.intValue()-2; y <= c.intValue(); y++)
                {
                    int place = Block.grass.blockId;
                    if(c>stone_level)
                    {
                        place = Block.smoothStone.blockId;
                    }
                    if(c>snow_level)
                    {
                        place = Block.snow.blockId;
                    }
                    if(c>ice_level)
                    {
                        place = Block.ice.blockId;
                    }
                    blockTypes[x][y][z] = place;
                }
			}
		}
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				int heightVal = height[x][z];
				blocks_IsOnSurface[x][heightVal][z]	= true;
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
}