package com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl;

import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmConfiguration;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.OlmAttack;
import com.grinder.game.content.minigame.warriorsguild.drops.Misc;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Skill;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class SphereOlmAttack implements OlmAttack {

    private enum Sphere {

        MAGIC(OlmConfiguration.PURPLE_ORB, "@blu@", "magical power", AttackType.MAGIC),
        RANGED(OlmConfiguration.GREEN_ORB, "@gre@", "accuracy and dexterity", AttackType.RANGED),

        MELEE(OlmConfiguration.RED_ORB, "@red@", "aggression", AttackType.MELEE),

        ;

        private int projectile;
        private String colour;
        private String power;
        private AttackType type;

        Sphere(int projectile, String colour, String power, AttackType type) {
            this.projectile = projectile;
            this.colour = colour;
            this.power = power;
            this.type = type;
        }

        private static final Sphere[] VALUES = values();

        private static Sphere getRandom() {
            return VALUES[Misc.random(VALUES.length - 1)];
        }
    }

    @Override
    public void execute(NPC npc, Player target) {
        Sphere sphere = Sphere.getRandom();

        target.getPacketSender().sendMessage(sphere.colour+"The Great Olm fires a sphere of "+sphere.power+"!");
        target.getPacketSender().sendMessage(sphere.colour+"Your prayers have been sapped!");

        new Projectile(npc.getPosition(), target.getPosition(), 0, sphere.projectile, 55, 50, 43, 21, 0, 3, 0).sendProjectile();

        PrayerHandler.deactivatePrayers(target);

        TaskManager.submit(new Task(4) {
            @Override
            protected void execute() {
                if(PrayerHandler.hasProtectionPrayer(target, sphere.type)) {
                    int halfPrayer = target.getSkillManager().getCurrentLevel(Skill.PRAYER) / 2;
                    target.getSkillManager().setCurrentLevel(Skill.PRAYER, halfPrayer, true);
                } else {
                    int halfHP = target.getHitpoints() / 2;
                    target.getCombat().queue(Damage.create(halfHP));
                }
                stop();
            }
        });
    }
}
