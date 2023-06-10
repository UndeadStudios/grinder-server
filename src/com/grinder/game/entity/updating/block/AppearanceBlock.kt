package com.grinder.game.entity.updating.block

import com.google.gson.annotations.Expose
import com.grinder.game.entity.agent.player.Appearance
import com.grinder.game.entity.updating.UpdateBlock
import com.grinder.game.model.item.container.player.Equipment

/**
 * @author L E G E N D
 */
data class BasicAnimationSet(
    val idle: Int = 808,
    val turn: Int = 823,
    val walk: Int = 819,
    val turnBack: Int = 820,
    val turnLeft: Int = 821,
    val turnRight: Int = 822,
    val run: Int = 824
)

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/03/2020
 * @version 1.0
 */
class AppearanceBlock(
    @Expose val appearance: Appearance,
    @Expose val skullIcon: Int,
    @Expose val npcTransformId: Int,
    @Expose val equipment: Equipment,
    @Expose val bas: BasicAnimationSet,
    @Expose val name: Long,
    @Expose val combat: Int,
    @Expose val rights: Int,
    @Expose val crown: Int,
    @Expose val title: String,
    @Expose val hide: Boolean,
    @Expose val colors: Array<Array<Int>>?
) : UpdateBlock() {

    /**
     * If the player is appearing as an npc or not.
     *
     * @return `true` if the player is appearing as an npc, otherwise `false`.
     */
    fun appearingAsNpc() = npcTransformId != -1

    fun updateColors() = colors != null
}