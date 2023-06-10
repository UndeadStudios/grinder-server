package com.grinder.game.content.minigame.chamberoxeric.room;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class COXPassage {

    private static final Position SKILLING_TO_MUTADILES = new Position(3267, 5453);

    private static final Position MYSTICS_TO_VANGAURD = new Position(3343, 5250);

    public static void pass(Player p) {
        if (Boundary.inside(p, SKILLING_TO_MUTADILES, 3)) {
            move(p, COXMap.MUTADILES.position);
        } else if (Boundary.inside(p, MYSTICS_TO_VANGAURD, 3)) {
            new DialogueBuilder(DialogueType.OPTION)
                    .firstOption("Vanguard", $ -> COXPassage.move(p, COXMap.VANGUARD.position))
                    .secondOption("Skilling area", $ -> COXPassage.move(p, COXMap.SKILLING_AREA.position))
                    .start(p);
        }
    }

    public static void move(Player p, Position destination) {
        Position pos = destination.clone();

        int ownerIndex = p.getCOX().getParty().clanChat.getOwner().getIndex();

        int height = (ownerIndex * 4) + destination.getZ();

        pos.transform(0, 0, height);

        p.getPacketSender().sendMinimapFlagRemoval();
        p.getMotion().cancelTask();

        p.getPacketSender().sendFadeScreen("", 2, 5);

        p.delayedMoveTo(pos, 3);
    }
}
