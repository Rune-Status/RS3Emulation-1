package net.ieldor.io;

//import com.rs.game.player.Player;

public class InputStream extends Stream {

	public void initBitAccess() {
		bitPosition = offset * 8;
	}

	private static final int[] BIT_MASK = new int[] { 0, 1, 3, 7, 15, 31, 63,
			127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071,
			262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215,
			33554431, 67108863, 134217727, 268435455, 536870911, 1073741823,
			2147483647, -1 };

	public void finishBitAccess() {
		offset = (7 + bitPosition) / 8;
	}

	public int readBits(int bitOffset) {

		int bytePos = bitPosition >> 1779819011;
		int i_8_ = -(0x7 & bitPosition) + 8;
		bitPosition += bitOffset;
		int value = 0;
		for (/**/; (bitOffset ^ 0xffffffff) < (i_8_ ^ 0xffffffff); i_8_ = 8) {
			value += (BIT_MASK[i_8_] & buffer[bytePos++]) << -i_8_ + bitOffset;
			bitOffset -= i_8_;
		}
		if ((i_8_ ^ 0xffffffff) == (bitOffset ^ 0xffffffff))
			value += buffer[bytePos] & BIT_MASK[i_8_];
		else
			value += (buffer[bytePos] >> -bitOffset + i_8_ & BIT_MASK[bitOffset]);
		return value;
	}

	public InputStream(int capacity) {
		buffer = new byte[capacity];
	}

	public InputStream(byte[] buffer) {
		this.buffer = buffer;
		this.length = buffer.length;
	}

	public void checkCapacity(int length) {
		if (offset + length >= buffer.length) {
			byte[] newBuffer = new byte[(offset + length) * 2];
			System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
			buffer = newBuffer;
		}
	}

	public int read24BitInt() {
		return (readUnsignedByte() << 16) + (readUnsignedByte() << 8)
				+ (readUnsignedByte());
	}

