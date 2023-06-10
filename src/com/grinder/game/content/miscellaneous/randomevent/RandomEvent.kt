package com.grinder.game.content.miscellaneous.randomevent

import com.grinder.game.World
import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.entity.agent.inWilderness
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.npc.NPCFactory
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerSaving
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.passedTime
import com.grinder.game.entity.setBoolean
import com.grinder.game.entity.setInt
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.areas.InstancedArea
import com.grinder.game.model.areas.instanced.AquaisNeigeArea
import com.grinder.game.model.areas.instanced.FightCaveArea
import com.grinder.game.model.areas.instanced.PestControlArea
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.util.Misc
import com.grinder.util.NpcID
import com.grinder.util.oldgrinder.Area
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import kotlin.random.Random

/**
 * TODO: add documentation
 *
 * @author  Lou Grinder (https://www.rune-server.ee/members/Lou55/
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/11/2019
 * @version 1.0
 */
enum class RandomEvent : Consumer<Player> {

    FOOD_PUZZLE {
        override fun trigger(player: Player) {
            if (!player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT) && player.passedTime(Attribute.LAST_RANDOM_EVENT, 10L, TimeUnit.MINUTES,false)) {
                if (player.busy()) {
                    return
                }
                if (player.minigame != null) {
                    return;
                }
                if (player.area is PestControlArea) {
                    return
                }
                if (player.isJailed) {
                    return
                }
                if (AreaManager.DUEL_ARENA.contains(player)) {
                    return
                }
                if (AreaManager.DuelFightArena.contains(player)) {
                    return
                }
                if (AreaManager.MINIGAME_LOBBY.contains(player)) {
                    return
                }
                if (player.area != null && player.area is FightCaveArea) {
                    return
                }
                if (player.area != null && player.area is AquaisNeigeArea) {
                    return
                }
                if (player.area != null && player.area is PestControlArea) {
                    return
                }
                if (player.combat.isInCombat) {
                    return;
                }
                if (player.status === PlayerStatus.TRADING) {
                    return
                }
                if (player.status === PlayerStatus.BANKING) {
                    return
                }
                if (player.status === PlayerStatus.PRICE_CHECKING) {
                    return
                }
                if (player.status === PlayerStatus.DUELING) {
                    return
                }
                if (player.isInTutorial) {
                    return
                }
                if (player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT) || player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2)) {
                    return
                }
                if (player.getBoolean(Attribute.DID_FAIL_AGILITY_OBSTACLE, false)) {
                    return;
                }
                if (player.getBoolean(Attribute.STALL_HITS, false)) {
                    return;
                }
                if (player.inWilderness() || player.wildernessLevel > 0) {
                    accept(player)
                    return
                }
                if (AreaManager.CASTLE_WARS.contains(player)) {
                    return
                }
                if (player.getBoolean(Attribute.STALL_HITS, false)) {
                    return
                }
                player.BLOCK_ALL_BUT_TALKING = true;
                player.setBoolean(Attribute.HAS_PENDING_RANDOM_EVENT, true)
                player.motion.update(MovementStatus.NONE)
                player.motion.clearSteps()
                SkillUtil.stopSkillable(player)

                // Spawn the mysterious old man npc and make it face player/npc
                Area(4)
                    .getAbsolute(player.position)
                    .findRandomOpenPosition(player.plane, 1, player.position)
                    .ifPresent {
                        // Spawn the mysterious old man npc and make it face player/npc
                        val npc = NPCFactory.create(NpcID.SANDWICH_LADY, it)
                        npc.owner = player
                        npc.positionToFace = player.position
                        World.npcAddQueue.add(npc)

                        // Start the puff gfx and sound
                        npc.performGraphic(Graphic(188, GraphicHeight.HIGH))
                        player.packetSender.sendAreaPlayerSound(1930, 5, 1, 0)
                        TaskManager.submit(2) {
                            npc.say("" + player.username + "! Can you please help me choose the right sandwhich?!")
                            player.positionToFace = npc.position
                        }

                        // Npc casts animation after 3 ticks from spawning
                        TaskManager.submit(3) {
                            npc.performAnimation(Animation(864))
                        }

                        // Npc is removed
                        TaskManager.submit(5) {
                            npc.performGraphic(Graphic(188, GraphicHeight.HIGH))
                            if (npc.owner.username === player.username) World.npcRemoveQueue.add(npc)
                            player.packetSender.sendSound(Sounds.TELEOTHER_TELEPORTING)
                        }
                    }


                // Your current position is saved prior to  teleporting so you can get back
                player.oldPosition = player.position.clone()
                // Teleport player to the random event place

