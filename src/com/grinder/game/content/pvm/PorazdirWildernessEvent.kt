package com.grinder.game.content.pvm

import com.grinder.game.World
import com.grinder.game.content.pvm.PorazdirWildernessEvent.bossGenerators
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.NPCDropGenerator
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.impl.PorazdirBoss
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.agent.player.createGroundItem
import com.grinder.game.entity.agent.player.statement
import com.grinder.game.model.ButtonActions
import com.grinder.game.model.Position
import com.grinder.game.model.attribute.AttributeManager.Points
import com.grinder.game.model.item.Item
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.NpcID
import java.util.function.Function

/**
 * Handles special versions of specific bosses, see [bossGenerators],
 * that spawn every 8 hours in the wilderness pvp arena.
 *
 * Upon killing a wilderness boss spirit, the killer receives
 * a huge money drop and the boss loot (given that the killer is not an ironman).
 *
 * @author  2012 (for the base)
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   22/04/2020
 * @version 1.0
 */
object PorazdirWildernessEvent {

    var BOSS_RANDOM_DROP_POOL_ITEMS = intArrayOf(
        ItemID.AMULET_OF_FURY,
        ItemID.WIZARD_BOOTS,
        ItemID.BANDOS_BOOTS,
        ItemID.BERSERKER_RING,
        ItemID.ARCHERS_RING,
        ItemID.NECKLACE_OF_ANGUISH,
        ItemID.BARROWS_GLOVES,
        ItemID.ABYSSAL_WHIP,
        ItemID.DRAGON_SQ_SHIELD,
        ItemID.DRAGON_CHAINBODY_2,
        21895,
        ItemID.DRAGON_PLATELEGS,
        ItemID.DRAGON_PLATEBODY,
        ItemID.HOLY_BLESSING,
        ItemID.WAR_BLESSING,
        ItemID.UNHOLY_BLESSING,
        ItemID.SEERS_RING,
        ItemID.ROBIN_HOOD_HAT,

        ItemID.RANGER_GLOVES,
        ItemID.MASTER_WAND,
        ItemID.LIGHT_BALLISTA,
        ItemID.NIGHTMARE_STAFF,
        ItemID.STAFF_OF_THE_DEAD,
        ItemID.SARADOMIN_SWORD,
        15433,
        ItemID._3RD_AGE_CLOAK,
        ItemID.SKULL_SCEPTRE,
        ItemID.ANCIENT_STAFF,
        ItemID.TOKTZ_KET_XIL,
        ItemID.MEAT_TENDERISER,

        22616,
        22619,
        22613,
        22610,
        22622,
        22625,
        22628,
        22631,
        22638,
        22641,
        22644,
        22647,
        22650,
        22653,
        22656,
        ItemID.BLACK_SALAMANDER,
        ItemID.RED_SALAMANDER,
        ItemID.TRIDENT_OF_THE_SEAS_FULL_,
        ItemID.SLAYERS_STAFF,
        ItemID.UNCHARGED_TRIDENT,
        ItemID.TANZANITE_FANG,
    )

    var BOSS_RANDOM_DROP_POOL_RESOURCE_ITEMS = intArrayOf(
        21928,
        21944,
        21326,
        6686,
        9305,
        20849,
        19484,
        11230,
        11212,
        ItemID.FAYRG_BONES_2,
        ItemID.DRAGON_ARROW_P_PLUS_PLUS_,
        ItemID.ANGLERFISH_2,
        ItemID.COOKED_KARAMBWAN_2,
        ItemID.RUNE_ARROW_P_PLUS_PLUS_,
        ItemID.SUPER_RESTORE_4_2,
        ItemID.ASTRAL_RUNE,
        ItemID.SOUL_RUNE,
        ItemID.WRATH_RUNE,
        ItemID.COSMIC_RUNE,
        ItemID.BOLT_RACK,
        ItemID.AMETHYST_JAVELIN_P_PLUS_PLUS_,
        22636,
        22634,
        10034,
        11959,
        ItemID.GRANITE_CANNONBALL,
        ItemID.RING_OF_RECOIL_2,
        ItemID.HARRALANDER_TAR,
        ItemID.TARROMIN_TAR,
        21948,
        21950,
        ItemID.BIG_BONES_2,
        ItemID.BLOOD_RUNE,
        ItemID.ADAMANT_ARROW_P_PLUS_PLUS_,
        ItemID.LAW_RUNE,
        ItemID.CHAOS_RUNE,
        ItemID.FLAX_2,
        ItemID.GRIMY_AVANTOE_2,
        ItemID.GRIMY_CADANTINE_2,
        ItemID.GRIMY_KWUARM_2,
        ItemID.GRIMY_LANTADYME_2
    )

