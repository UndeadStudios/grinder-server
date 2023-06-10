//package com.grinder.game.entity.agent.npc.monster.impl
//
//import com.grinder.game.World
//import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler.PrayerType.PROTECT_FROM_MAGIC
//import com.grinder.game.entity.agent.Agent
//import com.grinder.game.entity.agent.combat.attack.AttackContext
//import com.grinder.game.entity.agent.combat.attack.AttackProvider
//import com.grinder.game.entity.agent.combat.attack.AttackType
//import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds.ONE_FOURTH
//import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds.THREE_FOURTH
//import com.grinder.game.entity.agent.combat.event.CombatState
//import com.grinder.game.entity.agent.combat.event.impl.WyvernIceEvent
//import com.grinder.game.entity.agent.combat.hit.HitTemplate
//import com.grinder.game.entity.agent.combat.subscribe
//import com.grinder.game.entity.agent.npc.monster.Monster
//import com.grinder.game.entity.agent.player.Player
//import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
//import com.grinder.game.entity.agent.player.message
//import com.grinder.game.model.*
//import java.util.*
//import java.util.function.Consumer
//import java.util.stream.Stream
//
//class AncientWyvernMonster(id: Int, position: Position) : Monster(id, position), AttackProvider {
//
//    private var onHitAction = Consumer<Player> {}
//
//    init {
//        combat.subscribe {
//            if(it == CombatState.STARTING_ATTACK){
//                if(attack.type() == AttackType.MAGIC){
//                    // TODO: implement this proper, not sure how osrs handles the two gfxes
//                    World.spawn(TileGraphic(position.clone(), BREATH_START))
//                }
//            }
//            return@subscribe false
//        }
//    }
//
//    override fun attackTypes() = AttackType
//            .builder()
//            .add(ONE_FOURTH, AttackType.MAGIC)
//            .add(THREE_FOURTH, AttackType.MELEE)
//            .build()
//
//    override fun attackRange(type: AttackType) = 1
//
//    override fun fetchAttackDuration(type: AttackType?): Int = if (type == AttackType.MAGIC)
//        6 else 4
//
//    override fun getAttackAnimation(type: AttackType?): Animation = if(type == AttackType.MELEE)
//        Animation(2985) else Animation(2988)
//
//    override fun fetchAttackGraphic(type: AttackType?): Optional<Graphic> = if(type == AttackType.MAGIC)
//        Optional.of(BREATH) else Optional.empty()
//
//    override fun fetchHits(type: AttackType?): Stream<HitTemplate> = HitTemplate
//            .builder(type)
//            .setDelay(0)
//            .also {
//                if(type == AttackType.MAGIC) {
//                    it.setSuccessOrFailedEvent(WyvernIceEvent())
//                    it.setOnSuccessOrFailed(Consumer {  agent ->
//                        if(agent is Player)
//                            onHitAction.accept(agent)
//                    })
//                }
//            }
//            .buildAsStream()
//
//    override fun getMaxHit(type: AttackType, context: AttackContext): Int {
//        if(type == AttackType.MAGIC){
//            if(context.isFightingPlayer){
//                var extendedHit = 35
//                if(EquipmentUtil.isWearingWyvernBreathProtection(context.targetEquipment))
//                    extendedHit -= 25
//                if(context.isTargetUsingPrayer(PROTECT_FROM_MAGIC))
//                    extendedHit -= 20
//                when {
//                    extendedHit < 0 -> {
//                        extendedHit = 0
//                        onHitAction = Consumer { it.message("You're protected from the wyvern ice breath!") }
//                    }
//                    extendedHit > 20 -> {
//                        onHitAction = Consumer {
//                            it.message("The wyvern's ice breath chills you to the bone!")
//                            it.message("You should equip an elemental, mind or dragonfire shield.")
//                            it.say("Ow!")
//                        }
//                    }
//                    else -> onHitAction = Consumer {  }
//                }
//                return extendedHit
//            }
//        }
//        return super.getMaxHit(type, context)
//    }
//
//    companion object {
//        private val BREATH = Graphic(501, GraphicHeight.MIDDLE)
//        private val BREATH_START = Graphic(499, GraphicHeight.MIDDLE)
//    }
//}