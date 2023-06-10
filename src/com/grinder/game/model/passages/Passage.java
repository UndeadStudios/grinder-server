package com.grinder.game.model.passages;

import com.google.gson.annotations.Expose;
import com.grinder.game.World;
import com.grinder.game.collision.CollisionManager;
import com.grinder.game.definition.ObjectDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.passages.link.PassageLink;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Executable;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.oldgrinder.Compass;

import java.text.NumberFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.grinder.game.model.passages.PassageManager.STUCK_TIME;
import static com.grinder.game.model.passages.PassageMode.FORCE;
import static com.grinder.game.model.passages.PassageMode.NORMAL;
import static com.grinder.game.model.passages.PassageState.CLOSED;
import static com.grinder.game.model.passages.PassageState.OPENED;
import static com.grinder.game.model.passages.PassageType.DOUBLE;
import static com.grinder.game.model.passages.PassageType.SINGLE;

@SuppressWarnings("unused")
public final class Passage {

    @Expose
    private String name;
    @Expose
    private PassageCategory category;
    @Expose
    private final PassageState defaultState;
    @Expose
    private PassageState currentState;
    @Expose
    private final PassageMode mode;
    @Expose
    private PassageType type;
    @Expose
    private boolean locked;
    @Expose
    private boolean broken;
    @Expose
    private int requiredThievingLevel;
    @Expose
    private boolean lockpickRequired;
    @Expose
    private int walkOffsetX;
    @Expose
    private int walkOffsetY;
    @Expose
    private Direction requiredDirection;
    @Expose
    private int animation;
    @Expose
    private int cost;
    @Expose
    private int revertTime;
    @Expose
    private final Map<PassageState, PassageData> data;
    @Expose
    private PassageLink link;
    @Expose
    public Position climbPosition;
    private List<PassageRequirement> requirements;
    private Task revertTask;
    private int lockUntil;
    private int lockExpire;
    private int closeCounter;
    private boolean busy;
    private Predicate<Player> onOpen;
    private Predicate<Player> onClose;
    private Predicate<Player> onClick;
    private Predicate<Player> onEnter;
    private Predicate<Player> onLeave;
    private Predicate<Player> onPaymentFail;
    private Predicate<Player> onPaymentSuccess;
    private Map<String, Predicate<Player>> onOption;

    public Passage(PassageCategory category, GameObject closed, GameObject opened, PassageMode mode, PassageType type, PassageState currentState) {
        this(category, closed, opened, -1, -1, -1, mode, type, currentState);
    }

    public Passage(PassageCategory category, GameObject closed, GameObject opened, int openSound, int closeSound, PassageMode mode, PassageType type, PassageState currentState) {
        this(category, closed, opened, openSound, closeSound, -1, mode, type, currentState);
    }

    public Passage(PassageCategory category, GameObject closed, GameObject opened, int openSound, int closeSound, int animation, PassageMode mode, PassageType type, PassageState currentState) {
        this.category = category;
        this.mode = mode;
        this.type = type;
        this.defaultState = currentState;
        this.currentState = currentState;
        this.data = new HashMap<>();
        this.requirements = new ArrayList<>();
        this.onOption = new HashMap<>();
        this.animation = animation;
        if (opened != null) {
            data.put(OPENED, new PassageData(opened.getId(), openSound, opened.getFace(), opened.getObjectType(), opened.getPosition().clone()));
        }
        if (closed != null) {
            data.put(CLOSED, new PassageData(closed.getId(), closeSound, closed.getFace(), closed.getObjectType(), closed.getPosition().clone()));
        }
    }

    public boolean open(Player player) {
        return category.open(player, this);
    }

    public boolean close(Player player) {
        return category.close(player, this);
    }

    public void switchAttachment() {
        if (link != null) {
            link.transformPassage(this).switchAsAttachment();
        }
    }

    public void switchAsAttachment() {
        switchState();
        if (mode == FORCE) {
            TaskManager.submit(2, this::switchState);
        } else {
            autoRevert();
        }
    }

    public boolean handle(Player player) {
        return handle(player, 1);
    }

