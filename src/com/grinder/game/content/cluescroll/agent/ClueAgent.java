package com.grinder.game.content.cluescroll.agent;

import com.grinder.game.World;
import com.grinder.game.content.cluescroll.ClueConstants;
import com.grinder.game.content.cluescroll.task.ClueTaskAgent;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.content.cluescroll.task.ClueTask;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Priority;

import java.util.function.Predicate;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-10
 */
public class ClueAgent extends NPC {

    private final boolean combative;
    private Predicate<Player> onDeathStart;
    private ClueTask task;
    private int lifeSpan;
    private boolean disposed;

    public ClueAgent(boolean combative, int id, Position position) {
        super(id, position);
        this.combative = combative;
    }

    @Override
    public void onAdd() {
        super.onAdd();
        if (combative)
            getCombat().initiateCombat(getOwner());
    }

    @Override
    public void sequence() {
        super.sequence();
        processLifeSpan();
    }

    @Override
    public void appendDeath() {
        setDying(true);
        onDeathStart.test(getOwner());
        TaskManager.submit(2, () -> {
            int deathEmote = fetchDefinition().getDeathAnim();

            if (deathEmote > 0)
                performAnimation(new Animation(deathEmote, Priority.HIGH));

            final Agent killer = getCombat().findKiller(false).orElse(null);
            if (killer instanceof Player)
                sendDeathSound((Player) killer);
        });
        TaskManager.submit(4, this::dispose);
    }

    /**
     * Process this NPC life span, in case the life span value is positive and
     * decreases to negative, the NPC will be removed from scene and disposed.
     * <br>
     * In case the NPC owner is no longer available, the NPC will be removed
     * from the scene and disposed.
     **/
    protected void processLifeSpan() {
        if(!disposed && !isDying()) {
            if (reducedLifeSpan() <= 0
                    || getOwner() == null
                    || !getOwner().isActive()) {
                dispose();
            }
        }
    }

    private int reducedLifeSpan() {
        return --lifeSpan;
    }

    /**
     * Remove this NPC from scene and NPC container's index.
     */
    public final void dispose() {
        disposed = true;
        World.getNpcRemoveQueue().add(this);
    }

    /**
     * Set a death start predicate
     */
    public void onDeathStart(Predicate<Player> onDeathStart) {
        this.onDeathStart = onDeathStart;
    }

    public void setTask(ClueTask task) {
        this.task = task;
    }

    public ClueTask getTask() {
        return task;
    }

    public void setLifeSpan(int lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    public boolean isCombative() {
        return combative;
    }
}
