package com.grinder.game.model

import com.grinder.game.definition.ObjectDefinition
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.`object`.ObjectType
import com.grinder.game.entity.`object`.name
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.movement.task.WalkToAction
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.statement
import com.grinder.net.packet.impl.ObjectActionPacketListener
import com.grinder.util.Executable

/**
 * TODO: add documentation
 * TODO: add priority to listeners?
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   30/11/2019
 * @version 1.0
 */
object ObjectActions {

    /**
     * Position actions are executed before [objectActions].
     */
    private val positionActions = HashMap<Position, ArrayList<(ClickAction) -> Boolean>>()
    private val objectActions = HashMap<Int, ArrayList<(ClickAction) -> Boolean>>()
    private val disabledObjects = HashSet<Triple<Int, Int, Int>>()

    fun onClick(position: Position, function: ClickAction.() -> Boolean) {
        positionActions.putIfAbsent(position, ArrayList())
        positionActions[position]!!.add(function)
    }

    fun onClick(vararg positions: Position, function: (ClickAction) -> Boolean) {
        positions.forEach {
            onClick(position = it, function = function)
        }
    }

    fun onClick(vararg objectIds: Int, function: (ClickAction) -> Boolean) {
        objectIds.forEach {
            objectActions.putIfAbsent(it, ArrayList())
            objectActions[it]!!.add(function)
        }
    }

    fun onClick(objectIds: List<Int>, function: (ClickAction) -> Unit) {
        objectIds.forEach {
            objectActions.putIfAbsent(it, ArrayList())
            objectActions[it]!!.add { event -> function(event); true}
        }
    }

    fun handleClick(player: Player, objectActionDetails: ObjectActionDetails, type: ClickAction.Type, executeImmediately: Boolean) : Boolean {

        if(disabledObjects.contains(Triple(objectActionDetails.objectId, objectActionDetails.x, objectActionDetails.y))){
            player.statement("This object is currently disabled :(")
            return true
        }
        val idListeners = objectActions[objectActionDetails.objectId]
                ?: emptyList<(ClickAction) -> Boolean>()
        val positionListeners = positionActions[objectActionDetails.gameObject.position]
                ?: emptyList<(ClickAction) -> Boolean>()
        val listeners = positionListeners + idListeners

        if (listeners.isEmpty())
            return false

        ObjectActionPacketListener.preStartWalkTask(player)

        val gameObject = objectActionDetails.gameObject
        val executable = Executable {

            ObjectActionPacketListener.onExecutableStart(player, gameObject, gameObject.definition)

            val action = ClickAction(player, objectActionDetails, type)

            for (listener in listeners)
                if (listener.invoke(action))
                    return@Executable

            ObjectActionPacketListener.handleActionDefault(action, true)
        }

        if(executeImmediately)
            executable.execute()
        else
            player.setWalkToTask(WalkToAction(player, gameObject, 1, executable))
        return true
    }
    
    fun toggleClickable(objectId: Int, objectX: Int, objectY: Int) : Boolean {
        val triple = Triple(objectId, objectX, objectY)
        return if (disabledObjects.contains(triple)) {
            disabledObjects.remove(triple)
            false
        } else {
            disabledObjects.add(triple)
            true
        }
    }

    class ClickAction(val player: Player, val objectActionMessage: ObjectActionDetails, val type: Type) {

        fun getX() = objectActionMessage.x
        fun getY() = objectActionMessage.y
        fun getObject() = objectActionMessage.gameObject

        fun isFirstOption() = type == Type.FIRST_OPTION
        fun isSecondOption() = type == Type.SECOND_OPTION
        fun isThirdOption() = type == Type.THIRD_OPTION
        fun isFourthOption() = type == Type.FOURTH_OPTION
        fun isFifthOption() = type == Type.FIFTH_OPTION

        fun objectName() = getObject().name()

        fun actionNameContains(string: String, ignoreCase: Boolean = false) : Boolean {
            val actionName = getActionNameOrNull()?:return false
            return actionName.contains(string, ignoreCase)
        }

        fun getActionNameOrNull() : String? {
            val actions = getObject().definition?.actions?:return null
            if (actions.size > type.ordinal)
                return actions[type.ordinal]
            return null
        }

        enum class Type {
            FIRST_OPTION,
            SECOND_OPTION,
            THIRD_OPTION,
            FOURTH_OPTION,
            FIFTH_OPTION,
        }
    }

    class ObjectActionDetails(val objectId: Int, val x: Int, val y: Int, val opcode: Int, val gameObject: GameObject)

    /**
     * @author Tom <rspsmods@gmail.com>
     */
    fun faceObj(pawn: Agent, obj: GameObject, def: ObjectDefinition) {

        val rot = obj.face
        val type = obj.objectType

        when (type) {
            ObjectType.LENGTHWISE_WALL.value -> {
                if (!pawn.position.sameAs(obj.position)) {
                    pawn.setPositionToFace(obj.position, true)
                }
            }
            ObjectType.INTERACTABLE_WALL_DECORATION.value, ObjectType.INTERACTABLE_WALL.value -> {
                val dir = when (rot) {
                    0 -> Direction.WEST
                    1 -> Direction.NORTH
                    2 -> Direction.EAST
                    3 -> Direction.SOUTH
                    else -> throw IllegalStateException("Invalid object rotation: $obj")
                }
                pawn.setPositionToFace(pawn.position.clone().move(dir), true)
            }
            else -> {
                var width = def.sizeX
                var length = def.sizeY
                if (rot == 1 || rot == 3) {
                    width = def.sizeY
                    length = def.sizeX
                }
                pawn.setPositionToFace(obj.position.transform(width shr 1, length shr 1, 0), true, width, length)
            }
        }
    }
}