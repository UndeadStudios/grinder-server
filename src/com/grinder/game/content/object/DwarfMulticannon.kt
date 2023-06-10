package com.grinder.game.content.`object`

import com.google.common.collect.MinMaxPriorityQueue
import com.grinder.game.content.quest.QuestManager
import com.grinder.game.entity.`object`.DynamicGameObject
import com.grinder.game.entity.`object`.ObjectManager
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.LineOfSight
import com.grinder.game.entity.agent.combat.attack.*
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.formula.CombatFormulaType
import com.grinder.game.entity.agent.combat.formula.CombatFormulas
import com.grinder.game.entity.agent.combat.formula.CombatSnapshot
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.impl.RockCrab
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.entity.getTimePassed
import com.grinder.game.entity.markTime
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.*
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.attribute.Attribute.Companion.CANNON_DEGRADE_TIMER
import com.grinder.game.model.attribute.Attribute.Companion.CANNON_RECLAIM_STATUS
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.NpcID
import com.grinder.util.ObjectID
import com.grinder.util.math.Vector2i
import com.grinder.util.time.toCycles
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.min

/**
 * Created by Kyle Fricilone on May 27, 2020.
 */
enum class CannonRotation(
    val direction: Direction,
    val animation: Int,
    val vectors: List<Vector2i> = listOf()
) {

    NORTH_WEST(
        Direction.NORTH_WEST, 514, listOf(
            Vector2i(-1, 1), Vector2i(-2, 1),
            Vector2i(-1, 2), Vector2i(-3, 1), Vector2i(-1, 3)
        )
    ),
    NORTH(Direction.NORTH, 515),
    NORTH_EAST(
        Direction.NORTH_EAST, 516, listOf(
            Vector2i(1, 1), Vector2i(1, 2),
            Vector2i(2, 1), Vector2i(1, 3), Vector2i(3, 1)
        )
    ),
    EAST(Direction.EAST, 517),
    SOUTH_EAST(
        Direction.SOUTH_EAST, 518, listOf(
            Vector2i(1, -1), Vector2i(2, -1),
            Vector2i(1, -2), Vector2i(3, -1), Vector2i(1, -3)
        )
    ),
    SOUTH(Direction.SOUTH, 519),
    SOUTH_WEST(
        Direction.SOUTH_WEST, 520, listOf(
            Vector2i(-1, -1), Vector2i(-1, -2),
            Vector2i(-2, -1), Vector2i(-1, -3), Vector2i(-3, -1)
        )
    ),
    WEST(Direction.WEST, 521);

    val next: Int
        get() = (ordinal + 1).rem(8)

    companion object {

        val VALUES = values()

    }

}

enum class CannonAmmo(
    val id: Int,
    val hit: HitTemplate
) {

    GRANITE_CANNONBALL(
        ItemID.GRANITE_CANNONBALL, HitTemplateBuilder(AttackType.RANGED)
            .setIgnoreAmmunitionEffects(true)
            .setIgnorePoisonEffects(true)
            .setDelay(0)
            .setIgnoreAttackStats(true)
            .setIgnoreStrengthStats(true)
            .setDamageRange(0..35)
            .build()
    ),
    CANNONBALL(
        ItemID.CANNONBALL, HitTemplateBuilder(AttackType.RANGED)
            .setIgnoreAmmunitionEffects(true)
            .setIgnorePoisonEffects(true)
            .setDelay(0)
            .setIgnoreAttackStats(true)
            .setIgnoreStrengthStats(true)
            .setDamageRange(0..30)
            .build()
    )

}

