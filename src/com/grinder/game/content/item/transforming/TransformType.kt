package com.grinder.game.content.item.transforming

/**
 * The type of the transform.
 *
 * @author Blake
 */
internal enum class TransformType {

    DISMANTLE,
    DISMANTLE_WITH_WARNING,
    SERPENTINE_VISAGE,
    REVERT,
    REVERT_NO_WARNING,
    RESTORE,
    RESTORE_NO_WARNING,
    DISSOLVE,
    BREAKDOWN,
    DAMAGE
    ;

    override fun toString() = name.toLowerCase()
}