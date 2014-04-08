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
package net.ieldor.network.packet.decoder;

import net.ieldor.io.PacketReader;
import net.ieldor.network.packet.PacketDecoder;
import net.ieldor.network.packet.context.ActionButtonContext;
import net.ieldor.network.packet.context.ActionButtonContext.MenuOption;

/**
 * An {@link PacketDecoder} that is used to handle action buttons.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class ActionButtonDecoder implements PacketDecoder<ActionButtonContext> {

	/* (non-Javadoc)
	 * @see net.ieldor.network.packet.PacketDecoder#decode(net.ieldor.io.PacketReader)
	 */
	@Override
	public ActionButtonContext decode(PacketReader packet) {
		MenuOption option = MenuOption.getOptionFromOpcode(packet.getOpcode());
		int buttonID = packet.getLEShortA() & 0xFFFF;
		if(buttonID == 65535) {
			buttonID = -1;
		}
		int buttonId2 = packet.getShort() & 0xFFFF;
		if(buttonId2 == 65535) {
			buttonId2 = -1;
		}
		int interfaceId = packet.getInt();
		return new ActionButtonContext(option, interfaceId, buttonID, buttonId2);
	}
}
