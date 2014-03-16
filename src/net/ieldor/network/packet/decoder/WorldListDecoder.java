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
package net.ieldor.network.packet.decoder;

import net.ieldor.io.PacketReader;
import net.ieldor.network.packet.PacketDecoder;
import net.ieldor.network.packet.context.WorldListContext;

/**
 * RS3Emulator
 * WorldListDecoder.java
 * 16/03/2014
 * @author Im Frizzy : Kyle Friz : <skype:kfriz1998>
 */
public class WorldListDecoder implements PacketDecoder<WorldListContext> {

	/* (non-Javadoc)
	 * @see net.ieldor.network.packet.PacketDecoder#decode(net.ieldor.io.PacketReader)
	 */
	@Override
	public WorldListContext decode(PacketReader packet) {
		// TODO Auto-generated method stub
		int updateHash = packet.getInt();
		return new WorldListContext(updateHash);
	}

}