    public boolean handle(Player player, int optionId) {
        var definition = ObjectDefinition.forId(getId());
        var actions = definition == null ? new String[10] : definition.actions;
        var clickedOption = actions != null ? actions[optionId - 1].toLowerCase() : "";
        if (clickedOption.contains("pay")) {
            if (this.pay(player)) {
                open(player);
                return true;
            }
        }
        if (onOption != null) {
            for (var customOption : onOption.keySet()) {
                if (customOption.equalsIgnoreCase(clickedOption)) {
                    if (onOption.get(customOption).test(player))
                        return true;
                }
            }
        }
        if (onClick != null) {
            if (onClick.test(player)) {
                return true;
            }
        }
        if (busy) {
            return true;
        }
        if (requiredDirection != null) {
            if (!isFacing(player)) {
                player.sendMessage("You cannot open the door from this side.");
                return true;
            }
        }
        if (clickedOption.contains("search")) {
            player.sendMessage("Nothing interesting here.");
            return true;
        }
        if (category == PassageCategory.TRAPDOOR) {
            if (clickedOption.contains("climb")) {
                climbTrapdoor(player, this);
                return true;
            } else if (clickedOption.contains("forfeit")) {
                player.getDueling().forfeit();
                return true;
            } else if (clickedOption.contains("inspect")) {
                player.sendMessage("You inspect the trapdoor and find nothing.");
                return true;
            } else if (clickedOption.contains("travel")) {
                player.sendMessage("You can't travel using this trapdoor anymore.");
                return true;
            }
        } else if (category == PassageCategory.CURTAIN) {
            if (clickedOption.contains("enter")) {
                pass(player);
                return true;
            }
        }
        if (clickedOption.contains("push")) {
            player.sendMessage("You push the " + category + ".");
        }
        if (canPicklock(player)) {
            if (clickedOption.contains("pick")) {
                if (picklock(player)) {
                    setLocked(false);
                    open(player);
                    setLocked(true);
                }
                return true;
            }
        }
        if (currentState == OPENED) {
            if (mode == FORCE) //stop player from closing FORCE doors
                return true;
            if (onClose != null) {
                if (onClose.test(player)) {
                    close(player);
                }
                return true;
            }
            close(player);
        } else if (meetsAllRequirements(player)) {
            var pass = true;
            if (cost > 0) {
                pass = pay(player);
            }
            if (pass) {
                if (onOpen != null) {
                    if (onOpen.test(player)) {
                        open(player);
                    }
                    return true;
                }
                if (isFacing(player)) {
                    if (onEnter != null) {
                        if (onEnter.test(player)) {
                            open(player);
                            return true;
                        }
                    }
                } else {
                    if (onLeave != null) {
                        if (onLeave.test(player)) {
                            open(player);
                            return true;
                        }

                    }
                }
                open(player);
            }
        }
        return true;
    }

    public void payAndOpen(Player player) {
        if (pay(player)) {
            open(player);
        }
    }

    public boolean pay(Player player) {
        if (cost <= 0) {
            player.sendMessage("You shall go in for free this time.");
            return true;
        }
        if (player.getInventory().contains(new Item(ItemID.COINS, cost))) {
            player.getInventory().delete(ItemID.COINS, cost);
            if (onPaymentSuccess != null) {
                if (onPaymentSuccess.test(player)) {
                    return true;
                }
            } else {
                player.sendMessage("You pay the fee.");
            }
            return true;
        } else {
            if (onPaymentFail != null) {
                onPaymentFail.test(player);
                return false;
            }
            player.sendMessage("You must pay " + NumberFormat.getIntegerInstance().format(cost) + " coins to go through this " + category.getName().toLowerCase() + ".");
        }
        return false;
    }


    public boolean picklock(Player player) {
        if (player.getSkillManager().getCurrentLevel(Skill.THIEVING) >= requiredThievingLevel) {
            if (lockpickRequired) {
                if (!player.getInventory().contains(ItemID.LOCKPICK) && !player.getInventory().contains(ItemID.LOCKPICK_2)) {
                    player.sendMessage("This" + category.name().toLowerCase() + " requires you to have a lockpick to picklock.");
                    return false;
                }
            }
            if (canPicklock(player)) {
                var success = Misc.randomInclusive(0, 5) == 5 && getId(currentState.opposite()) != -1;
                if (success) {
                    player.sendMessage("You manage to pick the lock.");
                } else {
                    player.sendMessage("You fail to pick the lock.");
                }
                return success;
            }
        } else {
            player.sendMessage("You need a thieving level of at-least " + requiredThievingLevel + " to picklock this door.");
        }
        return false;
    }

