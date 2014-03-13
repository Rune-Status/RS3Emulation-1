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
 * An {@link PacketContext} that represents the context of the
 * {@link UnequipItemDecoder}.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class UnequipItemContext implements PacketContext {

	/**
	 * The attributes of the context.
	 */
	private int slot, item, childId, interfaceId;
	
	/**
	 * Constructs a new {@code UnequipItemContext} instance.
	 * @param slot The slot.
	 * @param item The item.
	 * @param childId The child id.
	 * @param interfaceId The interface id.
	 */
	public UnequipItemContext(int slot, int item, int childId, int interfaceId) {
		this.slot = slot;
		this.item = item;
		this.childId = childId;
		this.interfaceId = interfaceId;
	}

	/**
	 * Gets the slot.
	 * @return the slot
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * Gets the item.
	 * @return the item
	 */
	public int getItem() {
		return item;
	}

	/**
	 * Gets the child id.
	 * @return the childId
	 */
	public int getChildId() {
		return childId;
	}

	/**
	 * Gets the interface id.
	 * @return the intefaceId
	 */
	public int getInterfaceId() {
		return interfaceId;
	}

}
