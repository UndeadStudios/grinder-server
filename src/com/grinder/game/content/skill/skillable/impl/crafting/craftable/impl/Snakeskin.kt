package com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl

import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.content.skill.skillable.ItemCreationSkillable
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.Craftable
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.CraftableItem
import com.grinder.game.model.Animation
import com.grinder.game.model.AnimationLoop
import com.grinder.game.model.ItemActions
import com.grinder.game.model.Skill
import com.grinder.game.model.interfaces.menu.CreationMenu
import com.grinder.game.model.interfaces.menu.impl.FiveItemCreationMenu
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.RequiredItem
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.SoundLoop
import com.grinder.util.ItemID
import java.util.*

/**
 * Handles snakeskin tanning, based on [Leather] implementation.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/02/2021
 */
enum class Snakeskin(private vararg val craftables: CraftableItem) : Craftable {
//    BROODOO_SHIELD(
//        CraftableItem(
//            Item(ItemID.BROODOO_SHIELD),
//            35,
//            100.0,
//            "You make a broodoo shield.",
//            4,
//            Item(ItemID.SNAKESKIN, 2),
//            Item(ItemID.TRIBAL_MASK),
//            Item(ItemID.BRONZE_NAILS))),
    SNAKESKIN_BOOTS(
        CraftableItem(
            Item(ItemID.SNAKESKIN_BOOTS),
            45,
            30.0,
            "You make a pair of snakeskin boots.",
            4,
            Item(ItemID.SNAKESKIN, 6))),
    SNAKESKIN_VAMBRACES(
        CraftableItem(
            Item(ItemID.SNAKESKIN_VAMBRACES),
            45,
            30.0,
            "You make a pair of snakeskin vambraces.",
            4,
            Item(ItemID.SNAKESKIN, 8))),
    SNAKESKIN_BANDANA(
        CraftableItem(
            Item(ItemID.SNAKESKIN_BANDANA),
            47,
            35.0,
            "You make a snakeskin bandana.",
            4,
            Item(ItemID.SNAKESKIN, 5))),
    SNAKESKIN_CHAPS(
        CraftableItem(
            Item(ItemID.SNAKESKIN_CHAPS),
            48,
            45.0,
            "You make snakeskin chaps.",
            4,
            Item(ItemID.SNAKESKIN, 12))),
    SNAKESKIN_BODY(
        CraftableItem(
            Item(ItemID.SNAKESKIN_BODY),
            53,
            55.0,
            "You make a snakeskin body.",
            4,
            Item(ItemID.SNAKESKIN, 15))),
    ;

    override fun getUse() = Item(ItemID.NEEDLE)
    override fun getWith() = Item(ItemID.SNAKESKIN)
    override fun getName() = "Snakeskin"
    override fun getAnimationLoop() = AnimationLoop(Animation(1249), 6)
    override fun getSoundLoop() = SoundLoop(Sound(2266), 6)
    override fun getCraftableItems() = craftables
    override fun getRequiredItems(index: Int): Array<RequiredItem> {
        val craftableItem = craftables[index]
        val required = craftableItem.requiredItems
        val requiredItems = ArrayList<RequiredItem>()
        requiredItems.add(RequiredItem(use))
        for (item in required) {
            requiredItems.add(RequiredItem(Item(ItemID.THREAD, item.amount), true))
            requiredItems.add(RequiredItem(item, true))
        }
        return requiredItems.toTypedArray()
    }

    companion object {

        init {
            ItemActions.onItemOnItem(ItemID.NEEDLE to ItemID.SNAKESKIN) {
                player.creationMenu = Optional.of(FiveItemCreationMenu(
                    player = player,
                    title = "What snakeskin item would you like to make?",
                    action = CreationMenu.CreationMenuAction { index, item, amount ->
                        for (snakeskin in values()){
                            for ((craftableIndex, craftable) in snakeskin.craftables.withIndex()){
                                if (craftable.product.id == item){
                                    SkillUtil.startSkillable(
                                        player,
                                        ItemCreationSkillable(
                                            snakeskin.getRequiredItems(craftableIndex).toMutableList(),
                                            craftable.product,
                                            amount,
                                            snakeskin.animationLoop,
                                            snakeskin.soundLoop,
                                            craftable.level,
                                            craftable.experience.toInt(),
                                            Skill.CRAFTING,
                                            craftable.messageLoop,
                                            craftable.cyclesRequired
                                        )
                                    )
                                }
                            }
                        }

                    },
                    item1 = ItemID.SNAKESKIN_BODY to "Body",
                    item2 = ItemID.SNAKESKIN_CHAPS to "Chaps",
                    item3 = ItemID.SNAKESKIN_VAMBRACES to "Vambraces",
                    item4 = ItemID.SNAKESKIN_BANDANA to "Bandana",
                    item5 = ItemID.SNAKESKIN_BOOTS to "Boots")
                    .open())
                return@onItemOnItem true
            }
        }
    }
}