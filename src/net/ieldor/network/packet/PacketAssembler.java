package net.ieldor.network.packet;

/**
 * @author Sean
 */
public final class PacketAssembler {

	/**
	 * The packet decoder.
	 */
	private final PacketDecoder<?> decoder;
	
	/**
	 * The packet handler
	 */
	private final PacketHandler<?> handler;
	
	/**
	 * Creates a new GamePacketAssembler.
	 * @param decoder The packet decoder.
	 * @param handler The packet handler.
	 */
	public PacketAssembler(PacketDecoder<?> decoder, PacketHandler<?>  handler) {
		this.decoder = decoder;
		this.handler = handler;
	}

	/**
	 * Creates a new PacketAssembler with no decoder.
	 * @param handler The packet handler.
	 */
	public PacketAssembler(PacketHandler<?>  handler) {
		this(null, handler);
	}
	
	/**
	 * Creates a new GamePacketAssembler with a chain of game packet handlers.
	 * @param decoder The packet decoder.
	 * @param handlers The packet handlers.
	 */
	@SuppressWarnings("unchecked")
	public PacketAssembler(PacketDecoder<?> decoder, PacketHandler<?>...  handlers) {
		this(decoder, new PacketChain((PacketHandler<PacketContext>[]) handlers));
	}
	
	/**
	 * Gets the decoder.
	 * @return the decoder.
	 */
	public PacketDecoder<?> getDecoder() {
		return decoder;
	}

	/**
	 * Gets the packet handler.
	 * @return the handler.
	 */
	public PacketHandler<?> getHandler() {
		return handler;
	}
}
