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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * An class used to represent the connection between this application and the
 * IRC server.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class IRCConnection {
	
	/**
	 * The port of the connection.
	 */
	private int serverPort;
	
	/**
	 * The irc server connection attributes.
	 */
	private String serverName, username, password, ircChannel;

	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(IRCConnection.class.getName());
	
	/**
	 * The connection reader for the irc connection.
	 */
	private BufferedReader connectionReader;
	
	/**
	 * The connection writer for the irc connection.
	 */
	private BufferedWriter connectionWriter;
	
	/**
	 * Constructs a new {@code IRCConnection} instance.
	 * 
	 * @param serverName The name of the server.
	 * @param serverPort The port of the server.
	 * @param username The username of the bot.   
	 * @param password The password of the bot.
	 * @param ircChannel The IRC channel name.
	 */
	public IRCConnection(String serverName, int serverPort, String username, String password, String ircChannel) {
		this.serverName = serverName;
		this.serverPort = serverPort;
		this.username = username;
		this.password = password;
		this.ircChannel = ircChannel;
	}

	/**
	 * Begins the connection to the IRC server.
	 * @param verboseMode The verbose mode flag.
	 * @throws IOException An I/O based error has occured.
	 * @throws UnknownHostException The host can not be established.
	 */
	@SuppressWarnings("resource")
	public void beginConnect(boolean verboseMode) throws UnknownHostException, IOException {
		logger.info("Beginning connection to " + getServerName() + ":" + getServerPort());
		
		Socket socketConnection = new Socket(getServerName(), getServerPort());
		this.setConnectionWriter(new BufferedWriter(new OutputStreamWriter(socketConnection.getOutputStream())));
		this.setConnectionReader(new BufferedReader(new InputStreamReader(socketConnection.getInputStream())));
	
		IRCRequest ircRequest = new IRCRequest(this);
		ircRequest.performLogin();
		ircRequest.performJoin();
	}
	
	/**
	 * Gets the connection reader.
	 * @return the connectionReader
	 */
	public BufferedReader getConnectionReader() {
		return connectionReader;
	}

	/**
	 * Sets the connection reader.
	 * @param connectionReader the connectionReader to set
	 */
	public void setConnectionReader(BufferedReader connectionReader) {
		this.connectionReader = connectionReader;
	}

	/**
	 * Gets the connection writer.
	 * @return the connectionWriter
	 */
	public BufferedWriter getConnectionWriter() {
		return connectionWriter;
	}

	/**
	 * Sets the connection writer.
	 * @param connectionWriter the connectionWriter to set
	 */
	public void setConnectionWriter(BufferedWriter connectionWriter) {
		this.connectionWriter = connectionWriter;
	}

	/**
	 * Gets the server port.
	 * @return the serverPort
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * Gets the server name.
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
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
	 * Gets the IRC channel.
	 * @return the ircChannel
	 */
	public String getIrcChannel() {
		return ircChannel;
	}
}
