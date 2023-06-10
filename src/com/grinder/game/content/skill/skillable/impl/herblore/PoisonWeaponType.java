package com.grinder.game.content.skill.skillable.impl.herblore;

public enum PoisonWeaponType {

    BRONZE_DAGGER(1205, 1221, 5670, 5688, 1),
    IRON_DAGGER(1203, 1219, 5668, 5686, 1),
    STEEL_DAGGER(1207, 1223, 5672, 5690, 1),
    BLACK_DAGGER(1217, 1233, 5682, 5700, 1),
    MITHRIL_DAGGER(1209, 1225, 5674, 5692, 1),
    ADAMANT_DAGGER(1211, 1227, 5676, 5694, 1),
    RUNE_DAGGER(1213, 1229, 5678, 5696, 1),
    DRAGON_DAGGER(1215, 1231, 5680, 5698, 1),
    WHITE_DAGGER(6591, 6593, 6595, 6597, 1),
    BONE_DAGGER(8872, 8874, 8876, 8878, 1),
    ABYSSAL_DAGGER(13265, 13267, 13269, 13271, 1),

    BRONZE_SPEAR(1237, 1251, 5704, 5718, 1),
    IRON_SPEAR(1239, 1253, 5706, 5720, 1),
    STEEL_SPEAR(1241, 1255, 5708, 5722, 1),
    BLACK_SPEAR(4580, 4582, 5734, 5736, 1),
    MITHRIL_SPEAR(1243, 1257, 5710, 5724, 1),
    ADAMANT_SPEAR(1245, 1259, 5712, 5726, 1),
    RUNE_SPEAR(1247, 1261, 5714, 5728, 1),
    DRAGON_SPEAR(1249, 1263, 5716, 5730, 1),

    BRONZE_HASTA(11367, 11379, 11382, 11384, 1),
    IRON_HASTA(11369, 11386, 11389, 11391, 1),
    STEEL_HASTA(11371, 11393, 11396, 11398, 1),
    MITHRIL_HASTA(11373, 11400,  11403, 11405, 1),
    ADAMANT_HASTA(11375, 11407, 11410, 11412, 1),
    RUNE_HASTA(11377, 11414, 11417, 11419, 1),
    DRAGON_HASTA(22731, 22734, 22737, 22740, 1),

    BRONZE_ARROW(882, 883, 5616, 5622, 5),
    IRON_ARROW(884, 885, 5617, 5623, 5),
    STEEL_ARROW(886, 887, 5618, 5624, 5),
    MITHRIL_ARROW(888, 889, 5619, 5625, 5),
    ADAMANT_ARROW(890, 891, 5620, 5626, 5),
    RUNE_ARROW(892, 893, 5621, 5627, 5),
    DRAGON_ARROW(11212, 11227, 11228, 11229, 5),
    AMETHYST_ARROW(21326, 21332, 21334, 21336, 5),

    BRONZE_BOLTS(877, 878, 6061, 6062, 5),
    IRON_BOLTS(9140, 9287, 9294, 9301, 5),
    STEEL_BOLTS(9141, 9288, 9295, 9302, 5),
    MITHRIL_BOLTS(9142, 9289, 9296, 9303, 5),
    ADAMANT_BOLTS(9143, 9290, 9297, 9304, 5),
    RUNITE_BOLTS(9144, 9291, 9298, 9305, 5),
    SILVER_BOLTS(9145, 9292, 9299, 9306, 5),
    BLURITE_BOLTS(9139, 9286, 9293, 9300, 5),
    DRAGON_BOLTS(21905, 21924, 21926, 21928, 5),

    BRONZE_DART(806, 812, 5628, 5635, 5),
    IRON_DART(807, 813, 5629, 5636, 5),
    STEEL_DART(808, 814, 5630, 5637, 5),
    BLACK_DART(3093, 3094, 5631, 5638, 5),
    MITHRIL_DART(809, 815, 5632, 5639, 5),
    ADAMANT_DART(810, 816, 5633, 5640, 5),
    RUNE_DART(811, 817, 5634, 5641, 5),
    AMETHYST_DART(25849, 25851, 25855, 25857, 5),
    DRAGON_DART(11230, 11231, 11233, 11234, 5),

    BRONZE_KNIFE(864, 870, 5654, 5661, 5),
    IRON_KNIFE(863, 871, 5655, 5662, 5),
    STEEL_KNIFE(865, 872, 5656, 5663, 5),
    BLACK_KNIFE(869, 874, 5658, 5665, 5),
    MITHRIL_KNIFE(866, 873, 5657, 5664, 5),
    ADAMANT_KNIFE(867, 875, 5659, 5666, 5),
    RUNE_KNIFE(868, 876, 5660, 5667, 5),
    DRAGON_KNIFE(22804, 22806, 22808, 22810, 5),

    BRONZE_JAVELIN(825, 831, 5642, 5648, 5),
    IRON_JAVELIN(826, 832, 5643, 5649, 5),
    STEEL_JAVELIN(827, 833, 5644, 5650, 5),
    MITHRIL_JAVELIN(828, 834, 5645, 5651, 5),
    ADAMANT_JAVELIN(829, 835, 5646, 5652, 5),
    RUNE_JAVELIN(830, 836, 5647, 5653, 5),
    DRAGON_JAVELIN(19484, 19486, 19488, 19490, 5),
    AMETHYST_JAVELIN(21318, 21320, 21322, 21324, 5);


    private final int weaponId;

    private final int poisonId;

    private final int poisonPlusId;

    private final int poisonPlusPlusId;

    private final int amount;

    PoisonWeaponType(final int weaponId, final int poisonId, final int poisonIdPlus, final int poisonIdPlusPlus, final int amount) {
        this.weaponId = weaponId;
        this.poisonId = poisonId;
        this.poisonPlusId = poisonIdPlus;
        this.poisonPlusPlusId = poisonIdPlusPlus;
        this.amount = amount;
    }

    public int getWeaponId() {
        return weaponId;
    }

    public int getPoisonId() {
        return poisonId;
    }

    public int getPoisonPlusId() {
        return poisonPlusId;
    }

    public int getPoisonPlusPlusId() {
        return poisonPlusPlusId;
    }

    public int getAmount() {
        return amount;
    }

}


