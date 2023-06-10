package com.grinder.game.entity.agent.combat.attack.weapon.magic;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.minigame.warriorsguild.WarriorGuildNpc;
import com.grinder.game.content.skill.skillable.impl.magic.CastSpellAnimation;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType;
import com.grinder.game.entity.agent.combat.event.impl.BindEvent;
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.MonsterRace;
import com.grinder.game.entity.agent.npc.monster.boss.impl.CallistoBoss;
import com.grinder.game.entity.agent.npc.monster.boss.impl.VenenatisBoss;
import com.grinder.game.entity.agent.npc.monster.boss.impl.VetionBoss;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.model.*;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.model.sound.Sounds;
import com.grinder.util.DistanceUtil;
import com.grinder.util.ItemID;
import com.grinder.util.Priority;
import com.grinder.util.timing.TimerKey;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public enum CombatSpellType {

    WIND_STRIKE(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.WIND_STRIKE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_STRIKE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 91, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_STRIKE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_STRIKE_CONTACT);
            return Optional.of(new Graphic(92, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(90, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 5;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556), new Item(558) });
        }

        @Override
        public int levelRequired() {
            return 1;
        }

        @Override
        public int spellId() {
            return 1152;
        }
    }),
    CONFUSE(new CombatEffectSpell(false) {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.EFFECTIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.CONFUSE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.CONFUSE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 103, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public void spellEffect(Agent cast, Agent castOn) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkillManager().getCurrentLevel(Skill.ATTACK) < player.getSkillManager().getMaxLevel(Skill.ATTACK)) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketSender().sendMessage(
                                "The spell has no effect because the player has already been weakened.");
                    }
                    return;
                }
                int decrease = (int) (0.05 * (player.getSkillManager().getCurrentLevel(Skill.ATTACK)));
                player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkillManager().getCurrentLevel(Skill.ATTACK) - decrease, true);
                player.getSkillManager().updateSkill(Skill.ATTACK);

                player.getPacketSender().sendMessage(
                        "You feel slightly weakened.");
            } /*else if (castOn.isNpc()) {
				NPC npc = (NPC) castOn;

				if (npc.getDefenceWeakened()[0] || npc.getStrengthWeakened()[0]) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the NPC has already been weakened.");
					}
					return;
				}

				npc.getDefenceWeakened()[0] = true;
			}*/
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.CONFUSE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.CONFUSE_CONTACT);
            return Optional.of(new Graphic(104, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(102, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 13;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(555, 3), new Item(557, 2), new Item(559) });
        }

        @Override
        public int levelRequired() {
            return 3;
        }

        @Override
        public int spellId() {
            return 1153;
        }
    }),
    WATER_STRIKE(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.WATER_STRIKE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_STRIKE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 94, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_STRIKE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_STRIKE_CONTACT);
            return Optional.of(new Graphic(95, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(93, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 7;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(555), new Item(556), new Item(558) });
        }

        @Override
        public int levelRequired() {
            return 5;
        }

        @Override
        public int spellId() {
            return 1154;
        }
    }),
    EARTH_STRIKE(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.EARTH_STRIKE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_STRIKE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 97, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_STRIKE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_STRIKE_CONTACT);
            return Optional.of(new Graphic(98, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(96, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 9;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 1), new Item(558, 1), new Item(557, 2) });
        }

        @Override
        public int levelRequired() {
            return 9;
        }

        @Override
        public int spellId() {
            return 1156;
        }
    }),
    WEAKEN(new CombatEffectSpell(false) {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.EFFECTIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.WEAKEN_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WEAKEN_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 106, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public void spellEffect(Agent cast, Agent castOn) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkillManager().getCurrentLevel(Skill.STRENGTH) < player.getSkillManager().getMaxLevel(Skill.STRENGTH)) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketSender().sendMessage(
                                "The spell has no effect because the player has already been weakened.");
                    }
                    return;
                }

                int decrease = (int) (0.05 * (player.getSkillManager().getCurrentLevel(Skill.STRENGTH)));
                player.getSkillManager().setCurrentLevel(Skill.STRENGTH, player.getSkillManager().getCurrentLevel(Skill.STRENGTH) - decrease, true);
                player.getSkillManager().updateSkill(Skill.STRENGTH);
                player.getPacketSender().sendMessage(
                        "You feel slightly weakened.");
            } /*else if (castOn.isNpc()) {
				NPC npc = (NPC) castOn;

				if (npc.getDefenceWeakened()[1] || npc.getStrengthWeakened()[1]) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the NPC has already been weakened.");
					}
					return;
				}

				npc.getDefenceWeakened()[1] = true;
			}*/
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WEAKEN_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WEAKEN_CONTACT);
            return Optional.of(new Graphic(107, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(105, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 21;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(555, 3), new Item(557, 2), new Item(559, 1) });
        }

        @Override
        public int levelRequired() {
            return 11;
        }

        @Override
        public int spellId() {
            return 1157;
        }
    }),
    FIRE_STRIKE(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.FIRE_STRIKE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_STRIKE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 100, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_STRIKE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_STRIKE_CONTACT);
            return Optional.of(new Graphic(101, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(99, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 11;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 1), new Item(558, 1), new Item(554, 3) });
        }

        @Override
        public int levelRequired() {
            return 13;
        }

        @Override
        public int spellId() {
            return 1158;
        }
    }),
    WIND_BOLT(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.WIND_BOLT_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_BOLT_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 118, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_BOLT_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_BOLT_CONTACT);
            return Optional.of(new Graphic(119, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(117, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 13;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 2), new Item(562, 1) });
        }

        @Override
        public int levelRequired() {
            return 17;
        }

        @Override
        public int spellId() {
            return 1160;
        }
    }),
    CURSE(new CombatEffectSpell(false) {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE_2.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.CURSE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.CURSE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 109, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public void spellEffect(Agent cast, Agent castOn) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkillManager().getCurrentLevel(Skill.DEFENCE) < player.getSkillManager().getMaxLevel(Skill.DEFENCE)) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketSender().sendMessage(
                                "The spell has no effect because the player has already been weakened.");
                    }
                    return;
                }

                int decrease = (int) (0.05 * (player.getSkillManager().getCurrentLevel(Skill.DEFENCE)));
                player.getSkillManager().setCurrentLevel(Skill.DEFENCE, player.getSkillManager().getCurrentLevel(Skill.DEFENCE) - decrease, true);
                player.getSkillManager().updateSkill(Skill.DEFENCE);

                player.getPacketSender().sendMessage(
                        "You feel slightly weakened.");
            }/* else if (castOn.isNpc()) {
				NPC npc = (NPC) castOn;

				if (npc.getDefenceWeakened()[2] || npc.getStrengthWeakened()[2]) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the NPC has already been weakened.");
					}
					return;
				}

				npc.getDefenceWeakened()[2] = true;
			}*/
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.CURSE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.CURSE_CONTACT);
            return Optional.of(new Graphic(110, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(108, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 29;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(555, 2), new Item(557, 3), new Item(559, 1) });
        }

        @Override
        public int levelRequired() {
            return 19;
        }

        @Override
        public int spellId() {
            return 1161;
        }
    }),
    BIND(new CombatEffectSpell(false) {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE_2.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.BIND_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.BIND_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 178, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public void spellEffect(Agent cast, Agent castOn) {
            if (castOn != null && castOn.isNpc())
                castOn.getCombat().submit(new BindEvent(cast, 8, 5));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.BIND_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.BIND_CONTACT);
            return Optional.of(new Graphic(181, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(177, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(555, 3), new Item(557, 3), new Item(561, 2) });
        }

        @Override
        public int levelRequired() {
            return 20;
        }

        @Override
        public int spellId() {
            return 1572;
        }
    }),
    WATER_BOLT(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.WATER_BOLT_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_BOLT_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 121, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_BOLT_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_BOLT_CONTACT);
            return Optional.of(new Graphic(122, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(120, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 16;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 2), new Item(562, 1), new Item(555, 2) });
        }

        @Override
        public int levelRequired() {
            return 23;
        }

        @Override
        public int spellId() {
            return 1163;
        }
    }),
    EARTH_BOLT(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.EARTH_BOLT_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_BOLT_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 124, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_BOLT_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_BOLT_CONTACT);
            return Optional.of(new Graphic(125, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(123, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 19;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 2), new Item(562, 1), new Item(557, 3) });
        }

        @Override
        public int levelRequired() {
            return 29;
        }

        @Override
        public int spellId() {
            return 1166;
        }
    }),
    FIRE_BOLT(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_BOLT_CAST);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_BOLT_CAST);
            return Optional.of(new Projectile(cast, castOn, 127, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_BOLT_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_BOLT_CONTACT);
            return Optional.of(new Graphic(128, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(126, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 22;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 3), new Item(562, 1), new Item(554, 4) });
        }

        @Override
        public int levelRequired() {
            return 35;
        }

        @Override
        public int spellId() {
            return 1169;
        }
    }),
    CRUMBLE_UNDEAD(new CombatNormalSpell() {

        @Override
        public boolean canCast(Player player, Agent target, boolean deleteRunes) {

            if(target instanceof NPC){

                final NPC npcTarget = (NPC) target;

                if(MonsterRace.Companion.isRace(npcTarget, MonsterRace.UNDEAD))
                    return super.canCast(player, target, deleteRunes);
            }

            player.sendMessage("This spell only affects skeletons, zombies, ghosts and shades.");
            player.getCombat().reset(false);
            player.getMotion().clearSteps();
            player.getMotion().cancelTask();
            player.setEntityInteraction(null);
            return false;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.CRUMBLE_UNDEAD.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.CRUMBLE_UNDEAD_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.CRUMBLE_UNDEAD_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 146, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.CRUMBLE_UNDEAD_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.CRUMBLE_UNDEAD_CONTACT);
            return Optional.of(new Graphic(147, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(145));
        }

        @Override
        public int baseExperience() {
            return 24;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 2), new Item(562, 1), new Item(557, 2) });
        }

        @Override
        public int levelRequired() {
            return 39;
        }

        @Override
        public int spellId() {
            return 1171;
        }
    }),
    WIND_BLAST(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.WIND_BLAST_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_BLAST_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 133, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_BLAST_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_BLAST_CONTACT);
            return Optional.of(new Graphic(134, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(132, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 25;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 3), new Item(560, 1) });
        }

        @Override
        public int levelRequired() {
            return 41;
        }

        @Override
        public int spellId() {
            return 1172;
        }
    }),
    WATER_BLAST(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.WATER_BLAST_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_BLAST_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 136, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_BLAST_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_BLAST_CONTACT);
            return Optional.of(new Graphic(137, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(135, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 28;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(555, 3), new Item(556, 3), new Item(560, 1) });
        }

        @Override
        public int levelRequired() {
            return 47;
        }

        @Override
        public int spellId() {
            return 1175;
        }
    }),
    IBAN_BLAST(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(708));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.IBANS_BLAST_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.IBANS_BLAST_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 88, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.IBANS_BLAST_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.IBANS_BLAST_CONTACT);
            return Optional.of(new Graphic(89));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(87));
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.of(new Item[] { new Item(1409) });
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(560, 1), new Item(554, 5) });
        }

        @Override
        public int levelRequired() {
            return 50;
        }

        @Override
        public int spellId() {
            return 1539;
        }
    }),
    SNARE(new CombatEffectSpell(true) {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE_2.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.SNARE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SNARE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 178, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public void spellEffect(Agent cast, Agent castOn) {
            if (castOn != null && castOn.isNpc())
                castOn.getCombat().submit(new BindEvent(cast, 16, 5));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SNARE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SNARE_CONTACT);
            return Optional.of(new Graphic(180, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(177, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 60;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(555, 3), new Item(557, 4), new Item(561, 3) });
        }

        @Override
        public int levelRequired() {
            return 50;
        }

        @Override
        public int spellId() {
            return 1582;
        }
    }),
    MAGIC_DART(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1576));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.MAGIC_DART_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.MAGIC_DART_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 328, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.MAGIC_DART_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.MAGIC_DART_CONTACT);
            return Optional.of(new Graphic(329));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(327));
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.of(new Item[] { new Item(4170), new Item(21255), new Item(22296), new Item(11791), new Item(12904), new Item(12902), new Item(8841) });
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(558, 4), new Item(560, 1) });
        }

        @Override
        public int levelRequired() {
            return 50;
        }

        @Override
        public int spellId() {
            return 12037;
        }
    }),
    EARTH_BLAST(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.EARTH_BLAST_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_BLAST_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 139, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_BLAST_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_BLAST_CONTACT);
            return Optional.of(new Graphic(140, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(138, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 31;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 3), new Item(560, 1), new Item(557, 4) });
        }

        @Override
        public int levelRequired() {
            return 53;
        }

        @Override
        public int spellId() {
            return 1177;
        }
    }),
    FIRE_BLAST(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_BLAST_CAST);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_BLAST_CAST);
            return Optional.of(new Projectile(cast, castOn, 130, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_BLAST_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_BLAST_CONTACT);
            return Optional.of(new Graphic(131, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(129, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 34;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 4), new Item(560, 1), new Item(554, 5) });
        }

        @Override
        public int levelRequired() {
            return 59;
        }

        @Override
        public int spellId() {
            return 1181;
        }
    }),
    SARADOMIN_STRIKE(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(811));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.SARADOMIN_STRIKE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SARADOMIN_STRIKE_CAST);
            }
            return Optional.empty();
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SARADOMIN_STRIKE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SARADOMIN_STRIKE_CONTACT);
            return Optional.of(new Graphic(76));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 35;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.of(new Item[] { new Item(2415), new Item(ItemID.STAFF_OF_LIGHT) });
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 4), new Item(565, 2), new Item(554, 2) });
        }

        @Override
        public int levelRequired() {
            return 60;
        }

        @Override
        public int spellId() {
            return 1190;
        }
    }),
    CLAWS_OF_GUTHIX(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(811));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.CLAWS_OF_GUTHIX_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.CLAWS_OF_GUTHIX_CAST);
            }
            return Optional.empty();
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.CLAWS_OF_GUTHIX_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.CLAWS_OF_GUTHIX_CONTACT);
            return Optional.of(new Graphic(77));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 35;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.of(new Item[] { new Item(2416), new Item(24144) });
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 4), new Item(565, 2), new Item(554, 2) });
        }

        @Override
        public int levelRequired() {
            return 60;
        }

        @Override
        public int spellId() {
            return 1191;
        }
    }),
    FLAMES_OF_ZAMORAK(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(811));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.FLAMES_OF_ZAMORAK_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FLAMES_OF_ZAMORAK_CAST);
            }
            return Optional.empty();
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FLAMES_OF_ZAMORAK_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FLAMES_OF_ZAMORAK_CONTACT);
            return Optional.of(new Graphic(78));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 35;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.of(new Item[] { new Item(2417), new Item(11791), new Item(12904), new Item(ItemID.STAFF_OF_LIGHT)});
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 4), new Item(565, 2), new Item(554, 2) });
        }

        @Override
        public int levelRequired() {
            return 60;
        }

        @Override
        public int spellId() {
            return 1192;
        }
    }),
    WIND_WAVE(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE_3.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.WIND_WAVE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_WAVE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 159, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_WAVE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_WAVE_CONTACT);
            return Optional.of(new Graphic(160, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(158, GraphicHeight.MIDDLE));
        }

        @Override
        public int baseExperience() {
            return 36;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 5), new Item(565, 1) });
        }

        @Override
        public int levelRequired() {
            return 62;
        }

        @Override
        public int spellId() {
            return 1183;
        }
    }),
    WATER_WAVE(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE_3.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.WATER_WAVE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_WAVE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 162, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_WAVE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_WAVE_CONTACT);
            return Optional.of(new Graphic(163, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(161, GraphicHeight.MIDDLE));
        }

        @Override
        public int baseExperience() {
            return 37;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 5), new Item(565, 1), new Item(555, 7) });
        }

        @Override
        public int levelRequired() {
            return 65;
        }

        @Override
        public int spellId() {
            return 1185;
        }
    }),
    VULNERABILITY(new CombatEffectSpell(false) {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.EFFECTIVE_2.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.VULNERABILITY_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.VULNERABILITY_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 168, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public void spellEffect(Agent cast, Agent castOn) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkillManager().getCurrentLevel(Skill.DEFENCE) < player.getSkillManager().getMaxLevel(Skill.DEFENCE)) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketSender().sendMessage(
                                "The spell has no effect because the player is already weakened.");
                    }
                    return;
                }

                int decrease = (int) (0.10 * (player.getSkillManager().getCurrentLevel(Skill.DEFENCE)));
                player.getSkillManager().setCurrentLevel(Skill.DEFENCE, player.getSkillManager().getCurrentLevel(Skill.DEFENCE) - decrease, true);
                player.getSkillManager().updateSkill(Skill.DEFENCE);
                player.getPacketSender().sendMessage(
                        "You feel slightly weakened.");
            }/* else if (castOn.isNpc()) {
				NPC npc = (NPC) castOn;

				if (npc.getDefenceWeakened()[2] || npc.getStrengthWeakened()[2]) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the NPC is already weakened.");
					}
					return;
				}

				npc.getStrengthWeakened()[2] = true;
			}*/
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.VULNERABILITY_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.VULNERABILITY_CONTACT);
            return Optional.of(new Graphic(169));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(167));
        }

        @Override
        public int baseExperience() {
            return 76;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(557, 5), new Item(555, 5), new Item(566, 1) });
        }

        @Override
        public int levelRequired() {
            return 66;
        }

        @Override
        public int spellId() {
            return 1542;
        }
    }),
    EARTH_WAVE(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE_3.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.EARTH_WAVE_CAST);
            } else if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_WAVE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 165, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_WAVE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_WAVE_CONTACT);
            return Optional.of(new Graphic(166, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(164, GraphicHeight.MIDDLE));
        }

        @Override
        public int baseExperience() {
            return 40;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 5), new Item(565, 1), new Item(557, 7) });
        }

        @Override
        public int levelRequired() {
            return 70;
        }

        @Override
        public int spellId() {
            return 1188;
        }
    }),
    ENFEEBLE(new CombatEffectSpell(false) {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.EFFECTIVE_2.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.ENFEEBLE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ENFEEBLE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 171, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public void spellEffect(Agent cast, Agent castOn) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkillManager().getCurrentLevel(Skill.STRENGTH) < player.getSkillManager().getMaxLevel(Skill.STRENGTH)) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketSender().sendMessage(
                                "The spell has no effect because the player is already weakened.");
                    }
                    return;
                }

                int decrease = (int) (0.10 * (player.getSkillManager().getCurrentLevel(Skill.STRENGTH)));
                player.getSkillManager().setCurrentLevel(Skill.STRENGTH, player.getSkillManager().getCurrentLevel(Skill.STRENGTH) - decrease, true);
                player.getSkillManager().updateSkill(Skill.STRENGTH);
                player.getPacketSender().sendMessage("You feel slightly weakened.");
            }
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ENFEEBLES_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ENFEEBLES_CONTACT);
            return Optional.of(new Graphic(172));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(170));
        }

        @Override
        public int baseExperience() {
            return 83;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(557, 8), new Item(555, 8), new Item(566, 1) });
        }

        @Override
        public int levelRequired() {
            return 73;
        }

        @Override
        public int spellId() {
            return 1543;
        }
    }),
    FIRE_WAVE(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE_3.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.FIRE_WAVE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_WAVE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 156, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_WAVE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_WAVE_CONTACT);
            return Optional.of(new Graphic(157, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(155, GraphicHeight.MIDDLE, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 42;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 5), new Item(565, 1), new Item(554, 7) });
        }

        @Override
        public int levelRequired() {
            return 75;
        }

        @Override
        public int spellId() {
            return 1189;
        }
    }),
    ENTANGLE(new CombatEffectSpell(true) {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE_2.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.ENTANGLE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ENTANGLE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 178, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public void spellEffect(Agent cast, Agent castOn) {
            if (castOn != null && castOn.isNpc())
                castOn.getCombat().submit(new BindEvent(cast, 24, 5));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ENTANGLE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ENTANGLE_CONTACT);
            return Optional.of(new Graphic(179, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(177, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 91;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(555, 5), new Item(557, 5), new Item(561, 4) });
        }

        @Override
        public int levelRequired() {
            return 79;
        }

        @Override
        public int spellId() {
            return 1592;
        }
    }),
    STUN(new CombatEffectSpell(false) {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.EFFECTIVE_2.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.STUN_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.STUN_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 174, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public void spellEffect(Agent cast, Agent castOn) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkillManager().getCurrentLevel(Skill.ATTACK) < player.getSkillManager().getMaxLevel(Skill.ATTACK)) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketSender().sendMessage(
                                "The spell has no effect because the player is already weakened.");
                    }
                    return;
                }

                int decrease = (int) (0.10 * (player.getSkillManager().getCurrentLevel(Skill.ATTACK)));
                player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkillManager().getCurrentLevel(Skill.ATTACK) - decrease, true);
                player.getSkillManager().updateSkill(Skill.ATTACK);
                player.getPacketSender().sendMessage(
                        "You feel slightly weakened.");
            }/* else if (castOn.isNpc()) {
				NPC npc = (NPC) castOn;

				if (npc.getDefenceWeakened()[0] || npc.getStrengthWeakened()[0]) {
					if (cast.isPlayer()) {
						((Player) cast).getPacketSender().sendMessage(
								"The spell has no effect because the NPC is already weakened.");
					}
					return;
				}

				npc.getStrengthWeakened()[0] = true;
			}*/
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.STUN_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.STUN_CONTACT);
            return Optional.of(new Graphic(107));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(173));
        }

        @Override
        public int baseExperience() {
            return 90;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(557, 12), new Item(555, 12), new Item(556, 1) });
        }

        @Override
        public int levelRequired() {
            return 80;
        }

        @Override
        public int spellId() {
            return 1562;
        }
    }),
    WIND_SURGE(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_STRIKE_CAST);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_STRIKE_CAST);
            return Optional.of(new Projectile(cast, castOn, 1456, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_SURGE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WIND_SURGE_CONTACT);
            return Optional.of(new Graphic(1454, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(1455, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 44;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 7), new Item(21880, 1) });
        }

        @Override
        public int levelRequired() {
            return 81;
        }

        @Override
        public int spellId() {
            return 30791;
        }
    }),
    WATER_SURGE(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_SURGE_CAST);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_SURGE_CAST);
            return Optional.of(new Projectile(cast, castOn, 1459, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_SURGE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_SURGE_CONTACT);
            return Optional.of(new Graphic(1460, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(1458, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 46;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 7), new Item(555, 10), new Item(21880, 1) });
        }

        @Override
        public int levelRequired() {
            return 85;
        }

        @Override
        public int spellId() {
            return 30794;
        }
    }),
    TELEBLOCK(new CombatEffectSpell(false) {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1819));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.TELE_BLOCK_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.TELE_BLOCK_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 344, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public void spellEffect(Agent cast, Agent castOn) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (!player.getCombat().getTeleBlockTimer().finished()) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketSender().sendMessage(
                                "The spell has no effect because the player is already teleblocked.");
                    }
                    return;
                }

                final int seconds = player.getPrayerActive()[PrayerHandler.PROTECT_FROM_MAGIC] ? 150 : 300;

                player.getCombat().getTeleBlockTimer().start(seconds);
                player.getPacketSender().sendEffectTimer(seconds, EffectTimer.TELE_BLOCK);

                AchievementManager.processFor(AchievementType.TROUBLE_BLOCKED, player);
                player.sendMessage(player.getPrayerActive()[PrayerHandler.PROTECT_FROM_MAGIC]  ? "<col=4f006f>A teleblock spell has been cast on you. It will expire in 2.5 minutes." : "<col=4f006f>A teleblock spell has been cast on you. It will expire in 5 minutes.");

            } else if (castOn.isNpc()) {
                if (cast.isPlayer()) {
                    ((Player) cast).getPacketSender().sendMessage(
                            "Your spell has no effect on this target.");
                }
            }
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.TELE_BLOCK_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.TELE_BLOCK_CONTACT);
            return Optional.of(new Graphic(345));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 65;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(563, 1), new Item(562, 1), new Item(560, 1) });
        }

        @Override
        public int levelRequired() {
            return 85;
        }

        @Override
        public int spellId() {
            return 12445;
        }
    }),
    EARTH_SURGE(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_SURGE_CAST);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_SURGE_CAST);
            return Optional.of(new Projectile(cast, castOn, 1462, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_SURGE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.EARTH_SURGE_CONTACT);
            return Optional.of(new Graphic(1463, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(1461, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 48;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 7), new Item(557, 10), new Item(21880, 1) });
        }

        @Override
        public int levelRequired() {
            return 90;
        }

        @Override
        public int spellId() {
            return 30797;
        }
    }),
    FIRE_SURGE(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_SURGE_CAST);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_SURGE_CAST);
            return Optional.of(new Projectile(cast, castOn, 1465, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_SURGE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_SURGE_CONTACT);
            return Optional.of(new Graphic(1466, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(1464, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 51;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 7), new Item(554, 10), new Item(21880, 1) });
        }

        @Override
        public int levelRequired() {
            return 95;
        }

        @Override
        public int spellId() {
            return 30799;
        }
    }),
    DEMON_AGONY(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE_4.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.FIRE_WAVE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_WAVE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 1227, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_WAVE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_WAVE_CONTACT);
            return Optional.of(new Graphic(1247, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(1226, GraphicHeight.MIDDLE));
        }

        @Override
        public int baseExperience() {
            return 65;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.of(new Item[] { new Item(15298), new Item(23854)});
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(ItemID.SOUL_RUNE, 30), new Item(ItemID.ASTRAL_RUNE, 30), new Item(21880, 10) });
        }

        @Override
        public int levelRequired() {
            return 96;
        }

        @Override
        public int spellId() {
            return 34502;
        }
    }),
    UNDEAD_BASH(new CombatNormalSpell() {
        @Override
        public boolean canCast(Player player, Agent target, boolean deleteRunes) {

            if(target instanceof NPC){

                final NPC npcTarget = (NPC) target;

                if(MonsterRace.Companion.isRace(npcTarget, MonsterRace.UNDEAD))
                    return super.canCast(player, target, deleteRunes);
            }

            player.sendMessage("This spell only affects skeletons, zombies, ghosts and shades.");
            player.getCombat().reset(false);
            player.getMotion().clearSteps();
            player.getMotion().cancelTask();
            player.setEntityInteraction(null);
            return false;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE_4.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.CRUMBLE_UNDEAD_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.CRUMBLE_UNDEAD_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 1274, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.CRUMBLE_UNDEAD_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.CRUMBLE_UNDEAD_CONTACT);
            return Optional.of(new Graphic(305, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(1009, GraphicHeight.MIDDLE));
        }

        @Override
        public int baseExperience() {
            return 58;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(ItemID.SOUL_RUNE, 15), new Item(ItemID.COSMIC_RUNE, 15), new Item(21880, 5) });
        }

        @Override
        public int levelRequired() {
            return 92;
        }

        @Override
        public int spellId() {
            return 34501;
        }
    }),
    SMOKE_RUSH(new CombatAncientSpell() {

        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            PoisonEffect.applyPoisonTo(castOn, PoisonType.MILD);
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1978));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.SMOKE_RUSH_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_RUSH_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 384, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 0, 11, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_RUSH_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_RUSH_CONTACT);
            return Optional.of(new Graphic(385));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 1), new Item(554, 1), new Item(562, 2), new Item(560, 2) });
        }

        @Override
        public int levelRequired() {
            return 50;
        }

        @Override
        public int spellId() {
            return 12939;
        }
    }),
    SHADOW_RUSH(new CombatAncientSpell() {
        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkillManager().getCurrentLevel(Skill.ATTACK) < player.getSkillManager().getMaxLevel(Skill.ATTACK)) {
                    return;
                }

                int decrease = (int) (0.1 * (player.getSkillManager().getCurrentLevel(Skill.ATTACK)));
                player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkillManager().getCurrentLevel(Skill.ATTACK) - decrease, true);
                player.getSkillManager().updateSkill(Skill.ATTACK);
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1978));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.SHADOW_RUSH_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SHADOW_RUSH_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 378, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SHADOW_RUSH_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SHADOW_RUSH_CONTACT);
            return Optional.of(new Graphic(379));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 31;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 1), new Item(566, 1), new Item(562, 2), new Item(560, 2) });
        }

        @Override
        public int levelRequired() {
            return 52;
        }

        @Override
        public int spellId() {
            return 12987;
        }
    }),
    BLOOD_RUSH(new CombatAncientSpell() {
        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            final Player player = cast.getAsPlayer();
            final int heal = (int) Math.round(damage * 0.10);
            if (player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) < player.getSkillManager().getMaxLevel(Skill.HITPOINTS)) {
                int level = player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) + heal > player.getSkillManager().getMaxLevel(Skill.HITPOINTS) ? player.getSkillManager().getMaxLevel(Skill.HITPOINTS) : player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) + heal;
                player.getSkillManager().setCurrentLevel(Skill.HITPOINTS, level, true);
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1978));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.BLOOD_RUSH_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.BLOOD_RUSH_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 372, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 0, 11, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.BLOOD_RUSH_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.BLOOD_RUSH_CONTACT);
            return Optional.of(new Graphic(373));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 33;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(565, 1), new Item(562, 2), new Item(560, 2) });
        }

        @Override
        public int levelRequired() {
            return 56;
        }

        @Override
        public int spellId() {
            return 12901;
        }
    }),
    ICE_RUSH(new CombatAncientSpell() {
        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            if (castOn != null && castOn.isNpc())
            castOn.getCombat().submit(new FreezeEvent(5, false));
            if (castOn.getTimerRepository().has(TimerKey.FREEZE) && cast.isPlayer()) {
                cast.getAsPlayer().sendMessage("Your target is already held by a magical force.");
            } else if (castOn.getTimerRepository().has(TimerKey.FREEZE_IMMUNITY) && cast.isPlayer()) {
                cast.getAsPlayer().sendMessage("Your target is currently immune to that spell.");
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1978));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.ICE_RUSH_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ICE_RUSH_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 360, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 0, 11,280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ICE_RUSH_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ICE_RUSH_CONTACT);
            return Optional.of(new Graphic(361));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 34;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(555, 2), new Item(562, 2), new Item(560, 2) });
        }

        @Override
        public int levelRequired() {
            return 58;
        }

        @Override
        public int spellId() {
            return 12861;
        }
    }),
    SMOKE_BURST(new CombatAncientSpell() {
        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            PoisonEffect.applyPoisonTo(castOn, PoisonType.MILD);
        }

        @Override
        public int spellRadius() {
            return 3;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1979));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.SMOKE_BURST_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_BURST_CAST);
            }
            return Optional.empty();
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_BURST_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_BURST_CONTACT);
            return Optional.of(new Graphic(389));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 36;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 2), new Item(554, 2), new Item(562, 4), new Item(560, 2) });
        }

        @Override
        public int levelRequired() {
            return 62;
        }

        @Override
        public int spellId() {
            return 12963;
        }
    }),
    SHADOW_BURST(new CombatAncientSpell() {
        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkillManager().getCurrentLevel(Skill.ATTACK) < player.getSkillManager().getMaxLevel(Skill.ATTACK)) {
                    return;
                }

                int decrease = (int) (0.1 * (player.getSkillManager().getCurrentLevel(Skill.ATTACK)));
                player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkillManager().getCurrentLevel(Skill.ATTACK) - decrease, true);
                player.getSkillManager().updateSkill(Skill.ATTACK);
            }
        }

        @Override
        public int spellRadius() {
            return 3;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1979));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.SHADOW_BURST_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SHADOW_BURST_CAST);
            }
            return Optional.empty();
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SHADOW_BURST_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SHADOW_BURST_CONTACT);
            return Optional.of(new Graphic(382));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 37;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 1), new Item(566, 2), new Item(562, 4), new Item(560, 2) });
        }

        @Override
        public int levelRequired() {
            return 64;
        }

        @Override
        public int spellId() {
            return 13011;
        }
    }),
    BLOOD_BURST(new CombatAncientSpell() {
        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            final Player player = cast.getAsPlayer();
            final int heal = (int) Math.round(damage * 0.15);
            if (player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) < player.getSkillManager().getMaxLevel(Skill.HITPOINTS)) {
                int level = player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) + heal > player.getSkillManager().getMaxLevel(Skill.HITPOINTS) ? player.getSkillManager().getMaxLevel(Skill.HITPOINTS) : player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) + heal;
                player.getSkillManager().setCurrentLevel(Skill.HITPOINTS, level, true);
            }
        }

        @Override
        public int spellRadius() {
            return 3;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1979));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.BLOOD_BURST_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.BLOOD_BURST_CAST);
            }
            return Optional.empty();
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.BLOOD_BURST_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.BLOOD_BURST_CONTACT);
            return Optional.of(new Graphic(376));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 39;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(565, 2), new Item(562, 4), new Item(560, 2) });
        }

        @Override
        public int levelRequired() {
            return 68;
        }

        @Override
        public int spellId() {
            return 12919;
        }
    }),
    ICE_BURST(new CombatAncientSpell() {
        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            if (castOn != null && castOn.isNpc())
            castOn.getCombat().submit(new FreezeEvent(10, false));
            if (castOn.getTimerRepository().has(TimerKey.FREEZE) && cast.isPlayer()) {
                cast.getAsPlayer().sendMessage("Your target is already held by a magical force.");
            } else if (castOn.getTimerRepository().has(TimerKey.FREEZE_IMMUNITY) && cast.isPlayer()) {
                cast.getAsPlayer().sendMessage("Your target is currently immune to that spell.");
            }
        }

        @Override
        public int spellRadius() {
            return 3;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1979));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.ICE_BURST_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ICE_BURST_CAST);
            }
            return Optional.empty();
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ICE_BURST_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ICE_BURST_CONTACT);
            return Optional.of(new Graphic(363));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 40;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(555, 4), new Item(562, 4), new Item(560, 2) });
        }

        @Override
        public int levelRequired() {
            return 70;
        }

        @Override
        public int spellId() {
            return 12881;
        }
    }),
    SMOKE_BLITZ(new CombatAncientSpell() {
        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            PoisonEffect.applyPoisonTo(castOn, PoisonType.EXTRA);
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1978));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.SMOKE_BLITZ_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_BLITZ_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 386, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 0, 11, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_BLITZ_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_BLITZ_CONTACT);
            return Optional.of(new Graphic(387));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 42;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 2), new Item(554, 2), new Item(565, 2), new Item(560, 2) });
        }

        @Override
        public int levelRequired() {
            return 74;
        }

        @Override
        public int spellId() {
            return 12951;
        }
    }),
    SHADOW_BLITZ(new CombatAncientSpell() {
        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkillManager().getCurrentLevel(Skill.ATTACK) < player.getSkillManager().getMaxLevel(Skill.ATTACK)) {
                    return;
                }

                int decrease = (int) (0.15 * (player.getSkillManager().getCurrentLevel(Skill.ATTACK)));
                player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkillManager().getCurrentLevel(Skill.ATTACK) - decrease, true);
                player.getSkillManager().updateSkill(Skill.ATTACK);
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1978));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.SHADOW_BLITZ_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SHADOW_BLITZ_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 380, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SHADOW_BLITZ_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SHADOW_BLITZ_CONTACT);
            return Optional.of(new Graphic(381));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 43;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 2), new Item(566, 2), new Item(565, 2), new Item(560, 2) });
        }

        @Override
        public int levelRequired() {
            return 76;
        }

        @Override
        public int spellId() {
            return 12999;
        }
    }),
    BLOOD_BLITZ(new CombatAncientSpell() {
        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            final Player player = cast.getAsPlayer();
            final int heal = (int) Math.round(damage * 0.20);
            if (player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) < player.getSkillManager().getMaxLevel(Skill.HITPOINTS)) {
                int level = player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) + heal > player.getSkillManager().getMaxLevel(Skill.HITPOINTS) ? player.getSkillManager().getMaxLevel(Skill.HITPOINTS) : player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) + heal;
                player.getSkillManager().setCurrentLevel(Skill.HITPOINTS, level, true);
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1978));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.BLOOD_BLITZ_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.BLOOD_BLITZ_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 374, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 0, 11, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.BLOOD_BLITZ_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.BLOOD_BLITZ_CONTACT);
            return Optional.of(new Graphic(375));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 45;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(565, 4), new Item(560, 2) });
        }

        @Override
        public int levelRequired() {
            return 80;
        }

        @Override
        public int spellId() {
            return 12911;
        }
    }),
    ICE_BLITZ(new CombatAncientSpell() {
        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            if (castOn != null && castOn.isNpc())
            castOn.getCombat().submit(new FreezeEvent(15, false));
            if (castOn.getTimerRepository().has(TimerKey.FREEZE) && cast.isPlayer()) {
                cast.getAsPlayer().sendMessage("Your target is already held by a magical force.");
            } else if (castOn.getTimerRepository().has(TimerKey.FREEZE_IMMUNITY) && cast.isPlayer()) {
                cast.getAsPlayer().sendMessage("Your target is currently immune to that spell.");
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1978));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.ICE_BLITZ_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ICE_BLITZ_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 368, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 0, 11, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ICE_BLITZ_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ICE_BLITZ_CONTACT);
            return Optional.of(new Graphic(367));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(366));
        }

        @Override
        public int baseExperience() {
            return 46;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(555, 3), new Item(565, 2), new Item(560, 2) });
        }

        @Override
        public int levelRequired() {
            return 82;
        }

        @Override
        public int spellId() {
            return 12871;
        }
    }),
    SMOKE_BARRAGE(new CombatAncientSpell() {
        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            PoisonEffect.applyPoisonTo(castOn, PoisonType.SUPER);
        }

        @Override
        public int spellRadius() {
            return 3;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1979));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.SMOKE_BARRAGE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_BARRAGE_CAST);
            }
            return Optional.empty();
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_BARRAGE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_BARRAGE_CONTACT);
            return Optional.of(new Graphic(387));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 48;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 4), new Item(554, 4), new Item(565, 2), new Item(560, 4) });
        }

        @Override
        public int levelRequired() {
            return 86;
        }

        @Override
        public int spellId() {
            return 12975;
        }
    }),
    SHADOW_BARRAGE(new CombatAncientSpell() {
        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkillManager().getCurrentLevel(Skill.ATTACK) < player.getSkillManager().getMaxLevel(Skill.ATTACK)) {
                    return;
                }

                int decrease = (int) (0.15 * (player.getSkillManager().getCurrentLevel(Skill.ATTACK)));
                player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkillManager().getCurrentLevel(Skill.ATTACK) - decrease, true);
                player.getSkillManager().updateSkill(Skill.ATTACK);
            }
        }

        @Override
        public int spellRadius() {
            return 3;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1979));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.SHADOW_BARRAGE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SHADOW_BARRAGE_CAST);
            }
            return Optional.empty();
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SHADOW_BARRAGE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SHADOW_BARRAGE_CONTACT);
            return Optional.of(new Graphic(383));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 49;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(556, 4), new Item(566, 3), new Item(565, 2), new Item(560, 4) });
        }

        @Override
        public int levelRequired() {
            return 88;
        }

        @Override
        public int spellId() {
            return 13023;
        }
    }),
    BLOOD_BARRAGE(new CombatAncientSpell() {
        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            final Player player = cast.getAsPlayer();
            final int heal = (int) Math.round(damage * (0.25 + (EquipmentUtil.getSpellExtraHealAmount(cast) * 0.015)));
            if (player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) < player.getSkillManager().getMaxLevel(Skill.HITPOINTS)) {
                int level = player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) + heal > player.getSkillManager().getMaxLevel(Skill.HITPOINTS) ? player.getSkillManager().getMaxLevel(Skill.HITPOINTS) : player.getSkillManager().getCurrentLevel(Skill.HITPOINTS) + heal;
                player.getSkillManager().setCurrentLevel(Skill.HITPOINTS, level, true);
            }
        }

        @Override
        public int spellRadius() {
            return 3;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1979));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.BLOOD_BARRAGE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.BLOOD_BARRAGE_CAST);
            }
            return Optional.empty();
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.BLOOD_BARRAGE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.BLOOD_BARRAGE_CONTACT);
            return Optional.of(new Graphic(377));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 51;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(560, 4), new Item(566, 1), new Item(565, 4) });
        }

        @Override
        public int levelRequired() {
            return 92;
        }

        @Override
        public int spellId() {
            return 12929;
        }
    }),
    ICE_BARRAGE(new CombatAncientSpell() {
        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            if (castOn != null && castOn.isNpc())
            castOn.getCombat().submit(new FreezeEvent(20, false));
            if (castOn.getTimerRepository().has(TimerKey.FREEZE) && cast.isPlayer()) {
                cast.getAsPlayer().sendMessage("Your target is already held by a magical force.");
            } else if (castOn.getTimerRepository().has(TimerKey.FREEZE_IMMUNITY) && cast.isPlayer()) {
                cast.getAsPlayer().sendMessage("Your target is currently immune to that spell.");
            }
        }

        @Override
        public int spellRadius() {
            return 3;
        }

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1979));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.ICE_BARRAGE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ICE_BARRAGE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 368, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 0, 11, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ICE_BARRAGE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.ICE_BARRAGE_CONTACT);
            return Optional.of(new Graphic(369));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(366));
        }

        @Override
        public int baseExperience() {
            return 52;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[] { new Item(555, 6), new Item(565, 2), new Item(560, 4) });
        }

        @Override
        public int levelRequired() {
            return 94;
        }

        @Override
        public int spellId() {
            return 12891;
        }
    }),
    TRIDENT_OF_THE_SEAS(new CombatNormalSpell() {

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1167));
        }


        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.WATER_WAVE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_WAVE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 1252, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 30, 10, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_WAVE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_WAVE_CONTACT);
            return Optional.of(new Graphic(1253));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(1251, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 50;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public int levelRequired() {
            return 75;
        }

        @Override
        public int spellId() {
            return 1;
        }
    }),
    SANGUINESTI_STAFF(new CombatAncientSpell() {

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1167));
        }


        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.SMOKE_RUSH_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_RUSH_CAST);
            }
            new Projectile(cast, castOn, 1248, 0, 40, 30, 10, 0);
            return Optional.of(new Projectile(cast, castOn, 1248, 0, 20, 30, 10, 0));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_RUSH_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_RUSH_CONTACT);
            return Optional.of(new Graphic(1249));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 65;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            if(cast.isPlayer() && ThreadLocalRandom.current().nextInt(1, 6) == 1) {
                cast.getAsPlayer().heal(damage / 2);
                castOn.performGraphic(new Graphic(377));
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public int levelRequired() {
            return 88;
        }

        @Override
        public int spellId() {
            return 12600;
        }
    }),
    HOLY_SANGUINESTI_STAFF(new CombatAncientSpell() {

        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1167));
        }


        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.SMOKE_RUSH_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_RUSH_CAST);
            }
            new Projectile(cast, castOn, 1248, 0, 40, 30, 10, 0);
            return Optional.of(new Projectile(cast, castOn, 1248, 0, 20, 30, 10, 0));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_RUSH_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.SMOKE_RUSH_CONTACT);
            return Optional.of(new Graphic(1249));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.empty();
        }

        @Override
        public int baseExperience() {
            return 65;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void spellEffect(Agent cast, Agent castOn, int damage) {
            if(cast.isPlayer() && ThreadLocalRandom.current().nextInt(1, 6) == 1) {
                cast.getAsPlayer().heal(damage / 2);
                castOn.performGraphic(new Graphic(377));
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public int levelRequired() {
            return 88;
        }

        @Override
        public int spellId() {
            return 12600;
        }
    }),
    THAMMARON_SCEPTRE(new CombatNormalSpell() {
        @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(CastSpellAnimation.OFFENSIVE_3.getAnimation(cast));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.FIRE_WAVE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_WAVE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 1465, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 43, 31, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_WAVE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.FIRE_WAVE_CONTACT);
            return Optional.of(new Graphic(1466, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(1464, GraphicHeight.MIDDLE));
        }

        @Override
        public int baseExperience() {
            return 60;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public int levelRequired() {
            return 82;
        }

        @Override
        public int spellId() {
            return 12601;
        }
    }),
    TRIDENT_OF_THE_SWAMP(new CombatNormalSpell() {
   	 @Override
        public Optional<Animation> castAnimation(Agent cast) {
            return Optional.of(new Animation(1167));
        }

        @Override
        public Optional<Projectile> castProjectile(Agent cast, Agent castOn) {
            if (cast.isPlayer() && !castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendSound(Sounds.WATER_WAVE_CAST);
            } else if (cast.isPlayer() && castOn.isPlayer()) {
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_WAVE_CAST);
            }
            return Optional.of(new Projectile(cast, castOn, 1040, 51, 2 + DistanceUtil.getChebyshevDistance(cast.getPosition(), castOn.getPosition()), 30, 10, 280));
        }

        @Override
        public Optional<Graphic> endGraphic(Agent cast, Agent castOn) {
            if (cast.isPlayer())
                cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_WAVE_CONTACT);
            else if (castOn.isPlayer())
                castOn.getAsPlayer().getPacketSender().sendAreaPlayerSound(Sounds.WATER_WAVE_CONTACT);
            return Optional.of(new Graphic(1042));
        }

        @Override
        public Optional<Graphic> startGraphic() {
            return Optional.of(new Graphic(665, GraphicHeight.HIGH, Priority.HIGHEST));
        }

        @Override
        public int baseExperience() {
            return 50;
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public int levelRequired() {
            return 75;
        }

        @Override
        public int spellId() {
            return 1;
        }
   });

    @NotNull
    private static Projectile createSurgeProjectile(Agent cast, Agent castOn, int projectileId, int contactGraphicId, int sound) {
        final Projectile projectile = new Projectile(cast, castOn, ProjectileTemplate.builder(projectileId)
                .setSourceOffset(1)
                .setDelay(0)
                .setSpeed(23)
                .setHeights(42, 31)
                .setCurve(25)
                .build());
        projectile.onArrival(() -> {
            cast.getAsPlayer().getPacketSender().sendAreaPlayerSound(sound);
            castOn.performGraphic(new Graphic(contactGraphicId, 40, GraphicHeight.HIGH, Priority.HIGHEST));
        });
        return projectile;
    }

    /**
     * The spell attached to this element.
     */
    private final CombatSpell spell;

    /**
     * Creates a new {@link CombatSpellType}.
     *
     * @param spell
     *            the spell attached to this element.
     */
    CombatSpellType(CombatSpell spell) {
        this.spell = spell;
    }


    /**
     * Gets the spell attached to this element.
     *
     * @return the spell.
     */
    public final CombatSpell getSpell() {
        return spell;
    }

    /**
     * Gets the spell with a {@link CombatSpell#spellId()} of {@code id}.
     *
     * @param id
     *            the identification of the combat spell.
     * @return the combat spell with that identification.
     */
    public static Optional<CombatSpellType> getCombatSpells(int id) {
        return Arrays.stream(CombatSpellType.values()).filter(s -> s != null && s.getSpell().spellId() == id).findFirst();
    }

	public static CombatSpell getCombatSpell(int spellId) {
        return getCombatSpells(spellId).map(CombatSpellType::getSpell).orElse(null);
    }

    static boolean displaySpellGraphic(NPC casterNPC) {
        return IntStream.of(2000, 109, 3580, 2007).anyMatch(id -> casterNPC.getId() == id);
    }

    public static boolean isAFasterNormalSpell(CombatSpell spell) {
        return spell == TRIDENT_OF_THE_SEAS.getSpell() || spell == TRIDENT_OF_THE_SWAMP.getSpell() || spell == THAMMARON_SCEPTRE.getSpell();
    }

    public static boolean isAFasterAncientSpell(CombatSpell spell) {
        return spell == SMOKE_RUSH.getSpell() || spell == SHADOW_RUSH.getSpell()
                || spell == BLOOD_RUSH.getSpell() || spell == ICE_RUSH.getSpell()
                || spell == SMOKE_BLITZ.getSpell() || spell == SHADOW_BLITZ.getSpell()
                || spell == BLOOD_BLITZ.getSpell() || spell == ICE_BLITZ.getSpell();
    }

    public static boolean dealsDamage(CombatSpell spell){
        return spell != ENFEEBLE.getSpell() && spell != STUN.getSpell();
    }

    public static boolean isImmuneToMagicDamage(Agent agent){
        return agent instanceof CallistoBoss
                || agent instanceof WarriorGuildNpc
                || agent instanceof VenenatisBoss
                || agent instanceof VetionBoss;
    }

    public static final HashMap<Integer, CombatSpellType> FOR_ID = new HashMap<>();

    static {
        for(CombatSpellType type : values()) {
            FOR_ID.put(type.spell.spellId(), type);
        }
    }

    public int getBaseMaxHit(){
        switch (this){
            case WIND_STRIKE:
                return 2;
            case WATER_STRIKE:
                return 4;
            case EARTH_STRIKE:
                return 6;
            case FIRE_STRIKE:
                return 8;
            case WIND_BOLT:
                return 9;
            case WATER_BOLT:
                return 10;
            case EARTH_BOLT:
                return 11;
            case FIRE_BOLT:
                return 12;
            case WIND_BLAST:
                return 13;
            case WATER_BLAST:
                return 14;
            case EARTH_BLAST:
                return 15;
            case FIRE_BLAST:
                return 16;
            case WIND_WAVE:
                return 17;
            case WATER_WAVE:
                return 18;
            case EARTH_WAVE:
                return 19;
            case FIRE_WAVE:
                return 20;
            case WIND_SURGE:
                return 21;
            case WATER_SURGE:
                return 22;
            case EARTH_SURGE:
                return 23;
            case FIRE_SURGE:
                return 24;
            case SMOKE_RUSH:
                return 14;
            case SHADOW_RUSH:
                return 15;
            case BLOOD_RUSH:
                return 16;
            case ICE_RUSH:
                return 17;
            case SMOKE_BURST:
                return 18;
            case SHADOW_BURST:
                return 19;
            case BLOOD_BURST:
                return 21;
            case ICE_BURST:
                return 22;
            case SMOKE_BLITZ:
                return 23;
            case SHADOW_BLITZ:
                return 24;
            case BLOOD_BLITZ:
                return 25;
            case ICE_BLITZ:
                return 26;
            case SMOKE_BARRAGE:
                return 27;
            case SHADOW_BARRAGE:
                return 28;
            case BLOOD_BARRAGE:
                return 29;
            case ICE_BARRAGE:
                return 30;
            case SANGUINESTI_STAFF:
                return 44;
            case HOLY_SANGUINESTI_STAFF:
                return 44;
            case THAMMARON_SCEPTRE:
                return 30;
            case DEMON_AGONY:
                return 31;
            case CRUMBLE_UNDEAD:
                return 15;
            case UNDEAD_BASH:
                return 27;
            case IBAN_BLAST:
                return 25;
            case SARADOMIN_STRIKE:
            case CLAWS_OF_GUTHIX:
            case FLAMES_OF_ZAMORAK:
                return 20;
            case SNARE:
                return 2;
            case ENTANGLE:
                return 5;
            case CONFUSE: // No damage
            case WEAKEN:
            case CURSE:
            case BIND:
            case VULNERABILITY:
            case ENFEEBLE:
            case STUN:
            case TELEBLOCK:
                return -1;
        }
        return 0;
    }

    public boolean isBoltSpell() {
        return this == WIND_BOLT || this == WATER_BOLT || this == EARTH_BOLT || this == FIRE_BOLT;
    }

    public boolean isGodSpell(){
        return this == SARADOMIN_STRIKE || this == CLAWS_OF_GUTHIX || this == FLAMES_OF_ZAMORAK;
    }
}