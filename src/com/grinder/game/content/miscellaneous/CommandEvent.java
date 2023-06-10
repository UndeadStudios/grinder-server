package com.grinder.game.content.miscellaneous;

import com.grinder.game.model.attribute.AttributeManager.Points;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

import static com.grinder.util.ItemID.COINS;
import static com.grinder.util.ItemID.BLOOD_MONEY;

/**
 * Handles the command event
 *
 * @author 2012
 */
public class CommandEvent {

    /**
     * The valid chars
     */
    private static final String CHARACTERS[] = {"z", "x", "c", "a", "s", "q"};

    /**
     * The easy rewards
     */
    private static final Item[] EASY_REWARDS = {new Item(COINS, 3000000), new Item(BLOOD_MONEY, 1000), new Item(BLOOD_MONEY, 1200), new Item(1632, 25), new Item(1748, 30), new Item(532, 40), new Item(532, 20), new Item(4812, 1), new Item(536, 10),
            new Item(1618, 30), new Item(1780, 100), new Item(200, 25), new Item(2486, 15), new Item(13431, 25), new Item(5291, 10), new Item(2350, 100), new Item(1437, 250), new Item(7937, 100),
            new Item(318, 250), new Item(336, 100), new Item(342, 100), new Item(378, 120), new Item(391, 50), new Item(995, 2000000)};

    /**
     * The hard rewards
     */
    private static final Item[] HARD_REWARDS = {new Item(536, 20), new Item(995, 4500000), new Item(536, 25), new Item(536, 30), new Item(995, 5500000),
            new Item(ItemID.LAVA_DRAGON_BONES_2, 7), new Item(4812, 7), new Item(536, 20), new Item(8322, 1), new Item(775, 1), new Item(21009, 1), new Item(9075, 100), new Item(6809, 1), new Item(537, 15), new Item(13431, 25), new Item(892, 100), new Item(21905, 50),
            new Item(565, 200), new Item(563, 250), new Item(384, 250), new Item(10589, 1), new Item(10564, 1), new Item(561, 250), new Item(12696, 10), new Item(4835, 5), new Item(1512, 350), new Item(1520, 250), new Item(1516, 75), new Item(BLOOD_MONEY, 2000), new Item(BLOOD_MONEY, 3500), new Item(BLOOD_MONEY, 5000),};

    /**
     * The command
     */
    private static String word;

    /**
     * Whether completed
     */
    private static boolean completed;

    /**
     * The time it took
     */
    private static long time;

    /**
     * Hard event
     */
    private static boolean hard;

    /**
     * Points event
     */
    private static boolean points;

    /**
     * The reward
     */
    private static Item reward;

    private static Points pointsReward;

    /**
     * Checking if correct
     *
     * @param player  the player
     * @param command the command
     */
    public static boolean check(Player player, String command) {

        /*
         * Already completed
         */
        if (completed) {
            return false;
        }
		/*if (player.getGameMode().isUltimate() && command.equals(word)) {
			player.getPacketSender().sendMessage("You're not allowed to play the command trivia as an Hardcore Iron Man.");
			return false;
		}*/
        if (player.getGameMode().isSpawn() && command.equals(word)) {
            player.getPacketSender().sendMessage("You're not allowed to play the command trivia in spawn game mode.");
            return false;
        }
        /*
         * Matches
         */
        if (command.equals(word)) {
            completed = true;
            AchievementManager.processFor(AchievementType.PARTY_UP, player);
            if (reward.getAmount() <= 1 || reward.getId() == 8322) {
                PlayerUtil.broadcastMessage("<img=749> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has just claimed @dre@" + reward.getDefinition().getName() + "</col> from the command trivia!");
            } else {
                PlayerUtil.broadcastMessage("<img=749> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has just claimed @dre@" + Misc.insertCommasToNumber(reward.getAmount()) + "x " + reward.getDefinition().getName() + "</col> from the command trivia!");
            }
            if (player.getGameMode().isUltimate()) {
                if (!reward.getDefinition().isNoted() && reward.getDefinition().getNoteId() != -1) {
                    ItemContainerUtil.dropUnder(player,reward.getId() + 1, reward.getAmount());
                } else {
                    ItemContainerUtil.dropUnder(player,reward.getId(), reward.getAmount());
                }
            } else {
                BankUtil.addToBank(player, reward);
            }
            player.getPacketSender().sendMessage("The reward " + (player.getGameMode().isUltimate() ? "is dropped under you" : "has been sent to your bank") + ".");
            // Collection Log Entry
            player.getCollectionLog().createOrUpdateEntry(player,  "Command Trivia", new Item(reward.getId(), reward.getAmount()));
            return true;
        }
        return false;
    }

    /**
     * Sends the announcement
     */
    public static void sendAnnouncement() {
        completed = false;
        hard = Misc.getRandomInclusive(5) == 1;
        reward = hard ? EASY_REWARDS[Misc.getRandomInclusive(EASY_REWARDS.length - 1)]
                : HARD_REWARDS[Misc.getRandomInclusive(HARD_REWARDS.length - 1)];

        // Unnote the reward because it will be going to the bank
        if (reward.getDefinition().isNoted()) {
            reward = new Item(reward.getDefinition().unNote(), reward.getAmount());
        }

        time = System.currentTimeMillis();
        word = generateWord();
        if (hard) {
            PlayerUtil.broadcastMessage("<img=749> The first person to type ::" + word + " wins "
                    + Misc.insertCommasToNumber(reward.getAmount()) + " @dre@" + reward.getDefinition().getName() + ".");
        } else if (points) {
            PlayerUtil.broadcastMessage("<img=749> The first person to type ::" + word + " wins "
                    + Misc.insertCommasToNumber(pointsReward.name().toLowerCase()) + " " + Points.VOTING_POINTS);
        } else {
            if (reward.getDefinition().getId() == 8322 || reward.getAmount() == 1) {
                PlayerUtil.broadcastMessage("<img=749> The first person to type ::" + word + " wins @dre@" + reward.getDefinition().getName() + ".");
            } else {
                PlayerUtil.broadcastMessage("<img=749> The first person to type ::" + word + " wins @dre@"
                        + Misc.insertCommasToNumber(reward.getAmount()) + " " + reward.getDefinition().getName() + ".");
            }
        }
    }

    /**
     * Generates the command
     *
     * @return the command
     */
    private static String generateWord() {
        String returns = "";
        if (hard) {
            while (returns.length() < 15)
                returns += CHARACTERS[Misc.getRandomInclusive(CHARACTERS.length - 1)];
        } else {
            while (returns.length() < 8)
                returns += CHARACTERS[Misc.getRandomInclusive(CHARACTERS.length - 1)];
        }
        return returns;
    }

}