	public void skip(int length) {
		offset += length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getRemaining() {
		return offset < length ? length - offset : 0;
	}

	public void addBytes(byte[] b, int offset, int length) {
		checkCapacity(length - offset);
		System.arraycopy(b, offset, buffer, this.offset, length);
		this.length += length - offset;
	}
	
	public byte peek (int pos) {
		return buffer[pos];
	}

	/*public int readPacket(Player player) {
		int id = 0xff & readUnsignedByte() - player.getIsaacKeyPair().inKey().getNextValue();
		//System.out.println("Packet ID="+id);
		if(id < 128) {
			return id;
		}
		return (id - 128 << 8) + (readUnsignedByte() - player.getIsaacKeyPair().inKey().getNextValue());
	}*/

	/**
	 * Reads a byte.
	 * @return A single byte.
	 */
	public byte readByte() {
		return getRemaining() > 0 ? buffer[offset++] : 0;
	}

	/**
	 * Reads a type A byte.
	 * @return A type A byte.
	 */
	public byte readByteA() {//Aka readByte128
		return (byte) (readByte() - 128);
	}
	
	/**
	 * Reads a type C byte.
	 * @return A type C byte.
	 */
	public byte readByteC() {
		return (byte) -readByte();
	}

	/**
	 * Gets a type S byte.
	 * @return A type S byte.
	 */
	public byte readByteS() {//Aka read128Byte()
		return (byte) (128 - readByte());
	}

	/**
	 * Reads an unsigned byte.
	 * @return An unsigned single byte.
	 */
	public int readUnsignedByte() {
		return readByte() & 0xff;
	}

	/**
	 * Reads an unsigned type A byte.
	 * @return An unsigned type A byte.
	 */
	public int readUnsignedByteA() {
		return readUnsignedByte() - 128 & 0xff;
	}

	/**
	 * Reads an unsigned type C byte.
	 * @return An unsigned type C byte.
	 */
	public int readUnsignedByteC() {
		return -readUnsignedByte() & 0xff;
	}

	/**
	 * Reads an unsigned type S byte.
	 * @return An unsigned type S byte.
	 */
	public int readUnsignedByteS() {
		return 128 - readUnsignedByte() & 0xff;
	}

	/**
	 * Reads an integer.
	 * @return An integer.
	 */
	public int readInt() {
		return (readUnsignedByte() << 24) | (readUnsignedByte() << 16)
				| (readUnsignedByte() << 8) | readUnsignedByte();
	}

	/**
	 * Reads a V1 integer.
	 * @return A V1 integer.
	 */
	public int readIntV1() {
		return (readUnsignedByte() << 8) | readUnsignedByte()
				| (readUnsignedByte() << 24) | (readUnsignedByte() << 16);
	}

	/**
	 * Reads a V2 integer.
	 * @return A V2 integer.
	 */
	public int readIntV2() {
		return (readUnsignedByte() << 16) | (readUnsignedByte() << 24)
				| readUnsignedByte() | (readUnsignedByte() << 8);
	}

	/**
	 * Reads a little endian integer.
	 * @return A little endian.
	 */
	public int readIntLE() {
		return readUnsignedByte() + (readUnsignedByte() << 8)
				+ (readUnsignedByte() << 16) + (readUnsignedByte() << 24);
	}

	/**
	 * Reads a short.
	 * @return A short.
	 */
	public short readShort() {
		int i = (readUnsignedByte() << 8) + readUnsignedByte();
		if (i > 32767) {
			i -= 0x10000;
		}
		return (short) i;
	}

	/**
	 * Reads an unsigned short.
	 * @return An unsigned short.
	 */
	public int readUnsignedShort() {
		return (readUnsignedByte() << 8) + readUnsignedByte();
	}

	/**
	 * Reads a little-endian short.
	 * @return A little-endian short.
	 */
	public short readLEShort() {
		int i = readUnsignedByte() + (readUnsignedByte() << 8);
		if (i > 32767) {
			i -= 0x10000;
		}
		return (short) i;
	}

	/**
	 * Reads an unsigned little-endian short.
	 * @return An unsigned little-endian short.
	 */
	public int readUnsignedLEShort() {
		return readUnsignedByte() + (readUnsignedByte() << 8);
	}

	/**
	 * Reads a type A short.
	 * @return A type A short.
	 */
	public int readShortA() {
		int i = (readUnsignedByte() << 8) + (readByte() - 128 & 0xff);
		if (i > 32767) {
			i -= 0x10000;
		}
		return i;
	}

	/**
	 * Reads an unsigned type A short.
	 * @return An unsigned type A short.
	 */
	public int readUnsignedShortA() {
		return (readUnsignedByte() << 8) + (readByte() - 128 & 0xff);
	}

	/**
	 * Reads a little-endian type A short.
	 * @return A little-endian type A short.
	 */
	public short readLEShortA() {
		int i = (readByte() - 128 & 0xff) + (readUnsignedByte() << 8);
		if (i > 32767) {
			i -= 0x10000;
		}
		return (short) i;
	}

	/**
	 * Reads an unsigned little-endian type A short.
	 * @return An unsigned little-endian type A short.
	 */
	public int readUnsignedLEShortA() {
		return (readByte() - 128 & 0xff) + (readUnsignedByte() << 8);
	}

	/**
	 * Reads a type S short.
	 * @return A type S short.
	 */
	public int readLEShortS() {
		int i = (128 - readByte() & 0xff) + (readUnsignedByte() << 8);
		if (i > 32767) {
			i -= 0x10000;
		}
		return i;
	}

	/**
	 * Reads an unsigned little-endian type S short.
	 * @return An unsigned little-endian type S short.
	 */
	public int readUnsignedLEShortS() {
		return (128 - readByte() & 0xff) + (readUnsignedByte() << 8);
	}	

	/**
	 * Reads a series of bytes.
	 * @param is The target byte array.
	 * @param offset The offset.
	 * @param length The length.
	 */
	public void readBytes(byte[] buffer, int off, int len) {
		for (int k = off; k < len + off; k++) {
			buffer[k] = (byte) readByte();
		}
	}

	/**
	 * Reads several bytes.
	 * @param b The target array.
	 */
	public void readBytes(byte[] buffer) {
		readBytes(buffer, 0, buffer.length);
	}
	
	/**
	 * Reads a smart.
	 * @return The smart.
	 */
	public int readSmart () {
		int peek = peek(getPosition());
		if (peek < 128) {
			return readUnsignedByte();
		} else {
			return ((readShort() & 0xFFFF) - 49152);
		}
	}

	public int readSmart2() {
		int i = 0;
		int i_33_ = readUnsignedSmart();
		while (i_33_ == 32767) {
			i_33_ = readUnsignedSmart();
			i += 32767;
		}
		i += i_33_;
		return i;
	}

	// @SuppressWarnings("unused")
	public int readBigSmart() {
		/*
		 * if(Settings.CLIENT_BUILD < 670) return readUnsignedShort();
		 */
		if ((buffer[offset] ^ 0xffffffff) <= -1) {
			int value = readUnsignedShort();
			if (value == 32767) {
				return -1;
			}
			return value;
		}
		return readInt() & 0x7fffffff;
	}

	public long readLong() {
		long l = readInt() & 0xffffffffL;
		long l1 = readInt() & 0xffffffffL;
		return (l << 32) + l1;
	}

	/*
	 * public String readString() { String s = ""; int b; while ((b =
	 * readByte()) != 0) { s += (char) b; } return s; }
	 */

	public String readString() {
		StringBuilder s = new StringBuilder();
		int b;
		while ((b = readByte()) != 0)
			s.append((char) b);
		return s.toString();
	}

	public String readJagString() {
		readByte();
		String s = "";
		int b;
		while ((b = readByte()) != 0) {
			s += (char) b;
		}
		return s;
	}

	public int readUnsignedSmart() {
		int i = 0xff & buffer[offset];
		if (i >= 128) {
			return -32768 + readUnsignedShort();
		}
		return readUnsignedByte();
	}

}