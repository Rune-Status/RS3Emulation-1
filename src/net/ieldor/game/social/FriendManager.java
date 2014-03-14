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

import java.util.ArrayList;

import net.ieldor.game.model.player.Player;

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
	
	public void initPlayer () {
		player.getActionSender().sendOnlineStatus(onlineStatus);
		player.getActionSender().sendFriends(friends);
		player.getActionSender().sendIgnores(ignores);
	}
	
	public void addIgnore (String displayName, boolean tillLogout) {		
		if (displayName.equalsIgnoreCase(player.getDisplayName()) 
				|| displayName.equalsIgnoreCase(player.getPrevDisplayName())) {
			//TODO add message send (cannot add self)
			return;
		}
		
		Ignore ignore = new Ignore(displayName, "", "");
		//TODO add method which finds ignore's previous display name
		synchronized (this) {
			if (ignores.size() >= IGNORE_LIST_MAX) {
				//TODO add message send (ignore list full)
				return;
			}
			
			if (friends.contains(displayName)) {
				//TODO add message send (on friends list)
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
		Friend friend = new Friend(displayName, "", null, 0, false, "");
		//TODO add method which finds ignore's player data (prev name, world, etc)
		
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
