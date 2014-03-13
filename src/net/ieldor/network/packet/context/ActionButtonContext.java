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
 * An {@link PacketContext} that is used for action buttons.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class ActionButtonContext implements PacketContext {

	/**
	 * The attributes for the context.
	 */
	private int interfaceId, buttonId, buttonId2;
	
	/**
	 * Constructs a new {@code ActionButtonContext} instance.
	 * @param interfaceId The interface id.
	 * @param buttonId The button id.
	 * @param buttonId2 The second button id.
	 */
	public ActionButtonContext(int interfaceId, int buttonId, int buttonId2) {
		this.interfaceId = interfaceId;
		this.buttonId = buttonId;
		this.buttonId2 = buttonId2;
	}

	/**
	 * Gets the interface
	 * @return the interfaceId
	 */
	public int getInterfaceId() {
		return interfaceId;
	}

	/**
	 * Gets the button
	 * @return the buttonId
	 */
	public int getButtonId() {
		return buttonId;
	}

	/**
	 * Gets the second button
	 * @return the buttonId2
	 */
	public int getButtonId2() {
		return buttonId2;
	}
}
