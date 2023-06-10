package com.grinder.game.content.miscellaneous

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.entity.*
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.ButtonActions
import com.grinder.game.model.attribute.Attribute
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * The rank chooser allows the player to select the rank they want to be
 * displayed.
 *
 * @author Blake
 */
object RankChooser {

    private const val INTERFACE_ID = 59000
    private const val BUTTON_START = 59014
    private const val DEFAULT_RANK_STRING_ID = 59004
    private const val SELECTED_RANK_STRING_ID = 59006
    private const val CONFIRM_BUTTON_ID = 59008

    private val BUTTON_END = BUTTON_START + PlayerRights.values().size + 1

    init {
        ButtonActions.onClick(BUTTON_START..BUTTON_END){
            if(!player.BLOCK_ALL_BUT_TALKING && !player.isInTutorial)
                selectRank(player, id)
        }
        ButtonActions.onClick(CONFIRM_BUTTON_ID) {
            if(!player.BLOCK_ALL_BUT_TALKING && !player.isInTutorial)
                confirmRank(player)
        }
    }

    /**
     * Attempts to select the current rank.
     *
     * @param player the [Player] selecting a rank
     */
    private fun selectRank(player: Player, buttonId: Int) {
        var index = buttonId - BUTTON_START + 1;
        if (index >= PlayerRights.BRONZE_MEMBER.ordinal) {
            index += PlayerRights.BRONZE_MEMBER.ordinal + 1
        }
        val selected = PlayerRights.forId(index)
        if (selected != null) {
            player.setString(Attribute.SELECTED_RANK, selected.name)
            player.packetSender.sendString(SELECTED_RANK_STRING_ID, if (selected.name.lowercase(Locale.getDefault()) == "motm") "MOTM" else selected.toString())
        }
    }

    /**
     * Attempts to confirm the current rank.
     *
     * @param player the [Player] confirming the selected rank
     */
    private fun confirmRank(player: Player) {

        if(!player.passedTime(Attribute.GENERIC_ACTION, 1, TimeUnit.SECONDS))
            return

        if(player.hasAttribute(Attribute.SELECTED_RANK)){
            val rank = PlayerRights.valueOf(player.getString(Attribute.SELECTED_RANK))
            if (!canSelect(player, rank)) {
                player.message("That rank is unavailable to you.")
                return
            }
            val rights = rank.ordinal
            if (rights != player.crown) {
                player.crown = rights
                player.packetSender.sendRights()
                AchievementManager.processFor(AchievementType.HIDDEN, player)
                player.packetSender.sendMessage("<img=779> You have successfully changed your display rank.")
/*                when (rank) {
                    PlayerRights.RUBY_MEMBER -> AchievementManager.processFor(AchievementType.SPREAD_LOVE, player)
                    PlayerRights.TOPAZ_MEMBER -> AchievementManager.processFor(AchievementType.SUPERIOR_SUPPORT, player)
                    PlayerRights.AMETHYST_MEMBER -> AchievementManager.processFor(AchievementType.EXTREME_SUPPORT, player)
                    else -> {}
                }*/
            } else {
                player.message("You already have that rank.")
            }
        } else
            player.message("Please select a rank first.")
    }

