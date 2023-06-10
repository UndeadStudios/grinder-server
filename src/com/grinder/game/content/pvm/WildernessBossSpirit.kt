package com.grinder.game.content.pvm

import com.grinder.game.World
import com.grinder.game.content.pvm.WildernessBossSpirit.bossGenerators
import com.grinder.game.definition.NpcDropDefinition
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.NPCDropGenerator
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.impl.*
import com.grinder.game.entity.agent.npc.monster.boss.impl.corporealbeast.CorporealBeastBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos.GeneralGraardorBoss
import com.grinder.game.entity.agent.npc.name
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.passedTime
import com.grinder.game.model.ButtonActions
import com.grinder.game.model.Position
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.attribute.AttributeManager.Points
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.interfaces.dialogue.DialogueOptions
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.nameAndQuantity
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.Executable
import com.grinder.util.ItemID
import com.grinder.util.MiscUtils
import com.grinder.util.NpcID
import java.util.concurrent.TimeUnit
import java.util.function.Function

/**
 * Handles special versions of specific bosses, see [bossGenerators],
 * that spawn every 90 minutes in the wilderness.
 *
 * Upon killing a wilderness boss spirit, the killer receives
 * a blood money drop (given that the killer is not an ironman).
 *
 * @author  2012 (for the base)
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   22/04/2020
 * @version 1.0
 */
object WildernessBossSpirit {

    private var activeBoss: Boss? = null
    private var activeLocation: Locations? = null

    private val bossGenerators: Array<Function<Position, Boss>> = arrayOf(
            Function { KingBlackDragonBoss(NpcID.KING_BLACK_DRAGON_6502, it) },
            Function { GeneralGraardorBoss(NpcID.GENERAL_GRAARDOR_6494, it, false) },
            Function { JungleDemonBossSpirit(NpcID.JUNGLE_DEMON_6382, it) },
            Function { MutantTarnBoss(NpcID.MUTANT_TARN_9346, it) },
            Function { CorporealBeastBoss(NpcID.CORPOREAL_BEAST_9347, it) },
            Function { BlackKnightTitanBoss(NpcID.BKT_9350, it, false) },
            Function { KamilBossSpirit(NpcID.KAMIL_6345, it) }
    )

    private val reward = Item(ItemID.BLOOD_MONEY, 50_000)

    private var minutesPassed = 0
    private const val SPAWN_INTERVAL_IN_MINUTES = 60

