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

import net.ieldor.Main;
import net.ieldor.game.model.player.Player;
import net.ieldor.modules.login.BinaryPlayerManager.StreamUtil;
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
	
	private static final int FRIENDS_LIST_MAX = 400;
	private static final int IGNORE_LIST_MAX = 400;
	
	private ArrayList<Friend> friends = new ArrayList<Friend>(FRIENDS_LIST_MAX);
	private ArrayList<Ignore> ignores = new ArrayList<Ignore>(IGNORE_LIST_MAX);
	
	private OnlineStatus onlineStatus = OnlineStatus.NOBODY;
	
	public FriendManager (Player player) {
		this.player = player;
	}
	
	public void initFriends () {
		for (Friend f : friends) {
			DisplayName nameData = Main.getloginServer().nameManager.getDisplayNamesFromUsername(f.username);
			f.setDisplayNames(nameData.getDisplayName(), nameData.getPrevName());
			//TODO Add world determining logic
		}
		for (Ignore i : ignores) {
			DisplayName nameData = Main.getloginServer().nameManager.getDisplayNamesFromUsername(i.username);
			i.setDisplayNames(nameData.getDisplayName(), nameData.getPrevName());
		}
		player.getActionSender().sendOnlineStatus(onlineStatus);
		player.getActionSender().sendFriends(friends);
		player.getActionSender().sendIgnores(ignores);
	}
	
	public void serialise (DataOutputStream output) throws IOException {
		synchronized (this) {
			output.writeShort(friends.size());
			for (Friend f : friends) {
				StreamUtil.writeString(output, f.username);
				output.writeByte(f.isReferred() ? 1 : 0);
				output.writeByte(f.getFcRank());
				StreamUtil.writeString(output, f.getNote());
			}
			output.writeShort(ignores.size());
			for (Ignore i : ignores) {
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
					friends.add(f);
				}
				int ignoreListSize = input.readUnsignedShort();
				for (int i=0;i<ignoreListSize;i++) {
					name = StreamUtil.readString(input);
					note = StreamUtil.readString(input);
					Ignore ig = new Ignore(name, note);
					ignores.add(ig);
				}
				int onlineStatusCode = input.readUnsignedByte();
				onlineStatus = OnlineStatus.get(onlineStatusCode);
			}
		}
	}
	
	public void addIgnore (String displayName, boolean tillLogout) {		
		if (displayName.equalsIgnoreCase(player.getDisplayName()) 
				|| displayName.equalsIgnoreCase(player.getPrevDisplayName())) {
			//TODO add message send (cannot add self)
			return;
		}
		
		DisplayName nameData = Main.getloginServer().nameManager.getNameObject(displayName);
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
			
			if (friends.contains(displayName)) {
				//TODO add message send (on friends list). This also needs to be fixed, as it only searches using a name
				return;
			}
			ignores.add(ignore);
		}
		player.getActionSender().sendIgnore(ignore, false);
	}
	
	public void addFriend (String displayName) {		
		if (displayName.equalsIgnoreCase(player.getDisplayName()) 
				|| displayName.equalsIgnoreCase(player.getPrevDisplayName())) {
			//TODO add message send (cannot add self)
			return;
		}
		DisplayName nameData = Main.getloginServer().nameManager.getNameObject(displayName);		
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
				//TODO add message send (ignore list full)
				return;
			}
			
			if (ignores.contains(displayName)) {
				//TODO add message send (on ignore list)
				return;
			}			
			friends.add(friend);
		}
		player.getActionSender().sendFriend(friend, false);
	}
}
