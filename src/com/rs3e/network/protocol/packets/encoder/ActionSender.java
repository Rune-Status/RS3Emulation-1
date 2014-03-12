package com.rs3e.network.protocol.packets.encoder;

import com.rs3e.game.player.Player;
import com.rs3e.network.protocol.messages.GameMessage;
import com.rs3e.network.protocol.messages.GameMessage.MessageOpcode;

public class ActionSender {

	/**
	 * Represents the player.
	 */
	private final Player player;

	/**
	 * Constructs a new {@code ActionSender.java}.
	 * 
	 * @param player
	 *            The player.
	 */
	public ActionSender(Player player) {
		this.player = player;
	}

	public void sendGameMessage(String message) {
		sendGameMessage(new GameMessage(MessageOpcode.CHAT_BOX, message, player));
	}

	/**
	 * @param gameMessage
	 */
	private void sendGameMessage(GameMessage gameMessage) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}
}
