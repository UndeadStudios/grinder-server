package com.grinder.game.content.skill.skillable.impl.hunter

import com.grinder.GrinderPlayerTest
import com.grinder.game.World
import com.grinder.game.entity.`object`.ObjectManager
import com.grinder.game.entity.agent.npc.NPCFactory
import com.grinder.game.message.impl.ItemActionMessage
import com.grinder.game.model.ItemActions
import com.grinder.game.model.ObjectActions
import com.grinder.game.model.Position
import com.grinder.game.model.Skill
import com.grinder.game.model.item.container.player.Inventory
import com.grinder.game.task.TaskManager
import com.grinder.net.packet.PacketConstants
import com.grinder.util.ServerClassPreLoader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class HunterTest
    : GrinderPlayerTest("hunter",
        printMessage = false,
        position = Position(3120, 3755))
{

    @BeforeEach
    fun setup(){
        ServerClassPreLoader.forceInit(HunterActions::class.java)
        HunterTraps.PLAYER_TRAPS[player.username]?.also {
            for(trap in it.toList())
                trap.remove()
        }
        player.skillManager.addExperience(Skill.HUNTER, 10_000_000)
    }

    @Test
    fun trapLootLifeCycle(){
        layTrap()

        var trap = HunterTraps.findTrap(player, player.position)
        Assertions.assertNotNull(trap)

        val prey = catchPrey(trap)

        val obj = trap!!.obj

        ObjectActions.handleClick(player,
                ObjectActions.ObjectActionDetails(obj.id, obj.position.x, obj.position.y, PacketConstants.OBJECT_FIRST_CLICK_OPCODE, obj),
                ObjectActions.ClickAction.Type.FIRST_OPTION,
                true)

        trap = HunterTraps.findTrap(player, player.position)
        Assertions.assertNull(trap)

        val optionalObj = ObjectManager.findDynamicObjectAt(player.position)
        Assertions.assertFalse(optionalObj.isPresent)

        Assertions.assertEquals(prey.loot, player.inventory[0])
    }

    @Test
    fun trapReset(){

        layTrap()

        var trap = HunterTraps.findTrap(player, player.position)
        Assertions.assertNotNull(trap)

        catchPrey(trap)

        val obj = trap!!.obj

        ObjectActions.handleClick(player,
                ObjectActions.ObjectActionDetails(obj.id, obj.position.x, obj.position.y, PacketConstants.OBJECT_SECOND_CLICK_OPCODE, obj),
                ObjectActions.ClickAction.Type.SECOND_OPTION,
                true)

        trap = HunterTraps.findTrap(player, player.position)
        Assertions.assertNotNull(trap)

        Assertions.assertEquals(HunterTrapState.WAITING, trap!!.state)

        val optionalObj = ObjectManager.findDynamicObjectAt(player.position)
        Assertions.assertTrue(optionalObj.isPresent)

    }

    private fun layTrap() {
        val type = HunterToolType.BOX_TRAP
        val tool = type.requiredItem!!.clone()
        player.inventory[0] = tool
        ItemActions.handleClick(player, ItemActionMessage(tool.id, 0, Inventory.INTERFACE_ID, PacketConstants.FIRST_ITEM_ACTION_OPCODE))
    }

    private fun catchPrey(trap: HunterTrap?): HunterCatchType {
        val prey = HunterCatchType.FERRET
        val npc = NPCFactory.create(prey.npcId, player.position.clone())
        World.npcs.add(npc)

        HunterTraps.process(npc)

        Assertions.assertTrue(trap!!.state != HunterTrapState.WAITING)

        TaskManager.sequence()
        TaskManager.sequence()
        TaskManager.sequence()
        return prey
    }

}