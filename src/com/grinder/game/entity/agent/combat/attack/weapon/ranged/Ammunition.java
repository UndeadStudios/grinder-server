package com.grinder.game.entity.agent.combat.attack.weapon.ranged;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.grinder.game.content.item.charging.impl.Blowpipe;
import com.grinder.game.content.item.charging.impl.ShayzienBlowpipe;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.util.ItemID;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Stan van der Bend
 * @since 5-4-19
 */
public enum Ammunition {

    CRYSTAL_ARROW(-1, new Graphic(250, GraphicHeight.HIGH), 249, 60),
    CRAWS_ARROW(-1, new Graphic(1475, GraphicHeight.HIGH), 1476, 100),
    BRONZE_ARROW(882, new Graphic(19, GraphicHeight.HIGH), new Graphic(1104, GraphicHeight.HIGH), 10, 7),
    BRONZE_ARROW_P1(883, new Graphic(19, GraphicHeight.HIGH), new Graphic(1104, GraphicHeight.HIGH), 10, 7),
    BRONZE_ARROW_P2(5616, new Graphic(19, GraphicHeight.HIGH), new Graphic(1104, GraphicHeight.HIGH), 10, 7),
    BRONZE_ARROW_P3(5622, new Graphic(19, GraphicHeight.HIGH), new Graphic(1104, GraphicHeight.HIGH), 10, 7),
    IRON_ARROW(884, new Graphic(18, GraphicHeight.HIGH), new Graphic(1105, GraphicHeight.HIGH), 9, 10),
    IRON_ARROW_P1(885, new Graphic(18, GraphicHeight.HIGH), new Graphic(1105, GraphicHeight.HIGH), 9, 10),
    IRON_ARROW_P2(5617, new Graphic(18, GraphicHeight.HIGH), new Graphic(1105, GraphicHeight.HIGH), 9, 10),
    IRON_ARROW_P3(5623, new Graphic(18, GraphicHeight.HIGH), new Graphic(1105, GraphicHeight.HIGH), 9, 10),
    STEEL_ARROW(886, new Graphic(20, GraphicHeight.HIGH), new Graphic(1106, GraphicHeight.HIGH), 11, 16),
    STEEL_ARROW_P1(887, new Graphic(20, GraphicHeight.HIGH), new Graphic(1106, GraphicHeight.HIGH), 11, 16),
    STEEL_ARROW_P2(5618, new Graphic(20, GraphicHeight.HIGH), new Graphic(1106, GraphicHeight.HIGH), 11, 16),
    STEEL_ARROW_P3(5624, new Graphic(20, GraphicHeight.HIGH), new Graphic(1106, GraphicHeight.HIGH), 11, 16),
    MITHRIL_ARROW(888, new Graphic(21, GraphicHeight.HIGH), new Graphic(1107, GraphicHeight.HIGH), 12, 22),
    MITHRIL_ARROW_P1(889, new Graphic(21, GraphicHeight.HIGH), new Graphic(1107, GraphicHeight.HIGH), 12, 22),
    MITHRIL_ARROW_P2(5619, new Graphic(21, GraphicHeight.HIGH), new Graphic(1107, GraphicHeight.HIGH), 12, 22),
    MITHRIL_ARROW_P3(5625, new Graphic(21, GraphicHeight.HIGH), new Graphic(1107, GraphicHeight.HIGH), 12, 22),
    ADAMANT_ARROW(890, new Graphic(22, GraphicHeight.HIGH), new Graphic(1108, GraphicHeight.HIGH), 13, 31),
    ADAMANT_ARROW_P1(891, new Graphic(22, GraphicHeight.HIGH), new Graphic(1108, GraphicHeight.HIGH), 13, 31),
    ADAMANT_ARROW_P2(5620, new Graphic(22, GraphicHeight.HIGH), new Graphic(1108, GraphicHeight.HIGH), 13, 31),
    ADAMANT_ARROW_P3(5626, new Graphic(22, GraphicHeight.HIGH), new Graphic(1108, GraphicHeight.HIGH), 13, 31),
    RUNE_ARROW(892, new Graphic(24, GraphicHeight.HIGH), new Graphic(1109, GraphicHeight.HIGH), 15, 50),
    RUNE_ARROW_P1(893, new Graphic(24, GraphicHeight.HIGH), new Graphic(1109, GraphicHeight.HIGH), 15, 50),
    RUNE_ARROW_P2(5621, new Graphic(24, GraphicHeight.HIGH), new Graphic(1109, GraphicHeight.HIGH), 15, 50),
    RUNE_ARROW_P3(5627, new Graphic(24, GraphicHeight.HIGH), new Graphic(1109, GraphicHeight.HIGH), 15, 50),

