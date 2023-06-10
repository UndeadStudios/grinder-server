package com.grinder.game.content.miscellaneous

import com.grinder.game.World.npcAddQueue
import com.grinder.game.World.npcRemoveQueue
import com.grinder.game.content.minigame.MinigameManager
import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.entity.agent.npc.NPCFactory.create
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.*
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.areas.InstancedArea
import com.grinder.game.model.areas.instanced.AquaisNeigeArea
import com.grinder.game.model.areas.instanced.FightCaveArea
import com.grinder.game.model.areas.instanced.PestControlArea
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.NpcID
import com.grinder.util.ObjectID
import com.grinder.util.oldgrinder.Area

/**
 * A Mysterious man pops up and teleports the player to a location
 * this is used to help against botting.
 *
 * @author Blake
 */
object MysteriousManEvent {


    init {

        /*
        * Escape Rope
         */
        onFirstObjectAction(ObjectID.ESCAPE_ROPE) {
            it.player.performAnimation(Animation(828))
            it.player.BLOCK_ALL_BUT_TALKING = true
            TaskManager.submit(object : Task(2) {
                override fun execute() {
                    stop()
                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("This rope leads to a dead end...?", "Perhaps I should use one of the exit portals.")
                        .setExpression(DialogueExpression.ANNOYED)
                        .start(it.player)
                    it.player.BLOCK_ALL_BUT_TALKING = false
                }
            })
        }

        /**
         * Escape portal: Anti botting portal
         */
        onFirstObjectAction(ObjectID.PORTAL_7) {
            DialogueBuilder(DialogueType.STATEMENT)
                .setText("Are you absolutely sure you want to use this exit portal?")
                .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                .firstOption("Use portal.") { player ->
                    player.BLOCK_ALL_BUT_TALKING = true
                    player.motion.enqueuePathToWithoutCollisionChecks(
                        it.getX(),
                        it.getY()
                    )

                    // Remove interfaces
                    player.packetSender.sendInterfaceRemoval()



                    // Send event music
                    player.packetSender.sendJinglebitMusic(150, 0)

                    // Delay after moving inside the portal
                    TaskManager.submit(object : Task(3) {
                        override fun execute() {
                            stop()
                            player.BLOCK_ALL_BUT_TALKING = false
                            player.setBoolean(Attribute.HAS_TRIGGER_RANDOM_EVENT, false)
                            // Use portal
                            if (player.oldPosition != null) {
                                //player.moveTo(player.getOldPosition().clone());
                                TeleportHandler.teleportNoReq(
                                    player,
                                    player.oldPosition,
                                    player.spellbook.teleportType,
                                    false,
                                    false
                                )
                            } else {
                                TeleportHandler.teleportNoReq(
                                    player,
                                    Position(
                                        3086 + Misc.getRandomInclusive(2),
                                        3496 + Misc.getRandomInclusive(2),
                                        0
                                    ),
                                    player.spellbook.teleportType,
                                    false,
                                    false
                                )
                            }
                            player.motion.update(MovementStatus.NONE)
                            player.motion.clearSteps()
                            TaskManager.submit(object : Task(4) {
                                override fun execute() {
                                    stop()
                                    player.performGraphic(Graphic(188, GraphicHeight.HIGH))
                                    player.packetSender.sendAreaPlayerSound(1930)
                                    ItemContainerUtil.addOrDrop(player.inventory, player,  Item(ItemID.LAW_RUNE, Misc.random(250)))
                                    ItemContainerUtil.addOrDrop(player.inventory, player,  Item(ItemID.BLOOD_MONEY, Misc.random(1500)))
                                    player.sendMessage("You got rewarded with some Law runes and Blood money for finishing the event.");
                                    if (Misc.random(10) == 1) {
                                        player.getCollectionLog().createOrUpdateEntry(player, "Random Events",
                                            Item(ItemID.MYSTERY_BOX)
                                        )
                                        player.sendMessage("@red@You got a bonus Mystery box reward for finishing the event.");
                                        ItemContainerUtil.addOrDrop(player.inventory, player,  Item(ItemID.MYSTERY_BOX, 1))
                                    } else if (Misc.random(10) == 1) {
                                        player.sendMessage("@red@You got a bonus Book of knowledge reward for finishing the event.");
                                        ItemContainerUtil.addOrDrop(player.inventory, player,  Item(ItemID.BOOK_OF_KNOWLEDGE, 1))
                                    }
                                }
                            })
                            return
                        }
                    })
                }
                .addCancel("Don't use.").start(it.player)
        }
    }


