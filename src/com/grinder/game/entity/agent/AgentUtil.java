package com.grinder.game.entity.agent;

import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.collision.CollisionPolicy;
import com.grinder.game.entity.Entity;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.combat.LineOfSight;
import com.grinder.game.entity.agent.movement.task.WalkToAction;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.area.Region;
import com.grinder.game.model.area.RegionCoordinates;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.util.timing.TimerKey;

import java.util.stream.Stream;

/**
 * An utility class for {@link Agent}s.
 *
 * This class should be non-existent :D
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-23
 */
public class AgentUtil {

    private static boolean canRetrieveAgent(Agent agent, Position sourcePosition, int maxDistance) {

        if (agent instanceof Player){
            final Player player = (Player) agent;
            if (player.isInTutorial() || EntityExtKt.getBoolean(player, Attribute.INVISIBLE, false) || player.isAccountFlagged() || (player.getGameMode().isOneLife()) && player.fallenOneLifeGameMode())
                return false;
        }

        return agent.getPosition().getZ() == sourcePosition.getZ()
                && agent.getPosition().getDistance(sourcePosition) <= maxDistance;
    }

    public static Stream<Agent> getAgentsInProximity(final Position sourcePosition, int sourceSize, final int maxDistance, final CollisionPolicy policy){
        final Stream.Builder<Agent> playerBuilder = Stream.builder();

        final Region sourceRegion = World.getRegions().fromPosition(sourcePosition);
        for(final RegionCoordinates coordinates : sourceRegion.getSurrounding()){
            final Region surroundingRegion = World.getRegions().get(coordinates);
            surroundingRegion.<Agent>getPlayersNpcs()
                    .filter(agent -> canRetrieveAgent(agent, sourcePosition, maxDistance))
                    .forEach(playerBuilder::add);
        }

        final Stream<Agent> agentStream = playerBuilder.build();

        return policy == CollisionPolicy.NONE ? agentStream
                : policy == CollisionPolicy.PROJECTILE ? agentStream.filter(player -> LineOfSight.withinSight(sourcePosition, player.getPosition(), false))
                : agentStream.filter(player ->  !CollisionManager.canMove(sourcePosition, player.getPosition(), 1, sourceSize));
    }

    public static Stream<Agent> getPlayers(final Position sourcePosition, int sourceSize, final int maxDistance, final CollisionPolicy policy){
        final Stream.Builder<Agent> playerBuilder = Stream.builder();

        final Region sourceRegion = World.getRegions().fromPosition(sourcePosition);
        for(final RegionCoordinates coordinates : sourceRegion.getSurrounding()){
            final Region surroundingRegion = World.getRegions().get(coordinates);
            surroundingRegion.<Agent>getPlayersStream()
                    .filter(agent -> canRetrieveAgent(agent, sourcePosition, maxDistance))
                    .forEach(playerBuilder::add);
        }

        final Stream<Agent> agentStream = playerBuilder.build();

        return policy == CollisionPolicy.NONE ? agentStream
                : policy == CollisionPolicy.PROJECTILE ? agentStream.filter(player -> LineOfSight.withinSight(sourcePosition, player.getPosition(), false))
                : agentStream.filter(player ->  !CollisionManager.canMove(sourcePosition, player.getPosition(), 1, sourceSize));
    }

    public static Stream<Player> getPlayersInProximity(final Position sourcePosition, int sourceSize, final int maxDistance, final CollisionPolicy policy){
        return getPlayers(sourcePosition, sourceSize, maxDistance, policy)
                .map(Player.class::cast);
    }

    public static Stream<Player> getPlayersInProximity(final Entity source, final int maxDistance, final CollisionPolicy policy){
        final Position sourcePosition = source.getPosition();
        return getPlayersInProximity(sourcePosition, source.getSize(), maxDistance, policy);
    }

    /**
     * @deprecated use {@link #getPlayersInProximity(Entity, int, CollisionPolicy)} instead.
     */
    @Deprecated
    public static Stream<Player> getPlayersInProximity(final Entity source, final int maxDistance, final boolean ignoreCollisionChecks){
        return getPlayersInProximity(source, maxDistance, ignoreCollisionChecks ? CollisionPolicy.NONE : CollisionPolicy.AGENT);
    }

    public static Stream<NPC> getNPCsInProximity(final Entity source, final int maxDistance, final CollisionPolicy policy){
        return getNPCsInProximity(source.getPosition(), source.getSize(), maxDistance, policy);
    }

    public static Stream<NPC> getNPCsInProximity(final Position sourcePosition, int sourceSize, final int maxDistance, final CollisionPolicy policy){
        return getAgentsInProximity(sourcePosition, sourceSize, maxDistance, policy)
                .filter(agent -> agent instanceof NPC)
                .map(NPC.class::cast);
    }

    public static boolean isFrozen(Agent agent) {
        return agent.getTimerRepository().has(TimerKey.FREEZE);
    }

    public static boolean isStunned(Agent agent) {
        return agent.getTimerRepository().has(TimerKey.STUN);
    }

    public static int getProjectileIndex(Agent agent) {
        return (agent.isPlayer() ? -agent.getIndex() - 1 : agent.getIndex() + 1);
    }

    public static int getRegionID(Agent agent) {
        int regionX = agent.getPosition().getX() >> 3;
        int regionY = agent.getPosition().getY() >> 3;

        return ((regionX / 8) << 8) + (regionY / 8);
    }

    public static boolean inArea(Agent agent, com.grinder.util.oldgrinder.Area area) {
        return agent.getPosition().getX() >= area.getWest() && agent.getPosition().getY() >= area.getSouth() && agent.getPosition().getX() <= area.getEast() && agent.getPosition().getY() <= area.getNorth();
    }
}
