package com.grinder.game.content.skill.skillable.impl.magic;

import com.grinder.game.content.item.charging.impl.*;
import com.grinder.game.content.skill.skillable.impl.runecrafting.CombinationRune;
import com.grinder.game.content.skill.skillable.impl.runecrafting.CraftableRune;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.Entity;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.model.MagicSpellbook;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.game.model.item.container.player.RunePouch;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.oldgrinder.EquipSlot;

import java.util.*;

/**
 * A parent class represented by any generic spell able to be cast by an
 * {@link Entity}.
 *
 * @author lare96
 */
public abstract class Spell {

    /**
     * Determines if this spell is able to be cast by the argued {@link Player}.
     * We do not include {@link NPC}s here since no checks need to be made for
     * them when they cast a spell.
     *
     * @param player the player casting the spell.
     * @param target an optional target (may be null for non-combat spells)
     * @return <code>true</code> if the spell can be cast by the player,
     * <code>false</code> otherwise.
     */
    public boolean canCast(Player player, Agent target, boolean deleteRunes) {
        if (player.BLOCK_ALL_BUT_TALKING)
            return false;
        // We first check the level required.
        if (player.getSkillManager().getCurrentLevel(Skill.MAGIC) < levelRequired()) {
            player.sendMessage("You need a Magic level of " + levelRequired() + " to cast this spell.");
            player.getCombat().reset(false);
            player.getMotion().reset();
            player.getMotion().clearSteps();
            //player.getPacketSender().resetFlag();
            player.setEntityInteraction(null);
            return false;
        }

        Item weapon = player.getEquipment().get(EquipSlot.WEAPON);

        // Handle trident of seas
        if (player.getCombat().getAutocastSpell() == CombatSpellType.TRIDENT_OF_THE_SEAS.getSpell() && spellId() == CombatSpellType.TRIDENT_OF_THE_SEAS.getSpell().spellId()) {
            if (weapon.getId() == ItemID.TRIDENT_OF_THE_SEAS || weapon.getId() == ItemID.TRIDENT_OF_THE_SEAS_FULL_) {
                if (TridentOfSeas.INSTANCE.getCharges(weapon) > 0) {
                    return true;
                } else {
                    player.sendMessage("Your trident has run out of charges.");
                    return false;
                }
            } else if (weapon.getId() == 22288 || weapon.getId() == ItemID.TRIDENT_OF_THE_SEAS_FULL_) {
                if (TridentOfSeasEnchanced.INSTANCE.getCharges(weapon) > 0) {
                    return true;
                } else {
                    player.sendMessage("Your trident has run out of charges.");
                    return false;
                }
            }
        }
        // Handle trident of swamp
        else if (player.getCombat().getAutocastSpell() == CombatSpellType.TRIDENT_OF_THE_SWAMP.getSpell() && spellId() == CombatSpellType.TRIDENT_OF_THE_SWAMP.getSpell().spellId()) {
            if (weapon.getId() == ItemID.TRIDENT_OF_THE_SWAMP) {
                if (TridentOfSwamp.INSTANCE.getCharges(weapon) > 0) {
                    return true;
                } else {
                    player.sendMessage("Your trident has run out of charges.");
                    return false;
                }
            } else if (weapon.getId() == 22292) {
                if (TridentOfSwampEnchanced.INSTANCE.getCharges(weapon) > 0) {
                    return true;
                }
            } else {
                player.sendMessage("Your trident has run out of charges.");
                return false;
            }
        } else if (player.getCombat().getAutocastSpell() == CombatSpellType.SANGUINESTI_STAFF.getSpell() && spellId() == CombatSpellType.SANGUINESTI_STAFF.getSpell().spellId()) { // Custom spell
            int sangCharges = SanguinestiStaff.INSTANCE.getCharges(player.getEquipment().get(EquipSlot.WEAPON));

            if (sangCharges > 0) {
                SanguinestiStaff.INSTANCE.decrementCharges(player, player.getEquipment().get(EquipSlot.WEAPON));
                return true;
            } else {
                player.sendMessage("Your staff has run out of charges.");
                return false;
            }
        } else if (player.getCombat().getAutocastSpell() == CombatSpellType.HOLY_SANGUINESTI_STAFF.getSpell() && spellId() == CombatSpellType.HOLY_SANGUINESTI_STAFF.getSpell().spellId()) { // Custom spell
            int holySangCharges = HolySanguinestiStaff.INSTANCE.getCharges(player.getEquipment().get(EquipSlot.WEAPON));

            if (holySangCharges > 0) {
                HolySanguinestiStaff.INSTANCE.decrementCharges(player, player.getEquipment().get(EquipSlot.WEAPON));
                return true;
            } else {
                player.sendMessage("Your staff has run out of charges.");
                return false;
            }
        } else if (player.getCombat().getAutocastSpell() == CombatSpellType.THAMMARON_SCEPTRE.getSpell() && spellId() == CombatSpellType.THAMMARON_SCEPTRE.getSpell().spellId()) { // Custom spell
            int thamCharges = ThammaronsSceptre.INSTANCE.getCharges(player.getEquipment().get(EquipSlot.WEAPON));

            if (thamCharges > 0) {
                ThammaronsSceptre.INSTANCE.decrementCharges(player, player.getEquipment().get(EquipSlot.WEAPON));
                return true;
            } else {
                player.sendMessage("Your sceptre has run out of charges.");
                return false;
            }
        }
        // Secondly we check if they have proper magic spellbook
        // If not, reset all magic attributes such as current spell
        // Aswell as autocast spell
        if (!player.getSpellbook().equals(getSpellbook())) {
            SpellCasting.setSpellToCastAutomatically(player, null);
            player.getCombat().resetMagicCasting();
            player.getCombat().reset(false);
            player.getMotion().reset();
            player.getMotion().clearSteps();
            //player.getPacketSender().resetFlag();
            player.setEntityInteraction(null);
            return false;
        }

        // Then we check the items required.
        if (itemsRequired(player).isPresent()) {
            // Suppress the runes based on the staff, we then use the new array
            // of items that don't include suppressed runes.
            Item[] items = itemsRequired(player).get();

            items = TomeOfFire.INSTANCE.suppressRunes(player, this, items);
            TomeOfWater.INSTANCE.suppressRunes(player, this, items);
            ElementStaffType.suppressRunes(player, items);

            // Now check if we have all of the runes.
            ItemContainer runesContainer = player.getInventory();
            if (player.getInventory().contains(ItemID.RUNE_POUCH)) {
                runesContainer = Misc.concat(player.getInventory(), player.getRunePouch());
            }

            Optional<Item> missingRune = missingRequiredRunes(items, runesContainer);
            if (missingRune.isPresent()) {
                // We don't, so we can't cast.
                missingRune.ifPresent(missingRuneItem -> {
                    player.sendMessage("You don't have enough " + Misc.capitalizeWords(missingRuneItem.getDefinition().getName()) + "s to cast this spell.");
                });

                player.getCombat().resetMagicCasting();
                player.getCombat().reset(false);
                player.getMotion().clearSteps();
                //player.getPacketSender().resetFlag();
                player.setEntityInteraction(null);
                return false;
            }

            // Finally, we check the equipment required.
            if (!hasEquipmentRequired(player)) {
                player.sendMessage("You do not have the required equipment to cast this spell.");
                player.getCombat().resetMagicCasting();
                player.getCombat().reset(false);
                player.getMotion().clearSteps();
                //player.getPacketSender().resetFlag();
                player.setEntityInteraction(null);
                return false;
            }
            if (deleteRunes) {
                deleteItemsRequired(player);
            }

        }
        return true;
    }

