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
package net.ieldor.network.codec.messages;

/**
 * RS3Emulator
 * GameLoginData.java
 * 21/03/2014
 * @author Sundays211
 */
public class GameLoginData {
	public final int rights;
	public final String displayName;
	public final int playerIndex;
	public final boolean isMember;
	
	public GameLoginData (int rights, String displayName, int playerIndex, boolean isMembersWorld) {
		this.rights = rights;
		this.displayName = displayName;
		this.playerIndex = playerIndex;
		this.isMember = isMembersWorld;
	}
}
