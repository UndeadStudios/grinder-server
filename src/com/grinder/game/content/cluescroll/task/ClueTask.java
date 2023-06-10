package com.grinder.game.content.cluescroll.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.grinder.game.World;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.Position;
import com.grinder.game.model.PositionUtil;
import com.grinder.game.content.cluescroll.ClueConstants;
import com.grinder.game.content.cluescroll.ClueScroll;
import com.grinder.game.content.cluescroll.agent.ClueAgent;
import com.grinder.game.content.cluescroll.scroll.*;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.container.SlotItem;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

/**
 * Represents one of <b>many</b> tasks from a Clue Scroll of same difficulty.
 * <p>
 * Each time a player click on a Clue Scroll, in case there aren't any active
 * task, a new task will be created from a TaskType with given Difficulty.
 * Whenever this task is completed, whether player will gain another task
 * depending on the count of completed tasks compared with the
 * {@code tasksAmount} to be completed or when {@code taskCount} equal
 * {@code tasksAmount} the player will receive a casket as Clue Scroll
 * completion.
 *
 * @author Pb600
 * @version 1.0 08/05/2016
 */
public abstract class ClueTask implements Cloneable {

    public final ClueScroll clueScroll;
    protected final int taskID;
    protected List<Integer> scrollProgress = new ArrayList<>();
    protected SlotItem[] requiredEquipments;
    protected ClueTaskAgent clueTaskAgent;
    protected int taskCount;
    protected int tasksAmount;
    private Predicate<Player> finishCondition;

    protected ClueTask(int taskID, ClueScroll clueScroll) {
        this.taskID = taskID;
        this.clueScroll = clueScroll;
        this.taskCount = 0;
        this.tasksAmount = getTaskAmount(clueScroll.getDifficulty());
    }

    /**
     * Generate a random amount of tasks to be completed to get this clue scroll
     * finished.
     *
     * @param difficulty Difficulty of the Clue Scroll.
     * @return task amount as a <b>random</b> value.
     */
    private static int getTaskAmount(ScrollDifficulty difficulty) {
        return difficulty.getTaskAmount().getRandomAmount();
    }

    public ClueTask setRequiredEquipments(SlotItem... requiredEquipments) {
        this.requiredEquipments = requiredEquipments;
        return this;
    }

    public ClueTask setAgent(ClueTaskAgent clueTaskAgent) {
        this.clueTaskAgent = clueTaskAgent;
        return this;
    }

    public void setScrollProgress(List<Integer> scrollProgress) {
        if (scrollProgress != null) {
            this.scrollProgress = scrollProgress;
        }
    }

    /**
     * Display a guide of task completion.
     *
     * @param c Player to receive the guide.
     */
    public void openGuide(Player c) {
        clueScroll.getClueGuide().display(c);
    }

    /**
     * Register count of completed tasks to keep track of when all tasks of the
     * Clue Scroll are completed.
     *
     * @param taskCount Count of completed tasks from this Clue Scroll
     * @return Instance of current ScrollTask instance.
     */
    public ClueTask setCount(int taskCount) {
        this.taskCount = taskCount;
        return this;
    }

    /**
     * Get difficulty of current task.
     *
     * @return difficulty of current task.
     */
    public ScrollDifficulty getDifficulty() {
        return clueScroll.getDifficulty();
    }

    /**
     * Get Index of this task, used to map and localize.
     *
     * @return index of this task.
     */
    public int getTaskID() {
        return taskID;
    }

    /**
     * Check given type math current task type.
     *
     * @param taskType Given task type to be compared.
     * @return true in case given task type math current task type.
     */
    public boolean isType(ClueType taskType) {
        return clueScroll.getTaskType() == taskType;
    }

    /**
     * Attempt to perform an operation on this task and progress. At first it's
     * checked if player has the scroll in the inventory. Then it will check if
     * task action type match current type of action. Finally it will check if
     * the action behave as expected by this task.
     *
     * @param c        Client to process task progress.
     * @param taskType Type of action being executed.
     * @param args     Arguments related to current action values.
     */
    public boolean performOperation(Player c, ClueType taskType, Object... args) {
        if (isType(taskType) && containScroll(c) && isPerformingTask(c, args)) {

            ClueTaskState clueTaskState = hasCompletedTask(c);
            if (clueTaskState.getCompleted()) {
                completeTask(c);
                return true;
            }

            return clueTaskState.getPreventDefaultOperation();
        }
        return false;
    }

    /**
     * Check if player has a scroll of this task difficulty in the inventory, in
     * order to continue processing the task actions.
     *
     * @param c Client to check for inventory.
     * @return true in case player has the scroll in the inventory.
     */
    private boolean containScroll(Player c) {
        return c.getInventory().contains(getDifficulty().getScrollID());
    }

