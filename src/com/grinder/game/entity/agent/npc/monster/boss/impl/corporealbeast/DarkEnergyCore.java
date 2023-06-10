package com.grinder.game.entity.agent.npc.monster.boss.impl.corporealbeast;

import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.monster.Monster;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import org.jetbrains.annotations.NotNull;

/**
 * @author L E G E N D
 * @date 2/24/2021
 * @time 3:45 AM
 * @discord L E G E N D#4380
 */

@SuppressWarnings("unused")
public final class DarkEnergyCore extends Monster {

    private final ProjectileTemplate PROJECTILE_TEMPLATE = ProjectileTemplate.builder(319)
            .setStartHeight(0).setEndHeight(0).setCurve(25).setSpeed(100).setDelay(50).build();

    private final CorporealBeastBoss corp;
    private Task task;
    private boolean destroyed;

    public DarkEnergyCore(CorporealBeastBoss corp) {
        super(NpcID.DARK_ENERGY_CORE, corp.getPosition());
        this.corp = corp;
        getMotion().update(MovementStatus.DISABLED);
    }

    @Override
    public void sequence() {
        super.sequence();
        final var player = getBestCandidate();
        if (player == null) {
            return;
        }
        var distance = Misc.getDistance(player.getPosition(), getPosition());
        if (distance < 2) {
            var damage = (int) (Misc.random(15) * (2 - distance / 2.0));
            corp.heal(damage);
            player.getCombat().queue(Damage.createPoisonHit(damage));
        } else {
            if (!corp.getCombat().isUnderAttack()) {
                return;
            }
            if (isFlying() || !visibilityProperty().get()) {
                skipNextCombatSequence();
                return;
            }
            TaskManager.submit(task = new Task(1) {
                @Override
                protected void execute() {
                    TaskManager.submit(1, () -> setVisible(false));
                    var projectile = new Projectile(getPosition(), player.getPosition(), PROJECTILE_TEMPLATE);
                    projectile.sendProjectile();
                    projectile.onArrival(() -> {
                        if (!getPosition().equals(projectile.getTarget())) {
                            moveTo(projectile.getTarget());
                        }
                        setEntityInteraction(player);
                        setVisible(true);
                    });
                    stop();
                }
            });
            TaskManager.submit(3, () -> task = null);
        }
    }

    public void kill() {
        super.appendDeath();
    }

    @Override
    public void appendDeath() {
        if (isFlying() && (getInteractingEntity() != null && !isWithinDistance(getInteractingEntity(), 2))) {
            destroyed = true;
        }
        if (!destroyed) {
            if (corp.isAlive()) {
                TaskManager.submit(Misc.random(75), this::respawn);
            }
        }
        super.appendDeath();
    }

    @Override
    public int attackRange(@NotNull AttackType type) {
        return 1;
    }

    public Player getBestCandidate() {
        var playersInCave = AreaManager.getPlayers(new Boundary(corp.getX() - 20, corp.getX() + 20, corp.getY() - 20, corp.getY() + 20));
        Player player = getAsPlayer();
        if (player == null) {
            return null;
        }
        if (playersInCave.length == 0) {
            return null;
        }
        if (playersInCave.length == 1) {
            player = playersInCave[0];
        }

        for (var playerInside : playersInCave) {
            if (player == playerInside) {
                continue;
            }
            assert player != null;
            if (player.getX() < 2974) {
                continue;
            }
            if (playerInside.getY() > player.getY() || playerInside.getX() > player.getX()) {
                player = playerInside;
            }
        }
        return player;
    }

    public boolean isFlying() {
        return task != null;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
