package com.grinder.game.content.skill.task;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a task to be completed
 * 
 * @author 2012
 *
 */
public class SkillingTask {

	/**
	 * The interaction with
	 */
	private Set<Integer> interaction = new HashSet<>();

	/**
	 * The level required
	 */
	private int levelRequired;

	/**
	 * The description
	 */
	private String description;

	/**
	 * The skilling tasks
	 * 
	 * @param interaction
	 *            the interaction
	 * @param levelRequired
	 *            the level required
	 * @param description
	 *            the description
	 */
	public SkillingTask(int[] interaction, int levelRequired, String description) {
		for (int id : interaction) {
			this.interaction.add(id);
		}
		this.setLevelRequired(levelRequired);
		this.setDescription(description);
	}

	/**
	 * Sets the interaction
	 *
	 * @return the interaction
	 */
	public Set<Integer> getInteraction() {
		return interaction;
	}

	/**
	 * Sets the levelRequired
	 *
	 * @return the levelRequired
	 */
	public int getLevelRequired() {
		return levelRequired;
	}

	/**
	 * Sets the levelRequired
	 * 
	 * @param levelRequired
	 *            the levelRequired
	 */
	public void setLevelRequired(int levelRequired) {
		this.levelRequired = levelRequired;
	}

	/**
	 * Sets the description
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description
	 * 
	 * @param description
	 *            the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
