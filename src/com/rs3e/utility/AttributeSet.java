package com.rs3e.utility;

import java.util.HashMap;
import java.util.Map;

public class AttributeSet {

	/**
	 * Represents the message flags.
	 */
	protected final Map<String, Object> flags = new HashMap<>();

	/**
	 * Returns a flag.
	 * 
	 * @param key
	 *            The key.
	 * @param failure
	 *            The failure return.
	 * @return The flag.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getFlag(String key, T failure) {
		if (!flags.containsKey(key)) {
			return failure;
		}
		return (T) flags.get(key);
	}

	/**
	 * Remvoes a key from the flags.
	 * 
	 * @param key
	 *            The key to remove.
	 */
	public void removeFlag(String key) {
		flags.remove(key);
	}

	/**
	 * Returns if the flag set contains a key.
	 * 
	 * @param key
	 *            The key to look for.
	 * @return True if so; false otherwise.
	 */
	public boolean containsFlag(String key) {
		return flags.containsKey(key);
	}

	/**
	 * Puts a flag in the map.
	 * 
	 * @param key
	 *            The key.
	 * @param value
	 *            The value.
	 */
	public void putFlag(String key, Object value) {
		flags.put(key, value);
	}
}
