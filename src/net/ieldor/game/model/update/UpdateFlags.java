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
package net.ieldor.game.model.update;

import java.util.BitSet;

/**
 * A class used for flagging updates for an {@link Entity}.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * @since Jul 18, 2013
 * @version 1.0
 */
public class UpdateFlags {

	/**
	 * The possible update flags.
	 * 
	 * @author Thomas Le Godais <thomaslegodais@live.com>
	 * @since Jul 18, 2013
	 * @version 1.0
	 */
	public enum Flags {
		APPEARANCE, CHANGE_COLOR, GFX1, GFX2, GFX3, GFX4, ANIMATION, FACE_LOCATION, HIT_UPDATE
	}

	/**
	 * A {@link Set} used for storing flags.
	 */
	private BitSet bitSet = new BitSet();

	/**
	 * Marks an update to be processed.
	 * 
	 * @param flag
	 *            The flag to mark.
	 */
	public void mark(Flags flag) {
		bitSet.set(flag.ordinal(), true);
	}

	/**
	 * Checks if an {@link Flags} is marked for update.
	 * 
	 * @param flag
	 *            The flag to check.
	 * @return If the flag is marked, or not.
	 */
	public boolean isMarked(Flags flag) {
		return bitSet.get(flag.ordinal());
	}

	/**
	 * Clears the flag set.
	 */
	public void reset() {
		bitSet.clear();
	}

	/**
	 * Checks if there is an update required.
	 * 
	 * @return Is there an update?
	 */
	public boolean requiresUpdate() {
		return !bitSet.isEmpty();
	}
}
