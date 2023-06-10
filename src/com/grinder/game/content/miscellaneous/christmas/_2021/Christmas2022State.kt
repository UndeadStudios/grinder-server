package com.grinder.game.content.miscellaneous.christmas._2022

import com.grinder.game.World
import com.grinder.game.content.miscellaneous.christmas.SantaMinionNpc
import com.grinder.game.content.miscellaneous.christmas.SantaNpc
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.BARTENDER
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.BARTENDER_1310
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.BARTENDER_1311
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.BARTENDER_1313
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.BARTENDER_1314
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.BARTENDER_1318
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.BARTENDER_1319
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.BARTENDER_1320
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.BAR_TENDER_ID
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.EMILY
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.KAYLE
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.ZAMBO
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.barDialogue
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.getState
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.minionDialogue
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.npcDialogue
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.playerDialogue
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022.setState
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.NPCFactory
import com.grinder.game.entity.agent.npc.monster.impl.GuardMonster
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.agent.player.event.PlayerEvents
import com.grinder.game.entity.markTime
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.model.areas.instanced.BlueMoonInnArea
import com.grinder.game.model.areas.instanced.VarrockPalaceArea
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression.*
import com.grinder.game.model.interfaces.dialogue.DialogueType.*
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.interfaces.dialogue.secondOption
import com.grinder.game.model.interfaces.dialogue.setAction
import com.grinder.game.model.item.Item
import com.grinder.game.task.TaskManager
import com.grinder.util.Executable
import com.grinder.util.ItemID
import com.grinder.util.NpcID
import com.grinder.util.ObjectID
import com.grinder.util.oldgrinder.Area
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.random.Random

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   20/12/2019
 * @version 1.0
 */
