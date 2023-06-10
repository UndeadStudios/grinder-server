package com.grinder.game.entity.agent.npc.monster.boss;

import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.item.container.player.Equipment;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.util.Misc;
import com.grinder.util.oldgrinder.Area;

/**
 * Helper class for writing {@link BossAttack boss attacks}.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-19
 */
public final class BossAttackUtil {

    /**
     * Teleports the argued {@link Player} to an open position within the provided {@link Area}.
     *
     * @param player the {@link Player} to be teleported.
     * @param area   the {@link Area} to teleport the player in.
     */
    public static void teleport(final Player player, final Area area){
        teleport(player, area.findOpenPositions(player.getPlane()).toArray(Position[]::new));
    }

    /**
     * Teleports the argued {@link Player} to one of the provided {@link Position positions}.
     *
     * @param player            the {@link Player} to be teleported.
     * @param possiblePositions array of {@link Position positions} that the player can teleport in.
     */
    public static void teleport(final Player player, final Position... possiblePositions){
        player.moveTo(Misc.random(possiblePositions));
    }

    /**
     * Moves the currently equipped weapon of the {@link Player} to their inventory.
     *
     * This method does nothing if the inventory is full or no weapon is equipped.
     *
     * @param player the {@link Player} to disarm.
     */
    public static void disarm(final Player player) {

        final Inventory inventory = player.getInventory();

        if (!inventory.isFull()) {

            final Equipment equipment = player.getEquipment();

            final int[] occupiedSlots = ItemContainerUtil.occupiedSlotStream(equipment).toArray();

            if(occupiedSlots.length == 0)
                return;

            final int randomSlot = Misc.randomInt(occupiedSlots);
            final Item itemAtSlot = equipment.atSlot(randomSlot);

            if (itemAtSlot != null && itemAtSlot.isValid()) {

                if(!inventory.canHold(itemAtSlot))
                   return;

                equipment.reset(randomSlot);
                equipment.refreshItems();

                inventory.add(itemAtSlot.clone());

                WeaponInterfaces.INSTANCE.assign(player);
                EquipmentBonuses.update(player);

                player.sendMessage("You have been disarmed!");
                player.updateAppearance();
            }
        }
    }
}
