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
package net.ieldor.network.codec.messages;

import net.ieldor.network.codec.handshake.HandshakeState;

/**
 * An message that is sent from the handshake procedure.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class HandshakeMessage {
	
	/**
	 * The handshake state.
	 */
	private HandshakeState state;

	/**
	 * Constructs a new {@code HandshakeMessage} instance.
	 * @param incomingOpcode The state of handshake.
	 */
	public HandshakeMessage(HandshakeState state) {
		this.state = state;
	}

	/**
	 * Gets the state.
	 * @return the state
	 */
	public HandshakeState getState() {
		return state;
	}
}
