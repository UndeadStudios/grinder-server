package com.grinder.game.entity.agent.combat.attack.weapon.ranged;

import com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;

/**
 * see https://oldschool.runescape.wiki/w/Attack_range
 *
 * @author Stan van der Bend
 * @since 5-4-19
 */
public enum RangedWeaponType {

    DARTS(3, 5, WeaponFightType.DART_LONGRANGE),
    KNIVES(4, 6, WeaponFightType.KNIFE_LONGRANGE),
    THROWING_AXES(4, 6, WeaponFightType.THROWNAXE_LONGRANGE),
    TOKTZ_XIL_UL(5, 6, WeaponFightType.OBBY_RING_LONGRANGE),


    TOXIC_BLOWPIPE(5, 7, WeaponFightType.BLOWPIPE_LONGRANGE),
    SHAYZIEN_BLOWPIPE(5, 7, WeaponFightType.BLOWPIPE_LONGRANGE),
    DORGESHUUN_CBOW(6, 8, WeaponFightType.DOGRESHUUN_LONGRANGE),
    CROSSBOWS(7, 9, WeaponFightType.CROSSBOW_LONGRANGE),
    SHORTBOWS(7, 9, WeaponFightType.SHORTBOW_LONGRANGE),
    COMP_OGRE_BOW(5, 7, WeaponFightType.SHORTBOW_LONGRANGE),
    ARMADYL_CROSSBOW(8, 10, WeaponFightType.CROSSBOW_LONGRANGE),
    ZARYTE_CROSSBOW(8, 10, WeaponFightType.CROSSBOW_LONGRANGE),
    KARILS_CROSSBOW(8, 10, WeaponFightType.KARILS_CROSSBOW_LONGRANGE),
    SEERCULL_BOW(8, 10, WeaponFightType.SHORTBOW_LONGRANGE),
    LONGBOWS(9, 10, WeaponFightType.LONGBOW_LONGRANGE),
    BALLISTA(9, 10, WeaponFightType.BALLISTA_LONGRANGE),

    CHINCHOMPA(9, 10, WeaponFightType.CHIN_LONG),

    LIZARD(1, 1, WeaponFightType.LIZARD_BLAZE),
    SALAMANDER(1, 1, WeaponFightType.SALAMANDER_BLAZE),

    THIRD_AGE_BOW(9, 10, WeaponFightType.SHORTBOW_LONGRANGE),
    CRAWS_BOW(9, 10, WeaponFightType.SHORTBOW_LONGRANGE),

    OGRE_BOW(10, 10, WeaponFightType.LONGBOW_LONGRANGE),
    COMPOSITE_BOWS(10, 10, WeaponFightType.LONGBOW_LONGRANGE),
    CRYSTAL_BOW(10, 10, WeaponFightType.CRYSTALBOW_LONGRANGE),
    DRAGON_HUNTER_BOW(10, 10, WeaponFightType.DRAGON_HUNTER_BOW_LONGRANGE),
    DARK_BOW(10, 10, WeaponFightType.DARKBOW_LONGRANGE),
    TWISTED_BOW(10, 10, WeaponFightType.SHORTBOW_LONGRANGE);

    private final WeaponFightType longRangeFightType;
    private final int defaultDistance;
    private final int longRangeDistance;

    RangedWeaponType(int defaultDistance, int longRangeDistance, WeaponFightType longRangeFightType) {
        this.defaultDistance = defaultDistance;
        this.longRangeDistance = longRangeDistance;
        this.longRangeFightType = longRangeFightType;
    }

    public boolean isCrossbow(){
        return this == CROSSBOWS || this == ARMADYL_CROSSBOW || this == KARILS_CROSSBOW || this == DORGESHUUN_CBOW || this == ZARYTE_CROSSBOW;
    }

    public boolean isShortBow(){
        return this == SHORTBOWS || this == THIRD_AGE_BOW || this == CRAWS_BOW || this == TWISTED_BOW || this == COMP_OGRE_BOW;
    }

    public boolean isLongBow(){
        return this == LONGBOWS || this == COMPOSITE_BOWS || this == DARK_BOW || this == SEERCULL_BOW || this == CRYSTAL_BOW || this == OGRE_BOW || this == DRAGON_HUNTER_BOW;
    }

    public boolean isThrowable(){
        return this == KNIVES || this == THROWING_AXES || this == DARTS || this == TOKTZ_XIL_UL || this == CHINCHOMPA;
    }

    public int getAmmunitionSlot(){
        return isThrowable()
                ? EquipmentConstants.WEAPON_SLOT
                : EquipmentConstants.AMMUNITION_SLOT;
    }

    public int getDefaultDistance() {
        return defaultDistance;
    }

    public int getLongRangeDistance() {
        return longRangeDistance;
    }

    public WeaponFightType getLongRangeFightType() {
        return longRangeFightType;
    }
}
