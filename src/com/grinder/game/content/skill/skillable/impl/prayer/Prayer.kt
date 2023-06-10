package com.grinder.game.content.skill.skillable.impl.prayer

import com.grinder.game.content.minigame.MinigameManager.BATTLE_ROYALE
import com.grinder.game.content.minigame.MinigameManager.WEAPON_GAME
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler.PrayerType
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.Skill
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.sound.Sounds
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * This class handles prayer related mechanics, mostly draining.
 *
 * https://oldschool.runescape.wiki/w/Prayer
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   18/04/2020
 * @version 1.0
 */
class Prayer {

    private var drainCount = 0.0
    private val prayerTimerMap = HashMap<PrayerType, Long>()
    private var depleted = false

    fun sequence(player: Player){

        if(player.getLevel(Skill.PRAYER) == 0){
            if (player.prayersUsed().isNotEmpty()) {
                PrayerHandler.deactivatePrayers(player)
                player.message("You ran out of Prayer points!", 1)
                player.points.increase(AttributeManager.Points.RUN_OUT_OF_PRAYER_TIMES, 1) // Increase points

                player.playSound(Sounds.PRAYER_DEPLETED)
                drainCount = 0.0
                depleted = true
            }
            return
        }

        depleted = false
        var drainAmount = 0

        val equipmentBonus = PrayerHandler.getEquipmentBonus(player)
        val drainingPrayers = HashSet<PrayerType>()

        for (entry in prayerTimerMap) {
            val type = entry.key
            val lastEffect = entry.value
            val interval = type.drainInterval * (1.0 + equipmentBonus.div(30.0))
            val secondPassed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - lastEffect)
                    .div(1_000.0)

            // Member rank benefit
            if (player.wildernessLevel <= 0 && player.area != BATTLE_ROYALE.boundaries() && player.area != WEAPON_GAME.boundaries()) {
                when {
                    PlayerUtil.isDiamondMember(player) -> {
                        drainCount *= 0.65;
                    }
                    PlayerUtil.isTitaniumMember(player) -> {
                        drainCount *= 0.72;
                    }
                    PlayerUtil.isPlatinumMember(player) -> {
                        drainCount *= 0.80;
                    }
                    PlayerUtil.isLegendaryMember(player) -> {
                        drainCount *= 0.82;
                    }
                    PlayerUtil.isAmethystMember(player) -> {
                        drainCount *= 0.85;
                    }
                    PlayerUtil.isTopazMember(player) -> {
                        drainCount *= 0.90;
                    }
                }
            }

            if(secondPassed > interval){
                drainingPrayers.add(type)
                prayerTimerMap[type] = System.nanoTime()
            }
        }
        
        val drainEffect = drainingPrayers.sumBy { it.drainEffect }
        val drainResistance = max(1.0, 60.0 + (equipmentBonus * 2.0))

        drainCount += drainEffect.toDouble()

        while (drainCount > drainResistance) {
            drainCount -= drainResistance
            drainAmount++
        }

        drainAmount += drainingPrayers.size

        if (drainAmount > 0) {
            player.decreaseLevel(Skill.PRAYER, drainAmount)

            if(player.getLevel(Skill.PRAYER) <= 0){
                PrayerHandler.deactivatePrayers(player)
                player.message("You ran out of Prayer points!", 1)
                player.points.increase(AttributeManager.Points.RUN_OUT_OF_PRAYER_TIMES, 1) // Increase points

                player.playSound(Sounds.PRAYER_DEPLETED)
                drainCount = 0.0
                depleted = true
            }
        }

    }

    fun activatePrayer(prayerType: PrayerType){
        prayerTimerMap[prayerType] = System.nanoTime()
    }

    fun deActivatePrayer(prayerType: PrayerType){
        prayerTimerMap.remove(prayerType)
    }
}