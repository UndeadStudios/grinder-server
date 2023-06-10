package com.grinder.game.content.cluescroll.test;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.content.cluescroll.task.ClueTask;
import com.grinder.game.content.cluescroll.task.ClueTaskFactory;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

public class TestClueTaskInterfaces implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Displays a array list of clues interface.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {

        final int delay = 2;
        final boolean checkAll = true;
        final int[] check = {221, 222, 223, 225, 226, 229, 230, 236, 303, 317, 318, 319, 320, 321, 327, 329, 334};

        final Map<Integer, ClueTask> taskMap = ClueTaskFactory.getInstance().getTaskMap();
        final Set<Map.Entry<Integer, ClueTask>> entries = taskMap.entrySet();

        if(!checkAll)
            entries.removeIf(integerClueTaskEntry -> IntStream.of(check).noneMatch(id -> integerClueTaskEntry.getKey() == id));

        final Iterator<Map.Entry<Integer, ClueTask>> iterator = entries.iterator();

        TaskManager.submit(new Task(delay, false) {
            @Override
            protected void execute() {

                if(!iterator.hasNext()) {
                    stop();
                    return;
                }

                final Map.Entry<Integer, ClueTask> next = iterator.next();
                final ClueTask task = next.getValue();

                final int interfaceId = task.clueScroll.getClueGuide().getInterfaceID();

                task.openGuide(player);

                player.sendMessage("Currently viewing clue task "+next.getKey()+", interface = "+interfaceId);
            }
        }.bind(player));

    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().isStaff(PlayerRights.DEVELOPER);
    }
}
