package com.grinder.util.timing;

/**
 * Created by Bart on 8/12/2015.
 */
public class Timer {

	private final TimerKey key;
	private int ticks;
	private final int initialTicks;

	public Timer(TimerKey key, int ticks) {
		this.key = key;
		this.ticks = ticks;
		initialTicks = ticks;
	}

	public int ticks() {
		return ticks;
	}

	public TimerKey key() {
		return key;
	}

	public void tick() {
		if (ticks > 0)
			ticks--;
	}

	public void extendOrCap(int extraTicks, int maxExtra){
		ticks = Math.min(ticks + extraTicks, initialTicks + maxExtra);
	}
}
