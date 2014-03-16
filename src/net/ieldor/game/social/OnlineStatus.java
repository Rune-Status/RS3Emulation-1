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
package net.ieldor.game.social;
/**
 * An {@link Enum} that represents a player's online status
 *
 * @author Sundays211
 *
 */
public enum OnlineStatus {
	EVERYONE(0),
	FRIENDS(1),
	NOBODY(2);
	
	private final int statusCode;
	OnlineStatus (int code) {
		this.statusCode = code;
	}
	
	public int getCode () {
		return statusCode;
	}
	
	public static OnlineStatus get (int code) {
		switch (code) {
		case 0:
			return EVERYONE;
		case 1:
			return FRIENDS;
		case 2:
			return NOBODY;
		default:
			return NOBODY;
		}
	}
}
