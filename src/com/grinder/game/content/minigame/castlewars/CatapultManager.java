package com.grinder.game.content.minigame.castlewars;

import com.grinder.game.World;
import com.grinder.game.content.minigame.Party;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.model.projectile.ProjectileTemplateBuilder;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

import java.util.stream.Stream;

import static com.grinder.game.content.minigame.castlewars.CastleWars.*;

public class CatapultManager {
    public static int[][] SARADOMIN_COORDINATES = { { 1, 6 }, { 1, 5 }, { 1, 5 }, { 1, 3 }, { 1, 2 }, { 1, 1 }, { 0, 5 }, { 0, 4 }, { 0, 3 }, { 0, 2 }, { 0, 1 }, { -1, 4 }, { -1, 3 }, { -1, 2 }, { -1, 1 }, { -1, 0 }, { -1, -1 }, { -2, 3 }, { -2, 2 }, { -2, 1 }, { -2, 0 }, { -2, -1 }, { -3, 3 }, { -3, 2 }, { -3, 1 }, { -3, 0 }, { -3, -1 }, { -4, 1 }, { -4, 0 }, { -4, -1 }, { -5, 0 }, { -5, -1 }, { -6, -1 }, };

    public static int[][] ZAMORAK_COORDINATES = { { -1, -1 }, { -1, -2 }, { -1, -3 }, { -1, -4 }, { -1, -5 }, { -1, -6 }, { 0, -1 }, { 0, -2 }, { 0, -3 }, { 0, -4 }, { 0, -5 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 1, -2 }, { 1, -3 }, { 1, -4 }, { 2, 1 }, { 2, 0 }, { 2, -1 }, { 2, -2 }, { 2, -3 }, { 3, 1 }, { 3, 0 }, { 3, -1 }, { 3, -2 }, { 3, -3 }, { 4, 1 }, { 4, 0 }, { 4, -1 }, { 5, 0 }, { 5, 1 }, { 6, 1 }, };


    private static long catapultTimer[] = new long[2];

    public static void operateCatapult(Party teamCatapult, Player player) {
        Party playerParty = player.getCurrentParty();

        if (!isCloseFromCatapult(teamCatapult, player)) {
            player.sendMessage("You can't reach that!");
            return;
        }

        if (playerParty != teamCatapult) {
            player.sendMessage("You can't use the other team's catapult");
            return;
        }

        if (playerParty != saradominParty && playerParty != zamorakParty) {
            return;
        }

        if (catapultState[playerParty.getTeamID()] != CatapultState.OPERATIONAL) {
            return;
        }

        updateCatapultInfo(player);
        player.totalShotCatapultDamage = 0;
        player.usingCatapult = true;

        player.setInterfaceId(11169);
        //InterfaceHandler.openNSOverlayTabInterface(player, playerParty == saradominParty ? 36 : 37);

    }

    public static void updateCatapultInfo(Player player) {
        Item ammo = player.getInventory().getById(CastleWarsConstants.ITEM_ROCK);
        int ammoCount = 0;
        if (ammo != null) {
            ammoCount = ammo.getAmount();
        }
        if (player.getCurrentParty() == saradominParty) {
            //StreamHandler.sendChildMessage(player, 36, 35, "Ammo: " + ammoCount);
        } else if (player.getCurrentParty() == zamorakParty) {
            //StreamHandler.sendChildMessage(player, 37, 35, "Ammo: " + ammoCount);
        }
    }