    AMETHYST_ARROW(21326, new Graphic(1385, GraphicHeight.HIGH), 1384, 65),
    AMETHYST_ARROW_P1(21332, new Graphic(1385, GraphicHeight.HIGH), 1384, 65),
    AMETHYST_ARROW_P2(21334, new Graphic(1385, GraphicHeight.HIGH), 1384, 65),
    AMETHYST_ARROW_P3(21336, new Graphic(1385, GraphicHeight.HIGH), 1384, 65),

    ICE_ARROW(78, new Graphic(25, GraphicHeight.HIGH), new Graphic(1110, GraphicHeight.HIGH), 16, 60),
    BROAD_ARROW(4160, new Graphic(20, GraphicHeight.HIGH), new Graphic(1111, GraphicHeight.HIGH), 11, 34),

    DRAGON_ARROW(11212, new Graphic(1116, GraphicHeight.HIGH), new Graphic(1111, GraphicHeight.HIGH), 1120, 75),
    DRAGON_ARROW_P1(11227, new Graphic(1116, GraphicHeight.HIGH), new Graphic(1111, GraphicHeight.HIGH), 1120, 75),
    DRAGON_ARROW_P2(11228, new Graphic(1116, GraphicHeight.HIGH), new Graphic(1111, GraphicHeight.HIGH), 1120, 75),
    DRAGON_ARROW_P3(11229, new Graphic(1116, GraphicHeight.HIGH), new Graphic(1111, GraphicHeight.HIGH), 1120, 75),


    BRONZE_BRUTAL_ARROW(4773, new Graphic(403, GraphicHeight.MIDDLE), 404, 11),
    IRON_BRUTAL_ARROW(4778, new Graphic(403, GraphicHeight.MIDDLE), 404, 13),
    STEEL_BRUTAL_ARROW(4783, new Graphic(403, GraphicHeight.MIDDLE), 404, 19),
    BLACK_BRUTAL_ARROW(4788, new Graphic(403, GraphicHeight.MIDDLE), 404, 22),
    MITHRIL_BRUTAL_ARROW(4793, new Graphic(403, GraphicHeight.MIDDLE), 404, 34),
    ADAMANT_BRUTAL_ARROW(4798, new Graphic(403, GraphicHeight.MIDDLE), 404, 45),
    RUNE_BRUTAL_ARROW(4803, new Graphic(403, GraphicHeight.MIDDLE), 404, 60),

