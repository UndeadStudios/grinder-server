package com.grinder.game.content.skill.skillable.impl.herblore;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.game.model.sound.Sounds;
import com.grinder.util.ItemID;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles the decanting of potions.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-25
 */
public final class PotionDecanting {

    /**
     * Attempts to decant two potions into one.
     */
    public static boolean decant(Player player, Item first, Item second, int firstSlot, int secondSlot) {

        final Optional<PotionDosageType> firstPotion = PotionDosageType.forId(first.getId());
        final Optional<PotionDosageType> secondPotion = PotionDosageType.forId(second.getId());

        final boolean firstIsVial = first.getId() == ItemID.EMPTY_VIAL;
        final boolean secondIsVial = second.getId() == ItemID.EMPTY_VIAL;

        if (firstIsVial || secondIsVial) {

            final Optional<PotionDosageType> optionalPotionDosageType = firstIsVial ? secondPotion : firstPotion;

            if (optionalPotionDosageType.isPresent()) {

                final PotionDosageType dosageType = optionalPotionDosageType.get();
                final int doses = dosageType.getDoseForID((firstIsVial ? second : first).getId());
                final int half = doses / 2;
                final int remainder = doses - half;

                if (half > 0) {
                    final Inventory inventory = player.getInventory();
                    inventory.set(firstSlot, new Item(dosageType.getIDForDose(firstIsVial ? half : remainder)));
                    inventory.set(secondSlot, new Item(dosageType.getIDForDose(firstIsVial ? remainder : half)));
                    inventory.refreshItems();
                    PlayerExtKt.message(player, "You divide the liquid between the vessels.");
                    return true;
                }
            }
        }

        /*
         * Combine
         */
        if (firstPotion.isPresent() && secondPotion.isPresent()) {

            final PotionDosageType firstPotionType = firstPotion.get();
            final PotionDosageType secondPotionType = secondPotion.get();

            if(firstPotionType != secondPotionType)
                return false;

            final int maxDoses = 4;
            final int firstDoses = firstPotionType.getDoseForID(first.getId());
            final int secondDoses = secondPotionType.getDoseForID(second.getId());

            if (firstDoses > 0 && firstDoses < maxDoses && secondDoses > 0 && secondDoses < maxDoses) {
                final int totalDoses = firstDoses + secondDoses;
                final int newSecondDoses = Math.min(totalDoses, maxDoses);
                final int newFirstDoses = totalDoses - newSecondDoses;
                final Inventory inventory = player.getInventory();
                inventory.set(firstSlot, new Item(secondPotion.get().getIDForDose(newFirstDoses)));
                inventory.set(secondSlot, new Item(secondPotion.get().getIDForDose(newSecondDoses)));
                inventory.refreshItems();
                PlayerExtKt.playSound(player, Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND, 0, 1);
                PlayerExtKt.message(player, "You have combined the liquid into " + newSecondDoses + " doses.");
                return true;
            }
        }

        return false;
    }

