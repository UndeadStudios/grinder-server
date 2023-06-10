package com.grinder.game.model.consumable.edible

import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.getLevel
import com.grinder.game.entity.agent.player.getMaxLevel
import com.grinder.game.model.Skill
import com.grinder.util.ItemID

/**
 * Data about beverages. NOTE: This could be transformed into json and read in easier.
 *
 * @param beverageID The id of the drink.
 * @param replaceWith The ID that is replaced upon consumption.
 * @param healAmt The amount healed upon consumption.
 * @param drink Any affects that occur during consumption.
 * @param message Optional message to send upon consumption
 */
enum class Beverage(val beverageID: Int, val replaceWith: Int, val healAmt: Int, val drink: (Player) -> Unit = fun(_: Player) {},
                    val message: String = "You drink the ${ItemDefinition.forId(beverageID).name}.") {
    FRUIT_BLAST(ItemID.FRUIT_BLAST, ItemID.COCKTAIL_GLASS, 9),
    DIRTY_BLAST(ItemID.DIRTY_BLAST, ItemID.COCKTAIL_GLASS, 5),
    PREMADE_FRUIT_BLAST(ItemID.PREMADE_FR_BLAST, ItemID.COCKTAIL_GLASS, 9),
    PINEAPPLE_PUNCH(ItemID.PINEAPPLE_PUNCH, ItemID.COCKTAIL_GLASS, 9),
    PREMADE_PINEAPPLE_PUNCH(ItemID.PREMADE_P_PUNCH, ItemID.COCKTAIL_GLASS, 9),
    WIZARD_BLIZZARD(ItemID.WIZARD_BLIZZARD, ItemID.COCKTAIL_GLASS, 5, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 6)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 4, 0)
    }),
    PREMADE_WIZARD_BLIZZARD(ItemID.PREMADE_WIZ_BLZD, ItemID.COCKTAIL_GLASS, 5, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 6)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 4, 0)
    }),
    SHORT_GREEN_GUY(ItemID.SHORT_GREEN_GUY, ItemID.COCKTAIL_GLASS, 5, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 4)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 3, 0)
    }),
    PREMADE_SHORT_GREEN_GUY(ItemID.PREMADE_SGG, ItemID.COCKTAIL_GLASS, 5, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 4)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 3, 0)
    }),
    DRUNK_DRAGON(ItemID.DRUNK_DRAGON, ItemID.COCKTAIL_GLASS, 5, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 7)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 4, 0)
    }),
    PREMADE_DRUNK_DRAGON(ItemID.PREMADE_DR_DRAGON, ItemID.COCKTAIL_GLASS, 5, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 7)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 4, 0)
    }),
    CHOC_SATURDAY(ItemID.CHOC_SATURDAY, ItemID.COCKTAIL_GLASS, 5, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 7)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 4, 0)
    }),
    PREMADE_CHOC_SATURDAY(ItemID.PREMADE_CHOC_SDY, ItemID.COCKTAIL_GLASS, 5, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 7)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 4, 0)
    }),
    BLURBERRY_SPECIAL(ItemID.BLURBERRY_SPECIAL, ItemID.COCKTAIL_GLASS, 6, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 6)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 4, 0)
    }),
    WINE_JUG(ItemID.JUG_OF_WINE, ItemID.JUG, 11, fun(player: Player) {
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 2, 0)
    }),
    /*BEER(ItemIdentifiers.BEER, ItemIdentifiers.BEER_GLASS, 1, fun(player: Player) {
        val attack = player.skillManager.getMaxLevel(Skill.ATTACK)
        val str = player.skillManager.getMaxLevel(Skill.STRENGTH)
        player.skillManager.decreaseLevelTemporarily(Skill.STRENGTH, (str * .06 + 1).toInt())
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, (attack * .06 + 1).toInt(), 0)
    }),*/
    ASGARNIAN_ALE(ItemID.ASGARNIAN_ALE, ItemID.BEER_GLASS, 2, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 2)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 4, 0)
    }),
    ASGARNIAN_ALE_M(ItemID.ASGARNIAN_ALE_M_, ItemID.BEER_GLASS, 2, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 3)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 6, 0)
    }),
    AXEMAN_FOLLY(ItemID.AXEMANS_FOLLY, ItemID.BEER_GLASS, 1, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.WOODCUTTING, 1)
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 3, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 3, 0)
    }),
    AXEMAN_FOLLY_M(ItemID.AXEMANS_FOLLY_M_, ItemID.BEER_GLASS, 2, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.WOODCUTTING, 2)
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 4, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 4, 0)
    }),
    /*BANDIT_BREW(ItemIdentifiers.BANDITS_BREW, ItemIdentifiers.BEER_GLASS, 1, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.THIEVING, 1)
        player.skillManager.increaseLevelTemporarily(Skill.ATTACK, 1)
        player.skillManager.decreaseLevelTemporarily(Skill.STRENGTH, (kotlin.math.floor(player.getMaxLevel(Skill.DEFENCE) * 0.06) + 3).toInt())
        player.skillManager.decreaseLevelTemporarily(Skill.DEFENCE, (kotlin.math.floor(player.getMaxLevel(Skill.DEFENCE) * 0.06) + 3).toInt())
    }),*/
    BLOOD_PINT(-1, ItemID.BEER_GLASS, 1, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.ATTACK, 5)
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 5)
        player.skillManager.decreaseLevelTemporarily(Skill.MAGIC, 5)
        player.skillManager.decreaseLevelTemporarily(Skill.PRAYER, 5, 0)
    }, message = "You drink the Blood Pint. It tastes unsurprisingly awful and makes you feel a bit sick."),
    BLOODY_BRACER(22430, ItemID.BEER_GLASS, 2, fun(player: Player) {
        player.skillManager.decreaseLevelTemporarily(Skill.PRAYER, (2 + player.getLevel(Skill.PRAYER) * 0.04).toInt(), 0)
    }),
    CHEF_DELIGHT(ItemID.CHEFS_DELIGHT, ItemID.BEER_GLASS, 1, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.COOKING, (1 + player.getLevel(Skill.COOKING) * 0.05).toInt())
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 2, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.STRENGTH, 2, 0)
    }),
    CHEF_DELIGHT_M(ItemID.CHEFS_DELIGHT_M_, ItemID.BEER_GLASS, 2, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.COOKING, (2 + player.getLevel(Skill.COOKING) * 0.05).toInt())
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 3, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.STRENGTH, 3, 0)
    }),
    CIDER(ItemID.CIDER, ItemID.BEER_GLASS, 2, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.FARMING, 1)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 2, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.STRENGTH, 2, 0)
    }),
    CIDER_M(ItemID.MATURE_CIDER, ItemID.BEER_GLASS, 2, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.FARMING, 2)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 5, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.STRENGTH, 5, 0)
    }),
    DRAGON_BITTER(ItemID.DRAGON_BITTER, ItemID.BEER_GLASS, 1, fun(player: Player) {
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 4, 0)
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 2)
    }),
    DRAGON_BITTER_M(ItemID.DRAGON_BITTER_M_, ItemID.BEER_GLASS, 2, fun(player: Player) {
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 6, 0)
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 3)
    }),
    DWARVEN_STOUT(ItemID.DWARVEN_STOUT, ItemID.BEER_GLASS, 1, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.MINING, 1)
        player.skillManager.increaseLevelTemporarily(Skill.SMITHING, 1)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 2, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.STRENGTH, 2, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.DEFENCE, 2, 0)
    }),
    DWARVEN_STOUT_M(ItemID.DWARVEN_STOUT_M_, ItemID.BEER_GLASS, 1, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.MINING, 2)
        player.skillManager.increaseLevelTemporarily(Skill.SMITHING, 2)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 7, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.STRENGTH, 7, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.DEFENCE, 7, 0)
    }),
    ELVEN_DAWN(23948, ItemID.BEER_GLASS, 1, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.AGILITY, 1)
        player.skillManager.decreaseLevelTemporarily(Skill.STRENGTH, 1, 0)
    }),
    GROG(ItemID.GROG, ItemID.BEER_GLASS, 3, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 3)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 6, 0)
    }),
    GREENMANS_ALE(ItemID.GREENMANS_ALE, ItemID.BEER_GLASS, 1, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.HERBLORE, 1)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 3, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.STRENGTH, 3, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.DEFENCE, 3, 0)
    }),
    GREENMANS_ALE_M(ItemID.GREENMANS_ALE_M_, ItemID.BEER_GLASS, 1, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.HERBLORE, 2)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 2, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.STRENGTH, 2, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.DEFENCE, 2, 0)
    }),
    MOONLIGHT_MEAD(ItemID.MOONLIGHT_MEAD, ItemID.BEER_GLASS, 4,
            message = "It tastes like something just died in your mouth."),
    MOONLIGHT_MEAD_M(ItemID.MOONLIGHT_MEAD_M_, ItemID.BEER_GLASS, 6,
            message = "It tastes like something just died in your mouth."),
    SLAYER_RESPITE(ItemID.SLAYERS_RESPITE, ItemID.BEER_GLASS, 1, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.SLAYER, 2)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 2, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.DEFENCE, 2, 0)
    }),
    SLAYER_RESPITE_M(ItemID.SLAYERS_RESPITE_M_, ItemID.BEER_GLASS, 1, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.SLAYER, 4)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 2, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.DEFENCE, 2, 0)
    }),
    /*WIZARD_MIND_BOMB(ItemIdentifiers.WIZARDS_MIND_BOMB, ItemIdentifiers.BEER_GLASS, 1, fun(player: Player) {
        val mStat = if (player.getMaxLevel(Skill.MAGIC) > 49) 3 else 2
        player.skillManager.increaseLevelTemporarily(Skill.MAGIC, (player.getLevel(Skill.MAGIC) * 0.02).toInt() + mStat)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, (1 + player.getLevel(Skill.ATTACK) * 0.05).toInt(), 0)
        player.skillManager.decreaseLevelTemporarily(Skill.STRENGTH, (1 + player.getLevel(Skill.ATTACK) * 0.05).toInt(), 0)
        player.skillManager.decreaseLevelTemporarily(Skill.DEFENCE, (1 + player.getLevel(Skill.ATTACK) * 0.05).toInt(), 0)
    }),*/
    WIZARD_MIND_BOMB_M(ItemID.MATURE_WMB, ItemID.BEER_GLASS, 1, fun(player: Player) {
        val mStat = if (player.getMaxLevel(Skill.MAGIC) > 49) 4 else 3
        player.skillManager.increaseLevelTemporarily(Skill.MAGIC, if (player.getMaxLevel(Skill.MAGIC) > 49) 4 else 3)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 5, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.STRENGTH, 5, 0)
        player.skillManager.decreaseLevelTemporarily(Skill.DEFENCE, 5, 0)
    }),

    WHISKY(ItemID.WHISKY, -1, 5, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 3)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 4, 0)
    }, message = "You drink the whisky. You feel slightly reinvigorated..."),

    VODKA(ItemID.VODKA, -1, 0, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 3)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 3, 0)
    }, message = "You drink the vodka. You feel slightly reinvigorated..."),

    BOTTLE_OF_WINE(ItemID.BOTTLE_OF_WINE, -1, 0, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 3)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 3, 0)
    }, message = "You drink the wine. You feel slightly reinvigorated..."),

    GIN(ItemID.GIN, -1, 2, fun(player: Player) {
        player.skillManager.increaseLevelTemporarily(Skill.STRENGTH, 1)
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 4, 0)
    }, message = "You drink the gin. You feel slightly reinvigorated..."),

    BRANDY(ItemID.BRANDY, -1, 5, fun(player: Player) {
        player.skillManager.decreaseLevelTemporarily(Skill.ATTACK, 4, 0)
    }, message = "You drink the brandy. You feel slightly reinvigorated..."),

    ;

    companion object {
        val listByID = HashMap<Int, Beverage>()

        init {
            for (bev in values())
                listByID[bev.beverageID] = bev
        }

        fun byID(id: Int): Beverage? {
            return listByID[id]
        }
    }
}