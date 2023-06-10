package com.grinder.game.entity.agent.player

import com.grinder.util.Misc

/**
 * Holds rights that a [Player] can have, this will be relevant in various
 * game situations; e.g. losing items on death or performing punishments.
 *
 * @author unknown
 * @author Stan van der Bend (added docs, converted to Kotlin)
 */
enum class PlayerRights(var image: String = "") {

    /**
     * Regular player.
     */
    NONE,

    /**
     * Players that are on trial to become a moderator.
     */
    SERVER_SUPPORTER("<img=763>"),

    /**
     * Enforces game rules.
     */
    MODERATOR("<img=740>"),

    /**
     * Oversees moderators, reports to admins.
     */
    GLOBAL_MODERATOR("<img=741>"),

    /**
     * Has extra privileges, helps oversee the staff team, can perform heavy punishments.
     */
    ADMINISTRATOR("<img=742>"),

    /**
     * Has almost all privileges in the game, oversees the staff team, reports to owner.
     */
    CO_OWNER("<img=788>"),

    /**
     * Has all privileges a player can get, makes executive decisions.
     */
    OWNER("<img=743>"),

    /**
     * By far the coolest people in the game, godlike would be an appropriate description.
     */
    DEVELOPER("<img=744>"),

    /**
     * Players that donated 10$ ore more.
     */
    BRONZE_MEMBER("<img=1025>"),

    /**
     * Players that donated 50$ ore more.
     */
    RUBY_MEMBER("<img=745>"),

    /**
     * Players that donated 100$ ore more.
     */
    TOPAZ_MEMBER("<img=746>"),

    /**
     * Players that donated 150$ ore more.
     */
    AMETHYST_MEMBER("<img=747>"),

    /**
     * Players that donated 250$ ore more.
     */
    LEGENDARY_MEMBER("<img=1026>"),

    /**
     * Players that donated 500$ ore more.
     */
    PLATINUM_MEMBER("<img=1027>"),

    /**
     * Players that donated 750 ore more.
     */
    TITANIUM_MEMBER("<img=1227>"),

    /**
     * Players that donated 1000 ore more.
     */
    DIAMOND_MEMBER("<img=1228>"),

    /**
     * Players that are allowed to host bets.
     */
    DICER("<img=770>"),

    /**
     * Players that make youtube videos on a regular basis.
     */
    YOUTUBER("<img=748>"),

    /**
     * Players that maintain the wiki.
     */
    WIKI_EDITOR("<img=796>"),

    /**
     * Players that do graphic design for either the game, or the community.
     */
    DESIGNER("<img=751>"),

    /**
     * Players that are allowed to middleman OSRS to grinder trades.
     */
    MIDDLEMAN("<img=939>"),

    /**
     * Players that are allowed to host community events.
     */
    EVENT_HOST("<img=940>"),

    /**
     * True MVPs of the community.
     */
    VETERAN("<img=941>"),

    /**
     * Retired staff member.
     */
    EX_STAFF("<img=942>"),

    /**
     * Probably someone who donated a lot, or has been important in GS history.
     */
    RESPECTED("<img=943>"),

    /**
     * Content creator/Creative work
     */
    CAMPAIGN_DEVELOPER("<img=1028>"),

    /**
     * Players who contribute to the server with support
     */
    CONTRIBUTOR("<img=1229>"),

    /**
     * Players who earn the member of the month event
     */
    MOTM("<img=1241>"),
    ;

    val isMember: Boolean
        get() = MEMBER.contains(this)
    val isStaff: Boolean
        get() = STAFF.contains(this)
    val isHighStaff: Boolean
        get() = HIGH_STAFF.contains(this)
    val isAdvancedStaff: Boolean
        get() = ADVANCED_STAFF.contains(this)

    /**
     * Check whether both [minimumRights] and this rights are staff,
     * and this rights' ordinal is equal to or greater than the ordinal of [minimumRights].
     */
    fun isStaff(minimumRights: PlayerRights): Boolean {
        require(minimumRights.isStaff)
        return STAFF.contains(this)
                && ordinal >= minimumRights.ordinal
    }

    /**
     * Check whether this rights is present in [playerRights].
     */
    fun anyMatch(vararg playerRights: PlayerRights): Boolean {
        return playerRights.contains(this)
    }

    /**
     * Whether a [Player] with this rights loses items on death.
     */
    fun loseItemsOnDeath(): Boolean {
        return !(this == ADMINISTRATOR || this == DEVELOPER || this == OWNER || this == CO_OWNER)
    }

    /**
     * Format the [name] of these rights into a format that can be represented to players.
     */
    override fun toString(): String {
        return Misc.formatName(name.toLowerCase().replace("_".toRegex(), " "))
    }

    companion object {
        val MEMBER_ORDINAL = BRONZE_MEMBER.ordinal

        /**
         * Contains all donator 'member' rights.
         */
        val MEMBER = setOf(BRONZE_MEMBER, RUBY_MEMBER, TOPAZ_MEMBER, AMETHYST_MEMBER, LEGENDARY_MEMBER, PLATINUM_MEMBER, TITANIUM_MEMBER, DIAMOND_MEMBER)

        /**
         * Contains all low-tier staff member rights.
         */
        val STAFF = setOf(SERVER_SUPPORTER, MODERATOR, GLOBAL_MODERATOR, CAMPAIGN_DEVELOPER, ADMINISTRATOR, CO_OWNER, OWNER, DEVELOPER)

        /**
         * Contains all high-tier staff member rights.
         */
        val ADVANCED_STAFF = setOf(ADMINISTRATOR, CO_OWNER, OWNER, DEVELOPER)

        /**
         * Contains staff member rights that have all possible powers.
         */
        val HIGH_STAFF = setOf(OWNER, DEVELOPER)

        fun forId(id: Int): PlayerRights? {
            return if (id < 0 || id >= values().size) {
                null
            } else values()[id]
        }
    }
}