    public static void fireCatapult(Player player, int team, int buttonID) {

        if (!player.usingCatapult) {
            return;
        }
        Party playerParty = player.getCurrentParty();

        if (playerParty == null) {
            return;
        }

        if (!player.getInventory().containsAny(CastleWarsConstants.ITEM_ROCK)) {
            player.sendMessage("You need a rock to launch from the catapult");
            return;
        }

        if (playerParty == zamorakParty && team != CastleWarsConstants.ZAMORAK_TEAM) {
            return;
        }
        if (playerParty == saradominParty && team != CastleWarsConstants.SARADOMIN_TEAM) {
            return;
        }

        int playerTeam = playerParty.getTeamID();

        if (catapultState[playerTeam] != CatapultState.OPERATIONAL) {
            player.sendMessage("The catapult is not operational.");
            interruptCatapultUsage(player);
            return;
        }

        if (System.currentTimeMillis() - catapultTimer[playerTeam] < 4000) {
            player.sendMessage("The catapult is cooling down.");
            return;
        }

        if (buttonID < 2 && buttonID >= 34) {
            return;
        }

        catapultTimer[playerTeam] = System.currentTimeMillis();

        int coordinate = buttonID - 2;

        int shotX = playerTeam == 0 ? SARADOMIN_COORDINATES[coordinate][0] : ZAMORAK_COORDINATES[coordinate][0];
        int shotY = playerTeam == 0 ? SARADOMIN_COORDINATES[coordinate][1] : ZAMORAK_COORDINATES[coordinate][1];

        int sourceX = playerParty == saradominParty ? 2416 : 2383;
        int sourceY = playerParty == saradominParty ? 3090 : 3117;

        if (player.isWithinDistance(new Position(sourceX, sourceY), 4)) {
            return;
        }

        //		System.out.println("Source: " + sourceX + ", " + sourceY + " Shot: " + shotX +", " + shotY);
        int targetX = (int) (sourceX + ((float) shotX * 5F));
        int targetY = (int) (sourceY + ((float) shotY * 5F));

        //		player.sendMessage("Launching rock at: "+ targetX + ", " + targetY);

        int offsetX = targetX - (sourceX);
        int offsetY = targetY - (sourceY);

        player.getInventory().delete(CastleWarsConstants.ITEM_ROCK, 1);
        player.sendMessage("The catapult fires and...");
        updateCatapultInfo(player);

        if (playerParty == saradominParty) {
            offsetX += 2;
            player.getPacketSender().sendObjectAnimation(2413, 3088, 10, 0, new Animation(2129));
            //player.getPA().createPlayersProjectile(sourceX - 4, sourceY, offsetY, offsetX, 90, 120, 2690, 140, 10, 0, 40);
            final ProjectileTemplate template = new ProjectileTemplateBuilder(2690)
                    .setCurve(10)
                    .setStartHeight(90)
                    .setEndHeight(40)
                    .build();

            final Position source = new Position(sourceX, sourceY);
            final Position offset = new Position(sourceX+offsetX, sourceY+offsetY);
            final Projectile projectile = new Projectile(source, offset, template);
            projectile.sendProjectile();
        } else if (playerParty == zamorakParty) {
            offsetX -= 2;
            player.getPacketSender().sendObjectAnimation(2384, 3117, 10, 2, new Animation(2129));
            //player.getPA().createPlayersProjectile(sourceX, sourceY, offsetY, offsetX, 90, 120, 2690, 140, 10, 0, 40);

            final ProjectileTemplate template = new ProjectileTemplateBuilder(2690)
                    .setCurve(10)
                    .setStartHeight(90)
                    .setEndHeight(40)
                    .build();

            final Position source = new Position(sourceX, sourceY);
            final Position offset = new Position(sourceX+offsetX, sourceY+offsetY);
            final Projectile projectile = new Projectile(source, offset, template);
            projectile.sendProjectile();
        }
        TaskManager.submit(new Task(5) {
            int totalDamage = 0;
            int affectedPlayers = 0;
            @Override
            protected void execute() {
                Stream<Player> stream = Stream.concat(zamorakParty.getPlayers().stream(), saradominParty.getPlayers().stream());
                stream.forEach((enemy) -> {
                    if (enemy != null) {
                        if (enemy.inCastleWars() && isOpositeTeam(playerParty, enemy.getCurrentParty())) {
                            if (enemy.isWithinDistance(new Position(targetX, targetY), 2)) {

                                int damage = enemy.getSkills().getMaximumLevel(Skill.HITPOINTS) / 8;
                                player.getCombat().queue(new Damage(damage, DamageMask.REGULAR_HIT));
                                totalDamage += damage;
                                if (affectedPlayers++ < 2)
                                    player.sendMessage("It hit " + enemy.getUsername() + " for " + damage + " damage.");


                            }
                        }
                    }
                });

                if (affectedPlayers > 2) {
                    if (affectedPlayers >= 4) {
                        //player.getTask().finishTask(Tasks.LUCKY_SHOT);
                    }
                    affectedPlayers -= 2;
                    player.sendMessage("It hit " + affectedPlayers + (affectedPlayers > 1 ? " other" : " other") + ", totalling " + totalDamage + " damage!");
                }

                if (totalDamage <= 0) {
                    player.sendMessage("It didn't hit anyone.");
                } else {
                    player.totalShotCatapultDamage += totalDamage;
                    player.cwGameCatapultDamage += totalDamage;
                    player.castleWarsCatapultDamage += totalDamage;
                }

                //player.getPA().createPlayersStillGfx(2051, targetX, targetY, 0, 0);

            }
        });
    }