enum class Christmas2022State {
    TALK_TO_MINIONS {
        override fun handleNpcOption(player: Player, npc: NPC): Boolean {
            if(npc is SantaMinionNpc){
                val optionDialogue = DialogueBuilder(OPTION)
                optionDialogue.firstOption("He's acting strange?") {
                    losingControlOption(npc, player, it)
                }.addCancel()
                minionDialogue(DISTRESSED,
                        "Hello there ${player.username},",
                        "We've found ourselves to be in quite the conundrum!")
                        .add(playerDialogue(DEFAULT,
                                "I am sorry to hear,",
                                "What kind of conundrum are we speaking of?"))
                        .add(minionDialogue(DISTRESSED_2,
                                "It's about Santa Claus...",
                                "He's acting strange!",
                                "and we're unsure what to do about it."))
                        .add(minionDialogue(DISTRESSED_2,
                                "He has yet to start delivering gifts,",
                                "Christmas is doomed!"))
                        .add(optionDialogue)
                        .start(player)
                return true
            }
            return super.handleNpcOption(player, npc)
        }

        private fun losingControlOption(npc: SantaMinionNpc, player: Player, it: Player) {
            npcDialogue(npc.id, CRYING_ALMOST)
                    .setText(
                            "Yes ${player.username},",
                            "ever since he started drinking this new ale!")
                    .add(playerDialogue(CURIOUS, "A new ale you say hmmm.."))
                    .add(minionDialogue(DISTRESSED,
                            "He kept on talking about this tavern,",
                            "the 'Blue Moon Inn'.",
                            "Are you familiar with it?"))
                    .add(OPTION)
                    .firstOption("Yes.") {
                        talkToBartender(it)
                    }
                    .secondOption("No.") {
                        playerDialogue(SAD, "No.").start(it)
                    }
                    .start(it)
        }

        private fun talkToBartender(it: Player) {
            playerDialogue(THINKING, "Yes. I have heard of it.")
                    .add(minionDialogue(CRYING_ALMOST, "Dear traveler, can you help us?"))
                    .add(OPTION)
                    .firstOption("I'd like to help!") {
                        it.setState(TALK_TO_BAR_TENDER)
                        playerDialogue(CALM, "Alright I will do my best in finding this Blue Moon Inn.")
                                .add(minionDialogue(HAPPY, "Thank you brave adventurer.", "All of Gielinor depends on you for saving Christmas!"))
                                .start(it)
                    }
                    .addCancel("Never mind.")
                    .start(it)
        }
    },
    TALK_TO_BAR_TENDER{
        override fun handleNpcOption(player: Player, npc: NPC): Boolean {
            if(npc.id == BAR_TENDER_ID){
                barDialogue(SAD_HEAD_BOW,"Would you like anything to drink?")
                        .add(OPTION)
                        .firstOption("Yes please!") {
                            buyBeer(it)
                        }.secondOption("No thanks.") {
                            inquireBarTender(player, npc, it)
                        }.start(player)
                return true
            }
            if(npc is SantaMinionNpc){
                minionDialogue(DISTRESSED_2, "Have you found out more yet?")
                        .add(playerDialogue(DEFAULT, "No."))
                        .start(player)
                return true
            }
            return super.handleNpcOption(player, npc)
        }

        private fun inquireBarTender(player: Player, npc: NPC, it: Player) {
            playerDialogue(DEFAULT, "No thank you...")
                    .add(barDialogue(ANNOYED,
                            "What you in my tavern for then?"))
                    .add(playerDialogue(DEFAULT,
                            "I heard some distressing reports",
                            "regarding a new ale being sold in this tavern."))
                    .add(barDialogue(DISTRESSED,
                            "New ale?",
                            "We only sell plain beer here."))
                    .add(OPTION)
                    .firstOption("You're lying!") {
                        playerDialogue(DEFAULT, "You're lying to me...")
                                .add(OPTION)
                                .firstOption("Become violent!") {
                                    it.removeInterfaces()
                                    it.packetSender.sendFadeScreen("", 2, 2)
                                    TaskManager.submit(2) {
                                        val height = player.index shl 2
                                        val barTender = NPCFactory.create(npc.id, npc.position.clone().setZ(height))
                                        val area = BlueMoonInnArea(barTender)
                                        it.moveTo(player.position.clone().setZ(height))
                                        area.add(it)
                                        area.add(barTender)
                                        it.area = area
                                        World.npcAddQueue.add(barTender)
                                        it.setEntityInteraction(barTender)
                                        it.block()
                                        TaskManager.submit(1) {

                                            val fightType = WeaponFightType.UNARMED_PUNCH
                                            it.performAnimation(Animation(fightType.animation))
                                            TaskManager.submit(1) {
                                                barTender.performAnimation(Animation(2241))
                                                barTender.say("Ouch!")
                                            }
                                            TaskManager.submit(3) {
                                                playerDialogue(ANGRY, "Are you ready to talk now?")
                                                        .add(barDialogue(CRYING_ALMOST,
                                                                "She is forcing me to do this!",
                                                                "She has my family..."))
                                                        .add(OPTION)
                                                        .firstOption("Who has?") {
                                                            barDialogue(CRYING_ALMOST_2, "The lady..")
                                                                    .add(playerDialogue(DEFAULT, "What lady?"))
                                                                    .add(barDialogue(CRYING_ALMOST_2, "I don't know who she is!", "She gave me the recipe and supply chain,", "she has my children..."))
                                                                    .add(playerDialogue(DEFAULT, "Where can I find this lady?"))
                                                                    .add(barDialogue(DISTRESSED, "I do not know!", "The supplies come from a nearby castle."))
                                                                    .setAction(Consumer {
                                                                        it.unblock()
                                                                        it.notify(PlayerEvents.INTERACTED)
                                                                    })
                                                                    .start(it)
                                                        }.secondOption("What did you do?") {
                                                            playerDialogue(ANGRY, "What did you do?")
                                                                    .add(barDialogue(CRYING_ALMOST_2, "I added a special ingredient", "to santa's ale."))
                                                                    .add(barDialogue(CRYING_ALMOST, "It is some sort of addictive chemical."))
                                                                    .add(playerDialogue(ANGRY, "Why did you do this?"))
                                                                    .add(barDialogue(CRYING_ALMOST_2, "This lady forced me!"))
                                                                    .add(playerDialogue(ANGRY, "What lady?"))
                                                                    .add(barDialogue(CRYING_ALMOST_2, "I don't know who she is!", "She gave me the recipe and supply chain,", "she has my children..."))
                                                                    .add(playerDialogue(DEFAULT, "Where can I find this lady?"))
                                                                    .add(barDialogue(DISTRESSED, "I do not know!", "The supplies come from a nearby castle."))
                                                                    .setAction(Consumer {
                                                                        it.unblock()
                                                                        it.notify(PlayerEvents.INTERACTED)
                                                                    })
                                                                    .start(it)
                                                        }
                                                        .start(it)
                                                it.setState(TALK_TO_SANTA_EX_WIFE)
                                                it.subscribe { event ->
                                                    if (event == PlayerEvents.WALK || event == PlayerEvents.INTERACTED) {
                                                        it.packetSender.sendFadeScreen("", 2, 3)
                                                        TaskManager.submit(2) {
                                                            it.unblock()
                                                            it.moveTo(it.position.clone().setZ(0))
                                                        }
                                                        return@subscribe true
                                                    }
                                                    return@subscribe false
                                                }
                                            }
                                        }
                                    }
                                }
                                .secondOption("Inquire further.") {
                                    playerDialogue(CURIOUS, "Then how do you explain santa's erratic behaviour?")
                                            .add(barDialogue(ANGRY, "I do not know..", "Maybe his recent breakup", "has something to with it."))
                                            .add(playerDialogue(CURIOUS, "Whom did he breakup with?"))
                                            .add(barDialogue(DISTRESSED_2, "I am not sure."))
                                            .add(playerDialogue(ANGRY, "Stop lying to me!", "It is obvious that the mere thought of her", "frightens you."))
                                            .add(barDialogue(DISTRESSED_2, "She is forcing me to do this.."))
                                            .add(playerDialogue(CURIOUS, "Forcing you to do what?"))
                                            .add(barDialogue(DISTRESSED_2, "To add a special ingredient to santa's ale."))
                                            .add(playerDialogue(CURIOUS, "Is this the cause for its addictive property?"))
                                            .add(barDialogue(DISTRESSED_2, "I am afraid so..."))
                                            .add(playerDialogue(CURIOUS, "Why are you doing this!"))
                                            .add(barDialogue(CRYING_ALMOST, "She has me children!"))
                                            .add(playerDialogue(CURIOUS, "I see.", "Alright,", "can you tell me where I can find her?"))
                                            .add(barDialogue(CRYING_ALMOST, "The ingredient she wants me to add", "are shipped from a nearby castle."))
                                            .add(barDialogue(CRYING_ALMOST, "I told you everything I know!"))
                                            .setAction(Consumer { it.setState(TALK_TO_SANTA_EX_WIFE) })
                                            .start(it)
                                }
                                .start(it)
                    }
                    .addCancel("I'll go now.")
                    .start(it)
        }

        public fun buyBeer(it: Player) {
            barDialogue(HAPPY, "Alright that'd be 10 gold coins then.")
                    .add(OPTION)
                    .firstOption("Buy beer for 10 coins.") {
                        val cost = Item(995, 10)
                        if (it.removeInventoryItem(cost)) {
                            it.addInventoryItem(Item(ItemID.BEER))
                            barDialogue(HAPPY, "Enjoy!").start(it)
                        } else
                            barDialogue(ANNOYED, "First get some gold!").start(it)
                    }
                    .secondOption("Too expensive for me.") {
                        barDialogue(ANNOYED, "Suit yourself!").start(it)
                    }
                    .start(it)
        }
    },
    TALK_TO_SANTA_EX_WIFE{
        override fun handleNpcOption(player: Player, npc: NPC): Boolean {
            if(npc is SantaMinionNpc){
                minionDialogue(DISTRESSED_2, "Have you found out more yet?")
                        .add(playerDialogue(DEFAULT, "Yes,", "apparently santa had a rough breakup", "and now his ex-girlfriend", "tries to turn him into a drunk."))
                        .add(minionDialogue(DISTRESSED,"Oh my!", "So Ellamaria is behind all this!", "She lives in the Varrock Palace."))
                        .add(playerDialogue(DEFAULT, "I will be on my way now then!"))
                        .add(minionDialogue(HAPPY, "Thank you ${player.username}!"))
                        .start(player)
                return true
            }
            if(npc.id == NpcID.AEONISIG_RAISPHER_8043){
                npcDialogue(npc.id)
                        .setText("What is it?")
                        .add(playerDialogue(DEFAULT, "I got word a malicious shipment", "is being send from a nearby castle."))
                        .add(npcDialogue(npc.id, ANNOYED).setText("We are not shipping any goods at the moment."))
                        .start(player)
                return true
            }

            if(npc.id == NpcID.QUEEN_ELLAMARIA){
                npcDialogue(npc.id)
                        .setText("Hiya!")
                        .add(PLAYER_STATEMENT)
                        .setText("I got word you are holding children hostage.", "I am here to free them", "and stop your acts of malevolence!")
                        .add(npcDialogue(npc.id, ANGRY_3))
                        .setText("How dare thee suggest such a thing!", "I am a queen, those words are punishable by death!")
                        .add(createFightOrFleeOptionDialogue(npc))
                        .start(player)
                return true
            }
            return super.handleNpcOption(player, npc)
        }

        override fun handleObjectOption(player: Player, gameObjectId: Int): Boolean {
            return SEARCH_SANTA_EX_WIFE_DRAWERS.handleObjectOption(player, gameObjectId)
        }
    },
    FIGHT_SANTA_EX_WIFE_GUARDS {
        override fun handleNpcOption(player: Player, npc: NPC): Boolean {

            if(npc.id == NpcID.QUEEN_ELLAMARIA)
                return true

            return TALK_TO_SANTA_EX_WIFE.handleNpcOption(player, npc)
        }
        override fun handleObjectOption(player: Player, gameObjectId: Int) = TALK_TO_SANTA_EX_WIFE.handleObjectOption(player, gameObjectId)
        override fun handleItemOption(player: Player, item: Item, item2: Item) = TALK_TO_SANTA_EX_WIFE.handleItemOption(player, item, item2)
    },
    SEARCH_SANTA_EX_WIFE_DRAWERS {
        override fun handleNpcOption(player: Player, npc: NPC): Boolean {
            if(npc is SantaMinionNpc){
                minionDialogue(DISTRESSED_2, "How did it go?")
                        .add(playerDialogue(DISTRESSED, "She tried to kill me!"))
                        .add(minionDialogue(DISTRESSED,"Oh no!"))
                        .add(playerDialogue(DEFAULT, "I am going to need evidence on her."))
                        .add(minionDialogue(DISTRESSED,"Have you tried searching her room?"))
                        .add(playerDialogue(DEFAULT, "No not yet, I'll try that!"))
                        .start(player)
                return true
            }
            if(npc.id == NpcID.QUEEN_ELLAMARIA){
                npcDialogue(npc.id)
                        .setText("You again!")
                        .add(createFightOrFleeOptionDialogue(npc))
                        .start(player)
                return true
            }
            return super.handleNpcOption(player, npc)
        }

        override fun handleObjectOption(player: Player, gameObjectId: Int): Boolean {

            if(gameObjectId == ObjectID.DRAWERS_14) {
                if (player.getState() == SEARCH_SANTA_EX_WIFE_DRAWERS) {
                    player.setState(FOUND_LETTER)
                    player.block(blockMovement = true)
                    player.statement("You search Ellamaria's drawers...")
                    player.performAnimation(Animation(881, 15))
                    TaskManager.submit(2) {
                        player.unblock(unblockMovement = true)
                        player.itemStatement(ItemID.LETTER, 200,"You find a letter!")
                        player.inventory.add(Item(ItemID.LETTER, 1), true, true)
                    }
                    return true
                }
            }
            return super.handleObjectOption(player, gameObjectId)
        }
    },
    FOUND_LETTER {
        override fun handleNpcOption(player: Player, npc: NPC): Boolean {
            if(npc is SantaMinionNpc){
                minionDialogue(DISTRESSED, "Have you found any evidence yet?")
                        .add(playerDialogue(DEFAULT,"Yes I found a suspicious looking letter", "in her drawers."))
                        .add(minionDialogue(DEFAULT, "Have you read it yet?"))
                        .add(playerDialogue(DEFAULT, "No... Maybe I should."))
                        .start(player)
                return true
            }

            return super.handleNpcOption(player, npc)
        }
    },
    READ_LETTER {
        override fun handleNpcOption(player: Player, npc: NPC): Boolean {

            if(npc is SantaMinionNpc){
                minionDialogue(DISTRESSED, "Have you found any evidence yet?")
                        .add(playerDialogue(DEFAULT, "Yes I found a suspicious looking letter", "in her drawers.", "It originates from someone named 'Nora'."))
                        .add(minionDialogue(ANGRY, "Nora!", "The old hag!"))
                        .add(playerDialogue(CURIOUS, "You know her?"))
                        .add(minionDialogue(DISTRESSED, "We have a history with Nora...", "She lives in Taverly."))
                        .add(playerDialogue(DEFAULT, "I guess I'll be on my way then."))
                        .setAction(Consumer { it.setState(SEE_WITCH) })
                        .start(player)
                return true
            }
            if(npc.id == NpcID.QUEEN_ELLAMARIA){
                DialogueBuilder(PLAYER_STATEMENT)
                        .setText("I found a suspicious letter in your drawers,", "who is this nora person?")
                        .add(npcDialogue(npc.id)
                                .setText("That I will not confess to thee."))
                        .start(player)
                return true
            }
            return false
        }
    },
    SEE_WITCH{
        override fun handleNpcOption(player: Player, npc: NPC): Boolean {

            if(npc is SantaMinionNpc){
                playerDialogue(CURIOUS, "Can you tell me something about Nora?")
                        .add(minionDialogue(DISTRESSED, "We have a history with Nora...", "She lives in Taverly."))
                        .add(playerDialogue(DEFAULT, "I guess I'll be on my way then."))
                        .start(player)
                return true
            }

            return super.handleNpcOption(player, npc)
        }
    },
    MAKE_CURE{
        override fun handleNpcOption(player: Player, npc: NPC): Boolean {

            if(npc is SantaMinionNpc) {
                playerDialogue(DEFAULT, "I found a book containing a recipe", "for an antidote!")
                        .add(minionDialogue(HAPPY, "That is some amazing news!"))
                        .add(playerDialogue(DEFAULT, "I just have to find the right ingredients now."))
                        .add(minionDialogue(HAPPY, "I've been hearing that you can", "find some of the recipe items nearby", "on the beach sand crabs."))
                        .start(player)
                return true
            }

            return super.handleNpcOption(player, npc)
        }
    },
    HAND_OVER_CURE{
        override fun handleNpcOption(player: Player, npc: NPC): Boolean {

            if(npc is SantaMinionNpc) {
                playerDialogue(HAPPY, "I succeeded in making the antidote!")
                        .add(minionDialogue(HAPPY, "Awesome!", "I'll administer it to Santa ASAP!"))
                        .add(OPTION)
                        .firstOption("Hand-over antidote.") {
                            if(player.removeInventoryItem(Item(ItemID.MIXTURE))){
                                player.setState(CURED_SANTA)
                                DialogueBuilder(ITEM_STATEMENT)
                                        .setItem(ItemID.MIXTURE, 200)
                                        .setText("You hand over the antidote!")
                                        .add(minionDialogue(HAPPY, "Thank you ${it.username}.", "Feel free to take a present from the tree!"))
                                        .start(it)
                            } else
                                it.statement("You don't have the antidote with you!")
                        }
                        .addCancel()
                        .start(player)
                return true
            }
            return super.handleNpcOption(player, npc)
        }
    },
    CURED_SANTA,
    RECEIVED_GIFT,
    COMPLETED;

