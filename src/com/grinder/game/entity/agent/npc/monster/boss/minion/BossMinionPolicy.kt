package com.grinder.game.entity.agent.npc.monster.boss.minion

/**
 * Represents policies that can be provided to [minions][BossMinion].
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-19
 */
enum class BossMinionPolicy {

    /**
     * Never respawn the minion.
     */
    NO_RESPAWN,

    /**
     * Respawn the minion when their [BossMinion.bossNPC] respawns.
     */
    RESPAWN_ON_BOSS_SPAWN,

    /**
     * Remove the minion if their [BossMinion.bossNPC] was removed.
     */
    REMOVE_WHEN_BOSS_REMOVED,

    /**
     * Aggravate the minion to attack the preferred opponent of their [BossMinion.bossNPC].
     */
    ATTACK_PREFERRED_OPPONENT
}