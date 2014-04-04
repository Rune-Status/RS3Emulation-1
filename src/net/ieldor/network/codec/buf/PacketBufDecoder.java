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
package net.ieldor.network.codec.buf;

import java.io.IOException;

import net.ieldor.io.Packet;
import net.ieldor.io.Packet.PacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * An {@link ByteToMessageDecoder} that is used to decode incoming packets from the
 * client.
 * 
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public class PacketBufDecoder extends ByteToMessageDecoder<Packet> {
	
	private static final int[] SIZES_802 = new int[120];
	static {//TODO Identify packets
		SIZES_802[0] = 3;
		SIZES_802[1] = 7;
		SIZES_802[2] = 6;
		SIZES_802[3] = 0;
		SIZES_802[4] = -1;
		SIZES_802[5] = 3;
		SIZES_802[6] = -1;
		SIZES_802[7] = -2;
		SIZES_802[8] = 0;
		SIZES_802[9] = 3;
		SIZES_802[10] = -2;
		SIZES_802[11] = 7;
		SIZES_802[12] = -1;
		SIZES_802[13] = 3;
		SIZES_802[14] = 8;
		SIZES_802[15] = 7;
		SIZES_802[16] = 8;
		SIZES_802[17] = -1;
		SIZES_802[18] = 9;
		SIZES_802[19] = 4;
		SIZES_802[20] = -1;
		SIZES_802[21] = 8;
		SIZES_802[22] = -1;
		SIZES_802[23] = 8;
		SIZES_802[24] = 0;
		SIZES_802[25] = 7;
		SIZES_802[26] = 9;
		SIZES_802[27] = 1;
		SIZES_802[28] = 8;
		SIZES_802[29] = 6;
		SIZES_802[30] = 3;
		SIZES_802[31] = -2;
		SIZES_802[32] = 3;
		SIZES_802[33] = 15;
		SIZES_802[34] = 1;
		SIZES_802[35] = -1;
		SIZES_802[36] = 18;
		SIZES_802[37] = -1;
		SIZES_802[38] = 9;
		SIZES_802[39] = 4;
		SIZES_802[40] = 9;
		SIZES_802[41] = -1;
		SIZES_802[42] = -1;
		SIZES_802[43] = -1;
		SIZES_802[44] = 9;
		SIZES_802[45] = 8;
		SIZES_802[46] = -1;
		SIZES_802[47] = -2;
		SIZES_802[48] = 9;
		SIZES_802[49] = 7;
		SIZES_802[50] = 15;
		SIZES_802[51] = 3;
		SIZES_802[52] = 3;
		SIZES_802[53] = -1;
		SIZES_802[54] = 8;
		SIZES_802[55] = 11;
		SIZES_802[56] = 7;
		SIZES_802[57] = -1;
		SIZES_802[58] = -1;
		SIZES_802[59] = -2;
		SIZES_802[60] = 4;
		SIZES_802[61] = 3;
		SIZES_802[62] = -1;
		SIZES_802[63] = 12;
		SIZES_802[64] = 4;
		SIZES_802[65] = 4;
		SIZES_802[66] = 7;
		SIZES_802[67] = -1;
		SIZES_802[68] = 3;
		SIZES_802[69] = 11;
		SIZES_802[70] = 2;
		SIZES_802[71] = -1;
		SIZES_802[72] = 3;
		SIZES_802[73] = -1;
		SIZES_802[74] = 4;
		SIZES_802[75] = -1;
		SIZES_802[76] = 9;
		SIZES_802[77] = -1;
		SIZES_802[78] = 16;
		SIZES_802[79] = 3;
		SIZES_802[80] = -1;
		SIZES_802[81] = 3;
		SIZES_802[82] = -2;
		SIZES_802[83] = 3;
		SIZES_802[84] = 3;
		SIZES_802[85] = 4;
		SIZES_802[86] = 1;
		SIZES_802[87] = 8;
		SIZES_802[88] = -1;
		SIZES_802[89] = 5;
		SIZES_802[90] = 1;
		SIZES_802[91] = 0;
		SIZES_802[92] = -2;
		SIZES_802[93] = 17;
		SIZES_802[94] = 3;
		SIZES_802[95] = -2;
		SIZES_802[96] = -2;
		SIZES_802[97] = -1;
		SIZES_802[98] = -2;
		SIZES_802[99] = -1;
		SIZES_802[100] = 2;
		SIZES_802[101] = 3;
		SIZES_802[102] = 18;
		SIZES_802[103] = 2;
		SIZES_802[104] = 1;
		SIZES_802[105] = 6;
		SIZES_802[106] = -1;
		SIZES_802[107] = 8;
		SIZES_802[108] = 0;
		SIZES_802[109] = -1;
		SIZES_802[110] = 4;
		SIZES_802[111] = 4;
		SIZES_802[112] = 16;
		SIZES_802[113] = -1;
		SIZES_802[114] = 8;
		SIZES_802[115] = 0;
		SIZES_802[116] = 4;
		SIZES_802[117] = 1;
		SIZES_802[118] = 2;
		SIZES_802[119] = 9;
	}

	private enum State {
		READ_OPCODE, READ_SIZE, READ_PAYLOAD
	}

	private State state = State.READ_OPCODE;
	private boolean variable;
	private int opcode, size;


	@Override
	public Packet decode(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
		if (state == State.READ_OPCODE) {
			if (!buf.readable())
				return null;

			opcode = (buf.readUnsignedByte()) & 0xFF;
			if (opcode > SIZES_802.length || opcode < 0) {
				System.out.println("Invalid opcode received: "+opcode);
				return null;
			}
			size = SIZES_802[opcode];

			if (size == -3) {
				System.out.println("Invalid packet size for: "+opcode);
				return null;
				//throw new IOException("Illegal opcode " + opcode + ".");
			}

			variable = size == -1;
			state = variable ? State.READ_SIZE : State.READ_PAYLOAD;
		}

		if (state == State.READ_SIZE) {
			if (!buf.readable()) {
				return null;
			}

			size = buf.readUnsignedByte();
			state = State.READ_PAYLOAD;
		}

		if (state == State.READ_PAYLOAD) {
			if (buf.readableBytes() < size)
				return null;

			ByteBuf payload = buf.readBytes(size);
			state = State.READ_OPCODE;
			
			//System.out.println("Received packet: opcode="+opcode+", size="+size);

			return new Packet(opcode, variable ? PacketType.BYTE : PacketType.FIXED, payload);
		}

		throw new IllegalStateException();
	}

}