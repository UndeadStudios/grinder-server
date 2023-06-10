package com.grinder.game.content.item.charging.impl

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.content.item.charging.Charge
import com.grinder.game.content.item.charging.ChargeableDeathPolicy
import com.grinder.game.content.item.charging.ClearableChargedItems
import com.grinder.game.content.item.charging.ItemChargeable
import com.grinder.game.content.item.charging.impl.AncientWyvernShield.CHARGED
import com.grinder.game.content.item.charging.impl.AncientWyvernShield.UNCHARGED
import com.grinder.game.entity.agent.combat.Combat
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.passedTime
import com.grinder.game.model.*
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.AttributeKey
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainer
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.task.TaskManager
import com.grinder.net.packet.impl.EquipPacketListener
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.Priority
import com.grinder.util.time.TimeUtil
import com.grinder.util.oldgrinder.EquipSlot
import java.util.concurrent.TimeUnit
import kotlin.math.floor
import kotlin.math.min

/**
 * Handles the ancient wyvern shield mechanics.
 *
 * TODO: add fossil charge option
 * TODO: implement "Recharge" option
 *
 * @see ClearableChargedItems for the "Empty" option handling of the item.
 *
 * @version 1.0
 * @since 2019-03-15
 */
@Charge(CHARGED, UNCHARGED)
object AncientWyvernShield : ItemChargeable {

    val CHARGES = AttributeKey("aws-charges")

    const val CHARGED = ItemID.ANCIENT_WYVERN_SHIELD_CHARGED
    const val UNCHARGED = ItemID.ANCIENT_WYVERN_SHIELD

    const val NUMULITES_TO_CHARGES_RATIO = 500
    const val MAX_CHARGES = 50

    private val CHARGE_ANIMATION = Animation(6695, Priority.MEDIUM)
    private val FIRE_ANIMATION = Animation(6696, Priority.HIGH)
    private val FIRE_GRAPHIC = Graphic(1165, GraphicHeight.LOW)
    private val PROPELLED_FIRE_GRAPHIC = Graphic(1166)

    init {

        onSecondContainerEquipmentAction(CHARGED, UNCHARGED) {
            if(getItemId() == CHARGED)
                fire(player)
            else
                player.message("Your ancient wyvern shield does not have any more charges.")
        }

        onThirdInventoryAction(CHARGED, UNCHARGED){

            val charges = getCharges(player.inventory[getSlot()])

            if (charges <= 0)
                player.message("Your shield has no charges.")
            else
                player.message("Your shield has $charges charges.")
        }
    }

    override val deathPolicy: ChargeableDeathPolicy
        get() = ChargeableDeathPolicy.DROP_UNCHARGED

    override fun getCharges(item: Item): Int {
        return if(!item.hasAttributes() || item.id != CHARGED) 0
        else item.asAttributable.getAttribute(CHARGES) ?: 0
    }

    override fun decrementCharges(player: Player, item: Item) {
        if(item.hasAttributes()) {
            val charges = item.asAttributable.decrement(CHARGES) ?: 0
            if(charges <= 0)
                player.replaceEquipmentItem(Item(CHARGED), Item(UNCHARGED), 0)
        }
    }

    override fun toChargeItems(item: AttributableItem): Array<Item> {
        val numulitesAmount = item.getAttribute(CHARGES)?:0 * NUMULITES_TO_CHARGES_RATIO
        if (numulitesAmount <= 0)
            return emptyArray()
        return arrayOf(Item(ItemID.NUMULITES, numulitesAmount))
    }

    override fun charge(player: Player, used: Int, with: Int, withSlot: Int): Boolean {
        if (player.getBoolean(
                Attribute.HAS_PENDING_RANDOM_EVENT,
                false
            ) || player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2, false)
        ) return false
        if (player.BLOCK_ALL_BUT_TALKING) return false
        if (player.isInTutorial) return false
        if (player.status === PlayerStatus.AWAY_FROM_KEYBOARD) return false

        if (player.getBoolean(Attribute.HAS_TRIGGER_RANDOM_EVENT, false)) {
            player.sendMessage("Please finish your random event before doing anything else.")
            return false
        }
        if (player.busy()) {
            player.sendMessage("You cannot do that when you are busy.")
            return false;
        }

