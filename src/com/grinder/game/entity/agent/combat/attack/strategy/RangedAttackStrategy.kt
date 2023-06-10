package com.grinder.game.entity.agent.combat.attack.strategy

import com.grinder.game.collision.CollisionManager
import com.grinder.game.content.dueling.DuelRule
import com.grinder.game.content.item.charging.impl.Blowpipe
import com.grinder.game.content.item.charging.impl.BowOfFaerdhinen
import com.grinder.game.content.item.charging.impl.CrawsBow
import com.grinder.game.content.item.charging.impl.ShayzienBlowpipe
import com.grinder.game.content.item.jewerly.DoubleXPRing
import com.grinder.game.definition.NpcHeights.getNpcHeight
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Chinchompas.canUse
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Chinchompas.postHit
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeaponType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.grounditem.ItemOnGroundManager
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.item.Item
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplateBuilder
import com.grinder.game.model.sound.Sounds
import com.grinder.util.DistanceUtil
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.Priority
import com.grinder.util.oldgrinder.EquipSlot
import kotlin.math.roundToInt

/**
 * Represents an [AttackStrategy] for [ranged][AttackType.RANGED] combat.
 *
 * @author Professor Oak
 */
class RangedAttackStrategy : AttackStrategy<Agent> {

    override fun type() = AttackType.RANGED

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {

        val actorCombat = actor.combat
        val rangedWeapon = actorCombat.rangedWeapon
        val fightType = actorCombat.fightType

        var hitBuilder = HitTemplate.builder(AttackType.RANGED)
        val hitDelay = getHitDelay(rangedWeapon, actor, target)

        hitBuilder.setDelay(hitDelay)

        if (rangedWeapon != null) {
            if (rangedWeapon == RangedWeapon.SWAMP_LIZARD) {
                hitBuilder = when (fightType) {
                    WeaponFightType.LIZARD_SCORCH -> HitTemplate.builder(AttackType.MELEE)
                    WeaponFightType.LIZARD_BLAZE -> HitTemplate.builder(AttackType.MAGIC)
                    else -> HitTemplate.builder(AttackType.RANGED)
                }
            } else if (rangedWeapon == RangedWeapon.ORANGE_SALAMANDER || rangedWeapon == RangedWeapon.RED_SALAMANDER || rangedWeapon == RangedWeapon.BLACK_SALAMANDER) {
                hitBuilder = when (fightType) {
                    WeaponFightType.SALAMANDER_SCORCH -> HitTemplate.builder(AttackType.MELEE)
                    WeaponFightType.SALAMANDER_BLAZE -> HitTemplate.builder(AttackType.MAGIC)
                    else -> HitTemplate.builder(AttackType.RANGED)
                }
            }

            hitBuilder.setIgnoreAttackStats(rangedWeapon.ignoreAttackStats())

            if (rangedWeapon.numberOfHits > 1) {
                if (rangedWeapon.type == RangedWeaponType.DARK_BOW){
                    val (first, second) = getDarkBowHitDelayPair(actor, target)
                    val firstTemplate = hitBuilder
                            .setDelay(first)
                            .build()
                    val secondTemplate = hitBuilder
                            .setDelay(second)
                            .build()
                    return arrayOf(
                            Hit(actor, target, this, firstTemplate),
                            Hit(actor, target, this, secondTemplate))
                }
                val hitTemplate = hitBuilder.build()
                return  Array(rangedWeapon.numberOfHits) {
                    Hit(actor, target, this, hitTemplate).also {
                        it.extendDelay(rangedWeapon.hitTickInterval)
                    }
                }
            }
        }
        val hitTemplate = hitBuilder.build()
        val firstHit = Hit(actor, target, this, hitTemplate)
        if (actor is Player) {
            if (EquipmentUtil.hasAnyAmuletOfTheDamned(actor)) {
                if (EquipmentUtil.isWearingKarilSet(actor)) {
                    if (Misc.randomChance(25f)) {
                        val secondHit = Hit(actor, target, this, hitTemplate)
                        secondHit.multiplyDamage(0.5)
                        return arrayOf(firstHit, secondHit)
                    }
                }
            }
        }
        return arrayOf(firstHit)
    }

