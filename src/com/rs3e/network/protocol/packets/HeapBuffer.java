package com.rs3e.network.protocol.packets;

import java.nio.ByteBuffer;

public abstract class HeapBuffer {

	/**
	 * The bit masks
	 */
	public static final int[] BIT_MASK = new int[32];

	/**
	 * The buffer to put data to
	 */
	protected byte[] buffer;

	/**
	 * The current data position
	 */
	protected int pos;

	/**
	 * The current bit position
	 */
	protected int bitPos;

	/**
	 * The opcode start
	 */
	protected int opcodeStart = 0;

	/*
	 * Populates the bit masks
	 */
	static {
		for (int i = 0; i < 32; i++)
			BIT_MASK[i] = (1 << i) - 1;
	}

	/**
	 * Constructs a new {@code XplurHeapBuffer} with a fixed byte capacity. If
	 * bytes are written over this amount, than more space will be reserved for
	 * this buffer
	 * 
	 * @param capacity
	 *            The capacity to allocate
	 */
	public HeapBuffer(int capacity) {
		buffer = new byte[capacity];
	}

	/**
	 * Constructs a new {@code XplurHeapBuffer} with a fixed amount of bytes.
	 * These bytes will be automatically allocated in this buffer without having
	 * to put them later.
	 * 
	 * @param buffer
	 *            The byte buffer to allocate in this {@code XplurHeapBuffer}
	 */
	public HeapBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	/**
	 * puts a single byte into this {@code XplurHeapBuffer}
	 * 
	 * @param value
	 *            The byte to put
	 * @param position
	 *            The position to put to
	 */
	public void put(int value, int position) {
		ensureCompression(position);
		buffer[position] = (byte) value;
	}

	/**
	 * puts a single byte into this {@code XplurHeapBuffer}
	 * 
	 * @param value
	 *            The byte to put
	 * @param position
	 *            The position to put to
	 */
	public void put(int value) {
		put(value, pos++);
	}

	/**
	 * gets a single byte from this {@code XplurHeapBuffer}
	 * 
	 * @return The byte at the current byte position
	 */
	public int get() {
		return buffer[pos++];
	}

	/**
	 * Ensures the compression of bytes in this buffer
	 * 
	 * @param position
	 *            The position to check at
	 */
	public void ensureCompression(int position) {
		if (position >= buffer.length) {
			byte[] newBuffer = new byte[position + 16];
			System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
			buffer = newBuffer;
		}
	}

	/**
	 * Skips a specified amount of bytes that are in this buffer
	 * 
	 * @param amount
	 *            The amount of bytes to skip
	 */
	public void skip(int amount) {
		pos += amount;
	}

	/**
	 * Returns the buffer of this heap buffer
	 * 
	 * @return The buffer
	 */
	public byte[] buffer() {
		return buffer;
	}

	/**
	 * @return The position
	 */
	public int getPosition() {
		return pos;
	}

	/**
	 * @return The remaining bytes
	 */
	public int remaining() {
		return buffer.length - pos;
	}

	/**
	 * Returns a in buffer with the bytes of a byte buffer.
	 * 
	 * @param buffer
	 *            The byte buffer.
	 * @return The in buffer.
	 */
	public static PacketReader allocate(ByteBuffer buffer) {
		return new PacketReader(buffer.array());
	}
}