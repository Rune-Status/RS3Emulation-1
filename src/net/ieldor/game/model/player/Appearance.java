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

import net.ieldor.io.PacketBuf;

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
	private boolean male = true;
	
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

	public void pack(PacketBuf block) {
		boolean prefixTitle = false;
		boolean suffixTitle = false;
		int flags = 0;
		if (!male) {
			flags |= 0x1;//Female
		}
		if (prefixTitle) {
			flags |= 0x40;//Title prefix
		}
		if (suffixTitle) {
			flags |= 0x80;//Title suffix
		}
		PacketBuf update = new PacketBuf();
		update.put(flags);//Flags
		if (prefixTitle) {
			update.putJagString("");//Title prefix
		}
		if (suffixTitle) {
			update.putJagString("");//Title suffix
		}
		update.put(0);//Is hidden
		//TODO: Fill in some missing data related to something...		
		for (int slot = 0; slot < 4; slot++) {
			update.put((byte) 0);//TODO: Update this
		}
		update.putShort(256 + looks[2]);
		update.putShort(256 + looks[3]);
		update.putShort(256 + looks[5]);
		update.putShort(256 + looks[0]);
		update.putShort(256 + looks[4]);
		update.putShort(256 + looks[6]);
		update.putShort(256 + looks[1]);
		for (int c : colours) {
			update.put((byte) c);
		}
		update.putShort(0x328);
		update.putShort(0x337);
		update.putShort(0x333);
		update.putShort(0x334);
		update.putShort(0x335);
		update.putShort(0x336);
		update.putShort(0x338);

		update.putString("Player name");//Display name		
		update.put(4);//Combat/total level
		update.put(0);//Combat level offset
		update.put(-1);//Unknown (related to the above somehow)
		
		update.put(0);//Has NPC Transform
		block.put(update.getLength());
		block.putBytesA(update.toPacket(), 0, update.getLength());
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
		return male ? 0 : 1;
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
		this.male = (gender == 0);
	}
}
