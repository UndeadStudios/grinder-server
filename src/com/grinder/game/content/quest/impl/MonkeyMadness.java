package com.grinder.game.content.quest.impl;

import com.grinder.game.World;
import com.grinder.game.content.miscellaneous.TravelSystem;
import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;

import java.util.concurrent.TimeUnit;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class MonkeyMadness extends Quest {

    private static final Position JUNGLE_DEMON_DUNGEON = new Position(2715, 9165);

    private static final Position JUNGLE_DEMON_POS = new Position(2714, 9202);

    private static final Position ENTRY_POSITION = new Position(2715, 9184);

    private static final Position EXIT_POSITION = new Position(2579, 4456);
    private static final int JUNGLE_DEMON = 6321;

    private void spawnJungleDemon(Player p) {
        QuestManager.despawnNpcs(p);

        int height = (p.getIndex() * 4) + 1;

        NPC demon = NPCFactory.INSTANCE.create(JUNGLE_DEMON, JUNGLE_DEMON_POS.clone().transform(0, 0, height));

        demon.setOwner(p);

        World.getNpcAddQueue().add(demon);

        p.getQuest().spawnedNpcs.add(demon);

        //p.moveTo(EXIT_POSITION.clone().transform(0, 0, height));
    }

    public MonkeyMadness() {
        super("Monkey Madness", false, 3, 3);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{

                {"",
                        "The King of the northern Gnomes, Narnode ",
                        "Shareen, is once again in need of your ",
                        "help. He recently decided to send an ",
                        "envoy of his Royal Guard, the 10th squad, ",
                        "to oversee the decommissioning of the ",
                        "Gnome owned ship-building facilities ",
                        "on the eastern coast of Karamja. It has ",
                        "been quite some time since the 10th squad ",
                        "were dispatched and they have been deemed ",
                        "missing in action. It will be up to you, ",
                        "should you decide to help, to find out ",
                        "what fate befell the 10th squad and if ",
                        "possible, track them down.  If only it ",
                        "were so simple. Sinister forces have ",
                        "begun to spread through the Gnome hierarchy ",
                        "and threaten to unleash an unknown terror ",
                        "upon the world. Far across the land, ",
                        "the fires of vengeance are being stoked ",
                        "once again. Can you unravel the mystery ",
                        "behind the deception? Can you separate ",
                        "the truth from the lies? Can you decide ",
                        "for yourself what is real and what is ",
                        "not? ",
                        "",
                        "Requirements:",
                        "",
                        "Ability to defeat a level 327 Jungle Demon",
                },
                {"",
                        "I been told about a small crack terrorizing",
                        "the stability in the jungle. I been asked to",
                        "take check it and take down the demon.",
                },
                {"",
                        "I have defeated the Jungle Demon, I should",
                        "report back.",
                },
                {""},
        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{1423};
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if (id == 6) {
            increaseStage(player);
            sendDialogue(player, 8);
        } else if (id == 10) {
            player.getPacketSender().sendFadeScreen("", 2, 5);
            player.delayedMoveTo(JUNGLE_DEMON_DUNGEON, 3);
            player.getPacketSender().sendInterfaceRemoval();
        } else if (id == 14) {
            increaseStage(player);
            QuestManager.complete(player, quest, new String[]{
                    "150,000 Attack XP",
                    "75,000 Agility XP",
                    "Ability to equip the",
                    "Dragon scimitar/Lava blade",
                    "Access to Jungle demon lair",
            }, ItemID.DRAGON_SCIMITAR);
            player.getSkillManager().addFixedDelayedExperience(Skill.ATTACK, 150_000);
            player.getSkillManager().addFixedDelayedExperience(Skill.AGILITY, 75_000);
        }
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 5798:
                if (getStage(player) == 1) {
                    if (player.getPosition().sameAs(ENTRY_POSITION)) {
                        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 3, TimeUnit.SECONDS, false, true)) {
                            return true;
                        }
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                        player.getMotion().update(MovementStatus.DISABLED);
                        player.performAnimation(new Animation(2240));
                        player.getPacketSender().sendMessage("You try to squeeze through the crack..");
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                player.getMotion().update(MovementStatus.NONE);
                                stop();

                                new DialogueBuilder(DialogueType.STATEMENT)
                                        .setText("WARNING - You notice a dangerous demon capable of",
                                                "tearing you a new one in seconds. Proceed with caution!")
                                        .add(DialogueType.OPTION)
                                        .setOptionTitle("Do you wish to fight the Jungle Demon?")
                                        .firstOption("Yes - I am brave enough.", player1 ->  {

                                            player.getPacketSender().sendInterfaceRemoval();
                                            player.performAnimation(new Animation(2796, 25));
                                            player.playSound(new Sound(Sounds.CRAWL_THROUGH_TUNNEL));
                                            player.BLOCK_ALL_BUT_TALKING = true;
                                            player.moveToByFadeScreen(player, new Position(2696, 9212, (player.getIndex() * 4) + 1), "You enter the crevice");
                                            TaskManager.submit(3, () -> {
                                                spawnJungleDemon(player);
                                                player.resetAnimation();
                                                player.BLOCK_ALL_BUT_TALKING = false;
                                            });
                                        }).addCancel("No - I will pass.").start(player);
                            }
                        });

                        return true;
                    }
                }
                return false;
        }
        return false;
    }

    @Override
    public boolean handleNpcDeath(Player player, NPC npc) {
        switch (npc.getId()) {
            case JUNGLE_DEMON:
                if (getStage(player) == 1) {
                    increaseStage(player);
                }
                return true;
        }
        return false;
    }

    @Override
    public boolean hasRequirements(Player player) {
        return true;
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}
