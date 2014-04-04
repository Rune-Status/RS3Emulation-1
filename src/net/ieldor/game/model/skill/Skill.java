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
package net.ieldor.game.model.skill;

/**
 * Represents a single skill.
 * 
 * @author Graham
 */
public final class Skill {

	/**
	 * The attack id.
	 */
	public static final int ATTACK = 0;

	/**
	 * The defence id.
	 */
	public static final int DEFENCE = 1;

	/**
	 * The strength id.
	 */
	public static final int STRENGTH = 2;

	/**
	 * The hitpoints id.
	 */
	public static final int HITPOINTS = 3;

	/**
	 * The ranged id.
	 */
	public static final int RANGED = 4;

	/**
	 * The prayer id.
	 */
	public static final int PRAYER = 5;

	/**
	 * The magic id.
	 */
	public static final int MAGIC = 6;

	/**
	 * The cooking id.
	 */
	public static final int COOKING = 7;

	/**
	 * The woodcutting id.
	 */
	public static final int WOODCUTTING = 8;

	/**
	 * The fletching id.
	 */
	public static final int FLETCHING = 9;

	/**
	 * The fishing id.
	 */
	public static final int FISHING = 10;

	/**
	 * The firemaking id.
	 */
	public static final int FIREMAKING = 11;

	/**
	 * The crafting id.
	 */
	public static final int CRAFTING = 12;

	/**
	 * The smithing id.
	 */
	public static final int SMITHING = 13;

	/**
	 * The mining id.rivate
	 */
	public static final int MINING = 14;

	/**
	 * The herblore id.
	 */
	public static final int HERBLORE = 15;

	/**
	 * The agility id.
	 */
	public static final int AGILITY = 16;

	/**
	 * The thieving id.
	 */
	public static final int THIEVING = 17;

	/**
	 * The slayer id.
	 */
	public static final int SLAYER = 18;

	/**
	 * The farming id.
	 */
	public static final int FARMING = 19;

	/**
	 * The runecrafting id.
	 */
	public static final int RUNECRAFTING = 20;

	/**
	 * The construction id.
	 */
	public static final int CONSTRUCTION = 21;

	/**
	 * The hunter id.
	 */
	public static final int HUNTER = 22;

	/**
	 * The summoning id.
	 */
	public static final int SUMMONING = 23;
	
	/**
	 * The dungeoneering id.
	 */
	public static final int DUNGEONEERING = 24;
	
	/**
	 * The divination id.
	 */
	public static final int DIVINATION = 25;
	
	/**
	 * The skill names.
	 */
	private static final String SKILL_NAMES[] = { "Attack", "Defence",
			"Strength", "Hitpoints", "Ranged", "Prayer", "Magic", "Cooking",
			"Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting",
			"Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer",
			"Farming", "Runecraft", "Construction", "Hunter", "Summoning", "Dungeoneering", "Divination" };

	/**
	 * Gets the name of a skill.
	 * 
	 * @param id
	 *            The skill's id.
	 * @return The skill's name.
	 */
	public static String getName(int id) {
		return SKILL_NAMES[id];
	}

	/**
	 * The experience.
	 */
	private final double experience;

	/**
	 * The current level.
	 */
	private final int currentLevel;

	/**
	 * The maximum level.
	 */
	private final int maximumLevel;

	/**
	 * Creates a skill.
	 * 
	 * @param experience
	 *            The experience.
	 * @param currentLevel
	 *            The current level.
	 * @param maximumLevel
	 *            The maximum level.
	 */
	public Skill(double experience, int currentLevel, int maximumLevel) {
		this.experience = experience;
		this.currentLevel = currentLevel;
		this.maximumLevel = maximumLevel;
	}

	/**
	 * Gets the experience.
	 * 
	 * @return The experience.
	 */
	public double getExperience() {
		return experience;
	}

	/**
	 * Gets the current level.
	 * @return The current level.
	 */
	public int getCurrentLevel() {
		return currentLevel;
	}

	/**
	 * Gets the maximum level.
	 * @return The maximum level.
	 */
	public int getMaximumLevel() {
		return maximumLevel;
	}

}