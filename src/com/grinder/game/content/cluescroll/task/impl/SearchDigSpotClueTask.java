package com.grinder.game.content.cluescroll.task.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.grinder.game.entity.agent.AgentUtil;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.content.cluescroll.ClueScroll;
import com.grinder.game.content.cluescroll.scroll.ScrollType;
import com.grinder.game.content.cluescroll.scroll.ScrollDifficulty;
import com.grinder.game.content.cluescroll.scroll.ScrollManager;
import com.grinder.game.content.cluescroll.task.ClueTask;
import com.grinder.util.oldgrinder.Area;

import java.util.Arrays;

/**
 * This task represents a {@link ClueTask} that includes digging at a certain {@link Position} or {@link Area}.
 *
 * @author Pb600
 * @author Stan van der Bend
 */
public class SearchDigSpotClueTask extends ClueTask {

    protected transient final Area area;
    protected transient final int height;

    public SearchDigSpotClueTask(int taskID, ClueScroll clueScroll, Position position) {
        super(taskID, clueScroll);
        this.area = new Area(position.getX(), position.getY(), position.getX(), position.getY());
        this.height = position.getZ();
    }

    public SearchDigSpotClueTask(int taskID, ClueScroll clueScroll, Position position, int radius) {
        super(taskID, clueScroll);
        this.area = new Area(position.getX() - radius, position.getY() - radius, position.getX() + radius, position.getY() + radius);
        this.height = position.getZ();
    }

    private SearchDigSpotClueTask(int taskID, ClueScroll clueScroll, Area area, int height) {
        super(taskID, clueScroll);
        if (area == null)
            throw new NullPointerException("Area value can't be null.");
        this.area = area;
        this.height = height;
    }

    public Area getArea() {
        return area;
    }

    public int getNextTaskRewardType(ScrollDifficulty difficulty) {

        final ScrollType scrollType = ScrollType.forDifficulty(difficulty);

        return scrollType != null ? scrollType.getScrollRewardBox() : -1;
    }

    @Override
    public boolean isPerformingTask(Player player, Object... args) {

        if (AgentUtil.inArea(player, area)){

            if (involvesNPCInteraction()) {

                final ScrollManager scrollManager = player.getClueScrollManager().getScrollManager();
                final boolean combativeAgent = scrollManager.hasCombativeAgent();
                final boolean hasKilledAgent = scrollManager.hasKilledAgent();
                final boolean hasActiveAgent = scrollManager.hasActiveAgent();

                if (!hasActiveAgent) {
                    if (combativeAgent && hasKilledAgent)
                        return true;
                    spawnAgent(player);
                }
                return !combativeAgent || hasKilledAgent;
            }
            return true;
        }

        return false;
    }

    @Override
    protected boolean preventDefault() {
        return true;
    }

    @Override
    public String toString() {
        return "DigTaskType [area=" + area + ", height=" + height + ", taskID=" + taskID + ", clueScroll=" + clueScroll + ", requiredEquipments=" + Arrays.toString(requiredEquipments) + ", taskAgent=" + clueTaskAgent + ", taskCount=" + taskCount + ", tasksAmount=" + tasksAmount + ", scrollProgress=" + scrollProgress + "]";
    }

    @Override
    public ClueTask randomize() {
        return this;
    }

    @Override
    public ClueTask clone() {
        return new SearchDigSpotClueTask(taskID, clueScroll, area, height).setRequiredEquipments(requiredEquipments).setAgent(clueTaskAgent);
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