    override fun canAttack(actor: Agent, target: Agent): Boolean {


        actor.positionToFace = target.position

        if (actor is NPC)
            return true

        val player = actor as Player
        val combat = player.combat

        if (player.dueling.inDuel() && player.dueling.rules[DuelRule.NO_RANGED.ordinal]) {
            DialogueManager.sendStatement(player, "Ranged has been disabled in this duel!")
            combat.reset(false)
            return false
        }

        val weapon = combat.rangedWeapon

        if (weapon == RangedWeapon.BOFA) {
            val uncharged = player.equipment.containsAtSlot(EquipmentConstants.WEAPON_SLOT, BowOfFaerdhinen.UNCHARGED) // if you
            if (uncharged) {
                player.sendMessage("You need to charge your ${BowOfFaerdhinen.NAME} before you can use it!")
                return false
            }
            val charges = BowOfFaerdhinen.getCharges(actor.equipment[EquipSlot.WEAPON])
            if(charges <= 0){
                player.message("There is not enough charges left powering your bow.")
                return false
            }
        } else if(weapon == RangedWeapon.CRAWS_BOW) {
            val charges = CrawsBow.getCharges(actor.equipment[EquipSlot.WEAPON])
            if(charges <= 0){
                player.message("There is not enough revenant ether left powering your bow.")
                return false
            }
        }

        val ammoRequired = if(combat.rangedWeapon == RangedWeapon.DARK_BOW) 2 else 1
        return checkAmmo(player, ammoRequired)
    }

    override fun sequence(actor: Agent, target: Agent) {
        if (actor.isNpc) {
            val def = (actor as NPC).fetchDefinition()
            if (def.rangeProjectile > 0) {
                val builder = ProjectileTemplateBuilder(def.rangeProjectile)
                        .setSourceOffset(0)
                        .setDelay(if (def.rangeProjectileDelay == 0) 41 else def.rangeProjectileDelay) // Loaded from npc_defs
                        .setSpeed(2)
                        .setStartHeight(46)
                        .setEndHeight(31)
                        .setCurve(5)
                Projectile(actor, target, builder.build()).sendProjectile()
            }
            return
        }
        val ammo = actor.combat.ammunition
        val rangedWeapon = actor.combat.rangedWeapon
        if (ammo == null || rangedWeapon == null) {
            return
        }
        var projectileId = ammo.projectileId

        if (rangedWeapon == RangedWeapon.DRAGON_HUNTER_BOW)
            projectileId = 631
        val builder = ProjectileTemplateBuilder(projectileId)
                .setSourceOffset(0)
                .setDelay(41)
                .setSpeed(-2)
                .setStartHeight(43)
                .setEndHeight(43)
                .setCurve(5)

        val endHeight = if (target is NPC) getNpcHeight(target) else 196
        var heightMultiplier = if (endHeight > 500) 0.5 else 0.75

        if (rangedWeapon.type.isCrossbow) {
            builder.setStartHeight(46)
                    .setDelay(58)
                    .setSpeed(1 - DistanceUtil.getChebyshevDistance(actor.position, target.position))
                    .setCurve(if (endHeight > 300) 5 else 1)
        } else if (rangedWeapon.type.isLongBow) {
            heightMultiplier *= 0.9
            builder.setStartHeight(41)
                    .setDelay(52)
                    .setSpeed(2)
                    .setCurve(6)
        } else if (rangedWeapon.type.isShortBow) {
            heightMultiplier *= 0.8
            builder.setStartHeight(43)
                    .setDelay(52)
                    .setSpeed(2)
                    .setCurve(15)
        } else if (rangedWeapon.type == RangedWeaponType.TOXIC_BLOWPIPE) {
            heightMultiplier *= 0.9
            builder.setStartHeight(45)
                    .setDelay(30)
                    .setSpeed(-2)
                    .setCurve(5)
        } else if (rangedWeapon.type == RangedWeaponType.SHAYZIEN_BLOWPIPE) {
            heightMultiplier *= 0.9
            builder.setStartHeight(45)
                .setDelay(30)
                .setSpeed(-2)
                .setCurve(5)
        } else if (rangedWeapon.type == RangedWeaponType.KNIVES || rangedWeapon.type == RangedWeaponType.DARTS || rangedWeapon.type == RangedWeaponType.CHINCHOMPA) {
            heightMultiplier *= 0.8
            builder.setStartHeight(47)
                    .setDelay(40)
                    .setSpeed(0)
                    .setCurve(15)
        } else if (rangedWeapon.type == RangedWeaponType.CHINCHOMPA) {
            heightMultiplier *= 0.8
            builder.setStartHeight(47)
                .setDelay(30)
                .setSpeed(0)
                .setCurve(15)
        } else if (ammo == Ammunition.TOKTZ_XIL_UL) {
            heightMultiplier *= 0.8
            builder.setDelay(30)
                    .setSpeed(7)
        }
        builder.setEndHeight((endHeight / 4 * heightMultiplier).toInt())

        // Fire projectile
        val projectile = Projectile(actor, target, builder.build())
        projectile.sendProjectile()
        if (rangedWeapon.type == RangedWeaponType.CHINCHOMPA) {
            projectile.onArrival {
                target.performGraphic(Graphic(1466, GraphicHeight.HIGH))
                if (actor.isPlayer) {
                    actor.asPlayer.packetSender.sendSound(Sounds.CHINCHOMPA_CONTACT_SOUND)
                }
            }
        }

        val amount: Int = if (rangedWeapon == RangedWeapon.DARK_BOW) {
            Projectile(actor, target, builder.setStartHeight(41).setDelay(40).build()).sendProjectile()
            2
        } else {
            1
        }
        processAmmunitionProjectile(actor, ammo, projectile, amount)
    }

