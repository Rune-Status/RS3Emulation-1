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

/**
 * An {@link Object} that represents a player's friend
 *
 * @author Sundays211
 *
 */
public class Friend {
	public final String username;
	private String currentName;
	private String previousName;
	private World currentWorld = null;
	private int friendsChatRank = 0;
	private boolean isReferred = false;
	private String note = "";
	
	public Friend (String username, boolean referred) {
		this(username, referred, 0, "");
	}
	
	public Friend (String username, boolean referred, int fcRank, String note) {
		this.username = username;
		this.isReferred = referred;
		this.friendsChatRank = fcRank;
		this.note = note;
	}
	
	public void setDisplayNames (String current, String previous) {
		this.currentName = current;
		this.previousName = previous;
	}
	
	public void setWorld (World world) {
		this.currentWorld = world;
	}
	
	protected void setFcRank (int rank) {
		this.friendsChatRank = rank;
	}
	
	protected void setNote (String note) {
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
