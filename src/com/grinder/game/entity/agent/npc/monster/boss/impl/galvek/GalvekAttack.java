package com.grinder.game.entity.agent.npc.monster.boss.impl.galvek;

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackProvider;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.event.impl.StunEvent;
import com.grinder.game.entity.agent.combat.hit.HitTemplate;

import static com.grinder.game.entity.agent.combat.attack.AttackType.*;
import static com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.GalvekConstants.*;

import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.task.FireArcTileTask;
import com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.task.GalvekFireBalls;
import com.grinder.game.entity.agent.npc.monster.boss.impl.galvek.task.GalvekTsunamiWall;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.model.*;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handles all the attacks animations, projectiles, and special attack sequences,
 * and groups them based on the phases. In hindsight an enum was a bad design choice,
 * and may be slightly redundant, but it works.
 *
 * @author Pea2nuts
 *
 */

public enum GalvekAttack implements AttackProvider {
    MELEE_ATTACK_SITTING(AttackType.MELEE, GalvekPhase.FIRE, GalvekPhase.EARTH) {

        @Override
        public Animation getAttackAnimation(AttackType type) {
            return swipeAttackSitting;
        }
    },
    MELEE_ATTACK_FLOATING(AttackType.MELEE, GalvekPhase.AIR, GalvekPhase.WATER) {

        @Override
        public Animation getAttackAnimation(AttackType type) {
            return biteAttackFloating;
        }
    },

    RANGE_RED_ATTACK_SITTING(AttackType.RANGED, GalvekPhase.FIRE, GalvekPhase.EARTH) {

        @NotNull
        @Override
        public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
            return Stream.of(AIR_RANGE_RED);
        }

        @Override
        public Animation getAttackAnimation(AttackType type) {
            return normalRangedAttackSitting;
        }
    },
    RANGE_RED_ATTACK_FLOATING(AttackType.RANGED, GalvekPhase.AIR, GalvekPhase.WATER) {

        @NotNull
        @Override
        public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
            return Stream.of(AIR_RANGE_RED);
        }

        @Override
        public Animation getAttackAnimation(AttackType type) {
            return normalRangedAttackFloating;
        }
    },

    MAGIC_AIR_ATTACK_SITTING(AttackType.MAGIC, GalvekPhase.FIRE, GalvekPhase.EARTH) {

        @NotNull
        @Override
        public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
            return Stream.of(FIRE_MAGIC_AIRBLAST);
        }

        @Override
        public Animation getAttackAnimation(AttackType type) {
            return normalRangedAttackSitting;
        }
    },
    MAGIC_AIR_ATTACK_FLOATING(AttackType.MAGIC, GalvekPhase.AIR, GalvekPhase.WATER) {

        @NotNull
        @Override
        public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
            return Stream.of(FIRE_MAGIC_AIRBLAST);
        }

        @Override
        public Animation getAttackAnimation(AttackType type) {
            return normalRangedAttackFloating;
        }
    },

    DRAGONFIRE_ATTACK_SITTING(AttackType.SPECIAL, GalvekPhase.FIRE, GalvekPhase.EARTH) {

        @Override
        public Animation getAttackAnimation(AttackType type) {
            return breatheFireSitting; //normalRangedAttackSitting;
        }

        @Override
        public void sequence(GalvekBoss hydraBossNPC, Agent target) {
            sendDragonFire(hydraBossNPC, target);
        }

        //TODO: if need to change the attack durations
        //@Override
        //public int fetchAttackDuration(AttackType type) {
        //    return 6;
        //}
    },
    DRAGONFIRE_ATTACK_FLOATING(AttackType.SPECIAL, GalvekPhase.AIR, GalvekPhase.WATER) {

        @Override
        public Animation getAttackAnimation(AttackType type) {
            return normalRangedAttackFloating;
        }

        @Override
        public void sequence(GalvekBoss hydraBossNPC, Agent target) {
            sendDragonFire(hydraBossNPC, target);
        }
    },
    AIR_GUST(AttackType.SPECIAL, GalvekPhase.AIR){

        @NotNull
        @Override
        public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
            return Stream.of(AIR_GUST_DRAIN);
        }
        @Override
        public Animation getAttackAnimation(AttackType type) {
            return normalRangedAttackFloating;
        }

        @Override
        public void sequence(GalvekBoss hydraBossNPC, Agent target) {
            drainPlayer(hydraBossNPC, target);
        }
    },

    GRAB_SEEKER_FLOATING(AttackType.SPECIAL, GalvekPhase.WATER, GalvekPhase.AIR) {

        @Override
        public boolean canAttack(GalvekBoss boss, Agent target) {
            return Misc.getDistance(target.getPosition(), boss.getPosition()) > 5;
        }

        @NotNull
        @Override
        public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
            return Stream.of(GRAB_PLAYER_MISSILE);
        }
        @Override
        public Animation getAttackAnimation(AttackType type) {
            return normalRangedAttackFloating;
        }

        @Override
        public void sequence(GalvekBoss boss, Agent target) {
            grabPlayer(boss, target);
        }
    },
    GRAB_SEEKER_SITTING(AttackType.SPECIAL, GalvekPhase.FIRE, GalvekPhase.EARTH) {

        @Override
        public boolean canAttack(GalvekBoss boss, Agent target) {
            return Misc.getDistance(target.getPosition(), boss.getPosition()) > 5;
        }

        @NotNull
        @Override
        public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
            return Stream.of(GRAB_PLAYER_MISSILE);
        }

        @Override
        public Animation getAttackAnimation(AttackType type) {
            return normalRangedAttackSitting;
        }

        @Override
        public void sequence(GalvekBoss boss, Agent target) {
            grabPlayer(boss, target);
        }
    },
    BOULDER_STUN(AttackType.SPECIAL, GalvekPhase.EARTH){

        @NotNull
        @Override
        public Stream<ProjectileTemplate> fetchProjectiles(AttackType type) {
            return Stream.of(EARTH_BOULDER_PROJECTILE);
        }
        @Override
        public Animation getAttackAnimation(AttackType type) {
            return normalRangedAttackSitting;
        }

        @Override
        public void sequence(GalvekBoss hydraBossNPC, Agent target) {
            stunPlayer(hydraBossNPC, target);
        }
    },
