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
package net.ieldor.network.session.impl;

import java.io.IOException;
import java.util.logging.Logger;

import io.netty.channel.ChannelHandlerContext;
import net.ieldor.GameEngine;
import net.ieldor.Main;
import net.ieldor.game.model.player.Player;
import net.ieldor.io.Packet;
import net.ieldor.io.PacketReader;
import net.ieldor.modules.login.BinaryPlayerManager;
import net.ieldor.network.MiscPacketDecoder;
import net.ieldor.network.packet.PacketAssembler;
import net.ieldor.network.packet.PacketContext;
import net.ieldor.network.packet.PacketDecoder;
import net.ieldor.network.packet.PacketHandler;
import net.ieldor.network.session.Session;

/**
 * An {@link Session} that handles the game.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class GameSession extends Session {

	/**
	 * The player instance.
	 */
	private Player player;

	/**
	 * Constructs a new {@code GameSession} instance/
	 * @param context The context of the session.
	 */
	public GameSession(ChannelHandlerContext context, Player player) {
		super(context);
		this.player = player;
	}

	/* (non-Javadoc)
	 * @see net.ieldor.network.session.Session#disconnected()
	 */
	@Override
	public void disconnected() {
		player.disconnect();
		try {
			BinaryPlayerManager.savePlayer(player);
		} catch (IOException e) {
			e.printStackTrace();
		}
		synchronized(Main.getPlayers()) {
			Main.getPlayers().remove(player);
		}
		Logger.getAnonymousLogger().info("Successfully unregistered player from world [username=" + player.getUsername() + " online=" + Main.getPlayers().size() + "]");		
	}

	/* (non-Javadoc)
	 * @see net.ieldor.network.session.Session#message(java.lang.Object)
	 */
	@Override
	public void message(Object obj) {
		Packet gamePacket = (Packet) obj;
		PacketAssembler assembler = GameEngine.getGameEngine().getPacketCodec().get(gamePacket.getOpcode());
		//System.out.println("Packet="+gamePacket.getOpcode()+", assembler="+(assembler == null ? "null" : "exists"));
		if(assembler != null) {
			PacketDecoder<?> decoder = assembler.getDecoder();
			if(decoder != null) {
				PacketContext context = decoder.decode(new PacketReader(gamePacket));
				if(context != null) {
					@SuppressWarnings("unchecked")
					PacketHandler<PacketContext> handler = (PacketHandler<PacketContext>) assembler.getHandler();
					if(handler != null) {
						handler.handle(player, context);
					}
				}
			} else {
				PacketHandler<?> handler = assembler.getHandler();
				if(handler != null) {
					handler.handle(player, null);
				}
			}
		} else {
			//Do manual packet management
			//TODO Somehow integrate this into the main packet management system
			MiscPacketDecoder.decodeMiscPacket(new PacketReader(gamePacket), player);
		}
	}
}
