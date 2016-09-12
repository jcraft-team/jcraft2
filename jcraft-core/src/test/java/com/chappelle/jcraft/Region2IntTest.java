package com.chappelle.jcraft;

import static org.junit.Assert.assertTrue;

import java.util.*;

import org.junit.Test;

public class Region2IntTest
{
	@Test
	public void testIterator()
	{
		Region2Int region = Region2Int.createFromCenterExtents(new Vector2Int(2, 2), 2);
		List<Vector2Int> locations = new ArrayList<>();
		
		for(Vector2Int location : region)
		{
			locations.add(location);
		}
		assertTrue(locations.contains(new Vector2Int(0, 0)));
		assertTrue(locations.contains(new Vector2Int(1, 0)));
		assertTrue(locations.contains(new Vector2Int(2, 0)));
		assertTrue(locations.contains(new Vector2Int(3, 0)));
		assertTrue(locations.contains(new Vector2Int(4, 0)));

		assertTrue(locations.contains(new Vector2Int(0, 1)));
		assertTrue(locations.contains(new Vector2Int(0, 2)));
		assertTrue(locations.contains(new Vector2Int(0, 3)));
		assertTrue(locations.contains(new Vector2Int(0, 4)));
		
		assertTrue(locations.contains(new Vector2Int(2, 3)));
	}
}
