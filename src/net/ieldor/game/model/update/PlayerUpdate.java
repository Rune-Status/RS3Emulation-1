/*
 * This file is part of RS3Emulator.
 *
 * RS3Emulator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RS3Emulator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RS3Emulator.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.ieldor.game.model.update;

import java.util.ArrayList;
import java.util.Iterator;

import net.ieldor.game.World;
import net.ieldor.game.model.Direction;
import net.ieldor.game.model.Entity;
import net.ieldor.game.model.Position;
import net.ieldor.game.model.player.Player;
import net.ieldor.game.model.update.UpdateFlags.Flags;
import net.ieldor.io.Packet;
import net.ieldor.io.PacketBuf;
import net.ieldor.network.ActionSender;

/**
*
* @author Tom
*
*/
public class PlayerUpdate {
	
	private Player player;
	
	private ArrayList<Player> localPlayers = new ArrayList<Player>(2048);
	private byte[] playerFlags = new byte[2048];
	private int[] localPlayerIndicies = new int[2048];
	private int localPlayerCount = 0;
	private int[] otherPlayerIndicies = new int[2048];
	private int otherPlayerCount = 0;
	
	private int[] regionHashes = new int[2048];
	
	public PlayerUpdate(Player player) {
		this.player = player;
	}
	
	public void init (PacketBuf buf) {
		buf.switchToBitAccess();
		buf.putBits(30, player.getPosition().hashCode());
		localPlayers.add(player);
		localPlayerIndicies[localPlayerCount++] = player.getIndex();
		for (int index = 1;index<2048;index++) {
			if (index == player.getIndex()) {
				continue;
			}
			buf.putBits(18, 0);
			//buf.putBits(18, (player == null) ? 0 : player.getPosition().regionHash());
			otherPlayerIndicies[otherPlayerCount++] = index;
		}
		buf.switchToByteAccess();
	}

	public Packet makePacket() {
		if (player.isTeleported()) {
			player.getActionSender().sendMapRegion(false);
		}
		
		PacketBuf buffer = new PacketBuf(ActionSender.PLAYER_UPDATE_PACKET, Packet.PacketType.SHORT);
		PacketBuf block = new PacketBuf();
		
		packLocalPlayers(buffer, block, true);
		packLocalPlayers(buffer, block, false);
		packOtherPlayers(buffer, block, true);
		packOtherPlayers(buffer, block, false);
		if (block.getLength() > 0) {
			buffer.put(block.getPayload());
		}
		localPlayerCount = 0;
		otherPlayerCount = 0;
		for (int playerIndex = 1; playerIndex < 2048; playerIndex++) {
			playerFlags[playerIndex] >>= 1;
			Player p = localPlayers.get(playerIndex);
			if (p == null) {
				otherPlayerIndicies[otherPlayerCount++] = playerIndex;
			} else {
				localPlayerIndicies[localPlayerCount++] = playerIndex;
			}
		}
		/*buffer.switchToBitAccess();
		updateMyMovement(player, buffer);
		
		if (player.getUpdateFlags().requiresUpdate()) {
			updateMasks(player, block, false);
		}
		buffer.putBits(8, player.getLocalPlayers().size());*/
		
		/*for (Iterator<Player> it$ = player.getLocalPlayers().iterator(); it$.hasNext(); ) {
			Player local = it$.next();
			if (!local.getPosition().isWithinDistance(player.getPosition(), 15)) {
				buffer.putBits(1, 1);
				buffer.putBits(2, 3);
				it$.remove();
				continue;
			}
			packLocalPlayers(local, buffer);
			if (local.getUpdateFlags().requiresUpdate()) {
				updateMasks(player, block, false);
			}
		}
		
		int addedCount = 0;
		for (Player world : World.getWorld().getPlayers()) {
			if (world == null || world.getIndex() == player.getIndex() || addedCount >= 25) {
				continue;
			}
			if (player.getLocalPlayers().contains(world) || !world.getPosition().isWithinDistance(player.getPosition(), Position.MAX_DISTANCE)) {
				continue;
			}
			addedCount++;
			player.getLocalPlayers().add(world);
			//packOtherPlayers(player, world, buffer);
			updateMasks(player, block, true);
		}
		
		if (block.getLength() > 0) {
			buffer.putBits(11, 2047);
		}
		
		buffer.switchToByteAccess();
		if (block.getLength() > 0) {
			buffer.put(block.getPayload());
		}*/
		return buffer.toPacket();
	}

