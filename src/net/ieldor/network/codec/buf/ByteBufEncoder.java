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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * An {@link MessageToByteEncoder} that handles the writting of raw {@link ByteBuf}'s
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class ByteBufEncoder extends MessageToByteEncoder<ByteBuf> {
	
	/**
	 * Constructs a new {@link ByteBufEncoder} instance.
	 */
	public ByteBufEncoder() {
		super(ByteBuf.class);
	}

	/* (non-Javadoc)
	 * @see io.netty.handler.codec.MessageToByteEncoder#encode(io.netty.channel.ChannelHandlerContext, java.lang.Object, io.netty.buffer.ByteBuf)
	 */
	@Override
	public void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
		if(msg == null)
			throw new EncoderException("Please revise your buffer.");
		out.writeBytes(msg);
	}
}
