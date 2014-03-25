package net.ieldor.network.codec.messages;

import net.ieldor.modules.worldlist.Country;
import net.ieldor.modules.worldlist.WorldData;

public final class WorldListMessage {

	private final int sessionId;
	private final Country[] countries;
	private final WorldData[] worlds;
	private final int[] players;

	public WorldListMessage(int sessionId, Country[] countries, WorldData[] worlds, int[] players) {
		this.sessionId = sessionId;
		this.countries = countries;
		this.worlds = worlds;
		this.players = players;
	}

	public int getSessionId() {
		return sessionId;
	}

	public Country[] getCountries() {
		return countries;
	}

	public WorldData[] getWorlds() {
		return worlds;
	}

	public int[] getPlayers() {
		return players;
	}

}
