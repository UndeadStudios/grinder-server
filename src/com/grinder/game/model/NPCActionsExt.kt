package com.grinder.game.model

import com.grinder.game.model.NPCActions.ClickAction.Type

fun onFirstNPCAction(vararg ids: Int, function: NPCActions.ClickAction.() -> Unit){
    onNPCAction(*ids, type =  Type.FIRST_OPTION, function = function)
}

fun onSecondNPCAction(vararg ids: Int, function: NPCActions.ClickAction.() -> Unit){
    onNPCAction(*ids, type =  Type.SECOND_OPTION, function = function)
}

fun onThirdNPCAction(vararg ids: Int, function: NPCActions.ClickAction.() -> Unit){
    onNPCAction(*ids, type =  Type.THIRD_OPTION, function = function)
}

fun onFourthNPCAction(vararg ids: Int, function: NPCActions.ClickAction.() -> Unit){
    onNPCAction(*ids, type =  Type.FOURTH_OPTION, function = function)
}

fun onNPCAction(vararg ids: Int, type: Type, function: NPCActions.ClickAction.() -> Unit){
    NPCActions.onClick(*ids){
        if(it.type == type) {
            function.invoke(it)
            return@onClick true
        }
        return@onClick false
    }
}