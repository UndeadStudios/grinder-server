package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;

public class SellX implements EnterSyntax {

    private final int slot, itemId;

    public SellX(int itemId, int slot) {
        this.itemId = itemId;
        this.slot = slot;
    }

    @Override
    public void handleSyntax(Player player, String input) {

    }

    @Override
    public void handleSyntax(Player player, int input) {
        if (player.getStatus() == PlayerStatus.SHOPPING) {
            ShopManager.sellItem(player, slot, itemId, input);
        }
    }

}
