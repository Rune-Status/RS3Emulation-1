package net.ieldor.game.social;

public enum OnlineStatus {
	EVERYONE(0),
	FRIENDS(1),
	NOBODY(2);
	
	private final int statusCode;
	OnlineStatus (int code) {
		this.statusCode = code;
	}
	
	public int getCode () {
		return statusCode;
	}
	
	public static OnlineStatus get (int code) {
		switch (code) {
		case 0:
			return EVERYONE;
		case 1:
			return FRIENDS;
		case 2:
			return NOBODY;
		default:
			return NOBODY;
		}
	}
}
