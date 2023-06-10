package com.grinder.game.content.miscellaneous.spirittree

enum class SpiritTreeData(val spiritTreeId: Int, val spiritTreeTeleportData: Array<SpiritTreeTeleportData>) {
    TREE_GNOME_VILLAGE_TREE(1293, arrayOf<SpiritTreeTeleportData>(SpiritTreeTeleportData.TREE_GNOME_VILLAGE, SpiritTreeTeleportData.TREE_GNOME_STRONGHOLD, SpiritTreeTeleportData.BATTLEFIELD_OF_KHAZARD, SpiritTreeTeleportData.GRAND_EXCHANGE)),
    TREE_GNOME_STRONGHOLD_TREE(1294, arrayOf<SpiritTreeTeleportData>(SpiritTreeTeleportData.TREE_GNOME_VILLAGE, SpiritTreeTeleportData.TREE_GNOME_STRONGHOLD, SpiritTreeTeleportData.BATTLEFIELD_OF_KHAZARD, SpiritTreeTeleportData.GRAND_EXCHANGE)),
    BATTLEFIELD_OF_KHAZARD_TREE(1295, arrayOf<SpiritTreeTeleportData>(SpiritTreeTeleportData.TREE_GNOME_VILLAGE, SpiritTreeTeleportData.TREE_GNOME_STRONGHOLD, SpiritTreeTeleportData.BATTLEFIELD_OF_KHAZARD, SpiritTreeTeleportData.GRAND_EXCHANGE)),
    GRAND_EXCHANGE_TREE(1296, arrayOf<SpiritTreeTeleportData>(SpiritTreeTeleportData.TREE_GNOME_VILLAGE, SpiritTreeTeleportData.TREE_GNOME_STRONGHOLD, SpiritTreeTeleportData.BATTLEFIELD_OF_KHAZARD, SpiritTreeTeleportData.GRAND_EXCHANGE));

    companion object {
        private val map = values().associateBy(SpiritTreeData::spiritTreeId)
        fun fromId(type: Int) = map[type]
    }
}