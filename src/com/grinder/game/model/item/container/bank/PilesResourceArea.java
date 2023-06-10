package com.grinder.game.model.item.container.bank;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.ObjectDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.util.TextUtil;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * TODO: add documentation
 */
public class PilesResourceArea {

    public static long determineValueInBankOf(final Player player){
        return Stream.of(player.getBanks())
                .filter(Objects::nonNull)
                .mapToLong(ItemContainerUtil::determineValueOfContents)
                .sum();
    }

    public static void createNotingDialogue(final ObjectDefinition bankBoothDefinition, final Item item, final Player player) {
        createNotingDialogue(bankBoothDefinition, item.getId(), item.getAmount(), player);
    }

    private static void createNotingDialogue(final ObjectDefinition bankBoothDefinition, final int id, final int amount, final Player player) {

        if(!bankBoothDefinition.getName().toLowerCase().contains("bank"))
            return;

        final ItemDefinition definition = ItemDefinition.forId(id);

        if (definition.getNoteId() == -1) {
            player.getPacketSender().sendMessage("You can't note the " + definition.getName().toLowerCase() + ".", 1000);
            return;
        }

        if (definition.isNoted()) {

            new DialogueBuilder(DialogueType.OPTION)
                    .firstOption("Un-note all", futurePlayer -> unnoteItem(id, amount, definition, futurePlayer))
                    .secondOption("Un-note x", futurePlayer -> {
                        futurePlayer.getPacketSender().sendInterfaceRemoval();
                        futurePlayer.setEnterSyntax(new EnterSyntax() {
                            @Override
                            public void handleSyntax(Player player, String input) {
                                try {
                                    int value = Integer.parseInt(input);
                                } catch (NumberFormatException e){
                                    player.getPacketSender().sendMessage("Please enter a valid input numbers.");
                                    return;
                                }
                                if(TextUtil.isInteger(input))
                                    handleSyntax(player, Integer.parseInt(input));
                            }
                            @Override
                            public void handleSyntax(Player player, int input) {
                                unnoteItem(id, input, definition, player);
                            }
                        });
                        futurePlayer.getPacketSender().sendEnterInputPrompt("Enter how many items you would like to un-note");
                    })
                    .addCancel()
                    .start(player);

        } else if(definition.getNoteId() != -1) {
            new DialogueBuilder(DialogueType.OPTION)
                    .firstOption("Note all", futurePlayer -> {
                        noteItem(id, player.getInventory().getAmount(id), definition, futurePlayer);
                    })
                    .secondOption("Note x", futurePlayer -> {
                        futurePlayer.setEnterSyntax(new EnterSyntax() {
                            @Override
                            public void handleSyntax(Player player, String input) {
                                try {
                                    int value = Integer.parseInt(input);
                                } catch (NumberFormatException e){
                                    player.getPacketSender().sendMessage("Please enter a valid input numbers.");
                                    return;
                                }
                                if(TextUtil.isInteger(input))
                                    handleSyntax(player, Integer.parseInt(input));
                            }
                            @Override
                            public void handleSyntax(Player player, int input) {
                                noteItem(id, input, definition, player);
                            }
                        });
                        futurePlayer.getPacketSender().sendEnterInputPrompt("Enter how many items you would like to note");
                    })
                    .addCancel()
                    .start(player);
        }
    }