    open fun handleNpcOption(player: Player, npc: NPC): Boolean {

        val state = player.getState().ordinal

       if(npc is SantaNpc){
           player.statement( "Santa is not interested in speaking with you :(")
            return true
        }

        if(npc is SantaMinionNpc) {
            if (state >= CURED_SANTA.ordinal) {
                minionDialogue(HAPPY, "I wish you a splendorous day!").start(player)
                return true
            }
        }

        if(npc.id == NpcID.QUEEN_ELLAMARIA){
            if(state < TALK_TO_SANTA_EX_WIFE.ordinal) {
                npcDialogue(npc.id).setText("Hiya!").start(player)
            } else
                npcDialogue(npc.id, ANGRY).setText("I am in no mood to talk to you right now!").start(player)
            return true
        }

        if(npc.id == EMILY || npc.id == KAYLE || npc.id == ZAMBO || npc.id == BARTENDER || npc.id == BARTENDER_1310 ||
                npc.id == BARTENDER_1311 || npc.id == BAR_TENDER_ID
                || npc.id == BARTENDER_1313 || npc.id == BARTENDER_1314
                || npc.id == BARTENDER_1318 || npc.id == BARTENDER_1319
                || npc.id == BARTENDER_1320
        ){
            if(state == 0 || state >= HAND_OVER_CURE.ordinal || state == FOUND_LETTER.ordinal || state == READ_LETTER.ordinal){
                npcDialogue(npc.id, SAD_HEAD_BOW)
                        .setText("Would you like anything to drink?")
                        .add(OPTION)
                        .firstOption("Yes please!") {
                            npcDialogue(npc.id)
                                    .setText("Alright that'd be 10 gold coins then.")
                                    .add(OPTION)
                                    .firstOption("Buy beer for 10 coins.") {
                                        val cost = Item(995, 10)
                                        if(it.inventory.contains(cost)){
                                            it.inventory.delete(cost)
                                            it.inventory.add(Item(ItemID.BEER), true, true)
                                            npcDialogue(npc.id)
                                                    .setText("Enjoy!")
                                                    .start(it)
                                        } else npcDialogue(npc.id, ANNOYED)
                                                .setText("First get some gold!")
                                                .start(it)
                                    }
                                    .secondOption("Too expensive for me.") {
                                        npcDialogue(npc.id, ANNOYED)
                                                .setText("Suit yourself!")
                                                .start(it)
                                    }.start(it)
                        }
                        .secondOption("No thanks.") {
                            DialogueBuilder(PLAYER_STATEMENT)
                                    .setText("No thank you...")
                                    .add(npcDialogue(npc.id)
                                            .setExpression(ANNOYED)
                                            .setText("Stop bothering me then!"))
                                    .start(it)
                        }.start(player)
                return true
            } else if(state > TALK_TO_BAR_TENDER.ordinal) {
                npcDialogue(npc.id, SAD_HEAD_BOW)
                        .setText("I told you everything I know!", "Try finding a nearby castle.")
                        .start(player)
            }
            return true
        }

        if(npc.id == NpcID.AEONISIG_RAISPHER_8043) {
            npcDialogue(npc.id)
                    .setText("What is it?")
                    .start(player)
            return true
        }
        return false
    }

