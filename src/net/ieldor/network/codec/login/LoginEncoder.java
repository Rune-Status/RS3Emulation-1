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
package net.ieldor.network.codec.login;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.ieldor.network.codec.messages.LoginResponse;

/**
 * An {@link MessageToByteEncoder} that is used for writting the login response.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class LoginEncoder extends MessageToByteEncoder<LoginResponse> {
	
	/**
	 * Constructs a new {@link LoginEncoder} instance.
	 */
	public LoginEncoder() {
		super(LoginResponse.class);
	}

	/* (non-Javadoc)
	 * @see io.netty.handler.codec.MessageToByteEncoder#encode(io.netty.channel.ChannelHandlerContext, java.lang.Object, io.netty.buffer.ByteBuf)
	 */
	@Override
	public void encode(ChannelHandlerContext ctx, LoginResponse msg, ByteBuf out) throws Exception {		 
		out.writeByte(msg.getReturnCode());
		if (msg.hasPayload()) {
			if (msg.isVarShort()) {
				out.writeShort(msg.getPayloadSize());
			} else {
				System.out.println("Sending player data. Size="+msg.getPayloadSize());
				out.writeByte(msg.getPayloadSize());
			}
			out.writeBytes(msg.getPayload());
		}
	}
}