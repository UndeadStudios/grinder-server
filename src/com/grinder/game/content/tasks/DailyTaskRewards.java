package com.grinder.game.content.tasks;

import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

public enum DailyTaskRewards {

    COINS_1(new Item(ItemID.COINS, 10_000_000), 7.5),
    BM(new Item(ItemID.BLOOD_MONEY, 3500), 15.0),
    DRAGON_BONES(new Item(ItemID.DRAGON_BONES_2, 100), 20.0),
    OURG_BONES(new Item(ItemID.OURG_BONES_2, 50), 20.0),
    WHIP(new Item(ItemID.ABYSSAL_WHIP, 1), 10.0),
    DARK_BOW(new Item(ItemID.DARK_BOW, 1), 10.0),
    DRAGON_DART(new Item(ItemID.DRAGON_DART, 100), 15.0),
    DRAGON_BOLT(new Item(ItemID.DRAGON_BOLTS, 250), 15.0),
    BLACK_CHIN(new Item(ItemID.BLACK_CHINCHOMPA, 100), 5.0),
    RED_CHIN(new Item(ItemID.RED_CHINCHOMPA_2, 150), 20.0),
    CHIN(new Item(ItemID.CHINCHOMPA_2, 250), 30.0),
    BLACK_DRAGONHIDE(new Item(ItemID.BLACK_DRAGONHIDE_2, 100), 20.0),
    RED_DRAGONHIDE(new Item(ItemID.RED_DRAGONHIDE_2, 150), 30.0),
    BLUE_DRAGONHIDE(new Item(ItemID.BLUE_DRAGONHIDE_2, 200), 40.0),
    GREEN_DRAGONHIDE(new Item(ItemID.GREEN_DRAGONHIDE_2, 250), 50.0),
    IRON_BAR(new Item(ItemID.IRON_BAR_2, 250), 50.0),
    STEEL_BAR(new Item(ItemID.STEEL_BAR_2, 200), 50.0),
    RUNITE_BAR_2(new Item(ItemID.RUNITE_BAR_2, 100), 50.0),


    NEW_CRYSTAL_SHIELD(new Item(ItemID.NEW_CRYSTAL_SHIELD, 1), 10.0),
    SARADOMIN_GODSWORD(new Item(ItemID.SARADOMIN_GODSWORD, 1), 1.0),
    VOTING_TICKETS(new Item(15031, 10), 10.0),
    D_SCIM_PPLUS(new Item(15514, 1), 10.0),
    AMULET_OF_FURY(new Item(ItemID.AMULET_OF_FURY, 1), 10.0),
    BLESSED_SPIRIT_SHIELD(new Item(ItemID.BLESSED_SPIRIT_SHIELD, 1), 10.0),
    HELM_OF_NEITIZNOT(new Item(ItemID.HELM_OF_NEITIZNOT, 1), 10.0),
    BERSERKER_NECKLACE(new Item(ItemID.BERSERKER_NECKLACE, 1), 10.0),


    DRAGON_SQ_SHIELD(new Item(ItemID.DRAGON_SQ_SHIELD, 1), 25.0),
    DRAGON_FULL_HELM(new Item(ItemID.DRAGON_FULL_HELM, 1), 25.0),
    BANDOS_BOOTS(new Item(ItemID.BANDOS_BOOTS, 1), 5.0),
    DRAGON_BOOTS(new Item(ItemID.DRAGON_BOOTS, 1), 5.0),
    DRAGON_PLATESKIRT(new Item(ItemID.DRAGON_PLATESKIRT, 1), 25.0),
    DRAGON_MED(new Item(ItemID.DRAGON_MED_HELM, 1), 25.0),
    DIAMOND_BOLTS_E_(new Item(ItemID.DIAMOND_BOLTS_E_, 250), 25.0),
    BOLT_RACK(new Item(ItemID.BOLT_RACK, 250), 25.0),
    ZULRAHS_SCALES(new Item(ItemID.ZULRAHS_SCALES, 250), 25.0),
    AMETHYST_ARROW(new Item(ItemID.AMETHYST_ARROW, 100), 25.0),
    SEA_TURTLE_2(new Item(ItemID.SEA_TURTLE_2, 100), 25.0),
    CANNONBALL(new Item(ItemID.CANNONBALL, 250), 25.0),
    DRAGON_BOLTS_E_(new Item(ItemID.DRAGON_BOLTS_E_, 250), 25.0),



