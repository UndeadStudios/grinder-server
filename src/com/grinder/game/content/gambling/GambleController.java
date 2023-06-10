package com.grinder.game.content.gambling;

import com.grinder.Server;
import com.grinder.game.GameConstants;
import com.grinder.game.content.dueling.Scoreboard;
import com.grinder.game.content.gambling.blackjack.BlackJack;
import com.grinder.game.content.gambling.dicing.RollDiceTask;
import com.grinder.game.content.gambling.flower_poker.PlantFlowerTask;
import com.grinder.game.content.miscellaneous.npcs.AnnouncerNPC;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerSaving;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.event.PlayerEvents;
import com.grinder.game.model.Animation;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.time.SecondsTimer;
import com.grinder.util.tools.DupeDetector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a controller for gamble sessions between two players.
 *
 * @author Blake
 */
public final class GambleController {

    private final Player player;
    private final ItemContainer container;

    private Player other;

    private GambleType mode = GambleType.FLOWER_POKER;
    private GambleState state = GambleState.NONE;

    private final SecondsTimer requestDelay = new SecondsTimer();

    /**
     * Constructs a new {@link GambleController}.
     *
     * @param player the {@link Player} for whom to controll gamble actions.
     */
    public GambleController(Player player) {
        this.player = player;
        container = new GambleContainer(player);
        player.subscribe(event -> {
            if(event == PlayerEvents.LOGGED_OUT){
                if(state != GambleState.NONE) {
                    final Player other_ = other;
                    decline();
                    if(other != null)
                        other_.getGambling().decline();
                }
            }
            return false;
        });
    }

    public void request(Player other) {

        player.setEntityInteraction(other);

        if (GambleUtil.cannotRequestGamble(other, player))
            return;

        if (Server.isUpdating()) {
            player.sendMessage("You cannot gamble with other players while the server is being updated.");
            return;
        }

        player.setPositionToFace(other.getPosition());

        if (state == GambleState.NONE || state == GambleState.REQUESTED) {

            if (!requestDelay.finished()) {
                final int seconds = requestDelay.secondsRemaining();
                player.sendMessage("You must wait another " + (seconds == 1 ? "second" : "" + seconds + " seconds") + " before sending more gamble requests.");
                return;
            }

            setOther(other);
            setState(GambleState.REQUESTED);

            if (GambleUtil.isBusy(other, player))
                return;

            final GambleController otherController = other.getGambling();
            final GambleState otherState = otherController.getState();
            final Player otherOther = otherController.getOther();

            final boolean start = otherState == GambleState.REQUESTED && otherOther == player;

            if (start) {
                initiate();
                otherController.initiate();
                final GambleType challengerGameType = GambleType.values()[other.getAttributes().numAttr(Attribute.GAMBLE_TYPE, 3).getValue().intValue()];
                final GambleType playerChallengerGameType = GambleType.values()[player.getAttributes().numAttr(Attribute.GAMBLE_TYPE, 3).getValue().intValue()];
                selectMode(challengerGameType);
                selectMode(playerChallengerGameType);
            } else {
                player.sendMessage("You've sent a gambling request to " + other.getUsername() + ".");
                other.sendMessage(player.getUsername() + ":gamblereq:");
            }
            requestDelay.start(1);
        } else {
            player.sendMessage("You can't do that right now.");
        }
    }

    private void initiate() {

        if (GambleUtil.notInDicingArea(player) || GambleUtil.notInDicingArea(other))
            return;

        player.setPositionToFace(other.getPosition());

        setState(GambleState.SELECT_RULES);

        player.getMotion().reset();
        player.getMotion().cancelTask();
        player.getMotion().resetTargetFollowing();

        other.getMotion().reset();
        other.getMotion().cancelTask();
        other.getMotion().resetTargetFollowing();

        player.getPacketSender().sendString(GambleConstants.STATUS_STRING_ID, "");
        player.getPacketSender().sendString(GambleConstants.GAMBLING_WITH_STRING_ID, "Gambling with: " + getOther().getUsername());
        player.setPositionToFace(getOther().getPosition());



        container.resetItems();
        container.refreshItems();
    }

