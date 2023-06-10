package com.grinder.util;

public class AtomicInteger {

	public AtomicInteger() {
		this.value = 0;
	}

	private int value;

	public AtomicInteger(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public int setValue(int value) {
		this.value = value;
		return value;
	}

	public void add(int amount) {
		this.value += amount;
	}

	public int remove(int amount) {
		this.value -= amount;
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public boolean canHold(int itemAmount) {
		return (long) ((long) value + (long) itemAmount) < Integer.MAX_VALUE;
	}

}
