package com.grinder.game.entity.agent.combat.hit

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.model.Graphic
import com.grinder.game.model.sound.Sound
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 06/11/2019
 */
class HitTemplateBuilder(val type: AttackType?) {

    private var damageRange : IntRange? = null
    private var successSound: Sound? = null
    private var failedSound: Sound? = null
    private var successGraphic: Graphic? = null
    private var failedGraphic: Graphic? = null
    private var hitAmount = 1
    private var delay = 1
    private var ignorePrayer = false
    private var ignoreAmmunitionEffects = false
    private var ignorePoisonEffects = false
    private var ignoreAttackStats = false
    private var ignoreStrengthStats = false
    private var attackStat = -1
    private var defenceStat = -1
    private var onSuccess = Consumer<Agent> {}
    private var onFailed = Consumer<Agent> {}

    fun setDamageRange(range: IntRange) = also { it.damageRange = range }
    fun setSuccessOrFailedGraphic(graphic: Graphic?) = setSuccessGraphic(graphic).setFailedGraphic(graphic)
    fun setSuccessGraphic(graphic: Graphic?) = also { it.successGraphic = graphic }
    fun setFailedGraphic(graphic: Graphic?) = also { it.failedGraphic = graphic }
    fun setSuccessOrFailedSound(sound: Sound?) = setSuccessSound(sound).setFailedSound(sound)
    fun setSuccessSound(sound: Sound?) = also { it.successSound = sound }
    fun setFailedSound(sound: Sound?) = also { it.failedSound = sound }
    fun setHitAmount(hitAmount: Int)  = also { it.hitAmount = hitAmount }
    fun setDelay(delay: Int) = also { it.delay = delay}
    fun setIgnorePrayer(ignorePrayer: Boolean) = also { it.ignorePrayer = ignorePrayer }
    fun setIgnoreAmmunitionEffects(ignoreAmmunitionEffects: Boolean) = also { it.ignoreAmmunitionEffects = ignoreAmmunitionEffects }
    fun setIgnorePoisonEffects(ignorePoisonEffects: Boolean) = also { it.ignorePoisonEffects = ignorePoisonEffects }
    fun setIgnoreAttackStats(ignoreAttackStats: Boolean) = also { it.ignoreAttackStats = ignoreAttackStats}
    fun setIgnoreStrengthStats(ignoreStrengthStats: Boolean) = also { it.ignoreStrengthStats = ignoreStrengthStats}
    fun setAttackStat(attackStat: Int) = also { this.attackStat = attackStat }
    fun setDefenceStat(defenceStat: Int) = also { this.defenceStat = defenceStat }
    fun setOnSuccessKt(unit: (Agent) -> Unit) =  setOnSuccess(Consumer { unit.invoke(it) })
    fun setOnSuccess(consumer: Consumer<Agent>) = also { it.onSuccess = consumer }
    fun setOnFailedKt(unit: (Agent) -> Unit) =  setOnFailed(Consumer { unit.invoke(it) })
    fun setOnFailed(consumer: Consumer<Agent>) = also { it.onSuccess = consumer }
    fun setOnSuccessOrFailed(consumer: Consumer<Agent>) = setOnSuccess(consumer).setOnFailed(consumer)

    fun build() = object : HitTemplate {
        override fun damageRange() = Optional.ofNullable(damageRange)
        override fun successSound() = Optional.ofNullable(successSound)
        override fun failedSound() = Optional.ofNullable(successSound)
        override fun successGraphic() = Optional.ofNullable(successGraphic)
        override fun failedGraphic() = Optional.ofNullable(failedGraphic)
        override fun amount() = hitAmount
        override fun defenceStat() = defenceStat
        override fun attackStat() = attackStat
        override fun onSuccess() = onSuccess
        override fun onFailed() = onFailed
        override fun delay() = delay
        override fun ignoreAttackStats() = ignoreAttackStats
        override fun ignoreStrengthStats() = ignoreStrengthStats
        override fun ignorePrayer() = ignorePrayer
        override fun ignoreAmmunitionEffects() = ignoreAmmunitionEffects
        override fun ignorePoisonEffects() = ignorePoisonEffects
        override fun type() = type
    }

    fun buildAsStream(): Stream<HitTemplate> = Stream.of(build())
}