	private void packOtherPlayers(PacketBuf buffer, PacketBuf updateBlock, boolean nsn2) {
		buffer.switchToBitAccess();
		int skip = 0;
		for (int i=0; i<otherPlayerCount;i++) {
			int playerIdx = otherPlayerIndicies[i];
			if (nsn2 ? (0x1 & playerFlags[playerIdx]) == 0
					: (0x1 & playerFlags[playerIdx]) != 0) {
				continue;
			}
			if (skip > 0) {
				skip--;
				playerFlags[playerIdx] = (byte) (playerFlags[playerIdx] | 2);
				continue;
			}
			for (Player p : World.getDefaultWorld().getPlayers()) {
				if (p.getIndex() != playerIdx) {
					continue;//TODO: Remove the looping (somehow)
				}
				if (needsAdd(p)) {
					packAdd(buffer, updateBlock, p);
				} else {
					if (p != null && p.getPosition().regionHash() != p.getLastPosition().regionHash()) {
						buffer.putBits(1, 1);//Update needed
						packRegionHash(buffer, p);
					} else {
						buffer.putBits(1, 0);//Update not needed
					}
					for (int i2 = playerIdx+1; i2>otherPlayerCount;i2++) {
						int playerIdx2 = otherPlayerIndicies[i2];
						if (nsn2 ? (0x1 & playerFlags[playerIdx2]) != 0
								: (0x1 & playerFlags[playerIdx2]) == 0) {
							continue;
						}
						Player p2 = localPlayers.get(playerIdx2);
						if (needsRemove(p2) || p2.isTeleported() 
								|| p2.getWalkDir() != Direction.NONE || p2.getUpdateFlags().requiresUpdate()) {
							break;
						}
						skip++;
					}
					packSkip(buffer, skip);
					playerFlags[playerIdx] = (byte) (playerFlags[playerIdx] | 2);
				}
			}			
		}
		buffer.switchToByteAccess();
		/*buffer.putBits(11, local.getIndex());

		int deltaY = local.getPosition().getY() - player.getPosition().getY();
		if(deltaY < 0) {
			deltaY += 32;
		}
		
		int deltaX = local.getPosition().getX() - player.getPosition().getX();
		if(deltaX < 0) {
			deltaX += 32;
		}
		
		buffer.putBits(1, 1);
		buffer.putBits(5, deltaY);	
		buffer.putBits(3, 1);
		buffer.putBits(1, 1);
		buffer.putBits(5, deltaX);*/
	}

	private void packLocalPlayers(PacketBuf buffer, PacketBuf updateBlock, boolean nsn0) {
		buffer.switchToBitAccess();
		int skip = 0;
		for (int i = 0; i < localPlayerCount; i++) {
			int playerIdx = localPlayerIndicies[i];
			if (nsn0 ? (0x1 & playerFlags[playerIdx]) != 0
					: (0x1 & playerFlags[playerIdx]) == 0) {
				continue;
			}
			if (skip > 0) {
				skip--;
				playerFlags[playerIdx] = (byte) (playerFlags[playerIdx] | 2);
				continue;
			}
			Player p = localPlayers.get(playerIdx);
			if (needsRemove(p)) {
				packRemove(buffer, p);
			} else {
				skip = packUpdate(buffer, updateBlock, nsn0, p, skip, i);
			}
		}
		/*for (Iterator<Player> it$ = player.getLocalPlayers().iterator(); it$.hasNext(); ) {
			Player local = it$.next();
			if (!local.getPosition().isWithinDistance(player.getPosition(), 15)) {
				buffer.putBits(1, 1);
				buffer.putBits(2, 3);
				it$.remove();
				continue;
			}
			//packLocalPlayers(local, buffer);
			//if (local.getUpdateFlags().requiresUpdate()) {
			//	updateMasks(player, block, false);
			//}
		}*/
		buffer.switchToByteAccess();
		/*if (player.getWalkDir() == Direction.NONE) {
			if(player.getUpdateFlags().requiresUpdate()) {
				buffer.putBits(1, 1);
				buffer.putBits(2, 0);
			} else {
				buffer.putBits(1, 0);
			}
		} else if (player.getRunDir() == Direction.NONE) {
			buffer.putBits(1, 1);
			buffer.putBits(2, 1);
			buffer.putBits(3, player.getWalkDir().toValue());
			buffer.putBits(1, player.getUpdateFlags().requiresUpdate() ? 1 : 0);
		} else {
			buffer.putBits(1, 1);
			buffer.putBits(2, 2);
			buffer.putBits(3, player.getWalkDir().toValue());
			buffer.putBits(3, player.getRunDir().toValue());
			buffer.putBits(1, player.getUpdateFlags().requiresUpdate() ? 1 : 0);
		}*/
	}

	private boolean needsRemove(Player p) {
		//TODO: Remove players who have logged out
		return !player.getPosition().isWithinDistance(p.getPosition(), Position.MAX_DISTANCE);
	}
	
