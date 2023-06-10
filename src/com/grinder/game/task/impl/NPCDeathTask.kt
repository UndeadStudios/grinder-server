package com.grinder.game.task.impl

import com.grinder.game.World
import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc
import com.grinder.game.content.minigame.fightcave.FightCaveNpc
import com.grinder.game.content.minigame.fightcave.monsters.TzTokJad
import com.grinder.game.content.points.ParticipationPoints.addPoints
import com.grinder.game.content.pvm.MonsterKillTracker
import com.grinder.game.content.pvm.WildernessBossSpirit.isBoss
import com.grinder.game.content.pvm.contract.MonsterHunting
import com.grinder.game.content.skill.skillable.impl.slayer.superior.SuperiorSlayerMonsters
import com.grinder.game.entity.agent.combat.event.impl.KilledTargetEvent
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.NPCDropGenerator
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.impl.*
import com.grinder.game.entity.agent.npc.monster.boss.impl.GiantSeaSnakeBoss.SeaSnakeling
import com.grinder.game.entity.agent.npc.monster.boss.impl.IceTrollKingBoss.IceTrollRunt
import com.grinder.game.entity.agent.npc.monster.boss.impl.corporealbeast.CorporealBeastBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.corporealbeast.DarkEnergyCore
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos.GeneralGraardorBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath.VorkathBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath.ZombifiedSpawn
import com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah.ZulrahBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.zulrah.ZulrahBoss.SnakelingMinion
import com.grinder.game.entity.agent.player.Color
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.incInt
import com.grinder.game.model.Animation
import com.grinder.game.model.Skill
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.areas.InstancedArea
import com.grinder.game.model.areas.InstancedBossArea
import com.grinder.game.model.areas.UntypedInstancedBossArea
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.net.packet.interaction.PacketInteractionManager
import com.grinder.util.*
import java.util.*

/**
 * Represent a [Task] that is started when a npc dies (i.e. health reaches 0).
 *
 * @author relex lawl
 * @author Stan van der Bend
 *
 * @param npc       the [NPC] dying.
 * @param delay     the delay per tick of this task.
 * @param instant   should the first tick be instant, or after the delay.
 */
