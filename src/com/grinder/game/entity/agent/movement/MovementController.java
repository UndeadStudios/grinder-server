package com.grinder.game.entity.agent.movement;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-20
 */
public interface MovementController {

    void onMovementSequence();

    void sequenceMovement();

    boolean skipNextSequence();

    boolean bypassClippingCheck();

}
