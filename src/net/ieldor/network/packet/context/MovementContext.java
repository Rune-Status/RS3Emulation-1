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
package net.ieldor.network.packet.context;

import net.ieldor.io.PacketReader;
import net.ieldor.network.packet.PacketContext;

/**
 * Represents the context of movement for an {@link Player}.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class MovementContext implements PacketContext {

	/**
	 * The movement packet.
	 */
	private PacketReader packet;
	
	/**
	 * The packet size.
	 */
	private int packetSize;

	/**
	 * Constructs a new {@code MovementContext} instance.
	 * @param packet The packet.
	 * @param packetSize The packet size.
	 */
	public MovementContext(PacketReader packet, int packetSize) {
		this.packet = packet;
		this.packetSize = packetSize;
	}

	/**
	 * Gets the packet.
	 * @return the packet
	 */
	public PacketReader getPacket() {
		return packet;
	}

	/**
	 * Gets the packet size.
	 * @return the packetSize
	 */
	public int getPacketSize() {
		return packetSize;
	}


}