    OGRE_ARROWS(2866, new Graphic(243, GraphicHeight.MIDDLE), 242, 22),
    //TOOD: TRAINING_ARROWS(9706, new Graphic(22, GraphicHeight.HIGH), new Graphic(1108, GraphicHeight.HIGH), 13, 31),

    
    KEBBIT_BOLT(10158, new Graphic(955, GraphicHeight.HIGH), 27, 31),
    LONG_KEBBIT_BOLT(10159, new Graphic(955, GraphicHeight.HIGH), 27, 45),
    BONE_BOLT(8882, new Graphic(696, GraphicHeight.HIGH), 27, 13),
    BRONZE_BOLT(877, null, 27, 13),
    BRONZE_BOLT_P1(878, null, 27, 13),
    BRONZE_BOLT_P2(6061, null, 27, 13),
    BRONZE_BOLT_P3(6062, null, 27, 13),
    OPAL_BOLT(879, null, 27, 20),
    ENCHANTED_OPAL_BOLT(9236, null, 27, 20),
    IRON_BOLT(9140, null, 27, 28),
    IRON_BOLT_P1(9287, null, 27, 28),
    IRON_BOLT_P2(9294, null, 27, 28),
    IRON_BOLT_P3(9301, null, 27, 28),
    JADE_BOLT(9335, null, 27, 31),
    ENCHANTED_JADE_BOLT(9237, null, 27, 31),
    STEEL_BOLT(9141, null, 27, 35),
    STEEL_BOLT_P1(9288, null, 27, 35),
    STEEL_BOLT_P2(9295, null, 27, 35),
    STEEL_BOLT_P3(9302, null, 27, 35),
    PEARL_BOLT(880, null, 27, 38),
    ENCHANTED_PEARL_BOLT(9238, null, 27, 38),
    MITHRIL_BOLT(9142, null, 27, 40),
    MITHRIL_BOLT_P1(9289, null, 27, 40),
    MITHRIL_BOLT_P2(9296, null, 27, 40),
    MITHRIL_BOLT_P3(9303, null, 27, 40),
    TOPAZ_BOLT(9336, null, 27, 50),
    ENCHANTED_TOPAZ_BOLT(9239, null, 27, 50),
    ADAMANT_BOLT(9143, null, 27, 60),
    ADAMANT_BOLT_P1(9290, null, 27, 60),
    ADAMANT_BOLT_P2(9297, null, 27, 60),
    ADAMANT_BOLT_P3(9304, null, 27, 60),
    SAPPHIRE_BOLT(9337, null, 27, 65),
    ENCHANTED_SAPPHIRE_BOLT(9240, null, 27, 65),
    EMERALD_BOLT(9338, null, 27, 70),
    ENCHANTED_EMERALD_BOLT(9241, null, 27, 70),
    RUBY_BOLT(9339, null, 27, 75),
    ENCHANTED_RUBY_BOLT(9242, null, 27, 75),
    BROAD_BOLT(11875, null, 27, 100),
    RUNITE_BOLT(9144, null, 27, 115),
    RUNITE_BOLT_P1(9291, null, 27, 115),
    RUNITE_BOLT_P2(9298, null, 27, 115),
    RUNITE_BOLT_P3(9305, null, 27, 115),
    DIAMOND_BOLT(9340, null, 27, 105),
    ENCHANTED_DIAMOND_BOLT(9243, null, 27, 105),
    AMETHYST_BROAD_BOLT(21316, null, 27, 115),
    DRAGONSTONE_BOLT(9341, null, 27, 117),
    ENCHANTED_DRAGON_BOLT(9244, null, 27, 117),
    ONYX_BOLT(9342, null, 27, 120),
    ENCHANTED_ONYX_BOLT(9245, null, 27, 120),
    DRAGON_BOLT(21905, null, 1468, 130),
    DRAGON_BOLT_P1(21924, null, 1468, 130),
    DRAGON_BOLT_P2(21926, null, 1468, 130),
    DRAGON_BOLT_P3(21928, null, 1468, 130),
    ENCHANTED_OPAL_DRAGON_BOLT(21932, null, 1468, 130),
    ENCHANTED_JADE_DRAGON_BOLT(21934, null, 1468, 130),
    ENCHANTED_PEARL_DRAGON_BOLT(21936, null, 1468, 130),
    ENCHANTED_TOPAZ_DRAGON_BOLT(21938, null, 1468, 130),
    ENCHANTED_SAPPHIRE_DRAGON_BOLT(21940, null, 1468, 130),
    ENCHANTED_EMERALD_DRAGON_BOLT(21942, null, 1468, 130),
    ENCHANTED_RUBY_DRAGON_BOLT(21944, null, 1468, 130),
    ENCHANTED_DIAMOND_DRAGON_BOLT(21946, null, 1468, 130),
    ENCHANTED_DRAGON_DRAGON_BOLT(21948, null, 1468, 130),
    ENCHANTED_ONYX_DRAGON_BOLT(21950, null, 1468, 130),
    OPAL_DRAGON_BOLT(21955, null, 1468, 130),
    JADE_DRAGON_BOLT(21957, null, 1468, 130),
    PEARL_DRAGON_BOLT(21959, null, 1468, 130),
    TOPAZ_DRAGON_BOLT(21961, null, 1468, 130),
    SAPPHIRE_DRAGON_BOLT(21963, null, 1468, 130),
    EMERALD_DRAGON_BOLT(21965, null, 1468, 130),
    RUBY_DRAGON_BOLT(21967, null, 1468, 130),
    DIAMOND_DRAGON_BOLT(21969, null, 1468, 130),
    DRAGON_DRAGON_BOLT(21971, null, 1468, 130),
    ONYX_DRAGON_BOLT(21973, null, 1468, 130),
    BRONZE_DART(806, new Graphic(232, GraphicHeight.HIGH), 226, 1),
    BRONZE_DART_P1(812, new Graphic(232, GraphicHeight.HIGH), 226, 1),
    BRONZE_DART_P2(5628, new Graphic(232, GraphicHeight.HIGH), 226, 1),
    BRONZE_DART_P3(5635, new Graphic(232, GraphicHeight.HIGH), 226, 1),
    IRON_DART(807, new Graphic(233, GraphicHeight.HIGH), 227, 4),
    IRON_DART_P1(813, new Graphic(233, GraphicHeight.HIGH), 227, 4),
    IRON_DART_P2(5629, new Graphic(233, GraphicHeight.HIGH), 227, 4),
    IRON_DART_P3(5636, new Graphic(233, GraphicHeight.HIGH), 227, 4),
    STEEL_DART(808, new Graphic(234, GraphicHeight.HIGH), 228, 6),
    STEEL_DART_P1(814, new Graphic(234, GraphicHeight.HIGH), 228, 6),
    STEEL_DART_P2(5630, new Graphic(234, GraphicHeight.HIGH), 228, 6),
    STEEL_DART_P3(5637, new Graphic(234, GraphicHeight.HIGH), 228, 6),
    BLACK_DART(3093, new Graphic(273, GraphicHeight.HIGH), 34, 7),
    BLACK_DART_P1(3094, new Graphic(273, GraphicHeight.HIGH), 34, 7),
    BLACK_DART_P2(5631, new Graphic(273, GraphicHeight.HIGH), 34, 7),
    BLACK_DART_P3(5638, new Graphic(273, GraphicHeight.HIGH), 34, 7),
    MITHRIL_DART(809, new Graphic(235, GraphicHeight.HIGH), 229, 8),
    MITHRIL_DART_P1(815, new Graphic(235, GraphicHeight.HIGH), 229, 8),
    MITHRIL_DART_P2(5632, new Graphic(235, GraphicHeight.HIGH), 229, 8),
    MITHRIL_DART_P3(5639, new Graphic(235, GraphicHeight.HIGH), 229, 8),
    ADAMANT_DART(810, new Graphic(236, GraphicHeight.HIGH), 230, 13),
    ADAMANT_DART_P1(816, new Graphic(236, GraphicHeight.HIGH), 230, 13),
    ADAMANT_DART_P2(5633, new Graphic(236, GraphicHeight.HIGH), 230, 13),
    ADAMANT_DART_P3(5640, new Graphic(236, GraphicHeight.HIGH), 230, 13),
    RUNE_DART(811, new Graphic(237, GraphicHeight.HIGH), 231, 17),
    RUNE_DART_P1(817, new Graphic(237, GraphicHeight.HIGH), 231, 17),
    RUNE_DART_P2(5634, new Graphic(237, GraphicHeight.HIGH), 231, 17),
    RUNE_DART_P3(5641, new Graphic(237, GraphicHeight.HIGH), 231, 17),
    AMETHYST_DART(25849, new Graphic(1123, GraphicHeight.HIGH), 1384, 21), // TODO: DUMP DATA
    AMETHYST_DART_P1(25851, new Graphic(1123, GraphicHeight.HIGH), 1384, 21),
    AMETHYST_DART_P2(25855, new Graphic(1123, GraphicHeight.HIGH), 1384, 21),
    AMETHYST_DART_P3(25857, new Graphic(1123, GraphicHeight.HIGH), 1384, 21),
    DRAGON_DART(11230, new Graphic(1123, GraphicHeight.HIGH), 1122, 24),
    DRAGON_DART_P1(11231, new Graphic(1123, GraphicHeight.HIGH), 1122, 24),
    DRAGON_DART_P2(11233, new Graphic(1123, GraphicHeight.HIGH), 1122, 24),
    DRAGON_DART_P3(11234, new Graphic(1123, GraphicHeight.HIGH), 1122, 24),

