package com.chappelle.jcraft.world.chunk;

/**
 * Implementors generate terrain features like trees, tall grass, and plants. A feature may
 * also generate the basic terrain for the world
 */
public interface Feature
{
	/**
	 * Generates the terrain for this feature
	 * @param blockTypes The array of block types in a chunk
	 * @param blocks_IsOnSurface The surface flags
	 */
	void generate(int[][][] blockTypes, boolean[][][] blocks_IsOnSurface);
}
