package net.ieldor.game.sync;

import net.ieldor.Main;
import net.ieldor.game.model.player.Player;
import net.ieldor.game.sync.task.PlayerSynchronizationTask;
import net.ieldor.game.sync.task.PostPlayerSynchronizationTask;
import net.ieldor.game.sync.task.PrePlayerSynchronizationTask;
import net.ieldor.game.sync.task.SynchronizationTask;
import net.ieldor.utility.CharacterRepository;

/**
 * An implementation of {@link ClientSynchronizer} which runs in a single
 * thread (the {@link GameService} thread from which this is called). Each
 * client is processed sequentially. Therefore this class will work well on
 * machines with a single core/processor. The {@link ParallelClientSynchronizer}
 * will work better on machines with multiple cores/processors, however, both
 * classes will work.
 * @author Graham
 */
public final class SequentialClientSynchronizer extends ClientSynchronizer {

	@Override
	public void synchronize() {
		CharacterRepository<Player> players = Main.getPlayers();

		for (Player player : players) {
			SynchronizationTask task = new PrePlayerSynchronizationTask(player);
			task.run();
		}

		for (Player player : players) {
			SynchronizationTask task = new PlayerSynchronizationTask(player);
			task.run();
		}

		for (Player player : players) {
			SynchronizationTask task = new PostPlayerSynchronizationTask(player);
			task.run();
		}
	}

}
