package com.grinder.game.content.gambling.dicing;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.gambling.GambleType;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Color;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.Animation;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.Task;
import com.grinder.util.random.RandomGen;

import java.util.List;

/**
 * Represents a task that is executed upon dice rolling.
 *
 * @author Blake
 */
public class RollDiceTask extends Task {

    private final Player host;
    private final Player other;
    private final GambleType mode;
    private final List<Item> firstItems;
    private final List<Item> secondItems;
    private int cycle;

    public RollDiceTask(Player host, Player other, GambleType mode, List<Item> firstItems, List<Item> secondItems) {
        this.host = host;
        this.other = other;
        this.mode = mode;
        this.firstItems = firstItems;
        this.secondItems = secondItems;
    }

    /**
     * Get a dice percentile value
     *
     * @return percentile value of the dice.
     */
    public static int rollDice() {
        // The {@RandomGen} which will help us randomize numbers..
        RandomGen random = new RandomGen();
        //double rollAmount = (double) (Math.floor(random.nextFloat() * 10 + 0.4) / 10);
        int rollAmount = (random.get().nextInt(101));
        if (rollAmount <= 55) {
            return random.get().nextInt(55);
        } else {
            return 56 + random.get().nextInt(45);
        }
    }


    @Override
    protected void execute() {
        if (cycle == 0) {
            startDiceRoll(host);
            startDiceRoll(other);
            host.performAnimation(new Animation(6703));
            if (mode == GambleType.BOTH_HOST)
                other.performAnimation(new Animation(6703));
            sendInfo();
        } else if (cycle == 3) {
            int hostRoll = rollDice();
            if (mode == GambleType.BOTH_HOST) {
                int otherRoll = rollDice();
                EntityExtKt.removeAttribute(host, Attribute.DICE_SCORE);
                EntityExtKt.removeAttribute(other, Attribute.DICE_SCORE);
                onRoll(hostRoll, otherRoll);
            } else {
                if (hostRoll >= 100) {
                    AchievementManager.processFor(AchievementType.OVER_DICE, host);
                }
                other.sendMessage("<img=770>@dre@ " + host.getUsername() + " @bla@rolled @whi@" + hostRoll + " @bla@on the percentile dice.");
                host.sendMessage("<img=770>@red@ You @bla@rolled @whi@" + hostRoll + " @bla@on the percentile dice.");
                host.say("I have rolled " + hostRoll + "% on the percentile dice!");
                EntityExtKt.setInt(host, Attribute.DICE_ROLL, hostRoll, 0);
            }
        } else if (cycle == 4 && mode != GambleType.BOTH_HOST) {
            final int hostRoll = getRoll(host);
            final Player winner = (hostRoll >= 0 && hostRoll <= 55) ? host : other;
            final Player loser = winner == other ? host : other;
            host.getGambling().end(winner, loser, firstItems, secondItems, false, true);
            EntityExtKt.removeAttribute(host, Attribute.DICE_ROLL);
            PlayerExtKt.unblock(host, true, true);
			PlayerExtKt.unblock(other, true, true);
            stop();
        } else if (mode == GambleType.BOTH_HOST) {
            if (cycle == 6) sendScore();
            else if (cycle == 9) rollOrFinish();
            else if (cycle == 12) sendScore();
            else if (cycle == 15) rollOrFinish();
            else if (cycle == 18) checkDraw();
            else if (cycle == 21) rollOrFinish();
            else if (cycle == 24) checkDraw();
            else if (cycle == 27) rollOrFinish();
            else if (cycle == 30) finishGame();
        }
        cycle++;
    }

    private static void startDiceRoll(Player player) {
        SkillUtil.stopSkillable(player);
        PlayerExtKt.block(player, true, false);
        PlayerExtKt.resetInteractions(player, true, false);
        PlayerExtKt.message(player,"<img=770> @whi@Rolling...", Color.NONE);
    }

    private void rollOrFinish() {
        if (getScore(other) != 3 && getScore(host) != 3) {
            host.performAnimation(new Animation(6703));
            other.performAnimation(new Animation(6703));
            int hostRoll = rollDice();
            int otherRoll = rollDice();
            onRoll(hostRoll, otherRoll);
        } else
            finishGame();
    }

