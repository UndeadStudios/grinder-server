package com.grinder.game.content.cluescroll.task.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.content.cluescroll.ClueScroll;
import com.grinder.game.content.cluescroll.task.ClueTask;

/**
 * This task represents a {@link ClueTask} that includes entering an answer that must equal {@link #answer}.
 *
 * @author Pb600
 * @author Stan van der Bend
 */
public class AnswerQuestionClueTask extends ClueTask {

    private final String answer;

    public AnswerQuestionClueTask(int taskID, ClueScroll clueScroll, String answer) {
        super(taskID, clueScroll);
        this.answer = answer;
    }

    @Override
    public boolean isPerformingTask(Player player, Object... args) {

        if (args.length < 2)
            return false;

        final String input = String.valueOf(args[1]);
        final boolean answeredCorrectly = input != null && input.equalsIgnoreCase(answer);

        player.sendMessage(answeredCorrectly ? "You have answered it correctly!" : "The answer is wrong.");
        player.getPacketSender().sendInterfaceRemoval();

        return answeredCorrectly;
    }

    @Override
    public ClueTask randomize() {
        return this;
    }

    @Override
    public AnswerQuestionClueTask clone() {
        return new AnswerQuestionClueTask(taskID, clueScroll, answer);
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
