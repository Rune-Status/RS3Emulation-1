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
package net.ieldor.modules.login;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.ieldor.game.model.Position;
import net.ieldor.game.model.player.LoadResult;
import net.ieldor.game.model.player.Player;
import net.ieldor.network.codec.messages.LoginHandshakeMessage;
import net.ieldor.network.codec.messages.LoginResponse;
import net.ieldor.utility.Base37Utils;

/**
 * An class used to save a players account information in binary format.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class BinaryPlayerManager {
	
	/**
	 * The player has an invalid password.
	 */
    //public static final byte INVALID_PASSWORD = 3;
    
    /**
     * The player is already online.
     */
    //public static final byte ALREADY_ONLINE = 5;

    /**
     * The player has a successful login.
     */
	//private static final int STATUS_OKAY = 2;

	/**
	 * The default spawn position.
	 */
	private static final Position SPAWN_POSITION = new Position(3200, 3200, 0);
	
	/**
	 * The current player file version. This should be incremented if new fields are added to the player data file
	 */
	private static final int PLAYER_FILE_VERSION = 1;

	/**
	 * Loads a player.
	 * 
	 * @param message The message containing the details to load.
	 * @return The {@code LoadResult} or {@code Null}.
	 * @throws IOException An IO error has occured.
	 */
	public static LoadResult loadPlayer(LoginHandshakeMessage message) throws IOException {
		File file = BinaryPlayerUtil.getFile(message.getUsername());
		if(!file.exists()) {
			return new LoadResult(new Player(message.getContext().channel(), message.getUsername(), message.getPassword(), SPAWN_POSITION), LoginResponse.SUCCESS);
		}
		
		DataInputStream inputStream = new DataInputStream(new FileInputStream(file));
		
		int version = inputStream.readShort();
		
		String username = StreamUtil.readString(inputStream);
		String password = StreamUtil.readString(inputStream);
		
		if(!username.equalsIgnoreCase(message.getUsername()) || !password.equalsIgnoreCase(message.getPassword())) {
			return new LoadResult(null, LoginResponse.INVALID_UN_PWD);
		}
		
		int x = inputStream.readUnsignedShort();
		int y = inputStream.readUnsignedShort();
		int height = inputStream.readUnsignedShort();
		
		Player player = new Player(message.getContext().channel(), message.getUsername(), message.getPassword(), new Position(x, y, height));
		
		int gender = inputStream.readByte();
		
		int[] style = new int[7];
		for(int i = 0; i < style.length; i++) 
			style[i] = inputStream.readByte();
		
		int[] colour = new int[5];
		for(int i = 0; i < colour.length; i++) {
			colour[i] = inputStream.readByte();
		}		
		
		player.getFriendManager().deserialise(inputStream, version);//Deserialises the friends/ignores for the player
		
		player.getAppearance().setGender(gender);
		player.getAppearance().setLooks(style);
		player.getAppearance().setColours(colour);
		
		return new LoadResult(player, LoginResponse.SUCCESS);
	}

	/**
	 * Saves an player's data.
	 * 
	 * @param player The player to save.
	 * @throws IOException An IO error has occured.
	 */
	public static void savePlayer(Player player) throws IOException {
		File file = BinaryPlayerUtil.getFile(player.getUsername());
		DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(file));
		
		outputStream.writeShort(PLAYER_FILE_VERSION);
		
		StreamUtil.writeString(outputStream, player.getUsername());
		StreamUtil.writeString(outputStream, player.getPassword());
		
		Position position = player.getPosition();
		outputStream.writeShort(position.getX());
		outputStream.writeShort(position.getY());
		outputStream.writeShort(position.getHeight());
		
		outputStream.writeByte(player.getAppearance().getGender());
		
		int[] style = player.getAppearance().getLooks();
		for(int i = 0; i < style.length; i++)
			outputStream.writeByte(style[i]);
		
		int[] colour = player.getAppearance().getColours();
		for(int i = 0; i < colour.length; i++) {
			outputStream.writeByte(colour[i]);
		}
		
		player.getFriendManager().serialise(outputStream);//Saves the friends/ignores for the player
		
		outputStream.flush();
	}
	
	/**
	 * A utility class with common functionality used by the binary player loader/
	 * savers.
	 * @author Graham
	 */
	public final static class BinaryPlayerUtil {

		/**
		 * The saved games directory.
		 */
		private static final File SAVED_GAMES_DIRECTORY = new File("data/savedGames");

		/**
		 * Creates the saved games directory if it does not exist.
		 */
		static {
			if (!SAVED_GAMES_DIRECTORY.exists()) {
				SAVED_GAMES_DIRECTORY.mkdir();
			}
		}

		/**
		 * Gets the file for the specified player.
		 * @param name The name of the player.
		 * @return The file.
		 */
		public static File getFile(String name) {
			name = Base37Utils.decodeBase37(Base37Utils.encodeBase37(name));
			return new File(SAVED_GAMES_DIRECTORY, name + ".dat");
		}

		/**
		 * Default private constructor to prevent instantiation.
		 */
		private BinaryPlayerUtil() {

		}
	}
	
	/**
	 * A class which contains {@link InputStream}- and {@link OutputStream}-related
	 * utility methods.
	 * @author Graham
	 */
	public static final class StreamUtil {

		/**
		 * Writes a string to the specified output stream.
		 * @param os The output stream.
		 * @param str The string.
		 * @throws IOException if an I/O error occurs.
		 */
		public static void writeString(OutputStream os, String str) throws IOException {
			for (char c : str.toCharArray()) {
				os.write(c);
			}
			os.write('\0');
		}

		/**
		 * Reads a string from the specified input stream.
		 * @param is The input stream.
		 * @return The string.
		 * @throws IOException if an I/O error occurs.
		 */
		public static String readString(InputStream is) throws IOException {
			StringBuilder builder = new StringBuilder();
			int character;
			while ((character = is.read()) != -1 && character != '\0') {
				builder.append((char) character);
			}
			return builder.toString();
		}

		/**
		 * Default private constructor to prevent instantiation.
		 */
		private StreamUtil() {

		}
	}
}
