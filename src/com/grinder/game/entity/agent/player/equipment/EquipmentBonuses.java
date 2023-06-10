package com.grinder.game.entity.agent.player.equipment;

import com.grinder.game.content.item.charging.impl.AncientWyvernShield;
import com.grinder.game.content.item.charging.impl.Blowpipe;
import com.grinder.game.content.item.charging.impl.DragonFireWard;
import com.grinder.game.content.item.charging.impl.ShayzienBlowpipe;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.combat.formula.CombatSnapshot;
import com.grinder.game.entity.agent.combat.PlayerCombat;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy;
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy;
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon;
import com.grinder.game.entity.agent.combat.formula.CombatFormulaType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.Item;
import com.grinder.net.packet.PacketSender;
import com.grinder.util.oldgrinder.EquipSlot;

/**
 * Represents a bonus manager. Handles a player's equipment bonuses.
 *
 * @author Professor Oak
 */
public class EquipmentBonuses {

    public static final int
            ATTACK_STAB = 0, ATTACK_SLASH = 1, ATTACK_CRUSH = 2, ATTACK_MAGIC = 3, ATTACK_RANGE = 4,
            DEFENCE_STAB = 0, DEFENCE_SLASH = 1, DEFENCE_CRUSH = 2, DEFENCE_MAGIC = 3, DEFENCE_RANGE = 4,
            STRENGTH = 0, RANGED_STRENGTH = 1, MAGIC_STRENGTH = 2,
            PRAYER = 3;

    public static final int INTERFACE_ID = 15106;

    private static final String[][] STRING_ID = {
            {"1675", "Stab"}, {"1676", "Slash"}, {"1677", "Crush"},
            {"1678", "Magic"}, {"1679", "Range"},
            {"1680", "Stab"}, {"1681", "Slash"}, {"1682", "Crush"}, {"1683", "Magic"}, {"1684", "Range"},
            {"1686", "Strength"}, {"1670", "Ranged Strength"}, {"1671", "Magic Strength"},
            {"1687", "Prayer"}
    };

    private static final int MELEE_MAXHIT_FRAME = 15115;
    private static final int RANGED_MAXHIT_FRAME = 15116;
    private static final int MAGIC_MAXHIT_FRAME = 15117;
    public static final int CONTAINER_INVENTORY_INTERFACE = 3213;
    public static final int INVENTORY_CONTAINER_INTERFACE = 3322;

    private final double[] attackBonus = new double[5];
    private final double[] defenceBonus = new double[5];
    private final double[] otherBonus = new double[4];

    /**
     * Opens the interface which displays the player's bonuses.
     */
    public static void open(Player player) {
        update(player, true);
    	player.getPacketSender().sendInterfaceSet(INTERFACE_ID, CONTAINER_INVENTORY_INTERFACE);
    }

    /**
     * Updates bonuses for a player.
     */
    public static void update(Player player) {
        update(player, player.getInterfaceId() == INTERFACE_ID);
    }

