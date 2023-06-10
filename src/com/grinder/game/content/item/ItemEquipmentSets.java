package com.grinder.game.content.item;

import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;

import java.util.concurrent.TimeUnit;

/**
 * TODO: add documentation
 *
 * @author ? Probably Lou
 * @since 15/01/2021
 */
public class ItemEquipmentSets {

    public static boolean cannotOpenSet(Player player, int freeSlotsRequired) {
        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
            return true;
        }
        if ((player.getInventory().countFreeSlots()) < freeSlotsRequired) {
            player.sendMessage("You need at least "+freeSlotsRequired+" free inventory slots to do that.", 1000);
            return true;
        }
        if (player.busy()) {
            player.sendMessage("You can't do that when you're busy.", 1000);
            return true;
        }
        if (player.getCombat().isInCombat()) {
            player.sendMessage("You must wait 10 seconds after being out of combat to do this!", 1000);
            return true;
        }
        EntityExtKt.markTime(player, Attribute.LAST_PRAY);
        return false;
    }

    public static void openThirdAgeMageSet(Player player, int itemId) {

        if (cannotOpenSet(player, 3))
            return;

        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION).setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15212, 1);
                    player.getInventory().add(10342, 1);
                    player.getInventory().add(10338, 1);
                    player.getInventory().add(10340, 1);
                    player.sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                    player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel()
                .start(player);
    }


    public static void openThirdAgeRangeSet(Player player, int itemId) {

        if (cannotOpenSet(player, 4))
            return;

        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION)
                .setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15211, 1);
                    player.getInventory().add(10334, 1);
                    player.getInventory().add(10330, 1);
                    player.getInventory().add(10332, 1);
                    player.getInventory().add(10336, 1);
                    player.sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                    player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel()
                .start(player);
    }

    public static void openThirdAgeMeleeSet(Player player, int itemId) {

        if (cannotOpenSet(player, 4))
            return;

        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION)
                .setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15210, 1);
                    player.getInventory().add(10350, 1);
                    player.getInventory().add(10348, 1);
                    player.getInventory().add(10346, 1);
                    player.getInventory().add(10352, 1);
                    player.sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                    player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel()
                .start(player);
    }

    public static void openSuperRingsSet(Player player, int itemId) {

        if (cannotOpenSet(player, 8))
            return;

        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION)
                .setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15264, 1);
                    player.getInventory().add(11770, 1);
                    player.getInventory().add(11771, 1);
                    player.getInventory().add(11772, 1);
                    player.getInventory().add(11773, 1);
                    player.getInventory().add(12691, 1);
                    player.getInventory().add(12692, 1);
                    player.getInventory().add(12785, 1);
                    player.getInventory().add(13202, 1);
                    player.sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                    player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel().start(player);
    }

    public static void openJusticiarSet(Player player, int itemId) {
        if (cannotOpenSet(player, 3))
            return;
        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION)
                .setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15263, 1);
                    player.getInventory().add(22326, 1);
                    player.getInventory().add(22327, 1);
                    player.getInventory().add(22328, 1);
                    player.sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                    player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel()
                .start(player);
    }

    public static void openSpiritShieldSet(Player player, int itemId) {
        if (cannotOpenSet(player, 3))
            return;
        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION)
                .setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15266, 1);
                    player.getInventory().add(12817, 1);
                    player.getInventory().add(12821, 1);
                    player.getInventory().add(12825, 1);
                    player.sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                    player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel()
                .start(player);
    }

    public static void openSuperBootsSet(Player player, int itemId) {

        if (cannotOpenSet(player, 3))
            return;

        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION)
                .setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15265, 1);
                    player.getInventory().add(13239, 1);
                    player.getInventory().add(13237, 1);
                    player.getInventory().add(13235, 1);
                    player.sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                    player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel()
                .start(player);
    }

    public static void openZurielSet(Player player, int itemId) {

        if (cannotOpenSet(player, 4))
            return;

        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION)
                .setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15219, 1);
                    player.getInventory().add(22650, 1);
                    player.getInventory().add(22653, 1);
                    player.getInventory().add(22656, 1);
                    player.getInventory().add(22647, 1);
                    player.sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                    player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel()
                .start(player);
    }

    public static void openMorriganSet(Player player, int itemId) {

        if (cannotOpenSet(player, 5))
            return;

        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION)
                .setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15218, 1);
                    player.getInventory().add(22638, 1);
                    player.getInventory().add(22641, 1);
                    player.getInventory().add(22644, 1);
                    player.getInventory().add(22636, 100);
                    player.getInventory().add(22634, 100);
                    player.sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                    player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel()
                .start(player);
    }

    public static void openVestaArmourSet(Player player, int itemId) {

        if (cannotOpenSet(player, 4))
            return;

        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION).setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15217, 1);
                    player.getInventory().add(22616, 1);
                    player.getInventory().add(22619, 1);
                    player.getInventory().add(22613, 1);
                    player.getInventory().add(22610, 1);
                    player.sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                    player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel().start(player);
    }

    public static void openStatiusArmourSet(Player player, int itemId) {

        if (cannotOpenSet(player, 4))
            return;

        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION)
                .setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15216, 1);
                    player.getInventory().add(22625, 1);
                    player.getInventory().add(22628, 1);
                    player.getInventory().add(22631, 1);
                    player.getInventory().add(22622, 1);
                    player.getPacketSender()
                            .sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                                                player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel()
                .start(player);
    }

    public static void openCorruptedArmourSet(Player player, int itemId) {

        if (cannotOpenSet(player, 5))
            return;

        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION)
                .setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15214, 1);
                    player.getInventory().add(20838, 1);
                    player.getInventory().add(20840, 1);
                    player.getInventory().add(20842, 1);
                    player.getInventory().add(20844, 1);
                    player.getInventory().add(20846, 1);
                    player.getPacketSender()
                            .sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                                                player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel().start(player);
    }

    public static void openInfinityRobesSet(Player player, int itemId) {

        if (cannotOpenSet(player, 5))
            return;

        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION)
                .setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15213, 1);
                    player.getInventory().add(6918, 1);
                    player.getInventory().add(6916, 1);
                    player.getInventory().add(6924, 1);
                    player.getInventory().add(6922, 1);
                    player.getInventory().add(6920, 1);
                    player.getPacketSender()
                            .sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                                                player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel()
                .start(player);
    }

    public static void openEliteVoidKnightSet(Player player, int itemId) {

        if (cannotOpenSet(player, 6))
            return;

        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION).setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15209, 1);
                    player.getInventory().add(11665, 1);
                    player.getInventory().add(11664, 1);
                    player.getInventory().add(11663, 1);
                    player.getInventory().add(13072, 1);
                    player.getInventory().add(13073, 1);
                    player.getInventory().add(8842, 1);
                    player.getPacketSender()
                            .sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                                                player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel()
                .start(player);
    }

    public static void openSuperiorVoidKnightSet(Player player, int itemId) {

        if (cannotOpenSet(player, 6))
            return;

        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION).setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15726, 1);
                    player.getInventory().add(26477, 1);
                    player.getInventory().add(26475, 1);
                    player.getInventory().add(26473, 1);
                    player.getInventory().add(26469, 1);
                    player.getInventory().add(26471, 1);
                    player.getInventory().add(26467, 1);
                    player.getPacketSender()
                            .sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                    player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel()
                .start(player);
    }

    public static void openVoidKnightSet(Player player, int itemId) {

        if (cannotOpenSet(player, 6))
            return;

        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(itemId, 200)
                .setText("Are you absolutely sure you want to open", "@dre@" + ItemDefinition.forId(itemId).getName() +"</col>? This action is irreversible.")
                .add(DialogueType.OPTION)
                .setOptionTitle("Choose an Option.")
                .firstOption("Open " + ItemDefinition.forId(itemId).getName() +".", player2 -> {
                    if (!player.getInventory().contains(itemId)) {
                        return;
                    }
                    player.getInventory().delete(15208, 1);
                    player.getInventory().add(11665, 1);
                    player.getInventory().add(11664, 1);
                    player.getInventory().add(11663, 1);
                    player.getInventory().add(8839, 1);
                    player.getInventory().add(8840, 1);
                    player.getInventory().add(8842, 1);
                    player.getPacketSender()
                            .sendMessage("You've opened your @dre@" + ItemDefinition.forId(itemId).getName() + "</col>.");
                    player.getPacketSender().sendSound(72);
                    player.getPacketSender().sendInterfaceRemoval();
                })
                .addCancel().start(player);
    }
}
