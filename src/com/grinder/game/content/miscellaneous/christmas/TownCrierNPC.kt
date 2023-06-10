package com.grinder.game.content.miscellaneous.christmas

import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.util.NpcID

class TownCrierNPC : NPC(NpcID.TOWN_CRIER, Position(3094, 3502, 0)) {

    init {
        movementCoordinator.radius = 6
    }

    var cycle = 0
//    val shouts = arrayOf("Sell your items for quick cash in OSRS Store!", "Ding Ding!",
//            "Join the lottery if you feel you are lucky!", "Hoo hoo!",
//            "Enjoy your time at Grinderscape!")
val shouts = arrayOf("Did you complete your collection log stranger?", "Ding Ding!",
    "Join the lottery if you feel you are lucky!", "Hoo hoo!", "Gamble your angelic cape at ::dice for a colorful version!",
    "Did you have all your achievements completed? Goodluck I guess!", "::tourny gives 3x Experience, and Wilderness 2x when training any skill",
    "Enjoy your time at Grinderscape!")

    override fun sequence() {
        super.sequence()

        if(++cycle % 60 == 0){
            performAnimation(Animation(6865))
            say(shouts.random())
        }
        if(cycle >= Integer.MAX_VALUE)
            cycle = 0
    }

}