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

import net.ieldor.config.IncommingOpcode;
import net.ieldor.network.packet.PacketContext;

/**
 * An {@link PacketContext} that is used for action buttons.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class ActionButtonContext implements PacketContext {
	
	public enum MenuOption {
		NONE(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), 
		SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10);
		
		private final int optionID;
		MenuOption(int optID) {
			this.optionID = optID;
		}
		
		public static MenuOption getOptionFromOpcode (int opcode) {
			switch (opcode) {
			case IncommingOpcode.ACTION_BUTTON_1_PACKET:
				return MenuOption.ONE;
			case IncommingOpcode.ACTION_BUTTON_2_PACKET:
				return MenuOption.TWO;
			case IncommingOpcode.ACTION_BUTTON_3_PACKET:
				return MenuOption.THREE;
			case IncommingOpcode.ACTION_BUTTON_4_PACKET:
				return MenuOption.FOUR;
			case IncommingOpcode.ACTION_BUTTON_5_PACKET:
				return MenuOption.FIVE;
			case IncommingOpcode.ACTION_BUTTON_6_PACKET:
				return MenuOption.SIX;
			case IncommingOpcode.ACTION_BUTTON_7_PACKET:
				return MenuOption.SEVEN;
			case IncommingOpcode.ACTION_BUTTON_8_PACKET:
				return MenuOption.EIGHT;
			case IncommingOpcode.ACTION_BUTTON_9_PACKET:
				return MenuOption.NINE;
			case IncommingOpcode.ACTION_BUTTON_10_PACKET:
				return MenuOption.TEN;
			default:
				return MenuOption.NONE;
			}
			
		}
		
		public static MenuOption getOption (int id) {
			for (MenuOption opt : MenuOption.values()) {
				if (opt.optionID == id) {
					return opt;
				}
			}
			return MenuOption.NONE;
		}
	}

	/**
	 * The attributes for the context.
	 */
	private int interfaceId, buttonId, buttonId2;
	private MenuOption option;
	
	/**
	 * Constructs a new {@code ActionButtonContext} instance.
	 * @param interfaceId The interface id.
	 * @param buttonId The button id.
	 * @param buttonId2 The second button id.
	 */
	public ActionButtonContext(MenuOption option, int interfaceId, int buttonId, int buttonId2) {
		this.option = option;
		this.interfaceId = interfaceId;
		this.buttonId = buttonId;
		this.buttonId2 = buttonId2;
	}

	/**
	 * Gets the interface
	 * @return The interfaceId
	 */
	public int getInterfaceID() {
		return interfaceId >> 16;
	}

	/**
	 * Gets the interface component ID
	 * @return The component ID
	 */
	public int getComponentID() {
		return interfaceId & 0xffff;
	}
	
	/**
	 * Gets the selected context menu option
	 * @return	The selected menu option
	 */
	public MenuOption getOption () {
		return option;
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
