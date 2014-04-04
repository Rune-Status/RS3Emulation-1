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
package net.ieldor.network.packet.handlers;

import net.ieldor.Constants;
import net.ieldor.game.model.player.Player;
import net.ieldor.network.packet.PacketHandler;
import net.ieldor.network.packet.context.ActionButtonContext;

/**
 * An {@link PacketHandler} that is used to handle action buttons.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class ActionButtonHandler implements PacketHandler<ActionButtonContext> {

	/**
	 * The skill index.
	 */
	private int currentSkillIndex;

	/* (non-Javadoc)
	 * @see net.ieldor.network.packet.PacketHandler#handle(net.ieldor.game.model.player.Player, net.ieldor.network.packet.PacketContext)
	 */
	@Override
	public void handle(Player player, ActionButtonContext context) {
		System.out.println("InterfaceId: " + context.getInterfaceId() + " ButtonId: " + context.getButtonId() + " ButtonId2: " + context.getButtonId2());
		switch(context.getInterfaceId()) {
		case 182:
			if(context.getButtonId() == 6) {
				player.getActionSender().sendLogout();
			}
			break;
		case 750:
			if(!player.isRunning()) {
				player.setRunning(true);
				player.getActionSender().sendVarp(173, 1);
			} else {
				player.setRunning(false);
				player.getActionSender().sendVarp(173, 0);
			}
			break;
		case 261:
			if(!player.isRunning()) {
				player.setRunning(true);
				player.getActionSender().sendVarp(173, 1);
			} else {
				player.setRunning(false);
				player.getActionSender().sendVarp(173, 0);
			}
			break;
			case 320: 
				int skillIndex = 0;
				for (int buttonId = 125; buttonId < 149; buttonId++) {
					if (context.getButtonId() == buttonId) {
						player.getActionSender().sendInterface(499);
						player.getActionSender().sendLargeVarp(965, Constants.MENU_ID[skillIndex]);
						this.currentSkillIndex = Constants.MENU_ID[skillIndex];
						break;
					}
					skillIndex++;
				}
				break;
			case 499:
				player.getActionSender().sendLargeVarp(965, Constants.SUB_CONFIG[context.getButtonId() - 10] + currentSkillIndex);
				break;
		}
	}
}