    public boolean meetsAllRequirements(Player player) {
        if (requirements == null) {
            return true;
        }
        for (var requirement : requirements) {
            if (!requirement.test(player)) {
                if (requirement.getOnFail() != null) {
                    requirement.getOnFail().accept(player);
                }
                player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                return false;
            }
        }
        for (var requirement : requirements) {
            if (requirement.getOnSuccess() != null) {
                requirement.getOnSuccess().accept(player);
            }
        }
        return true;
    }

    private void switchState(PassageState nextState) {
        switch (category) {
            case DOOR:
            case GATE:
            case WOODEN_GATE:
                getObject(nextState).ifPresentOrElse(object -> {
                    ObjectManager.remove(object, true);
                    if (object instanceof DynamicGameObject) {
                        var dynamicObject = (DynamicGameObject) object;
                        ObjectManager.updateNearbyPlayers(dynamicObject.getOriginalObject(), ObjectManager.OperationType.ADD);
                        if (mode == NORMAL) {
                            CollisionManager.addObjectClipping(dynamicObject.getOriginalObject());
                        }
                    }
                    getObject(nextState.opposite()).ifPresent(previousPassage -> ObjectManager.remove(previousPassage, true));

                }, () -> add(nextState));
                break;
            case CURTAIN:
            case TRAPDOOR:
                add(nextState);
                break;
        }
        this.currentState = nextState;
    }

    private void add(PassageState newState) {
        var object = DynamicGameObject.createPublic(getId(newState), getPosition(newState), data.get(newState).getShape(), getFace(newState));
        switch (category) {
            case DOOR:
            case GATE:
            case WOODEN_GATE:
                object.setOriginalObject(DynamicGameObject.createPublic(-1, object.getPosition(), object.getObjectType(), object.getFace()));
                var nullObject = DynamicGameObject.createPublic(-1, getPosition(newState.opposite()), data.get(currentState).getShape(), getFace(newState.opposite()));
                var previousDoor = DynamicGameObject.createPublic(getId(newState.opposite()), nullObject.getPosition(), nullObject.getObjectType(), nullObject.getFace());
                nullObject.setOriginalObject(previousDoor);
                if (mode == NORMAL) {
                    CollisionManager.removeObjectClipping(previousDoor);
                }
                ObjectManager.add(nullObject, true);
                ObjectManager.add(object, true);
                break;
            case CURTAIN:
            case TRAPDOOR:
                ObjectManager.remove(getObjectOrNew(newState.opposite()), true);
                ObjectManager.add(object, true);
                if (newState == OPENED) {
                    CollisionManager.removeObjectClipping(object);
                }
                break;
        }
    }

    public void switchState() {
        switchState(currentState.opposite());
    }

    public void checkCloseCount() {
        //expire the close counter
        //if (World.getTick() > lockExpire) {
        //    setCloseCounter(0);
        //}
        //lockExpire = World.getTick() + 150; //expire lock counter after 150 ticks
        if (getCloseCounter() > 3) {
            setCloseCounter(0);
            setLockUntil(World.getTick() + STUCK_TIME);
        }
    }

    public void autoRevert() {
        if (revertTask != null) {
            revertTask.stop();
            revertTask = null;
        }
        var time = revertTime > 0 ? revertTime : PassageManager.AUTO_REVERT_TIME;
        revertTask = new Task(time, false) {
            @Override
            protected void execute() {
                if (currentState != defaultState) {
                    switchState();
                }
                stop();
            }
        };
        TaskManager.submit(revertTask);
    }

    public void pass(Player player) {
        setBusy(true);
        player.BLOCK_ALL_BUT_TALKING = true;
        TaskManager.submit(2, this::switchState);
        TaskManager.submit(3, () -> player.BLOCK_ALL_BUT_TALKING = false);
        TaskManager.submit(4, () -> setBusy(false));
        Executable walkAction = () -> {
            var otherSide = getOtherSide(player);
            if (walkOffsetX != 0 || walkOffsetY != 0)
                otherSide = player.getPosition();
            if (isFacing(player)) {
                otherSide = otherSide.transform(walkOffsetX, walkOffsetY, 0);
            }
            player.getMotion().enqueuePathToWithoutCollisionChecks(otherSide.getX(), otherSide.getY());
        };
        walkAction.execute();
    }

