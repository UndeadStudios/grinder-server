package com.grinder.game.content.minigame.fightcave.monsters;

import com.grinder.game.content.minigame.fightcave.FightCaveNpc;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;

import java.util.stream.Stream;

/**
 * @author L E G E N D
 */
public final class TzKih extends FightCaveNpc implements AttackProvider {

    public TzKih(int npcId, Position position) {
        super(npcId, position);
    }

    @Override
    public Stream<HitTemplate> fetchHits(AttackType type) {
        int prayerLevel = getOwner().getSkillManager().getCurrentLevel(Skill.PRAYER);
        if (prayerLevel > 0)
            getOwner().getSkillManager().setCurrentLevel(Skill.PRAYER, prayerLevel - 1, true);
        return HitTemplate.builder(type).setDelay(0).buildAsStream();
    }
}
