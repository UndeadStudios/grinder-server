package com.grinder.game.content.minigame.fightcave.monsters;

import com.grinder.game.content.minigame.fightcave.FightCaveNpc;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder;
import com.grinder.game.model.Position;

import java.util.stream.Stream;

import static com.grinder.game.entity.agent.combat.attack.AttackType.MELEE;

/**
 * @author L E G E N D
 */
public class YtMejKot extends FightCaveNpc implements AttackProvider {
    private int attacks = 0;


    public YtMejKot(int id, Position position) {
        super(id, position);
    }

    @Override
    public Stream<HitTemplate> fetchHits(AttackType type) {
        final HitTemplateBuilder builder = HitTemplate.builder(type).setDelay(0);
        if (type == MELEE)
            if (attacks < 2)
                attacks++;
            else {
                if (getHitpoints() < 40) {
                    startHealing();
                    healNearby();
                } else {
                    attacks = 0;
                }
            }


        return builder.buildAsStream();
    }

    public void startHealing() {
        heal();
        attacks = 0;
    }

    public void healNearby() {
        var position = getPosition();

        for (var npc : getOwner().getLocalNpcs()) {
            if (npc instanceof FightCaveNpc)
                if (position.isWithinDistance(npc.getPosition(), 5) && !npc.isDying() && npc.getHitpoints() > 0) {
                    if (npc.getHitpoints() < npc.getMaxHitpoints())
                        ((FightCaveNpc) npc).heal();
                }
        }
    }

}
