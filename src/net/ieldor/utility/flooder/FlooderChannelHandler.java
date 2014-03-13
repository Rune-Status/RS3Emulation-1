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
package net.ieldor.utility.flooder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.SecureRandom;

import net.ieldor.utility.Base37Utils;
import net.ieldor.utility.ByteBufUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundByteHandlerAdapter;

public final class FlooderChannelHandler extends ChannelInboundByteHandlerAdapter {

	private enum State {
		READ_SERVER_SESSION_KEY,
		READ_LOGIN_STATUS,
		READ_LOGIN_PAYLOAD,
		READ_GAME_OPCODE
	}

	private static final SecureRandom random = new SecureRandom();

	private State state = State.READ_SERVER_SESSION_KEY;
	private final int[] crc;
	private final long clientSessionKey = random.nextLong();
	private String username, password;
	private long serverSessionKey, encodedUsername;

	public FlooderChannelHandler(int[] crc) {
		this.crc = crc;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();

		username = "bot" + ((InetSocketAddress) channel.localAddress()).getPort();
		password = "password";
		encodedUsername = Base37Utils.encodeBase37(username);

		ByteBuf packet = Unpooled.buffer();
		packet.writeByte(14);
		packet.writeByte((byte) ((encodedUsername >> 16) & 31));
		channel.write(packet);
	}

	@Override
	public void inboundBufferUpdated(ChannelHandlerContext ctx, ByteBuf buf) throws IOException {
		Channel channel = ctx.channel();

		if (state == State.READ_SERVER_SESSION_KEY) {
			if (buf.readableBytes() >= 9) {
				if (buf.readUnsignedByte() != 0)
					throw new IOException("expecting EXCHANGE_KEYS opcode");

				serverSessionKey = buf.readLong();

				ByteBuf payload = Unpooled.buffer();
				payload.writeInt(530);

				payload.writeByte(0);
				payload.writeByte(0);
				payload.writeByte(0);

				payload.writeByte(0);
				payload.writeShort(765);
				payload.writeShort(503);

				payload.writeByte(0);

				for (int i = 0; i < 24; i++)
					payload.writeByte(0);

				ByteBufUtils.writeString(payload, "kKmok3kJqOeN6D3mDdihco3oPeYN2KFy6W5--vZUbNA");

				payload.writeInt(0);
				payload.writeInt(0);
				payload.writeShort(0);

				for (int i = 0; i < 28; i++) {
					payload.writeInt(crc[i]);
				}

				payload.writeByte(10);
				payload.writeLong(clientSessionKey);
				payload.writeLong(serverSessionKey);
				payload.writeLong(encodedUsername);
				ByteBufUtils.writeString(payload, password);

				ByteBuf packet = Unpooled.buffer();
				packet.writeByte(18);
				packet.writeShort(payload.readableBytes());
				packet.writeBytes(payload);

				channel.write(packet);

				int[] seed = new int[4];
				seed[0] = (int) (clientSessionKey >> 32);
				seed[1] = (int) clientSessionKey;
				seed[2] = (int) (serverSessionKey >> 32);
				seed[3] = (int) serverSessionKey;

				state = State.READ_LOGIN_STATUS;
			}
		}

		if (state == State.READ_LOGIN_STATUS) {
			if (buf.readable()) {
				int status = buf.readUnsignedByte();
				if (status != 2)
					throw new IOException("expecting OK login response");

				state = State.READ_LOGIN_PAYLOAD;
			}
		}

		if (state == State.READ_LOGIN_PAYLOAD) {
			if (buf.readableBytes() >= 11) {
				buf.readerIndex(buf.readerIndex() + 11);

				state = State.READ_GAME_OPCODE;
			}
		}

		if (state == State.READ_GAME_OPCODE) {
			
		}
	}
}
