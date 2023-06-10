package com.grinder.game.content.`object`

import com.grinder.game.content.miscellaneous.TravelSystem
import com.grinder.game.content.miscellaneous.TravelSystem.instantMoveTo
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.areas.impl.BossInstances
import com.grinder.game.model.areas.impl.BossInstances.Companion.instanceDialogue
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.onFirstObjectAction
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.util.Misc
import com.grinder.util.ObjectID

object Portals {
    
    init {

        // altar exit portals
        onFirstObjectAction(34748, 34750, 34751, 34752, 34753, 34754, 34757, 34756, 34755, 34758, 34759, 34825) {
            DialogueBuilder(DialogueType.STATEMENT)
                    .setText("This portal teleports you to the Aubury in Varrock.", "Are you absolutely sure you want to teleport?")
                    .add(DialogueType.OPTION)
                    .setOptionTitle("Select an Option")
                    .firstOption("Teleport Home.") { player ->
                        player.block()
                        player.removeInterfaces()
                        //player.motion.enqueueStepsTo(Position(it.getX(), it.getY(), 0), true)
                        //find(player, Position(it.getX(), it.getY(), 0), false)
                        player.motion.enqueuePathToWithoutCollisionChecks(
                            it.getX(),
                            it.getY()
                        )
                        TaskManager.submit(player, 3) {
                            player.unblock()
                            TeleportHandler.teleport(player, Position(3252 + Misc.random(1), 3400 + Misc.random(3), 0), player.spellbook.teleportType, true, true)
                        }
                    }
                    .addCancel("Stay here.")
                    .start(it.player)
        }

        onFirstObjectAction(ObjectID.EXIT_PORTAL_3) {
            TeleportHandler.teleport(it.player,
                    Position(3085 + Misc.random(3), 3490 + Misc.random(2)),
                    TeleportType.NORMAL, false, true)
        }

        onFirstObjectAction(ObjectID.EXIT_PORTAL_4, ObjectID.EXIT_PORTAL_5) {
            val player = it.player
            TeleportHandler.teleport(player,
                    Position(3323 + Misc.getRandomInclusive(2), 4968 + Misc.getRandomInclusive(5), 0),
                    TeleportType.NORMAL, false, false)
            player.resetAttributes()
        }

        onFirstObjectAction(ObjectID.EXIT_PORTAL_6) {
            val player = it.player
            val destination = when {
                player.y > 4785 -> Position(3446 + Misc.random(5), 4738 + Misc.random(14), 0)
                else -> Position(3446 + Misc.random(3), 4719 + Misc.random(13), 0)
            }
            TravelSystem.scheduleMoveTo(it, destination = destination)
        }
        
        onFirstObjectAction(ObjectID.PORTAL_66) {
            TeleportHandler.teleport(it.player,
                    Position(3287 + Misc.getRandomInclusive(9), 4953 + Misc.getRandomInclusive(10), 0),
                    TeleportType.NORMAL, false, false)
        }

        onFirstObjectAction(ObjectID.PORTAL_HOME) {
            it.player.message("The high voltage of the surroundings weakens the portal's ability to teleport.")
        }
        
        onFirstObjectAction(ObjectID.PORTAL_57, ObjectID.PORTAL_58, ObjectID.PORTAL_62, ObjectID.PORTAL_63) {
            val player = it.player
            val playerPos = player.position
            val destine = when {
                playerPos.x == 1863 && playerPos.y == 5239 -> Position(3081, 3421, 0)
                playerPos.x == 2364 && playerPos.y == 5212 -> Position(2123, 5252, 0)
                playerPos.y == 5239 || playerPos.y == 5240 -> Position(1863, 5239, 0)
                else -> Position(2040, 5240, 0)
            }
            instantMoveTo(player, destine)
        }
        
        // sea troll queen portal
        onFirstObjectAction(ObjectID.PORTAL_3) {
            val player = it.player
            if(player.position.y < 3894)
                instanceDialogue(
                    player, Position(2500, 3896), BossInstances.SEA_TROLL, moveTo = false
                )
            else
                instantMoveTo(player, Position(2514, 3883, 0))
        }

        // stronghold of security
        onFirstObjectAction(ObjectID.PORTAL_OF_DEATH, ObjectID.PORTAL_OF_DEATH_2, ObjectID.PORTAL_OF_DEATH_3, ObjectID.PORTAL_OF_DEATH_4) {
            val player = it.player
            player.message("You try to open the door..")
            player.playSound(Sounds.OPEN_BANK_BOOTH)
            val x = player.position.x
            val y = player.position.y
            val destine = getStrongHoldOfSecurityDeathDoorDestine(y, x, player)
            if(destine == null)
                player.message("Did not find action for interaction $it (please report to staff)", Color.RED)
            else {
                TravelSystem.scheduleMoveTo(it, Animation(2246, 15), 2, destine)
                TaskManager.submit(2) {
                    player.message("The magical door opens and teleports you further.")
                    player.playSound(Sounds.STRONGHOLD_SECURITY_DOOR_OPENING)
                }
            }
        }
    }

