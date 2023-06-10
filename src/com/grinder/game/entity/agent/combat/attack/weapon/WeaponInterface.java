package com.grinder.game.entity.agent.combat.attack.weapon;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy;
import com.grinder.util.ItemID;

import static com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType.*;

/**
 * All of the interfaces for weapons and the data needed to display these
 * interfaces properly.
 *
 * @author lare96
 */
public enum WeaponInterface {

    STAFF(328, 355, 5, new WeaponFightType[]{STAFF_BASH, STAFF_POUND, STAFF_FOCUS}, 7549, 7561),
    HUNTING_KNIFE(425, 428, 4, new WeaponFightType[]{HUNTING_KNIFE_POUND, HUNTING_KNIFE_PUMMEL, HUNTING_KNIFE_BLOCK}, 7474, 7486),
    WARHAMMER(425, 428, 6, new WeaponFightType[]{WARHAMMER_POUND, WARHAMMER_PUMMEL, WARHAMMER_BLOCK}, 7474, 7486),
    TORAG_HAMMER(425, 428, 5, new WeaponFightType[]{TORAG_WARHAMMER_POUND, TORAG_WARHAMMER_PUMMEL, TORAG_WARHAMMER_BLOCK}, 7474, 7486),
    HAM_JOINT(425, 428, 3, new WeaponFightType[]{WARHAMMER_POUND, WARHAMMER_PUMMEL, WARHAMMER_BLOCK}, 7474, 7486),
    BARRELCHEST_ANCHOR(425, 428, 6, new WeaponFightType[]{BARRELCHEST_POUND, BARRELCHEST_PUMMEL, BARRELCHEST_BLOCK}, 7474, 7486),
    WARHAMMER_CASKET(425, 428, 6, new WeaponFightType[]{WARHAMMER_CASKET_POUND, WARHAMMER_CASKET_PUMMEL, WARHAMMER_CASKET_BLOCK}, 7474, 7486),
    MAUL(425, 428, 7, new WeaponFightType[]{MAUL_POUND, MAUL_PUMMEL, MAUL_BLOCK}, 7474, 7486),
    BIRTHDAY_CAKE(425, 428, 4, new WeaponFightType[]{BIRTHDAY_CAKE_POUND, BIRTHDAY_CAKE_PUMMEL, BIRTHDAY_CAKE_BLOCK}, 7474, 7486),
    BIRTHDAY_BALLOON(425, 428, 4, new WeaponFightType[]{BIRTHDAY_BALLOON_POUND, BIRTHDAY_BALLOON_PUMMEL, BIRTHDAY_BALLOON_BLOCK}, 7474, 7486),
    MEAT_TENDERISER(425, 428, 6, new WeaponFightType[]{TENDERISER_POUND, TENDERISER_PUMMEL, TENDERISER_BLOCK}, 7474, 7486),
    GRANITE_MAUL(425, 428, 7, new WeaponFightType[]{GRANITE_MAUL_POUND, GRANITE_MAUL_PUMMEL, GRANITE_MAUL_BLOCK}, 7474, 7486),
    VERACS_FLAIL(3796, 3799, 5, new WeaponFightType[]{VERACS_FLAIL_POUND, VERACS_FLAIL_PUMMEL, VERACS_FLAIL_SPIKE, VERACS_FLAIL_BLOCK}, 7624, 7636),
    VIGGORA_CHAINMACE(3796, 3799, 5, new WeaponFightType[]{VIGGORA_POUND, VIGGORA_PUMMEL, VIGGORA_SPIKE, VIGGORA_BLOCK}, 7624, 7636),
    SCYTHE(776, 779, 7, new WeaponFightType[]{SCYTHE_REAP, SCYTHE_CHOP, SCYTHE_JAB, SCYTHE_BLOCK}),
    SCYTHE_VITUR(776, 779, 5, new WeaponFightType[]{SCYTHE_VITUR_REAP, SCYTHE_VITUR_CHOP, SCYTHE_VITUR_JAB, SCYTHE_VITUR_BLOCK}),
    BATTLEAXE(1698, 1701, 6, new WeaponFightType[]{BATTLEAXE_CHOP, BATTLEAXE_HACK, BATTLEAXE_SMASH, BATTLEAXE_BLOCK}, 7499, 7511),
    GREATAXE(1698, 1701, 7, new WeaponFightType[]{GREATAXE_CHOP, GREATAXE_HACK, GREATAXE_SMASH, GREATAXE_BLOCK}, 7499, 7511),
    CROSSBOW(1764, 1767, 6, new WeaponFightType[]{CROSSBOW_ACCURATE, CROSSBOW_RAPID, CROSSBOW_LONGRANGE}, 7549, 7561),
    DORGRESHUUN(1764, 1767, 5, new WeaponFightType[]{CROSSBOW_ACCURATE, CROSSBOW_RAPID, CROSSBOW_LONGRANGE}, 7549, 7561),
    HUNTER_CROSSBOW(1764, 1767, 4, new WeaponFightType[]{CROSSBOW_ACCURATE, CROSSBOW_RAPID, CROSSBOW_LONGRANGE}, 7549, 7561),
    BALLISTA(1764, 1767, 7, new WeaponFightType[]{BALLISTA_ACCURATE, BALLISTA_RAPID, BALLISTA_LONGRANGE}, 7549, 7561),

