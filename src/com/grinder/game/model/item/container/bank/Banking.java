package com.grinder.game.model.item.container.bank;

import com.grinder.game.content.item.LootingBag;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.model.interfaces.syntax.impl.BankQuantityX;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.player.Equipment;
import com.grinder.net.packet.impl.EquipPacketListener;
import com.grinder.util.Logging;

import java.util.ArrayList;

import static com.grinder.util.ItemID.BLOOD_MONEY;
import static com.grinder.util.ItemID.COINS;

public final class Banking {

    /**
     * Withdraws an item from the bank.
     */
    public static void withdraw(Player player, int item, int slot, int amount, int fromBankTab) {

        if (player.getStatus() == PlayerStatus.BANKING && player.getInterfaceId() == BankConstants.INTERFACE_ID) {

            if (player.getGameMode().isUltimate()) {
                player.getPacketSender().sendMessage("You can't withdraw items as an ultimate ironman.", 1000);
                return;
            }

            if (player.getMinigame() != null) {
                Logging.log("withdrawbankdupe", "" + player.getUsername() + " tried to withdraw item while inside a minigame " + player.getMinigame() +" " + player.getPosition() +"");
                PlayerUtil.broadcastPlayerDeveloperMessage("" + player.getUsername() + " tried to withdraw item while inside a minigame " + player.getMinigame() +" " + player.getPosition() +"");
                return;
            }

            final Bank bank = player.getBank(fromBankTab);

            if(!bank.containsAtSlot(slot, item))
                return;

            final Item itemAtSlot = bank.atSlot(slot);

            if(itemAtSlot == null)
                return;

            if(itemAtSlot.getId() != item)
                return;

            final int max_amount = itemAtSlot.getAmount();

            if (amount == -1 || amount > max_amount)
                amount = max_amount;

            // Delete placeholder
            if (amount <= 0) {
                itemAtSlot.setId(-1);
                player.getBank(player.getCurrentBankTab()).refreshItems();
                return;
            }

            final Item itemInstance = itemAtSlot.clone();
            itemInstance.setAmount(amount);

            bank.moveItemFromSlot(player.getInventory(), itemInstance, slot, false, false);

            if (slot == 0)
                reconfigureTabs(player);

            // Refresh items in our current tab
            player.getBank(player.getCurrentBankTab()).refreshItems();
            player.getInventory().refreshItems();
        }
    }

    /**
     * Deposits an item to the bank.
     */
    public static void depositFromInventory(Player player, int itemId, int slot, int amount, boolean ignoreReqs) {
        depositFromItemContainer(player, player.getInventory(), itemId, slot, amount, ignoreReqs);
    }

    public static void depositFromItemContainer(Player player, ItemContainer container, int itemId, int slot, int amount, boolean ignoreReqs) {

        if (ignoreReqs || player.getStatus() == PlayerStatus.BANKING && player.getInterfaceId() == BankConstants.INTERFACE_ID || player.getInterfaceId() == BankConstants.DEPOSIT_BOX_INTERFACE_ID) {

            final Item itemInstance = container.atSlot(slot).clone();

            if (itemInstance.getId() != itemId)
                return;

            if (player.getGameMode().isUltimate()) {
                player.getPacketSender().sendMessage("You can't deposit items as an Ultimate Iron Man.", 1000);
                return;
            }

            if (player.getMinigame() != null) {
                Logging.log("depositbankdupe", "" + player.getUsername() + " tried to deposit item while inside a minigame " + player.getMinigame() +" " + player.getPosition() +"");
                PlayerUtil.broadcastPlayerDeveloperMessage("" + player.getUsername() + " tried to deposit item while inside a minigame " + player.getMinigame() +" " + player.getPosition() +"");
                return;
            }

            if (itemInstance.getDefinition().isStackable()) {
                if (amount == -1 || amount > container.getAmountForSlot(slot))
                    amount = container.getAmountForSlot(slot);
            } else {
                if (amount == -1 || amount > container.getAmount(itemInstance))
                    amount = container.getAmount(itemInstance);
            }

            if (amount <= 0)
                return;

            final int tab = getTabContainingItemOrDefault(player, itemInstance);
            final Bank bank = player.getBank(tab);
            final int amountInBank = bank.getAmount(itemInstance);

            if ((long) amountInBank + (long) (amount) > Integer.MAX_VALUE) {
                amount = Integer.MAX_VALUE - amountInBank;
                player.sendMessage("Your bank is too full to hold any more " + ItemDefinition.forId(itemId).getName().toLowerCase() +".", 1000);
            }

            if (amount <= 0)
                return;

            itemInstance.setAmount(amount);

            container.moveItemFromSlot(bank, itemInstance, slot, false, true);
            container.refreshItems();
        }
    }