    /**
     * Attempts to decant all potions in a player's inventory to be a specified dosage.
     */
    public static void decantInventory(Player player, int doses, boolean dialogue) {
        final ArrayList<Item> unnoted = getPotionsToDecant(player, doses, false);
        final ArrayList<Item> noted = getPotionsToDecant(player, doses, true);
        final ArrayList<Item> newUnnoted = getPotionsAfterDecanting(doses, unnoted);
        final ArrayList<Item> newNoted = getPotionsAfterDecanting(doses, noted);

        boolean newUnnotedOverflowed = newUnnoted == null;
        boolean newNotedOverflowed = newNoted == null;
        if (newUnnotedOverflowed || newNotedOverflowed) {
            if (dialogue)
                DialogueManager.start(player, 2645); // You are trying to decant too many potions at once!
            return;
        }

        final PotionDecantOutcome unnotedOutcome = decantPotions(player, unnoted, newUnnoted, false);
        final PotionDecantOutcome notedOutcome = decantPotions(player, noted, newNoted, true);

        if (dialogue) {
            // No potions found in either
            if (unnotedOutcome == PotionDecantOutcome.NO_POTS_FOUND && notedOutcome == PotionDecantOutcome.NO_POTS_FOUND) {
                DialogueManager.start(player, 2646); // I don't think you've got anything that I can decant.
                return;
            }

            // Not enough inventory space
            if ((unnotedOutcome == PotionDecantOutcome.NO_INV_SPACE && notedOutcome == PotionDecantOutcome.NO_POTS_FOUND) || (unnotedOutcome == PotionDecantOutcome.NO_POTS_FOUND && notedOutcome == PotionDecantOutcome.NO_INV_SPACE) || (unnotedOutcome == PotionDecantOutcome.NO_INV_SPACE && notedOutcome == PotionDecantOutcome.NO_INV_SPACE)) {
                DialogueManager.start(player, 2647); // You're a bit short of inventory space.
                return;
            }

            // Can't make purchase
            if ((unnotedOutcome == PotionDecantOutcome.CANT_PAY && notedOutcome == PotionDecantOutcome.NO_POTS_FOUND) || (unnotedOutcome == PotionDecantOutcome.NO_POTS_FOUND && notedOutcome == PotionDecantOutcome.CANT_PAY) || (unnotedOutcome == PotionDecantOutcome.CANT_PAY && notedOutcome == PotionDecantOutcome.CANT_PAY)) {
                DialogueManager.start(player, 2648); // You're a bit short of empty vessels or cash. Empty vials
                return;
            }

            // Only decanted one due to lack of payment for the other
            if ((unnotedOutcome == PotionDecantOutcome.CANT_PAY && notedOutcome == PotionDecantOutcome.SUCCESS) || (unnotedOutcome == PotionDecantOutcome.SUCCESS && notedOutcome == PotionDecantOutcome.CANT_PAY)) {
                DialogueManager.start(player, 2649); // There, I've done what I can, but you're a bit short of
                player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                AchievementManager.processFor(AchievementType.DECANTER, player);
                return;
            }

            // Decanted
            if ((unnotedOutcome == PotionDecantOutcome.SUCCESS && notedOutcome == PotionDecantOutcome.NO_POTS_FOUND) || (unnotedOutcome == PotionDecantOutcome.NO_POTS_FOUND && notedOutcome == PotionDecantOutcome.SUCCESS) || (unnotedOutcome == PotionDecantOutcome.SUCCESS && notedOutcome == PotionDecantOutcome.SUCCESS)) {
                DialogueManager.start(player, 2650); // There, all done.
                player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                AchievementManager.processFor(AchievementType.DECANTER, player);
            }
        }
    }

    /**
     * Finds all potions in a player's inventory
     */
    public static ArrayList<Item> getPotionsToDecant(Player player, int doses, boolean isNoted) {
        final ArrayList<Item> items = new ArrayList<>();

        for (final Item item : player.getInventory().getValidItems()) {

            int id = item.getId();

            if (isNoted) {
                int unnotedId = item.getDefinition().unNote();
                if (unnotedId == item.getId()) {
                    continue;
                }
                id = unnotedId;
            }

            Optional<PotionDosageType> potion = PotionDosageType.forId(id);

            if (potion.isPresent() && potion.get().getDoseForID(id) != doses) {
                items.add(new Item(id, item.getAmount()));
            }
        }
        return items;
    }

    /**
     * Decants potions in an item array of potions to all be of a given dosage
     */
    public static ArrayList<Item> getPotionsAfterDecanting(int doses, ArrayList<Item> oldItems) {
        ArrayList<Item> newItems = new ArrayList<>();

        // Find every type of potion in the item array
        List<Optional<PotionDosageType>> potions = new ArrayList<>();
        for (Item item : oldItems) {
            Optional<PotionDosageType> potion = PotionDosageType.forId(item.getId());
            if (potion.isPresent() && !potions.contains(potion)) {
                potions.add(potion);
            }
        }

        // Decant each potion type
        for (Optional<PotionDosageType> potion : potions) {
            int totalItems = 0;
            long totalDoses = 0;

            for (Item item : oldItems) {
                Optional<PotionDosageType> notedPotion2 = PotionDosageType.forId(item.getId());
                if (potion.equals(notedPotion2)) {
                    totalItems += item.getAmount();
                    totalDoses += (long) notedPotion2.get().getDoseForID(item.getId()) * item.getAmount();
                }
            }

            if (totalDoses > Integer.MAX_VALUE) {
                return null;
            }

            int id;
            if (totalDoses > doses || totalItems != 1) {
                // Decant
                int potionsOfDosageToAdd = (int) totalDoses / doses;
                id = potion.get().getIDForDose(doses);
                newItems.add(new Item(id, potionsOfDosageToAdd));
                int addedDoses = potionsOfDosageToAdd * doses;
                if (addedDoses < totalDoses) {
                    int remainingDoses = (int) totalDoses - addedDoses;
                    id = potion.get().getIDForDose(remainingDoses);
                    newItems.add(new Item(id));
                    //addedDoses += remainingDoses;
                }
            } else {
                // Already the correct dosage, don't decant
                id = potion.get().getIDForDose((int) totalDoses);
                oldItems.remove(new Item(id));
            }
        }
        return newItems;
    }

