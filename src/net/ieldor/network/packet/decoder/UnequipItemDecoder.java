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
import net.ieldor.network.packet.context.UnequipItemContext;

/**
 * An {@link PacketDecoder} that decodes the stream for the unequip packet.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class UnequipItemDecoder implements PacketDecoder<UnequipItemContext> {

	/* (non-Javadoc)
	 * @see net.ieldor.network.packet.PacketDecoder#decode(net.ieldor.io.PacketReader)
	 */
	@Override
	public UnequipItemContext decode(PacketReader packet) {
		int slot = packet.getShortA();
		int item = packet.getShort();
		int childId = packet.getShort();
		int interfaceId = packet.getShort();
		if (slot < 0 || slot > 28) {
			return null;
		}
		return new UnequipItemContext(slot, item, childId, interfaceId);
	}
}
