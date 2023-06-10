package com.grinder.game.content.miscellaneous;

import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;

import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @since 01/04/2020
 */
public class WizardSig {

    private final static int TRAVEL_PRICE = 5_000_000;

    public static void handleFirstClickOption(Player player){
        DialogueManager.start(player, 2572);
        player.setDialogueOptions(new DialogueOptions() {
            @Override
            public void handleOption(Player player1, int option) {
                switch (option) {
                    case 1:
                        if (player1.getInventory().getAmount(ItemID.COINS) >= TRAVEL_PRICE) {
                            player1.getInventory().delete(995, TRAVEL_PRICE);
                            travel(player, "North Miscellania", new Position(2516 + Misc.getRandomInclusive(2), 3882 + Misc.getRandomInclusive(1)), 171);
                        } else {
                            DialogueManager.start(player1, 2574);
                        }
                        break;
                    case 2:
                        if (player.getRights().isHighStaff() || PlayerUtil.isBronzeMember(player)) {
                            travel(player, "Bronze member island", new Position(2145 + Misc.getRandomInclusive(4), 2587 + Misc.getRandomInclusive(3)), 253);
                        } else {
                            new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.SAILOR_3936)
                                    .setText("I am sorry, you are not eligible to travel to this island.", "You must be a Bronze member or higher in rank to do that.")
                                    .add(DialogueType.NPC_STATEMENT)
                                    .setText("Come back again once you're ready.")
                                    .start(player);
                        }
                        break;
                    case 3:
                        if (player.getRights().isHighStaff() || PlayerUtil.isRubyMember(player)) {
                            travel(player, "La Isla Ebana", new Position(3675 + Misc.getRandomInclusive(1), 2974 + Misc.getRandomInclusive(3)), 253);
                        } else {
                            new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.SAILOR_3936)
                                    .setText("I am sorry, you are not eligible to travel to this area.", "You must be a Ruby member or higher in rank to do that.")
                                    .add(DialogueType.NPC_STATEMENT)
                                    .setText("Come back again once you're ready.")
                                    .start(player);
                        }
                        break;
                    case 4:
                        if (player.getRights().isHighStaff() || PlayerUtil.isPlatinumMember(player)) {
                            travel(player, "Platinum member island", new Position(2043 + Misc.getRandomInclusive(4), 3683 + Misc.getRandomInclusive(3)), 253);
                        } else {
                            new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.SAILOR_3936)
                                    .setText("I am sorry, you are not eligible to travel to this island.", "You must be a Platinum member or higher in rank to do that.")
                                    .add(DialogueType.NPC_STATEMENT)
                                    .setText("Come back again once you're ready.")
                                    .start(player);
                        }
                        break;
                }
            }
        });
    }


    public static void handleSecondClickOption(Player player) {
        new DialogueBuilder(DialogueType.OPTION)
                .firstOption("North Miscellania.", player1 -> {
                    if (player.getInventory().getAmount(ItemID.COINS) >= TRAVEL_PRICE) {
                        player.getInventory().delete(995, TRAVEL_PRICE);
                        travel(player, "North Miscellania", new Position(2516 + Misc.getRandomInclusive(2), 3882 + Misc.getRandomInclusive(1)), 171);
                    } else {
                         DialogueManager.start(player, 2574);
                    }
                }).secondOption("Bronze member island @gre@(FREE)</col>.", player1 -> {
                    if (player.getRights().isHighStaff() || PlayerUtil.isBronzeMember(player)) {
                        travel(player, "Bronze member island", new Position(2145 + Misc.getRandomInclusive(4), 2587 + Misc.getRandomInclusive(3)), 253);
                    } else {
                        new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.SAILOR_3936)
                                .setText("I am sorry, you are not eligible to travel to this island.", "You must be a Bronze member or higher in rank to do that.")
                                .add(DialogueType.NPC_STATEMENT)
                                .setText("Come back again once you're ready.")
                                .start(player);
                    }
                }).thirdOption("La Isla Ebana @gre@(FREE)</col>.", player1 -> {
                    if (player.getRights().isHighStaff() || PlayerUtil.isRubyMember(player)) {
                        travel(player, "La Isla Ebana", new Position(3675 + Misc.getRandomInclusive(1), 2974 + Misc.getRandomInclusive(3)), 253);
                    } else {
                        new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.SAILOR_3936)
                                .setText("I am sorry, you are not eligible to travel to this island.", "You must be a Ruby member or higher in rank to do that.")
                                .add(DialogueType.NPC_STATEMENT)
                                .setText("Come back again once you're ready.")
                                .start(player);
                    }
                }).fourthOption("Platinum member island @gre@(FREE)</col>.", player1 -> {
                    if (player.getRights().isHighStaff() || PlayerUtil.isPlatinumMember(player)) {
                        travel(player, "Platinum member island", new Position(2043 + Misc.getRandomInclusive(4), 3683 + Misc.getRandomInclusive(3)), 253);
                    } else {
                        new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.SAILOR_3936)
                                .setText("I am sorry, you are not eligible to travel to this island.", "You must be a Platinum member or higher in rank to do that.")
                                .add(DialogueType.NPC_STATEMENT)
                                .setText("Come back again once you're ready.")
                                .start(player);
                    }
        }).start(player);
    }

    public static void travel(Player player, String areaName, Position position, int jinglebit){

        if (player.busy())
            return;

        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 3, TimeUnit.SECONDS, false, true))
            return;

        player.BLOCK_ALL_BUT_TALKING = true;

        player.getPacketSender().sendFadeScreen("Traveling to " + areaName +"", 2, 6);
        player.getPacketSender().sendJinglebitMusic(jinglebit, 15);

        TaskManager.submit(player, 4, () -> {
            player.moveTo(position);
            player.getPacketSender().sendInterfaceRemoval();
            player.BLOCK_ALL_BUT_TALKING = false;
        });
    }

}