    private boolean isFacing(Player player) {
        if (type == SINGLE) {
            var object = getObjectOrNew(CLOSED);
            final var shape = object.getObjectType();
            final var rotation = object.getFace();
            final var direction = Misc.getDirectionBetween(player, object);
            if (requiredDirection != null) {
                return direction.isParent(requiredDirection) || requiredDirection == direction;
            }
            var dx = Misc.clamp(player.getX() - object.getX(), -1, 1);
            var dy = Misc.clamp(player.getY() - object.getY(), -1, 1);
            if (shape == 0) {
                if (rotation == 0) {
                    return dx > 0;
                } else if (rotation == 1) {
                    return dy > 0;
                } else if (rotation == 2) {
                    return dx < 1;
                } else {
                    return dy < 1;
                }
            } else if (shape == 10) {
                if (rotation == 0) {
                    return dx > 0;
                } else if (rotation == 1) {
                    return dy > 0;
                } else if (rotation == 2) {
                    return dx < 1;
                } else {
                    return dy < 1;
                }
            } else {
                System.err.println("Unrecognized passage shape type: " + shape);
            }
            return false;
        } else {
            var x = getPosition().getX();
            var y = getPosition().getY();
            var attachmentPosition = getAttachment().getPosition(currentState);
            if (attachmentPosition != null && player.getPosition().getDistance(attachmentPosition) <= player.getPosition().getDistance(getPosition())) {
                x -= getPosition().getDelta(attachmentPosition).getX();
                y -= getPosition().getDelta(attachmentPosition).getY();
            }
            final var delta = player.getPosition().getDelta(new Position(x, y, player.getZ()));
            final var deltaDirection = Direction.fromDeltas(delta.getX(), delta.getY());
            return deltaDirection.inFront(player.getPosition(), getPosition(), getObjectOrNew(currentState).getWidth() * 2) || deltaDirection == Direction.NONE;
        }
    }


    private Position getOtherSide(Player player) {
        if (getType() == SINGLE) {
            int[] delta = Misc.getDirectionBetween(player, getObjectOrNew(CLOSED)).getOpposite().getDirectionDelta();
            if (walkOffsetX == 0 && walkOffsetY == 0) {
                var walkOffsetX = getObjectOrNew(CLOSED).getRotatedWidth();
                var walkOffsetY = getObjectOrNew(CLOSED).getRotatedLength();
                return player.getPosition().transform(delta[0] * walkOffsetX, delta[1] * walkOffsetY, 0);
            } else {
                return player.getPosition().transform(delta[0] * walkOffsetX, delta[1] * walkOffsetY, 0);
            }
        } else {
            Compass faceSide = null;
            var object = getObjectOrNew(CLOSED);
            var x = object.getX();
            var y = object.getY();
            if (link != null) {
                var position = link.getPosition(currentState);
                if (position != null && player.getPosition().getDistance(position) <= player.getPosition().getDistance(getPosition())) {
                    x -= getPosition().getDelta(position).getX();
                    y -= getPosition().getDelta(position).getY();
                }
            }
            int direction = object.getFace();
            if (direction == 0) {
                if (player.getPosition().getX() >= x) {
                    faceSide = Compass.EAST;
                } else {
                    faceSide = Compass.WEAST;
                }
            } else if (direction == 2) {
                if (player.getPosition().getX() <= x) {
                    faceSide = Compass.WEAST;
                } else {
                    faceSide = Compass.EAST;
                }
            } else if (direction == 1) {
                if (player.getPosition().getY() > y) {
                    faceSide = Compass.NORTH;
                } else {
                    faceSide = Compass.SOUTH;
                }
            } else if (direction == 3) {
                if (player.getPosition().getY() < y) {
                    faceSide = Compass.SOUTH;
                } else {
                    faceSide = Compass.NORTH;
                }
            }
            if (faceSide == Compass.EAST) {
                x += direction == 0 ? -1 : 0;
            } else if (faceSide == Compass.WEAST) {
                x += direction == 0 ? 0 : 1;
            } else if (faceSide == Compass.NORTH) {
                y += direction == 3 ? -1 : 0;
            } else if (faceSide == Compass.SOUTH) {
                y += direction == 1 ? 1 : 0;
            }
            return new Position(x, y);
        }
    }


    public void onOpen(Predicate<Player> openAction) {
        this.onOpen = openAction;
    }

    public void onOpen(Consumer<Player> openAction) {
        onOpen(player -> {
            openAction.accept(player);
            return true;
        });
    }

