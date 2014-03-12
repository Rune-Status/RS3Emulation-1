package com.rs3e.network.protocol.packets;

import com.rs3e.utility.JagString;

public final class PacketBuilder extends HeapBuffer {

	/**
	 * Constructs a new {@code PsycOutBuffer.java}.
	 * 
	 * @param buffer
	 */
	public PacketBuilder(byte[] buffer) {
		super(buffer);
	}

	public PacketBuilder() {
		super(new byte[16]);
	}

	public PacketBuilder(int capacity) {
		super(capacity);
	}

	public void checkCapacityPosition(int position) {
		if (position >= buffer().length) {
			byte[] newBuffer = new byte[position + 16];
			System.arraycopy(buffer(), 0, newBuffer, 0, buffer().length);
			setBuffer(newBuffer);
		}
	}

	@Override
	public void skip(int length) {
		setOffset(pos + length);
	}

	public void setOffset(int offset) {
		this.pos = offset;
	}

	public void put(byte[] b, int offset, int length) {
		checkCapacityPosition(this.pos + length - offset);
		System.arraycopy(b, offset, buffer(), this.pos, length);
		this.setOffset(this.pos + (length - offset));
	}

	public void put(byte[] b) {
		int offset = 0;
		int length = b.length;
		checkCapacityPosition(this.pos + length - offset);
		System.arraycopy(b, offset, buffer(), this.pos, length);
		this.setOffset(this.pos + (length - offset));
	}

	public void putA(byte[] data, int offset, int len) {
		for (int k = offset; k < len; k++)
			put((byte) (data[k] + 128));
	}

	public void putS(byte[] data, int offset, int len) {
		for (int k = offset; k < len; k++)
			put((byte) (-128 + data[k]));
	}

	public void putReverse(byte[] data, int offset, int len) {
		for (int i = len - 1; i >= 0; i--) {
			put((data[i]));
		}
	}

	public void putBackwards(byte[] src, int offset, int length) {
		for (int i = length - 1; i >= offset; i--) {
			put(src[i]);
		}
	}

	public void putReverseA(byte[] data, int offset, int len) {
		for (int i = len - 1; i >= 0; i--) {
			put((byte) (data[i] + 128));
		}
	}

	public void putNegativeByte(int i) {
		put(-i, pos++);
	}

	@Override
	public void put(int i, int position) {
		checkCapacityPosition(position);
		buffer()[position] = (byte) i;
	}

	public void putByteA(int i) {
		put(i + 128);
	}

	public void putByteC(int i) {
		put(-i);
	}

	public void putByteS(int i) {
		put(128 - i);
	}

	public void putLEShortA(int i) {
		put(i + 128);
		put(i >> 8);
	}

	public void putShortA(int i) {
		put(i >> 8);
		put(i + 128);
	}

	public void putSmart(int i) {
		if (i >= 128) {
			putShort(i + 32768);
		} else {
			put(i);
		}
	}

	public void putBigSmart(int i) {
		if (i >= Short.MAX_VALUE)
			putInt(i - Integer.MAX_VALUE - 1);
		else {
			putShort(i >= 0 ? i : 32767);
		}
	}

	public void putShort(int i) {
		put(i >> 8);
		put(i);
	}

	public void putLEShort(int i) {
		put(i);
		put(i >> 8);
	}

	public void putTriByte(int i) {
		put(i >> 16);
		put(i >> 8);
		put(i);
	}

	public void put24BitIntegerV2(int i) {
		put(i >> 16);
		put(i);
		put(i >> 8);
	}

	public void putInt(int i) {
		put(i >> 24);
		put(i >> 16);
		put(i >> 8);
		put(i);
	}

	public void putIntV1(int i) {
		put(i >> 8);
		put(i);
		put(i >> 24);
		put(i >> 16);
	}

	public void putIntV2(int i) {
		put(i >> 16);
		put(i >> 24);
		put(i);
		put(i >> 8);
	}

	public void putLEInt(int i) {
		put(i);
		put(i >> 8);
		put(i >> 16);
		put(i >> 24);
	}