    /**
     * Identify if current task is being performed by an action, by checking if
     * the action math task expected action.
     *
     * @param c    Client checking if the action is referent current task.
     * @param args Arguments provided to process action.
     * @return true in case the action math task expected action.
     */
    public abstract boolean isPerformingTask(Player c, Object... args);

    /**
     * Check whether this task is completed or not in order to proceed and
     * reward player another task or casket.
     *
     * @param c Player performing a task.
     * @return true in case the task is valid.
     */
    public ClueTaskState hasCompletedTask(Player c) {
        return ClueConstants.DEFAULT_ACTION_PROGRESS;
    }

    /**
     * Randomize current task value
     *
     * @return Current ScrollTask instance.
     */
    public abstract ClueTask randomize();

    /**
     * Process this task completion Another task will be given in case the
     * player haven't finished finishing the {@link #tasksAmount} total amount of
     * task. Each time a task is completed the {@link #taskCount} is incremented
     * and passed to the next task.
     * <p>
     * Whenever the total of tasks from this clue is finished, the scroll gets
     * deleted and a casket rewarded to the player, firstly it will attempt to
     * deposit on player inventory, in case there aren't space it will attempt
     * to deposit on player's bank, as last case the casket reward will be
     * placed on the floor under placer.
     *
     * @param c Player completing the task.
     */
    public boolean completeTask(Player c) {

        int scrollType = getDifficulty().getScrollID();

        if (!PlayerUtil.hasAnyItems(c, scrollType))
            return false;

        if (++taskCount >= tasksAmount) {
            c.getInventory().delete(new Item(getDifficulty().getScrollID(), 1), true);
            ClueTaskFactory.getInstance().removeTask(c, getDifficulty());
            int casketID = ClueTaskFactory.getInstance().rewardCasket(c, getDifficulty());
            createItemDialogue(casketID, "You have found a casket!").start(c);
            taskCount = 0;
        } else {
            scrollProgress.add(getTaskID());
            onComplete();
            ClueTaskFactory.getInstance().startTask(c, getDifficulty(), taskCount, scrollProgress);
            rewardNextScroll(c, getDifficulty());
            // Dialogue chat with item
        }
        c.getClueScrollManager().getScrollManager().setKilledAgent(false);
        return preventDefault();
    }

    private DialogueBuilder createItemDialogue(int itemId, String... text) {
        final ItemDefinition itemDefinition = ItemDefinition.forId(itemId);
        String name = itemDefinition == null ? "Clue" : itemDefinition.getName();
        if (name == null)
            name = "Clue";
        //System.out.println("Creating item dialogue "+itemId+", "+name+", "+text);
        // hmm I think the interface does get closed yeah
        return new DialogueBuilder(DialogueType.ITEM_STATEMENT).setItem(itemId, 200, name).setText(text);

    }

    /**
     * Reward a casket that contains another clue scroll.
     *
     * @param c          Player that will be rewarded
     * @param difficulty Difficulty of the current task.
     */
    private void rewardNextScroll(Player c, ScrollDifficulty difficulty) {

        final Inventory inventory = c.getInventory();

        final int scrollID = ClueTaskFactory.getInstance().getScrollType(difficulty);
        final int taskReward = getNextTaskRewardType(difficulty);

        if (scrollID != -1 && taskReward != -1) {
            if (inventory.contains(scrollID)) {

                DialogueBuilder builder = null;

                if (ScrollType.forScrollBox(taskReward) != null)
                    builder = createItemDialogue(taskReward, "You have been given a scroll box!");
                else if (ScrollType.forScrollRewardCasket(taskReward) != null)
                    builder = createItemDialogue(taskReward, "You have found a casket!");
                else if (ScrollType.forScrollType(taskReward) != null)
                    builder = createItemDialogue(taskReward, "You have found a clue scroll!");

                //c.sendMessage("You task difficulty is " + difficulty + ", your reward is " + taskReward + ", builder = " + builder);

                if (builder != null) {
                    final DialogueBuilder finalBuilder = builder;
                    TaskManager.submit(1, () -> {
                        inventory.delete(scrollID, 1);
                        inventory.add(taskReward, 1);
                        finalBuilder.start(c);
                    });
                }
            }
        }
    }

    /**
     * Get next task scroll reward.
     */
    public int getNextTaskRewardType(ScrollDifficulty difficulty) {
        ScrollType scrollType = ScrollType.forDifficulty(difficulty);
        if (scrollType != null) {
            return scrollType.getScrollRewardBox();
        }
        return -1;
    }

    /**
     * Allow to prevent any in-game action default operation upon this task
     * completion. <br>
     * <b>Should be overridden on parent class.</b>
     *
     * @return true in case this task completion should prevent default action.
     */
    protected boolean preventDefault() {
        return false;
    }

    protected abstract void deserialize(JsonObject jsonObject);

