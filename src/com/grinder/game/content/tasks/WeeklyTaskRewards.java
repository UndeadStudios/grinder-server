package com.grinder.game.content.tasks;

import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

public enum WeeklyTaskRewards {


    COINS_1(new Item(ItemID.COINS, 100_000_000), 7.5),
    COINS_2(new Item(ItemID.COINS, 500_000_000), 1.5),
    COINS_3(new Item(ItemID.COINS, 999_999_999), 1.0),
    BLOOD_MONEY(new Item(ItemID.BLOOD_MONEY, 50_000), 7.5),
    SUPERIOR_DRAGON_BONES(new Item(22125, 100), 20.0),

    GOLD_WHIP(new Item(15156, 1), 5.0),
    ABYSSAL_DAGGER(new Item(ItemID.ABYSSAL_DAGGER_P_PLUS_PLUS_, 1), 10.0),
    BANDOS_GODSWORD(new Item(ItemID.BANDOS_GODSWORD, 1), 10.0),
    VOTING_TICKETS(new Item(15031, 50), 10.0),
    DRAGON_FIRE_SHIELD(new Item(ItemID.DRAGONFIRE_SHIELD_2, 1), 5.0),
    TWISTED_BUCKLER(new Item(ItemID.TWISTED_BUCKLER, 1), 5.0),
    D_SCIM_PPLUS(new Item(15514, 1), 10.0),
    DRAGON_CROSSBOW(new Item(21902, 1), 5.0),
    MASTER_WAND(new Item(ItemID.MASTER_WAND, 1), 10.0),
    AMULET_OF_FURY(new Item(ItemID.AMULET_OF_FURY, 1), 10.0),
    INFINITY_SET(new Item(15213, 1), 10.0),
    SUPER_MYSTERY_BOX(new Item(15205, 1), 10.0),
    DRAGON_CLAWS(new Item(ItemID.DRAGON_CLAWS, 1), 5.0),
    SARADOMIN_SWORD(new Item(ItemID.SARADOMIN_SWORD, 1), 5.0),
    LAVA_BLADE(new Item(ItemID.LAVA_BLADE, 1), 5.0),
    TOXIC_BLOWPIPE_EMPTY_(new Item(ItemID.TOXIC_BLOWPIPE_EMPTY_, 1), 5.0),
    _3RD_AGE_AMULET(new Item(ItemID._3RD_AGE_AMULET, 1), 5.0),
    BANDOS_TASSETS(new Item(ItemID.BANDOS_TASSETS, 1), 5.0),
    BANDOS_CHESTPLATE(new Item(ItemID.BANDOS_CHESTPLATE, 1), 5.0),
    ARMADYL_HELMET(new Item(ItemID.ARMADYL_HELMET, 1), 5.0),
    ARMADYL_PLATEBODY(new Item(ItemID.ARMADYL_PLATEBODY, 1), 5.0),
    ARMADYL_PLATELEGS(new Item(ItemID.ARMADYL_PLATELEGS, 1), 5.0),
    DRAGON_HUNTER_LANCE(new Item(ItemID.DRAGON_HUNTER_LANCE, 1), 10.0),
    DRAGON_PLATEBODY(new Item(ItemID.DRAGON_PLATEBODY, 1), 10.0),

    DRAGON_DART(new Item(ItemID.DRAGON_DART, 1000), 15.0),
    DRAGON_BOLT(new Item(ItemID.DRAGON_BOLTS, 1000), 15.0),
    BLACK_CHIN(new Item(ItemID.BLACK_CHINCHOMPA, 500), 5.0),
    RED_CHIN(new Item(ItemID.RED_CHINCHOMPA_2, 750), 20.0),

    BLACK_DRAGONHIDE(new Item(ItemID.BLACK_DRAGONHIDE_2, 500), 20.0),
    RED_DRAGONHIDE(new Item(ItemID.RED_DRAGONHIDE_2, 700), 30.0),
    BLUE_DRAGONHIDE(new Item(ItemID.BLUE_DRAGONHIDE_2, 800), 40.0),
    GREEN_DRAGONHIDE(new Item(ItemID.GREEN_DRAGONHIDE_2, 1000), 50.0),
    MITH_BAR(new Item(ItemID.MITHRIL_BAR, 500), 50.0),
    ADDY_BAR(new Item(ItemID.ADAMANTITE_BAR, 350), 50.0),
    RUNITE_BAR(new Item(ItemID.RUNITE_BAR_2, 250), 50.0),

