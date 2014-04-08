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
package net.ieldor.game.sync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadFactory;

import net.ieldor.Main;
import net.ieldor.game.World;
import net.ieldor.game.model.player.Player;
import net.ieldor.game.sync.task.PhasedSynchronizationTask;
import net.ieldor.game.sync.task.PlayerSynchronizationTask;
import net.ieldor.game.sync.task.PostPlayerSynchronizationTask;
import net.ieldor.game.sync.task.PrePlayerSynchronizationTask;
import net.ieldor.game.sync.task.SynchronizationTask;
import net.ieldor.utility.CharacterRepository;
import net.ieldor.utility.NamedThreadFactory;

/**
 * An implementation of {@link ClientSynchronizer} which runs in a thread pool.
 * A {@link Phaser} is used to ensure that the synchronization is complete,
 * allowing control to return to the {@link GameService} that started the
 * synchronization. This class will scale well with machines that have multiple
 * cores/processors. The {@link SequentialClientSynchronizer} will work better
 * on machines with a single core/processor, however, both classes will work.
 * @author Graham
 */
public final class ParallelClientSynchronizer extends ClientSynchronizer {

	/**
	 * The executor service.
	 */
	private final ExecutorService executor;

	/**
	 * The phaser.
	 */
	private final Phaser phaser = new Phaser(1);

	/**
	 * Creates the parallel client synchronizer backed by a thread pool with a
	 * number of threads equal to the number of processing cores available
	 * (this is found by the {@link Runtime#availableProcessors()} method.
	 */
	public ParallelClientSynchronizer() {
		int processors = Runtime.getRuntime().availableProcessors();
		ThreadFactory factory = new NamedThreadFactory("ClientSynchronizer");
		executor = Executors.newFixedThreadPool(processors, factory);
	}

	@Override
	public void synchronize() {
		long begin = System.nanoTime();
		for (World world : Main.getWorlds()) {
			if (!world.isGame()) {
				continue;//Process is only needed for game worlds
			}
			CharacterRepository<Player> players = world.getPlayers();//Main.getPlayers();
			int playerCount = players.size();
	
			phaser.bulkRegister(playerCount);
			for (Player player : players) {
				SynchronizationTask task = new PrePlayerSynchronizationTask(player);
				executor.submit(new PhasedSynchronizationTask(phaser, task));
			}
			phaser.arriveAndAwaitAdvance();
	
			phaser.bulkRegister(playerCount);
			for (Player player : players) {
				SynchronizationTask task = new PlayerSynchronizationTask(player);
				executor.submit(new PhasedSynchronizationTask(phaser, task));
			}
			phaser.arriveAndAwaitAdvance();
	
			phaser.bulkRegister(playerCount);
			for (Player player : players) {
				SynchronizationTask task = new PostPlayerSynchronizationTask(player);
				executor.submit(new PhasedSynchronizationTask(phaser, task));
			}
			phaser.arriveAndAwaitAdvance();
		}
		long end = System.nanoTime();
		
		//System.out.println("Took " + (end - begin) / (1000000D * Main.getPlayers().size()) + "ms, with " + Main.getPlayers().size() + " players.");
	}

}
