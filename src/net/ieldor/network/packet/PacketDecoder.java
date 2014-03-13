package net.ieldor.network.packet;

import net.ieldor.io.PacketReader;

/**
 * @author Sean
 * 
 * @param <T>
 */
public interface PacketDecoder<T extends PacketContext> {

	/**
	 * Decodes a certain packet sent from the client.
	 * @param packet The packet to decode.
	 * @return The packet to decode.
	 */
	public T decode(PacketReader packet);
}
