package com.grinder.game.model

fun onFirstObjectAction(vararg ids: Int, function: (ObjectActions.ClickAction) -> Unit){
    ObjectActions.onClick(*ids){
        if(it.type == ObjectActions.ClickAction.Type.FIRST_OPTION) {
            function.invoke(it)
            return@onClick true
        }
        return@onClick false
    }
}

fun onSecondObjectAction(vararg ids: Int, function: (ObjectActions.ClickAction) -> Unit){
    ObjectActions.onClick(*ids){
        if(it.type == ObjectActions.ClickAction.Type.SECOND_OPTION) {
            function.invoke(it)
            return@onClick true
        }
        return@onClick false
    }
}