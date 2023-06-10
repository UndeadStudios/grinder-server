package com.grinder.game.content.cluescroll.task.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.grinder.game.content.miscellaneous.Emotes.EmoteData;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.content.cluescroll.ClueScroll;
import com.grinder.game.content.cluescroll.task.ClueTask;
import com.grinder.game.content.cluescroll.task.ClueTaskState;
import com.grinder.util.oldgrinder.Area;

/**
 * This task represents a {@link ClueTask} that includes performing an emote.
 *
 * @author Pb600
 * @author Stan van der Bend
 */
public class PerformEmoteClueTask extends ClueTask {

    private transient final Area area;
    private transient final int emoteOrdinal;
    private transient final int height;

    public PerformEmoteClueTask(int taskID, int height, int emoteOrdinal, Area area, ClueScroll clueScroll) {
        super(taskID, clueScroll);
        this.emoteOrdinal = emoteOrdinal;
        this.area = area;
        this.height = height;
    }

    public PerformEmoteClueTask(int taskID, int height, EmoteData emoteData, Area area, ClueScroll clueScroll) {
        super(taskID, clueScroll);
        this.emoteOrdinal = emoteData.ordinal();
        this.area = area;
        this.height = height;
    }

    public Area getArea() {
        return area;
    }

    public int getEmoteOrdinal() {
        return emoteOrdinal;
    }

    @Override
    public boolean isPerformingTask(Player c, Object... args) {

        if (args.length < 1)
            return false;

        final int emoteOrdinal = ((EmoteData) args[0]).ordinal();

        if (emoteOrdinal == this.emoteOrdinal) {

            if (!c.getPosition().inside(area) || c.getPosition().getZ() != height)
                return false;

            return hasEquipments(c);
        }
        return false;
    }

    @Override
    public ClueTaskState hasCompletedTask(Player player) {

        if (involvesNPCInteraction()) {

            if (!player.getClueScrollManager().getScrollManager().hasActiveAgent())
                spawnAgent(player);

            return new ClueTaskState(false, true);
        }

        return new ClueTaskState(true, true);
    }

    @Override
    public ClueTask randomize() {
        return this;
    }

    @Override
    public ClueTask clone() {
        return new PerformEmoteClueTask(taskID, height, emoteOrdinal, area, clueScroll).setRequiredEquipments(requiredEquipments).setFinishCondition(getFinishCondition()).setAgent(clueTaskAgent);
    }

    @Override
    public String toString() {
        return "DanceTaskType [emoteOrdinal=" + emoteOrdinal + ", area=" + area + ", height=" + height + "]";
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
