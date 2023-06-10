package com.grinder.util.math;

/**
 * A vector of 2 dimensions integer based.
 * 
 * @author Pb600
 * 
 */
public class Vector2i {
	private int x;
	private int y;
	
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int length() {
		return (int) Math.sqrt(x * x + y * y);
	}
	
	public float dot(Vector2i r) {
		return (x * r.getX() + y * r.getY());
	}
	
	public Vector2i normalized() {
		int length = length();
		
		return new Vector2i(x / length, y / length);
	}
	
	public Vector2i rotate(float angle) {
		double rad = Math.toRadians(angle);
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);
		
		return new Vector2i((int) (x * cos - y * sin), (int) (x * sin + y * cos));
	}
	
	public Vector2i add(Vector2i r) {
		return new Vector2i(x + r.getX(), y + r.getY());
	}
	
	public Vector2i add(int r) {
		return new Vector2i(x + r, y + r);
	}
	
	public Vector2i sub(Vector2i r) {
		return new Vector2i(x - r.getX(), y - r.getY());
	}
	
	public Vector2i sub(int r) {
		return new Vector2i(x - r, y - r);
	}
	
	public Vector2i mul(Vector2i r) {
		return new Vector2i(x * r.getX(), y * r.getY());
	}
	
	public Vector2i mul(int r) {
		return new Vector2i(x * r, y * r);
	}
	
	public Vector2i div(Vector2i r) {
		return new Vector2i(x / r.getX(), y / r.getY());
	}
	
	public Vector2i div(int r) {
		return new Vector2i(x / r, y / r);
	}
	
	public Vector2i abs() {
		return new Vector2i(Math.abs(x), Math.abs(y));
	}
	
	public float cross(Vector2i v) {
		return x * v.y - y * v.x;
	}
	
	public String toString() {
		return "(" + x + " " + y + ")";
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	public Vector2i toIntegerNormalized() {
		x = x >= 1 ? 1 : x <= -1 ? -1 : 0;
		y = y >= 1 ? 1 : y <= -1 ? -1 : 0;
		return new Vector2i(x, y);
	}

	public Vector2f toFloat() {
		return new Vector2f(x, y);
	}
}