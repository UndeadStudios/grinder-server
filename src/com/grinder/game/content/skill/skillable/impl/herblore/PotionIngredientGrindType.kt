package com.grinder.game.content.skill.skillable.impl.herblore

import com.grinder.util.ItemID

/**
 * Represent items that can be crushed with a pestle and mortar.
 *
 * TODO: use [ItemID] instead of magic constants.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-25
 */
enum class PotionIngredientGrindType(
    val id: Int,
    val result: Int,
    val swampTarRequired: Boolean = false,
    val requiredLevel: Int = 1,
    val xp: Int = 0)
{
    // #Herblore
    GROUND_GUAM(
        id = 249,
        result = 6681
    ),
    GUAM_TAR(
        id = 6681,
        result = 10142,
        swampTarRequired = true,
        xp = 30
    ),
    MARRENTILL_TAR(
        id = 251,
        result = 10143,
        requiredLevel = 31,
        swampTarRequired = true,
        xp = 42
    ),
    TARROMIN_TAR(
        id = 253,
        result = 10144,
        requiredLevel = 39,
        swampTarRequired = true,
        xp = 55
    ),
    HARRALANDER_TAR(
        id = 255,
        result = 10145,
        requiredLevel = 44,
        swampTarRequired = true,
        xp = 72
    ),
    KARAMBWAN_PASTE(
        id = ItemID.RAW_KARAMBWAN,
        result = ItemID.KARAMBWAN_PASTE
    ),
    POISON_KARAMBWAN_PASTE(
        id = ItemID.POISON_KARAMBWAN,
        result = ItemID.KARAMBWAN_PASTE_2
    ),
    BLAMISH_SNAIL_SLIME(
        id = 3363,
        result = 1581
    ),
    UNICORN_HORN(
        id = 237,
        result = 235
    ),
    CHOCOLATE_BAR(
        id = 1973,
        result = 1975
    ),
    BIRDS_NEST(
        id = 5075,
        result = 6693
    ),
    KEBBIT_TEETH(
        id = 10109,
        result = 10111
    ),
    DIAMOND_ROOT(
        id = 14703,
        result = 14704
    ),
    DESERT_GOAT_HORN(
        id = 9735,
        result = 9736
    ),
    RUNE_SHARDS(
        id = 6466,
        result = 6467
    ),
    GORAK_CLAWS(
        9016,
        9018
    ),
    CHARCOAL(
        id = 973,
        result = 704
    ),
    DRAGON_DUST(
        id = 243,
        result = 241
    ),
    ASHES(
        id = 592,
        result = 8865
    ),
    MUD_RUNE(
        id = 4698,
        result = 9594
    ),
    BAT_BONES(
        id = 530,
        result = 2391
    ),
    SULPHER(
        id = 3209,
        result = 3215
    ),
    THISTLE(
        id = 3263,
        result = 3264
    ),
    SEAWEED(
        id = 401,
        result = 6683
    ),
    KELP(
        id = 7516,
        result = 7517
    ),
    CRAB_MEAT(
        id = 7519,
        result = 7527
    ),
    COD(
        id = 339,
        result = 7528
    ),
    TOOTH(
        id = 2377,
        result = 9082
    ),
    ASTRAL_RUNE(
        id = 9075,
        result = 11155
    ),
    SUPERIOR_DRAGON_BONES(
        id = 22124,
        result = 21975
    ),
    LAVA_SHARDS(
        id = 11992,
        result = 11994
    ),
    GROUND_ASHES(
        id = 592,
        result = 8865
    ),
    CRYSTAL_SHARD(
        id = 23866,
        result = 23804
    ),
    SILVER_DUST(
        id = 2355,
        result = 7650
    );
}