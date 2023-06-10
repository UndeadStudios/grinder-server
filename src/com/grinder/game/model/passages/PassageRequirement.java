package com.grinder.game.model.passages;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Skill;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class PassageRequirement {

    private static final Consumer<Player> DEFAULT_FAIL = player -> player.sendMessage("You don't have the requirements to go through this passage.");

    private final Predicate<Player> predicate;
    private final Consumer<Player> onFail;
    private final Consumer<Player> onSuccess;

    public PassageRequirement(Predicate<Player> predicate, Consumer<Player> onFail, Consumer<Player> onSuccess) {
        this.predicate = predicate;
        this.onFail = onFail;
        this.onSuccess = onSuccess;
    }

    public boolean test(Player player) {
        return predicate.test(player);
    }

    public Consumer<Player> getOnFail() {
        return onFail != null ? onFail : DEFAULT_FAIL;
    }

    public Consumer<Player> getOnSuccess() {
        return onSuccess;
    }

    public static Predicate<Player> requiredSkill(Skill skill, int level) {
        return player -> player.getSkillManager().getCurrentLevel(skill) >= level;
    }}
