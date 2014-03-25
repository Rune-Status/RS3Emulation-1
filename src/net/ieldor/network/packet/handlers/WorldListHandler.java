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
package net.ieldor.network.packet.handlers;

import java.util.Map.Entry;

import net.ieldor.game.model.player.Player;
import net.ieldor.io.PacketBuf;
import net.ieldor.io.Packet.PacketType;
import net.ieldor.modules.worldlist.ServerLocation;
import net.ieldor.modules.worldlist.WorldData;
import net.ieldor.modules.worldlist.WorldList;
import net.ieldor.network.ActionSender;
import net.ieldor.network.packet.PacketHandler;
import net.ieldor.network.packet.context.WorldListContext;

/**
 * RS3Emulator
 * WorldListHandler.java
 * 16/03/2014
 * @author Sundays211
 */
public class WorldListHandler implements PacketHandler<WorldListContext> {

	/* (non-Javadoc)
	 * @see net.ieldor.network.packet.PacketHandler#handle(net.ieldor.game.model.player.Player, net.ieldor.network.packet.PacketContext)
	 */
	@Override
	public void handle(Player player, WorldListContext context) {
		/*
		 * Credits for lobby handling: http://www.rune-server.org/runescape-development/rs-503-client-server/snippets/466556-718-complete-lobby.html
		 */
		boolean full = (context.getListHash() == 0);
		PacketBuf buf = new PacketBuf(ActionSender.WORLD_LIST_PACKET, PacketType.SHORT);
		buf.put(1);//Must equal 1
		buf.put(2);//Must equal 2
		buf.put(full ? 1 : 0);
		if (full) {
			buf.putSmart(ServerLocation.locations.size());
			for (ServerLocation loc : ServerLocation.locations) {
				buf.putSmart(loc.getCountryID());
				buf.putJagString(loc.getName());
			}
			int worldListSize = WorldList.WORLDS.size();
			
			buf.putSmart(0);
			buf.putSmart(worldListSize + 1);
			buf.putSmart(worldListSize);
			
			for (WorldData world : WorldList.WORLDS.values()) {
				buf.putSmart(world.getNodeId());//World node ID
				buf.put(world.getServerLocation().getID());//Physical location 
				
				buf.putInt(world.getFlags());
				
				buf.putSmart(world.getCountry().getFlag());
				if (world.getCountry().getFlag() != 0) {
					buf.putJagString(world.getCountry().getName());
				}
				
				buf.putJagString(world.getActivity());//World activity
				
				buf.putJagString(world.getIp());//Endpoint to connect to				
			}			
			buf.putInt((int)Math.random()*Integer.MAX_VALUE);//World list hash (fairly sure this can be any non-zero value)
		}
		for (Entry<Integer, Integer> players : WorldList.playerCounts.entrySet()) {
			buf.putSmart(players.getKey());//Node id
			buf.putShort(players.getValue());//Player count	
		}
		player.getActionSender().sendPacket(buf.toPacket());
	}
}
