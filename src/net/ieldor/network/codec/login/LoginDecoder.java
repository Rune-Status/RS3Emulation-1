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
package net.ieldor.network.codec.login;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.math.BigInteger;
import java.net.ProtocolException;
import java.security.SecureRandom;

import net.ieldor.Constants;
import net.ieldor.network.codec.login.LoginPayload.LoginType;
import net.ieldor.network.codec.messages.LoginResponse;
import net.ieldor.utility.ByteBufUtils;

/**
 * An {@link ByteToMessageDecoder} that handles the login procedure.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class LoginDecoder extends ByteToMessageDecoder<Object> {

	/**
	 * An enumeration used for storing the possible states of login.
	 */
	public enum LoginState {
		DECODE_HEADER, CONNECTION_TYPE, CLIENT_DETAILS, LOBBY_PAYLOAD
	};

	/**
	 * The default login state.
	 */
	private LoginState state = LoginState.CONNECTION_TYPE;

	private int loginSize;
	private LoginType currentLoginType;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rse.network.StatedByteToMessageDecoder#decode(io.netty.channel.
	 * ChannelHandlerContext, io.netty.buffer.ByteBuf)
	 */
	@Override
	public Object decode(ChannelHandlerContext ctx, ByteBuf buf)
			throws Exception {
		// System.out.println("Received login request...");
		switch (getState()) {
		case DECODE_HEADER:
			return decodeHeader(ctx, buf);
		case CONNECTION_TYPE:
			return decodeConnectionType(buf);
			// return decodePayload(ctx, buf);
		case CLIENT_DETAILS:
			return decodeClientDetails(buf);
		case LOBBY_PAYLOAD:
			decodeLobbyPayload(ctx, buf);
			break;
		}
		return null;
	}

	/**
	 * Decodes the header of login.
	 * 
	 * @param ctx
	 *            The channel handler context.
	 * @param buf
	 *            The byte buf for writing data.
	 * @return The login message, or {@code Null}.
	 */
	private Object decodeHeader(ChannelHandlerContext ctx, ByteBuf buf) {
		if (buf.readable()) {

			new SecureRandom().nextInt();

			ByteBuf unpooled = Unpooled.buffer();
			unpooled.writeByte(0);
			// unpooled.writeLong(secureKey);
			ctx.channel().write(unpooled);

			setState(LoginState.CONNECTION_TYPE);
		}
		return null;
	}

	/**
	 * Decodes the payload of login.
	 * 
	 * @param ctx
	 *            The channel handler context.
	 * @param buf
	 *            The byte buf for writing data.
	 * @return The login message, or {@code Null}.
	 */
	/*
	 * private Object decodePayload(ChannelHandlerContext ctx, ByteBuf buf) {
	 * if(buf.readable()) {
	 * 
	 * int loginType = buf.readByte(); System.out.println("Login Type: " +
	 * loginType); } return null; }
	 */

	private Object decodeLobbyPayload(ChannelHandlerContext context,
			ByteBuf buffer) throws ProtocolException {
		int secureBufferSize = buffer.readShort() & 0xFFFF;
		if (buffer.readableBytes() < secureBufferSize) {
			throw new ProtocolException("Invalid secure buffer size.");
		}

		byte[] secureBytes = new byte[secureBufferSize];
		buffer.readBytes(secureBytes);

		ByteBuf secureBuffer = Unpooled.wrappedBuffer(new BigInteger(
				secureBytes).modPow(Constants.JS5PrivateKey,
				Constants.JS5ModulusKey).toByteArray());
		int blockOpcode = secureBuffer.readUnsignedByte();

		if (blockOpcode != 10) {
			throw new ProtocolException("Invalid block opcode.");
		}

		int[] xteaKey = new int[4];
		for (int key = 0; key < xteaKey.length; key++) {
			xteaKey[key] = secureBuffer.readInt();
		}

		long vHash = secureBuffer.readLong();
		if (vHash != 0L) {
			throw new ProtocolException("Invalid login virtual hash.");
		}

		ByteBufUtils.readString(secureBuffer);

		long[] loginSeeds = new long[2];
		for (int seed = 0; seed < loginSeeds.length; seed++) {
			loginSeeds[seed] = secureBuffer.readLong();
		}

		byte[] xteaBlock = new byte[buffer.readableBytes()];
		buffer.readBytes(xteaBlock);
		return null;
		// return new LoginPayload(password, xteaKey, xteaBlock);
	}

	private Object decodeClientDetails(ByteBuf buffer) throws ProtocolException {
		if (buffer.readableBytes() < loginSize) {	
			return null;
			//throw new ProtocolException("Invalid login size. Expected: "+loginSize+", found="+buffer.readableBytes());
		}

		int version = buffer.readInt();
		int subVersion = buffer.readInt();

		if (version != Constants.ServerRevision
				&& subVersion != Constants.ServerSubRevision) {
			return new LoginResponse(LoginResponse.GAME_UPDATED);
			// throw new
			// ProtocolException("Invalid client version/sub-version.");
		}

		/*
		 * if (currentLoginType.equals(LoginTypes.GAME)) { buffer.readByte(); }
		 */

		byte[] payload = new byte[loginSize - 8];
		buffer.readBytes(payload);
		return new LoginPayload(currentLoginType, payload);
		// state = currentLoginType.equals(LoginTypes.LOBBY) ?
		// LoginState.LOBBY_PAYLOAD : LoginState.GAME_PAYLOAD;
		// return null;
	}

	private Object decodeConnectionType(ByteBuf buffer) throws ProtocolException {
		int loginType = buffer.readUnsignedByte();
		if (loginType != 16 && loginType != 18 && loginType != 19) {
			System.out.println("Invalid login opcode: " + loginType);
			return new LoginResponse(LoginResponse.BAD_LOGIN_PACKET);
			// throw new ProtocolException("Invalid login opcode: " + loginType);
		}

		currentLoginType = loginType == 19 ? LoginType.LOBBY : LoginType.GAME;
		loginSize = buffer.readShort() & 0xFFFF;

		state = LoginState.CLIENT_DETAILS;
		return decodeClientDetails(buffer);
	}

	/**
	 * Gets the state.
	 * 
	 * @return the state
	 */
	public LoginState getState() {
		return state;
	}

	/**
	 * Sets the state.
	 * 
	 * @param state
	 *            the state to set
	 */
	public void setState(LoginState state) {
		this.state = state;
	}
}
