package com.grinder.game.model.consumable.potion

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.getLevel
import com.grinder.game.entity.agent.player.getMaxLevel
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.EffectTimer
import com.grinder.game.model.Skill
import com.grinder.game.task.TaskManager
import com.grinder.game.task.impl.OverloadTask
import com.grinder.util.ItemID
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Handles various [Potion] effects.
 *
 * TODO: handle this better, maybe using some event based system.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-25
 */
internal object PotionEffects {
    
    /**
     * Temporarily increases the [skill] level of the [player],
     * the amount by which to increase depends on the [boostType].
     */
    @JvmStatic
    fun genericTemporarySkillLevelBoost(player: Player, skill: Skill, boostType: PotionBoostType) {
        
        val currentLevel = player.getLevel(skill)
        val maxLevel = player.getMaxLevel(skill)
        var boostLevel = (maxLevel * boostType.amount).roundToInt()
        
        if (boostType == PotionBoostType.LOW) 
            boostLevel += 3
        
        val maxLevelCap = maxLevel + boostLevel
        
        if (maxLevel + boostLevel > currentLevel) 
            player.skillManager.increaseLevelTemporarily(skill, boostLevel, maxLevelCap)
    }

    /**
     * Temporarily increases and decreases various
     * [skill][Skill] levels of the [player].
     */
    @JvmStatic
    fun saradominTemporarySkillLevelBoost(player: Player) {
        
        val currentHp = player.getLevel(Skill.HITPOINTS)
        val maxHp = player.getMaxLevel(Skill.HITPOINTS)
        val healAmount = floor(2 + 0.15 * maxHp).toInt()
        var healed = 0
        
        if (healAmount + currentHp > maxHp) 
            healed = maxHp - currentHp
        if (healAmount < 0) 
            healed = 0
        
        if (healed > 0) 
            AchievementManager.processFor(AchievementType.SAFETY_FIRST, healed, player)
        
        val skills = player.skillManager
        skills.increaseLevelTemporarily(Skill.DEFENCE, floor(2 + 0.120 * player.getMaxLevel(Skill.DEFENCE)).toInt())
        skills.increaseLevelTemporarily(Skill.HITPOINTS, healAmount)
        skills.decreaseLevelTemporarily(Skill.ATTACK, floor(0.10 * player.getMaxLevel(Skill.ATTACK)).toInt(), -1)
        skills.decreaseLevelTemporarily(Skill.STRENGTH, floor(0.10 * player.getLevel(Skill.STRENGTH)).toInt(), -1)
        skills.decreaseLevelTemporarily(Skill.MAGIC, floor(0.10 * player.getLevel(Skill.MAGIC)).toInt(), -1)
        skills.decreaseLevelTemporarily(Skill.RANGED, floor(0.10 * player.getLevel(Skill.RANGED)).toInt(), -1)
    }

    /**
     * Temporarily increases and decreases various
     * [skill][Skill] levels of the [player].
     */
    @JvmStatic
    fun zamorakTemporarySkillLevelBoost(player: Player) {
        val skills = player.skillManager
        skills.increaseLevelTemporarily(Skill.ATTACK, floor(2 + 0.20 * player.getMaxLevel(Skill.ATTACK)).toInt())
        skills.increaseLevelTemporarily(Skill.STRENGTH, floor(2 + 0.12 * player.getMaxLevel(Skill.STRENGTH)).toInt())
        skills.decreaseLevelTemporarily(Skill.DEFENCE, floor(2 + 0.10 * player.getMaxLevel(Skill.DEFENCE)).toInt(), -1)
        skills.decreaseLevelTemporarily(Skill.HITPOINTS, floor(2 + 0.10 * player.getLevel(Skill.HITPOINTS)).toInt(), 1)
        skills.increaseLevelTemporarily(Skill.PRAYER, floor(0.10 * player.getMaxLevel(Skill.PRAYER)).toInt(), player.getMaxLevel(Skill.PRAYER))
    }

