package com.grinder.game.content.miscellaneous.voting;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager.Points;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.util.Misc;

public abstract class StreakReward {

	public static final int MIN_REDUCTION = 15;

	private static int MAX_STREAK_PENALTY = 25;
	private static ArrayList<StreakReward> streakRewards = null;

	private final int requiredStreak;
	private final int resetStage;
	private final boolean requireNoPenalty;
	private final String description;
	private final String privateMessage;
	private final String publicMessage;

	public StreakReward(int streak, int resetStage, boolean requireNoPenalty, String description, String privateMessage,
			String publicMessage) {
		this.requiredStreak = streak;
		this.resetStage = resetStage;
		this.requireNoPenalty = requireNoPenalty;
		this.description = description;
		this.privateMessage = privateMessage;
		this.publicMessage = publicMessage;
	}

	public void rewardPlayer(Player c) {
		if (privateMessage != null) {
			c.sendMessage(privateMessage);
		}
		if (publicMessage != null) {
//			String broadcastMessage = publicMessage.replaceAll("#1", c.getUsername());
//			PlayerHandler.broadcastMessage(broadcastMessage, c);
		}
		giveReward(c);
	}

	public boolean isEligible(float prevStreak, float currentStreak) {
		return justAchievedStreak(prevStreak, currentStreak) || (isRecursive() && currentStreak >= requiredStreak);
	}

	private boolean isRecursive() {
		return resetStage > 0;
	}

	public boolean justAchievedStreak(float prevStreak, float currentStreak) {
		return prevStreak < requiredStreak && currentStreak >= requiredStreak;
	}

	abstract void giveReward(Player c);

	public static ArrayList<StreakReward> getVoteStreakRewards(float prevStreak, float currentStreak,
			float gainedStreak) {
		ArrayList<StreakReward> voteStreakRewards = new ArrayList<StreakReward>();
		for (StreakReward reward : getStreakRewards()) {
			if (reward.isEligible(prevStreak, currentStreak)) {
				if (reward.justAchievedStreak(prevStreak, currentStreak)) {
					voteStreakRewards.add(reward);
				} else {
					float prevStreakMod = prevStreak % reward.resetStage;
					if (prevStreakMod < reward.resetStage) {
						if (prevStreakMod + gainedStreak >= reward.resetStage) {
							voteStreakRewards.add(reward);
						}
					}
				}
			}
		}
		return voteStreakRewards;
	}

	public static ArrayList<StreakReward> getStreakRewards() {
		if (streakRewards == null) {
			initialize();
		}
		return streakRewards;
	}

