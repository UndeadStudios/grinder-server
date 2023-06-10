package com.grinder.game.entity.updating;

import java.util.HashMap;
import java.util.Map;

/**
 * A specialized collection of {@link UpdateBlock}s.
 *
 * @author Graham
 */
public final class UpdateBlockSet implements Cloneable {

	/**
	 * A {@link Map} of {@link UpdateBlock}s.
	 */
	private final Map<Class<? extends UpdateBlock>, UpdateBlock> blocks = new HashMap<>(8);

	/**
	 * Adds a {@link UpdateBlock}.
	 *
	 * @param block The block to add.
	 */
	public void add(UpdateBlock block) {
		Class<? extends UpdateBlock> clazz = block.getClass();
		blocks.put(clazz, block);
	}

	/**
	 * Clears the set.
	 */
	public void clear() {
		blocks.clear();
	}

	@Override
	public UpdateBlockSet clone() {
		UpdateBlockSet copy = new UpdateBlockSet();
		copy.blocks.putAll(blocks);
		return copy;
	}

	/**
	 * Checks if this set contains the specified {@link UpdateBlock}.
	 *
	 * @param clazz The block's class.
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean contains(Class<? extends UpdateBlock> clazz) {
		return blocks.containsKey(clazz);
	}

	/**
	 * Gets a {@link UpdateBlock} from this set.
	 *
	 * @param clazz The block's class.
	 * @return The block.
	 */
	@SuppressWarnings("unchecked")
	public <T extends UpdateBlock> T get(Class<T> clazz) {
		return (T) blocks.get(clazz);
	}

	/**
	 * Removes a {@link UpdateBlock} from this set.
	 *
	 * @param clazz The block's class.
	 * @return The removed block.
	 */
	@SuppressWarnings("unchecked")
	public <T extends UpdateBlock> T remove(Class<? extends UpdateBlock> clazz) {
		return (T) blocks.remove(clazz);
	}

	/**
	 * Gets the size of this set.
	 *
	 * @return The size.
	 */
	public int size() {
		return blocks.size();
	}

}