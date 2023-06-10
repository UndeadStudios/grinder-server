package com.grinder.game.entity.agent.combat.attack.weapon.poison;

import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.slayer.Gargoyle;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.task.Task;
import com.grinder.util.NpcID;

/**
 * A {@link Task} implementation that handles the poisoning process.
 *
 * @author Professor Oak
 * @author Stan van der Bend
 */
public class PoisonEffectTask extends Task {

    private final Agent target;

    /**
     * Create a new {@link PoisonEffectTask}.
     *
     * @param target the entity being inflicted with poison.
     */
    public PoisonEffectTask(Agent target) {
        super(30, target,false);
        this.target = target;
        bind(target);
    }

    @Override
    public void execute(){

        if(!target.isRegistered() || stopCondition()) {
            stop();
            return;
        }

        sequence();
    }

    private boolean stopCondition() {
        return (target instanceof Gargoyle && target.getHitpoints() <= Gargoyle.HAMMER_HP_THRESHOLD) || !target.isPoisoned() || !target.getCombat().getPoisonImmunityTimer().finished();
    }

    private void sequence() {


        if(target instanceof Player){

            final Player targetPlayer = ((Player) target);

            final int lifePoints = target.getHitpoints();
            int poisonDamage = target.getPoisonDamage();

            if(targetPlayer.getDueling().inDuel() && lifePoints <= 0)
                target.setPoisonDamage(0);
            if (poisonDamage > lifePoints)
            	poisonDamage = lifePoints;

        }

        final int newPoisonDamage = target.decrementPoisonDamage();

        if (target instanceof NPC) {
            if (target.getAsNpc().fetchDefinition().getId() == NpcID.COMBAT_DUMMY || target.getAsNpc().fetchDefinition().getId() == NpcID.UNDEAD_COMBAT_DUMMY) {
                if (target.getPoisonDamage() >= 20) {
                    target.setPoisonDamage(0);
                }
            }
        }

        target.getCombat().queue(new Damage(newPoisonDamage, DamageMask.POISON));
        if (target.isPlayer()) {
        	AchievementManager.processFor(AchievementType.CONTAGIOUS_VENOM, newPoisonDamage, target.getAsPlayer());
        	AchievementManager.processFor(AchievementType.POISON_SPREE, newPoisonDamage, target.getAsPlayer());
        	AchievementManager.processFor(AchievementType.POISON_WOUND, newPoisonDamage, target.getAsPlayer());
        }

        if (newPoisonDamage <= 1)
            this.stop();
    }

    @Override
    public void stop() {
        target.setPoisonDamage(0);
        if (target.isPlayer()) {
            target.getAsPlayer().getPacketSender().sendOrbConfig();
        }
        super.stop();
    }

}