	public static void initialize() {
		streakRewards = new ArrayList<StreakReward>();
		addReward(new StreakReward(1, -1, true, "Unlock max hits display at equipments.",
				"@or3@You have unlocked max hits display!",
				"@or3@#1 has unlocked max hits display as voting streak reward!") {
			@Override
			void giveReward(Player c) {
			}
		});

		addReward(new StreakReward(3, -1, false, "Set Bonesack collect limit to 25 extra bones.",
				"@or3@Your Bonesack collect limit was expanded by 25 as voting streak reward!",
				"@or3@#1 got his Bonesack collect limit expanded by 25 as voting streak reward!") {
			@Override
			void giveReward(Player c) {
			}
		});

		addReward(new StreakReward(5, -1, true, "Allow Bonesack to hold Dagannoth bones.",
				"@or3@Your Bonesack can now hold dagannoth bones!",
				"@or3@#1 got his Bonesack upgraded as voting streak reward!") {
			@Override
			void giveReward(Player c) {
			}
		});

		addReward(new StreakReward(5, 5, false, "2 Clue Scroll Reward Re-roll credit",
				"@or3@You received 2 scroll reward re-roll as voting streak reward!",
				"@or3@#1 received 2 scroll reward re-roll as voting streak reward!") {
			@Override
			void giveReward(Player c) {
			}
		});

		addReward(new StreakReward(6, 6, false, "1 dicing host credit",
				"@or3@You received 1 dicing host credit as voting streak reward!",
				"@or3@#1 received 1 dicing host credit as voting streak reward!") {
			@Override
			void giveReward(Player c) {
			}
		});

		addReward(new StreakReward(8, -1, false, "Set Bonesack collect limit to 50 extra bones",
				"@or3@Your Bonesack collect limit was expanded by 50 as voting streak reward!",
				"@or3@#1 got his Bonesack collect limit expanded by 50 as voting streak reward!") {
			@Override
			void giveReward(Player c) {
			}
		});

		addReward(new StreakReward(10, 10, false, "500K experience bonus",
				"@or3@You received 500k of experience bonus as voting streak reward!",
				"@or3@#1 received 500k of experience bonus as voting streak reward!") {
			@Override
			void giveReward(Player c) {
			}
		});

		addReward(new StreakReward(12, 12, false, "2 member days",
				"@or3@You have received 2 member days as voting streak reward!",
				"@or3@#1 has received 2 member days as voting streak reward!") {
			@Override
			void giveReward(Player c) {
			}
		});

		addReward(new StreakReward(14, 14, false, "Convert your current voting streak points into voting points.", null,
				null) {
			@Override
			void giveReward(Player c) {
			}
		});

		addReward(new StreakReward(20, -1, false, "Double Castle Wars reward boost",
				"You have now double Castle Wars reward boost as voting streak reward!",
				"@or3@#1 now has double castle wars reward boost from voting reward!") {
			@Override
			void giveReward(Player c) {
				c.sendMessage("Congratulations! Now as long as you maintain your voting streak you will earn double");
				c.sendMessage("Castle Wars rewards!");
			}
		});

		addReward(new StreakReward(24, -1, false, "Unlock permanently 20 extra bank spaces.",
				"You've unlocked 20 extra bank spaces as voting streak reward!",
				"@or3@#1 has unlocked 20 extra bank spaces as voting streak reward!") {
			@Override
			void giveReward(Player c) {
			}
		});

		addReward(new StreakReward(31, 31, false, "5000 premium credits",
				"You have received 5000 premium points as voting streak reward!",
				"@or3@#1 has received 5000 premium points as voting streak reward!") {
			@Override
			void giveReward(Player c) {
			}
		});
	}

	private static void addReward(StreakReward voteStreakReward) {
		if (!streakRewards.contains(voteStreakReward)) {
			streakRewards.add(voteStreakReward);
		}
	}



	public static void displayVotingRewards(Player c) {
		ArrayList<String> votingRewards = new ArrayList<String>();
		votingRewards.add("@red@Bonus voting rewards from day 1 to 5!");
		votingRewards.add("@or3@Players who have voted in the last 12 hours during bonus days");
		votingRewards.add("@or3@will gain some extra rewards:");
		votingRewards.add("----------------------------------------------");
		votingRewards.add("All votes will reward 50% streak boost.");
		votingRewards.add("Players will gain 30% boosted vote points reward.");
		votingRewards.add("Players will gain 25% XP boost.");
		votingRewards.add("Players will gain 20% PK points boost.");
		votingRewards.add("----------------------------------------------");
		votingRewards.add("");
		votingRewards.add("Every time you vote you will receive voting points.");
		votingRewards.add("The amount of points received is based upon which sites");
		votingRewards.add("you voted on, each site offer a different amount of points:");
		votingRewards.add("RuneLocus(3), Top100Arena(2), TopG(2), MMOTopList(1)");
		votingRewards.add("");
		votingRewards.add("Voting on a daily basis will reward you with voting streaks.");
		votingRewards.add("If you vote between an interval of 12 and 16 hours you will");
		votingRewards.add("gain 50% more streak points than usual.");
		votingRewards.add("Talk to Akrisae at Edgeville to find out more about voting streaks!");
	}

	public static boolean hasStreak(Player c, int points) {
		return c.getPoints().get(Points.VOTING_STREAK)>= points;
	}

	public static boolean hasCleanStreak(Player c, int points) {
		return c.getAttributes().numInt(Attribute.STREAK_PENALTY) <= 0 && c.getPoints().get(Points.VOTING_STREAK) >= points;
	}

	@Override
	public String toString() {
		return "StreakReward [requiredStreak=" + requiredStreak + ", resetStage=" + resetStage + ", description="
				+ description + ", privateMessage=" + privateMessage + ", publicMessage=" + publicMessage + "]";
	}
}