    public static void interruptCatapultUsage(Player c) {
        if (c.usingCatapult) {
            if (c.totalShotCatapultDamage > 0) {
                c.sendMessage("You dealt a total of " + c.totalShotCatapultDamage + " damage with the catapult.");
            }
            c.totalShotCatapultDamage = 0;
            c.usingCatapult = false;
            c.dispatchInterfaceClose();

        }
    }

    private static boolean manageCatapult(Player player, Party playerParty, int catapultID) {

        if (playerParty != saradominParty && playerParty != zamorakParty) {
            return false;
        }

        if (isTeamCatapult(zamorakParty, catapultID)) {
            if (!isCloseFromCatapult(zamorakParty, player)) {
                return false;
            }
        } else if (isTeamCatapult(saradominParty, catapultID)) {
            if (!isCloseFromCatapult(saradominParty, player)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isCloseFromCatapult(Party catapultTeam, Player player) {
        if (catapultTeam == zamorakParty) {
            if (player.getPosition().getX() == 2387 && player.getPosition().getY() == 3116) {
                return false;
            }
            if (player.isWithinDistance(new Position(2384, 3117), 2)) {
                return true;
            }
        } else if (catapultTeam == saradominParty) {
            if (player.getPosition().getX() == 2412 && player.getPosition().getY() == 3091) {
                return false;
            }
            if (player.isWithinDistance(new Position(2413, 3088), 2)) {
                return true;
            }
        }
        return false;
    }

    private static Party getCatapultTeam(int catapultID) {
        if (catapultID == CastleWarsConstants.OBJECT_SARADOMIN_CATAPULT || catapultID == CastleWarsConstants.OBJECT_SARADOMIN_BROKEN_CATAPULT || catapultID == CastleWarsConstants.OBJECT_SARADOMIN_BURNING_CATAPULT) {
            return saradominParty;
        } else if (catapultID == CastleWarsConstants.OBJECT_ZAMORAK_CATAPULT || catapultID == CastleWarsConstants.OBJECT_ZAMORAK_BROKEN_CATAPULT || catapultID == CastleWarsConstants.OBJECT_ZAMORAK_BURNING_CATAPULT) {
            return zamorakParty;
        }
        return null;
    }

    private static boolean isTeamCatapult(Party team, int catapultID) {
        Party catapultTeam = getCatapultTeam(catapultID);

        return catapultTeam == team;
    }

    public static void explodeCatapult(Player player, int catapultID, int itemSlot) {
        Party playerParty = player.getCurrentParty();

        if (!manageCatapult(player, playerParty, catapultID)) {
            return;
        }

        if (playerParty == getCatapultTeam(catapultID)) {
            player.sendMessage("You don't want to damage your own catapult.");
            return;
        }

        int catapultTeam = playerParty == saradominParty ? zamorakParty.getTeamID() : saradominParty.getTeamID();

        if (player.getInventory().get(itemSlot).getId() == CastleWarsConstants.ITEM_EXPLOSIVE) {

            player.getInventory().delete(new Item(CastleWarsConstants.ITEM_EXPLOSIVE, 1), itemSlot);

            catapultState[catapultTeam] = CatapultState.DESTROYED;
            setUpdateInterface(true);
            if (playerParty == saradominParty) {
                World.addObject(StaticGameObjectFactory.produce(CastleWarsConstants.OBJECT_ZAMORAK_BROKEN_CATAPULT, new Position(2384, 3117, 0), 10, 2));
            } else if (playerParty == zamorakParty) {
                World.addObject(StaticGameObjectFactory.produce(CastleWarsConstants.OBJECT_SARADOMIN_BROKEN_CATAPULT, new Position(2413, 3088, 0), 10, 0));
            }
        }


    }

    public static void burnCatapult(Player player, int catapultID) {
        Party playerParty = player.getCurrentParty();

        if (!manageCatapult(player, playerParty, catapultID)) {
            return;
        }

        Party catapultTeam = getCatapultTeam(catapultID);
        if (playerParty == catapultTeam) {
            player.sendMessage("You don't want to damage your own catapult.");
            return;
        }

        int teamID = catapultTeam.getTeamID();

        if (player.getInventory().contains(CastleWarsConstants.ITEM_TINDERBOX)) {

            catapultState[teamID] = CatapultState.BURNING;

            if (catapultTeam == zamorakParty) {
                GameObject burningCatapult = StaticGameObjectFactory.produce(CastleWarsConstants.OBJECT_ZAMORAK_BURNING_CATAPULT, new Position(2384, 3117, 0), 10, 2);

                GameObject brokenCatapult = StaticGameObjectFactory.produce(CastleWarsConstants.OBJECT_ZAMORAK_BROKEN_CATAPULT, new Position(2384, 3117, 0), 10, 2);
                TaskManager.submit(9, () -> {

                    if (catapultState[teamID] == CatapultState.BURNING) {
                        World.addObject(brokenCatapult);
                        catapultState[teamID] = CatapultState.DESTROYED;
                        setUpdateInterface(true);
                    }
                });
                World.addObject(burningCatapult);

            } else if (catapultTeam == saradominParty) {

                GameObject burningCatapult = StaticGameObjectFactory.produce(CastleWarsConstants.OBJECT_SARADOMIN_BURNING_CATAPULT, new Position(2413, 3088, 0), 10, 0);

                GameObject brokenCatapult = StaticGameObjectFactory.produce(CastleWarsConstants.OBJECT_SARADOMIN_BROKEN_CATAPULT, new Position(2413, 3088, 0), 10, 0);
                TaskManager.submit(9, () -> {

                    if (catapultState[teamID] == CatapultState.BURNING) {
                        World.addObject(brokenCatapult);
                        catapultState[teamID] = CatapultState.DESTROYED;
                        setUpdateInterface(true);
                    }
                });
                World.addObject(burningCatapult);
            }
        }

    }

    public static void extinguishCatapultFire(Player player, int catapultID, int itemSlot) {
        Party playerParty = player.getCurrentParty();

        if (!manageCatapult(player, playerParty, catapultID)) {
            return;
        }

        if (playerParty != getCatapultTeam(catapultID)) {
            player.sendMessage("You can't extinguish the enemy catapult.");
            return;
        }

        int teamID = playerParty.getTeamID();

        if (catapultState[teamID] != CatapultState.BURNING) {
            return;
        }

        if (player.getInventory().get(itemSlot).getId() == CastleWarsConstants.ITEM_BUCKET_OF_WATER) {

            player.getInventory().get(itemSlot).setId(CastleWarsConstants.ITEM_BUCKET);
            player.getInventory().refreshItems();

            catapultState[teamID] = CatapultState.OPERATIONAL;

            if (playerParty == saradominParty) {
                StaticGameObjectFactory.produce(CastleWarsConstants.OBJECT_SARADOMIN_CATAPULT, new Position(2413, 3088, 0), 10, 0);
            } else if (playerParty == zamorakParty) {
                StaticGameObjectFactory.produce(CastleWarsConstants.OBJECT_ZAMORAK_CATAPULT, new Position(2384, 3117, 0), 10, 2);
            }
            setUpdateInterface(true);
        }

    }

    public static void repairCatapult(Player player, int catapultID) {
        Party playerParty = player.getCurrentParty();

        if (!manageCatapult(player, playerParty, catapultID)) {
            return;
        }

        if (playerParty != getCatapultTeam(catapultID)) {
            player.sendMessage("You don't want to repair your opponents catapult.");
            return;
        }

        int teamID = playerParty.getTeamID();

        if (catapultState[teamID] == CatapultState.OPERATIONAL) {
            return;
        }

        if (!player.getInventory().contains(CastleWarsConstants.ITEM_TOOLKIT)) {
            player.sendMessage("You need a toolkit to repair the catapult.");
            return;
        }

        setCatapultRepaired(playerParty);
    }

    public static void setCatapultRepaired(Party team) {
        if (team == saradominParty || team == zamorakParty) {
            catapultState[team.getTeamID()] = CastleWars.CatapultState.OPERATIONAL;

            if (team == saradominParty) {
                World.addObject(StaticGameObjectFactory.produce(CastleWarsConstants.OBJECT_SARADOMIN_CATAPULT, new Position(2413, 3088, 0), 10, 0));
            } else if (team == zamorakParty) {
                World.addObject(StaticGameObjectFactory.produce(CastleWarsConstants.OBJECT_ZAMORAK_CATAPULT, new Position(2384, 3117, 0), 10, 2));
            }
            setUpdateInterface(true);
        }
    }
}
