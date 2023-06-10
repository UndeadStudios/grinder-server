package com.grinder.game.model.progresstracker;

import com.grinder.game.entity.agent.player.Player;

import java.util.HashMap;

public class ProgressTracker {

	public static final String MAIN_DIRECTORY = "./data/saves/progress/";

	public HashMap<String, Integer> progress = new HashMap<String, Integer>();

	private int mainInterface;

	public ProgressTracker() {

	}

	public ProgressTracker(int mainInterface) {
		this.mainInterface = mainInterface;
	}

	public void open(Player player) {
		update();

		if (player != null) {
			player.getPacketSender().sendInterface(mainInterface);
		}
	}

	public void setProgress(String progress, int value) {
		this.progress.put(progress, value);
	}

	public void progress(String progress) {
		progress(progress, 1);
	}

	public void progress(String progress, int amount) {
		if (this.progress.get(progress) == null) {
			this.progress.put(progress, 0);
		}
		this.progress.merge(progress, amount, Integer::sum);
	}

	public int getProgress(String progress) {
		if (this.progress.get(progress) == null) {
			return 0;
		}
		return this.progress.get(progress);
	}

	public int getRequirement(String progress) {
		if (getRequirement() == null) {
			return 1;
		}
		if (getRequirement().get(progress) == null) {
			return 1;
		}
		return getRequirement().get(progress);
	}

	public int getPercentage(String progress) {
		final int completed = getProgress(progress);

		int required = getRequirement(progress);

		if (completed >= required) {
			return 100;
		}

		return completed * 100 / required;
	}

	public int getTotalCompleted() {
		int total = 0;
		for (String s : progress.keySet()) {
			if (complete(s)) {
				total++;
			}
		}
		return total;
	}

	public boolean allCompleted() {
		return getTotalCompleted() >= getRequirement().size();
	}

	public void reset() {
		progress.clear();
	}

	public String sendTotalCompleted() {
		return "Completed: " + getTotalCompleted() + "/" + getRequirement().size();
	}

	public String sendProgress(String progress) {
		return "Completed: " + getProgress(progress) + "/" + getRequirement(progress);
	}

	public boolean complete(String progress) {
		return getPercentage(progress) == 100;
	}

	public void decrease(String progress, int amount) {
		int current = getProgress(progress);

		if (amount > current) {
			amount = current;
		}

		if (amount == 0) {
			return;
		}

		setProgress(progress, current - amount);
	}

	public void update() {

	}

	public HashMap<String, Integer> getRequirement() {
		return null;
	}
}
