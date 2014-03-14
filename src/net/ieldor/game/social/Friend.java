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

import net.ieldor.utility.world.World;

public class Friend {
	private String currentName;
	private String previousName;
	private World currentWorld = null;
	private int friendsChatRank = 0;
	private boolean isReferred = false;
	private String note;
	
	public Friend (String currentName, String previousName, World world, int rank, boolean referred, String note) {
		this.currentName = currentName;
		this.previousName = previousName;
		this.currentWorld = world;
		this.friendsChatRank = rank;
		this.isReferred = referred;
		this.note = note;
	}
	
	public String getName () {
		return currentName;
	}
	
	public String getPrevName () {
		return previousName;
	}
	
	public World getWorld () {
		return currentWorld;
	}
	
	public int getFcRank () {
		return friendsChatRank;
	}
	
	public boolean isReferred () {
		return isReferred;
	}
	
	public String getNote () {
		return note;
	}
}