    open fun handleObjectOption(player: Player, gameObjectId: Int): Boolean {
        if(gameObjectId == ObjectID.DRAWERS_14) {
            if (player.passedTime(Attribute.GENERIC_ACTION, 1, message = false)) {
                player.setPositionToFace(Position(3223, 3478, 1), true)
                TaskManager.submit(1) {
                    player.markTime(Attribute.LAST_PRAY)
                    player.motion.update(MovementStatus.DISABLED)
                    player.performAnimation(Animation(6840))
                    player.sendMessage("You attempt to search the drawers...")
                    TaskManager.submit(3) {
                        player.motion.update(MovementStatus.NONE)
                        player.sendMessage("You don't find anything valuable.")
                    }
                }
            }
            return true
        }
        return false
    }

    open fun handleItemOption(player: Player, item: Item, item2: Item) = false

    fun createFightOrFleeOptionDialogue(npc: NPC): DialogueBuilder {
        return DialogueBuilder(OPTION)
                .firstOption("Flee!") {
                    it.setState(SEARCH_SANTA_EX_WIFE_DRAWERS)
                    it.removeInterfaces()
                    it.packetSender.sendFadeScreen("You flee the scene", 2, 4)
                    TaskManager.submit(1) {
                        npc.say("Guards!!")
                        it.packetSender.sendJinglebitMusic(158, 0)
                    }
                    TaskManager.submit(3) {
                        it.moveTo(Position(3235, 3465, 0))
                        DialogueBuilder(STATEMENT)
                                .setText("You fled the scene.")
                                .start(it)
                    }
                }.secondOption("Fight!") {
                    it.setState(FIGHT_SANTA_EX_WIFE_GUARDS)
                    it.removeInterfaces()
                    it.packetSender.sendFadeScreen("The queen shouts for her guards", 2, 8)
                    TaskManager.submit(1) {
                        npc.say("Guards!!")
                        it.packetSender.sendJinglebitMusic(156, 0)
                    }
                    TaskManager.submit(7) {
                        val height = it.index shl 2
                        val queen = NPCFactory.create(npc.id, npc.position.clone().setZ(height))
                        val guards = Area.of(3228, 3488, 3232, 3489)
                                .findPositions(height)
                                .map { position -> GuardMonster(it, if (Random.nextBoolean()) NpcID.GUARD_3010 else NpcID.GUARD_3011, position.clone().setZ(height)) }
                                .collect(Collectors.toList())
                        val area = VarrockPalaceArea(it, guards, completeAction = Executable {
                            npc.setPositionToFace(it.position, true)
                            it.block()
                            npcDialogue(npc.id, DISTRESSED)
                                    .setText("You killed all me guards!")
                                    .add(playerDialogue(DEFAULT,
                                            "Yes and you're next", "unless you tell me", "how I can fix Santa's addiction."))
                                    .add(npcDialogue(npc.id, DISTRESSED)
                                            .setText("I plea for thee not to kill me."))
                                    .add(npcDialogue(npc.id, DISTRESSED_2)
                                            .setText("Nora did this...", "Only she can help you."))
                                    .setAction {
                                        it.unblock()
                                    }
                                    .start(it)
                            it.setState(SEE_WITCH)
                        })

                        it.moveTo(it.position.clone().setZ(height))

                        World.npcAddQueue.add(queen)

                        area.add(it)
                        area.add(queen)
                        it.area = area
                        queen.say("Get ${if (it.appearance.isMale) "him" else "her"}!")
                    }
                }
    }
}