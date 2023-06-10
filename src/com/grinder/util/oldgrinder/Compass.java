package com.grinder.util.oldgrinder;

public enum Compass {
	
	NORTH(-1), SOUTH(1), EAST(-1), WEAST(1);
	
	private final int offset;
	
	private Compass(int offset) {
		this.offset = offset;
	}
	
	public int getOffset() {
		return offset;
	}
}
