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
package net.ieldor.network.codec.messages;

import io.netty.channel.ChannelHandlerContext;

/**
 * An simple handshake message from the login procedure.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class LoginHandshakeMessage {

	/**
	 * The username and password.
	 */
	private String username, password;
	
	/**
	 * The channel handler context.
	 */
	private ChannelHandlerContext ctx;
 
	/**
	 * Constructs a new {@link LoginHandshakeMessage} instance.
	 * @param username The username.
	 * @param password The password.
	 * @param ctx The channel context.
	 */
	public LoginHandshakeMessage(String username, String password, ChannelHandlerContext ctx) {
		this.username = username;
		this.password = password;
		this.ctx = ctx;
	}

	/**
	 * Gets the username.
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Gets the password.
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Gets the channel handler context.
	 * @return The context.
	 */
	public ChannelHandlerContext getContext() {
		return ctx;
	}
}
