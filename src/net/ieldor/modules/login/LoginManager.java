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
import net.ieldor.modules.worldlist.World;
import net.ieldor.modules.worldlist.WorldList;
import net.ieldor.network.codec.login.LoginConfigData;
import net.ieldor.network.codec.messages.GameLoginData;
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
	
	public Player runGameLogin (ByteBuf buffer, Channel channel, ChannelHandlerContext context) {
		boolean unknownEquals14 = buffer.readUnsignedByte() == 1;
		int secureBufferSize = buffer.readShort() & 0xFFFF;
		if (buffer.readableBytes() < secureBufferSize) {
			channel.write(new LoginResponse(LoginResponse.BAD_SESSION));
			//session.getLoginPackets().sendClientPacket(10);
			return null;
		}
		byte[] secureBytes = new byte[secureBufferSize];
		buffer.readBytes(secureBytes);

		ByteBuf secureBuffer = Unpooled.wrappedBuffer(new BigInteger(secureBytes)
			.modPow(Constants.JS5PrivateKey, Constants.JS5ModulusKey).toByteArray());
		/*byte[] data = new byte[rsaBlockSize];
		buffer.readBytes(data, 0, rsaBlockSize);
		InputStream rsaStream = new InputStream(Utils.cryptRSA(data, Settings.PRIVATE_EXPONENT, Settings.MODULUS));*/
		int blockOpcode = secureBuffer.readUnsignedByte();

		if (blockOpcode != 10) {
			channel.write(new LoginResponse(LoginResponse.BAD_LOGIN_PACKET));
			//session.getLoginPackets().sendClientPacket(10);
			return null;
		}

		int[] xteaKey = new int[4];
		for (int key = 0; key < xteaKey.length; key++) {
			xteaKey[key] = secureBuffer.readInt();
		}

		long vHash = secureBuffer.readLong();
		if (vHash != 0L) {// rsa block check, pass part
			channel.write(new LoginResponse(LoginResponse.BAD_LOGIN_PACKET));
			return null;
		}

		String password = ByteBufUtils.readString(secureBuffer);
		System.out.println("Password: "+password);
		//TODO: Implement password encryption
		//password = Encrypt.encryptSHA1(password);
		//System.out.println("Found password: "+password);
		long[] loginSeeds = new long[2];
		for (int seed = 0; seed < loginSeeds.length; seed++) {
			loginSeeds[seed] = secureBuffer.readLong();
		}

		byte[] xteaBlock = new byte[buffer.readableBytes()];
		System.out.println("Xtea size: "+xteaBlock.length);
		buffer.readBytes(xteaBlock);
		XTEA xtea = new XTEA(xteaKey);
		xtea.decrypt(xteaBlock, 0, xteaBlock.length);

		InputStream xteaBuffer = new InputStream(xteaBlock);
		
		boolean decodeAsString = xteaBuffer.readByte() == 1;
		String username = decodeAsString ? xteaBuffer.readString()
				: Base37Utils.decodeBase37(xteaBuffer.readLong());
		System.out.println("Username: "+username);
		int displayMode = xteaBuffer.readUnsignedByte();
		int screenWidth = xteaBuffer.readUnsignedShort();
		int screenHeight = xteaBuffer.readUnsignedShort();
		int unknown2 = xteaBuffer.readUnsignedByte();
		
		xteaBuffer.skip(24); // 24bytes directly from a file, no idea whats there
		
		String settings = xteaBuffer.readString();
		int affid = xteaBuffer.readInt();
		int indexFiles = xteaBuffer.readByte() & 0xff;

		int[] crcValues = new int[indexFiles];
		int crcCount = xteaBuffer.readUnsignedByte();
		for (int i = 1; i < crcValues.length; i++) {
			crcValues[i] = xteaBuffer.readUnsignedByte();
		}
		
		@SuppressWarnings("unused")
		MachineData data = new MachineData(xteaBuffer);
		
		xteaBuffer.readInt();// Packet receive count
		xteaBuffer.readInt();//Unknown
		xteaBuffer.readInt();//Unknown
		xteaBuffer.readString();// Some param string (empty)
		boolean hasAditionalInformation = xteaBuffer.readUnsignedByte() == 1;
		if (hasAditionalInformation) {
			xteaBuffer.readString(); // aditionalInformation
		}
		boolean hasJagtheora = xteaBuffer.readUnsignedByte() == 1;		
		boolean js = xteaBuffer.readUnsignedByte() == 1;
		boolean hc = xteaBuffer.readUnsignedByte() == 1;
		int unknown4 = xteaBuffer.readByte();
		int unknown5 = xteaBuffer.readInt();
		
		String serverToken = xteaBuffer.readString();
		if (!serverToken.equals(Constants.SERVER_TOKEN)) {
			System.out.println("Expected token: "+Constants.SERVER_TOKEN+", found: "+serverToken);
			channel.write(new LoginResponse(LoginResponse.BAD_SESSION));
			return null;
		}
		boolean unknown7 = xteaBuffer.readUnsignedByte() == 1;
		
		for (int index = 0; index < crcCount; index++) {
			//int crc = CacheManager.STORE.getIndexes()[index] == null ? -1011863738 : CacheManager.STORE
			//		.getIndexes()[index].getCRC();
			int receivedCRC = xteaBuffer.readInt();
			/*if (crc != receivedCRC && index < 32) {
				Logger.log(this,
				 "Invalid CRC at index: "+index+", "+receivedCRC+", "+crc);
				session.getLoginPackets().sendClientPacket(6);
				return;
			}*/
		}		
		//TODO: Implement the following checks
		/*if (Utils.invalidAccountName(username)) {
			session.getLoginPackets().sendClientPacket(3);
			return;
		}
		if (World.getPlayers().size() >= Settings.PLAYERS_LIMIT - 10) {
			session.getLoginPackets().sendClientPacket(7);
			return;
		}
		if (World.containsPlayer(username)) {
			session.getLoginPackets().sendClientPacket(5);
			return;
		}
		if (AntiFlood.getSessionsIP(session.getIP()) > 3) {
			session.getLoginPackets().sendClientPacket(9);
			return;
		}*/
		LoadResult result = null;
		try {
			result = BinaryPlayerManager.loadPlayer(new LoginHandshakeMessage(username, password, context));
		} catch (IOException e) {
			channel.write(new LoginResponse(LoginResponse.ERROR_PROFILE_LOAD));
			return null;
		}
		if (result.getReturnCode() != LoginResponse.SUCCESS) {
			channel.write(new LoginResponse(result.getReturnCode()));
			return null;
		}
		Player player = result.getPlayer();
		player.initDisplayName();
		channel.write(new LoginConfigData(Constants.NIS_CONFIG, true));
		return player;
		/*player.init(session, username, displayMode, screenWidth, screenHeight, mInformation, new IsaacKeyPair(isaacKeys));
		session.getLoginPackets().sendLoginDetails(player);
		session.setDecoder(3, player);
		session.setEncoder(2, player);*/
		//player.start();
		/*Player player;
		if (!SerializableFilesManager.containsPlayer(username)) 
			player = new Player(password);
		else {
			player = SerializableFilesManager.loadPlayer(username);
			if (player == null) {
				session.getLoginPackets().sendClientPacket(20);
				return;
			}
			if (!SerializableFilesManager.createBackup(username)) {
				session.getLoginPackets().sendClientPacket(20);
				return;
			}
			if (!password.equals(player.getPassword())) {
				session.getLoginPackets().sendClientPacket(3);
				return;
			}
		}
		if (player.isPermBanned() || player.getBanned() > Utils.currentTimeMillis()) {
			session.getLoginPackets().sendClientPacket(4);
			return;
		}*/
	}
	
	public void sendPlayerData (Player player, Channel channel, ChannelHandlerContext context) {
		int rights = 0;
		String displayName = player.getDisplayName();
		int playerIndex = 20;
		boolean isMember = true;
		channel.write(new GameLoginData(rights, displayName, playerIndex, isMember));
		player.gameLogin(2, context);
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
