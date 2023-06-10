package com.grinder.game.content.skill.skillable.impl.slayer.superior;

import com.grinder.game.World;
import com.grinder.game.entity.agent.AgentUtil;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.FacingDirection;
import com.grinder.game.model.Position;
import com.grinder.util.Misc;

/**
 * @author Luka Furlan, luka.furlan9@gmail.com
 * @since 14/7/2020
 */
public class SuperiorSlayerMonster extends NPC {

    private final int AREA_LEAVE_DESPAWN_TICKS = Misc.getTicks(30);
    private final int NO_ACTIVITY_DESPAWN_TICKS = Misc.getTicks(120);

    private int despawnCounter = 0;
    private boolean despawned = false;

    /**
     * Constructs a new npc.
     * <p>
     * Please do not use the constructor directly.
     *
     * @param id       The npc id.
     * @param position The npc spawn (default) {@link Position}.
     */
    public SuperiorSlayerMonster(int id, Position position, Player player) {
        super(id, position);
        setOwner(player);
    }

    @Override
    public void sequence() {
        super.sequence();
        if (!despawned)
            processLifetime();
    }

    private void processLifetime() {
        Player player = this.getOwner();

        if (player == null || !player.isActive()) {
            despawn();
            return;
        }

        if (getCombat().isInCombat()) {
            despawnCounter = 0;
            return;
        }

        int ownerRegionId = AgentUtil.getRegionID(player);
        int npcRegionId = AgentUtil.getRegionID(this);

        if (((ownerRegionId != npcRegionId)
                && despawnCounter >= AREA_LEAVE_DESPAWN_TICKS)
                || despawnCounter >= NO_ACTIVITY_DESPAWN_TICKS) {
            despawn();
            return;
        }

        despawnCounter++;
    }

    private void despawn() {
        despawned = true;
        World.getNpcRemoveQueue().add(this);
    }
}
