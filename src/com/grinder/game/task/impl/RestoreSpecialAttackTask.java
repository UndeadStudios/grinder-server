package com.grinder.game.task.impl;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.task.Task;

/**
 * A {@link Task} implementation which handles
 * the regeneration of special attack.
 *
 * @author Professor Oak
 */
public class RestoreSpecialAttackTask extends Task {

    private final Agent agent;

    public RestoreSpecialAttackTask(Agent agent) {
        super(20, agent, false);
        this.agent = agent;
        agent.setRecoveringSpecialAttack(true);
    }

    @Override
    public void execute() {

        if (agent == null || !agent.isRegistered() || agent.getSpecialPercentage() >= 100 || !agent.isRecoveringSpecialAttack()) {
            stop();
            return;
        }

        agent.incrementSpecialPercentage(5);

        final int amount = agent.getSpecialPercentage();

        if (agent instanceof Player) {

            final Player player = (Player) agent;

            SpecialAttackType.updateBar(player, true);

            if (amount % 25 == 0) {
                player.getPacketSender().sendMessage("Your special attack energy is now " + amount + "%.");
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        if(agent != null)
            agent.setRecoveringSpecialAttack(false);
    }
}