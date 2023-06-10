package com.grinder.net.packet.impl;

import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.container.bank.BankConstants;
import com.grinder.game.model.item.container.bank.Banking;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

/**
 * This packet listener is called when an item is dragged onto another slot.
 *
 * @author relex lawl
 */

public class SwitchItemSlotPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

        int interfaceId = packetReader.readInt();
        int value = packetReader.readByteC();
        int fromSlot = packetReader.readLEShortA();
        int toSlot = packetReader.readLEShort();

        if (player.getHitpoints() <= 0) {
            return;
        }
        if (player.BLOCK_ALL_BUT_TALKING)
            return;
        if (player.isInTutorial())
            return;
        if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false) )
            return;
        if (EntityExtKt.getBoolean(player, Attribute.HAS_TRIGGER_RANDOM_EVENT, false)) {
            player.sendMessage("Please finish your random event before doing anything else.");
            return;
        }

        if (!MorphItems.INSTANCE.notTransformed(player, "do this", true, true))
            return;

        if(player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME)
            player.stopTeleporting();


        //Bank..
        if (interfaceId >= BankConstants.CONTAINER_START && interfaceId < BankConstants.CONTAINER_START + BankConstants.TOTAL_BANK_TABS) {

            final int tab = interfaceId - BankConstants.CONTAINER_START;

            if (fromSlot >= 0 && fromSlot < player.getBank(tab).capacity() && toSlot >= 0 && toSlot < player.getBank(tab).capacity() && toSlot != fromSlot) {
                Banking.rearrange(player, player.getBank(tab), fromSlot, toSlot, true);
            }

            return;
        }

        switch (interfaceId) {
            case Inventory.INTERFACE_ID:
            case BankConstants.INVENTORY_INTERFACE_ID:
                final Inventory inventory = player.getInventory();
                final int capacity = inventory.capacity();

                if (player.getInterfaceId() != BankConstants.INTERFACE_ID) {
                    player.getPacketSender().sendInterfaceRemoval();
                    player.setDialogue(null);
                    player.setDialogueOptions(null);
                    player.setDialogueContinueAction(null);
                    player.setEnterSyntax(null);

                    SkillUtil.stopSkillable(player);
                }
                if (player.getInterfaceId() != BankConstants.INTERFACE_ID) {
                    if (player.busy()) {
                        player.sendMessage("You can't do that when you are busy.");
                        return;
                    }
                }
                if (fromSlot >= 0 && fromSlot < capacity && toSlot >= 0 && toSlot < capacity && toSlot != fromSlot) {
                    inventory.swap(fromSlot, toSlot);
                    player.getPacketSender().sendItemOnInterface(Inventory.INTERFACE_ID, inventory.atSlot(fromSlot), fromSlot);
                    player.getPacketSender().sendItemOnInterface(Inventory.INTERFACE_ID, inventory.atSlot(toSlot), toSlot);
                }
                player.getInventory().refreshItems();
                break;
        }
    }
}
