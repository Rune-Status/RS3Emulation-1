package com.rs3e.utility;

import java.nio.ByteBuffer;

/**
 * @author Sean
 */
public final class XTEA {

	/**
	 * Deciphers xtea encryption.
	 * 
	 * @param key
	 *            the key.
	 * @param data
	 *            The data to decrypt.
	 * @return The deciphered data.
	 */
	public static byte[] decipher(int[] key, byte[] data) {
		return decipher(key, data, 0, data.length);
	}

	/**
	 * Deciphers the xtea encryption.
	 * 
	 * @param key
	 *            The key
	 * @param data
	 *            The data to decipher
	 * @param offset
	 *            The offset.
	 * @param length
	 *            The length of the data to dicpher.
	 * @return The deciphered data.
	 */
	public static byte[] decipher(int[] key, byte[] data, int offset, int length) {
		int numBlocks = (length - offset) / 8;
		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.position(offset);
		for (int i = 0; i < numBlocks; i++) {
			int y = buffer.getInt();
			int z = buffer.getInt();
			int sum = -957401312;
			int delta = -1640531527;
			int numRounds = 32;
			while (numRounds > 0) {
				z -= ((y >>> 5 ^ y << 4) + y ^ sum
						+ key[sum >>> 11 & 0x56c00003]);
				sum -= delta;
				y -= ((z >>> 5 ^ z << 4) - -z ^ sum + key[sum & 0x3]);
				numRounds--;
			}
			buffer.position(buffer.position() - 8);
			buffer.putInt(y);
			buffer.putInt(z);
		}
		return buffer.array();
	}
}
