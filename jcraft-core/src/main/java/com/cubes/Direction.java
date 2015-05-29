package com.cubes;

public enum Direction
{
	LEFT(new Vector3Int(-1, 0, 0)), 
	RIGHT(new Vector3Int(1, 0, 0)), 
	FRONT(new Vector3Int(0, 0, 1)), 
	BACK(new Vector3Int(0, 0, -1)), 
	UP(new Vector3Int(0, 1, 0)), 
	DOWN(new Vector3Int(0, -1, 0)); 
	
	public Vector3Int vector;
	
	private Direction(Vector3Int v)
	{
		this.vector = v;
	}
	
	public Vector3Int getVector()
	{
		return vector;
	}
}