    private var activeBoss: Boss? = null
    private var activeLocation: Locations? = null

    private val bossGenerators: Array<Function<Position, Boss>> = arrayOf(
            Function { PorazdirBoss(NpcID.PORAZDIR, it) },
    )

    private var minutesPassed = 0
    private const val SPAWN_INTERVAL_IN_MINUTES = 480

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
                                "<img=792> Porazdir boss in the Wilderness Event minigame is re-spawning in $minutesRemaining minutes!" +
                                        " Defeating the boss will reward valuable drop loot to all players who deal significant damage."
                            )
                        }
                    }
                }
            }
        })
        ButtonActions.onClick(31318, 31320) {
            val location = activeLocation
            updateQuestTab(player)
                if (id == 31318) {
                    if (location == null) {
                        player.statement(
                            "Porazdir boss has not yet respawned.", "The Wilderness Event minigame boss respawns every 8 hours.")
                    } else {
                    player.statement(
                        "Porazdir respawns once every 8 hours and is now active.",
                        "You can teleport using the teleport menu.")
                    }
                    } else {
                    player.statement(
                        "Defeating the boss will reward a valuable drop loot to all players ", "who deal significant damage.")
            }
        }
    }

    fun updateQuestTab(player: Player) {
        if (activeLocation != null) {
            player.packetSender.sendString(31319, "@gre@Spawned</col>", true)
        } else {
            player.packetSender.sendString(31319, "${minutesRemaining()} min", true)
        }
        player.packetSender.sendString(31321, "", true)
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
                            if (totalTrackedDamage.damage < 50 || agent !is Player)
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
              //  if (player.gameMode.isAnyIronman) {
            //        player.message("You're not eligible for blood money reward as an Iron Man.")
//                } else if (player.gameMode.isSpawn) {
//                    player.message("You're not eligible for blood money reward in spawn game mode.")
    //            } else {
                    player.createGroundItem(Item(995, 10_000_000 + Misc.random(50_000_000)), boss.position)
                    player.createGroundItem(Item(13307,  Misc.random(100_000)), boss.position)
                    player.createGroundItem(Item(BOSS_RANDOM_DROP_POOL_ITEMS[Misc.getRandomInclusive(BOSS_RANDOM_DROP_POOL_ITEMS.size - 1)], 1))
                    player.createGroundItem(Item(BOSS_RANDOM_DROP_POOL_RESOURCE_ITEMS[Misc.getRandomInclusive(BOSS_RANDOM_DROP_POOL_RESOURCE_ITEMS.size - 1)], Misc.random(1000)))

                    //val npcDropGenerator = NPCDropGenerator(player, NpcDropDefinition.get(boss.id).get())
                    //if (npcDropGenerator.dropList.size > 0)
                        //player.createGroundItem(npcDropGenerator.dropList.shuffled()[0], boss.position)
                        NPCDropGenerator.start(player, boss.asNpc)
          //      }
                PlayerUtil.broadcastMessage("<img=789> @red@" + PlayerUtil.getImages(player) + "" + player.username + " has just defeated Porazdir in the Wilderness Event minigame!");
                // Increase points
                player.points.increase(Points.PORAZDIR_BOSS_SLAY_COUNT, 1)
                player.sendMessage("@red@You have slain Porazdir in the Wilderness Event minigame " + player.points.get(Points.PORAZDIR_BOSS_SLAY_COUNT) + " times!")
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
            "<img=792> Porazdir Event minigame boss has respawned after 8 hours from being slain.</col>."
                    + " Defeating the boss will reward valuable drop loot to all players who deal significant damage.</col>!"
        )
        PlayerUtil.broadcastMessage(
            "<img=792> You can teleport to Porazdir in the Wilderness Event minigame using the teleport menu!"
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
        WILDERNESS_EVENT_MINIGAME(Position(3421, 4715, 0), "At the Wilderness Event minigame");
    }
}