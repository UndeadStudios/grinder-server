package com.grinder.game.content.item

import com.grinder.game.entity.agent.npc.monster.impl.Cows
import com.grinder.game.entity.`object`.name
import com.grinder.game.entity.agent.player.event.PlayerEvents
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.playAreaSound
import com.grinder.game.entity.agent.player.replaceInventoryItem
import com.grinder.game.entity.agent.player.subscribe
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.removeAttribute
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.Animation
import com.grinder.game.model.ItemActions
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.name
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.ObjectID.DAIRY_COW

/**
 * Handles filling of water containers
 *
 * @author Eli
 * @author Spetsnaz
 * @author Stan van der Bend
 */
object WaterSource {

    private val EMOTE = Animation(832)

    /**
     * Array of water containers {empty, full}
     */
    public val CONTAINERS = mapOf(
            (229 to 227),
            (1935 to 1937),
            (1923 to 1921),
            (1925 to 1929),
            (6667 to 6668))

    /**
     * Array of water sources' object ids.
     */
    private val sources = intArrayOf(880, 884, 874, 879, 878, 873, 25929, 25729, 24004, 24102, 24150, 8699, 21355, 16705, 14868, 14998, 6249, 6232, 7143, 3641, 7422, 5125, 1763, 43)

    init {
        for (pair in CONTAINERS) {
            ItemActions.onItemOnObjectByItemId(pair.key) {

                if (getObjectId() == DAIRY_COW) {
                    Cows.milk(player)
                    return@onItemOnObjectByItemId true
                }

                if (sources.contains(getObjectId())) {

                    if (player.getBoolean(Attribute.FILLING_WATER_CONTAINERS))
                        return@onItemOnObjectByItemId true

                    player.setBoolean(Attribute.FILLING_WATER_CONTAINERS, true)

                    val task = object:Task(3, true) {
                        override fun execute() {
                            val fullId = CONTAINERS[getItemId()]
                            val toFill = Item(getItemId(), 1)
                            val amount = player.inventory.getAmount(getItemId())
                            if (fullId == null || amount == 0 || !player.getBoolean(Attribute.FILLING_WATER_CONTAINERS)){
                                player.message("You have filled all of your ${toFill.name()}s.")
                                stop()
                                return
                            }
                            player.performAnimation(EMOTE)
                            if (player.replaceInventoryItem(toFill, Item(fullId, 1))){
                                player.playAreaSound(Sounds.FILLING_POTION_FROM_FOUNTAIN, gameObject)
                                player.message("You fill the ${toFill.name()} from the ${gameObject.name()}.")
                            }
                        }

                        override fun stop() {
                            super.stop()
                            player.removeAttribute(Attribute.FILLING_WATER_CONTAINERS)
                        }
                    }
                    player.subscribe {
                        if (it is PlayerEvents){
                            if (task.isRunning)
                                TaskManager.cancelTasks(task)
                            return@subscribe true
                        }
                        return@subscribe false
                    }
                    TaskManager.submit(task)
                    return@onItemOnObjectByItemId true
                }
                return@onItemOnObjectByItemId false
            }
        }
    }
}