    /**
     * Restores [amount] of run energy of the [player].
     *
     * @param stamina 'true' if this was invoked through the use of a stamina potion.
     */
    @JvmStatic
    fun restoreEnergy(player: Player, amount: Int, stamina: Boolean) {

        val restored = if (player.runEnergy + amount > 10000)
            10000 - (player.runEnergy + amount - amount)
        else
            amount

        if (restored > 0)
            AchievementManager.processFor(AchievementType.NEED_ENERGY, restored, player)

        player.runEnergy = player.runEnergy + amount

        if (player.runEnergy > 10000)
            player.runEnergy = 10000

        if (stamina)
            player.staminaPot.start(200)

        player.packetSender.sendOrbConfig()
    }

    /**
     * The method that executes the prayer potion action.
     *
     * @param player the player to do this action for.
     * @param restorePotion `true` if this potion is a restore potion, `false` otherwise.
     */
    @JvmStatic
    fun increasePrayerLevel(player: Player, restorePotion: Boolean) {
        val maxLevel = player.getMaxLevel(Skill.PRAYER)

        var min = floor((if (restorePotion) 8 else 7) + (maxLevel / 4).toDouble()).toInt()

        // Holy wrench / prayer cape effect
        if (player.inventory.containsAny(ItemID.HOLY_WRENCH, ItemID.PRAYER_CAPE_T_, ItemID.PRAYER_CAPE) || player.equipment.containsAny(ItemID.PRAYER_CAPE, ItemID.RING_OF_THE_GODS_I_, ItemID.PRAYER_CAPE_T_)) {
            min = floor((if (restorePotion) 8 else 7) + (maxLevel * 27 / 100).toDouble()).toInt()
            }

        player.skillManager.increaseLevelTemporarily(Skill.PRAYER, min, maxLevel)
    }

    /**
     * Starts an [OverloadTask] for the [player].
     */
    @JvmStatic
    fun startOverloadTask(player: Player) {
        TaskManager.submit(OverloadTask(player))
        player.packetSender.sendEffectTimer(300, EffectTimer.OVERLOAD)
    }

    /**
     * Restores the default (combat) stats of the [player].
     */
    @JvmStatic
    fun superRestoreStats(player: Player) {
        for (skillIndex in 0..6) {
            val skill = Skill.values()[skillIndex]
            if (skill == Skill.PRAYER || skill == Skill.HITPOINTS)
                continue
            val maxLevel = player.getMaxLevel(skill)
            val currLevel = player.getLevel(skill)
            if (currLevel < maxLevel) {
                player.skillManager.increaseLevelTemporarily(skill, floor(8 + (maxLevel / 4).toDouble()).toInt(), maxLevel)
            }
        }
    }

    /**
     * Restores the default (combat) stats of the [player].
     */
    @JvmStatic
    fun restoreStats(player: Player) {
        for (skillIndex in 0..6) {
            val skill = Skill.values()[skillIndex]
            if (skill == Skill.PRAYER || skill == Skill.HITPOINTS)
                continue
            val maxLevel = player.getMaxLevel(skill)
            val currLevel = player.getLevel(skill)
            if (currLevel < maxLevel) {
                player.skillManager.increaseLevelTemporarily(skill, floor(10 + (maxLevel * 3 / 10).toDouble()).toInt(), maxLevel)
            }
        }
    }

    /**
     * Boosts the [skill] level of the argued [player].
     */
    @JvmStatic
    fun boostSkillLevelTemporarily(player: Player, skill: Skill) {
        val maxLevel = player.getMaxLevel(skill)
        val boostLevel = 3
        val cap = maxLevel + boostLevel
        if (maxLevel + boostLevel > player.getLevel(skill))
            player.skillManager.increaseLevelTemporarily(skill, boostLevel, cap)
    }