class DwarfCannon(
    var obj: DynamicGameObject
) {

    private var degradeTask: Task? = null

    var fireTask: Task? = null
    var rotation: CannonRotation = CannonRotation.NORTH
    val ammoQueue: MinMaxPriorityQueue<CannonAmmo> = MinMaxPriorityQueue
        .orderedBy(Comparator<CannonAmmo> { o1, o2 -> o1.compareTo(o2) })
        .maximumSize(QUEUE_CAPACITY)
        .create()

    fun load() {
        val player = obj.spawnedFor.get()

        if (player.inventory.contains(ItemID.GRANITE_CANNONBALL)) {
            val avail = player.inventory.getAmount(ItemID.GRANITE_CANNONBALL)
            val space = QUEUE_CAPACITY - ammoQueue.size
            val loadAmt = min(avail, space)

            if (loadAmt > 0) {
                repeat(loadAmt) { ammoQueue.offer(CannonAmmo.GRANITE_CANNONBALL) }

                player.inventory.delete(ItemID.GRANITE_CANNONBALL, loadAmt)
                player.packetSender.sendMessage("You load the cannon with $loadAmt granite cannonballs.")
            }
        }

        if (player.inventory.contains(ItemID.CANNONBALL)) {
            val avail = player.inventory.getAmount(ItemID.CANNONBALL)
            val space = QUEUE_CAPACITY - ammoQueue.size
            val loadAmt = min(avail, space)

            if (loadAmt > 0) {
                repeat(loadAmt) { ammoQueue.offer(CannonAmmo.CANNONBALL) }

                player.inventory.delete(ItemID.CANNONBALL, loadAmt)
                player.packetSender.sendMessage("You load the cannon with $loadAmt cannonballs.")
            }
        }
    }

    fun unload() {
        val player = obj.spawnedFor.get()

        var normalCount = 0
        var graniteCount = 0

        if (ammoQueue.isEmpty()) {
            player.sendMessage("Your cannon is already empty.")
            return
        }

        while (ammoQueue.isNotEmpty()) {
            val ammo = ammoQueue.poll()

            if (ammo == CannonAmmo.GRANITE_CANNONBALL) {
                graniteCount++
            } else {
                normalCount++
            }
        }

        if (graniteCount > 0) {
            player.inventory.add(ItemID.GRANITE_CANNONBALL, graniteCount)
            player.packetSender.sendMessage("You unload your cannon and receive Granite Cannonball x $graniteCount.")
        }

        if (normalCount > 0) {
            player.inventory.add(ItemID.CANNONBALL, normalCount)
            player.packetSender.sendMessage("You unload your cannon and receive Cannonball x $normalCount.")
        }
    }

    fun pickup(onLogout: Boolean) {
        val player = obj.spawnedFor.get()

        val requiredSlots = if (player.inventory.contains(ItemID.CANNON_BALL)) 4 else 5

        if (!onLogout) {
            if (player.inventory.countFreeSlots() <= requiredSlots) {
                player.sendMessage("You need at least $requiredSlots free inventory slots to pickup your cannon.")
                return;
            }
        }

        unload()
        fireTask?.stop()
        degradeTask?.stop()

        player.setPositionToFace(obj.position.copy().move(Direction.NORTH_EAST), true)
        player.performAnimation(Animation(827))
        player.packetSender.sendSound(Sounds.PICKUP_ITEM)
        ObjectManager.remove(obj, true)

        CANNON_ITEM_IDS.forEach {
            if (player.inventory.canHold(it, 1)) {
                player.inventory.add(it, 1)
            } else {
                player.getBank(0).add(it, 1)
            }
        }

        player.cannon = null
        player.packetSender.sendMessage("You pick up the cannon. It's really heavy.")
    }

    fun onLogout() {
        val player = obj.spawnedFor.get()

        unload()
        fireTask?.stop()
        degradeTask?.stop()

        player.setPositionToFace(obj.position.copy().move(Direction.NORTH_EAST), true)
        player.performAnimation(Animation(827))
        player.packetSender.sendSound(Sounds.PICKUP_ITEM)
        ObjectManager.remove(obj, true)

        CANNON_ITEM_IDS.forEach {
            if (player.inventory.canHold(it, 1)) {
                player.inventory.add(it, 1)
            } else {
                player.getBank(0).add(it, 1)
            }
        }

        player.cannon = null
        player.packetSender.sendMessage("You pick up the cannon. It's really heavy.")
    }

    fun setupDegradeTask() {
        val player = obj.spawnedFor.get()

        player.markTime(CANNON_DEGRADE_TIMER)
        val delay = TimeUnit.MINUTES.toCycles(5) + 1
        degradeTask = object : Task(delay) {

            var warned = false

            override fun execute() {
                val passed = player.getTimePassed(CANNON_DEGRADE_TIMER, TimeUnit.MINUTES)
                if (!warned && passed >= DEGRADE_TIME) {
                    fireTask?.stop()
                    ObjectManager.remove(obj, true)
                    obj = DynamicGameObject.createPublic(ObjectID.BROKEN_MULTICANNON_2, obj.position, 10, 0)
                    obj.setSpawnedFor(player)
                    ObjectManager.add(obj, true)
                    player.packetSender.sendMessage("Your cannon has broken.")
                    player.markTime(CANNON_DEGRADE_TIMER)
                    warned = true
                } else if (warned && passed >= DISAPPEAR_TIME) {
                    ObjectManager.remove(obj, true)
                    player.cannon = null
                    player.packetSender.sendMessage("Your cannon has decayed. Speak to Nulodion to get a new one!.")
                    player.setBoolean(CANNON_RECLAIM_STATUS, true)
                    stop()
                }
            }

            override fun stop() {
                super.stop()
                degradeTask = null
            }
        }

        TaskManager.submit(degradeTask)
    }

    fun repair() {
        val player = obj.spawnedFor.get()
        ObjectManager.remove(obj, true)
        obj = DynamicGameObject.createPublic(ObjectID.DWARF_MULTICANNON, obj.position, 10, 0)
        obj.setSpawnedFor(player)
        ObjectManager.add(obj, true)
        player.packetSender.sendMessage("You repair your cannon, restoring it to working order.")
        player.packetSender.sendSound(1770)

        degradeTask?.stop()
        setupDegradeTask()
    }

    companion object {

        private const val QUEUE_CAPACITY = 30
        private const val DEGRADE_TIME = 25
        private const val DISAPPEAR_TIME = 10

        private val CANNON_ITEM_IDS = intArrayOf(
            ItemID.CANNON_BASE,
            ItemID.CANNON_STAND,
            ItemID.CANNON_BARRELS,
            ItemID.CANNON_FURNACE
        )

    }
}

