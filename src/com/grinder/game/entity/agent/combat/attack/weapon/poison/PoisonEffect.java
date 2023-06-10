package com.grinder.game.entity.agent.combat.attack.weapon.poison;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The small utility class that manages all of the combat poison data.
 *
 * @author lare96
 * @author Advocatus
 * @author Stan van der Bend
 * @since 5-4-19
 */
public class PoisonEffect {

    public static final float APPLY_POISON_CHANCE = 50.0f;

    /**
     * The map of all of the different weapons that poison.
     */
    private static final Map<Integer, PoisonType> POISONED_ITEMS_MAP = new HashMap<>(97);

    /**
     * Gets the poison type of the specified item.
     *
     * @param item the item to get the poison type of.
     * @return the poison type of the specified item, or <code>null</code> if the
     * item is not able to poison the victim.
     */
    public static Optional<PoisonType> findPoison(Item item) {

        if (item == null || item.getId() < 1 || item.getAmount() < 1)
            return Optional.empty();

        return Optional.ofNullable(POISONED_ITEMS_MAP.get(item.getId()));
    }


    /**
     * Attempts to poison the argued {@link Agent} with the argued
     * {@link PoisonType}.
     *
     * This attackStrategy will have no effect if the target is already poisoned.
     *
     * @param target the {@link Agent} that will be poisoned, if not already.
     * @param poisonType the {@link PoisonType} that this entity is being inflicted with.
     */
    public static void applyPoisonTo(Agent target, PoisonType poisonType) {

        final boolean venom = PoisonType.VENOM.equals(poisonType);

        if ((!venom && target.isPoisoned()) || target.isVenomed())
            return;

/*        if (target.isNpc() && target.getAsNpc().fetchDefinition().getName().toLowerCase().contains("dummy")) {
            return;
        }*/

        if (target instanceof Player) {

            final Player targetPlayer = (Player) target;

            if (targetPlayer.getEquipment().contains(EquipmentUtil.SERPENTINE_HELM_ITEM_ID) || targetPlayer.getEquipment().contains(EquipmentUtil.MAGMA_HELM_ITEM_ID)
                    || targetPlayer.getEquipment().contains(EquipmentUtil.TANZANITE_HELM_ITEM_ID))
                return;

            if ((!venom && !targetPlayer.getCombat().getPoisonImmunityTimer().finished()) || !targetPlayer.getCombat().getVenomImmunityTimer().finished())
                return;

            targetPlayer.getPacketSender().sendMessage("<col=00FF00>You have been " + (venom ? "venomed" : "poisoned") + "!</col>");
        }

        if (venom) {
            target.setVenomDamage(poisonType.getDamage());
            TaskManager.submit(new VenomEffectTask(target));
        } else {
            target.setPoisonDamage(poisonType.getDamage());
            TaskManager.submit(new PoisonEffectTask(target));
        }
    }
    
    /**
     * Load all of the poison data.
     */
    public static void init() {

        POISONED_ITEMS_MAP.put(817, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(816, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(818, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(831, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(812, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(813, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(814, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(815, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(883, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(885, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(887, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(889, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(891, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(893, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(870, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(871, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(872, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(873, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(874, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(875, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(876, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(878, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(834, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(835, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(832, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(833, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(836, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(1221, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(1223, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(1219, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(1229, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(1231, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(1225, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(1227, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(1233, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(1253, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(1251, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(1263, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(1261, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(1259, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(1257, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(3094, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(4582, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(6593, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(8874, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(9286, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(9287, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(9288, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(9289, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(9290, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(9291, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(9292, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(11227, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(11231, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(11379, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(11386, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(11393, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(11400, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(11407, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(11414, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(19486, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(21320, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(21332, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(13267, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(21924, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(22734, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(22806, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(15346, PoisonType.MILD); // D scim (p)
        
        POISONED_ITEMS_MAP.put(5621, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5620, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5617, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5616, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5619, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5618, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5629, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5628, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5631, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5630, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5645, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5644, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5647, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5646, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5643, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5642, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5633, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5632, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5634, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5660, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5656, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5657, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5658, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5659, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5654, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5655, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5680, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5682, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5684, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5704, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5706, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5708, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5710, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5712, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5714, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5716, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(5734, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(6061, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(6595, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(8876, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(9293, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(9294, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(9295, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(9296, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(9297, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(9298, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(9299, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(10583, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(11228, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(11233, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(11382, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(11389, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(11396, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(11403, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(11410, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(11417, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(19488, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(21926, PoisonType.MILD);
        POISONED_ITEMS_MAP.put(21322, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(21334, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(22737, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(22808, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(15347, PoisonType.EXTRA); // D scim (p+)

        POISONED_ITEMS_MAP.put(5623, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5622, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5625, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5624, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5627, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5626, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5698, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(15348, PoisonType.SUPER); // D scim (p++)
        POISONED_ITEMS_MAP.put(5730, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5641, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5640, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5637, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5636, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5639, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5638, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5635, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5661, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5662, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5663, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5652, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5653, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5648, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5649, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5650, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5651, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5667, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5666, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5665, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5664, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5686, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5688, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5690, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5692, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5694, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5696, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5700, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5702, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5718, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5720, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5722, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5724, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5726, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5728, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(5736, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(6062, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(6597, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(8878, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(9300, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(9301, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(9302, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(9303, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(9304, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(9305, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(9306, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(10584, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(11229, PoisonType.VENOM); // Only for dragon arrows p++ for its great effect
        POISONED_ITEMS_MAP.put(11234, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(11384, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(11391, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(11398, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(11405, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(11412, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(11419, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(19490, PoisonType.VENOM); // Only for dragon javelin p++ VENOM
        POISONED_ITEMS_MAP.put(21324, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(21336, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(22740, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(22743, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(22810, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(13269, PoisonType.EXTRA);
        POISONED_ITEMS_MAP.put(13271, PoisonType.SUPER);

        POISONED_ITEMS_MAP.put(21928, PoisonType.SUPER);
        POISONED_ITEMS_MAP.put(12926, PoisonType.VENOM);
        POISONED_ITEMS_MAP.put(15834, PoisonType.VENOM);
        POISONED_ITEMS_MAP.put(12006, PoisonType.MILD);
    }

}
