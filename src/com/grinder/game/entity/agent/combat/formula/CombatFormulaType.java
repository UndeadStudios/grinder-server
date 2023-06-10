package com.grinder.game.entity.agent.combat.formula;

import com.grinder.game.content.skill.Skills;
import com.grinder.game.entity.agent.combat.attack.AttackContext;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponEffectType;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon;
import com.grinder.game.model.Skill;

/**
 * TODO: accuracy special attack bonuses (Sec 5.4 http://services.runescape.com/m=forum/forums.ws?317,318,712,65587452,goto,1)
 *
 * archive: https://www.rune-server.ee/runescape-development/rs2-server/informative-threads/688072-archive-combat-formulas.html
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-07-13
 */
public enum CombatFormulaType implements CombatFormula {

    MELEE {
        @Override
        public double calculateMaxAttackRoll(CombatSnapshot snapshot, CombatSnapshot targetSnapshot) {
            final double attackLevel = snapshot.getAttackLevel();

            return calculateAccuracyRoll(snapshot, targetSnapshot, calculateMaximumAccuracyRoll(snapshot, attackLevel));
        }

        @Override
        public double calculateStrength(CombatSnapshot snapshot, CombatSnapshot targetSnapshot) {

            final CombatBonuses bonuses = snapshot.getBonuses();
            final AttackContext details = snapshot.getContext();

            int maximumHit = bonuses.getCustomMaxHit()
                    .apply(snapshot)
                    .orElse(calculateMaximumBaseHit(snapshot, snapshot.getStrengthLevel()));

            double firstBonus = bonuses.getPrimarySpecialDamageBonus().apply(details);
            double secondBonus = bonuses.getSecondarySpecialDamageBonus().apply(targetSnapshot);

            if(details.used(SpecialAttackType.PENANCE)) {
                secondBonus = 1.0 + ((snapshot.getPrayerPointsLost() * 0.5) / 100);
            }

            maximumHit = (int) Math.round(firstBonus * (double) maximumHit);
            maximumHit = (int) Math.round(secondBonus * (double) maximumHit);

            if(!details.isDefilerSetEffectActivated()) {
                if(details.applyProtectivePVPDamageReduction()) {
                    maximumHit = (int) Math.round(0.6 * (double) maximumHit);
                }
            }

            final WeaponEffectType finalEffect = bonuses.getItemEffect();

            if(finalEffect == WeaponEffectType.VERACS) // There is also a guaranteed bonus of one extra damage, making it impossible to hit 0 when the effect occurs.
                maximumHit++;
            else {

                double tertiaryBonus = bonuses.getTertiarySpecialDamageBonus().apply(snapshot.getAgent(), snapshot);

                if(finalEffect == WeaponEffectType.DHAROKS)
                    tertiaryBonus = 1.0 + ((snapshot.getHitPointsLost() / 100D) * (snapshot.getMaxHitPoints()/100D));

                maximumHit = (int) Math.round(tertiaryBonus * (double) maximumHit);
            }

            // Twisted bow max hit cap
            if (details.used(RangedWeapon.TWISTED_BOW) && maximumHit > 89) {
                maximumHit = 89;
            }

            return maximumHit;
        }

        @Override
        public double calculateMaxDefenceRoll(CombatSnapshot snapshot, CombatSnapshot targetSnapshot) {

            double defenceLevel = targetSnapshot.getDefenceLevel();

            if (snapshot.isMagicalMelee()){
                defenceLevel *= 0.3;
                defenceLevel += targetSnapshot.getMagicLevel() * 0.7;
            }

            return calculateMaximumDefenceRoll(targetSnapshot, defenceLevel);
        }
    },
    RANGED {
        @Override
        public double calculateMaxAttackRoll(CombatSnapshot snapshot, CombatSnapshot targetSnapshot) {

            final double rangedLevel = snapshot.getRangedLevel();

            return calculateAccuracyRoll(snapshot, targetSnapshot, calculateMaximumAccuracyRoll(snapshot, rangedLevel));
        }

        @Override
        public double calculateStrength(CombatSnapshot snapshot, CombatSnapshot targetSnapshot) {

            final CombatBonuses bonuses = snapshot.getBonuses();
            final AttackContext details = snapshot.getContext();
            final Skills skills = snapshot.getSkills();
            final double rangedLevel = skills.getLevel(Skill.RANGED);

            int maximumHit = bonuses
                    .getCustomMaxHit()
                    .apply(snapshot)
                    .orElseGet(() -> {
                        int baseHit = calculateMaximumBaseHit(snapshot, rangedLevel);
                        final double firstBonus = bonuses.getPrimarySpecialDamageBonus().apply(details);
                        double secondBonus = bonuses.getSecondarySpecialDamageBonus().apply(targetSnapshot);

                        baseHit = (int) Math.round(firstBonus * (double) baseHit);
                        baseHit = (int) Math.round(secondBonus * (double) baseHit);
                        return baseHit;
                    });

            if(details.applyProtectivePVPDamageReduction())
                maximumHit = (int) Math.round(0.6 * (double) maximumHit);

            if (details.used(RangedWeapon.TWISTED_BOW) && maximumHit > 89)
                maximumHit = 89;



            return maximumHit;
        }

        @Override
        public double calculateMaxDefenceRoll(CombatSnapshot snapshot, CombatSnapshot targetSnapshot) {

            final double defenceLevel = targetSnapshot.getDefenceLevel();

            return calculateMaximumDefenceRoll(targetSnapshot, defenceLevel);
        }
    },
    MAGIC {
        @Override
        public double calculateMaxAttackRoll(CombatSnapshot snapshot, CombatSnapshot targetSnapshot) {

            final AttackContext context = snapshot.getContext();
            final double magicLevel = snapshot.getMagicLevel();

            if(context.isPlayer())
                return calculateAccuracyRoll(snapshot, targetSnapshot,
                        calculateMaximumAccuracyRoll(snapshot, magicLevel));

            final double attackRoll = snapshot.getStats().getAttackBonus(context);

            return (magicLevel + 9) * (attackRoll + 64.0);
        }

        @Override
        public double calculateStrength(CombatSnapshot snapshot, CombatSnapshot targetSnapshot) {
            final CombatBonuses bonuses = snapshot.getBonuses();
            final AttackContext details = snapshot.getContext();

            int maxHit = bonuses.getCustomMaxHit().apply(snapshot)
                    .orElse(calculateMaximumBaseHit(snapshot, snapshot.getMagicLevel()));

            double stackedBonus = bonuses.getMagicStrengthBonus().apply(details);
            double hitMultiplier = bonuses.getMagicMaxHitMultiplier().apply(details);

            maxHit = (int) Math.round((double) maxHit * stackedBonus);
            maxHit = (int) Math.round((double) maxHit * hitMultiplier);

            if(details.applyProtectivePVPDamageReduction())
                maxHit = (int) Math.round(0.6 * (double) maxHit);

            return maxHit;
        }

        @Override
        public double calculateMaxDefenceRoll(CombatSnapshot snapshot, CombatSnapshot targetSnapshot) {

            final AttackContext details = snapshot.getContext();

            final CombatBonuses targetBonuses = targetSnapshot.getBonuses();
            final CombatStats targetStats = targetSnapshot.getStats();
            final double magicLevel = targetSnapshot.getMagicLevel();

            if(targetSnapshot.getContext().isPlayer()){

                final double defenceLevel = targetSnapshot.getDefenceLevel();
                final double prayerAdjustment = targetBonuses.getPrayerDefenceAdjustment();
                final double stanceBonus = targetBonuses.getStanceDefenceBonus();
                final double defenceEffectiveLevel = (defenceLevel * prayerAdjustment) + stanceBonus + 8;
                final double equipmentBonus = targetStats.getDefenceBonus(details);
                final int baseRoll = Math.toIntExact(Math.round(defenceEffectiveLevel * 0.30));

                int effectiveLevel = Math.toIntExact(Math.round(magicLevel * targetBonuses.getPrayerMagicAdjustment()));
                effectiveLevel = (int) ((double) effectiveLevel * 0.70);
                effectiveLevel += baseRoll;

                return (double) effectiveLevel * (equipmentBonus + 64.0);

            } else {

                final double monsterMagicDefenceBonus = targetStats.getDefenceBonus(details);

                return (magicLevel + 8) * (monsterMagicDefenceBonus + 64.0);
            }
        }
    };

