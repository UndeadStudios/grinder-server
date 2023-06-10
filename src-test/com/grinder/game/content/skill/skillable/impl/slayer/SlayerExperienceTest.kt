package com.grinder.game.content.skill.skillable.impl.slayer

import com.grinder.GrinderTest

internal class SlayerExperience : GrinderTest() {

//    @Test
//    fun test(){
//        val npcs = NpcDefinition.definitions.values
//        val slayerNpcIds = ArrayList<Int>()
//
//        for (npc in npcs) {
//            val npcName = npc.name?.toLowerCase()?:continue
//            for (type in SlayerMonsterType.values()){
//                if (SlayerManager.isMonsterPartOfTask(type, npcName)){
//                    slayerNpcIds.add(npc.id)
//                    break
//                }
//            }
//        }
//
//        for (npc in slayerNpcIds.map { NpcDefinition.forId(it) }.sortedByDescending { it.hitpoints }){
//            val hp = npc.hitpoints
//            val oldExperience = npc.combatLevel * 2 * GameConstants.REGULAR_SKILLS_EXP_MULTIPLIER.toInt()
//            val newExperience = hp * GameConstants.REGULAR_SKILLS_EXP_MULTIPLIER.toInt()
//            println("NPC = {id = ${npc.id}, name = ${npc.name}} - Slayer experience = {old = $oldExperience, new = $newExperience, difference = ${abs(newExperience-oldExperience)}}")
//        }
//    }

}