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
package net.ieldor.game.model.player;

import java.util.logging.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.ieldor.Main;
import net.ieldor.game.model.Entity;
import net.ieldor.game.model.Position;
import net.ieldor.network.ActionSender;
import net.ieldor.network.ServerChannelAdapterHandler;
import net.ieldor.network.codec.buf.PacketBufDecoder;
import net.ieldor.network.codec.buf.PacketBufEncoder;
import net.ieldor.network.session.impl.GameSession;

/**
 * Represents an {@link Entity} that is controlled by a physical human being.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class Player extends Entity {

	/**
	 * The channel of the player.
	 */
	private Channel channel;
	
	/**
	 * The username and password of the user.
	 */
	private String username, password;
	
	/**
	 * The current and previous display names of the user.
	 */
	private String displayName, prevDisplayName;
	
	/**
	 * The action sender instance.
	 */
	private ActionSender actionSender = new ActionSender(this);
	
	/**
	 * The players appearance.
	 */
	private Appearance appearance = new Appearance();
	
	
	/**
	 * Constructs a new {@code Player} instance.
	 * @param channel The channel of the player.
	 * @param username The username of the player.
	 * @param password The password of the player.
	 */
	public Player(Channel channel, String username, String password, Position position) {
		super(position);
		this.channel = channel;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * @param returnCode
	 * @param channelHandlerContext The channel handler context.
	 */
	public void login(int returnCode, ChannelHandlerContext channelHandlerContext) {				
		channel.pipeline().addFirst(new PacketBufEncoder(), new PacketBufDecoder());

		GameSession gameSession = new GameSession(channelHandlerContext, this);
		channelHandlerContext.channel().attr(ServerChannelAdapterHandler.attributeMap).set(gameSession);
			
		if(returnCode == 2) { 
			Logger.getAnonymousLogger().info("Successfully registered player into world [username=" + username + " index=" + getIndex() + " online=" + Main.getPlayers().size() + "]");
			actionSender.sendLogin();
		}
	}
	
	public void initDisplayName (String name, String prevName) {
		prevDisplayName = prevName;
		displayName = name;
	}
	
	public void changeDisplayName (String newName) {
		//TODO Write display name change logic
	}

	/**
	 * Gets the channel.
	 * @return the channel
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * Gets the username.
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Gets the current display name of the player.
	 * @return the display name
	 */
	public String getDisplayName () {
		return (displayName == null ? username : displayName);
	}
	
	/**
	 * Gets the previous display name for the player.
	 * @return the previous display name
	 */
	public String getPrevDisplayName () {
		return prevDisplayName;
	}

	/**
	 * Gets the password.
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Gets the action sender instance.
	 * @return The action sender.
	 */
	public ActionSender getActionSender() {
		return actionSender;
	}

	/**
	 * Gets the appearance.
	 * @return the appearance
	 */
	public Appearance getAppearance() {
		return appearance;
	}
}
