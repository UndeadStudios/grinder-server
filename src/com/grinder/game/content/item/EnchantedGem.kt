package com.grinder.game.content.item

import com.grinder.game.content.pvm.MonsterKillTracker
import com.grinder.game.content.skill.skillable.impl.magic.Teleporting
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.passedTime
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.game.model.onEquipAction
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.game.model.onSecondInventoryAction
import com.grinder.game.model.onThirdInventoryAction
import com.grinder.util.ItemID
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * Handles item menu actions for the enchanted gem item.
 *
 */
object EnchantedGem {

    init {
        onFirstInventoryAction(ItemID.ENCHANTED_GEM) {
            rub(player)
        }
        onSecondInventoryAction(ItemID.ENCHANTED_GEM) {
            MonsterKillTracker.sendSlayerLog(player)
        }
        onThirdInventoryAction(ItemID.ENCHANTED_GEM) {
            player.message("You don't have an active Slayer partner.")
        }

        onEquipAction(ItemID.ENCHANTED_GEM) {
            check(player)
        }
    }

    private fun rub(player: Player) {
        if (player.slayer.task == null) {
            sendNoTaskOption(player)
            return
        }
        DialogueBuilder(DialogueType.STATEMENT)
            .setText("Do you wish to teleport to your Slayer task", "monster location?")
            .add(DialogueType.OPTION)
            .firstOption("Teleport.") {
                //println("HERE");
                //println(it.slayer.task);
                //println(it.slayer.task.monster);
                //println(it.slayer.task.monster.locations[0])
                //TeleportHandler.teleport(it, it.slayer.task.monster.locations[0], it.spellbook.teleportType, true, true)
                DialogueManager.sendStatement(player, "You must use a Slayer's ring to teleport to your Slayer task directly.")
            }
            .secondOption("Teleport me to my Slayer Master.") {
                teleportToSlayerMaster(player)
            }.addCancel("Cancel.").start(player)

    }