    /**
     * Temporarily increases and decreases various
     * [skill][Skill] levels of the [player].
     */
    @JvmStatic
    fun sanfewTemporarySkillLevelBoost(player: Player) {
        for (index in 0..6) {

            val skill = Skill.values()[index]

            if (skill == Skill.HITPOINTS)
                continue

            val maxLevel = player.getMaxLevel(skill)
            val currLevel = player.getLevel(skill)

            if (currLevel < maxLevel) {

                if (skill == Skill.PRAYER && player.inventory.contains(ItemID.HOLY_WRENCH) || player.inventory.contains(ItemID.PRAYER_CAPE_T_) || player.inventory.contains(ItemID.PRAYER_CAPE) || player.equipment.contains(ItemID.PRAYER_CAPE)
                    || player.equipment.contains(ItemID.RING_OF_THE_GODS_I_) || player.equipment.contains(ItemID.PRAYER_CAPE_T_)) {
                player.skillManager.increaseLevelTemporarily(Skill.PRAYER, floor(4 + (maxLevel * 32 / 100).toDouble()).toInt(), maxLevel)
                }
                player.skillManager.increaseLevelTemporarily(skill, floor(4 + (maxLevel * 3 / 10).toDouble()).toInt(), maxLevel)
            }
        }
    }

    /**
     * Makes the [player] immune to dragon-fire for a total of [duration] seconds,
     * or extends the current fire immunity timer with [duration] if [extend] is 'true'.
     */
    @JvmStatic
    fun startFireImmunity(player: Player, duration: Int, extend: Boolean) {

        if (extend)
            player.combat.fireImmunityTimer.extendOrStart(duration)
        else
            player.combat.fireImmunityTimer.start(duration)

        player.packetSender.sendEffectTimer(player.combat.fireImmunityTimer.secondsRemaining(), EffectTimer.ANTIFIRE)
    }

    /**
     * Makes the [player] immune to dragon-fire for a total of [duration] seconds,
     * or extends the current fire immunity timer with [duration] if [extend] is 'true'.
     */
    @JvmStatic
    fun startSuperFireImmunity(player: Player, duration: Int, extend: Boolean) {

        if (extend)
            player.combat.superFireImmunityTimer.extendOrStart(duration)
        else
            player.combat.superFireImmunityTimer.start(duration)

        player.packetSender.sendEffectTimer(player.combat.superFireImmunityTimer.secondsRemaining(), EffectTimer.ANTIFIRE)
    }

    /**
     * Nullifies any poison damage and makes the [player]
     * immune to poisons for a total of [duration] seconds.
     */
    @JvmStatic
    fun startPoisonImmunity(player: Player, duration: Int) {
        player.poisonDamage = 0
        player.message("Your poison has been cured.")
        if (duration > 0) {
            player.combat.poisonImmunityTimer.start(duration)
            player.message("You're now immune to poison for another $duration seconds.")
            player.packetSender.sendEffectTimer(duration, EffectTimer.ANTIPOISON)
        }
    }

    /**
     * Nullifies any poison damage and makes the [player]
     * immune to venom for a total of [duration] seconds.
     */
    @JvmStatic
    fun startVenomImmunity(player: Player, duration: Int) {
        player.poisonDamage = 0
        player.venomDamage = 0;
        // TODO: ADD SUPPORT FOR VENOM THE PROPER WAY
        // Remove poison from hp bar
        // Add poison immunity
        // allow players to click on hp bar to auto cure if u have a potion else say a message
        player.message("Your venom has been cured.")
        if (duration > 0) {
            player.combat.poisonImmunityTimer.start(duration)
            player.combat.venomImmunityTimer.start(duration)
            player.message("You're now immune to venom for another $duration seconds.")
            player.packetSender.sendEffectTimer(duration, EffectTimer.ANTI_VENOM)
        }
    }

    /**
     * Make monsters aggressive towards the [player]
     * for a total of [duration] seconds.
     */
    @JvmStatic
    fun startMonsterAggression(player: Player, duration: Int) {
        if (duration > 0) {
            player.combat.aggressivityTimer.start(duration)
            player.message("NPC's are now aggressive towards you for another " + duration / 60 + " minutes.")
            player.packetSender.sendEffectTimer(duration, EffectTimer.AGGRESSIVITY)
        }
    }
}