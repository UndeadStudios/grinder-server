package com.grinder.game.model.areas.instanced

import com.grinder.game.content.miscellaneous.TravelSystem.fadeTravelAction
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.npc.monster.boss.impl.CerberusBoss
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.resetInteractions
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Animation
import com.grinder.game.model.Boundary
import com.grinder.game.model.Position
import com.grinder.game.model.areas.InstancedBossArea
import com.grinder.game.model.attribute.Attribute
import com.grinder.util.NpcID
import java.util.concurrent.TimeUnit

class CerberusArea(cerberusBoss: CerberusBoss)
    : InstancedBossArea(Boundary(1219, 1256, 1218, 1267))
{
    init {
//        ClippedMapObjects.findObject(ObjectID.PORTCULLIS_4, Position(1239, 1225, 0)).ifPresent { closed ->
//            val cloned = DynamicGameObject.createLocal(
//                ObjectID.PORTCULLIS_4,
//                closed.position.clone().setZ(cerberusBoss.position.z),
//                closed.objectType,
//                closed.face)
//            add(cloned)
//            val opened = DynamicGameObject.createLocal(
//                21771,
//                cloned.position.clone(),
//                cloned.objectType,
//                cloned.face)
//            PassageManager.add(Passage(
//                PassageCategory.GATE,
//                cloned,
//                opened,
//                PassageMode.FORCE,
//                PassageType.SINGLE,
//                PassageState.CLOSED))
//        }
    }

    override fun removeGameObject(entity: GameObject?) {
        super.removeGameObject(entity)
//        val passage = PassageManager.getPassages().lookup(entity?.position)
//        if (passage != null)
//            PassageManager.getPassages().remove(passage)
    }

    override fun isSafeForHardcore() = false

    override fun isCannonProhibited() = true

    companion object {

        fun handleCaveEntrance(player: Player): Boolean {

            if (!player.passedTime(Attribute.GENERIC_ACTION, 2, TimeUnit.SECONDS))
                return true

            if (player.busy())
                return true

            if (player.area is CerberusArea) {
                player.message("You must leave the dungeon area to fight Cerberus again!")
                return true
            }
            player.resetInteractions()
            player.motion.update(MovementStatus.DISABLED)
            player.performAnimation(Animation(844))
            val heightIndex = player.index shl 2
            fadeTravelAction(player, true, true, "", 2, 5, 3) {
                player.moveTo(Position(1240, 1226, heightIndex))
                player.motion.update(MovementStatus.NONE)
                val cerberusBoss = CerberusBoss(NpcID.CERBERUS, Position(1238, 1252, heightIndex))
                val area = CerberusArea(cerberusBoss)
                cerberusBoss.positionToFace = player.position
                cerberusBoss.owner = player
                cerberusBoss.spawn()
                player.positionToFace = Position(player.position.x, player.position.y + 2, heightIndex)
                area.enter(player)
                area.add(cerberusBoss)
            }
            return true
        }
    }
}