    public void onClose(Predicate<Player> closeAction) {
        this.onClose = closeAction;
    }

    public void onClose(Consumer<Player> closeAction) {
        onClose(player -> {
            closeAction.accept(player);
            return true;
        });
    }

    /**
     * if this action is not null then it will invoke and ignore the door completely
     * basically a replacement for ObjectsActions
     */
    public void onClick(Predicate<Player> clickAction) {
        this.onClick = clickAction;
    }

    public void onClick(Consumer<Player> clickAction) {
        onClick(player -> {
            clickAction.accept(player);
            return true;
        });
    }

    public void onOption(String option, Predicate<Player> action) {
        if (onOption == null)
            onOption = new HashMap<>();
        this.onOption.put(option, action);
    }

    public void onOption(String option, Consumer<Player> action) {
        onOption(option, player -> {
            action.accept(player);
            return true;
        });
    }

    public void onEnter(Predicate<Player> action) {
        this.onEnter = action;
    }

    public void onEnter(Consumer<Player> action) {
        this.onEnter(player -> {
            action.accept(player);
            return true;
        });
    }

    public void onLeave(Predicate<Player> action) {
        this.onLeave = action;
    }

    public void onLeave(Consumer<Player> action) {
        this.onLeave(player -> {
            action.accept(player);
            return true;
        });
    }

    public void onPaymentFail(Predicate<Player> onPaymentFail) {
        this.onPaymentFail = onPaymentFail;
    }

    public void onPaymentFail(Consumer<Player> onPaymentFail) {
        onPaymentFail(player -> {
            onPaymentFail.accept(player);
            return true;
        });
    }

    public void onPaymentSuccess(Predicate<Player> onPaymentSuccess) {
        this.onPaymentSuccess = onPaymentSuccess;
    }

    public void onPaymentSuccess(Consumer<Player> onPaymentSuccess) {
        onPaymentSuccess(player -> {
            onPaymentSuccess.accept(player);
            return true;
        });
    }

    public Passage attach(PassageLink link) {
        if (link != null) {
            this.link = link;
            setType(DOUBLE);
        }
        return this;
    }

    public Passage deAttach(PassageLink link) {
        if (link == this.link) {
            this.link = null;
            setType(SINGLE);
        }
        return this;
    }

    public void playSound(Player player, PassageState passageState) {
        if (getSoundId() > 0) {
            player.playSound(new Sound(getSoundId()));
        } else {
            getCategory().playSound(player, passageState);
        }
    }

    public void playAnimation(Player player) {
        if (animation > 0) {
            player.performAnimation(new Animation(animation));
        }
    }

    public boolean canPicklock(Player player) {
        return player.getSkillManager().getCurrentLevel(Skill.THIEVING) >= requiredThievingLevel;
    }

    public Predicate<Player> getOnEnter() {
        return onEnter;
    }

    public Predicate<Player> getOnLeave() {
        return onLeave;
    }

    public Predicate<Player> getOnOpen() {
        return onOpen;
    }

    public Predicate<Player> getOnClose() {
        return onClose;
    }

    public Predicate<Player> getOnClick() {
        return onClick;
    }

    public Predicate<Player> getOnPaymentFail() {
        return onPaymentFail;
    }

    public Predicate<Player> getOnPaymentSuccess() {
        return onPaymentSuccess;
    }

