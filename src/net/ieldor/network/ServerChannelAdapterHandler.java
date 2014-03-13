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
package net.ieldor.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.util.AttributeKey;

import java.util.logging.Logger;

import net.ieldor.Main;
import net.ieldor.network.codec.messages.HandshakeMessage;
import net.ieldor.network.session.Session;
import net.ieldor.network.session.impl.LoginSession;
import net.ieldor.network.session.impl.UpdateSession;
import net.ieldor.network.session.impl.WorldListSession;

/**
 * An {@link ChannelInboundMessageHandlerAdapter} that is used to handle incoming {@link Channel}s.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class ServerChannelAdapterHandler extends ChannelInboundMessageHandlerAdapter<Object> {
	
	/**
	 * An {@link AttributeKey} that is used for storing attributes of a specific channel.
	 */
	public static final AttributeKey<Session> attributeMap = new AttributeKey<Session>("ServerChannelAdapterHandler.attr");
	
	/**
	 * The main context of the server.
	 */
	private Main mainContext;
	
	/**
	 * Constructs a new {@code ServerChannelAdapterHandler} instance.
	 * @param mainContext The main context of the server.
	 */
	public ServerChannelAdapterHandler(Main mainContext) {
		this.mainContext = mainContext;
	}
	
	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundMessageHandlerAdapter#messageReceived(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
		Object attachment = ctx.channel().attr(attributeMap).get();
		if(attachment == null) {
			HandshakeMessage handshakeMessage = (HandshakeMessage) msg;
			switch(handshakeMessage.getState()) {
			case HANDSHAKE_UPDATE:
				ctx.channel().attr(attributeMap).set(new UpdateSession(ctx, mainContext));
				break;
			case HANDSHAKE_WORLD_LIST:
				ctx.channel().attr(attributeMap).set(new WorldListSession(ctx));
				break;
			case HANDSHAKE_LOGIN:
				ctx.channel().attr(attributeMap).set(new LoginSession(ctx));
				break;
				default: throw new IllegalStateException("Invalid handshake state requested.");
			}
		} else
			ctx.channel().attr(attributeMap).get().message(msg);
	}
	
	/*
	 * (non-Javadoc)
	 * @see io.netty.channel.ChannelStateHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
		t.printStackTrace();
		if (ctx.channel().isOpen())
			  ctx.channel().close();
	}
	
	/*
	 * (non-Javadoc)
	 * @see io.netty.channel.ChannelStateHandlerAdapter#channelUnregistered(io.netty.channel.ChannelHandlerContext)
	 */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		Object attachment = channel.attr(attributeMap).get();
		if(attachment != null) {
			((Session) attachment).disconnected();
		}
		Logger.getAnonymousLogger().info("Channel disconnected from game: " + channel.remoteAddress() + ".");
    }
}
