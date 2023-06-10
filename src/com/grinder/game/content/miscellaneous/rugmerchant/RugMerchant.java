package com.grinder.game.content.miscellaneous.rugmerchant;

import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.updating.block.BasicAnimationSet;
import com.grinder.game.model.Animation;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.timing.TimerKey;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

import static com.grinder.util.NpcID.*;


/**
 * @author L E G E N D
 * @date 2/16/2021
 * @time 4:32 AM
 * @discord L E G E N D#4380
 */
public final class RugMerchant extends NPC {


    public static final int COST = 500_000;
    public static final int TAKE_OFF_SOUND = 1196;
    public static final int LANDING_SOUND = 1195;

    public static final int BAS_ID = 6936;
    public static final int WALKING_ANIMATION = 2261;
    public static final int TAKE_OFF_ANIMATION = 2262;
    public static final int RUN_ANIMATION = 2263;
    public static final int TURN_RIGHT_ANIMATION = 2264;
    public static final int TURN_LEFT_ANIMATION = 2265;
    public static final int PRE_TAKE_OFF_ANIMATION = 2266;

    public RugMerchant(int id, Position position) {
        super(id, position);
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{RUG_MERCHANT}, action -> {
            var destinations = new RugDestination[]{RugDestination.UZER, RugDestination.BEDABIN_CAMP, RugDestination.NORTH_POLLNIVNEACH};
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                sendDialogue(action.getPlayer(), destinations);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                sendTravelDialogue(action.getPlayer(), destinations);
            }
            return true;
        });
        NPCActions.INSTANCE.onClick(new int[]{RUG_MERCHANT_18}, action -> {
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                sendDialogue(action.getPlayer(), RugDestination.SHANTAY_PASS);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                sendTravelDialogue(action.getPlayer(), RugDestination.SHANTAY_PASS);
            }
            return true;
        });
        NPCActions.INSTANCE.onClick(new int[]{RUG_MERCHANT_19}, action -> {
            var destinations = new RugDestination[]{RugDestination.SOPHANEM, RugDestination.MENAPHOS, RugDestination.NARDAH};
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                sendDialogue(action.getPlayer(), destinations);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                sendTravelDialogue(action.getPlayer(), destinations);
            }
            return true;
        });
        NPCActions.INSTANCE.onClick(new int[]{RUG_MERCHANT_20}, action -> {
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                sendDialogue(action.getPlayer(), RugDestination.SHANTAY_PASS);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                sendTravelDialogue(action.getPlayer(), RugDestination.SHANTAY_PASS);
            }
            return true;
        });
        NPCActions.INSTANCE.onClick(new int[]{RUG_MERCHANT_22}, action -> {
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                sendDialogue(action.getPlayer(), RugDestination.SOUTH_POLLNIVNEACH);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                sendTravelDialogue(action.getPlayer(), RugDestination.SOUTH_POLLNIVNEACH);
            }
            return true;
        });
    }

    public static void ride(Player player, RugDestination destination) {
        var rugObject = RugDestination.getClosest(player).getObject();
        if (rugObject == null) {
            return;
        }
        var rugPosition = rugObject.getPosition();
        player.setEntityInteraction(null);
        player.getPacketSender().sendJinglebitMusic(132, 50);
        player.getPacketSender().sendCameraSpin(rugPosition.getLocalX(), rugPosition.getLocalY(), 750, 3, 2);
        player.setPositionToFace(rugPosition);
        player.getMotion().enqueuePathToWithoutCollisionChecks(rugPosition.getX(), rugPosition.getY());
        TaskManager.submit(new Task() {
            @Override
            protected void execute() {
                if (Misc.getDistance(rugPosition, player.getPosition()) == 1) {
                    player.setPositionToFace(player.getPosition().transform(0, -1, 0));
                    TaskManager.submit(2, () -> {
                        player.getPacketSender().sendCameraNeutrality();
                        player.getTimerRepository().register(TimerKey.ATTACK_IMMUNITY, 1000);
                        player.BLOCK_ALL_BUT_TALKING = true;
                        player.performAnimation(new Animation(PRE_TAKE_OFF_ANIMATION));
                        player.playSound(new Sound(TAKE_OFF_SOUND));
                        player.setBas(new BasicAnimationSet(BAS_ID, BAS_ID, BAS_ID, BAS_ID, TURN_LEFT_ANIMATION, TURN_RIGHT_ANIMATION, BAS_ID));
                        player.updateAppearance();
                        player.setPositionToFace(destination.getPosition());
                        TaskManager.submit(3, () -> player.performAnimation(new Animation(TAKE_OFF_ANIMATION)));
                        TaskManager.submit(new Task(6) {
                            @Override
                            protected void execute() {
                                if (player.getPosition().equals(destination.getPosition())) {
                                    //On Arrival
                                    player.performAnimation(new Animation(RUN_ANIMATION));
                                    TaskManager.submit(4, () -> {
                                        player.playSound(new Sound(LANDING_SOUND));
                                        player.resetBas();
                                        player.updateAppearance();
                                        player.resetAnimation();
                                        player.getTimerRepository().register(TimerKey.ATTACK_IMMUNITY, 1);
                                        player.BLOCK_ALL_BUT_TALKING = false;
                                    });
                                    stop();
                                    return;
                                }
                                if (!player.getMotion().hasTask()) {
                                    player.getMotion().enqueuePathToWithoutCollisionChecks(destination.getPosition().getX(), destination.getPosition().getY());
                                }
                            }
                        }.bind(player));
                    });
                    stop();
                }
            }
        }.bind(player));

    }

    private static void sendTravelDialogue(Player player, RugDestination... destinations) {
        var options = new ArrayList<Pair<String, Consumer<Player>>>();
        for (var destination : destinations) {
            options.add(getOption(destination));
        }
        var cost = getCost(player);
        var dialogue = new DialogueBuilder(DialogueType.OPTION)
                .setOptionTitle("Ride cost is " + (cost <= 0 ? " Free" : Misc.formatWithAbbreviation2(cost)) + "!")
                .addOptions(options)
                .addCancel("Cancel");
        if (PlayerUtil.isMember(player)) {
            dialogue = new DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setText("You get a 50% discount for being a member.").setNext(dialogue);
        }
        dialogue.start(player);
    }

    private static void sendDialogue(Player player, RugDestination... destinations) {
        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                .setText("Hello.")
                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setText("Greetings, desert traveler. Do you require the services",
                                "of Ali Morrisane's flying carpet fleet?")
                        .setNext(constructAll(player, destinations))).start(player);
    }

    private static Pair<String, Consumer<Player>> getFirstOption(Player player, int option, RugDestination... destinations) {
        var text = option == 1 ? "Yes please." : "I want to travel by magic carpet.";
        return new Pair<>(text, $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText(text)
                        .setNext(new DialogueBuilder()
                                .setPostAction($$ -> sendTravelDialogue(player,destinations)))
                        .start(player));
    }

    private static Pair<String, Consumer<Player>> getSecondOption(Player player,RugDestination... destinations) {
        return new Pair<>("Tell me about Ali Morrisane.", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Tell me about Ali Morrisane.")
                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setText("What, you haven't heard of Ali M? Possibly the greatest",
                                        "salesman of the Kharidian empire if not all Gielinor.")
                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText(
                                                "Ah yes I remember him now, I went ona wild goose",
                                                "chase looking for his nephew.")
                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                .setText("Ha! No doubt old Ali M instigated the whole thing.")
                                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                        .setText("I had a bit of fun though, the whole job was quite", "diverting.")
                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setText("There's never a dull moment around that man, he's",
                                                                        "always looking for a way to make a quick coin or two.")
                                                                .setNext(constructAllBut(player, 2,destinations)
                                                                )))))).start(player));
    }

    private static Pair<String, Consumer<Player>> getThirdOption(Player player, RugDestination... destinations) {
        return new Pair<>("Tell me about this magic carpet fleet.", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Tell me about this magic carpet fleet.")
                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setText("the latest idea from the great Ali Morrisane. Desert",
                                        "travel will never be the same again.")
                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("So how does it work?")
                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                .setText("The carpet or the whole enterprise?")
                                                .setNext(new DialogueBuilder(DialogueType.OPTION)
                                                        .firstOption("Tell me about how the carpet works.", $$ ->
                                                                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                        .setText("Tell me about how the carpet works.")
                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                .setText("I'm not really too sure, it's just an enchanted rug",
                                                                                        "really, made out of special Ugthanki hair. It flies to",
                                                                                        "whatever destination it's owner commands.")
                                                                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                        .setText("Are they for sale then?")
                                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                                .setText("Do you think I'm mad? do you think that Ali",
                                                                                                        "Morrisane would throw his magic carpet monopoly", "away?")
                                                                                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                                        .setText("Well perhaps if I offered the right price?")
                                                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                                                .setText("Not a hope. Could you imagine the mess there'd be if",
                                                                                                                        "people were constantly zooming through Al Kharid and",
                                                                                                                        "Pollnivneach? It would be chaos. This way, we can keep",
                                                                                                                        "the carpet traffic outside towns and other busy places.")
                                                                                                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                                                        .setText("I suppose getting stuck in a carpet jam could get a bit", "tiresome.")
                                                                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                                                                .setText("Just think of the friction burns you would get if you",
                                                                                                                                        "were in a carpet crash.")
                                                                                                                                .setNext(constructAllBut(player, 3, destinations)
                                                                                                                                )))))))).start(player))
                                                        .secondOption("Tell me about the enterprise then.", $$$ ->
                                                                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                        .setText("Tell me about the enterprise then.")
                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                .setText("It's quite simple really, Ali Morrisane has hired myself",
                                                                                        "and a few other to set up carpet stations at some of",
                                                                                        "the desert's more populated places and run flights",
                                                                                        "between the stations.")
                                                                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                        .setText("So why has he limited the service to just the desert?")
                                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                                .setText("I don't think Ali is prepared to take on Gnome Air just",
                                                                                                        "Yet, their gliders are much faster than our carpets",
                                                                                                        "besides that I think we are in the short haul business,",
                                                                                                        "something that would only work in harsh conditions like")
                                                                                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                                        .setText("the desert.")
                                                                                                        .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                                                .setText("Why is that?")
                                                                                                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                                                        .setText("I suppose because people would just walk. Getting lost",
                                                                                                                                "isn't too much of a problem generally but it's a",
                                                                                                                                "different matter when you're in the middle of the",
                                                                                                                                "Kharidian desert with a dry waterskin and no idea")
                                                                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                                                                .setText("which direction to go in.")
                                                                                                                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                                                                        .setText("You're right I guess. How's the business going then?")
                                                                                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                                                                                .setText("Not too bad. the hubs are generally quite busy. But the",
                                                                                                                                                        "stations in Uzer and the Bedabin camp could do with a",
                                                                                                                                                        "bit more traffic.")
                                                                                                                                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                                                                                        .setText("a growth market I guess.")
                                                                                                                                                        .setNext(constructAllBut(player, 3, destinations)
                                                                                                                                                        ))))))))))).start(player))

                                                )))).start(player));
    }

    private static Pair<String, Consumer<Player>> getFourthOption(Player player, RugDestination... destinations) {
        return new Pair<>("I have some questions.", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setText("I have some questions.")
                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT).setText("I'll try to help you as much as I can.")
                                .setNext(new DialogueBuilder(DialogueType.OPTION)
                                        .firstOption("What are you doing here?", $$ ->
                                                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                        .setText("What are you doing here?")
                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT).setText(
                                                                "Well this is a good position for desert traffic. Shantay",
                                                                "seems to have a nice little money spinner setup, but I",
                                                                "reckon, this could turn out even better.")
                                                                .setNext(constructAll(player, 2, destinations))
                                                        ).start(player))
                                        .secondOption("Is that your pet money nearby?", $$ ->
                                                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                        .setText("Is that your pet money nearby?")
                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setText("He's his own monkey, he does whatever suits him, a", "total nuisance.")
                                                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                        .setText("I detect a degree of hostility being directed towards the", "monkey.")
                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                .setText("I shouldn't say this really, but sometimes I begin to",
                                                                                        "question some of ALi Morrisane's ideas, he says that",
                                                                                        "associating a money with any product will increase",
                                                                                        "sales. I just don't know, what will be next?")
                                                                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                        .setText("Frogs?")
                                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                                .setText("I doubt it, amphibians don't have the same curtest factor", "as monkeys")
                                                                                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                                        .setText("I'm confused. I thought you didn't like monkeys.")
                                                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                                                .setText("I don't dislike monkeys, it's just that monkeys. I don't",
                                                                                                                        "know, I might just be paranoid but I think he's... well...", "evil.")
                                                                                                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                                                        .setText("Hmmm... Interesting")
                                                                                                                        .setNext(constructAll(player, 2, destinations))
                                                                                                                )))))))).start(player))
                                        .thirdOption("Where did you get that hat?", $$$ ->
                                                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                        .setText("Where did you get that hat?")
                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setText(
                                                                        "My fez?, I got it from Ali Morrisane. it's a uniform of",
                                                                        "sorts apparently it makes us more visible, but I'm not",
                                                                        "too sure about it.")
                                                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                        .setText("Well it is quite distinctive.")
                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                .setText(
                                                                                        "Do you like it? I haven't really made my mind up",
                                                                                        "about it yet. You see it's not all that practical for desert",
                                                                                        "conditions.")
                                                                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                        .setText("How so?")
                                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                                .setText(
                                                                                                        "Well it doesn't keep the sun out of eyes after",
                                                                                                        "a while sitting out in the desert they really being to", "burn.")
                                                                                                .setNext(constructAll(player, 2, destinations))
                                                                                        ))))).start(player)
                                        ))).start(player));
    }

    private static Pair<String, Consumer<Player>> getFifthOption(Player player, int type) {
        return new Pair<>(type == 1 ? "No thanks." : "Thanks, I'm done here.", $ ->
                new DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setText("Come back anytime.").start(player));
    }

    private static DialogueBuilder constructAllBut(Player player, int exclusion, RugDestination... destinations) {
        var options = new ArrayList<Pair<String, Consumer<Player>>>();
        options.add(getFirstOption(player, 2));
        options.add(getSecondOption(player));
        options.add(getThirdOption(player,destinations));
        options.add(getFourthOption(player, destinations));
        options.add(getFifthOption(player, 2));
        options.remove(exclusion - 1);
        return new DialogueBuilder(DialogueType.OPTION).addOptions(options);
    }

    private static DialogueBuilder constructAll(Player player, RugDestination... destinations) {
        return constructAll(player, 1, destinations);
    }

    private static DialogueBuilder constructAll(Player player, int type, RugDestination... destinations) {
        var fifth = getFifthOption(player, type);
        return new DialogueBuilder(DialogueType.OPTION).addOptions(Arrays.asList(
                getFirstOption(player, type,destinations), getSecondOption(player,destinations),
                getThirdOption(player,destinations), getFourthOption(player, destinations))).
                lastOption(fifth.getFirst(), fifth.getSecond());
    }

    private static Pair<String, Consumer<Player>> getOption(RugDestination destination) {
        return new Pair<>(destination.getName() + ".", player -> {
            if (player.getInventory().getAmount(ItemID.COINS) < getCost(player)) {
                new DialogueBuilder(DialogueType.NPC_STATEMENT).setText("You don't have enough coins to travel.")
                        .start(player);
                return;
            }
            player.getInventory().delete(ItemID.COINS, getCost(player));
            ride(player, destination);
            DialogueManager.start(player, -1);
        });
    }

    public static int getCost(Player player) {
        var cost = COST;
        if (PlayerUtil.isMember(player)) {
            cost *= 0.50;
        }
        if (player.getEquipment().contains(ItemID.RING_OF_CHAROS_A_)) {
            cost *= 0.50;
        }

        return cost;

    }
}