    /**
     * Checks if the player can select the specified rank.
     *
     * @param player the [Player] confirming the selected rank
     * @param selected the selected [PlayerRights].
     *
     * @return `true` if the player can select the rank,
     *          'false' if not.
     */
    private fun canSelect(player: Player, selected: PlayerRights): Boolean {
        if (player.rights.isHighStaff) {
            return true
        } else if (!player.rights.isStaff && selected.isStaff
            && selected != PlayerRights.YOUTUBER
            && selected != PlayerRights.WIKI_EDITOR
            && selected != PlayerRights.DESIGNER
            && selected != PlayerRights.MIDDLEMAN
            && selected != PlayerRights.VETERAN
            && selected != PlayerRights.EX_STAFF
            && selected != PlayerRights.RESPECTED
            && selected != PlayerRights.CAMPAIGN_DEVELOPER
            && selected != PlayerRights.CONTRIBUTOR
            && selected != PlayerRights.MOTM
        ) {
            return false
        } else if (selected == PlayerRights.YOUTUBER && player.getBoolean(Attribute.YOUTUBER)) {
            return true
        } else if (selected == PlayerRights.WIKI_EDITOR && player.getBoolean(Attribute.WIKI_EDITOR)) {
            return true
        } else if (selected == PlayerRights.DESIGNER && player.getBoolean(Attribute.DESIGNER)) {
            return true
        } else if (selected == PlayerRights.MIDDLEMAN && player.getBoolean(Attribute.MIDDLEMAN)) {
            return true
        } else if (selected == PlayerRights.EVENT_HOST && player.getBoolean(Attribute.EVENT_HOST)) {
            return true
        } else if (selected == PlayerRights.VETERAN && player.getBoolean(Attribute.VETERAN)) {
            return true
        } else if (selected == PlayerRights.EX_STAFF && player.getBoolean(Attribute.EX_STAFF)) {
            return true
        } else if (selected == PlayerRights.RESPECTED && player.getBoolean(Attribute.RESPECTED)) {
            return true
        } else if (selected == PlayerRights.CAMPAIGN_DEVELOPER && player.getBoolean(Attribute.CAMPAIGN_DEVELOPER)) {
            return true
        } else if (selected == PlayerRights.CONTRIBUTOR && player.getBoolean(Attribute.CONTRIBUTOR)) {
            return true
        } else if (selected == PlayerRights.MOTM && player.getBoolean(Attribute.MOTM)) {
            return true
        }
/*        if (selected.isMember) {
            if (PlayerUtil.getMemberRights(player).ordinal >= selected.ordinal) {
                AchievementManager.processFor(AchievementType.SPREAD_LOVE, player)
                return true
            }
        }*/
        return player.rights.ordinal >= selected.ordinal
    }

    /**
     * Opens the rank chooser's interface.
     *
     * @param player the [Player] for whom to open the interface
     */
	@JvmStatic
	fun openInterface(player: Player) {
        player.packetSender.sendInterfaceReset()
        player.packetSender.sendString(SELECTED_RANK_STRING_ID, getPlayerSelectedRank(player))
        player.packetSender.sendString(DEFAULT_RANK_STRING_ID, player.rights.toString())
        player.packetSender.sendInterface(INTERFACE_ID)
        var id = BUTTON_START
/*        if (canSelect(player, PlayerRights.NONE)) {
            player.packetSender.sendStringColour(id++, 0x00ff00)
        } else {
            player.packetSender.sendStringColour(id++, 0xff0000)
        }*/
        for (rights in PlayerRights.values()) {
            if (rights == PlayerRights.NONE)
                continue
            if (rights == PlayerRights.BRONZE_MEMBER)
                continue
            if (rights == PlayerRights.RUBY_MEMBER)
                continue
            if (rights == PlayerRights.TOPAZ_MEMBER)
                continue
            if (rights == PlayerRights.AMETHYST_MEMBER)
                continue
            if (rights == PlayerRights.LEGENDARY_MEMBER)
                continue
            if (rights == PlayerRights.PLATINUM_MEMBER)
                continue
            if (rights == PlayerRights.TITANIUM_MEMBER)
                continue
            if (rights == PlayerRights.DIAMOND_MEMBER)
                continue
            if (rights == PlayerRights.DICER)
                continue
            if (canSelect(player, rights))
                player.packetSender.sendStringColour(id++, 0x00ff00)
            else
                player.packetSender.sendStringColour(id++, 0xff0000)
        }
    }

    /**
     * Gets the rank that the player has selected.
     *
     * @param player the [Player] for whom to get the rank of
     * @return the rank of the [player] as a string
     */
    private fun getPlayerSelectedRank(player: Player): String {
        val rights = PlayerRights.forId(player.crown)
        return rights?.toString() ?: player.rights.toString()
    }
}