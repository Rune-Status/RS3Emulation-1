package net.ieldor.game.sync.task;

import java.util.Iterator;
import java.util.PriorityQueue;

import net.ieldor.Main;
import net.ieldor.game.model.Direction;
import net.ieldor.game.model.Position;
import net.ieldor.game.model.masks.UpdateMask;
import net.ieldor.game.model.player.Player;
import net.ieldor.io.Packet.PacketType;
import net.ieldor.io.PacketBuf;
import net.ieldor.network.ActionSender;

/**
 * Performs the player updating procedure.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 * 
 */
public final class PlayerSynchronizationTask extends SynchronizationTask {

	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * Creates the {@link PlayerSynchronizationTask} for the specified player.
	 * @param player The player.
	 */
	public PlayerSynchronizationTask(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		long begin = System.nanoTime();
		System.out.println("Sending player updates to: "+player);
		player.getActionSender().sendPlayerUpdates();
		/*if(player.hasScheduledRegionChange()) {
			player.getActionSender().sendMapRegion(false);
		}
		PacketBuf packet = new PacketBuf(ActionSender.PLAYER_UPDATE_PACKET, PacketType.SHORT);
		PacketBuf block = new PacketBuf();
		
		packet.switchToBitAccess();
		updateMyMovement(player, packet);
		if (!player.getUpdateMasks().isEmpty())
			updateMasks(player, block, false);
		packet.putBits(8, player.getLocalPlayers().size());
		
		for(Iterator<Player> it$ = player.getLocalPlayers().iterator(); it$.hasNext();) {
			Player local = it$.next();
			if(!local.getPosition().isWithinDistance(player.getPosition(), Position.MAX_DISTANCE)) {
				packet.putBit(true);
				packet.putBits(2, 3);
				it$.remove();
				continue;
			}
			updateLocalMovement(local, packet);
			if (!local.getUpdateMasks().isEmpty())
				updateMasks(local, block, false);
		}
		
		int addedCount = 0;
		for(Player world : Main.getPlayers()) {
			if(world == null || world.getIndex() == player.getIndex() || addedCount >= 25)
				continue;
			
			if(player.getLocalPlayers().contains(world) || !world.getPosition().isWithinDistance(player.getPosition(), Position.MAX_DISTANCE))
				continue;
			
			addedCount++;
			player.getLocalPlayers().add(world);
			addLocalPlayer(player, world, packet);
			updateMasks(world, block, true);
		}
		
		if(block.getLength() > 0) 
			packet.putBits(11, 2047);
		packet.switchToByteAccess();
		if(block.getLength() > 0)
			packet.getPayload().writeBytes(block.getPayload());
		player.getChannel().write(packet.toPacket());*/
		
		long end = System.nanoTime();
		
		System.out.println("Took " + (end - begin) / (1000000D * Main.getPlayers().size()) + "ms, with " + Main.getPlayers().size() + " players.");
	}
	
	/**
	 * Updates my players movement (THIS).
	 * @param player The player.
	 * @param packet The packet.
	 */
	private void updateMyMovement(Player player, PacketBuf packet) {
		boolean didTeleport = false;
		if(didTeleport) {
			packet.putBit(true);
			packet.putBits(2, 3);
			packet.putBits(7, player.getPosition().getLocalY(player.getLastPosition()));
			packet.putBit(didTeleport);
			packet.putBits(2, player.getPosition().getHeight());
			packet.putBit(player.getUpdateMasks().size() > 0);
			packet.putBits(7, player.getPosition().getLocalX(player.getLastPosition()));
		} else {
			if(player.getWalkDir() == Direction.NONE) {
				packet.putBit(player.getUpdateMasks().size() > 0);
				if(player.getUpdateMasks().size() > 0)
					packet.putBits(2, 0);
			} else {
				if(player.getRunDir() != Direction.NONE) {
					packet.putBit(true);
					packet.putBits(2, 2);
					packet.putBit(true);
					packet.putBits(3, player.getWalkDir().toValue());
					packet.putBits(3, player.getRunDir().toValue());
					packet.putBit(player.getUpdateMasks().size() > 0);
				} else {
					packet.putBit(true);
					packet.putBits(2, 1);
					packet.putBits(3, player.getWalkDir().toValue());
					packet.putBit(player.getUpdateMasks().size() > 0);
				}
			}
		}
	}
	
	/**
	 * Updates an players mask.
	 * @param player The player.
	 * @param block The block.
	 * @param forceAppearance The force appearance flag.
	 */
	public void updateMasks(Player player, PacketBuf block, boolean forceAppearance) {		
		PriorityQueue<UpdateMask> localMasks = new PriorityQueue<UpdateMask>();
		localMasks.addAll(player.getUpdateMasks());
		
		for (UpdateMask updateMask : localMasks) 
			updateMask.appendMask(block);		
	}
	
	/**
	 * Updates the local players movement (OTHER).
	 * @param player The player.
	 * @param packet The packet.
	 */
	private void updateLocalMovement(Player player, PacketBuf packet) {
		if(player.getWalkDir() == Direction.NONE) {
			if(player.getUpdateMasks().size() > 0) {
				packet.putBit(true);
				packet.putBits(2, 0);
			} else 
				packet.putBit(false);
		} else if(player.getRunDir() == Direction.NONE) {
			packet.putBit(true);
			packet.putBits(2, 1);
			packet.putBits(3, player.getWalkDir().toValue());
			packet.putBit(player.getUpdateMasks().size() > 0);
		} else {
			packet.putBit(true);
			packet.putBits(2, 2);
			packet.putBit(true);
			packet.putBits(3, player.getWalkDir().toValue());
			packet.putBits(3, player.getRunDir().toValue());
			packet.putBit(player.getUpdateMasks().size() > 0);
		}
	}
	
	/**
	 * Adds an local player.
	 * @param player The player.
	 * @param local The local player.
	 * @param packet The packet.
	 */
	private void addLocalPlayer(Player player, Player local, PacketBuf packet) {
		packet.putBits(11, local.getIndex());
		
		int deltaY = local.getPosition().getY() - player.getPosition().getY();
		if(deltaY < 0) {
			deltaY += 32;
		}
		int deltaX = local.getPosition().getX() - player.getPosition().getX();
		if(deltaX < 0) {
			deltaX += 32;
		}
		
		packet.putBit(true);
		packet.putBits(5, deltaX);
		packet.putBits(3, 6);
		packet.putBit(true);
		packet.putBits(5, deltaY);
	}
}
