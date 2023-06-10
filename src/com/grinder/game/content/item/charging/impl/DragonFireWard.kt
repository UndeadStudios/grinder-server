package com.grinder.game.content.item.charging.impl

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.item.charging.*
import com.grinder.game.content.item.charging.Charge
import com.grinder.game.entity.agent.combat.Combat
import com.grinder.game.entity.agent.combat.event.CombatEvent
import com.grinder.game.entity.agent.combat.event.impl.DragonFireEvent
import com.grinder.game.entity.agent.combat.event.impl.WyvernIceEvent
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.playAreaSound
import com.grinder.game.entity.agent.player.replaceEquipmentItem
import com.grinder.game.entity.passedTime
import com.grinder.game.model.*
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.AttributeKey
import com.grinder.game.model.item.Item
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.Priority
import com.grinder.util.time.TimeUtil
import com.grinder.util.oldgrinder.EquipSlot
import java.util.concurrent.TimeUnit

/**
 * Represents a handler for the Dragon fire ward item.
 *
 * @see ClearableChargedItems for the "Empty" option handling of the item.
 *
 * TODO: vial of powered bottled dragonbreath to charge ward
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-15
 */
@Charge(DragonFireWard.CHARGED, DragonFireWard.UNCHARGED)
object DragonFireWard : Chargeable {

    val CHARGES = AttributeKey("dfw-charges")

    const val CHARGED = ItemID.DRAGONFIRE_WARD_CHARGED
    const val UNCHARGED = ItemID.DRAGONFIRE_WARD

    private val CHARGE_ANIMATION = Animation(6695, Priority.MEDIUM)
    private val FIRE_ANIMATION = Animation(6696, Priority.HIGH)
    private val FIRE_GRAPHIC = Graphic(1165, GraphicHeight.LOW)
    private val PROPELLED_FIRE_GRAPHIC = Graphic(1166)

    private const val DRAGON_FIRE_ATTACK_PREFIX = "dragon's fiery"
    private const val WYVERN_ICE_ATTACK_PREFIX = "wyvern's icy"

    init {

        /*
         Configure the "Operate" option.
         */
        onSecondContainerEquipmentAction(CHARGED, UNCHARGED) {
            if(getItemId() == CHARGED)
                fire(player)
            else
                player.message("Your dragonfire ward does not have any more charges.")
        }

        /*
         Configure the "Inspect" option.
         */
        onThirdInventoryAction(CHARGED, UNCHARGED){

            val charges = getCharges(player.inventory[getSlot()])

            if (charges <= 0)
                player.message("Your ward has no charges.")
            else
                player.message("Your ward has $charges charges.")
        }

        CombatActions.onAnyEvent(DragonFireEvent::class, WyvernIceEvent::class) {
            ifActorIsPlayer {
                charge(it, combatEvent)
            }
        }
    }

    override val deathPolicy: ChargeableDeathPolicy
        get() = ChargeableDeathPolicy.DROP_UNCHARGED

    override fun toChargeItems(item: AttributableItem): Array<Item> {
        return emptyArray()
    }

    private fun charge(player: Player, combatEvent: CombatEvent) {

        val shield = player.equipment[EquipSlot.SHIELD]?:return

        if(shield.id != CHARGED && shield.id != UNCHARGED)
            return

        if(shield is AttributableItem){

            val prefix = if(combatEvent is DragonFireEvent)
                DRAGON_FIRE_ATTACK_PREFIX
            else
                WYVERN_ICE_ATTACK_PREFIX

            val charges = shield.getAttribute(CHARGES)?:0

            if(charges >= 50){
                if(player.passedTime(Attribute.DFS_CHARGE_TIMER, 45, TimeUnit.SECONDS, message = false)){
                    player.message("Your ward absorbs most of the $prefix breath!")
                    player.message("Your dragonfire ward is already fulled charged.")
                }
            } else {
                shield.setAttribute(CHARGES, getCharges(shield)+1)
                AchievementManager.processFor(AchievementType.THE_CHARGER, player)
                player.message("Your ward absorbs most of the $prefix breath!")
                player.message("Your dragonfire ward glows more brightly.")
                if (!player.motion.notCompleted() && prefix == DRAGON_FIRE_ATTACK_PREFIX) {
                    player.performAnimation(CHARGE_ANIMATION)
/*                    player.combat.getNextAttackTimer(true)
                            ?.extendOrCap(2, 1)*/
                }
            }
        } else if(shield.id == UNCHARGED){
            val chargedShield = AttributableItem(CHARGED, 1)
            chargedShield.setAttribute(CHARGES, 1)
            player.replaceEquipmentItem(shield, chargedShield, 0)
            player.message("Your dragonfire ward glows more brightly.")
        }
    }

    private fun fire(player: Player) {

        val shield = player.equipment[EquipSlot.SHIELD]

        if(shield.id != CHARGED && shield.id != UNCHARGED) {
            player.message("You're not wearing a dragonfire ward.")
            return
        }

        if(shield !is AttributableItem){
            player.message("Your dragonfire ward doesn't have anymore charges.")
            return
        }

        if(!player.passedTime(Attribute.DRAGONFIRE_WARD_EFFECT, TimeUtil.GAME_CYCLES.toMillis(45), TimeUnit.MILLISECONDS, message = false)){
            player.message("You must let the shield warm up before using it again.")
            return
        }

        val combat: Combat<*> = player.combat
        val target = combat.target

        if(target == null){
            player.message("You do not have a target!")
            return
        }

        shield.decrement(CHARGES)

        if (shield.getAttribute(CHARGES)?:0 <= 0)
            player.replaceEquipmentItem(shield, Item(UNCHARGED))

        player.performAnimation(FIRE_ANIMATION)
        player.performGraphic(FIRE_GRAPHIC)
        player.playAreaSound(Sounds.DFS_SPECIAL_ATTACK_SOUND)

        val maximum = 28
        var damage = Misc.getRandomInclusive(maximum)
        if (target is Player) {
            val hasDragonFireDefence = !target.combat.fireImmunityTimer.finished()
                    || EquipmentUtil.isWearingDragonFireProtection(target) || !target.combat.superFireImmunityTimer.finished()
            if (hasDragonFireDefence)
                damage = Misc.getRandomInclusive(11)
        }
        if (target is NPC) {
            damage += 5 + Misc.random(10);
        }

        val dragonFireDamage = Damage(damage, DamageMask.REGULAR_HIT)

        combat.extendNextAttackDelay(2)

        TaskManager.submit(player, 1) {
            val template = ProjectileTemplate.builder(PROPELLED_FIRE_GRAPHIC.id)
                    .setDelay(55)
                    .setSpeed(13)
                    .setHeights(37, 37)
                    .setCurve(16)
                    .build()

            val projectile = Projectile(player, target, template)

            projectile.sendProjectile()
            projectile.onArrival {
                if (target!!.isUntargetable || target!!.isTeleporting)
                    return@onArrival
                target.combat.queue(dragonFireDamage)
            }
        }
    }

    override fun getCharges(item: Item) : Int {
        return if(!item.hasAttributes() || item.id != CHARGED) 0
        else item.asAttributable.getAttribute(CHARGES) ?: 0
    }

}