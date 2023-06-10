package com.grinder.game.entity.agent.combat.misc


import com.grinder.game.entity.setInt
import com.grinder.game.entity.getInt
import com.grinder.game.entity.passedTime
import com.grinder.game.entity.decInt
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.util.ItemID
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.item.container.player.Equipment
import com.grinder.game.content.minigame.pestcontrol.PestControl
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.GameConstants
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.model.Skill
import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Graphics
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.Item
import com.grinder.util.Misc
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.DoubleStream
import kotlin.math.roundToInt

/**
 * @author Stan van der Bend
 * @since 5-4-19
 */
object CombatEquipment {
    private const val JUSTICIAR_SET_DAMAGE_REDUCTION_BONUS_DIVISOR = 3000.0
    @JvmStatic
    fun findPoisonousWeapon(type: AttackType, attacker: Player): Optional<PoisonType> {

        // TODO: 2019-06-08 This should not be hardcoded!!!
        val wieldingBlowPipe =
            EquipmentUtil.isWearingAtSlot(attacker, EquipmentConstants.WEAPON_SLOT, ItemID.TOXIC_BLOWPIPE)
        val weapon = attacker.combat.weapon ?: return Optional.empty()
        return if (weapon.isThrowable() || type == AttackType.MELEE) PoisonEffect.findPoison(attacker.equipment[EquipmentConstants.WEAPON_SLOT]) else if (type == AttackType.RANGED) if (wieldingBlowPipe) PoisonEffect.findPoison(
            attacker.equipment[EquipmentConstants.WEAPON_SLOT]
        ) else PoisonEffect.findPoison(attacker.equipment[EquipmentConstants.AMMUNITION_SLOT]) else Optional.empty()
    }

    @JvmStatic
    fun handleLavaBlade(player: Player, attacker: Agent) {
        val returnDmg = 5
        if (attacker.isPlayer) {

            // Reduce blade charges
            player.setInt(Attribute.LAVA_BLADE_CHARGES, player.getInt(Attribute.LAVA_BLADE_CHARGES, 125) - 1, 125)

            // Deal damage back to attacker
            attacker.combat.queue(Damage(returnDmg, DamageMask.REGULAR_HIT))

            // Send message to the attacker
            attacker.asPlayer.packetSender.sendMessage("Your opponent's lava blade burns you from it's high heat.")

            // Degrade blade effect
            if (EquipmentUtil.isWearingAtSlot(
                    player,
                    EquipmentConstants.WEAPON_SLOT,
                    EquipmentUtil.LAVA_BLADE_ITEM_ID
                ) && player.getInt(
                    Attribute.LAVA_BLADE_CHARGES, 125
                ) <= 0
            ) {
                player.equipment[EquipmentConstants.WEAPON_SLOT] = Item(-1)
                player.equipment.refreshItems()
                EquipmentBonuses.update(player)
                player.packetSender.sendMessage("Your lava blade has overheated and turned into ashes.")
                player.setInt(Attribute.LAVA_BLADE_CHARGES, 125, 125)
            }
        }
    }

    @JvmStatic
    fun handleInfernalBlade(player: Player, attacker: Agent) {
        val returnDmg = 5
        if (attacker.isPlayer) {

            // Reduce blade charges
            player.setInt(Attribute.INFERNAL_BLADE_CHARGES, player.getInt(Attribute.INFERNAL_BLADE_CHARGES, 250) - 1, 250)

            // Deal damage back to attacker
            attacker.combat.queue(Damage(returnDmg, DamageMask.REGULAR_HIT))

            // Send message to the attacker
            attacker.asPlayer.packetSender.sendMessage("Your opponent's infernal blade burns you from it's high heat.")

            // Degrade blade effect
            if (EquipmentUtil.isWearingAtSlot(
                    player,
                    EquipmentConstants.WEAPON_SLOT,
                    EquipmentUtil.INFERNAL_BLADE_ITEM_ID
                ) && player.getInt(
                    Attribute.INFERNAL_BLADE_CHARGES, 250
                ) <= 0
            ) {
                player.equipment[EquipmentConstants.WEAPON_SLOT] = Item(-1)
                player.equipment.refreshItems()
                EquipmentBonuses.update(player)
                player.packetSender.sendMessage("Your infernal blade has overheated and turned into ashes.")
                player.setInt(Attribute.INFERNAL_BLADE_CHARGES, 250, 250)
            }
        }
    }

