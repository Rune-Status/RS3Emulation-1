package net.ieldor.network.packet;

import net.ieldor.network.packet.decoder.ActionButtonDecoder;
import net.ieldor.network.packet.decoder.ChatDecoder;
import net.ieldor.network.packet.decoder.CommandDecoder;
import net.ieldor.network.packet.decoder.EquipItemDecoder;
import net.ieldor.network.packet.decoder.MovementDecoder;
import net.ieldor.network.packet.decoder.UnequipItemDecoder;
import net.ieldor.network.packet.handlers.ActionButtonHandler;
import net.ieldor.network.packet.handlers.ChatHandler;
import net.ieldor.network.packet.handlers.CommandHandler;
import net.ieldor.network.packet.handlers.EquipItemHandler;
import net.ieldor.network.packet.handlers.MovementHandler;
import net.ieldor.network.packet.handlers.UnequipItemHandler;

/**
 * @author Sean
 */
public final class PacketCodec {

	/**
	 * The maximum packets.
	 */
	private static final int MAX_PACKETS = 256;

	/**
	 * An array of packet decoders.
	 */
	private final PacketAssembler[] decoders = new PacketAssembler[MAX_PACKETS];

	/**
	 * Creates the PacketCodec
	 */
	public PacketCodec() {
		try {
			register(new int[] { 155, 10 }, new ActionButtonDecoder(), new ActionButtonHandler());
			register(new int[] { 215, 39, 77 }, new MovementDecoder(), new MovementHandler());
			register(new int[] { 237 }, new ChatDecoder(), new ChatHandler());
			register(new int[] { 55 }, new EquipItemDecoder(), new EquipItemHandler());
			register(new int[] { 81 }, new UnequipItemDecoder(), new UnequipItemHandler());
			register(new int[] { 44 }, new CommandDecoder(), new CommandHandler());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Gets a packet decoder.
	 * @param opcode The packet opcode.
	 * @return The packet decoder.
	 */
	public PacketAssembler get(int opcode) {
		return decoders[opcode];
	}

	/**
	 * Registers a decoder.
	 * @param opcodes The opcode of the decoder.
	 * @param decoder The packet decoder to register.
	 * @param handler The packet handler.
	 */
	public void register(int[] opcodes, PacketDecoder<?> decoder, PacketHandler<?> handler) {
		for(int opcode : opcodes)
			decoders[opcode] = new PacketAssembler(decoder, handler);
	}
	
	
	/**
	 * Registers a decoder.
	 * @param opcode The opcode of the decoder.
	 * @param handler The packet handler
	 */
	public void register(int[] opcodes, PacketHandler<?> handler) {
		for(int opcode : opcodes)
			decoders[opcode] = new PacketAssembler(handler);
	}
}
