package com.grinder.util.collection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A circular list whose elements can't change after instantiation.
 */
public class ShuffledCircularList<T> {

	private int counter;

	private final List<T> data;

	@SafeVarargs
	private ShuffledCircularList(T... t) {
		this.data = Arrays.asList(t);

		Collections.shuffle(this.data);
	}

	@SafeVarargs
	public static <T> ShuffledCircularList<T> of(T... t) {
		return new ShuffledCircularList<T>(t);
	}

	public void increment() {
		this.counter = (this.counter + 1) % data.size();
	}

	public T get() {
		return this.data.get(this.counter);
	}

}
