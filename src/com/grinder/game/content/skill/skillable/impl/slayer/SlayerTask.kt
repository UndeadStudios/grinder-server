package com.grinder.game.content.skill.skillable.impl.slayer

/**
 * The slayer task
 *
 * @author 2012
 */
class SlayerTask(
        var name: String,
        var master: SlayerMaster,
        var monster: SlayerMonsterType,
        var amountLeft: Int,
        var initialAmount: Int)