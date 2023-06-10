package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;

public class CreationMenuX implements EnterSyntax {

	private final int index;
    private final int item;

    public CreationMenuX(int index, int item) {
    	this.index = index;
        this.item = item;
    }

    @Override
    public void handleSyntax(Player player, String input) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleSyntax(Player player, int input) {
        if (input <= 0 || input > Integer.MAX_VALUE) {
            return;
        }
        player.getPacketSender().sendInterfaceRemoval();
        if (player.getCreationMenu().isPresent()) {
            player.getCreationMenu().get().getAction().execute(index, item, input);
        }
    }

}
