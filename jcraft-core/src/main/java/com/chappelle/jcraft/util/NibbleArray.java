package com.chappelle.jcraft.util;

public class NibbleArray
{
	/**
	 * Byte array of data stored in this holder. Possibly a light map or some
	 * chunk data. Data is accessed in 4-bit pieces.
	 */
	private final byte[] data;

	public NibbleArray(int length)
	{
		this.data = new byte[length/2];
	}
	
	public int length()
	{
		return data.length*2;
	}
	
	public byte get(int index)
	{
		return (byte)(data[index/2] >> ((index) % 2 * 4) & 0xF);
	}
	
	public void set(int index, byte value)
	{
		value &= 0xF;
		data[index/2] &= (byte)(0xF << ((index + 1) % 2 * 4));
		data[index/2] |= (byte)(value << (index % 2 * 4));
	}
}
