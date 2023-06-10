package com.grinder.game.content.minigame.chamberoxeric.room.olm.attack;

import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl.*;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl.lefthand.AutoHealOlmAttack;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl.lefthand.CrystalBustOlmAttack;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl.lefthand.LightningOlmAttack;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.attack.impl.lefthand.SwapOlmAttack;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public enum OlmCombatAttack {

    MAGIC_ATTACK(new MagicStandardOlmAttack()),

    RANGE_ATTACK(new RangeStandardOlmAttack()),

    SPHERE(new SphereOlmAttack()),

    CRYSTAL_BURST(new CrystalBustOlmAttack()),

    LIGHTNING(new LightningOlmAttack()),

    SWAP(new SwapOlmAttack()),

    AUTO_HEAL(new AutoHealOlmAttack()),

    ACID_SPRAY(new AcidSprayOlmAttack()),

    ACID_DRIP(new AcidDripOlmAttack()),

    DEEP_BURN(new DeepBurnOlmAttack()),

    FIRE_WALL(new FireWallOlmAttack()),

    FALLING_CRYSTALS(new TargetFallingCrystalsOlmAttack()),

    CRYSTAL_BOMBS(new CrystalBombsOlmAttack()),

    LIFE_SIPHON(new LifeSiphonOlmAttack()),

    ;

    public OlmAttack attack;

    OlmCombatAttack(OlmAttack attack) {
        this.attack = attack;
    }
}