    override fun duration(actor: Agent): Int {
        val combat = actor.combat
        val type = actor.combat.rangedWeapon
        if (type == RangedWeapon.TOXIC_BLOWPIPE) {
            if (combat.target is Player || combat.opponent is Player) {
                return actor.baseAttackSpeed + 1
            }
        }
        if (type == RangedWeapon.SHAYZIEN_TOXIC_BLOWPIPE) {
            if (combat.target is Player || combat.opponent is Player) {
                return actor.baseAttackSpeed + 1
            }
        }
        return actor.baseAttackSpeed
    }

    override fun requiredDistance(actor: Agent): Int {
        val weapon = actor.combat.rangedWeapon
        if (weapon != null) {
            val isLongRangeWeapon = actor.combat.fightType == weapon.type.longRangeFightType
            return if (isLongRangeWeapon) weapon.type.longRangeDistance else weapon.type.defaultDistance
        }
        return 6
    }

    override fun animate(actor: Agent) {
        if (actor.isNpc) {
            val def = (actor as NPC).fetchDefinition()
            actor.performAnimation(Animation(def.rangeAnim, Priority.HIGH))
            if (def.rangeStartGfx > 0) {
                actor.performGraphic(Graphic(def.rangeStartGfx, GraphicHeight.MIDDLE))
            }
            return
        }
        val weapon = actor.combat.rangedWeapon
        val ammo = actor.combat.ammunition
        val animation = if (weapon == RangedWeapon.DRAGON_KNIFE) 8194 else if (weapon == RangedWeapon.DRAGON_KNIFE_P) 8195 else actor.attackAnim
        if (animation != -1) actor.performAnimation(Animation(animation, 5, Priority.HIGH))
        if (ammo != null) {
            val startGraphic = ammo.startGraphic
            val darkBowGraphic = ammo.darkBowGraphic
            var ammunitionGraphic = if (weapon == RangedWeapon.DARK_BOW && darkBowGraphic != null) darkBowGraphic else startGraphic
            if (ammunitionGraphic != null && weapon.type != RangedWeaponType.TOXIC_BLOWPIPE && weapon.type != RangedWeaponType.SHAYZIEN_BLOWPIPE) actor.performGraphic(Graphic(ammunitionGraphic.id, 5, ammunitionGraphic.height))
        }
    }

