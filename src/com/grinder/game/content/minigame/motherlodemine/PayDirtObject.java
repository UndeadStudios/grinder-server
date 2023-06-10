package com.grinder.game.content.minigame.motherlodemine;

import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.ObjectID;

/**
 * @author L E G E N D
 * @date 2/11/2021
 * @time 2:35 AM
 * @discord L E G E N D#4380
 */
public final class PayDirtObject extends GameObject {

    private PayDirtObjectType type;
    private boolean depleteNextMine;

    public PayDirtObject(int id, Position position, int type, int orientation) {
        super(id, position, type, orientation);
        this.type = PayDirtObjectType.AMOUNT;
    }

    public void check() {
        if (type == PayDirtObjectType.AMOUNT) {
            depleteNextMine = Misc.random(3) % 2 == 0;
        } else {
            TaskManager.submit(Misc.random(25, 45), () -> depleteNextMine = true);
        }
        if (depleteNextMine) {
            deplete();
        }
    }

    public void deplete() {
        ObjectManager.findDynamicObjectAt(getPosition()).ifPresent(object -> ObjectManager.remove(object, false));
        ObjectManager.add(DynamicGameObject.createPublic(getId() + 4, getPosition(), getObjectType(), getFace()), true);
        TaskManager.submit(50, this::respawn);
    }

    public void respawn() {
        ObjectManager.findDynamicObjectAt(getPosition()).ifPresent(object -> ObjectManager.remove(object, false));
        ObjectManager.add(DynamicGameObject.createPublic(getId(), getPosition(), getObjectType(), getFace()), true);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.DYNAMIC_OBJECT;
    }

    @Override
    public boolean viewableBy(Player player) {
        return true;
    }

    public PayDirtObjectType getType() {
        return type;
    }

    public void setType(PayDirtObjectType type) {
        this.type = type;
    }

    public boolean isDepleting() {
        return depleteNextMine;
    }

    public static boolean isPayDirt(int id) {
        return id >= ObjectID.ORE_VEIN_29 && id <= ObjectID.DEPLETED_VEIN_8;
    }

    public static PayDirtObject get(GameObject object) {
        return new PayDirtObject(object.getId(), object.getPosition(), object.getObjectType(), object.getFace());
    }
}