            /**
             * Triggers the event
             *
             * @param player the [Player] to trigger the event on
             */
            fun trigger(player: Player) {
                if (player.minigame != null) {
                    return;
                }
                if (player.area is PestControlArea) {
                    return
                }
                if (player.isJailed) {
                    return
                }
                if (player.combat.isInCombat) {
                    return
                }
                if (AreaManager.DUEL_ARENA.contains(player)) {
                    return
                }
                if (MinigameManager.WEAPON_GAME.contains(player)) {
                    return
                }
                if (MinigameManager.BATTLE_ROYALE.contains(player)) {
                    return
                }
                if (AreaManager.DuelFightArena.contains(player)) {
                    return
                }
                if (player.area != null && player.area is FightCaveArea) {
                    return
                }
                if (player.area != null && player.area is AquaisNeigeArea) {
                    return
                }
                if (AreaManager.MINIGAME_LOBBY.contains(player)) {
                    return
                }
                if (AreaManager.CASTLE_WARS.contains(player)) {
                    return
                }
                if (player.getBoolean(Attribute.STALL_HITS, false)) {
                    return
                }
                if (player.area != null && player.area is InstancedArea) {
                    return
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
                if (player.getBoolean(Attribute.HAS_TRIGGER_RANDOM_EVENT)) {
                    return;
                }
                if (player.wildernessLevel > 0) {
                    return;
                }
                if (player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT) || player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2)) {
                    return
                }
                // Start the random event and block all actions
                player.BLOCK_ALL_BUT_TALKING = true
                player.setBoolean(Attribute.HAS_TRIGGER_RANDOM_EVENT, true)
                player.motion.update(MovementStatus.NONE)
                player.motion.clearSteps()
                SkillUtil.stopSkillable(player)


                // Spawn the mysterious old man npc and make it face player/npc
                Area(3).getAbsolute(player.position)
                    .findRandomOpenPosition(player.plane, 1, player.position)
                    .ifPresent {
                        val npc = create(NpcID.MYSTERIOUS_OLD_MAN_6750, it)
                        npc.owner = player
                        npc.positionToFace = player.position
                        npcAddQueue.add(npc)

                        // Start the puff gfx and sound
                        npc.performGraphic(Graphic(188, GraphicHeight.HIGH))
                        player.packetSender.sendAreaPlayerSound(1930, 5, 1, 0)
                        TaskManager.submit(2) {
                            npc.say("Hey you " + player.username + "! Why not come with me for a brief moment!")
                            player.positionToFace = npc.position
                        }
                        // Npc casts animation after 3 ticks from spawning
                        TaskManager.submit(3) {
                            npc.performAnimation(Animation(863))
                        }
                        // Npc is removed
                        TaskManager.submit(5) {
                            npc.performGraphic(Graphic(188, GraphicHeight.HIGH))
                            if (npc.owner.username === player.username) npcRemoveQueue.add(npc)
                            player.packetSender.sendSound(Sounds.TELEOTHER_TELEPORTING)
                        }
                }


                // Your current position is saved prior to  teleporting so you can get back
                player.oldPosition = player.position.clone()
                // Teleport player to the random event place
                TaskManager.submit(object : Task(5) {
                    override fun execute() {
                        stop()
                        player.moveTo(Position(2640 + Misc.random(2), 10024 + Misc.random(2), 0))
                        player.sendMessage("@red@Use any of the exit portals in this area leave.")
                        DialogueManager.sendStatement(player, "@red@Use any of the exit portals in this area leave.")
                        player.BLOCK_ALL_BUT_TALKING = false
                    }
                })
                // Start the puff gfx and sound after you are in the random event place
                TaskManager.submit(6) {
                    player.performGraphic(Graphic(188, GraphicHeight.HIGH))
                    player.packetSender.sendAreaPlayerSound(1930, 5, 1, 0)
                }
            }
        }
