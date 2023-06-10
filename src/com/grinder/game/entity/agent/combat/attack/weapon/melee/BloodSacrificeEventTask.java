package com.grinder.game.entity.agent.combat.attack.weapon.melee;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.model.Graphic;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.timing.TimerKey;

/**
 * Handles the Ancient Godsword's Blood Sacrifice special attack's damaging task.
 * @author R-Y-M-R
 * @date 5/25/2022
 * @see <a href="https://www.rune-server.ee/members/necrotic/">RuneServer</a>
 */
public class BloodSacrificeEventTask extends Task {
    private final Agent target;

    public BloodSacrificeEventTask(Agent target) {
        super(0, target, true);
        this.target = target;
        bind(target);
    }

    @Override
    public void execute() {
        if (!target.isRegistered() || stopCondition()) {
            stop();
            return;
        }
        sequence();
    }

    /**
     * Contains stop condiitons for when the function is done.
     * @return true if target is null, dead, or the timer is out.
     */
    private boolean stopCondition() {
        if (target == null) {
            return true;
        }
        if (!target.isAlive()) {
            return true;
        }
        return !target.getTimerRepository().has(TimerKey.BLOOD_SACRIFICE_TIMER);
    }

    /**
     * Creates the Damaging Event on a 8 tick delay. Deals damage if within 5 tiles to the attacker.
     */
    private void sequence() {
        Agent attacker = getOpponent();
        if (attacker != null) {
            TaskManager.submit(new Task(8) {
                @Override
                protected void execute() {
                    if (target == null) {
                        stop();
                        return;
                    }
                    if (!target.isAlive() || !attacker.isAlive()) {
                        stop();
                        return;
                    }
                    if (target.getPosition().getDistance(attacker.getPosition()) < 5) {
                        target.performGraphic(new Graphic(377));
                        final int damage = Math.min(25, target.getHitpoints());
                        target.getCombat().queue(new Damage(damage, DamageMask.REGULAR_HIT));
                        attacker.heal(damage);
                    }
                    stop();
                }
            });
            this.stop();
        }
    }

    /**
     * Attempts to get the opponent for combat
     * @return Agent
     */
    private Agent getOpponent() {
        Agent p = target.getCombat().getTarget();
        if (p == null) {
            p = target.getCombat().getOpponent();
        }
        return p;
    }

}