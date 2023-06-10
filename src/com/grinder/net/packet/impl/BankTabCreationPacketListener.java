package com.grinder.net.packet.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.message.decoder.BankTabCreationMessageDecoder;
import com.grinder.game.message.impl.BankTabCreationMessage;
import com.grinder.game.model.item.Item;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.item.container.bank.Bank;
import com.grinder.game.model.item.container.bank.BankConstants;
import com.grinder.game.model.item.container.bank.Banking;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

/**
 * This packet listener is called when an item is dragged onto a bank tab.
 *
 * @author Professor Oak
 */

public class BankTabCreationPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

        final BankTabCreationMessageDecoder messageDecoder = new BankTabCreationMessageDecoder();
        final BankTabCreationMessage message = messageDecoder.decode(packetReader.getPacket());

        final int interfaceId = message.getInterfaceId();
        final int fromSlot = message.getSourceItemSlot();
        final int toTab = message.getTargetTab();
        final int fromTab = interfaceId - BankConstants.CONTAINER_START;

        if (fromTab >= 0 && fromTab < BankConstants.TOTAL_BANK_TABS && toTab >= 0 && toTab < BankConstants.TOTAL_BANK_TABS) {
            if (player.getStatus() == PlayerStatus.BANKING && player.getInterfaceId() == 5292) {

                if (toTab > 1 && player.getBank(toTab - 1).isEmpty()) {
                	return;
                }

                final Bank bank = player.getBank(fromTab);
                final Item item = bank.atSlot(fromSlot).clone();

                if (fromTab != Banking.getTabContainingItemOrDefault(player, item))
                    return;

                //Let's move the item to the new tab
                final int slot = bank.getSlot(item);

                if (slot != fromSlot)
                    return;

                //Temporarily disable note whilst we do switch
                final boolean note = player.withdrawAsNote();
                player.setNoteWithdrawal(false);

                //Make the item switch
                bank.moveItemFromSlot(player.getBank(toTab), item, slot, false, false);

                //Re-set the note var
                player.setNoteWithdrawal(note);

                //Update all tabs
                Banking.reconfigureTabs(player);

                //Refresh items in our current tab
                player.getBank(player.getCurrentBankTab()).refreshItems();
            }
        }
    }
}
