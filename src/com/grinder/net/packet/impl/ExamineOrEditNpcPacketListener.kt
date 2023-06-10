package com.grinder.net.packet.impl

import com.grinder.game.World
import com.grinder.game.definition.NpcDefinition
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.interfaces.dialogue.secondOption
import com.grinder.game.model.interfaces.dialogue.thirdOption
import com.grinder.game.model.interfaces.dialogue.fourthOption
import com.grinder.game.model.interfaces.dialogue.fifthOption
import com.grinder.game.model.interfaces.syntax.EnterSyntax
import com.grinder.game.task.TaskManager
import com.grinder.net.packet.PacketListener
import com.grinder.net.packet.PacketReader
import java.util.function.IntConsumer

class ExamineOrEditNpcPacketListener : PacketListener {

    override fun handleMessage(player: Player, packetReader: PacketReader, packetOpcode: Int) {
        val data = packetReader.readShort().toInt()

        if (data < 0)
            return

        if (player.rights.isHighStaff)
            edit(player, data)
        else
            examine(player, data)
    }

    private fun edit(player: Player, npcIndex: Int) {

        val npc = World.npcs.get(npcIndex)

        if (npc != null) {

            player.packetSender.sendEntityHint(npc)
            TaskManager.submit(10) {
                player.packetSender.sendEntityHintRemoval(false)
            }

            val optionMenu = DialogueBuilder(DialogueType.OPTION)

            optionMenu
                    .firstOption("Reset Mechanic") {
                        DialogueBuilder(DialogueType.OPTION)
                                .setOptionTitle("Choose a Mechanic")
                                .firstOption("combat") { it.sendMessage("You reset the combat of the npc"); it.packetSender.sendInterfaceRemoval(); npc.combat.reset(true) }
                                .secondOption("retreat") { it.sendMessage("You reset the retreating of the npc"); it.packetSender.sendInterfaceRemoval(); npc.movementCoordinator.reset() }
                                .thirdOption("movement") {  it.sendMessage("You reset the movement of the npc"); it.packetSender.sendInterfaceRemoval(); npc.motion.clearSteps() }
                                .fourthOption("go back") { optionMenu.start(it) }
                                .start(it)
                    }
                    .secondOption("Edit Param") {
                        DialogueBuilder(DialogueType.OPTION)
                                .setOptionTitle("Choose a Param")
                                .firstOption("walk-radius   (${npc.movementCoordinator.radius})") { promptIntSyntax(it, "walk-radius", IntConsumer { npc.movementCoordinator.radius = it}) }
                                .secondOption("respawn-time (${npc.fetchDefinition().respawn})") { promptIntSyntax(it, "respawn-time", IntConsumer { npc.fetchDefinition().respawn = it})  }
                                .thirdOption("spawn-position (${npc.spawnPosition.compactString()})") {
                                    val pos = npc.spawnPosition
                                    DialogueBuilder(DialogueType.OPTION)
                                            .setOptionTitle("Edit coordinate")
                                            .firstOption("x     (${pos.x})") { promptIntSyntax(it, "spawn-x", IntConsumer { pos.x = it }) }
                                            .secondOption("y     (${pos.y})") { promptIntSyntax(it, "spawn-y", IntConsumer { pos.y= it }) }
                                            .thirdOption("z     (${pos.z})") { promptIntSyntax(it, "spawn-z", IntConsumer { pos.z = it }) }
                                            .fourthOption("x, y, z") {
                                                it.enterSyntax = object: EnterSyntax {
                                                    override fun handleSyntax(player: Player?, input: String?) {
                                                        val coordinates = input?.split(",")?.map { it.trim().toIntOrNull() }
                                                        if(coordinates != null){
                                                            coordinates[0]?.let { pos.x = it }
                                                            coordinates[1]?.let { pos.y = it }
                                                            coordinates[2]?.let { pos.z = it }
                                                            DialogueBuilder(DialogueType.STATEMENT)
                                                                    .setText("You set the spawn-position of the npc to ${pos.compactString()}", "This change is not permanent!")
                                                                    .start(player?:return)
                                                        }
                                                    }
                                                    override fun handleSyntax(player: Player?, input: Int) {}
                                                }
                                                it.packetSender.sendEnterInputPrompt("Please enter like this: x, y, z (e.g.: ${pos.compactString()})")
                                            }
                                            .start(it)
                                }
                                .fourthOption("set size (${npc.size})") { promptIntSyntax(it, "size", IntConsumer { npc.size = it }) }
                                .fifthOption("go back") { optionMenu.start(it) }
                                .start(it)
                    }
                    .thirdOption("Force Re-spawn") {
                        World.npcRemoveQueue.add(npc)
                        npc.respawn()
                        it.sendMessage("You re-spawned the npc at ${npc.spawnPosition}")
                        it.packetSender.sendInterfaceRemoval();
                    }
                    .fourthOption("Move to Spawn Position") {it.sendMessage("You moved the npc to ${npc.spawnPosition}"); it.packetSender.sendInterfaceRemoval(); npc.moveTo(npc.spawnPosition) }
                    .fifthOption("Attack me") {
                        npc.combat.initiateCombat(it)
                        it.packetSender.sendInterfaceRemoval();
                    }
                    .start(player)
        }
    }

    private fun promptIntSyntax(player: Player, paramName: String, consumer: IntConsumer) {
        player.enterSyntax = object : EnterSyntax {
            override fun handleSyntax(player: Player?, input: String?) {
                input?.toIntOrNull()?.let {
                    handleSyntax(player, it)
                }
            }
            override fun handleSyntax(player: Player?, input: Int) {
                consumer.accept(input)
                DialogueBuilder(DialogueType.STATEMENT)
                        .setText("You set the $paramName of the npc to $input", "This change is not permanent!")
                        .start(player?:return)
            }
        }
        player.packetSender.sendEnterInputPrompt("Set the $paramName of the npc (integer values only)!")
    }

    private fun examine(player: Player, npcId: Int) {

        val definition = NpcDefinition.forId(npcId)

        if (definition != null) {


            val npcName = definition.name?.toLowerCase()?:return
            val examine = definition.examine?:return

            if (examine.isBlank()) {
                val prefix = if (startsWithVowel(npcName)) "an" else "a"
                player.packetSender.sendMessage("It's $prefix $npcName!")
            } else
                player.packetSender.sendMessage(examine)
        }
    }

    private fun startsWithVowel(npcName: String): Boolean {
        return npcName.startsWith("a") || npcName.startsWith("e") || npcName.startsWith("i") || npcName.startsWith("o") || npcName.startsWith("u")
    }

}
