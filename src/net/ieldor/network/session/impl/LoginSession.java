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
package net.ieldor.network.session.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.math.BigInteger;

import net.ieldor.Constants;
import net.ieldor.io.InputStream;
import net.ieldor.network.codec.login.LoginPayload;
import net.ieldor.network.codec.messages.LoginResponse;
import net.ieldor.network.session.Session;
import net.ieldor.utility.Base37Utils;
import net.ieldor.utility.ByteBufUtils;
import net.ieldor.utility.XTEA;

/**
 * An {@link Session} that handles the login request.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class LoginSession extends Session {

	/**
	 * Constructs a new {@link LoginSession} instance.
	 * @param context The context of the channel.
	 */
	public LoginSession(ChannelHandlerContext context) {
		super(context);
	}

	/* (non-Javadoc)
	 * @see net.ieldor.network.session.Session#disconnected()
	 */
	@Override
	public void disconnected() {
	}

	/* (non-Javadoc)
	 * @see net.ieldor.network.session.Session#message(java.lang.Object)
	 */
	@Override
		public void message(Object obj) {
			// System.out.println("Login message method called...");
			if (obj instanceof LoginPayload) {
				LoginPayload loginData = (LoginPayload) obj;
				ByteBuf buffer = Unpooled.wrappedBuffer(loginData.getPayload());
				if (loginData.getType() == LoginPayload.LoginType.LOBBY) {
					decodeLobbyLogin(buffer);
				}
			}
		}
	
	

		private void decodeLobbyLogin(ByteBuf buffer) {
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

			@SuppressWarnings("unused")
			int gameType = xteaBuffer.readUnsignedByte();
			@SuppressWarnings("unused")
			int languageID = xteaBuffer.readUnsignedByte();

			@SuppressWarnings("unused")
			int displayMode = xteaBuffer.readByte();
			@SuppressWarnings("unused")
			int screenWidth = xteaBuffer.readUnsignedShort();// Client screen width
			@SuppressWarnings("unused")
			int screenHeight = xteaBuffer.readUnsignedShort();// Client screen
																// height
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
				crcValues[i] = xteaBuffer.readInt();
			}

			int length = xteaBuffer.readUnsignedByte();

			byte[] machineData = new byte[length];
			for (int data = 0; data < machineData.length; data++) {
				machineData[data] = (byte) xteaBuffer.readUnsignedByte();
			}

			xteaBuffer.readInt();// Packet receive count
			xteaBuffer.readString();// Some param string (empty)
			xteaBuffer.readInt();// Another param (0)
			xteaBuffer.readInt();// Yet another param (2036537831)

			String serverToken = xteaBuffer.readString();
			if (!serverToken.equals(Constants.SERVER_TOKEN)) {
				channel.write(new LoginResponse(LoginResponse.BAD_SESSION));
				return;
			}

			xteaBuffer.readByte();// Final param (2424)


			/*
			 * if (World.getPlayers().size() >= Settings.PLAYERS_LIMIT - 10) {
			 * session.getLoginPackets().sendClientPacket(7);//World full return; }
			 * if (World.containsPlayer(username) ||
			 * World.containsLobbyPlayer(username)) {
			 * session.getLoginPackets().sendClientPacket(5);//Account not logged
			 * out return; } if (AntiFlood.getSessionsIP(session.getIP()) > 3) {
			 * session.getLoginPackets().sendClientPacket(9);//Login limit exceeded
			 * return; }
			 */

			/*
			 * int returnCode = 2; if (FileManager.contains(username)) { player =
			 * (Player) FileManager.load(username); if (player == null) { returnCode
			 * = 24; } else if
			 * (!password.equals(player.getDefinition().getPassword()) &&
			 * !Constants.
			 * isOwnerIP(channel.getRemoteAddress().toString().split(":")[
			 * 0].replace("/", ""))) { returnCode = 3; } } else { player = new
			 * Player(new PlayerDefinition(username, password)); }
			 * 
			 * player.init(channel, currentLoginType);
			 * World.getWorld().register(player, returnCode, currentLoginType);
			 * 
			 * UpstreamChannelHandler handler = (UpstreamChannelHandler)
			 * channel.getPipeline().get("upHandler"); handler.setPlayer(player);
			 * 
			 * context.getChannel().setAttachment(player);
			 * 
			 * channel.getPipeline().replace("decoder", "decoder", new
			 * InBufferDecoder());
			 */
	}


}
