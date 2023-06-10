package com.grinder.util.collection;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A utility class containing helper methods for various {@link Collection} objects.
 *
 * @author Ryley
 */
public final class CollectionUtil {

	/**
	 * Polls every element within the specified {@link Queue} and performs the specified {@link Consumer} event for
	 * each element.
	 *
	 * @param queue The {@link Queue} to poll elements from. Must not be {@code null}.
	 * @param consumer The {@link Consumer} to execute for each polled element. Must not be {@code null}.
	 */
	public static <T> void pollAll(Queue<T> queue, Consumer<T> consumer) {
		Preconditions.checkNotNull(queue, "Queue may not be null");
		Preconditions.checkNotNull(consumer, "Consumer may not be null");

		T element;
		while ((element = queue.poll()) != null) {
			consumer.accept(element);
		}
	}

	/**
	 * Concatenates two lists of types that extend the specified type.
	 *
	 * @param list1 the first list
	 * @param list2 the second list
	 * @param <T> the type of the list element
	 * @return a List of the elements in both input lists.
	 */
	public static<T> List<T> merge(List<? extends T> list1, List<? extends T> list2)
	{
		return Stream.concat(list1.stream(), list2.stream())
				.collect(Collectors.toList());
	}

	/**
	 * Suppresses the default public constructor to discourage normal instantiation outside of this class.
	 */
	private CollectionUtil() {

	}

}