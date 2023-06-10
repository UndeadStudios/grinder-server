package com.grinder.game.content.item

/**
 * TODO: use ItemIdentifier references instead of hardcoded ids.
 */
object MemberItems {

    /**
     * Contains item ids that are only for Bronze member players.
     */
    private val bronzeMemberItemIds = intArrayOf(20834, 9906, 13655, 21209, 21354, 21314, 20243, 12319, 20240)

    /**
     * Contains item ids that are only for Ruby member players.
     */
    private val rubyMemberItemIds = intArrayOf()

    /**
     * Contains item ids that are only for Topaz member players.
     */
    private val topazMemberItemIds = intArrayOf()

    /**
     * Contains item ids that are only for Amethyst member players.
     */
    private val amethystMemberItemIds = intArrayOf()

    /**
     * Contains item ids that are only for Legendary member players.
     */
    private val legendaryMemberItemIds = intArrayOf()

    /**
     * Contains item ids that are only for Platinum member players.
     */
    private val platinumMemberItemIds = intArrayOf()

    /**
     * Contains item ids that are only for Titanium member players.
     */
    private val titaniumMemberItemIds = intArrayOf()

    /**
     * Contains item ids that are only for Diamond member players.
     */
    private val diamondMemberItemIds = intArrayOf()

    /**
     * Check if the [id] is in [bronzeMemberItemIds].
     */
	@JvmStatic
	fun isBronzeMemberItem(id: Int) = bronzeMemberItemIds.contains(id)

    /**
     * Check if the [id] is in [rubyMemberItemIds].
     */
    @JvmStatic
    fun isRubyMemberItem(id: Int) = rubyMemberItemIds.contains(id)

    /**
     * Check if the [id] is in [topazMemberItemIds].
     */
    @JvmStatic
    fun isTopazMemberItem(id: Int) = topazMemberItemIds.contains(id)

    /**
     * Check if the [id] is in [amethystMemberItemIds].
     */
    @JvmStatic
    fun isAmethystMemberItem(id: Int) = amethystMemberItemIds.contains(id)

    /**
     * Check if the [id] is in [legendaryMemberItemIds].
     */
    @JvmStatic
    fun isLegendaryMemberItem(id: Int) = legendaryMemberItemIds.contains(id)

    /**
     * Check if the [id] is in [platinumMemberItemIds].
     */
    @JvmStatic
    fun isPlatinumMemberItem(id: Int) = platinumMemberItemIds.contains(id)

    /**
     * Check if the [id] is in [titaniumMemberItemIds].
     */
    @JvmStatic
    fun isTitaniumMemberItem(id: Int) = titaniumMemberItemIds.contains(id)

    /**
     * Check if the [id] is in [diamondMemberItemIds].
     */
    @JvmStatic
    fun isDiamondMemberItem(id: Int) = diamondMemberItemIds.contains(id)

}