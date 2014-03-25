package net.ieldor.modules.worldlist;

public final class WorldData {

	public static final int FLAG_MEMBERS    = 0x1;
	public static final int FLAG_QUICK_CHAT = 0x2;
	public static final int FLAG_PVP        = 0x4;
	public static final int FLAG_LOOT_SHARE = 0x8;
	public static final int FLAG_HIGHLIGHT  = 0x10;

	private final int id, flags;
	private final String activity, ip, name;
	private final Country country;
	private final ServerLocation location;

	public WorldData(int id, String name, int flags, Country country, String activity, String ip, ServerLocation location) {
		this.id = id;
		this.name = name;
		this.flags = flags;
		this.country = country;
		this.activity = activity;
		this.ip = ip;
		this.location = location;
	}

	public int getNodeId() {
		return id;
	}
	
	public String getName () {
		return name;
	}

	public int getFlags() {
		return flags;
	}

	public Country getCountry() {
		return country;
	}

	public String getActivity() {
		return activity;
	}

	public String getIp() {
		return ip;
	}
	
	public ServerLocation getServerLocation () {
		return location;
	}

}
