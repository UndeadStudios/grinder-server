package com.grinder.util.chance;
/**
 * Represents a weighted object.
 * 
 * @author Michael | Chex
 */
public interface WeightedObject<T> extends Comparable<WeightedObject<T>> {

	/**
	 * Gets the object's weight.
	 * 
	 * @return The weight.
	 */
	double getWeight();

	/**
	 * Gets the representation of the weighted chance.
	 * 
	 * @return The representation.
	 */
	T get();

	/**
	 * The toString method.
	 * 
	 * @return The class variables represented in a string.
	 */
	@Override
	String toString();
	
}