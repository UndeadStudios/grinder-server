package com.grinder.game.entity.agent.npc.monster.impl

import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.npc.monster.Monster
import com.grinder.game.entity.agent.npc.monster.scheduleRandomSpeech
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.NPCActions
import com.grinder.game.model.Position
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.util.NpcID
import java.util.function.Consumer

class SkrachUglogwee(id: Int, position: Position) : Monster(id, position) {
    
    init {
        scheduleRandomSpeech(22..25,
            "The ice will crumble soon!",
            "We are afraid of drowning here!",
            "Survive!",
            "Grunt Skreerek!",
            "Midnight, on the bridge. Come alone.")
    }

    override fun attackRange(type: AttackType) = 0

    companion object {

        init {
            NPCActions.onClick(NpcID.SKRACH_UGLOGWEE_4853) { action ->
                val player: Player = action.player
                if (action.type === NPCActions.ClickAction.Type.FIRST_OPTION) {
                    DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.SKRACH_UGLOGWEE_4853)
                        .setText("We don't have much time left before the ice melts", "into water!")
                        .add(DialogueType.PLAYER_STATEMENT)
                        .setText("Huh?")
                        .add(DialogueType.OPTION).setOptionTitle("Select an Option")
                        .firstOption("Who are you?", Consumer {
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("Who are you?")
                                .add(DialogueType.NPC_STATEMENT)
                                .setText("I am the lone survivor and guardian of this place.", "It's my hometown now.")
                                .add(DialogueType.NPC_STATEMENT)
                                .setText(
                                    "It's one of the coldest places where only the true hereos",
                                    "survive the extreme weather conditions."
                                )
                                .add(DialogueType.NPC_STATEMENT)
                                .setText(
                                    "I am sure if you accept to take the challenge through",
                                    "this cave, you can also become a champion."
                                )
                                .add(DialogueType.NPC_STATEMENT)
                                .setText("Good luck to you I guess.")
                                .start(player)
                        }).secondOption("What are you doing here?", Consumer {
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("What are you doing here?")
                                .add(DialogueType.NPC_STATEMENT)
                                .setText(
                                    "After I was able to survive the weather conditions here, I",
                                    "became familiar with this place and it became my hometown."
                                )
                                .add(DialogueType.NPC_STATEMENT)
                                .setText(
                                    "For your information there are many people who tried",
                                    "to survive on here but ended up being dead."
                                )
                                .add(DialogueType.NPC_STATEMENT)
                                .setText(
                                    "When I have broken down all trace of your body, I",
                                    "then rebuild it into the form I am thinking of!"
                                )
                                .add(DialogueType.NPC_STATEMENT)
                                .setText(
                                    "You should leave this place, unless you",
                                    "are able to withstand the harsh conditions."
                                )
                                .start(player)
                        }).thirdOption("What is this place?", Consumer {
                            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("What is this place?")
                                .add(DialogueType.NPC_STATEMENT)
                                .setText(
                                    "It's one of the coldest places.",
                                    "There's a cave entrance which consists of 30 hard waves",
                                    "of dangerous lurking monsters."
                                )
                                .add(DialogueType.NPC_STATEMENT)
                                .setText("Only the true champions are able to survive.")
                                .add(DialogueType.NPC_STATEMENT)
                                .setText(
                                    "I suggest if you are not up to the challenge to leave.",
                                    "Those who survive the challenge are rewarded with great rewards."
                                )
                                .add(DialogueType.NPC_STATEMENT)
                                .setText("Good luck, dear " + player.username + "!")
                                .add(DialogueType.PLAYER_STATEMENT)
                                .setText("Thanks I guess.")
                                .start(player)
                        }).addCancel().start(player)
                }
                true
            }
        }
    }
}