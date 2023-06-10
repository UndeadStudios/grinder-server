package com.grinder.game.model.areas.impl.slayer

import com.grinder.game.World
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager
import com.grinder.game.content.skill.skillable.impl.slayer.superior.SuperiorSlayerMonsters
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Boundary
import com.grinder.game.model.ForceMovement
import com.grinder.game.model.Position
import com.grinder.game.model.areas.Area
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.game.task.impl.ForceMovementTask
import java.util.*
import java.util.concurrent.TimeUnit


class KaruulmDungeon : Area(DUNGEON_BOUNDS, SLAYER_ONLY_ONE, SLAYER_ONLY_TWO, SLAYER_ONLY_THREE, SAFE_BOUNDS) {

    override fun process(agent: Agent) {
        if (agent is Player) {
            val player = agent.getAsPlayer()
            if (World.tick % 2 == 0 && !isProtectedFromBurn(player) && !player.position.inside(1303, 10187, 1320, 10214) && player.hitpoints > 0 && !player.position.inside(1303, 10187, 1320, 10214)) {
                if (player.isInTutorial)
                    return
                var damage = 4
                if (player.hitpoints - damage < 1) damage = player.hitpoints
                player.combat.queue(Damage.create(damage))
            }
        }
    }

    override fun enter(agent: Agent?) {

        if(agent is Player) {
            super.enter(agent)
        }
    }

    override fun leave(agent: Agent?) {
        if(agent is Player) {
            super.leave(agent)
        }
    }

    override fun canTeleport(player: Player): Boolean {
        return true
    }

    override fun canAttack(attacker: Agent, target: Agent): Boolean {

        if (attacker.isPlayer && target.isNpc) {
            if (SLAYER_ONLY_ONE.contains(attacker.position) || SLAYER_ONLY_TWO.contains(attacker.position) || SLAYER_ONLY_THREE.contains(
                    attacker.position
                )
            ) {
                val npc = target.asNpc
                val pl = attacker.asPlayer
                return if (pl.slayer.task != null && pl.slayer.task.amountLeft > 0 && SlayerManager.isMonsterPartOfTask(
                        pl,
                        npc.fetchDefinition()
                    )
                    || SuperiorSlayerMonsters.forId(npc.id).isPresent
                ) {
                    true
                } else {
                    if (pl.passedTime(timerAttribute, 10, TimeUnit.SECONDS, false))
                        kaalKetJor.start(pl)
                    pl.sendMessage("Kaal-Ket-Jor wants you to stick to your Slayer assignments.")

                    false
                }
            }
        }
        return !(attacker.isPlayer && target.isPlayer)
    }

    override fun canTrade(player: Player, target: Player): Boolean {
        return true
    }

    override fun isMulti(agent: Agent): Boolean {
        return false
    }

    override fun canEat(player: Player, itemId: Int): Boolean {
        return true
    }

    override fun canDrink(player: Player, itemId: Int): Boolean {
        return true
    }

    override fun dropItemsOnDeath(player: Player, killer: Optional<Player>): Boolean {
        return true
    }

