package com.grinder.game.content.minigame.warriorsguild.tokens;

import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.npc.monster.Monster;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import org.jetbrains.annotations.NotNull;

/**
 * @author L E G E N D
 */
public final class AnimatedArmour extends Monster {

    public AnimatedArmour(Armour armour, Position position) {
        super(armour.getNpcId(), position);
    }

    private Armour armour;

    private Task lostTask;

    public void setArmour(Armour armour) {
        this.armour = armour;
    }

    public Armour getArmour() {
        return armour;
    }

    @Override
    public void appendDeath() {
        if (getOwner() != null){
            getOwner().getWarriorsGuild().setLastAnimatedArmour(null);
        }
        if (lostTask != null && lostTask.isRunning()) {
            lostTask.stop();
        }
        super.appendDeath();
    }

    public void setLostTask(Task task) {
        this.lostTask = task;
    }

    @Override
    public int attackRange(@NotNull AttackType type) {
        return 1;
    }
}