    MAGE_BOOK(new Item(ItemID.MAGES_BOOK, 1), 25.0),
    WIZARD_BOOTS(new Item(ItemID.WIZARD_BOOTS, 1), 25.0),
    RANGER_BOOTS(new Item(ItemID.RANGER_BOOTS, 1), 25.0),
    RANGER_GLOVES(new Item(ItemID.RANGER_GLOVES, 1), 25.0),
    RANGER_TUNIC(new Item(ItemID.RANGERS_TUNIC, 1), 25.0),

    VERAC_HELM(new Item(ItemID.VERACS_HELM, 1), 10.0),
    VERAC_FLAIL(new Item(ItemID.VERACS_FLAIL, 1), 10.0),
    VERAC_BRASSARD(new Item(ItemID.VERACS_BRASSARD, 1), 10.0),
    VERAC_PLATESKIRT(new Item(ItemID.VERACS_PLATESKIRT, 1), 10.0),

    TORAG_HELM(new Item(ItemID.TORAGS_HELM, 1), 10.0),
    TORAG_HAMMERS(new Item(ItemID.TORAGS_HAMMERS, 1), 10.0),
    TORAG_PLATEBODY(new Item(ItemID.TORAGS_PLATEBODY, 1), 10.0),
    TORAG_PLATELEGS(new Item(ItemID.TORAGS_PLATELEGS, 1), 10.0),

    AHRIM_HOOD(new Item(ItemID.AHRIMS_HOOD, 1), 10.0),
    AHRIM_STAFF(new Item(ItemID.AHRIMS_STAFF, 1), 10.0),
    AHRIM_ROBETOP(new Item(ItemID.AHRIMS_ROBETOP, 1), 10.0),
    AHRIM_ROBESKIRT(new Item(ItemID.AHRIMS_ROBESKIRT, 1), 10.0),

    KARIL_COIF(new Item(ItemID.KARILS_COIF, 1), 10.0),
    KARIL_CROSSBOW(new Item(ItemID.KARILS_CROSSBOW, 1), 10.0),
    KARIL_LEATHERTOP(new Item(ItemID.KARILS_LEATHERTOP, 1), 10.0),
    KARIL_LEATHERSKIRT(new Item(ItemID.KARILS_LEATHERSKIRT, 1), 10.0),

    GUTHANS_HELM(new Item(ItemID.GUTHANS_HELM, 1), 10.0),
    GUTHANS_WARSPEAR(new Item(ItemID.GUTHANS_WARSPEAR, 1), 10.0),
    GUTHANS_BODY(new Item(ItemID.GUTHANS_PLATEBODY, 1), 10.0),
    GUTHANS_CHAINSKIRT(new Item(ItemID.GUTHANS_CHAINSKIRT, 1), 10.0),

    DHAROK_HELM(new Item(ItemID.DHAROKS_HELM, 1), 10.0),
    DHAROK_GREATAXE(new Item(ItemID.DHAROKS_GREATAXE, 1), 10.0),
    DHAROK_PLATEBODY(new Item(ItemID.DHAROKS_PLATEBODY, 1), 10.0),
    DHAROK_PLATELEGS(new Item(ItemID.DHAROKS_PLATELEGS, 1), 10.0),

    BOWSTRING(new Item(ItemID.BOW_STRING_2, 1000), 30.0),

    DIAMOND(new Item(ItemID.DIAMOND_2, 500), 10.0),
    DRAGONSTONE(new Item(ItemID.DRAGONSTONE_2, 350), 10.0),

    LOGS(new Item(ItemID.LOGS_2, 1000), 50.0),
    OAK_LOGS(new Item(ItemID.OAK_LOGS_2, 850), 40.0),
    WILLOW_LOGS(new Item(ItemID.WILLOW_LOGS, 750), 40.0),
    MAPLE_LOGS(new Item(ItemID.MAPLE_LOGS_2, 650), 35.0),
    YEW_LOGS(new Item(ItemID.YEW_LOGS_2, 500), 20.0),
    MAGIC_LOGS(new Item(ItemID.MAGIC_LOGS, 350), 10.0),


