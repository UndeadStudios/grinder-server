package com.grinder.game.entity.agent.combat.formula;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-07-13
 */
public interface CombatFormula {

    double calculateMaxAttackRoll(CombatSnapshot snapshot, CombatSnapshot targetSnapshot);

    double calculateStrength(CombatSnapshot snapshot, CombatSnapshot targetSnapshot);

    double calculateMaxDefenceRoll(CombatSnapshot snapshot, CombatSnapshot targetSnapshot);

}
