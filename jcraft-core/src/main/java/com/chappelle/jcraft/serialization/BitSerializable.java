package com.chappelle.jcraft.serialization;

import java.io.IOException;

/**
 * @author Carl
 */
public interface BitSerializable
{
	public abstract void write(BitOutputStream outputStream);

	public abstract void read(BitInputStream inputStream) throws IOException;
}