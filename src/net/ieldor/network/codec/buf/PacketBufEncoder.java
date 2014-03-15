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

import net.ieldor.io.Packet;
import net.ieldor.io.Packet.PacketType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * An {@link MessageToByteEncoder} that is used to encode an outgoing {@link Packet}.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class PacketBufEncoder extends MessageToByteEncoder<Packet> {
	
	/**
	 * Constructs a new {@link PacketBufEncoder} instance.
	 */
	public PacketBufEncoder() {
		super(Packet.class);
	}

	/* (non-Javadoc)
	 * @see io.netty.handler.codec.MessageToByteEncoder#encode(io.netty.channel.ChannelHandlerContext, java.lang.Object, io.netty.buffer.ByteBuf)
	 */
	@Override
	public void encode(ChannelHandlerContext ctx, Packet buf, ByteBuf out) throws Exception {		 
		if (buf.isHeaderless()) {
			out.writeBytes(buf.getPayload());
		} else {
			int opcode = buf.getOpcode();
			PacketType type = buf.getType();
			int length = buf.getLength();
			int finalLength = length + 2 + type.getValue();
			ByteBuf buffer = Unpooled.buffer(finalLength);
			if (opcode >= 128) {
				buffer.writeByte((opcode >> 8) + 128);
				buffer.writeByte(opcode);
			} else {
				buffer.writeByte(opcode);
			}
			//System.out.println("Sending packet. Opcode="+((opcode) & 0xFF));
			switch (type) {
			case BYTE:
				buffer.writeByte(length);
				break;
			case SHORT:
				buffer.writeShort(length);
				break;
			default:
				break;
			}
			buffer.writeBytes(buf.getPayload());
			out.writeBytes(buffer);
		}		
	}
}
