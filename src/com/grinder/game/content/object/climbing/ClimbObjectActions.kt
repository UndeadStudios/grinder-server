package com.grinder.game.content.`object`.climbing

import com.grinder.game.content.`object`.climbing.ClimbObjectUtil.ignore
import com.grinder.game.content.`object`.climbing.ClimbableObjectType.Companion.forDefinition
import com.grinder.game.definition.ObjectDefinition
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.Skill
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.game.model.sound.AreaSound
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.util.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.set

/**
 * Handles object action packets for climbable objects.
 *
 * @author ?
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 */
object ClimbObjectActions {

    private val ON_MOVE_ACTION: MutableMap<Int, Player.() -> Unit> = HashMap()
    private val DISABLED_IDS: MutableSet<Int> = HashSet()

    @JvmStatic
	fun handleClimbObject(player: Player, obj: GameObject, definition: ObjectDefinition, position: Position, optionIndex: Int): Boolean {

        if (ignore(definition))
            return false

            if (player.isJailed && definition.id == ObjectID.LADDER_154) {
                handleLadderInJail(player)
                return true
            }

            if (definition.id == ObjectID.LADDER_30 && !player.getBoolean(Attribute.PAID_BRIMHAVEN_AGILITY_FEE)) {
                promptBrimHavenAgilityFee(player);
                return true
            }
            if (definition.id == ObjectID.LADDER_206) {
                player.sendMessage("This ladder seems to be broken.")
                return true
            }

            if (definition.id == ObjectID.LADDER_354) {
                if (player.skillManager.getCurrentLevel(Skill.MINING) < 60) {
                    player.sendMessage("You need a Mining level of 60 to enter the Mining Guild.")
                    DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(4512)
                        .setText("Sorry, but you need level 60 Mining to go in there.").start(player)
                    return true
                }
            }

            if (DISABLED_IDS.contains(definition.id)) {
                player.sendMessage("This ladder seems to be broken and cannot be used.")
                return true
            }

            val climbableObjectType = forDefinition(definition) ?: return false
            val climbDirection = ClimbDirection[climbableObjectType, definition, optionIndex] ?: return false
            val climbAnimation = climbableObjectType.moveAnimation(climbDirection)

            val preClimbHeight = player.position.z
            var postClimbHeight = when {
                climbDirection === ClimbDirection.UP -> preClimbHeight + 1
                else -> preClimbHeight - 1
            }

            if (!player.passedTime(Attribute.GENERIC_ACTION, 3, TimeUnit.SECONDS))
                return true

            player.resetInteractions(combat = true)

            if (climbAnimation != null)
                player.performAnimation(climbAnimation)

            player.block(blockMovement = true)

            Logging.log(
                "climb_actions", player.username +
                        " has climbed " + climbDirection + " a " + climbableObjectType + " | position = " + position +
                        " | anim = " + climbAnimation + " | current height = " + preClimbHeight +
                        " | newHeight = " + postClimbHeight + " | objectId = " + definition.id +
                        " | objectName = " + definition.name
            )

            TaskManager.submit(climbableObjectType.moveToDelay()) {
                player.unblock(unblockMovement = true)
                val playerPosition = player.position
                val transformer = ClimbDestinations.DESTINATIONS[definition.id]
                var destination: Position? = null

                if (transformer != null)
                    destination = transformer.apply(postClimbHeight, playerPosition)

                if (destination == null) {

                    if (climbableObjectType == ClimbableObjectType.LADDER) {
                        postClimbHeight = preClimbHeight

                        if (climbDirection == ClimbDirection.UP) {
                            destination = if (player.position.y >= 6400) {
                                playerPosition.transform(0, -6400, 0)
                            } else {
                                if (preClimbHeight < 3) {
                                    playerPosition.transform(0, 0, +1)
                                } else {
                                    player.message("You cannot climb ${climbDirection.name.toLowerCase()} here.")
                                    return@submit
                                }
                            }
                        } else {
                            destination = if (preClimbHeight == 0 && player.position.y <= 6400) {
                                playerPosition.transform(0, +6400, 0)
                            } else if (preClimbHeight > 0 && player.position.y >= 6400) {
                                playerPosition.transform(0, 0, -1)
                            } else if (preClimbHeight > 0 && player.position.y <= 6400) {
                                playerPosition.transform(0, 0, -1)
                            } else if (preClimbHeight > 0) {
                                playerPosition.transform(0, 0, -1)
                            } else {
                                player.message("You cannot climb ${climbDirection.name.toLowerCase()} here.")
                                return@submit
                            }
                        }
                    }

                    /*if (preClimbHeight == 0) {
                            val yOffset = when (climbDirection) {
                                ClimbDirection.UP -> -6400
                                ClimbDirection.DOWN -> +6400
                            }
                            destination = playerPosition.transform(0, yOffset, 0)
                        }
                    }

                    if (destination?.y?:0 < 0) {
                        val zOffset = when (climbDirection){
                            ClimbDirection.UP -> +1
                            ClimbDirection.DOWN -> -1
                        }
                        destination = playerPosition.transform(0, 0, zOffset)
                        player.message("You cannot climb ${climbDirection.name.toLowerCase()} here.")
                        //return@submit
                    }*/

                    if (postClimbHeight < 0 || postClimbHeight > 3) {
                        player.message("You cannot climb ${climbDirection.name.toLowerCase()} here.")
                        return@submit
                    }

                    if (climbableObjectType === ClimbableObjectType.PIPE)
                        return@submit

                    if (climbableObjectType == ClimbableObjectType.STAIRS
                        || climbableObjectType == ClimbableObjectType.STAIRCASE
                    ) {
                        player.sendDevelopersMessage("face=" + obj.face + " dir=${obj.facing} " + definition.getSizeX() + ", " + definition.getSizeY())


                        /*
                         * Fix for Wilderness Stairs Glitch
                         */
                        if (handleRogueCastleStairs(obj, player, climbDirection)) {
                            return@submit
                        }

                        if (definition.getSizeX() >= 2 && definition.getSizeY() >= 2) {

                            val (dx, dy) = when (obj.face) {
                                0 -> 0 to 4
                                1 -> 4 to 0
                                2 -> 0 to -4
                                else -> 0 to 0
                            }
                            val up = preClimbHeight < postClimbHeight
                            destination = if (up) {
                                Position(playerPosition.x + dx, playerPosition.y + dy, postClimbHeight)
                            } else
                                Position(playerPosition.x - dx, playerPosition.y - dy, postClimbHeight)
                        }
                    }
                    if (destination == null)
                        destination = Position(playerPosition.x, playerPosition.y, postClimbHeight)
                }

                ON_MOVE_ACTION[definition.id]?.invoke(player)

                //TODO: Godwars quick fix, needs better way of doing this...
                if (destination.sameAs(Position(2915, 5300, 2)))
                    destination = Position(2912, 5300, 2);
                if (destination.sameAs(Position(2919, 5274, 1)))
                    destination = Position(2920, 5276, 1);

                player.moveTo(destination)

                // Increase points
                player.points.increase(AttributeManager.Points.LADDERS_CLIMBED, 1) // Increase points


                if (climbableObjectType.sendClimbMessage())
                    player.message("You climb " + climbDirection.formatted() + " the " + climbableObjectType.formatted() + ".")
            }
        return true
    }
    /**
     * Resolves the error with the stairs responding improperly; allowing users to run around on Height level 3 across Wildy.
     */
    private fun handleRogueCastleStairs(obj: GameObject, player: Player, climbDirection: ClimbDirection): Boolean {

        var stairsArray: IntArray = intArrayOf(ObjectID.STAIRCASE_76, ObjectID.STAIRCASE_77, ObjectID.STAIRCASE_78)

        if (stairsArray.contains(obj.id)) {

            var destination: Position = if (climbDirection == ClimbDirection.UP) {
                player.position.clone().setZ(player.position.z + 1)
            } else {
                player.position.clone().setZ(player.position.z - 1)
            }

            ON_MOVE_ACTION[obj.definition.id]?.invoke(player)

            player.moveTo(destination)

            // Increase points
            player.points.increase(AttributeManager.Points.LADDERS_CLIMBED, 1) // Increase points
            player.sendMessage("You climb ${climbDirection.formatted()} the stairs.")
            return true
        }
        return false
    }

