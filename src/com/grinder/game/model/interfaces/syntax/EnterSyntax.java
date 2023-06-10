package com.grinder.game.model.interfaces.syntax;

import com.grinder.game.entity.agent.player.Player;

public interface EnterSyntax {

    void handleSyntax(Player player, String input);

    void handleSyntax(Player player, int input);
}