    /**
     * Serialize parent task into a JSON Document
     *
     * @param gson       Gson used to serialize the task data.
     * @param jsonObject This task document instance being serialized.
     */
    protected abstract void serialize(Gson gson, JsonObject jsonObject);

    public void fromJson(Gson gson, JsonObject jsonObject) {
        this.taskCount = jsonObject.get("taskCount").getAsInt();
        this.tasksAmount = jsonObject.get("tasksAmount").getAsInt();
        JsonElement progressObject = jsonObject.get("scrollProgress");

        List<Integer> previousTasks = gson.fromJson(progressObject, new TypeToken<Collection<Integer>>() {
        }.getType());
        if (previousTasks != null) {
            this.scrollProgress = previousTasks;
        }
        deserialize(jsonObject);
    }

    /**
     * Convert this task into a JSON document.
     *
     * @return a serialized JSON document.
     */
    public JsonObject toJson() {
        Gson gson = ClueConstants.getGson();
        JsonObject document = new JsonObject();
        document.add("taskID", new JsonPrimitive(getTaskID()));
        document.add("taskCount", new JsonPrimitive(taskCount));
        document.add("tasksAmount", new JsonPrimitive(tasksAmount));
        if (scrollProgress != null && scrollProgress.size() > 0) {
            document.add("scrollProgress", gson.toJsonTree(scrollProgress, new TypeToken<Collection<Integer>>() {
            }.getType()));
        }
        serialize(gson, document);
        return document;
    }

    @Override
    public abstract ClueTask clone();

    /**
     * Method called when this task is completed so the parent task handler can
     * deal with it.
     */
    protected abstract void onComplete();

    public boolean involvesNPCInteraction() {
        return clueTaskAgent != null;
    }

    /**
     * Spawn a task agent whether it will be a god wizard or a double agent,
     * depending on the task difficulty there will not be any aggressive agent,
     * only reward agent.
     *
     * @param owner Player that will be spawend the agent.
     */
    protected void spawnAgent(Player owner) {

        if (clueTaskAgent != null) {

            final boolean isAggressive = clueTaskAgent.hasCombativeForm();
            final int npcId = isAggressive ? clueTaskAgent.getCombativeAgentNpcId() : clueTaskAgent.getAgentNpcId();

            final Position spawnPosition = PositionUtil.findFreeTileEnclosing(owner.getPosition()).orElse(owner.getPosition().clone());
            final ClueAgent agent = new ClueAgent(clueTaskAgent.hasCombativeForm(), npcId, spawnPosition.clone());
            agent.setOwner(owner);
            agent.setPositionToFace(owner.getPosition());
            agent.setLifeSpan(60_000);
            agent.setTask(this);

            World.getNpcAddQueue().add(agent);

            final NpcDefinition definition = agent.fetchDefinition();

            if (definition == null) {
                owner.sendMessage("Your clue agent is invalid!");
                return;
            }

            final ScrollManager scrollManager = owner.getClueScrollManager().getScrollManager();
            scrollManager.setKilledAgent(false);
            scrollManager.setTaskAgent(agent);

            if (clueTaskAgent.hasCombativeForm()) {
                agent.onDeathStart((killer) -> {
                    scrollManager.setKilledAgent(true);
                    if (clueTaskAgent.hasAgent()) {
                        final ClueAgent agent2 = new ClueAgent(false, clueTaskAgent.getAgentNpcId(), agent.getPosition().clone());
                        agent2.setOwner(owner);
                        agent2.setPositionToFace(owner.getPosition());
                        agent2.setLifeSpan(60_000);
                        agent2.setTask(this);
                        scrollManager.setTaskAgent(agent2);

                        Graphic.sendGlobal(ClueConstants.AGENT_SPAWN_GRAPHIC2, agent2.getPosition());
                        TaskManager.submit(6, ()-> {
                            //agent.performGraphic(ClueConstants.AGENT_SPAWN_GRAPHIC);
                            World.getNpcAddQueue().add(agent2);
                        });
                    }
                    killer.getCombat().reset(true);
                    return true;
                });
            }

        }
    }

    public Predicate<Player> getFinishCondition() {
        return finishCondition;
    }

    public ClueTask setFinishCondition(Predicate<Player> finishCondition) {
        this.finishCondition = finishCondition;
        return this;
    }

    protected boolean isProgressingTask(Player c, ClueType taskType) {
        return containScroll(c) && isType(taskType);
    }

    public boolean hasEquipments(Player player) {
        if (requiredEquipments != null) {
            for (SlotItem requiredItem : requiredEquipments) {

                final Item item = player.getEquipment().get(requiredItem.getItemSlot());

                if (item == null || item.getId() != requiredItem.getId()) {
                    return false;
                }

            }
        }
        return true;
    }

    public SlotItem[] getRequiredEquipments() {
        return requiredEquipments;
    }
}
