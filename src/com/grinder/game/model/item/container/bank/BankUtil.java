package com.grinder.game.model.item.container.bank;

import com.grinder.game.GameConstants;
import com.grinder.game.World;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.ObjectDefinition;
import com.grinder.game.entity.agent.player.*;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.StaffLogRelay;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.net.packet.PacketSender;
import com.grinder.util.Logging;
import com.grinder.util.Misc;
import com.grinder.util.TextUtil;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-23
 */
public class BankUtil {

    /**
     * Displays the {@link Player#getBanks() bank containers} of the argued owner.
     *
     * @see #findOfflineBankContainers(String) for offline bank loading.
     *
     * @param player            the {@link Player} to display the bank for.
     * @param bankOwnerName     the username of the {@link Player} to display the bank of.
     */
    public static void displayBank(Player player, String bankOwnerName){

        World.findPlayerByName(bankOwnerName)
                .map(Player::getBanks)
                .or(() -> findOfflineBankContainers(bankOwnerName)).ifPresentOrElse(
                (banks) -> {
                    final PacketSender sender = player.getPacketSender();
                    player.setStatus(PlayerStatus.NONE);
                    player.sendMessage("Viewing @dre@" + Misc.formatPlayerName(bankOwnerName) + "@bla@'s bank.");

                    long bankValue = Stream.of(banks)
                            .filter(Objects::nonNull)
                            .mapToLong(ItemContainerUtil::determineValueOfContents)
                            .sum();

                    sender.sendString(5383, "Viewing the Bank of @red@(" + Misc.formatPlayerName(bankOwnerName) +")</col> (est. value = " + Misc.formatWithAbbreviation(bankValue) + ")");
                    sender.sendString(50053, "" + banks[0].getValidItems().size());
                    sender.sendString(50054, "" + banks[0].capacity());

                    for (int i = 0; i < BankConstants.TOTAL_BANK_TABS; i++)
                        if (banks[i] != null)
                            sender.sendItemContainer(banks[i], BankConstants.CONTAINER_START + i);

                    sender.sendInterface(BankConstants.INTERFACE_ID);
                    sender.sendInterfaceScrollReset(BankConstants.BANK_SCROLL_BAR_INTERFACE_ID);

                },
                () -> player.sendMessage("Could not load the bank of '"+bankOwnerName+"'.")
        );
    }

    private static Optional<Bank[]> findOfflineBankContainers(String bankOwnerName){
        if(!PlayerSaving.playerExists(bankOwnerName))
            return Optional.empty();
        else
            return Optional.ofNullable(PlayerLoading.getBankContainers(bankOwnerName));
    }

    /**
     * TEMP method to resolve broken banks manually!
     */
    public static void validateBank(String username, Bank[] bankTabs){
        final HashSet<Integer> uniqueIds = new HashSet<>();
        StringBuilder messageBuilder = new StringBuilder();
        for (int tab = 0; tab < bankTabs.length; tab++) {
            Bank bank = bankTabs[tab];
            if (bank != null) {
                Item[] items = bank.getItems();
                for (int slot = 0; slot < items.length; slot++) {
                    Item item = items[slot];
                    if (item != null && item.getId() != -1) {
                        if(!uniqueIds.add(item.getId())){
                            if(messageBuilder.length() > 0)
                                messageBuilder.append(", ");
                            messageBuilder.append("[").append(tab).append("][").append(slot).append("]");
                        }
                    }
                }
            }
        }
        final String message = messageBuilder.toString();
        if(!message.isEmpty()){
            PlayerUtil.broadcastPlayerMediumStaffMessage("[Bank_Issue]['"+username+"']: "+message+" | {please report on discord :)}");
        }
    }

    public static void replaceAll(Bank[] bankTabs, int toFindId, int toReplaceId){
        for(Bank bank : bankTabs){
            if(bank != null){
                bank.replaceAll(toFindId, toReplaceId);
            }
        }
    }

    public static boolean addToBank(Player player, Item item){
        return addToBank(player, item, player.getStatus() == PlayerStatus.BANKING);
    }

    public static boolean addToBank(Player player, Item item, boolean refresh){

        final ItemDefinition definition = item.getDefinition();

        final int bankTabId = Banking.getTabContainingItemOrDefault(player, item);
        final Bank bankTab = player.getBank(bankTabId);

        if(bankTab.countFreeSlots() <= 0 && !bankTab.contains(item.getId())){
            player.sendMessage("Could not add item to your bank, bank is full!");
            player.sendMessage("@red@WARNING: The item is dropped the item beneath you instead!");
            ItemOnGroundManager.registerNonGlobal(player, item);
            return false;
        }

        final int slot;
        final long amountInBank;

        if (definition.isNoted() && !ItemDefinition.forId(definition.getNoteId()).isNoted()) {
            final int itemId = item.getDefinition().getNoteId();
            slot = bankTab.getSlot(itemId);
        } else {
            slot = bankTab.getSlot(item);
        }

        amountInBank = bankTab.getAmount(item);
        final long newAmount = amountInBank + (long)item.getAmount();

        if(newAmount > Integer.MAX_VALUE){
            player.sendMessage("@red@WARNING: You have too many of the item in your bank, thus it has been dropped beneath you!");
            ItemOnGroundManager.registerNonGlobal(player, item);
            return false;
        }

        if(bankTab.contains(item.getId())){
            bankTab.set(slot, item.clone().setAmount((int) newAmount));
        } else
            bankTab.add(item, false);
        return true;
    }

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

        if (!player.getRights().isStaff(PlayerRights.DEVELOPER) && !PlayerUtil.isMember(player) && !player.getGameMode().isUltimate()) {
            DialogueManager.sendStatement(player, "<img=745> You must have the Ruby member rank or higher to use this feature.");
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

    private static void noteItem(final int id, final int amount, final ItemDefinition definition, final Player player) {
        final int amountInInventory = player.getInventory().getAmount(id);
        final int amountToAdd = Math.min(amountInInventory, amount);
        final int notedId = definition.getNoteId();

        if(amountInInventory < amount)
            player.sendMessage("You could only note "+ amountInInventory +" items.");

        player.getInventory().delete(id, amountToAdd);
        player.getInventory().add(notedId, amountToAdd);

        if (AreaManager.inWilderness(player))
            AchievementManager.processFor(AchievementType.COLLECTOR, player);

        player.getPacketSender().sendInterfaceRemoval();
        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(notedId, 200)
                .setText("The bank exchanges your items for banknotes.")
                .start(player);
    }

    private static void unnoteItem(final int id, final int amount, final ItemDefinition definition, final Player player) {
        final int unnotedId = definition.getNoteId();
        final int amountInInventory = player.getInventory().getAmount(id);
        int amountToAdd = Math.min(amountInInventory, amount);

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

        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                .setItem(unnotedId, 200)
                .setText("The bank exchanges your banknotes for items.")
                .start(player);

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

    public static void logAccountLock(String username) {
        StaffLogRelay.INSTANCE.save(StaffLogRelay.StaffLogType.LOCKED, username, "is locked - reason: too many incorrect pin entries");
        Logging.log("locks", username + " has been locked for incorrect bank PIN tries");
    }

    public static boolean contains(Player player, int id) {
        for (Bank banks : player.getBanks()) {
            if (banks == null) {
                continue;
            }
            if (banks.contains(id)) {
                return true;
            }
        }
        return false;
    }
}