                TaskManager.submit(5) {
                    player.moveTo(Position(2401 + Misc.random(5), 2601 + Misc.random(4), 0))
                    PlayerSaving.save(player)
                    accept(player)
                    player.setBoolean(Attribute.HAS_PENDING_RANDOM_EVENT, true)
                    player.setBoolean(Attribute.RANDOM_FORFEIT, false)
                    player.BLOCK_ALL_BUT_TALKING = false
                }

                // Start the puff gfx and sound after you are in the random event place
                TaskManager.submit(6) {
                    player.performGraphic(Graphic(188, GraphicHeight.HIGH))
                    player.packetSender.sendAreaPlayerSound(1930, 5, 1, 0)
                }
                player.setBoolean(Attribute.DOING_FOOD_PUZZLE, true)
            }
        }

        override fun triggeronLogin(player: Player) {
                player.setBoolean(Attribute.HAS_PENDING_RANDOM_EVENT, true)
                player.setBoolean(Attribute.RANDOM_FORFEIT, false)
                PlayerSaving.save(player)
                SkillUtil.stopSkillable(player)
                player.oldPosition = player.position.clone()
                player.moveTo(Position(2401, 2601, 0))
                player.performGraphic(Graphic(188, GraphicHeight.HIGH))
                player.packetSender.sendAreaPlayerSound(1930, 5, 1, 0)
                accept(player)
                player.setBoolean(Attribute.DOING_FOOD_PUZZLE, true)
            }

        override fun accept(t: Player) {
            val packet = t.packetSender

            packet.sendMinimapFlagRemoval()
            packet.sendInterfaceModel(4545, 385, 250)
            packet.sendString(4553, "Shark")
            packet.sendInterfaceModel(4546, 379, 250)
            packet.sendString(4554, "Lobster")
            packet.sendInterfaceModel(4547, 391, 250)
            packet.sendString(4555, "Manta")
            packet.sendInterfaceModel(4548, 373, 250)
            packet.sendString(4556, "Swordfish")

            val fish = Misc.random(397, 7946, 365, 329, 361)!!

            when(Random.nextInt(1, 4)) {
                1 -> {
                    packet.sendInterfaceModel(4550, fish, 250);
                    packet.sendInterfaceModel(4551, Misc.random(1285, 9044, 1303, 7675, 1055, 13576), 250)
                    packet.sendInterfaceModel(4552, Misc.random(21003, 6889, 6570, 6585, 4310, 13241), 250)
                    t.setInt(Attribute.RANDOM_EVENT_PUZZLE, 1)
                }
                2 -> {
                    packet.sendInterfaceModel(4551, fish, 250)
                    packet.sendInterfaceModel(4550, Misc.random(6570, 21006, 1313, 1755, 11718, 590), 250)
                    packet.sendInterfaceModel(4552, Misc.random(2347, 8844, 1333, 1187, 1379, 1303), 250)
                    t.setInt(Attribute.RANDOM_EVENT_PUZZLE, 2)
                }
                3 -> {
                    packet.sendInterfaceModel(4552, fish, 250)
                    packet.sendInterfaceModel(4550, Misc.random(6570, 4718, 11802, 7804, 1303, 747), 250)
                    packet.sendInterfaceModel(4551, Misc.random(4087, 9044, 952, 2552, 1053, 11773), 250)
                    t.setInt(Attribute.RANDOM_EVENT_PUZZLE, 3)
                }
            }
            //packet.sendInterface(4543)
            t.packetSender.sendInterfaceSet(4543, 3321)

        }
    },
    REFRESHMENTS_PUZZLE {

        override fun trigger(player: Player) {
            if (!player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2) && player.passedTime(Attribute.LAST_RANDOM_EVENT, 10L, TimeUnit.MINUTES,false)) {

                if (player.busy())
                    return

                if (player.combat.isInCombat)
                    return

                SkillUtil.stopSkillable(player)
                player.setBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2, true)
                player.setBoolean(Attribute.RANDOM_FORFEIT, false)
                PlayerSaving.save(player)
                accept(player)
                player.setBoolean(Attribute.DOING_FOOD_PUZZLE, true)
            }
        }

        override fun triggeronLogin(player: Player) {
            SkillUtil.stopSkillable(player)
            player.setBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2, true)
            player.setBoolean(Attribute.RANDOM_FORFEIT, false)
            PlayerSaving.save(player)
            accept(player)
            player.setBoolean(Attribute.DOING_FOOD_PUZZLE, true)
        }

        override fun accept(t: Player) {
            val packet = t.packetSender
            packet.sendString(16131, " ")
            //packet.sendInterface(16135)
            t.packetSender.sendInterfaceSet(16135, 3321)
            val refreshment = RandomEventRefreshment.values().random()
            t.selectedRefreshment = refreshment
            packet.sendString(16145, refreshment.asSelectText())
        }
    };

    abstract fun trigger(player: Player)
    abstract fun triggeronLogin(player: Player)

}