    override fun postHitAction(actor: Agent, target: Agent) {}
    override fun postHitEffect(hit: Hit) {
        val actor = hit.attacker
        if (actor.isNpc) {
            val def = (actor as NPC).fetchDefinition()
            if (def.rangeEndGfx > 0) {
                hit.target.performGraphic(Graphic(def.rangeEndGfx, GraphicHeight.MIDDLE))
            }
        }
        if (actor.isPlayer) {
            if (canUse(actor.asPlayer)) {
                postHit(actor.asPlayer, hit)
            }
            if (actor.asPlayer.equipment.get(EquipSlot.RING).id == ItemID.RING_OF_CHAROS) {
                DoubleXPRing.use(actor.asPlayer)
            }
        }
    }

    override fun postIncomingHitEffect(hit: Hit) {}

    companion object {

        /**
         * The default ranged combat attackStrategy
         */
        @JvmField
        val INSTANCE = RangedAttackStrategy()

        fun getHitDelay(weapon: RangedWeapon? = null, attacker: Agent, target: Agent) : Int {

            if (weapon != null) {
                val distance = DistanceUtil.getChebyshevDistance(attacker!!.position, target!!.position)
                return when {
                    weapon.type.isShortBow
                            || weapon.type.isLongBow
                            || weapon.type.isCrossbow
                    -> (1F + ((3F + distance) / 6F)).roundToInt()
                    weapon.type == RangedWeaponType.BALLISTA
                    -> getBallistaHitDelay(attacker, target)
                    weapon.type == RangedWeaponType.CHINCHOMPA
                    -> getChinchompasHitDelay(attacker, target)
                    weapon.type.isThrowable
                    -> getThrowableHitDelay(attacker, target)
                    weapon.type == RangedWeaponType.TOXIC_BLOWPIPE
                    -> getToxicBlowPipeHitDelay(attacker, target)
                    weapon.type == RangedWeaponType.SHAYZIEN_BLOWPIPE
                    -> getToxicBlowPipeHitDelay(attacker, target)
                    else -> 2
                }
            }
            return 2
        }

        fun getDarkBowHitDelayPair(attacker: Agent, target: Agent) : Pair<Int, Int> {
            val distance = DistanceUtil.getChebyshevDistance(attacker.position, target.position)
            var first =  (1F + ((3F + distance) / 6F)).roundToInt()
            var second = (1F + ((2F + distance) / 6F)).roundToInt()
//            if (target is NPC){
//                first++
//                second++
//            }
            return first to second
        }

        fun processAmmunitionProjectile(actor: Agent?, ammo: Ammunition, projectile: Projectile, amount: Int) {
            if (actor is Player) {
                if (ammo.dropOnFloor()) {

                    // Chance to not use arrows if wearing an Ava's device.
                    val save = saveArrow(actor)
                    if (save) return
                    if (actor.combat.rangedWeapon == RangedWeapon.DRAGON_HUNTER_BOW) {
                        decrementAmmo(actor, amount)
                        return
                    }

                    // If the player is wearing an Ava's device, do not put non-saved arrows on the floor.
                    if (!EquipmentUtil.isWearingAvas(actor)) {
                        if (!CollisionManager.blocked(projectile.target)) {
                            projectile.onArrival {
                                ItemOnGroundManager
                                        .register(actor, Item(ammo.itemId, amount), projectile.target)
                            }
                        }
                    }
                }
                decrementAmmo(actor, amount)
            }
        }

        /**
         * Checks if a player has enough ammo to perform a ranged attack
         *
         * @param player
         * The player to run the check for
         * @return True if player has ammo, false otherwise
         */
        fun checkAmmo(player: Player, amountRequired: Int): Boolean {
            // Get the ranged weapon data
            val rangedWeapon = player.combat.rangedWeapon

            // Get the ranged ammo data
            val ammoData = player.combat.ammunition
            if (rangedWeapon == null) {
                player.combat.reset(false)
                return false
            }
            if (rangedWeapon == RangedWeapon.TOXIC_BLOWPIPE) {
                if (Blowpipe.getCharges(player.equipment[EquipSlot.WEAPON]) <= 0) {
                    player.packetSender.sendMessage("You blowpipe does not have any charges.", 1000)
                    player.combat.reset(false)
                    return false
                }
                return true
            }
            if (rangedWeapon == RangedWeapon.SHAYZIEN_TOXIC_BLOWPIPE) {
                if (ShayzienBlowpipe.getCharges(player.equipment[EquipSlot.WEAPON]) <= 0) {
                    player.packetSender.sendMessage("You blowpipe does not have any charges.", 1000)
                    player.combat.reset(false)
                    return false
                }
                return true
            }
            if (ammoData == null) {
                player.packetSender.sendMessage("You don't have any ammunition to fire.", 1000)
                player.combat.reset(false)
                return false
            }
            if (rangedWeapon.type.isThrowable) return true
            val ammoSlotItem = player.equipment.items[EquipmentConstants.AMMUNITION_SLOT]
            var properReq = false

            // TODO: BAD LOOP
            for (d in rangedWeapon.ammunitionData) {
                if (d == ammoData) {
                    if (d.itemId == ammoSlotItem.id) {
                        properReq = true
                        break
                    }
                }
            }
            if (rangedWeapon != RangedWeapon.CRYSTAL_BOW && rangedWeapon != RangedWeapon.CRAWS_BOW && rangedWeapon != RangedWeapon.BOFA) {
                if (ammoSlotItem.id == -1 || ammoSlotItem.amount < amountRequired) {
                    player.sendMessage("You don't have enough ammunition to fire.")
                    player.combat.reset(false, true)
                    if (player.isSpecialActivated) {
                        player.packetSender.sendSpecialAttackState(false)
                        SpecialAttackType.updateBar(player, true)
                    }
                    return false
                }
            }
            if (rangedWeapon == RangedWeapon.DARK_BOW && player.equipment[EquipmentConstants.AMMUNITION_SLOT].amount == 1) {
                player.sendMessage("You don't have enough ammunition to fire.")
                player.combat.reset(false, true)
                return false
            }
            if (!properReq && rangedWeapon != RangedWeapon.CRYSTAL_BOW && rangedWeapon != RangedWeapon.CRAWS_BOW && rangedWeapon != RangedWeapon.BOFA) {
                if (player.equipment.items[EquipmentConstants.AMMUNITION_SLOT].id == -1) {
                    player.packetSender.sendMessage("You don't have any ammunition that you can use with " + player.equipment.items[EquipmentConstants.WEAPON_SLOT].definition.name + ".")
                    player.combat.reset(false)
                    return false
                }
                val ammoName = ammoSlotItem.definition.name
                val weaponName = player.equipment.items[EquipmentConstants.WEAPON_SLOT].definition.name
                val add = if (!ammoName.endsWith("s") && !ammoName.endsWith("(e)")) "s" else ""
                player.packetSender.sendMessage("You can't use " + ammoName + "" + add + " with "
                        + Misc.anOrA(weaponName) + " " + weaponName + ".", 1000)
                player.combat.reset(false)
                return false
            }
            return true
        }

        /**
         * Decrements the amount ammo the [Player] currently has equipped.
         *
         * @param player
         * the player to decrement ammo for.
         */
        fun decrementAmmo(player: Player, amount: Int) {
            val rangedWeapon = player.combat.rangedWeapon
            if (rangedWeapon == RangedWeapon.CRYSTAL_BOW || rangedWeapon == RangedWeapon.CRAWS_BOW) return
            val slot = rangedWeapon.type.ammunitionSlot
            val equipment = player.equipment
            val ammunitionItem = equipment[slot]
            if (rangedWeapon == RangedWeapon.TOXIC_BLOWPIPE) {
                Blowpipe.decrementCharges(player, equipment[EquipSlot.WEAPON])
                return
            } else if (rangedWeapon == RangedWeapon.SHAYZIEN_TOXIC_BLOWPIPE) {
                ShayzienBlowpipe.decrementCharges(player, equipment[EquipSlot.WEAPON])
                return
            } else if (rangedWeapon == RangedWeapon.BOFA) {
                BowOfFaerdhinen.decrementCharges(player, equipment[EquipSlot.WEAPON])
                return
            }
            ammunitionItem.decrementAmountBy(amount)
            if (ammunitionItem.amount == 0) {
                equipment[slot] = Item(-1)
                player.sendMessage("You have run out of ammunition!")
                player.combat.reset(false)
                if (slot == EquipmentConstants.WEAPON_SLOT) {
                    WeaponInterfaces.assign(player)
                    player.updateAppearance()
                }
            }
            equipment.refreshItems()
        }

        /*
        * Ballistas have the following hit delay based on distance (squares)
         */
        private fun getBallistaHitDelay(attacker: Agent, target: Agent): Int {
            return when (DistanceUtil.getChebyshevDistance(attacker.position, target.position)) {
                in 0..4 -> 2
                in 5..10 -> 3
                else -> 3
            }
        }

        /*
        * Throwables have the following hit delay based on distance (squares)
         */
        private fun getThrowableHitDelay(attacker: Agent, target: Agent): Int {
            return when (DistanceUtil.getChebyshevDistance(attacker.position, target.position)) {
                in 0..1 -> 1
                in 2..5 -> 2
                else -> 3
            }
        }



        /*
        * Chinchompas have the following hit delay based on distance (squares)
         */
        private fun getChinchompasHitDelay(attacker: Agent, target: Agent): Int {
            return when (DistanceUtil.getChebyshevDistance(attacker.position, target.position)) {
                in 0..8 -> 2
                else -> 2
            }
        }

        /*
        * Blowpipe have the following hit delay based on distance (squares)
        */
        private fun getToxicBlowPipeHitDelay(attacker: Agent, target: Agent): Int {
            return when (DistanceUtil.getChebyshevDistance(attacker.position, target.position)) {
                in 0..3 -> 1
                else -> 2
            }
        }

        /**
         * Range hit delays for NPC's not sure if this is the exact proper but this is how it was observed on OSRS
         */
        fun getNPCRangeHitDelay(attacker: Agent, target: Agent): Int {
            return when (DistanceUtil.getChebyshevDistance(attacker.position, target.position)) {
                in 0..2 -> 1
                in 3..8 -> 2
                else -> 3
            }
        }

        /**
         * Check if arrow should be saved by accumulator.
         *
         * @param player player shooting.
         * @return true if arrow should be saved.
         */
        private fun saveArrow(player: Player): Boolean {
            if (EquipmentUtil.isWearingMetalBody(player)) return false
            when (player.equipment[EquipmentConstants.CAPE_SLOT].id) {
                21898, 22109 -> return Misc.randomChance(80f)
                ItemID.AVAS_MAX_CAPE -> return Misc.randomChance(72f)
                ItemID.AVAS_ACCUMULATOR -> return Misc.randomChance(72f)
                ItemID.AVAS_ATTRACTOR -> return Misc.randomChance(72f)
                ItemID.AVAS_MAX_CAPE -> return Misc.randomChance(80f)
            }
            return false
        }
    }
}