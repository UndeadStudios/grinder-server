package com.grinder.game.content.skill.skillable.impl.hunter

import com.grinder.game.model.Animation
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/11/2019
 * @version 1.0
 */
enum class HunterToolType(
        val requiredItem: Item? = null,
        val emptyItem: Item? = null,
        val containedItem: Item? = null,
        val check: Animation? = null,
        val catch: Animation? = null,
        val waitingObjectId: Int = -1,
        val failedObjectId: Int = -1,
        val successObjectId: Int = -1
) {
    NONE,
    BABY_IMPLING_JAR(containedItem = Item(11238), emptyItem = Item(11260)),
    YOUNG_IMPLING_JAR(containedItem = Item(11240), emptyItem = Item(11260)),
    GOURMET_IMPLING_JAR(containedItem = Item(11242), emptyItem = Item(11260)),
    EARTH_IMPLING_JAR(containedItem = Item(11244), emptyItem = Item(11260)),
    ESSENCE_IMPLING_JAR(containedItem = Item(11246), emptyItem = Item(11260)),
    ECLECTIC_IMPLING_JAR(containedItem = Item(11248), emptyItem = Item(11260)),
    NATURE_IMPLING_JAR(containedItem = Item(11250), emptyItem = Item(11260)),
    MAGPIE_IMPLING_JAR(containedItem = Item(11252), emptyItem = Item(11260)),
    NINJA_IMPLING_JAR(containedItem = Item(11254), emptyItem = Item(11260)),
    DRAGON_IMPLING_JAR(containedItem = Item(11256), emptyItem = Item(11260)),

    RUBY_HARVEST_HARVEST(emptyItem = Item(10012), containedItem = Item( 10020)),
    SAPPHIRE_GLACIALIS_HARVEST(emptyItem = Item(10012), containedItem = Item(10018)),
    SNOWY_KNIGHT_HARVEST(emptyItem = Item(10012), containedItem = Item(10016)),
    BLACK_WARLOCK_HARVEST(emptyItem = Item(10012), containedItem = Item(10014)),

    BIRD_SNARE(requiredItem = Item(ItemID.BIRD_SNARE),
            check = Animation(827),
            waitingObjectId = 9345,
            failedObjectId =  9344,
            successObjectId = 9348),

    BOX_TRAP(requiredItem = Item(ItemID.BOX_TRAP),
            check = Animation(827),
            catch = Animation(5175),
            waitingObjectId = 9380,
            failedObjectId =  9385,
            successObjectId = 9384),

    RABBIT_SNARE(requiredItem = Item(ItemID.RABBIT_SNARE),
            check = Animation(827),
            waitingObjectId = 19333,
            failedObjectId =  19334,
            successObjectId = 19335)
    ;
    
}