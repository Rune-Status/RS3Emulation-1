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
package net.ieldor.game.model.masks.player;

import net.ieldor.game.model.masks.UpdateMask;
import net.ieldor.game.model.player.Player;
import net.ieldor.io.PacketBuf;
import net.ieldor.network.packet.context.ChatContext;
import net.ieldor.network.packet.decoder.ChatDecoder;

/**
 * An {@link UpdateMask} that is used to update chat.
 *
 * @author Thomas Le Godais <thomaslegodais@live.com>
 *
 */
public class ChatUpdateMask extends UpdateMask {

	/**
	 * The data hex for the mask.
	 */
	private static final int MASK_DATA = 0x80;
	
	/**
	 * The priority of the mask synchronization.
	 */
	private static final int MASK_PRIORITY = 3;

	/**
	 * The chat context.
	 */
	private ChatContext context;

	/**
	 * Constructs a new {@code ChatUpdateMask} instance.
	 * @param player The player chatting.
 	 * @param context The context of the chat.
	 */
	public ChatUpdateMask(Player player, ChatContext context) {
		super(player, MASK_DATA, MASK_PRIORITY);
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see net.ieldor.game.model.masks.UpdateMask#appendMask(net.ieldor.io.PacketBuf)
	 */
	@Override
	public void appendMask(PacketBuf buf) {
		buf.putLEShort((context.getColour() << 8) + context.getEffects());
		buf.put((byte) 0);

		byte[] chatData = new byte[256];
		int offset = ChatDecoder.encryptPlayerChat(context.getText(), chatData);
		
		buf.put((byte) offset);
		buf.putReverse(chatData, 0, offset);
	}
}
