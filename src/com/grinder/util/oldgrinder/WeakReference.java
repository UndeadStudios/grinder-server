package com.grinder.util.oldgrinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Create a weak reference of a variable.
 * 
 * Represents a link to a physical data, the link can be cut and data will may
 * be ready to be Garbage Collected.
 * 
 * @author Pb600
 *
 * @param <T>
 *            Type of referenced data
 */
public class WeakReference<T> {

	private static final WeakReference<?> EMPTY = new WeakReference();

	private List<CallBack> clearListeners = new ArrayList<>();

	/**
	 * Register a clear event call back listener
	 * 
	 * @param callback
	 *            call back listener
	 */
	public void addClearListener(CallBack callback) {
		clearListeners.add(callback);
	}

	/**
	 * Dispatch clear event callback to all listeners
	 */
	public void dispatchClearEvent() {
		clearListeners.stream().forEach(cb -> cb.callBack());
	}
	/**
	 * Strong reference of the instance.
	 */
	private T instance;

	/**
	 * Create an empty weak reference
	 */
	private WeakReference() {
		this.instance = null;
	}

	/**
	 * Create a weak reference from a strong reference.
	 * 
	 * @param instance
	 *            Strong reference to be stored.
	 */
	public WeakReference(T instance) {
		this.instance = instance;
	}

	/**
	 * Check whether the reference is present or not.
	 * 
	 * @return true in case reference is present.
	 */
	public boolean isPresent() {
		return instance != null;
	}

	/**
	 * Perform an operation in case reference is present.
	 * 
	 * @param consumer
	 *            the argued operation.
	 */
	public void ifPresent(Consumer<T> consumer) {
		if (instance != null) {
			consumer.accept(instance);
		}
	}

	/**
	 * Get current weak reference object.
	 * 
	 * @return the referenced object
	 */
	public T get() {
		return instance;
	}

	/**
	 * Clear the reference from memory, allowing it to be Garbage Collected in
	 * case it's not referenced anywhere else
	 */
	public void clear() {
		this.instance = null;
		dispatchClearEvent();
	}

	/**
	 * Get an object instance from a weak reference.
	 * 
	 * @param weakReference
	 *            weak reference containing the object instance
	 * @return the object instance in case available in the reference.
	 */
	public static <T> T instanceOf(WeakReference<T> weakReference) {
		if (weakReference != null) {
			return weakReference.get();
		}
		return null;
	}

	/**
	 * Get an object instance from a referencer.
	 * 
	 * @param referenceable
	 * @return
	 */
	public static <T> WeakReference<T> referenceOf(Referenceable<T> referenceable) {
		if (referenceable != null)
			return referenceable.getReference();
		return null;
	}
	
	public static <T> WeakReference<T> referenceOfNullable(Referenceable<T> referenceable) {
		if (referenceable != null)
			return referenceable.getReference();
		return empty();
	}

	/**
	 * Check whether given object is represented by current weak reference.
	 * 
	 * @param object
	 *            object to be checked
	 * @return true in case current weak reference holds the given object
	 */
	public boolean represents(T object) {
		Objects.requireNonNull(object);
		return object.equals(instance);
	}

	/**
	 * Create an empty weak reference instance
	 * 
	 * @return an empty weak reference instance.
	 */
	public static <T> WeakReference<T> empty() {
		@SuppressWarnings("unchecked")
		WeakReference<T> t = (WeakReference<T>) EMPTY;
		return t;
	}
}
