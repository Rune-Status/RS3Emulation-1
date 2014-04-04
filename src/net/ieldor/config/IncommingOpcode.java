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
 * IncomingOpcode.java
 * 4/04/2014
 * @author Sundays211
 */
public class IncommingOpcode {
	public final static int SCREEN_PACKET = 70;//803
	public final static int PING_PACKET = 92;//803
	public final static int WORLD_LIST_UPDATE = 56;//803
	
	public final static int ACTION_BUTTON1_PACKET = -1;
	public final static int ACTION_BUTTON2_PACKET = -1;
	public final static int ACTION_BUTTON3_PACKET = -1;
	public final static int ACTION_BUTTON4_PACKET = -1;
	public final static int ACTION_BUTTON5_PACKET = -1;
	public final static int ACTION_BUTTON6_PACKET = -1;
	public final static int ACTION_BUTTON7_PACKET = -1;
	public final static int ACTION_BUTTON8_PACKET = -1;
	public final static int ACTION_BUTTON9_PACKET = -1;
	public final static int ACTION_BUTTON10_PACKET = -1;
	
	public final static int JOIN_FRIEND_CHAT_PACKET = 71;//795
	public final static int ONLINE_STATUS_PACKET = 22;//803
	public final static int ADD_FRIEND_PACKET = 97;//803
	public final static int REMOVE_FRIEND_PACKET = 58;//803
	public final static int ADD_IGNORE_PACKET = 81;//803
	public final static int REMOVE_IGNORE_PACKET = 76;//803
	
	private static final int[] SIZES_803 = new int[120];
	static {//TODO Identify packets
		SIZES_803[0] = -1;
		SIZES_803[1] = -2;
		SIZES_803[2] = -1;
		SIZES_803[3] = 7;
		SIZES_803[4] = -1;
		SIZES_803[5] = 7;
		SIZES_803[6] = 8;
		SIZES_803[7] = 4;
		SIZES_803[8] = -1;
		SIZES_803[9] = -1;
		SIZES_803[10] = 16;
		SIZES_803[11] = -1;
		SIZES_803[12] = 3;
		SIZES_803[13] = 1;
		SIZES_803[14] = 2;
		SIZES_803[15] = 8;
		SIZES_803[16] = 8;
		SIZES_803[17] = 2;
		SIZES_803[18] = 7;
		SIZES_803[19] = -1;
		SIZES_803[20] = -2;
		SIZES_803[21] = 0;
		SIZES_803[22] = 3;
		SIZES_803[23] = 4;
		SIZES_803[24] = 3;
		SIZES_803[25] = 3;
		SIZES_803[26] = 0;
		SIZES_803[27] = 4;
		SIZES_803[28] = 1;
		SIZES_803[29] = -1;
		SIZES_803[30] = 9;
		SIZES_803[31] = 3;
		SIZES_803[32] = 3;
		SIZES_803[33] = 8;
		SIZES_803[34] = 6;
		SIZES_803[35] = 7;
		SIZES_803[36] = 3;
		SIZES_803[37] = 11;
		SIZES_803[38] = -2;
		SIZES_803[39] = 12;
		SIZES_803[40] = 1;
		SIZES_803[41] = -2;
		SIZES_803[42] = 4;
		SIZES_803[43] = 11;
		SIZES_803[44] = -2;
		SIZES_803[45] = 9;
		SIZES_803[46] = 15;
		SIZES_803[47] = -1;
		SIZES_803[48] = 15;
		SIZES_803[49] = -1;
		SIZES_803[50] = 9;
		SIZES_803[51] = 17;
		SIZES_803[52] = 4;
		SIZES_803[53] = -1;
		SIZES_803[54] = 0;
		SIZES_803[55] = 7;
		SIZES_803[56] = 4;
		SIZES_803[57] = 2;
		SIZES_803[58] = -1;
		SIZES_803[59] = 8;
		SIZES_803[60] = -1;
		SIZES_803[61] = 3;
		SIZES_803[62] = -1;
		SIZES_803[63] = 4;
		SIZES_803[64] = 0;
		SIZES_803[65] = -2;
		SIZES_803[66] = 18;
		SIZES_803[67] = 8;
		SIZES_803[68] = 6;
		SIZES_803[69] = -1;
		SIZES_803[70] = 6;
		SIZES_803[71] = 3;
		SIZES_803[72] = -1;
		SIZES_803[73] = -1;
		SIZES_803[74] = -1;
		SIZES_803[75] = 3;
		SIZES_803[76] = -1;
		SIZES_803[77] = -1;
		SIZES_803[78] = 8;
		SIZES_803[79] = 3;
		SIZES_803[80] = 1;
		SIZES_803[81] = -1;
		SIZES_803[82] = 16;
		SIZES_803[83] = 4;
		SIZES_803[84] = -1;
		SIZES_803[85] = 4;
		SIZES_803[86] = 3;
		SIZES_803[87] = 4;
		SIZES_803[88] = 3;
		SIZES_803[89] = 9;
		SIZES_803[90] = 3;
		SIZES_803[91] = 7;
		SIZES_803[92] = 0;
		SIZES_803[93] = -1;
		SIZES_803[94] = 3;
		SIZES_803[95] = 5;
		SIZES_803[96] = -1;
		SIZES_803[97] = -1;
		SIZES_803[98] = -2;
		SIZES_803[99] = -1;
		SIZES_803[100] = 3;
		SIZES_803[101] = 3;
		SIZES_803[102] = 9;
		SIZES_803[103] = -2;
		SIZES_803[104] = 1;
		SIZES_803[105] = 1;
		SIZES_803[106] = 2;
		SIZES_803[107] = 8;
		SIZES_803[108] = -1;
		SIZES_803[109] = -1;
		SIZES_803[110] = 7;
		SIZES_803[111] = 18;
		SIZES_803[112] = 8;
		SIZES_803[113] = 9;
		SIZES_803[114] = -2;
		SIZES_803[115] = 9;
		SIZES_803[116] = 0;
		SIZES_803[117] = 9;
		SIZES_803[118] = 8;
		SIZES_803[119] = -2;
	}
	
	public static int[] getPacketSizes () {
		return SIZES_803;
	}
}