    public void deleteItemsRequired(Player player) {

        itemsRequired(player).ifPresent(requiredItems -> {
            requiredItems = TomeOfFire.INSTANCE.suppressRunes(player, this, requiredItems);
            TomeOfWater.INSTANCE.suppressRunes(player, this, requiredItems);
            ElementStaffType.suppressRunes(player, requiredItems);

            if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == ItemID.STAFF_OF_THE_DEAD || player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == ItemID.TOXIC_STAFF_OF_THE_DEAD
                    || player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 24144 || player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == ItemID.STAFF_OF_LIGHT) {
                if (Misc.randomChance(12.5F)) {
                    player.sendMessage("Your " + ItemDefinition.forId(player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId()).getName() +" negated your runes for this cast.");
                    return;
                }
            }

            final List<Item> requiredRunes = new ArrayList<>();
            Collections.addAll(requiredRunes, requiredItems);
            final Map<CraftableRune, Integer> requiredRuneCount = new HashMap<>();
            final Set<CombinationRune> combinationRuneSet = new HashSet<>();
            final List<Item> nonRuneRequiredItems = new ArrayList<>();
            for (Item item : requiredRunes) {
                if (item != null) {
                    final int requiredAmount = item.getAmount();
                    final CraftableRune craftableRune = CraftableRune.Companion.getRune(item.getId());
                    if (craftableRune != null) {
                        requiredRuneCount.compute(craftableRune, (rune, integer) -> integer == null ? requiredAmount : integer + requiredAmount);
                        final List<CombinationRune> combinationRunes = CombinationRune.Companion.getCombinationAlternativeTo(item.getId());
                        if (!combinationRunes.isEmpty())
                            combinationRuneSet.addAll(combinationRunes);
                    } else {
                        nonRuneRequiredItems.add(item);
                    }
                }
            }

            final Inventory inventory = player.getInventory();
            final RunePouch runePouch = player.getRunePouch();

            for (CombinationRune combinationRune : combinationRuneSet) {
                final int inInventory = inventory.getAmount(combinationRune.getItemId());
                final int inRunePouch = runePouch.getAmount(combinationRune.getItemId());
                final int total = inInventory + inRunePouch;
                if (total > 0) {
                    final int firstRequiredAmount = requiredRuneCount.getOrDefault(combinationRune.getFirst(), 0);
                    final int secondRequiredAMount = requiredRuneCount.getOrDefault(combinationRune.getSecond(), 0);
                    final int maxRequiredAmount = Math.max(firstRequiredAmount, secondRequiredAMount);
                    final int totalToRemove = Math.min(total, maxRequiredAmount);
                    if (totalToRemove > 0) {
                        requiredRuneCount.computeIfPresent(combinationRune.getFirst(), (rune, amount) -> amount - Math.min(amount, totalToRemove));
                        requiredRuneCount.computeIfPresent(combinationRune.getSecond(), (rune, amount) -> amount - Math.min(amount, totalToRemove));
                        removeItem(inventory, runePouch, inInventory, inRunePouch, totalToRemove, combinationRune.getItemId());
                    }
                }
            }

            for (Map.Entry<CraftableRune, Integer> entry : requiredRuneCount.entrySet()) {
                final CraftableRune rune = entry.getKey();
                final int amount = entry.getValue();
                if (amount > 0) {
                    final int inInventory = inventory.getAmount(rune.getItemId());
                    final int inRunePouch = runePouch.getAmount(rune.getItemId());
                    final int totalToRemove = Math.min(inInventory + inRunePouch, amount);
                    if (totalToRemove > 0) {
                        requiredRuneCount.computeIfPresent(rune, (_rune, _amount) -> _amount - totalToRemove);
                        removeItem(inventory, runePouch, inInventory, inRunePouch, totalToRemove, rune.getItemId());
                    }
                }
            }

            for (Item item : nonRuneRequiredItems)
                inventory.delete(item, false);

            inventory.refreshItems();
            runePouch.refreshItems();
        });
    }

    private void removeItem(Inventory inventory, RunePouch runePouch, int inInventory, int inRunePouch, int totalToRemove, int itemId) {
        int remainderToRemove = totalToRemove;
        if (inRunePouch > 0) {
            final int toRemoveFromPouch = Math.min(inRunePouch, remainderToRemove);
            runePouch.delete(itemId, toRemoveFromPouch, false);
            remainderToRemove -= toRemoveFromPouch;
        }
        if (remainderToRemove > 0) {
            if (inInventory > 0) {
                final int toRemoveFromInventory = Math.min(inInventory, remainderToRemove);
                inventory.delete(itemId, toRemoveFromInventory, false);
            }
        }
    }

    public boolean hasEquipmentRequired(final Player player) {
        if (equipmentRequired(player).isPresent()) {
            return player.getEquipment().containsAny(equipmentRequired(player).get());
        }
        return true;
    }


    public abstract int spellId();

    /**
     * The level required to cast this spell.
     *
     * @return the level required to cast this spell.
     */
    public abstract int levelRequired();

    /**
     * The base experience given when this spell is cast.
     *
     * @return the base experience given when this spell is cast.
     */
    public abstract int baseExperience();

    /**
     * The items required to cast this spell.
     *
     * @param player the player's inventory to check for these items.
     * @return the items required to cast this spell, or <code>null</code> if
     * there are no items required.
     */
    public abstract Optional<Item[]> itemsRequired(Player player);

    /**
     * The equipment required to cast this spell.
     *
     * @param player the player's equipment to check for these items.
     * @return the equipment required to cast this spell, or <code>null</code>
     * if there is no equipment required.
     */
    public abstract Optional<Item[]> equipmentRequired(Player player);

    /**
     * The method invoked when the spell is cast.
     *
     * @param cast   the entity casting the spell.
     * @param castOn the target of the spell.
     */
    public abstract void startCast(Agent cast, Agent castOn);

    /**
     * Returns the spellbook in which this spell is.
     *
     * @return
     */
    public MagicSpellbook getSpellbook() {
        return MagicSpellbook.NORMAL;
    }

    public static Optional<Item> missingRequiredRunes(Item[] required, ItemContainer finalRunesContainer) {
        return Arrays.stream(required)
                .filter(item -> {
                    if (item == null) return false;

                    List<CombinationRune> combinationRunes = CombinationRune.Companion.getCombinationAlternativeTo(item.getId());

                    if (combinationRunes.isEmpty()) {
                        return !finalRunesContainer.contains(item);
                    } else {
                        return !finalRunesContainer.contains(item)
                                && combinationRunes.stream().noneMatch(it -> finalRunesContainer.contains(new Item(it.getItemId(), item.getAmount())));
                    }
                }).findFirst();
    }
}