    /**
     * Attack speed for toxic blowpip is 4 during pvp,
     * see {@link RangedAttackStrategy#duration(Agent)}
     */
    BLOWPIPE(1764, 1767, 3, new WeaponFightType[]{BLOWPIPE_ACCURATE, BLOWPIPE_RAPID, BLOWPIPE_LONGRANGE}, 7549, 7561),
    KARILS_CROSSBOW(1764, 1767, 4, new WeaponFightType[]{KARILS_CROSSBOW_ACCURATE, KARILS_CROSSBOW_RAPID, KARILS_CROSSBOW_LONGRANGE}, 7549, 7561),
    SHORTBOW(1764, 1767, 4, new WeaponFightType[]{SHORTBOW_ACCURATE, SHORTBOW_RAPID, SHORTBOW_LONGRANGE}, 7549, 7561),
    CRAWS_BOW(1764, 1767, 4, new WeaponFightType[]{CRYSTALBOW_ACCURATE, CRYSTALBOW_RAPID, CRYSTALBOW_LONGRANGE}, 7549, 7561),
    SEERCULL(1764, 1767, 5, new WeaponFightType[]{SHORTBOW_ACCURATE, SHORTBOW_RAPID, SHORTBOW_LONGRANGE}, 7549, 7561),
    RAIN_BOW(1764, 1767, 3, new WeaponFightType[]{SHORTBOW_ACCURATE, SHORTBOW_RAPID, SHORTBOW_LONGRANGE}, 7549, 7561),
    COMPOSITE_BOW(1764, 1767, 5, new WeaponFightType[]{SHORTBOW_ACCURATE, SHORTBOW_RAPID, SHORTBOW_LONGRANGE}, 7549, 7561),
    CRYSTALBOW(1764, 1767, 5, new WeaponFightType[]{CRYSTALBOW_ACCURATE, CRYSTALBOW_RAPID, CRYSTALBOW_LONGRANGE}, 7549, 7561),
    LONGBOW(1764, 1767, 6, new WeaponFightType[]{LONGBOW_ACCURATE, LONGBOW_RAPID, LONGBOW_LONGRANGE}, 7549, 7561),
    OGRE_BOW(1764, 1767, 8, new WeaponFightType[]{LONGBOW_ACCURATE, LONGBOW_RAPID, LONGBOW_LONGRANGE}, 7549, 7561),
    DRAGON_DAGGER(2276, 2279, 4, new WeaponFightType[]{DRAGON_DAGGER_STAB, DRAGON_DAGGER_LUNGE, DRAGON_DAGGER_SLASH, DRAGON_DAGGER_BLOCK}, 7574, 7586),
    ABYSSAL_DAGGER(2276, 2279, 4, new WeaponFightType[]{DRAGON_DAGGER_STAB, DRAGON_DAGGER_LUNGE, DRAGON_DAGGER_SLASH, DRAGON_DAGGER_BLOCK}, 7574, 7586),
    DAGGER(2276, 2279, 4, new WeaponFightType[]{DAGGER_STAB, DAGGER_LUNGE, DAGGER_SLASH, DAGGER_BLOCK}, 7574, 7586),
    SWIFT_BLADE(2276, 2279, 3, new WeaponFightType[]{DAGGER_STAB, DAGGER_LUNGE, DAGGER_SLASH, DAGGER_BLOCK}, 7574, 7586),
    SWORD(2276, 2279, 4, new WeaponFightType[]{SWORD_STAB, SWORD_LUNGE, SWORD_SLASH, SWORD_BLOCK}, 7574, 7586),
    BONE_DAGGER(2276, 2279, 4, new WeaponFightType[]{BONE_DAGGER_STAB, BONE_DAGGER_LUNGE, BONE_DAGGER_SLASH, BONE_DAGGER_BLOCK}, 7574, 7586),
    SCIMITAR(2423, 2426, 4, new WeaponFightType[]{SCIMITAR_CHOP, SCIMITAR_SLASH, SCIMITAR_LUNGE, SCIMITAR_BLOCK}, 7599, 7611),
    BRINESABRE(2423, 2426, 4, new WeaponFightType[]{BRINESABRE_CHOP, BRINESABRE_SLASH, BRINESABRE_LUNGE, BRINESABRE_BLOCK}, 7599, 7611),
    LONGSWORD(2423, 2426, 5, new WeaponFightType[]{LONGSWORD_CHOP, LONGSWORD_SLASH, LONGSWORD_LUNGE, LONGSWORD_BLOCK}, 7599, 7611),
    ARCLIGHT(2423, 2426, 3, new WeaponFightType[]{LONGSWORD_CHOP, LONGSWORD_SLASH, LONGSWORD_LUNGE, LONGSWORD_BLOCK}, 7599, 7611),
    RAPIER(2276, 2279, 3, new WeaponFightType[]{RAPIER_STAB, RAPIER_LUNGE, RAPIER_SLASH, RAPIER_BLOCK}, 7574, 7586),
    INQUISITORS_MACE(3796, 3799, 4, new WeaponFightType[]{INQ_MACE_POUND, INQ_MACE_PUMMEL, INQ_MACE_SPIKE, INQ_MACE_BLOCK}, 7624, 7636),
    MACE(3796, 3799, 5, new WeaponFightType[]{MACE_POUND, MACE_PUMMEL, MACE_SPIKE, MACE_BLOCK}, 7624, 7636),
    KNIFE(4446, 4449, 3, new WeaponFightType[]{KNIFE_ACCURATE, KNIFE_RAPID, KNIFE_LONGRANGE}, 7649, 7661),
    OBBY_RINGS(4446, 4449, 4, new WeaponFightType[]{OBBY_RING_ACCURATE, OBBY_RING_RAPID, OBBY_RING_LONGRANGE}, 7649, 7661),
    BOFA(1764, 1767, 5, new WeaponFightType[]{CRYSTALBOW_ACCURATE, CRYSTALBOW_RAPID, CRYSTALBOW_LONGRANGE}, 7549, 7561),
    SPEAR(4679, 4682, 5, new WeaponFightType[]{SPEAR_LUNGE, SPEAR_SWIPE, SPEAR_POUND, SPEAR_BLOCK}, 7674, 7686) {
        @Override
        public int getSpeed(int itemId) {
            if (itemId == ItemID.ZAMORAKIAN_SPEAR || itemId == ItemID.ZAMORAKIAN_HASTA)
                return 4;
            return super.getSpeed(itemId);
        }
    },
    DRAGON_LANCE(4679, 4682, 4, new WeaponFightType[]{DRAGON_LANCE_LUNGE, DRAGON_LANCE_SWIPE, DRAGON_LANCE_POUND, DRAGON_LANCE_BLOCK}, 7674, 7686),
    TWO_HANDED_SWORD(4705, 4708, 7, new WeaponFightType[]{TWOHANDEDSWORD_CHOP, TWOHANDEDSWORD_SLASH, TWOHANDEDSWORD_SMASH, TWOHANDEDSWORD_BLOCK}, 7699, 7711),
    SHADOW_SWORD(4705, 4708, 7, new WeaponFightType[]{SHADOW_TWOHANDEDSWORD_CHOP, SHADOW_TWOHANDEDSWORD_SLASH, SHADOW_TWOHANDEDSWORD_SMASH, SHADOW_TWOHANDEDSWORD_BLOCK}, 7699, 7711),
    PICKAXE(5570, 5573, 5, new WeaponFightType[]{PICKAXE_SPIKE, PICKAXE_IMPALE, PICKAXE_SMASH, PICKAXE_BLOCK}, 7724, 7736),
    CLAWS(7762, 7765, 4, new WeaponFightType[]{CLAWS_CHOP, CLAWS_SLASH, CLAWS_LUNGE, CLAWS_BLOCK}, 7800, 7812),
    BOXING(7762, 7765, 6, new WeaponFightType[]{BOXING_CHOP, BOXING_SLASH, BOXING_LUNGE, BOXING_BLOCK}, 7800, 7812),
    HALBERD(8460, 8463, 7, new WeaponFightType[]{HALBERD_JAB, HALBERD_SWIPE, HALBERD_FEND}, 8493, 8505),
    HASTA(4679, 4682, 4, new WeaponFightType[]{HASTA_LUNGE, HASTA_SWIPE, HASTA_POUND, HASTA_BLOCK}, 7674, 7686),
    UNARMED(5855, 5857, 4, new WeaponFightType[]{UNARMED_PUNCH, UNARMED_KICK, UNARMED_BLOCK}),
    SNOWBALL(5855, 5857, 4, new WeaponFightType[]{UNARMED_PUNCH, UNARMED_KICK, UNARMED_BLOCK}),
    WHIP(12290, 12293, 4, new WeaponFightType[]{WHIP_FLICK, WHIP_LASH, WHIP_DEFLECT}, 12323, 12335),
    PIMPZ_WHIP(12290, 12293, 4, new WeaponFightType[]{PIMPZ_WHIP_FLICK, PIMPZ_WHIP_LASH, PIMPZ_WHIP_DEFLECT}, 12323, 12335),
    THROWNAXE(4446, 4449, 5, new WeaponFightType[]{THROWNAXE_ACCURATE, THROWNAXE_RAPID, THROWNAXE_LONGRANGE}, 7649, 7661),
    CHINCHOMPA(24055, 24056, 4, new WeaponFightType[]{CHIN_SHORT, CHIN_MED, CHIN_LONG}, 7649, 7661),
    LIZARD(24074, 24075, 5, new WeaponFightType[]{LIZARD_SCORCH, LIZARD_FLARE, LIZARD_BLAZE}, 7649, 7661),
    SALAMANDER(24074, 24075, 5, new WeaponFightType[]{SALAMANDER_SCORCH, SALAMANDER_FLARE, SALAMANDER_BLAZE}, 7649, 7661),
    DART(4446, 4449, 3, new WeaponFightType[]{DART_ACCURATE, DART_RAPID, DART_LONGRANGE}, 7649, 7661),
    JAVELIN(4446, 4449, 4, new WeaponFightType[]{JAVELIN_ACCURATE, JAVELIN_RAPID, JAVELIN_LONGRANGE}, 7649, 7661),
    ANCIENT_STAFF(328, 355, 4, new WeaponFightType[]{STAFF_BASH, STAFF_POUND, STAFF_FOCUS}, 18566, 18569),
    DARK_BOW(1764, 1767, 9, new WeaponFightType[]{DARKBOW_ACCURATE, DARKBOW_RAPID, DARKBOW_LONGRANGE}, 7549, 7561),
    DRAGON_HUNTER_BOW(1764, 1767, 6, new WeaponFightType[]{DRAGON_HUNTER_BOW_ACCURATE, DRAGON_HUNTER_BOW_RAPID, DRAGON_HUNTER_BOW_LONGRANGE}, 7549, 7561),
    GODSWORD(4705, 4708, 6, new WeaponFightType[]{GODSWORD_CHOP, GODSWORD_SLASH, GODSWORD_SMASH, GODSWORD_BLOCK}, 7699, 7711),
    ABYSSAL_BLUDGEON(4705, 4708, 4, new WeaponFightType[]{ABYSSAL_BLUDGEON_CHOP, ABYSSAL_BLUDGEON_SLASH, ABYSSAL_BLUDGEON_SMASH, ABYSSAL_BLUDGEON_BLOCK}, 7699, 7711),
    SARADOMIN_SWORD(4705, 4708, 4, new WeaponFightType[]{SARADOMIN_SWORD_CHOP, SARADOMIN_SWORD_SLASH, SARADOMIN_SWORD_SMASH, SARADOMIN_SWORD_BLOCK}, 7699, 7711),
    ELDER_MAUL(425, 428, 6, new WeaponFightType[]{ELDER_MAUL_POUND, ELDER_MAUL_PUMMEL, ELDER_MAUL_BLOCK}, 7474, 7486),
    ROYAL_SCEPTRE(328, 355, 5, new WeaponFightType[]{STAFF_BASH, STAFF_POUND, STAFF_FOCUS}),
    BLADED_STAFF(65000, 65016, 4, new WeaponFightType[]{STAFF_BASH, STAFF_POUND, STAFF_FOCUS}, 65001, 65014),
    LEAF_BLADED_BATTLEAXE(1698, 1701, 5, new WeaponFightType[]{LEAF_BLADED_BATTLEAXE_CHOP, LEAF_BLADED_BATTLEAXE_HACK, LEAF_BLADED_BATTLEAXE_SMASH, LEAF_BLADED_BATTLEAXE_BLOCK}, 7499, 7511),
    CANE(3796, 3799, 4, new WeaponFightType[]{CANE_POUND, CANE_SPIKE, CANE_PUMMEL, CANE_BLOCK});

