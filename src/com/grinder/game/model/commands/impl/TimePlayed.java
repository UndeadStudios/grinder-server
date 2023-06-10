package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.util.time.TimeUnits;

public class TimePlayed implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Shouts the current total playing time.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {
		try {

			TimeUnits firstUnit = TimeUnits.getHighestUnitForMilliseconds(player.getTimePlayed(TimeUnits.MILLISECOND));
			TimeUnits secondUnit = TimeUnits
					.getSecondHighestUnitForMilliseconds(player.getTimePlayed(TimeUnits.MILLISECOND));

			long firstAmount = player.getTimePlayed(TimeUnits.MILLISECOND) / firstUnit.getMilisecondValue();
			long secondAmount = player.getTimePlayed(TimeUnits.MILLISECOND) % firstUnit.getMilisecondValue()
					/ secondUnit.getMilisecondValue();

			String message = "I've been playing for a total of " + firstAmount + " " + firstUnit.name().toLowerCase()
					+ (firstAmount != 1 ? "s, and " : ", and ") + secondAmount + " " + secondUnit.name().toLowerCase()
					+ (firstAmount != 1 ? "s." : ". ");

			player.say(message);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    public boolean canUse(Player player) {
        return player.getTimePlayed(TimeUnits.SECOND) > 0;
    }
}