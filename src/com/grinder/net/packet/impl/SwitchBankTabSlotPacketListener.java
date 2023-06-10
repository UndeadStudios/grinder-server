package com.grinder.net.packet.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.item.container.bank.Bank;
import com.grinder.game.model.item.container.bank.BankConstants;
import com.grinder.game.model.item.container.bank.Banking;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

/**
 * This packet listener is called when an item is dragged from one bank tab into another
 *
 */

public class SwitchBankTabSlotPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
        int fromTab = packetReader.readInt();
        int toTab = packetReader.readInt();

        if (player.getHitpoints() <= 0)
            return;

        if (player == null || player.getHitpoints() <= 0) {
            return;
        }

        // Can not rearrange with first tab or same tab or empty tab
        if (fromTab > 0 && fromTab < BankConstants.TOTAL_BANK_TABS && toTab > 0 && toTab < BankConstants.TOTAL_BANK_TABS
                && fromTab != toTab && !player.getBank(fromTab).isEmpty() && !player.getBank(toTab).isEmpty()) {
            if (player.getStatus() == PlayerStatus.BANKING && player.getInterfaceId() == BankConstants.INTERFACE_ID) {
                Bank oldTab = null;
                // Make a copy of our current bank tab if we're not in the main tab
                if (player.getCurrentBankTab() > 0) {
                    oldTab = player.getBank(player.getCurrentBankTab());
                }

                // Rearrange the tabs
                Banking.rearrangeBankTabs(player, fromTab, toTab, false);

                // If we have a copy of our old current bank tab, find its new index and send it
                if (oldTab != null) {
                    for (int i = 0; i < BankConstants.TOTAL_BANK_TABS; i++) {
                        if (player.getBank(i).equals(oldTab)) {
                            player.setCurrentBankTab(i);
                            player.getPacketSender().sendCurrentBankTab(player.getCurrentBankTab());
                            break;
                        }
                    }
                }

                // Refresh containers
                player.getBank(player.getCurrentBankTab()).refreshItems();
            }
            return;
        }
    }
}
