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

import java.util.Collection;
import java.util.List;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.ieldor.Constants;
import net.ieldor.game.model.player.Player;
import net.ieldor.game.model.skill.Skill;
import net.ieldor.game.social.Friend;
import net.ieldor.game.social.Ignore;
import net.ieldor.game.social.OnlineStatus;
import net.ieldor.io.Packet;
import net.ieldor.io.PacketBuf;
import net.ieldor.io.Packet.PacketType;
import net.ieldor.modules.worldlist.WorldData;
import net.ieldor.utility.BinaryLandscapeHandler;

/**
 * A class used to store the packets (actions) that an {@link Entity} can
 * perform.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class ActionSender {
	
	//TODO: Update packet opcodes to the required revision. If the revision in the comments is not correct (or missing), the packet needs updating
	private static final int KEEP_ALIVE_PACKET = 50;//802
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
		buf.putInt1(xteas[3]);
		buf.putInt(xteas[2]);
		buf.putShort(id);
		buf.putLEInt(xteas[1]);
		buf.putInt(xteas[0]);
		buf.putByteS(type);
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
		
		PacketBuf buf = new PacketBuf(INTERFACE_PACKET);
		buf.putInt(xteas[3]);
		buf.putInt1(xteas[2]);
		buf.putLEShort(interfaceID);//Interface ID
		buf.putLEInt(xteas[0]);
		buf.putInt2(xteas[1]);
		buf.putByteC(clipped ? 1 : 0);//Clipped
		buf.putInt1(windowID << 16 | windowComponent);//Parent hash
		
		sendPacket(buf.toPacket());
	}

	/**
	 * Sends a fixed-sized configuration key-value pair (8 bits). Known as "Config1" on some servers
	 * @param id	 The id (key) of the client varp
	 * @param value The value of the client varp
	 */
	public void sendFixedVarp(int id, int value) {
		//NOTE: The order and encoding methods of this packet vary between client revisions
		PacketBuf buf = new PacketBuf(FIXED_VARP_PACKET);
		buf.putByteA(value);
		buf.putLEShort(id);
		sendPacket(buf.toPacket());
	}

	/**
	 * Sends a dynamic-sized configuration key-value pair (8-32 bits). Known as "Config2" on some servers
	 * @param id	 The id (key) of the client varp
	 * @param value The value of the client varp
	 */
	public void sendDynamicVarp(int id, int value) {
		//NOTE: The order and encoding methods of this packet vary between client revisions
		PacketBuf buf = new PacketBuf(DYNAMIC_VARP_PACKET);
		buf.putLEShortA(id);
		buf.putInt1(value);
		sendPacket(buf.toPacket());
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
		sendPacket(buf.toPacket());
	}
	
	/**
	 * Sends a packet notifying the client that the friends server is being connected to.
	 * Changes friends list message from "Loading Friends List." to "Connecting to Friend Server."
	 */
	public void sendUnlockFriendsList () {
		PacketBuf buf = new PacketBuf(UNLOCK_FRIENDS_LIST);
		sendPacket(buf.toPacket());
	}
	
	/**
	 * Sends all the friends currently on a player's friends list (used for friends list initialisation
	 * @param friends A list containing all the friends on the player's friends list
	 */
	public void sendFriends (Collection<Friend> friends) {
		PacketBuf buf = new PacketBuf(FRIENDS_PACKET, PacketType.SHORT);
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
		PacketBuf buf = new PacketBuf(FRIENDS_PACKET, PacketType.SHORT);
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
		PacketBuf buf = new PacketBuf(IGNORES_PACKET, PacketType.SHORT);
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
		PacketBuf buf = new PacketBuf(IGNORES_PACKET, PacketType.SHORT);
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
		PacketBuf buf = new PacketBuf(MESSAGE_PACKET, PacketType.BYTE);
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
		player.sendLobbyConfigs(Constants.LOBBY_CONFIGS_802);
		sendWindowPane(1477, 0);
		sendGameScreen();
		sendDefaultPlayersOptions();
		sendRunEnergy();
		sendGameMessage("Welcome to Ieldor.", false);
		//sendMessage("Ieldor is currently in the BETA stage.");
		player.getFriendManager().init();
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
	private void sendGameScreen() {		
		sendInterface(true, 1477, 85, 1482);//id=1482, clipped=1, target=[1477, 85]
		sendInterface(true, 1477, 59, 1466);//id=1466, clipped=1, target=[1477, 59]
		sendInterface(true, 1477, 39, 1220);//id=1220, clipped=1, target=[1477, 39] (Active task)
		//sendInterface(true, 1477, 130, 1473);//id=1473, clipped=1, target=[1477, 130] (Inventory)
		sendInterface(true, 1477, 202, 1464);//id=1464, clipped=1, target=[1477, 202]
		//sendInterface(true, 1477, 69, 1458);//id=1458, clipped=1, target=[1477, 69] (Prayer points)
		//sendInterface(true, 1477, 241, 1460);//id=1460, clipped=1, target=[1477, 241] 
		//sendInterface(true, 1477, 251, 1452);//id=1452, clipped=1, target=[1477, 251]
		//sendInterface(true, 1477, 5, 1461);//id=1461, clipped=1, target=[1477, 5]
		//sendInterface(true, 1477, 15, 1449);//id=1449, clipped=1, target=[1477, 15]
		sendInterface(true, 1477, 117, 550);//id=550, clipped=1, target=[1477, 117] (Friends list)
		//sendInterface(true, 1477, 92, 1427);//id=1427, clipped=1, target=[1477, 92] (Friends chat)
		//sendInterface(true, 1477, 107, 1110);//id=1110, clipped=1, target=[1477, 107] (Clan chat)
		//sendInterface(true, 1477, 49, 590);//id=590, clipped=1, target=[1477, 49]
		//sendInterface(true, 1477, 87, 1416);//id=1416, clipped=1, target=[1477, 87] (Music player)
		sendInterface(true, 1477, 97, 1417);//id=1417, clipped=1, target=[1477, 97] (Notes)
		sendInterface(true, 1477, 174, 1431);//id=1431, clipped=1, target=[1477, 174] (Launcher bar (links to settings, social, powers, etc))
		sendInterface(true, 1477, 57, 1430);//id=1430, clipped=1, target=[1477, 57] (Action bar)
		sendInterface(true, 1477, 59, 1465);//id=1465, clipped=1, target=[1477, 59] (Minimap)
		sendInterface(true, 1477, 33, 1433);//id=1433, clipped=1, target=[1477, 33] (Settings menu)
		sendInterface(true, 1477, 136, 1483);//id=1483, clipped=1, target=[1477, 136] (Grave timer)
		sendInterface(true, 1477, 155, 745);//id=745, clipped=1, target=[1477, 155] (Assisting interface)
		//sendInterface(true, 1477, 132, 1485);//id=1485, clipped=1, target=[1477, 132] 
		sendInterface(true, 1477, 0, 1213);//id=1213, clipped=1, target=[1477, 0] (Skill popups)
		//sendInterface(true, 1477, 74, 1448);//id=1448, clipped=1, target=[1477, 74]
		sendInterface(true, 1477, 66, 557);//id=557, clipped=1, target=[1477, 66] (Current task)
		sendInterface(true, 1477, 106, 137);//id=137, clipped=1, target=[1477, 106] (Chat box)
		//sendInterface(true, 1477, 178, 1467);//id=1467, clipped=1, target=[1477, 178] (Another chat box)
		//sendInterface(true, 1477, 186, 1472);//id=1472, clipped=1, target=[1477, 186] (Another chat box)
		//sendInterface(true, 1477, 194, 1471);//id=1471, clipped=1, target=[1477, 194] (Another chat box)
		//sendInterface(true, 1477, 79, 1470);//id=1470, clipped=1, target=[1477, 79] (Another chat box)
		//sendInterface(true, 1477, 58, 464);//id=464, clipped=1, target=[1477, 58] (Another chat box)
		//sendInterface(true, 1477, 222, 182);//id=182, clipped=1, target=[1477, 222]
		//sendInterface(true, 1477, 37, 1488);//id=1488, clipped=1, target=[1477, 37]
		//sendInterface(true, 1477, 159, 669);//id=669, clipped=1, target=[1477, 159] (Information box)
		sendInterface(true, 1477, 21, 1215);//id=1215, clipped=1, target=[1477, 21] (Experience counter)
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
		PacketBuf buf = new PacketBuf(STATIC_MAP_REGION_PACKET, PacketType.SHORT);
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
		buf.putByteC(0);//mapSize
		buf.putByteC(isLogin ? 1 : 0);//forceRefresh
		buf.putLEShort(player.getPosition().getRegionY());
		buf.putShort(player.getPosition().getRegionX());
		System.out.println("mapSize=0, posX="+player.getPosition().getRegionX()+", posY="+player.getPosition().getRegionY());
		buf.put(Constants.MAP_SIZES[0]);//regionCount (number of Xteas)
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
		PacketBuf buf = new PacketBuf(SKILL_DATA_PACKET);
		buf.putByteC((byte) skillID);
		buf.putInt2((int) skill.getExperience());
		buf.putByteS(skill.getCurrentLevel());
		player.getChannel().write(buf.toPacket());
	}

	/**
	 * Sends the run energy.
	 */
	public void sendRunEnergy() {
		PacketBuf buf = new PacketBuf(RUN_ENERGY_PACKET);
		buf.put(player.getRunEnergy());
		player.getChannel().write(buf.toPacket());
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
	 * Sends a player option.
	 * @param option	The option string
	 * @param slot		The option slot ID
	 * @param top		Whether the option should be at the top
	 * @param cursor	The cursor sprite ID to use
	 */
	public void sendPlayerOption(String option, int slot, boolean top, int cursor) {
		PacketBuf buf = new PacketBuf(PLAYER_OPTION_PACKET, PacketType.BYTE);
		buf.putString(option);
		buf.put(top ? 1 : 0);//isOnTop
		buf.putByteC(slot);
		buf.putLEShort(cursor);//Cursor
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
