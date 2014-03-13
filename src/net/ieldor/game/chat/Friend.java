package net.ieldor.game.chat;

import net.ieldor.utility.world.World;

public class Friend {
	private String currentName;
	private String previousName;
	private World currentWorld = null;
	private int friendsChatRank = 0;
	private boolean isReferred = false;
	private String note;
	
	public Friend (String currentName, String previousName, World world, int rank, boolean referred, String note) {
		this.currentName = currentName;
		this.previousName = previousName;
		this.currentWorld = world;
		this.friendsChatRank = rank;
		this.isReferred = referred;
		this.note = note;
	}
	
	public String getName () {
		return currentName;
	}
	
	public String getPrevName () {
		return previousName;
	}
	
	public World getWorld () {
		return currentWorld;
	}
	
	public int getFcRank () {
		return friendsChatRank;
	}
	
	public boolean isReferred () {
		return isReferred;
	}
	
	public String getNote () {
		return note;
	}
}