    /**
     * Moves an item from one slot to another using the insert method. It will
     * shift all other items to the right.
     */
    public static void rearrange(Player player, Bank bank, int fromSlot, int toSlot, boolean refresh) {
        if (player.insertMode() && bank.getItems()[toSlot].getId() != -1) { // If inserting into empty slot, just use the swap method

            int tempFrom = fromSlot;

            while (tempFrom != toSlot) {
                if (tempFrom > toSlot) {
                    bank.swap(tempFrom, tempFrom - 1);
                    tempFrom--;
                } else {
                    bank.swap(tempFrom, tempFrom + 1);
                    tempFrom++;
                }
            }

        } else {
            bank.swap(fromSlot, toSlot);
        }

        if(refresh) {
            if(player.getCurrentBankTab() == 0) {
                player.getBank(0).refreshItems();
            } else {
                bank.refreshItems();
            }
        }

        // Update all tabs if we moved an item from/to the first item slot
        if (fromSlot == 0 || toSlot == 0) {
            reconfigureTabs(player);
        }
    }

    /**
     * Moves a bank tab from one slot to another using the insert method.
     * It will shift all other bank tabs to the right.
     */
    public static void rearrangeBankTabs(Player player, int fromTab, int toTab, boolean refresh) {
        // Can not rearrange with first tab or same tab or empty tab
        if (fromTab < 1 || fromTab >= BankConstants.TOTAL_BANK_TABS || toTab < 1 || toTab >= BankConstants.TOTAL_BANK_TABS
                || fromTab == toTab || player.getBank(fromTab).isEmpty() || player.getBank(toTab).isEmpty())
            return;

        if(player.insertMode()) {

            int tempFrom = fromTab;

            while (tempFrom != toTab) {
                if(tempFrom > toTab) {
                    swapBankTabs(player, tempFrom, tempFrom - 1);
                    //bank.swap(tempFrom, tempFrom - 1);
                    tempFrom--;
                } else {
                    swapBankTabs(player, tempFrom, tempFrom + 1);
                    //bank.swap(tempFrom, tempFrom + 1);
                    tempFrom++;
                }
            }

        } else {
            swapBankTabs(player, fromTab, toTab);
        }

        if(refresh) {
            player.getBank(player.getCurrentBankTab()).refreshItems();
        }
    }

    /**
     * Swaps a player's two bank tabs.
     */
    private static void swapBankTabs(Player player, int fromTab, int toTab) {
        Bank temporaryBank = player.getBank(fromTab);
        if (temporaryBank == null)
            return;
        player.setBank(fromTab, player.getBank(toTab));
        player.setBank(toTab, temporaryBank);
    }

    /**
     * Handles a button pressed in the bank interface.
     */
    public static boolean handleButton(Player player, int button, int action) {

        if (player.getInterfaceId() == BankConstants.INTERFACE_ID) {
            if (player.getStatus() == PlayerStatus.BANKING) {
                switch (button) {
                    case 5386:
                        player.setNoteWithdrawal(true);
                        break;
                    case 5387:
                        player.setNoteWithdrawal(false);
                        break;

                    case 8130:
                        player.setInsertMode(false);
                        break;
                    case 8131:
                        player.setInsertMode(true);

                        break;

                    case 50010:
                    case 50011:
                    case 50012:
                    case 50013:
                    case 50014:
                    case 50015:
                    case 50016:
                    case 50017:
                    case 50018:
                    case 50019:
                        // Tabs
                        int tab = button - 50010;
                        if (player.getBank(tab).isEmpty()) {
                            player.getPacketSender().sendMessage("To create a new tab, drag items from your bank onto this tab.", 1200);
                        } else {
                            switch(action) {
                                case 0:
                                    player.setCurrentBankTab(tab);
                                    break;
                                case 1:
                                    collapseTab(player, tab);
                                    break;
                                case 2:
                                    removePlaceholdersTab(player, tab);
                                    break;
                            }
                        }
                        break;

                    case 50072:
                        player.setEnterSyntax(new BankQuantityX());
                        player.getPacketSender().sendEnterAmountPrompt("Enter amount:");
                        break;

                    case 50073:
                    case 50074:
                    case 50075:
                    case 50076:
                    case 50077:
                        // Custom quantity
                        player.setBankQuantityConfig(button - 50073);
                        break;

                    case 50082:
                        // Always set placeholders
                        player.setPlaceholders(!player.hasPlaceHoldersEnabled());
                        break;

                    case 50086:
                        // Deposit inventory
                        LootingBag.deposit(player);
                        depositItems(player, player.getInventory(), false);
                        break;

                    case 50089:
                        // Deposit equipment
                        depositItems(player, player.getEquipment(), false);
                        break;

                    case 50104:
                    case 50105:
                    case 50106:
                    case 50107:
                        // Tab display
                        player.setTabDisplayConfig(button - 50104);
                        break;

                    case 50109:
                        // Fixed bank width
                        player.setFixedBankWidth(!player.isFixedBankWidth());
                        break;

                    case 50110:
                        // Deposit worn items button
                        player.setShowDepositWornItems(!player.showDepositWornItems());
                        break;

                    case 50111:
                        // Release all placeholders button
                        if (getPlaceholdersAmount(player) > 0) {
                            removePlaceholdersAllTabs(player);
                        } else {
                            player.getPacketSender().sendMessage("You don't have any placeholders to release.", 1000);
                        }
                        break;
                }
            }
            return true;
        }

        return false;
    }

