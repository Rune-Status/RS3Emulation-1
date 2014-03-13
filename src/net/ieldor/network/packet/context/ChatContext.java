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
 * The context for public chat handling.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class ChatContext implements PacketContext {

	/**
	 * The text attributes.
	 */
	private int colour, effects, size;
	
	/**
	 * The chat text.
	 */
	private String text;
	
	/**
	 * Constructs a new  {@code ChatContext} instance.
	 * @param colour The colour of the chat.
	 * @param effects The effects of the chat.
	 * @param text The text of the chat.
	 */
	public ChatContext(int colour, int effects, int size, String text) {
		this.colour = colour;
		this.effects = effects;
		this.size = size;
		this.text = text;
	}

	/**
	 * Gets the colour.
	 * @return the colour
	 */
	public int getColour() {
		return colour;
	}

	/**
	 * Gets the effects.
	 * @return the effects
	 */
	public int getEffects() {
		return effects;
	}

	/**
	 * Gets the size.
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Gets the text.
	 * @return the text
	 */
	public String getText() {
		return text;
	}
}
