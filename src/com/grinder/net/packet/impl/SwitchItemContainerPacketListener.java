package com.grinder.net.packet.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.item.container.bank.Bank;
import com.grinder.game.model.item.container.bank.BankConstants;
import com.grinder.game.model.item.container.bank.Banking;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

/**
 * This packet listener is called when an item is dragged onto another container.
 *
 */

public class SwitchItemContainerPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
        int fromContainerId = packetReader.readInt();
        int toContainerId = packetReader.readInt();
        int fromSlot = packetReader.readLEShortA();
        int toSlot = packetReader.readLEShort();

        if (player.getHitpoints() <= 0)
            return;


        if (fromContainerId >= BankConstants.CONTAINER_START && fromContainerId < BankConstants.CONTAINER_START + BankConstants.TOTAL_BANK_TABS
                && toContainerId >= BankConstants.CONTAINER_START && toContainerId < BankConstants.CONTAINER_START + BankConstants.TOTAL_BANK_TABS) {

            final int fromTab = fromContainerId - BankConstants.CONTAINER_START;
            final int toTab = toContainerId - BankConstants.CONTAINER_START;

            System.out.println("SWITCH["+packetOpcode+"] from ["+fromTab+"]["+fromSlot+"] to ["+toTab+"]["+toSlot +"]");

            if (player.getStatus() == PlayerStatus.BANKING && player.getInterfaceId() == BankConstants.INTERFACE_ID) {
               
                final Bank sourceBank = player.getBank(fromTab);
                final Bank targetBank = player.getBank(toTab);

                // The dragged item
                final Item draggedItem = sourceBank.atSlot(fromSlot).clone();
                if(fromTab != Banking.getTabContainingItemOrDefault(player, draggedItem))
                    return;

                if (!sourceBank.containsAtSlot(fromSlot, draggedItem.getId()))
                    return;

                // Temporarily disable note whilst we do switch
                final boolean note = player.withdrawAsNote();
                player.setNoteWithdrawal(false);

                if (toSlot > targetBank.getLastItemSlot() && !targetBank.isSlotOccupied(toSlot)) {
                    // If dragging to slot after last item's slot in tab (and extra check that the slot is empty)
                    // Make the item switch
                    sourceBank.moveItemFromSlot(targetBank, draggedItem, fromSlot, false, false);

                } else if (player.insertMode() || !targetBank.isSlotOccupied(toSlot)) {
                    // Inserting or Swapping with empty slot, only moving dragged item
                    // Make the item switch
                    sourceBank.moveItemFromSlot(targetBank, draggedItem, fromSlot, false, false);

                    // Insert to end of tab
                    targetBank.swap(targetBank.getSlot(draggedItem), targetBank.getLastItemSlot() + 1);
                    // Rearrange item
                    Banking.rearrange(player, targetBank, targetBank.getSlot(draggedItem), toSlot, false);
                } else {
                    // Swapping two items
                    // The item to swap with
                    final Item draggedIntoItem = targetBank.getItems()[toSlot].clone();
                    if (toTab != Banking.getTabContainingItemOrDefault(player, draggedIntoItem))
                        return;

                    // Make sure the item to swap with is in the slot
                    if (!targetBank.containsAtSlot(toSlot, draggedIntoItem.getId()))
                        return;

                    // Make the item switch for both items
                    sourceBank.moveItemFromSlot(targetBank, draggedItem, fromSlot, false, false);
                    targetBank.moveItemFromSlot(sourceBank, draggedIntoItem, toSlot, false, false);

                    // Rearrange items
                    Banking.rearrange(player, targetBank, targetBank.getSlot(draggedItem), toSlot, false);
                    Banking.rearrange(player, sourceBank, sourceBank.getSlot(draggedIntoItem), fromSlot, false);
                }

                // Re-set the note var
                player.setNoteWithdrawal(note);

                // Refresh containers
                player.getBank(player.getCurrentBankTab()).refreshItems();
            }
        }
    }
}