    @JvmStatic
    fun handleRingOfLife(
        actorPlayer: Player,
        equipment: Equipment,
        currentHitPoints: Int,
        maxHitPoints: Int,
        ringItem: Item?
    ) {
        if (actorPlayer.isAlive && currentHitPoints <= maxHitPoints / 4 && PestControl.PEST_CONTROL_INSTANCE.outsideMapInstance(
                actorPlayer
            )
        ) {

            //final Minigame minigame = actorPlayer.getMinigame();
            //final boolean isInMinigame = minigame != null;
            val canTeleport = TeleportHandler.checkReqs(
                actorPlayer,
                GameConstants.DEFAULT_DEATH_POSITION,
                true,
                false,
                TeleportType.JEWELRY_RUB
            )
            if (canTeleport) {

                // Defence skill cape effect (Ring of life once every 24 hours)
                if (actorPlayer.equipment.containsAny(
                        ItemID.DEFENCE_CAPE,
                        ItemID.DEFENCE_CAPE_T_
                    ) && actorPlayer.getInt(
                        Attribute.LAST_DEFENCE_CAPE_TELEPORT, 1
                    ) <= 0
                ) {
                    if (actorPlayer.passedTime(Attribute.LAST_DEFENCE_CAPE_TELEPORT, 1, TimeUnit.DAYS, false, true)) {
                        actorPlayer.setInt(Attribute.LAST_DEFENCE_CAPE_TELEPORT, 1, 1)
                    }
                    actorPlayer.decInt(Attribute.LAST_DEFENCE_CAPE_TELEPORT, 1, 0, 1)
                    actorPlayer.hitpoints = actorPlayer.skillManager.getMaxLevel(Skill.HITPOINTS)
                    actorPlayer.sendMessage("Your defence cape has saved you. This effect can be used again after 24 hours.")
                    return
                }
                equipment.delete(ringItem)
                TeleportHandler.teleport(
                    actorPlayer,
                    GameConstants.DEFAULT_DEATH_POSITION,
                    TeleportType.JEWELRY_RUB,
                    false,
                    true
                )
                if (actorPlayer.skillManager.calculateCombatLevel() >= 100) {
                    AchievementManager.processFor(AchievementType.OWING_A_LIFE, actorPlayer)
                }
                actorPlayer.sendMessage("Your ring of life saves you!")
                actorPlayer.hitpoints = actorPlayer.skillManager.getMaxLevel(Skill.HITPOINTS)
            } else {
                if (actorPlayer.equipment.containsAny(
                        ItemID.DEFENCE_CAPE,
                        ItemID.DEFENCE_CAPE_T_
                    ) && actorPlayer.getInt(
                        Attribute.LAST_DEFENCE_CAPE_TELEPORT, 1
                    ) <= 0
                ) {
                    actorPlayer.sendMessage("Your defence cape won't svae you here....")
                } else {
                    actorPlayer.sendMessage("Your ring of life won't save you here....")
                }
            }
        }
    }

    @JvmStatic
    fun handleGuthans(player: Agent, target: Agent, damage: Int) {
        target.performGraphic(Graphics.GUTHAN_EFFECT_GRAPHIC)
        player.heal(damage)
    }

    @JvmStatic
    fun handleElysianShield(target: Player, queuedDamage: Damage) {
        if (Misc.getRandomInclusive(100) <= 70) {
            queuedDamage.multiplyDamage(CombatPrayer.ELYSIAN_DAMAGE_REDUCTION_MULTIPLIER)
            target.performGraphic(Graphics.ELYSIAN_HIT_ABSORPTION_GRAPHIC)
        }
    }

    @JvmStatic
    fun handleDivineShield(target: Player, queuedDamage: Damage) {
        if (queuedDamage.value > 2) {
        val prayerDrainValue = (queuedDamage.value * 0.30).div(2).roundToInt()
        target.skillManager.setCurrentLevel(
            Skill.PRAYER,
            target.skillManager.getCurrentLevel(Skill.PRAYER) - prayerDrainValue,
            true
        )
        queuedDamage.multiplyDamage(CombatPrayer.DIVINE_DAMAGE_REDUCTION_MULTIPLIER)
        target.performGraphic(Graphics.ELYSIAN_HIT_ABSORPTION_GRAPHIC)
    }
    }

    @JvmStatic
    fun handleJusticiar(actor: Agent, targetPlayer: Player, queuedDamage: Damage, damage: Int) {

        if (actor is Player) return
        val type = actor.combat.determineStrategy().type()
        val bonusManager = targetPlayer.bonusManager
        val defenceBonuses = bonusManager.defenceBonus
        val defenceBonus: Double
        defenceBonus = if (type == AttackType.MELEE) {
            val slashBonus = defenceBonuses[EquipmentBonuses.DEFENCE_SLASH]
            val crushBonus = defenceBonuses[EquipmentBonuses.DEFENCE_CRUSH]
            val stabBonus = defenceBonuses[EquipmentBonuses.DEFENCE_STAB]
            DoubleStream.of(slashBonus, crushBonus, stabBonus).max().orElse(0.0)
        } else defenceBonuses[if (type == AttackType.MAGIC) EquipmentBonuses.DEFENCE_MAGIC else EquipmentBonuses.DEFENCE_RANGE]
        val damageReduction = defenceBonus / JUSTICIAR_SET_DAMAGE_REDUCTION_BONUS_DIVISOR * damage
        val newDamage = (queuedDamage.value - damageReduction).toInt()
        queuedDamage.set(newDamage)
    }

    fun isFullyProtectedAgainstDragonFire(player: Player): Boolean {
        return (PrayerHandler.isActivated(player, PrayerHandler.PROTECT_FROM_MAGIC)
                && EquipmentUtil.isWearingDragonFireProtection(player)
                && !player.asPlayer.combat.fireImmunityTimer.finished()) ||
                !player.asPlayer.combat.superFireImmunityTimer.finished()
    }
}