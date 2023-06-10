package com.grinder.game.model.sound;

public class Sound {
	
	private final int id;
	private int delay;
	private final int loopCount;

	public Sound(int id) {
		this(id, 0);
	}

	public Sound(int id, int delay) {
		this(id, delay, 1);
	}

	public Sound(int id, int delay, int loopCount) {
		this.id = id;
		this.delay = delay;
		this.loopCount = loopCount;
	}
	
	public int getId() {
		return id;
	}
	
	public int getDelay() {
		return delay;
	}

	public int getLoopCount() {
		return loopCount;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}
}

