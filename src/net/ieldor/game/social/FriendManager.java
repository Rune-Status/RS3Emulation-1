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
import java.util.concurrent.ConcurrentHashMap;

import net.ieldor.Main;
import net.ieldor.game.model.player.Player;
import net.ieldor.modules.login.BinaryPlayerManager.StreamUtil;
import net.ieldor.modules.login.NameManager;
import net.ieldor.modules.login.NameManager.DisplayName;
import net.ieldor.modules.worldlist.World;

/**
 * The tools necessary to control a player's friends list, ignore list, and private messaging. 
 * Also has the tools for managing the player's own friends chat (though the friends chat channel itself is run separately)
 *
 * @author Sundays211
 *
 */
public class FriendManager {
	
	private static final ConcurrentHashMap<String, FriendManager> onlinePlayers = new ConcurrentHashMap<String, FriendManager>();
	
	private Player player;
	private NameManager nameManager;
	
	private static final int FRIENDS_LIST_MAX = 400;
	private static final int IGNORE_LIST_MAX = 400;
	
	private HashMap<String, Friend> friends = new HashMap<String, Friend>(FRIENDS_LIST_MAX);
	private HashMap<String, Ignore> ignores = new HashMap<String, Ignore>(IGNORE_LIST_MAX);
	
	private OnlineStatus onlineStatus = OnlineStatus.NOBODY;
	
	private World currentWorld = null;
	
	public FriendManager (Player player, NameManager nameManager) {
		this.player = player;
		this.nameManager = nameManager;
	}
	
	public void disconnect () {
		this.currentWorld = null;
		sendStatusUpdate(this, false);
	}
	
	public String getSimplifedUsername () {
		return NameManager.simplifyName(player.getUsername());
	}
	
	public void init () {
		onlinePlayers.put(NameManager.simplifyName(player.getUsername()), this);
		currentWorld = player.getWorldInfo();
		for (Friend f : friends.values()) {
			DisplayName nameData = nameManager.getDisplayNamesFromUsername(f.username);
			if (nameData == null) {
				f.setDisplayNames(f.username, "");
			} else {
				f.setDisplayNames(nameData.getDisplayName(), nameData.getPrevName());
			}
			f.setWorld(getFriendWorld(f.username));
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
		sendStatusUpdate(this, false);
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
					if (name.length() == 0) {
						continue;
					}
					Friend f = new Friend(name, isReferred, fcRank, note);
					friends.put(NameManager.simplifyName(name), f);
				}
				int ignoreListSize = input.readUnsignedShort();
				for (int i=0;i<ignoreListSize;i++) {
					name = StreamUtil.readString(input);
					note = StreamUtil.readString(input);
					if (name.length() == 0) {
						continue;
					}
					Ignore ig = new Ignore(name, note);
					ignores.put(NameManager.simplifyName(name), ig);
				}
				int onlineStatusCode = input.readUnsignedByte();
				onlineStatus = OnlineStatus.get(onlineStatusCode);
			}
		}
	}
	
	public OnlineStatus getOnlineStatus () {
		return onlineStatus;
	}
	
	public World getWorldInfo (FriendManager friend) {
		if (onlineStatus == OnlineStatus.EVERYONE) {
			return currentWorld;
		} else if (onlineStatus == OnlineStatus.FRIENDS 
				&& friends.containsKey(friend.getSimplifedUsername())) {
			return currentWorld;
		} else {
			return null;
		}
	}
	
	private World getFriendWorld (String friendUsername) {
		if (!onlinePlayers.containsKey(NameManager.simplifyName(friendUsername))) {
			return null;
		} else {
			FriendManager friendData = onlinePlayers.get(NameManager.simplifyName(friendUsername));
			return friendData.getWorldInfo(this);
			/*if (friendData.getOnlineStatus() == OnlineStatus.EVERYONE) {
				return friendData.getWorldInfo();
			} else if (friendData.getOnlineStatus() == OnlineStatus.FRIENDS
					&& friendData.friends.containsKey(NameManager.simplifyName(player.getUsername()))) {
				return friendData.getWorldInfo();
			}*/
		}
	}
	
	private static void sendStatusUpdate (FriendManager p, boolean isNameChange) {
		for (FriendManager f : onlinePlayers.values()) {
			//if (onlineStatus == OnlineStatus.FRIENDS && friends.containsKey(NameManager.simplifyName(f.player.getUsername()))
			f.setFriendStatus(p, isNameChange);
		}
	}
	
	private void setFriendStatus (FriendManager p2, boolean isNameChange) {
		if (friends.containsKey(p2.getSimplifedUsername())) {
			Friend f = friends.get(p2.getSimplifedUsername());
			f.setWorld(p2.getWorldInfo(this));
			player.getActionSender().sendFriend(f, isNameChange);
		}
	}
	
	public void setOnlineStatus (OnlineStatus status) {
		onlineStatus = status;		
		player.getActionSender().sendOnlineStatus(status);
		sendStatusUpdate(this, false);//Corrects the online status for all other players		
	}
	
	public void addIgnore (String displayName, boolean tillLogout) {
		if (displayName.length() == 0) {
			return;//Don't bother adding empty names
		}
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
		if (displayName.length() == 0) {
			return;//Don't bother adding empty names
		}
		if (displayName.equalsIgnoreCase(player.getDisplayName()) 
				|| displayName.equalsIgnoreCase(player.getPrevDisplayName())) {
			//TODO add message send (cannot add self)
			return;
		}
		DisplayName nameData = nameManager.getNameObject(displayName);		
		Friend friend = null;
		FriendManager friendData;
		if (nameData == null) {
			//Player does not exist. In the main game, this would spring an error. Here, we will allow it.
			friend = new Friend(displayName, false);
			friend.setDisplayNames(displayName, "");
			friend.setWorld(getFriendWorld(displayName));//Finds the world data for the friend
			friendData = onlinePlayers.get(NameManager.simplifyName(displayName));
		} else {
			friend = new Friend(nameData.username, false);
			friend.setDisplayNames(nameData.getDisplayName(), nameData.getPrevName());
			friend.setWorld(getFriendWorld(nameData.username));//Finds the world data for the friend
			friendData = onlinePlayers.get(NameManager.simplifyName(displayName));
		}
		
		synchronized (this) {//Synchronised to avoid concurrent modification issues
			if (friends.size() >= FRIENDS_LIST_MAX) {
				//TODO add message send (friend list full)
				return;
			}
			
			if (ignores.containsKey(NameManager.simplifyName(friend.username))) {
				//TODO add message send (on ignore list)
				return;
			}			
			friends.put(NameManager.simplifyName(friend.username), friend);
		}
		player.getActionSender().sendFriend(friend, false);
		
		if (friendData != null) {
			friendData.setFriendStatus(this, false);//Updates the online status displayed of the current player visible to the player who was removed
		}
	}
	
	/**
	 * Removes the player of the specified name from the current player's friends list
	 * @param displayName the display name of the player to remove
	 */
	public void removeFriend (String displayName) {
		DisplayName names = nameManager.getNameObject(displayName);
		FriendManager friendData;
		if (names == null) {
			//This removes a player based off their display name. If the server allows players to choose display names which are being used as usernames, this should be removed.
			friends.remove(NameManager.simplifyName(displayName));
			friendData = onlinePlayers.get(NameManager.simplifyName(displayName));
		} else {
			friends.remove(NameManager.simplifyName(names.username));
			friendData = onlinePlayers.get(NameManager.simplifyName(names.username));
		}
		if (friendData != null) {
			friendData.setFriendStatus(this, false);//Updates the online status displayed of the current player visible to the player who was removed
		}
	}
}
