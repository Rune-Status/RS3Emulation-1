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
package net.ieldor.modules.login;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.ieldor.modules.login.BinaryPlayerManager.StreamUtil;

/**
 * RS3Emulator
 * DisplayNameManager.java
 * 14/03/2014
 * @author Sundays211
 */
public class NameManager {
	
	public static final File DISPLAY_NAME_FILE_LOC = new File("data/displayNames.bin");
	
	private HashMap<String, DisplayName> displayNameCache = new HashMap<String, DisplayName>();
	
	private HashMap<String, DisplayName> prevNameCache = new HashMap<String, DisplayName>();
	
	private HashMap<String, DisplayName> usernameMatcher = new HashMap<String, DisplayName>();	
	
	private ArrayList<DisplayName> playerDisplayNames;
	
	public class DisplayName {
		String prevName;
		String displayName;
		public final String username;
		
		DisplayName (String username, String displayName, String prevName) {
			this.username = username;
			this.displayName = displayName;
			this.prevName = prevName;
		}
		
		public String getDisplayName () {
			return this.displayName;
		}
		
		public String getPrevName () {
			return (this.prevName == null ? "" : this.prevName);
		}
	}
	
	public void init () throws IOException {
		if (DISPLAY_NAME_FILE_LOC.exists()) {
			loadNames();
		} else {
			DISPLAY_NAME_FILE_LOC.createNewFile();
			playerDisplayNames = new ArrayList<DisplayName>();
			saveNames();
		}
	}
	
	public void save () throws IOException {
		saveNames();
	}
	
	public void loadNames () throws IOException {
		DataInputStream inputStream = null;
		try {
			inputStream = new DataInputStream(new FileInputStream(DISPLAY_NAME_FILE_LOC));
			synchronized (this) {
				int displayNameCount = inputStream.readInt();
				if (playerDisplayNames == null) {
					playerDisplayNames = new ArrayList<DisplayName>(displayNameCount);
					// = new HashMap<String, DisplayName>(displayNameCount);
				} else {
					playerDisplayNames.clear();
				}
				String username, displayName, prevName;
				DisplayName nameObject;
				for (int i=0;i<displayNameCount;i++) {
					username = StreamUtil.readString(inputStream);
					displayName = StreamUtil.readString(inputStream);
					prevName = StreamUtil.readString(inputStream);
					nameObject = new DisplayName(username, displayName, prevName);
					playerDisplayNames.add(nameObject);
					displayNameCache.put(simplifyName(displayName), nameObject);
					usernameMatcher.put(simplifyName(username), nameObject);
					if (!prevName.isEmpty()) {
						prevNameCache.put(simplifyName(prevName), nameObject);
					}
				}
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
	
	public void saveNames () throws IOException {
		DataOutputStream outputStream = null;
		try {
			outputStream = new DataOutputStream(new FileOutputStream(DISPLAY_NAME_FILE_LOC));
			synchronized (this) {
				outputStream.writeInt(playerDisplayNames.size());
				for (DisplayName nameInfo : playerDisplayNames) {
					StreamUtil.writeString(outputStream, nameInfo.username);
					StreamUtil.writeString(outputStream, nameInfo.displayName);
					StreamUtil.writeString(outputStream, nameInfo.prevName);
				}
			}
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}
	
	/**
	 * Fetches the name object associated with the specified display name. Also fetches the name object if the name is currently being held for a player
	 * @param displayName	The display name to fetch
	 * @return				The display name object, or null if none was found
	 */
	public DisplayName getNameObject (String displayName) {
		if (displayNameCache.containsKey(simplifyName(displayName))) {
			return displayNameCache.get(simplifyName(displayName));
		} else if (prevNameCache.containsKey(simplifyName(displayName))) {
			return prevNameCache.get(simplifyName(displayName));
		} else {
			return null;
		}
	}
	
	/**
	 * Fetches the display name data from the specified username
	 * @param username	The username to fetch the display name data from
	 * @return			The display name data object, or null if none exists
	 */
	public DisplayName getDisplayNamesFromUsername (String username) {
		return usernameMatcher.get(simplifyName(username));
	}
	
	/**
	 * Checks whether a specified display name is currently in use (or is being held)
	 * @param displayName	The display name to check
	 * @return				Whether the display name is in use or is being held
	 */
	public boolean nameExists (String displayName) {
		return displayNameCache.containsKey(simplifyName(displayName)) || prevNameCache.containsKey(simplifyName(displayName));
	}
	
	/**
	 * Changes the display name of a player
	 * @param prevName	The previous (current) display name of the player.
	 * @param newName	The desired display name for the player.
	 * @return			true if the name was changed successfully, false otherwise
	 */
	public boolean changeDisplayName (String prevName, String newName) {
		synchronized (this) {
			if (!nameExists(prevName)) {
				//Player does not exist, for some reason...
				return false;
			}
			if (nameExists(newName)) {
				//If the chosen name is already in use
				if (simplifyName(getNameObject(prevName).prevName) != simplifyName(newName)) {
					return false;//If the name is not the player's previous display name, do not change
				}			
			}
			DisplayName nameObj = displayNameCache.get(simplifyName(prevName));
			prevNameCache.remove(simplifyName(nameObj.prevName));
			nameObj.displayName = newName;
			nameObj.prevName = prevName;
			prevNameCache.put(simplifyName(nameObj.prevName), nameObj);
			displayNameCache.put(simplifyName(nameObj.displayName), nameObj);
			return true;
		}
	}
	
	/**
	 * Sets the initial display name of a player
	 * @param username		The username for the player.
	 * @param displayName	The desired display name.
	 * @return				true if the name was set successfully, false otherwise
	 */
	public boolean setNewDisplayName (String username, String displayName) {
		if (nameExists(displayName)) {
			return false;
		}
		DisplayName nameObj = new DisplayName(username, displayName, "");
		displayNameCache.put(simplifyName(nameObj.displayName), nameObj);
		return true;
	}
	
	/**
	 * Simplifies the specified name, for use in comparisons
	 * @param name	The name to simplify
	 * @return		A String representing the simplified name
	 */
	public static String simplifyName(String name) {
		if (name == null) {
			return "";
		}
		name = name.replaceAll(" ", "_");
		name = name.toLowerCase();
		return name;
	}

	public static String formatDisplayName(String name) {
		if (name == null)
			return "";
		name = name.replaceAll("_", " ");
		name = name.toLowerCase();
		StringBuilder newName = new StringBuilder();
		boolean wasSpace = true;
		for (int i = 0; i < name.length(); i++) {
			if (wasSpace) {
				newName.append(("" + name.charAt(i)).toUpperCase());
				wasSpace = false;
			} else {
				newName.append(name.charAt(i));
			}
			if (name.charAt(i) == ' ') {
				wasSpace = true;
			}
		}
		return newName.toString();
	}
}