class NPCDeathTask(
        private val npc: NPC,
        delay: Int = 2,
        instant: Boolean = false
) : Task(delay, npc, instant) {

    private val definition = npc.fetchDefinition()

    private val killer = npc.combat.findKiller(false).orElse(null)

    private val duration = definition.deathTime.coerceAtLeast(1)
    private var ticks = duration

    private val area = npc.area

    private val blockRespawn = area is InstancedArea && !area.canRespawnNPC(npc)

    init {
        npc.isDying = true

        val motion = npc.motion
        motion.update(MovementStatus.DISABLED)
        motion.clearSteps()
        motion.cancelTask()
    }

    public override fun execute() {
        try {
            when (ticks) {
                duration -> {

                    val combat = npc.combat
                    if (combat.isBeingAttacked)
                        combat.opponent.combat.clearOpponent(npc)

                    val deathEmote = definition.deathAnim

                    if (deathEmote > 0)
                        npc.performAnimation(Animation(deathEmote, Priority.HIGH))

                    npc.onDeath()

                    if (killer is Player)
                        PacketInteractionManager.handleNPCDeath(killer, npc)

                    if (killer is Player)
                        npc.sendDeathSound(killer)
                }
                0 -> stop()
            }
            ticks--
        } catch (e: Exception) {
            e.printStackTrace()
            ErrorLogging.log("npc_death", e)
            stop()
        }
    }

    override fun stop() {
        super.stop()


        if (killer != null)
            handleKiller()

        if (area != null) {
            area.handleDeath(npc)
            area.leave(npc)
        }

        npc.area = null
        npc.isDying = false
        npc.motion.update(MovementStatus.NONE)
        npc.setEntityInteraction(null)

        if (!World.npcRemoveQueue.contains(npc))
            World.npcRemoveQueue.add(npc)

        if (blockRespawn || ignoreRespawn(npc))
            return

        npc.combat.reset(true)

        var ticksTillRespawn = definition.respawn

        if(killer is Player) {
            killer.ifPlayer { player: Player ->
                if(player.instance != null) {
                    if(!player.instance.doesNPCRespawn(npc)) {
                        ticksTillRespawn = 0;
                    }
                }
            }
        }

        if (ticksTillRespawn > 0)
            TaskManager.submit(NPCRespawnTask(npc, ticksTillRespawn))
    }

    private fun handleKiller() {

        if (killer is Player) {
            val killTracker = killer.killTracker
            if (isHighTier(npc) && npc !is FightCaveNpc && npc !is AquaisNeigeNpc) {
                killTracker.incrementBossKillCount(1)
                AchievementManager.processFor(AchievementType.BOSS_OBLITERATOR, killer)
                val bossesKilled = killTracker.bossesKilled
                handleHighTierKillReward(killer, bossesKilled)
            } else
                killTracker.incrementMonsterKillCount(1)


            processMonsterKillAchievements(killer, npc)
            if (KamilBoss.isKamilMinion(npc.id)/* && AreaManager.KAMIL.contains(killer)*/ && killer.getPosition().inside(2872, 3717, 2901, 3766)) {
                val kc = killer.incInt(Attribute.KAMIL_MINION_KILL_COUNT, 1)
                killer.message("Your kill count is @dre@$kc</col>!")
            }
        }

        if (npc is Monster) {
            npc.killedBy(killer)
        }

        killer.combat.submit(KilledTargetEvent(killer, npc))
        killer.ifPlayer { player: Player ->
            if (npc !is FightCaveNpc && npc !is AquaisNeigeNpc) {
                if (player.gameMode.isSpawn) {
                    player.sendMessage("NPC's do not drop items when being slain by spawn game mode players.");
                } else {
                    if (npc != null
                        && npc.id != NpcID.KING_BLACK_DRAGON_6502
                        && npc.id != NpcID.GENERAL_GRAARDOR_6494
                        && npc.id != NpcID.JUNGLE_DEMON_6382
                        && npc.id != NpcID.MUTANT_TARN_9346
                        && npc.id != NpcID.CORPOREAL_BEAST_9347
                        && npc.id != NpcID.BKT_9350
                        && npc.id != NpcID.KAMIL_6345
                    )
                    NPCDropGenerator.start(player, npc)
                }
                if (killer is Player) {
                    val killTracker = killer.killTracker
                    if (killTracker.fighting === npc) {
                        val difference = MonsterKillTracker.track(killer, npc)
                        MonsterHunting.onNpcKill(killer, npc, difference)
                    }
                }
            }
            if (blockRespawn && !npc.removeRespawnMessage && (area is UntypedInstancedBossArea || area is InstancedBossArea) && npc is Boss)
                player.message("The slain boss will not re-spawn automatically, please re-enter the area.", Color.RED)
            npc.botHandler?.onDeath(player)
            area?.defeated(player, npc)
        }
    }

    private fun handleHighTierKillReward(player: Player, bossesKilled: Int) {
        var cashRewardAmount = -1
        var participationPoints = 0
        when {
            bossesKilled % 100 == 0 -> cashRewardAmount = 5_000_000
            bossesKilled % 50 == 0 -> {
                cashRewardAmount = 2_500_000
                participationPoints = 25
            }
            bossesKilled % 25 == 0 -> {
                cashRewardAmount = 1_000_000
                participationPoints = 15
            }
            bossesKilled % 10 == 0 -> {
                cashRewardAmount = 500_000
                participationPoints = 10
            }
            bossesKilled % 5 == 0 -> {
                participationPoints = 5
            }
        }
        if (participationPoints > 0)
            addPoints(player, participationPoints, "@dre@from slaying bosses</col>.")

        if (cashRewardAmount > 0 && participationPoints > 0) {
            val reward = Item(ItemID.COINS, cashRewardAmount)
            ItemContainerUtil.addOrDrop(player.inventory, player, reward)
            DialogueBuilder(DialogueType.ITEM_STATEMENT)
                    .setItem(reward.id, 200, "Cash reward")
                    .setText(
                            "You received @dre@" + Misc.format(cashRewardAmount) + "</col> gp",
                            "and @dre@$participationPoints</col> participation points",
                            "for killing a total of @dre@$bossesKilled</col> high tier npcs."
                    )
                    .start(player)
        } else if (participationPoints > 0 && cashRewardAmount <= 0) {
            DialogueBuilder(DialogueType.STATEMENT)
                    .setText(
                            "You received @dre@$participationPoints</col> participation points",
                            "for killing a total of @dre@$bossesKilled</col> high tier npcs."
                    )
                    .start(player)
        } else if (cashRewardAmount > 0 && participationPoints == 0) {
            val reward = Item(ItemID.COINS, cashRewardAmount)
            ItemContainerUtil.addOrDrop(player.inventory, player, reward)
            DialogueBuilder(DialogueType.ITEM_STATEMENT)
                .setItem(reward.id, 200, "Cash reward")
                .setText(
                    "You received @dre@" + Misc.format(cashRewardAmount) + "</col> gp",
                    "for killing a total of @dre@$bossesKilled</col> high tier npcs."
                )
                .start(player)
        }
    }

    companion object {
        private fun ignoreRespawn(npc: NPC): Boolean {
            if (npc.fetchDefinition().respawn == -1 || npc.fetchDefinition().respawn == 0)
                return true
            if (isBoss(npc))
                return true
            if (npc is FightCaveNpc
                    || npc is VorkathBoss
                    || npc is ZulrahBoss
                    || npc is SnakelingMinion
                    || npc is IceTrollRunt
                    || npc is SeaSnakeling
                    || npc is DarkEnergyCore
                    || npc is CerberusBoss
                    || npc is ZombifiedSpawn
            )
                return true
            val npcId = npc.id
            return if (SuperiorSlayerMonsters.forId(npcId).isPresent) {
                true
            } else npcId == NpcID.AHRIM_THE_BLIGHTED
                    || npcId == NpcID.DHAROK_THE_WRETCHED
                    || npcId == NpcID.GUTHAN_THE_INFESTED
                    || npcId == NpcID.KARIL_THE_TAINTED
                    || npcId == NpcID.TORAG_THE_CORRUPTED
                    || npcId == NpcID.VERAC_THE_DEFILED
        }

        /**
         * Contains high tier npcs that reward player participation points.
         */
        private val HIGH_TIER_NPC_IDS: Set<Int> = HashSet(
                listOf(
                        NpcID.GREATER_ABYSSAL_DEMON,
                        NpcID.MITHRIL_DRAGON,
                        NpcID.ADAMANT_DRAGON,
                        NpcID.RUNE_DRAGON,
                        NpcID.RUNE_DRAGON_8031,
                        NpcID.LIZARDMAN_SHAMAN_7573,
                        NpcID.LIZARDMAN_SHAMAN_7574,
                        NpcID.LIZARDMAN_SHAMAN_7744,
                        NpcID.LIZARDMAN_SHAMAN_7745,
                        NpcID.TARN
                )
        )

        private fun isHighTier(npc: NPC): Boolean {
            val def = npc.fetchDefinition()
            val combatLevel = def.combatLevel
            return ((combatLevel > 300 || npc is Boss || HIGH_TIER_NPC_IDS.contains(def.id))
                    && combatLevel != 304 && combatLevel != 380 && combatLevel != 338 && def.id != NpcID.DAD && def.id != NpcID.LAVA_DRAGON && def.id != 9021)
        }

        private fun processMonsterKillAchievements(player: Player, npc: NPC) {
            val skills = player.skillManager
            AchievementManager.processFor(AchievementType.I_DID_IT_MY_SELF, player)
            AchievementManager.processFor(AchievementType.MONSTER_MURDERER, player)
            AchievementManager.processFor(AchievementType.MONSTER_KILLER, player)
            when {
                player.killedBarrows.size == 6 -> AchievementManager.processFor(AchievementType.BARROWS_ENEMY, player)
                npc is GeneralGraardorBoss -> AchievementManager.processFor(AchievementType.BRAVE_WARRIOR, player)
                npc is CorporealBeastBoss -> {
                    if (skills.getCurrentLevel(Skill.HITPOINTS) == skills.getMaxLevel(Skill.HITPOINTS))
                        AchievementManager.processFor(AchievementType.BEAST_SLAYER, player)
                }
                npc.id == NpcID.CRAZY_ARCHAEOLOGIST -> AchievementManager.processFor(
                        AchievementType.RECLAIMING_THE_RUINS,
                        player
                )
                npc is VenenatisBoss -> AchievementManager.processFor(AchievementType.SPIDER_HUNTER, player)
                npc is CallistoBoss -> AchievementManager.processFor(AchievementType.BEAR_HUNTER, player)
                npc is ScorpiaBoss -> AchievementManager.processFor(AchievementType.SURVIVING_THE_PIT, player)
                npc is TzTokJad -> {
                    AchievementManager.processFor(AchievementType.JAD_HEAD, player)
                    AchievementManager.processFor(AchievementType.FIRE_WARRIOR, player)
                }
                npc is MutantTarnBoss -> {
                    if (skills.getCurrentLevel(Skill.PRAYER) == 0)
                        AchievementManager.processFor(AchievementType.DREAM_MENTOR, player)
                }
            }
        }
    }
}