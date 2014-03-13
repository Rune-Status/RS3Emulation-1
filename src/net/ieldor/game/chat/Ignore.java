package net.ieldor.game.chat;

public class Ignore {
	private String currentName;
	private String previousName;
	private String note;
	
	public Ignore (String currentName, String previousName, String note) {
		this.currentName = currentName;
		this.previousName = previousName;
		this.note = note;
	}
	
	
	public String getCurrentName () {
		return currentName;
	}
	
	public String getPreviousName () {
		return previousName;
	}
	
	public String getNote () {
		return note;
	}
}
