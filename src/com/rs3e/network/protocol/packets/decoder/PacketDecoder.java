package com.rs3e.network.protocol.packets.decoder;

import com.rs3e.network.protocol.handlers.PacketHandler;
import com.rs3e.network.protocol.packets.PacketReader;
import com.rs3e.network.session.Session;

/**
 * @author Taylor Moon
 * @since Jan 23, 2014
 * @param <T>
 *            The handler expected to handle the decoded structure.
 */
public interface PacketDecoder<T extends PacketHandler<? extends Session>> {

	/**
	 * Called when the RS2Network recieves a decodable packet from the client.
	 * 
	 * @param packet
	 *            The packet being decoded.
	 * @param session
	 *            The bound session to this decoder.
	 * @param opcode
	 *            The opcode passed through the decoder.
	 * @return The handler of the recently decoded packet.
	 */
	T decodePacket(PacketReader packet, Session session, int opcode);

	/**
	 * Returns an array of possible opcodes that will activate this decoder.
	 * 
	 * @return The opcodes holding the possibility of activating this decoder.
	 */
	int[] getPossiblePackets();
}
