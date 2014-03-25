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
package net.ieldor.network.codec.messages;

import net.ieldor.game.model.player.Player;
import net.ieldor.modules.worldlist.WorldData;

/**
 * RS3Emulator
 * LobbyLoginData.java
 * 16/03/2014
 * @author Sundays211
 */
public class LobbyLoginData {

	public final int rights;
	public final long membershipEndDate;
	public final int membershipFlags;
	public final int lastLoggedInDay;
	public final int recoveryQuestionsSetDay;
	public final int messageCount;
	public final String lastLoginIp;
	public final int emailStatus;
	public final String displayName;
	public final String defaultWorldEndpoint;
	public final int defaultWorldNodeID;
	
	public LobbyLoginData (int rights, long memberEndDate, int memberFlags, int lastLoginDay, int recoverySetDay, 
			int msgCount, String loginIP, int emailStatus, String displayName, WorldData defaultWorld) {
		this.rights = rights;
		this.membershipEndDate = memberEndDate;
		this.membershipFlags = memberFlags;
		this.lastLoggedInDay = lastLoginDay;
		this.recoveryQuestionsSetDay = recoverySetDay;
		this.messageCount = msgCount;
		this.lastLoginIp = loginIP;
		this.emailStatus = emailStatus;
		this.displayName = displayName;
		this.defaultWorldEndpoint = defaultWorld.getIp();
		this.defaultWorldNodeID = defaultWorld.getNodeId();
	}
}