    public void decline() {
        if (state == GambleState.SELECT_RULES || state == GambleState.ACCEPTED_RULES) {

            for (Item stakedItem : container.getValidItems())
                ItemContainerUtil.switchItem(container, player.getInventory(), stakedItem.clone(), false, false);

            player.getInventory().refreshItems();
            resetAttributes();

            player.sendMessage("Gamble session declined.");
            player.getPacketSender().sendInterfaceRemoval();
        }
    }

    public void accept() {

        EntityExtKt.markTime(player, Attribute.LAST_DICE_BUTTON);


        if (state == GambleState.SELECT_RULES) {

            final GambleController otherController = other.getGambling();
            final GambleState otherState = otherController.getState();
            final ItemContainer otherContainer = otherController.getContainer();

            final List<Item> playerItems = container.getValidItems();
            final List<Item> otherItems = otherContainer.getValidItems();

            if (GambleUtil.maxStackItems(otherContainer, playerItems, other)) {
                player.sendMessage("@or3@" + other.getUsername() + " won't be able to hold the total quantity of one ore more items in his or her inventory.", 2000);
                other.sendMessage("Dice can't be accepted, you can't hold the total quantity of one ore mote items.", 2000);
                return;
            }

            final int slotsNeeded = GambleUtil.countRequiredSlots(other, player, playerItems, otherItems);
            final int freeSlots = other.getInventory().countFreeSlots();

            if (slotsNeeded > freeSlots) {
                player.sendMessage("@or3@" + other.getUsername() + " won't be able to hold that much items. They have " + freeSlots + " free inventory slot" + (freeSlots == 1 ? "." : "s") + ".", 2000);
                other.sendMessage("Dice can't be accepted, you don't have enough free inventory space.", 2000);
                return;
            }

            if (PlantFlowerTask.someonePlanting()) {
                player.sendMessage("Please wait until the other party finishes the flower gamble.", 1000);
                other.sendMessage("Please wait until the other party finishes the flower gamble.", 1000);
                return;
            }

            if (Server.isUpdating()) {
                player.sendMessage("You cannot gamble with other players while the server is being updated.");
                return;
            }

            setState(GambleState.ACCEPTED_RULES);

            player.getPacketSender().sendString(GambleConstants.STATUS_STRING_ID, "Waiting for other player...");
            other.getPacketSender().sendString(GambleConstants.STATUS_STRING_ID, "Other player has accepted!");

            if (state == GambleState.ACCEPTED_RULES && otherState == GambleState.ACCEPTED_RULES) {

                player.getMotion().reset();
                player.getMotion().cancelTask();
                player.getMotion().resetTargetFollowing();

                other.getMotion().reset();
                other.getMotion().cancelTask();
                other.getMotion().resetTargetFollowing();

                setState(GambleState.STARTED);
                otherController.setState(GambleState.STARTED);

                player.setStatus(PlayerStatus.DICING);
                other.setStatus(PlayerStatus.DICING);

                startGame(playerItems, otherItems);

                player.setBlockLogout(true);
                other.setBlockLogout(true);

                player.BLOCK_ALL_BUT_TALKING = true;
                other.BLOCK_ALL_BUT_TALKING = true;

                player.getPacketSender().sendInterfaceRemoval();
                other.getPacketSender().sendInterfaceRemoval();
            }
        }
    }

    private void startGame(List<Item> playerItems, List<Item> otherItems) {
        if (mode == GambleType.BOTH_HOST) {
            if (Misc.randomBoolean())
                TaskManager.submit(new RollDiceTask(player, other, mode, playerItems, otherItems));
            else
                TaskManager.submit(new RollDiceTask(other, player, mode, playerItems, otherItems));
        } else if (mode == GambleType.YOU_HOST) {
            TaskManager.submit(new RollDiceTask(player, other, mode, playerItems, otherItems));
        } else if (mode == GambleType.OTHER_HOST) {
            TaskManager.submit(new RollDiceTask(other, player, mode, playerItems, otherItems));
        } else if (mode == GambleType.FLOWER_POKER) {
            TaskManager.submit(new PlantFlowerTask(other, player, playerItems, otherItems));
        } else if (mode == GambleType.BLACKJACK) {
            BlackJack.addGame(player, other, playerItems, otherItems);
        } else { // Just in case
            TaskManager.submit(new PlantFlowerTask(other, player, playerItems, otherItems));
        }
    }

