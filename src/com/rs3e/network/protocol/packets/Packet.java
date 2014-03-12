package com.rs3e.network.protocol.packets;

public class Packet {

	/**
	 * The opcode
	 */
	private final int opcode;

	/**
	 * The buffer
	 */
	private final HeapBuffer buffer;

	/**
	 * Constructs a new {@code Packet.java}
	 * 
	 * @param opcode
	 *            The opcode of this packet
	 * @param buffer
	 *            The buffer payload
	 */
	public Packet(int opcode, HeapBuffer buffer) {
		this.opcode = opcode;
		this.buffer = buffer;
	}

	/**
	 * @return The opcode
	 */
	public int getOpcode() {
		return opcode;
	}

	/**
	 * @return The buffer
	 */
	public HeapBuffer getBuffer() {
		return buffer;
	}

}
