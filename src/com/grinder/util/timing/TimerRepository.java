package com.grinder.util.timing;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Bart on 8/12/2015.
 */
public class TimerRepository {

	private Map<TimerKey, Timer> timers = new EnumMap<>(TimerKey.class);

	public boolean has(TimerKey key) {
		Timer timer = timers.get(key);
		return timer != null && timer.ticks() > 0;
	}

	public void register(Timer timer) {
		timers.put(timer.key(), timer);
	}

	public int left(TimerKey key) {
		Timer timer = timers.get(key);
		return timer != null ? timer.ticks() : -1;
	}

	public void register(TimerKey key, int ticks) {
		timers.put(key, new Timer(key, ticks));
	}

	/**
	 * Extend up to (if exists) the given ticks, or register new
	 */
	public void replaceIfLongerOrRegister(TimerKey key, int ticks) {
		timers.compute(key, (k, timer) -> timer == null || timer.ticks() < ticks ? new Timer(key, ticks) : timer);
	}

	public void extendOrRegister(TimerKey key, int ticksToAdd){
		timers.compute(key, (k, timer) ->  new Timer(key, timer == null ? ticksToAdd : timer.ticks() + ticksToAdd));
	}

	/**
	 * Register if non-existant, or extend.
	 */
	public void addOrSet(TimerKey key, int ticks) {
		timers.compute(key, (k, t) -> t == null ? new Timer(key, ticks) : new Timer(key, t.ticks() + ticks));
	}

	public void cancel(TimerKey name) {
		timers.remove(name);
	}

	public void process() {
		if (!timers.isEmpty()) {
			Set<Map.Entry<TimerKey, Timer>> entries = timers.entrySet();
			for (Map.Entry<TimerKey, Timer> entry : entries) {
				entry.getValue().tick();
//				System.out.println("new tick of "+entry.getKey()+" is "+entry.getValue().ticks());
			}
		}
	}

	public Map<TimerKey, Timer> timers() {
		return timers;
	}

}