    /**
     * The interface that will be displayed on the sidebar.
     */
    private final int interfaceId;

    /**
     * The line that the name of the item will be printed to.
     */
    private final int nameLineId;

    /**
     * The attack speed of weapons using this interface.
     */
    private final int speed;

    /**
     * The fight types that correspond with this interface.
     */
    private final WeaponFightType[] fightType;

    /**
     * The id of the special bar for this interface.
     */
    private final int specialBar;

    /**
     * The id of the special meter for this interface.
     */
    private final int specialMeter;

    /**
     * Creates a new weapon interface.
     *
     * @param interfaceId  the interface that will be displayed on the sidebar.
     * @param nameLineId   the line that the name of the item will be printed to.
     * @param speed        the attack speed of weapons using this interface.
     * @param fightType    the fight types that correspond with this interface.
     * @param specialBar   the id of the special bar for this interface.
     * @param specialMeter the id of the special meter for this interface.
     */
    WeaponInterface(int interfaceId, int nameLineId, int speed, WeaponFightType[] fightType, int specialBar,
                    int specialMeter) {
        this.interfaceId = interfaceId;
        this.nameLineId = nameLineId;
        this.speed = speed;
        this.fightType = fightType;
        this.specialBar = specialBar;
        this.specialMeter = specialMeter;
    }