	private boolean needsAdd(Player p) {
		//TODO: Make sure the player has logged in
		return p != null && player.getPosition().isWithinDistance(p.getPosition(), Position.MAX_DISTANCE);
	}
	
	private void packAdd (PacketBuf buffer, PacketBuf updateBlock, Player p) {
		buffer.putBits(1, 1);//Needs update	
		buffer.putBits(2, 0); // request add
		if (p.getPosition().regionHash() == p.getLastPosition().regionHash()) {
			buffer.putBits(1, 0);
		} else {
			buffer.putBits(1, 1);
			packRegionHash(buffer, p);
		}
		buffer.putBits(6, p.getPosition().getXInRegion());
		buffer.putBits(6, p.getPosition().getYInRegion());
		packMaskUpdate(p, updateBlock, true);
		buffer.putBits(1, 1);
	}
	
	private int packUpdate (PacketBuf buffer, PacketBuf updateBlock, boolean nsn0, Player p, int skip, int localPlayerIdx) {
		boolean maskUpdate = p.getUpdateFlags().requiresUpdate();
		if (maskUpdate) {
			packMaskUpdate(p, updateBlock, false);
		}
		if (p.isTeleported()) {
			buffer.putBits(1, 1);//Needs update	
			buffer.putBits(1, maskUpdate ? 1 : 0);
			buffer.putBits(2, 3);//Teleport update
			int xOffset = p.getPosition().getLocalX(p.getLastPosition());
			int yOffset = p.getPosition().getLocalY(p.getLastPosition());
			int planeOffset = p.getPosition().getHeight() - p.getLastPosition().getHeight();
			if (p.getPosition().isWithinDistance(p.getLastPosition(), Position.MAX_DISTANCE)) {
				buffer.putBits(1, 1);//Within range
				/*buffer.putBits(7, xOffset);
				//buffer.putBits(1, p.getUpdateFlags().requiresUpdate() ? 1 : 0);
				buffer.putBits(2, planeOffset);
				buffer.putBits(7, yOffset);*/
				buffer.putBits(30, (yOffset & 0x3fff)
						| ((xOffset & 0x3fff) << 14)
						| ((planeOffset & 0x3) << 28));
			} else {
				buffer.putBits(1, 0);//Not within range
				if (xOffset < 0) {// viewport used to be 15 now 16
					xOffset += 32;
				}
				if (yOffset < 0) {
					yOffset += 32;
				}
				buffer.putBits(12, yOffset | (xOffset << 5) | (planeOffset << 10));
			}
		} else if (player.getWalkDir() != Direction.NONE)  {
			buffer.putBits(1, 1);//Needs update	
			buffer.putBits(1, maskUpdate ? 1 : 0);
			if (player.getRunDir() == Direction.NONE) {
				buffer.putBits(2, 1);//Walk update				
				buffer.putBits(3, player.getWalkDir().toValue());//Direction
				buffer.putBits(1, 0);//TODO: Figure out what this is for
			} else {
				buffer.putBits(2, 1);//Run update
				buffer.putBits(4, player.getRunDir().toValue());//Direction				
			}
		} else if (maskUpdate) {
			buffer.putBits(1, 1);//Needs update	
			buffer.putBits(1, 1);//Mask update
			buffer.putBits(2, 0);//No movement change
		} else {//Skip
			buffer.putBits(1, 0); // no update needed
			for (int i = localPlayerIdx+1; i>localPlayerCount;i++) {
				int playerIdx = localPlayerIndicies[i];
				if (nsn0 ? (0x1 & playerFlags[playerIdx]) != 0
						: (0x1 & playerFlags[playerIdx]) == 0) {
					continue;
				}
				Player p2 = localPlayers.get(playerIdx);
				if (needsRemove(p2) || p2.isTeleported() 
						|| p2.getWalkDir() != Direction.NONE || p2.getUpdateFlags().requiresUpdate()) {
					break;
				}
				skip++;
			}
			packSkip(buffer, skip);
			playerFlags[localPlayerIdx] = (byte) (playerFlags[localPlayerIdx] | 2);
		}
		return skip;
	}
	
	private void packRemove (PacketBuf buffer, Player p) {
		buffer.putBits(1, 1); // needs update
		buffer.putBits(1, 0); // no masks update needed
		buffer.putBits(2, 0); // request remove
		//regionHashes[p.getIndex()] = (p.getLastPosition() == null) ? p.getPosition().regionHash() : p.getLastPosition().regionHash();
		if (p.getLastPosition() == null) {
			regionHashes[p.getIndex()] = p.getPosition().regionHash();
			buffer.putBits(1, 0);
		} else {
			buffer.putBits(1, 1);
			packRegionHash(buffer, p);
			regionHashes[p.getIndex()] = p.getPosition().regionHash();			
		}
	}
	
