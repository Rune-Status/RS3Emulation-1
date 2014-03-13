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
import net.ieldor.network.packet.context.EquipItemContext;

/**
 * An {@link PacketDecoder} that decodes the streams of the equip item packet.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class EquipItemDecoder implements PacketDecoder<EquipItemContext> {

	/* (non-Javadoc)
	 * @see net.ieldor.network.packet.PacketDecoder#decode(net.ieldor.io.PacketReader)
	 */
	@Override
	public EquipItemContext decode(PacketReader packet) {
		int item = packet.getLEShort();
		int slot = packet.getShortA();
		int interfaceId = packet.getInt();	
		if (slot > 28 || slot < 0) {
			return null;
		}
		return new EquipItemContext(item, slot, interfaceId);
	}
}