        if (!player.notTransformed("do this", true, true)) return false
        if (used != ItemID.NUMULITES)
            return false

        if (with != CHARGED && with != UNCHARGED)
            return false

        val charges = getCharges(player.inventory[withSlot])
        if (charges >= MAX_CHARGES) {
            player.sendMessage("Your shield already has the full 50 charges.")
            return true
        }

        val numulitesInInventory = player.inventory.getAmount(ItemID.NUMULITES)
        val maxChargesFromNumulites = floor(numulitesInInventory / NUMULITES_TO_CHARGES_RATIO.toDouble()).toInt()
        val chargesToAdd = min(maxChargesFromNumulites, MAX_CHARGES-charges)

        if (chargesToAdd <= 0) {
            player.message("You need to have at least 500 numulites per charge.")
            return true
        }

        if(player.removeInventoryItem(Item(ItemID.NUMULITES, chargesToAdd * NUMULITES_TO_CHARGES_RATIO), 0)) {
            player.message("You charge your shield with $chargesToAdd charges.")
            charge(player, chargesToAdd, player.inventory, withSlot)
            return true
        }
        return false
    }

    private fun charge(target: Player, charges: Int, container: ItemContainer, slot: Int) {

        val shield = container[slot]?:return

        if(shield.id == UNCHARGED){
            val charged = AttributableItem(CHARGED, 1)
            charged.setAttribute(CHARGES, charges)
            container.replace(shield, charged)
            container.refreshItems()
            EquipPacketListener.resetWeapon(target)
            WeaponInterfaces.assign(target)
            EquipmentBonuses.update(target)
        } else if(shield is AttributableItem && shield.id == CHARGED){
            val oldCharges = getCharges(shield)
            if(oldCharges >= 50){
                target.message("Your ancient wyvern shield is already fully charged.")
                return
            }
            shield.setAttribute(CHARGES, min(oldCharges + charges, MAX_CHARGES))
            AchievementManager.processFor(AchievementType.THE_CHARGER, charges, target)
            EquipmentBonuses.update(target)
            target.performAnimation(CHARGE_ANIMATION)
        }
    }

    private fun fire(player: Player) {

        val shield = player.equipment[EquipSlot.SHIELD]

        if(shield.id != CHARGED && shield.id != UNCHARGED) {
            player.message("You're not wearing an ancient wyvern shield.")
            return
        }

        if(shield !is AttributableItem) {
            player.message("Your ancient wyvern shield doesn't have anymore charges.")
            return
        }

        if(!player.passedTime(Attribute.ANCIENT_WYVERN_SHIELD_EFFECT, TimeUtil.GAME_CYCLES.toMillis(45), TimeUnit.MILLISECONDS, message = false)){
            player.message("You must let the shield warm up before using it again.")
            return
        }

        val combat: Combat<*> = player.combat
        val target = combat.target

        if (target == null) {
            player.message("You do not have a target!")
            return
        }

        shield.decrement(CHARGES)
        player.performAnimation(FIRE_ANIMATION)
        player.performGraphic(FIRE_GRAPHIC)

        if (shield.getAttribute(CHARGES)?:0 <= 0)
            player.replaceEquipmentItem(shield, Item(UNCHARGED))

        val maximum = 28
        var damage = Misc.getRandomInclusive(maximum)
        if (target is Player) {
            val hasDragonFireDefence = !target.combat.fireImmunityTimer.finished()
                    || EquipmentUtil.isWearingDragonFireProtection(target) || !target.combat.superFireImmunityTimer.finished()
            if (hasDragonFireDefence)
                damage = Misc.getRandomInclusive(11)
        }
        val dragonFireDamage = Damage(damage, DamageMask.REGULAR_HIT)

        combat.extendNextAttackDelay(2)

        TaskManager.submit(player, 1) {
            val template = ProjectileTemplate.builder(PROPELLED_FIRE_GRAPHIC.id)
                    .setDelay(55)
                    .setSpeed(75)
                    .setHeights(37, 37)
                    .setCurve(16)
                    .build()

            val projectile = Projectile(player, target, template)

            projectile.sendProjectile()
            projectile.onArrival {
                if (target.isUntargetable || target.isTeleporting)
                    return@onArrival
                target.combat.queue(dragonFireDamage)
                target.combat.submit(FreezeEvent(3, true))
            }
        }
    }
}