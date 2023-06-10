package com.grinder.game.content.item.degrading

import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.content.item.degrading.DegradingType.*
import com.grinder.game.entity.agent.player.message
import com.grinder.util.ItemID
import com.grinder.util.Misc
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Represents degradeable item types.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 *
 * @param itemId            the item id of the degradeable item.
 * @param transformID       the item id of the item that the degradeable item
 *                          should transform into upon degradation.
 * @param initialValue      the initial value of the [degradation][DegradingType].
 * @param degradeAmount     the amount to degrade after every degradation event.
 * @param degradingMessage  the message to send whenever the item degrades fully.
 * @param canCheckIntegrity 'true' if it is possible to [check the integrity][checkIntegrity] of the item.
 * @param degradingType     the type of degradation for this degradeable.
 */
enum class DegradableType(
        val itemId: Int,
        val transformID: Int,
        val initialValue: Int,
        val degradeAmount: Int,
        val degradingMessage: String?,
        val canCheckIntegrity: Boolean,
        val degradingType: DegradingType
) {

    //ABYSSAL_TENTACLE(12006, 12004, 500, 1, "Your #item has degraded.", true, ATTACK),

    ELDER_MAUL_C(15020, -1, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_TIME),
    KODAI_WAND_C(15021, -1, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_TIME),
    HEAVY_BALLISTA_C(15022, -1, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_TIME),
    TWISTED_BOW_C(15023, -1, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_TIME),
    ABYSSAL_WHIP_C(20405, -1, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_TIME),
    DARK_BOW_C(20408, -1, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_TIME),
    ARMADYL_GODSWORD_C(20593, -1, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_TIME),

    DRAGON_CLAWS_C(20784, -1, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_TIME),
    DRAGON_GODSWORD(15160, -1, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),
    BARROWS_WHIP(15370, -1, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME), // 110, 117 in the past

    BLESSED_SARADOMIN_SWORD_FULL(12808, 12809, 1, 1, "Your #item degrades.", true, ATTACK),
    BLESSED_SARADOMIN_SWROD(12809, 12804, 1000, 1, "Your #item degrades into dust leaving you with Saradomin's tear.", true, ATTACK),

    SPECTRAL_SPIRIT_SHIELD_C(15024, -1, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_TIME),
    ARCANE_SPIRIT_SHIELD_C(15025, -1, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_TIME),

    BANDOS_CHESTPLATE_C(15026, -1, TimeUnit.MINUTES.toMillis(40).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_TIME),
    BANDOS_TASSETS_C(15027, -1, TimeUnit.MINUTES.toMillis(40).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_TIME),

    ARMADYL_HELMET_C(15028, -1, TimeUnit.MINUTES.toMillis(40).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_TIME),
    ARMADYL_CHESTPLATE_C(15029, -1, TimeUnit.MINUTES.toMillis(40).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_TIME),
    ARMADYL_CHAINSKIRT_C(15030, -1, TimeUnit.MINUTES.toMillis(40).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_TIME),


    INFERNAL_AXE(13241, 13242, 250, 1, "Your #item has run out of charges", true, SKILLING),
    INFERNAL_PICKAXE(13243, 13244, 250, 1, "Your #item has run out of charges", true, SKILLING),
    INFERNAL_HARPOON(21031, 21033, 250, 1, "Your #item has run out of charges", true, SKILLING),

    CRYSTAL_HELMET(23971, 23956, TimeUnit.MINUTES.toMillis(15).toInt(), 1200, "Your #item degrades into a Crystal armour seed.", true, WEAR_COMBAT_TIME),
    CRYSTAL_PLATEBODY(23975, 23956, TimeUnit.MINUTES.toMillis(15).toInt(), 1200, "Your #item degrades into a Crystal armour seed.", true, WEAR_COMBAT_TIME),
    CRYSTAL_PLATELEGS(23979, 23956, TimeUnit.MINUTES.toMillis(15).toInt(), 1200, "Your #item degrades into a Crystal armour seed.", true, WEAR_COMBAT_TIME),

    BLACK_MASK_10(8901, 8903, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (9).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_9(8903, 8905, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (8).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_8(8905, 8907, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (7).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_7(8907, 8909, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (6).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_6(8909, 8911, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (5).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_5(8911, 8913, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (4).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_4(8913, 8915, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (3).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_3(8915, 8917, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (2).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_2(8917, 8919, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (1).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_1(8919, 8921, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask.", true, WEAR_COMBAT_TIME),
    BLACK_MASK_10_I(11774, 11775, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (9) (i).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_9_I(11775, 11776, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (8) (i).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_8_I(11776, 11777, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (7) (i).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_7_I(11777, 11778, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (6) (i).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_6_I(11778, 11779, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (5) (i).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_5_I(11779, 11780, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (4) (i).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_4_I(11780, 11781, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (3) (i).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_3_I(11781, 11782, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (2) (i).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_2_I(11782, 11783, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (1) (i).", true, WEAR_COMBAT_TIME),
    BLACK_MASK_1_I(11783, 11784, TimeUnit.MINUTES.toMillis(3).toInt(), 1200, "Your #item degrades into Black mask (i).", true, WEAR_COMBAT_TIME),

    CRYSTAL_BOW_NEW(4212, 4215, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 9/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW_FULL(4214, 4215, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 9/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW_9(4215, 4216, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 8/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW_8(4216, 4217, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 7/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW_7(4217, 4218, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 6/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW_6(4218, 4219, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 5/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW_5(4219, 4220, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 4/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW_4(4220, 4221, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 3/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW_3(4221, 4222, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 2/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW_2(4222, 4223, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 1/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW_1(4223, 23808, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into a Magic seed.", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW_I__NEW(11748, 11750, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 9/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW__I_FULL(11749, 11750, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 9/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW_I__9(11750, 11751, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 8/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW__I_8(11751, 11752, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 7/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW_I__7(11752, 11753, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 6/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW__I_6(11753, 11754, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 5/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW_I__5(11754, 11755, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 4/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW__I_4(11755, 11756, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 3/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW_I__3(11756, 11757, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 2/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW__I_2(11757, 11758, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal bow 1/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_BOW__I_1(11758, 23808, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into a Magic seed.", true, WEAR_COMBAT_TIME),

    CRYSTAL_SHIELD_NEW(4224, 4226, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 9/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD_FULL(4225, 4226, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 9/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD_9(4226, 4227, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 8/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD_8(4227, 4228, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 7/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD_7(4228, 4229, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 6/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD_6(4229, 4230, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 5/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD_5(4230, 4231, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 4/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD_4(4231, 4232, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 3/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD_3(4232, 4233, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 2/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD_2(4233, 4234, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 1/10.", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD_1(4234, 23808, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into a Magic seed.", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD_I__NEW(11759, 11761, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 9/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD__I_FULL(11760, 11761, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 9/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD_I__9(11761, 11762, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 8/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD__I_8(11762, 11763, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 7/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD_I__7(11763, 11764, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 6/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD__I_6(11764, 11765, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 5/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD_I__5(11765, 11766, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 4/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD__I_4(11766, 11767, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 3/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD_I__3(11767, 11768, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 2/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD__I_2(11768, 11769, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into Crystal shield 1/10 (i).", true, WEAR_COMBAT_TIME),
    CRYSTAL_SHIELD__I_1(11769, 23808, TimeUnit.MINUTES.toMillis(5).toInt(), 1200, "Your #item degrades into a Magic seed.", true, WEAR_COMBAT_TIME),

    INFERNAL_WHIP(15722, 15730, TimeUnit.MINUTES.toMillis(60).toInt(), 1200, "Your #item has degraded and is now damaged.", true, WEAR_COMBAT_TIME),
    TORVA_WHIP(15720, 15729, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item has degraded and is now damaged.", true, WEAR_COMBAT_TIME),
    BANDOS_WHIP(15751, 15797, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item has degraded and is now damaged.", true, WEAR_COMBAT_TIME),
    //ZAMORAK_WHIP(15846, 15852, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item has degraded and is now damaged.", true, WEAR_COMBAT_TIME),
    //SARADOMIN_WHIP(15848, 15853, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item has degraded and is now damaged.", true, WEAR_COMBAT_TIME),
    //GUTHIX_WHIP(15850, 15854, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item has degraded and is now damaged.", true, WEAR_COMBAT_TIME),
    //TORVA_HELM(ItemID.TORVA_FULL_HELM, ItemID.TORVA_FULL_HELM_DAMAGED, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item has degraded and is now damaged.", true, WEAR_COMBAT_TIME),
    //TORVA_PLATEBODY(ItemID.TORVA_PLATEBODY, ItemID.TORVA_PLATEBODY_DAMAGED, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item has degraded and is now damaged.", true, WEAR_COMBAT_TIME),
    //TORVA_PLATELEGS(ItemID.TORVA_PLATELEGS, ItemID.TORVA_PLATELEGS_DAMAGED, TimeUnit.MINUTES.toMillis(30).toInt(), 1200, "Your #item has degraded and is now damaged.", true, WEAR_COMBAT_TIME),


    VESTA_LONGSWORD(22613, -1, TimeUnit.MINUTES.toMillis(15).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),
    VESTA_SPEAR(22610, -1, TimeUnit.MINUTES.toMillis(15).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),
    VESTA_CHAIN(22616, -1, TimeUnit.MINUTES.toMillis(15).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),
    VESTA_PLATESKIRT(22619, -1, TimeUnit.MINUTES.toMillis(15).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),

    STATIUS_WARHAMMER(22622, -1, TimeUnit.MINUTES.toMillis(10).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),
    STATIUS_FULL_HELM(22625, -1, TimeUnit.MINUTES.toMillis(10).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),
    STATIUS_PLATEBODY(22628, -1, TimeUnit.MINUTES.toMillis(10).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),
    STATIUS_PLATELEGS(22631, -1, TimeUnit.MINUTES.toMillis(10).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),

    MORRIGAN_COIF(22638, -1, TimeUnit.MINUTES.toMillis(10).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),
    MORRIGAN_BODY(22641, -1, TimeUnit.MINUTES.toMillis(10).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),
    MORRIGAN_CHAPS(22644, -1, TimeUnit.MINUTES.toMillis(10).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),

    ZURIEL_BOTTOM(22656, -1, TimeUnit.MINUTES.toMillis(10).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),
    ZURIEL_TOP(22653, -1, TimeUnit.MINUTES.toMillis(10).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),
    ZURIEL_HOOD(22650, -1, TimeUnit.MINUTES.toMillis(10).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),
    ZURIEL_STAFF(22647, -1, TimeUnit.MINUTES.toMillis(10).toInt(), 1200, "Your #item degrades into dust.", true, WEAR_COMBAT_TIME),

    VERAC_HELM_FULL(4753, 4976, 1, 1, null, false, ATTACK),
    VERAC_HELM_100(4976, 4977, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, ATTACK),
    VERAC_HELM_75(4977, 4978, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    VERAC_HELM_50(4978, 4979, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    VERAC_HELM_25(4979, 4980, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    VERAC_FLAIL_FULL(4755, 4982, 1, 1, "Your #item has degraded.", false, ATTACK),
    VERAC_FLAIL_100(4982, 4983, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    VERAC_FLAIL_75(4983, 4984, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    VERAC_FLAIL_50(4984, 4985, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    VERAC_FLAIL_25(4985, 4986, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    VERAC_BRASSARD_FULL(4757, 4988, 1, 1, "Your #item has degraded.", false, ATTACK),
    VERAC_BRASSARD_100(4988, 4989, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    VERAC_BRASSARD_75(4989, 4990, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    VERAC_BRASSARD_50(4990, 4991, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    VERAC_BRASSARD_25(4991, 4992, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    VERAC_SKIRT_FULL(4759, 4994, 1, 1, null, false, ATTACK),
    VERAC_SKIRT_100(4994, 4995, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    VERAC_SKIRT_75(4995, 4996, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    VERAC_SKIRT_50(4996, 4997, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    VERAC_SKIRT_25(4997, 4998, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),

    DHAROK_HELM_FULL(4716, 4880, 1, 1, "Your #item has degraded.", false, ATTACK),
    DHAROK_HELM_100(4880, 4881, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    DHAROK_HELM_75(4881, 4882, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    DHAROK_HELM_50(4882, 4883, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    DHAROK_HELM_25(4883, 4884, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    DHAROK_AXE_FULL(4718, 4886, 1, 1, "Your #item has degraded.", false, ATTACK),
    DHAROK_AXE_100(4886, 4887, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    DHAROK_AXE_75(4887, 4888, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    DHAROK_AXE_50(4888, 4889, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    DHAROK_AXE_25(4889, 4890, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    DHAROK_PLATE_FULL(4720, 4892, 1, 1, "Your #item has degraded.", false, ATTACK),
    DHAROK_PLATE_100(4892, 4893, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    DHAROK_PLATE_75(4893, 4894, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    DHAROK_PLATE_50(4894, 4895, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    DHAROK_PLATE_25(4895, 4896, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    DHAROK_LEGS_FULL(4722, 4898, 1, 1, "Your #item has degraded.", false, ATTACK),
    DHAROK_LEGS_100(4898, 4899, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    DHAROK_LEGS_75(4899, 4900, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    DHAROK_LEGS_50(4900, 4901, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    DHAROK_LEGS_25(4901, 4902, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),

    GUTHAN_HELM_FULL(4724, 4904, 1, 1, "Your #item has degraded.", false, ATTACK),
    GUTHAN_HELM_100(4904, 4905, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    GUTHAN_HELM_75(4905, 4906, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    GUTHAN_HELM_50(4906, 4907, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    GUTHAN_HELM_25(4907, 4908, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    GUTHAN_WARSPEAR_FULL(4726, 4910, 1, 1, "Your #item has degraded.", false, ATTACK),
    GUTHAN_WARSPEAR_100(4910, 4911, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    GUTHAN_WARSPEAR_75(4911, 4912, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    GUTHAN_WARSPEAR_50(4912, 4913, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    GUTHAN_WARSPEAR_25(4913, 4914, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    GUTHAN_PLATE_FULL(4728, 4916, 1, 1, "Your #item has degraded.", false, ATTACK),
    GUTHAN_PLATE_100(4916, 4917, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    GUTHAN_PLATE_75(4917, 4918, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    GUTHAN_PLATE_50(4918, 4919, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    GUTHAN_PLATE_25(4919, 4920, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    GUTHAN_SKIRT_FULL(4730, 4922, 1, 1, "Your #item has degraded.", false, ATTACK),
    GUTHAN_SKIRT_100(4922, 4923, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    GUTHAN_SKIRT_75(4923, 4924, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    GUTHAN_SKIRT_50(4924, 4925, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    GUTHAN_SKIRT_25(4925, 4926, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),

    TORAG_HELM_FULL(4745, 4952, 1, 1, "Your #item has degraded.", false, ATTACK),
    TORAG_HELM_100(4952, 4953, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    TORAG_HELM_75(4953, 4954, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    TORAG_HELM_50(4954, 4955, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    TORAG_HELM_25(4955, 4956, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    TORAG_HAMMER_FULL(4747, 4958, 1, 1, "Your #item has degraded.", false, ATTACK),
    TORAG_HAMMER_100(4958, 4959, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    TORAG_HAMMER_75(4959, 4960, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    TORAG_HAMMER_50(4960, 4961, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    TORAG_HAMMER_25(4961, 4962, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    TORAG_PLATE_FULL(4749, 4964, 1, 1, "Your #item has degraded.", false, ATTACK),
    TORAG_PLATE_100(4964, 4965, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    TORAG_PLATE_75(4965, 4966, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    TORAG_PLATE_50(4966, 4967, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    TORAG_PLATE_25(4967, 4968, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    TORAG_LEGS_FULL(4751, 4970, 1, 1, "Your #item has degraded.", false, ATTACK),
    TORAG_LEGS_100(4970, 4971, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    TORAG_LEGS_75(4971, 4972, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    TORAG_LEGS_50(4972, 4973, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    TORAG_LEGS_25(4973, 4974, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),

    AHRIM_HOOD_FULL(4708, 4856, 1, 1, "Your #item has degraded.", false, ATTACK),
    AHRIM_HOOD_100(4856, 4857, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    AHRIM_HOOD_75(4857, 4858, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    AHRIM_HOOD_50(4858, 4859, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    AHRIM_HOOD_25(4859, 4860, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    AHRIM_STAFF_FULL(4710, 4862, 1, 1, "Your #item has degraded.", false, ATTACK),
    AHRIM_STAFF_100(4862, 4863, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    AHRIM_STAFF_75(4863, 4864, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    AHRIM_STAFF_50(4864, 4865, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    AHRIM_STAFF_25(4865, 4866, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    AHRIM_ROBE_TOP_FULL(4712, 4868, 1, 1, "Your #item has degraded.", false, ATTACK),
    AHRIM_ROBE_TOP_100(4868, 4869, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    AHRIM_ROBE_TOP_75(4869, 4870, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, null, false, WEAR_COMBAT_TIME),
    AHRIM_ROBE_TOP_50(4870, 4871, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, null, false, WEAR_COMBAT_TIME),
    AHRIM_ROBE_TOP_25(4871, 4872, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, null, false, WEAR_COMBAT_TIME),
    AHRIM_ROBE_SKIRT_FULL(4714, 4874, 1, 1, "Your #item has degraded.", false, ATTACK),
    AHRIM_ROBE_SKIRT_100(4874, 4875, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    AHRIM_ROBE_SKIRT_75(4875, 4876, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    AHRIM_ROBE_SKIRT_50(4876, 4877, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    AHRIM_ROBE_SKIRT_25(4877, 4878, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),

    KARIL_COIF_FULL(4732, 4928, 1, 1, "Your #item has degraded.", false, ATTACK),
    KARIL_COIF_100(4928, 4929, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    KARIL_COIF_75(4929, 4930, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    KARIL_COIF_50(4930, 4931, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    KARIL_COIF_25(4931, 4932, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    KARIL_CROSSBOW_FULL(4734, 4934, 1, 1, "Your #item has degraded.", false, ATTACK),
    KARIL_CROSSBOW_100(4934, 4935, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    KARIL_CROSSBOW_75(4935, 4936, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    KARIL_CROSSBOW_50(4936, 4937, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    KARIL_CROSSBOW_25(4937, 4938, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    KARIL_TOP_FULL(4736, 4940, 1, 1, "Your #item has degraded.", false, ATTACK),
    KARIL_TOP_100(4940, 4941, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    KARIL_TOP_75(4941, 4942, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    KARIL_TOP_50(4942, 4943, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    KARIL_TOP_25(4943, 4944, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    KARIL_SKIRT_FULL(4738, 4946, 1, 1, "Your #item has degraded.", false, ATTACK),
    KARIL_SKIRT_100(4946, 4947, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    KARIL_SKIRT_75(4947, 4948, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    KARIL_SKIRT_50(4948, 4949, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME),
    KARIL_SKIRT_25(4949, 4950, TimeUnit.MINUTES.toMillis(50).toInt(), 1200, "Your #item has degraded.", false, WEAR_COMBAT_TIME);

    val repairedBarrowsVersion: DegradableType
        get() {
            if (barrows.containsKey(itemId)) {
                val name = name
                val offset = name.lastIndexOf("_")
                return valueOf(name.substring(0, offset) + "_FULL")
            }
            return this
        }

    fun checkBarrows(player: Player, itemId: Int) {
        if (barrows.containsKey(itemId)) {
            val degradableType = barrows[itemId]
            val name = degradableType!!.name.toLowerCase().replace("_", " ")
            val formattedName = Misc.capitalize(name)
            if (name.startsWith("ahrim") || name.startsWith("dharok") || name.startsWith("verac") || name.startsWith("torag") || name.startsWith("karil") || name.startsWith("guthan")) {
                when {
                    name.endsWith("100") -> player.message("The $formattedName looks solid and reliable.")
                    name.endsWith("75") -> player.message("The $formattedName still looks stable enough.")
                    name.endsWith("50") -> player.message("The $formattedName looks like its starting to decay.")
                    name.endsWith("25") -> player.message("The $formattedName is almost completely decayed.")
                }
            }
        }
    }

    fun checkIntegrity(player: Player) {

        val integrity = getDegradingValue(player)

        if (isDegradingType(SKILLING) && integrity > initialValue) {
            player.itemDegradationManager.repair(itemId)
            return
        }

        val itemName =  ItemDefinition.forId(itemId)?.name ?: "unknown"

        if (integrity >= 0 && !itemName.contains("Black mask")) {
            player.message("The $itemName has an integrity of ${getIntegrity(integrity)}%")
        } else when (itemId) {
            11774, 8901 -> player.message("<col=8B008B>Your $itemName has 10 charges left.")
            11775, 8903 -> player.message("<col=8B008B>Your $itemName has 9 charges left.")
            11776, 8905 -> player.message("<col=8B008B>Your $itemName has 8 charges left.")
            11777, 8907 -> player.message("<col=8B008B>Your $itemName has 7 charges left.")
            11778, 8909 -> player.message("<col=8B008B>Your $itemName has 6 charges left.")
            11779, 8911 -> player.message("<col=8B008B>Your $itemName has 5 charges left.")
            11780, 8913 -> player.message("<col=8B008B>Your $itemName has 4 charges left.")
            11781, 8915 -> player.message("<col=8B008B>Your $itemName has 3 charges left.")
            11782, 8917 -> player.message("<col=8B008B>Your $itemName has 2 charges left.")
            11783, 8919 -> player.message("<col=8B008B>Your $itemName has one charge left.")
        }
    }

    fun getIntegrity(player: Player) = getIntegrity(getDegradingValue(player))

    fun getIntegrity(currentValue: Int) = (currentValue.toFloat() / initialValue.toFloat() * 100f).toInt()

    fun getDegradingValue(player: Player): Int {
        val degradationLevel = player.itemDegradationManager.getLevel(itemId)
        return if (degradationLevel <= -1)
            initialValue
        else
            degradationLevel
    }

    fun isDegradingType(vararg degradingTypes: DegradingType) = degradingTypes.any { it == degradingType }

    companion object {

        private val degradables = TreeMap<Int, DegradableType>()
        private val barrows = TreeMap<Int, DegradableType?>()

        @JvmField
        var brokenBarrows = HashMap<Int, Int>()

        init {
            for (degradable in values()) {
                degradables[degradable.itemId] = degradable
                val name: String = degradable.name.toLowerCase()
                if (name.startsWith("ahrim") || name.startsWith("dharok") || name.startsWith("verac") || name.startsWith("torag") || name.startsWith("karil") || name.startsWith("guthan")) {
                    if (name.endsWith("_25")) {
                        val offset: Int = name.lastIndexOf("_")
                        brokenBarrows[degradable.transformID] = valueOf(name.substring(0, offset).toUpperCase() + "_FULL").itemId
                    }
                    barrows[degradable.itemId] = degradable
                }
            }
        }

        @JvmStatic
        fun forItem(itemID: Int) = degradables[itemID]

        @JvmStatic
        fun findBrokenBarrowsVersionItemId(itemID: Int): OptionalInt {
            if (barrows.containsKey(itemID)) {
                val name = barrows[itemID]!!.name
                val offset = name.lastIndexOf("_")
                return OptionalInt.of(valueOf(name.substring(0, offset) + "_25").transformID)
            }
            return OptionalInt.empty()
        }

        @JvmStatic
        fun transformIdOnDrop(itemId: Int): Int {
            if (barrows.containsKey(itemId)) {
                val degradableType = barrows[itemId]
                var name = degradableType!!.name
                val split = name.split("_".toRegex()).toTypedArray()
                val lastIndex = split.size - 1
                name = name.substring(0, name.length - split[lastIndex].length)
                name += "25"
                return valueOf(name).transformID
            }
            return itemId
        }
    }
}