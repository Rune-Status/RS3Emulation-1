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
	
	private static final int[] SIZES = new int[120];
	static {//TODO Identify packets
		SIZES[0] = 0;
        SIZES[1] = -2;
        SIZES[2] = -1;
        SIZES[3] = 3;
        SIZES[4] = 9;
        SIZES[5] = 17;
        SIZES[6] = 3;
        SIZES[7] = 4;
        SIZES[8] = 4;
        SIZES[9] = 7;
        SIZES[10] = 1;
        SIZES[11] = -1;
        SIZES[12] = 15;
        SIZES[13] = 16;
        SIZES[14] = 2;
        SIZES[15] = 8;
        SIZES[16] = -2;
        SIZES[17] = -2;
        SIZES[18] = -1;
        SIZES[19] = -2;
        SIZES[20] = -2;
        SIZES[21] = 9;
        SIZES[22] = 8;
        SIZES[23] = 8;
        SIZES[24] = 7;
        SIZES[25] = 6;
        SIZES[26] = 4;
        SIZES[27] = -1;
        SIZES[28] = 9;
        SIZES[29] = 4;
        SIZES[30] = 15;
        SIZES[31] = -1;
        SIZES[32] = -1;
        SIZES[33] = 6;
        SIZES[34] = 18;
        SIZES[35] = 3;
        SIZES[36] = 2;
        SIZES[37] = 11;
        SIZES[38] = 3;
        SIZES[39] = 4;
        SIZES[40] = 8;
        SIZES[41] = -1;
        SIZES[42] = 1;
        SIZES[43] = 18;
        SIZES[44] = 9;
        SIZES[45] = 3;
        SIZES[46] = -2;
        SIZES[47] = 7;
        SIZES[48] = 3;
        SIZES[49] = 3;
        SIZES[50] = 0;
        SIZES[51] = 2;
        SIZES[52] = 1;
        SIZES[53] = 3;
        SIZES[54] = -1;
        SIZES[55] = 8;
        SIZES[56] = 11;
        SIZES[57] = 3;
        SIZES[58] = 4;
        SIZES[59] = 5;
        SIZES[60] = 4;
        SIZES[61] = 0;
        SIZES[62] = -1;
        SIZES[63] = 1;
        SIZES[64] = -1;
        SIZES[65] = 0;
        SIZES[66] = 2;
        SIZES[67] = 8;
        SIZES[68] = 7;
        SIZES[69] = -1;
        SIZES[70] = 3;
        SIZES[71] = -1;
        SIZES[72] = 12;
        SIZES[73] = -1;
        SIZES[74] = -1;
        SIZES[75] = 7;
        SIZES[76] = -2;
        SIZES[77] = 3;
        SIZES[78] = 9;
        SIZES[79] = -1;
        SIZES[80] = -1;
        SIZES[81] = 16;
        SIZES[82] = 6;
        SIZES[83] = 3;
        SIZES[84] = -1;
        SIZES[85] = 8;
        SIZES[86] = -1;
        SIZES[87] = 3;
        SIZES[88] = 0;
        SIZES[89] = -1;
        SIZES[90] = 8;
        SIZES[91] = 1;
        SIZES[92] = 8;
        SIZES[93] = 7;
        SIZES[94] = 3;
        SIZES[95] = 3;
        SIZES[96] = 7;
        SIZES[97] = -1;
        SIZES[98] = -2;
        SIZES[99] = 3;
        SIZES[100] = 4;
        SIZES[101] = 4;
        SIZES[102] = -1;
        SIZES[103] = -1;
        SIZES[104] = -1;
        SIZES[105] = 9;
        SIZES[106] = 0;
        SIZES[107] = 1;
        SIZES[108] = -2;
        SIZES[109] = -1;
        SIZES[110] = 4;
        SIZES[111] = 9;
        SIZES[112] = -1;
        SIZES[113] = -1;
        SIZES[114] = 8;
        SIZES[115] = 9;
        SIZES[116] = 3;
        SIZES[117] = -1;
        SIZES[118] = -2;
        SIZES[119] = -1;
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
			if (opcode > SIZES.length || opcode < 0) {
				System.out.println("Invalid opcode received: "+opcode);
				return null;
			}
			size = SIZES[opcode];

			if (size == -3) {
				System.out.println("Invalid packet size for: "+opcode);
				return null;
				//throw new IOException("Illegal opcode " + opcode + ".");
			}

			variable = size == -1;
			state = variable ? State.READ_SIZE : State.READ_PAYLOAD;
		}

		if (state == State.READ_SIZE) {
			if (!buf.readable())
				return null;

			size = buf.readUnsignedByte();
			state = State.READ_PAYLOAD;
		}

		if (state == State.READ_PAYLOAD) {
			if (buf.readableBytes() < size)
				return null;

			ByteBuf payload = buf.readBytes(size);
			state = State.READ_OPCODE;

			return new Packet(opcode, variable ? PacketType.BYTE : PacketType.FIXED, payload);
		}

		throw new IllegalStateException();
	}

}