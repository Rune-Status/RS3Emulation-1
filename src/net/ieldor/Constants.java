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
public final class Constants {

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

	public static final int ServerRevision = 795;

	
	public static final BigInteger JS5ModulusKey = new BigInteger("92952295964155672087801015402111750591919500795257231765611346743023141259087751200849540075517787992536588533840502270599726488785462782810450546358220539026800123618215080154270389875550255531705255494326594317637443265761822792347000385003452391742987636722632882107837694393096626546031683632300323372031");

	public static final BigInteger JS5PrivateKey = new BigInteger("48844029607909929282080219851655163753063517835529068275083137145982131295000747300835504541261906753238247645275176117230165873030406752131258615980047884895054026459192588976183254088467205779659961501829292823408290106695592149680053016585516891071746766448461496845037459094045320573736639805712121612241");

	public static final int ServerSubRevision = 1;
	
	/**
	 * The server token.
	 */
	public static final String SERVER_TOKEN = "0BlrYT6t63jxgB3JVUyThJPPCLV/VuhP";

	/**
	 * The second server token.
	 */
	public static final String SECOND_SERVER_TOKEN = "wwGlrZHF5gKN6D3mDdihco3oPeYN2KFybL9hUUFqOvk";

	
}
