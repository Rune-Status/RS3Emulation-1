package net.ieldor.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.ieldor.io.Packet.AccessMode;
import net.ieldor.io.Packet.PacketType;

/**
 * @author Sean
 * @author Graham (For the put methods that i used from Hyperion)
 */
public final class PacketBuf {

	/**
	 * The maximum bits that can be assigned to the buffer.
	 */
	private static final int MAX_BITS = 32;
	
	/**
	 *  TheBit mask array.
	 */
	public static final int[] BIT_MASKS = new int[MAX_BITS];
	
	/**
	 * Populates the bit mask array.
	 */
	static {
		for(int i = 0; i < BIT_MASKS.length; i++) {
			BIT_MASKS[i] = (1 << i) - 1;
		}
	}
	
	/**
	 * The packet opcode
	 */
	private final int opcode;
	
	/**
	 * The packet type
	 */
	private final PacketType type;
	
	/**
	 * The packets payload
	 */
	private final ByteBuf payload = Unpooled.buffer();
	
	/**
	 * The access mode of the builder.
	 */
	private AccessMode mode = AccessMode.BYTE_ACCESS;
	
	/**
	 * The bit index.
	 */
	private int bitIndex;

	/**
	 * Creates a new PacketBuf with no opcode or header.
	 */
	public PacketBuf() {
		this(-1, null);
	}

	/**
	 * Creates a new PacketBuf with the packet type set to Fixed.
	 * @param opcode The opcode.
	 */
	public PacketBuf(int opcode) {
		this(opcode, PacketType.FIXED);
	}
	
	/**
	 * Creates a new PacketBuf.
	 * @param opcode The opcode id.
	 * @param type The packet type.
	 */
	public PacketBuf(int opcode, PacketType type) {
		this.opcode = opcode;
		this.type = type;
	}

	/**
	 * Gets the length of the buffer.
	 * @return The length of the buffer.
	 */
	public int getLength() {
		return payload.writerIndex();
	}

	/**
	 * Checks if this packet builder is empty.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isEmpty() {
		return payload.writerIndex() == 0;
	}
	
	/**
	 * Writes an array of bytes.
	 * @param b The byte array.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf put(byte[] b) {
		payload.writeBytes(b);
		return this;
	}
	
	/**
	 * Puts a sequence of bytes in the buffer.
	 * @param data The bytes.
	 * @param offset The offset.
	 * @param length The length.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf put(byte[] data, int offset, int length) {
		payload.writeBytes(data, offset, length);
		return this;
	}
	
	/**
	 * Puts an <code>ChannelBuffer</code>.
	 * @param buf The buffer.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf put(ByteBuf buf) {
		payload.writeBytes(buf);
		return this;
	}
	
	/**
	 * Writes a byte.
	 * @param b The byte to write.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf put(int b) {
		payload.writeByte(b);
		return this;
	}
	
	/**
	 * Puts a GamePackets payload into this buffer.
	 * @param packet The game packet.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf put(Packet packet) {
		put(packet.getPayload());
		return this;
	}
	
	/**
	 * Adds the bytes of another builders payload..
	 * @param builder The game packet builder.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf put(PacketBuf builder) {
		payload.writeBytes(builder.payload);
		return this;
	}
	
	/**
	 * Puts a single bit into the buffer. If {@code flag} is {@code true}, the
	 * value of the bit is {@code 1}. If {@code flag} is {@code false}, the
	 * value of the bit is {@code 0}.
	 * @param flag The flag.
	 * @throws IllegalStateException if the builder is not in bit access mode.
	 */
	public void putBit(boolean flag) {
		putBit(flag ? 1 : 0);
	}

	/**
	 * Puts a single bit into the buffer with the value {@code value}.
	 * @param value The value.
	 * @throws IllegalStateException if the builder is not in bit access mode.
	 */
	public void putBit(int value) {
		putBits(1, value);
	}
	 