    VERAC_HELM(new Item(ItemID.VERACS_HELM, 1), 5.0),
    VERAC_FLAIL(new Item(ItemID.VERACS_FLAIL, 1), 5.0),
    VERAC_BRASSARD(new Item(ItemID.VERACS_BRASSARD, 1), 5.0),
    VERAC_PLATESKIRT(new Item(ItemID.VERACS_PLATESKIRT, 1), 5.0),

    TORAG_HELM(new Item(ItemID.TORAGS_HELM, 1), 5.0),
    TORAG_HAMMERS(new Item(ItemID.TORAGS_HAMMERS, 1), 5.0),
    TORAG_PLATEBODY(new Item(ItemID.TORAGS_PLATEBODY, 1), 5.0),
    TORAG_PLATELEGS(new Item(ItemID.TORAGS_PLATELEGS, 1), 5.0),

    AHRIM_HOOD(new Item(ItemID.AHRIMS_HOOD, 1), 5.0),
    AHRIM_STAFF(new Item(ItemID.AHRIMS_STAFF, 1), 5.0),
    AHRIM_ROBETOP(new Item(ItemID.AHRIMS_ROBETOP, 1), 5.0),
    AHRIM_ROBESKIRT(new Item(ItemID.AHRIMS_ROBESKIRT, 1), 5.0),

    KARIL_COIF(new Item(ItemID.KARILS_COIF, 1), 5.0),
    KARIL_CROSSBOW(new Item(ItemID.KARILS_CROSSBOW, 1), 5.0),
    KARIL_LEATHERTOP(new Item(ItemID.KARILS_LEATHERTOP, 1), 5.0),
    KARIL_LEATHERSKIRT(new Item(ItemID.KARILS_LEATHERSKIRT, 1), 5.0),

    GUTHANS_HELM(new Item(ItemID.GUTHANS_HELM, 1), 5.0),
    GUTHANS_WARSPEAR(new Item(ItemID.GUTHANS_WARSPEAR, 1), 5.0),
    GUTHANS_BODY(new Item(ItemID.GUTHANS_PLATEBODY, 1), 5.0),
    GUTHANS_CHAINSKIRT(new Item(ItemID.GUTHANS_CHAINSKIRT, 1), 5.0),

    DHAROK_HELM(new Item(ItemID.DHAROKS_HELM, 1), 5.0),
    DHAROK_GREATAXE(new Item(ItemID.DHAROKS_GREATAXE, 1), 5.0),
    DHAROK_PLATEBODY(new Item(ItemID.DHAROKS_PLATEBODY, 1), 5.0),
    DHAROK_PLATELEGS(new Item(ItemID.DHAROKS_PLATELEGS, 1), 5.0),

    BOWSTRING(new Item(ItemID.BOW_STRING_2, 250), 40.0),
    BRONZE_BAR(new Item(ItemID.BRONZE_BAR_2, 350), 40.0),
    MITHRIL_BAR(new Item(ItemID.MITHRIL_BAR_2, 200), 40.0),

    SAPPHIRE(new Item(ItemID.SAPPHIRE_2, 200), 40.0),
    EMERALD(new Item(ItemID.EMERALD_2, 150), 35.0),
    RUBY(new Item(ItemID.RUBY_2, 125), 30.0),
    DIAMOND(new Item(ItemID.DIAMOND_2, 100), 5.0),

