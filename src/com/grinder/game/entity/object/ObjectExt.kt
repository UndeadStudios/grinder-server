package com.grinder.game.entity.`object`

fun GameObject.name() = definition?.name?.toLowerCase()?:"null-$id"