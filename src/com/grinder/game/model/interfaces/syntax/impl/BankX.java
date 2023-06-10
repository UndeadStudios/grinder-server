package com.grinder.game.model.interfaces.syntax.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.game.model.item.container.bank.Banking;

public class BankX implements EnterSyntax {

    private int item_id;
    private int slot_id;

    public BankX(int item_id, int slot_id) {
        this.item_id = item_id;
        this.slot_id = slot_id;
    }

    @Override
    public void handleSyntax(Player player, String input) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleSyntax(Player player, int input) {
    	player.setModifiableXValue(input);
		player.getPacketSender().sendModifiableXValue();
		if(item_id < 0 || slot_id < 0 || input <= 0) {
			return;
		}
		Banking.depositFromInventory(player, item_id, slot_id, input, false);
    }

}
