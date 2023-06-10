package com.grinder.game.content.`object`

import com.grinder.Config
import com.grinder.game.definition.ItemValueType
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.entity.agent.player.removeInterfaces
import com.grinder.game.entity.grounditem.ItemOnGround
import com.grinder.game.entity.grounditem.ItemOnGroundManager
import com.grinder.game.model.Animation
import com.grinder.game.model.ItemActions
import com.grinder.game.model.StaffLogRelay
import com.grinder.game.model.StaffLogRelay.save
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.interfaces.dialogue.thirdOption
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.ItemUtil
import com.grinder.game.model.item.name
import com.grinder.game.model.sound.Sounds
import com.grinder.net.codec.database.SQLManager.Companion.INSTANCE
import com.grinder.net.codec.database.impl.staffpanel.DatabaseTableDropLogs
import com.grinder.util.Logging
import com.grinder.util.Misc
import com.grinder.util.ObjectID
import java.util.*

object Tables {

    init {
        for (objectId in validTableObjectIds) {
            ItemActions.onItemOnObjectByObjectId(objectId) {
                val item = getItem()?:return@onItemOnObjectByObjectId true
                val itemDefinition = item.definition
                val itemSlot = getSlot()
                if (!Config.trading_enabled) {
                    player.sendMessage("The @red@[ITEM ON TABLE]</col> system has been switched @red@OFF</col> by the server administrator.")
                    return@onItemOnObjectByObjectId true
                }
                if (player.gameMode.isSpawn) {
                    player.message("You cannot place items on the table in spawn game mode.")
                    return@onItemOnObjectByObjectId true
                }
                if (itemDefinition.isTradeable) {
                    if (player.isShowDropWarning)
                        createWarningDialogue(gameObject, item, itemSlot).start(player)
                    else
                        placeOnTable(player, gameObject, item, itemSlot)
                } else {
                    player.message("You cannot place this item on the table.")
                }
                return@onItemOnObjectByObjectId true
            }
        }
    }

    private fun createWarningDialogue(gameObject: GameObject, item: Item, itemSlot: Int) : DialogueBuilder {
        return DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
            .setItem(item.id, 200)
            .setText(
                "The item you are trying to place is considered @dre@valueable</col>.",
                "Are you absolutely sure you want to place it?"
            )
            .add(DialogueType.OPTION)
            .setOptionTitle("${item.name()}: Really place it?")
            .firstOption("Place it.") { player ->
                placeOnTable(player, gameObject, item, itemSlot)
                player.removeInterfaces()
            }
            .addCancel("No don't place it.")
            .thirdOption("Place it. Disable Warnings for Current Session.") { player ->
                placeOnTable(player, gameObject, item, itemSlot)
                player.isShowDropWarning = false
                player.removeInterfaces()
            }
    }

    private fun placeOnTable(
        player: Player,
        gameObject: GameObject,
        item: Item,
        itemSlot: Int
    ) {
        val itemAtSlot = player.inventory.get(itemSlot)?:return
        if (itemAtSlot.id != item.id || itemAtSlot.amount != item.amount)
            return
        if (itemAtSlot.amount <= 0) {
            return;
        }
        if (!itemAtSlot.isValid) {
            return
        }
        val nearestPosition = gameObject.getNearest(player.position)
        val itemOnGround = ItemOnGround(
            ItemOnGround.State.SEEN_BY_PLAYER,
            Optional.of(player.username),
            nearestPosition,
            item,
            !player.gameMode.isAnyIronman && !player.gameMode.isSpawn,
            -1,
            1000
        )

        player.positionToFace = nearestPosition
        player.performAnimation(Animation(832))
        ItemOnGroundManager.register(itemOnGround)
        player.playSound(Sounds.PICKUP_ITEM)
        player.points.increase(AttributeManager.Points.ITEMS_ON_TABLE, 1) // Increase points
        player.inventory.set(player.inventory.getSlot(item), ItemUtil.createInvalidItem())
        player.inventory.refreshItems()

        player.sendDevelopersMessage("Placing table item: " + item.id + ".")

        val itemName = item.name()
        val itemAmount = item.amount
        val itemValue = item.getValue(ItemValueType.PRICE_CHECKER)
        log(player, item, itemName, itemAmount, itemValue)
    }