    public static void update(Player player, boolean updateInterface) {

        final int totalBonuses = STRING_ID.length;
        final double[] bonuses = new double[totalBonuses];

        player.getCombat().determineStrategy();
        final RangedWeapon rangedWeapon = player.getCombat().getRangedWeapon();
        final Ammunition ammunition = player.getCombat().getAmmunition();

        if(rangedWeapon == RangedWeapon.TOXIC_BLOWPIPE) {
            Blowpipe.INSTANCE.getDartsUsed(player).ifPresent(it -> bonuses[11] += ItemDefinition.forId(it).getBonuses()[11]);
        } else if(rangedWeapon == RangedWeapon.SHAYZIEN_TOXIC_BLOWPIPE) {
            ShayzienBlowpipe.INSTANCE.getDartsUsed(player).ifPresent(it -> bonuses[11] += ItemDefinition.forId(it).getBonuses()[11]);
        }

        for (Item item : player.getEquipment().getItems()) {

            final ItemDefinition definition = ItemDefinition.forId(item.getId());
            final double[] itemBonuses = definition.getBonuses();

            if (itemBonuses != null) {

                boolean skipRangedBonus = false;

                if(rangedWeapon != null) {
                    final int slot = player.getEquipment().getSlot(item);
                    if (slot == EquipSlot.ARROWS)
                        skipRangedBonus = rangedWeapon.isNotCompatible(Ammunition.getFor(item.getId()));
                }

                for (int i = 0; i < itemBonuses.length; i++) {
                    if(i == 11 && skipRangedBonus)
                        continue;
                    bonuses[i] += itemBonuses[i];
                }
            }
        }

        if(player.getEquipment().containsAny(DragonFireWard.CHARGED)) {
        	for (int i = 0; i < DragonFireWard.INSTANCE.getCharges(player.getEquipment().getById(DragonFireWard.CHARGED)); i++) {
        		bonuses[5] += 1;
        		bonuses[6] += 1;
        		bonuses[7] += 1;
        		bonuses[9] += 1;
        	}
        }

        if(player.getEquipment().containsAny(AncientWyvernShield.CHARGED)) {
            for (int i = 0; i < AncientWyvernShield.INSTANCE.getCharges(player.getEquipment().getById(AncientWyvernShield.CHARGED)); i++) {
                bonuses[5] += 1;
                bonuses[6] += 1;
                bonuses[7] += 1;
                bonuses[9] += 1;
            }
        }

        for (int i = 0; i < totalBonuses; i++) {
            if (i <= 4) {
                player.getBonusManager().attackBonus[i] = bonuses[i];
            } else if (i <= 9) {
                int index = i - 5;
                player.getBonusManager().defenceBonus[index] = bonuses[i];
            } else {
                int index = i - 10;
                player.getBonusManager().otherBonus[index] = bonuses[i];
            }
            if(updateInterface)
                player.getPacketSender().sendString(Integer.parseInt(STRING_ID[i][0]), STRING_ID[i][1] + ": " + (bonuses[i] >= 0 ? "+" : "") + (int) bonuses[i], true);
        }

        /*
         * Update maxhit frames on the interface.
         */
        if (player.getInterfaceId() == INTERFACE_ID) {

            if(updateInterface) {
                if (player.getAttributes().bool(Attribute.CAN_LOOKUP_MAX_HIT) || PlayerUtil.isMember(player) || player.getRights().isHighStaff()) {

                    final PacketSender sender = player.getPacketSender();
                    final PlayerCombat combat = player.getCombat();
                    final AttackStrategy<?> strategy = combat.determineStrategy();

                    final AttackStrategy<?> meleeStrategy = strategy.type() == AttackType.MELEE ? strategy : MeleeAttackStrategy.INSTANCE;
                    final AttackStrategy<?> rangedStrategy = strategy.type() == AttackType.RANGED ? strategy : RangedAttackStrategy.INSTANCE;
                    final AttackStrategy<?> magicStrategy = strategy.type() == AttackType.MAGIC ? strategy : MagicAttackStrategy.INSTANCE;
                    final CombatSnapshot meleeSnapShot = CombatSnapshot.of(player, player, meleeStrategy, AttackType.MELEE);
                    final CombatSnapshot rangedSnapShot = CombatSnapshot.of(player, player, rangedStrategy, AttackType.RANGED);
                    final CombatSnapshot magicSnapShot = CombatSnapshot.of(player, player, magicStrategy, AttackType.MAGIC);
                    final int meleeMaxHit =
                            (int) CombatFormulaType.MELEE.calculateStrength(meleeSnapShot, meleeSnapShot);
                    final int rangedMaxHit = rangedWeapon == null || rangedWeapon.isNotCompatible(ammunition) ? 0
                            : (int) CombatFormulaType.RANGED.calculateStrength(rangedSnapShot, rangedSnapShot);
                    final int magicMaxHit = combat.getAutocastSpell() == null ? 0
                            : (int) CombatFormulaType.MAGIC.calculateStrength(magicSnapShot, magicSnapShot);

                    sender.sendString(MELEE_MAXHIT_FRAME, "Melee: " + getDamageString(meleeMaxHit));
                    sender.sendString(RANGED_MAXHIT_FRAME, "Ranged: " + getDamageString(rangedMaxHit));
                    sender.sendString(MAGIC_MAXHIT_FRAME, "Magic: " + getDamageString(magicMaxHit));
                } else {
                    player.getPacketSender().sendString(MELEE_MAXHIT_FRAME,
                            "<img=1025> Member's Feature!");
                    player.getPacketSender().sendString(RANGED_MAXHIT_FRAME,
                            "<img=1025> Member's Feature!");
                    player.getPacketSender().sendString(MAGIC_MAXHIT_FRAME,
                            "<img=1025> Member's Feature!");
                }
            }
        }
    }

    private static String getDamageString(int damage) {
        if (damage == 0)
            return "---";
        return String.valueOf(damage);
    }

    public double[] getAttackBonus() {
        return attackBonus;
    }

    public double[] getDefenceBonus() {
        return defenceBonus;
    }

    public double[] getOtherBonus() {
        return otherBonus;
    }

    public double getPrayerBonus() {
        return otherBonus[PRAYER];
    }
}
