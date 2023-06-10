package com.grinder.game.content.cluescroll.task.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.content.cluescroll.ClueScroll;
import com.grinder.game.content.cluescroll.task.ClueTask;

/**
 * This task represents a {@link ClueTask} that includes interacting with the specified {@link #interactionNpcId}.
 *
 * @author Pb600
 * @author Stan van der Bend
 */
public class SearchNpcClueTask extends ClueTask {

    private transient final int interactionNpcId;
    private transient final int interactionOption;

    public SearchNpcClueTask(int taskID, int interactionNpcId, int interactionOption, ClueScroll clueScroll) {
        super(taskID, clueScroll);
        this.interactionNpcId = interactionNpcId;
        this.interactionOption = interactionOption;
    }

    @Override
    public boolean isPerformingTask(Player c, Object... args) {

        if (args.length < 2)
            return false;

        final int interactedNpcId = (int) args[0];
        final int interactedOption = (int) args[1];

        return interactedNpcId == interactionNpcId && interactedOption == interactionOption;
    }

    @Override
    protected boolean preventDefault() {
        return true;
    }

    @Override
    public ClueTask randomize() {
        return this;
    }

    @Override
    public ClueTask clone() {
        return new SearchNpcClueTask(taskID, interactionNpcId, interactionOption, clueScroll).setRequiredEquipments(requiredEquipments).setAgent(clueTaskAgent);
    }

    @Override
    public String toString() {
        return "NpcClickTaskType [npcID=" + interactionNpcId + ", optionID=" + interactionOption + "]";
    }

    @Override
    protected void deserialize(JsonObject jsonObject) {
    }

    @Override
    protected void serialize(Gson gson, JsonObject jsonObject) {
    }

    @Override
    protected void onComplete() {
    }
}
