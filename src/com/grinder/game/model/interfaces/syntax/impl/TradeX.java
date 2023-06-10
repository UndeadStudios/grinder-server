package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;

public class TradeX implements EnterSyntax {

    private boolean deposit;
    private int item_id;
    private int slot_id;

    public TradeX(int item_id, int slot_id, boolean deposit) {
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

        ItemContainer to = deposit ? player.getTrading().getContainer() : player.getInventory();
        ItemContainer from = deposit ? player.getInventory() : player.getTrading().getContainer();

        player.getTrading().moveItem(item_id, input, slot_id, from, to);
    }

}
