package com.grinder.game.content.skill.skillable.impl.hunter

import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Boundary
import com.grinder.game.model.NPCActions
import com.grinder.game.model.areas.Area
import java.util.*

/**
 * This is currently not used for implementation!
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/11/2019
 * @version 1.0
 */
open class HunterArea(boundary: Boundary, vararg types: HunterCatchType) : Area(boundary) {

    init {
        for(type in types){
            NPCActions.onClick(type.npcId) {

                val player = it.player
                val npc = it.npc

                if(contains(npc)) {
                    if (!player.clickDelay.elapsed(600)){
                        if (type.technique.precondition.test(player, type)) {
                            val action = HunterCatchAction(type, npc)
                            SkillUtil.startActionTask(player, action)
                        }
                    }
                    return@onClick true
                }
                return@onClick false
            }
        }
    }

    override fun isMulti(agent: Agent?) = false
    override fun handleDeath(player: Player?, killer: Optional<Player>?) = false
    override fun handleDeath(npc: NPC?) = false
    override fun canAttack(attacker: Agent?, target: Agent?) = true

    override fun canEat(player: Player?, itemId: Int) = true
    override fun process(agent: Agent?) {

    }

    override fun canTeleport(player: Player?) = true
    override fun canTrade(player: Player?, target: Player?) = true

    override fun canDrink(player: Player?, itemId: Int) = true

    override fun handleObjectClick(player: Player?, obj:GameObject, type: Int) = false
    override fun onPlayerRightClick(player: Player?, rightClicked: Player?, option: Int) {}
    override fun dropItemsOnDeath(player: Player?, killer: Optional<Player>?) = true
    override fun defeated(player: Player?, agent: Agent?) {}

}