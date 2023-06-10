package com.grinder.game.entity.agent.npc.monster.pestcontrol;

import com.grinder.game.content.minigame.pestcontrol.PestControlDoorsManager;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.movement.Motion;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.Monster;
import com.grinder.game.model.Position;
import com.grinder.util.Misc;
import org.jetbrains.annotations.NotNull;

public class Torcher extends Monster {
    public Torcher(int id, @NotNull Position position) {
        super(id, position);
        this.fetchDefinition().setAggressive(false);
    }

    public void process(Position base, PestControlDoorsManager doorsManager, NPC voidKnight) {
        Position position = getPosition();
        Motion motion = getMotion();
        if (position.isWithinDistance(base.transform(7, 32, 0), 4)) {
            motion.traceTo(base.transform(18, 32 + Misc.random(1), 0));
        }
        else if (position.isWithinDistance(base.transform(22, 11, 0), 4) || position.isWithinDistance(base.transform(47, 14, 0), 5)) {
            motion.traceTo(base.transform(32 + Misc.random(1), 24, 0));
        }
        else if (position.isWithinDistance(base.transform(57, 30, 0), 4)) {
            motion.traceTo(base.transform(47, 32 + Misc.random(1), 0));
        }
        else {
            if (getCombat().isUnderAttack()) {
                getCombat().resetTarget();
                return;
            }
            if (voidKnight != null && !getCombat().isUnderAttack()) {
                getCombat().initiateCombat(voidKnight);
            }
        }
    }

    @Override
    public int attackRange(@NotNull AttackType type) {
        return 12;
    }
}