    private fun check(player: Player) {

        if (!player.passedTime(Attribute.GENERIC_ACTION, 1, TimeUnit.SECONDS, message = false))
            return

        val slayer = player.slayer

        player.packetSender.sendInterfaceRemoval()

        if (slayer.task == null) {
            sendNoTaskOption(player)
            return
        }

        val amountLeft =slayer.task.amountLeft
        val message =
            "Your current Slayer assignment is to slay $amountLeft " + "more ${slayer.task.monster.getName()}${if (amountLeft > 1) "'s" else ""}!"
        DialogueManager.sendStatement(player, message)
        player.sendMessage("Your current Slayer assignment is to slay $amountLeft " + "more @dre@${slayer.task.monster.getName()}${if (amountLeft > 1) "'s" else ""}</col>!")
        }

    private fun teleportToSlayerMaster(player: Player) {
        DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
            .firstOption("Turael",
                Consumer {
                    if (player.combat.isInCombat && !PlayerUtil.isDeveloper(player)) {
                        player.packetSender.sendMessage(
                            "You must wait a few seconds after being out of combat to teleport!",
                            1000
                        )
                        return@Consumer
                    }
                    if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.SLAYER_MASTER_TURAEL.position, false, false, TeleportType.NORMAL)) {
                        TeleportHandler.teleport(player, Teleporting.TeleportLocation.SLAYER_MASTER_TURAEL.position, TeleportType.PURO_PURO, false, true)
                    }
                }).secondOption("Mazchna",
                Consumer {
                    if (player.combat.isInCombat && !PlayerUtil.isDeveloper(player)) {
                        player.packetSender.sendMessage(
                            "You must wait a few seconds after being out of combat to teleport!",
                            1000
                        )
                        return@Consumer
                    }
                    if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.SLAYER_MASTER_MAZCHNA.position, false, false, TeleportType.NORMAL)) {
                        TeleportHandler.teleport(player, Teleporting.TeleportLocation.SLAYER_MASTER_MAZCHNA.position, TeleportType.PURO_PURO, false, true)
                    }
                }).thirdOption("Vannaka",
                Consumer {
                    if (player.combat.isInCombat && !PlayerUtil.isDeveloper(player)) {
                        player.packetSender.sendMessage(
                            "You must wait a few seconds after being out of combat to teleport!",
                            1000
                        )
                        return@Consumer
                    }
                    if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.SLAYER_MASTER_VANNAKA.position, false, false, TeleportType.NORMAL)) {
                        TeleportHandler.teleport(player, Teleporting.TeleportLocation.SLAYER_MASTER_VANNAKA.position, TeleportType.PURO_PURO, false, true)
                    }
                }).fourthOption("Chaeldar",
                Consumer {
                    if (player.combat.isInCombat && !PlayerUtil.isDeveloper(player)) {
                        player.packetSender.sendMessage(
                            "You must wait a few seconds after being out of combat to teleport!",
                            1000
                        )
                        return@Consumer
                    }
                    if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.SLAYER_MASTER_CHAELDAR.position, false, false, TeleportType.NORMAL)) {
                        TeleportHandler.teleport(player, Teleporting.TeleportLocation.SLAYER_MASTER_CHAELDAR.position, TeleportType.PURO_PURO, false, true)
                    }
                }).fifthOption("Next...", Consumer {
                    DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
                        .firstOption("Duradel",
                            Consumer {
                                if (player.combat.isInCombat && !PlayerUtil.isDeveloper(player)) {
                                    player.packetSender.sendMessage(
                                        "You must wait a few seconds after being out of combat to teleport!",
                                        1000
                                    )
                                    return@Consumer
                                }
                                if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.SLAYER_MASTER_DURADEL.position, false, false, TeleportType.NORMAL)) {
                                    TeleportHandler.teleport(player, Teleporting.TeleportLocation.SLAYER_MASTER_DURADEL.position, TeleportType.PURO_PURO, false, true)
                                }
                            }).secondOption("Konar quo Maten",
                            Consumer {
                                if (player.combat.isInCombat && !PlayerUtil.isDeveloper(player)) {
                                    player.packetSender.sendMessage(
                                        "You must wait a few seconds after being out of combat to teleport!",
                                        1000
                                    )
                                    return@Consumer
                                }
                                if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.SLAYER_MASTER_KONAR.position, false, false, TeleportType.NORMAL)) {
                                    TeleportHandler.teleport(player, Teleporting.TeleportLocation.SLAYER_MASTER_KONAR.position, TeleportType.PURO_PURO, false, true)
                                }
                            }).thirdOption("Nieve",
                            Consumer {
                                if (player.combat.isInCombat && !PlayerUtil.isDeveloper(player)) {
                                    player.packetSender.sendMessage(
                                        "You must wait a few seconds after being out of combat to teleport!",
                                        1000
                                    )
                                    return@Consumer
                                }
                                if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.SLAYER_MASTER_NIEVE.position, false, false, TeleportType.NORMAL)) {
                                    TeleportHandler.teleport(player, Teleporting.TeleportLocation.SLAYER_MASTER_NIEVE.position, TeleportType.PURO_PURO, false, true)
                                }
                            }).fourthOption("Krystilia",
                            Consumer {
                                if (player.combat.isInCombat && !PlayerUtil.isDeveloper(player)) {
                                    player.packetSender.sendMessage(
                                        "You must wait a few seconds after being out of combat to teleport!",
                                        1000
                                    )
                                    return@Consumer
                                }
                                if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.SLAYER_MASTER_KRYSTILIA.position, false, false, TeleportType.NORMAL)) {
                                    TeleportHandler.teleport(player, Teleporting.TeleportLocation.SLAYER_MASTER_KRYSTILIA.position, TeleportType.PURO_PURO, false, true)
                                }
                            }).addCancel().start(player)
                    }).start(player)
        }

    private fun sendNoTaskOption(player: Player) {
        DialogueBuilder(DialogueType.STATEMENT)
            .setText("You don't have a Slayer task. Would you like", "to visit a Slayer Master?")
            .add(DialogueType.OPTION)
            .firstOption("Yes please.") {
                teleportToSlayerMaster(player)
            }
            .addCancel("Maybe later!")
            .start(player)
        }
}