    /**
     * Handles the final part of decantation
     */
    public static PotionDecantOutcome decantPotions(Player player, ArrayList<Item> preDecantPotions, ArrayList<Item> postDecantPotions, boolean isNoted) {

        if (preDecantPotions.isEmpty() || postDecantPotions.isEmpty())
            return PotionDecantOutcome.NO_POTS_FOUND;

        final int preDecantPotionsCount = preDecantPotions.stream().mapToInt(Item::getAmount).sum();
        final int postDecantPotionsCount = postDecantPotions.stream().mapToInt(Item::getAmount).sum();
        final int deltaPotionsCount = preDecantPotionsCount - postDecantPotionsCount;
        final boolean returnEmptyVials = deltaPotionsCount > 0;
        final boolean requirePayment = deltaPotionsCount < 0;

        final Inventory inventory = player.getInventory();
        final int freeSlots = inventory.countFreeSlots();
        int slotsNeeded = (isNoted ? postDecantPotions.size() : postDecantPotionsCount)
                + (returnEmptyVials && !inventory.contains(ItemID.EMPTY_VIAL_NOTED) ? 1 : 0)
                - preDecantPotions.size();
        boolean returnAsNoted = isNoted;
        if (freeSlots < slotsNeeded) {
            if (!isNoted) {
                // Couldn't fit unnoted items, attempt to fit them in the inventory by noting them
                returnAsNoted = true;
                slotsNeeded = postDecantPotions.size() + (returnEmptyVials && !inventory.contains(ItemID.EMPTY_VIAL_NOTED) ? 1 : 0) - preDecantPotions.size();
                if (freeSlots < slotsNeeded)
                    return PotionDecantOutcome.NO_INV_SPACE;
            } else
                return PotionDecantOutcome.NO_INV_SPACE;
        }

        // Pay for vials if needed
        if (requirePayment) {

            final int emptyVialCount = inventory.getAmount(ItemID.EMPTY_VIAL);
            final int notedEmptyVialCount = inventory.getAmount(ItemID.EMPTY_VIAL_NOTED);
            final int coinsAmount = inventory.getAmount(ItemID.COINS);
            final boolean canMakePayment = emptyVialCount
                    + notedEmptyVialCount
                    + (coinsAmount / 5L)
                    >= -deltaPotionsCount;

            if (!canMakePayment)
                return PotionDecantOutcome.CANT_PAY;

            int amount;
            int amountPayed = 0;

            if (emptyVialCount > 0 && amountPayed < -deltaPotionsCount) {
                amount = Math.min(-deltaPotionsCount - amountPayed, emptyVialCount);
                inventory.delete(ItemID.EMPTY_VIAL, amount, false);
                amountPayed += amount;
            }
            if (notedEmptyVialCount > 0 && amountPayed < -deltaPotionsCount) {
                amount = Math.min(-deltaPotionsCount - amountPayed, notedEmptyVialCount);
                inventory.delete(ItemID.EMPTY_VIAL_NOTED, amount, false);
                amountPayed += amount;
            }
            if (coinsAmount > 0 && amountPayed < -deltaPotionsCount) {
                amount = Math.min(-deltaPotionsCount - amountPayed, coinsAmount / 5);
                inventory.delete(ItemID.COINS, amount * 5, false);
            }
        }

        for (Item item : preDecantPotions)
            inventory.delete(isNoted ? new Item(item.getDefinition().getNoteId(), item.getAmount()) : item, false);

        for (Item item : postDecantPotions) {
            if (returnAsNoted) {
                inventory.add(new Item(item.getDefinition().getNoteId(), item.getAmount()), false);
            } else {
                for (int i = 0; i < item.getAmount(); i++)
                    inventory.add(new Item(item.getId()), false);
            }
        }

        // Give player back any vials they lost
        if (returnEmptyVials)
            inventory.add(new Item(ItemID.EMPTY_VIAL_NOTED, deltaPotionsCount), false);

        inventory.refreshItems();

        return PotionDecantOutcome.SUCCESS;
    }
}
