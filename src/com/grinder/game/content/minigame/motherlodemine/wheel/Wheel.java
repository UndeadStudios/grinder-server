package com.grinder.game.content.minigame.motherlodemine.wheel;

import com.grinder.game.content.minigame.motherlodemine.MotherlodeMine;
import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.AtomicInteger;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author L E G E N D
 * @date 2/11/2021
 * @time 1:52 AM
 * @discord L E G E N D#4380
 */
public class Wheel extends GameObject {

    private static final int WATER_ID = 10459;
    private static final int SMALL_WATER_EFFECT = 2018;
    private static final int BIG_WATER_EFFECT = 2016;
    private static final int FIXING_WHEEL_ANIMATION = 3971;

    private static final Set<GameObject> WATER_EFFECTS = new HashSet<>(Arrays.asList(
            DynamicGameObject.createPublic(SMALL_WATER_EFFECT, new Position(3744, 5672, 0), 10, 2),
            DynamicGameObject.createPublic(SMALL_WATER_EFFECT, new Position(3748, 5671, 0), 10, 3),
            DynamicGameObject.createPublic(SMALL_WATER_EFFECT, new Position(3748, 5660, 0), 10, 0),
            DynamicGameObject.createPublic(SMALL_WATER_EFFECT, new Position(3744, 5660, 0), 10, 0)));

    private WheelState currentState;
    private final Strut strut;

    public Wheel(int id, Position position, int type, int orientation) {
        super(id, position, type, orientation);
        strut = Strut.get(this);
        currentState = id == WheelState.RUNNING.getId() ? WheelState.RUNNING : WheelState.BROKEN;
    }

    private GameObject create(WheelState state) {
        return DynamicGameObject.createPublic(state.getId(), getPosition(), 10, 0);
    }

    public void switchState(WheelState state) {
        Optional<GameObject> wheel = ObjectManager.findDynamicObjectAt(getId(), getPosition());
        if (wheel.isPresent())
            ObjectManager.remove(wheel.get(), false);
        ObjectManager.add(create(state), true);
        strut.switchState(state);
        MotherlodeMine.updateWheels();
        this.currentState = state;
    }

    public void fix(Player player) {
        if (getCurrentState() == WheelState.RUNNING) {
            return;
        }
        if (player.getInventory().contains(ItemID.HAMMER) || player.getInventory().contains(ItemID.HAMMER_2)) {
            var fixingDuration = new AtomicInteger(3 * Misc.random(1, 3));
            player.BLOCK_ALL_BUT_TALKING = true;
            TaskManager.submit(new Task(3, true) {
                @Override
                protected void execute() {
                    if (fixingDuration.getValue() > 0) {
                        player.performAnimation(new Animation(FIXING_WHEEL_ANIMATION, 25));
                        player.playSound(new Sound(1786, 10));
                        player.setPositionToFace(getPosition(), true);
                        fixingDuration.remove(3);
                    } else {
                        player.resetAnimation();
                        player.getSkillManager().addExperience(Skill.SMITHING, player.getSkillManager().getCurrentLevel(Skill.SMITHING) * 1.5);
                        addWater();
                        switchState(WheelState.RUNNING);
                        player.BLOCK_ALL_BUT_TALKING = false;
                        stop();
                    }
                }
            }.bind(player));
            TaskManager.submit(Misc.random(100, 400), this::markAsBroken);
        } else {
            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(ItemID.PAY_DIRT, 200)
                    .setText("You need a hammer to re-align the strut.").start(player);
        }
    }

    public void markAsBroken() {
        if (getCurrentState() == WheelState.RUNNING) {
            switchState(WheelState.BROKEN);
            removeWater();
            MotherlodeMine.stopMoving();
        }
    }

