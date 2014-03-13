package net.ieldor.io;

import io.netty.buffer.ByteBuf;
import net.ieldor.io.Packet.PacketType;
import net.ieldor.utility.ByteBufUtils;

/**
 * @author Sean
 * @author Graham (For the reading methods that i used from Hyperion)
 */
public final class PacketReader {

	/**
	 * The packet type.
	 */
	private final PacketType type;
	
	/**
	 * The packet opcode.
	 */
	private final int opcode;
	
	/**
	 * The packet payload.
	 */
	private final ByteBuf payload;
	
	/**
	 * The length of the payload;
	 */
	private final int length;
	
	/**
	 * Creates a new PacketReader
	 * @param packet The packet to be read.
	 */
	public PacketReader(Packet packet) {
		this.opcode = packet.getOpcode();
		this.type = packet.getType();
		this.payload = packet.getPayload();
		this.length = packet.getLength();
	}
	
	/**
	 * Reads a single byte.
	 * @return A single byte.
	 */
	public byte get() {
		return payload.readByte();
	}
	
	/**
	 * Reads several bytes.
	 * @param b The target array.
	 */
	public void get(byte[] b) {
		payload.readBytes(b);
	}
	
	/**
	 * Reads a series of bytes.
	 * @param is The target byte array.
	 * @param offset The offset.
	 * @param length The length.
	 */
	public void get(byte[] is, int offset, int length) {
		for(int i = 0; i < length; i++) {
			is[offset + i] = payload.readByte();
		}
	}
	
	/**
	 * Reads a byte.
	 * @return A single byte.
	 */
	public byte getByte() {
		return get();
	}
	
	/**
	 * Reads a type A byte.
	 * @return A type A byte.
	 */
	public byte getByteA() {
		return (byte) (get() - 128);
	}
	
	/**
	 * Reads a type C byte.
	 * @return A type C byte.
	 */
	public byte getByteC() {
		return (byte) (-get());
	}
	
	/**
	 * Gets a type S byte.
	 * @return A type S byte.
	 */
	public byte getByteS() {
		return (byte) (128 - get());
	}
	
	/**
	 * Reads an integer.
	 * @return An integer.
	 */
	public int getInt() {
		return payload.readInt();
	}

	/**
	 * Reads a V1 integer.
	 * @return A V1 integer.
	 */
	public int getInt1() {
		return ((payload.readByte() & 0xff) << 8)
				| (payload.readByte() & 0xff)
				| ((payload.readByte() & 0xff) << 24)
				| ((payload.readByte() & 0xff) << 16);
	}
	
	/**
	 * Reads a V2 integer.
	 * @return A V2 integer.
	 */	
	public int getInt2() {
		return ((payload.readByte() & 0xff) << 16)
				| ((payload.readByte() & 0xff) << 24)
				| (payload.readByte() & 0xff)
				| ((payload.readByte() & 0xff) << 8);
	}
	
	/**
	 * Reads a little endian integer.
	 * @return A V1 integer.
	 */
	public int getLEInt() {
		return (payload.readByte() & 0xff)
				| ((payload.readByte() & 0xff) << 8)
				| ((payload.readByte() & 0xff) << 16)
				| ((payload.readByte() & 0xff) << 24);
	}

	/**
	 * Gets the length of the buffer.
	 * @return The length of the buffer.
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Reads a little-endian short.
	 * @return A little-endian short.
	 */
	public short getLEShort() {
		int i = (payload.readByte() & 0xFF) | ((payload.readByte() & 0xFF) << 8);
		if(i > 32767)
			i -= 0x10000;
		return (short) i;
	}
	
	/**
	 * Reads a little-endian type A short.
	 * @return A little-endian type A short.
	 */
	public short getLEShortA() {
		int i = (payload.readByte() - 128 & 0xFF) | ((payload.readByte() & 0xFF) << 8);
		if(i > 32767)
			i -= 0x10000;
		return (short) i;
	}
	
	/**
	 * Reads a long.
	 * @return A long.
	 */
	public long getLong() {
		return payload.readLong();
	}
	
	
	/**
	 * Gets the opcode.
	 * @return the opcode
	 */
	public int getOpcode() {
		return opcode;
	}

	/**
	 * Reads a series of bytes in reverse.
	 * @param is The target byte array.
	 * @param offset The offset.
	 * @param length The length.
	 */
	public void getReverse(byte[] is, int offset, int length) {
		for(int i = (offset + length - 1); i >= offset; i--) {
			is[i] = payload.readByte();
		}
	}
	
	/**
	 * Reads a series of type A bytes in reverse.
	 * @param is The target byte array.
	 * @param offset The offset.
	 * @param length The length.
	 */
	public void getReverseA(byte[] is, int offset, int length) {
		for(int i = (offset + length - 1); i >= offset; i--) {
			is[i] = getByteA();
		}
	}
	
	/**
	 * Reads a short.
	 * @return A short.
	 */
	public short getShort() {
		return payload.readShort();
	}
	
	/**
	 * Reads a type A short.
	 * @return A type A short.
	 */
	public short getShortA() {
		return (short) ((get() << 8) + (get() - 128 & 0xff));
	}
	
	/**
	 * Reads a type A short.
	 * @return A type A short.
	 */
	public short getSignedShortA() {
		int i = ((payload.readByte() & 0xFF) << 8) | (payload.readByte() - 128 & 0xFF);
		if(i > 32767)
			i -= 0x10000;
		return (short) i;
	}
	
	/**
	 * Gets a signed smart.
	 * @return The signed smart.
	 */
	public int getSignedSmart() {
		int peek = payload.getByte(payload.readerIndex());
		if(peek < 128) {
			return ((get() & 0xFF) - 64);
		} else {
			return ((getShort() & 0xFFFF) - 49152);
		}
	}
	
	/**
	 * Gets a smart.
	 * @return The smart.
	 */
	public int getSmart() {
		int peek = payload.getByte(payload.readerIndex());
		if(peek < 128) {
			return (get() & 0xFF);
		} else {
			return (getShort() & 0xFFFF) - 32768;
		}
	}
	
	/**
	 * Gets a string from the buffer.
	 * @return The string.
	 */
	public String getString() {
		return ByteBufUtils.readString(payload);
	}
	
	/**
	 * Gets a 3-byte integer.
	 * @return The 3-byte integer.
	 */
	public int getTriByte() {
		return ((payload.readByte() << 16) & 0xFF) | ((payload.readByte() << 8) & 0xFF) | (payload.readByte() & 0xFF);
	}

	/**
	 * Gets the packet type.
	 * @return the type.
	 */
	public PacketType getType() {
		return type;
	}

	/**
	 * Reads an unsigned byte.
	 * @return An unsigned byte.
	 */
	public int getUnsignedByte() {
		return payload.readUnsignedByte();
	}
	
	/**
	 * Reads an unsigned short.
	 * @return An unsigned short.
	 */
	public int getUnsignedShort() {
		return payload.readUnsignedShort();
	}

	/**
	 * Returns the remaining payload data of this packet.
	 *
	 * @return The payload <code>byte</code> array
	 */
	public byte[] getRemainingData() {
		byte[] data = new byte[this.getLength() - this.payload.readerIndex()];
		for(int i = 0;i < data.length;i++) {
			data[i] = this.payload.getByte(i + this.payload.readerIndex());
		}
		this.payload.markReaderIndex();
		return data;
	}
}