    public static void createNotingDialogue2(final int npcId, final int id, final int amount, final Player player) {

        if(npcId != 13)
            return;

        final ItemDefinition definition = ItemDefinition.forId(id);

        if (definition.getNoteId() == -1) {
            DialogueManager.sendStatement(player, "You can't note the " + definition.getName().toLowerCase() + ".");
            return;
        }

        if (definition.isNoted()) {

            new DialogueBuilder(DialogueType.OPTION)
                    .firstOption("Un-note all", futurePlayer -> unnoteItem(id, amount, definition, futurePlayer))
                    .secondOption("Un-note x", futurePlayer -> {
                        futurePlayer.getPacketSender().sendInterfaceRemoval();
                        futurePlayer.setEnterSyntax(new EnterSyntax() {
                            @Override
                            public void handleSyntax(Player player, String input) {
                                try {
                                    int value = Integer.parseInt(input);
                                } catch (NumberFormatException e){
                                    player.getPacketSender().sendMessage("Please enter a valid input numbers.");
                                    return;
                                }
                                if(TextUtil.isInteger(input))
                                    handleSyntax(player, Integer.parseInt(input));
                            }
                            @Override
                            public void handleSyntax(Player player, int input) {
                                unnoteItem(id, input, definition, player);
                            }
                        });
                        futurePlayer.getPacketSender().sendEnterInputPrompt("Enter how many items you would like to un-note");
                    })
                    .addCancel()
                    .start(player);

        } else if(definition.getNoteId() != -1) {
            new DialogueBuilder(DialogueType.OPTION)
                    .firstOption("Note all", futurePlayer -> {
                        noteItem(id, player.getInventory().getAmount(id), definition, futurePlayer);
                    })
                    .secondOption("Note x", futurePlayer -> {
                        futurePlayer.setEnterSyntax(new EnterSyntax() {
                            @Override
                            public void handleSyntax(Player player, String input) {
                                try {
                                    int value = Integer.parseInt(input);
                                } catch (NumberFormatException e){
                                    player.getPacketSender().sendMessage("Please enter a valid input numbers.");
                                    return;
                                }
                                if(TextUtil.isInteger(input))
                                    handleSyntax(player, Integer.parseInt(input));
                            }
                            @Override
                            public void handleSyntax(Player player, int input) {
                                noteItem(id, input, definition, player);
                            }
                        });
                        futurePlayer.getPacketSender().sendEnterInputPrompt("Enter how many items you would like to note");
                    })
                    .addCancel()
                    .start(player);
        }
    }

    private static void noteItem(final int id, final int amount, final ItemDefinition definition, final Player player) {
        final int amountInInventory = player.getInventory().getAmount(id);
        final int amountToAdd = amountInInventory >= amount ? amount : amountInInventory;
        final int notedId = definition.getNoteId();

        if(amountInInventory < amount)
            player.sendMessage("You could only note @dre@"+ amountInInventory +"</col> items.");

        player.getInventory().delete(id, amountToAdd);
        player.getInventory().add(notedId, amountToAdd);
        if (AreaManager.inWilderness(player)) {
            AchievementManager.processFor(AchievementType.COLLECTOR, player);
        }
        player.getPacketSender().sendInterfaceRemoval();
        //player.getPacketSender().sendMessage("You have noted @dre@" + (amountToAdd) + " " + definition.getName() + (amountToAdd > 1 ? "s" : "") + "</col>.", 1000);
        player.getInventory().refreshItems();
        SkillUtil.stopSkillable(player);
        DialogueManager.sendStatement(player, "You have noted @dre@" + (amountToAdd) + " " + definition.getName() + (amountToAdd > 1 ? "s" : "") + "</col>.");
    }

    private static void unnoteItem(final int id, final int amount, final ItemDefinition definition, final Player player) {
        final int unnotedId = definition.getNoteId();
        final int amountInInventory = player.getInventory().getAmount(id);
        int amountToAdd = amountInInventory >= amount ? amount : amountInInventory;

        if(amountInInventory < amount)
            player.sendMessage("You could only un-note @dre@"+amount+"</col> items.");

        if (amountToAdd > player.getInventory().countFreeSlots())
            amountToAdd = player.getInventory().countFreeSlots();
        if (amountToAdd <= 0) {
            player.getPacketSender().sendInterfaceRemoval();
            return;
        }
        if (player.getInventory().countFreeSlots() == 0) {
            player.getPacketSender().sendMessage("You don't have enough space in your inventory.", 1000);
            return;
        }
        if (amountToAdd > player.getInventory().countFreeSlots())
            amountToAdd = player.getInventory().countFreeSlots();
        player.getInventory().getById(id).decrementAmountBy(amountToAdd);
        player.getInventory().add(unnotedId, amountToAdd);
        player.getPacketSender().sendInterfaceRemoval();
        player.getInventory().refreshItems();
        SkillUtil.stopSkillable(player);
        DialogueManager.sendStatement(player, "You have un-noted @dre@" + (amountToAdd) + " " + definition.getName() + (amountToAdd > 1 ? "s" : "") + "</col>.");
        //player.getPacketSender().sendMessage("You have un-noted @dre@" + (amountToAdd) + " " + definition.getName() + (amountToAdd > 1 ? "s" : "") + "</col>.", 1000);

    }


    /**
     * Integer hashing algorithm
     *
     * @param value: Integer value to be hashed
     * @return Hashed value
     */
    static int hashPin(int value) {
        value = ~value + (value << 15);
        value = value ^ (value >>> 12);
        value = value + (value << 2);
        value = value ^ (value >>> 4);
        value = value * 2057;
        value = value ^ (value >>> 16);
        return value;
    }
}
