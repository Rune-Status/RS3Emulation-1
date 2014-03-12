package com.rs3e.game.player;

import io.netty.channel.Channel;

import com.rs3e.game.Entity;

public class Player extends Entity {

	private final String password;
	private String username;
	private boolean permBanned;
	public boolean isMember;

	private transient Channel channel;

	public Player(String password) {
		this.password = password;
		isMember = true;
	}

	public void lobbyInit(Channel channel, String username) {
		this.channel = channel;
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public boolean isCorrectPassword(String pass1) {
		return password.equals(pass1);
	}

	public boolean isPermBanned() {
		return permBanned;
	}

	public Channel getChannel() {
		return channel;
	}

}
