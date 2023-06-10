package com.grinder.game.content.item

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.notInDangerOrAfkOrBusyOrInteracting
import com.grinder.game.entity.getInt
import com.grinder.game.entity.setInt
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.promptRedeemDialogue
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.util.ItemID

/**
 * Handles the olschool bond item, increases the donated amount registered
 * for the player using it.
 */
object OldschoolBond {

    init {
        onFirstInventoryAction(ItemID.OLD_SCHOOL_BOND) {
            if (player.gameMode.isSpawn) {
                player.sendMessage("You cannot use the member's bond in spawn game mode.")
                return@onFirstInventoryAction
            }
            if (player.notInDangerOrAfkOrBusyOrInteracting()){
                player.promptRedeemDialogue(getItem() ?: return@onFirstInventoryAction) {
                        player.message("<img=749> @yel@You have successfully bonded the member's rank on your account! $50.00 balance was added to your account.")
                    player.setInt(Attribute.AMOUNT_PAID, player.getInt(Attribute.AMOUNT_PAID) + 50)
                    player.setInt(Attribute.TIMES_PAID, player.getInt(Attribute.TIMES_PAID) + 1)
                    player.setInt(Attribute.FIFTY_DOLLAR_BOND, player.getInt(Attribute.FIFTY_DOLLAR_BOND) + 1)
/*                    if (!PlayerUtil.isStaff(player)) {
                        player.rights = PlayerUtil.getMemberRights(player)
                        if (!player.gameMode.isAnyIronman)
                            player.crown = PlayerUtil.getMemberRights(player).ordinal

                    }*/
                    player.packetSender.sendRights()
                    val memberRights = PlayerUtil.getMemberRights(player)
                    player.message("Congratulations, you're now a @dre@"+memberRights.image+" " + memberRights + "</col>.")
                    player.packetSender.sendJinglebitMusic(134, 0)
                    if (player.rights == PlayerRights.RUBY_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 49 || player.attributes.containsKey(Attribute.FREE_RUBY_MEMBER_RANK)) {
                        AchievementManager.processFor(AchievementType.SPREAD_LOVE, player);
                    }
                    if (player.rights == PlayerRights.TOPAZ_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 99) {
                        AchievementManager.processFor(AchievementType.SUPERIOR_SUPPORT, player);
                    }
                    if (player.rights == PlayerRights.AMETHYST_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 149) {
                        AchievementManager.processFor(AchievementType.EXTREME_SUPPORT, player);
                    }
                    if (player.rights == PlayerRights.LEGENDARY_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 249) {
                        AchievementManager.processFor(AchievementType.LEGENDARY_SUPPORT, player);
                    }
                    if (player.rights == PlayerRights.PLATINUM_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 499) {
                        AchievementManager.processFor(AchievementType.PLATINUM_SUPPORT, player);
                    }
                    if (player.rights == PlayerRights.TITANIUM_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 749) {
                        AchievementManager.processFor(AchievementType.TITANIUM_SUPPORT, player);
                    }
                    if (player.rights == PlayerRights.DIAMOND_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 999) {
                        AchievementManager.processFor(AchievementType.DIAMOND_SUPPORT, player);
                    }
                    PlayerUtil.broadcastMessage("<img=749> @yel@Congratulations! " + PlayerUtil.getImages(player) + "" + player.username +" has just redemeed $50.00 member's membership bond!")
                }
            }
        }
        onFirstInventoryAction(15828) {
            if (player.gameMode.isSpawn) {
                player.sendMessage("You cannot use the member's bond in spawn game mode.")
                return@onFirstInventoryAction
            }
            if (player.notInDangerOrAfkOrBusyOrInteracting()){
                player.promptRedeemDialogue(getItem() ?: return@onFirstInventoryAction) {

                    player.message("<img=749> @yel@You have successfully bonded the member's rank on your account! $10.00 balance was added to your account.")
                    player.setInt(Attribute.AMOUNT_PAID, player.getInt(Attribute.AMOUNT_PAID) + 10)
                    player.setInt(Attribute.TIMES_PAID, player.getInt(Attribute.TIMES_PAID) + 1)
                    player.setInt(Attribute.TEN_DOLLAR_BOND, player.getInt(Attribute.TEN_DOLLAR_BOND) + 1)


                    player.packetSender.sendRights()
                    val memberRights = PlayerUtil.getMemberRights(player)
                    player.message("Congratulations, you're now a @dre@"+memberRights.image+" " + memberRights + "</col>.")
                    player.packetSender.sendJinglebitMusic(134, 0)
                    if (player.rights == PlayerRights.RUBY_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 49 || player.attributes.containsKey(Attribute.FREE_RUBY_MEMBER_RANK)) {
                        AchievementManager.processFor(AchievementType.SPREAD_LOVE, player);
                    } else if (player.rights == PlayerRights.TOPAZ_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 99) {
                        AchievementManager.processFor(AchievementType.SUPERIOR_SUPPORT, player);
                    } else if (player.rights == PlayerRights.AMETHYST_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 149) {
                        AchievementManager.processFor(AchievementType.EXTREME_SUPPORT, player);
                    } else if (player.rights == PlayerRights.LEGENDARY_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 249) {
                        AchievementManager.processFor(AchievementType.LEGENDARY_SUPPORT, player);
                    } else if (player.rights == PlayerRights.PLATINUM_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 499) {
                        AchievementManager.processFor(AchievementType.PLATINUM_SUPPORT, player);
                    } else if (player.rights == PlayerRights.TITANIUM_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 749) {
                        AchievementManager.processFor(AchievementType.TITANIUM_SUPPORT, player);
                    } else if (player.rights == PlayerRights.DIAMOND_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 999) {
                        AchievementManager.processFor(AchievementType.DIAMOND_SUPPORT, player);
                    }
                    PlayerUtil.broadcastMessage("<img=749> @yel@Congratulations! " + PlayerUtil.getImages(player) + "" + player.username +" has just redemeed $10.00 member's membership bond!")
                }
            }
        }
        onFirstInventoryAction(15829) {
            if (player.gameMode.isSpawn) {
                player.sendMessage("You cannot use the member's bond in spawn game mode.")
                return@onFirstInventoryAction
            }
            if (player.notInDangerOrAfkOrBusyOrInteracting()){
                player.promptRedeemDialogue(getItem() ?: return@onFirstInventoryAction) {

                    player.message("<img=749> @yel@You have successfully bonded the member's rank on your account! $25.00 balance was added to your account.")
                    player.setInt(Attribute.AMOUNT_PAID, player.getInt(Attribute.AMOUNT_PAID) + 25)
                    player.setInt(Attribute.TIMES_PAID, player.getInt(Attribute.TIMES_PAID) + 1)
                    player.setInt(Attribute.TWENTY_FIVE_DOLLAR_BOND, player.getInt(Attribute.TWENTY_FIVE_DOLLAR_BOND) + 1)


                    player.packetSender.sendRights()
                    val memberRights = PlayerUtil.getMemberRights(player)
                    player.message("Congratulations, you're now a @dre@"+memberRights.image+" " + memberRights + "</col>.")
                    player.packetSender.sendJinglebitMusic(134, 0)
                    if (player.rights == PlayerRights.RUBY_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 49 || player.attributes.containsKey(Attribute.FREE_RUBY_MEMBER_RANK)) {
                        AchievementManager.processFor(AchievementType.SPREAD_LOVE, player);
                    } else if (player.rights == PlayerRights.TOPAZ_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 99) {
                        AchievementManager.processFor(AchievementType.SUPERIOR_SUPPORT, player);
                    } else if (player.rights == PlayerRights.AMETHYST_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 149) {
                        AchievementManager.processFor(AchievementType.EXTREME_SUPPORT, player);
                    } else if (player.rights == PlayerRights.LEGENDARY_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 249) {
                        AchievementManager.processFor(AchievementType.LEGENDARY_SUPPORT, player);
                    } else if (player.rights == PlayerRights.PLATINUM_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 499) {
                        AchievementManager.processFor(AchievementType.PLATINUM_SUPPORT, player);
                    } else if (player.rights == PlayerRights.TITANIUM_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 749) {
                        AchievementManager.processFor(AchievementType.TITANIUM_SUPPORT, player);
                    } else if (player.rights == PlayerRights.DIAMOND_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 999) {
                        AchievementManager.processFor(AchievementType.DIAMOND_SUPPORT, player);
                    }
                    PlayerUtil.broadcastMessage("<img=749> @yel@Congratulations! " + PlayerUtil.getImages(player) + "" + player.username +" has just redemeed $25.00 member's membership bond!")
                }
            }
        }
        onFirstInventoryAction(15830) {
            if (player.gameMode.isSpawn) {
                player.sendMessage("You cannot use the member's bond in spawn game mode.")
                return@onFirstInventoryAction
            }
            if (player.notInDangerOrAfkOrBusyOrInteracting()){
                player.promptRedeemDialogue(getItem() ?: return@onFirstInventoryAction) {

                    player.message("<img=749> @yel@You have successfully bonded the member's rank on your account! $100.00 balance was added to your account.")
                    player.setInt(Attribute.AMOUNT_PAID, player.getInt(Attribute.AMOUNT_PAID) + 100)
                    player.setInt(Attribute.TIMES_PAID, player.getInt(Attribute.TIMES_PAID) + 1)
                    player.setInt(Attribute.HUNDRED_DOLLAR_BOND, player.getInt(Attribute.HUNDRED_DOLLAR_BOND) + 1)


                    player.packetSender.sendRights()
                    val memberRights = PlayerUtil.getMemberRights(player)
                    player.message("Congratulations, you're now a @dre@"+memberRights.image+" " + memberRights + "</col>.")
                    player.packetSender.sendJinglebitMusic(134, 0)
                    if (player.rights == PlayerRights.RUBY_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 49 || player.attributes.containsKey(Attribute.FREE_RUBY_MEMBER_RANK)) {
                        AchievementManager.processFor(AchievementType.SPREAD_LOVE, player);
                    } else if (player.rights == PlayerRights.TOPAZ_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 99) {
                        AchievementManager.processFor(AchievementType.SUPERIOR_SUPPORT, player);
                    } else if (player.rights == PlayerRights.AMETHYST_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 149) {
                        AchievementManager.processFor(AchievementType.EXTREME_SUPPORT, player);
                    } else if (player.rights == PlayerRights.LEGENDARY_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 249) {
                        AchievementManager.processFor(AchievementType.LEGENDARY_SUPPORT, player);
                    } else if (player.rights == PlayerRights.PLATINUM_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 499) {
                        AchievementManager.processFor(AchievementType.PLATINUM_SUPPORT, player);
                    } else if (player.rights == PlayerRights.TITANIUM_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 749) {
                        AchievementManager.processFor(AchievementType.TITANIUM_SUPPORT, player);
                    } else if (player.rights == PlayerRights.DIAMOND_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 999) {
                        AchievementManager.processFor(AchievementType.DIAMOND_SUPPORT, player);
                    }
                    PlayerUtil.broadcastMessage("<img=749> @yel@Congratulations! " + PlayerUtil.getImages(player) + "" + player.username +" has just redemeed $100.00 member's membership bond!")
                }
            }
        }
        onFirstInventoryAction(15831) {
            if (player.gameMode.isSpawn) {
                player.sendMessage("You cannot use the member's bond in spawn game mode.")
                return@onFirstInventoryAction
            }
            if (player.notInDangerOrAfkOrBusyOrInteracting()){
                player.promptRedeemDialogue(getItem() ?: return@onFirstInventoryAction) {

                    player.message("<img=749> @yel@You have successfully bonded the member's rank on your account! $250.00 balance was added to your account.")
                    player.setInt(Attribute.AMOUNT_PAID, player.getInt(Attribute.AMOUNT_PAID) + 250)
                    player.setInt(Attribute.TIMES_PAID, player.getInt(Attribute.TIMES_PAID) + 1)
                    player.setInt(Attribute.TWO_HUNDRED_FIFTY_DOLLAR_BOND, player.getInt(Attribute.TWO_HUNDRED_FIFTY_DOLLAR_BOND) + 1)


                    player.packetSender.sendRights()
                    val memberRights = PlayerUtil.getMemberRights(player)
                    player.message("Congratulations, you're now a @dre@"+memberRights.image+" " + memberRights + "</col>.")
                    player.packetSender.sendJinglebitMusic(134, 0)
                    if (player.rights == PlayerRights.RUBY_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 49 || player.attributes.containsKey(Attribute.FREE_RUBY_MEMBER_RANK)) {
                        AchievementManager.processFor(AchievementType.SPREAD_LOVE, player);
                    } else if (player.rights == PlayerRights.TOPAZ_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 99) {
                        AchievementManager.processFor(AchievementType.SUPERIOR_SUPPORT, player);
                    } else if (player.rights == PlayerRights.AMETHYST_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 149) {
                        AchievementManager.processFor(AchievementType.EXTREME_SUPPORT, player);
                    } else if (player.rights == PlayerRights.LEGENDARY_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 249) {
                        AchievementManager.processFor(AchievementType.LEGENDARY_SUPPORT, player);
                    } else if (player.rights == PlayerRights.PLATINUM_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 499) {
                        AchievementManager.processFor(AchievementType.PLATINUM_SUPPORT, player);
                    } else if (player.rights == PlayerRights.TITANIUM_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 749) {
                        AchievementManager.processFor(AchievementType.TITANIUM_SUPPORT, player);
                    } else if (player.rights == PlayerRights.DIAMOND_MEMBER || player.attributes.numInt(Attribute.AMOUNT_PAID) >= 999) {
                        AchievementManager.processFor(AchievementType.DIAMOND_SUPPORT, player);
                    }
                    PlayerUtil.broadcastMessage("<img=749> @yel@Congratulations! " + PlayerUtil.getImages(player) + "" + player.username +" has just redemeed $250.00 member's membership bond!")
                }
            }
        }
    }
}