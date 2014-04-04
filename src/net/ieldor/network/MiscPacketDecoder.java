/*
 * This file is part of RS3Emulator.
 *
 * RS3Emulator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RS3Emulator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RS3Emulator.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.ieldor.network;

import net.ieldor.game.model.player.Player;
import net.ieldor.game.social.OnlineStatus;
import net.ieldor.io.Packet;
import net.ieldor.io.PacketReader;
import net.ieldor.network.packet.PacketCodec;

/**
 * RS3Emulator
 * MiscPacketDecoder.java
 * 16/03/2014
 * @author Sundays211
 */
public class MiscPacketDecoder {
	
	public static void decodeMiscPacket (PacketReader packet, Player player) {
		switch (packet.getOpcode()) {
		case PacketCodec.PING_PACKET:
			player.getActionSender().sendPing();
			break;
		case PacketCodec.ONLINE_STATUS_PACKET:
			int b1 = packet.getByte();
			OnlineStatus status = OnlineStatus.get(packet.getByte());
			int b3 = packet.getByte();
			player.getFriendManager().setOnlineStatus(status);
			break;
		case PacketCodec.ADD_FRIEND_PACKET:
			String name = packet.getString();
			player.getFriendManager().addFriend(name);
			break;
		case PacketCodec.REMOVE_FRIEND_PACKET:
			player.getFriendManager().removeFriend(packet.getString());
			break;
		case PacketCodec.ADD_IGNORE_PACKET:
			String ignore = packet.getString();
			boolean tillLogout = (packet.get() == 1);
			player.getFriendManager().addIgnore(ignore, tillLogout);
			break;
		case PacketCodec.REMOVE_IGNORE_PACKET:
			player.getFriendManager().removeIgnore(packet.getString());
			break;
		case PacketCodec.SCREEN_PACKET:
			//TODO: Handle this packet
			break;
		default:
			System.out.println("Unhandled packet: opcode="+packet.getOpcode()+", length="+packet.getLength());
			break;
		}
	}
}
