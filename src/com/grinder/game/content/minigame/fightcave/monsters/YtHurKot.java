package com.grinder.game.content.minigame.fightcave.monsters;

import com.grinder.game.content.minigame.fightcave.FightCaveNpc;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.model.*;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.Priority;

/**
 * @author L E G E N D
 */
public final class YtHurKot extends FightCaveNpc implements AttackProvider {

    public YtHurKot(int id, Position position) {
        super(id, position);
    }

    public void activate(TzTokJad jad) {
        TaskManager.submit(new Task(5) {
            @Override
            protected void execute() {
                if (!isActive()){
                    stop();
                    return;
                }
                if (getMotion().getTarget() == jad) {
                    if (jad.getHitpoints() < jad.getSkills().getMaximumLevel(Skill.HITPOINTS))
                        if (getPosition().getDistance(jad.getPosition()) <= 2)
                            if (Misc.random(2) % 2 == 0) {
                                jad.heal(10, 25);
                                performGraphic(new Graphic(444, 0, GraphicHeight.HIGH, Priority.HIGH));
                                performAnimation(new Animation(2639, 0));
                            }
                } else {
                    stop();
                }
            }
        });
    }
}
