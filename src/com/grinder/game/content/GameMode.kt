package com.grinder.game.content

import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.content.miscellaneous.Broadcast
import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces.assign
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerSaving
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Skill
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.item.Item
import com.grinder.game.task.TaskManager
import com.grinder.net.packet.impl.EquipPacketListener
import com.grinder.util.Executable
import com.grinder.util.ItemID
import com.grinder.util.Misc
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * A game mode is a distinct configuration that varies gameplay and affects how
 * other game mechanics behave.
 *
 * @author Blake
 */
enum class GameMode
/**
 * Constructs a new [GameMode].
 *
 * @param crown the icon id associated with this mode.
 * @param items the [items][Item] received when starting in this mode.
 */
    (val crown: Int, val items: Array<Item>?) {

    /*
    * Refer to #GameConstants.java to check the experience for each game mode and post 99 experience.
     */
    ONE_LIFE(
        1235,
        arrayOf<Item>(
            Item(ItemID.GROUP_IRON_HELM),
            Item(ItemID.GROUP_IRON_PLATEBODY),
            Item(ItemID.GROUP_IRON_PLATELEGS),
            Item(ItemID.GROUP_IRON_BRACERS),
            Item(13319),
            Item(15716)
        )
    ), // great drop rates in-game, regular XP, unique set, unique title, one life only.
    REALISM(
        1236,
        arrayOf<Item>(
            Item(ItemID.HARDCORE_GROUP_IRON_HELM),
            Item(ItemID.HARDCORE_GROUP_IRON_PLATEBODY),
            Item(ItemID.HARDCORE_GROUP_IRON_PLATELEGS),
            Item(ItemID.HARDCORE_GROUP_IRON_BRACERS),
            Item(13319),
            Item(15717),
            Item(15718),
            Item(15719),
            Item(ItemID.OGRE_ARROW, 250)
        )
    ), // Slowest XP in game, high drop rate, unique set, title.
    CLASSIC(77, null), // Intermediate drop rates, low xp rates, unique title, in-game features
    NORMAL(-1, null),
    PURE(1238, null), // fastest xp in game, low skilling xp, pure stats
    MASTER(1237, null), // fastest xp in game, low skilling xp, master stats
    SPAWN(1242, arrayOf<Item>(Item(ItemID.ROTTEN_POTATO))), // SPAWN MODE
    IRONMAN(807, arrayOf<Item>(Item(12810), Item(12811), Item(12812))), // Regular XP, unique features
    HARDCORE_IRONMAN(78, arrayOf<Item>(Item(20792), Item(20794), Item(20796))),
    ULTIMATE_IRONMAN(808, arrayOf<Item>(Item(12813), Item(12814), Item(12815)));


    /**
     * Gets if the mode is equal to [GameMode.NORMAL].
     *
     * @return `true` if equal.
     */
    val isNormal: Boolean
        get() = equals(NORMAL)

    /**
     * Gets if the mode is equal to [GameMode.ONE_LIFE].
     *
     * @return `true` if equal.
     */
    val isOneLife: Boolean
        get() = equals(ONE_LIFE)

    /**
     * Gets if the mode is equal to [GameMode.REALISM].
     *
     * @return `true` if equal.
     */
    val isRealism: Boolean
        get() = equals(REALISM)

    /**
     * Gets if the mode is equal to [GameMode.CLASSIC].
     *
     * @return `true` if equal.
     */
    val isClassic: Boolean
        get() = equals(CLASSIC)

    /**
     * Gets if the mode is equal to [GameMode.PURE].
     *
     * @return `true` if equal.
     */
    val isPure: Boolean
        get() = this == PURE

    /**
     * Gets if the mode is equal to [GameMode.PURE] & [GameMode.MASTER].
     *
     * @return `true` if equal.
     */
    val isPureOrMaster: Boolean
        get() = this == PURE || this == MASTER

    /**
     * Gets if the mode is equal to [GameMode.MASTER].
     *
     * @return `true` if equal.
     */
    val isMaster: Boolean
        get() = this == MASTER

    /**
     * Gets if the mode is equal to [GameMode.SPAWN].
     *
     * @return `true` if equal.
     */
    val isSpawn: Boolean
        get() = equals(SPAWN)

    val isAnyIronman: Boolean
        get() = this == IRONMAN || this == HARDCORE_IRONMAN || this == ULTIMATE_IRONMAN

    /**
     * Gets if the mode is any of the Iron Man game modes.
     *
     * @return `true` if the mode is an Iron Man mode.
     */
    val isIronman: Boolean
        get() = equals(IRONMAN)

    /**
     * Gets if the mode is equal to [GameMode.HARDCORE_IRONMAN].
     *
     * @return `true` if equal.
     */
    val isHardcore: Boolean
        get() = equals(HARDCORE_IRONMAN)

    /**
     * Gets if the mode is equal to [GameMode.ULTIMATE_IRONMAN].
     *
     * @return `true` if equal.
     */
    val isUltimate: Boolean
        get() = equals(ULTIMATE_IRONMAN)

    override fun toString(): String {
        return Misc.ucFirst(name.toLowerCase().replace("_".toRegex(), " "))
    }

    companion object {

        /**
         * Melee equipment gear
         */
        private val MELEE_EQUIPMENT = arrayOf(
            intArrayOf(ItemID.BRONZE_FULL_HELM),
            intArrayOf(ItemID.AMULET_OF_POWER),
            intArrayOf(ItemID.WOODEN_SWORD),
            intArrayOf(ItemID.BRONZE_PLATEBODY),
            intArrayOf(ItemID.WOODEN_SHIELD),
            intArrayOf(ItemID.BRONZE_PLATELEGS),
            intArrayOf(ItemID.IRON_GLOVES),
            intArrayOf(ItemID.CLIMBING_BOOTS),
            intArrayOf(ItemID.RING_OF_LIFE),
            intArrayOf(ItemID.BRONZE_ARROW, 500)
        )

        /**
         * Ranged equipment gear
         */
        private val RANGED_EQUIPMENT = arrayOf(
            intArrayOf(ItemID.COIF),
            intArrayOf(ItemID.AMULET_OF_ACCURACY),
            intArrayOf(ItemID.SHORTBOW),
            intArrayOf(ItemID.STUDDED_BODY),
            intArrayOf(ItemID.STUDDED_CHAPS),
            intArrayOf(ItemID.IRON_GLOVES),
            intArrayOf(ItemID.CLIMBING_BOOTS),
            intArrayOf(ItemID.RING_OF_LIFE),
            intArrayOf(ItemID.BRONZE_ARROW, 500)
        )

        /**
         * Magic equipment gear
         */
        private val MAGIC_EQUIPMENT = arrayOf(
            intArrayOf(ItemID.BLUE_HAT),
            intArrayOf(ItemID.AMULET_OF_MAGIC),
            intArrayOf(ItemID.STAFF_OF_WATER),
            intArrayOf(ItemID.BLUE_ROBE_TOP),
            intArrayOf(ItemID.BLUE_ROBE_BOTTOMS),
            intArrayOf(ItemID.ANTI_DRAGON_SHIELD),
            intArrayOf(ItemID.BLUE_HAT),
            intArrayOf(ItemID.TEAL_GLOVES),
            intArrayOf(ItemID.RING_OF_LIFE),
            intArrayOf(ItemID.BLUE_BOOTS)
        )

        /*
        * Random list of capes to give the player so not everyone wears the same thing.
         */
        private val CAPES = intArrayOf(
            ItemID.RED_CAPE,
            ItemID.BLACK_CAPE,
            ItemID.BLUE_CAPE,
            ItemID.ORANGE_CAPE,
            ItemID.GREEN_CAPE
        )

        /**
         * NO EQUIPMENT
         */
        private val NO_EQUIPMENT = arrayOf(
            intArrayOf(-1),
            intArrayOf(-1),
            intArrayOf(-1),
            intArrayOf(-1),
            intArrayOf(-1),
            intArrayOf(-1),
            intArrayOf(-1),
            intArrayOf(-1),
            intArrayOf(-1)
        )


        /**
         * Code to equip items for when confirming the game mode it also finishes some tasks
         * THIS METHOD SHOULD ONLY CALLED WHEN MAKING A NEW PLAYER ACCOUNT.
         */
        private fun equip(player: Player, vararg equipment: IntArray) {
            if (!player.passedTime(
                    Attribute.LAST_ACTION_BUTTON,
                    2,
                    TimeUnit.SECONDS,
                    message = false,
                    updateIfPassed = true
                )
            ) {
                return
            }

            // Equip items
            var addedCape = false
            for (item in equipment) {
                val id = item[0]
                val definition = ItemDefinition.forId(id)
                if (definition == null || id == -1) continue
                val slot = definition.equipmentType.slot
                val quantity = if (item.size == 2) item[1] else 1
                if (slot == 1) {
                    addedCape = true
                }
                player.equipment[slot] = Item(id, quantity)
            }
            if (!addedCape) {
                // Males get a different selection of capes while females only pink cape
                if (player.appearance.isMale) {
                    player.equipment[1] =
                        Item(Misc.randomElement(CAPES), 1)
                } else {
                    player.equipment[1] = Item(ItemID.PINK_CAPE, 1) // Always pink cape for females
                }
            }

            // Refresh
            player.equipment.refreshItems()
            EquipPacketListener.resetWeapon(player)
            assign(player)
            player.combat.reset(false)
            EquipmentBonuses.update(player)

            // Refresh item containers..
            player.inventory.refreshItems()
            player.equipment.refreshItems()
            player.runePouch.refreshItems()
            player.equipment.refreshItems()

            // Update appearance
            player.updateAppearance()

            // If new player send a congrats dialogue, otherwise regular one
                if (player.gameMode.isPure) {

                    DialogueBuilder(DialogueType.OPTION)
                        .setOptionTitle("Choose your stats.")
                        .firstOption("Pure (95 ATK, 95 STR, 1 DEF, 92 MGK, 95 RNG, 87 HP).",
                            Consumer {
                                // Set stats
                                setPureStats(player, 95, 95, 1, 92, 95, 87)
                                // Finish tutorial
                                processSelection(player)
                            })
                        .secondOption("Hybrid (70 ATK, 70 STR, 60 DEF, 70 MGK, 70 RNG, 75 HP).",
                            Consumer {
                                // Set stats
                                setPureStats(player, 70, 70, 60, 70, 70, 75)
                                // Finish tutorial
                                processSelection(player)
                            })
                        .thirdOption("Rusher (60 ATK, 82 STR, 1 DEF, 82 MGK, 75 RNG, 78 HP).",
                            Consumer {
                                // Set stats
                                setPureStats(player, 60, 82, 1, 82, 75, 78)
                                // Finish tutorial
                                processSelection(player)
                            })
                        .fourthOption("Void (42 ATK, 42 STR, 42 DEF, 82 MGK, 75 RNG, 72 HP).",
                            Consumer {
                                // Set stats
                                setPureStats(player, 42, 42, 42, 82, 75, 72)
                                // Finish tutorial
                                processSelection(player)
                            })
                        .fifthOption("None.",
                            Consumer {
                                // Finish tutorial
                                processSelection(player)
                            }).start(player)
                    return
                }

                // If new player is master game mode then set stats
                if (player.gameMode.isMaster) {
                    // Set stats
                    setPureStats(player, 99, 99, 99, 99, 99, 99)
                }
                // New player (non-pure/master) finish tutorial
                processSelection(player)
            }

        private fun setPureStats(player: Player, attack: Int, strength: Int, defence: Int, magic: Int, ranged: Int, hitpoints: Int) {
            player.skillManager.setCurrentLevel(Skill.ATTACK, attack, false)
            player.skillManager.setMaxLevel(Skill.ATTACK, attack, false)
            player.skillManager.setExperience(Skill.ATTACK, SkillUtil.calculateExperienceForLevel(attack))

            player.skillManager.setCurrentLevel(Skill.STRENGTH, strength, false)
            player.skillManager.setMaxLevel(Skill.STRENGTH, strength, false)
            player.skillManager.setExperience(Skill.STRENGTH, SkillUtil.calculateExperienceForLevel(strength))

            player.skillManager.setCurrentLevel(Skill.DEFENCE, defence, false)
            player.skillManager.setMaxLevel(Skill.DEFENCE, defence, false)
            player.skillManager.setExperience(Skill.DEFENCE, SkillUtil.calculateExperienceForLevel(defence))

            player.skillManager.setCurrentLevel(Skill.MAGIC, magic, false)
            player.skillManager.setMaxLevel(Skill.MAGIC, magic, false)
            player.skillManager.setExperience(Skill.MAGIC, SkillUtil.calculateExperienceForLevel(magic))

            player.skillManager.setCurrentLevel(Skill.RANGED, ranged, false)
            player.skillManager.setMaxLevel(Skill.RANGED, ranged, false)
            player.skillManager.setExperience(Skill.RANGED, SkillUtil.calculateExperienceForLevel(ranged))

            player.skillManager.setCurrentLevel(Skill.HITPOINTS, hitpoints, false)
            player.skillManager.setMaxLevel(Skill.HITPOINTS, hitpoints, false)
            player.skillManager.setExperience(Skill.HITPOINTS, SkillUtil.calculateExperienceForLevel(hitpoints))

            player.skillManager.calculateCombatLevel()
        }

        @JvmStatic
        fun processSelection(player: Player) {

            player.packetSender.sendInterfaceRemoval()

            DialogueManager.start(player, 2605)

                // Send jinglebit for finishing the starter guide
                player.packetSender.sendJinglebitMusic(269, 0)

                // Remove tutorial bind
                player.setOnTutorialMode(false)

                // Send random messages to help player get started through the game
                if (!player.gameMode.isSpawn && player.isNewPlayer) {
                    TaskManager.submit(25) {
                        // Add the gem after finishing the achievement so its added last after blood money from participation reward
                        if (player.inventory.countFreeSlots() > 0)
                            player.inventory.add(ItemID.ENCHANTED_GEM, 1)
                        // Send broadcast
                        Broadcast.broadcastSingle(
                            player,
                            30,
                            "Start training the Slayer skill by talking to Turael, west of home area.",
                            ""
                        )
                    }
                }

                // No longer a new player so no more game mode choice..etc
                player.isNewPlayer = false

                // Process achievements
                AchievementManager.processFor(AchievementType.GETTING_READY, player)

                // Remove blocking actions
                player.BLOCK_ALL_BUT_TALKING = false

                // Save player so its registered now, new players are not registered if they logout before
                PlayerSaving.save(player)

                // Send broadcast
                Broadcast.broadcastSingle(
                    player,
                    15,
                    "Make sure to have your Sounds & Music enabled for the great experience!",
                    ""
                )

                if (player.gameMode.isSpawn) {
                    // Send broadcast
                    Broadcast.broadcastSingle(
                        player,
                        120,
                        "Use the rotten potato to spawn & click on skill stats to set skill level!",
                        ""
                    )
                }

                player.sendMessage("@red@Type ::shops for shops, ::train for training, ::home for home teleport.")
            }

        /**
         * Attempts to select a game mode for the [player].
         */
        @JvmStatic
        fun select(player: Player) {

            if (!player.isInTutorial)
                return

            if (!player.isNewPlayer)
                return
            if (!player.passedTime(Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, message = false, updateIfPassed = true)) {
                return
            }

            player.packetSender.sendTabs()
            player.inventory.addItems(player.gameMode.items, false)
            player.inventory.refreshItems()
            //if (player.gameMode.crown != -1) {
            //player.crown = player.gameMode.crown
            player.packetSender.sendRights()
            //}
            // Add starter kit if the player is not any iron man mode
            if (!player.gameMode.isAnyIronman && !player.gameMode.isSpawn) {
                player.inventory.add(ItemID.FREE_TO_PLAY_STARTER_PACK, 1)
                player.inventory.add(ItemID.COINS, 250_000)
            }

            player.inventory.add(22711, 1);
            player.dialogueContinueAction = Executable {
                player.packetSender.sendInterfaceRemoval()
            }
            // Setup guide
            DialogueBuilder(DialogueType.OPTION)
                .setOptionTitle("Choose your setup.")
                .firstOption("Melee Setup.",
                    Consumer {
                        equip(
                            player,
                            *MELEE_EQUIPMENT
                        )
                    })
                .secondOption("Ranged Setup.",
                    Consumer {
                        equip(
                            player,
                            *RANGED_EQUIPMENT
                        )
                    })
                .thirdOption("Magic Setup.",
                    Consumer {
                        equip(
                            player,
                            *MAGIC_EQUIPMENT
                        )
//                        player.inventory.add(ItemID.AIR_RUNE, 250)
//                        player.inventory.add(ItemID.WATER_RUNE, 250)
//                        player.inventory.add(ItemID.EARTH_RUNE, 250)
//                        player.inventory.add(ItemID.FIRE_RUNE, 250)
//                        player.inventory.add(ItemID.BODY_RUNE, 250)
//                        player.inventory.add(ItemID.MIND_RUNE, 250)
//                        player.inventory.add(ItemID.CHAOS_RUNE, 100)
                    })
                .fourthOption("None.") {
                    equip(
                        player,
                        *NO_EQUIPMENT
                    )

                }.start(player)
        }
    }
}