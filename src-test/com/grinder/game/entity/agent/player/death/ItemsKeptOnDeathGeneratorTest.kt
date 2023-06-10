package com.grinder.game.entity.agent.player.death

import com.grinder.GrinderPlayerTest
import com.grinder.game.content.item.charging.impl.Blowpipe
import com.grinder.game.content.item.charging.impl.DragonFireShield
import com.grinder.game.content.item.degrading.DegradableType
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.BrokenItems
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.player.Inventory
import com.grinder.util.ItemID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for the [ItemsKeptOnDeathGenerator] evaluation.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since 20/02/2021
 */
internal class ItemsKeptOnDeathGeneratorTest : GrinderPlayerTest("") {

    private lateinit var inventory : Inventory

    @BeforeEach
    fun setup(){
        inventory = player.inventory
    }

    @Test
    fun `test broken and dropped items`() {
        val items = arrayOf(
            Item(ItemID.RUNE_PLATELEGS),
            Item(ItemID.COINS, 100),
            Item(BrokenItems.GUTHIX_HALO.originalItem))
        inventory[0] = items[0]
        inventory[1] = items[1]
        inventory[2] = items[2]
        val generator = ItemsKeptOnDeathGenerator(player, true)
        val result = generator.generate()
        Assertions.assertTrue(result.broken.isEmpty())
        Assertions.assertEquals(1, result.keep.count(items[0]))
        Assertions.assertEquals(1, result.keep.count(items[1].id, 1))
        Assertions.assertEquals(1, result.keep.count(items[2]))
        Assertions.assertEquals(1, result.dropped.count(items[1].id, 99))
        Assertions.assertTrue(result.lost.isEmpty())
    }

    @Test
    fun `test dropped broken and kept degradeable items`() {
        val items = arrayOf(
            Item(DegradableType.VESTA_CHAIN.itemId),
            Item(DegradableType.VESTA_PLATESKIRT.itemId),
            Item(DegradableType.VESTA_LONGSWORD.itemId),
            Item(BrokenItems.AVERNIC_DEFENDER_BROKEN.originalItem))
        inventory[0] = items[0]
        inventory[1] = items[1]
        inventory[2] = items[2]
        inventory[3] = items[3]
        val generator = ItemsKeptOnDeathGenerator(player, true)
        val result = generator.generate()
        Assertions.assertEquals(1, result.keep.count(items[0]))
        Assertions.assertEquals(1, result.keep.count(items[1]))
        Assertions.assertEquals(1, result.keep.count(items[2]))
        Assertions.assertEquals(1, result.broken.count(items[3]))
        Assertions.assertTrue(result.lost.isEmpty())
    }

    @Test
    fun `test keep broken items`() {
        val itemId = BrokenItems.AVERNIC_DEFENDER_BROKEN.originalItem
        val items = arrayOf(
            Item(itemId),
            Item(itemId),
            Item(itemId),
            Item(itemId))
        inventory[0] = items[0]
        inventory[1] = items[1]
        inventory[2] = items[2]
        inventory[3] = items[3]
        val generator = ItemsKeptOnDeathGenerator(player, true)
        val result = generator.generate()
        Assertions.assertEquals(1, result.broken.count(itemId))
        Assertions.assertEquals(3, result.keep.count(itemId))
        Assertions.assertTrue(result.dropped.isEmpty())
        Assertions.assertTrue(result.lost.isEmpty())
    }

    @Test
    fun `test keep blowpipe`() {
        val blowpipe = AttributableItem(Blowpipe.CHARGED)
        blowpipe.setAttribute(Blowpipe.SCALES, 100)
        inventory[0] = blowpipe
        val generator = ItemsKeptOnDeathGenerator(player, true)
        val result = generator.generate()
        Assertions.assertTrue(result.broken.isEmpty())
        Assertions.assertEquals(1, result.keep.count(blowpipe))
        Assertions.assertTrue(result.dropped.isEmpty())
        Assertions.assertTrue(result.lost.isEmpty())
    }

    @Test
    fun `test dropped blowpipe`() {
        val rapiers = arrayOf(
            Item(ItemID.GHRAZI_RAPIER, 1),
            Item(ItemID.GHRAZI_RAPIER, 1),
            Item(ItemID.GHRAZI_RAPIER, 1)
        )
        val blowpipe = AttributableItem(Blowpipe.CHARGED)
        inventory[0] = blowpipe
        inventory[1] = rapiers[0]
        inventory[2] = rapiers[1]
        inventory[3] = rapiers[2]
        val generator = ItemsKeptOnDeathGenerator(player, true)
        val result = generator.generate()
        Assertions.assertTrue(result.broken.isEmpty())
        Assertions.assertEquals(3, result.keep.count(rapiers[0]))
        Assertions.assertEquals(1, result.dropped.count(Blowpipe.UNCHARGED))
        Assertions.assertTrue(result.lost.isEmpty())
    }

    @Test
    fun `test dropped dfs`() {
        val rapiers = arrayOf(
            Item(ItemID.GHRAZI_RAPIER, 1),
            Item(ItemID.GHRAZI_RAPIER, 1),
            Item(ItemID.GHRAZI_RAPIER, 1)
        )
        val dfs = AttributableItem(DragonFireShield.CHARGED)
        inventory[0] = dfs
        inventory[1] = rapiers[0]
        inventory[2] = rapiers[1]
        inventory[3] = rapiers[2]
        val generator = ItemsKeptOnDeathGenerator(player, true)
        val result = generator.generate()
        Assertions.assertTrue(result.broken.isEmpty())
        Assertions.assertEquals(3, result.keep.count(rapiers[0]))
        Assertions.assertEquals(1, result.dropped.count(DragonFireShield.UNCHARGED))
        Assertions.assertTrue(result.lost.isEmpty())
    }

    @Test
    fun `test lost exceptions`() {
        val items = arrayOf(
            Item(ItemID.MAGIC_BUTTERFLY_NET, 1),
            Item(ItemID.JAR_GENERATOR, 1),
            Item(ItemID.BOLT_POUCH, 1)
        )
        inventory[0] = items[0]
        inventory[1] = items[1]
        inventory[2] = items[2]
        val generator = ItemsKeptOnDeathGenerator(player, true)
        val result = generator.generate()
        Assertions.assertTrue(result.broken.isEmpty())
        Assertions.assertTrue(result.keep.isEmpty())
        Assertions.assertTrue(result.dropped.isEmpty())
        Assertions.assertEquals(3, result.lost.size)
    }

    private fun List<Item>.any(id: Int, amount: Int = 1) = any {
        it.id == id && it.amount == amount
    }

    private fun List<Item>.any(item: Item) = any {
        it.hashCode() == item.hashCode()
    }

    private fun List<Item>.count(item: Item) = count {
        it.hashCode() == item.hashCode()
    }

    private fun List<Item>.count(id: Int, amount: Int = 1) = count {
        it.id == id && it.amount == amount
    }
}