    override fun handleObjectClick(player: Player, obj: GameObject, actionType: Int): Boolean {
        when (obj.id) {
            34544 -> {
                if (!isProtectedFromBurn(player) && player.position.inside(1303, 10187, 1320, 10214)) {

                    DialogueBuilder(DialogueType.STATEMENT)
                        .setText(
                            "The floor looks dangerously hot ahead, you will likely need feet protection.",
                            "Are you sure you want to continue?"
                        )
                        .add(DialogueType.OPTION)
                        .firstOption("Continue anyway without the proper footwear.") {
                            it.removeInterfaces()
                            player.motion.clearSteps()
                            if (player.position.y <= 10210 && player.position.x >= 1310) {
                                if (player.forceMovement == null && player.clickDelay.elapsed(2000)) {
                                    val crossDitch = Position(
                                        if (player.position.x < 1322) 2 else -2,
                                        0
                                    )
                                    TaskManager.submit(
                                        ForceMovementTask(
                                            player, 2, ForceMovement(
                                                player.position.clone(),
                                                crossDitch, 0, 70, if (crossDitch.x == 2) 1 else 3, 6132
                                            )
                                        )
                                    )
                                    player.packetSender.sendSound(Sounds.DITCH_JUMP)
                                    player.clickDelay.reset()
                                }
                            } else if (player.position.y <= 10210 && player.position.x <= 1310) {
                                if (player.forceMovement == null && player.clickDelay.elapsed(2000)) {
                                    val crossDitch = Position(
                                        if (player.position.x < 1303) 2 else -2,
                                        0
                                    )
                                    TaskManager.submit(
                                        ForceMovementTask(
                                            player, 2, ForceMovement(
                                                player.position.clone(),
                                                crossDitch, 0, 70, if (crossDitch.x == 2) 1 else 3, 6132
                                            )
                                        )
                                    )
                                    player.packetSender.sendSound(Sounds.DITCH_JUMP)
                                    player.clickDelay.reset()
                                }
                            } else {
                                if (player.forceMovement == null && player.clickDelay.elapsed(2000)) {
                                    val crossDitch = Position(
                                        0,
                                        if (player.position.y < 10216) 2 else -2
                                    )
                                    TaskManager.submit(
                                        ForceMovementTask(
                                            player, 3, ForceMovement(
                                                player.position.clone(),
                                                crossDitch, 0, 70, if (crossDitch.y == 2) 0 else 2, 6132
                                            )
                                        )
                                    )
                                    player.packetSender.sendSound(Sounds.DITCH_JUMP)
                                    player.clickDelay.reset()
                                }
                            }
                        }
                        .addCancel("Nevermind.")
                        .start(player)
                } else {
                    player.motion.clearSteps()
                    if (player.position.y <= 10210 && player.position.x >= 1310) {
                        if (player.forceMovement == null && player.clickDelay.elapsed(2000)) {
                            val crossDitch = Position(
                                if (player.position.x < 1322) 2 else -2,
                                0
                            )
                            TaskManager.submit(
                                ForceMovementTask(
                                    player, 2, ForceMovement(
                                        player.position.clone(),
                                        crossDitch, 0, 70, if (crossDitch.x == 2) 1 else 3, 6132
                                    )
                                )
                            )
                            player.packetSender.sendSound(Sounds.DITCH_JUMP)
                            player.clickDelay.reset()
                        }
                    } else if (player.position.y <= 10210 && player.position.x <= 1310) {
                        if (player.forceMovement == null && player.clickDelay.elapsed(2000)) {
                            val crossDitch = Position(
                                if (player.position.x < 1303) 2 else -2,
                                0
                            )
                            TaskManager.submit(
                                ForceMovementTask(
                                    player, 2, ForceMovement(
                                        player.position.clone(),
                                        crossDitch, 0, 70, if (crossDitch.x == 2) 1 else 3, 6132
                                    )
                                )
                            )
                            player.packetSender.sendSound(Sounds.DITCH_JUMP)
                            player.clickDelay.reset()
                        }
                    } else {
                        if (player.forceMovement == null && player.clickDelay.elapsed(2000)) {
                            val crossDitch = Position(
                                0,
                                if (player.position.y < 10216) 2 else -2
                            )
                            TaskManager.submit(
                                ForceMovementTask(
                                    player, 3, ForceMovement(
                                        player.position.clone(),
                                        crossDitch, 0, 70, if (crossDitch.y == 2) 0 else 2, 6132
                                    )
                                )
                            )
                            player.packetSender.sendSound(Sounds.DITCH_JUMP)
                            player.clickDelay.reset()
                        }
                    }
                }

            }
            34530 -> {
                when (player.position.z) {
                    0 -> player.moveTo(Position(1334, 10205, 1))
                    else -> player.moveTo(Position(1318, 10188, 2))
                }
            }
            34531 -> {
                when (player.position.z) {
                    2 -> player.moveTo(Position(1313, 10188, 1))
                    1 -> player.moveTo(Position(1329, 10205, 0))
                    else -> player.moveTo(Position(1318, 10188, 2))
                }
            }
        }
        return true
    }

    override fun handleDeath(player: Player, killer: Optional<Player>): Boolean {
        return false
    }

    override fun onPlayerRightClick(player: Player, rightClicked: Player, option: Int) {}
    override fun defeated(player: Player, agent: Agent) {}

    override fun handleDeath(npc: NPC): Boolean {
        return false
    }

    companion object {
        private val kaalKetJor = DialogueBuilder()
            .setNpcChatHead(8602)
            .setText("You better leave right now.", "This area is only for people on Slayer tasks.")
        private const val timerAttribute = "slayer_backoff_dialogue"

        private val DUNGEON_BOUNDS = Boundary(1216, 1406, 10112, 10303)
        private val SAFE_BOUNDS = Boundary(1303, 1320, 10187, 10214)
        private val SLAYER_ONLY_ONE = Boundary(1251, 1279, 10147, 10170)  // Wyrms
        private val SLAYER_ONLY_TWO = Boundary(1300, 1336, 10255, 10277)  // Hydras
        private val SLAYER_ONLY_THREE = Boundary(1337, 1366, 10223, 10255)
    }

    private fun isProtectedFromBurn(player: Player): Boolean {
        return (player.equipment.contains(23037) // boots of stone
                || player.equipment.contains(22951) // boots of brimstone
                || player.equipment.contains(21643)) // granite boots
    }

}