object DwarfMulticannon {

    private const val PROJ_ID = 53
    private const val RADIUS = 16
    private const val XP_PER_DMG = 2

    init {

        ObjectActions.onClick(ObjectID.DWARF_MULTICANNON) { action ->

            val player = action.player
            val obj = action.objectActionMessage.gameObject

            if (player.cannon == null || player.cannon.obj != obj) {
                player.packetSender.sendMessage("That is not your cannon.")
                return@onClick false
            }

            when (action.type) {
                ObjectActions.ClickAction.Type.FIRST_OPTION -> handleFire(player)
                ObjectActions.ClickAction.Type.SECOND_OPTION -> player.cannon.pickup(false)
                ObjectActions.ClickAction.Type.THIRD_OPTION -> player.cannon.unload()
                else -> return@onClick false
            }

            return@onClick true
        }

        ObjectActions.onClick(ObjectID.BROKEN_MULTICANNON_2) { action ->
            val player = action.player
            val obj = action.objectActionMessage.gameObject

            if (player.cannon == null || player.cannon.obj != obj) {
                player.packetSender.sendMessage("That is not your cannon.")
                return@onClick false
            }

            when (action.type) {
                ObjectActions.ClickAction.Type.FIRST_OPTION -> player.cannon.repair()
                ObjectActions.ClickAction.Type.SECOND_OPTION -> player.cannon.pickup(false)
                else -> return@onClick false
            }

            return@onClick true
        }

    }

    private fun handleFire(player: Player) {
        val cannon = player.cannon
        cannon.load()

        if (cannon.fireTask == null) {
            cannon.fireTask = object : Task() {

                override fun execute() {
                    if (cannon.ammoQueue.isEmpty()) {
                        player.packetSender.sendMessage("Your cannon has ran out of ammo.")
                        stop()
                        return
                    }

                    val rotation = cannon.rotation
                    val from = cannon.obj.position.copy().move(Direction.NORTH_EAST)

                    val targets = getTargets(player, from, rotation)
                    targets.firstOrNull()?.let { npc ->
                        val ammo = cannon.ammoQueue.poll()

                        val projBldr = ProjectileTemplate.builder(PROJ_ID)
                            .setSpeed(5)
                            .setSourceOffset(32)
                            .setStartHeight(32)
                            .setEndHeight(npc)
                            .setCurve(2)

                        player.packetSender.sendAreaEntitySound(cannon.obj, 1667, 10, 1, 0)
                        val projectile = Projectile(from, npc, projBldr.build())
                        projectile.sendProjectile()
                        projectile.onArrival {
                            if (!npc.combat.hasTarget()) {
                                npc.combat.initiateCombat(player)
                            }

                            if (isAccurate(player, npc)) {
                                val hit = Hit(player, npc, RangedAttackStrategy.INSTANCE, ammo!!.hit)
                                npc.combat.queue(hit)
                                player.skillManager.addExperience(Skill.RANGED, hit.totalDamage * XP_PER_DMG)
                            } else {
                                npc.combat.queue(Damage.createBlockedHit())
                            }
                        }
                    }

                    cannon.obj.performAnimation(Animation(rotation.animation))
                    cannon.rotation = CannonRotation.VALUES[rotation.next]
                    player.packetSender.sendAreaEntitySound(cannon.obj, 2877, 10, 1, 0)
                }

                override fun stop() {
                    super.stop()
                    cannon.fireTask = null
                }

            }

            TaskManager.submit(player.cannon.fireTask)
        }
    }

    fun getTargets(player: Player, position: Position, rotation: CannonRotation): List<NPC> {
        val targets = fetchTargets(player, position, rotation)

        if (player.combat.isBeingAttacked && !AreaManager.inMulti(player)) {
            return targets.filter { player.combat.isBeingAttackedBy(it) }
        }

        return targets
    }

