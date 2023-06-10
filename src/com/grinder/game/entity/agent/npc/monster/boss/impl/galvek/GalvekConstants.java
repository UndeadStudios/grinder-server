package com.grinder.game.entity.agent.npc.monster.boss.impl.galvek;

import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.model.projectile.ProjectileTemplateBuilder;
import com.grinder.game.model.sound.Sound;

/**
 * Represents an insane amount of time wasted searching for animation/graphics IDs.
 * Since we were unable to find a player with access to Galvek in a reasonable time,
 * we could not simply dump the IDs with runelite. For this reason the exact sounds
 * used in the real Galvek are still not located. Projectile templates likely will
 * need modified a tiny bit to look perfect.
 *
 * @author Pea2nuts
 */
public class GalvekConstants {

    /*
    7900 = swipe attack
    7901 = breathe fire
    7902 = normal stand anim
    7903 = floating a bit arms weird
    7904 = stomp anim
    7905 = bite
    7906 = float up
    7907 = float down
    7908 = angry fall down quickly
    7909 = angry fall up quickly
    7910 = clench fists
    7911 = clench fists, while flopping up hurt
    7912 = crack back/shoulders
    7913 = lay on ground breathing, injured
    7914 = clench fists and yell / breathe fire
    7915 = die anim
    7916 = laying on ground dead
     */
    public final static Animation swipeAttackSitting = new Animation(7900);
    public final static Animation breatheFireSitting = new Animation(7901);
    public final static Animation standAnimSitting = new Animation(7902);//and walk anim
    public final static Animation standAnimFloating = new Animation(7903);
    public final static Animation normalRangedAttackFloating = new Animation(7904);
    public final static Animation normalRangedAttackSitting = new Animation(7914);
    public final static Animation biteAttackFloating = new Animation(7905);
    public final static Animation floatingFlyUp = new Animation(7906);
    public final static Animation floatingLand = new Animation(7907);// Phase change 1 land
    public final static Animation sittingLand = new Animation(7908);
    public final static Animation sittingFlyUp = new Animation(7909);// Phase change 1
    public final static Animation arcAttack = new Animation(7910);// Fire arc anim
    public final static Animation arcAttackFloating = new Animation(7911);// Fire arc from floating stage & tsunami anim
    public final static Animation crackBack = new Animation(7912);//tailSweep
    public final static Animation layingAlive = new Animation(7913);
    public final static Animation dieAnim = new Animation(7915);
    public final static Animation layingDead = new Animation(7916);

    public final static Animation TSUNAMI_DEATH_ANIM = new Animation(7879);
    public final static Graphic FIRE_ARC_BLAST = new Graphic(1466, GraphicHeight.MIDDLE);
    public final static Graphic GRAB_BLAST = new Graphic(1492);
    public final static Graphic PURPLE_DRAGONFIRE_BLAST = new Graphic(1473, GraphicHeight.MIDDLE);
    public static final int TSUNAMI_NPC = 8099;
    public static final int FIRE_BALL_LOC = 32378;

    public static final ProjectileTemplate FIRE_MAGIC_AIRBLAST = new ProjectileTemplateBuilder(1490)
            .setSourceSize(3)
            .setStartHeight(60)
            .setEndHeight(31)
            .setDelay(30)
            .setSpeed(14)
            //.setSpeedStep(8)
            .setCurve(16)
            //.setDepartureSound(x)
            //.setArrivalSound(x)
            //.setSourceOffset(128)
            .build();

    public static final ProjectileTemplate GRAB_PLAYER_MISSILE = new ProjectileTemplateBuilder(1381)
            .setSourceSize(2)
            .setStartHeight(60)
            .setEndHeight(31)
            .setDelay(30)
            .setSpeed(14)
            //.setSpeedStep(8)
            .setCurve(16)
            //.setDepartureSound(x)
            //.setArrivalSound(x)
            //.setSourceOffset(128)
            .build();

    public static final ProjectileTemplate AIR_GUST_DRAIN = new ProjectileTemplateBuilder(1496)
            .setSourceSize(3)
            .setStartHeight(60)
            .setEndHeight(31)
            .setDelay(30)
            .setSpeed(14)
            //.setSpeedStep(8)
            .setCurve(16)
            //.setDepartureSound(x)
            //.setArrivalSound(x)
            //.setSourceOffset(128)
            .build();

