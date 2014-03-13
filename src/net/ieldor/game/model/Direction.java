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
package net.ieldor.game.model;

/**
 * An simple enumeration that is used to represent the possible directions of
 * movement an {@link Entity} can perform.
 * 
 * <p>
 * The movement is cycled by checking where the players next movement is, and
 * based on the offsets, the direction is calculated.
 * </p>
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public enum Direction {
	
	/*
	 * North movement.
	 */
	NORTH(1),

	/*
	 * North east movement.
	 */
	NORTH_EAST(2),

	/*
	 * East movement.
	 */
	EAST(4),

	/*
	 * South east movement.
	 */
	SOUTH_EAST(7),

	/*
	 * South movement.
	 */
	SOUTH(6),

	/*
	 * South west movement.
	 */
	SOUTH_WEST(5),

	/*
	 * West movement.
	 */
	WEST(3),

	/*
	 * North west movement.
	 */
	NORTH_WEST(0),

	/*
	 * No movement.
	 */
	NONE(-1);

	/**
	 * The value of the direction.
	 */
	private int value;
	
	/**
	 * Constructs a new Direction instance.
	 * @param value the value of the direction.
	 */
	private Direction(int value) {
		this.value = value;
	}
	
	/**
	 * An empty direction array.
	 */
	public static final Direction[] EMPTY_DIRECTION_ARRAY = new Direction[0];

	/**
	 * Checks if the direction represented by the two delta values can connect
	 * two points together in a single direction.
	 * @param deltaX The difference in X coordinates.
	 * @param deltaY The difference in X coordinates.
	 * @return {@code true} if so, {@code false} if not.
	 */
	public static boolean isConnectable(int deltaX, int deltaY) {
		return Math.abs(deltaX) == Math.abs(deltaY) || deltaX == 0 || deltaY == 0;
	}

	/**
	 * Creates a direction from the differences between X and Y.
	 * @param deltaX The difference between two X coordinates.
	 * @param deltaY The difference between two Y coordinates.
	 * @return The direction.
	 */
	public static Direction fromDeltas(int deltaX, int deltaY) {
		if (deltaY == 1) {
			if (deltaX == 1) {
				return Direction.NORTH_EAST;
			} else if (deltaX == 0) {
				return Direction.NORTH;
			} else {
				return Direction.NORTH_WEST;
			}
		} else if (deltaY == -1) {
			if (deltaX == 1) {
				return Direction.SOUTH_EAST;
			} else if (deltaX == 0) {
				return Direction.SOUTH;
			} else {
				return Direction.SOUTH_WEST;
			}
		} else {
			if (deltaX == 1) {
				return Direction.EAST;
			} else if (deltaX == -1) {
				return Direction.WEST;
			}
		}
		return Direction.NONE;
	}

	
	/**
	 * Gets the direction.
	 * @return the direction.
	 */
	public int toValue() {
		return value;
	}
}
