/*
 * This file is part of RS3Emulation.
 *
 * RS3Emulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RS3Emulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RS3Emulation.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rs3e.network.session.impl;

import io.netty.channel.ChannelHandlerContext;

import java.net.ProtocolException;

import com.rs3e.game.player.Player;
import com.rs3e.network.protocol.handlers.PacketHandler;
import com.rs3e.network.protocol.packets.Packet;
import com.rs3e.network.protocol.packets.PacketReader;
import com.rs3e.network.protocol.packets.decoder.PacketDecoder;
import com.rs3e.network.session.Session;

/**
 * 
 * RS3Emulation GameSession.java Mar 11, 2014
 * 
 * @author Im Frizzy : Kyle Friz : <skype:kfriz1998>
 */
public class GameSession extends Session {

	/**
	 * Represents the player.
	 */
	private Player player;

	/**
	 * Constructs a new {@code WorldSession.java}.
	 * 
	 * @param context
	 */
	public GameSession(ChannelHandlerContext context) {
		super(context);
	}

	@Override
	public void message(Object message) {
		if (!(message instanceof Packet)) {
			try {
				throw new ProtocolException(message.toString());
			} catch (ProtocolException e) {
				// GameServer.getEngine().handleException(e);
			}
		}
		PacketDecoder<? super PacketHandler<? super Session>> decoder = RS2Network
				.getDecoders().get(((Packet) message).getOpcode());
		if (decoder == null) {
			System.err.println("Unhandled packet: "
					+ ((Packet) message).getOpcode());
			return;
		}
		@SuppressWarnings("unchecked")
		PacketHandler<? super Session> handler = (PacketHandler<? super Session>) decoder
				.decodePacket(new PacketReader(((Packet) message).getBuffer()
						.buffer()), this, ((Packet) message).getOpcode());
		if (handler != null) {
			handler.handle(this);
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.rs3e.network.session.Session#disconnected()
	 */
	@Override
	public void disconnected() {
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

}
