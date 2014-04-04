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
package net.ieldor.modules.worldlist;

/**
 * RS3Emulator
 * LoginManager.java
 * 15/03/2014
 * @author Sundays211
 */
public final class WorldData {

	public static final int FLAG_MEMBERS    = 0x1;
	public static final int FLAG_QUICK_CHAT = 0x2;
	public static final int FLAG_PVP        = 0x4;
	public static final int FLAG_LOOT_SHARE = 0x8;
	public static final int FLAG_HIGHLIGHT  = 0x10;

	private final int id, flags;
	private final String activity, ip, name;
	private final Country country;
	private final ServerLocation location;

	public WorldData(int id, String name, int flags, Country country, String activity, String ip, ServerLocation location) {
		this.id = id;
		this.name = name;
		this.flags = flags;
		this.country = country;
		this.activity = activity;
		this.ip = ip;
		this.location = location;
	}

	public int getNodeId() {
		return id;
	}
	
	public String getName () {
		return name;
	}

	public int getFlags() {
		return flags;
	}

	public Country getCountry() {
		return country;
	}

	public String getActivity() {
		return activity;
	}

	public String getIp() {
		return ip;
	}
	
	public ServerLocation getServerLocation () {
		return location;
	}

}
