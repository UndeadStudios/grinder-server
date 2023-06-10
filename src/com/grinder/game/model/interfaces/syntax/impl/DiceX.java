package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;

public class DiceX implements EnterSyntax {

    private boolean deposit;
    private int item_id;
    private int slot_id;

    public DiceX(int item_id, int slot_id, boolean deposit) {
        this.item_id = item_id;
        this.slot_id = slot_id;
        this.deposit = deposit;
    }

    @Override
    public void handleSyntax(Player player, String input) {
    }

    @Override
    public void handleSyntax(Player player, int input) {
        if (item_id < 0 || slot_id < 0 || input <= 0) {
            return;
        }

        if (deposit) {
        	player.getGambling().deposit(item_id, slot_id, input);
        } else {
        	player.getGambling().withdraw(item_id, slot_id, input);
        }
    }

}
