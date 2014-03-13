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

import net.ieldor.network.packet.PacketContext;

/**
 * The {@link PacketContext} of the equip item packet.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class EquipItemContext implements PacketContext {

	/**
	 * The equip item attributes.
	 */
	private int item, slot, interfaceId;

	/** 
	 * Constructs a new {@code EquipItemContext} instance.
	 * @param item The item id.
	 * @param slot The slot id.
	 * @param interfaceId The interface id.
	 */
	public EquipItemContext(int item, int slot, int interfaceId) {
		this.item = item;
		this.slot = slot;
		this.interfaceId = interfaceId;
	}

	/**
	 * Gets the item.
	 * @return the item
	 */
	public int getItem() {
		return item;
	}

	/**
	 * Gets the slot.
	 * @return the slot
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * Gets the interface id.
	 * @return the interfaceId
	 */
	public int getInterfaceId() {
		return interfaceId;
	}
}
