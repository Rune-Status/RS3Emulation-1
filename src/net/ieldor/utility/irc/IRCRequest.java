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
package net.ieldor.utility.irc;

import java.io.IOException;

/**
 * Handles an IRC request.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class IRCRequest {

	/**
	 * The IRC connection.
	 */
	private IRCConnection ircConnection;

	/**
	 * Constructs a new {@code IRCRequest} instance. 
	 * @param ircConnection An IRC connection.
	 */
	public IRCRequest(IRCConnection ircConnection) {
		this.ircConnection = ircConnection;
	}
	
	/**
	 * Performs the login procedure.
	 * @throws IOException An IO error has occured.
	 */
	public void performLogin() throws IOException {
		writeResponse("NICK " + getIRCConnection().getUsername());
		writeResponse("USER RSPSBot.");
		writeResponse("PRIVMSG NickServ IDENTIFY " + getIRCConnection().getPassword());
	}
	
	/**
	 * Writes an response to the IRC server.
	 * @param response The response.
	 * @throws IOException An IO error has occured.
	 */
	private void writeResponse(String response) throws IOException {
		getIRCConnection().getConnectionWriter().write(response + "\r\n");
		getIRCConnection().getConnectionWriter().flush();
	}
	
	/**
	 * Joins an channel.
	 * @throws IOException An IO error has occucred.
	 */
	public void performJoin() throws IOException {
		writeResponse("JOIN " + getIRCConnection().getIrcChannel());
	} 

	/**
	 * Gets the IRC connection.  
	 * @return the connection.
	 */
	public IRCConnection getIRCConnection() {
		return ircConnection;
	}
}
