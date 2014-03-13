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
package net.ieldor.utility;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Handles anything related to the landscape keys in binary format.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class BinaryLandscapeHandler {

	/**
	 * The {@link Map} that is used to store the landscape keys.
	 */
	private static Map<Integer, int[]> landscapeKeys = new HashMap<Integer, int[]>();

	/**
	 * Loads the binary format of landscape keys.
	 * @throws IOException An I/O error has occured.
	 */
	@SuppressWarnings("resource")
	public static void loadLandscapes() throws IOException {
		DataInputStream inputStream = new DataInputStream(new FileInputStream("./data/landscapeKeys.bin"));
		
		int loadedLandscapes = 0;
		for(int region = 0; region < 16384;  region++) {
			int[] regionHash = new int[4];
			for(int regionPart = 0; regionPart < 4; regionPart++)
				regionHash[regionPart] = inputStream.readInt();
			
			if(regionHash[0] != 0 && regionHash[1] != 0 && regionHash[2] != 0 && regionHash[3] != 0) 
				loadedLandscapes++;
			
			landscapeKeys.put(region, regionHash);
		}
		
		Logger.getAnonymousLogger().info("Successfully loaded " + loadedLandscapes + " landscape keys.");
	}
	
	/**
	 * Gets the landscape key based off the region.
	 * @param region The region id.
	 * @return The landscape hash.
	 */
	public static int[] get(int region) {
		return landscapeKeys.get(region);
	}
}