    private void finishGame() {
        final Player winner = getScore(host) > getScore(other) ? host : other;
        final Player loser = winner == other ? host : other;
        final boolean draw = getScore(host) == getScore(other);
        host.getGambling().end(winner, loser, firstItems, secondItems, draw, true);
        EntityExtKt.removeAttribute(host, Attribute.DICE_SCORE);
        EntityExtKt.removeAttribute(other, Attribute.DICE_SCORE);
        PlayerExtKt.unblock(host, true, true);
        PlayerExtKt.unblock(other, true, true);
        stop();
    }

    private void checkDraw() {
        sendScore();
        if (getScore(other) == 3 && getScore(host) == 3)
            finishGame();
    }

    private void onRoll(int hostRoll, int otherRoll) {
        if (hostRoll >= 100) {
            AchievementManager.processFor(AchievementType.OVER_DICE, host);
        }
        sendRolls(hostRoll, otherRoll);
        if (hostRoll > otherRoll) {
            increaseScore(host);
        } else if (hostRoll == otherRoll) {
            increaseScore(host);
            increaseScore(other);
        }
        if (otherRoll > hostRoll)
            increaseScore(other);
    }

    private void increaseScore(Player player) {
        EntityExtKt.incInt(player, Attribute.DICE_SCORE, 1, 3, 0);
    }

    private int getScore(Player player) {
        return EntityExtKt.getInt(player, Attribute.DICE_SCORE, 0);
    }

    private int getRoll(Player player) {
        return EntityExtKt.getInt(player, Attribute.DICE_ROLL, 0);
    }

    private void sendScore() {
        host.sendMessage("<img=770>@bla@ Score: " + "@red@" + getScore(host) + " @bla@- @dre@" + getScore(other));
        other.sendMessage("<img=770>@bla@ Score: " + "@red@" + getScore(other) + " @bla@- @dre@" + getScore(host));
        // doesn't display above head due to dice roll, I think the above way makes it clearer what the score is ^
//        host.say(getScore(host) + "/3");
//        other.say(getScore(other) + "/3");
    }

    private void sendRolls(int hostRoll, int otherRoll) {
        host.sendMessage("<img=770>@red@ You rolled @whi@" + hostRoll + " @red@on the percentile dice.");
        host.say("I have rolled " + hostRoll + "% on the percentile dice!");
        other.sendMessage("<img=770>@red@ You rolled @whi@" + otherRoll + " @red@on the percentile dice.");
        other.say("I have rolled " + otherRoll + "% on the percentile dice!");
        host.sendMessage("<img=770>@dre@ " + other.getUsername() + " @dre@rolled @whi@" + otherRoll + " @dre@on the percentile dice.");
        other.sendMessage("<img=770>@dre@ " + host.getUsername() + " @dre@rolled @whi@" + hostRoll + " @dre@on the percentile dice.");
    }

    private void sendInfo() {
        if (mode == GambleType.BOTH_HOST) {
            host.sendMessage("<img=770>@bla@ Both of the players are now considered the hosts.");
            other.sendMessage("<img=770>@bla@ Both of the players are now considered the hosts.");
            host.sendMessage("<img=770>@bla@ Your chance: @whi@" + mode.getWinChance());
            other.sendMessage("<img=770>@bla@ Your chance: @whi@" + mode.getWinChance());
        } else {
            host.sendMessage("<img=770>@red@ You are now the host.");
            other.sendMessage("<img=770>@dre@ " + host.getUsername() + " is now the host.");

            if (mode == GambleType.YOU_HOST) {
                host.sendMessage("<img=770>@bla@ Your chance to win: @whi@0-" + mode.getWinChance());
                other.sendMessage("<img=770>@bla@ Your chance to win: @whi@56-100");

                host.sendMessage("<img=770>@bla@ Other chance to win: @whi@56-100");
                other.sendMessage("<img=770>@bla@ Other chance to win:@whi@ 0-" + mode.getWinChance());
            } else {
                host.sendMessage("<img=770>@bla@ Your chance to win: @whi@56-100");
                other.sendMessage("<img=770>@bla@ Your chance to win: @whi@0-55");

                host.sendMessage("<img=770>@bla@ Other chance to win: @whi@56-100");
                other.sendMessage("<img=770>@bla@ Other chance to win: @whi@0-55");
            }
        }
    }
}