    private fun log(player: Player, item: Item, itemName: String, itemAmount: Int, itemValue: Long) {
        player.sendDevelopersMessage("Placing table item: " + item.id + ".")
        if (itemValue * itemAmount > 2000000) {
            Logging.log(
                "itemtabledrops",
                "" + player.username + " placed: " + Misc.insertCommasToNumber(itemAmount) + " x " + itemName + ""
            )
            DatabaseTableDropLogs(
                INSTANCE,
                player.username,
                itemName,
                itemAmount,
                if (player.area != null) player.area.toString() else " None"
            ).schedule(player)
        }
        if (itemValue > 100_000 || itemAmount > 500 || ItemUtil.isHighValuedItem(itemName)) save(
            StaffLogRelay.StaffLogType.DROP,
            player.username,
            item
        )
    }
}

/**
 * Contains all object ids that items may be placed upon.
 *
 */
private val validTableObjectIds = intArrayOf(
    ObjectID.BANK_TABLE,
    ObjectID.BANK_TABLE_2,
    ObjectID.BATHROOM_TABLE,
    ObjectID.TABLE_1,
    ObjectID.TABLE_2,
    ObjectID.TABLE_3,
    ObjectID.TABLE_4,
    ObjectID.TABLE_5,
    ObjectID.TABLE_6,
    ObjectID.TABLE_7,
    ObjectID.TABLE_8,
    ObjectID.TABLE_9,
    ObjectID.TABLE_10,
    ObjectID.TABLE_11,
    ObjectID.TABLE_12,
    ObjectID.TABLE_13,
    ObjectID.TABLE_14,
    ObjectID.TABLE_15,
    ObjectID.TABLE_16,
    ObjectID.TABLE_17,
    ObjectID.SMALL_TABLE,
    ObjectID.TABLE_18,
    ObjectID.CHEMISTRY_TABLE,
    ObjectID.CHEMISTRY_TABLE_2,
    ObjectID.TABLE_19,
    ObjectID.TABLE_20,
    ObjectID.TABLE_21,
    ObjectID.TABLE_22,
    ObjectID.TABLE_23,
    ObjectID.TABLE_24,
    ObjectID.BANK_TABLE_3,
    ObjectID.TABLE_25,
    ObjectID.SMASHED_TABLE,
    ObjectID.TABLE_26,
    ObjectID.TABLE_27,
    ObjectID.TABLE_28,
    ObjectID.TABLE_29,
    ObjectID.TABLE_30,
    ObjectID.TABLE_31,
    ObjectID.TABLE_32,
    ObjectID.TABLE_33,
    ObjectID.TABLE_34,
    ObjectID.TABLE_35,
    ObjectID.TABLE_36,
    ObjectID.TABLE_37,
    ObjectID.TABLE_38,
    ObjectID.OPERATING_TABLE,
    ObjectID.OPERATING_TABLE_2,
    ObjectID.STONE_TABLET,
    ObjectID.STONE_TABLET_2,
    ObjectID.STONE_TABLET_3,
    ObjectID.STONE_TABLET_4,
    ObjectID.STONE_TABLET_5,
    ObjectID.STONE_TABLET_6,
    ObjectID.STONE_TABLET_7,
    ObjectID.STONE_TABLET_8,
    ObjectID.SMASHED_TABLE_2,
    ObjectID.FROZEN_TABLE,
    ObjectID.WOODEN_TABLE,
    ObjectID.WOODEN_TABLE_2,
    ObjectID.TABLE_39,
    ObjectID.TABLE_40,
    ObjectID.TABLE_41,
    ObjectID.TABLE_42,
    ObjectID.TABLE_43,
    ObjectID.TABLE_44,
    ObjectID.TABLE_45,
    ObjectID.TABLE_46,
    ObjectID.TABLE_47,
    ObjectID.TABLE_48,
    ObjectID.TABLE_49,
    ObjectID.TABLE_50,
    ObjectID.TABLE_51,
    ObjectID.CAPTAINS_TABLE,
    ObjectID.NEDS_TABLE,
    ObjectID.WOODEN_TABLE_3,
    ObjectID.TABLE_52,
    ObjectID.TABLE_53,
    ObjectID.BANK_TABLE_4,
    ObjectID.BANK_TABLE_5,
    ObjectID.TABLE_54,
    ObjectID.TABLE_55,
    ObjectID.TABLE_56,
    ObjectID.TABLE_57,
    ObjectID.TABLE_58,
    ObjectID.TABLE_59,
    ObjectID.DWARVEN_CONSORTIUM_TABLE,
    ObjectID.TABLE_60,
    ObjectID.TABLE_61,
    ObjectID.TABLE_62,
    ObjectID.CEREMONIAL_TABLE,
    ObjectID.TABLE_63,
    ObjectID.TABLE_64,
    ObjectID.TABLE_65,
    ObjectID.TABLE_66,
    ObjectID.TABLE_67,
    ObjectID.TABLE_68,
    ObjectID.TEAK_TABLE,
    ObjectID.TEAK_TABLE_2,
    ObjectID.TABLE_69,
    ObjectID.TABLE_70,
    ObjectID.TABLE_71,
    ObjectID.TABLE,
    ObjectID.SMASHED_TABLE_3,
    ObjectID.TABLE_72,
    ObjectID.TABLE_73,
    ObjectID.TABLE_74,
    ObjectID.TABLE_75,
    ObjectID.TABLE_76,
    ObjectID.TABLE_77,
    ObjectID.TABLE_78,
    ObjectID.TABLE_79,
    ObjectID.TABLE_80,
    ObjectID.TABLE_81,
    ObjectID.TABLE_82,
    ObjectID.LARGE_TABLE,
    ObjectID.TABLE_83,
    ObjectID.SMALL_TABLE_2,
    ObjectID.TABLE_84,
    ObjectID.LARGE_TABLE_2,
    ObjectID.LARGE_TABLE_3,
    ObjectID.LARGE_TABLE_4,
    ObjectID.LARGE_TABLE_5,
    ObjectID.TABLE_85,
    ObjectID.TABLE_86,
    ObjectID.TABLE_87,
    ObjectID.TABLE_88,
    ObjectID.TABLE_89,
    ObjectID.TABLE_90,
    ObjectID.TABLE_91,
    ObjectID.TABLE_92,
    ObjectID.LARGE_TABLE_6,
    ObjectID.TABLE_93,
    ObjectID.TABLE_94,
    ObjectID.TABLE_95,
    ObjectID.TABLE_96,
    ObjectID.TABLE_97,
    ObjectID.TABLE_98,
    ObjectID.TABLE_99,
    ObjectID.SMALL_TABLE_3,
    ObjectID.TABLE_100,
    ObjectID.TABLE_101,
    ObjectID.TABLE_102,
    ObjectID.LARGE_TABLE_7,
    ObjectID.SMALL_TABLE_4,
    ObjectID.BANQUET_TABLE,
    ObjectID.BANQUET_TABLE_2,
    ObjectID.BANQUET_TABLE_3,
    ObjectID.BANQUET_TABLE_4,
    ObjectID.TABLE_103,
    ObjectID.TABLE_104,
    ObjectID.TABLE_105,
    ObjectID.TABLE_106,
    ObjectID.TABLE_107,
    ObjectID.LARGE_TABLE_8,
    ObjectID.TABLE_108,
    ObjectID.TABLE_109,
    ObjectID.TABLE_110,
    ObjectID.TABLE_111,
    ObjectID.TABLE_112,
    ObjectID.SMALL_TABLE_5,
    ObjectID.TABLE_113,
    ObjectID.TABLE_114,
    ObjectID.TABLE_115,
    ObjectID.WOODEN_TABLE_4,
    ObjectID.OAK_TABLE,
    ObjectID.OAK_TABLE_2,
    ObjectID.TEAK_TABLE_3,
    ObjectID.TEAK_TABLE_4,
    ObjectID.MAHOGANY_TABLE,
    ObjectID.OPULENT_TABLE,
    ObjectID.TABLE_BLOCKADE,
    ObjectID.TABLE_BLOCKADE_2,
    ObjectID.WOODEN_TABLE_5,
    ObjectID.OAK_TABLE_3,
    ObjectID.TEAK_TABLE_5,
    ObjectID.BONZOS_TABLE,
    ObjectID.BONZOS_TABLE_2,
    ObjectID.TABLE_116,
    ObjectID.TABLE_117,
    ObjectID.SMALL_TABLE_6,
    ObjectID.TABLE_118,
    ObjectID.TABLE_119,
    ObjectID.TABLE_120,
    ObjectID.TABLE_121,
    ObjectID.TABLE_122,
    ObjectID.TABLE_123,
    ObjectID.PICNIC_TABLE,
    ObjectID.TABLE_124,
    ObjectID.TABLE_125,
    ObjectID.TABLE_126,
    ObjectID.TABLE_SPACE,
    ObjectID.TABLE_SPACE_2,
    ObjectID.MAHOGANY_TABLE_2,
    ObjectID.SMALL_TABLE_7,
    ObjectID.BANK_TABLE_6,
    ObjectID.TABLE_127,
    ObjectID.TABLE_128,
    ObjectID.TABLE_129,
    ObjectID.TABLE_130,
    ObjectID.TABLE_131,
    ObjectID.TABLE_132,
    ObjectID.MEDICAL_TABLE,
    ObjectID.MAP_TABLE,
    ObjectID.TABLE_133,
    ObjectID.TABLE_134,
    ObjectID.TABLE_135,
    ObjectID.TABLE_136,
    ObjectID.TABLE_137,
    ObjectID.TABLE_138,
    ObjectID.TABLE_139,
    ObjectID.TABLE_140,
    ObjectID.TABLE_141,
    ObjectID.TABLE_142,
    ObjectID.WOODEN_TABLE_6,
    ObjectID.TABLE_143,
    ObjectID.GNOME_TABLE,
    ObjectID.GNOME_TABLE_2,
    ObjectID.GNOME_TABLE_3,
    ObjectID.GNOME_TABLE_4,
    ObjectID.OFFICE_TABLE,
    ObjectID.TABLE_144,
    ObjectID.TABLE_145,
    ObjectID.TABLE_146,
    ObjectID.TABLE_147,
    ObjectID.STONE_TABLET_9,
    ObjectID.STONE_TABLE,
    ObjectID.STONE_TABLE_2,
    ObjectID.STONE_TABLE_3,
    ObjectID.STONE_TABLE_4,
    ObjectID.TABLE_148,
    ObjectID.TABLE_149,
    ObjectID.TABLE_150,
    ObjectID.TABLE_151,
    ObjectID.TABLE_152,
    ObjectID.TABLE_153,
    ObjectID.LAB_TABLE,
    ObjectID.LAB_TABLE_2,
    ObjectID.LAB_TABLE_3,
    ObjectID.LAB_TABLE_4,
    ObjectID.CORNER_TABLE,
    ObjectID.CUTTING_TABLE,
    ObjectID.TABLE_AND_TILL,
    ObjectID.TABLE_154,
    ObjectID.TABLE_155,
    ObjectID.TRAPDOOR_TABLE,
    ObjectID.TABLE_156,
    ObjectID.TRAPDOOR_TABLE_2,
    ObjectID.TRAPDOOR_TABLE_3,
    ObjectID.LARGE_TABLE_9,
    ObjectID.DISPLAY_TABLE,
    ObjectID.DISPLAY_TABLE_2,
    ObjectID.TABLE_157,
    ObjectID.TABLE_158,
    ObjectID.TABLE_159,
    ObjectID.TABLE_160,
    ObjectID.TABLE_161,
    ObjectID.SCROLL_TABLE,
    ObjectID.BANK_TABLE_7,
    ObjectID.BANK_TABLE_8,
    ObjectID.TABLE_162,
    ObjectID.CLOTH_COVERED_TABLE,
    ObjectID.TABLE_163,
    ObjectID.BANK_TABLE_9,
    ObjectID.TABLE_164,
    ObjectID.TABLE_165,
    ObjectID.TABLE_166,
    ObjectID.TABLE_167,
    ObjectID.CHOPPING_TABLE,
    ObjectID.ORE_TABLE,
    ObjectID.TABLE_168,
    ObjectID.TABLE_169,
    ObjectID.TABLE_170,
    ObjectID.TABLE_171,
    ObjectID.OPERATION_TABLE,
    ObjectID.MAGIC_TABLE,
    ObjectID.SHACKLE_TABLE,
    ObjectID.EXPERIMENT_TABLE,
    ObjectID.EXPERIMENT_TABLE_2,
    ObjectID.EXPERIMENT_TABLE_3,
    ObjectID.TABLE_172,
    ObjectID.TABLE_173,
    ObjectID.TABLE_174,
    ObjectID.TABLE_175,
    ObjectID.TABLE_176,
    ObjectID.TABLE_177,
    ObjectID.TABLE_178,
    ObjectID.TABLE_179,
    ObjectID.TABLE_180,
    ObjectID.TABLE_181,
    ObjectID.KITCHEN_TABLE,
    ObjectID.TOY_TABLE,
    ObjectID.TABLE_182,
    ObjectID.TABLE_183,
    ObjectID.TABLE_184,
    ObjectID.TABLE_185,
    ObjectID.TABLE_186,
    ObjectID.TABLE_187,
    ObjectID.TABLE_188,
    ObjectID.TABLE_189,
    ObjectID.TABLE_190,
    ObjectID.SMALL_TABLE_8,
    ObjectID.TABLE_191,
    ObjectID.SPECIMEN_TABLE,
    ObjectID.SAMPLE_TABLE,
    ObjectID.SAMPLE_TABLE_2,
    ObjectID.TABLE_192,
    ObjectID.TABLE_193,
    ObjectID.LARGE_TABLE_10,
    ObjectID.TABLE_194,
    ObjectID.TABLE_195,
    ObjectID.TABLE_196,
    ObjectID.PICNIC_TABLE_2,
    ObjectID.TABLE_197,
    ObjectID.TABLE_198,
    ObjectID.TABLE_199,
    ObjectID.TABLE_200,
    ObjectID.SPECIMEN_TABLE_2,
    ObjectID.SPECIMEN_TABLE_3,
    ObjectID.TABLE_201,
    ObjectID.TABLE_202,
    ObjectID.TABLE_203,
    ObjectID.BARBARIAN_TABLE,
    ObjectID.PROFESSORS_TABLE,
    ObjectID.TABLE_204,
    ObjectID.TABLE_205,
    ObjectID.TABLE_206,
    ObjectID.TABLE_207,
    ObjectID.TABLE_208,
    ObjectID.TABLE_209,
    ObjectID.TABLE_210,
    ObjectID.TABLE_211,
    ObjectID.TABLE_212,
    ObjectID.TABLE_213,
    ObjectID.TABLE_214,
    ObjectID.TABLE_215,
    ObjectID.TABLE_216,
    ObjectID.TABLE_217,
    ObjectID.TABLE_218,
    ObjectID.TABLE_219,
    ObjectID.TABLE_220,
    ObjectID.TABLE_221,
    ObjectID.IVORY_TABLE,
    ObjectID.IVORY_TABLE_2,
    ObjectID.IVORY_TABLE_3,
    ObjectID.IVORY_TABLE_4,
    ObjectID.IVORY_TABLE_5,
    ObjectID.IVORY_TABLE_6,
    ObjectID.IVORY_TABLE_7,
    ObjectID.IVORY_TABLE_8,
    ObjectID.IVORY_TABLE_9,
    ObjectID.IVORY_TABLE_10,
    ObjectID.IVORY_TABLE_11,
    ObjectID.IVORY_TABLE_12,
    ObjectID.TABLE_222,
    ObjectID.TABLE_223,
    ObjectID.TABLE_224,
    ObjectID.TABLE_225,
    ObjectID.MEAT_TABLE,
    ObjectID.BUFFET_TABLE,
    ObjectID.SEED_TABLE,
    ObjectID.ROUND_TABLE,
    ObjectID.TABLE_226,
    ObjectID.TABLE_227,
    ObjectID.TABLE_228,
    ObjectID.TABLE_229,
    ObjectID.TABLE_230,
    ObjectID.TABLE_231,
    ObjectID.TABLE_232,
    ObjectID.TABLE_233,
    ObjectID.BROKEN_TABLE,
    ObjectID.TABLE_234,
    ObjectID.TABLE_235,
    ObjectID.TABLE_236,
    ObjectID.TABLE_237,
    ObjectID.TABLE_238,
    ObjectID.TABLE_239,
    ObjectID.TABLE_240,
    ObjectID.TABLE_241,
    ObjectID.TABLE_242,
    ObjectID.TABLE_243,
    ObjectID.TABLE_244,
    ObjectID.TABLE_245,
    ObjectID.TABLE_246,
    ObjectID.TABLE_247,
    ObjectID.TABLE_248,
    ObjectID.TABLE_249,
    ObjectID.TABLE_250,
    ObjectID.TABLE_251,
    ObjectID.TABLE_252,
    ObjectID.TABLE_253,
    ObjectID.NECROMANCERS_TABLE,
    ObjectID.NECROMANCERS_TABLE_2,
    ObjectID.NECROMANCERS_TABLE_3,
    ObjectID.GLASS_TABLE,
    ObjectID.TABLE_254,
    ObjectID.WORK_TABLE,
    ObjectID.SMALL_TABLE_9,
    ObjectID.WHEELY_TABLE,
    ObjectID.GNOME_TABLE_5,
    ObjectID.TABLE_255,
    ObjectID.TABLE_256,
    ObjectID.TABLE_257,
    ObjectID.BANQUET_TABLE_5,
    ObjectID.PRESENT_TABLE,
    ObjectID.BIRTHDAY_HAT_TABLE,
    ObjectID.LARGE_TABLE_11,
    ObjectID.TABLE_258
)