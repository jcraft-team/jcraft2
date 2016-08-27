package com.chappelle.jcraft.util;

import com.chappelle.jcraft.Enemy;
import com.chappelle.jcraft.blocks.Block;
import com.jme3.math.Vector3f;

public class RayTrace
{
	/** x coordinate of the block ray traced against */
	public int blockX;

	/** y coordinate of the block ray traced against */
	public int blockY;

	/** z coordinate of the block ray traced against */
	public int blockZ;

	/**
	 * Which side was hit. If its -1 then it went the full length of the ray
	 * trace. Bottom = 0, Top = 1, East = 2, West = 3, North = 4, South = 5.
	 */
//	public int sideHit;
	public Block.Face sideHit;

	/** The vector position of the hit */
	public Vector3f hitVec;
	
	public Enemy enemy;
	
	public RayTrace(Enemy enemy)
	{
		this.enemy = enemy;
	}
	
	public RayTrace(int blockX, int blockY, int blockZ, Block.Face sideHit, Vector3f hitVec)
	{
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		this.sideHit = sideHit;
		this.hitVec = hitVec.clone();
	}
}
