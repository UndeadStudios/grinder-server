package com.grinder.game.content.minigame.motherlodemine.wheel;

import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.model.Position;
import com.grinder.util.ObjectID;

import java.util.Optional;

/**
 * @author L E G E N D
 * @date 2/11/2021
 * @time 3:03 AM
 * @discord L E G E N D#4380
 */
public final class Strut extends GameObject {

    private WheelState currentState;

    public Strut(int id, Position position, int type, int orientation) {
        super(id, position, type, orientation);
    }

    public GameObject create(WheelState state) {
        return DynamicGameObject.createPublic(state.getStrutId(), getPosition(), 10, 0);
    }

    public void switchState(WheelState state) {
        Optional<GameObject> strut = ObjectManager.findDynamicObjectAt(getId(), getPosition());
        if (strut.isPresent())
            ObjectManager.remove(strut.get(), false);
        ObjectManager.add(create(state), true);
    }

    public void markAsRunning() {
        switchState(WheelState.RUNNING);
    }

    public void markAsBroken() {
        switchState(WheelState.BROKEN);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.DYNAMIC_OBJECT;
    }

    @Override
    public boolean viewableBy(Player player) {
        return true;
    }

    public WheelState getCurrentState() {
        return currentState;
    }

    public static Strut get(Wheel wheel) {
        return new Strut(wheel.getId(), wheel.getPosition().transform(-1, 1, 0), wheel.getObjectType(), wheel.getFace());
    }

    public static boolean isStrut(GameObject object) {
        return object.getId() == ObjectID.BROKEN_STRUT || object.getId() == ObjectID.STRUT;
    }
}


