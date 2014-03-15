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

import java.util.List;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.ieldor.game.model.player.Player;
import net.ieldor.game.social.Friend;
import net.ieldor.game.social.Ignore;
import net.ieldor.game.social.OnlineStatus;
import net.ieldor.io.PacketBuf;
import net.ieldor.io.Packet.PacketType;
import net.ieldor.utility.BinaryLandscapeHandler;
import net.ieldor.utility.world.World;

/**
 * A class used to store the packets (actions) that an {@link Entity} can
 * perform.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class ActionSender {
	
	//TODO: Update packet opcodes to the required revision. If the revision in the comments is not correct (or missing), the packet needs updating
	private static final int KEEP_ALIVE_PACKET = 110;//795
	private static final int DYNAMIC_VARP_PACKET = 2;//795
	private static final int FIXED_VARP_PACKET = 156;//795
	
	private static final int UNLOCK_FRIENDS_LIST = 154;//795
	private static final int ONLINE_STATUS_PACKET = 47;
	private static final int FRIENDS_PACKET = 3;//795
	private static final int IGNORES_PACKET = 15;//795
	private static final int FRIENDS_CHANNEL_PACKET = 82;
	private static final int CLAN_CHANNEL_PACKET = 47;//795
	
	private static final int WINDOW_PANE_PACKET = 71;//795
	private static final int WORLD_LIST_PACKET = 117;//795
	private static final int MESSAGE_PACKET = 95;
	private static final int FRIENDS_CHAT_MESSAGE_PACKET = 111;
	

	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * The interface sent count.
	 */
	private int interfaceCount = 0;

	/**
	 * Constructs a new {@code ActionSender} instance.
	 * @param player The player performing the action.
	 */
	public ActionSender(Player player) {
		this.player = player;
	}
	
	/**
	 * Sends a ping (keep-alive) packet back to the client. Used to ensure the connection is not dropped
	 */
	public void sendPing () {
		PacketBuf buf = new PacketBuf(KEEP_ALIVE_PACKET);
		player.getChannel().write(buf.toPacket());
	}
	
	/**
	 * Sets the top-level interface (window pane) to the specified interface
	 * @param id	The interface ID of the window pane to use
	 * @param type	The type of window pane (usually zero)
	 */
	public void sendWindowPane (int id, int type) {
		//NOTE: The order and encoding methods of this packet vary between client revisions
		int[] xteas = new int[4];
		
		PacketBuf buf = new PacketBuf(WINDOW_PANE_PACKET);
		buf.putInt(xteas[0]);
		buf.putByteS(type);
		buf.putLEInt(xteas[2]);
		buf.putShortA(id);
		buf.putInt(xteas[1]);
		buf.putInt(xteas[3]);
		player.getChannel().write(buf.toPacket());
	}

	/**
	 * Sends a fixed-sized configuration key-value pair (8 bits). Known as "Config1" on some servers
	 * @param id	 The id (key) of the client varp
	 * @param value The value of the client varp
	 */
	public void sendFixedVarp(int id, int value) {
		if (!player.getChannel().isOpen()) {
			return;
		}
		//NOTE: The order and encoding methods of this packet vary between client revisions
		PacketBuf buf = new PacketBuf(FIXED_VARP_PACKET);
		buf.putLEShortA(id);
		buf.putByteC(value);
		player.getChannel().write(buf.toPacket());
	}

	/**
	 * Sends a dynamic-sized configuration key-value pair (8-32 bits). Known as "Config2" on some servers
	 * @param id	 The id (key) of the client varp
	 * @param value The value of the client varp
	 */
	public void sendDynamicVarp(int id, int value) {
		if (!player.getChannel().isOpen()) {
			return;
		}
		//NOTE: The order and encoding methods of this packet vary between client revisions
		PacketBuf buf = new PacketBuf(DYNAMIC_VARP_PACKET);
		buf.putInt(value);
		buf.putLEShort(id);
		player.getChannel().write(buf.toPacket());
	}

	/**
	 * Sends an config (client varp).
	 * @param id 	 The varp id.
	 * @param value The varp value.
	 */
	public void sendVarp(int id, int value) {
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			sendDynamicVarp(id, value);
		} else {
			sendFixedVarp(id, value);
		}
	}
	
	/**
	 * Notifies the client of the player's current online status
	 * @param status The player's online status
	 */
	public void sendOnlineStatus(OnlineStatus status) {
		PacketBuf buf = new PacketBuf(ONLINE_STATUS_PACKET);
		buf.put(status.getCode());
		player.getChannel().write(buf.toPacket());
	}
	
	/**
	 * Sends a packet notifying the client that the friends server is being connected to.
	 * Changes friends list message from "Loading Friends List." to "Connecting to Friend Server."
	 */
	public void sendUnlockFriendsList () {
		PacketBuf buf = new PacketBuf(UNLOCK_FRIENDS_LIST);
		player.getChannel().write(buf.toPacket());
	}
	
	/**
	 * Sends all the friends currently on a player's friends list (used for friends list initialisation
	 * @param friends A list containing all the friends on the player's friends list
	 */
	public void sendFriends (List<Friend> friends) {
		PacketBuf buf = new PacketBuf(FRIENDS_PACKET);
		for (Friend f : friends) {
			packFriend(f, false, buf);
		}
		player.getChannel().write(buf.toPacket());
	}
	
	/**
	 * Sends an individual friend to the player. This could be either an update to a friend's details or a friend addition
	 * @param friend 		The details for the specified friend
	 * @param isNameChange	Whether or not this is a notification of a friend changing their name.
	 */
	public void sendFriend (Friend friend, boolean isNameChange) {
		PacketBuf buf = new PacketBuf(FRIENDS_PACKET);
		packFriend(friend, isNameChange, buf);
		player.getChannel().write(buf.toPacket());
	}	
	
	/**
	 * Writes the friend details to the specified packet
	 * @param friend		The friend object from which to fetch details
	 * @param isNameChange	Whether the request represents a name change
	 * @param packet		The packet in which to write the friend details
	 */
	public void packFriend(Friend friend, boolean isNameChange, PacketBuf packet) {
		World world = friend.getWorld();
		boolean putOnline = (world != null);
		int flags = 0;
		/*if (friend.isRecruited()) {
			flags |= 0x1;
		}*/
		if (friend.isReferred()) {
			flags |= 0x2;
		}
		
		packet.put(isNameChange ? 0 : 1);//Is this a notification of a friend name change
		packet.putString(friend.getName());//Current display name
		packet.putString(friend.getPrevName() == null ? "" : friend.getPrevName());//Previous display name, or empty string if null
		packet.putShort(putOnline ? world.getNodeId() : 0);//NodeID (world ID) of friend, or 0 if offline
		packet.put(friend.getFcRank());//Rank in player's friends chat
		packet.put(flags);//Flags (0x2=referred, 0x1=recruited)
		if (putOnline) {
			packet.putString(world.getName());//Friend world name
			packet.put(0);//This always seems to be zero. Possibly physical server location? More info is needed.
			packet.putInt(world.getFlags());//Friend server flags
		}
		packet.putString(friend.getNote());//Note
	}
	
	/**
	 * Sends all the ignores currently on a player's ignore list. Used for initialising the ignore list
	 * @param ignores A list containing all the ignores on the player's ignore list
	 */
	public void sendIgnores(List<Ignore> ignores) {
		PacketBuf buf = new PacketBuf(IGNORES_PACKET);
		for (Ignore i : ignores) {
			packIgnore(i, false, buf);
		}
		player.getChannel().write(buf.toPacket());			
	}
	
	/**
	 * Sends an individual ignore to the player. This could either be a new ignore or an update to an existing ignore
	 * @param ignore		The details for the specified ignore
	 * @param isNameChange Whether this notification represents a name change
	 */
	public void sendIgnore(Ignore ignore, boolean isNameChange) {
		PacketBuf buf = new PacketBuf(IGNORES_PACKET);
		packIgnore(ignore, isNameChange, buf);
		player.getChannel().write(buf.toPacket());		
	}
	
	/**
	 * Writes the ignore details to the specified packet buffer
	 * @param ignore		The ignore object from which to fetch details
	 * @param isNameChange Whether the request represents a name change
	 * @param packet		The packet into which to write the ignore details
	 */
	public void packIgnore(Ignore ignore, boolean isNameChange, PacketBuf packet) {
		packet.put((isNameChange ? 1 : 0));
		packet.putString(ignore.getName());
		packet.putString(ignore.getPreviousName());
		packet.putString(ignore.getNote());
	}
	
	/**
	 * Sends the default login data.
	 */
	public void sendLogin() {
		sendMapRegion();
		sendWindowPane(548, 0);
		sendGameScreen();
		sendPlayerConfig();
		sendMessage("Welcome to Ieldor.");
		sendMessage("Ieldor is currently in the BETA stage.");
	}

	/**
	 * Sends the player configuration.
	 */
	private void sendPlayerConfig() {		
		this.sendEnergy();
		this.sendOnlineStatus(OnlineStatus.NOBODY);
	}

	/**
	 * Sends the game screen.
	 */
	private void sendGameScreen() {
		sendTab(14, 751); // Chat options
		sendTab(75, 752); // Chatbox
		sendTab(70, 748); // HP bar
		sendTab(71, 749); // Prayer bar
		sendTab(72, 750); // Energy bar
		sendTab(67, 747); // Summoning bar
		sendInterface(1, 752, 8, 137); // Username on chat
		sendTab(83, 92); // Attack tab
		sendTab(84, 320); // Skill tab
		sendTab(85, 274); // Quest tab
		sendTab(86, 149); // Inventory tab
		sendTab(87, 387); // Equipment tab
		sendTab(88, 271); // Prayer tab
		sendTab(89, 192); // Magic tab
		sendTab(91, 550); // Friend tab
		sendTab(92, 551); // Ignore tab
		sendTab(93, 589); // Clan tab
		sendTab(94, 261); // Setting tab
		sendTab(95, 464); // Emote tab
		sendTab(96, 187); // Music tab
		sendTab(97, 182); // Logout tab
		sendTab(10, 754); // PM split chat		
	}

	/**
	 * Sends an tab to the game screen.
	 * @param tab The tab id.
	 * @param child The child id.
	 */
	private void sendTab(int tab, int child) {
		sendInterface(1, child == 137 ? 752 : 548, tab, child);
	}

	/**
	 * Sends an interface to a specified window.
	 * @param show The show id.
	 * @param window The window id.
	 * @param inter The interface id.
	 * @param child The child id.
	 */
	private void sendInterface(int show, int window, int inter, int child) {
		int id = window * 65536 + inter;	
		
		PacketBuf buf = new PacketBuf(155);
		buf.put((byte) show);
		buf.putInt2(id);
		buf.putShortA(interfaceCount++);
		buf.putShort(child);
		
		player.getChannel().write(buf.toPacket());
	}

	/**
	 * Sends the map region data for the players position.
	 */
	public void sendMapRegion() {
		player.setLastPosition(player.getPosition());
		PacketBuf buf = new PacketBuf(162, PacketType.SHORT);
		boolean forceSend = true;
		if((((player.getPosition().getRegionX() / 8) == 48) || ((player.getPosition().getRegionX() / 8) == 49)) && ((player.getPosition().getRegionY() / 8) == 48)) {
			forceSend = false;
		}
		if(((player.getPosition().getRegionX() / 8) == 48) && ((player.getPosition().getRegionY() / 8) == 148)) {
			forceSend = false;
		}
		
		buf.putShortA(player.getPosition().getLocalX());
		for(int xCalc = (player.getPosition().getRegionX() - 6) / 8; xCalc <= ((player.getPosition().getRegionX() + 6) / 8); xCalc++) {
			for(int yCalc = (player.getPosition().getRegionY() - 6) / 8; yCalc <= ((player.getPosition().getRegionY() + 6) / 8); yCalc++) {
				int region = yCalc + (xCalc << 8);
				if(forceSend || ((yCalc != 49) && (yCalc != 149) && (yCalc != 147) && (xCalc != 50) && ((xCalc != 49) || (yCalc != 47)))) {
					int[] regionHash = BinaryLandscapeHandler.get(region);
					if(regionHash == null)
						regionHash = new int[4];
					
					for(int hash : regionHash)
						buf.putInt2(hash);
				}
			}
		}
		
		buf.putByteS(player.getPosition().getHeight());
		buf.putShort(player.getPosition().getRegionX());
		buf.putShortA(player.getPosition().getRegionY());
		buf.putShortA(player.getPosition().getLocalY());
		player.getChannel().write(buf.toPacket());
	}

	/**
	 * Sends the logout to the game screen.
	 */
	public void sendLogout() {
		player.getChannel().write(new PacketBuf(86).toPacket()).addListener(new ChannelFutureListener() {

			/*
			 * (non-Javadoc)
			 * @see io.netty.channel.ChannelFutureListener#operationComplete(io.netty.channel.ChannelFuture)
			 */
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				future.channel().close();
			}
		});
	}

	/**
	 * Sends a packet to update a group of items.
	 * 
	 * @param interfaceId The interface id.
	 * @param childId The child id of the current container.
	 * @param type The type of container we're sending.
	 * @param items The items.
	 * @return The action sender instance, for chaining.
	 */
