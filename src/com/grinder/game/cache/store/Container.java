package com.grinder.game.cache.store;

import com.grinder.game.cache.io.InputStream;
import com.grinder.game.cache.io.OutputStream;

/**
 * A class serving as a container where we can read from and write to.
 * 
 * @author Displee
 */
public interface Container {

	/**
	 * Used to read data from an index, an archive or a file.
	 */
	public boolean read(InputStream inputStream);

	/**
	 * Write data to an index, archive or file.
	 */
	public byte[] write(OutputStream outputStream);

}