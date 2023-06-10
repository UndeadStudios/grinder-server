package com.grinder.game.content.npc

import com.grinder.game.content.pvm.BossDropTables
import com.grinder.game.content.pvm.ItemDropFinderInterface
import com.grinder.game.content.pvm.MonsterKillTracker
import com.grinder.game.content.skill.skillable.impl.magic.Teleporting
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Position
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.onFirstNPCAction
import com.grinder.game.model.onSecondNPCAction
import com.grinder.game.model.onThirdNPCAction
import com.grinder.util.NpcID
import java.util.function.Consumer

object KingPercival {

    init {
        onFirstNPCAction(NpcID.KING_PERCIVAL) {
            startDialogue(player, npc)
        }
        onSecondNPCAction(NpcID.KING_PERCIVAL) {
            BossDropTables.openInterface(player)
        }
        onThirdNPCAction(NpcID.KING_PERCIVAL) {
            ItemDropFinderInterface.openInterface(player)
        }
    }

    private fun startDialogue(player: Player, npc: NPC) {
        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                .setText("Hello there, King!")
                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                .setText("Hi Captain! What you've been up to?")
                .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                .firstOption("What do I get for defeating my opponent?", Consumer { player1: Player? ->
                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("What do I get for defeating my opponent?")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("Defeating your opponent in the wilderness rewards", "you with @red@3,000 Blood money</col> as basic reward. However,", "if you or your opponent is on a kill streak, then", "the reward is multiplied based on the streak.")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("Keep in mind if you defeat the same player again in", "the Wilderness, you will not be eligible for a reward.", "However, the items of your opponent still drops.")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("This is to keep the economy safe and to prevent players", "from boosting.")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("Defeating a player with untradeable items that can break", "such as Void, Fire cape, and many others will drop", "Blood money instead of the item, while the player", "keeps the broken item.")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("Keep in mind, if you die with a broken item, it will be", "dropped on death and you will no longer keep it.")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("Remember as soon as you enter the Wilderness, you will", "be assigned a target. This is very beneficial", "so that you can teleport to your bounty target anywhere", "in the Wilderness.")
                            .start(player)
                }).secondOption("What is the Wilderness Spirit spawn?", Consumer { player1: Player? ->
                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("What is the Wilderness Spirit spawn?")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("In simple words, it is a minigame that starts once", "every 90 minutes.")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("The minigame summons the spirit of a boss.", "The spirit is blessed with evil spirits.", "This makes the boss hard to slay.")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("The boss spirit fades as its energy is drained.", "Upon slaying the spirit, you will receive", "a generous reward, and all participating players.")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("You can check the status of the spawn, or when", "it is going to start from your quest tab.")
                            .start(player)
                }).thirdOption("Can you teleport me to Fun PvP zone?", Consumer { player1: Player? ->
                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("Can you teleport me to Fun PvP zone?")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("Absolutely! It is a place where all of the greatest", "PKer's trained their skills.")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("So, are you sure you want me to teleport you there?")
                            .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                            .firstOption("I'm certain.", Consumer { player2: Player? ->
                                if (TeleportHandler.checkReqs(player, Position(3323, 4969, 0), true, true, player.spellbook.teleportType)) {
                                    //TeleportHandler.teleport(player, Teleporting.TeleportLocation.FUN_PVP_ZONE.getPosition(),
                                    //		player.getSpellbook().getTeleportType(), false, true);
                                    TeleportHandler.offerTeleportFromNPC(player, Teleporting.TeleportLocation.FUN_PVP_ZONE.position,
                                            player.spellbook.teleportType, false, true, npc.id, "Fun PvP zone")
                                }
                            }).secondOption("No, I've changed my mind.", Consumer { player2: Player? ->
                                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("No, I've changed my mind.")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("Come back anytime!").start(player)
                            }).start(player)
                }).fourthOption("What is the Revenant Caves for?", Consumer { player1: Player? ->
                    DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("What is the Revenant Caves for?")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("There are mysterious creatures that dwell in the caves.")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("It is also in the Wilderness which can be a risk", "from other PKers.")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("Revenant creatures do however drop some of the best", "items in game such as @dre@Sanguinesti staff</col>, @dre@Craw's bow</col>,", "dre@Viggora's chainmace</col>, and @dre@Thammaron's sceptre</col>.")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("The items can be usable after using an Ancient crystal", "on the (u) item(s).")
                            .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                            .setText("Some items will require charging them with", "revenant ether and other runes for", "a very strong spell. You can examine", "items for more.")
                            .start(player)
                }).fifthOption("Next...", Consumer { player1: Player? ->
                    DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
                            .firstOption("Can I see the list of my slayed monsters?", Consumer { player2: Player? -> MonsterKillTracker.displayNPCList(player) }).secondOption("What do the Wilderness bosses drop?", Consumer { player3: Player? ->
                                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("What do the Wilderness bosses drop?")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("You can find a list of all item drops from", "your quest tab.")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("The bosses in the Wilderness do drop a little", "Blood money guranteed on every kill.")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("If you want to search for a specific item drop, then", "you can use the Item drop finder in your quest tab.")
                                        .start(player)
                            }).thirdOption("Why are some bosses impossible to slay?", Consumer { player3: Player? ->
                                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("Why are some bosses impossible to slay?")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("Bosses are known for their tanky and massive stats.")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("You should look up the boss stats, and see the", "weakest stat that you should use against.")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("For example, @dre@Vet'ion</col> is weak to Crush attacks.", "This means you should use Crush weapons against him.")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("It is also advised to use @dre@Verac's armour</col> as there", "is a chance to ignore the target's defence.")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("Some bosses are weak to certain @blu@Magic Spells</col>, and", "some are weak to @blu@Ranged</col> attacks.")
                                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                        .setText("Tip: Using Craw's bow in the Wilderness grants 50%", "increased accuracy against NPC's.")
                                        .start(player)
                            }).fourthOption("I'm off.", Consumer { player3: Player? ->
                                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("I'm off.")
                                        .add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANGRY_2)
                                        .setText("Now leave me alone.").start(player)
                            }).start(player)
                }).start(player)
    }
}