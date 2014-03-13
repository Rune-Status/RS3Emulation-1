package net.ieldor.network.packet;

import java.util.ArrayList;
import java.util.List;

import net.ieldor.game.model.player.Player;

/**
 * @author Sean
 */
public class PacketChain implements PacketHandler<PacketContext> {

	/**
	 * A list of packet handlers to chain.
	 */
	private final List<PacketHandler<PacketContext>> handlerChain = new ArrayList<PacketHandler<PacketContext>>();
	
	/**
	 * Creates a new GamePacketChain.
	 * @param context The game packet context.
	 * 
	 */
	@SafeVarargs
	public PacketChain(PacketHandler<PacketContext>... handlers) {
		for(PacketHandler<PacketContext> handler : handlers) {
			handlerChain.add(handler);
		}
	}

	@Override
	public void handle(Player player, PacketContext context) {
		for(PacketHandler<PacketContext> handler : handlerChain) {
			handler.handle(player, context);
		}
	}
}
