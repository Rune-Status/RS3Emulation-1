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

public class Ignore {
	private String currentName;
	private String previousName;
	private String note;
	
	public Ignore (String currentName, String previousName, String note) {
		this.currentName = currentName;
		this.previousName = previousName;
		this.note = note;
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
