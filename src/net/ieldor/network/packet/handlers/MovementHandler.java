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

import net.ieldor.game.model.Position;
import net.ieldor.game.model.player.Player;
import net.ieldor.io.PacketReader;
import net.ieldor.network.packet.PacketHandler;
import net.ieldor.network.packet.context.MovementContext;

/**
 * An {@link PacketHandler} that is used to handle player movement.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class MovementHandler implements PacketHandler<MovementContext> {

	/* (non-Javadoc)
	 * @see net.ieldor.network.packet.PacketHandler#handle(net.ieldor.game.model.player.Player, net.ieldor.network.packet.PacketContext)
	 */
	@Override
	public void handle(Player player, MovementContext context) {
		PacketReader reader = context.getPacket();
		int size = context.getPacketSize();
		
		final int steps = (size - 5) / 2;
		final int[][] path = new int[steps][2];
		final boolean runSteps = reader.getByteA() == 1;

		final int firstX = reader.getShort();
		final int firstY = reader.getShortA();
		for (int i = 0; i < steps; i++) {
			path[i][0] = reader.getByteA();
			path[i][1] = reader.getByteS();
		}
		if (!player.getMovementPulse().addFirstStep(new Position(firstX, firstY))) {
			return;
		}

		player.getMovementPulse().setRunningQueue(runSteps);
		
		for (int i = 0; i < steps; i++) {
			path[i][0] += firstX;
			path[i][1] += firstY;
			
			player.getMovementPulse().addStep(new Position(path[i][0], path[i][1]));
		}
	}
}
