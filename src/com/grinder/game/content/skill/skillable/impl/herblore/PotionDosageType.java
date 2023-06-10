package com.grinder.game.content.skill.skillable.impl.herblore;

import com.grinder.util.ItemID;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.grinder.util.ItemID.*;
import static com.grinder.util.ItemID.SUPER_COMBAT_POTION_4_;

/**
 * The enumerated type containing all of the data we need to know about
 * potions to combine them properly.
 *
 * @author Ruse
 * @author Stan van der Bend
 */
public enum PotionDosageType {

    STRENGTH(119, 117, 115, 113, ItemID.VIAL_OF_WATER, "Strength"),
    SUPER_STRENGTH(161, 159, 157, 2440, ItemID.VIAL_OF_WATER, "Super strength"),
    ATTACK(125, 123, 121, 2428, ItemID.VIAL_OF_WATER, "Attack"),
    SUPER_ATTACK(149, 147, 145, 2436, ItemID.VIAL_OF_WATER, "Super attack"),
    DEFENCE(137, 135, 133, 2432, ItemID.VIAL_OF_WATER, "Defence"),
    SUPER_DEFENCE(167, 165, 163, 2442, ItemID.VIAL_OF_WATER, "Super defence"),
    RANGING_POTION(173, 171, 169, 2444, ItemID.VIAL_OF_WATER, "Ranging"),
    FISHING(155, 153, 151, 2438, ItemID.VIAL_OF_WATER, "Fishing"),
    PRAYER(143, 141, 139, 2434, ItemID.VIAL_OF_WATER, "Prayer"),
    ANTIFIRE(2458, 2456, 2454, 2452, ItemID.VIAL_OF_WATER, "Antifire"),
    ZAMORAK_BREW(193, 191, 189, 2450, ItemID.VIAL_OF_WATER, "Zamorakian brew"),
    ANTIPOISON(179, 177, 175, 2446, ItemID.VIAL_OF_WATER, "Antipoison"),
    RESTORE(131, 129, 127, 2430, ItemID.VIAL_OF_WATER, "Restoration"),
    MAGIC_POTION(3046, 3044, 3042, 3040, ItemID.VIAL_OF_WATER, "Magic"),
    SUPER_RESTORE(3030, 3028, 3026, 3024, ItemID.VIAL_OF_WATER, "Super restoration"),
    ENERGY(3014, 3012, 3010, 3008, ItemID.VIAL_OF_WATER, "Energy"),
    SUPER_ENERGY(3022, 3020, 3018, 3016, ItemID.VIAL_OF_WATER, "Super Energy"),
    AGILITY(3038, 3036, 3034, 3032, ItemID.VIAL_OF_WATER, "Agility"),
    SARADOMIN_BREW(6691, 6689, 6687, 6685, ItemID.VIAL_OF_WATER, "Saradomin brew"),
    ANTIPOISON1(5949, 5947, 5945, 5943, ItemID.VIAL_OF_WATER, "Antipoison(+)"),
    ANTIPOISON2(5958, 5956, 5954, 5952, ItemID.VIAL_OF_WATER, "Antipoison(++)"),
    SUPER_ANTIPOISON(185, 183, 181, 2448, ItemID.VIAL_OF_WATER, "Super antipoison"),
    RELICYMS_BALM(4848, 4846, 4844, 4842, ItemID.VIAL_OF_WATER, "Relicym's balm"),
    SERUM_207(3414, 3412, 3410, 3408, ItemID.VIAL_OF_WATER, "Serum 207"),
    COMBAT(9745, 9743, 9741, 9739, ItemID.VIAL_OF_WATER, "Combat"),
    BASTION(22470, 22467, 22464, 22461, ItemID.VIAL_OF_WATER, "Bastion"),
    BATTLEMAGE(22458, 22455, 22452, 22449, ItemID.VIAL_OF_WATER, "Battlemage"),
    STAMINA_POTION(STAMINA_POTION_1_, STAMINA_POTION_2_, STAMINA_POTION_3_, STAMINA_POTION_4_, ItemID.VIAL_OF_WATER, "Stamina"),
    SUPER_COMBAT(SUPER_COMBAT_POTION_1_, SUPER_COMBAT_POTION_2_, SUPER_COMBAT_POTION_3_, SUPER_COMBAT_POTION_4_, ItemID.VIAL_OF_WATER, "Super combat"),
    AGGRESSIVITY_POTION(15274, -1, -1, -1, ItemID.VIAL_OF_WATER, "Aggressivity"),
    EXTENDED_ANTIFIRE(EXTENDED_ANTIFIRE_1_, EXTENDED_ANTIFIRE_2_, EXTENDED_ANTIFIRE_3_, EXTENDED_ANTIFIRE_4_, ItemID.VIAL_OF_WATER, "Extended antifire"),
    OVERLOAD(11733, 11732, 11731, 11730, ItemID.VIAL_OF_WATER, "Overload"),
    SUPER_ANTIFIRE(SUPER_ANTIFIRE_POTION_1, SUPER_ANTIFIRE_POTION_2, SUPER_ANTIFIRE_POTION_3, SUPER_ANTIFIRE_POTION_4, ItemID.VIAL_OF_WATER, "Super antifire"),
    EXTENDED_SUPER_ANTIFIRE_POTION(EXTENDED_SUPER_ANTIFIRE_POTION_1, EXTENDED_SUPER_ANTIFIRE_POTION_2, EXTENDED_SUPER_ANTIFIRE_POTION_3, EXTENDED_SUPER_ANTIFIRE_POTION_4, ItemID.VIAL_OF_WATER, "Extended super antifire"),
    SANFEW_SERUM(SANFEW_SERUM_1_, SANFEW_SERUM_2_, SANFEW_SERUM_3_, SANFEW_SERUM_4_, ItemID.VIAL_OF_WATER, "Sanfew");

