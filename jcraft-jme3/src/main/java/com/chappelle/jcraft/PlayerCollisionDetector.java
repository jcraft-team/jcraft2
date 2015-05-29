package com.chappelle.jcraft;

import com.cubes.Block;
import com.cubes.CubesSettings;
import com.cubes.Vector3Int;
import com.cubes.World;
import com.jme3.math.Vector3f;

public class PlayerCollisionDetector
{
	private World world;
	private float blockSize;

	public float xPen = 0, yPen = 0, zPen = 0; // penetration vector
	public boolean penetrating = false;
	private Vector3f corner1;
	private Vector3f corner2;
	private Vector3f corner3;
	private Vector3f corner4;
	private Vector3f corner5;
	private Vector3f corner6;
	private Vector3f corner7;
	private Vector3f corner8;
	
	public PlayerCollisionDetector(World world, CubesSettings cubesSettings)
	{
		this.world = world;
		
		blockSize = cubesSettings.getBlockSize();
		float extents = cubesSettings.getBlockSize()/4;
		
		//TOP
		corner1 = new Vector3f(-extents, blockSize, -extents);
		corner2 = new Vector3f(-extents, blockSize, extents);
		corner3 = new Vector3f(extents, blockSize, -extents);
		corner4 = new Vector3f(extents, blockSize, extents);

		//BOTTOM
		corner5 = new Vector3f(-extents, -blockSize, -extents);
		corner6 = new Vector3f(-extents, -blockSize, extents);
		corner7 = new Vector3f(extents, -blockSize, -extents);
		corner8 = new Vector3f(extents, -blockSize, extents);
	}
	
	public void calculate(Vector3f playerLocation)
	{
		xPen = 0;
		yPen = 0;
		zPen = 0;
		penetrating = false;
		
		calculateCorner(playerLocation, corner1);
		calculateCorner(playerLocation, corner2);
		calculateCorner(playerLocation, corner3);
		calculateCorner(playerLocation, corner4);
		calculateCorner(playerLocation, corner5);
		calculateCorner(playerLocation, corner6);
		calculateCorner(playerLocation, corner7);
		calculateCorner(playerLocation, corner8);
	}

	private void calculateCorner(Vector3f playerLocation, Vector3f cornerOffset)
	{
		Vector3f corner = playerLocation.add(cornerOffset);
		Vector3Int cell = Vector3Int.fromVector3f(corner.divide(blockSize));
		Block block = world.getBlock(cell);
		if(block != null && block.isSolid())
		{
			cell.multLocal((int)blockSize);//Convert cell to world coordinates
			
			float x = cornerOffset.x < 0 ? cell.x + blockSize - corner.x : corner.x - cell.x;
		    float y = cornerOffset.y < 0 ? cell.y + blockSize - corner.y : corner.y - cell.y;
		    float z = cornerOffset.z < 0 ? cell.z + blockSize - corner.z : corner.z - cell.z;
		    // Update xPen, yPen, and zPen only if abs(n) < abs(nPen)
		    if(xPen == 0 || Math.abs(x) < Math.abs(xPen)) 
	    	{
		    	xPen = x;
	    	}
		    if(yPen == 0 || Math.abs(y) < Math.abs(yPen)) 
		    {
		    	yPen = y;
		    }
		    if(zPen == 0 || Math.abs(z) < Math.abs(zPen)) 
		    {
		    	zPen = z;
		    }
		    penetrating = true;
		}
	}
	
	@Override
	public String toString()
	{
		return "Collision: [" + "xPen=" + xPen + " yPen=" + yPen + " zPen=" + zPen + "]";
	}
}
