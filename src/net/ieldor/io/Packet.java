package net.ieldor.io;

import io.netty.buffer.ByteBuf;

/**
 * @author Sean
 */
public final class Packet {

	/**
	 * @author Sean
	 */
	public enum AccessMode {
		/**
		 * Byte access allows you to access and modify individual bytes directly in this buffer.
		 */
		BYTE_ACCESS, 
		
		/**
		 * Bit access allows you to access and modify individual bits (which are packed into bytes) directly in this buffer.
		 */
		BIT_ACCESS;
	}
	
	/**
	 * The type of packet.
	 * @author Sean
	 */
	public enum PacketType {
		
		FIXED(0), BYTE(1), SHORT(2);

		/**
		 * The size of the packet type.
		 */
		private final int value;

		/**
		 * Creates a new PacketType.
		 * @param value The size of the packet type.
		 */
		PacketType(int value) {
			this.value = value;
		}

		/**
		 * Gets the size of the packet type.
		 * @return the value.
		 */
		public int getValue() {
			return value;
		}
		
	}
	
	/**
	 * The opcode of the packet.
	 */
	private final int opcode;
	
	/**
	 * The amount of bytes in the buffer.
	 */
	private final int length; 
	
	/**
	 * The type.
	 */
	private final PacketType type;
	
	/**
	 * The payload.
	 */
	private final ByteBuf payload;
	
	/**
	 * Creates a packet.
	 * @param opcode The opcode.
	 * @param type The type.
	 * @param payload The payload.
	 */
	public Packet(int opcode, PacketType type, ByteBuf payload) {
		this.opcode = opcode;
		this.type = type;
		this.payload = payload;
		this.length = payload.readableBytes();
	}
	
	/**
	 * Gets the length.
	 * @return the length.
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Gets the opcode.
	 * @return The opcode.
	 */
	public int getOpcode() {
		return opcode;
	}
	
	
	/**
	 * Gets the payload of the packet.
	 * @return The payload.
	 */
	public ByteBuf getPayload() {
		return payload;
	}

	/**
	 * Gets the packet type.
	 * @return The type.
	 */
	public PacketType getType() {
		return type;
	}
	
	/**
	 * Is the buffer empty.
	 * @return true if the buffer is empty, false if not.
	 */
	public boolean isEmpty() {
		return length == 0;
	}
	
	/**
	 * Checks if the packet has no header.
	 * @return true or false.
	 */
	public boolean isHeaderless() {
		return type == null;
	}
}
