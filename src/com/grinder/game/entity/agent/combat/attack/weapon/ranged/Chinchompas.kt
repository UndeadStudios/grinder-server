package com.grinder.game.entity.agent.combat.attack.weapon.ranged

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.removeInterfaces
import com.grinder.game.model.areas.Area
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.areas.impl.WildernessArea
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.ItemUtil
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID

/**
 * Created by Kyle Fricilone on Jun 23, 2020.
 */
object Chinchompas {

    private const val PVM_CAP = 11
    private const val PVP_CAP = 9

    private val CHINS = intArrayOf(
        ItemID.CHINCHOMPA_2,
        ItemID.RED_CHINCHOMPA_2,
        ItemID.BLACK_CHINCHOMPA
    )

    /**
     * Handles chinchompas dropping
     */
    fun handleChinDropping(player: Player, itemSlot: Int, item: Item) {
        DialogueBuilder(DialogueType.OPTION).setOptionTitle("Drop all of your " + item.definition.name + "?")
            .firstOption("Yes.") {
                player.removeInterfaces()
                player.inventory.set(itemSlot, ItemUtil.createInvalidItem())
                player.inventory.refreshItems()
                player.packetSender.sendSound(Sounds.CHIN_DROP)
                player.sendMessage("You release the chinchompa and it bounds away.")
                }
            .addCancel("No.")
            .start(player)
    }

    fun isAnyChin(item: Item) = CHINS.any { it == item.id }

    // Note: Because chinchompas are live animals, if a player dies with chinchompas in their inventory or equipped, the chinchompas will run away and cannot be retrieved.
    // However, if a player dies in the Wilderness, the killer can pick up the killed player's chinchompas.
    fun isDropable(item: Item, area: Area) = isAnyChin(item) && area is WildernessArea

    fun canUse(player: Player) = player.equipment.containsAny(*CHINS)

    fun postHit(player: Player, hit: Hit) {
        if (hit.missed()) {
            return;
        }

        val target = hit.target
        val agents: List<Agent> = if (target.isPlayer) {
            player.localPlayers
        } else {
            player.localNpcs
        }

        agents.asSequence()
            .filterNot { it == target }
            .filter { if (it.isNpc) it.asNpc.fetchDefinition().isAttackable else it.combat.canBeAttackedBy(player, true) }
            .filter { AreaManager.inMulti(it) }
            .filter { target.position.isWithinDistance(it.position, 1)}
            .take(if (target.isPlayer) PVP_CAP else PVM_CAP)
            .forEach {
                val hit = Hit(player, it, RangedAttackStrategy.INSTANCE, true, 1, 0)
                    .setHandleAfterHitEffects(false)

                player.combat.queueOutgoingHit(hit)
            }
    }

}