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

import net.ieldor.game.model.player.Player;

/**
 * A {@link SkillListener} which notifies the player when they have levelled up
 * a skill.
 * @author Graham
 */
public final class LevelUpSkillListener extends SkillAdapter {

	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * Creates the level up listener for the specified player.
	 * @param player The player.
	 */
	public LevelUpSkillListener(Player player) {
		this.player = player;
	}

	@Override
	public void levelledUp(SkillSet set, int id, Skill skill) {
		// TODO show the interface
		String name = Skill.getName(id);
		//String article = LanguageUtil.getIndefiniteArticle(name);
		//player.sendMessage("You've just advanced " + article + " " + name + " level! You have reached level " + skill.getMaximumLevel() + ".");
	}

}
