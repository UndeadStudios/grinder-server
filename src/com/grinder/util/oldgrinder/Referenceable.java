package com.grinder.util.oldgrinder;

/**
 * Interface
 * 
 * @author Pb600
 *
 * @param <T>
 */
public interface Referenceable<T> {
	
	public WeakReference<T> getReference();

	public void clearReference();
	
}