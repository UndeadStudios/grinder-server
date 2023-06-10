package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;

public class BankQuantityX implements EnterSyntax {

    @Override
    public void handleSyntax(Player player, String input) {
    }

    @Override
    public void handleSyntax(Player player, int input) {
    	player.setModifiableXValue(input);
        player.getPacketSender().sendModifiableXValue();
        if (input <= 0) {
            return;
        }
        player.getPacketSender().sendConfig(1114, 3);
        player.setBankQuantityConfig(3);
    }

}
