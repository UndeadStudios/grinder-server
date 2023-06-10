package com.grinder.util.oldgrinder;

/**
 * Map Entry wrapper.
 * 
 * @author Pb600
 *
 */
public class MapEntry {

	private final Object key;
	private final Object value;

	public MapEntry(Object key, Object value) {
		this.key = key;
		this.value = value;
	}

	public Object getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public static MapEntry of(Object key, Object value) {
		return new MapEntry(key, value);
	}
	
	@Override
	public String toString() {
		return "MapEntry [key=" + key + ", value=" + value + "]";
	}

}