    public Map<String, Predicate<Player>> getOnOption() {
        return onOption;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getRequiredThievingLevel() {
        return requiredThievingLevel;
    }

    public boolean isLockpickRequired() {
        return lockpickRequired;
    }

    public Map<PassageState, PassageData> getData() {
        return data;
    }

    public PassageCategory getCategory() {
        return category;
    }

    public void setCategory(PassageCategory category) {
        this.category = category;
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public PassageMode getMode() {
        return mode;
    }

    public PassageType getType() {
        return type;
    }

    public void setType(PassageType type) {
        this.type = type;
    }

    public PassageState getCurrentState() {
        return currentState;
    }

    public int getSoundId() {
        return getSoundId(currentState);
    }

    public int getRevertTime() {
        return revertTime;
    }

    public void setRevertTime(int revertTime) {
        this.revertTime = revertTime;
    }

    public int getSoundId(PassageState state) {
        if (data.containsKey(state)) {
            return data.get(state).getSound();
        }
        return -1;
    }

    public int getAnimation() {
        return animation;
    }

    public void setAnimation(int id) {
        this.animation = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PassageRequirement> getRequirements() {
        if (requirements == null)
            requirements = new ArrayList<>();
        return requirements.stream().collect(Collectors.toUnmodifiableList());
    }

    public void addRequirement(PassageRequirement... requirements) {
        if (this.requirements == null)
            this.requirements = new ArrayList<>();
        this.requirements.addAll(Arrays.asList(requirements));
    }

    public void addSkillRequirement(Skill skill, int level) {
        addSkillRequirement(skill, level, null);
    }

    public void addSkillRequirement(Skill skill, int level, Consumer<Player> onFail) {
        addSkillRequirement(skill, level, onFail, null);
    }

    public void addSkillRequirement(Skill skill, int level, Consumer<Player> onFail, Consumer<Player> onSuccess) {
        addRequirement(PassageRequirement.requiredSkill(skill, level), onFail, onSuccess);
    }

    public void addRequirement(Predicate<Player> predicate) {
        addRequirement(predicate, null, null);
    }

    public void addRequirement(Predicate<Player> predicate, Consumer<Player> onFail) {
        addRequirement(predicate, onFail, null);
    }

    public void addRequirement(Predicate<Player> predicate, Consumer<Player> onFail, Consumer<Player> onSuccess) {
        addRequirement(new PassageRequirement(predicate, onFail, onSuccess));
    }

    public void removeRequirement(PassageRequirement... requirements) {
        if (requirements == null) {
            return;
        }
        this.requirements.removeAll(Arrays.asList(requirements));
    }

    public boolean isBusy() {
        return busy;
    }

    public boolean isStuck() {
        return World.getTick() <= lockUntil;
    }

    public PassageState getDefaultState() {
        return defaultState;
    }

    public PassageLink getAttachment() {
        return link;
    }

    public int getId(PassageState state) {
        if (data.containsKey(state)) {
            return data.get(state).getId();
        }
        return -1;
    }

    public int getId() {
        return getId(currentState);
    }

    public Position getPosition(PassageState state) {
        if (data.containsKey(state)) {
            return data.get(state).getPosition();
        }
        return null;
    }

    public Position getPosition() {
        return getPosition(currentState);
    }

    public int getFace(PassageState state) {
        if (data.containsKey(state)) {
            return data.get(state).getFace();
        }
        return -1;
    }

    public int getFace() {
        return getFace(currentState);
    }

    public Direction getRequiredDirection() {
        return requiredDirection;
    }

    public GameObject getObject() {
        return ObjectManager.findDynamicObjectAt(getPosition(currentState)).orElse(null);
    }

    public Optional<GameObject> getObject(PassageState state) {
        return ObjectManager.findDynamicObjectAt(getPosition(state));
    }

    public GameObject getObjectOrNew(PassageState state) {
        return getObject(state).orElseGet(() -> DynamicGameObject.createPublic(getId(state), getPosition(state), data.get(state).getShape(), getFace(state)));
    }

    public PassageLink getAsAttachment() {
        return new PassageLink(getObjectOrNew(CLOSED), getObjectOrNew(OPENED));
    }

    static void climbTrapdoor(Player player, Passage passage) {
        if (passage.climbPosition != null) {
            player.sendMessage("You climb down the trapdoor.");
            player.performAnimation(new Animation(827));
            TaskManager.submit(2, () -> player.moveTo(passage.climbPosition));
            var forceClose = true;
            var definitions = ObjectDefinition.forId(passage.getId(OPENED));
            if (definitions != null) {
                var options = definitions.actions;
                if (options != null && options.length != 0) {
                    for (var option : options) {
                        if (option != null) {
                            if (option.toLowerCase().contains("close")) {
                                forceClose = false;
                            }
                        }
                    }
                }
            }
            if (passage.getId(passage.getCurrentState().opposite()) != -1 && forceClose) {
                TaskManager.submit(2, passage::switchState);
            }
        } else {
            player.sendMessage("It's too dark down there!");
        }
    }

    public int getLockUntil() {
        return lockUntil;
    }

    public void setLockUntil(int lockUntil) {
        this.lockUntil = lockUntil;
    }

    public int getCloseCounter() {
        return closeCounter;
    }

    public void setCloseCounter(int closeCounter) {
        this.closeCounter = closeCounter;
    }

    public void setBusy(boolean value) {
        busy = value;
    }
}