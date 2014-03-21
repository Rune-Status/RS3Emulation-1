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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.math.BigInteger;

import net.ieldor.Constants;
import net.ieldor.game.model.player.LoadResult;
import net.ieldor.game.model.player.Player;
import net.ieldor.io.InputStream;
import net.ieldor.io.Packet;
import net.ieldor.io.PacketBuf;
import net.ieldor.modules.login.BinaryPlayerManager;
import net.ieldor.modules.login.LoginManager;
import net.ieldor.modules.login.MachineData;
import net.ieldor.network.codec.login.LoginPayload;
import net.ieldor.network.codec.messages.LoginHandshakeMessage;
import net.ieldor.network.codec.messages.LoginResponse;
import net.ieldor.network.session.Session;
import net.ieldor.utility.Base37Utils;
import net.ieldor.utility.ByteBufUtils;
import net.ieldor.utility.XTEA;

/**
 * An {@link Session} that handles the login request.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class LoginSession extends Session {
	
	private LoginManager loginManager;

	/**
	 * Constructs a new {@link LoginSession} instance.
	 * @param context The context of the channel.
	 */
	public LoginSession(ChannelHandlerContext context, LoginManager loginManager) {
		super(context);
		this.loginManager = loginManager;
	}

	/* (non-Javadoc)
	 * @see net.ieldor.network.session.Session#disconnected()
	 */
	@Override
	public void disconnected() {
		
	}
	
	private Player player;
	/* (non-Javadoc)
	 * @see net.ieldor.network.session.Session#message(java.lang.Object)
	 */
	@Override
	public void message(Object obj) {
		//System.out.println("Login message method called...");
		if (obj instanceof LoginPayload) {
			LoginPayload loginData = (LoginPayload) obj;
			if (loginData.getPayload() == null) {
				loginManager.sendPlayerData(player, channel, context);
				return;
			}
			ByteBuf buffer = Unpooled.wrappedBuffer(loginData.getPayload());
			if (loginData.getType() == LoginPayload.LoginType.LOBBY) {
				loginManager.runLobbyLogin(buffer, channel, context);
			} else if (loginData.getType() == LoginPayload.LoginType.GAME) {
				//System.out.println("Received game login request");
				player = loginManager.runGameLogin(buffer, channel, context);
			}
		}
	}
}
