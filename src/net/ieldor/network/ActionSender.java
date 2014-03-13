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

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.ieldor.game.model.player.Player;
import net.ieldor.io.PacketBuf;
import net.ieldor.io.Packet.PacketType;
import net.ieldor.utility.BinaryLandscapeHandler;

/**
 * A class used to store the packets (actions) that an {@link Entity} can
 * perform.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class ActionSender {

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
	 * Sends the default login data.
	 */
	public void sendLogin() {
		sendMapRegion();
		sendWindowPane(548);
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
		this.sendFriendsStatus();
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
	 * Sends an window pane.
	 * @param paneId The id of the window pane.
	 */
	private void sendWindowPane(int paneId) {
		PacketBuf buf = new PacketBuf(145);
		buf.putLEShortA(paneId);
		buf.putByteA(0);
		buf.putLEShortA(interfaceCount++);
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
	 * Sends an config.
	 * @param id The config id.
	 * @param value The config value.
	 */
	public void sendConfig(int id, int value) {
		if(value < 128 && value > -128) {
			sendConfig1(id, value);
		} else {
			sendConfig2(id, value);
		}		
	}

	/**
	 * Sends an config.
	 * @param id The config id.
	 * @param value The config value.
	 */
	private void sendConfig1(int id, int value) {
		PacketBuf buf = new PacketBuf(60);
		buf.putShortA(id);
		buf.putByteC(value);
		player.getChannel().write(buf.toPacket());
	}
	
	/**
	 * Sends an config.
	 * @param id The config id.
	 * @param value The config value.
	 */
	public void sendConfig2(int id, int value) {
		PacketBuf buf = new PacketBuf(226);
		buf.putInt(value);
		buf.putShortA(id);
		player.getChannel().write(buf.toPacket());
	}
	
	/**
	 * Sends the friend status.
	 */
	public void sendFriendsStatus() {
		PacketBuf buf = new PacketBuf(197);
		buf.put(2);
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
