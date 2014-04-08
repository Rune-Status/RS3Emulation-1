package net.ieldor.network.packet;

import net.ieldor.config.IncommingOpcode;
import net.ieldor.network.packet.decoder.ActionButtonDecoder;
import net.ieldor.network.packet.decoder.ChatDecoder;
import net.ieldor.network.packet.decoder.CommandDecoder;
import net.ieldor.network.packet.decoder.EquipItemDecoder;
import net.ieldor.network.packet.decoder.MovementDecoder;
import net.ieldor.network.packet.decoder.UnequipItemDecoder;
import net.ieldor.network.packet.decoder.WorldListDecoder;
import net.ieldor.network.packet.handlers.ActionButtonHandler;
import net.ieldor.network.packet.handlers.ChatHandler;
import net.ieldor.network.packet.handlers.CommandHandler;
import net.ieldor.network.packet.handlers.EquipItemHandler;
import net.ieldor.network.packet.handlers.MovementHandler;
import net.ieldor.network.packet.handlers.UnequipItemHandler;
import net.ieldor.network.packet.handlers.WorldListHandler;

/**
 * @author Sean
 */
public final class PacketCodec {
	
	//TODO Update packet opcodes to 795
	/*public final static int SCREEN_PACKET = 25;//795
	public final static int PING_PACKET = 24;//802
	public final static int WORLD_LIST_UPDATE = -1;//111;//802
	
	public final static int ACTION_BUTTON1_PACKET = -1;
	public final static int ACTION_BUTTON2_PACKET = -1;
	public final static int ACTION_BUTTON3_PACKET = -1;
	public final static int ACTION_BUTTON4_PACKET = -1;
	public final static int ACTION_BUTTON5_PACKET = -1;
	public final static int ACTION_BUTTON6_PACKET = -1;
	public final static int ACTION_BUTTON7_PACKET = -1;
	public final static int ACTION_BUTTON8_PACKET = -1;
	public final static int ACTION_BUTTON9_PACKET = -1;
	public final static int ACTION_BUTTON10_PACKET = -1;
	
	public final static int JOIN_FRIEND_CHAT_PACKET = 71;//795
	public final static int ONLINE_STATUS_PACKET = 83;//795
	public final static int ADD_FRIEND_PACKET = 64;//795
	public final static int REMOVE_FRIEND_PACKET = 112;//795
	public final static int ADD_IGNORE_PACKET = 11;//795
	public final static int REMOVE_IGNORE_PACKET = 80;//795*/

	/**
	 * The maximum packets.
	 */
	private static final int MAX_PACKETS = 120;

	/**
	 * An array of packet decoders.
	 */
	private final PacketAssembler[] decoders = new PacketAssembler[MAX_PACKETS];

	/**
	 * Creates the PacketCodec
	 */
	public PacketCodec() {
		try {
			register(new int[] { 
						IncommingOpcode.ACTION_BUTTON_1_PACKET, IncommingOpcode.ACTION_BUTTON_2_PACKET,
						IncommingOpcode.ACTION_BUTTON_3_PACKET, IncommingOpcode.ACTION_BUTTON_4_PACKET,
						IncommingOpcode.ACTION_BUTTON_5_PACKET, IncommingOpcode.ACTION_BUTTON_6_PACKET,
						IncommingOpcode.ACTION_BUTTON_7_PACKET, IncommingOpcode.ACTION_BUTTON_8_PACKET,
						IncommingOpcode.ACTION_BUTTON_9_PACKET, IncommingOpcode.ACTION_BUTTON_10_PACKET
					}, new ActionButtonDecoder(), new ActionButtonHandler());
			//register(new int[] { 77, 39, 77 }, new MovementDecoder(), new MovementHandler());
			//register(new int[] { 237 }, new ChatDecoder(), new ChatHandler());
			//register(new int[] { 55 }, new EquipItemDecoder(), new EquipItemHandler());
			//register(new int[] { 81 }, new UnequipItemDecoder(), new UnequipItemHandler());
			register(new int[] { IncommingOpcode.WORLD_LIST_UPDATE }, new WorldListDecoder(), new WorldListHandler());
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
