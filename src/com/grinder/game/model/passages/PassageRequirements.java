package com.grinder.game.model.passages;

import com.grinder.game.content.minigame.warriorsguild.WarriorsGuild;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.godwars.GodChamber;
import com.grinder.game.model.areas.impl.BossInstances;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;
import com.grinder.util.timing.TimerKey;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public final class PassageRequirements {

    public static void init() {
        guilds();
        gwd();
        dks();

        find("Edgeville Dungeon").addRequirement(player -> player.getInventory().contains(ItemID.BRASS_KEY) || player.getY() >= 3450,
                player -> player.sendMessage("The door is tightly locked. Perhaps I need some sort of a key to open it."));

        find("Wilderness Resource Area").onOption("Quick-Entry", player -> {
            if (player.getY() <= 3944) {
                find("Wilderness Resource Area").open(player);
                return;
            }
            if (player.getInventory().getAmount(ItemID.COINS) < 2_500_000) {
                new DialogueBuilder(DialogueType.STATEMENT)
                        .setText("You don't have enough coins to pay the fee.")
                        .start(player);
                return;
            }
            find("Wilderness Resource Area").open(player);
            player.sendMessage("@red@You receieve 25% bonus experience when skilling in the Wilderness resource area.");
            player.sendMessage("@red@Please be aware of PKer's!");
            player.getInventory().delete(ItemID.COINS, 2_500_000);
        });

        find("Taverly Dungeon").addRequirement(player -> player.getInventory().contains(ItemID.DUSTY_KEY) || player.getX() <= 2923,
                player -> {
                    player.sendMessage("The gate is locked from this side.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });

        find("Taverly Jail").addRequirement(player -> player.getInventory().contains(ItemID.SHINY_KEY),
                player -> {
                    player.sendMessage("The door is tightly locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });

        find("Taverly Jail2").addRequirement(player -> player.getInventory().contains(ItemID.SHINY_KEY),
                player -> {
                    player.sendMessage("The door is tightly locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });

        find("Melzar Maze Main").addRequirement(player -> player.getInventory().contains(ItemID.MAZE_KEY),
                player -> {
                    player.sendMessage("The door is locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });

        find("Melzar Maze Magneta").addRequirement(player -> player.getInventory().contains(ItemID.KEY_7),
                player -> {
                    player.sendMessage("The door is locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });

        find("Melzar Maze Blue").addRequirement(player -> player.getInventory().contains(ItemID.KEY_6),
                player -> {
                    player.sendMessage("The door is locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });

        find("Melzar Maze Green").addRequirement(player -> player.getInventory().contains(ItemID.KEY_8),
                player -> {
                    player.sendMessage("The door is locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });

        find("Melzar Maze Red").addRequirement(player -> player.getInventory().contains(ItemID.KEY_3),
                player -> {
                    player.sendMessage("The door is locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });
        find("Melzar Maze Red2").addRequirement(player -> player.getInventory().contains(ItemID.KEY_3),
                player -> {
                    player.sendMessage("The door is locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });
        find("Melzar Maze Red3").addRequirement(player -> player.getInventory().contains(ItemID.KEY_3),
                player -> {
                    player.sendMessage("The door is locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });

        find("Melzar Maze Orange").addRequirement(player -> player.getInventory().contains(ItemID.KEY_4),
                player -> {
                    player.sendMessage("The door is locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });
        find("Melzar Maze Orange2").addRequirement(player -> player.getInventory().contains(ItemID.KEY_4),
                player -> {
                    player.sendMessage("The door is locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });
        find("Melzar Maze Orange3").addRequirement(player -> player.getInventory().contains(ItemID.KEY_4),
                player -> {
                    player.sendMessage("The door is locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });
        find("Melzar Maze Orange4").addRequirement(player -> player.getInventory().contains(ItemID.KEY_4),
                player -> {
                    player.sendMessage("The door is locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });
        find("Melzar Maze Yellow").addRequirement(player -> player.getInventory().contains(ItemID.KEY_5),
                player -> {
                    player.sendMessage("The door is locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });
        find("Melzar Maze Yellow2").addRequirement(player -> player.getInventory().contains(ItemID.KEY_5),
                player -> {
                    player.sendMessage("The door is locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });
        find("Melzar Maze Yellow3").addRequirement(player -> player.getInventory().contains(ItemID.KEY_5),
                player -> {
                    player.sendMessage("The door is locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });
        find("Melzar Maze Yellow4").addRequirement(player -> player.getInventory().contains(ItemID.KEY_5),
                player -> {
                    player.sendMessage("The door is locked.");
                    player.playSound(new Sound(Sounds.USE_KEY_ON_LOCKED_DOOR));
                });

        find("Champion Guild").addRequirement(player -> player.getPoints().get(AttributeManager.Points.QUEST_POINTS) > 25,
                player -> {
                        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                .setText("Can I get in please?").add(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.GUILDMASTER)
                                .setText("I am sorry, but only true champions on are allowed", "to enter!").add(DialogueType.PLAYER_STATEMENT)
                                .setText("...Perhaps I should finish some more quests.")
                                .start(player);
                    player.playSound(new Sound(Sounds.BANK_PIN_WRONG));
                    return;
                });

        find("Draynor Rubber Door").addRequirement(player -> player.getInventory().contains(ItemID.KEY),
                player -> {
                        player.getPacketSender().sendMessage("The door seems to be locked. Perhaps I should search for a key..");
                        player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
                        return;
                });

        find("Wilderness Resource Area").onOption("Open", player -> {
            if (player.getY() <= 3944) {
                find("Wilderness Resource Area").open(player);
                return;
            }
            if (player.getInventory().getAmount(ItemID.COINS) < 2_500_000) {
                new DialogueBuilder(DialogueType.STATEMENT)
                        .setText("You don't have enough coins pay the fee.")
                        .start(player);
                return;
            }
            new DialogueBuilder(DialogueType.OPTION)
                    .setOptionTitle("Pay entrance fee of 2,500,000 coins?")
                    .firstOption("Yes.", $ -> {
                        find("Wilderness Resource Area").open(player);
                        player.getInventory().delete(ItemID.COINS, 2_500_000);
                        player.sendMessage("@red@You receieve 25% bonus experience when skilling in the Wilderness resource area.");
                        player.sendMessage("@red@Please be aware of PKer's!");
                        DialogueManager.start(player, -1);
                    })
                    .addCancel("No")
                    .start(player);
        });

        find("Al-Kharid Gate").onPaymentSuccess(player -> {
            player.sendMessage("You pay the guard.");
            return true;
        });
        find("Al-Kharid Gate").onClick(player -> {
            new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                    .setText("Can I come through this gate?")
                    .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                            .setNpcChatHead(NpcID.BORDER_GUARD)
                            .setText("You must pay a toll of 10 gold coins to pass.")
                            .setNext(new DialogueBuilder(DialogueType.OPTION)
                                    .firstOption("No thank you, I'll walk around.", new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                            .setText("No thank you, I'll walk around.")
                                            .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.BORDER_GUARD)
                                                    .setText("Ok suit yourself."))::start)
                                    .secondOption("Who does my money go to?", new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                            .setText("Who does my money go to?")
                                            .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.BORDER_GUARD)
                                                    .setText("The money goes to the city of Al-Kharid."))::start)
                                    .thirdOption("Yes, ok.", $ -> {
                                        find("Al-Kharid Gate").payAndOpen(player);
                                        if (player.getInventory().contains(new Item(ItemID.COINS, 10))) {
                                            DialogueManager.start(player, -1);
                                        } else {
                                            new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setText("Yes, ok.")
                                                    .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                            .setText("Oh dear I don't actually seem to have enough money.")).start(player);
                                        }

                                    }))).start(player);
        });


        find("Varrock West Bank").onClick(player -> {
            new DialogueBuilder(DialogueType.TITLED_STATEMENT_NO_CONTINUE).setText("Knock knock...").start(player);
            player.playSound(new Sound(1735));
            player.BLOCK_ALL_BUT_TALKING = true;
            TaskManager.submit(new Task(3) {
                @Override
                protected void execute() {
                    var options = new DialogueBuilder(DialogueType.OPTION)
                            .setOptionTitle("What would you like to say?")

                            .firstOption("I'm " + player.getUsername() + ". Please let me in.", $ -> new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("I'm " + player.getUsername() + ". Please let me in.")
                                    .setNext(
                                            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                    .setNpcChatHead(2898)
                                                    .setText("No. Staff only beyond this point.", "You can't come in here.")).start(player))

                            .secondOption("Boo.", $ -> new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("Boo.").setNext(
                                            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                    .setNpcChatHead(2898)
                                                    .setText("Boo who?")
                                                    .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                            .setText("There's no need to cry!")))
                                    .start(player))

                            .thirdOption("Kanga.", $ -> new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("Kanga.").setNext(
                                            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                    .setNpcChatHead(2898)
                                                    .setText("Kanga who?")
                                                    .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                            .setText("No, 'kangaroo'."))).
                                    setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                            .setNpcChatHead(2898)
                                            .setText("Stop messing around and go away!"))
                                    .start(player))

                            .fourthOption("Thank.", $ -> new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                    .setText("Thank.").setNext(
                                            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                    .setNpcChatHead(2898)
                                                    .setText("Thank who?")
                                                    .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                            .setText("You're welcome!")))
                                    .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                            .setNpcChatHead(2898)
                                            .setText("Stop it!"))
                                    .start(player))

                            .fifthOption("Doctor.", $ -> new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                    .setNpcChatHead(2898)
                                    .setText("Doctor wh.. hang on, I'm not falling for that one again!", "Go away.")
                                    .start(player));

                    new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("I don't think I'm ever going to be allowed in there.")
                            .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                    .setNpcChatHead(2898)
                                    .setText("Who's there?").setNext(options)).start(player);
                    player.BLOCK_ALL_BUT_TALKING = false;
                    stop();
                }
            });


        });

        find("Alchemical Hydra").onEnter(player -> {
            player.getTimerRepository().register(TimerKey.ATTACK_IMMUNITY, 3);

            player.sendMessage("You did not create an instance for this door.");
        });

        find("Alchemical Hydra").onLeave(player -> {
            if (player.getArea() != null) {
                player.getArea().leave(player);

                TaskManager.submit(new Task(3) {
                    @Override
                    protected void execute() {
                        player.moveTo(player.getPosition().clone().setZ(0));
                        stop();
                    }
                });
            }
        });
    }

    private static boolean playerAtSpot(Player player, Position position) {
        for (var localPlayer : player.getLocalPlayers()) {
            if (localPlayer.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    private static void dks() {
        find("DKS 1").addRequirement(player -> {
            var firstSpot = new Position(2490, 10162, 0);
            var secondSpot = firstSpot.transform(0, 2, 0);
            var petRockAtFirstSpot = ItemOnGroundManager.getItemOnGround(Optional.of(player.getUsername()), ItemID.PET_ROCK, firstSpot).isPresent();
            var petRockAtSecondSpot = ItemOnGroundManager.getItemOnGround(Optional.of(player.getUsername()), ItemID.PET_ROCK, secondSpot).isPresent();
            if (player.getPosition().equals(firstSpot)) {
                return petRockAtSecondSpot || playerAtSpot(player, secondSpot);
            } else if (player.getPosition().equals(secondSpot)) {
                return petRockAtFirstSpot || playerAtSpot(player, firstSpot);
            } else {
                return (petRockAtFirstSpot || playerAtSpot(player, firstSpot)) && (petRockAtSecondSpot || playerAtSpot(player, secondSpot));
            }
        }, player -> player.sendMessage("Something blocking the door from opening."));

        find("DKS 2").addRequirement(player -> {
            var firstSpot = new Position(2490, 10146, 0);
            var secondSpot = firstSpot.transform(0, 2, 0);
            var petRockAtFirstSpot = ItemOnGroundManager.getItemOnGround(Optional.of(player.getUsername()), ItemID.PET_ROCK, firstSpot).isPresent();
            var petRockAtSecondSpot = ItemOnGroundManager.getItemOnGround(Optional.of(player.getUsername()), ItemID.PET_ROCK, secondSpot).isPresent();
            if (player.getPosition().equals(firstSpot)) {
                return petRockAtSecondSpot || playerAtSpot(player, secondSpot);
            } else if (player.getPosition().equals(secondSpot)) {
                return petRockAtFirstSpot || playerAtSpot(player, firstSpot);
            } else {
                return (petRockAtFirstSpot || playerAtSpot(player, firstSpot)) && (petRockAtSecondSpot || playerAtSpot(player, secondSpot));
            }
        }, player -> player.sendMessage("Something blocking the door from opening."));

        find("DKS 3").addRequirement(player -> {
            var firstSpot = new Position(2490, 10130, 0);
            var secondSpot = firstSpot.transform(0, 2, 0);
            var petRockAtFirstSpot = ItemOnGroundManager.getItemOnGround(Optional.of(player.getUsername()), ItemID.PET_ROCK, firstSpot).isPresent();
            var petRockAtSecondSpot = ItemOnGroundManager.getItemOnGround(Optional.of(player.getUsername()), ItemID.PET_ROCK, secondSpot).isPresent();
            if (player.getPosition().equals(firstSpot)) {
                return petRockAtSecondSpot || playerAtSpot(player, secondSpot);
            } else if (player.getPosition().equals(secondSpot)) {
                return petRockAtFirstSpot || playerAtSpot(player, firstSpot);
            } else {
                return (petRockAtFirstSpot || playerAtSpot(player, firstSpot)) && (petRockAtSecondSpot || playerAtSpot(player, secondSpot));
            }
        }, player -> player.sendMessage("Something blocking the door from opening."));

    }

    private static void gwd() {
        find("Bandos").onOpen(player -> {
            var cost = BossInstances.Companion.getCost(player);
            var enter = new AtomicBoolean(false);
            new DialogueBuilder(DialogueType.OPTION).firstOption("Enter the chamber.", $ -> {
                if (player.getAttributes().numInt(Attribute.BANDOS_KILL_COUNT) < (PlayerUtil.isMember(player) ? 5 : 20)) {
                    player.getInventory().delete(ItemID.ECUMENICAL_KEY, 1);
                }
                find("Bandos").open(player);
                DialogueManager.start(player, -1);
                enter.set(true);
            }).secondOption("Create personal instance (" + Misc.formatWithAbbreviation2(cost) + ")", $ -> {
                if (!player.getInventory().contains(new Item(995, cost))) {
                    DialogueManager.sendStatement(player, "You do not have enough coins.");
                    return;
                }
                find("Bandos").open(player);
                player.getTimerRepository().register(TimerKey.ATTACK_IMMUNITY, 6);
                TaskManager.submit(1, () -> {
                    BossInstances.Companion.createPersonalInstance(player, cost, true, player.getPosition(), BossInstances.GRAARDOR);
                    enter.set(true);
                });
                DialogueManager.start(player, -1);
            }).thirdOption("Never mind.", $ -> DialogueManager.start(player, -1)).start(player);
            return enter.get();
        });


        find("Bandos").addRequirement(player -> {
            if (!player.getInventory().contains(ItemID.ECUMENICAL_KEY)) {
                return player.getAttributes().numInt(Attribute.BANDOS_KILL_COUNT) >= (PlayerUtil.isMember(player) ? 5 : 20);
            }
            return true;
        }, player -> player.sendMessage("You need a kill count of at least " + (PlayerUtil.isMember(player) ? 5 : 20) + " to enter to the boss chamber."));

        find("Bandos").onOption("Peek", GodChamber.BANDOS::requestPlayerCount);
//        find("Bandos").onOpen(player -> {
//            if (player.getAttributes().numInt(Attribute.BANDOS_KILL_COUNT) < 40) {
//                player.getInventory().delete(ItemID.ECUMENICAL_KEY, 1);
//            }
//        });

        find("Saradomin").onOpen(player -> {
            var cost = BossInstances.Companion.getCost(player);
            var enter = new AtomicBoolean(false);
            new DialogueBuilder(DialogueType.OPTION).firstOption("Enter the chamber.", $ -> {
                if (player.getAttributes().numInt(Attribute.BANDOS_KILL_COUNT) < (PlayerUtil.isMember(player) ? 5 : 20)) {
                    player.getInventory().delete(ItemID.ECUMENICAL_KEY, 1);
                }
                find("Saradomin").open(player);
                DialogueManager.start(player, -1);
                enter.set(true);
            }).secondOption("Create personal instance (" + Misc.formatWithAbbreviation2(cost) + ")", $ -> {
                if (!player.getInventory().contains(new Item(995, cost))) {
                    DialogueManager.sendStatement(player, "You do not have enough coins.");
                    return;
                }
                find("Saradomin").open(player);
                player.getTimerRepository().register(TimerKey.ATTACK_IMMUNITY, 6);
                TaskManager.submit(1, () -> {
                    BossInstances.Companion.createPersonalInstance(player, cost, true, player.getPosition(), BossInstances.ZILYANA);
                    enter.set(true);
                });
                DialogueManager.start(player, -1);
            }).thirdOption("Never mind.", $ -> DialogueManager.start(player, -1)).start(player);
            return enter.get();
        });
        find("Saradomin").addRequirement(player -> {
            if (!player.getInventory().contains(ItemID.ECUMENICAL_KEY)) {
                return player.getAttributes().numInt(Attribute.SARADOMIN_KILL_COUNT) >= (PlayerUtil.isMember(player) ? 5 : 20);
            }
            return true;
        }, player -> player.sendMessage("You need a kill count of at least " + (PlayerUtil.isMember(player) ? 5 : 20) + " to enter to the boss chamber."));

        find("Saradomin").onOption("Peek", GodChamber.SARADOMIN::requestPlayerCount);
//        find("Saradomin").onOpen(player -> {
//            if (player.getAttributes().numInt(Attribute.SARADOMIN_KILL_COUNT) < 40) {
//                player.getInventory().delete(ItemID.ECUMENICAL_KEY, 1);
//            }
//        });

        find("Zamorak").onOpen(player -> {
            var cost = BossInstances.Companion.getCost(player);
            var enter = new AtomicBoolean(false);
            new DialogueBuilder(DialogueType.OPTION).firstOption("Enter the chamber.", $ -> {
                if (player.getAttributes().numInt(Attribute.BANDOS_KILL_COUNT) < (PlayerUtil.isMember(player) ? 5 : 20)) {
                    player.getInventory().delete(ItemID.ECUMENICAL_KEY, 1);
                }
                find("Zamorak").open(player);
                DialogueManager.start(player, -1);
                enter.set(true);
            }).secondOption("Create personal instance (" + Misc.formatWithAbbreviation2(cost) + ")", $ -> {
                if (!player.getInventory().contains(new Item(995, cost))) {
                    DialogueManager.sendStatement(player, "You do not have enough coins.");
                    return;
                }
                find("Zamorak").open(player);
                player.getTimerRepository().register(TimerKey.ATTACK_IMMUNITY, 6);
                TaskManager.submit(1, () -> {
                    BossInstances.Companion.createPersonalInstance(player, cost, true, player.getPosition(), BossInstances.KRIL);
                    enter.set(true);
                });
                DialogueManager.start(player, -1);
            }).thirdOption("Never mind.", $ -> DialogueManager.start(player, -1)).start(player);
            return enter.get();
        });

        find("Zamorak").addRequirement(player -> {
            if (!player.getInventory().contains(ItemID.ECUMENICAL_KEY)) {
                return player.getAttributes().numInt(Attribute.ZAMORAK_KILL_COUNT) >= (PlayerUtil.isMember(player) ? 5 : 20);
            }
            return true;
        }, player -> player.sendMessage("You need a kill count of at least " + (PlayerUtil.isMember(player) ? 5 : 20) + " to enter to the boss chamber."));

        find("Zamorak").onOption("Peek", GodChamber.ZAMORAK::requestPlayerCount);
//        find("Zamorak").onOpen(player -> {
//            if (player.getAttributes().numInt(Attribute.ZAMORAK_KILL_COUNT) < 40) {
//                player.getInventory().delete(ItemID.ECUMENICAL_KEY, 1);
//            }
//        });

        find("Armadyl").onOpen(player -> {
            var cost = BossInstances.Companion.getCost(player);
            var enter = new AtomicBoolean(false);
            new DialogueBuilder(DialogueType.OPTION).firstOption("Enter the chamber.", $ -> {
                if (player.getAttributes().numInt(Attribute.BANDOS_KILL_COUNT) < (PlayerUtil.isMember(player) ? 5 : 20)) {
                    player.getInventory().delete(ItemID.ECUMENICAL_KEY, 1);
                }
                find("Armadyl").open(player);
                DialogueManager.start(player, -1);
                enter.set(true);
            }).secondOption("Create personal instance (" + Misc.formatWithAbbreviation2(cost) + ")", $ -> {
                if (!player.getInventory().contains(new Item(995, cost))) {
                    DialogueManager.sendStatement(player, "You do not have enough coins.");
                    return;
                }
                find("Armadyl").open(player);
                player.getTimerRepository().register(TimerKey.ATTACK_IMMUNITY, 6);
                TaskManager.submit(1, () -> {
                    BossInstances.Companion.createPersonalInstance(player, cost, true, player.getPosition(), BossInstances.KREE);
                    enter.set(true);
                });
                DialogueManager.start(player, -1);
            }).thirdOption("Never mind.", $ -> DialogueManager.start(player, -1)).start(player);
            return enter.get();
        });

        find("Armadyl").addRequirement(player -> {
            if (!player.getInventory().contains(ItemID.ECUMENICAL_KEY)) {
                return player.getAttributes().numInt(Attribute.ARMADYL_KILL_COUNT) >= (PlayerUtil.isMember(player) ? 5 : 20);
            }
            return true;
        }, player -> player.sendMessage("You need a kill count of at least " + (PlayerUtil.isMember(player) ? 5 : 20) + " to enter to the boss chamber."));

        find("Armadyl").onOption("Peek", GodChamber.ARMADYL::requestPlayerCount);
//        find("Armadyl").onOpen(player -> {
//            if (player.getAttributes().numInt(Attribute.ARMADYL_KILL_COUNT) < 40) {
//                player.getInventory().delete(ItemID.ECUMENICAL_KEY, 1);
//            }
//        });
    }

    private static void guilds() {
        find("Woodcutting Guild").addSkillRequirement(Skill.WOODCUTTING, 60,
                playerFail -> playerFail.sendMessage("You need a Woodcutting level of 60 to enter the Woodcutting Guild."),
                playerSuccess -> {
                    if (playerSuccess.getArea() != AreaManager.WOODCUTTING_GUILD_AREA) {
                        new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.BERRY_7235).
                                setText("Welcome to the woodcutting guild, adventurer.").start(playerSuccess);
                    } else {
                        new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.BERRY_7235).
                                setText("Good Bye.").start(playerSuccess);
                    }
                });

        find("Mining Guild").addSkillRequirement(Skill.MINING, 60,
                playerFail -> playerFail.sendMessage("You need to have 60 Mining or higher to enter the Mining guild."));


        find("Fishing Guild").addSkillRequirement(Skill.FISHING, 68,
                playerFail -> playerFail.sendMessage("You need a level of at least 68 Fishing to enter the Fishing guild."));


        find("Cooks Guild").addSkillRequirement(Skill.COOKING, 32, player ->
                player.sendMessage("You need a at least 32 in Cooking skill to enter this guild."));

        find("Cooks Guild").addRequirement(player ->
                        player.getEquipment().contains(ItemID.CHEFS_HAT) ||
                                player.getEquipment().contains(ItemID.GOLDEN_CHEFS_HAT) ||
                                player.getEquipment().contains(ItemID.COOKING_CAPE) ||
                                player.getEquipment().contains(ItemID.COOKING_CAPE_2) ||
                                player.getEquipment().contains(ItemID.COOKING_CAPE_T_) ||
                                player.getEquipment().contains(ItemID.MAX_CAPE) || player.getEquipment().contains(ItemID.AVAS_MAX_CAPE) || player.getEquipment().contains(ItemID.ARDOUGNE_MAX_CAPE)
                                || player.getEquipment().contains(ItemID.FIRE_MAX_CAPE)
                                || player.getEquipment().contains(ItemID.FIRE_MAX_CAPE_2)
                                || player.getEquipment().contains(ItemID.GUTHIX_MAX_CAPE)
                                || player.getEquipment().contains(ItemID.INFERNAL_MAX_CAPE)
                                || player.getEquipment().contains(ItemID.SARADOMIN_MAX_CAPE)
                                || player.getEquipment().contains(ItemID.ZAMORAK_MAX_CAPE)
                                || player.getEquipment().contains(ItemID.MAX_CAPE_2)
                                || player.getEquipment().contains(ItemID.MAX_CAPE_3)
                                || player.getEquipment().contains(ItemID.MYTHICAL_MAX_CAPE),
                player -> new DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(NpcID.HEAD_CHEF)
                        .setText("You can't come in here unless you're wearing a chef's", "hat, or something like that.")
                        .start(player));


        find("Crafting Guild").addSkillRequirement(Skill.CRAFTING, 40, player ->
                player.sendMessage("You need level 40 in Crafting to enter the Crafting guild."));

        find("Crafting Guild").addRequirement(player ->
                        player.getEquipment().contains(ItemID.BROWN_APRON) ||
                                player.getEquipment().contains(ItemID.BROWN_APRON_2) ||
                                player.getEquipment().contains(ItemID.GOLDEN_APRON) ||
                                player.getEquipment().contains(ItemID.GOLDEN_APRON_2) ||
                                player.getEquipment().contains(ItemID.CRAFTING_CAPE) ||
                                player.getEquipment().contains(ItemID.CRAFTING_CAPE_2) ||
                                player.getEquipment().contains(ItemID.CRAFTING_CAPE_T_) ||
                                player.getEquipment().contains(ItemID.MAX_CAPE) || player.getEquipment().contains(ItemID.AVAS_MAX_CAPE) || player.getEquipment().contains(ItemID.ARDOUGNE_MAX_CAPE)
                                || player.getEquipment().contains(ItemID.FIRE_MAX_CAPE)
                                || player.getEquipment().contains(ItemID.FIRE_MAX_CAPE_2)
                                || player.getEquipment().contains(ItemID.GUTHIX_MAX_CAPE)
                                || player.getEquipment().contains(ItemID.INFERNAL_MAX_CAPE)
                                || player.getEquipment().contains(ItemID.SARADOMIN_MAX_CAPE)
                                || player.getEquipment().contains(ItemID.ZAMORAK_MAX_CAPE)
                                || player.getEquipment().contains(ItemID.MYTHICAL_MAX_CAPE)
                                || player.getEquipment().contains(ItemID.MAX_CAPE_2)
                                || player.getEquipment().contains(ItemID.MAX_CAPE_3),
                player -> new DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(NpcID.MASTER_CRAFTER)
                        .setText("Where's your brown apron? You can't come in here", "unless you're wearing one.")
                        .start(player));


        find("Magic Guild East").addSkillRequirement(Skill.MAGIC, 66,
                playerFail -> playerFail.sendMessage("You need a level of at least 66 Magic to enter the Magic guild."));
        find("Magic Guild West").addSkillRequirement(Skill.MAGIC, 66,
                playerFail -> playerFail.sendMessage("You need a level of at least 66 Magic to enter the Magic guild."));

        find("Warriors Guild Entrance").addRequirement(WarriorsGuild::hasRequirements,
                player -> new DialogueBuilder(DialogueType.STATEMENT).setText("You are not strong enough to enter this guild.",
                        "Perhaps you should train your attack and strength before coming back.").start(player));

        find("Warrior Guild Top Floor").addRequirement(WarriorsGuild::canEnterUpperRoom);

        find("Warrior Guild Bottom Floor").addRequirement(WarriorsGuild::enterBaseRoom);

        find("Ranging Guild").addSkillRequirement(Skill.RANGED, 40,
                player -> player.sendMessage("You need at least 40 Ranged to enter the Farming guild."));

        find("Farming Guild").addSkillRequirement(Skill.FARMING, 45,
                player -> player.sendMessage("You need at least 45 Farming to enter the Fcanarming guild."));

    }

    public static Passage find(String name) {
        var passage = PassageManager.lookup(name);
        if (passage == null) {
            System.err.println("Unable to find a passage with the name '" + name + "'.");
            return new Passage(PassageCategory.DOOR, null, null, PassageMode.FORCE, PassageType.SINGLE, PassageState.CLOSED);
        }
        return passage;
    }
}
