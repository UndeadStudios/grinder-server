package com.grinder.game.content.skill.skillable.impl.herblore;

import com.grinder.util.ItemID;

public enum BarbarianPotionType {

    ATTACK_MIX_CAVIAR(ItemID.ATTACK_MIX_2_, ItemID.ATTACK_POTION_2_, ItemID.CAVIAR, 4, 8),
    ATTACK_MIX_ROE(ItemID.ATTACK_MIX_2_, ItemID.ATTACK_POTION_2_, ItemID.ROE, 4, 8),
    ANTIPOISON_MIX_CAVIAR(ItemID.ANTIPOISON_MIX_2_, ItemID.ANTIPOISON_2_, ItemID.CAVIAR, 6, 12),
    ANTIPOISON_MIX_ROE(ItemID.ANTIPOISON_MIX_2_, ItemID.ANTIPOISON_2_, ItemID.ROE, 6, 12),
    RELICYMS_MIX_CAVIAR(ItemID.RELICYMS_MIX_2_, ItemID.RELICYMS_BALM_2_, ItemID.CAVIAR, 9, 14),
    RELICYMS_MIX_ROE(ItemID.RELICYMS_MIX_2_, ItemID.RELICYMS_BALM_2_, ItemID.ROE, 9, 14),
    STRENGTH_MIX_CAVIAR(ItemID.STRENGTH_MIX_2_, ItemID.STRENGTH_POTION_2_, ItemID.CAVIAR, 9, 17),
    STRENGTH_MIX_ROE(ItemID.STRENGTH_MIX_2_, ItemID.STRENGTH_POTION_2_, ItemID.ROE, 9, 17),
    RESTORE_MIX_CAVIAR(ItemID.RESTORE_MIX_2_, ItemID.RESTORE_POTION_2_, ItemID.CAVIAR, 24, 21),
    RESTORE_MIX_ROE(ItemID.RESTORE_MIX_2_, ItemID.RESTORE_POTION_2_, ItemID.ROE, 24, 21),
    ENERGY_MIX(ItemID.ENERGY_MIX_2_, ItemID.ENERGY_POTION_2_, ItemID.CAVIAR, 29, 23),
    DEFENCE_MIX(ItemID.DEFENCE_MIX_2_, ItemID.DEFENCE_POTION_2_, ItemID.CAVIAR, 11, 25),
    AGILITY_MIX(ItemID.AGILITY_MIX_2_, ItemID.AGILITY_POTION_2_, ItemID.CAVIAR, 37, 27),
    COMBAT_MIX(ItemID.COMBAT_MIX_2_, ItemID.COMBAT_POTION_2_, ItemID.CAVIAR, 40, 28),
    PRAYER_MIX(ItemID.PRAYER_MIX_2_, ItemID.PRAYER_POTION_2_, ItemID.CAVIAR, 42, 29),
    SUPER_ATTACK_MIX(ItemID.SUPERATTACK_MIX_2_, ItemID.SUPER_ATTACK_2_, ItemID.CAVIAR, 47, 33),
    SUPER_ANTIPOISON_MIX(ItemID.ANTI_POISON_SUPERMIX_2_, ItemID.SUPERANTIPOISON_2_, ItemID.CAVIAR, 51, 35),
    FISHING_MIX(ItemID.FISHING_MIX_2_, ItemID.FISHING_POTION_2_, ItemID.CAVIAR, 53, 38),
    SUPER_ENERGY_MIX(ItemID.SUPER_ENERGY_MIX_2_, ItemID.SUPER_ENERGY_2_, ItemID.CAVIAR, 56, 39),
    HUNTING_MIX(ItemID.HUNTING_MIX_2_, ItemID.HUNTER_POTION_2_, ItemID.CAVIAR, 58, 40),
    SUPER_STR_MIX(ItemID.SUPER_STR_MIX_2_, ItemID.SUPER_STRENGTH_2_, ItemID.CAVIAR, 59, 42),
    MAGIC_ESSENCE_MIX(ItemID.MAGIC_ESSENCE_MIX_2_, ItemID.MAGIC_ESSENCE_2_, ItemID.CAVIAR, 61, 43),
    SUPER_RESTORE_MIX(ItemID.SUPER_RESTORE_MIX_2_, ItemID.SUPER_RESTORE_2_, ItemID.CAVIAR, 67, 48),
    SUPER_DEF_MIX(ItemID.SUPER_DEF_MIX_2_, ItemID.SUPER_DEFENCE_2_, ItemID.CAVIAR, 71, 50),
    ANTIDOTE_PLUS_MIX(ItemID.ANTIDOTE_PLUS_MIX_2_, ItemID.ANTIDOTE_PLUS_2_, ItemID.CAVIAR, 74, 52),
    ANTIFIRE_MIX(ItemID.ANTIFIRE_MIX_2_, ItemID.ANTIFIRE_POTION_2_, ItemID.CAVIAR, 75, 53),
    RANGING_MIX(ItemID.RANGING_MIX_2_, ItemID.RANGING_POTION_2_, ItemID.CAVIAR, 80, 54),
    MAGIC_MIX(ItemID.MAGIC_MIX_2_, ItemID.MAGIC_POTION_2_, ItemID.CAVIAR, 83, 57),
    ZAMORAK_MIX(ItemID.ZAMORAK_MIX_2_, ItemID.ZAMORAK_BREW_2_, ItemID.CAVIAR, 85, 58),
    STAMINA_MIX(ItemID.STAMINA_MIX_2_, ItemID.STAMINA_POTION_2_, ItemID.CAVIAR, 86, 60),
    EXTENDED_ANTIFIRE_MIX(ItemID.EXTENDED_ANTIFIRE_MIX_2_, ItemID.EXTENDED_ANTIFIRE_2_, ItemID.CAVIAR, 91, 61),
    SUPER_ANTIFIRE_MIX(ItemID.SUPER_ANTIFIRE_MIX_2, ItemID.SUPER_ANTIFIRE_POTION_2, ItemID.CAVIAR, 98, 70),
    EXTENDED_SUPER_ANTIFIRE_MIX(ItemID.EXTENDED_SUPER_ANTIFIRE_MIX_2, ItemID.EXTENDED_SUPER_ANTIFIRE_POTION_2, ItemID.CAVIAR, 99, 78);

    private final int barbarianPotion;

    private final int potionRequired;

    private final int secondIngredient;

    private final int levelRequired;

    private final int xp;

    BarbarianPotionType(final int barbarianPotion, final int potionRequired, final int secondIngredient, final int levelRequired, final int xp) {
        this.barbarianPotion = barbarianPotion;
        this.potionRequired = potionRequired;
        this.secondIngredient = secondIngredient;
        this.levelRequired = levelRequired;
        this.xp = xp;
    }

    public int getBarbarianPotion() {
        return barbarianPotion;
    }

    public int getPotionRequired() {
        return potionRequired;
    }

    public int getSecondIngredient() {
        return secondIngredient;
    }

    public int getLevelRequired() {
        return levelRequired;
    }

    public int getXp() {
        return xp;
    }
}