    BRONZE_KNIFE(864, new Graphic(219, GraphicHeight.HIGH), 212, 3),
    BRONZE_KNIFE_P1(870, new Graphic(219, GraphicHeight.HIGH), 212, 3),
    BRONZE_KNIFE_P2(5654, new Graphic(219, GraphicHeight.HIGH), 212, 3),
    BRONZE_KNIFE_P3(5661, new Graphic(219, GraphicHeight.HIGH), 212, 3),

    IRON_KNIFE(863, new Graphic(220, GraphicHeight.HIGH), 213, 4),
    IRON_KNIFE_P1(871, new Graphic(220, GraphicHeight.HIGH), 213, 4),
    IRON_KNIFE_P2(5655, new Graphic(220, GraphicHeight.HIGH), 213, 4),
    IRON_KNIFE_P3(5662, new Graphic(220, GraphicHeight.HIGH), 213, 4),

    STEEL_KNIFE(865, new Graphic(221, GraphicHeight.HIGH), 214, 7),
    STEEL_KNIFE_P1(872, new Graphic(221, GraphicHeight.HIGH), 214, 7),
    STEEL_KNIFE_P2(5656, new Graphic(221, GraphicHeight.HIGH), 214, 7),
    STEEL_KNIFE_P3(5663, new Graphic(221, GraphicHeight.HIGH), 214, 7),

