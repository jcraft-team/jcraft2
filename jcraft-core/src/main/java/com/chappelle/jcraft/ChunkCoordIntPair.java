package com.chappelle.jcraft;

public class ChunkCoordIntPair
{
	/** The X position of this Chunk Coordinate Pair */
	public final int chunkXPos;

	/** The Z position of this Chunk Coordinate Pair */
	public final int chunkZPos;

	public ChunkCoordIntPair(int x, int z)
	{
		this.chunkXPos = x;
		this.chunkZPos = z;
	}

	/**
	 * converts a chunk coordinate pair to an integer (suitable for hashing)
	 */
	public static long chunkXZ2Int(int x, int z)
	{
		return (long) x & 4294967295L | ((long) z & 4294967295L) << 32;
	}

	public int hashCode()
	{
		long i = chunkXZ2Int(this.chunkXPos, this.chunkZPos);
		int j = (int) i;
		int k = (int) (i >> 32);
		return j ^ k;
	}

	public boolean equals(Object par1Obj)
	{
		ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) par1Obj;
		return chunkcoordintpair.chunkXPos == this.chunkXPos && chunkcoordintpair.chunkZPos == this.chunkZPos;
	}

	public int getCenterXPos()
	{
		return (this.chunkXPos << 4) + 8;
	}

	public int getCenterZPosition()
	{
		return (this.chunkZPos << 4) + 8;
	}

	public String toString()
	{
		return "[" + this.chunkXPos + ", " + this.chunkZPos + "]";
	}
}
