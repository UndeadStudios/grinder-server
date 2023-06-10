package com.grinder.game.entity.agent.npc.monster.boss.impl.hydra;

import com.grinder.game.collision.CollisionManager;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.oldgrinder.Area;

import java.util.Optional;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-06-01
 */
class VentObject  {

    private final Chemical chemical;
    private final Area effectiveArea;

    private final DynamicGameObject gameObject;

    VentObject(Chemical chemical, Position position) {
        gameObject = DynamicGameObject.createLocal(chemical.objectId, position.clone(), 10, 0);
        this.chemical = chemical;
        effectiveArea = Area.of(3, 3, 3, 3).getAbsolute(gameObject.getCenterPosition());
//        effectiveArea.findPositions(position.getZ())
//                .forEach(CollisionManager::clearClipping);
//        CollisionManager.removeObjectClipping(gameObject);
    }

    Optional<Chemical> sprayedChemical(AlchemicalHydraBoss bossNPC){

        gameObject.performAnimation(new Animation(8279));

        TaskManager.submit(new Task(5) {
            @Override
            protected void execute() {
                stop();
                gameObject.performAnimation(new Animation(8280));
            }
        });

        bossNPC.playerStream(20)
                .filter(effectiveArea::contains)
                .forEach(player -> player.getCombat().queue(Damage.create(10, 20)));
//        effectiveArea.findPositions(bossNPC.getPosition().getZ()).forEach(pos -> bossNPC.playerStream(20).forEach(player -> player.getPacketSender().sendGraphic(new Graphic(271), pos)));

        if(effectiveArea.contains(bossNPC))
            return Optional.of(chemical);

        return Optional.empty();
    }

    GameObject getObject(){
        return gameObject;
    }

    /**
     *   if(getId() == 34570 && hydraId == 8620)
     *             droppedShield = hydraPosition.isWithinDiagonalDistance(getPosition(), bossNPC.getSize(), getSize());
     *         else if(getId() == 34569 && hydraId == 8619)
     *             droppedShield = hydraPosition.isWithinDiagonalDistance(getPosition(), bossNPC.getSize(), getSize());
     *         else if(getId() == 34568 && (hydraId == 8615 || hydraId == -1))
     *             droppedShield = hydraPosition.isWithinDiagonalDistance(getPosition(), bossNPC.getSize(), getSize());
     */

    enum Chemical {

        RED(34568, HydraState.POISON),
        GREEN(34569, HydraState.LIGHTNING),
        BLUE(34570, HydraState.FLAME),
        NONE(-1, HydraState.ENRAGED);

        private final int objectId;
        private final HydraState affective;

        Chemical(int objectId, HydraState affective) {
            this.objectId = objectId;
            this.affective = affective;
        }

        public HydraState getAffectedState() {
            return affective;
        }
    }


}
