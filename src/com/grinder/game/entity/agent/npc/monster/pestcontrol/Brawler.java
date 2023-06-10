package com.grinder.game.entity.agent.npc.monster.pestcontrol;

import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.monster.Monster;
import com.grinder.game.model.Position;
import org.jetbrains.annotations.NotNull;

public class Brawler extends Monster {
    public Brawler(int id, @NotNull Position position) {
        super(id, position);
        this.fetchDefinition().setAggressive(true);
    }

    @Override
    public int attackRange(@NotNull AttackType type) {
        return 1;
    }
}
