package com.grinder.game.content.item

import com.grinder.game.entity.agent.player.notInDangerOrAfkOrBusyOrInteracting
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.interfaces.dialogue.promptRedeemDialogue
import com.grinder.game.model.item.Item
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.util.DiscordBot
import com.grinder.util.Logging
import com.grinder.util.Misc

object PremiumTicket {

    init {
        onFirstInventoryAction(15731) {
            if (player.gameMode.isSpawn) {
                player.sendMessage("You cannot use this item.")
                return@onFirstInventoryAction;
            }
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                val amount = player.inventory.getAmount(15731)
                if (amount > 0) {
                    player.promptRedeemDialogue(Item(15731, amount)) {
                        it.points.increase(AttributeManager.Points.PREMIUM_POINTS, 5000 * amount)
                        //case MINIGAME_POINTS:
                        player.packetSender.sendMessage("<img=766> You have redeemed @dre@" + Misc.format(5000 * amount) + " premium points</col>.")
                        // Disable loggin for spawn game modes
                        if (!player.gameMode.isSpawn) {
                            Logging.log(
                                "premiumtokenredeem",
                                player.username + ": has just redemeed 5,000 premium points ticket."
                            );
                            if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("" + player.username + " has just redeemed 5,000 premium points token.")
                        }
                    }
                }
            }
        }
        onFirstInventoryAction(15732) {
            if (player.gameMode.isSpawn) {
                player.sendMessage("You cannot use this item.")
                return@onFirstInventoryAction;
            }
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                val amount = player.inventory.getAmount(15732)
                if (amount > 0) {
                    player.promptRedeemDialogue(Item(15732, amount)) {
                        it.points.increase(AttributeManager.Points.PREMIUM_POINTS, 10_000 * amount)
                        player.packetSender.sendMessage("<img=766> You have redeemed @dre@" + Misc.format(10000 * amount) + " premium points</col>.")
                        // Disable loggin for spawn game modes
                        if (!player.gameMode.isSpawn) {
                            Logging.log(
                                "premiumtokenredeem",
                                player.username + ": has just redemeed 10,000 premium points ticket."
                            );
                            if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("" + player.username + " has just redeemed 10,000 premium points token.")
                        }
                    }
                }
            }
        }

        onFirstInventoryAction(15733) {
            if (player.gameMode.isSpawn) {
                player.sendMessage("You cannot use this item.")
                return@onFirstInventoryAction;
            }
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                val amount = player.inventory.getAmount(15733)
                if (amount > 0) {
                    player.promptRedeemDialogue(Item(15733, amount)) {
                        it.points.increase(AttributeManager.Points.PREMIUM_POINTS, 25_000 * amount)
                        player.packetSender.sendMessage("<img=766> You have redeemed @dre@" + Misc.format(25000 * amount) + " premium points</col>.")
                        // Disable loggin for spawn game modes
                        if (!player.gameMode.isSpawn) {
                            Logging.log(
                                "premiumtokenredeem",
                                player.username + ": has just redemeed 25,000 premium points ticket."
                            );
                            if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("" + player.username + " has just redeemed 25,000 premium points token.")
                        }
                    }
                }
            }
        }

        onFirstInventoryAction(15734) {
            if (player.gameMode.isSpawn) {
                player.sendMessage("You cannot use this item.")
                return@onFirstInventoryAction;
            }
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                val amount = player.inventory.getAmount(15734)
                if (amount > 0) {
                    player.promptRedeemDialogue(Item(15734, amount)) {
                        it.points.increase(AttributeManager.Points.PREMIUM_POINTS, 50_000 * amount)
                        player.packetSender.sendMessage("<img=766> You have redeemed @dre@" + Misc.format(50000 * amount) + " premium points</col>.")
                        // Disable loggin for spawn game modes
                        if (!player.gameMode.isSpawn) {
                            Logging.log(
                                "premiumtokenredeem",
                                player.username + ": has just redemeed 50,000 premium points ticket."
                            );
                            if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("" + player.username + " has just redeemed 50,000 premium points token.")
                        }
                    }
                }
            }
        }

        onFirstInventoryAction(15735) {
            if (player.gameMode.isSpawn) {
                player.sendMessage("You cannot use this item.")
                return@onFirstInventoryAction;
            }
            if (player.notInDangerOrAfkOrBusyOrInteracting()) {
                val amount = player.inventory.getAmount(15735)
                if (amount > 0) {
                    player.promptRedeemDialogue(Item(15735, amount)) {
                        it.points.increase(AttributeManager.Points.PREMIUM_POINTS, 100_000 * amount)
                        player.packetSender.sendMessage("<img=766> You have redeemed @dre@" + Misc.format(100000 * amount) + " premium points</col>.")
                        // Disable loggin for spawn game modes
                        if (!player.gameMode.isSpawn) {
                            Logging.log(
                                "premiumtokenredeem",
                                player.username + ": has just redemeed 100,000 premium points ticket."
                            );
                            if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("" + player.username + " has just redeemed 100,000 premium points token.")
                        }
                    }
                }
            }
        }

    }
}