    public void end(Player winner, Player loser, List<Item> firstItems, List<Item> secondItems, boolean draw, boolean removeInterface) {

        if (draw) {
            firstItems.forEach(item -> winner.getInventory().add(item, false));
            secondItems.forEach(item -> loser.getInventory().add(item, false));
            winner.getInventory().refreshItems();
            loser.getInventory().refreshItems();

            // Jinglebit
            winner.getPacketSender().sendJinglebitMusic(247, 0);
            loser.getPacketSender().sendJinglebitMusic(247, 0);
        } else {

            // Perform winner animation
            TaskManager.submit(2, () -> {
                if (removeInterface) {
                    winner.getPacketSender().sendInterfaceRemoval();
                    loser.getPacketSender().sendInterfaceRemoval();
                }
                winner.performAnimation(new Animation(862));
                loser.performAnimation(new Animation(860));
            });

            final ArrayList<Item> items = Misc.concat(firstItems, secondItems);


            if (!GameConstants.BONUS_DOUBLE_EXP_WEEKEND) {
                player.sendMessage("@red@Enjoy a full tax rate reduction every weekend while gambling and staking!");
                GambleTax.applyTaxIfApplicable(winner, loser, items);
            }

            GambleUtil.logWonItems(winner, loser, items);

            items.forEach(item -> winner.getInventory().add(item, false));
            winner.getInventory().refreshItems();

            winner.sendMessage("<img=749> @gre@Congratulations, you have won the pot!");
            winner.getPoints().increase(AttributeManager.Points.DICE_WINS);

            // Increase points
            winner.getPoints().increase(AttributeManager.Points.DICE_WINS, 1); // Increase points
            winner.getPoints().increase(AttributeManager.Points.GAMBLE_WIN_STREAK, 1); // Increase points

            loser.getPoints().increase(AttributeManager.Points.DICE_LOSES, 1); // Increase points
            loser.getPoints().set(AttributeManager.Points.GAMBLE_WIN_STREAK, 0); // Increase points

            winner.BLOCK_ALL_BUT_TALKING = false;
            loser.BLOCK_ALL_BUT_TALKING = false;


            // Jinglebit
            winner.getPacketSender().sendJinglebitMusic(244, 0);
            loser.getPacketSender().sendJinglebitMusic(268, 0);


            loser.sendMessage("<img=779> @red@You have lost the bet!");
            loser.getPoints().increase(AttributeManager.Points.DICE_LOSES);

            AnnouncerNPC.announceWinner(winner, loser, "gambling");

            Scoreboard.INSTANCE.addEntry(winner, loser, false, "gambling", mode.toString());

            Stream.concat(winner.getLocalPlayers().stream(), loser.getLocalPlayers().stream())
                    .distinct()
                    .forEach(p -> p.sendMessage("<img=770> " + Misc.capitalize(winner.getUsername()) + " has won the pot against " + loser.getUsername() + "!"));
        }

        DupeDetector.INSTANCE.check(winner);
        DupeDetector.INSTANCE.check(loser);

        PlayerSaving.save(winner);
        PlayerSaving.save(loser);

        winner.getGambling().resetAttributes();
        loser.getGambling().resetAttributes();

        winner.setBlockLogout(false);
        loser.setBlockLogout(false);
    }


    private void onContainerChange(){
        state = GambleState.SELECT_RULES;
        other.getGambling().setState(GambleState.SELECT_RULES);
        player.getPacketSender().sendString(GambleConstants.STATUS_STRING_ID, "");
        other.getPacketSender().sendString(GambleConstants.STATUS_STRING_ID, "");
    }

