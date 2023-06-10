package com.grinder.game.content.miscellaneous

import com.grinder.game.World
import com.grinder.game.World.npcAddQueue
import com.grinder.game.World.npcRemoveQueue
import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.NPCFactory.create
import com.grinder.game.entity.agent.player.Appearance
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.game.entity.agent.player.removeInterfaces
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.*
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.areas.instanced.FightCaveArea
import com.grinder.game.model.areas.instanced.PestControlArea
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.item.container.shop.ShopManager
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.NpcID
import com.grinder.util.ShopIdentifiers
import com.grinder.util.oldgrinder.Area
import java.util.function.Consumer

/**
 * A frog prince event where you have to talk to the correct frog
 * and receive a token after a small dialogue which makes the npcs vanish
 * If no interaction within 30 ticks they will all vanish themselves.
 *
 * @author Blake
 */
object FrogPrinceEvent {

    init {
        onFirstNPCAction(NpcID.FROG_5432) {
            startFrogDialogue(player, npc)
        }
        onFirstNPCAction(NpcID.FROG_5833) {
            startPrinceFrogDialogue(player, npc)
        }
        onFirstNPCAction(NpcID.FROG_PRINCE, NpcID.FROG_PRINCESS) {
            princeDefaultDialogue(player, npc)
        }
    }

    /**
     * The regular frog dialogue for regular player and owner dialouge
     */
    private fun startFrogDialogue(player: Player, npc: NPC) {
        when (npc.owner) {
            player -> {
                DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                    .setText("Our frog princess is looking for you!")
                    .add(DialogueType.PLAYER_STATEMENT)
                    .setText("I guess okay..")
                    .start(player)
            }
            else -> {
                DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                    .setText("Is it our princess lucky day?")
                    .setExpression(DialogueExpression.HAPPY)
                    .add(DialogueType.PLAYER_STATEMENT)
                    .setText("Perhaps.")
                    .start(player)
            }
        }
    }

    /**
     * The default prince/princess dialogue when players interact with it
     */
    private fun princeDefaultDialogue(player: Player, npc: NPC) {
        when (npc.owner) {
            player -> {
                // Start dialogue
                DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                    .setExpression(DialogueExpression.HAPPY)
                    .setText(
                        "Thank you so much, " + player.username + ". I must return to",
                        "my fairytale kingdom. But take this as a reward. You",
                        "can exchange it at the Varrock clothes store for a",
                        "costume."
                    )
            }
            else -> {
                DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                    .setText("I am not looking for you " + player.username +".")
                    .setExpression(DialogueExpression.DISTRESSED)
                    .add(DialogueType.PLAYER_STATEMENT)
                    .setText("Okay I guess.")
                    .start(player)
            }
        }
    }

    /**
     * The regular frog dialogue for regular player and owner dialouge
     */
    private fun startPrinceFrogDialogue(player: Player, npc: NPC) {
        when (npc.owner) {
            player -> {
                DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                    .setText("I was transformed into a frog by a well-meaning but", "sadly frog-obsessed old wizard. Only a kiss can turn", "me back. Will you help me?")
                    .add(DialogueType.OPTION)
                    .firstOption("All right.") {
                        transformFrogToPrince(player, npc);
                    }
                    .addCancel("Eww, no way!")
                    .start(player)
            }
            else -> {
                DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                    .setText("I am not looking for you " + player.username +".")
                    .add(DialogueType.PLAYER_STATEMENT)
                    .setText("Who are you looking for then?")
                    .add(DialogueType.NPC_STATEMENT)
                    .setText("I was transformed into a frog by a well-meaning but", "sadly frog-obsessed old wizard. Only a kiss can turn", "me back from " + npc.owner.username +".")
                    .start(player)
            }
        }
    }

