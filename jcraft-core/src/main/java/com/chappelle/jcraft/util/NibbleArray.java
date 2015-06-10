package com.chappelle.jcraft.util;

public class NibbleArray
{
	/**
	 * Byte array of data stored in this holder. Possibly a light map or some
	 * chunk data. Data is accessed in 4-bit pieces.
	 */
	public final byte[] data;

	/**
	 * Log base 2 of the chunk height (128); applied as a shift on Z coordinate
	 */
	private final int depthBits;

	/**
	 * Log base 2 of the chunk height (128) * width (16); applied as a shift on
	 * X coordinate
	 */
	private final int depthBitsPlusFour;

	public NibbleArray(int length, int depthBits)
	{
		this.data = new byte[length >> 1];
		this.depthBits = depthBits;
		this.depthBitsPlusFour = depthBits + 4;
	}

	public NibbleArray(byte[] data, int depthBits)
	{
		this.data = data;
		this.depthBits = depthBits;
		this.depthBitsPlusFour = depthBits + 4;
	}

	/**
	 * Returns the nibble of data corresponding to the passed in x, y, z. y is
	 * at most 6 bits, z is at most 4.
	 */
	public int get(int x, int y, int z)
	{
		int l = y << this.depthBitsPlusFour | z << this.depthBits | x;
		int i1 = l >> 1;
		int j1 = l & 1;
		return j1 == 0 ? this.data[i1] & 15 : this.data[i1] >> 4 & 15;
	}

	/**
	 * Arguments are x, y, z, val. Sets the nibble of data at x << 11 | z << 7 |
	 * y to val.
	 */
	public void set(int x, int y, int z, int val)
	{
		int i1 = y << this.depthBitsPlusFour | z << this.depthBits | x;
		int j1 = i1 >> 1;
		int k1 = i1 & 1;

		if (k1 == 0)
		{
			this.data[j1] = (byte) (this.data[j1] & 240 | val & 15);
		} else
		{
			this.data[j1] = (byte) (this.data[j1] & 15 | (val & 15) << 4);
		}
	}
	
	public static void main(String[] args)
	{
		int x = 8;
		System.out.println(x >> 1);
		System.out.println(x << 1);
//		NibbleArray a = new NibbleArray(16, 4);
//		a.set(1, 1, 1, 10);
//		
//		System.out.println(a.get(1, 1, 1));
		
	}
}
