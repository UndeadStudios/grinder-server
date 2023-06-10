package com.grinder.util.debug;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.Combat;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.boss.impl.hydra.AlchemicalHydraBoss;
import com.grinder.game.entity.agent.player.Player;

import java.util.Optional;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-27
 */
public enum DebugType implements DebugProvider {

    NONE {
        @Override
        public Optional<DebugListener> provide(Player player) {
            return Optional.empty();
        }
    },
    COMBAT {
        @Override
        public Optional<DebugListener> provide(final Player player) {

            for(NPC npc : player.getLocalNpcs())
                player.getPacketSender().sendNpcDebug(npc, true);

            if(player.getCombat().getTarget() instanceof AlchemicalHydraBoss)
                return Optional.ofNullable(((AlchemicalHydraBoss) player.getCombat().getTarget()));

            final Optional<DebugListener> optionalCombat = Optional.of(player.getCombat()).map(Combat::getOpponent).map(Agent::getCombat);

            if(optionalCombat.isPresent()){
                return optionalCombat;
            }
            final int secondsTillTolerant = player.getAggressionTolerance().secondsRemaining();

            return Optional.of(() -> new String[]{
                    "Not in combat",
                    "Area: "+Optional.ofNullable(player.getArea()).map(Object::toString).orElse("UNDEFINED"),
                    "Aggression: "+(secondsTillTolerant > 0 ? secondsTillTolerant+"s" : "TOLERANT"),
                    "NPCs in area: "+(player.getLocalNpcs().size()),
                    "Strategy: " + (player.getCombat().determineStrategy().getClass().getSimpleName()),
                    "",
                    "",
                    ""
            });
        }
    }

}
