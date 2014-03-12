package com.rs3e.network.protocol.packets;

public final class PacketReader extends HeapBuffer {

	/**
	 * Constructs a new {@code PsycInBuffer.java}.
	 * 
	 * @param buffer
	 *            The buffer used to allocate bytes.
	 */
	public PacketReader(byte[] buffer) {
		super(buffer);
	}

	public int getBits(int bitOffset) {
		int bytePos = bitPos >> 1779819011;
		int i_8_ = -(0x7 & bitPos) + 8;
		bitPos += bitOffset;
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

	public int get24BitInt() {
		return (getUnsignedByte() << 16) + (getUnsignedByte() << 8)
				+ (getUnsignedByte());
	}

	public void setOffset(int offset) {
		this.pos = offset;
	}

	public int getRemaining() {
		return pos < buffer.length ? buffer.length - pos : 0;
	}

	public void addBytes(byte[] b, int offset, int length) {
		ensureCompression(length - offset);
		System.arraycopy(b, offset, buffer, this.pos, length);
	}

	public int getPacket() {
		int id = 0xff & getUnsignedByte();
		if (id < 128)
			return id;
		return (id - 128 << 8) + (getUnsignedByte());
	}

	@Override
	public int get() {
		return getRemaining() > 0 ? buffer[pos++] : 0;
	}

	public void getBytes(byte buffer[], int off, int len) {
		for (int k = off; k < len + off; k++) {
			buffer[k] = (byte) get();
		}
	}

	public void getBytes(byte buffer[]) {
		getBytes(buffer, 0, buffer.length);
	}

	public int getSmart2() {
		int var2 = 0;

		int var3;
		for (var3 = getSmart(); var3 == 32767; var3 = getSmart()) {
			var2 += 32767;
		}

		var2 += var3;
		return var2;
	}

	public int getUnsignedByte() {
		return get() & 0xff;
	}

	public int getByteA() {
		return (byte) (get() - 128);
	}

	public int getByteC() {
		return (byte) -get();
	}

	public int getByteS() {
		return (byte) (128 - get());
	}

	public int getUnsignedByte128() {
		return getUnsignedByte() - 128 & 0xff;
	}

	public int getUnsignedByteC() {
		return -getUnsignedByte() & 0xff;
	}

	public int getUnsigned128Byte() {
		return 128 - getUnsignedByte() & 0xff;
	}

	public int getLEShort() {
		int i = getUnsignedByte() + (getUnsignedByte() << 8);
		if (i > 32767) {
			i -= 0x10000;
		}
		return i;
	}

	public int getShortA() {
		int i = (getUnsignedByte() << 8) + (get() - 128 & 0xff);
		if (i > 32767) {
			i -= 0x10000;
		}
		return i;
	}

	public int getLEShortA() {
		int i = (get() - 128 & 0xff) + (getUnsignedByte() << 8);
		if (i > 32767) {
			i -= 0x10000;
		}
		return i;
	}

	public int get128ShortLE() {
		int i = (128 - get() & 0xff) + (getUnsignedByte() << 8);
		if (i > 32767) {
			i -= 0x10000;
		}
		return i;
	}

	public int getShort() {
		int i = (getUnsignedByte() << 8) + getUnsignedByte();
		if (i > 32767) {
			i -= 0x10000;
		}
		return i;
	}

	public int getUnsignedShortLE() {
		return getUnsignedByte() + (getUnsignedByte() << 8);
	}

	public int getUnsignedShort() {
		return (getUnsignedByte() << 8) + getUnsignedByte();
	}

	public int getBigSmart() {
		if ((buffer[pos] ^ 0xffffffff) <= -1) {
			int value = getUnsignedShort();
			if (value == 32767) {
				return -1;
			}
			return value;
		}
		return getInt() & 0x7fffffff;
	}

	public int getUnsignedShortA() {
		return (getUnsignedByte() << 8) + (get() - 128 & 0xff);
	}

	public int getShortLE() {
		return (get()) + (get() << 8);
	}

	public int getUnsignedLEShortA() {
		return (get() - 128 & 0xff) + (getUnsignedByte() << 8);
	}

	public int getInt() {
		return (getUnsignedByte() << 24) + (getUnsignedByte() << 16)
				+ (getUnsignedByte() << 8) + getUnsignedByte();
	}

	public int getIntV1() {
		return (getUnsignedByte() << 8) + getUnsignedByte()
				+ (getUnsignedByte() << 24) + (getUnsignedByte() << 16);
	}

	public int getIntV2() {
		return (getUnsignedByte() << 16) + (getUnsignedByte() << 24)
				+ getUnsignedByte() + (getUnsignedByte() << 8);
	}

	public int getLEInt() {
		return getUnsignedByte() + (getUnsignedByte() << 8)
				+ (getUnsignedByte() << 16) + (getUnsignedByte() << 24);
	}

	public long getLong() {
		long l = getInt() & 0xffffffffL;
		long l1 = getInt() & 0xffffffffL;
		return (l << 32) + l1;
	}

	/*
	 * public String getString() { String s = ""; int b; while ((b = getByte())
	 * != 0) { s += (char) b; } return s; }
	 */

	public String getString() {
		StringBuilder s = new StringBuilder();
		int b;
		while ((b = get()) != 0)
			s.append((char) b);
		return s.toString();
	}

	public String getJagString() {
		get();
		String s = "";
		int b;
		while ((b = get()) != 0) {
			s += (char) b;
		}
		return s;
	}

	public int getUnsignedSmart() {
		int i = 0xff & buffer[pos];
		if (i >= 128) {
			return -32768 + getUnsignedShort();
		}
		return getUnsignedByte();
	}

	public int getTriByte() {
		return (get() << 16) + (get() << 8) + get() & 0xFF;
	}

	public int getSmart() {
		int var2 = get() & 0xFF;
		if (var2 < 128) {
			return this.getUnsignedByte();
		} else {
			return this.getUnsignedShort() - 32768;
		}
	}

	public int getLargeSmart() {
		if (this.buffer[pos] < 0) {
			return this.getInt() & Integer.MAX_VALUE;
		} else {
			int var2 = this.getUnsignedShort();
			return var2 == 32767 ? -1 : var2;
		}
	}

}