    BLACK_KNIFE(869, new Graphic(222, GraphicHeight.HIGH), 215, 8),
    BLACK_KNIFE_P1(874, new Graphic(222, GraphicHeight.HIGH), 215, 8),
    BLACK_KNIFE_P2(5658, new Graphic(222, GraphicHeight.HIGH), 215, 8),
    BLACK_KNIFE_P3(5665, new Graphic(222, GraphicHeight.HIGH), 215, 8),

    MITHRIL_KNIFE(866, new Graphic(223, GraphicHeight.HIGH), 215, 10),
    MITHRIL_KNIFE_P1(873, new Graphic(223, GraphicHeight.HIGH), 215, 10),
    MITHRIL_KNIFE_P2(5657, new Graphic(223, GraphicHeight.HIGH), 215, 10),
    MITHRIL_KNIFE_P3(5664, new Graphic(223, GraphicHeight.HIGH), 215, 10),

    ADAMANT_KNIFE(867, new Graphic(224, GraphicHeight.HIGH), 217, 14),
    ADAMANT_KNIFE_P1(875, new Graphic(224, GraphicHeight.HIGH), 217, 14),
    ADAMANT_KNIFE_P2(5659, new Graphic(224, GraphicHeight.HIGH), 217, 14),
    ADAMANT_KNIFE_P3(5666, new Graphic(224, GraphicHeight.HIGH), 217, 14),

    RUNE_KNIFE(868, new Graphic(225, GraphicHeight.HIGH), 218, 24),
    RUNE_KNIFE_P1(876, new Graphic(225, GraphicHeight.HIGH), 218, 24),
    RUNE_KNIFE_P2(5660, new Graphic(225, GraphicHeight.HIGH), 218, 24),
    RUNE_KNIFE_P3(5667, new Graphic(225, GraphicHeight.HIGH), 218, 24),

    DRAGON_KNIFE(22804, new Graphic(-1, GraphicHeight.HIGH), 28, 32),
    DRAGON_KNIFE_P1(22806, new Graphic(-1, GraphicHeight.HIGH), 697, 32),
    DRAGON_KNIFE_P2(22808, new Graphic(-1, GraphicHeight.HIGH), 697, 32),
    DRAGON_KNIFE_P3(22810, new Graphic(-1, GraphicHeight.HIGH), 697, 32),

