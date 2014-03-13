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
 * Represents the appearance (or looks) of an {@link Player}. 
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class Appearance {

	/**
	 * The npc id of the player.
	 */
	private int npcId = -1;
	
	/**
	 * The look array of the player.
	 */
	private int[] looks = new int[7];
	
	/**
	 * The colours of the playr.
	 */
	private int[] colours = new int[5];
	
	/**
	 * The gender of the player.
	 */
	private int gender = 0;
	
	/**
	 * Constructs a new Appearance instance.
	 */
	public Appearance() {
		looks[0] = 264; // Hair
		looks[1] = 14; // Beard
		looks[2] = 18; // Torso
		looks[3] = 26; // Arms
		looks[4] = 34; // Bracelets
		looks[5] = 38; // Legs
		looks[6] = 42; // Shoes
		colours[2] = 15;
		colours[1] = 15;
		for(int i = 0; i < 5; i++) {
			colours[2] = 15;
			colours[1] = 16;
			colours[0] = 5;
		}
	}

	/**
	 * Gets the npc ID.
	 * @return the npcId
	 */
	public int getNpcId() {
		return npcId;
	}

	/**
	 * Sets the npc id.
	 * @param npcId the npcId to set
	 */
	public void setNpcId(int npcId) {
		this.npcId = npcId;
	}

	/**
	 * Gets the looks
	 * @return the looks
	 */
	public int[] getLooks() {
		return looks;
	}

	/**
	 * Gets the colours.
	 * @return the colours
	 */
	public int[] getColours() {
		return colours;
	}

	/**
	 * Gets the gender.
	 * @return the gender
	 */
	public int getGender() {
		return gender;
	}

	/**
	 * @param looks the looks to set
	 */
	public void setLooks(int[] looks) {
		this.looks = looks;
	}

	/**
	 * @param colours the colours to set
	 */
	public void setColours(int[] colours) {
		this.colours = colours;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(int gender) {
		this.gender = gender;
	}
}