    public static final ProjectileTemplate AIR_RANGE_RED = new ProjectileTemplateBuilder(1489)
            .setSourceSize(3)
            .setStartHeight(60)
            .setEndHeight(31)
            .setDelay(30)
            .setSpeed(14)
            //.setSpeedStep(8)
            .setCurve(16)
            //.setDepartureSound(x)
            //.setArrivalSound(x)
            //.setSourceOffset(128)
            .build();
    public static ProjectileTemplateBuilder TSUNAMI_PROJECTILE = new ProjectileTemplateBuilder(1497)
            .setSourceSize(3)
            .setStartHeight(105)
            .setEndHeight(15)
            .setDelay(40)
            .setSpeed(14)
            //.setSpeedStep(8)
            .setCurve(30)
            //.setDepartureSound(x)
            //.setArrivalSound(x)
            //.setSourceOffset(64)
            ;

    public static final ProjectileTemplate ORANGE_DRAGONFIRE = new ProjectileTemplateBuilder(393)
            .setSourceSize(5)
            .setStartHeight(60)
            .setEndHeight(31)
            .setDelay(50)
            .setSpeed(25)
            //.setSpeedStep(8)
            .setCurve(16)
            //.setDepartureSound(x)
            //.setArrivalSound(x)
            //.setSourceOffset(128)
            .build();
    public static final ProjectileTemplate PURPLE_DRAGONFIRE = new ProjectileTemplateBuilder(1471)
            .setSourceSize(5)
            .setStartHeight(60)
            .setEndHeight(31)
            .setDelay(50)
            .setSpeed(25)
            //.setSpeedStep(8)
            .setCurve(16)
            //.setDepartureSound(x)
            //.setArrivalSound(x)
            //.setSourceOffset(128)
            .build();
    public static final ProjectileTemplate FIRE_ARC_ATTACK = new ProjectileTemplateBuilder(1481)
            .setSourceSize(2)
            .setStartHeight(125)
            .setEndHeight(31)
            .setDelay(25)
            .setSpeed(45)
            //.setSpeedStep(8)
            .setCurve(16)
            //.setDepartureSound(x)
            //.setArrivalSound(x)
            .setDepartureSound(new Sound(1519, 10))
            //.setSourceOffset(64)
            .setArrivalGraphic(FIRE_ARC_BLAST)
            .build()
//            .setSourceSize(5)
//            .setSourceOffset(1)
//            .setHeights(60, 0)
//            .setCurve(45)
//            .setSpeed(120)
//            .setDepartureSound(new Sound(1519, 10))
//            .setDelay(40)
//            .build()
            ;
    public static final ProjectileTemplate FIRE_TRAP_ATTACK = new ProjectileTemplateBuilder(1495)
            .setSourceSize(2)
            .setStartHeight(60)
            .setEndHeight(31)
            .setDelay(25)
            .setSpeed(45)
            //.setSpeedStep(8)
            .setCurve(40)
            //.setDepartureSound(x)
            //.setArrivalSound(x)
            .setSourceOffset(64)
            .build();

    public static final ProjectileTemplate EARTH_BOULDER_PROJECTILE = new ProjectileTemplateBuilder(1493)
            .setSourceSize(2)
            .setStartHeight(60)
            .setEndHeight(15)
            .setDelay(40)
            .setSpeed(15)
            //.setSpeedStep(12)
            .setCurve(16)
            //.setDepartureSound(x)
            //.setArrivalSound(x)
           // .setSourceOffset(64)
            .build();
/*
    private static final Projectile FIRE_MAGIC = new Projectile(1490, 60, 31, 40, 25, 8, 16, 128);
    private static final Projectile DRAIN = new Projectile(1496, 60, 31, 40, 25, 8, 16, 128);
    private static final Projectile AIR_RANGE_RED = new Projectile(1489, 60, 31, 40, 25, 8, 16, 128);
    private static final Projectile RED_DRAGONFIRE = new Projectile(393, 60, 31, 40, 25, 8, 16, 128);
    private static final Projectile PURPLE_DRAGONFIRE = new Projectile(1471, 60, 31, 40, 25, 8, 16, 128);
    private static final Projectile FIRE_ARC_ATTACK = new Projectile(1481, 125, 31, 40, 75, 8, 40, 64);
    private static final Projectile BOULDER_PROJECTILE = new Projectile(1493, 60, 15, 40, 25, 12, 16, 64);

 */

}