    MANTA_RAY(new Item(ItemID.MANTA_RAY_2, 500), 20.0),
    BIG_SHARK(new Item(15891, 250), 20.0),
    MONKFISH(new Item(ItemID.MONKFISH_2, 1500), 20.0),

    RAW_LOBSTER(new Item(ItemID.RAW_LOBSTER_2, 500), 35.0),
    RAW_SHARK(new Item(ItemID.RAW_SHARK_2, 250), 20.0),
    RAW_BIG_SHARK(new Item(15702, 250), 20.0),
    RAW_MONKFISH(new Item(ItemID.RAW_MONKFISH_2, 350), 20.0),

    SUPER_ATTACK(new Item(ItemID.SUPER_ATTACK_4_2, 250), 25.0),
    SUPER_STRENGTH(new Item(ItemID.SUPER_STRENGTH_4_2, 250), 25.0),
    SUPER_DEFENCE(new Item(ItemID.SUPER_DEFENCE_4_2, 250), 25.0),
    RANGING_POTION(new Item(ItemID.RANGING_POTION_4_2, 250), 25.0),
    MAGIC_POTION(new Item(ItemID.MAGIC_POTION_4_2, 250), 25.0),
    SUPER_COMBAT_POTION_4(new Item(ItemID.SUPER_COMBAT_POTION_4_2, 100), 25.0),

    RUNITE_BOLTS(new Item(ItemID.RUNITE_BOLTS, 1000), 20.0),
    DRAGON_BOLTS_E_(new Item(ItemID.DRAGON_BOLTS_E_, 500), 20.0),
    RUBY_DRAGONBOLTS_E_(new Item(ItemID.RUBY_DRAGONBOLTS_E_, 300), 20.0),

    DEATH_RUNES(new Item(ItemID.DEATH_RUNE, 2500), 20.0),
    LAW_RUNES(new Item(ItemID.LAW_RUNE, 1250), 20.0),
    NATURE_RUNES(new Item(ItemID.NATURE_RUNE, 1000), 25.0),
    BLOOD_RUNES(new Item(ItemID.BLOOD_RUNE, 500), 10.0),
    WRATH_RUNES(new Item(ItemID.WRATH_RUNE, 250), 10.0),

    GRIMY_RANARR(new Item(ItemID.GRIMY_RANARR_WEED_2, 250), 20.0),
    GRIMY_IRIT(new Item(ItemID.GRIMY_IRIT_LEAF_2, 250), 20.0),
    GRIMY_CADANTINE(new Item(ItemID.GRIMY_CADANTINE_2, 250), 20.0),
    GRIMY_DWARF(new Item(ItemID.GRIMY_DWARF_WEED_2, 250), 20.0),
    GRIMY_AVANTOE(new Item(ItemID.GRIMY_AVANTOE_2, 250), 20.0),

    RUNE_ARROWTIPS(new Item(ItemID.RUNE_ARROWTIPS, 1000), 20.0),
    COAL(new Item(ItemID.COAL_2, 1000), 20.0),
    MITHRIL_ORE(new Item(ItemID.MITHRIL_ORE_2, 500), 20.0),
    RUNITE_ORE(new Item(ItemID.RUNITE_ORE_2, 250), 10.0),
    IRON_ORE(new Item(ItemID.IRON_ORE_2, 1000), 25.0),

    ZAMORAK_BREW(new Item(ItemID.ZAMORAK_BREW_3_2, 100), 20.0),

    BESERKER_RING(new Item(ItemID.BERSERKER_RING, 1), 2.5),
    ARCHERS_RING(new Item(ItemID.ARCHERS_RING, 1), 2.5),
    SEERS_RING(new Item(ItemID.SEERS_RING, 1), 2.5),
    ;

    private final Item item;
    private final double chance;

    private WeeklyTaskRewards(Item item, double chance)
    {
        this.item = item;
        this.chance = chance;
    }

    public Item GetItem()
    {
        return item;
    }

    public double GetChance()
    {
        return chance;
    }
}
