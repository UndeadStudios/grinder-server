package com.grinder.game.entity

/**
 * The enumerated type whose elements represent the different types of
 * node implementations.
 *
 * @author lare96 <http:></http:>//github.com/lare96>
 * @author Major (took two methods from apollo)
 */
enum class EntityType {

    /**
     * The element used to represent the [com.grinder.game.model.item.Item] implementation.
     */
    ITEM,

    /**
     * The element used to represent the [com.grinder.game.entity.object.GameObject] implementation.
     */
    DYNAMIC_OBJECT, STATIC_OBJECT, GRAPHIC,

    /**
     * A projectile (e.g. an arrow).
     */
    PROJECTILE,

    /**
     * The element used to represent the [com.grinder.game.entity.agent.player.Player] implementation.
     */
    PLAYER,

    /**
     * The element used to represent the [com.grinder.game.entity.agent.npc.NPC] implementation.
     */
    NPC;

    /**
     * Returns whether or not this EntityType is for a Mob.
     *
     * @return `true` if this EntityType is for a Mob, otherwise `false`.
     */
    val isMob: Boolean
        get() = this == PLAYER || this == NPC

    /**
     * Returns whether or not this EntityType should be short-lived (i.e. not added to its regions local objects).
     *
     * @return `true` if this EntityType is short-lived.
     */
    val isTransient: Boolean
        get() = this == PROJECTILE || this == GRAPHIC
}