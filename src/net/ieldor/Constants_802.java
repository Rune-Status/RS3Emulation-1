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
package net.ieldor;

import java.math.BigInteger;

/**
 * A class used to store constants for the game.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public final class Constants_802 {

	/**
	 * The skill menu configs.
	 */
	public static final int[] MENU_ID = {
		1, 2, 5, 3, 7, 4, 12, 22, 6, 8, 9, 
		10, 11, 19, 20, 23, 13, 14, 15, 16,
		17, 18, 21, 24
	};
	
	/**
	 * The sub-skill menu configs.
	 */
	public static final int[] SUB_CONFIG = {
		0, 1024, 2048, 3072, 4096, 5120, 6144, 7168, 
		8192, 9216, 10240, 11264, 12288, 13312, 14355
	};	

	public static final int[] CACHE_DATA_SIZES = { 2303, 69795, 41433, 35866,
		358716, 44375, 0, 18361, 24018, 124392, 976352, 336290, 478954,
		627398, 931989, 28244, 597933, 18398, 1244, 37784, 1973, 119,
		1096329, 2737242, 8010, 21656 };

	public static final int ServerRevision = 802;

	public static final int MAX_WORLD_PLAYERS = 2000;
	
	public static final BigInteger JS5ModulusKey = new BigInteger("92952295964155672087801015402111750591919500795257231765611346743023141259087751200849540075517787992536588533840502270599726488785462782810450546358220539026800123618215080154270389875550255531705255494326594317637443265761822792347000385003452391742987636722632882107837694393096626546031683632300323372031");

	public static final BigInteger JS5PrivateKey = new BigInteger("48844029607909929282080219851655163753063517835529068275083137145982131295000747300835504541261906753238247645275176117230165873030406752131258615980047884895054026459192588976183254088467205779659961501829292823408290106695592149680053016585516891071746766448461496845037459094045320573736639805712121612241");

	public static final int ServerSubRevision = 1;
	
	/**
	 * Possible map sizes
	 */
	public static final int[] MAP_SIZES = { 104, 120, 136, 168, 72 };
	
	/**
	 * The server token.
	 */
	public static final String SERVER_TOKEN = "GrJAcr2Zt9GLjqvzF0cU1x8W9ljVk07L";

	/**
	 * The second server token.
	 */
	public static final String SECOND_SERVER_TOKEN = "wwGlrZHF5gKN6D3mDdihco3oPeYN2KFybL9hUUFqOvk";

	public static final int[] LOBBY_CONFIGS_802 = new int[4200];
	static {
		LOBBY_CONFIGS_802[27] = -1;
		LOBBY_CONFIGS_802[61] = -1;
		LOBBY_CONFIGS_802[85] = -1;
		LOBBY_CONFIGS_802[89] = 2;
		LOBBY_CONFIGS_802[97] = -1;
		LOBBY_CONFIGS_802[120] = -1;
		LOBBY_CONFIGS_802[135] = -1;
		LOBBY_CONFIGS_802[142] = -1;
		LOBBY_CONFIGS_802[161] = 0;
		LOBBY_CONFIGS_802[178] = 1;
		LOBBY_CONFIGS_802[183] = 5;
		LOBBY_CONFIGS_802[186] = -1;
		LOBBY_CONFIGS_802[187] = -1;
		LOBBY_CONFIGS_802[188] = -1;
		LOBBY_CONFIGS_802[190] = 2;
		LOBBY_CONFIGS_802[249] = -1;
		LOBBY_CONFIGS_802[250] = -1;
		LOBBY_CONFIGS_802[260] = -1;
		LOBBY_CONFIGS_802[261] = -1;
		LOBBY_CONFIGS_802[262] = -1;
		LOBBY_CONFIGS_802[284] = 20;
		LOBBY_CONFIGS_802[299] = -1;
		LOBBY_CONFIGS_802[300] = -1;
		LOBBY_CONFIGS_802[304] = -1;
		LOBBY_CONFIGS_802[305] = -1;
		LOBBY_CONFIGS_802[306] = -1;
		LOBBY_CONFIGS_802[414] = 64;
		LOBBY_CONFIGS_802[429] = -1;
		LOBBY_CONFIGS_802[438] = 5;
		LOBBY_CONFIGS_802[452] = 0;
		LOBBY_CONFIGS_802[453] = 4;
		LOBBY_CONFIGS_802[454] = 4;
		LOBBY_CONFIGS_802[455] = 0;
		LOBBY_CONFIGS_802[456] = 1;
		LOBBY_CONFIGS_802[457] = 1;
		LOBBY_CONFIGS_802[460] = 4;
		LOBBY_CONFIGS_802[462] = 1;
		LOBBY_CONFIGS_802[463] = 1;
		LOBBY_CONFIGS_802[476] = -1;
		LOBBY_CONFIGS_802[612] = 18;
		LOBBY_CONFIGS_802[616] = -1;
		LOBBY_CONFIGS_802[623] = -1;
		LOBBY_CONFIGS_802[662] = 3;
		LOBBY_CONFIGS_802[664] = 6;
		LOBBY_CONFIGS_802[686] = -1;
		LOBBY_CONFIGS_802[687] = -1;
		LOBBY_CONFIGS_802[688] = -1;
		LOBBY_CONFIGS_802[698] = -1;
		LOBBY_CONFIGS_802[699] = -1;
		LOBBY_CONFIGS_802[700] = -1;
		LOBBY_CONFIGS_802[727] = 97;
		LOBBY_CONFIGS_802[728] = 113;
		LOBBY_CONFIGS_802[729] = 17;
		LOBBY_CONFIGS_802[730] = 49;
		LOBBY_CONFIGS_802[732] = 66;
		LOBBY_CONFIGS_802[735] = 33;
		LOBBY_CONFIGS_802[739] = 97;
		LOBBY_CONFIGS_802[740] = 113;
		LOBBY_CONFIGS_802[741] = 17;
		LOBBY_CONFIGS_802[742] = 49;
		LOBBY_CONFIGS_802[744] = 66;
		LOBBY_CONFIGS_802[747] = 33;
		LOBBY_CONFIGS_802[751] = 17;
		LOBBY_CONFIGS_802[752] = 18;
		LOBBY_CONFIGS_802[753] = 81;
		LOBBY_CONFIGS_802[754] = 50;
		LOBBY_CONFIGS_802[755] = 98;
		LOBBY_CONFIGS_802[757] = 114;
		LOBBY_CONFIGS_802[760] = 19;
		LOBBY_CONFIGS_802[761] = 35;
		LOBBY_CONFIGS_802[762] = 38;
		LOBBY_CONFIGS_802[811] = -1;
		LOBBY_CONFIGS_802[812] = -1;
		LOBBY_CONFIGS_802[813] = -1;
		LOBBY_CONFIGS_802[814] = -1;
		LOBBY_CONFIGS_802[815] = -1;
		LOBBY_CONFIGS_802[816] = -1;
		LOBBY_CONFIGS_802[817] = -1;
		LOBBY_CONFIGS_802[818] = -1;
		LOBBY_CONFIGS_802[819] = -1;
		LOBBY_CONFIGS_802[820] = -1;
		LOBBY_CONFIGS_802[821] = -1;
		LOBBY_CONFIGS_802[822] = -1;
		LOBBY_CONFIGS_802[823] = -1;
		LOBBY_CONFIGS_802[824] = -1;
		LOBBY_CONFIGS_802[825] = -1;
		LOBBY_CONFIGS_802[826] = -1;
		LOBBY_CONFIGS_802[827] = -1;
		LOBBY_CONFIGS_802[828] = -1;
		LOBBY_CONFIGS_802[829] = -1;
		LOBBY_CONFIGS_802[830] = -1;
		LOBBY_CONFIGS_802[831] = -1;
		LOBBY_CONFIGS_802[832] = -1;
		LOBBY_CONFIGS_802[833] = -1;
		LOBBY_CONFIGS_802[834] = -1;
		LOBBY_CONFIGS_802[835] = -1;
		LOBBY_CONFIGS_802[836] = -1;
		LOBBY_CONFIGS_802[837] = -1;
		LOBBY_CONFIGS_802[838] = -1;
		LOBBY_CONFIGS_802[839] = -1;
		LOBBY_CONFIGS_802[840] = -1;
		LOBBY_CONFIGS_802[841] = -1;
		LOBBY_CONFIGS_802[842] = -1;
		LOBBY_CONFIGS_802[843] = -1;
		LOBBY_CONFIGS_802[844] = -1;
		LOBBY_CONFIGS_802[845] = -1;
		LOBBY_CONFIGS_802[846] = -1;
		LOBBY_CONFIGS_802[847] = -1;
		LOBBY_CONFIGS_802[848] = -1;
		LOBBY_CONFIGS_802[849] = -1;
		LOBBY_CONFIGS_802[850] = -1;
		LOBBY_CONFIGS_802[851] = -1;
		LOBBY_CONFIGS_802[852] = -1;
		LOBBY_CONFIGS_802[853] = -1;
		LOBBY_CONFIGS_802[854] = -1;
		LOBBY_CONFIGS_802[855] = -1;
		LOBBY_CONFIGS_802[856] = -1;
		LOBBY_CONFIGS_802[857] = -1;
		LOBBY_CONFIGS_802[858] = -1;
		LOBBY_CONFIGS_802[859] = -1;
		LOBBY_CONFIGS_802[860] = -1;
		LOBBY_CONFIGS_802[861] = -1;
		LOBBY_CONFIGS_802[862] = -1;
		LOBBY_CONFIGS_802[863] = -1;
		LOBBY_CONFIGS_802[864] = -1;
		LOBBY_CONFIGS_802[865] = -1;
		LOBBY_CONFIGS_802[866] = -1;
		LOBBY_CONFIGS_802[867] = -1;
		LOBBY_CONFIGS_802[868] = -1;
		LOBBY_CONFIGS_802[869] = -1;
		LOBBY_CONFIGS_802[870] = -1;
		LOBBY_CONFIGS_802[871] = -1;
		LOBBY_CONFIGS_802[872] = -1;
		LOBBY_CONFIGS_802[873] = -1;
		LOBBY_CONFIGS_802[874] = -1;
		LOBBY_CONFIGS_802[875] = -1;
		LOBBY_CONFIGS_802[876] = -1;
		LOBBY_CONFIGS_802[877] = -1;
		LOBBY_CONFIGS_802[878] = -1;
		LOBBY_CONFIGS_802[879] = -1;
		LOBBY_CONFIGS_802[880] = -1;
		LOBBY_CONFIGS_802[881] = -1;
		LOBBY_CONFIGS_802[882] = -1;
		LOBBY_CONFIGS_802[883] = -1;
		LOBBY_CONFIGS_802[884] = -1;
		LOBBY_CONFIGS_802[885] = -1;
		LOBBY_CONFIGS_802[886] = -1;
		LOBBY_CONFIGS_802[887] = -1;
		LOBBY_CONFIGS_802[888] = -1;
		LOBBY_CONFIGS_802[889] = -1;
		LOBBY_CONFIGS_802[890] = -1;
		LOBBY_CONFIGS_802[891] = -1;
		LOBBY_CONFIGS_802[892] = -1;
		LOBBY_CONFIGS_802[893] = -1;
		LOBBY_CONFIGS_802[894] = -1;
		LOBBY_CONFIGS_802[1069] = -1;
		LOBBY_CONFIGS_802[1071] = 39;
		LOBBY_CONFIGS_802[1072] = 1;
		LOBBY_CONFIGS_802[1073] = 4;
		LOBBY_CONFIGS_802[1100] = 6;
		LOBBY_CONFIGS_802[1101] = -1;
		LOBBY_CONFIGS_802[1104] = -1;
		LOBBY_CONFIGS_802[1106] = -1;
		LOBBY_CONFIGS_802[1118] = 99;
		LOBBY_CONFIGS_802[1119] = 99;
		LOBBY_CONFIGS_802[1120] = 99;
		LOBBY_CONFIGS_802[1122] = 99;
		LOBBY_CONFIGS_802[1125] = 80;
		LOBBY_CONFIGS_802[1129] = 99;
		LOBBY_CONFIGS_802[1130] = 99;
		LOBBY_CONFIGS_802[1131] = 99;
		LOBBY_CONFIGS_802[1132] = 99;
		LOBBY_CONFIGS_802[1137] = 55;
		LOBBY_CONFIGS_802[1142] = 85;
		LOBBY_CONFIGS_802[1143] = 85;
		LOBBY_CONFIGS_802[1144] = 84;
		LOBBY_CONFIGS_802[1145] = 96;
		LOBBY_CONFIGS_802[1147] = 81;
		LOBBY_CONFIGS_802[1150] = 58;
		LOBBY_CONFIGS_802[1154] = 82;
		LOBBY_CONFIGS_802[1155] = 66;
		LOBBY_CONFIGS_802[1156] = 61;
		LOBBY_CONFIGS_802[1157] = 96;
		LOBBY_CONFIGS_802[1162] = 32;
		LOBBY_CONFIGS_802[1167] = 32;
		LOBBY_CONFIGS_802[1168] = -1;
		LOBBY_CONFIGS_802[1169] = -1;
		LOBBY_CONFIGS_802[1170] = -1;
		LOBBY_CONFIGS_802[1172] = 58;
		LOBBY_CONFIGS_802[1175] = -1;
		LOBBY_CONFIGS_802[1238] = -1;
		LOBBY_CONFIGS_802[1246] = 8;
		LOBBY_CONFIGS_802[1247] = 16;
		LOBBY_CONFIGS_802[1263] = -1;
		LOBBY_CONFIGS_802[1296] = 20;
		LOBBY_CONFIGS_802[1297] = 83;
		LOBBY_CONFIGS_802[1302] = -1;
		LOBBY_CONFIGS_802[1335] = 2;
		LOBBY_CONFIGS_802[1384] = -1;
		LOBBY_CONFIGS_802[1442] = -1;
		LOBBY_CONFIGS_802[1455] = -1;
		LOBBY_CONFIGS_802[1499] = -1;
		LOBBY_CONFIGS_802[1500] = -1;
		LOBBY_CONFIGS_802[1501] = -1;
		LOBBY_CONFIGS_802[1502] = -1;
		LOBBY_CONFIGS_802[1503] = -1;
		LOBBY_CONFIGS_802[1504] = -1;
		LOBBY_CONFIGS_802[1506] = -1;
		LOBBY_CONFIGS_802[1507] = -1;
		LOBBY_CONFIGS_802[1508] = -1;
		LOBBY_CONFIGS_802[1509] = -1;
		LOBBY_CONFIGS_802[1510] = -1;
		LOBBY_CONFIGS_802[1526] = 109;
		LOBBY_CONFIGS_802[1543] = 1;
		LOBBY_CONFIGS_802[1579] = 8;
		LOBBY_CONFIGS_802[1581] = 60;
		LOBBY_CONFIGS_802[1602] = -1;
		LOBBY_CONFIGS_802[1604] = -1;
		LOBBY_CONFIGS_802[1606] = 11;
		LOBBY_CONFIGS_802[1608] = -1;
		LOBBY_CONFIGS_802[1609] = -1;
		LOBBY_CONFIGS_802[1613] = 1;
		LOBBY_CONFIGS_802[1651] = 4;
		LOBBY_CONFIGS_802[1661] = -1;
		LOBBY_CONFIGS_802[1696] = 41;
		LOBBY_CONFIGS_802[1701] = -1;
		LOBBY_CONFIGS_802[1720] = -1;
		LOBBY_CONFIGS_802[1728] = 2;
		LOBBY_CONFIGS_802[1754] = 57;
		LOBBY_CONFIGS_802[1760] = -1;
		LOBBY_CONFIGS_802[1761] = -1;
		LOBBY_CONFIGS_802[1762] = -1;
		LOBBY_CONFIGS_802[1763] = -1;
		LOBBY_CONFIGS_802[1764] = -1;
		LOBBY_CONFIGS_802[1765] = -1;
		LOBBY_CONFIGS_802[1766] = -1;
		LOBBY_CONFIGS_802[1767] = -1;
		LOBBY_CONFIGS_802[1775] = 4;
		LOBBY_CONFIGS_802[1784] = -1;
		LOBBY_CONFIGS_802[1787] = 60;
		LOBBY_CONFIGS_802[1788] = 1;
		LOBBY_CONFIGS_802[1789] = 3;
		LOBBY_CONFIGS_802[1831] = -1;
		LOBBY_CONFIGS_802[1846] = 126;
		LOBBY_CONFIGS_802[1858] = 8;
		LOBBY_CONFIGS_802[2002] = 29;
		LOBBY_CONFIGS_802[2004] = 3;
		LOBBY_CONFIGS_802[2088] = -1;
		LOBBY_CONFIGS_802[2137] = 6;
		LOBBY_CONFIGS_802[2163] = 21;
		LOBBY_CONFIGS_802[2164] = 9;
		LOBBY_CONFIGS_802[2171] = 61;
		LOBBY_CONFIGS_802[2175] = 6;
		LOBBY_CONFIGS_802[2183] = 3;
		LOBBY_CONFIGS_802[2227] = 4;
		LOBBY_CONFIGS_802[2261] = 100;
		LOBBY_CONFIGS_802[2262] = 10;
		LOBBY_CONFIGS_802[2268] = 10;
		LOBBY_CONFIGS_802[2279] = -1;
		LOBBY_CONFIGS_802[2280] = -1;
		LOBBY_CONFIGS_802[2281] = -1;
		LOBBY_CONFIGS_802[2282] = -1;
		LOBBY_CONFIGS_802[2283] = -1;
		LOBBY_CONFIGS_802[2284] = -1;
		LOBBY_CONFIGS_802[2324] = 5;
		LOBBY_CONFIGS_802[2325] = 102;
		LOBBY_CONFIGS_802[2339] = 80;
		LOBBY_CONFIGS_802[2347] = 50;
		LOBBY_CONFIGS_802[2355] = 10;
		LOBBY_CONFIGS_802[2388] = 110;
		LOBBY_CONFIGS_802[2412] = 35;
		LOBBY_CONFIGS_802[2473] = 3;
		LOBBY_CONFIGS_802[2484] = 13;
		LOBBY_CONFIGS_802[2492] = 2;
		LOBBY_CONFIGS_802[2523] = 1;
		LOBBY_CONFIGS_802[2533] = -1;
		LOBBY_CONFIGS_802[2547] = 7;
		LOBBY_CONFIGS_802[2551] = 6;
		LOBBY_CONFIGS_802[2552] = 4;
		LOBBY_CONFIGS_802[2553] = 1;
		LOBBY_CONFIGS_802[2594] = 4;
		LOBBY_CONFIGS_802[2604] = -1;
		LOBBY_CONFIGS_802[2605] = -1;
		LOBBY_CONFIGS_802[2606] = -1;
		LOBBY_CONFIGS_802[2607] = -1;
		LOBBY_CONFIGS_802[2608] = -1;
		LOBBY_CONFIGS_802[2609] = -1;
		LOBBY_CONFIGS_802[2610] = -1;
		LOBBY_CONFIGS_802[2611] = -1;
		LOBBY_CONFIGS_802[2615] = 8;
		LOBBY_CONFIGS_802[2644] = -1;
		LOBBY_CONFIGS_802[2645] = -1;
		LOBBY_CONFIGS_802[2646] = -1;
		LOBBY_CONFIGS_802[2647] = -1;
		LOBBY_CONFIGS_802[2648] = -1;
		LOBBY_CONFIGS_802[2649] = -1;
		LOBBY_CONFIGS_802[2650] = -1;
		LOBBY_CONFIGS_802[2651] = -1;
		LOBBY_CONFIGS_802[2652] = -1;
		LOBBY_CONFIGS_802[2653] = -1;
		LOBBY_CONFIGS_802[2654] = -1;
		LOBBY_CONFIGS_802[2655] = -1;
		LOBBY_CONFIGS_802[2656] = -1;
		LOBBY_CONFIGS_802[2661] = 9;
		LOBBY_CONFIGS_802[2662] = -1;
		LOBBY_CONFIGS_802[2663] = 11;
		LOBBY_CONFIGS_802[2669] = 2;
		LOBBY_CONFIGS_802[2689] = 6;
		LOBBY_CONFIGS_802[2692] = 100;
		LOBBY_CONFIGS_802[2693] = 6;
		LOBBY_CONFIGS_802[2695] = 4;
		LOBBY_CONFIGS_802[2732] = 11;
		LOBBY_CONFIGS_802[2738] = 7;
		LOBBY_CONFIGS_802[2772] = 5;
		LOBBY_CONFIGS_802[2776] = -1;
		LOBBY_CONFIGS_802[2793] = 15;
		LOBBY_CONFIGS_802[2805] = 17;
		LOBBY_CONFIGS_802[2807] = -1;
		LOBBY_CONFIGS_802[2810] = 21;
		LOBBY_CONFIGS_802[2900] = 15;
		LOBBY_CONFIGS_802[2947] = -1;
		LOBBY_CONFIGS_802[2956] = -1;
		LOBBY_CONFIGS_802[2957] = -1;
		LOBBY_CONFIGS_802[2958] = -1;
		LOBBY_CONFIGS_802[2975] = -1;
		LOBBY_CONFIGS_802[2985] = -1;
		LOBBY_CONFIGS_802[3013] = -1;
		LOBBY_CONFIGS_802[3014] = -1;
		LOBBY_CONFIGS_802[3015] = -1;
		LOBBY_CONFIGS_802[3016] = -1;
		LOBBY_CONFIGS_802[3017] = 56;
		LOBBY_CONFIGS_802[3045] = -1;
		LOBBY_CONFIGS_802[3096] = 20;
		LOBBY_CONFIGS_802[3109] = 15;
		LOBBY_CONFIGS_802[3149] = 1;
		LOBBY_CONFIGS_802[3150] = 1;
		LOBBY_CONFIGS_802[3170] = -1;
		LOBBY_CONFIGS_802[3173] = 2;
		LOBBY_CONFIGS_802[3184] = 1;
		LOBBY_CONFIGS_802[3185] = 4;
		LOBBY_CONFIGS_802[3202] = 7;
		LOBBY_CONFIGS_802[3226] = -1;
		LOBBY_CONFIGS_802[3252] = -1;
		LOBBY_CONFIGS_802[3253] = -1;
		LOBBY_CONFIGS_802[3254] = -1;
		LOBBY_CONFIGS_802[3255] = -1;
		LOBBY_CONFIGS_802[3256] = -1;
		LOBBY_CONFIGS_802[3257] = -1;
		LOBBY_CONFIGS_802[3258] = -1;
		LOBBY_CONFIGS_802[3261] = -4;
		LOBBY_CONFIGS_802[3267] = 10;
		LOBBY_CONFIGS_802[3277] = 4;
		LOBBY_CONFIGS_802[3285] = 9;
		LOBBY_CONFIGS_802[3354] = -1;
		LOBBY_CONFIGS_802[3472] = -1;
		LOBBY_CONFIGS_802[3473] = -1;
		LOBBY_CONFIGS_802[3480] = 64;
		LOBBY_CONFIGS_802[3506] = -1;
		LOBBY_CONFIGS_802[3514] = 16;
		LOBBY_CONFIGS_802[3538] = -1;
		LOBBY_CONFIGS_802[3550] = -1;
		LOBBY_CONFIGS_802[3573] = -1;
		LOBBY_CONFIGS_802[3574] = -1;
		LOBBY_CONFIGS_802[3580] = 31;
		LOBBY_CONFIGS_802[3603] = 1;
		LOBBY_CONFIGS_802[3608] = 1;
		LOBBY_CONFIGS_802[3680] = 1;
		LOBBY_CONFIGS_802[3691] = 1;
		LOBBY_CONFIGS_802[3695] = -1;
		LOBBY_CONFIGS_802[3696] = -1;
		LOBBY_CONFIGS_802[3697] = -1;
		LOBBY_CONFIGS_802[3698] = -1;
		LOBBY_CONFIGS_802[3699] = -1;
		LOBBY_CONFIGS_802[3709] = 32;
		LOBBY_CONFIGS_802[3857] = -1;
		LOBBY_CONFIGS_802[3858] = -1;
		LOBBY_CONFIGS_802[3864] = -1;
		LOBBY_CONFIGS_802[3865] = -1;
		LOBBY_CONFIGS_802[3866] = -1;
		LOBBY_CONFIGS_802[3867] = -1;
		LOBBY_CONFIGS_802[3883] = 7;
		LOBBY_CONFIGS_802[3890] = -1;
		LOBBY_CONFIGS_802[3906] = -1;
		LOBBY_CONFIGS_802[3924] = -1;
		LOBBY_CONFIGS_802[3928] = -1;
		LOBBY_CONFIGS_802[3936] = 20;
		LOBBY_CONFIGS_802[3986] = -1;
		LOBBY_CONFIGS_802[4056] = 1;
		LOBBY_CONFIGS_802[4060] = -1;
		LOBBY_CONFIGS_802[4070] = 18;
		LOBBY_CONFIGS_802[4072] = 17;
		LOBBY_CONFIGS_802[4074] = 49;
		LOBBY_CONFIGS_802[4076] = 19;
		LOBBY_CONFIGS_802[4078] = 81;
		LOBBY_CONFIGS_802[4080] = 17;
		LOBBY_CONFIGS_802[4082] = 17;
		LOBBY_CONFIGS_802[4084] = 19;
		LOBBY_CONFIGS_802[4086] = 17;
		LOBBY_CONFIGS_802[4088] = 18;
		LOBBY_CONFIGS_802[4090] = 81;
		LOBBY_CONFIGS_802[4092] = 81;
		LOBBY_CONFIGS_802[4094] = 17;
		LOBBY_CONFIGS_802[4096] = 18;
		LOBBY_CONFIGS_802[4098] = 17;
		LOBBY_CONFIGS_802[4100] = 18;
		LOBBY_CONFIGS_802[4102] = 19;
		LOBBY_CONFIGS_802[4104] = 17;
		LOBBY_CONFIGS_802[4106] = 17;
		LOBBY_CONFIGS_802[4108] = 20;
		LOBBY_CONFIGS_802[4110] = 17;
		LOBBY_CONFIGS_802[4112] = 17;
		LOBBY_CONFIGS_802[4114] = 17;
		LOBBY_CONFIGS_802[4116] = 17;
		LOBBY_CONFIGS_802[4120] = 20;
		LOBBY_CONFIGS_802[4122] = 20;
		LOBBY_CONFIGS_802[4124] = 17;
		LOBBY_CONFIGS_802[4126] = 21;
		LOBBY_CONFIGS_802[4128] = 22;
		LOBBY_CONFIGS_802[4129] = -1;
		LOBBY_CONFIGS_802[4131] = -1;
		LOBBY_CONFIGS_802[4133] = -1;
		LOBBY_CONFIGS_802[4135] = -1;
		LOBBY_CONFIGS_802[4137] = -1;
		LOBBY_CONFIGS_802[4160] = -1;
		LOBBY_CONFIGS_802[4161] = 1;
		LOBBY_CONFIGS_802[4165] = 1;
		//LOBBY_CONFIGS_802[1027] = 1;
		//LOBBY_CONFIGS_802[1034] = 2;
		//LOBBY_CONFIGS_802[3905] = 0;
	}
	
	public static final int[] NIS_CONFIG = new int[4000];
	
	static {
		NIS_CONFIG[2989] = -2147344282;
		NIS_CONFIG[2988] = 3084;
		NIS_CONFIG[2991] = -2145025348;
		NIS_CONFIG[2985] = 286597345;
		NIS_CONFIG[2984] = 3072;
		NIS_CONFIG[2987] = -2147176268;
		NIS_CONFIG[2986] = 27521023;
		NIS_CONFIG[2981] = -2146303776;
		NIS_CONFIG[2980] = 4192253;
		NIS_CONFIG[2983] = -2147348360;
		NIS_CONFIG[2982] = 8388607;
		NIS_CONFIG[2977] = -2146115221;
		NIS_CONFIG[2976] = 5603327;
		NIS_CONFIG[2979] = -2146664148;
		NIS_CONFIG[2978] = 2047;
		NIS_CONFIG[2996] = 1929;
		NIS_CONFIG[2992] = 8386557;
		NIS_CONFIG[2993] = -2146848419;
		NIS_CONFIG[2994] = 4179967;
		NIS_CONFIG[2995] = 2360096;
		NIS_CONFIG[2959] = 269820129;
		NIS_CONFIG[2958] = 419426304;
		NIS_CONFIG[2957] = 386839026;
		NIS_CONFIG[2956] = 16777215;
		NIS_CONFIG[2955] = 1102022;
		NIS_CONFIG[2953] = 1638625;
		NIS_CONFIG[2952] = 16777215;
		NIS_CONFIG[2951] = 1638625;
		NIS_CONFIG[2950] = 16777215;
		NIS_CONFIG[2949] = 1638625;
		NIS_CONFIG[2948] = 16777215;
		NIS_CONFIG[2947] = 1638625;
		NIS_CONFIG[2946] = 16777215;
		NIS_CONFIG[2945] = 1638625;
		NIS_CONFIG[2944] = 8384512;
		NIS_CONFIG[2974] = 10175616;
		NIS_CONFIG[2975] = -2147348352;
		NIS_CONFIG[2972] = 279244799;
		NIS_CONFIG[2973] = -2147221254;
		NIS_CONFIG[2970] = 296013823;
		NIS_CONFIG[2971] = 253042913;
		NIS_CONFIG[2968] = 16777215;
		NIS_CONFIG[2969] = -2095767327;
		NIS_CONFIG[2966] = 16777215;
		NIS_CONFIG[2967] = 1638625;
		NIS_CONFIG[2964] = 167772160;
		NIS_CONFIG[2965] = 1638625;
		NIS_CONFIG[2962] = 16777215;
		NIS_CONFIG[2963] = 369098752;
		NIS_CONFIG[2960] = 10809343;
		NIS_CONFIG[2961] = 1638625;
		NIS_CONFIG[3465] = 125;
		NIS_CONFIG[3770] = 16773120;
		NIS_CONFIG[3769] = 168735218;
		NIS_CONFIG[3721] = 100992003;
		NIS_CONFIG[3722] = 65287;
		NIS_CONFIG[2852] = 319951120;
		NIS_CONFIG[2853] = 387323156;
		NIS_CONFIG[2854] = 454695192;
		NIS_CONFIG[2855] = 572588031;
		NIS_CONFIG[2860] = 1023;
		NIS_CONFIG[2862] = -1;
		NIS_CONFIG[2863] = 691470335;
		NIS_CONFIG[2856] = 319951120;
		NIS_CONFIG[2857] = -236;
		NIS_CONFIG[2858] = 23039;
		NIS_CONFIG[2859] = -268435456;
		NIS_CONFIG[2869] = 33646952;
		NIS_CONFIG[2868] = 842019105;
		NIS_CONFIG[2865] = -13162457;
		NIS_CONFIG[2864] = 642008373;
		NIS_CONFIG[2867] = 589579832;
		NIS_CONFIG[2866] = -1;
		NIS_CONFIG[2912] = 32;
		NIS_CONFIG[2913] = -2130706433;
		NIS_CONFIG[2915] = -2130509472;
		NIS_CONFIG[2916] = 33554431;
		NIS_CONFIG[2917] = -2146565919;
		NIS_CONFIG[2918] = 4095;
		NIS_CONFIG[2919] = -2113691330;
		NIS_CONFIG[2920] = 33552306;
		NIS_CONFIG[2921] = -2146520590;
		NIS_CONFIG[2922] = 352317440;
		NIS_CONFIG[2923] = 1384673;
		NIS_CONFIG[2924] = 262459391;
		NIS_CONFIG[2925] = -2130709558;
		NIS_CONFIG[2927] = 318767104;
		NIS_CONFIG[2929] = 335544320;
		NIS_CONFIG[2928] = 352321536;
		NIS_CONFIG[2931] = 353284595;
		NIS_CONFIG[2930] = 369098752;
		NIS_CONFIG[2933] = 1638625;
		NIS_CONFIG[2932] = 402649088;
		NIS_CONFIG[2935] = -2146885110;
		NIS_CONFIG[2934] = 16777215;
		NIS_CONFIG[2937] = -2146041344;
		NIS_CONFIG[2936] = 16773120;
		NIS_CONFIG[2939] = 1638625;
		NIS_CONFIG[2938] = 8386557;
		NIS_CONFIG[2941] = 1638625;
		NIS_CONFIG[2940] = 16777215;
		NIS_CONFIG[2943] = 287005;
		NIS_CONFIG[2942] = 16777215;
	}
}