    public void addWater() {
        ObjectManager.add(DynamicGameObject.createPublic(BIG_WATER_EFFECT, new Position(getX() - 1, getY() + 2, 0), 10, 1), true);
        if (MotherlodeMine.getWheelsState() == WheelState.RUNNING) {
            return;
        }
        for (var effect : WATER_EFFECTS) {
            ObjectManager.add(effect, true);
        }
        int x = 3743;
        int y = 5672;
        // Top Left to Top Right
        for (int i = 0; i < 5; i++) {
            ObjectManager.add(DynamicGameObject.createPublic(WATER_ID, new Position(x++, y, 0), 22, 3), true);
        }
        // Top Right to Bottom Right
        for (int i = 0; i < 12; i++) {
            ObjectManager.add(DynamicGameObject.createPublic(WATER_ID, new Position(x, y--, 0), 22, 0), true);
        }
        // Bottom Right to Bottom Left
        for (int i = 0; i < 5; i++) {
            ObjectManager.add(DynamicGameObject.createPublic(WATER_ID, new Position(x--, y, 0), 22, 1), true);
        }
        // Bottom Left to Top Left
        for (int i = 0; i < 12; i++) {
            ObjectManager.add(DynamicGameObject.createPublic(WATER_ID, new Position(x, y++, 0), 22, 2), true);
        }
    }

    public void removeWater() {
        ObjectManager.findDynamicObjectAt(BIG_WATER_EFFECT, new Position(getX() - 1, getY() + 2)).ifPresent(object -> ObjectManager.remove(object, true));
        if (MotherlodeMine.getWheelsState() == WheelState.RUNNING) {
            return;
        }
        int x = 3743;
        int y = 5672;
        for (var effect : WATER_EFFECTS) {
            ObjectManager.remove(effect, true);
        }
        // Top Left to Top Right
        for (int i = 0; i < 5; i++) {
            ObjectManager.findDynamicObjectAt(WATER_ID, new Position(x++, y, 0)).ifPresent(waterObject -> ObjectManager.remove(waterObject, true));
        }
        // Top Right to Bottom Right
        for (int i = 0; i < 12; i++) {
            ObjectManager.findDynamicObjectAt(WATER_ID, new Position(x, y--, 0)).ifPresent(waterObject -> ObjectManager.remove(waterObject, true));
        }
        // Bottom Right to Bottom Left
        for (int i = 0; i < 5; i++) {
            ObjectManager.findDynamicObjectAt(WATER_ID, new Position(x--, y, 0)).ifPresent(waterObject -> ObjectManager.remove(waterObject, true));
        }
        // Bottom Left to Top Left
        for (int i = 0; i < 12; i++) {
            ObjectManager.findDynamicObjectAt(WATER_ID, new Position(x, y++, 0)).ifPresent(waterObject -> ObjectManager.remove(waterObject, true));
        }
    }

    public static Wheel get(Position position) {
        var workingWheel = ObjectManager.findDynamicObjectAt(WheelState.RUNNING.getId(), position);
        var brokenWheel = ClippedMapObjects.findObject(WheelState.BROKEN.getId(), position);
        if (workingWheel.isPresent()) {
            return Wheel.get(workingWheel.get());
        } else if (brokenWheel.isPresent()) {
            return Wheel.get(brokenWheel.get());
        }
        return null;
    }

    public static Wheel get(GameObject object) {
        if (object == null) {
            return null;
        } else if (Strut.isStrut(object)) {
            var wheel = ClippedMapObjects.findObject(26672, object.getPosition().transform(1, -1, 0));
            if (wheel.isPresent()) {
                return new Wheel(wheel.get().getId(), wheel.get().getPosition(), wheel.get().getObjectType(), wheel.get().getFace());
            }
        }
        return new Wheel(object.getId(), object.getPosition(), object.getObjectType(), object.getFace());
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.DYNAMIC_OBJECT;
    }

    @Override
    public boolean viewableBy(Player player) {
        return true;
    }

    public boolean isRunning() {
        return getCurrentState() == WheelState.RUNNING;
    }

    public boolean isBroken() {
        return getCurrentState() == WheelState.BROKEN;
    }

    public Strut getStrut() {
        return strut;
    }

    public WheelState getCurrentState() {
        return currentState;
    }
}