    init {
        // 100 ticks = 60_000 millis = 60 seconds = 1 minute
        TaskManager.submit(object : Task(100) {
            override fun execute() {
                if (activeBoss == null) {
                    if (++minutesPassed >= SPAWN_INTERVAL_IN_MINUTES) {
                        spawn()
                    } else {
                        val minutesRemaining = minutesRemaining()
                        if (minutesRemaining <= 3) {
                            PlayerUtil.broadcastMessage(
                                "<img=792> A boss spirit is spawning in $minutesRemaining minutes!" +
                                        " Slay the spirit for a reward of @red@${reward.nameAndQuantity()}</col>!"
                            )
                        }
                    }
                }
            }
        })
        ButtonActions.onClick(31311, 31313, 31315) {
            val location = activeLocation
            updateQuestTab(player)
                if (id == 31311) {

                    if (location == null) {
                        player.statement(
                            "There is currently no active Wilderness Spirit Boss event.",
                            "Try again in ${minutesRemainingText()}.")
                    } else {
                            player.statement(
                                "There is an @gre@ACTIVE</col> Wilderness Spirit boss in the Wilderness.",
                                "You can teleport to the boss spirit by clicking the button below.")
                        }
                } else if (id == 31315) {
                    player.statement(
                        "Players who deal 200 or more damage to the Wilderness Spirit boss.",
                        "will be eligible for boss drop and a bonus 50,000 blood money reward.")
                } else {
                    if (location == null) {
                        player.statement(
                            "The Wilderness Spirit Boss event has not yet spawned.", "Try again in ${minutesRemainingText()}.")
                        return@onClick
                    }
                    if (player.passedTime(Attribute.WILDERNESS_BOSS_TELEPORT_TIMER, 120, TimeUnit.SECONDS)) {
                        val warning = StringBuilder()
                        warning.append("Are you sure you want to teleport there? ")
                        if (location != null) {
                            TeleportHandler.buildWildernessWarning(
                                location.position,
                                MiscUtils.getWildernessLevelFrom(player, location.position),
                                warning
                            )
                        }
                        player.dialogueContinueAction = Executable {
                            DialogueManager.start(player, 2523)
                            player.dialogueOptions = object : DialogueOptions() {
                                override fun handleOption(player1: Player, option: Int) {
                                    player1.packetSender.sendInterfaceRemoval()
                                    if (option == 1) {

                                        if (location != null) {
                                            if (TeleportHandler.checkReqs(
                                                    player,
                                                    location.position,
                                                    true,
                                                    false,
                                                    player.spellbook.teleportType
                                                )
                                            ) {
                                                TeleportHandler.teleport(
                                                    player,
                                                    location.position,
                                                    player.spellbook.teleportType,
                                                    false,
                                                    false
                                                )
                                                PlayerUtil.broadcastMessage("<img=792> ${player.username} has just teleported to the Wilderness boss event!")
                                                PlayerUtil.broadcastMessage("<img=792> You can teleport to the boss event using your quest tab.")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        DialogueManager.sendStatement(player, warning.toString())
                    } else {
                        player.sendMessage("You can only teleport to the Wilderness boss event once every two minutes.")
                    }
                }
            }
        }

    fun updateQuestTab(player: Player) {
        if (activeLocation != null) {
            player.packetSender.sendString(31312, "@gre@Spawned</col>", true)
        } else {
            player.packetSender.sendString(31312, "${minutesRemaining()} min", true)
        }
        player.packetSender.sendString(31314, activeLocation?.description ?: "N/A", true)
        player.packetSender.sendString(31316, "", true)
    }

    fun spawn() {
        activeBoss?.let {
            World.npcAddQueue.add(it)
        }
        val location = Locations.values().random()
        val boss = bossGenerators.random().apply(location.position.clone())
        boss.onKilled {
            val damageMap = boss.combat.damageMap
            val playerList = mutableSetOf<Player>()

            if (damageMap.isNotEmpty()) {
                when (damageMap.size) {
                    1 -> playerList.add(damageMap.keys.toTypedArray()[0] as Player)
                    else -> {
                        damageMap.forEach { (agent, totalTrackedDamage) ->
                            if (totalTrackedDamage.damage < 200 || agent !is Player)
                                return@forEach

                            if (!playerList.contains(agent))
                            playerList.add(agent)
                        }
                    }
                }
            } else {
                // This shouldn't happen but just to be sure. :>
                playerList.add(it.asPlayer)
            }

            for (player in playerList) {
                if (player.gameMode.isAnyIronman) {
                    player.message("You're not eligible for blood money reward as an Iron Man.")
                } else if (player.gameMode.isSpawn) {
                    player.message("You're not eligible for blood money reward in spawn game mode.")
                } else {
                    player.createGroundItem(reward.clone(), boss.position)
                    val npcDropGenerator = NPCDropGenerator(player, NpcDropDefinition.get(boss.id).get())
                    //if (npcDropGenerator.dropList.size > 0)
                        //player.createGroundItem(npcDropGenerator.dropList.shuffled()[0], boss.position)
                        NPCDropGenerator.start(player, boss.asNpc)
                }
                PlayerUtil.broadcastMessage(
                    "<img=792> @whi@${player.username} has slain ${boss.name()} for 50,000 blood money!."
                )
                // Increase points
                player.points.increase(Points.WILDERNESS_SPIRIT_SLAIN, 1)
                player.sendMessage("@red@You have completed the Wilderness spirit event " + player.points.get(Points.WILDERNESS_SPIRIT_SLAIN) + " times!")
            }
        }
        boss.onEvent {
            if (it == MonsterEvents.REMOVED) {
                this.activeLocation = null
                this.activeBoss = null
                this.activeBoss?.remove()
            }
        }
        boss.spawn()

        PlayerUtil.broadcastMessage(
            "<img=792> A @dre@${boss.name()}</col> has spawned @dre@${location.description}</col>."
                    + " Slaying the boss spirit will reward anyone who deals 200 or more damage @red@${reward.nameAndQuantity()}</col>!"
        )
        PlayerUtil.broadcastMessage(
            "<img=792> You can teleport directly to the boss via the quest tab!"
        )

        activeLocation = location
        activeBoss = boss
        minutesPassed = 0
    }

    fun isBoss(npc: NPC) = activeBoss == npc

    private fun minutesRemaining() = SPAWN_INTERVAL_IN_MINUTES - minutesPassed

    private fun minutesRemainingText(): String {
        val remaining = minutesRemaining()
        return if (remaining > 1)
            "$remaining minutes"
        else
            "$remaining minute"
    }

    enum class Locations(val position: Position, val description: String) {
        BANDIT_CAMP(Position(3033, 3698, 0), "At bandits camp"),

        //AVATARS(Position(3250, 3819, 0), "North of Avatars"),
        MOSS_GIANT_HILL(Position(3135, 3863, 0), "Moss Giant Hill"),
        GDZ(Position(3252, 3883, 0), "Demonic Ruins"),
        RESOURCE(Position(3223, 3914, 0), "Resource Area");
    }
}