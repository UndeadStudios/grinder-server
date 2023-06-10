package com.grinder.game.content.item.jewerly

import com.grinder.game.content.skill.skillable.impl.magic.Teleporting
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.passedTime
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.game.model.item.Item
import com.grinder.game.model.onSecondContainerEquipmentAction
import com.grinder.game.model.onSecondInventoryAction
import com.grinder.game.model.onThirdInventoryAction
import com.grinder.util.ItemID
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * Handles slayer ring
 *
 * @author 2012
 */
object SlayerRing {

    init {
        onSecondInventoryAction(
            ItemID.SLAYER_RING_ETERNAL_,
            ItemID.SLAYER_RING_8_, ItemID.SLAYER_RING_7_,
            ItemID.SLAYER_RING_6_, ItemID.SLAYER_RING_5_,
            ItemID.SLAYER_RING_4_, ItemID.SLAYER_RING_3_,
            ItemID.SLAYER_RING_2_, ItemID.SLAYER_RING_1_,
            24444, 24370, 23075, 15910, 16109, 23073, 21888, 21890)
        { check(player) }

        onSecondContainerEquipmentAction(
            ItemID.SLAYER_RING_ETERNAL_,
            ItemID.SLAYER_RING_8_, ItemID.SLAYER_RING_7_,
            ItemID.SLAYER_RING_6_, ItemID.SLAYER_RING_5_,
            ItemID.SLAYER_RING_4_, ItemID.SLAYER_RING_3_,
            ItemID.SLAYER_RING_2_, ItemID.SLAYER_RING_1_,
            24444, 24370, 23075, 15910, 16109, 23073, 21888, 21890)
        { check(player) }

        onThirdInventoryAction(
            ItemID.SLAYER_RING_ETERNAL_,
            ItemID.SLAYER_RING_8_, ItemID.SLAYER_RING_7_,
            ItemID.SLAYER_RING_6_, ItemID.SLAYER_RING_5_,
            ItemID.SLAYER_RING_4_, ItemID.SLAYER_RING_3_,
            ItemID.SLAYER_RING_2_, ItemID.SLAYER_RING_1_)
        { rub(player, getItem(), getSlot()) }
    }

	fun rub(player: Player, item: Item?, slot: Int) {

        if (player.slayer.task == null || player.slayer.task.monster.locations[0] == null) {
            sendNoTaskOption(player)
            return
        }

        DialogueBuilder(DialogueType.STATEMENT)
            .setText("Do you wish to teleport to your Slayer task", "monster location?")
            .add(DialogueType.OPTION)
            .firstOption("Yes please.") {
                val slayerRing = player.inventory.findAtSlot(slot)
                slayerRing.ifPresent { ring ->
                    if (ring.id in ItemID.SLAYER_RING_8_..ItemID.SLAYER_RING_1_){
                        if (ring.id == ItemID.SLAYER_RING_1_){
                            it.replaceInventoryItem(ring, Item(-1), 3)
                            player.message("Your Slayer ring shatters!", Color.MAGENTA, 3)
                        } else
                        it.replaceInventoryItem(ring, Item(ring.id+1), 3)
                    }
                    TeleportHandler.teleport(
                        it,
                        it!!.slayer!!.task!!.monster!!.locations[0],
                        it.spellbook.teleportType,
                        true,
                        true)
                }

            }
            .secondOption("Teleport me to my Slayer Master.") {
                teleportToSlayerMaster(player)
            }
            .addCancel("Maybe later!")
            .start(player)
    }

	fun check(player: Player) {

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

    fun sendNoTaskOption(player: Player) {
        DialogueBuilder(DialogueType.STATEMENT)
            .setText("You don't have a Slayer task. Would you like", "to visit a Slayer Master?")
            .add(DialogueType.OPTION)
            .firstOption("Yes please.") {
                teleportToSlayerMaster(player)
            }
            .addCancel("Maybe later!")
            .start(player)
    }

    fun teleportToSlayerMaster(player: Player) {
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
                        TeleportHandler.teleport(player, Teleporting.TeleportLocation.SLAYER_MASTER_TURAEL.position, TeleportType.NORMAL, false, true)
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
                        TeleportHandler.teleport(player, Teleporting.TeleportLocation.SLAYER_MASTER_MAZCHNA.position, TeleportType.NORMAL, false, true)
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
                        TeleportHandler.teleport(player, Teleporting.TeleportLocation.SLAYER_MASTER_VANNAKA.position, TeleportType.NORMAL, false, true)
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
                        TeleportHandler.teleport(player, Teleporting.TeleportLocation.SLAYER_MASTER_CHAELDAR.position, TeleportType.NORMAL, false, true)
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
                                TeleportHandler.teleport(player, Teleporting.TeleportLocation.SLAYER_MASTER_KONAR.position, TeleportType.NORMAL, false, true)
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
                                TeleportHandler.teleport(player, Teleporting.TeleportLocation.SLAYER_MASTER_NIEVE.position, TeleportType.NORMAL, false, true)
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
                                TeleportHandler.teleport(player, Teleporting.TeleportLocation.SLAYER_MASTER_KRYSTILIA.position, TeleportType.NORMAL, false, true)
                            }
                        }).addCancel().start(player)
            }).start(player)
    }
}