    LOGS(new Item(ItemID.LOGS_2, 500), 50.0),
    OAK_LOGS(new Item(ItemID.OAK_LOGS_2, 500), 40.0),
    WILLOW_LOGS(new Item(ItemID.WILLOW_LOGS, 250), 40.0),
    MAPLE_LOGS(new Item(ItemID.MAPLE_LOGS_2, 250), 35.0),
    YEW_LOGS(new Item(ItemID.YEW_LOGS_2, 250), 20.0),
    MAGIC_LOGS(new Item(ItemID.MAGIC_LOGS, 150), 10.0),

    LOBSTER(new Item(ItemID.LOBSTER_2, 400), 40.0),
    SHARK(new Item(ItemID.SHARK_2, 250), 20.0),
    MONKFISH(new Item(ItemID.MONKFISH_2, 375), 20.0),

    RAW_LOBSTER(new Item(ItemID.RAW_LOBSTER_2, 400), 35.0),
    RAW_SHARK(new Item(ItemID.RAW_SHARK_2, 250), 20.0),
    RAW_MANTA_RAY(new Item(ItemID.RAW_MANTA_RAY_2, 175), 20.0),
    RAW_MONKFISH(new Item(ItemID.RAW_MONKFISH_2, 325), 20.0),

    SUPER_ATTACK(new Item(ItemID.SUPER_ATTACK_4_2, 25), 25.0),
    SUPER_STRENGTH(new Item(ItemID.SUPER_STRENGTH_4_2, 25), 25.0),
    SUPER_DEFENCE(new Item(ItemID.SUPER_DEFENCE_4_2, 25), 25.0),
    RANGING_POTION(new Item(ItemID.RANGING_POTION_4_2, 25), 25.0),
    MAGIC_POTION(new Item(ItemID.MAGIC_POTION_4_2, 25), 25.0),

    RUNITE_BOLTS(new Item(ItemID.RUNITE_BOLTS_P_PLUS_PLUS_, 250), 20.0),
    DRAGON_DAGGER(new Item(ItemID.DRAGON_DAGGER_P_PLUS_PLUS_, 1), 5.0),

    CHAOS_RUNES(new Item(ItemID.CHAOS_RUNE, 1000), 40.0),
    DEATH_RUNES(new Item(ItemID.DEATH_RUNE, 750), 20.0),
    LAW_RUNES(new Item(ItemID.LAW_RUNE, 500), 20.0),
    NATURE_RUNES(new Item(ItemID.NATURE_RUNE, 500), 25.0),
    BLOOD_RUNES(new Item(ItemID.BLOOD_RUNE, 500), 10.0),
    WRATH_RUNE(new Item(ItemID.WRATH_RUNE, 100), 10.0),

    GRIMY_GUAM(new Item(ItemID.GRIMY_GUAM_LEAF_2, 250), 30.0),
    GRIMY_MARRENTILL(new Item(ItemID.GRIMY_MARRENTILL_2, 200), 30.0),
    GRIMY_TARROMIN(new Item(ItemID.GRIMY_TARROMIN_2, 200), 25.0),
    GRIMY_HARRALANDER(new Item(ItemID.GRIMY_HARRALANDER_2, 150), 25.0),
    GRIMY_RANARR(new Item(ItemID.GRIMY_RANARR_WEED_2, 125), 20.0),
    GRIMY_IRIT(new Item(ItemID.GRIMY_IRIT_LEAF_2, 100), 20.0),

    YEW_SEED(new Item(ItemID.YEW_SEED, 25), 25.0),
    MAGIC_SEED(new Item(ItemID.MAGIC_SEED, 25), 20.0),

    RUNE_ARROWTIPS(new Item(ItemID.RUNE_ARROWTIPS, 250), 20.0),
    COAL(new Item(ItemID.COAL_2, 345), 20.0),
    MITHRIL_ORE(new Item(ItemID.MITHRIL_ORE_2, 225), 20.0),
    RUNITE_ORE(new Item(ItemID.RUNITE_ORE_2, 100), 10.0),
    IRON_ORE(new Item(ItemID.IRON_ORE_2, 500), 30.0),

    ;

    private final Item item;
    private final double chance;

    private DailyTaskRewards(Item item, double chance)
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
