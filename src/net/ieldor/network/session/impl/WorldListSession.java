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

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.ieldor.network.codec.messages.WorldListMessage;
import net.ieldor.network.session.Session;
import net.ieldor.utility.world.Country;
import net.ieldor.utility.world.World;

/**
 * An {@link Session} that handles the world list update.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class WorldListSession extends Session {

	/**
	 * The current countries of the server.
	 */
	private static final Country[] COUNTRIES = {
		new Country(Country.FLAG_USA, "USA"),
		new Country(Country.FLAG_AUSTRALIA, "Australia")
	};
	
	/**
	 * Constructs a new {@link WorldListSession} instance.
	 * @param context The context of the channel.
	 */
	public WorldListSession(ChannelHandlerContext context) {
		super(context);
	}

	/* (non-Javadoc)
	 * @see net.ieldor.network.session.Session#disconnected()
	 */
	@Override
	public void disconnected() {
		/*
		 * Nothing to do here.
		 */
	}

	/* (non-Javadoc)
	 * @see net.ieldor.network.session.Session#message(java.lang.Object)
	 */
	@Override
	public void message(Object obj) {
		World[] worlds = { new World(1, "World 1", World.FLAG_MEMBERS | World.FLAG_QUICK_CHAT, 0, "Ieldor BETA", "127.0.0.1"), new World(2, "World 2", World.FLAG_MEMBERS | World.FLAG_QUICK_CHAT, 0, "Ieldor BETA", "127.0.0.1") };
		int[] players = { 0 };
		channel.write(new WorldListMessage(0xDEADBEEF, COUNTRIES, worlds, players)).addListener(ChannelFutureListener.CLOSE);
	}
}
