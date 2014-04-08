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
package net.ieldor.config;

/**
 * RS3Emulator
 * OutgoingOpcode.java
 * 4/04/2014
 * @author Sundays211
 */
public class OutgoingOpcode {
	public static final int KEEP_ALIVE_PACKET = 155;//803
	public static final int LARGE_VARP_PACKET = 153;//803
	public static final int SMALL_VARP_PACKET = 96;//803
	
	public static final int UNLOCK_FRIENDS_LIST = 18;//803
	public static final int ONLINE_STATUS_PACKET = 43;//803
	public static final int FRIENDS_PACKET = 56;//803
	public static final int IGNORES_PACKET = 68;//803
	public static final int FRIENDS_CHANNEL_PACKET = 82;
	public static final int CLAN_CHANNEL_PACKET = 47;//795
	
	public static final int STATIC_MAP_REGION_PACKET = 77;//803
	public static final int DYNAMIC_MAP_REGION_PACKET = 31;//803
	public static final int PLAYER_UPDATE_PACKET = 44;//803
	
	public static final int PLAYER_OPTION_PACKET = 3;//803
	public static final int RUN_ENERGY_PACKET = 13;//795
	public static final int SKILL_DATA_PACKET = 4;//803
	
	public static final int INTERFACE_PACKET = 49;//803
	public static final int INTERFACE_SETTINGS_PACKET = 134;//803
	public static final int WINDOW_PANE_PACKET = 76;//803
	public static final int RUN_CS2_PACKET = 167;//803
	
	public static final int WORLD_LIST_PACKET = 98;//803
	public static final int MESSAGE_PACKET = 137;//803
	public static final int FRIENDS_CHAT_MESSAGE_PACKET = 111;
}
