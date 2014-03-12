package com.rs3e.network.protocol.packets.encoder.impl;

import com.rs3e.network.protocol.messages.GameMessage;
import com.rs3e.network.protocol.packets.PacketBuilder;
import com.rs3e.network.protocol.packets.encoder.PacketEncoder;

public class GameMessageEncoder implements PacketEncoder<GameMessage> {

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.psyc.net.codec.encoder.PacketEncoder#encode(java.lang.Object)
	 */
	@Override
	public PacketBuilder buildPacket(GameMessage message) {
		PacketBuilder buffer = new PacketBuilder();
		// XXX:TODO streams.
		buffer.putString(message.getMessage());
		return buffer;
	}

}