    /**
     * Deposits items from another container into the bank. Used for depositing
     * inventory/equipment.
     */
    public static void depositItems(Player player, ItemContainer from, boolean ignoreReqs) {
        if (player.getGameMode().isUltimate()) {
            return;
        }
        if (player.getMinigame() != null) {
            Logging.log("depositbankdupe2", "" + player.getUsername() + " tried to deposit2 item while inside a minigame " + player.getMinigame() +" " + player.getPosition() +"");
            PlayerUtil.broadcastPlayerDeveloperMessage("" + player.getUsername() + " tried to deposit2 item while inside a minigame " + player.getMinigame() +" " + player.getPosition() +"");
            return;
        }
        if (!ignoreReqs) {
            if (player.getStatus() != PlayerStatus.BANKING || player.getInterfaceId() != BankConstants.INTERFACE_ID) {
                return;
            }
        }
        for (Item item : from.getValidItems()) {
            if (item.getId() == COINS || item.getId() == BLOOD_MONEY || item.getAmount() >= 10_000_000) {
                depositFromItemContainer(player, from, item.getId(), from.getSlot(item), item.getAmount(), ignoreReqs);
            } else {
                from.moveItemFromSlot(player.getBank(getTabContainingItemOrDefault(player, item)), item.clone(),
                        from.getSlot(item), false, false);
            }
        }

        from.refreshItems();

        player.getBank(player.getCurrentBankTab()).refreshItems();

        if (from instanceof Equipment) {
            EquipPacketListener.resetWeapon(player);
            player.getCombat().reset(false);
            EquipmentBonuses.update(player);
            player.getEquipment().refreshItems();
            WeaponInterfaces.INSTANCE.assign(player);
            player.updateAppearance();
        }
    }

    /**
     * Is a bank empty?
     */
    public static boolean isEmpty(Bank bank) {
        return bank.getValidItems().size() <= 0;
    }

    /**
     * Reconfigures our bank tabs
     */
    public static boolean reconfigureTabs(Player player) {

        boolean updateRequired = false;
        for (int k = 0; k < BankConstants.TOTAL_BANK_TABS - 1; k++) {
            if (isEmpty(player.getBank(k)) || updateRequired) {
                player.setBank(k, player.getBank(k + 1));
                player.setBank(k + 1, new Bank(player));
                updateRequired = true;
            }
        }

        // Check if we're in a tab that's empty
        // If so, open the next non-empty tab
        int total_tabs = getTabCount(player);
        if (player.getCurrentBankTab() > total_tabs) {
            player.setCurrentBankTab(total_tabs);
            player.getBank(total_tabs).open();
            return true;
        }

        return false;
    }

    /**
     * Gets the amount of filled tabs we have.
     */
    public static int getTabCount(Player player) {
        int tabs = 0;
        for (int i = 1; i < BankConstants.TOTAL_BANK_TABS; i++) {
            if (!isEmpty(player.getBank(i))) {
                tabs++;
            } else
                break;
        }
        return tabs;
    }

    /**
     * Gets the specific tab in which an item is.
     */
    public static int getTabContainingItemOrDefault(Player player, Item item) {

        if(item.hasAttributes()) {
            for (int tab = 0; tab < BankConstants.TOTAL_BANK_TABS; tab++) {

                final Bank bank = player.getBank(tab);
                final int slot = bank.getSlot(item);

                if (slot >= 0)
                    return tab;
            }
            return findTabWithFreeSlot(player);
        }

        return getTabContainingItemOrDefault(player, item.getId());
    }

    public static int getTabContainingItemOrDefault(Player player, int itemID) {
        if (ItemDefinition.forId(itemID).isNoted()) {
            itemID = ItemDefinition.forId(itemID).unNote();
        }
        for (int k = 0; k < BankConstants.TOTAL_BANK_TABS; k++) {
            if (player.getBank(k).contains(itemID)) {
                return k;
            }
        }

        // Find empty bank slot
        return findTabWithFreeSlot(player);
    }

