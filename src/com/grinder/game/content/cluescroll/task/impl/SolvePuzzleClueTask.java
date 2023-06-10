package com.grinder.game.content.cluescroll.task.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.content.cluescroll.ClueScroll;
import com.grinder.game.content.cluescroll.ClueScrollManager;
import com.grinder.game.content.cluescroll.scroll.ScrollManager;
import com.grinder.game.content.cluescroll.task.ClueTask;
import com.grinder.game.content.cluescroll.task.ClueTaskState;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.interfaces.dialogue.impl.DialogueChat;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.util.oldgrinder.NPContainer;

import java.util.function.Predicate;

/**
 * This task represents a {@link ClueTask} that includes solving a puzzle box.
 *
 * @author Pb600
 * @author Stan van der Bend
 */
public class SolvePuzzleClueTask extends ClueTask {

    private final int npcID;
    private final int optionID;
    private final int puzzleBox;

    private Predicate<Player> requirements;
    private DialogueChat fullInventoryMessage;
    private DialogueChat taskRequestMessage;
    private DialogueChat taskCompletionMessage;
    private DialogueChat taskNotCompletedMessage;

    public SolvePuzzleClueTask(int taskID, ClueScroll clueScroll, int npcID, int optionID, int puzzleBox) {
        this(taskID, clueScroll, npcID, optionID, puzzleBox, null);
    }

    private SolvePuzzleClueTask(int taskID, ClueScroll clueScroll, int npcID, int optionID, int puzzleBox, Predicate<Player> requirements) {
        super(taskID, clueScroll);
        this.npcID = npcID;
        this.puzzleBox = puzzleBox;
        this.optionID = optionID;
        this.requirements = requirements;
    }

    public SolvePuzzleClueTask setRequirements(Predicate<Player> requirements) {
        this.requirements = requirements;
        return this;
    }

    public SolvePuzzleClueTask setMessages(DialogueChat fullInventoryMessage, DialogueChat taskRequestMessage, DialogueChat taskCompletionMessage, DialogueChat taskNotCompletedMessage) {
        this.fullInventoryMessage = fullInventoryMessage;
        this.taskRequestMessage = taskRequestMessage;
        this.taskCompletionMessage = taskCompletionMessage;
        this.taskNotCompletedMessage = taskNotCompletedMessage;
        return this;
    }

    @Override
    public boolean isPerformingTask(Player c, Object... args) {
        if (args.length < 2) {
            return false;
        }

        if (requirements != null) {
            if (!requirements.test(c)) {
                return false;
            }
        }

        int npcID = (int) args[0];
        int optionID = (int) args[1];
        return npcID == this.npcID && optionID == this.optionID;
    }

    @Override
    public ClueTask randomize() {
        return null;
    }

    @Override
    protected void deserialize(JsonObject jsonObject) {
    }

    @Override
    protected void serialize(Gson gson, JsonObject jsonObject) {
    }

    @Override
    public ClueTask clone() {
        return new SolvePuzzleClueTask(taskID, clueScroll, npcID, optionID, puzzleBox).setMessages(fullInventoryMessage, taskRequestMessage, taskCompletionMessage, taskNotCompletedMessage).setRequirements(requirements);
    }

    @Override
    protected void onComplete() {
    }

    @Override
    public ClueTaskState hasCompletedTask(Player playerDoingClue) {
        final ClueScrollManager clueScrollManager = playerDoingClue.getClueScrollManager();
        final ScrollManager scrollManager = clueScrollManager.getScrollManager();

        if (ItemContainerUtil.getAccountItemsCount(playerDoingClue, puzzleBox) <= 0) {

            scrollManager.reset();

            if (!playerDoingClue.getInventory().canDeposit(puzzleBox, 1)) {
                DialogueChat fullInventoryMessage = this.fullInventoryMessage;
                if (fullInventoryMessage == null) {
                    new DialogueBuilder(DialogueType.NPC_STATEMENT)
                            .setExpression(DialogueExpression.ANNOYED)
                            .setNpcChatHead(npcID)
                            .setText("I need to give you something,", " please get some free space!")
                    .start(playerDoingClue);
                } else {
                    new DialogueBuilder(fullInventoryMessage)
                            .start(playerDoingClue);
                }
                return new ClueTaskState(false, true);
            }


            DialogueChat taskRequestMessage = this.taskRequestMessage;
            DialogueBuilder builder;

            if (taskRequestMessage == null)
            {
                builder = new DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(npcID)
                        .setText("Oh! I've been expecting you.", "Take a look!");
            } else
                builder = new DialogueBuilder(taskRequestMessage);


            builder
                    .add(DialogueType.ITEM_STATEMENT)
                    .setText("You've received a puzzle box.")
                    .setItem(puzzleBox, 200)
                    .setAction(player -> player.getInventory().add(puzzleBox, 1));

            builder.start(playerDoingClue);

            return new ClueTaskState(false, true);
        }

        if (scrollManager.getCurrentPuzzle() != null) {
            if (scrollManager.hasCompleted() && playerDoingClue.getInventory().contains(puzzleBox)) {


                DialogueChat taskCompletionMessage = this.taskCompletionMessage;

                if (taskCompletionMessage == null)
                    taskCompletionMessage = new DialogueChat("Great! This is just what I wanted!").setNPC(npcID, NPContainer.getNPCName(npcID));
                DialogueBuilder builder = new DialogueBuilder(taskCompletionMessage);

                builder.setAction(player -> {
                    player.getClueScrollManager().completedPuzzles++;
                    completeTask(player);
                    player.getClueScrollManager().getScrollManager().reset();
                    if (player.getInventory().contains(puzzleBox))
                        player.getInventory().delete(puzzleBox, 1);
                });

                builder.start(playerDoingClue);


                return new ClueTaskState(false, true);
            }
        }
        DialogueChat taskNotCompletedMessage = this.taskNotCompletedMessage;
        if (taskNotCompletedMessage == null) {
            taskNotCompletedMessage = new DialogueChat("So you could not solve the puzzle yet...?")
                    .setNPC(npcID, NPContainer.getNPCName(npcID))
                    .setAnimation(DialogueExpression.DISTRESSED_2);
        }
        DialogueBuilder builder = new DialogueBuilder(taskNotCompletedMessage);
        builder.start(playerDoingClue);
        return new ClueTaskState(false, true);
    }

    @Override
    public String toString() {
        return "PuzzleTaskType [npcID=" + npcID + ", optionID=" + optionID + "]";
    }
}
