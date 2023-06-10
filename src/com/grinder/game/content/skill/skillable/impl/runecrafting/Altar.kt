package com.grinder.game.content.skill.skillable.impl.runecrafting

enum class Altar(val rune : CraftableRune, val talisman : Talisman, val objectId : Int) {

    AIR(CraftableRune.AIR, Talisman.AIR, 34760),
    MIND(CraftableRune.MIND, Talisman.MIND, 34761),
    WATER(CraftableRune.WATER, Talisman.WATER, 34762),
    EARTH(CraftableRune.EARTH, Talisman.EARTH, 34763),
    FIRE(CraftableRune.FIRE, Talisman.FIRE, 34764),
    BODY(CraftableRune.BODY, Talisman.BODY, 34765),
    COSMIC(CraftableRune.COSMIC, Talisman.COSMIC, 34766),
    CHAOS(CraftableRune.CHAOS, Talisman.CHAOS, 34769),
    NATURE(CraftableRune.NATURE, Talisman.NATURE, 34768),
    LAW(CraftableRune.LAW, Talisman.LAW, 34767),
    DEATH(CraftableRune.DEATH, Talisman.DEATH, 34770),
    BLOOD(CraftableRune.BLOOD, Talisman.BLOOD, 27978),
    ASTRAL(CraftableRune.ASTRAL, Talisman.ASTRAL, 34771),
    WRATH(CraftableRune.WRATH, Talisman.WRATH, 34772)
    ;

    fun getName() = toString().toLowerCase().replace("_", " ")

    companion object {

        /**
         * Find the [Altar] corresponding to the object id, or null.
         */
        fun getAltarForObject(altar: Int) = values().find { it.objectId == altar }

    }
}