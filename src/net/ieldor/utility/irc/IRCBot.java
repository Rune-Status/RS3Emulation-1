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
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * The main application entry for the IRC bot.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public final class IRCBot {

	/**
	 * The main entry point of the application.
	 * @param args The command-line arguments.
	 */
	public static void main(String... args) {
		if(args.length < 5)
			throw new IllegalStateException("USAGE: <SERVER> <PORT> <USERNAME> <PASSWORD> <CHANNEL>");
		
		String serverName = args[0];
		if(serverName == null)
			throw new IllegalStateException("Server name is null.");
		
		int serverPort = Integer.valueOf(args[1]);
		if(serverPort < 0 || serverPort > 25565)
			throw new IllegalStateException("Port out of range!");
		
		String username = args[2];
		if(username == null)
			throw new IllegalStateException("Username is null.");
		
		String password = args[3];
		if(password == null)
			throw new IllegalStateException("Password is null.");
		
		String ircChannel = args[4];
		if(ircChannel == null)
			throw new IllegalStateException("IRC Channel is null.");
		
		IRCConnection ircConnection = new IRCConnection(serverName, serverPort, username, password, ircChannel);
		try {
			ircConnection.beginConnect(true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger.getAnonymousLogger().info("Complete!");
	}
}
