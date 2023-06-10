package com.grinder.game.entity.agent.combat.attack.special;

import com.grinder.game.content.dueling.DuelRule;
import com.grinder.game.content.skill.SkillManager;
import com.grinder.game.entity.Entity;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.sound.Sounds;
import com.grinder.util.ItemID;
import com.grinder.util.oldgrinder.EquipSlot;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Attr;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.grinder.game.model.Skill.MINING;
import static com.grinder.game.model.Skill.WOODCUTTING;

final class NonAttackSpecialAttacks {

    @NotNull
    static Consumer<Player> staffOfTheDeadEffect() {
        return player -> {
            if (player.getSpecialPercentage() < 100) {
                player.sendMessage("You do not have enough special attack!");
                return;
            }
            if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_MAGIC.ordinal()]) {
                player.sendMessage("Magic has been disabled in this duel!");
                return;
            }

            player.performGraphic(new Graphic(1228, GraphicHeight.HIGH_400));
            player.performAnimation(new Animation(1720));
            player.sendMessage("<col=007f00>Spirits of deceased evildoers offer you their protection.");
            player.setSpecialActivatedAndSendState(false);
            SpecialAttackType.drain(player, 100);
            EntityExtKt.markTime(player, Attribute.SOTD_SPEC_EFFECT);
        };
    }

    @NotNull
    static Consumer<Player> dragonPickaxeEffect() {
        return player -> {

            if (player.getSpecialPercentage() < 100) {
                player.sendMessage("You do not have enough special attack!");
                return;
            }

            final SkillManager skillManager = player.getSkillManager();

            if (skillManager.getCurrentLevel(Skill.ATTACK) < 60) {
                player.sendMessage("You need at least an Attack level of 60 to utilize this special!");
                return;
            }

            player.say("Smashing!");
            player.performAnimation(new Animation(2661));
            player.getPacketSender().sendAreaPlayerSound(Sounds.DRAGON_PICKAXE_SPECIAL_SOUND);
            player.setSpecialActivatedAndSendState(false);
            skillManager.increaseLevelTemporarily(MINING, 3);

            SpecialAttackType.drain(player, 100);

        };
    }

    @NotNull
    static Consumer<Player> dragonBattleAxeEffect() {
        return player -> {

            if (player.getSpecialPercentage() < 100) {
                player.sendMessage("You do not have enough special attack!");
                return;
            }

            final SkillManager skillManager = player.getSkillManager();

            if (skillManager.getCurrentLevel(Skill.ATTACK) < 60) {
                player.sendMessage("You need at least an Attack level of 60 to utilize this special!");
                return;
            }

            player.say("Raarrrrrgggggghhhhhhh!");
            player.performAnimation(new Animation(1056));
            player.performGraphic(new Graphic(246));
            player.getPacketSender().sendAreaPlayerSound(Sounds.DRAGON_BATTLE_AXE_SPECIAL_SOUND);
            player.setSpecialActivatedAndSendState(false);
            final int levelsDrained = (int) (player.getSkillManager().getCurrentLevel(Skill.ATTACK) * 0.10 +
                    player.getSkillManager().getCurrentLevel(Skill.DEFENCE) * 0.10 +
                    player.getSkillManager().getCurrentLevel(Skill.RANGED) * 0.10 +
                    player.getSkillManager().getCurrentLevel(Skill.MAGIC) * 0.10);
            skillManager.setCurrentLevel(Skill.ATTACK, (int) (player.getSkillManager().getCurrentLevel(Skill.ATTACK) * 0.90), true);
            skillManager.setCurrentLevel(Skill.DEFENCE, (int) (player.getSkillManager().getCurrentLevel(Skill.DEFENCE) * 0.90), true);
            skillManager.setCurrentLevel(Skill.RANGED, (int) (player.getSkillManager().getCurrentLevel(Skill.RANGED) * 0.90), true);
            skillManager.setCurrentLevel(Skill.MAGIC, (int) (player.getSkillManager().getCurrentLevel(Skill.MAGIC) * 0.90), true);
            skillManager.increaseLevelTemporarily(Skill.STRENGTH, (10 + (levelsDrained / 4)));
            SpecialAttackType.drain(player, 100);

        };
    }

    @NotNull
    static Consumer<Player> dragonAxeEffect() {
        return player -> {

            if (player.getSpecialPercentage() < 100) {
                player.sendMessage("You do not have enough special attack!");
                return;
            }

            final SkillManager skillManager = player.getSkillManager();

            if (skillManager.getCurrentLevel(Skill.ATTACK) < 60) {
                player.sendMessage("You need at least an Attack level of 60 to utilize this special!");
                return;
            }

            player.say("Chop chop!");
            player.performAnimation(new Animation(2876));
            player.performGraphic(new Graphic(479));
            player.getPacketSender().sendAreaPlayerSound(Sounds.DRAGON_AXE_SPECIAL_SOUND);
            player.setSpecialActivatedAndSendState(false);
            skillManager.increaseLevelTemporarily(WOODCUTTING, 3);

            SpecialAttackType.drain(player, 100);

        };
    }

    @NotNull
    static Consumer<Player> excaliburEffect() {
        return player -> {

            if (player.getSpecialPercentage() < 100) {
                player.sendMessage("You do not have enough special attack!");
                return;
            }

            final SkillManager skillManager = player.getSkillManager();

            if (skillManager.getCurrentLevel(Skill.ATTACK) < 20) {
                player.sendMessage("You need at least an Attack level of 20 to utilize this special!");
                return;
            }

            player.say("For Camelot!");
            player.performAnimation(new Animation(1057));
            player.performGraphic(new Graphic(247));
            player.getPacketSender().sendAreaPlayerSound(Sounds.EXCALIBUR_SPECIAL_SOUND);
            player.setSpecialActivatedAndSendState(false);
            skillManager.increaseLevelTemporarily(Skill.DEFENCE, 8);
            SpecialAttackType.drain(player, 100);

        };
    }

    static Consumer<Player> harpoonEffect() {
        return player -> {
            if (player.getSpecialPercentage() < 100) {
                player.sendMessage("You do not have enough special attack!");
                return;
            }

            final SkillManager skillManager = player.getSkillManager();
            boolean usingInfernal = Stream.of(ItemID.INFERNAL_HARPOON).anyMatch(itemId -> itemId == player.getEquipment().atSlot(EquipSlot.WEAPON).getId());

            if (skillManager.getCurrentLevel(Skill.ATTACK) < 60) {
                player.sendMessage("You need at least an Attack level of 60 to utilize this special!");
                return;
            }

            player.say("Here fishy fishies!");
            player.performAnimation(new Animation(usingInfernal ? 7394 : 7393));
            player.getPacketSender().sendSound(2538);
            player.performGraphic(new Graphic(246));
            skillManager.increaseLevelTemporarily(Skill.FISHING, 3);
            SpecialAttackType.drain(player, 100);
        };
    }
}
