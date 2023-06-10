package com.grinder.game.content.minigame.warriorsguild.npcs;

import com.grinder.game.content.minigame.warriorsguild.WarriorGuildNpc;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.util.ItemID;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.function.Consumer;

import static com.grinder.util.NpcID.SLOANE;

/**
 * @author L E G E N D
 */
public final class Sloane extends WarriorGuildNpc {

    public Sloane(int id, Position position) {
        super(id, position);
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{SLOANE}, action -> {
            var player = action.getPlayer();
            var strLevel = player.getSkills().getLevel(Skill.STRENGTH);
            DialogueBuilder optionsDialogue = new DialogueBuilder(DialogueType.OPTION);
            var claimTokensDialogue = makePlayerDialogue(player.getWarriorsGuild().getTokens() > 0 ?
                            makeNpcDialogue(makePlayerDialogue(null,
                                    "Thanks!"),
                                    "Of course! Here you go, you've earned " + player.getWarriorsGuild().getTokens() + " tokens!").setAction($ -> {
                                ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.WARRIOR_GUILD_TOKEN, player.getWarriorsGuild().getTokens()));
                                player.getWarriorsGuild().setTokens(0);
                            }) : makeNpcDialogue(makePlayerDialogue(null,
                    "Okay, I'll go see what I can find"),
                    "I'm afraid you have not earned any tokens yet. Try",
                    "some of the activities around the guild to earn some."),
                    "May I claim my tokens please?");
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                var options = new ArrayList<Pair<String, Consumer<Player>>>();
                options.add(new Pair<>("Can you tell me about skillcapes, please?", $ -> makeNpcDialogue(optionsDialogue, "Unfortunately, I no longer sell the Cape of Strength.").start(player)));
                options.add(new Pair<>("What can I do here?", $ -> makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(
                        makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(optionsDialogue,
                                "Ahh that would be telling..."),
                                "What was your best?"),
                                "Basically you'll need to go in there, make sure you've",
                                "got nothing cluttering up your hands, pick up a shot",
                                "and throw it.... depending upon your technique, you",
                                "can get quite long throws."),
                                "Ok, what else?"),
                        "I personally prefer ground ashes, it makes a nice fine",
                        "powder and gives some really good grip."),
                        "I'll remember that. What should I use?"),
                        "Ahh, the shot put is a great test of strength and can be",
                        "quite rewarding. Mind you do it properly though, you",
                        "might want to dust your hands with some powdery",
                        "substance first. It'll give better grip."),
                        "What can I do here?").
                        start(player)));
                options.add(new Pair<>("That's a big axe!", $ -> makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(
                        makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(makeNpcDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(optionsDialogue,
                                "Aye indeed. Brawn isn't all you need to wield a big axe,",
                                "brains are required too!"),
                                "I sense a moral in there somewhere."),
                                "I simply went and chopped the tree down with this big",
                                "old axe, still holding it. The squirrel was so petrified it",
                                "simply jumped straight out of the tree onto me, I won",
                                "the axe and let the poor creature go!"),
                                "was my turn...."),
                                "He went running after the squirrel... nearly caught it",
                                "too... it shot up a tree and he tried to climb up it, only",
                                "got a bit tangled up with his axe cause he couldn't hang",
                                "on to it at the same time... he fell out of the tree and it"),
                                "What happened?"),
                                "I bet him he couldn't catch a squirrel whilst still holding",
                                " his axe but that I could... and that if I won I'd get his","axe."),
                                "Oh?"),
                        "Maybe, maybe not, but I still had to beat a Barbarian",
                        "to get it. Mind you, usually they don't part with them.",
                        "This was an unusual circumstance."),
                        "But you don't look that strong!"),
                        "Yes indeed it is. Have to be mighty strong to wield it", "too."),
                        "That's a big axe!").start(player)));
                options.add(new Pair<>("May I claim my tokens please?", $ -> claimTokensDialogue.start(player)));
                options.add(new Pair<>("Bye!", $ -> makePlayerDialogue(makeNpcDialogue(null, "Be well, warrior " + player.getUsername() + "."), "Bye!").start(player)));
                optionsDialogue.addOptions(options);
                DialogueBuilder initialDialogue = makeNpcDialogue(strLevel >= 99 ? makeNpcDialogue(makeNpcDialogue(optionsDialogue,
                        "As an additional recognition of your cape achievement, the",
                        "Cape of Strength can teleport you to our guild." ),
                        "Ah, but I can see you are already as strong as is possible!",
                        "art of attacking, perhaps you have come to me to",
                        "purchase a Cape of Strength, and thus join the elite few",
                        "who have mastered this exacting skill?") : optionsDialogue,
                        "Ahh, hello there, " + player.getUsername() + ".");
                initialDialogue.start(player);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                claimTokensDialogue.start(player);
            }
            return true;
        });
    }
}