	/**
	 * Puts a number of bits into the buffer.
	 * @param numberOfBits The number of bits to added into the buffer.
	 * @param value The value
	 */
	public void putBits(int numberOfBits, int value) {
		if(numberOfBits > MAX_BITS) {
			throw new IllegalStateException("Error! Cant assign more than "+MAX_BITS+" to the buffer");
		}
		if (mode != AccessMode.BIT_ACCESS) {
			throw new IllegalStateException("Error! AccessMode set to BYTE_ACCESS");
		}
		int bytePos = bitIndex >> 3;
		int bitOffset = 8 - (bitIndex & 7);
		bitIndex += numberOfBits;
		int requiredSpace = bytePos - payload.writerIndex() + 1;
		requiredSpace += (numberOfBits + 7) / 8;
		payload.ensureWritableBytes(requiredSpace);
		for (; numberOfBits > bitOffset; bitOffset = 8) {
			int tmp = payload.getByte(bytePos);
			tmp &= ~BIT_MASKS[bitOffset];
			tmp |= (value >> (numberOfBits-bitOffset)) & BIT_MASKS[bitOffset];
			payload.setByte(bytePos++, tmp);
			numberOfBits -= bitOffset;
		}
		if (numberOfBits == bitOffset) {
			int tmp = payload.getByte(bytePos);
			tmp &= ~BIT_MASKS[bitOffset];
			tmp |= value & BIT_MASKS[bitOffset];
			payload.setByte(bytePos, tmp);
		} else {
			int tmp = payload.getByte(bytePos);
			tmp &= ~(BIT_MASKS[numberOfBits] << (bitOffset - numberOfBits));
			tmp |= (value & BIT_MASKS[numberOfBits]) << (bitOffset - numberOfBits);
			payload.setByte(bytePos, tmp);
		}
        payload.setIndex(0, bytePos);
	}

	/**
	 * Puts a type-A byte in the buffer.
	 * @param val The value.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putByteA(byte val) {
		payload.writeByte(val + 128);
		return this;
	}

	/**
	 * Writes a type-A byte.
	 * @param val The value.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putByteA(int val) {
		payload.writeByte(val + 128);
		return this;
	}

	/**
	 * Puts a type-C byte in the buffer.
	 * @param val The value.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putByteC(byte val) {
		payload.writeByte(-val);
		return this;
	}

	/**
	 * Writes a type-C byte.
	 * @param val The value to write.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putByteC(int val) {
		put(-val);
		return this;
	}
	
	/**
	 * Adds the contents of <code>byte</code> array <code>data</code>
	 * to the packet. The size of this packet will grow by the length of
	 * the provided array.
	 *
	 * @param data The bytes to add to this packet
	 * @return A reference to this object
	 */
	public PacketBuf putBytes(byte[] data) {
		return putBytes(data, 0, data.length);
	}

	/**
	 * Adds the contents of <code>byte</code> array <code>data</code>,
	 * starting at index <code>offset</code>. The size of this packet will
	 * grow by <code>len</code> bytes.
	 *
	 * @param data   The bytes to add to this packet
	 * @param offset The index of the first byte to append
	 * @param len	The number of bytes to append
	 * @return A reference to this object
	 */
	public PacketBuf putBytes(byte[] data, int offset, int len) {
		for (int i = offset; i < len - offset; i++) {
			put(data[i]);
		}
		return this;
	}
	/**
	 * Puts a type-S byte in the buffer.
	 * @param i The value.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putByteS(int i) {
		payload.writeByte(128 - i);
		return this;
	}

	/**
	 * Puts several type-A bytes into the buffer.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putBytesA(byte[] data, int offset, int len) {
		for (int i = offset; i < len - offset; i++) {
			putByteA(data[i]);
		}
		return this;
	}
	
	/**
	 * Puts several type-A bytes into the buffer.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putBytesA(Packet data, int offset, int len) {
		for (int i = offset; i < len - offset; i++) {
			putByteA(data.getPayload().getByte(i));
		}
		return this;
	}
	
	/**
	 * Writes an integer.
	 * @param i The integer.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putInt(int i) {
		payload.writeInt(i);
		return this;
	}

	/**
	 * Writes a type-1 integer.
	 * @param val The value.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putInt1(int val) {
		payload.writeByte(val >> 8);
		payload.writeByte(val);
		payload.writeByte(val >> 24);
		payload.writeByte(val >> 16);
		return this;
	}
	
	/**
	 * Writes a type-2 integer.
	 * @param val The value.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putInt2(int val) {
		payload.writeByte(val >> 16);
		payload.writeByte(val >> 24);
		payload.writeByte(val);
		payload.writeByte(val >> 8);
		return this;
	}
	
	/**
	 * Puts a string into the buffer. (it prefixes & postfixes the string)
	 * @param str The string.
	 * @return The PacketBuf for chaining.
	 */
	public PacketBuf putJagString(String str) {
		put(0);
		putBytes(str.getBytes());
		put(0);
		return this;
	}
	
