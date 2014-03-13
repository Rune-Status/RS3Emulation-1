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
package net.ieldor.game.model.player;

/**
 * The load result of a player.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class LoadResult {

	/**
	 * The player for the result.
	 */
	private Player player;
	
	/**
	 * The return code.
	 */
	private int returnCode;

	/**
	 * Constructs a new {@code LoadResult} instance.
	 * @param player The player.
	 * @param returnCode The return code.
	 */
	public LoadResult(Player player, int returnCode) {
		this.player = player;
		this.returnCode = returnCode;
	}

	/**
	 * Gets the player.
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Gets the return code.
	 * @return the returnCode
	 */
	public int getReturnCode() {
		return returnCode;
	}

}
