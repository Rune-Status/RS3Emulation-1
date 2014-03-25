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
package net.ieldor.modules.worldlist;

import java.util.HashMap;

/**
 * RS3Emulator
 * WorldList.java
 * 16/03/2014
 * @author Sundays211
 */
public class WorldList {
	public static final HashMap<Integer, WorldData> WORLDS = new HashMap<Integer, WorldData>();
	public static HashMap<Integer, Integer> playerCounts = new HashMap<Integer, Integer>();
	
	public static final WorldData DEFAULT_WORLD 
		= new WorldData(2, "World 2", WorldData.FLAG_MEMBERS | WorldData.FLAG_LOOT_SHARE, Country.USA, "Default", "127.0.0.1", ServerLocation.US_EAST_1);
	public static final WorldData LOBBY = new WorldData(1100, "Lobby", 0, Country.USA, "", "127.0.0.1", ServerLocation.US_EAST_1);

	
	public static void init () {
		WORLDS.put(1, new WorldData(1, "World 1", WorldData.FLAG_MEMBERS, Country.UK, "Test", "127.0.0.1", ServerLocation.UK));
		playerCounts.put(1, 10);
		WORLDS.put(2, DEFAULT_WORLD);
		playerCounts.put(2, 20);
	}
	
	public static void updatePlayerCount (int nodeID, int count) {
		playerCounts.put(nodeID, count);
	}
}
