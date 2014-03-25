package net.ieldor.game.model.region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ieldor.game.model.Position;
import net.ieldor.game.model.player.Player;
import net.ieldor.game.model.region.Region.RegionState;

public class RegionRepository {

	private static final Map<RegionCoordinates, Region> activeRegions = new HashMap<RegionCoordinates, Region>();


	static {
		/**
		 * Load regions, such as home beforehand to avoid potential problems here.
		 */
	}

	private final List<Player> regionPlayers = new ArrayList<Player>();
	//private final List<NPC> regionNPCs =  new ArrayList<NPC>();
	//private final List<StaticObject> regionObjects = new ArrayList<StaticObject>();
	//private final Map<Position, List<GroundItem>> groundItems = new HashMap<Position, List<GroundItem>>();
	public final int id;

	public RegionRepository(int id) {
		this.id = id;
	}

	public static void shiftRegionForPlayer(Player player, int x, int y) {
		Region currentRegion = player.getCurrentRegion();
		if (currentRegion != null) {
			RegionRepository resp = currentRegion.getRegionRepository();
			resp.getRegionPlayers().remove(player);
			if (resp.getRegionPlayers().size() == 0)
				currentRegion.setState(RegionState.IDLE);
		}
		if (x == -1 && y == -1)
			return;
		RegionCoordinates regionCoordinates = new RegionCoordinates(x, y);
		Region region = activeRegions.get(regionCoordinates);
		if (region == null) {
			region = new Region(regionCoordinates, new RegionRepository(x / 8 << 8 | y / 8));
			activeRegions.put(regionCoordinates, region);
		}
		region.setState(RegionState.ACTIVE);
		region.getRegionRepository().getRegionPlayers().add(player);
		player.setCurrentRegion(region);
	}

	public List<Player> getRegionPlayers() {
		return regionPlayers;
	}

	public List<Player> getRegionPlayersWithinRadius(Position pos, int radius) {
		List<Player> regionPlayers = new LinkedList<Player>();
		for (Player player : getRegionPlayers()) {
			if (!player.isActive())
				continue;
			if (pos.getDistance(player.getPosition()) < radius)
				regionPlayers.add(player);
		}
		return regionPlayers;
	}
	
	/*public List<NPC> getRegionNPCs() {
		return regionNPCs;
	}

	public List<StaticObject> getRegionObjects() {
		return regionObjects;
	}

	public Map<Position, List<GroundItem>> getRegionItems() {
		return groundItems;
	}*/

	public static Map<RegionCoordinates, Region> getActiveRegions() {
		return activeRegions;
	}
	
	public static Region getRegion(int x, int y) {
		RegionCoordinates key = new RegionCoordinates(x, y);
		System.out.println("X "+x + " Y "+y);
		if(activeRegions.containsKey(key))
			return activeRegions.get(key);
		return null;
	}

	public void purgeData(Region region) {
		//regionNPCs.clear();
		activeRegions.remove(region);
		//regionObjects.clear();
		//groundItems.clear();
	}
}
