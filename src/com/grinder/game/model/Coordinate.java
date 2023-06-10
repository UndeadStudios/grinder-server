package com.grinder.game.model;


public class Coordinate {
	
	private int x;
	private int y;
	private int h;
	
	private final transient int hash;
	
	public Coordinate(int x, int y, int h) {
		this.x = x;
		this.y = y;
		this.h = h;
		this.hash = (x & 0x3FFF) << 17 | (y & 0x3FFF) << 3 | (h & 7);
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getH() {
		return h;
	}
	
	public void setH(int h) {
		this.h = h;
	}
	
	@Override
	public boolean equals(Object objects) {
		return objects.hashCode() == hashCode();
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		return "Coordinate [x=" + x + ", y=" + y + ", h=" + h + "]";
	}
	
	public Position toPosition() {
		return new Position(x, y, h);
	}
}