    public static CombatFormulaType getFormula(AttackType type){
        switch (type){
            case RANGED: return CombatFormulaType.RANGED;
            case MAGIC: return CombatFormulaType.MAGIC;
            default: return CombatFormulaType.MELEE;
        }
    }

    private static int calculateMaximumBaseHit(CombatSnapshot snapshot, double level) {
        final CombatStats stats = snapshot.getStats();
        final CombatBonuses bonuses = snapshot.getBonuses();
        final AttackContext details = snapshot.getContext();

        final double prayerAdjustment = bonuses.getPrayerStrengthAdjustment().apply(details);
        final double stanceBonus = bonuses.getStanceStrengthBonus();
        final double armourBonus = bonuses.getArmourStrengthBonus();
        final double effectiveLevel = armourBonus * ((level * prayerAdjustment) + stanceBonus + 8);
        final double equipmentBonus = stats.getStrengthBonus(details);

        return Math.toIntExact(Math.round(0.5D + effectiveLevel * (equipmentBonus + 64D) / 640D));
    }

    private static double calculateMaximumAccuracyRoll(CombatSnapshot snapshot, double level) {
        final CombatStats stats = snapshot.getStats();
        final CombatBonuses bonuses = snapshot.getBonuses();
        final AttackContext details = snapshot.getContext();

        final double prayerAdjustment = bonuses.getPrayerAccuracyAdjustment().apply(details);
        final double stanceBonus = bonuses.getStanceAccuracyBonus();
        final double armourBonus = bonuses.getArmourAccuracyBonus();
        final double effectiveLevel = armourBonus * ((level * prayerAdjustment) + stanceBonus + 8);
        final double equipmentBonus = stats.getAttackBonus(details);

        return (effectiveLevel * (equipmentBonus + 64.0));
    }