    static Map<Integer, PotionDosageType> potions = new HashMap<>();

    static {
        for (PotionDosageType potion : PotionDosageType.values()) {
            potions.put(potion.oneDosePotionID, potion);
            potions.put(potion.twoDosePotionID, potion);
            potions.put(potion.threeDosePotionID, potion);
            potions.put(potion.fourDosePotionID, potion);
        }
    }

    int oneDosePotionID, twoDosePotionID, threeDosePotionID, fourDosePotionID, vial;
    String potionName;

    PotionDosageType(int oneDosePotionID, int twoDosePotionID, int threeDosePotionID, int fourDosePotionID, int vial, String potionName) {
        this.oneDosePotionID = oneDosePotionID;
        this.twoDosePotionID = twoDosePotionID;
        this.threeDosePotionID = threeDosePotionID;
        this.fourDosePotionID = fourDosePotionID;
        this.vial = vial;
        this.potionName = potionName;
    }

    public static Optional<PotionDosageType> forId(int itemId) {
        PotionDosageType potion = potions.get(itemId);
        if (potion != null) {
            return Optional.of(potion);
        }
        return Optional.empty();
    }

    public int getVial() {
        return vial;
    }

    public String getPotionName() {
        return potionName;
    }

    public int getDoseForID(int id) {
        if (id == this.oneDosePotionID) {
            return 1;
        }
        if (id == this.twoDosePotionID) {
            return 2;
        }
        if (id == this.threeDosePotionID) {
            return 3;
        }
        if (id == this.fourDosePotionID) {
            return 4;
        }
        return -1;
    }

    public int getIDForDose(int dose) {
        if (dose == 1) {
            return this.oneDosePotionID;
        }
        if (dose == 2) {
            return this.twoDosePotionID;
        }
        if (dose == 3) {
            return this.threeDosePotionID;
        }
        if (dose == 4) {
            return this.fourDosePotionID;
        }
        if (dose == 0) {
            return ItemID.EMPTY_VIAL;
        }
        return -1;
    }
}