    /**
     * Transforms the prince frog into a prince or princess
     *
     * @param player the [Player] owner
     */
    private fun transformFrogToPrince(player: Player, npc: NPC) {

        // Remove interfaces
        player.removeInterfaces()

        // Block all actions
        player.BLOCK_ALL_BUT_TALKING = true

        // Perform npc and player animations
        npc.performAnimation(Animation(2374))
        player.performAnimation(Animation(2376))

        // Save the position for after transformation
        val princePosition = npc.position.clone()

        TaskManager.submit(object : Task(2) {
            override fun execute() {
                stop()
                npc.performAnimation(Animation(2375))
            }
        })

        // Remove the frog npc
        TaskManager.submit(object : Task(3) {
            override fun execute() {
                stop()
                npcRemoveQueue.add(npc)

                // Details of the prince/princess
                val id = if (player.appearance.isMale) NpcID.FROG_PRINCESS else NpcID.FROG_PRINCE
                val princeNPC = create(id, princePosition)

                // Add the new prince/princess
                npcAddQueue.add(princeNPC)

                // Perform graphic after transformation
                princeNPC.performGraphic(Graphics.SPLASH_GRAPHIC)

                // Set interactions
                princeNPC.setEntityInteraction(player);

                // Start dialogue
                DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(id)
                    .setExpression(DialogueExpression.HAPPY)
                    .setText(
                        "Thank you so much, " + player.username + ". I must return to",
                        "my fairytale kingdom. But take this as a reward. You",
                        "can exchange it at the Varrock clothes store for a",
                        "costume."
                    )
                    .setPostAction {
                        var receivedGift = false
                        if (!receivedGift) {

                            // Kiss player
                            npc.performAnimation(Animation(1374))
                            npc.performGraphic(Graphic(574, 25))

                            // Add item
                            ItemContainerUtil.addOrDrop(player.inventory, player, Item(ItemID.FROG_TOKEN))
                            receivedGift = true
                            player.getCollectionLog().createOrUpdateEntry(player, "Random Events", Item(ItemID.FROG_TOKEN))

                            // Block all actions
                            player.BLOCK_ALL_BUT_TALKING = false

                            // Remove npc
                            TaskManager.submit(object : Task(7) {
                                override fun execute() {
                                    stop()
                                    World.spawn(
                                        TileGraphic(
                                            princeNPC.position,
                                            Graphic(188, GraphicHeight.MIDDLE)
                                        )
                                    )
                                    player.packetSender.sendAreaEntitySound(princeNPC, 1930, 5, 1, 0)
                                    npcRemoveQueue.add(princeNPC)
                                }
                            })
                        }
                    }
                    .start(player)
            }
        })
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
        if (player.wildernessLevel > 0) {
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

        if (AreaManager.CASTLE_WARS.contains(player)) {
            return
        }
        if (player.getBoolean(Attribute.STALL_HITS, false)) {
            return
        }
        // Start the random event and block all actions
        SkillUtil.stopSkillable(player)

        // Spawn the frog stuff
        Area(3).getAbsolute(player.position).findRandomOpenPosition(player.plane, 1, player.position).ifPresent {
            val id = if (player.appearance.isMale) NpcID.FROG_PRINCESS else NpcID.FROG_PRINCE
            //val princeNPC = create(id, it)
            val frogPrince = create(NpcID.FROG_5833, it.randomize(3))
            val frogOne = create(NpcID.FROG_5432, it.randomize(3))
            val frogTwo = create(NpcID.FROG_5432, it.randomize(3))
            val frogThree = create(NpcID.FROG_5432, it.randomize(3))
            val frogFour = create(NpcID.FROG_5432, it.randomize(3))
            //princeNPC.owner = player
            frogPrince.owner = player
            frogOne.owner = player
            frogTwo.owner = player
            frogThree.owner = player
            frogFour.owner = player
            //princeNPC.movementCoordinator.radius = 3;
            frogPrince.movementCoordinator.radius = 3;
            frogOne.movementCoordinator.radius = 3;
            frogTwo.movementCoordinator.radius = 3;
            frogThree.movementCoordinator.radius = 3;
            frogFour.movementCoordinator.radius = 3;
            //npcAddQueue.add(princeNPC)

            Area(3).getAbsolute(player.position).findRandomOpenPosition(player.plane, 1, player.position).ifPresent {
                npcAddQueue.add(frogPrince)
                npcAddQueue.add(frogOne)
                npcAddQueue.add(frogTwo)
                npcAddQueue.add(frogThree)
                npcAddQueue.add(frogFour)
            }

            // Start the puff sound
            player.packetSender.sendAreaPlayerSound(1930, 5, 1, 0)
            frogPrince.performGraphic(Graphic(188, GraphicHeight.HIGH))
            TaskManager.submit(object : Task(2) {
                override fun execute() {
                    stop()
                    if (player.appearance.isMale) {
                        frogPrince.say("" + player.username + ", the frog princess desires your attention!")
                    } else {
                        frogPrince.say("" + player.username + ", the frog prince desires your attention!")
                    }
                }
            })

            // Npc is removed
            TaskManager.submit(object : Task(25) {
                override fun execute() {
                    stop()
                    if (frogPrince.isActive) {
                        World.spawn(TileGraphic(frogPrince.position, Graphic(188, GraphicHeight.MIDDLE)))
                        player.packetSender.sendAreaEntitySound(frogPrince, 1930, 5, 1, 0)
                        npcRemoveQueue.add(frogPrince)
                    }
                    npcRemoveQueue.add(frogOne)
                    npcRemoveQueue.add(frogTwo)
                    npcRemoveQueue.add(frogThree)
                    npcRemoveQueue.add(frogFour)
                }
            })
        }
    }
}