    public void withdraw(int id, int slot, int amount) {

        if(state != GambleState.SELECT_RULES && state != GambleState.ACCEPTED_RULES)
            return;

        if (player.getInterfaceId() == GambleConstants.INTERFACE_ID) {


            final ItemDefinition definition = ItemDefinition.forId(id);
            final boolean canTrade = definition.isTradeable();
            final boolean stacksInSlot = definition.isStackable();

            if (!canTrade) {
                player.sendMessage("You can't gamble untradeable items!");
                return;
            }

            if (amount == 1 || stacksInSlot) {
                final Item itemAtSlot = container.getItems()[slot];
                if (itemAtSlot != null && itemAtSlot.getId() == id)
                    amount = Math.min(amount, itemAtSlot.getAmount());
                else
                    return;
            } else {
                final int amountInContainer = container.getAmount(id);
                amount = Math.min(amount, amountInContainer);
            }

            if (amount <= 0)
                return;

            final Inventory inventory = player.getInventory();
            final int requiredSlots = stacksInSlot ? inventory.contains(id) ? 0 : 1 : amount;

            if (inventory.countFreeSlots() < requiredSlots) {
                inventory.full();
                return;
            }

            onContainerChange();

            container.delete(id, amount);
            inventory.add(new Item(id, amount), false);

            ItemContainerUtil.shiftValidItemsToLeft(container);
            container.refreshItems();
            inventory.refreshItems();
        }
    }

    public void deposit(int id, int slot, int amount) {

        if(state != GambleState.SELECT_RULES && state != GambleState.ACCEPTED_RULES)
            return;

        final Item item = new Item(id, amount);

        if (player.getInterfaceId() == GambleConstants.INTERFACE_ID) {

            if (GambleUtil.cannotGambleItem(id, item, player))
                return;

            if (player.getInventory().getItems()[slot].getId() == id) {

                final int freeSlots = container.countFreeSlots();

                boolean full = false;

                if (!ItemDefinition.forId(id).isStackable()) {
                    amount = Math.min(amount, freeSlots);

                    if (item.getAmount() > amount)
                        full = true;

                } else {
                    if (container.contains(id)) {
                        final int amountInContainer = getContainer().getAmount(id);
                        final long totalAmount = (long) amount + amountInContainer;
                        if (totalAmount > Integer.MAX_VALUE) {
                            amount = (int) (totalAmount - Integer.MAX_VALUE);
                            full = true;
                        }
                    } else if (freeSlots == 0)
                        amount = 0;
                }

                if (amount > 0) {

                    if (full)
                        container.full();

                    if (item.getAmount() > amount)
                        item.setAmount(amount);

                } else {
                    container.full();
                    return;
                }
                onContainerChange();
                ItemContainerUtil.switchItem(player.getInventory(), container, item, false, true);
            }
        }
    }

    public void resetAttributes() {
        setOther(null);
        setState(GambleState.NONE);
        //setType(GambleType.BOTH_HOST);
        setType(GambleType.values()[player.getAttributes().numAttr(Attribute.GAMBLE_TYPE, 3).getValue().intValue()]);

        if (player.getStatus() == PlayerStatus.DICING)
            player.setStatus(PlayerStatus.NONE);

        player.getPacketSender().sendString(GambleConstants.TAX_AMOUNT_LEFT_CHILD_ID, "");
        container.resetItems();
    }

    public boolean handleButton(int buttonId) {

        /*if (!EntityExtKt.passedTime(player, Attribute.GENERIC_ACTION, 1, TimeUnit.SECONDS, false, true)) {
            selectMode(getType());
            return true;
        }*/

        if (BlackJack.clickButton(player, buttonId))
            return true;

        switch (buttonId) {
            case 60021:
                accept();
                return true;
            case 60022:
                final Player other_ = other;
                decline();
                if (other_ != null)
                    other_.getGambling().decline();
                return true;
            case 60038:
                selectMode(GambleType.YOU_HOST);
                return true;
            case 60039:
                selectMode(GambleType.OTHER_HOST);
                return true;
            case 60040:
                selectMode(GambleType.BOTH_HOST);
                return true;
            case 60041:
                selectMode(GambleType.FLOWER_POKER);
                break;
            case 60042:
                selectMode(GambleType.BLACKJACK);
                break;
        }

        return false;
    }

