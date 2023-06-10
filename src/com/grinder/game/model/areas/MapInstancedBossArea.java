package com.grinder.game.model.areas;

import com.grinder.game.World;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.model.Boundary;
import com.grinder.game.task.TaskManager;

public class MapInstancedBossArea extends InstancedBossArea {

    private MapInstance mapInstance;

    private boolean reUseMapInstance;

    public void setReuseable(boolean reuse) {
        this.reUseMapInstance = reuse;
    }

    public MapInstancedBossArea(MapInstance mapInstance) {
        super(new Boundary(mapInstance.getBasePosition().getX(), mapInstance.getBasePosition().getX() + InstanceManager.SIZE_IN_TILES,
                mapInstance.getBasePosition().getY(), mapInstance.getBasePosition().getY() + InstanceManager.SIZE_IN_TILES));
        this.mapInstance = mapInstance;
        reUseMapInstance = true;
        mapInstance.setArea(this);

        AreaManager.mapAreaToRegion(this);
    }

    @Override
    public void enter(Agent agent) {
        super.enter(agent);
        agent.setMapInstance(mapInstance);
        mapInstance.resetExpireTick();
        if (agent.isPlayer()) {
            agent.getAsPlayer().instancedMapTick = mapInstance.getCreationTick();
        }
    }

    @Override
    public void leave(Agent agent) {
        super.leave(agent);
        agent.setMapInstance(null);
        if (agent != null && agent.isPlayer()) {
            mapInstance.resetExpireTick();
        }
    }

    @Override
    public void destroy() {

        if(destroyed)
            return;
        npcs.forEach(npc -> {
            npc.setArea(null);
            if (npc.isActive() || npc.isAlive()) {
                World.getNpcRemoveQueue().add(npc);
                TaskManager.cancelTasks(npc);
            }
        });

        players.forEach(player -> {
            player.setArea(null);
            player.setLargeViewport(false);
        });
        objects.clear();
        npcs.clear();
        players.clear();
        if (!reUseMapInstance) {
            destroyed = true;
            InstanceManager.discardInstance(this.mapInstance);
        }
    }
}