    private static int findTabWithFreeSlot(Player player) {
        if (player.getBank(player.getCurrentBankTab()).countFreeSlots() > 0) {
            return player.getCurrentBankTab();
        }
        for (int k = 0; k < BankConstants.TOTAL_BANK_TABS; k++) {
            if (player.getBank(k).countFreeSlots() > 0) {
                return k;
            }
        }
        return 0;
    }

    /**
     * Get's the last filled tab in a player's bank.
     * Collapsing tab 0 will collapse all tabs.
     */
    public static int getLastTab(Player player) {
        int last = 0;

        for (int k = 0; k < BankConstants.TOTAL_BANK_TABS; k++) {
            if (!player.getBank(k).isEmpty()) {
                last = k;
            }
        }

        return last;
    }

    /**
     * Collapses a tab in a player's bank.
     * Collapsing tab 0 will collapse all tabs.
     * @param player	The player.
     * @param tab 		The tab.
     */
    public static void collapseTab(Player player, int tab) {
        // Cannot collapse empty tab
        if(player.getBank(tab).isEmpty()) {
            return;
        }

        if (tab == 0) {
            // Do it backwards as the tabs reconfigure
            for (int i = BankConstants.TOTAL_BANK_TABS - 1; i >= 1; i--) {
                collapseTab(player, i);
            }
        } else {
            // Collapse tab
            ArrayList<Item> items = player.getBank(tab).getValidItems();

            // Check if main tab has space
            if(player.getBank(0).countFreeSlots() < items.size()) {
                player.getPacketSender().sendMessage("You don't have enough free slots in your main tab to do that.", 600);
                return;
            }

            // Temporarily disabled note withdrawal...
            final boolean noteWithdrawal = player.withdrawAsNote();
            player.setNoteWithdrawal(false);

            // Move items from tab to main tab
            for (Item item : items) {
                player.getBank(tab).moveItemFromSlot(player.getBank(0), item.clone(), player.getBank(tab).getSlot(item), false, false);
            }

            // Reactivate note withdrawal if it was active
            player.setNoteWithdrawal(noteWithdrawal);

            // Collapsing current tab
            if(tab == player.getCurrentBankTab()) {
                player.setCurrentBankTab(0);
                player.getPacketSender().sendCurrentBankTab(player.getCurrentBankTab());
                player.getBank(player.getCurrentBankTab()).refreshItems();
            }
        }

        // Update tabs
        reconfigureTabs(player);

        // Update
        player.getBank(player.getCurrentBankTab()).refreshItems();
    }

    /**
     * Removes placeholders from a tab in a player's bank.
     * @param player	The player.
     * @param tab 		The tab.
     */
    public static void removePlaceholdersTab(Player player, int tab) {
        // Cannot remove placeholders from empty tab
        if(player.getBank(tab).isEmpty()) {
            return;
        }

        for (int slot = 0; slot < player.getBank(tab).capacity(); slot++) {
            Item item = player.getBank(tab).getItems()[slot];
            if (item != null && item.getId() > 0 && item.getAmount() <= 0) {
                player.getBank(tab).getItems()[slot].setId(-1);
            }
        }
        player.getBank(player.getCurrentBankTab()).refreshItems();
    }

    /**
     * Removes placeholders from every tab in a player's bank.
     * @param player	The player.
     */
    public static void removePlaceholdersAllTabs(Player player) {
        // Do it backwards as the tabs reconfigure
        for (int tab = BankConstants.TOTAL_BANK_TABS - 1; tab >= 0; tab--) {
            removePlaceholdersTab(player, tab);
        }
    }

    /**
     * Gets the amount of placeholders in a player's bank.
     * @param player	The player.
     * @return			The amount of placeholders.
     */
    public static int getPlaceholdersAmount(Player player) {
        int amount = 0;
        for (int i = 0; i < BankConstants.TOTAL_BANK_TABS; i++) {
            for (Item item : player.getBank(i).getItems()) {
                if (item != null && item.getId() > 0 && item.getAmount() <= 0) {
                    amount++;
                }
            }
        }
        return amount;
    }

    /**
     * Wipes all containers in a player's bank.
     * @param player	The player.
     */
    public static void wipe(Player player) {
        for (int i = BankConstants.TOTAL_BANK_TABS - 1; i >= 0; i--) {
            player.getBank(i).resetItems().refreshItems();
        }
    }

    public static void openDepositBox(Player player) {
        player.getPacketSender().sendInterface(BankConstants.DEPOSIT_BOX_INTERFACE_ID);
        player.getPacketSender().sendItemContainer(player.getInventory(), BankConstants.DEPOSIT_BOX_ITEM_CONTAINER_ID);
    }
}
