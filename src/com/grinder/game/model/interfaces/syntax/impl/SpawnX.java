package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.net.packet.impl.SpawnItemPacketListener;

public class SpawnX implements EnterSyntax {

    private int item_id;
    private boolean toBank;

    public SpawnX(int item_id, boolean toBank) {
        this.item_id = item_id;
        this.toBank = toBank;
    }

    @Override
    public void handleSyntax(Player player, String input) {
    }

    @Override
    public void handleSyntax(Player player, int input) {
        SpawnItemPacketListener.spawn(player, item_id, input, toBank);
    }
}
