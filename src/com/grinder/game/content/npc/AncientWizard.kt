package com.grinder.game.content.npc

import com.grinder.game.content.skill.skillable.impl.magic.Teleporting
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.item.container.shop.ShopManager
import com.grinder.game.model.onFirstNPCAction
import com.grinder.game.model.onSecondNPCAction
import com.grinder.game.model.onThirdNPCAction
import com.grinder.util.Misc
import com.grinder.util.NpcID
import com.grinder.util.ShopIdentifiers
import java.util.function.Consumer

object AncientWizard {

    init {
        onFirstNPCAction(NpcID.ANCIENT_WIZARD) {
            startDialogue(player, npc)
        }
        onSecondNPCAction(NpcID.ANCIENT_WIZARD) {
            Teleporting.handleButton(player, 55555)
        }
        onThirdNPCAction(NpcID.ANCIENT_WIZARD) {
            ShopManager.open(player, ShopIdentifiers.ANCIENT_WIZARD_STORE)
        }
    }

    private fun startDialogue(player: Player, npc: NPC) {
        when {
            Misc.random(1) == 1 -> {
                DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("What you're up to for today, " + player.username + "?")
                        .add(DialogueType.PLAYER_STATEMENT)
                        .setText("I need some help with teleports.").add(DialogueType.OPTION).setOptionTitle("Select an Option")
                        .firstOption("What are other ways of teleporting?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("I'm looking to teleport into different locations", "that you do not offer perhaps.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Indeed, my magical knowledge is not yet enough", "to master teleports to every location. However, I can", "explain what you will need to reach those unique", "locations.")
                                    .add(DialogueType.NPC_STATEMENT).setText("Which locations are you looking for?")
                                    .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                                    .firstOption("Skilling Guilds.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("How can I go to a skilling guild?").setExpression(DialogueExpression.DISTRESSED)
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Unfortunately, I cannot teleport you there.").add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Good news is you can use a charged @dre@Skilling necklace</col>", "to teleport to any skilling guild of you choice.", "All of our skilling guilds makes skilling easier.", "That also includes the Farming guild!")
                                                .start(player)
                                    }).secondOption("Bossing Teleports.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("Can I reach bosses directly?")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Some bosses require a certain kill count to reach, while", "others require a specific item.", "It all differs from each type of boss.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("You can use @dre@Max Cape</col> to teleport to several bosses directly.", "A charged @dre@Skull sceptre</col> can also be used for different bosses.", "For more information you can open the Boss Drop Table",
                                                        "from your quest tab which displays more information.")
                                                .start(player)
                                    }).thirdOption("Slayer Teleports.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("I'm looking for Slayer teleports.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("If you're looking to teleport to your Slayer task then", "you can use a @dre@Slayer's ring</col> to teleport there, or", "rubbing an @dre@Enchanted gem</col> will teleport you directly", "to your Slayer's task.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("In addittion, Slayer masters are easy to find. They can be", "reached with by the regular teleports. However, @dre@Duradel</col>", "is the only master that you", "can reach by using @dre@Slayer's enchantment</col> item.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Keep in mind that you will need a Slayer level", "of at least 50 and a combat level of 100 to be able", "to get a task from the master.")
                                                .start(player)
                                    }).fourthOption("Chest's Teleports.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("Where are all the chests located?")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Let's start off by the @blu@Crystal chest</col> which can be found in", "Edgeville's bank. You will need a @dre@Crystal key</col> to open it.", "You can find all the rewards on our @dre@Wiki</col> page!")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("The @blu@Muddy chest</col> can be found in the", "Wilderness maze east of KBD's lair.", "It's a dangerous area, be careful from Pker's.", "You can find all the rewards on our @dre@Wiki</col> page!")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Finally, @blu@Brimstone chest</col> can be found in the", "Mount Karuulm where Hydra's are located.", "You can find all the rewards on our @dre@Wiki</col> page!")
                                                .start(player)
                                    }).addCancel("Nevermind.").start(player)
                        }).secondOption("How much do you charge?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("Do you charge anything for teleports?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Teleports are completely free of cost...at least for now.").start(player)
                        }).thirdOption("What's your armour called?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("What's your armour called?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("My magical armour is the strongest ancient magical armour", "you will ever find, and it is called Ancient Qunitope robes.")
                                    .add(DialogueType.PLAYER_STATEMENT).setText("Where can I get one?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Hahaha, this armour cannot be found anywhere.", "Good luck finding one!").setExpression(DialogueExpression.DISTRESSED)
                                    .start(player)
                        }).fourthOption("Where is the Fountain of Rune?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("Where is the Fountain of Rune?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("The Fountain of Rune is a magical rock found within the", "ruined fortress south of the Wilderness Volcano. Players who", "have completed the hard tasks in the Wilderness Diary", "will be able to teleport there using the Wilderness sword 3/4!")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Players can charge their dragonstone jewellery", "@dre@Amulet of glory, combat bracelet, skills necklace, and", "@dre@ring of wealth</col> on the Fountain of Rune.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Fountain of Rune is the only location where the ring of wealth", "can be recharged. Each ring receives five teleport charges.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("In addition, while charging amulets of glory,", "there is a 1 in 25,000 chance that an amulet of glory", "will transmute into an amulet of eternal glory, which provides", "unlimited teleports rather than the usual six.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("When this occurs, The power of the fountain is transferred", "into an amulet of eternal glory. It will now have unlimited", "charges. will appear in the chatbox, replacing the...")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Fountain of Rune's standard message for charging jewellery.")
                                    .start(player)
                        }).fifthOption("Next...", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
                                    .firstOption("Teleport to Bronze Members island.", Consumer { player2: Player? ->
                                        if (PlayerUtil.isMember(player)) {
                                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                    .setText("Can you teleport me to the @redThe Deserted Reef</col>?")
                                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                    .setText("Aboslutely! Do you want me to teleport you now?").setExpression(DialogueExpression.HAPPY)
                                                    .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                                                    .firstOption("Yes please.", Consumer { player4: Player? ->
                                                        TeleportHandler.offerTeleportFromNPC(player, Teleporting.TeleportLocation.THE_DESERTED_REEF.position,
                                                                player.spellbook.teleportType, false, true, npc.id, "The Deserted Reef")
                                                    }).addCancel("Maybe later.").start(player)
                                        } else {
                                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                    .setText("Can you teleport me to the @red@The Deserted Reef</col>?")
                                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                    .setText("I'm sorry only Bronze members or higher are allowed to access that area.").setExpression(DialogueExpression.ANGRY_4)
                                                    .start(player)
                                        }
                                    }).secondOption("Skilling Masters.", Consumer { player3: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("Can you show me where skilling masters are located?")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Skilling masters can be found where you train the skill", "Each master also gives skilling tasks which when", "completed can reward you with skilling points", "that can be used to buy untradeable armour")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Skilling equipment and skillcapes of the skill", "gives additional perks and boosts the experience gained!")
                                                .start(player)
                                    }).thirdOption("I'm looking to trade.", Consumer { player3: Player? -> ShopManager.open(player, ShopIdentifiers.ANCIENT_WIZARD_STORE) }).addCancel("Nevermind.").start(player)
                        }).start(player)
            }
            Misc.random(3) == 2 -> {
                DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("MA LIUS KAZIIUT VECTOS!!! AD K L M N AAAA!").setExpression(DialogueExpression.EVIL_3)
                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("Whoops!").setExpression(DialogueExpression.EVIL_DELIGHTED)
                        .add(DialogueType.PLAYER_STATEMENT)
                        .setText("What were you doing?").setExpression(DialogueExpression.ANNOYED)
                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("Haha nothing to worry about! What did you want?").setExpression(DialogueExpression.HAPPY)
                        .add(DialogueType.PLAYER_STATEMENT)
                        .setText("I needed some help with teleports.").setExpression(DialogueExpression.CALM)
                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("Sure! What do you want to know?").setExpression(DialogueExpression.CALM)
                        .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                        .firstOption("What are other ways of teleporting?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("I'm looking to teleport into different locations", "that you do not offer perhaps.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Indeed, my magical knowledge is not yet enough", "to master teleports to every location. However, I can", "explain what you will need to reach those unique", "locations.")
                                    .add(DialogueType.NPC_STATEMENT).setText("Which locations are you looking for?")
                                    .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                                    .firstOption("Skilling Guilds.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("How can I go to a skilling guild?").setExpression(DialogueExpression.DISTRESSED)
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Unfortunately, I cannot teleport you there.").add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Good news is you can use a charged @dre@Skilling necklace</col>", "to teleport to any skilling guild of you choice.", "All of our skilling guilds makes skilling easier.", "That also includes the Farming guild!")
                                                .start(player)
                                    }).secondOption("Bossing Teleports.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("Can I reach bosses directly?")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Some bosses require a certain kill count to reach, while", "others require a specific item.", "It all differs from each type of boss.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("You can use @dre@Max Cape</col> to teleport to several bosses directly.", "A charged @dre@Skull sceptre</col> can also be used for different bosses.", "For more information you can open the Boss Drop Table",
                                                        "from your quest tab which displays more information.")
                                                .start(player)
                                    }).thirdOption("Slayer Teleports.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("I'm looking for Slayer teleports.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("If you're looking to teleport to your Slayer task then", "you can use a @dre@Slayer's ring</col> to teleport there, or", "rubbing an @dre@Enchanted gem</col> will teleport you directly", "to your Slayer's task.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("In addittion, Slayer masters are easy to find. They can be", "reached with by the regular teleports. However, @dre@Duradel</col>", "is the only master that you", "can reach by using @dre@Slayer's enchantment</col> item.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Keep in mind that you will need a Slayer level", "of at least 50 and a combat level of 100 to be able", "to get a task from the master.")
                                                .start(player)
                                    }).fourthOption("Chest's Teleports.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("Where are all the chests located?")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Let's start off by the @blu@Crystal chest</col> which can be found in", "Edgeville's bank. You will need a @dre@Crystal key</col> to open it.", "You can find all the rewards on our @dre@Wiki</col> page!")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("The @blu@Muddy chest</col> can be found in the", "Wilderness maze east of KBD's lair.", "It's a dangerous area, be careful from Pker's.", "You can find all the rewards on our @dre@Wiki</col> page!")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Finally, @blu@Brimstone chest</col> can be found in the", "Mount Karuulm where Hydra's are located.", "You can find all the rewards on our @dre@Wiki</col> page!")
                                                .start(player)
                                    }).addCancel("Nevermind.").start(player)
                        }).secondOption("How much do you charge?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("Do you charge anything for teleports?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Teleports are completely free of cost...at least for now.").start(player)
                        }).thirdOption("What's your armour called?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("What's your armour called?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("My magical armour is the strongest ancient magical armour", "you will ever find, and it is called Ancient Qunitope robes.")
                                    .add(DialogueType.PLAYER_STATEMENT).setText("Where can I get one?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Hahaha, this armour cannot be found anywhere.", "Good luck finding one!").setExpression(DialogueExpression.DISTRESSED)
                                    .start(player)
                        }).fourthOption("Where is the Fountain of Rune?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("Where is the Fountain of Rune?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("The Fountain of Rune is a magical rock found within the", "ruined fortress south of the Wilderness Volcano. Players who", "have completed the hard tasks in the Wilderness Diary", "will be able to teleport there using the Wilderness sword 3/4!")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Players can charge their dragonstone jewellery", "@dre@Amulet of glory, combat bracelet, skills necklace, and", "@dre@ring of wealth</col> on the Fountain of Rune.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Fountain of Rune is the only location where the ring of wealth", "can be recharged. Each ring receives five teleport charges.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("In addition, while charging amulets of glory,", "there is a 1 in 25,000 chance that an amulet of glory", "will transmute into an amulet of eternal glory, which provides", "unlimited teleports rather than the usual six.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("When this occurs, The power of the fountain is transferred", "into an amulet of eternal glory. It will now have unlimited", "charges. will appear in the chatbox, replacing the...")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Fountain of Rune's standard message for charging jewellery.")
                                    .start(player)
                        }).fifthOption("Next...", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
                                    .firstOption("Teleport to Bronze Island island.", Consumer { player2: Player? ->
                                        if (PlayerUtil.isMember(player)) {
                                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                    .setText("Can you teleport me to the @red@The Deserted Reef</col>?")
                                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                    .setText("Aboslutely! Do you want me to teleport you now?").setExpression(DialogueExpression.HAPPY)
                                                    .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                                                    .firstOption("Yes please.", Consumer { player4: Player? ->
                                                        TeleportHandler.offerTeleportFromNPC(player, Teleporting.TeleportLocation.THE_DESERTED_REEF.position,
                                                                player.spellbook.teleportType, false, true, npc.id, "Bronze Island island")
                                                    }).addCancel("Maybe later.").start(player)
                                        } else {
                                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                    .setText("Can you teleport me to the @red@The Deserted Reef</col>?")
                                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                    .setText("I'm sorry only Bronze members or higher are allowed to access this area.").setExpression(DialogueExpression.ANGRY_4)
                                                    .start(player)
                                        }
                                    }).secondOption("Skilling Masters.", Consumer { player3: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("Can you show me where skilling masters are located?")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Skilling masters can be found where you train the skill", "Each master also gives skilling tasks which when", "completed can reward you with skilling points", "that can be used to buy untradeable armour")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Skilling equipment and skillcapes of the skill", "gives additional perks and boosts the experience gained!")
                                                .start(player)
                                    }).thirdOption("I'm looking to trade.", Consumer { player3: Player? -> ShopManager.open(player, ShopIdentifiers.ANCIENT_WIZARD_STORE) }).addCancel("Nevermind.").start(player)
                        }).start(player)
            }
            Misc.random(3) == 3 -> {
                DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("Be quick " + player.username + "! I'm busy teleporting players!", "What you're looking for?")
                        .add(DialogueType.PLAYER_STATEMENT)
                        .setText("I need some help with teleports.").add(DialogueType.OPTION).setOptionTitle("Select an Option")
                        .firstOption("What are other ways of teleporting?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("I'm looking to teleport into different locations", "that you do not offer perhaps.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Indeed, my magical knowledge is not yet enough", "to master teleports to every location. However, I can", "explain what you will need to reach those unique", "locations.")
                                    .add(DialogueType.NPC_STATEMENT).setText("Which locations are you looking for?")
                                    .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                                    .firstOption("Skilling Guilds.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("How can I go to a skilling guild?").setExpression(DialogueExpression.DISTRESSED)
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Unfortunately, I cannot teleport you there.").add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Good news is you can use a charged @dre@Skilling necklace</col>", "to teleport to any skilling guild of you choice.", "All of our skilling guilds makes skilling easier.", "That also includes the Farming guild!")
                                                .start(player)
                                    }).secondOption("Bossing Teleports.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("Can I reach bosses directly?")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Some bosses require a certain kill count to reach, while", "others require a specific item.", "It all differs from each type of boss.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("You can use @dre@Max Cape</col> to teleport to several bosses directly.", "A charged @dre@Skull sceptre</col> can also be used for different bosses.", "For more information you can open the Boss Drop Table",
                                                        "from your quest tab which displays more information.")
                                                .start(player)
                                    }).thirdOption("Slayer Teleports.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("I'm looking for Slayer teleports.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("If you're looking to teleport to your Slayer task then", "you can use a @dre@Slayer's ring</col> to teleport there, or", "rubbing an @dre@Enchanted gem</col> will teleport you directly", "to your Slayer's task.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("In addittion, Slayer masters are easy to find. They can be", "reached with by the regular teleports. However, @dre@Duradel</col>", "is the only master that you", "can reach by using @dre@Slayer's enchantment</col> item.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Keep in mind that you will need a Slayer level", "of at least 50 and a combat level of 100 to be able", "to get a task from the master.")
                                                .start(player)
                                    }).fourthOption("Chest's Teleports.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("Where are all the chests located?")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Let's start off by the @blu@Crystal chest</col> which can be found in", "Edgeville's bank. You will need a @dre@Crystal key</col> to open it.", "You can find all the rewards on our @dre@Wiki</col> page!")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("The @blu@Muddy chest</col> can be found in the", "Wilderness maze east of KBD's lair.", "It's a dangerous area, be careful from Pker's.", "You can find all the rewards on our @dre@Wiki</col> page!")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Finally, @blu@Brimstone chest</col> can be found in the", "Mount Karuulm where Hydra's are located.", "You can find all the rewards on our @dre@Wiki</col> page!")
                                                .start(player)
                                    }).addCancel("Nevermind.").start(player)
                        }).secondOption("How much do you charge?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("Do you charge anything for teleports?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Teleports are completely free of cost...at least for now.").start(player)
                        }).thirdOption("What's your armour called?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("What's your armour called?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("My magical armour is the strongest ancient magical armour", "you will ever find, and it is called Ancient Qunitope robes.")
                                    .add(DialogueType.PLAYER_STATEMENT).setText("Where can I get one?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Hahaha, this armour cannot be found anywhere.", "Good luck finding one!").setExpression(DialogueExpression.DISTRESSED)
                                    .start(player)
                        }).fourthOption("Where is the Fountain of Rune?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("Where is the Fountain of Rune?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("The Fountain of Rune is a magical rock found within the", "ruined fortress south of the Wilderness Volcano. Players who", "have completed the hard tasks in the Wilderness Diary", "will be able to teleport there using the Wilderness sword 3/4!")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Players can charge their dragonstone jewellery", "@dre@Amulet of glory, combat bracelet, skills necklace, and", "@dre@ring of wealth</col> on the Fountain of Rune.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Fountain of Rune is the only location where the ring of wealth", "can be recharged. Each ring receives five teleport charges.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("In addition, while charging amulets of glory,", "there is a 1 in 25,000 chance that an amulet of glory", "will transmute into an amulet of eternal glory, which provides", "unlimited teleports rather than the usual six.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("When this occurs, The power of the fountain is transferred", "into an amulet of eternal glory. It will now have unlimited", "charges. will appear in the chatbox, replacing the...")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Fountain of Rune's standard message for charging jewellery.")
                                    .start(player)
                        }).fifthOption("Next...", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
                                    .firstOption("Teleport to La Isla Ebana.", Consumer { player2: Player? ->
                                        if (PlayerUtil.isMember(player)) {
                                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                    .setText("Can you teleport me to the @red@La Isla Ebana</col>?")
                                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                    .setText("Aboslutely! Do you want me to teleport you now?").setExpression(DialogueExpression.HAPPY)
                                                    .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                                                    .firstOption("Yes please.", Consumer { player4: Player? ->
                                                        TeleportHandler.offerTeleportFromNPC(player, Teleporting.TeleportLocation.LA_ISLA_EBANA.position,
                                                                player.spellbook.teleportType, false, true, npc.id, "La Isla Ebana")
                                                    }).addCancel("Maybe later.").start(player)
                                        } else {
                                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                    .setText("Can you teleport me to the @red@La Isla Ebana</col>?")
                                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                    .setText("I'm sorry only Ruby members or higher are allowed to access that area.").setExpression(DialogueExpression.ANGRY_4)
                                                    .start(player)
                                        }
                                    }).secondOption("Skilling Masters.", Consumer { player3: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("Can you show me where skilling masters are located?")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Skilling masters can be found where you train the skill", "Each master also gives skilling tasks which when", "completed can reward you with skilling points", "that can be used to buy untradeable armour")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Skilling equipment and skillcapes of the skill", "gives additional perks and boosts the experience gained!")
                                                .start(player)
                                    }).thirdOption("I'm looking to trade.", Consumer { player3: Player? -> ShopManager.open(player, ShopIdentifiers.ANCIENT_WIZARD_STORE) }).addCancel("Nevermind.").start(player)
                        }).start(player)
            }
            else -> {
                DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("Whooho, " + player.username + " what's up?")
                        .add(DialogueType.PLAYER_STATEMENT)
                        .setText("Can you help me with teleports?")
                        .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                        .setText("What do you need to know?")
                        .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                        .firstOption("What are other ways of teleporting?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("I'm looking to teleport into different locations", "that you do not offer perhaps.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Indeed, my magical knowledge is not yet enough", "to master teleports to every location. However, I can", "explain what you will need to reach those unique", "locations.")
                                    .add(DialogueType.NPC_STATEMENT).setText("Which locations are you looking for?")
                                    .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                                    .firstOption("Skilling Guilds.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("How can I go to a skilling guild?").setExpression(DialogueExpression.DISTRESSED)
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Unfortunately, I cannot teleport you there.").add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Good news is you can use a charged @dre@Skilling necklace</col>", "to teleport to any skilling guild of you choice.", "All of our skilling guilds makes skilling easier.", "That also includes the Farming guild!")
                                                .start(player)
                                    }).secondOption("Bossing Teleports.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("Can I reach bosses directly?")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Some bosses require a certain kill count to reach, while", "others require a specific item.", "It all differs from each type of boss.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("You can use @dre@Max Cape</col> to teleport to several bosses directly.", "A charged @dre@Skull sceptre</col> can also be used for different bosses.", "For more information you can open the Boss Drop Table",
                                                        "from your quest tab which displays more information.")
                                                .start(player)
                                    }).thirdOption("Slayer Teleports.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("I'm looking for Slayer teleports.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("If you're looking to teleport to your Slayer task then", "you can use a @dre@Slayer's ring</col> to teleport there, or", "rubbing an @dre@Enchanted gem</col> will teleport you directly", "to your Slayer's task.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("In addittion, Slayer masters are easy to find. They can be", "reached with by the regular teleports. However, @dre@Duradel</col>", "is the only master that you", "can reach by using @dre@Slayer's enchantment</col> item.")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Keep in mind that you will need a Slayer level", "of at least 50 and a combat level of 100 to be able", "to get a task from the master.")
                                                .start(player)
                                    }).fourthOption("Chest's Teleports.", Consumer { player2: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("Where are all the chests located?")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Let's start off by the @blu@Crystal chest</col> which can be found in", "Edgeville's bank. You will need a @dre@Crystal key</col> to open it.", "You can find all the rewards on our @dre@Wiki</col> page!")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("The @blu@Muddy chest</col> can be found in the", "Wilderness maze east of KBD's lair.", "It's a dangerous area, be careful from Pker's.", "You can find all the rewards on our @dre@Wiki</col> page!")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Finally, @blu@Brimstone chest</col> can be found in the", "Mount Karuulm where Hydra's are located.", "You can find all the rewards on our @dre@Wiki</col> page!")
                                                .start(player)
                                    }).addCancel("Nevermind.").start(player)
                        }).secondOption("How much do you charge?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("Do you charge anything for teleports?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Teleports are completely free of cost...at least for now.").start(player)
                        }).thirdOption("What's your armour called?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("What's your armour called?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("My magical armour is the strongest ancient magical armour", "you will ever find, and it is called Ancient Qunitope robes.")
                                    .add(DialogueType.PLAYER_STATEMENT).setText("Where can I get one?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Hahaha, this armour cannot be found anywhere.", "Good luck finding one!").setExpression(DialogueExpression.DISTRESSED)
                                    .start(player)
                        }).fourthOption("Where is the Fountain of Rune?", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("Where is the Fountain of Rune?")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("The Fountain of Rune is a magical rock found within the", "ruined fortress south of the Wilderness Volcano. Players who", "have completed the hard tasks in the Wilderness Diary", "will be able to teleport there using the Wilderness sword 3/4!")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Players can charge their dragonstone jewellery", "@dre@Amulet of glory, combat bracelet, skills necklace, and", "@dre@ring of wealth</col> on the Fountain of Rune.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Fountain of Rune is the only location where the ring of wealth", "can be recharged. Each ring receives five teleport charges.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("In addition, while charging amulets of glory,", "there is a 1 in 25,000 chance that an amulet of glory", "will transmute into an amulet of eternal glory, which provides", "unlimited teleports rather than the usual six.")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("When this occurs, The power of the fountain is transferred", "into an amulet of eternal glory. It will now have unlimited", "charges. will appear in the chatbox, replacing the...")
                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                    .setText("Fountain of Rune's standard message for charging jewellery.")
                                    .start(player)
                        }).fifthOption("Next...", Consumer { player1: Player? ->
                            DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
                                    .firstOption("Teleport to members zone.", Consumer { player2: Player? ->
                                        if (PlayerUtil.isMember(player)) {
                                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                    .setText("Can you teleport me to the @red@La Isla Ebana</col>?")
                                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                    .setText("Aboslutely! Do you want me to teleport you now?").setExpression(DialogueExpression.HAPPY)
                                                    .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                                                    .firstOption("Yes please.", Consumer { player4: Player? ->
                                                        TeleportHandler.offerTeleportFromNPC(player, Teleporting.TeleportLocation.LA_ISLA_EBANA.position,
                                                                player.spellbook.teleportType, false, true, npc.id, "La Isla Ebana")
                                                    }).addCancel("Maybe later.").start(player)
                                        } else {
                                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                    .setText("Can you teleport me to the @redLa Isla Ebana</col>?")
                                                    .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                    .setText("I'm sorry only Bronze members or higher are allowed to access that area.").setExpression(DialogueExpression.ANGRY_4)
                                                    .start(player)
                                        }
                                    }).secondOption("Skilling Masters.", Consumer { player3: Player? ->
                                        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("Can you show me where skilling masters are located?")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Skilling masters can be found where you train the skill", "Each master also gives skilling tasks which when", "completed can reward you with skilling points", "that can be used to buy untradeable armour")
                                                .add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.id)
                                                .setText("Skilling equipment and skillcapes of the skill", "gives additional perks and boosts the experience gained!")
                                                .start(player)
                                    }).thirdOption("I'm looking to trade.", Consumer { player3: Player? -> ShopManager.open(player, ShopIdentifiers.ANCIENT_WIZARD_STORE) }).addCancel("Nevermind.").start(player)
                        }).start(player)
            }
        }
    }
}