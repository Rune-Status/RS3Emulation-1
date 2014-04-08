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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.ieldor.Constants;
import net.ieldor.config.ClientVarps;
import net.ieldor.config.OutgoingOpcode;
import net.ieldor.game.model.player.Player;
import net.ieldor.game.model.skill.Skill;
import net.ieldor.game.social.Friend;
import net.ieldor.game.social.Ignore;
import net.ieldor.game.social.OnlineStatus;
import net.ieldor.io.Packet;
import net.ieldor.io.PacketBuf;
import net.ieldor.io.Packet.PacketType;
import net.ieldor.modules.worldlist.WorldData;

/**
 * A class used to store the packets (actions) that an {@link Entity} can
 * perform.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class ActionSender {
	
	//TODO: Update packet opcodes to the required revision. If the revision in the comments is not correct (or missing), the packet needs updating
	/*private static final int KEEP_ALIVE_PACKET = 50;//802
	private static final int DYNAMIC_VARP_PACKET = 98;//802
	private static final int FIXED_VARP_PACKET = 136;//802
	
	private static final int UNLOCK_FRIENDS_LIST = 31;//802
	private static final int ONLINE_STATUS_PACKET = 4;//795
	private static final int FRIENDS_PACKET = 80;//802
	private static final int IGNORES_PACKET = 99;//802
	private static final int FRIENDS_CHANNEL_PACKET = 82;
	private static final int CLAN_CHANNEL_PACKET = 47;//795
	
	private static final int STATIC_MAP_REGION_PACKET = 107;//795
	private static final int DYNAMIC_MAP_REGION_PACKET = 63;//795
	public static final int PLAYER_UPDATE_PACKET = 40;//795
	
	private static final int PLAYER_OPTION_PACKET = 120;//795
	private static final int RUN_ENERGY_PACKET = 13;//795
	private static final int SKILL_DATA_PACKET = 87;//795
	
	private static final int INTERFACE_PACKET = 41;//795
	private static final int WINDOW_PANE_PACKET = 86;//802
	
	public static final int WORLD_LIST_PACKET = 156;//802
	private static final int MESSAGE_PACKET = 17;//795
	private static final int FRIENDS_CHAT_MESSAGE_PACKET = 111;*/
	

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
	 * Sends the specified packet to the player, so long as the player's channel is still open.
	 * @param packet the packet to send.
	 */
	public void sendPacket (Packet packet) {
		if (!player.getChannel().isOpen()) {
			return;//If the channel has been closed, don't bother trying to send the packet
		}		
		player.getChannel().write(packet);
	}
	
	/**
	 * Sends a ping (keep-alive) packet back to the client. Used to ensure the connection is not dropped
	 */
	public void sendPing () {
		PacketBuf buf = new PacketBuf(OutgoingOpcode.KEEP_ALIVE_PACKET);
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
		
		PacketBuf buf = new PacketBuf(OutgoingOpcode.WINDOW_PANE_PACKET);
		buf.putByteC(type);
		buf.putLEInt(xteas[0]);
		buf.putLEShortA(id);
		buf.putInt2(xteas[2]);
		buf.putInt2(xteas[3]);
		buf.putLEInt(xteas[1]);
		sendPacket(buf.toPacket());
	}
	
	/**
	 * Sends an interface to a specified window.
	 * @param show The show id.
	 * @param window The window id.
	 * @param inter The interface id.
	 * @param child The child id.
	 */
	private void sendInterface(boolean clipped, int windowID, int windowComponent, int interfaceID) {
		//NOTE: The order and encoding methods of this packet vary between client revisions
		int[] xteas = new int[4];
		
		PacketBuf buf = new PacketBuf(OutgoingOpcode.INTERFACE_PACKET);
		buf.putLEShortA(interfaceID);//Interface ID
		buf.putLEInt(xteas[1]);
		buf.putByteA(clipped ? 1 : 0);//Clipped
		buf.putInt2(xteas[3]);
		buf.putInt1(xteas[0]);
		buf.putInt(xteas[2]);
		buf.putInt(windowID << 16 | windowComponent);//Parent hash
		
		sendPacket(buf.toPacket());
	}
	
	private void sendInterfaceSettings(int iFaceID, int component, int fromSlot, int toSlot, int settingsHash) {
		//NOTE: The order and encoding methods of this packet vary between client revisions		
		PacketBuf buf = new PacketBuf(OutgoingOpcode.INTERFACE_SETTINGS_PACKET);
		buf.putInt1(iFaceID << 16 | component);//Interface Component Hash
		buf.putShortA(toSlot);//The end slot
		buf.putShortA(fromSlot);//The start slot
		buf.putInt2(settingsHash);//The settings hash
		
		sendPacket(buf.toPacket());
	}
	
	/**
	 * Sends a player option.
	 * @param option	The option string
	 * @param slot		The option slot ID
	 * @param top		Whether the option should be at the top
	 * @param cursor	The cursor sprite ID to use
	 */
	public void sendPlayerOption(String option, int slot, boolean top, int cursor) {
		//NOTE: The order and encoding methods of this packet vary between client revisions
		PacketBuf buf = new PacketBuf(OutgoingOpcode.PLAYER_OPTION_PACKET, PacketType.BYTE);
		buf.putByteC(top ? 1 : 0);//isOnTop
		buf.putString(option);
		buf.putByteA(slot);
		buf.putLEShortA(cursor);//Cursor
		player.getChannel().write(buf.toPacket());
	}

	/**
	 * Sends a fixed-sized configuration key-value pair (8 bits). Known as "Config1" on some servers
	 * @param id	 The id (key) of the client varp
	 * @param value The value of the client varp
	 */
	public void sendSmallVarp(int id, int value) {
		//NOTE: The order and encoding methods of this packet vary between client revisions
		PacketBuf buf = new PacketBuf(OutgoingOpcode.SMALL_VARP_PACKET);
		buf.putShortA(id);
		buf.putByteA(value);
		sendPacket(buf.toPacket());
	}

	/**
	 * Sends a dynamic-sized configuration key-value pair (8-32 bits). Known as "Config2" on some servers
	 * @param id	 The id (key) of the client varp
	 * @param value The value of the client varp
	 */
	public void sendLargeVarp(int id, int value) {
		//NOTE: The order and encoding methods of this packet vary between client revisions
		PacketBuf buf = new PacketBuf(OutgoingOpcode.LARGE_VARP_PACKET);
		buf.putLEShortA(id);
		buf.putInt(value);
		sendPacket(buf.toPacket());
	}

	/**
	 * Sends an config (client varp).
	 * @param id 	 The varp id.
	 * @param value The varp value.
	 */
	public void sendVarp(int id, int value) {
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			sendLargeVarp(id, value);
		} else {
			sendSmallVarp(id, value);
		}
	}
	
	/**
	 * Notifies the client of the player's current online status
	 * @param status The player's online status
	 */
	public void sendOnlineStatus(OnlineStatus status) {
		PacketBuf buf = new PacketBuf(OutgoingOpcode.ONLINE_STATUS_PACKET);
		buf.put(status.getCode());
		sendPacket(buf.toPacket());
	}
	
	/**
	 * Sends a packet notifying the client that the friends server is being connected to.
	 * Changes friends list message from "Loading Friends List." to "Connecting to Friend Server."
	 */
	public void sendUnlockFriendsList () {
		PacketBuf buf = new PacketBuf(OutgoingOpcode.UNLOCK_FRIENDS_LIST);
		sendPacket(buf.toPacket());
	}
	
	/**
	 * Sends all the friends currently on a player's friends list (used for friends list initialisation
	 * @param friends A list containing all the friends on the player's friends list
	 */
	public void sendFriends (Collection<Friend> friends) {
		PacketBuf buf = new PacketBuf(OutgoingOpcode.FRIENDS_PACKET, PacketType.SHORT);
		for (Friend f : friends) {
			packFriend(f, false, buf);
		}
		sendPacket(buf.toPacket());
	}
	
	/**
	 * Sends an individual friend to the player. This could be either an update to a friend's details or a friend addition
	 * @param friend 		The details for the specified friend
	 * @param isNameChange	Whether or not this is a notification of a friend changing their name.
	 */
	public void sendFriend (Friend friend, boolean isNameChange) {
		PacketBuf buf = new PacketBuf(OutgoingOpcode.FRIENDS_PACKET, PacketType.SHORT);
		packFriend(friend, isNameChange, buf);
		sendPacket(buf.toPacket());
	}	
	
	/**
	 * Writes the friend details to the specified packet
	 * @param friend		The friend object from which to fetch details
	 * @param isNameChange	Whether the request represents a name change
	 * @param packet		The packet in which to write the friend details
	 */
	public void packFriend(Friend friend, boolean isNameChange, PacketBuf packet) {
		WorldData world = friend.getWorld();
		boolean putOnline = (world != null);
		int flags = 0;
		/*if (friend.isRecruited()) {
			flags |= 0x1;
		}*/
		if (friend.isReferred()) {
			flags |= 0x2;
		}
		
		packet.put(isNameChange ? 1 : 0);//Is this a notification of a friend name change
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
	public void sendIgnores(Collection<Ignore> ignores) {
		PacketBuf buf = new PacketBuf(OutgoingOpcode.IGNORES_PACKET, PacketType.SHORT);
		for (Ignore i : ignores) {
			packIgnore(i, false, buf);
		}
		sendPacket(buf.toPacket());			
	}
	
	/**
	 * Sends an individual ignore to the player. This could either be a new ignore or an update to an existing ignore
	 * @param ignore		The details for the specified ignore
	 * @param isNameChange Whether this notification represents a name change
	 */
	public void sendIgnore(Ignore ignore, boolean isNameChange) {
		PacketBuf buf = new PacketBuf(OutgoingOpcode.IGNORES_PACKET, PacketType.SHORT);
		packIgnore(ignore, isNameChange, buf);
		sendPacket(buf.toPacket());		
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
	 * Sends a game message to the player
	 * @param message	The message to send
	 * @param filtered Whether or not the message can be filtered via the game filter
	 */
	public void sendGameMessage(String message, boolean filtered) {
		sendMessage(message, (filtered ? MessageOpcode.GAME_FILTERED : MessageOpcode.GAME_UNFILTERED), null);
	}
	
	public void sendMessage(String message, MessageOpcode opcode, Player sender) {
		PacketBuf buf = new PacketBuf(OutgoingOpcode.MESSAGE_PACKET, PacketType.BYTE);
		buf.putSmart(opcode.getOpcode());
		buf.putInt(0);//Purpose unknown
		int mask = 0;
		if (sender != null) {
			mask |= 0x1;
			if (sender.hasDifferentDisplayName()) {
				mask |= 0x2;
			}
		}
		buf.put(mask);
		if (sender != null) {
			buf.putString(sender.getDisplayName());
			if (sender.hasDifferentDisplayName()) {
				buf.putString(sender.getDisplayName());//TODO: Use player titles
			}
		}
		buf.putString(message);
		player.getChannel().write(buf.toPacket());
	}
	
	/**
	 * Sends the default login data.
	 */
	public void sendLogin() {
		sendMapRegion(true);
		//player.sendLobbyConfigs(ClientVarps.getGameVarps());
		sendWindowPane(1477, 0);
		sendGameScreen();
		//sendDefaultPlayersOptions();
		//sendRunEnergy();
		//sendGameMessage("Welcome to Ieldor.", false);
		//sendMessage("Ieldor is currently in the BETA stage.");
		//player.getFriendManager().init();
	}

	/**
	 * Sends the default options for other players
	 */
	private void sendDefaultPlayersOptions() {		
		sendPlayerOption("Follow", 2, false);
		sendPlayerOption("Trade with", 4, false);
	}

	/**
	 * Sends the game screen.
	 */
	private void sendGameScreen() {//TODO: Simplify this method...
		sendInterface(true, 1477, 87, 1482);//Interface: id=1482, clipped=1, parent=[1477, 87]
		sendInterface(true, 1477, 313, 1466);//Interface: id=1466, clipped=1, parent=[1477, 313]
		sendInterfaceSettings(1466, 10, 0, 26, 30);//IfaceSettings: 96075786, 26, 0, 30
		sendRunClientScript(8862, new Object[]{ 0, 1});//Runscript: [8862, 0, 1]
		sendInterface(true, 1477, 293, 1220);//Interface: id=1220, clipped=1, parent=[1477, 293]
		sendRunClientScript(8862, new Object[]{ 1, 1});//Runscript: [8862, 1, 1]
		sendInterface(true, 1477, 130, 1473);//Interface: id=1473, clipped=1, parent=[1477, 130]
		sendInterfaceSettings(1473, 8, -1, -1, 2097152);//IfaceSettings: 96534536, -1, -1, 2097152
		sendInterfaceSettings(1473, 8, 0, 27, 15302030);//IfaceSettings: 96534536, 27, 0, 15302030
		sendInterfaceSettings(1473, 0, 0, 27, 1536);//IfaceSettings: 96534528, 27, 0, 1536
		sendRunClientScript(8862, new Object[]{ 2, 1});//Runscript: [8862, 2, 1]
		sendInterface(true, 1477, 202, 1464);//Interface: id=1464, clipped=1, parent=[1477, 202]
		sendInterfaceSettings(1464, 14, 0, 15, 15302654);//IfaceSettings: 95944718, 15, 0, 15302654
		sendInterfaceSettings(1464, 12, 2, 7, 2);//IfaceSettings: 95944716, 7, 2, 2
		sendRunClientScript(8862, new Object[]{ 3, 1});//Runscript: [8862, 3, 1]
		sendInterface(true, 1477, 323, 1458);//Interface: id=1458, clipped=1, parent=[1477, 323]
		sendInterfaceSettings(1458, 24, 0, 28, 8388610);//IfaceSettings: 95551512, 28, 0, 8388610
		sendRunClientScript(8862, new Object[]{ 4, 1});//Runscript: [8862, 4, 1]
		sendInterface(true, 1477, 239, 1460);//Interface: id=1460, clipped=1, parent=[1477, 239]
		sendInterface(true, 1477, 249, 1452);//Interface: id=1452, clipped=1, parent=[1477, 249]
		sendInterface(true, 1477, 259, 1461);//Interface: id=1461, clipped=1, parent=[1477, 259]
		sendInterface(true, 1477, 269, 1449);//Interface: id=1449, clipped=1, parent=[1477, 269]
		sendInterfaceSettings(1460, 1, 0, 168, 10320902);//IfaceSettings: 95682561, 168, 0, 10320902
		sendInterfaceSettings(1452, 1, 0, 168, 10320902);//IfaceSettings: 95158273, 168, 0, 10320902
		sendInterfaceSettings(1461, 1, 0, 168, 10320902);//IfaceSettings: 95748097, 168, 0, 10320902
		sendInterfaceSettings(1449, 1, 0, 168, 10320902);//IfaceSettings: 94961665, 168, 0, 10320902
		sendInterfaceSettings(1460, 4, 6, 14, 2);//IfaceSettings: 95682564, 14, 6, 2
		sendInterfaceSettings(1452, 7, 6, 14, 2);//IfaceSettings: 95158279, 14, 6, 2
		sendInterfaceSettings(1461, 7, 6, 14, 2);//IfaceSettings: 95748103, 14, 6, 2
		sendInterfaceSettings(1449, 7, 6, 14, 2);//IfaceSettings: 94961671, 14, 6, 2
		sendRunClientScript(8862, new Object[]{ 5, 1});//Runscript: [8862, 5, 1]
		sendInterface(true, 1477, 371, 550);//Interface: id=550, clipped=1, parent=[1477, 371]
		sendRunClientScript(8862, new Object[]{ 14, 1});//Runscript: [8862, 14, 1]
		sendInterfaceSettings(550, 25, 0, 500, 510);//IfaceSettings: 36044825, 500, 0, 510
		sendInterfaceSettings(550, 23, 0, 500, 6);//IfaceSettings: 36044823, 500, 0, 6
		sendInterface(true, 1477, 602, 1427);//Interface: id=1427, clipped=1, parent=[1477, 602]
		sendRunClientScript(1303, new Object[]{ 93519895, 1, 1, player.getDisplayName()});//Runscript: [1303, 93519895, 1, 1, Test]
		sendInterfaceSettings(1427, 23, 0, 600, 1024);//IfaceSettings: 93519895, 600, 0, 1024
		sendRunClientScript(8862, new Object[]{ 15, 1});//Runscript: [8862, 15, 1]
		sendInterface(true, 1477, 361, 1110);//Interface: id=1110, clipped=1, parent=[1477, 361]
		sendRunClientScript(8862, new Object[]{ 16, 1});//Runscript: [8862, 16, 1]
		sendInterfaceSettings(1110, 20, 0, 200, 2);//IfaceSettings: 72744980, 200, 0, 2
		sendInterfaceSettings(1110, 25, 0, 600, 2);//IfaceSettings: 72744985, 600, 0, 2
		sendInterfaceSettings(1110, 23, 0, 600, 1024);//IfaceSettings: 72744983, 600, 0, 1024
		sendInterfaceSettings(1110, 14, 0, 600, 1024);//IfaceSettings: 72744974, 600, 0, 1024
		sendInterface(true, 1477, 303, 590);//Interface: id=590, clipped=1, parent=[1477, 303]
		sendRunClientScript(4717, new Object[]{ 38666248, 38666247, 38666249, 3874});//Runscript: [4717, 38666248, 38666247, 38666249, 3874]
		sendInterfaceSettings(590, 8, 0, 169, 8388614);//IfaceSettings: 38666248, 169, 0, 8388614
		sendInterfaceSettings(590, 13, 0, 11, 2);//IfaceSettings: 38666253, 11, 0, 2
		sendRunClientScript(4717, new Object[]{ 94240781, 94240780, 94240782, 3874});//Runscript: [4717, 94240781, 94240780, 94240782, 3874]
		sendRunClientScript(8862, new Object[]{ 9, 1});//Runscript: [8862, 9, 1]
		sendInterface(true, 1477, 341, 1416);//Interface: id=1416, clipped=1, parent=[1477, 341]
		sendInterfaceSettings(1416, 3, 0, 2443, 30);//IfaceSettings: 92798979, 2443, 0, 30
		sendInterfaceSettings(1416, 11, 0, 11, 2359302);//IfaceSettings: 92798987, 11, 0, 2359302
		sendInterfaceSettings(1416, 11, 12, 23, 4);//IfaceSettings: 92798987, 23, 12, 4
		sendInterfaceSettings(1416, 11, 24, 24, 2097152);//IfaceSettings: 92798987, 24, 24, 2097152
		sendRunClientScript(8862, new Object[]{ 10, 1});//Runscript: [8862, 10, 1]
		sendInterface(true, 1477, 351, 1417);//Interface: id=1417, clipped=1, parent=[1477, 351]
		sendInterfaceSettings(1417, 16, 0, 29, 2621470);//IfaceSettings: 92864528, 29, 0, 2621470
		sendRunClientScript(8862, new Object[]{ 11, 1});//Runscript: [8862, 11, 1]
		sendRunClientScript(8862, new Object[]{ 12, 0});//Runscript: [8862, 12, 0]
		sendInterface(true, 1477, 174, 1431);//Interface: id=1431, clipped=1, parent=[1477, 174]
		sendInterface(true, 1477, 835, 568);//Interface: id=568, clipped=1, parent=[1477, 835]
		sendInterfaceSettings(1477, 175, 1, 1, 2);//IfaceSettings: 96796847, 1, 1, 2
		sendInterface(true, 1477, 58, 1430);//Interface: id=1430, clipped=1, parent=[1477, 58]
		sendInterfaceSettings(1477, 80, 1, 1, 4);//IfaceSettings: 96796752, 1, 1, 4
		sendInterfaceSettings(1430, 118, -1, -1, 2098176);//IfaceSettings: 93716598, -1, -1, 2098176
		sendInterfaceSettings(1430, 123, -1, -1, 2098176);//IfaceSettings: 93716603, -1, -1, 2098176
		sendInterfaceSettings(1430, 124, -1, -1, 2098176);//IfaceSettings: 93716604, -1, -1, 2098176
		sendInterfaceSettings(1430, 129, -1, -1, 2098176);//IfaceSettings: 93716609, -1, -1, 2098176
		sendInterfaceSettings(1430, 130, -1, -1, 2098176);//IfaceSettings: 93716610, -1, -1, 2098176
		sendInterfaceSettings(1430, 135, -1, -1, 2098176);//IfaceSettings: 93716615, -1, -1, 2098176
		sendInterfaceSettings(1430, 136, -1, -1, 2098176);//IfaceSettings: 93716616, -1, -1, 2098176
		sendInterfaceSettings(1430, 141, -1, -1, 2098176);//IfaceSettings: 93716621, -1, -1, 2098176
		sendInterfaceSettings(1430, 142, -1, -1, 2098176);//IfaceSettings: 93716622, -1, -1, 2098176
		sendInterfaceSettings(1430, 147, -1, -1, 2098176);//IfaceSettings: 93716627, -1, -1, 2098176
		sendInterfaceSettings(1430, 148, -1, -1, 2098176);//IfaceSettings: 93716628, -1, -1, 2098176
		sendInterfaceSettings(1430, 153, -1, -1, 2098176);//IfaceSettings: 93716633, -1, -1, 2098176
		sendInterfaceSettings(1430, 154, -1, -1, 2098176);//IfaceSettings: 93716634, -1, -1, 2098176
		sendInterfaceSettings(1430, 159, -1, -1, 2098176);//IfaceSettings: 93716639, -1, -1, 2098176
		sendInterfaceSettings(1430, 160, -1, -1, 2098176);//IfaceSettings: 93716640, -1, -1, 2098176
		sendInterfaceSettings(1430, 165, -1, -1, 2098176);//IfaceSettings: 93716645, -1, -1, 2098176
		sendInterfaceSettings(1430, 166, -1, -1, 2098176);//IfaceSettings: 93716646, -1, -1, 2098176
		sendInterfaceSettings(1430, 171, -1, -1, 2098176);//IfaceSettings: 93716651, -1, -1, 2098176
		sendInterfaceSettings(1430, 172, -1, -1, 2098176);//IfaceSettings: 93716652, -1, -1, 2098176
		sendInterfaceSettings(1430, 177, -1, -1, 2098176);//IfaceSettings: 93716657, -1, -1, 2098176
		sendInterfaceSettings(1430, 178, -1, -1, 2098176);//IfaceSettings: 93716658, -1, -1, 2098176
		sendInterfaceSettings(1430, 183, -1, -1, 2098176);//IfaceSettings: 93716663, -1, -1, 2098176
		sendInterfaceSettings(1430, 184, -1, -1, 2098176);//IfaceSettings: 93716664, -1, -1, 2098176
		sendInterfaceSettings(1430, 189, -1, -1, 2098176);//IfaceSettings: 93716669, -1, -1, 2098176
		sendInterfaceSettings(1458, 24, 0, 28, 8388610);//IfaceSettings: 95551512, 28, 0, 8388610
		sendInterfaceSettings(1430, 10, -1, -1, 8388608);//IfaceSettings: 93716490, -1, -1, 8388608
		sendInterfaceSettings(1430, 8, -1, -1, 8650758);//IfaceSettings: 93716488, -1, -1, 8650758
		sendInterfaceSettings(1430, 11, -1, -1, 8388608);//IfaceSettings: 93716491, -1, -1, 8388608
		sendInterfaceSettings(1460, 1, 0, 168, 8485894);//IfaceSettings: 95682561, 168, 0, 8485894
		sendInterfaceSettings(1452, 1, 0, 168, 8485894);//IfaceSettings: 95158273, 168, 0, 8485894
		sendInterfaceSettings(1461, 1, 0, 168, 8485894);//IfaceSettings: 95748097, 168, 0, 8485894
		sendInterfaceSettings(1449, 1, 0, 168, 8485894);//IfaceSettings: 94961665, 168, 0, 8485894
		sendInterfaceSettings(590, 8, 0, 169, 8388614);//IfaceSettings: 38666248, 169, 0, 8388614
		sendInterface(true, 1477, 60, 1465);//Interface: id=1465, clipped=1, parent=[1477, 60]
		sendInterfaceSettings(1477, 82, 1, 1, 6);//IfaceSettings: 96796754, 1, 1, 6
		sendInterface(true, 1477, 34, 1433);//Interface: id=1433, clipped=1, parent=[1477, 34]
		sendInterface(true, 1477, 390, 1483);//Interface: id=1483, clipped=1, parent=[1477, 390]
		sendInterface(true, 1477, 409, 745);//Interface: id=745, clipped=1, parent=[1477, 409]
		sendInterface(true, 1477, 386, 1485);//Interface: id=1485, clipped=1, parent=[1477, 386]
		sendInterface(true, 1477, 0, 1213);//Interface: id=1213, clipped=1, parent=[1477, 0]
		sendInterface(true, 1477, 76, 1448);//Interface: id=1448, clipped=1, parent=[1477, 76]
		sendInterface(true, 1477, 832, 557);//Interface: id=557, clipped=1, parent=[1477, 832]
		sendInterface(true, 1477, 18, 1484);//Interface: id=1484, clipped=1, parent=[1477, 18]
		sendInterface(true, 1477, 106, 137);//Interface: id=137, clipped=1, parent=[1477, 106]
		sendInterface(true, 1477, 178, 1467);//Interface: id=1467, clipped=1, parent=[1477, 178]
		sendInterface(true, 1477, 186, 1472);//Interface: id=1472, clipped=1, parent=[1477, 186]
		sendInterface(true, 1477, 194, 1471);//Interface: id=1471, clipped=1, parent=[1477, 194]
		sendInterface(true, 1477, 333, 1470);//Interface: id=1470, clipped=1, parent=[1477, 333]
		sendInterface(true, 1477, 824, 464);//Interface: id=464, clipped=1, parent=[1477, 824]
		sendInterface(true, 1477, 222, 182);//Interface: id=182, clipped=1, parent=[1477, 222]
		sendInterfaceSettings(137, 90, 0, 99, 2046);//IfaceSettings: 8978522, 99, 0, 2046
		sendInterfaceSettings(1467, 61, 0, 99, 2046);//IfaceSettings: 96141373, 99, 0, 2046
		sendInterfaceSettings(1472, 61, 0, 99, 2046);//IfaceSettings: 96469053, 99, 0, 2046
		sendInterfaceSettings(1471, 60, 0, 99, 2046);//IfaceSettings: 96403516, 99, 0, 2046
		sendInterfaceSettings(1470, 60, 0, 99, 2046);//IfaceSettings: 96337980, 99, 0, 2046
		sendInterfaceSettings(464, 63, 0, 99, 2046);//IfaceSettings: 30408767, 99, 0, 2046
		sendInterfaceSettings(182, 0, 0, 99, 2046);//IfaceSettings: 11927552, 99, 0, 2046
		sendInterfaceSettings(1477, 63, -1, -1, 2097152);//IfaceSettings: 96796735, -1, -1, 2097152
		sendRunClientScript(139, new Object[]{ 96796736});//Runscript: [139, 96796736]
		sendInterface(true, 1477, 38, 1488);//Interface: id=1488, clipped=1, parent=[1477, 38]
		sendRunClientScript(8778, null);//Runscript: [8778]
		sendRunClientScript(4704, null);//Runscript: [4704]
		sendRunClientScript(4308, new Object[]{ 18, 0});//Runscript: [4308, 18, 0]
		sendInterface(true, 1477, 159, 669);//Interface: id=669, clipped=1, parent=[1477, 159]
		sendInterface(true, 1477, 21, 1215);//Interface: id=1215, clipped=1, parent=[1477, 21]
		sendRunClientScript(5559, new Object[]{ 3122093});//Runscript: [5559, 3122093]
		sendRunClientScript(5559, new Object[]{ 3122093});//Runscript: [5559, 3122093]
		sendRunClientScript(5557, new Object[]{ 1});//Runscript: [5557, 1]
		sendRunClientScript(6501, null);//Runscript: [6501]
		sendRunClientScript(279, null);//Runscript: [279]
		sendInterfaceSettings(1477, 173, 1, 7, 9175040);//IfaceSettings: 96796845, 7, 1, 9175040
		sendInterfaceSettings(1477, 173, 11, 13, 9175040);//IfaceSettings: 96796845, 13, 11, 9175040
		sendInterfaceSettings(1477, 173, 0, 0, 9175040);//IfaceSettings: 96796845, 0, 0, 9175040
		sendInterfaceSettings(1477, 78, -1, -1, 2097152);//IfaceSettings: 96796750, -1, -1, 2097152
		sendInterfaceSettings(1477, 57, 1, 7, 9175040);//IfaceSettings: 96796729, 7, 1, 9175040
		sendInterfaceSettings(1477, 57, 11, 13, 9175040);//IfaceSettings: 96796729, 13, 11, 9175040
		sendInterfaceSettings(1477, 57, 0, 0, 9175040);//IfaceSettings: 96796729, 0, 0, 9175040
		sendInterfaceSettings(1477, 79, -1, -1, 2097152);//IfaceSettings: 96796751, -1, -1, 2097152
		sendInterfaceSettings(1477, 62, 1, 7, 9175040);//IfaceSettings: 96796734, 7, 1, 9175040
		sendInterfaceSettings(1477, 62, 11, 13, 9175040);//IfaceSettings: 96796734, 13, 11, 9175040
		sendInterfaceSettings(1477, 62, 0, 0, 9175040);//IfaceSettings: 96796734, 0, 0, 9175040
		sendInterfaceSettings(1477, 62, 3, 4, 9175040);//IfaceSettings: 96796734, 4, 3, 9175040
		sendInterfaceSettings(1477, 81, -1, -1, 2097152);//IfaceSettings: 96796753, -1, -1, 2097152
		sendInterfaceSettings(1477, 109, 1, 7, 9175040);//IfaceSettings: 96796781, 7, 1, 9175040
		sendInterfaceSettings(1477, 109, 11, 13, 9175040);//IfaceSettings: 96796781, 13, 11, 9175040
		sendInterfaceSettings(1477, 109, 0, 0, 9175040);//IfaceSettings: 96796781, 0, 0, 9175040
		sendInterfaceSettings(1477, 109, 3, 4, 9175040);//IfaceSettings: 96796781, 4, 3, 9175040
		sendInterfaceSettings(1477, 99, -1, -1, 2097152);//IfaceSettings: 96796771, -1, -1, 2097152
		sendInterfaceSettings(1477, 180, 1, 7, 9175040);//IfaceSettings: 96796852, 7, 1, 9175040
		sendInterfaceSettings(1477, 180, 11, 13, 9175040);//IfaceSettings: 96796852, 13, 11, 9175040
		sendInterfaceSettings(1477, 180, 0, 0, 9175040);//IfaceSettings: 96796852, 0, 0, 9175040
		sendInterfaceSettings(1477, 180, 3, 4, 9175040);//IfaceSettings: 96796852, 4, 3, 9175040
		sendInterfaceSettings(1477, 100, -1, -1, 2097152);//IfaceSettings: 96796772, -1, -1, 2097152
		sendInterfaceSettings(1477, 188, 1, 7, 9175040);//IfaceSettings: 96796860, 7, 1, 9175040
		sendInterfaceSettings(1477, 188, 11, 13, 9175040);//IfaceSettings: 96796860, 13, 11, 9175040
		sendInterfaceSettings(1477, 188, 0, 0, 9175040);//IfaceSettings: 96796860, 0, 0, 9175040
		sendInterfaceSettings(1477, 188, 3, 4, 9175040);//IfaceSettings: 96796860, 4, 3, 9175040
		sendInterfaceSettings(1477, 101, -1, -1, 2097152);//IfaceSettings: 96796773, -1, -1, 2097152
		sendInterfaceSettings(1477, 196, 1, 7, 9175040);//IfaceSettings: 96796868, 7, 1, 9175040
		sendInterfaceSettings(1477, 196, 11, 13, 9175040);//IfaceSettings: 96796868, 13, 11, 9175040
		sendInterfaceSettings(1477, 196, 0, 0, 9175040);//IfaceSettings: 96796868, 0, 0, 9175040
		sendInterfaceSettings(1477, 196, 3, 4, 9175040);//IfaceSettings: 96796868, 4, 3, 9175040
		sendInterfaceSettings(1477, 102, -1, -1, 2097152);//IfaceSettings: 96796774, -1, -1, 2097152
		sendInterfaceSettings(1477, 335, 1, 7, 9175040);//IfaceSettings: 96797007, 7, 1, 9175040
		sendInterfaceSettings(1477, 335, 11, 13, 9175040);//IfaceSettings: 96797007, 13, 11, 9175040
		sendInterfaceSettings(1477, 335, 0, 0, 9175040);//IfaceSettings: 96797007, 0, 0, 9175040
		sendInterfaceSettings(1477, 335, 3, 4, 9175040);//IfaceSettings: 96797007, 4, 3, 9175040
		sendInterfaceSettings(1477, 103, -1, -1, 2097152);//IfaceSettings: 96796775, -1, -1, 2097152
		sendInterfaceSettings(1477, 826, 1, 7, 9175040);//IfaceSettings: 96797498, 7, 1, 9175040
		sendInterfaceSettings(1477, 826, 11, 13, 9175040);//IfaceSettings: 96797498, 13, 11, 9175040
		sendInterfaceSettings(1477, 826, 0, 0, 9175040);//IfaceSettings: 96797498, 0, 0, 9175040
		sendInterfaceSettings(1477, 826, 3, 4, 9175040);//IfaceSettings: 96797498, 4, 3, 9175040
		sendInterfaceSettings(1477, 104, -1, -1, 2097152);//IfaceSettings: 96796776, -1, -1, 2097152
		sendInterfaceSettings(1477, 306, 1, 7, 9175040);//IfaceSettings: 96796978, 7, 1, 9175040
		sendInterfaceSettings(1477, 306, 11, 13, 9175040);//IfaceSettings: 96796978, 13, 11, 9175040
		sendInterfaceSettings(1477, 306, 0, 0, 9175040);//IfaceSettings: 96796978, 0, 0, 9175040
		sendInterfaceSettings(1477, 306, 3, 4, 9175040);//IfaceSettings: 96796978, 4, 3, 9175040
		sendInterfaceSettings(1477, 97, -1, -1, 2097152);//IfaceSettings: 96796769, -1, -1, 2097152
		sendInterfaceSettings(1477, 133, 1, 7, 9175040);//IfaceSettings: 96796805, 7, 1, 9175040
		sendInterfaceSettings(1477, 133, 11, 13, 9175040);//IfaceSettings: 96796805, 13, 11, 9175040
		sendInterfaceSettings(1477, 133, 0, 0, 9175040);//IfaceSettings: 96796805, 0, 0, 9175040
		sendInterfaceSettings(1477, 133, 3, 4, 9175040);//IfaceSettings: 96796805, 4, 3, 9175040
		sendInterfaceSettings(1477, 98, -1, -1, 2097152);//IfaceSettings: 96796770, -1, -1, 2097152
		sendInterfaceSettings(1477, 134, 1, 1, 2);//IfaceSettings: 96796806, 1, 1, 2
		sendInterfaceSettings(1477, 242, 1, 7, 9175040);//IfaceSettings: 96796914, 7, 1, 9175040
		sendInterfaceSettings(1477, 242, 11, 13, 9175040);//IfaceSettings: 96796914, 13, 11, 9175040
		sendInterfaceSettings(1477, 242, 0, 0, 9175040);//IfaceSettings: 96796914, 0, 0, 9175040
		sendInterfaceSettings(1477, 242, 3, 4, 9175040);//IfaceSettings: 96796914, 4, 3, 9175040
		sendInterfaceSettings(1477, 117, -1, -1, 2097152);//IfaceSettings: 96796789, -1, -1, 2097152
		sendInterfaceSettings(1477, 243, 1, 1, 2);//IfaceSettings: 96796915, 1, 1, 2
		sendInterfaceSettings(1477, 252, 1, 7, 9175040);//IfaceSettings: 96796924, 7, 1, 9175040
		sendInterfaceSettings(1477, 252, 11, 13, 9175040);//IfaceSettings: 96796924, 13, 11, 9175040
		sendInterfaceSettings(1477, 252, 0, 0, 9175040);//IfaceSettings: 96796924, 0, 0, 9175040
		sendInterfaceSettings(1477, 252, 3, 4, 9175040);//IfaceSettings: 96796924, 4, 3, 9175040
		sendInterfaceSettings(1477, 118, -1, -1, 2097152);//IfaceSettings: 96796790, -1, -1, 2097152
		sendInterfaceSettings(1477, 253, 1, 1, 2);//IfaceSettings: 96796925, 1, 1, 2
		sendInterfaceSettings(1477, 262, 1, 7, 9175040);//IfaceSettings: 96796934, 7, 1, 9175040
		sendInterfaceSettings(1477, 262, 11, 13, 9175040);//IfaceSettings: 96796934, 13, 11, 9175040
		sendInterfaceSettings(1477, 262, 0, 0, 9175040);//IfaceSettings: 96796934, 0, 0, 9175040
		sendInterfaceSettings(1477, 262, 3, 4, 9175040);//IfaceSettings: 96796934, 4, 3, 9175040
		sendInterfaceSettings(1477, 119, -1, -1, 2097152);//IfaceSettings: 96796791, -1, -1, 2097152
		sendInterfaceSettings(1477, 263, 1, 1, 2);//IfaceSettings: 96796935, 1, 1, 2
		sendInterfaceSettings(1477, 272, 1, 7, 9175040);//IfaceSettings: 96796944, 7, 1, 9175040
		sendInterfaceSettings(1477, 272, 11, 13, 9175040);//IfaceSettings: 96796944, 13, 11, 9175040
		sendInterfaceSettings(1477, 272, 0, 0, 9175040);//IfaceSettings: 96796944, 0, 0, 9175040
		sendInterfaceSettings(1477, 272, 3, 4, 9175040);//IfaceSettings: 96796944, 4, 3, 9175040
		sendInterfaceSettings(1477, 120, -1, -1, 2097152);//IfaceSettings: 96796792, -1, -1, 2097152
		sendInterfaceSettings(1477, 273, 1, 1, 2);//IfaceSettings: 96796945, 1, 1, 2
		sendInterfaceSettings(1477, 205, 1, 7, 9175040);//IfaceSettings: 96796877, 7, 1, 9175040
		sendInterfaceSettings(1477, 205, 11, 13, 9175040);//IfaceSettings: 96796877, 13, 11, 9175040
		sendInterfaceSettings(1477, 205, 0, 0, 9175040);//IfaceSettings: 96796877, 0, 0, 9175040
		sendInterfaceSettings(1477, 205, 3, 4, 9175040);//IfaceSettings: 96796877, 4, 3, 9175040
		sendInterfaceSettings(1477, 114, -1, -1, 2097152);//IfaceSettings: 96796786, -1, -1, 2097152
		sendInterfaceSettings(1477, 206, 1, 1, 2);//IfaceSettings: 96796878, 1, 1, 2
		sendInterfaceSettings(1477, 215, 1, 7, 9175040);//IfaceSettings: 96796887, 7, 1, 9175040
		sendInterfaceSettings(1477, 215, 11, 13, 9175040);//IfaceSettings: 96796887, 13, 11, 9175040
		sendInterfaceSettings(1477, 215, 0, 0, 9175040);//IfaceSettings: 96796887, 0, 0, 9175040
		sendInterfaceSettings(1477, 215, 3, 4, 9175040);//IfaceSettings: 96796887, 4, 3, 9175040
		sendInterfaceSettings(1477, 115, -1, -1, 2097152);//IfaceSettings: 96796787, -1, -1, 2097152
		sendInterfaceSettings(1477, 216, 1, 1, 2);//IfaceSettings: 96796888, 1, 1, 2
		sendInterfaceSettings(1477, 296, 1, 7, 9175040);//IfaceSettings: 96796968, 7, 1, 9175040
		sendInterfaceSettings(1477, 296, 11, 13, 9175040);//IfaceSettings: 96796968, 13, 11, 9175040
		sendInterfaceSettings(1477, 296, 0, 0, 9175040);//IfaceSettings: 96796968, 0, 0, 9175040
		sendInterfaceSettings(1477, 296, 3, 4, 9175040);//IfaceSettings: 96796968, 4, 3, 9175040
		sendInterfaceSettings(1477, 123, -1, -1, 2097152);//IfaceSettings: 96796795, -1, -1, 2097152
		sendInterfaceSettings(1477, 297, 1, 1, 2);//IfaceSettings: 96796969, 1, 1, 2
		sendInterfaceSettings(1477, 281, 1, 7, 9175040);//IfaceSettings: 96796953, 7, 1, 9175040
		sendInterfaceSettings(1477, 281, 11, 13, 9175040);//IfaceSettings: 96796953, 13, 11, 9175040
		sendInterfaceSettings(1477, 281, 0, 0, 9175040);//IfaceSettings: 96796953, 0, 0, 9175040
		sendInterfaceSettings(1477, 281, 3, 4, 9175040);//IfaceSettings: 96796953, 4, 3, 9175040
		sendInterfaceSettings(1477, 121, -1, -1, 2097152);//IfaceSettings: 96796793, -1, -1, 2097152
		sendInterfaceSettings(1477, 282, 1, 1, 2);//IfaceSettings: 96796954, 1, 1, 2
		sendInterfaceSettings(1477, 316, 1, 7, 9175040);//IfaceSettings: 96796988, 7, 1, 9175040
		sendInterfaceSettings(1477, 316, 11, 13, 9175040);//IfaceSettings: 96796988, 13, 11, 9175040
		sendInterfaceSettings(1477, 316, 0, 0, 9175040);//IfaceSettings: 96796988, 0, 0, 9175040
		sendInterfaceSettings(1477, 316, 3, 4, 9175040);//IfaceSettings: 96796988, 4, 3, 9175040
		sendInterfaceSettings(1477, 122, -1, -1, 2097152);//IfaceSettings: 96796794, -1, -1, 2097152
		sendInterfaceSettings(1477, 317, 1, 1, 2);//IfaceSettings: 96796989, 1, 1, 2
		sendInterfaceSettings(1477, 326, 1, 7, 9175040);//IfaceSettings: 96796998, 7, 1, 9175040
		sendInterfaceSettings(1477, 326, 11, 13, 9175040);//IfaceSettings: 96796998, 13, 11, 9175040
		sendInterfaceSettings(1477, 326, 0, 0, 9175040);//IfaceSettings: 96796998, 0, 0, 9175040
		sendInterfaceSettings(1477, 326, 3, 4, 9175040);//IfaceSettings: 96796998, 4, 3, 9175040
		sendInterfaceSettings(1477, 116, -1, -1, 2097152);//IfaceSettings: 96796788, -1, -1, 2097152
		sendInterfaceSettings(1477, 327, 1, 1, 2);//IfaceSettings: 96796999, 1, 1, 2
		sendInterfaceSettings(1477, 344, 1, 7, 9175040);//IfaceSettings: 96797016, 7, 1, 9175040
		sendInterfaceSettings(1477, 344, 11, 13, 9175040);//IfaceSettings: 96797016, 13, 11, 9175040
		sendInterfaceSettings(1477, 344, 0, 0, 9175040);//IfaceSettings: 96797016, 0, 0, 9175040
		sendInterfaceSettings(1477, 344, 3, 4, 9175040);//IfaceSettings: 96797016, 4, 3, 9175040
		sendInterfaceSettings(1477, 124, -1, -1, 2097152);//IfaceSettings: 96796796, -1, -1, 2097152
		sendInterfaceSettings(1477, 345, 1, 1, 2);//IfaceSettings: 96797017, 1, 1, 2
		sendInterfaceSettings(1477, 354, 1, 7, 9175040);//IfaceSettings: 96797026, 7, 1, 9175040
		sendInterfaceSettings(1477, 354, 11, 13, 9175040);//IfaceSettings: 96797026, 13, 11, 9175040
		sendInterfaceSettings(1477, 354, 0, 0, 9175040);//IfaceSettings: 96797026, 0, 0, 9175040
		sendInterfaceSettings(1477, 354, 3, 4, 9175040);//IfaceSettings: 96797026, 4, 3, 9175040
		sendInterfaceSettings(1477, 127, -1, -1, 2097152);//IfaceSettings: 96796799, -1, -1, 2097152
		sendInterfaceSettings(1477, 355, 1, 1, 2);//IfaceSettings: 96797027, 1, 1, 2
		sendInterfaceSettings(1477, 374, 1, 7, 9175040);//IfaceSettings: 96797046, 7, 1, 9175040
		sendInterfaceSettings(1477, 374, 11, 13, 9175040);//IfaceSettings: 96797046, 13, 11, 9175040
		sendInterfaceSettings(1477, 374, 0, 0, 9175040);//IfaceSettings: 96797046, 0, 0, 9175040
		sendInterfaceSettings(1477, 374, 3, 4, 9175040);//IfaceSettings: 96797046, 4, 3, 9175040
		sendInterfaceSettings(1477, 125, -1, -1, 2097152);//IfaceSettings: 96796797, -1, -1, 2097152
		sendInterfaceSettings(1477, 375, 1, 1, 2);//IfaceSettings: 96797047, 1, 1, 2
		sendInterfaceSettings(1477, 364, 1, 7, 9175040);//IfaceSettings: 96797036, 7, 1, 9175040
		sendInterfaceSettings(1477, 364, 11, 13, 9175040);//IfaceSettings: 96797036, 13, 11, 9175040
		sendInterfaceSettings(1477, 364, 0, 0, 9175040);//IfaceSettings: 96797036, 0, 0, 9175040
		sendInterfaceSettings(1477, 364, 3, 4, 9175040);//IfaceSettings: 96797036, 4, 3, 9175040
		sendInterfaceSettings(1477, 126, -1, -1, 2097152);//IfaceSettings: 96796798, -1, -1, 2097152
		sendInterfaceSettings(1477, 365, 1, 1, 2);//IfaceSettings: 96797037, 1, 1, 2
		sendInterfaceSettings(1477, 605, 1, 7, 9175040);//IfaceSettings: 96797277, 7, 1, 9175040
		sendInterfaceSettings(1477, 605, 11, 13, 9175040);//IfaceSettings: 96797277, 13, 11, 9175040
		sendInterfaceSettings(1477, 605, 0, 0, 9175040);//IfaceSettings: 96797277, 0, 0, 9175040
		sendInterfaceSettings(1477, 605, 3, 4, 9175040);//IfaceSettings: 96797277, 4, 3, 9175040
		sendInterfaceSettings(1477, 128, -1, -1, 2097152);//IfaceSettings: 96796800, -1, -1, 2097152
		sendInterfaceSettings(1477, 606, 1, 1, 2);//IfaceSettings: 96797278, 1, 1, 2
		sendInterfaceSettings(1477, 407, 1, 7, 9175040);//IfaceSettings: 96797079, 7, 1, 9175040
		sendInterfaceSettings(1477, 407, 11, 13, 9175040);//IfaceSettings: 96797079, 13, 11, 9175040
		sendInterfaceSettings(1477, 407, 0, 0, 9175040);//IfaceSettings: 96797079, 0, 0, 9175040
		sendInterfaceSettings(1477, 407, 3, 4, 9175040);//IfaceSettings: 96797079, 4, 3, 9175040
		sendInterfaceSettings(1477, 235, 1, 2, 9175040);//IfaceSettings: 96796907, 2, 1, 9175040
		sendInterfaceSettings(1477, 235, 0, 0, 9175040);//IfaceSettings: 96796907, 0, 0, 9175040
		sendInterfaceSettings(1477, 235, 3, 4, 9175040);//IfaceSettings: 96796907, 4, 3, 9175040
		sendInterfaceSettings(1477, 237, 1, 2, 9175040);//IfaceSettings: 96796909, 2, 1, 9175040
		sendInterfaceSettings(1477, 237, 0, 0, 9175040);//IfaceSettings: 96796909, 0, 0, 9175040
		sendInterfaceSettings(1477, 237, 3, 4, 9175040);//IfaceSettings: 96796909, 4, 3, 9175040
		sendInterfaceSettings(1477, 290, 0, 0, 9175040);//IfaceSettings: 96796962, 0, 0, 9175040
		sendInterfaceSettings(1477, 51, -1, -1, 2097152);//IfaceSettings: 96796723, -1, -1, 2097152
		sendInterfaceSettings(1477, 291, 1, 1, 2);//IfaceSettings: 96796963, 1, 1, 2
		sendInterfaceSettings(1477, 387, 1, 2, 9175040);//IfaceSettings: 96797059, 2, 1, 9175040
		sendInterfaceSettings(1477, 387, 0, 0, 9175040);//IfaceSettings: 96797059, 0, 0, 9175040
		sendInterfaceSettings(1477, 387, 3, 4, 9175040);//IfaceSettings: 96797059, 4, 3, 9175040
		sendInterfaceSettings(1477, 391, 1, 2, 9175040);//IfaceSettings: 96797063, 2, 1, 9175040
		sendInterfaceSettings(1477, 391, 0, 0, 9175040);//IfaceSettings: 96797063, 0, 0, 9175040
		sendInterfaceSettings(1477, 391, 3, 4, 9175040);//IfaceSettings: 96797063, 4, 3, 9175040
		sendInterfaceSettings(1477, 395, 1, 2, 9175040);//IfaceSettings: 96797067, 2, 1, 9175040
		sendInterfaceSettings(1477, 395, 0, 0, 9175040);//IfaceSettings: 96797067, 0, 0, 9175040
		sendInterfaceSettings(1477, 395, 3, 4, 9175040);//IfaceSettings: 96797067, 4, 3, 9175040
		sendInterfaceSettings(1477, 404, 1, 2, 9175040);//IfaceSettings: 96797076, 2, 1, 9175040
		sendInterfaceSettings(1477, 404, 0, 0, 9175040);//IfaceSettings: 96797076, 0, 0, 9175040
		sendInterfaceSettings(1477, 404, 3, 4, 9175040);//IfaceSettings: 96797076, 4, 3, 9175040
		sendInterfaceSettings(1477, 383, 1, 2, 9175040);//IfaceSettings: 96797055, 2, 1, 9175040
		sendInterfaceSettings(1477, 383, 0, 0, 9175040);//IfaceSettings: 96797055, 0, 0, 9175040
		sendInterfaceSettings(1477, 383, 3, 4, 9175040);//IfaceSettings: 96797055, 4, 3, 9175040
		sendInterfaceSettings(1477, 410, 1, 2, 9175040);//IfaceSettings: 96797082, 2, 1, 9175040
		sendInterfaceSettings(1477, 410, 0, 0, 9175040);//IfaceSettings: 96797082, 0, 0, 9175040
		sendInterfaceSettings(1477, 410, 3, 4, 9175040);//IfaceSettings: 96797082, 4, 3, 9175040
		sendInterfaceSettings(1477, 22, 1, 2, 9175040);//IfaceSettings: 96796694, 2, 1, 9175040
		sendInterfaceSettings(1477, 22, 0, 0, 9175040);//IfaceSettings: 96796694, 0, 0, 9175040
		sendInterfaceSettings(1477, 22, 3, 4, 9175040);//IfaceSettings: 96796694, 4, 3, 9175040
		sendInterfaceSettings(1477, 19, 1, 2, 9175040);//IfaceSettings: 96796691, 2, 1, 9175040
		sendInterfaceSettings(1477, 19, 0, 0, 9175040);//IfaceSettings: 96796691, 0, 0, 9175040
		sendInterfaceSettings(1477, 19, 3, 4, 9175040);//IfaceSettings: 96796691, 4, 3, 9175040
		sendInterfaceSettings(1477, 16, 1, 2, 9175040);//IfaceSettings: 96796688, 2, 1, 9175040
		sendInterfaceSettings(1477, 16, 5, 5, 9175040);//IfaceSettings: 96796688, 5, 5, 9175040
		sendInterfaceSettings(1477, 16, 11, 13, 9175040);//IfaceSettings: 96796688, 13, 11, 9175040
		sendInterfaceSettings(1477, 16, 0, 0, 9175040);//IfaceSettings: 96796688, 0, 0, 9175040
		sendInterfaceSettings(1477, 16, 3, 4, 9175040);//IfaceSettings: 96796688, 4, 3, 9175040
		sendInterfaceSettings(1477, 728, 1, 2, 9175040);//IfaceSettings: 96797400, 2, 1, 9175040
		sendInterfaceSettings(1477, 728, 0, 0, 9175040);//IfaceSettings: 96797400, 0, 0, 9175040
		sendInterfaceSettings(1477, 728, 3, 4, 9175040);//IfaceSettings: 96797400, 4, 3, 9175040
		sendInterfaceSettings(1477, 223, 1, 7, 9175040);//IfaceSettings: 96796895, 7, 1, 9175040
		sendInterfaceSettings(1477, 223, 11, 13, 9175040);//IfaceSettings: 96796895, 13, 11, 9175040
		sendInterfaceSettings(1477, 223, 0, 0, 9175040);//IfaceSettings: 96796895, 0, 0, 9175040
		sendInterfaceSettings(1477, 223, 3, 4, 9175040);//IfaceSettings: 96796895, 4, 3, 9175040
		sendInterfaceSettings(1477, 55, 1, 2, 9175040);//IfaceSettings: 96796727, 2, 1, 9175040
		sendInterfaceSettings(1477, 55, 0, 0, 9175040);//IfaceSettings: 96796727, 0, 0, 9175040
		sendInterfaceSettings(1477, 55, 3, 4, 9175040);//IfaceSettings: 96796727, 4, 3, 9175040
		sendInterfaceSettings(1477, 12, -1, -1, 2097152);//IfaceSettings: 96796684, -1, -1, 2097152
		sendInterfaceSettings(1477, 77, 1, 1, 2);//IfaceSettings: 96796749, 1, 1, 2
		sendInterfaceSettings(1477, 96, 1, 7, 9175040);//IfaceSettings: 96796768, 7, 1, 9175040
		sendInterfaceSettings(1477, 96, 11, 13, 9175040);//IfaceSettings: 96796768, 13, 11, 9175040
		sendInterfaceSettings(1477, 96, 0, 0, 9175040);//IfaceSettings: 96796768, 0, 0, 9175040
		sendInterfaceSettings(1477, 96, 3, 4, 9175040);//IfaceSettings: 96796768, 4, 3, 9175040
		sendInterfaceSettings(1477, 7, -1, -1, 2097152);//IfaceSettings: 96796679, -1, -1, 2097152
		sendInterfaceSettings(1477, 833, 1, 2, 9175040);//IfaceSettings: 96797505, 2, 1, 9175040
		sendInterfaceSettings(1477, 833, 0, 0, 9175040);//IfaceSettings: 96797505, 0, 0, 9175040
		sendInterfaceSettings(1477, 833, 3, 4, 9175040);//IfaceSettings: 96797505, 4, 3, 9175040
		/*sendInterface(true, 1477, 87, 1482);//Interface: id=1482, clipped=1, parent=[1477, 87]
		sendInterface(true, 1477, 313, 1466);//Interface: id=1466, clipped=1, parent=[1477, 313]
		//sendInterfaceSettings(96075786, 0, 26, 30);//IfaceSettings: 96075786, 26, 0, 30
		sendInterface(true, 1477, 293, 1220);//Interface: id=1220, clipped=1, parent=[1477, 293] (Active task)
		sendInterface(true, 1477, 130, 1473);//Interface: id=1473, clipped=1, parent=[1477, 130] (Inventory)
		sendInterface(true, 1477, 202, 1464);//Interface: id=1464, clipped=1, parent=[1477, 202]
		sendInterface(true, 1477, 323, 1458);//Interface: id=1458, clipped=1, parent=[1477, 323] (Prayer points)
		sendInterface(true, 1477, 239, 1460);//Interface: id=1460, clipped=1, parent=[1477, 239]
		sendInterface(true, 1477, 249, 1452);//Interface: id=1452, clipped=1, parent=[1477, 249]
		sendInterface(true, 1477, 259, 1461);//Interface: id=1461, clipped=1, parent=[1477, 259]
		sendInterface(true, 1477, 269, 1449);//Interface: id=1449, clipped=1, parent=[1477, 269]
		sendInterface(true, 1477, 371, 550);//Interface: id=550, clipped=1, parent=[1477, 371] (Friends list)
		sendInterface(true, 1477, 602, 1427);//Interface: id=1427, clipped=1, parent=[1477, 602] (Friends chat)
		sendInterface(true, 1477, 361, 1110);//Interface: id=1110, clipped=1, parent=[1477, 361] (Clan chat)
		sendInterface(true, 1477, 303, 590);//Interface: id=590, clipped=1, parent=[1477, 303]
		sendInterface(true, 1477, 341, 1416);//Interface: id=1416, clipped=1, parent=[1477, 341] (Music player)
		sendInterface(true, 1477, 351, 1417);//Interface: id=1417, clipped=1, parent=[1477, 351] (Notes)
		sendInterface(true, 1477, 174, 1431);//Interface: id=1431, clipped=1, parent=[1477, 174] (Launcher bar (links to settings, social, powers, etc))
		sendInterface(true, 1477, 835, 568);//Interface: id=568, clipped=1, parent=[1477, 835]
		sendInterface(true, 1477, 58, 1430);//Interface: id=1430, clipped=1, parent=[1477, 58] (Action bar)
		sendInterface(true, 1477, 60, 1465);//Interface: id=1465, clipped=1, parent=[1477, 60] (Minimap)
		sendInterface(true, 1477, 34, 1433);//Interface: id=1433, clipped=1, parent=[1477, 34] (Settings menu)
		sendInterface(true, 1477, 390, 1483);//Interface: id=1483, clipped=1, parent=[1477, 390] (Grave timer)
		sendInterface(true, 1477, 409, 745);//Interface: id=745, clipped=1, parent=[1477, 409] (Assisting interface)
		sendInterface(true, 1477, 386, 1485);//Interface: id=1485, clipped=1, parent=[1477, 386]
		sendInterface(true, 1477, 0, 1213);//Interface: id=1213, clipped=1, parent=[1477, 0] (Skill popups)
		sendInterface(true, 1477, 76, 1448);//Interface: id=1448, clipped=1, parent=[1477, 76]
		sendInterface(true, 1477, 832, 557);//Interface: id=557, clipped=1, parent=[1477, 832] (Current task)
		sendInterface(true, 1477, 18, 1484);//Interface: id=1484, clipped=1, parent=[1477, 18]
		sendInterface(true, 1477, 106, 137);//Interface: id=137, clipped=1, parent=[1477, 106] (Chat box)
		sendInterface(true, 1477, 178, 1467);//Interface: id=1467, clipped=1, parent=[1477, 178] (Another chat box)
		sendInterface(true, 1477, 186, 1472);//Interface: id=1472, clipped=1, parent=[1477, 186] (Another chat box)
		sendInterface(true, 1477, 194, 1471);//Interface: id=1471, clipped=1, parent=[1477, 194] (Another chat box)
		sendInterface(true, 1477, 333, 1470);//Interface: id=1470, clipped=1, parent=[1477, 333] (Another chat box)
		sendInterface(true, 1477, 824, 464);//Interface: id=464, clipped=1, parent=[1477, 824] (Another chat box)
		sendInterface(true, 1477, 222, 182);//Interface: id=182, clipped=1, parent=[1477, 222]
		sendInterface(true, 1477, 38, 1488);//Interface: id=1488, clipped=1, parent=[1477, 38]
		sendInterface(true, 1477, 159, 669);//Interface: id=669, clipped=1, parent=[1477, 159] (Information box)
		sendInterface(true, 1477, 21, 1215);//Interface: id=1215, clipped=1, parent=[1477, 21] (Experience counter)
		/*sendTab(14, 751); // Chat options
		sendTab(75, 752); // Chatbox
		sendTab(70, 748); // HP bar
		sendTab(71, 749); // Prayer bar
		sendTab(72, 750); // Energy bar
		sendTab(67, 747); // Summoning bar
		sendInterface(true, 752, 8, 137); // Username on chat
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
		sendTab(10, 754); // PM split chat	*/
	}

	/**
	 * Sends an tab to the game screen.
	 * @param tab The tab id.
	 * @param child The child id.
	 */
	/*private void sendTab(int tab, int child) {
		sendInterface(true, child == 137 ? 752 : 548, tab, child);
	}*/

	/**
	 * Sends the map region data for the players position.
	 */
	public void sendMapRegion(boolean isLogin) {
		player.setLastPosition(player.getPosition());
		PacketBuf buf = new PacketBuf(OutgoingOpcode.STATIC_MAP_REGION_PACKET, PacketType.SHORT);
		boolean forceSend = true;
		if((((player.getPosition().getRegionX() / 8) == 48) || ((player.getPosition().getRegionX() / 8) == 49)) && ((player.getPosition().getRegionY() / 8) == 48)) {
			forceSend = false;
		}
		if(((player.getPosition().getRegionX() / 8) == 48) && ((player.getPosition().getRegionY() / 8) == 148)) {
			forceSend = false;
		}
		if (isLogin) {
			player.getPlayerUpdater().init(buf);
		}
		buf.putByteS(0);//mapSize
		buf.put(9);//Constants.MAP_SIZES[1]//regionCount (number of Xteas)
		buf.putShort(player.getPosition().getRegionY());
		buf.putLEShortA(player.getPosition().getRegionX());
		buf.putByteC((isLogin || forceSend) ? 1 : 0);//forceRefresh
		System.out.println("mapSize=0, posX="+player.getPosition().getRegionX()+", posY="+player.getPosition().getRegionY());
		/*buf.putShortA(player.getPosition().getLocalX());
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
		buf.putShortA(player.getPosition().getLocalY());*/
		sendPacket(buf.toPacket());
	}
	
	public void sendDynamicMapRegion (boolean isLogin) {
		//TODO: Complete this.
	}
	
	public void sendPlayerUpdates () {
		Packet packet = player.getPlayerUpdater().makePacket();
		sendPacket(packet);
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
	 * @param skillID The ID of the skill level.
	 */
	public void sendSkillLevel(int skillID) {
		Skill skill = player.getSkills().getSkill(skillID);
		PacketBuf buf = new PacketBuf(OutgoingOpcode.SKILL_DATA_PACKET);
		buf.putByteC((byte) skillID);
		buf.putInt2((int) skill.getExperience());
		buf.putByteS(skill.getCurrentLevel());
		player.getChannel().write(buf.toPacket());
	}

	/**
	 * Sends the run energy.
	 */
	public void sendRunEnergy() {
		PacketBuf buf = new PacketBuf(OutgoingOpcode.RUN_ENERGY_PACKET);
		buf.put(player.getRunEnergy());
		sendPacket(buf.toPacket());
	}
	
	/**
	 * Tells the client to run the specified script with the specified parameters
	 * @param scriptID	The ID of the script to run
	 * @param params	An array of parameters to pass to the script
	 */
	public void sendRunClientScript (int scriptID, Object... params) {
		PacketBuf buf = new PacketBuf(OutgoingOpcode.RUN_CS2_PACKET, PacketType.SHORT);
		String parameterString = "";
		if (params != null) {
			for (int count = 0; count<params.length; count++) {
				if (params[count] instanceof String) {
					parameterString += "s"; //String
				} else {
					parameterString += "i"; //Integer
				}
			}
		}
		buf.putString(parameterString);
		if (params != null) {
			for (int i = parameterString.length() - 1;i>=0; i--) {	
				if (parameterString.charAt(i) == 's') {
					buf.putString((String) params[i]);
				} else {
					buf.putInt((int) params[i]);
				}
			}
		}
		buf.putInt(scriptID);
		sendPacket(buf.toPacket());
	}
	
	/**
	 * Sends a player option.
	 * @param option	The option string
	 * @param slot		The option slot ID
	 * @param top		Whether the option should be at the top
	 */
	public void sendPlayerOption (String option, int slot, boolean top) {
		sendPlayerOption(option, slot, top, -1);
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
		sendInterface(false, 752, 11, childId);
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
    	sendInterface(false, 1477, 11, interfaceId);		
	}
}
