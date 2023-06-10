package com.grinder.game.content.minigame.warriorsguild.npcs;

import com.grinder.game.content.minigame.warriorsguild.WarriorGuildNpc;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.function.Consumer;

import static com.grinder.util.NpcID.JIMMY;

/**
 * @author L E G E N D
 */
public final class Jimmy extends WarriorGuildNpc {

    public Jimmy(int id, Position position) {
        super(id, position);
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{JIMMY}, action -> {
            var player = action.getPlayer();
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                var mainDialogue = new DialogueBuilder(DialogueType.OPTION);
                var options = new ArrayList<Pair<String, Consumer<Player>>>();

                options.add(new Pair<>("Tell me about this room.", $ -> makePlayerDialogue(makeNpcDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(
                        makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(mainDialogue,
                                "Keg balancin. I'm the besht."),
                                "Practish? I mean.. practise what?"),
                                "Yep... and to practish."),
                                "But you just nipped in to have a quick drink?"),
                        "That'sh what I shaid! *HIC* A shtore room.... Now",
                        "technic'ly shpeaking, I should be outshide guarding it..."),
                        "A store room you mean?"),
                        "Thish here'sh a shtore room right?"),
                        "Well... s'like thish..."),
                        "Tell me about this room.").start(player)));
                options.add(new Pair<>("Tell me how to balance kegs.", $ -> makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(
                        makeNpcDialogue(makePlayerDialogue(mainDialogue,
                                "That sounds like a challenge, I'll show you!"),
                                "But you couldn't ever balansh ash many ash meee!"),
                        "Yesh. Eashy."),
                        "Eashy?"),
                        "Yer very very shtrange. But.... you pick the keg up,",
                        "and balance it on yer head, then you pick another keg",
                        "up and put that on top. S'really very eashy."),
                        "TTell me how to balance kegs.").start(player)));
                options.add(new Pair<>("May I claim my tokens please?", $ -> makePlayerDialogue(player.getWarriorsGuild().getTokens() > 0 ?
                                makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(makeMessageDialogue(null,
                                        "The rather drunk Jimmy scribbles the tokens you've earned from",
                                        "Keg Balancing in the Ledger so that you can claim them from an",
                                        "official member of training staff."),
                                        "Nah... hic.... I'm a wizsh at copyin' signaturesh! Jus'",
                                        "ashk an offishal mem'er of shtaff like Shloane fer yer",
                                        "tokensh."),
                                        "Won't they know?"),
                                        "Well... err.. ish not offishal or anyfin... but I got the",
                                        "ledger of tokensh 'ere. I'll jus' err.. write it in!") : makeNpcDialogue(null,
                        "Wha'? You ain't earnt any mate!"),
                        "May I claim my tokens please?").start(player)));
                options.add(new Pair<>("Bye!", $ -> makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(null,
                        "No thanks, got things to do, people to see, tokens to", "earn..."),
                        "Shure you wouldn't like an ickle drinkie fore yer go?"),
                        "Bye!").start(player)));
                mainDialogue.addOptions(options);
                makeNpcDialogue(mainDialogue, "'Ello there.").start(player);
            }
            return true;
        });
    }
}