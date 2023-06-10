package com.grinder.game.entity.agent.npc.monster.boss.impl.god.zamorak

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds.ONE_THIRD
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds.THREE_FOURTH
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.God
import com.grinder.game.entity.agent.player.decreaseLevel
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.Skill
import com.grinder.game.model.areas.godwars.GodChamber
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import java.util.*
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   30/09/2019
 * @version 1.0
 */
class KrilTsutsarothBoss(npcId: Int, position: Position, inGodWars: Boolean)
    : God(npcId, position, inGodWars), AttackProvider {

    init {
        race = MonsterRace.DEMON
        combat.onOutgoingHitApplied {
            if(attackType == AttackType.SPECIAL){
                target?.ifPlayer {
                    it.decreaseLevel(Skill.PRAYER, Misc.random(10, 20))
                    it.message("K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.");
                }
            }
        }
    }

    override fun generateAttack() = BossAttack(this)

    override fun chamber() = GodChamber.ZAMORAK

    override fun randomizeAttack() {
        var useSpecial = false
        combat.target?.let {
            if(it.isPlayer){
                if(it.asPlayer.hasActivePrayer(PrayerHandler.PROTECT_FROM_MELEE)){
                    if(Misc.randomChance(20F)){
                        useSpecial = true
                    }
                }
            }
        }
        if(useSpecial){
            bossAttack.setType(AttackType.SPECIAL)
        } else
            super.randomizeAttack()
    }

    override fun getMaxHit(type: AttackType): Int {
        return when(type){
            AttackType.MAGIC -> 30
            AttackType.MELEE -> 46
            AttackType.SPECIAL -> 49
            else -> super.getMaxHit(type)
        }
    }

    override fun attackTypes(): AttackTypeProvider = AttackType.builder()
            .add(ONE_THIRD, AttackType.MELEE)
            .add(THREE_FOURTH, AttackType.MAGIC)
            .build()

    override fun attackRange(type: AttackType) = when(type) {
        AttackType.SPECIAL,
        AttackType.MAGIC -> 8
        else -> 2
    }
    override fun maxTargetsHitPerAttack(type: AttackType) = 5
    override fun fetchAttackDuration(type: AttackType?) = 6
    override fun getAttackAnimation(type: AttackType?) = when(type){
        AttackType.MELEE -> Animation(4680)
        AttackType.MAGIC -> Animation(6948)
        AttackType.SPECIAL -> Animation(6950)
        else -> Animation(attackAnim)
    }
    override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = when(type){
        AttackType.MAGIC -> {
            Stream.of(object : ProjectileTemplate {
                override fun sourceSize() = 4
                override fun sourceOffset() = 0
                override fun projectileId() = 1225
                override fun startHeight() = 40
                override fun endHeight() = 35
                override fun curve() = 5
                override fun lifetime() = if (asNpc.combat.target != null) (10 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 10
                override fun delay() = 50
            })
        }
        else -> Stream.empty()
    }
    override fun fetchHits(type: AttackType?) = HitTemplate
            .builder(if(type == AttackType.SPECIAL) AttackType.MAGIC else type)
            .setDelay(when(type) {
                AttackType.SPECIAL,
                AttackType.MAGIC -> if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target)
                else -> 1
            }).buildAsStream()

    override fun fetchTextAboveHead(type: AttackType?) = when {
        type == AttackType.SPECIAL -> Optional.of("YARRRRRRR!")
        Misc.randomChance(7.5F) -> Optional.of(MESSAGES.random())
        else -> Optional.empty()
    }

    companion object {
        val MESSAGES = arrayOf(
                "Attack them, you dogs!",
                "Forward!",
                "Death to Saradomin's dogs!",
                "Kill them, you cowards!",
                "The Dark One will have their souls!",
                "Zamorak curse them!",
                "Rend them limb from limb!",
                "No retreat!",
                "Flay them all!"
        )
    }
}