    private static double calculateMaximumDefenceRoll(CombatSnapshot targetSnapshot, double level) {

        final AttackContext context = targetSnapshot.getContext();
        final CombatStats targetStats = targetSnapshot.getStats();
        final CombatBonuses targetBonuses = targetSnapshot.getBonuses();

        final double prayerAdjustment = targetBonuses.getPrayerDefenceAdjustment();
        final double stanceBonus = targetBonuses.getStanceDefenceBonus();
        final double effectiveLevel = (level * prayerAdjustment) + stanceBonus + 8;
        final double equipmentBonus = targetStats.getDefenceBonus(context);
        return effectiveLevel * (equipmentBonus + 64.0);
    }

    public static double calculateAccuracyRoll(CombatSnapshot snapshot, CombatSnapshot targetSnapshot, double baseRoll) {
        final CombatBonuses bonuses = snapshot.getBonuses();
        final AttackContext context = snapshot.getContext();

        final double firstMod = bonuses.getPrimaryAccuracyModifier().apply(context);
        final double secondMod = bonuses.getSecondaryAccuracyModifier().apply(targetSnapshot);
        final double thirdMod = bonuses.getTertiaryAccuracyModifier().apply(snapshot.getAgent(), targetSnapshot);

        baseRoll = (int) Math.round(firstMod * baseRoll);
        baseRoll = (int) Math.round(secondMod * baseRoll);
        baseRoll = (int) Math.round(thirdMod * baseRoll);

        return baseRoll;
    }
}
