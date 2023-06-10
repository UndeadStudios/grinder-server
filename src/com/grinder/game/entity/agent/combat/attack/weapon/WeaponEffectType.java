package com.grinder.game.entity.agent.combat.attack.weapon;

import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.model.item.container.player.Equipment;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * This enum represents item effects that are applied during the final stages
 * in the combat calculations for the relevant type if the effect condition is met.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-07-17
 */
public enum WeaponEffectType {

    VERACS(EquipmentUtil::isWearingVeracSet, AttackType.MELEE),
    DHAROKS(EquipmentUtil::isWearingDharokSet, AttackType.MELEE),

    DRAGONSTONE_BOLTS(EquipmentUtil::isUsingDragonStoneBolts, AttackType.RANGED),
    OPAL_BOLTS(EquipmentUtil::isUsingOpalBolts, AttackType.RANGED),
    PEARL_BOLTS(EquipmentUtil::isUsingPearlBolts, AttackType.RANGED);

    private final Predicate<Equipment> condition;
    private final AttackType[] admissibleTypes;

    /**
     * Create a new {@link WeaponEffectType}.
     * 
     * @param condition         the equipment-specific condition that must be tested positively.
     * @param admissibleTypes   the {@link AttackType}s for which the effect is admissible.
     */
    WeaponEffectType(Predicate<Equipment> condition, AttackType... admissibleTypes) {
        this.condition = condition;
        this.admissibleTypes = admissibleTypes;
    }

    /**
     * Check whether the effect is admissible to the provided {@link AttackType}.
     *
     * @param type  the {@link AttackType} to check for admissibility.
     * @return {@code true} if the {@link #admissibleTypes} contains the specified type,
     *          {@code false} otherwise.
     */
    private boolean appliesTo(AttackType type){
        return Stream.of(admissibleTypes).anyMatch(Predicate.isEqual(type));
    }

    /**
     * Find a {@link WeaponEffectType} that is applicable given the parameters.
     *
     * @param equipment the {@link Equipment} to test for in the {@link WeaponEffectType#condition}.
     * @param type      the {@link AttackType} that must be present in the {@link WeaponEffectType#admissibleTypes}.
     * @return An {@link Optional<WeaponEffectType>} containing a {@link WeaponEffectType},
     *          if the predicate and type-admissibility are met, otherwise it is empty.
     */
    public static Optional<WeaponEffectType> findEffect(Equipment equipment, AttackType type){
        return Stream.of(values()).filter(effect -> effect.appliesTo(type) && effect.condition.test(equipment)).findAny();
    }
}
