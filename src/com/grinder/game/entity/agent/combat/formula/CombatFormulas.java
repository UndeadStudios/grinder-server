package com.grinder.game.entity.agent.combat.formula;

import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponEffectType;
import com.grinder.game.entity.agent.combat.misc.CombatEquipment;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.util.Misc;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles the rolling of base hit damage and hit accuracy.
 *
 * !NOTE! Please do not modify this class without contacting me first,
 *        this class should not be modified, everything related to damage and accuracy
 *        can and should be handled elsewhere.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-07-16
 */
public final class CombatFormulas {

    public static boolean rollAccuracy(CombatSnapshot snapshot, CombatSnapshot targetSnapshot, AttackType type){

        final CombatFormulaType formulaType = CombatFormulaType.getFormula(type);

        int maxAttackRoll = (int) formulaType.calculateMaxAttackRoll(snapshot, targetSnapshot);
        final int maxDefenceRoll = (int) (formulaType.calculateMaxDefenceRoll(snapshot, targetSnapshot)
                * snapshot.getBonuses().getDefenceRollModifier().apply(targetSnapshot));

        if (snapshot.getAgent() != null && snapshot.getAgent().isNpc() && snapshot.getAgent().getAsNpc().fetchDefinition().getCombatLevel() <= 200) {
            maxAttackRoll = (int) (maxAttackRoll / 1.5); // Cheap fix for npcs hitting too often
        }

        final double accuracy = maxAttackRoll > maxDefenceRoll
                ? 1.0 - (maxDefenceRoll + 2.0) / (2.0 * (maxAttackRoll + 1.0))
                : maxAttackRoll / (2.0 * (maxDefenceRoll+1));

        double randomRoll;
        try {
            randomRoll = SecureRandom.getInstanceStrong().nextDouble();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("CombatFormulas: "+e.getMessage());
            randomRoll = Misc.getRandomDouble();
        }

       final boolean hit = accuracy >= randomRoll;

        String color = hit ? "@gre@" : "@red@";
        // COMBAT DEBUG ATTACK ACCURACY
//            target.ifPlayer(player -> player.sendMessage("@bla@T: Accuracy[@gre@" + maxAttackRoll + " vs " + maxDefenceRoll + "@bla@] -> " + accuracy));
//        int finalMaxAttackRoll = maxAttackRoll;
//        targetSnapshot.getAgent().ifPlayer(player -> player.sendMessage("@mag@Defence:@bla@ [@whi@"+ finalMaxAttackRoll +"@bla@ vs @whi@"+maxDefenceRoll+"@bla@]"+color+" -> @mag@"+accuracy));
//        snapshot.getAgent().ifPlayer(player -> player.sendMessage("@yel@Accuracy:@bla@ [@whi@"+maxAttackRoll+"@bla@ vs @whi@"+maxDefenceRoll+"@bla@]"+color+" -> @yel@"+ accuracy));
        return hit;
    }

    public static boolean rollAccuracy(int maxAttackRoll, int maxDefenceRoll) {
        double accuracy = maxAttackRoll > maxDefenceRoll
                ? 1.0 - (maxDefenceRoll + 2.0) / (2.0 * (maxAttackRoll + 1.0))
                : maxAttackRoll / (2.0 * (maxDefenceRoll + 1));

        double randomRoll;
        try {
            randomRoll = SecureRandom.getInstanceStrong().nextDouble();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("CombatFormulas: " + e.getMessage());
            randomRoll = Misc.getRandomDouble();
        }

        return accuracy >= randomRoll;
    }

    public static int generateHit(CombatSnapshot snapshot, CombatSnapshot targetSnapshot, AttackType type){
        return generateHit(snapshot, targetSnapshot, type, 1.0D);
    }

    public static int generateHit(CombatSnapshot snapshot,
                                  CombatSnapshot targetSnapshot,
                                  AttackType type,
                                  double maxMultiplier){
        final CombatFormulaType formulaType = CombatFormulaType.getFormula(type);
        int maxDamage = (int) Math.floor(formulaType.calculateStrength(snapshot, targetSnapshot) * maxMultiplier);
        int hit = Misc.inclusive(0, maxDamage);
        return hit;
    }
}
