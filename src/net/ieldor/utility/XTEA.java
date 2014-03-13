package net.ieldor.utility;

import java.nio.ByteBuffer;

public class XTEA {
	
	private int[] keys;
	
	public XTEA (int[] keys) {
		this.keys = keys;
	}
	
	public byte[] decrypt(byte[] data, int offset, int length) {
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
				z -= ((y >>> 5 ^ y << 4) + y ^ sum + keys[sum >>> 11 & 0x56c00003]);
				sum -= delta;
				y -= ((z >>> 5 ^ z << 4) - -z ^ sum + keys[sum & 0x3]);
				numRounds--;
			}
			buffer.position(buffer.position() - 8);
			buffer.putInt(y);
			buffer.putInt(z);
		}
		return buffer.array();
	}
}
