package com.rs3e.network.protocol.handlers;

import com.rs3e.network.session.Session;
import com.rs3e.utility.AttributeSet;

public abstract class PacketHandler<T extends Session> extends AttributeSet {

	/**
	 * Called when a packet should be handled.
	 */
	public abstract void handle(T session);
}
