package com.grinder.game.content.item.transforming

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.interfaces.dialogue.DialogueManager
import com.grinder.game.model.interfaces.dialogue.impl.ConfirmDialogue
import com.grinder.game.model.item.Item
import com.grinder.net.packet.PacketConstants
import com.grinder.util.ItemID
import com.grinder.util.Misc

/**
 * TODO: add documentation
 *
 * @param opCode        the opcode of the transformation option.
 * @param product       the base [Item] product.
 * @param type          the [TransformType].
 * @param ingredients   the ingredients of this item.
 */
internal enum class Transformable(
        val opCode: Int,
        val product: Item,
        val type: TransformType,
        vararg val ingredients: Item
) {

    MAGIC_FANG(PacketConstants.SECOND_ITEM_ACTION_OPCODE, Item(12932), TransformType.DISMANTLE, Item(ItemID.ZULRAHS_SCALES, 20_000)),
    NEITZNOT_FACEGUARD(PacketConstants.SECOND_ITEM_ACTION_OPCODE, Item(24271), TransformType.DISMANTLE, Item(10828), Item(24268)),

    UNCHARGED_TOXIC_TRIDENT(PacketConstants.SECOND_ITEM_ACTION_OPCODE, Item(ItemID.UNCHARGED_TOXIC_TRIDENT), TransformType.DISMANTLE, Item(ItemID.MAGIC_FANG), Item(ItemID.UNCHARGED_TRIDENT)),
    UNCHARGED_TOXIC_TRIDENT_E(PacketConstants.SECOND_ITEM_ACTION_OPCODE, Item(22294), TransformType.DISMANTLE, Item(ItemID.MAGIC_FANG), Item(22290)),
    UNCHARGED_TOXIC_STAFF_OF_THE_DEAD(PacketConstants.SECOND_ITEM_ACTION_OPCODE, Item(ItemID.TOXIC_STAFF_UNCHARGED_), TransformType.DISMANTLE, Item(ItemID.MAGIC_FANG), Item(ItemID.STAFF_OF_THE_DEAD)),

    // Nightmare staffs dismantling
    ELDRICH_NIGHTMARE_STAFF(PacketConstants.SECOND_ITEM_ACTION_OPCODE, Item(24425), TransformType.DISMANTLE, Item(24517), Item(24422)),
    VOLATILE_NIGHTMARE_STAFF(PacketConstants.SECOND_ITEM_ACTION_OPCODE, Item(24424), TransformType.DISMANTLE, Item(24514), Item(24422)),
    HARMONISED_NIGHTMARE_STAFF(PacketConstants.SECOND_ITEM_ACTION_OPCODE, Item(24423), TransformType.DISMANTLE, Item(24511), Item(24422)),

    // Bandosian Components
    BANDOS_CHEST(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.BANDOS_CHESTPLATE), TransformType.BREAKDOWN, Item(ItemID.BANDOSIAN_COMPONENTS, 3)),
    BANDOS_TASS(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.BANDOS_TASSETS), TransformType.BREAKDOWN, Item(ItemID.BANDOSIAN_COMPONENTS, 2)),

    BANDOS_CHEST_OR(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.BANDOS_CHESTPLATE_OR), TransformType.BREAKDOWN, Item(ItemID.BANDOSIAN_COMPONENTS, 3)),
    BANDOS_TASS_OR(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.BANDOS_TASSETS_OR), TransformType.BREAKDOWN, Item(ItemID.BANDOSIAN_COMPONENTS, 2)),

    // Armadyl Components
    ARMADYL_HELM(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.ARMADYL_HELMET), TransformType.BREAKDOWN, Item(15898, 2)),
    ARMADYL_CHESTPLATE(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.ARMADYL_CHESTPLATE), TransformType.BREAKDOWN, Item(15898, 3)),
    ARMADYL_CHAINSKIRT(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.ARMADYL_CHAINSKIRT), TransformType.BREAKDOWN, Item(15898, 3)),

    ARMADYL_HELM_OR(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.ARMADYL_HELMET_OR), TransformType.BREAKDOWN, Item(15898, 2)),
    ARMADYL_CHESTPLATE_OR(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.ARMADYL_CHESTPLATE_OR), TransformType.BREAKDOWN, Item(15898, 3)),
    ARMADYL_CHAINSKIRT_OR(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.ARMADYL_CHAINSKIRT_OR), TransformType.BREAKDOWN, Item(15898, 3)),

    // Magical Components
    ANCESTRAL_HAT(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.ANCESTRAL_HAT), TransformType.BREAKDOWN, Item(15899, 2)),
    ANCESTRAL_ROBE_TOP(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.ANCESTRAL_ROBE_TOP), TransformType.BREAKDOWN, Item(15899, 5)),
    ANCESTRAL_ROBE_BOTTOM(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.ANCESTRAL_ROBE_BOTTOM), TransformType.BREAKDOWN, Item(15899, 3)),


    AMULET_OF_FURY(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.AMULET_OF_FURY), TransformType.BREAKDOWN, Item(ItemID.BLOOD_SHARD, 1000)),

    // Torva
    TORVA_FULL_HELM(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.TORVA_FULL_HELM), TransformType.DAMAGE, Item(ItemID.TORVA_FULL_HELM_DAMAGED, 1)),
    TORVA_PLATEBODY(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.TORVA_PLATEBODY), TransformType.DAMAGE, Item(ItemID.TORVA_PLATEBODY_DAMAGED, 1)),
    TORVA_PLATELEGS(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(ItemID.TORVA_PLATELEGS), TransformType.DAMAGE, Item(ItemID.TORVA_PLATELEGS_DAMAGED, 1)),

    // Pernix
    PERNIX_COWL(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(15883), TransformType.DAMAGE, Item(15895, 1)),
    PERNIX_BODY(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(15885), TransformType.DAMAGE, Item(15896, 1)),
    PERNIX_CHAPS(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(15887), TransformType.DAMAGE, Item(15897, 1)),

    // Virtus
    VIRTUS_MASK(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(15877), TransformType.DAMAGE, Item(15892, 1)),
    VIRTUS_TOP(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(15879), TransformType.DAMAGE, Item(15893, 1)),
    VIRTUS_ROBE_BOTTOMS(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(15881), TransformType.DAMAGE, Item(15894, 1)),

    AMULET_OF_BLOOD_FURY(PacketConstants.DROP_ITEM_OPCODE, Item(ItemID.AMULET_OF_BLOOD_FURY), TransformType.REVERT, Item(ItemID.AMULET_OF_FURY)),
    AMULET_OF_FURY_OR(PacketConstants.DROP_ITEM_OPCODE, Item(12436), TransformType.DISMANTLE, Item(6585), Item(12526)),
    DRAGON_FULL_HELM(PacketConstants.DROP_ITEM_OPCODE, Item(12417), TransformType.DISMANTLE, Item(11335), Item(12538)),
    DRAGON_PLATELEGS(PacketConstants.DROP_ITEM_OPCODE, Item(12415), TransformType.DISMANTLE, Item(4087), Item(12536)),
    DRAGON_PLATESKIRT(PacketConstants.DROP_ITEM_OPCODE, Item(12416), TransformType.DISMANTLE, Item(4585), Item(12536)),
    DRAGON_CHAINBODY(PacketConstants.DROP_ITEM_OPCODE, Item(12414), TransformType.DISMANTLE, Item(3140), Item(12534)),
    DRAGON_SQ_SHIELD(PacketConstants.DROP_ITEM_OPCODE, Item(12418), TransformType.DISMANTLE, Item(1187), Item(12532)),
    DRAGON_SCIMITAR_OR(PacketConstants.DROP_ITEM_OPCODE, Item(20000), TransformType.DISMANTLE, Item(4587), Item(20002)),
    DRAGON_PLATEBODY_G(PacketConstants.DROP_ITEM_OPCODE, Item(22242), TransformType.DISMANTLE, Item(21892), Item(22236)),
    DRAGON_BOOTS_G(PacketConstants.DROP_ITEM_OPCODE, Item(22234), TransformType.DISMANTLE, Item(11840), Item(22231)),
    DRAGON_DEFENDER_T(PacketConstants.DROP_ITEM_OPCODE, Item(19722), TransformType.DISMANTLE, Item(12954), Item(20143)),
    ODIUM_WARD_OR(PacketConstants.DROP_ITEM_OPCODE, Item(12807), TransformType.REVERT, Item(11926)),
    MALEDICTION_WARD_OR(PacketConstants.DROP_ITEM_OPCODE, Item(12806), TransformType.REVERT, Item(11924)),

    ABYSSAL_TENTACLE(PacketConstants.DROP_ITEM_OPCODE, Item(12006), TransformType.DISSOLVE, Item(12004)),

    ANCIENT_GODSWORD(PacketConstants.THIRD_ITEM_ACTION_OPCODE, Item(26233), TransformType.DISMANTLE, Item(11798), Item(26370)),

    BANDOS_GODSWORD_OR(PacketConstants.DROP_ITEM_OPCODE, Item(20370), TransformType.DISMANTLE, Item(11804), Item(20071)),
    ARMADYL_GODSWORD_OR(PacketConstants.DROP_ITEM_OPCODE, Item(20368), TransformType.DISMANTLE, Item(11802), Item(20068)),
    SARADOMIN_GODSWORD_OR(PacketConstants.DROP_ITEM_OPCODE, Item(20372), TransformType.DISMANTLE, Item(11806), Item(20074)),
    ZAMORAK_GODSWORD_OR(PacketConstants.DROP_ITEM_OPCODE, Item(20374), TransformType.DISMANTLE, Item(11808), Item(20077)),


    RUNE_SCIMITAR_SARADOMIN(PacketConstants.DROP_ITEM_OPCODE, Item(23332), TransformType.DISMANTLE, Item(1333), Item(23324)),
    RUNE_SCIMITAR_GUTHIX(PacketConstants.DROP_ITEM_OPCODE, Item(23330), TransformType.DISMANTLE, Item(1333), Item(23321)),
    RUNE_SCIMITAR_ZAMORAK(PacketConstants.DROP_ITEM_OPCODE, Item(23334), TransformType.DISMANTLE, Item(1333), Item(23327)),

    AMULET_OF_TORTURE_OR(PacketConstants.DROP_ITEM_OPCODE, Item(20366), TransformType.DISMANTLE, Item(19553), Item(20062)),
    OCCULT_NECKLACE_OR(PacketConstants.DROP_ITEM_OPCODE, Item(19720), TransformType.DISMANTLE, Item(12002), Item(20065)),
    GRANITE_MAUL_OR(PacketConstants.DROP_ITEM_OPCODE, Item(12848), TransformType.REVERT, Item(4153)),
    GRANITE_MAUL_ORANTE(PacketConstants.DROP_ITEM_OPCODE, Item(24225), TransformType.REVERT, Item(4153)),
    STEAM_BATTLESTAFF_OR(PacketConstants.DROP_ITEM_OPCODE, Item(12795), TransformType.REVERT, Item(11787)),
    MYSTIC_STEAM_BATTLESTAFF_OR(PacketConstants.DROP_ITEM_OPCODE, Item(12796), TransformType.REVERT, Item(11789)),
    NECKLACE_OF_ANGUISH_OR(PacketConstants.DROP_ITEM_OPCODE, Item(22249), TransformType.DISMANTLE, Item(19547), Item(22246)),
    BERSERKER_NECKLACE_OR(PacketConstants.DROP_ITEM_OPCODE, Item(23240), TransformType.DISMANTLE, Item(11128), Item(23237)),
    TORMENTED_BRACELET_OR(PacketConstants.DROP_ITEM_OPCODE, Item(23444), TransformType.DISMANTLE, Item(19544), Item(23348)),

    SLAYER_STAFF_ENCHANTED(PacketConstants.DROP_ITEM_OPCODE, Item(ItemID.SLAYERS_STAFF_E_), TransformType.REVERT, Item(ItemID.SLAYERS_STAFF)),
   /* CRAW_BOW(PacketConstants.DROP_ITEM_OPCODE, Item(22550), TransformType.REVERT, Item(22547)),
    VIGGORA_CHAINMACE(PacketConstants.DROP_ITEM_OPCODE, Item(22545), TransformType.REVERT, Item(22542)),
    THAMMARON_SCEPTRE(PacketConstants.DROP_ITEM_OPCODE, Item(22555), TransformType.REVERT, Item(22552)),
    ETHER_BRACELET(PacketConstants.DROP_ITEM_OPCODE, Item(21816), TransformType.REVERT, Item(21817)),*/



    SERPENTINE_HELM(PacketConstants.SECOND_ITEM_ACTION_OPCODE,
            Item(12929), TransformType.DISMANTLE, Item(12927)),
    SERPENTINE_VISAGE(PacketConstants.SECOND_ITEM_ACTION_OPCODE,
            Item(12927), TransformType.SERPENTINE_VISAGE, Item(12934, 20000)),
    DRAGON_PICKAXE(PacketConstants.DROP_ITEM_OPCODE,
            Item(12797), TransformType.REVERT, Item(11920)),
    TANZANITE_HELM(PacketConstants.SECOND_ITEM_ACTION_OPCODE,
            Item(13197), TransformType.RESTORE, Item(12929)),
    TANZANITE_HELM_UNCHARGED(PacketConstants.SECOND_ITEM_ACTION_OPCODE,
            Item(13196), TransformType.RESTORE, Item(12929)),
    MAGMA_HELM(PacketConstants.SECOND_ITEM_ACTION_OPCODE,
            Item(13199), TransformType.RESTORE, Item(12929)),
    MAGMA_HELM_UNCHARGED(PacketConstants.SECOND_ITEM_ACTION_OPCODE,
            Item(13198), TransformType.RESTORE, Item(12929)),
    BONECRUSHER_NECKLACE(PacketConstants.DROP_ITEM_OPCODE,
            Item(ItemID.BONECRUSHER_NECKALCE),
            TransformType.DISMANTLE,
            Item(ItemID.BONECRUSHER),
            Item(ItemID.DRAGONBONE_NECKLACE),
            Item(ItemID.HYDRA_TAIL));

    override fun toString(): String {
        return Misc.ucFirst(name.toLowerCase().replace("_".toRegex(), " "))
    }

    /**
     * Starts & handles the item transformation dialogue.
     *
     * @param player The player.
     */
    fun start(player: Player) {
        var msg = arrayOf("Are you sure you want to " + type.toString() + " your @dre@" + product.definition.name + "@bla@?")
        if (type == TransformType.REVERT || type == TransformType.RESTORE) {
            msg = arrayOf("Are you sure you want to " + type.toString() + " your @dre@" + product.definition.name + "@bla@?", "This action is irreversible.")
        } else if (type == TransformType.DISSOLVE ) {
            msg = arrayOf("This will dissolve your whip and destroy it.", "You won't be able to get the whip back again.", "You will remain with a Kraken tentacle.</col>.")
        } else if (type == TransformType.SERPENTINE_VISAGE ) {
            msg = arrayOf("This will dismantle the visage into 20,000 Zulrah scales.", "You won't be able to get the visage back again.")
        } else if (type == TransformType.BREAKDOWN ) {
            msg = arrayOf("This will breakdown ${product.definition.name} into components.",
                "You won't be able to get the $type back again."
            )
        } else if (type == TransformType.DAMAGE ) {
            msg = arrayOf("This will revert ${product.definition.name} into damaged state.",
                "You won't be able to undo this action."
            )
        } else if (type == TransformType.DISMANTLE && product.id == ItemID.MAGIC_FANG) {
                msg = arrayOf("This will dismantle the fang into 20,000 Zulrah scales.", "You won't be able to get the fang back again.")
        }
        DialogueManager.start(player, object : ConfirmDialogue(player, msg, arrayOf("Confirm.", "Cancel.")) {
            override fun onConfirm() {
                if (!player.inventory.contains(product)) {
                    return
                }
                if (player.inventory.countFreeSlots() < ingredients.size - 1) {
                    DialogueManager.sendStatement(player, "You do not have enough space in inventory to continue.")
                    return
                }
                player.inventory.delete(product)
                player.inventory.addItems(ingredients, true)
                when {
                    type.toString().toLowerCase() == "revert" -> {
                        player.sendMessage("You have reverted your @dre@" + product.definition.name + "@bla@!")
                    }
                    type.toString().toLowerCase() == "restore" -> {
                        player.sendMessage("You have restored your @dre@" + product.definition.name + "</col> back to its original state!")
                    }
                    type.toString().toLowerCase() == "breakdown" -> {
                        player.sendMessage("You have broken down your @dre@" + product.definition.name + "</col> into components!")
                    }
                    type.toString().toLowerCase() == "damage" -> {
                        player.sendMessage("You have restored your @dre@" + product.definition.name + "</col> back to its damaged state!")
                    }
                    else -> {
                        player.sendMessage("You have " + type.toString() + "d your @dre@" + product.definition.name + "</col>!")
                    }
                }
            }
        })
    }
}