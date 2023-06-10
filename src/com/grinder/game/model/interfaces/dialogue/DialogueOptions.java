package com.grinder.game.model.interfaces.dialogue;

import com.grinder.game.entity.agent.player.Player;

/**
 * An abstract class for handling dialogue options.
 *
 * @author Professor Oak
 */
public abstract class DialogueOptions {

    public abstract void handleOption(Player player, int option);
}