    /**
     * Creates a new weapon interface.
     *
     * @param interfaceId the interface that will be displayed on the sidebar.
     * @param nameLineId  the line that the name of the item will be printed to.
     * @param speed       the attack speed of weapons using this interface.
     * @param fightType   the fight types that correspond with this interface.
     */
    WeaponInterface(int interfaceId, int nameLineId, int speed, WeaponFightType[] fightType) {
        this(interfaceId, nameLineId, speed, fightType, -1, -1);
    }

    /**
     * Gets the interface that will be displayed on the sidebar.
     *
     * @return the interface id.
     */
    public int getInterfaceId() {
        return interfaceId;
    }

    /**
     * Gets the line that the name of the item will be printed to.
     *
     * @return the name line id.
     */
    public int getNameLineId() {
        return nameLineId;
    }

    /**
     * Gets the attack speed of weapons using this interface.
     *
     * @return the attack speed of weapons using this interface.
     * @param itemId
     */
    public int getSpeed(int itemId) {
        return speed;
    }

    /**
     * Gets the fight types that correspond with this interface.
     *
     * @return the fight types that correspond with this interface.
     */
    public WeaponFightType[] getFightType() {
        return fightType;
    }

    /**
     * Gets the id of the special bar for this interface.
     *
     * @return the id of the special bar for this interface.
     */
    public int getSpecialBar() {
        return specialBar;
    }

    /**
     * Gets the id of the special meter for this interface.
     *
     * @return the id of the special meter for this interface.
     */
    public int getSpecialMeter() {
        return specialMeter;
    }

    public boolean isThrowable() {
        return this == WeaponInterface.DART
                || this == WeaponInterface.KNIFE
                || this == WeaponInterface.THROWNAXE
                || this == WeaponInterface.JAVELIN;
    }
}
