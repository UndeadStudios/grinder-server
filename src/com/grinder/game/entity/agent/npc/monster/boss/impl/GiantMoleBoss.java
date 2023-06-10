package com.grinder.game.entity.agent.npc.monster.boss.impl;

import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider;
import com.grinder.game.entity.agent.combat.event.impl.ExtinguishLightEvent;
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitApplied;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;
import com.grinder.game.entity.agent.npc.monster.boss.Boss;
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.Priority;
import com.grinder.util.oldgrinder.Area;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import static com.grinder.game.entity.agent.combat.attack.AttackType.MELEE;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-10
 */
public class GiantMoleBoss extends Boss implements AttackProvider {

    private boolean underground = false;

    public GiantMoleBoss(int npcId, final Position position) {
        super(npcId, position);
        getCombat().subscribe(event -> {
            if (event instanceof IncomingHitApplied){
                if(!underground) {
                    if (getHitpoints() <= fetchDefinition().getHitpoints() / 2) {
                        if (Misc.randomChance(25.0F))
                            burrow();
                    }
                }
            }
            return false;
        });
    }

    private void burrow(){
        final Position destination = Misc.random(COORDINATES);
        getCombat().targetStream(10).forEach(player ->
        {
            if(player.getMotion().isFollowing(this)){
                player.getMotion().resetTargetFollowing();
            }
            Area.of(1, 1, 1, 1)
                    .getAbsolute(getCenterPosition())
                    .findPositions(player.getPosition().getZ())
                    .forEach(position -> player.getPacketSender().sendGraphic(new Graphic(855), position));
        });
        getCombat().reset(true);
        say("Meek meek!");
        performAnimation(DIGGING);

        underground = true;

        TaskManager.submit(new Task(3) {
            @Override
            protected void execute() {
                stop();
                performAnimation(DIGGING_OUT);
                moveTo(destination);
                underground = false;

                if(Misc.randomChance(50.0F)){

                    getCombat().targetStream(20).forEach(player -> {

                        player.getCombat().submit(new ExtinguishLightEvent());

                        //TODO: find the dirt graphic
                        player.sendMessage("As the Giant Mole digs away, it throws some dirt on your light source and it extinguishes!");
                    });
                }
            }
        });
    }

    @NotNull
    @Override
    public BossAttack generateAttack() {
        return new BossAttack(this);
    }

    @Override
    public boolean skipNextCombatSequence() {
        return underground;
    }

    @Override
    public boolean skipNextRetreatSequence() {
        return underground;
    }

    @NotNull
    @Override
    protected AttackTypeProvider attackTypes() {
        return MELEE;
    }

    @Override
    public int maxTargetsHitPerAttack(@NotNull AttackType type) {
        return 1;
    }

    @Override
    public int attackRange(@NotNull AttackType type) {
        return 4;
    }

    @Override
    public int fetchAttackDuration(AttackType type) {
        return 4;
    }

    @Override
    public Animation getAttackAnimation(AttackType type) {
        return new Animation(getAttackAnim());
    }

    @Override
    public Stream<HitTemplate> fetchHits(AttackType type) {
        return HitTemplate.builder(MELEE).setDelay(0).buildAsStream();
    }

    private static final Animation DIGGING = new Animation(3314, Priority.HIGH);
    private static final Animation DIGGING_OUT = new Animation(3315);

    private static final Position[] COORDINATES = {
            new Position(1760, 5164), new Position(1781, 5151),
            new Position(1753, 5150), new Position(1738, 5155),
            new Position(1742, 5171), new Position(1741, 5187),
            new Position(1738, 5209), new Position(1779, 5182),
            new Position(1754, 5206), new Position(1738, 5225),
            new Position(1770, 5228), new Position(1778, 5236),
            new Position(1779, 5208), new Position(1771, 5200),
            new Position(1779, 5188), new Position(1774, 5174),
            new Position(1763, 5184), new Position(1757, 5185),
            new Position(1760, 5192), new Position(1751, 5174),
    };
}
