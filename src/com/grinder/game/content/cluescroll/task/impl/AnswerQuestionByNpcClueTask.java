package com.grinder.game.content.cluescroll.task.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.content.cluescroll.ClueGuide;
import com.grinder.game.content.cluescroll.ClueScroll;
import com.grinder.game.content.cluescroll.task.ClueTask;
import com.grinder.game.content.cluescroll.task.ClueTaskState;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.util.TextUtil;

/**
 * This task represents a {@link ClueTask} that includes entering an answer that must equal {@link #correctAnswer}.
 * The question is stored in {@link #questionLines} that are communicated through a {@link DialogueType#NPC_STATEMENT}.
 *
 * @author Pb600
 * @author Stan van der Bend
 */
public class AnswerQuestionByNpcClueTask extends ClueTask {

	private final String[] questionLines;
	private final String correctAnswer;
    private final int questionerNpcId;

	public AnswerQuestionByNpcClueTask(String requiredAnswer, int taskID, int questionerNpcId, ClueScroll clueScroll, String... questionLines) {
		super(taskID, clueScroll);
        this.questionLines = questionLines;
        this.correctAnswer = requiredAnswer;
		this.questionerNpcId = questionerNpcId;
	}

    private EnterSyntax promptForAnswer() {
        return new EnterSyntax() {

            @Override
            public void handleSyntax(Player player, String input) {

                if (isProgressingTask(player, clueScroll.getTaskType())) {

                	if (input.equalsIgnoreCase(correctAnswer))
						correctAnswer(player);
					else
						wrongAnswer(player);

				}
            }

            @Override public void handleSyntax(Player player, int input) {

            	if(TextUtil.isInteger(correctAnswer)){

					if (isProgressingTask(player, clueScroll.getTaskType())) {
						if (input == Integer.parseInt(correctAnswer))
							correctAnswer(player);
						else
							wrongAnswer(player);
					}
				}

			}
        };
    }

	private void wrongAnswer(Player player) {
		new DialogueBuilder(DialogueType.NPC_STATEMENT)
				.setNpcChatHead(questionerNpcId)
				.setText("Wrong answer!")
				.start(player);
	}

	private void correctAnswer(Player player) {
		new DialogueBuilder(DialogueType.NPC_STATEMENT)
				.setNpcChatHead(questionerNpcId)
				.setText("Correct!")
				.setAction(this::completeTask)
				.start(player);
	}

	@Override
	public boolean isPerformingTask(Player player, Object... args) {

		if (args.length < 1)
			return false;

		final int interactedNpcId = (int) args[0];

		return interactedNpcId == questionerNpcId;
	}
	@Override
	public ClueTaskState hasCompletedTask(Player player) {
		new DialogueBuilder(DialogueType.NPC_STATEMENT)
				.setNpcChatHead(questionerNpcId)
				.setText("You found me!", "I have a question for you "+player.getUsername() +"!")
				.add(DialogueType.OPTION)
				.firstOption("Let's hear it.", futurePlayer -> {
					futurePlayer.getPacketSender().sendInterfaceRemoval();
					ClueGuide.renderText(player, questionLines);
					futurePlayer.setEnterSyntax(promptForAnswer());
					futurePlayer.getPacketSender().sendEnterAmountPrompt("Answer the question");
				})
				.addCancel("I am not interested.")
		.start(player);
		return new ClueTaskState(false, true);
	}
    @Override
	public ClueTask randomize() { return null; }
	@Override
	public ClueTask clone() { return new AnswerQuestionByNpcClueTask(correctAnswer, taskID, questionerNpcId, clueScroll, questionLines); }
    @Override
    protected void deserialize(JsonObject jsonObject) { }
    @Override
    protected void serialize(Gson gson, JsonObject jsonObject) { }
	@Override
	protected void onComplete() { }
}