/*	public void sendContainer(int interfaceId, int childId, int type, Item[] items) {
		PacketBuf buf = new PacketBuf(105, PacketType.SHORT);
		buf.putShort(interfaceId);
		buf.putShort(childId);
		buf.putShort(type);
		buf.putShort(items.length);
		for(int i = 0; i < items.length; i++) {
			Item item = items[i];
			int id, amt;
			if(item == null) {
				id = -1;
				amt = 0;
			} else {
				id = item.getId();
				amt = item.getAmount();
			}
			if(amt > 254) {
				buf.putByteS((byte) 255);
				buf.putInt(amt);
			} else {
				buf.putByteS((byte) amt);
			}
			buf.putShort(id+1);
		}
		player.getChannel().write(buf.toPacket());		
	}*/

	/**
	 * Sends a level of an skill.
	 * @param skillLevel The ID of the skill level.
	 *//*
	public void sendSkillLevel(int skillLevel) {
		PacketBuf buf = new PacketBuf(38);
		buf.putByteA(player.getSkills().currentLevel(skillLevel));
		buf.putInt1((int) player.getSkills().getExp(skillLevel));
		buf.put((byte) skillLevel);
		player.getChannel().write(buf.toPacket());
	}*/

	/**
	 * Sends a message to a player.
	 * @param message the message to send.
	 */
	public void sendMessage(String message) {
		PacketBuf buf = new PacketBuf(70, PacketType.BYTE);
		buf.putString(message);
		player.getChannel().write(buf.toPacket());
	}

	/**
	 * Sends the run energy.
	 */
	public void sendEnergy() {
		PacketBuf buf = new PacketBuf(234);
		buf.put(player.getRunEnergy());
		player.getChannel().write(buf.toPacket());
	}
	
	/**
	 * Sends a player option.
	 * @param option The option.
	 * @param slot The slot.
	 * @param position The position.
	 */
	public void sendPlayerOption(String option, int slot, int position) {
		PacketBuf buf = new PacketBuf(44, PacketType.BYTE);
		buf.putLEShortA(65535);
		buf.put(position);
		buf.put(slot);
		buf.putString(option);
		player.getChannel().write(buf.toPacket());
	}
	
	/**
	 * Sends the npc id.
	 * @param npcId The npc id.
	 * @param interfaceId The interface id.
	 * @param childId The child id.
	 */
	public void sendNpc(int npcId, int interfaceId, int childId) {
		PacketBuf buf = new PacketBuf(73);
		buf.putShortA(npcId);
		buf.putLEInt((interfaceId << 16) + childId);
		buf.putLEShort(interfaceCount++);
		player.getChannel().write(buf.toPacket());
	}
	
	/**
	 * Sends a players head on an interface.
	 * @param interfaceId The interface id.
	 * @param childId The child id.
	 */
	public void sendPlayer(int interfaceId, int childId) {
		PacketBuf buf = new PacketBuf(66);
		buf.putLEShortA(interfaceCount++);
		buf.putInt1((interfaceId << 16) + childId);
		player.getChannel().write(buf.toPacket());
	}
	
	/**
	 * Sends an animate interface.
	 * @param animation The animation.
	 * @param interfaceId The interface id.
	 * @param childId The child id.
	 */
	public void sendAnimateInterface(int animation, int interfaceId, int childId) {
		PacketBuf buf = new PacketBuf(36);
		buf.putInt2((interfaceId << 16) + childId);
		buf.putLEShort(animation);
		buf.putShortA(interfaceCount++);
		player.getChannel().write(buf.toPacket());
	}
	
	/**
	 * Sends the chatbox interface.
	 * @param childId The child id.
	 */
	public void sendChatboxInterface(int childId) {
		sendInterface(0, 752, 11, childId);
	}
	
	/**
	 * Sends an item on an interface.
	 * @param interfaceId The interface id.
	 * @param childId The child id.
	 * @param size The model size.
	 * @param model The model id.
	 */
	public void sendItemOnInterface(int interfaceId, int childId, int size, int model) {
		PacketBuf buf = new PacketBuf(50);
		buf.putInt(size);
		buf.putLEShort(interfaceId);
		buf.putLEShort(childId);
		buf.putLEShortA(model);
		buf.putLEShort(interfaceCount++);
		player.getChannel().write(buf.toPacket());
	}

	/**
	 * Sends an interface.
	 * @param interfaceId The interface id.
	 */
	public void sendInterface(int interfaceId) {
    	sendInterface(0, 548, 11, interfaceId);		
	}
}
