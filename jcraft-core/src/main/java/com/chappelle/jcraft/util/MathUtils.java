package com.chappelle.jcraft.util;

public final class MathUtils
{
	public static int floor_double(double val)
	{
		int intVal = (int) val;
		return val < (double) intVal ? intVal - 1 : intVal;
	}

	private MathUtils(){}
}