    private fun getStrongHoldOfSecurityDeathDoorDestine(y: Int, x: Int, player: Player): Position? {
        return if (y == 5206
                || (y == 5203 && (x == 2361 || x == 2362)) || (y == 5190 && (x == 2323 || x == 2324))
                || (y == 5187 && (x == 2323 || x == 2324)) || (y == 5212 && (x == 2319 || x == 2320))
                || (y == 5215 && (x == 2319 || x == 2320)) || (y == 5224 && (x == 2340 || x == 2341))
                || (y == 5221 && (x == 2340 || x == 2341)) || (y == 5239 && (x == 2323 || x == 2324))
                || (y == 5242 && (x == 2323 || x == 2324)) || (y == 5234 && (x == 2359 || x == 2360))
                || (y == 5231 && (x == 2359 || x == 2360)) || (y == 5221 && (x == 2355 || x == 2356))
                || (y == 5218 && (x == 2355 || x == 2356)) || (y == 5221 && (x == 2365 || x == 2366))
                || (y == 5218 && (x == 2365 || x == 2366))) {
            Position(x, y + 1, player.position.z)
        } else if (y == 5207
                || (y == 5204 && (x == 2361 || x == 2362)) || (y == 5191 && (x == 2323 || x == 2324))
                || (y == 5188 && (x == 2323 || x == 2324)) || (y == 5213 && (x == 2319 || x == 2320))
                || (y == 5216 && (x == 2319 || x == 2320)) || (y == 5225 && (x == 2340 || x == 2341))
                || (y == 5222 && (x == 2340 || x == 2341)) || (y == 5240 && (x == 2323 || x == 2324))
                || (y == 5243 && (x == 2323 || x == 2324)) || (y == 5235 && (x == 2359 || x == 2360))
                || (y == 5232 && (x == 2359 || x == 2360)) || (y == 5222 && (x == 2355 || x == 2356))
                || (y == 5219 && (x == 2355 || x == 2356)) || (y == 5222 && (x == 2365 || x == 2366))
                || (y == 5219 && (x == 2365 || x == 2366))) {
            Position(x, y - 1, player.position.z)
        } else if ((x == 2362 && (y == 5193 || y == 5194))
                || (x == 2359 && (y == 5193 || y == 5194)) || (x == 2361 && (y == 5188 || y == 5189))
                || (x == 2358 && (y == 5188 || y == 5189)) || (x == 2346 && (y == 5187 || y == 5188))
                || (x == 2343 && (y == 5187 || y == 5188)) || (x == 2335 && (y == 5193 || y == 5194))
                || (x == 2332 && (y == 5193 || y == 5194)) || (x == 2317 && (y == 5186 || y == 5187))
                || (x == 2314 && (y == 5186 || y == 5187)) || (x == 2308 && (y == 5204 || y == 5205))
                || (x == 2311 && (y == 5204 || y == 5205)) || (x == 2332 && (y == 5226 || y == 5227))
                || (x == 2335 && (y == 5226 || y == 5227)) || (x == 2310 && (y == 5224 || y == 5225))
                || (x == 2307 && (y == 5224 || y == 5225)) || (x == 2332 && (y == 5237 || y == 5238))
                || (x == 2335 && (y == 5237 || y == 5238)) || (x == 2352 && (y == 5245 || y == 5246))
                || (x == 2355 && (y == 5245 || y == 5246))) {
            Position(x + 1, y, player.position.z)
        } else if ((x == 2363 && (y == 5193 || y == 5194))
                || (x == 2360 && (y == 5193 || y == 5194)) || (x == 2362 && (y == 5188 || y == 5189))
                || (x == 2359 && (y == 5188 || y == 5189)) || (x == 2347 && (y == 5187 || y == 5188))
                || (x == 2344 && (y == 5187 || y == 5188)) || (x == 2336 && (y == 5193 || y == 5194))
                || (x == 2333 && (y == 5193 || y == 5194)) || (x == 2318 && (y == 5186 || y == 5187))
                || (x == 2315 && (y == 5186 || y == 5187)) || (x == 2309 && (y == 5204 || y == 5205))
                || (x == 2312 && (y == 5204 || y == 5205)) || (x == 2333 && (y == 5226 || y == 5227))
                || (x == 2336 && (y == 5226 || y == 5227)) || (x == 2311 && (y == 5224 || y == 5225))
                || (x == 2308 && (y == 5224 || y == 5225)) || (x == 2333 && (y == 5237 || y == 5238))
                || (x == 2336 && (y == 5237 || y == 5238)) || (x == 2353 && (y == 5245 || y == 5246))
                || (x == 2356 && (y == 5245 || y == 5246))) {
            Position(x - 1, y, player.position.z)
        } else
            null
    }

}