    CHINCHOMPA(ItemID.CHINCHOMPA_2, null, 908, 10),
    RED_CHINCHOMPA(ItemID.RED_CHINCHOMPA_2, null, 909, 14),
    BLACK_CHINCHOMPA(ItemID.BLACK_CHINCHOMPA, null, 1272, 24),

    GUAM_TAR(ItemID.GUAM_TAR, new Graphic(953, GraphicHeight.HIGH), -1, 16),
    MARRENTILL_TAR(ItemID.MARRENTILL_TAR, new Graphic(952, GraphicHeight.HIGH), -1, 22),
    TARROMIN_TAR(ItemID.TARROMIN_TAR, new Graphic(952, GraphicHeight.HIGH), -1, 31),
    HARRALANDER_TAR(ItemID.HARRALANDER_TAR, new Graphic(952, GraphicHeight.HIGH), -1, 49),

    BRONZE_THROWNAXE(800, new Graphic(43, GraphicHeight.HIGH), 36, 7),
    IRON_THROWNAXE(801, new Graphic(42, GraphicHeight.HIGH), 35, 9),
    STEEL_THROWNAXE(802, new Graphic(44, GraphicHeight.HIGH), 37, 11),
    MITHRIL_THROWNAXE(803, new Graphic(45, GraphicHeight.HIGH), 38, 13),
    ADAMANT_THROWNAXE(804, new Graphic(46, GraphicHeight.HIGH), 39, 15),
    RUNE_THROWNAXE(805, new Graphic(48, GraphicHeight.HIGH), 41, 17),
    DRAGON_THROWNAXE(20849, new Graphic(1320, GraphicHeight.HIGH), 1319, 25),
    MORRIGANS_THROWNAXE(22634, new Graphic(1451, GraphicHeight.HIGH), 1452, 32),

    BRONZE_JAVELIN(825, null, 200, 25),
    BRONZE_JAVELIN_P1(831, null, 200, 25),
    BRONZE_JAVELIN_P2(5642, null, 200, 25),
    BRONZE_JAVELIN_P3(5648, null, 200, 25),
    IRON_JAVELIN(826, null, 201, 42),
    IRON_JAVELIN_P1(832, null, 201, 42),
    IRON_JAVELIN_P2(5643, null, 201, 42),
    IRON_JAVELIN_P3(5649, null, 201, 42),
    STEEL_JAVELIN(827, null, 202, 64),
    STEEL_JAVELIN_P1(833, null, 202, 64),
    STEEL_JAVELIN_P2(5644, null, 202, 64),
    STEEL_JAVELIN_P3(5650, null, 202, 64),
    MITHRIL_JAVELIN(828, null, 203, 85),
    MITHRIL_JAVELIN_P1(834, null, 203, 85),
    MITHRIL_JAVELIN_P2(5645, null, 203, 85),
    MITHRIL_JAVELIN_P3(5651, null, 203, 85),
    ADAMANT_JAVELIN(829, null, 204, 107),
    ADAMANT_JAVELIN_P1(835, null, 204, 107),
    ADAMANT_JAVELIN_P2(5646, null, 204, 107),
    ADAMANT_JAVELIN_P3(5652, null, 204, 107),
    RUNE_JAVELIN(830, null, 205, 124),
    RUNE_JAVELIN_P1(836, null, 205, 124),
    RUNE_JAVELIN_P2(5647, null, 205, 124),
    RUNE_JAVELIN_P3(5653, null, 205, 124),
    DRAGON_JAVELIN(19484, null, 1301, 150),
    DRAGON_JAVELIN_P1(19486, null, 1301, 150),
    DRAGON_JAVELIN_P2(19488, null, 1301, 150),
    DRAGON_JAVELIN_P3(19490, null, 1301, 150),
    AMETHYST_JAVELIN(21318, null, 1386, 157),
    AMETHYST_JAVELIN_P1(21320, null, 1386, 157),
    AMETHYST_JAVELIN_P2(21322, null, 1386, 157),
    AMETHYST_JAVELIN_P3(21324, null, 1386, 157),
    MORRIGANS_JAVELIN(22636, null, 1386, 162),

    TOKTZ_XIL_UL(6522, null, 442, 58),

    BOLT_RACK(4740, null, 27, 55),
    ;

