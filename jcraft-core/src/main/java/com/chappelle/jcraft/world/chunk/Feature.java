package com.chappelle.jcraft.world.chunk;

/**
 * Implementors generate terrain features like trees, tall grass, and plants. A feature may
 * also generate the basic terrain for the world
 */
public interface Feature
{
	/**
	 * Generates the terrain for this feature
	 * @param chunkX The x coordinate of the chunk
	 * @param chunkZ The y coordinate of the chunk
	 * @param blockTypes The array of block types in a chunk
	 */
	void generate(int chunkX, int chunkZ, int[][][] blockTypes);
}
