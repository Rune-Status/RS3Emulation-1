/*
 * This file is part of Ieldor.
 *
 * Ieldor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ieldor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Ieldor.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.ieldor.network.codec.buf;

import java.io.IOException;

import net.ieldor.config.IncommingOpcode;
import net.ieldor.io.Packet;
import net.ieldor.io.Packet.PacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * An {@link ByteToMessageDecoder} that is used to decode incoming packets from the
 * client.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class PacketBufDecoder extends ByteToMessageDecoder<Packet> {
	
	public static final int[] SIZES = IncommingOpcode.getPacketSizes();

	private enum State {
		READ_OPCODE, READ_SIZE, READ_PAYLOAD
	}

	private State state = State.READ_OPCODE;
	private boolean variable;
	private int opcode, size;


	@Override
	public Packet decode(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
		if (state == State.READ_OPCODE) {
			if (!buf.readable())
				return null;

			opcode = (buf.readUnsignedByte()) & 0xFF;
			if (opcode > SIZES.length || opcode < 0) {
				System.out.println("Invalid opcode received: "+opcode);
				return null;
			}
			size = SIZES[opcode];

			if (size == -3) {
				System.out.println("Invalid packet size for: "+opcode);
				return null;
				//throw new IOException("Illegal opcode " + opcode + ".");
			}

			variable = size == -1;
			state = variable ? State.READ_SIZE : State.READ_PAYLOAD;
		}

		if (state == State.READ_SIZE) {
			if (!buf.readable()) {
				return null;
			}

			size = buf.readUnsignedByte();
			state = State.READ_PAYLOAD;
		}

		if (state == State.READ_PAYLOAD) {
			if (buf.readableBytes() < size)
				return null;

			ByteBuf payload = buf.readBytes(size);
			state = State.READ_OPCODE;
			
			//System.out.println("Received packet: opcode="+opcode+", size="+size);

			return new Packet(opcode, variable ? PacketType.BYTE : PacketType.FIXED, payload);
		}

		throw new IllegalStateException();
	}

}