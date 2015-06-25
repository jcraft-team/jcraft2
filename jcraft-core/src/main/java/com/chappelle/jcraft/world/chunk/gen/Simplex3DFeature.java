package com.chappelle.jcraft.world.chunk.gen;

import com.chappelle.jcraft.blocks.Block;
import com.chappelle.jcraft.world.chunk.Feature;

public class Simplex3DFeature implements Feature
{
    private final double simplexScale; // range from around 0.015 to around 0.001  The higher the number the more rugged and extreme the terain.
    private final float persistence;
	private final int iterations; // Use a value of 1 to get very smooth rolling hills.  No need to go higher than 4.
	private final int height;
	public Simplex3DFeature(long seed)
	{
		this(seed, 0.009f, 0.33f, 4, 50);
	}
	
	public Simplex3DFeature(long seed, float simplexScale, float persistence, int iterations, int height)
	{
		this.simplexScale = simplexScale;
		this.persistence = persistence;
		this.iterations = iterations;
		this.height = height;
	}

	@Override
	public void generate(int chunkX, int chunkZ, int[][][] blockTypes, int[][] heightMap)
	{
		int xOffset = chunkX*16;
		int zOffset = chunkZ*16;

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                for (int y = 0; y < height; y++)
                {
                    if(y == 0)
                    {
                    	blockTypes[x][0][z] = Block.bedrock.blockId;
                    }
                    else
                    {
                    	Double c = sumOctave(iterations, x+xOffset, y, z+zOffset, persistence, simplexScale);
                    	int place = Block.grass.blockId;
                    	if(c>.05)
                    	{
//                    		int b = y % 15;
//                    		if(b == 0)
//                    		{
//                    			place = Block.woolBlack.blockId;
//                    		}
//                    		if(b == 1)
//                    		{
//                    			place = Block.snow.blockId;
//                    		}
//                    		if(b == 2)
//                    		{
//                    			place = Block.woolBlue.blockId;
//                    		}
//                    		if(b == 3)
//                    		{
//                    			place = Block.woolRed.blockId;
//                    		}
//                    		if(b == 4)
//                    		{
//                    			place = Block.woolCyan.blockId;
//                    		}
//                    		if(b == 5)
//                    		{
//                    			place = Block.woolGray.blockId;
//                    		}
//                    		if(b == 6)
//                    		{
//                    			place = Block.woolLightBlue.blockId;
//                    		}
//                    		if(b == 7)
//                    		{
//                    			place = Block.woolLime.blockId;
//                    		}
//                    		if(b == 8)
//                    		{
//                    			place = Block.woolMagenta.blockId;
//                    		}
//                    		if(b == 9)
//                    		{
//                    			place = Block.woolOrange.blockId;
//                    		}
//                    		if(b == 10)
//                    		{
//                    			place = Block.woolPink.blockId;
//                    		}
//                    		if(b == 11)
//                    		{
//                    			place = Block.woolPurple.blockId;
//                    		}
//                    		if(b == 12)
//                    		{
//                    			place = Block.woolSilver.blockId;
//                    		}
//                    		if(b == 13)
//                    		{
//                    			place = Block.woolWhite.blockId;
//                    		}
                    		blockTypes[x][y][z] = place;
                    	}
//                    	if(c > .4)
//                    	{
//                    		blockTypes[x][y][z] = Block.gold.blockId;
//                    	}
//                    	if(c > .7 )
//                    	{
//                    		blockTypes[x][y][z] = Block.diamond.blockId;
//                    	}
                    }
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
            noise += SimplexNoise.noise3D(x, y, z, scale) * amp;
            maxAmp += amp;
            amp *= persistence;
            scale *= 2;
        }

        //take the average value of the iterations
        noise /= maxAmp;

        return noise;
    }
}
