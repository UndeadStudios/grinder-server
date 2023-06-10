package com.grinder.game.content.minigame.chamberoxeric.room.icedemon;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.hit.Hit;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class IceDemonCombat implements AttackStrategy<Agent> {

    @Override
    public void animate(@NotNull Agent actor) {
   }

    @Override
    public void sequence(@NotNull Agent actor, @NotNull Agent target) {
    }

    @Override
    public void postHitAction(@NotNull Agent actor, @NotNull Agent target) {
        Position position = target.getPosition().clone();

        new Projectile(actor.getPosition(), position, 0, 1324, 55, 50, 43, 21, 0, 3, 0).sendProjectile();

        TaskManager.submit(new Task(4) {
            @Override
            protected void execute() {
                if(target.isPlayer()) {
                    Player p = target.getAsPlayer();

                    p.getPacketSender().sendGlobalGraphic(new Graphic(1325), position);

                    if(p.getPosition().sameAs(position)) {
                        p.getCombat().queue(Damage.create(20));
                    }
                }
                stop();
            }
        });
    }

    @Override
    public void postHitEffect(@NotNull Hit hit) {
    }

    @Override
    public void postIncomingHitEffect(@NotNull Hit hit) {
    }

    @Override
    public int duration(@NotNull Agent actor) {
        return 5;
    }

    @Override
    public int requiredDistance(@NotNull Agent actor) {
        return 8;
    }

    @Override
    public boolean canAttack(@NotNull Agent actor, @NotNull Agent target) {
        return true;
    }

    @NotNull
    @Override
    public Hit[] createHits(@NotNull Agent actor, @NotNull Agent target) {
        return new Hit[0];
    }

    @Nullable
    @Override
    public AttackType type() {
        return AttackType.RANGED;
    }
}