    //Ammo that shouldn't be dropped on the floor
    private static final ImmutableSet<Ammunition> NO_GROUND_DROP = Sets.immutableEnumSet(
            CRAWS_ARROW, CRYSTAL_ARROW, BOLT_RACK, BROAD_ARROW,
            CHINCHOMPA, RED_CHINCHOMPA, BLACK_CHINCHOMPA,
            GUAM_TAR, MARRENTILL_TAR, TARROMIN_TAR, HARRALANDER_TAR,
            BRONZE_JAVELIN, BRONZE_JAVELIN_P1, BRONZE_JAVELIN_P2, BRONZE_JAVELIN_P3,
            IRON_JAVELIN, IRON_JAVELIN_P1, IRON_JAVELIN_P2, IRON_JAVELIN_P3,
            STEEL_JAVELIN, STEEL_JAVELIN_P1, STEEL_JAVELIN_P2, STEEL_JAVELIN_P3,
            ADAMANT_JAVELIN, ADAMANT_JAVELIN_P1, ADAMANT_JAVELIN_P2, ADAMANT_JAVELIN_P3,
            RUNE_JAVELIN, RUNE_JAVELIN_P1, RUNE_JAVELIN_P2, RUNE_JAVELIN_P3,
            DRAGON_JAVELIN, DRAGON_JAVELIN_P1, DRAGON_JAVELIN_P2, DRAGON_JAVELIN_P3,
            AMETHYST_JAVELIN, AMETHYST_JAVELIN_P1, AMETHYST_JAVELIN_P2, AMETHYST_JAVELIN_P3,
            MORRIGANS_JAVELIN
    );


    public static Ammunition[] DRAGON_ARROWS = {DRAGON_ARROW, DRAGON_ARROW_P1, DRAGON_ARROW_P2, DRAGON_ARROW_P3};
    public static Ammunition[] DIAMOND_BOLTS = {DIAMOND_BOLT, DIAMOND_DRAGON_BOLT, ENCHANTED_DIAMOND_BOLT, ENCHANTED_DIAMOND_DRAGON_BOLT};
    public static Ammunition[] ONYX_BOLTS = {ONYX_BOLT, ONYX_DRAGON_BOLT, ENCHANTED_ONYX_BOLT, ENCHANTED_ONYX_DRAGON_BOLT};

    public static Map<Integer, Ammunition> rangedAmmunition = new HashMap<>();

    static {
        for (Ammunition data : Ammunition.values()) {
            rangedAmmunition.put(data.getItemId(), data);
        }
    }

    private final Graphic startGfx;
    private final Graphic darkBowGfx;
    private final int itemId;
    private final int projectileId;
    private final int strength;

    Ammunition(int itemId, Graphic startGfx, int projectileId, int strength) {
        this(itemId, startGfx, null, projectileId, strength);
    }
    
    Ammunition(int itemId, Graphic startGfx, Graphic darkBowGfx, int projectileId, int strength) {
        this.itemId = itemId;
        this.startGfx = startGfx;
        this.darkBowGfx = darkBowGfx;
        this.projectileId = projectileId;
        this.strength = strength;
    }

    public static Ammunition getFor(Player p) {

        final RangedWeapon rangedWeapon = p.getCombat().getRangedWeapon();

        if(rangedWeapon == null)
            return null;

        final RangedWeaponType weaponType = rangedWeapon.getType();

        if (weaponType == RangedWeaponType.TOXIC_BLOWPIPE) {
            Optional<Integer> dartsUsed = Blowpipe.INSTANCE.getDartsUsed(p);
            if(dartsUsed.isPresent()) {
                return Ammunition.getFor(dartsUsed.get());
            } else {
                return Ammunition.BRONZE_DART;
            }
        } else if (weaponType == RangedWeaponType.SHAYZIEN_BLOWPIPE) {
            Optional<Integer> dartsUsed = ShayzienBlowpipe.INSTANCE.getDartsUsed(p);
            if(dartsUsed.isPresent()) {
                return Ammunition.getFor(dartsUsed.get());
            } else {
                return Ammunition.BRONZE_DART;
            }
        } else if (weaponType == RangedWeaponType.CRYSTAL_BOW)
            return Ammunition.CRYSTAL_ARROW;
        else if (weaponType == RangedWeaponType.CRAWS_BOW)
            return Ammunition.CRAWS_ARROW;

        final int weaponId = p.getEquipment().get(EquipmentConstants.WEAPON_SLOT).getId();

        final Ammunition throwWeapon = rangedAmmunition.get(weaponId);

        if (throwWeapon != null)
            return throwWeapon;

        final int ammunitionId = p.getEquipment().get(EquipmentConstants.AMMUNITION_SLOT).getId();
        return rangedAmmunition.get(ammunitionId);
    }