    private void selectMode(GambleType mode) {

        if(state != GambleState.SELECT_RULES && state != GambleState.ACCEPTED_RULES)
            return;

        final GambleController playerController = other.getGambling();
        final GambleType playerState = playerController.getType();

        final GambleController otherController = other.getGambling();
        final GambleType otherState = otherController.getType();

        if(state == GambleState.ACCEPTED_RULES) {
            other.getGambling().setState(GambleState.SELECT_RULES);
            state = GambleState.SELECT_RULES;
        }

        player.getAttributes().numAttr(Attribute.GAMBLE_TYPE, mode.ordinal()).setValue(mode.ordinal());
        other.getAttributes().numAttr(Attribute.GAMBLE_TYPE, mode.ordinal()).setValue(mode.ordinal());

        switch (mode) {
            case YOU_HOST:
                if (mode == GambleType.YOU_HOST && otherState == GambleType.OTHER_HOST)
                    return;

                setType(GambleType.YOU_HOST);
                otherController.setType(GambleType.OTHER_HOST);
                onContainerChange();

                player.getPacketSender().sendConfig(GambleConstants.CONFIG_ID, 0);
                other.getPacketSender().sendConfig(GambleConstants.CONFIG_ID, 1);

                player.sendMessage("You are now the host!");
                other.sendMessage(player.getUsername() + " is now the host!");
                break;

            case OTHER_HOST:

                if (mode == GambleType.OTHER_HOST && otherState == GambleType.YOU_HOST)
                    return;

                setType(GambleType.OTHER_HOST);
                otherController.setType(GambleType.YOU_HOST);
                onContainerChange();

                player.getPacketSender().sendConfig(GambleConstants.CONFIG_ID, 1);
                other.getPacketSender().sendConfig(GambleConstants.CONFIG_ID, 0);

                player.sendMessage(other.getUsername() + " is now the host!");
                other.sendMessage("You are now the host!");
                break;

            case BOTH_HOST:

               if (mode == GambleType.BOTH_HOST && otherState == GambleType.BOTH_HOST)
                    return;

                setType(GambleType.BOTH_HOST);
                otherController.setType(GambleType.BOTH_HOST);
                onContainerChange();

                player.getPacketSender().sendConfig(GambleConstants.CONFIG_ID, mode.ordinal());
                other.getPacketSender().sendConfig(GambleConstants.CONFIG_ID, mode.ordinal());

                player.sendMessage("You both are now the hosts!");
                other.sendMessage("You both are now the hosts!");
                break;
            case FLOWER_POKER:

                if (mode == GambleType.FLOWER_POKER && otherState == GambleType.FLOWER_POKER)
                    return;

                setType(GambleType.FLOWER_POKER);
                otherController.setType(GambleType.FLOWER_POKER);
                onContainerChange();

                player.getPacketSender().sendConfig(GambleConstants.CONFIG_ID, mode.ordinal());
                other.getPacketSender().sendConfig(GambleConstants.CONFIG_ID, mode.ordinal());
                break;
            case BLACKJACK:

                if (mode == GambleType.BLACKJACK && otherState == GambleType.BLACKJACK)
                    return;

                setType(GambleType.BLACKJACK);
                otherController.setType(GambleType.BLACKJACK);
                onContainerChange();

                player.getPacketSender().sendConfig(GambleConstants.CONFIG_ID, mode.ordinal());
                other.getPacketSender().sendConfig(GambleConstants.CONFIG_ID, mode.ordinal());
                break;
        }
    }

    public Player getOther() {
        return other;
    }

    public void setOther(Player other) {
        this.other = other;
    }

    ItemContainer getContainer() {
        return container;
    }

    public GambleType getType() {
        return mode;
    }

    public void setType(GambleType mode) {
        this.mode = mode;
    }

    public GambleState getState() {
        return state;
    }

    public void setState(GambleState state) {
        this.state = state;
    }

    public boolean inGambleWindow() {
        return player.getInterfaceId() == GambleConstants.INTERFACE_ID && (state == GambleState.SELECT_RULES || state == GambleState.ACCEPTED_RULES);
    }
}