FIRE_BALL_TRAPS(AttackType.SPECIAL, GalvekPhase.FIRE) {
    @Override
    public boolean isApplicable(GalvekBoss hydraBossNPC) {
        return !hydraBossNPC.fireTrapsActive && super.isApplicable(hydraBossNPC);
    }

    @Override
    public Animation getAttackAnimation(AttackType type) {
        return arcAttack;
    }

    @Override
    public void sequence(GalvekBoss boss, Agent target) {
        boss.fireTrapsActive = true;
        GalvekFireBalls balls = new GalvekFireBalls(boss);
        balls.spawnTraps();
    }
},
    FIRE_ARC_FLOATING(AttackType.SPECIAL, GalvekPhase.AIR, GalvekPhase.WATER){

        @Override
        public Animation getAttackAnimation(AttackType type) {
            return arcAttackFloating;
        }

        @Override
        public void sequence(GalvekBoss boss, Agent target) {
            TaskManager.submit(new FireArcTileTask(boss, target));
        }
    },
    FIRE_ARC_SITTING(AttackType.SPECIAL, GalvekPhase.FIRE, GalvekPhase.EARTH){

        @Override
        public Animation getAttackAnimation(AttackType type) {
            return arcAttack;
        }

        @Override
        public void sequence(GalvekBoss boss, Agent target) {
            TaskManager.submit(new FireArcTileTask(boss, target));
        }
    },
    TSUNAMI(AttackType.SPECIAL, GalvekPhase.WATER){
        @Override
        public boolean isApplicable(GalvekBoss hydraBossNPC) {
            return !hydraBossNPC.tsunamiActive && super.isApplicable(hydraBossNPC);
        }
        @Override
        public Animation getAttackAnimation(AttackType type) {
            return arcAttackFloating;
        }

        @Override
        public void sequence(GalvekBoss galvekBoss, Agent target) {
            galvekBoss.tsunamiActive = true;
            GalvekTsunamiWall tsu = new GalvekTsunamiWall(galvekBoss);
            tsu.start();
        }
    };

    // TODO: we could make some kind of special attack class that
    // runs a block on arrival, or maybe this already exists?
    void grabPlayer(GalvekBoss boss, Agent target) {
        Projectile projectile = new Projectile(boss, target, GalvekConstants.GRAB_PLAYER_MISSILE);
        projectile.sendProjectile();
        projectile.onArrival(() -> {
            target.performGraphic(GalvekConstants.GRAB_BLAST);
            target.moveTo(boss.getPosition().copy().add(0, -1));
        });
    }
    void stunPlayer(GalvekBoss boss, Agent target) {
        Projectile projectile = new Projectile(boss, target, EARTH_BOULDER_PROJECTILE);
        projectile.sendProjectile();
        projectile.onArrival(() -> {
            target.performGraphic(new Graphic(245, GraphicHeight.HIGH));
            target.getMotion().impairMovement(2);
            target.getAsPlayer().playSound(new Sound(2727));
//            target.messageIfPlayer("You feel stunned!");
            target.getAsPlayer().getCombat().submit(new StunEvent(2, true, true, true));
        });
    }

    void drainPlayer(GalvekBoss boss, Agent target) {
        Projectile projectile = new Projectile(boss, target, AIR_GUST_DRAIN);
        projectile.sendProjectile();
        projectile.onArrival(() -> {
            target.ifPlayer(player -> {
                player.getSkillManager().setCurrentLevel(Skill.PRAYER, player.getSkillManager().getCurrentLevel(Skill.PRAYER) - Misc.random(2, 5), true);
                int runEnergy = player.getRunEnergy();
                int afterDrain = runEnergy - Misc.random(20, 50);
                if(afterDrain < 0) {
                    afterDrain = 0;
                }
                player.setRunEnergy(afterDrain);
                player.getPacketSender().sendOrbConfig();
                player.sendMessage("You feel drained!");

            });

        });

    }


    void sendDragonFire(GalvekBoss boss, Agent target) {
        boolean purple = Misc.randomBoolean();
        Projectile projectile = new Projectile(boss, target, purple ? PURPLE_DRAGONFIRE : ORANGE_DRAGONFIRE);
        projectile.sendProjectile();
        projectile.onArrival(() -> {
            target.ifPlayer(player -> {
                Damage damageDone = createDragonFireDamage(player);
                if(purple && damageDone.getValue() > 0) {
                    PrayerHandler.deactivatePrayers(player);
                    player.sendMessage("Your prayers have been disabled!");
                }
                player.performGraphic(purple ? PURPLE_DRAGONFIRE_BLAST : FIRE_ARC_BLAST);
                player.getPacketSender().sendSound(223);
                player.getCombat().queue(damageDone);

            });

        });

    }

    /**
     * Custom dragonfire damage for galvek. Damage amount can be messed with.
     *
     * @param player The Player the fire will hit
     * @return the Damage that the dragonfire should cause
     */
    private Damage createDragonFireDamage(Player player) {
        var damageAmount = 40;
        if (EquipmentUtil.isWearingDragonFireProtection(player))
            damageAmount -= 30;
        if (!player.getCombat().getFireImmunityTimer().finished() || !player.getCombat().getSuperFireImmunityTimer().finished())
            damageAmount -= 10;


        if(damageAmount == 0) {
            player.sendMessage("You are protected from the dragon breath!");
            return Damage.createBlockedHit();

        } else if(damageAmount == 10) {
            player.sendMessage("You are a little burnt by the dragon fire!");
            return Damage.create(0, 10);
        } else if(damageAmount == 30) {
            player.sendMessage("You are a medium well burnt by the dragon fire!");
            return Damage.create(0, 25);
        } else  {
            player.sendMessage("You are badly burnt by the dragon fire!");
            return Damage.create(0, 73);
        }
    }

    public static final GalvekAttack[] VALUES = values();
    private final AttackType type;
    private final GalvekPhase[] applicableStates;

    GalvekAttack(AttackType type, GalvekPhase... states) {
        this.type = type;
        this.applicableStates = states;
    }


    /**
     * Checks if the current boss state matches any of this attack's possible states
     * @param boss
     * @return
     */
    public boolean isApplicable(GalvekBoss boss){
        return Arrays.stream(applicableStates).anyMatch(galvekState -> galvekState == boss.getPhase());
    }

    public boolean isFlying() {
        Stream<GalvekPhase> states = Arrays.stream(applicableStates);
        return states.anyMatch(phase -> (phase == GalvekPhase.WATER || phase == GalvekPhase.AIR));
    }

    public boolean canAttack(GalvekBoss hydraBossNPC, Agent target){
        return true;
    }

    public void sequence(GalvekBoss hydraBossNPC, Agent target){ }

    public AttackType getType() {
        return type;
    }

    @Override
    public int fetchAttackDuration(AttackType type) {
        return 6;
    }

    @Override
    public Stream<HitTemplate> fetchHits(AttackType type) {
        if(type != SPECIAL) {
            return HitTemplate.builder(type).buildAsStream();
        } else {
            return Stream.empty();// Specs do custom damage, dont do generic hits
        }
    }

    public GalvekAttack getRandomAttack(GalvekBoss boss) {
        if(this == TSUNAMI && boss.tsunamiActive) {
            return TSUNAMI;
        } else {
            final List<GalvekAttack> possibleAttacks = Arrays.stream(GalvekAttack.VALUES)
                    .filter(hydraAttack -> hydraAttack.getType() != SPECIAL)// NO specs
                    .filter(attack -> attack.isApplicable(boss))
                    .collect(Collectors.toList());

            if(possibleAttacks.size() > 1) {
                possibleAttacks.remove(boss.getAttackType());
            }

            return Misc.random(possibleAttacks);
        }
    }


}
