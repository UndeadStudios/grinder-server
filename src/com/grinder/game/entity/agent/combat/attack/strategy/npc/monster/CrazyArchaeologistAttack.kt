package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.collision.CollisionPolicy
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.AgentUtil
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.sound.AreaSound
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.Misc
import java.util.*

class CrazyArchaeologistAttack : AttackStrategy<NPC> {

    private enum class Attack {
        SPECIAL_ATTACK, DEFAULT_RANGED_ATTACK, DEFAULT_MELEE_ATTACK
    }

    private var attack = Attack.DEFAULT_RANGED_ATTACK

    override fun createHits(actor: NPC, target: Agent): Array<Hit> {
        if (attack == Attack.SPECIAL_ATTACK)
            return emptyArray()
        var delay = 2
        if (attack == Attack.DEFAULT_MELEE_ATTACK)
            delay = 0
        return arrayOf(Hit(actor, target, this, true, delay))
    }

    fun fetchAttackSound(type: AttackType?): Optional<Sound> {
        return when(type) {
            AttackType.MELEE -> Optional.of(Sound(Sounds.KICK_SOUND))
            AttackType.SPECIAL -> Optional.of(AreaSound(344, 0, 1, 7))
            AttackType.MAGIC -> {
                Optional.of(AreaSound(343, 0, 1, 7))
            }
            else -> Optional.empty()
        }
    }

    override fun sequence(actor: NPC, target: Agent) {
        if (!actor.isNpc || !target.isPlayer) return
        attack = Attack.DEFAULT_RANGED_ATTACK
        if (target.position.getDistance(actor.position) < 2 && Misc.getRandomInclusive(1) == 0) {
            attack = Attack.DEFAULT_MELEE_ATTACK
        }
        if (Misc.getRandomInclusive(10) < 3) {
            attack = Attack.SPECIAL_ATTACK
        }
        actor.say(QUOTES[Misc.getRandomExclusive(QUOTES.size - 1)])
        when (attack) {
            Attack.DEFAULT_RANGED_ATTACK -> {
                actor.performAnimation(RANGED_ATTACK_ANIM)
                Projectile(actor, target, 1259, 40, 20, 31, 43, 0).sendProjectile()
                TaskManager.submit(object : Task(3, target, false) {
                    public override fun execute() {
                        target.performGraphic(RANGED_END_GFX)
                        stop()
                    }
                })
            }
            Attack.SPECIAL_ATTACK -> {
                actor.performAnimation(RANGED_ATTACK_ANIM)
                actor.say("Rain of Knowledge!")
                val targetPos = target.position
                val attackPositions: MutableList<Position> = ArrayList()
                attackPositions.add(targetPos)
                for (i in 0..1) {
                    attackPositions.add(Position(targetPos.x - 1 + Misc.getRandomInclusive(3),
                            targetPos.y - 1 + Misc.getRandomInclusive(3)))
                }
                for (pos in attackPositions) {
                    Projectile(actor.position, pos, 0, 1260, 35, 80, 31, 43, 0, target.size, 0).sendProjectile()
                }
                TaskManager.submit(object : Task(4) {
                    public override fun execute() {
                        for (pos in attackPositions) {
                            target!!.asPlayer.packetSender.sendGlobalGraphic(MAKE_IT_RAIN_START_GFX, pos)
                            AgentUtil.getPlayersInProximity(actor, 10, CollisionPolicy.PROJECTILE).forEach { player: Player ->
                                if (player!!.position == pos) {
                                    player!!.combat
                                            .queue(Damage(Misc.getRandomInclusive(25), DamageMask.REGULAR_HIT))
                                    player!!.asPlayer.packetSender.sendAreaPlayerSound(Sounds.FIRE_WAVE_CONTACT)
                                }
                            }
                        }
                        postHitAction(actor, target)
                        stop()
                    }
                })
                actor.combat.setNextAttackDelay(5)
            }
            Attack.DEFAULT_MELEE_ATTACK -> {
                actor!!.performAnimation(MELEE_ATTACK_ANIM)
            }
        }
    }

    override fun duration(actor: NPC): Int {
        return if (attack == Attack.DEFAULT_MELEE_ATTACK) 3 else actor.baseAttackSpeed
    }

    override fun requiredDistance(actor: Agent): Int {
        if (attack == Attack.DEFAULT_MELEE_ATTACK)
            return 1
        return if (attack == Attack.SPECIAL_ATTACK)
            8
        else
            6
    }

    override fun type(): AttackType? {
        return if (attack == Attack.DEFAULT_MELEE_ATTACK) AttackType.MELEE else AttackType.RANGED
    }

    companion object {
        private val QUOTES = arrayOf(
                "I'm Bellock - respect me!",
                "Get off my site!",
                "No-one messes with Bellock's dig!",
                "These ruins are mine!",
                "Taste my knowledge!",
                "You belong in a museum!")
        private val RANGED_END_GFX = Graphic(305, GraphicHeight.HIGH)
        private val MAKE_IT_RAIN_START_GFX = Graphic(157, GraphicHeight.MIDDLE)
        private val MELEE_ATTACK_ANIM = Animation(423)
        private val RANGED_ATTACK_ANIM = Animation(3353)
    }
}