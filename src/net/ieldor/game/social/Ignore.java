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
 * An {@link Object} that represents a player's ignore
 *
 * @author Sundays211
 *
 */
public class Ignore {
	public final String username;
	private String currentName = "";
	private String previousName = "";
	private String note;
	
	public Ignore (String username) {
		this(username, "");
	}
	
	public Ignore (String username, String note) {
		this.username = username;
		this.note = note;
	}
	
	public void setDisplayNames (String current, String previous) {
		this.currentName = current;
		this.previousName = previous;
	}	
	
	public String getName () {
		return currentName;
	}
	
	public String getPreviousName () {
		return previousName;
	}
	
	public String getNote () {
		return note;
	}
}
