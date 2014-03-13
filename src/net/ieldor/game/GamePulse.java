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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.ieldor.game.service.Service;
import net.ieldor.game.sync.ClientSynchronizer;
import net.ieldor.utility.NamedThreadFactory;

/**
 * An {@link Service} that is used to handle all things the server should run on
 * a pulse.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class GamePulse extends Service {
	
	/**
	 * The {@link ClientSynchronizer}.
	 */
	private ClientSynchronizer synchronizer;
	
	/**
	 * The scheduled executor service.
	 */
	private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("GameService"));

	/**
	 * Constructs a new {@code GamePulse} instance.
	 * @throws Exception An error has occured.
	 */
	public GamePulse() throws Exception {
		Class<?> clazz = Class.forName("net.ieldor.game.sync.ParallelClientSynchronizer");
		synchronizer = (ClientSynchronizer) clazz.newInstance();
	}

	/* (non-Javadoc)
	 * @see net.ieldor.game.service.Service#start()
	 */
	@Override
	public void start() {
		scheduledExecutor.scheduleAtFixedRate(new GamePulseHandler(this), 600, 600, TimeUnit.MILLISECONDS);		
	}

	/**
	 * Pulse everything at a certain delay!
	 */
	public void pulse() {
		synchronized(this) {
			synchronizer.synchronize();	
		}
	}
}
