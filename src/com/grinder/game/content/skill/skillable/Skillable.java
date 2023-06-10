package com.grinder.game.content.skill.skillable;

import com.grinder.game.entity.agent.player.Player;

/**
 * Represents a skill action that can be performed by a {@link Player}
 * to gain experience and/or rewards.
 *
 * @author Professor Oak
 */
public interface Skillable {

    /**
     * Executed when starting to skill.
     *
     * @param player the {@link Player} starting to skill.
     */
    void start(Player player);

    /**
     * Executed when skill action was cancelled.
     *
     * @param player the {@link Player} canceling the skill.
     */
    void cancel(Player player);

    /**
     * Checks if the player has the requirements to start this skill.
     *
     * @param player the {@link Player} to check the requirements of.
     *
     * @return {@code true} if the player can start the skill,
     *          {@code false} if not.
     */
    boolean hasRequirements(Player player);

    /**
     * This should (repeatedly) animate the player over some interval.
     *
     * @param player the {@link Player} to be animated.
     */
    void startAnimationLoop(Player player);

    /**
     * This should (repeatedly) animate the player over some interval.
     *
     * @param player the {@link Player} to be animated.
     */
    void startGraphicsLoop(Player player);
    
    /**
     * This should (repeatedly) play sounds for the player over some interval.
     *
     * @param player the {@link Player} to play sounds for.
     */
    void startSoundLoop(Player player);

    /**
     * Used to determine how long it takes for this skill to execute
     * before the player receives experience/rewards.
     *
     * @return an {@code int} representing the cycles required for
     *          the skill to invoke {@link #finishedCycle(Player)}.
     */
    int cyclesRequired(Player player);

    /**
     * This method is invoked on every cycle.
     *
     * @param player the {@link Player} performing the skill action.
     */
    void onCycle(Player player);

    /**
     * Once the amount of cycles has hit {@link #cyclesRequired(Player)},
     * this method is invoked.
     *
     * This method should be used for rewarding the player
     * with items/experience related to the skill.
     *
     * @param player the {@link Player} that finished the skill action.
     */
    void finishedCycle(Player player);

    /**
     * This method is invoked when the skill action is canceled.
     *
     * @param player the {@link Player} canceling the skill action.
     */
	void onCancel(Player player);
}