	private void packSkip (PacketBuf buffer, int amount) {
		buffer.putBits(2, amount == 0 ? 0 : amount > 255 ? 3
				: (amount > 31 ? 2 : 1));
		if (amount > 0) {
			buffer.putBits(amount > 255 ? 11 : (amount > 31 ? 8 : 5), amount);
		}
	}
	
	private void packRegionHash (PacketBuf buffer, Entity p) {	
		int lastRegionX = p.getLastPosition().getRegionX();
		int lastRegionY = p.getLastPosition().getRegionY();
		int lastPlane = p.getLastPosition().getHeight();
		int currentRegionX = p.getPosition().getRegionX();
		int currentRegionY = p.getPosition().getRegionY();
		int currentPlane = p.getPosition().getHeight();
		int planeOffset = currentPlane - lastPlane;
		if (lastRegionX == currentRegionX && lastRegionY == currentRegionY) {
			buffer.putBits(2, 1);
			buffer.putBits(2, planeOffset);			
		} else if (Math.abs(currentRegionX - lastRegionX) <= 1 && Math.abs(currentRegionY - lastRegionY) <= 1) {
			int opcode;
			int dx = currentRegionX - lastRegionX;
			int dy = currentRegionY - lastRegionY;
			if (dx == -1 && dy == -1) {
				opcode = 0;
			} else if (dx == 1 && dy == -1) {
				opcode = 2;
			} else if (dx == -1 && dy == 1) {
				opcode = 5;
			} else if (dx == 1 && dy == 1) {
				opcode = 7;
			} else if (dy == -1) {
				opcode = 1;
			} else if (dx == -1) {
				opcode = 3;
			} else if (dx == 1) {
				opcode = 4;
			} else {
				opcode = 6;
			}
			buffer.putBits(2, 2);
			buffer.putBits(5, (planeOffset << 3) + (opcode & 0x7));
		} else {
			int xOffset = currentRegionX - lastRegionX;
			int yOffset = currentRegionY - lastRegionY;
			int anUnknownBit = 1;//TODO: Figure out what this is...
			buffer.putBits(2, 3);
			buffer.putBits(18, (yOffset & 0xff) | ((xOffset & 0xff) << 8) | (planeOffset << 16) | (anUnknownBit << 18));
		}
	}
	
	private void packMaskUpdate (Player p, PacketBuf buffer, boolean forceSync) {
		int maskData = 0;
		
		if (player.getUpdateFlags().isMarked(Flags.APPEARANCE)) {
			maskData |= 0x20;
		}
		
		if(maskData >= 0x8) {
			maskData |= 0x8;
			buffer.put((byte) (maskData & 0xFF));
			buffer.put((byte) (maskData >> 8));
		} else {
			buffer.put((byte) (maskData & 0xFF));
		}
		
		if (player.getUpdateFlags().isMarked(Flags.APPEARANCE)) {
			player.getAppearance().pack(buffer);
		}
	}
	
	/*private void updateMyMovement(Player player, PacketBuf buffer) {
		if (player.isTeleported()) {
			buffer.putBits(1, 1);
			buffer.putBits(2, 3);
			buffer.putBits(1, 1);
			buffer.putBits(7, player.getPosition().getLocalX(player.getLastPosition()));
			buffer.putBits(1, player.getUpdateFlags().requiresUpdate() ? 1 : 0);
			buffer.putBits(2, player.getPosition().getHeight());
			buffer.putBits(7, player.getPosition().getLocalY(player.getLastPosition()));
		} else {
			if (player.getWalkDir() == Direction.NONE) {
				buffer.putBits(1, player.getUpdateFlags().requiresUpdate() ? 1 : 0);
				if (player.getUpdateFlags().requiresUpdate()) {
					buffer.putBits(2, 0);
				}
			} else {
				if (player.getRunDir() != Direction.NONE) {
					buffer.putBits(1, 1);
					buffer.putBits(2, 2);
					buffer.putBits(3, player.getWalkDir().toValue());
					buffer.putBits(3, player.getRunDir().toValue());
					buffer.putBits(1, player.getUpdateFlags().requiresUpdate() ? 1 : 0);
				} else {
					buffer.putBits(1, 1);
					buffer.putBits(2, 1);
					buffer.putBits(3, player.getWalkDir().toValue());
					buffer.putBits(1, player.getUpdateFlags().requiresUpdate() ? 1 : 0);
				}
			}
		}		
	}

	private void updateMasks(Player player, PacketBuf block, boolean forceSync) {
		
	}*/
}