    private fun fetchTargets(player: Player, pos: Position, rotation: CannonRotation): List<NPC> {
        return player.localNpcs
            .filter { it.fetchDefinition().isAttackable }
            .filter { it.isAlive }
            .filter { !it.isDying }
            .filter { !it.isHide }
            .filter { it.combat.canBeAttackedBy(player, true) }
            .filter { it.position.isWithinDistance(pos, RADIUS) }
            .filter { it.fetchDefinition().id != NpcID.COMBAT_DUMMY || it.fetchDefinition().id != NpcID.UNDEAD_COMBAT_DUMMY }
            .also { npcs ->
                if (rotation.vectors.isNotEmpty()) {
                    npcs.filter { npc ->
                        rotation.vectors
                            .map { pos.copy().move(it) }
                            .any { it == npc.position }
                    }.let {
                        if (it.isNotEmpty()) {
                            return it
                        }
                    }
                }
            }
            .filter {
                when (rotation) {
                    CannonRotation.NORTH_WEST -> it.position.x - pos.x < -1 && it.position.y - pos.y > 1
                    CannonRotation.SOUTH_WEST -> it.position.x - pos.x < -1 && it.position.y - pos.y < -1
                    CannonRotation.NORTH_EAST -> it.position.x - pos.x > 1 && it.position.y - pos.y > 1
                    CannonRotation.SOUTH_EAST -> it.position.x - pos.x > 1 && it.position.y - pos.y < -1
                    CannonRotation.NORTH -> (pos.x - it.position.x).absoluteValue <= 1 && it.position.y - pos.y > 1
                    CannonRotation.SOUTH -> (pos.x - it.position.x).absoluteValue <= 1 && it.position.y - pos.y < -1
                    CannonRotation.EAST -> it.position.x - pos.x > 1 && (pos.y - it.position.y).absoluteValue <= 1
                    CannonRotation.WEST -> it.position.x - pos.x < -1 && (pos.y - it.position.y).absoluteValue <= 1
                }
            }
            .filter { LineOfSight.withinSight(pos, it) }
    }

    private fun isAccurate(
        actor: Player,
        target: Agent
    ): Boolean {
        val ranged = actor.combat.fightType.mode == AttackMode.RANGE
        val weapon = actor.equipment.atSlot(EquipmentConstants.WEAPON_SLOT)
        val powered = when (weapon.id) {
            ItemID.STARTER_STAFF -> true
            ItemID.TRIDENT_OF_THE_SEAS -> true
            ItemID.TRIDENT_OF_THE_SEAS_FULL_ -> true
            ItemID.TRIDENT_OF_THE_SWAMP -> true
            ItemID.SANGUINESTI_STAFF -> true
            ItemID.HOLY_SANGUINESTI_STAFF -> true
            ItemID.CRYSTAL_STAFF_BASIC -> true
            ItemID.CRYSTAL_STAFF_ATTUNED -> true
            ItemID.CRYSTAL_STAFF_PERFECTED -> true
            else -> false
        }

        val snapshotTriple = when {
            powered -> {
                val melee = getSnapshotMaxAttackRollPair(actor, target, AttackType.MELEE)
                val range = getSnapshotMaxAttackRollPair(actor, target, AttackType.RANGED)
                if (melee.third > range.third) {
                    melee
                } else {
                    range
                }
            }

            ranged -> getSnapshotMaxAttackRollPair(actor, target, AttackType.RANGED)
            else -> getSnapshotMaxAttackRollPair(actor, target, AttackType.MELEE)
        }

        val targetDefenceRoll = CombatFormulaType.RANGED
            .calculateMaxDefenceRoll(snapshotTriple.first, snapshotTriple.second)
            .toInt()

        return CombatFormulas.rollAccuracy(snapshotTriple.third, targetDefenceRoll)
    }

    private fun getSnapshotMaxAttackRollPair(
        actor: Agent,
        target: Agent,
        type: AttackType
    ): Triple<CombatSnapshot, CombatSnapshot, Int> {
        val strategy = when (type) {
            AttackType.RANGED -> RangedAttackStrategy.INSTANCE
            else -> MeleeAttackStrategy.INSTANCE
        }

        val context = AttackContext(actor.combat, target, strategy)
        val snapshot = CombatSnapshot.create(actor, context, type)
        val targetSnapshot = CombatSnapshot.create(target, context, AttackType.RANGED)
        return Triple(
            snapshot, targetSnapshot, CombatFormulaType
                .getFormula(type)
                .calculateMaxAttackRoll(snapshot, targetSnapshot)
                .toInt()
        )

    }

}