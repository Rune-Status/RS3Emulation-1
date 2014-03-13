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
package net.ieldor.game;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An {@link Runnable} class that is used to pulse the {@link GamePulse}.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class GamePulseHandler implements Runnable {

	/**
	 * The game pulse instance.
	 */
	private GamePulse gamePulse;
	
	/**
	 * The logger for this class.
	 */
	private static final Logger logger = Logger.getLogger(GamePulseHandler.class.getName());

	/**
	 * Constructs a {@code GamePulseHandler} instance.
	 * @param gamePulse The game pulse instance.
	 */
	public GamePulseHandler(GamePulse gamePulse) {
		this.gamePulse = gamePulse;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {	
		try {
			gamePulse.pulse();
		} catch (Throwable t) {
			logger.log(Level.SEVERE, "Exception during pulse.", t);
		}
	}
}
