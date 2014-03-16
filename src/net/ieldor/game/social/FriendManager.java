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
package net.ieldor.game.social;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.ieldor.Main;
import net.ieldor.game.model.player.Player;
import net.ieldor.modules.login.BinaryPlayerManager.StreamUtil;
import net.ieldor.modules.login.NameManager;
import net.ieldor.modules.login.NameManager.DisplayName;

/**
 * The tools necessary to control a player's friends list, ignore list, and private messaging. 
 * Also has the tools for managing the player's own friends chat (though the friends chat channel itself is run separately)
 *
 * @author Sundays211
 *
 */
public class FriendManager {
	
	private Player player;
	private NameManager nameManager;
	
	private static final int FRIENDS_LIST_MAX = 400;
	private static final int IGNORE_LIST_MAX = 400;
	
	private HashMap<String, Friend> friends = new HashMap<String, Friend>(FRIENDS_LIST_MAX);
	private HashMap<String, Ignore> ignores = new HashMap<String, Ignore>(IGNORE_LIST_MAX);
	
	private OnlineStatus onlineStatus = OnlineStatus.NOBODY;
	
	public FriendManager (Player player, NameManager nameManager) {
		this.player = player;
		this.nameManager = nameManager;
	}
	
	public void init () {
		for (Friend f : friends.values()) {
			DisplayName nameData = nameManager.getDisplayNamesFromUsername(f.username);
			if (nameData == null) {
				f.setDisplayNames(f.username, "");
			} else {
				f.setDisplayNames(nameData.getDisplayName(), nameData.getPrevName());
			}
			//TODO Add world determining logic
		}
		for (Ignore i : ignores.values()) {
			DisplayName nameData = nameManager.getDisplayNamesFromUsername(i.username);
			if (nameData == null) {
				i.setDisplayNames(i.username, "");
			} else {
				i.setDisplayNames(nameData.getDisplayName(), nameData.getPrevName());
			}
		}
		player.getActionSender().sendOnlineStatus(onlineStatus);
		player.getActionSender().sendFriends(friends.values());
		player.getActionSender().sendIgnores(ignores.values());
	}
	
	public void serialise (DataOutputStream output) throws IOException {
		synchronized (this) {
			output.writeShort(friends.size());
			for (Friend f : friends.values()) {
				StreamUtil.writeString(output, f.username);
				output.writeByte(f.isReferred() ? 1 : 0);
				output.writeByte(f.getFcRank());
				StreamUtil.writeString(output, f.getNote());
			}
			output.writeShort(ignores.size());
			for (Ignore i : ignores.values()) {
				StreamUtil.writeString(output, i.username);
				StreamUtil.writeString(output, i.getNote());
			}
			output.writeByte(onlineStatus.getCode());
		}
	}
	
	public void deserialise (DataInputStream input, int version) throws IOException {
		synchronized (this) {
			if (version >= 1) {
				int friendListSize = input.readUnsignedShort();
				String name, note;
				int fcRank;
				boolean isReferred;
				friends.clear();
				for (int i=0;i<friendListSize;i++) {
					name = StreamUtil.readString(input);
					isReferred = (input.readUnsignedByte() == 1);
					fcRank = input.readByte();
					note = StreamUtil.readString(input);
					Friend f = new Friend(name, isReferred, fcRank, note);
					friends.put(NameManager.simplifyName(name), f);
				}
				int ignoreListSize = input.readUnsignedShort();
				for (int i=0;i<ignoreListSize;i++) {
					name = StreamUtil.readString(input);
					note = StreamUtil.readString(input);
					Ignore ig = new Ignore(name, note);
					ignores.put(NameManager.simplifyName(name), ig);
				}
				int onlineStatusCode = input.readUnsignedByte();
				onlineStatus = OnlineStatus.get(onlineStatusCode);
			}
		}
	}
	
	public void setOnlineStatus (OnlineStatus status) {
		onlineStatus = status;		
		player.getActionSender().sendOnlineStatus(status);
		//TODO notify friends (and other players, if applicable) of online status change
	}
	
	public void addIgnore (String displayName, boolean tillLogout) {		
		if (displayName.equalsIgnoreCase(player.getDisplayName()) 
				|| displayName.equalsIgnoreCase(player.getPrevDisplayName())) {
			//TODO add message send (cannot add self)
			return;
		}
		
		DisplayName nameData = nameManager.getNameObject(displayName);
		Ignore ignore = null;
		if (nameData == null) {
			//Player does not exist. In the main game, this would spring an error. Here, we will allow it.
			ignore = new Ignore(displayName);
			ignore.setDisplayNames(displayName, "");
		} else {
			ignore = new Ignore(nameData.username);
			ignore.setDisplayNames(nameData.getDisplayName(), nameData.getPrevName());
		}
		synchronized (this) {
			if (ignores.size() >= IGNORE_LIST_MAX) {
				//TODO add message send (ignore list full)
				return;
			}
			
			if (friends.containsKey(ignore.username)) {
				//TODO add message send (on friends list). This also needs to be fixed, as it only searches using a name
				return;
			}
			ignores.put(NameManager.simplifyName(ignore.username), ignore);
		}
		player.getActionSender().sendIgnore(ignore, false);
	}
	
	public void removeIgnore (String displayName) {
		DisplayName names = nameManager.getNameObject(displayName);
		if (names == null) {
			//This removes a player based off their display name. If the server allows players to choose display names which are being used as usernames, this should be removed.
			ignores.remove(NameManager.simplifyName(displayName));
		} else {
			ignores.remove(NameManager.simplifyName(names.username));
		}
	}
	
	/**
	 * Adds the specified friend to the player's friends list, after performing a few checks
	 * @param displayName The display name of the friend to add
	 */
	public void addFriend (String displayName) {		
		if (displayName.equalsIgnoreCase(player.getDisplayName()) 
				|| displayName.equalsIgnoreCase(player.getPrevDisplayName())) {
			//TODO add message send (cannot add self)
			return;
		}
		DisplayName nameData = nameManager.getNameObject(displayName);		
		Friend friend = null;
		if (nameData == null) {
			//Player does not exist. In the main game, this would spring an error. Here, we will allow it.
			friend = new Friend(displayName, false);
			friend.setDisplayNames(displayName, "");
		} else {
			friend = new Friend(nameData.username, false);
			friend.setDisplayNames(nameData.getDisplayName(), nameData.getPrevName());
		}
		//TODO add method which finds friend's player data (prev name, world, etc)
		
		synchronized (this) {//Synchronised to avoid concurrent modification issues
			if (friends.size() >= FRIENDS_LIST_MAX) {
				//TODO add message send (friend list full)
				return;
			}
			
			if (ignores.containsKey(friend.username)) {
				//TODO add message send (on ignore list)
				return;
			}			
			friends.put(NameManager.simplifyName(friend.username), friend);
		}
		player.getActionSender().sendFriend(friend, false);
	}
	
	public void removeFriend (String displayName) {
		DisplayName names = nameManager.getNameObject(displayName);
		if (names == null) {
			//This removes a player based off their display name. If the server allows players to choose display names which are being used as usernames, this should be removed.
			friends.remove(NameManager.simplifyName(displayName));
		} else {
			friends.remove(NameManager.simplifyName(names.username));
		}
		//TODO Add logic to switch the online status of the player for the removed friend
	}
}