    private fun handleLadderInJail(player: Player) {
        player.performAnimation(Animation(828, 25))
        TaskManager.submit(2) {
            player.say("Ouch!!")
            player.performAnimation(Animation(2242))
            val areaSound: AreaSound = when {
                player.appearance.isMale -> AreaSound(Misc.randomInt(*Sounds.DEFAULT_MALE_UNPROCTED_BLOCK), 250, 1, 12)
                else -> AreaSound(Misc.randomInt(*Sounds.DEFAULT_FEMALE_UNPROCTED_BLOCK), 250, 1, 12)
            }
            player.playAreaSound(areaSound)
            player.message("Hahaha nice try " + player.username + "!", 0)
            TaskManager.submit(2) { player.performAnimation(Animation(-1)) }
        }
    }

    private fun promptBrimHavenAgilityFee(player: Player) {
        player.localNpcs.stream().filter { npc: NPC -> npc.id == NpcID.CAPN_IZZY_NOBEARD }
            .min(Comparator.comparingInt { npc: NPC -> npc.position.getDistance(player.position) })
            .ifPresent { IZZY_NPC: NPC ->
                player.positionToFace = IZZY_NPC.position
                IZZY_NPC.setEntityInteraction(player)
                TaskManager.submit(4) {
                    IZZY_NPC.resetEntityInteraction()
                    IZZY_NPC.handlePositionFacing()
                }
            }
        DialogueBuilder(DialogueType.NPC_STATEMENT)
            .setNpcChatHead(NpcID.PARROT_3853)
            .setText("Clap him in irons!")
            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.CAPN_IZZY_NOBEARD).setExpression(DialogueExpression.ANGRY)
            .setText("Ahoy there! Pay up first!")
            .start(player);
    }

    init {
        DISABLED_IDS.add(ObjectID.IRON_LADDER_4)
        DISABLED_IDS.add(ObjectID.LADDER_153)
        ON_MOVE_ACTION[ObjectID.ROPE_4] = { performAnimation(Animation(748, 25)) }
        ON_MOVE_ACTION[ObjectID.ROPE_5] = { performAnimation(Animation(748, 25)) }
        ON_MOVE_ACTION[ObjectID.ROPE_7] = { performAnimation(Animation(748, 25)) }
    }
}