package com.grinder.game.content.cluescroll.test;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.content.cluescroll.scroll.ScrollDifficulty;
import com.grinder.game.content.cluescroll.task.ClueTaskFactory;
import com.grinder.game.content.cluescroll.task.ClueTask;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-08
 */
public class SetScrollTaskCommand implements Command {

    @Override
    public String getSyntax() {
        return "[taskid] [diff]";
    }

    @Override
    public String getDescription() {
        return "Sets your clue task and string difficulty.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {

        final int taskID = Integer.parseInt(parts[1]);
        final String difficulty = parts[2];
        final ClueTask task = ClueTaskFactory.getInstance().getTaskForID(taskID);

        if (task != null) {
            final ScrollDifficulty scrollDifficulty =  ScrollDifficulty.forName(difficulty.toUpperCase());

            if (scrollDifficulty != null) {
                if(!player.getInventory().contains(scrollDifficulty.getScrollID())){
                    player.getInventory().add(scrollDifficulty.getScrollID(), 1);
                }
                ClueTaskFactory.getInstance().setTask(player, scrollDifficulty, task.clone());
            }

            player.sendMessage("A clue scroll task was set: " + task.getTaskID());
        } else
            player.sendMessage("The task was not found.");
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().isStaff(PlayerRights.DEVELOPER);
    }
}