	/**
	 * Writes a little-endian integer.
	 * @param val The value.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putLEInt(int val) {
		payload.writeByte(val);
		payload.writeByte(val >> 8);
		payload.writeByte(val >> 16);
		payload.writeByte(val >> 24);
		return this;
	}
	
	/**
	 * Writes a little-endian short.
	 * @param val The value.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putLEShort(int val) {
		payload.writeByte(val);
		payload.writeByte(val >> 8);
		return this;
	}
	
	/**
	 * Writes a little endian type-A short.
	 * @param val The value.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putLEShortA(int val) {
		payload.writeByte(val + 128);
		payload.writeByte(val >> 8);
		return this;
	}
	
	/**
	 * Writes a long.
	 * @param l The long.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putLong(long l) {
		payload.writeLong(l);
		return this;
	}

	/**
	 * Puts a series of reversed bytes in the buffer.
	 * @param is The source byte array.
	 * @param offset The offset.
	 * @param length The length.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putReverse(byte[] is, int offset, int length) {
		for(int i = (offset + length - 1); i >= offset; i--) {
			payload.writeByte(is[i]);
		}
		return this;
	}
	
	/**
	 * Puts a series of reversed type-A bytes in the buffer.
	 * @param is The source byte array.
	 * @param offset The offset.
	 * @param length The length.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putReverseA(byte[] is, int offset, int length) {
		for(int i = (offset + length - 1); i >= offset; i--) {
			putByteA(is[i]);
		}
		return this;
	}
	
	/**
	 * Writes a short.
	 * @param s The short.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putShort(int s) {
		payload.writeShort(s);
		return this;
	}
	
	/**
	 * Writes a type-A short.
	 * @param val The value.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putShortA(int val) {
		payload.writeByte(val >> 8);
		payload.writeByte(val + 128);
		return this;
	}
	
	/**
	 * Puts a byte or short for signed use.
	 * @param val The value.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putSignedSmart(int val) {
		if(val >= 128) {
			putShort(val + 49152);
		} else {
			put(val + 64);
		}
		return this;
	}
	
	/**
	 * Puts a byte or short.
	 * @param val The value.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putSmart(int val) {
		if(val >= 128) {
			putShort(val + 32768);
		} else {
			put(val);
		}
		return this;
	}
	
	/**
	 * Puts a 3-byte integer.
	 * @param val The value.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf putTriByte(int val) {
		payload.writeByte(val >> 16);
		payload.writeByte(val >> 8);
		payload.writeByte(val);
		return this;
	}
	
	/**
	 * Puts a 5-byte integer.
	 * @param val The value.
	 * @return The PacketBuf instance, for chaining.
	 */
	public PacketBuf put5ByteInteger(long value) {
		put((int) (value >> 32));
		putInt((int) (value & 0xffffffff));
		return this;
	}
	
	/**
	 * Switches the access mode to BIT_ACCESS
	 */
	public void switchToBitAccess() {
		if (mode == AccessMode.BIT_ACCESS) {
			throw new IllegalStateException("Already set to bit access mode");
		}
		mode = AccessMode.BIT_ACCESS;
		bitIndex = payload.writerIndex() * 8;
	}

	/**
	 * Switches the access mode to Byte_Access.
	 */
	public void switchToByteAccess() {
		if (mode == AccessMode.BYTE_ACCESS) {
			throw new IllegalStateException("Already set to byte access mode");
		}
		mode = AccessMode.BYTE_ACCESS;
		payload.writerIndex((bitIndex + 7) / 8);
	}

	/**
	 * Returns the PacketBuf data to a new Packet.
	 * @return A new Packet with the data from the PacketBuf.
	 */
	public Packet toPacket() {
		return new Packet(opcode, type, payload);
	}

	/**
	 * Gets the payload for this packet.
	 * @return the payload.
	 */
	public ByteBuf getPayload() {
		return payload;
	}

	/**
	 * Puts a string into the buffer.
	 * @param string The string to add.
	 * @return 
	 */
	public PacketBuf putString(String str) {
		char[] chars = str.toCharArray();
		for (char c : chars) {
			payload.writeByte((byte) c);
		}
		payload.writeByte(0);
		return this;
	}
}
