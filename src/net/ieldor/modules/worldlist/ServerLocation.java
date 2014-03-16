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

import java.util.ArrayList;

/**
 * RS3Emulator
 * ServerLocation.java
 * 16/03/2014
 * @author Sundays211
 */
public class ServerLocation {
	
	public static final ServerLocation US_EAST_2 = new ServerLocation(225, "United States (East Coast 2)", 0);
	public static final ServerLocation GERMANY = new ServerLocation(56, "Germany", 1);
	public static final ServerLocation US_WEST = new ServerLocation(225, "United States (West Coast)", 2);
	public static final ServerLocation US_EAST_1 = new ServerLocation(225, "United States (East Coast)", 3);
	public static final ServerLocation UK = new ServerLocation(77, "UK", 4);
	public static final ServerLocation AUSTRALIA = new ServerLocation(16, "Australia", 5);
	
	public static final ArrayList<ServerLocation> locations = new ArrayList<ServerLocation>();
	static {
		locations.add(US_EAST_2);
		locations.add(GERMANY);
		locations.add(US_WEST);
		locations.add(US_EAST_1);
		locations.add(UK);
		locations.add(AUSTRALIA);
	}
	
	private int countryID;
	private String name;
	private int ordial;
	
	private ServerLocation (int countryID, String name, int ordial) {
		this.countryID = countryID;
		this.name = name;
		this.ordial = ordial;
	}
	
	public int getCountryID () {
		return this.countryID;
	}
	
	public String getName () {
		return this.name;
	}
	
	public int getID () {
		return this.ordial;
	}
}