	public void putLong(long l) {
		put((int) (l >> 56));
		put((int) (l >> 48));
		put((int) (l >> 40));
		put((int) (l >> 32));
		put((int) (l >> 24));
		put((int) (l >> 16));
		put((int) (l >> 8));
		put((int) l);
	}

	public void putPSmarts(int i) {
		if (i < 128) {
			put(i);
			return;
		}
		if (i < 32768) {
			putShort(32768 + i);
			return;
		} else {
			System.out.println("Error psmarts out of range:");
			return;
		}
	}

	public void putString(String s) {
		checkCapacityPosition(pos + s.length() + 1);
		System.arraycopy(s.getBytes(), 0, buffer(), pos, s.length());
		setOffset(pos + s.length());
		put(0);
	}

	public void sendString(String s) {
		for (byte bytes : s.getBytes()) {
			put(bytes);
		}
		put(0);
	}

	public void putJagString2(String string) {
		byte[] packed = new byte[256];
		int length = JagString.packGJString2(0, packed, string);
		put(0);
		put(packed, 0, length);
		put(0);
	}

	public void putGJString(String s) {
		put(0);
		putString(s);
	}

	public void putGJString2(String string) {
		byte[] packed = new byte[256];
		int length = JagString.packGJString2(0, packed, string);
		put(0);
		put(packed, 0, length);
		put(0);
	}

	public void putGJString3(String s) {
		put(0);
		putString(s);
		put(0);
	}

	public PacketBuilder putPacket(int id) {
		if (id >= 128) {
			put((id >> 8) + 128);
			put(id);
		} else
			put(id);
		return this;
	}

	public void putPacketVarByte(int id) {
		putPacket(id);
		put(0);
		opcodeStart = pos - 1;
	}

	public void putPacketVarShort(int id) {
		putPacket(id);
		putShort(0);
		opcodeStart = pos - 2;
	}

	public void endPacketVarByte() {
		put(pos - (opcodeStart + 2) + 1, opcodeStart);
	}

	public void endPacketVarShort() {
		int size = pos - (opcodeStart + 2);
		put(size >> 8, opcodeStart++);
		put(size, opcodeStart);
	}

	public void syncBits() {
		bitPos = pos * 8;
	}

	public void unSyncBits() {
		setOffset((bitPos + 7) / 8);
	}

	public int getBitPos(int i) {
		return 8 * i - bitPos;
	}

	public void putBits(int numBits, int value) {
		int bytePos = bitPos >> 3;
		int bitOffset = 8 - (bitPos & 7);
		bitPos += numBits;
		for (; numBits > bitOffset; bitOffset = 8) {
			checkCapacityPosition(bytePos);
			buffer()[bytePos] &= ~BIT_MASK[bitOffset];
			buffer()[bytePos++] |= value >> numBits - bitOffset
					& BIT_MASK[bitOffset];
			numBits -= bitOffset;
		}
		checkCapacityPosition(bytePos);
		if (numBits == bitOffset) {
			buffer()[bytePos] &= ~BIT_MASK[bitOffset];
			buffer()[bytePos] |= value & BIT_MASK[bitOffset];
		} else {
			buffer()[bytePos] &= ~(BIT_MASK[numBits] << bitOffset - numBits);
			buffer()[bytePos] |= (value & BIT_MASK[numBits]) << bitOffset
					- numBits;
		}
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public void put40BitInteger(long value) {
		put((byte) (value >> 32));
		put((byte) (value >> 24));
		put((byte) (value >> 16));
		put((byte) (value >> 8));
		put((byte) value);
	}

	public void put5ByteInteger(long value) {
		put((int) (value >> 32));
		putInt((int) (value & 0xffffffff));
	}

	public void put24BitInteger(int i) {
		put(i >> 16);
		put(i >> 8);
		put(i);
	}

	public void fill(byte[] bytes) {
		fill(bytes, 0, bytes.length);
	}

	public void fill(byte[] bytes, int offset, int length) {
		System.arraycopy(buffer, pos, bytes, offset, length);
	}
}