    public static Ammunition getFor(int item) {
        return rangedAmmunition.get(item);
    }

    public int getItemId() {
        return itemId;
    }

    public Graphic getStartGraphic() {
        return startGfx;
    }
    
    public Graphic getDarkBowGraphic() {
        return darkBowGfx;
    }

    public int getProjectileId() {
        return projectileId;
    }

    public int getStrength() {
        return strength;
    }

    public boolean dropOnFloor() {
        return !NO_GROUND_DROP.contains(this);
    }

    public int effectOccurrenceChance(boolean pvp, boolean diary){
        switch (this){
            case ENCHANTED_OPAL_BOLT:
            case ENCHANTED_OPAL_DRAGON_BOLT:
                return (int) (diary ? 5.5f : 5.0f);
            case ENCHANTED_JADE_BOLT:
            case ENCHANTED_JADE_DRAGON_BOLT:
            case ENCHANTED_PEARL_BOLT:
            case ENCHANTED_PEARL_DRAGON_BOLT:
                return (int) (diary ? 6.6f : 6.0f);
            case ENCHANTED_TOPAZ_BOLT:
            case ENCHANTED_TOPAZ_DRAGON_BOLT:
                return (int) (!pvp ? 0f : diary ? 4.4f : 4.0f);
            case ENCHANTED_SAPPHIRE_BOLT:
            case ENCHANTED_SAPPHIRE_DRAGON_BOLT:
                return (int) (!pvp ? 0f : diary ? 5.5f : 5.0f);
            case ENCHANTED_EMERALD_BOLT:
            case ENCHANTED_EMERALD_DRAGON_BOLT:
                return (int) (diary ? pvp ? 59.4f : 60.5f
                                        : pvp ? 55.0f : 54.0f);
            case ENCHANTED_RUBY_BOLT:
            case ENCHANTED_RUBY_DRAGON_BOLT:
                return (int) (diary ? pvp ? 12.1f : 6.6f
                                        : pvp ? 11.0f : 6.0f);
            case ENCHANTED_DIAMOND_BOLT:
            case ENCHANTED_DIAMOND_DRAGON_BOLT:
                return (int) (diary ? pvp ? 5.5f : 11.0f
                                        : pvp ? 5.0f : 10.0f);
            case ENCHANTED_DRAGON_BOLT:
            case ENCHANTED_DRAGON_DRAGON_BOLT:
                return (int) (diary ? 6.6f : 6.0f);
            case ENCHANTED_ONYX_BOLT:
            case ENCHANTED_ONYX_DRAGON_BOLT:
                return (int) (diary ? pvp ? 11.0f : 12.1f
                                        : pvp ? 10.0f : 11.0f);
            default:
//                System.err.println("No effect occurence chance defined for {"+name()+"}");
                return (int) 20f;
        }
    }

    /**
     * If the agent is a player, is using Zaryte crossbow, and has the Special attack attribute
     * @param agent
     * @return
     */
    public boolean handleZaryteSpec(Agent agent) {
        if (agent.isPlayer() && agent != null) {
            Player p = agent.getAsPlayer();
            if (p.getEquipment().contains(ItemID.ZARYTE_CROSSBOW)) {
                final boolean ZARYTE_SPEC = p.getAttributes().bool(Attribute.ZARYTE_CROSSBOW);
                if (ZARYTE_SPEC) {
                    p.getAttributes().reset(Attribute.ZARYTE_CROSSBOW);
                    return true;
                }
            }
        }
        return false;
    }
}
