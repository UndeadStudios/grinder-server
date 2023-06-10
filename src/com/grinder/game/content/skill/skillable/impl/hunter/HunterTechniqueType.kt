package com.grinder.game.content.skill.skillable.impl.hunter

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.Skill
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.task.TaskManager
import com.grinder.game.task.impl.NPCRespawnTask
import com.grinder.util.ItemID
import java.util.function.BiPredicate

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/11/2019
 * @version 1.0
 */
enum class HunterTechniqueType(
        val precondition: BiPredicate<Player, HunterCatchType> = BiPredicate { _, _ -> true },
        val function: (HunterCatchResult) -> Unit = {}) {
    FALCONRY(
            function = { result ->
                val player = result.player
                val npc = result.npc
                TaskManager.submit(1, 2) { cycle ->
                    when (cycle) {
                        0 -> player.performGraphic(Graphic(920))
                        1 -> Projectile(player, npc, falconToNpcProjectileTemplate)
                                .also {
                                    it.onArrival {

                                        //TODO: complete this
                                        if (result.state == HunterCatchState.FAILED) {
                                            Projectile(npc, player, falconToPlayerProjectileTemplate)
                                                    .sendProjectile()
                                        }
                                    }
                                }.sendProjectile()
                    }
                }
            }),
    BUTTERFLY_NETTING(
            function = { result ->
                val player = result.player
                val npc = result.npc
                val npcName = npc.fetchDefinition().name
                val success = result.state == HunterCatchState.SUCCESS
                if(success){
                    World.npcRemoveQueue.add(npc)
                    TaskManager.submit(NPCRespawnTask(npc, 100))
                    player.sendMessage("You manage to catch the $npcName")
                } else
                    player.sendMessage("You failed to catch the $npcName")
                player.performAnimation(Animation(if (success) 6606 else 6605))
            }),
    IMPLINGS(
            precondition = BiPredicate<Player, HunterCatchType> { player, type ->
                if (!player.equipment.contains(ItemID.BUTTERFLY_NET)) {
                    if (player.skillManager.getCurrentLevel(Skill.HUNTER) < type.requiredLevel - 10) {
                        player.sendMessage("You need a butterfly net to catch these.")
                        return@BiPredicate false
                    }
                }
                return@BiPredicate true
            },
            function = BUTTERFLY_NETTING.function),
    TRAP;

    companion object {
        val falconToNpcProjectileTemplate = ProjectileTemplate.builder(922)
                .setDelay(44)
                .setSpeed(20)
                .setStartHeight(43)
                .setEndHeight(31)
                .build()
        val falconToPlayerProjectileTemplate = ProjectileTemplate.builder(922)
                .setDelay(44)
                .setSpeed(0)
                .setStartHeight(43)
                .setEndHeight(31)
                .build()
    }
}