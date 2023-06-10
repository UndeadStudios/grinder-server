package com.grinder.game.content.skill.skillable.impl.farming

import java.util.*

/**
 * This enum holds the data for each different plant type.
 *
 * @author Austin
 */
enum class PlantType(var plants: ArrayList<Plant>) {

    ALLOTMENT(Plant.getAllotments()),
    HOP(Plant.getHops()),
    TREE(Plant.getTrees()),
    FRUIT_TREE(Plant.getFruits()),
    BUSH(Plant.getBushes()),
    FLOWER(Plant.getFlowers()),
    HERB(Plant.getHerbs());

    companion object {

        fun getPlant(seedId: Int): Plant? {
            return Plant.values().find { it.seed.id == seedId }
        }
    }
}