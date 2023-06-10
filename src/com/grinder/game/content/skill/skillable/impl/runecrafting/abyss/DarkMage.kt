package com.grinder.game.content.skill.skillable.impl.runecrafting.abyss

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.NPCActions
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.util.NpcID

object DarkMage {

    init {

        NPCActions.onClick(NpcID.DARK_MAGE) {
            if(it.type == NPCActions.ClickAction.Type.FIRST_OPTION) {
                startDialogue(it.player)
            } else {
                if(hasPouchesToRepair(it.player)) {
                    repairPouches(it.player)
                    it.player.message("The mage repairs your pouches.")
                } else {
                    DialogueBuilder(DialogueType.STATEMENT)
                            .setText("You not not have any pouched that need repairing.")
                            .start(it.player)
                }
            }

            true
        }

    }

    fun getBuilder(dialogueType: DialogueType = DialogueType.PLAYER_STATEMENT): DialogueBuilder {
        return DialogueBuilder(dialogueType)
                .setExpression(DialogueExpression.CALM)
                .setNpcChatHead(NpcID.DARK_MAGE)
    }

    fun startDialogue(player: Player) {
        getBuilder()
                .setText("Hello there.")
                .add(DialogueType.NPC_STATEMENT)
                .setText("Quiet!", "You must not break my concentration!")
                .add(DialogueType.OPTION)
                .firstOption("Why not?", DarkMage::whyNot)
                .secondOption("What are you doing here?", DarkMage::whatAreYouDoing)
                .thirdOption("I need your help with something...", DarkMage::needHelp)
                .addCancel("Ok, sorry.")
                .start(player)
    }

    fun whyNot(player : Player) {
        getBuilder()
                .setText("Why not?")
                .add(DialogueType.NPC_STATEMENT)
                .setText(
                        "Well, if my concentration is broken while keeping",
                        " this gate open then, if we are lucky, everyone ",
                        "within a one mile radius will either have their heads explode",
                        "or will be consumed internally by creatures of the Abyss."
                )
                .add(DialogueType.PLAYER_STATEMENT)
                .setText("Erm... And if we are unlucky?")
                .add(DialogueType.NPC_STATEMENT)
                .setText(
                        "If we are unlucky, then the entire universe will",
                        "begin to fold in upon itself, and all reality as we know it ",
                        "will be annihilated in a single stroke."
                )
                .add(DialogueType.NPC_STATEMENT)
                .setText("So leave me alone!")
                .add(DialogueType.OPTION)
                .firstOption("What are you doing here?", DarkMage::whatAreYouDoing)
                .secondOption("I need your help with something...", DarkMage::needHelp)
                .addCancel("Ok, sorry.")
                .start(player)
    }

    fun whatAreYouDoing(player: Player) {
        getBuilder()
                .setText("What are you doing here?")
                .add(DialogueType.NPC_STATEMENT)
                .setText(
                        "Do you mean what am I doing here in Abyssal space,",
                        "or are you asking me what I consider my ultimate role",
                        "to be in the voyage that we call life?"
                )
                .add(DialogueType.PLAYER_STATEMENT)
                .setText("Um... the first one.")
                .add(DialogueType.NPC_STATEMENT)
                .setText(
                        "By remaining here and holding this portal open,",
                        "I am the providing a permanent link between normal space",
                        "and this strange dimension that we call Abyssal space."
                )
                .add(DialogueType.NPC_STATEMENT)
                .setText(
                        "As long as this spell remains in effect,",
                        "we have the capability to teleport into abyssal space at will."
                )
                .add(DialogueType.NPC_STATEMENT)
                .setText("Now leave me be!", "I can afford no distraction in my task!")
                .add(DialogueType.OPTION)
                .firstOption("Why not?", DarkMage::whyNot)
                .secondOption("I need your help with something...", DarkMage::needHelp)
                .addCancel("Ok, sorry.")
                .start(player)
    }

    fun needHelp(player: Player) {
        if(hasPouchesToRepair(player)) {
            getBuilder()
                    .setText(
                            "Sorry to disturb you,",
                            "I just needed your help with something quickly."
                    )
                    .add(DialogueType.PLAYER_STATEMENT)
                    .setText(
                            "I think my essence pouched might be... degrading",
                            "Can you restore them for me?"
                    )
                    .add(DialogueType.NPC_STATEMENT)
                    .setText(
                            "A simple transfiguration spell should resolve that for you",
                            "Now leave me be!"
                    ).start(player)
            repairPouches(player)
            player.message("The mage repairs your pouches.")
        } else {
            getBuilder()
                    .setText("Actually, I can't think of anything right now...")
                    .add(DialogueType.NPC_STATEMENT)
                    .setText("THEN STOP DISTRACTING ME!")
                    .add(DialogueType.NPC_STATEMENT)
                    .setText(
                            "Honestly, you have no idea of the pressure",
                            "I am under attempting to keep this portal open!"
                    ).start(player)
        }
    }

    /**
     * Check if the player has any pouches that need repairing.
     */
    fun hasPouchesToRepair(player: Player): Boolean {
        return player.pouches.entries.any { it.key.capacity > it.value.capacity }
    }

    /**
     * Repair pouches by restoring thei capacity.
     */
    fun repairPouches(player: Player) {
        player.pouches.entries.forEach { it.value.capacity = it.key.capacity}
    }
}