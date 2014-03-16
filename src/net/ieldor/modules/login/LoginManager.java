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
package net.ieldor.modules.login;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.math.BigInteger;

import net.ieldor.Constants;
import net.ieldor.game.model.player.LoadResult;
import net.ieldor.game.model.player.Player;
import net.ieldor.io.InputStream;
import net.ieldor.io.Packet;
import net.ieldor.modules.worldlist.World;
import net.ieldor.modules.worldlist.WorldList;
import net.ieldor.network.codec.messages.LobbyLoginData;
import net.ieldor.network.codec.messages.LoginHandshakeMessage;
import net.ieldor.network.codec.messages.LoginResponse;
import net.ieldor.utility.Base37Utils;
import net.ieldor.utility.ByteBufUtils;
import net.ieldor.utility.XTEA;

/**
 * RS3Emulator
 * LoginManager.java
 * 15/03/2014
 * @author Sundays211
 */
public class LoginManager {
	
	public NameManager nameManager = new NameManager();

	public LoginManager () {
		
	}
	
	public void init () throws IOException {
		nameManager.init();
	}
	
	public void runLobbyLogin (ByteBuf buffer, Channel channel, ChannelHandlerContext context) {
		int secureBufferSize = buffer.readShort() & 0xFFFF;
		if (buffer.readableBytes() < secureBufferSize) {
			channel.write(new LoginResponse(LoginResponse.BAD_LOGIN_PACKET));
			return;
		}

		byte[] secureBytes = new byte[secureBufferSize];
		buffer.readBytes(secureBytes);

		ByteBuf secureBuffer = Unpooled.wrappedBuffer(new BigInteger(
				secureBytes).modPow(Constants.JS5PrivateKey,
				Constants.JS5ModulusKey).toByteArray());
		int blockOpcode = secureBuffer.readUnsignedByte();

		if (blockOpcode != 10) {
			channel.write(new LoginResponse(LoginResponse.BAD_LOGIN_PACKET));
			return;
		}

		int[] xteaKey = new int[4];
		for (int key = 0; key < xteaKey.length; key++) {
			xteaKey[key] = secureBuffer.readInt();
		}

		long vHash = secureBuffer.readLong();
		if (vHash != 0L) {
			channel.write(new LoginResponse(LoginResponse.BAD_LOGIN_PACKET));
			return;
		}

		String password = ByteBufUtils.readString(secureBuffer);
		//System.out.println("Found password: "+password);
		long[] loginSeeds = new long[2];
		for (int seed = 0; seed < loginSeeds.length; seed++) {
			loginSeeds[seed] = secureBuffer.readLong();
		}

		byte[] xteaBlock = new byte[buffer.readableBytes()];
		buffer.readBytes(xteaBlock);
		XTEA xtea = new XTEA(xteaKey);
		xtea.decrypt(xteaBlock, 0, xteaBlock.length);

		InputStream xteaBuffer = new InputStream(xteaBlock);

		boolean decodeAsString = xteaBuffer.readByte() == 1;
		String username = decodeAsString ? xteaBuffer.readString()
				: Base37Utils.decodeBase37(xteaBuffer.readLong());
		//System.out.println("Found username: "+username);
		@SuppressWarnings("unused")
		int gameType = xteaBuffer.readUnsignedByte();
		@SuppressWarnings("unused")
		int languageID = xteaBuffer.readUnsignedByte();

		@SuppressWarnings("unused")
		int displayMode = xteaBuffer.readByte();
		@SuppressWarnings("unused")
		int screenWidth = xteaBuffer.readUnsignedShort();// Client screen width
		@SuppressWarnings("unused")
		int screenHeight = xteaBuffer.readUnsignedShort();// Client screen height
		@SuppressWarnings("unused")
		int anUnknownByte = xteaBuffer.readByte();

		byte[] randomData = new byte[24];
		for (int i = 0; i < randomData.length; i++) {
			randomData[i] = (byte) (xteaBuffer.readByte() & 0xFF);
		}

		@SuppressWarnings("unused")
		String clientSettings = xteaBuffer.readString();

		int indexFiles = xteaBuffer.readByte() & 0xff;

		int[] crcValues = new int[indexFiles];

		for (int i = 0; i < crcValues.length; i++) {
			crcValues[i] = xteaBuffer.readUnsignedByte();
		}
		@SuppressWarnings("unused")
		MachineData data = new MachineData(xteaBuffer);
		/*int length = xteaBuffer.readUnsignedByte();byte[] machineData = new byte[length];
		for (int data = 0; data < machineData.length; data++) {
			machineData[data] = (byte) xteaBuffer.readUnsignedByte();
		}*/

		xteaBuffer.readInt();// Packet receive count
		xteaBuffer.readString();// Some param string (empty)
		xteaBuffer.readInt();// Another param (0)
		xteaBuffer.readInt();// Yet another param (2036537831)

		String serverToken = xteaBuffer.readString();
		if (!serverToken.equals(Constants.SERVER_TOKEN)) {
			System.out.println("Expected token: "+Constants.SERVER_TOKEN+", found: "+serverToken);
			channel.write(new LoginResponse(LoginResponse.BAD_SESSION));
			return;
		}

		xteaBuffer.readByte();// Final param (2424)

		LoadResult result = null;
		try {
			result = BinaryPlayerManager.loadPlayer(new LoginHandshakeMessage(username, password, context));
		} catch (IOException e) {
			channel.write(new LoginResponse(LoginResponse.ERROR_PROFILE_LOAD));
			return;
		}
		if (result.getReturnCode() != LoginResponse.SUCCESS) {
			channel.write(new LoginResponse(result.getReturnCode()));
			return;
		}
		Player player = result.getPlayer();
		player.initDisplayName();
		int rights = 0;
		long memberEndDate = 1420073999999L;
		int memberFlags = 0x1;//0x1 - if members, 0x2 - subscription
		int lastLoginDay = 0;
		int recoverySetDay = 4000;//The day on which recovery questions were last set
		int msgCount = 0;
		String loginIp = null;
		int emailStatus = 3;//email status (0 - no email, 1 - pending parental confirmation, 2 - pending confirmation, 3 - registered)
		String displayName = player.getDisplayName();		
		World defaultWorld = WorldList.DEFAULT_WORLD;
		
		LobbyLoginData lobbyData = new LobbyLoginData(rights, memberEndDate, memberFlags, lastLoginDay,
				recoverySetDay, msgCount, loginIp, emailStatus, displayName, defaultWorld);
		
		channel.write(lobbyData);
		player.lobbyLogin(context);
		/*Packet response = encodeLobbyResponse(player);
		channel.write(new LoginResponse(LoginResponse.SUCCESS, response.getPayload(), response.getLength()));
		*/
	}
}
