package com.grinder.game.content.minigame.warriorsguild.npcs;

import com.grinder.game.content.minigame.warriorsguild.WarriorGuildNpc;
import com.grinder.game.content.minigame.warriorsguild.WarriorsGuild;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.util.ShopIdentifiers;

import static com.grinder.util.NpcID.GHOMMAL;

/**
 * @author L E G E N D
 */
public final class Ghommal extends WarriorGuildNpc {

    public Ghommal(int id, Position position) {
        super(id, position);
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{GHOMMAL}, action -> {
            var player = action.getPlayer();
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                if (WarriorsGuild.hasRequirements(player)) {
                    makeNpcDialogue(makePlayerDialogue(null, "Umm.. thank you, I think."), "Ghommal welcome you to Warrior Guild!").start(player);
                } else {
                    makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(null,
                            "Ghommal stick to Warrior's Code of Honour. When", "you a bigger, stronger warrior, you come back."),
                            "Why not?"),
                            "No! You is not a strong warrior. you not enter till you", "bigger. Ghommal does not take bribes."),
                            "Go on, let me in, you know you want to. I could...", "make it worth your while..."),
                            "Heehee... he say he warrior... I not heard that one", "for... at least' 5 minutes!"),
                            "What? But I'm a warrior!"),
                            "You not pass. you too weedy.").start(player);
                }
            }
            if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                ShopManager.open(player, ShopIdentifiers.WARRIORS_GUILD_STORE);
            }
            return true;
        });
    }
}
