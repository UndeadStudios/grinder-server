package com.grinder.game.content.minigame.motherlodemine.npcs;

import com.grinder.game.content.minigame.motherlodemine.MotherlodeMine;
import com.grinder.game.content.minigame.motherlodemine.sack.SackType;
import com.grinder.game.content.miscellaneous.Emotes;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.ShopIdentifiers;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

import static com.grinder.util.NpcID.PROSPECTOR_PERCY;

/**
 * @author L E G E N D
 * @date 2/14/2021
 * @time 3:18 AM
 * @discord L E G E N D#4380
 */
public final class ProspectorPercy extends NPC {

    private static final String[] ONE_WHEEL_BROKEN_CHATS = new String[]{
            "Git yer hammer an' fix that wheel!",
            "Ye'd better fix that wheel",
            "We got us a jammed wheel!"
    };
    private static final String[] BOTH_WHEELS_BROKEN_CHATS = new String[]{
            "Git yer hammer an' fix them wheels!",
            "Ye'd better fix them wheels",
            "Both them wheels be jammed",
            "That water ain't flowing!"
    };

    private int previousOverheadChat;
    private int timer;

    public ProspectorPercy(int id, Position position) {
        super(id, position);
        previousOverheadChat = -1;
        setArea(AreaManager.MOTHERLODE_MINE_AREA);
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{PROSPECTOR_PERCY}, action -> {
            var player = action.getPlayer();
            var type = action.getType();
            if (type == NPCActions.ClickAction.Type.FIRST_OPTION) {
                new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(PROSPECTOR_PERCY).
                        setText("Git back ter work, ye young vermint! There's treasure",
                                "in them walls and it's not gonna mine itself while ye",
                                "stand here yappin'.")
                        .setNext(constructAll(player))
                        .start(player);
            } else if (type == NPCActions.ClickAction.Type.SECOND_OPTION) {
                openShop(player);
            }
            return true;
        });
    }

    public void process() {
        if (++timer % 5 == 0) {
            String[] chats = null;
            if (!MotherlodeMine.isActive()) {
                chats = BOTH_WHEELS_BROKEN_CHATS;
            } else if (MotherlodeMine.getFirstWheel().isBroken() || MotherlodeMine.getSecondWheel().isBroken()) {
                chats = ONE_WHEEL_BROKEN_CHATS;
            }
            if (chats == null) {
                return;
            }
            performAnimation(Emotes.EmoteData.ANGRY.animation);
            int index;
            do {
                index = Misc.getRandomExclusive(chats.length);
            } while (index == previousOverheadChat);
            say(chats[index]);
            previousOverheadChat = index;
        }
    }

    private static Pair<String, Consumer<Player>> getFirstOption(Player player) {
        return new Pair<>("How do I mine here?", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("How do I mine here?")
                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setText("Git ahold of yer pickaxe, find a vien of ore, and set to",
                                        "work. If  ye got a bit of skill, ye'll have a pocket o' pay-",
                                        "dirt in no time.")
                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                        .setText("I've built me a contraption to wash the pay-dirt. Just",
                                                "drop yer pay-dirt in the hopper an' wait fer it at the", "other end.")
                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                .setText("I won't charge ye fer usin' my contraption, but ye'd",
                                                        "better fix it yerself when it breaks. A good whack with a",
                                                        "hammer usually settles it.")

                                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                        .setText("Now will ye be gettin' to work now, or are you gonna",
                                                                "keep yappin' like a doggone galoot?")
                                                        .setNext(constructDialogueAndExclude(player, 1))
                                                )))).start(player));
    }

    private static Pair<String, Consumer<Player>> getSecondOption(Player player) {
        return new Pair<>("Would you like to trade?", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Would you like to trade?")
                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setText("If ye've found yourself some golden nuggets in this 'ere",
                                        "mine, I'll do you a swap, yeah.")
                                .setNext(null)
                                .setAction($$ -> openShop(player)
                                )).start(player));
    }

    private static Pair<String, Consumer<Player>> getThirdOption(Player player) {
        return new Pair<>("Tell me about yourself.", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Tell me about yourself.")
                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setText("Why, I'm percy. Prospector Percy, the roughest,"
                                        , "toughest, gruffest miner in the land. I've been pannin'",
                                        "fer gold since I were a young'un, and here's where",
                                        "I've struck it lucky.")
                                .setNext(new DialogueBuilder(DialogueType.OPTION)
                                        .firstOption("Excuse me, but what language are you speaking?", $$ ->
                                                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                        .setText("Excuse me, but what language are you speaking?")
                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setText("Don't ye give me any of yer lip, ye dern varmint!",
                                                                        "Young'uns these days got no respect.")
                                                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                        .setText("Do go on.")
                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                .setText("This here's the richest seam of ore I've found in all my",
                                                                                        "days. After I built a contraption for washing the pay-",
                                                                                        "dirt, the dwarves let me run things down here.")
                                                                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                        .setText("Now, have ye any more idjit questions, or are ye ready",
                                                                                                "to do some real work?")
                                                                                        .setNext(constructDialogueAndExclude(player, 3))

                                                                                )))).start(player))
                                        .secondOption("You discovered this mine?", $$$ ->
                                                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                        .setText("You discovered this mine?")
                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setText("This here's the richest seam of ore I've found in all my",
                                                                        "days. After I built a contraption for washing the pay-",
                                                                        "dirt, the dwarves let me run things down here.")
                                                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                        .setText("Now, have ye any more idjit questions, or are ye ready",
                                                                                "to do some real work?")
                                                                        .setNext(constructDialogueAndExclude(player, 3))
                                                                )).start(player))
                                )).start(player));
    }

    private static Pair<String, Consumer<Player>> getFourthOption(Player player) {
        return new Pair<>("Is there anything else I can unlock here?", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Is there anything else I can unlock here?")
                        .setNext(null)
                        .setPostAction($$ -> {
                            if (player.getMotherlodeMine().getSack().getSackType() != SackType.NORMAL && player.getMotherlodeMine().isRestrictedAreaUnlocked()) {
                                new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                        .setText("Well, now I think ye've got everything already. Ye can",
                                                "already go climb the ladder to the restricted mine, and",
                                                "ye've got a bigger sack capacity too.")
                                        .setNext(constructDialogueAndExclude(player, 4))
                                        .start(player);
                            } else {
                                var upgradeSackOption = new Pair<String, Consumer<Player>>("Bigger sack: 200 nuggets", $$$ ->
                                        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("I'd like to get the bigger sack, please.")
                                                .setNext(null)
                                                .setPostAction($$$$ -> {
                                                    if (player.getInventory().contains(new Item(ItemID.GOLDEN_NUGGET, 200))) {
                                                        new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setNpcChatHead(PROSPECTOR_PERCY)
                                                                .setNext(null)
                                                                .setPostAction($$$$$ -> {
                                                                    if (PlayerUtil.isMember(player)) {
                                                                        player.getMotherlodeMine().getSack().setSackType(SackType.MEMBER);
                                                                        new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                .setNpcChatHead(PROSPECTOR_PERCY)
                                                                                .setText("Your sack can hold " + SackType.MEMBER.getSize() + " Ores instead of",
                                                                                        SackType.UPGRADED.getSize() + " for being a member.").start(player);
                                                                    } else {
                                                                        player.getMotherlodeMine().getSack().setSackType(SackType.UPGRADED);
                                                                    }

                                                                    player.getInventory().delete(ItemID.GOLDEN_NUGGET, 200, true);
                                                                })
                                                                .setText("Bigger sack unlocked you better get going.").start(player);
                                                    } else {
                                                        new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setNpcChatHead(PROSPECTOR_PERCY)
                                                                .setText("That'll be 200 nuggets. If ye ain't got enough, ye'd",
                                                                        "better do some more mining ' that's how ye get stuff",
                                                                        "round here!").start(player);
                                                    }
                                                }).start(player));

                                var restrictedAreaOption = new Pair<String, Consumer<Player>>("Restricted mine access: 100 nuggets", $$$ ->
                                        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("I'd like to have access to the restricted mine, please.")
                                                .setNext(null)
                                                .setPostAction($$$$ -> {
                                                    if (player.getInventory().contains(new Item(ItemID.GOLDEN_NUGGET, 100))) {
                                                        new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setNpcChatHead(PROSPECTOR_PERCY)
                                                                .setNext(null)
                                                                .setPostAction($$$$$ -> {
                                                                    player.getMotherlodeMine().unlockRestrictedArea();
                                                                    player.getInventory().delete(ItemID.GOLDEN_NUGGET, 100, true);
                                                                })
                                                                .setText("You have unlocked the restricted mine area", "you better get to work.").start(player);
                                                    } else {
                                                        new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setNpcChatHead(PROSPECTOR_PERCY)
                                                                .setText("That'll be 100 nuggets. If ye ain't got enough, ye'd",
                                                                        "better do some more mining ' that's how ye get stuff",
                                                                        "round here!").start(player);
                                                    }
                                                }).start(player));


                                var optionsDialogue = new DialogueBuilder(DialogueType.OPTION);

                                if (player.getMotherlodeMine().getSack().getSackType() == SackType.NORMAL) {
                                    optionsDialogue.firstOption(upgradeSackOption.getFirst(), upgradeSackOption.getSecond());
                                    if (!player.getMotherlodeMine().isRestrictedAreaUnlocked()) {
                                        optionsDialogue.secondOption(restrictedAreaOption.getFirst(), restrictedAreaOption.getSecond());
                                    }
                                } else {
                                    optionsDialogue.firstOption(restrictedAreaOption.getFirst(), restrictedAreaOption.getSecond());
                                }
                                optionsDialogue.lastOption("Cancel", $$$ -> DialogueManager.start(player, -1));
                                var mainDialogue = new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                        .setNpcChatHead(PROSPECTOR_PERCY)
                                        .setText("Well now, if ye've got level 72 Mining. ye could pay to",
                                                "use my restricted mine, up the ladder. 100 nuggets",
                                                "gives unlimited access")
                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                .setText("Or maybe ye'd like it if the sack were bigger? I'll let ye",
                                                        "have more ore in t here if ye pays me 200 nuggets.")
                                                .setNext(optionsDialogue));


                                mainDialogue.start(player);
                            }
                        }).start(player));
    }

    private static Pair<String, Consumer<Player>> getFifthOption(Player player) {
        return new Pair<>("I'll leave you alone.", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("I'll leave you alone.")
                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT).setText("Dern straight ye will."))
                        .start(player));
    }

    private static DialogueBuilder constructDialogueAndExclude(Player player, int option) {
        var options = new ArrayList<Pair<String, Consumer<Player>>>();
        options.add(getFirstOption(player));
        options.add(getSecondOption(player));
        options.add(getThirdOption(player));
        options.add(getFourthOption(player));
        options.add(getFifthOption(player));
        options.remove(option - 1);
        return new DialogueBuilder(DialogueType.OPTION).addOptions(options);
    }

    private static DialogueBuilder constructAll(Player player) {
        return new DialogueBuilder(DialogueType.OPTION).addOptions(Arrays.asList(getFirstOption(player), getSecondOption(player),
                getThirdOption(player), getFourthOption(player), getFifthOption(player)));
    }

    private static void openShop(Player player) {
        ShopManager.open(player, ShopIdentifiers.PROSPECTOR_PERCYS_NUGGET_SHOP);
    }
}
