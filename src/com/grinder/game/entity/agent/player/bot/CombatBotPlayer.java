package com.grinder.game.entity.agent.player.bot;

import com.grinder.game.World;
import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.util.Misc;
import com.grinder.util.benchmark.SimpleBenchMarker;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 16/04/2020
 */
public class CombatBotPlayer extends BotPlayer {

    /**
     * Creates a new {@link BotPlayer}.
     *
     * @param username The username.
     * @param position
     */
    public CombatBotPlayer(String username, Position position) {
        super(username, position);
    }

    @Override
    public void sequence(SimpleBenchMarker benchMarker) {
        super.sequence(benchMarker);

        if(getSkillManager().getCurrentLevel(Skill.HITPOINTS) < 50) {
            heal(50);
        }

        if(!getCombat().isAttacking()){
            if(getMotion().isMoving())
                return;
            World.getRegions()
                    .fromPosition(getPosition())
                    .getEntities(EntityType.NPC)
                    .filter(e -> {
                        if(e instanceof NPC){
                            return !((NPC) e).getCombat().isBeingAttacked();
                        }
                        return false;
                    })
                    .min(Comparator.comparingInt(e -> e.getPosition().getDistance(getPosition())))
                    .ifPresent(entity -> {
                        if(entity instanceof NPC){
                            getMotion().followTarget((NPC) entity);
                            getCombat().initiateCombat((NPC) entity);
                        }
                    });
        } else {

            if(getCombat().getTimeSinceLastAttack(TimeUnit.SECONDS) > 10) {
                if(Misc.randomChance(20.0f)) {
                    getCombat().reset(true);
                    say("Resetting combat");
                }
            } else
                say("attacking "+getCombat().getTarget().getIndex());
        }
    }
}
