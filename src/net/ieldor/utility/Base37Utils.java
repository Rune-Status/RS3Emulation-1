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
package net.ieldor.utility;

/**
 * Utils for Base37 encoding/decoding
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class Base37Utils {

	public static String decodeBase37(long value) {
		char[] chars = new char[12];
		int pos = 0;
		while (value != 0) {
			int remainder = (int) (value % 37);
			value /= 37;

			char c;
			if (remainder >= 1 && remainder <= 26)
				c = (char) ('a' + remainder - 1);
			else if (remainder >= 27 && remainder <= 36)
				c = (char) ('0' + remainder - 27);
			else
				c = '_';

			chars[chars.length - pos++ - 1] = c;
		}
		return new String(chars, chars.length - pos, pos);
	}

	public static long encodeBase37(String str) {
		int len = str.length();
		if (len > 12)
			throw new IllegalArgumentException("String too long.");

		long value = 0;
		for (int pos = 0; pos < len; pos++) {
			char c = str.charAt(pos);
			value *= 37;

			if (c >= 'A' && c <= 'Z')
				value += c - 'A' + 1;
			else if (c >= 'a' && c <= 'z')
				value += c - 'a' + 1;
			else if (c >= '0' && c <= '9')
				value += c - '0' + 27;
			else if (c != ' ' && c != '_')
				throw new IllegalArgumentException(
						"Illegal character in string: " + c + ".");
		}

		while (value != 0 && (value % 37) == 0)
			value /= 37;

		return value;
	}
}
