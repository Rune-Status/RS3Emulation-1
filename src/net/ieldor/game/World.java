package net.ieldor.game;

import java.io.IOException;
import java.util.logging.Logger;

import net.ieldor.Constants;
import net.ieldor.cache.Cache;
import net.ieldor.game.model.player.Player;
import net.ieldor.game.model.region.RegionRepository;
import net.ieldor.modules.worldlist.WorldData;
import net.ieldor.modules.worldlist.WorldList;
import net.ieldor.utility.CharacterRepository;

/**
 * The world class is a singleton which contains objects like the
 * {@link CharacterRepository} for players and NPCs. It should only contain
 * things relevant to the in-game world and not classes which deal with I/O and
 * such (these may be better off inside some custom {@link Service} or other
 * code, however, the circumstances are rare).
 * @author Graham
 */
public final class World {

	/**
	 * The logger for this class.
	 */
	private static final Logger logger = Logger.getLogger(World.class.getName());

	/**
	 * The world.
	 */
	private static final World WORLD = new World();

	/**
	 * Represents the different status codes for registering a player.
	 * @author Graham
	 */
	public enum RegistrationStatus {

		/**
		 * Indicates the world is full.
		 */
		WORLD_FULL,

		/**
		 * Indicates that the player is already online.
		 */
		ALREADY_ONLINE,

		/**
		 * Indicates that the player was registered successfully.
		 */
		OK;
	}

	/**
	 * Gets the world.
	 * @return The world.
	 */
	public static World getWorld() {
		return WORLD;
	}

	/**
	 * The scheduler.
	 */
	//private final Scheduler scheduler = new Scheduler();

	/**
	 * The {@link CharacterRepository} of {@link Player}s.
	 */
	private final CharacterRepository<Player> playerRepository = new CharacterRepository<Player>(Constants.MAX_WORLD_PLAYERS);

	/**
	 * Creates the world.
	 */
	private World() {

	}

	/**
	 * Initialises the world by loading definitions from the specified file
	 * system.
	 * @param release The release number.
	 * @param cache The file system.
	 * @throws IOException if an I/O error occurs.
	 */
	public void init(int release, Cache cache) throws IOException {
		/*logger.info("Loading world entries...");
		WorldList.init();
		//logger.info("Loading region xtea...");
		//RegionKeysLoader.init();
		
		logger.info("Loading equipment definitions...");
		int nonNull = 0;
		EquipmentDefinitionParser equipParser = new EquipmentDefinitionParser("data/equipment-" + release + ".dat");
		EquipmentDefinition[] equipDefs = equipParser.parse();
		for (EquipmentDefinition def : equipDefs) {
			if (def != null)
				nonNull++;
		}
		EquipmentDefinition.init(equipDefs);
		logger.info("Done (loaded " + nonNull + " equipment definitions).");
		
		ItemDefinitionParser itemParser = new ItemDefinitionParser("data/items-" + release + ".dat");
		ItemDefinition[] itemDefs = itemParser.parse();
		for (ItemDefinition def : itemDefs) {
			if (def != null)
				nonNull++;
		}
		ItemDefinition.init(itemDefs);
		logger.info("Done (loaded " + nonNull + " item definitions).");*/
		/*logger.info("Loading region information.");
		schedule(new RegionScheduleTask());*/
	}

	/**
	 * Gets the character repository. NOTE:
	 * {@link CharacterRepository#add(Character)} and
	 * {@link CharacterRepository#remove(Character)} should not be called
	 * directly! These mutation methods are not guaranteed to work in future
	 * releases!
	 * <p>
	 * Instead, use the {@link World#register(Player)} and
	 * {@link World#unregister(Player)} methods which do the same thing and
	 * will continue to work as normal in future releases.
	 * @return The character repository.
	 */
	public CharacterRepository<Player> getPlayerRepository() {
		return playerRepository;
	}

	/**
	 * Registers the specified player.
	 * @param player The player.
	 * @return A {@link RegistrationStatus}.
	 */
	public RegistrationStatus register(final Player player) {
		if (isPlayerOnline(player.getUsername())) {
			return RegistrationStatus.ALREADY_ONLINE;
		}

		boolean success = playerRepository.add(player);
		if (success) {
			logger.info("Registered player: " + player + " [online=" + playerRepository.size() + "]");
			return RegistrationStatus.OK;
		} else {
			logger.warning("Failed to register player (server full): " + player + " [online=" + playerRepository.size() + "]");
			return RegistrationStatus.WORLD_FULL;
		}
	}

	/**
	 * Checks if the specified player is online.
	 * @param name The player's name.
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean isPlayerOnline(String name) {
		// TODO: use a hash set or map in the future?
		for (Player player : playerRepository) {
			if (player.getUsername().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Unregisters the specified player.
	 * @param player The player.
	 */
	public void unregister(Player player) {
		if (playerRepository.remove(player)) {
			RegionRepository.shiftRegionForPlayer(player, -1, -1);
			logger.info("Unregistered player: " + player + " [online=" + playerRepository.size() + "]");
		} else {
			logger.warning("Could not find player to unregister: " + player + "!");
		}
	}

	/**
	 * Schedules a new task.
	 * @param task The {@link ScheduledTask}.
	 */
	/*public void schedule(ScheduledTask task) {
		scheduler.schedule(task);
	}*/

	/**
	 * Calls the {@link Scheduler#pulse()} method.
	 */
	/*public void pulse() {
		scheduler.pulse();
	}*/

}
