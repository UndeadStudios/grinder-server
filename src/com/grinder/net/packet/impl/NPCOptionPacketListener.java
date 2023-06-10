package com.grinder.net.packet.impl;

import com.grinder.game.World;
import com.grinder.game.content.gambling.lottery.Lottery;
import com.grinder.game.content.item.AngelicCapeGamble;
import com.grinder.game.content.item.ExchangeFireCape;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.item.mysterybox.SpinMysteryBox;
import com.grinder.game.content.item.mysterybox.SpinMysteryBoxType;
import com.grinder.game.content.minigame.castlewars.BarricadeManager;
import com.grinder.game.content.minigame.pestcontrol.PestControl;
import com.grinder.game.content.miscellaneous.*;
import com.grinder.game.content.miscellaneous.PetHandler.Pet;
import com.grinder.game.content.miscellaneous.WelcomeManager.WelcomeStage;
import com.grinder.game.content.miscellaneous.donating.Store;
import com.grinder.game.content.miscellaneous.voting.Voting;
import com.grinder.game.content.pvm.NpcInformation;
import com.grinder.game.content.pvm.contract.MonsterHunting;
import com.grinder.game.content.pvp.bountyhunter.BountyHunterManager;
import com.grinder.game.content.quest.QuestDialogueLoader;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.content.quest.impl.SheepShearer;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.impl.Thieving.Pickpocketing;
import com.grinder.game.content.skill.skillable.impl.crafting.HideTanData;
import com.grinder.game.content.skill.skillable.impl.herblore.PotionDecanting;
import com.grinder.game.content.skill.skillable.impl.magic.InteractiveSpell;
import com.grinder.game.content.skill.skillable.impl.magic.Teleporting;
import com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.MageOfZamorak;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager;
import com.grinder.game.content.skill.task.SkillMasterType;
import com.grinder.game.content.skill.task.SkillTaskManager;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.definition.NpcDropDefinition;
import com.grinder.game.entity.Entity;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpell;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.movement.task.WalkToAction;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.*;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.agent.player.event.PlayerEvents;
import com.grinder.game.message.decoder.MagicOnNPCMessageDecoder;
import com.grinder.game.message.decoder.NPCOptionMessageDecoder;
import com.grinder.game.message.impl.MagicOnNPCMessage;
import com.grinder.game.message.impl.NPCOptionMessage;
import com.grinder.game.model.*;
import com.grinder.game.model.NPCActions.ClickAction.Type;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.*;
import com.grinder.game.model.item.BrokenItems;
import com.grinder.game.model.item.ImbuedableItems;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.LogsToPlanks;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.PacketConstants;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.net.packet.interaction.PacketInteractionManager;
import com.grinder.util.*;
import com.grinder.util.debug.DebugManager;
import kotlin.random.Random;

import java.text.NumberFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.MageOfZamorak.*;
import static com.grinder.util.NpcID.*;

public class NPCOptionPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, PacketReader reader, int packetOpcode) {

		final NPCOptionMessage message;

		if (packetOpcode == PacketConstants.MAGE_NPC_OPCODE)
			message = MagicOnNPCMessageDecoder.Companion.decode(reader);
		else
			message = NPCOptionMessageDecoder.Companion.decode(packetOpcode, reader);

		final int index = message.getIndex();

		if (index < 0 || index > World.getNpcs().capacity())
			return;

		final NPC npc = World.getNpcs().get(index);

		if (npc == null)
			return;
		// Stop skilling
		SkillUtil.stopSkillable(player);

		if (player.busy()) {
			player.sendMessage("You can't do that right now.");
			return;
		}

		if (player.BLOCK_ALL_BUT_TALKING)
			return;

		if (player.isInTutorial())
			return;

		if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD)
			return;

		if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false) )
			return;

		if (player == null || player.getHitpoints() <= 0)
			return;

		if (npc != null && npc.isDying())
			return;

		if (npc != null && !npc.isAlive())
			return;

		if(player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME)
			player.stopTeleporting();

		if (player.isTeleporting())
			return;

		Optional<Pet> pet = Pet.getPet(npc.getId());
		if (pet != null && pet.isPresent()) {
			if (player.getCurrentPet() == null || player.getCurrentPet() != null && player.getCurrentPet() != npc) {
				player.sendMessage(pet.get().getName() + " is not interested in you...");
				return;
			}
		}

		if (!player.getCombat().isAttacking(npc))
			player.getCombat().reset(false);

		if (npc != null && npc.fetchDefinition() != null) {
			if (!npc.fetchDefinition().getName().toLowerCase().equals("fishing spot")
					&& npc.getId() != PUMPY && npc.getId() != DUMPY && npc.getId() != DUMPY_7387 && npc.getId() != STUMPY
					&& npc.getId() != NUMPTY && npc.getId() != THUMPY && npc.getId() != IFFIE && npc.getId() != SIR_TIFFY_CASHIEN
					&& npc.getId() != VORKATH_8059) {
				player.setEntityInteraction(npc);
			}
		}

		player.getPacketSender().sendInterfaceRemoval();

		if (player.getMotion().hasFollowTarget())
			player.getMotion().resetTargetFollowing();

		switch (packetOpcode) {
			case PacketConstants.ATTACK_NPC_OPCODE:
				attackNPC(player, npc);
				break;
			case PacketConstants.FIRST_CLICK_NPC_OPCODE:
				firstClick(player, npc);
				break;
			case PacketConstants.SECOND_CLICK_NPC_OPCODE:
				handleSecondClick(player, npc);
				break;
			case PacketConstants.THIRD_CLICK_NPC_OPCODE:
				handleThirdClick(player, npc);
				break;
			case PacketConstants.FOURTH_CLICK_NPC_OPCODE:
				handleFourthClick(player, npc);
				break;
			case PacketConstants.MAGE_NPC_OPCODE:
				mageNpc(player, npc, ((MagicOnNPCMessage) message).getSpellId() & 0xFFFF);
				break;
		}
	}

	public static boolean isBanker(int npcId) {
		switch (npcId) {
			case BANKER_1613:
			case BANKER_1618:
			case BANKER_3090:
			case BANKER_3091:
			case BANKER_3092:
			case BANKER_3093:
			case BANKER_2633:
			case BANKER:
			case JADE:
			case BANKER_TUTOR:
			case BANKER_1479:
			case BANKER_1480:
			case BANKER_2117:
			case BANKER_2119:
			case BANKER_2118:
			case BANKER_2292:
			case BANKER_2293:
			case BANKER_2368:
			case BANKER_2369:
			case BANKER_3094:
			case BANKER_2897:
			case BANKER_2898:
			case BANKER_3318:
			case BANKER_3887:
			case BANKER_3888:
			case BANKER_4054:
			case BANKER_4055:
			case BANKER_1633:
			case BANKER_1634:
			case BANKER_3089:
			case BANKER_6859:
			case BANKER_6860:
			case BANKER_6861:
			case BANKER_6862:
			case BANKER_6863:
			case BANKER_6864:
			case BANKER_6939:
			case BANKER_6940:
			case BANKER_6941:
			case BANKER_6942:
			case BANKER_6969:
			case TZHAARKETZUH:
			case 7678:
			case 11289:
				return true;
			default:
				return false;
		}
	}
	private static void firstClick(Player player, NPC npc) {

		if (player.getRights() == PlayerRights.DEVELOPER) {
			player.getPacketSender().sendMessage(
					"First click NPC: " + Integer.toString(npc.getId()) + ". " + npc.getPosition().toString(), 1000);
			//System.out.println("private static final int "+NpcDefinition.forId(npc.getId()).getName().replaceAll(" ", "_").toUpperCase()+" = "+npc.getId()+";");
		}

		DebugManager.debug(player, "npc-option", "1: npc: "+npc.getId()+", pos: "+npc.getPosition().toString());

		if (npc.getId() == NpcID.VORKATH) {
			player.getMotion().clearSteps();
			player.getMotion().traceTo(new Position(2272, 4060));
		}

		/*if (npc.fetchDefinition().getCombatLevel() <= 180) {
			int freezeTime = npc.getPosition().getDistance(player.getPosition());
			if (player.getMotion().isRunning())
				freezeTime /= 2;
			npc.getMotion().clearSteps();
			npc.getMotion().update(MovementStatus.DISABLED);
			TaskManager.submit(5 + Math.min(1, freezeTime), () -> {
				npc.getMotion().update(MovementStatus.NONE);
			});
		}*/

		final Executable action = () -> {
			player.subscribe(event -> {

				if (event == PlayerEvents.MOVED || event == PlayerEvents.LOGGED_OUT) {
					npc.resetEntityInteraction();
					return true;
				}

				final Entity playerInteracting = player.getInteractingEntity();
				final Entity npcInteracting = npc.getInteractingEntity();

				if (playerInteracting instanceof NPC && playerInteracting != npc) {
					npc.resetEntityInteraction();
					return true;
				}

				if (npcInteracting instanceof Player && npcInteracting != player) {
					npc.resetEntityInteraction();
					return true;
				}

				if (npc.getPosition().getDistance(player.getPosition()) > 1) {
					npc.debug("player too far away, resetting...");
					npc.resetEntityInteraction();
					return true;
				}
				return false;
			});


			if (!npc.fetchDefinition().getName().toLowerCase().equals("fishing spot")
					&& npc.getId() != PUMPY && npc.getId() != DUMPY && npc.getId() != DUMPY_7387 && npc.getId() != STUMPY
					&& npc.getId() != NUMPTY && npc.getId() != THUMPY && npc.getId() != IFFIE && npc.getId() != SIR_TIFFY_CASHIEN
			&& npc.getId() != VORKATH_8059 && npc.getId() != CRATE) {
				npc.setEntityInteraction(player);
			}

			if (QuestDialogueLoader.sendQuestDialogue(player, npc.getId())) {
				return;
			}


			if(PacketInteractionManager.handleNpcInteraction(player, npc, 1)) {
				return;
			}
			if (NPCActions.INSTANCE.handleClick(player, npc, Type.FIRST_OPTION))
				return;
			if (player.getClueScrollManager().handleNPCAction(1, npc))
				return;

			// Check if we're interacting with our pet..
			if (PetHandler.interact(player, npc)) {
				return;
			}

			if (SkillTaskManager.sendSkillMasterDialogue(player, npc.getId())) {
				return;
			}

			if (isBanker(npc.getId())) {
				if (player.busy()) {
					player.getPacketSender().sendMessage("You can't do this right now.");
					return;
				}
				player.getBankpin().openBank();
				return;
			}

			switch (npc.getId()) {
				case SHEEP_2693:
				case 2694:
				case 2695:
				case 2696:
				case 2697:
				case 2698:
				case 2699:
				case 2787:
				case 2786:
					SheepShearer.shaveSheep(player, npc);
					break;
				case 2110:
					ShopManager.open(player, 386);
					break;
				case MOURNER_9000: // Aquais Neige Store (Xagthan)
					ShopManager.open(player, ShopIdentifiers.AQUAIS_NEIGE_STORE);
					break;
				case LANTHUS:
					ShopManager.open(player, ShopIdentifiers.CASTLEWARS_TICKET_EXCHANGE);
					break;
				case ZOMBIE_PROTESTER:
				case ZOMBIE_PROTESTER_608:
				case ZOMBIE_PROTESTER_609:
				case ZOMBIE_PROTESTER_610:
				case ZOMBIE_PROTESTER_611:
				case ZOMBIE_PROTESTER_612:

					if (Misc.random(2) == 1) {
						new DialogueBuilder(DialogueType.NPC_STATEMENT)
								.setText("Happy halloween " + player.getUsername() + "!")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("Why are you protesting though?")
								.add(DialogueType.NPC_STATEMENT)
								.setText("?")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("Drinks?")
								.add(DialogueType.NPC_STATEMENT)
								.setText("It's clear we need more drinks to keep us alive!")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("Oh... happy halloween I suppose!.").start(player);
					} else {
						new DialogueBuilder(DialogueType.NPC_STATEMENT)
								.setText("Trick or Treat?")
								.add(DialogueType.OPTION).setOptionTitle("Select an Option")
								.firstOption("Trick.", player1 -> {
									new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
											.setText("Trick?")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Your gonna die!")
													.setPostAction ($ -> {
														npc.getMotion().traceTo(new Position(npc.getSpawnPosition().getX() + Random.Default.nextInt(-5, 5), npc.getSpawnPosition().getY() + Random.Default.nextInt(-5, 5)));
														npc.say("@red@ Zombies! Go and get " + player.getUsername() + " now!!!!!!!!!!!!");
													}).start(player);
								}).secondOption("Treat.", player1 -> {
									new DialogueBuilder(DialogueType.STATEMENT)
											.setText("The zombie nicely hands you some pumpkins.").start(player);
									player.getInventory().add(new Item(ItemID.PUMPKIN_24979));
								}).start(player);
					}
					break;
				case TZHAARKET_7679:
					new DialogueBuilder(DialogueType.NPC_STATEMENT)
							.setText("Hello JalYt " + player.getUsername() +"!")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("Hello!")
							.add(DialogueType.NPC_STATEMENT)
							.setText("You need help little JalYt?")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("Not unless you can help me in The Inferno.")
							.add(DialogueType.NPC_STATEMENT)
							.setText("We dare not go there. That job is for you.")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("Oh... worth a try I suppose.").start(player);
					break;
				case SAN_TOJALON:
					new DialogueBuilder(DialogueType.NPC_STATEMENT)
							.setText("Prepare yourself.")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("For what?")
							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANGRY)
							.setText("For a very dark ending!")
							.setPostAction ($ -> {
								TaskManager.submit(new Task(2) {
									@Override
									public void execute() {
										player.getPacketSender().sendInterfaceRemoval();
										npc.getCombat().initiateCombat(player);
										stop();
									}
								});
							}).start(player);
					break;
				case ELISE: // Mount Karuulm Store
					ShopManager.open(player, ShopIdentifiers.MOUNT_KARUULM_WEAPON_STORE);
					break;
				case KAALKETJOR: // The 3 npcs in Mount Karuulm Dungeon
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Human.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(8603)
							.setText("Leave us.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(8604)
							.setText("Your presence is not desired.")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("How friendly.")
							.start(player);
					break;
				case KAALMEJSAN:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Human.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(8602)
							.setText("Leave us.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(8604)
							.setText("Your presence is not desired.")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("How friendly.")
							.start(player);
					break;
				case KAALXILDAR:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Human.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(8602)
							.setText("Leave us.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(8603)
							.setText("Your presence is not desired.")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("How friendly.")
							.start(player);
					break;

				case SQUIRE_1762:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Save the void!")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("What's going on here?")
							.add(DialogueType.NPC_STATEMENT)
							.setText("This is where we launch our landers to combat the", "invasion of the nearby islands..")
							.add(DialogueType.NPC_STATEMENT)
							.setText("You will get Commendation points upon saving", "the void which can be exchanged for good rewards.")
							.add(DialogueType.NPC_STATEMENT)
							.setText("If you'd be willing to help us then just wait in the lander", "and we'll launch it as soon as it's ready.")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("I'll try my best veteran.")
							.start(player);
					break;
				case MANDRITH:
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Who are you and what is this place?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("My name is Mandrith.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("I collect valuable resources and pawn off access to", "them to foolish adventurers, like yourself.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("You should take a look inside my arena.", "There's an abundance of valuable resources inside.")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("And I can take whatever I want?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("It's all yours. All I ask is you pay the upfront fee.")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("Will others be able to kill me inside?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Yes. These walls will only hold them back for so long.")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("You'll stop them though, right?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Haha! For the right price, I won't deny any one access", "to my arena. Even if their intention is to kill you.")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("Right...")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("My arena holds many treasures that I've acquired at", "great expense, adventurer. Their bounty can come at a price.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("One day, adventurer, I will boast ownership of a much", "larger, much more dangerous arena than this.", "Take advantage of this offer while it lasts.")
							.start(player);
					break;
				case ZANDAR_HORFYRE:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("What is this place?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("My name is Zandar.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("I run a teaching school of magicks", "to those who don't know like yourself.")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("Can you teach me?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("It's not that easy.", "You have to come back later to me for this.")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("See you magician Zandar!")
							.start(player);
					break;
				case TZHAARHURTEL:
				case 7688:
				case 7689:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Welcome back " + player.getUsername() +"!")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("I have some questions please.")
							.add(DialogueType.OPTION).setOptionTitle("Select an Option")
							.firstOption("What are you doing here?", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
										.setText("What are you doing here?")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Get your Tzhaar stock before it runs out!", "I won't be here for long..")

								.start(player);
							}).secondOption("Where can I get Tokkuls?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Where can I get Tokkuls?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Tokkuls are usually dropped by slaying Tzhaar", "monsters that are found in the fire caves.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("You can spend the Tokkuls in my store.")
								.start(player);
					}).thirdOption("I'm looking to trade.", player1 -> {
						ShopManager.open(player, ShopIdentifiers.TZHAAR_STORE);

					}).fourthOption("Can I get a pet?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Can I get a pet?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Unfortunately I don't have one to give, however", "I heard there is someone who can be found in", "the fire caves who knows more", "about getting a Tzrek-jad pet.")
								.start(player);

					}).addCancel("Nevermind.").start(player);
					break;
				case ALRENA_4250:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Good day to you " + player.getUsername() +"!")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("Hi there!")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Can I assist you with anything?")
							.add(DialogueType.OPTION).setOptionTitle("Select an Option")
							.firstOption("What are skilling points?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("What are skilling points?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Skilling points is a points currency that can", "be obtained from skilling.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("By reaching a certain amount of experience, you", "will receive skilling points depending", "on the skill activity.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("You can spend those points here by purchasing unique", "items from my store.")
								.start(player);
							}).secondOption("How can I get skilling points?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("How can I get skilling points?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("You will automatically receive skilling points as", "you train your skills.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("The higher your stats and experience, the faster", "you will receive points.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("You will get a chatbox message upon gaining skilling", "points.")

								.start(player);
							}).thirdOption("What do you have for sale?", player1 -> {
								ShopManager.open(player, ShopIdentifiers.SKILLING_POINTS_STORE);
							}).addCancel("Nevermind.").start(player);
					break;
				case ENTOMOLOGIST:
					if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
						return;
					}
					//if (player.getPosition().getY() == 5241) {
						player.getMotion().clearSteps();
						player.getPacketSender().sendMinimapFlagRemoval();
						EntityExtKt.markTime(player, Attribute.LAST_PRAY);
						player.performAnimation(new Animation(881, 25));
						player.getMotion().update(MovementStatus.DISABLED);
						TaskManager.submit(new Task(3) {
							@Override
							public void execute() {
								DialogueManager.sendStatement(player,
										"You search the dead explorer, but nothing valuable has been found.");
								player.getMotion().update(MovementStatus.NONE);
								stop();
							}
						});
					//}
					break;
				case GEE: // Default npcs add below
				case DONIE:
				case DUKE_HORACIO:
				case ALMERA:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Hello, how's it going?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("I'm fine, how are you?").add(DialogueType.PLAYER_STATEMENT)
							.setText("Very well thank you.").start(player);
					break;
				case STICKY_SANDERS:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Are you interested in dueling me?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("I am not interested for now.").add(DialogueType.PLAYER_STATEMENT)
							.setText("K bye.").start(player);
					break;
				case KING_GJUKI_SORVOTT_IV:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Hey King!")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Hi there fellows!")
							.add(DialogueType.OPTION).setOptionTitle("Select an Option")
							.firstOption("Are you running any seasonal events?", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
										.setText("Are you running any seasonal events?")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("No, I have no running seasonal events at the moment.", "If there is anything, you will see it announced.").start(player);
							}).secondOption("What are you doing here?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("What are you doing here?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("My name is Gjuki and I was the ruler in an old", "city called Relleka.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("If you've heard my story you will know that my ship", "got wrecked while sailing and I was the only survivor.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("I've managed to escape and here I am now.")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("Interesting story, King!")
								.start(player);
							}).thirdOption("I'm looking for a quest.", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("I'm looking for a quest.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("I'm sorry I can't help you there.").start(player);

					}).start(player);
					break;
				case BROTHER_KOJO:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("This is such a memorial place.", "Thank you for helping me fix the Clock tower!").add(DialogueType.PLAYER_STATEMENT)
							.setText("Good luck with your adventures chief!").start(player);
					break;
				case COOK_4626:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("How is the adventuring going, my friend?").add(DialogueType.PLAYER_STATEMENT)
							.setText("I am getting strong and mighty.").start(player);
				case SANFEW:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("What can I do for you young 'un?").add(DialogueType.PLAYER_STATEMENT)
							.setText("Actually, I don't need to speak to you.").add(DialogueType.NPC_STATEMENT)
							.setText("Well, we all make mistakes sometimes.")
							.start(player);

					break;
				case DORIC:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Hello traveller, how is your metalworking coming along?").add(DialogueType.PLAYER_STATEMENT)
							.setText("Not too bad, Doric.").add(DialogueType.NPC_STATEMENT)
							.setText("Good, the love of metal is a thing close to my heart.")
							.start(player);

					break;
				case VERONICA:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Where is he now?").add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Oh he went off to talk to some green warty guy.", "I'm sure he'll be back soon.")
							.start(player);

					break;
				case PROFESSOR_ODDENSTEIN:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Be careful in here, there's lots of dangerous equipment.").add(DialogueType.PLAYER_STATEMENT)
							.setText("What does this machine do?").add(DialogueType.NPC_STATEMENT)
							.setText("Nothing at the moment... It's broken.", "It's meant to be a transmutation machine.")
							.start(player);

					break;
				case FRED_THE_FARMER:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("What did you suggest I do again?").add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Have you been to Seth Groats yet?", "He's over at the other farm on the way to Varrock!").add(DialogueType.PLAYER_STATEMENT)
							.setText("Ok, I'll go and see him then.")
							.start(player);

					break;
				case WIZARD_GRAYZAG:
				case WIZARD_MIZGOG:
				case WIZARD_MIZGOG_7747:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("So you think finding those beads makes you clever, do you?").add(DialogueType.PLAYER_STATEMENT)
							.setText("Well yes, actually.").add(DialogueType.NPC_STATEMENT)
							.setText("Well you'd better just watch your back,", "because when you least expect it I'll be there.")
							.start(player);

					break;
					// TODO: Guildmaster dialogue if quest completed or not.
				case GERTRUDE:
				case 3528:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Hello, my dear. How's things?").add(DialogueType.NPC_STATEMENT)
							.setText("Did you ever get to talk to those people I told you about?").add(DialogueType.PLAYER_STATEMENT)
							.setText("Yes, strange people. But interesting nonetheless.")
							.start(player);

					break;
				case REDBEARD_FRANK:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Arr, Matey!").add(DialogueType.NPC_STATEMENT)
							.setText("Arr!")
							.start(player);

					break;
				case BROTHER_OMAD:
				case BROTHER_CEDRIC:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Hello.").add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Honey, money, woman and wine!").add(DialogueType.PLAYER_STATEMENT)
							.setText("Are you ok?.").add(DialogueType.NPC_STATEMENT)
							.setText("Yesshh...hic up...beautiful!?").add(DialogueType.PLAYER_STATEMENT)
							.setText("Take care old monk.")
							.start(player);

					break;
				case SHILOP:
				case WILOUGH:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("You think you're tough do you?").add(DialogueType.NPC_STATEMENT)
							.setText("Pardon?").add(DialogueType.STATEMENT)
							.setText("The boy begins to jump around with his fists up.", "You decide it's best not to kill him just yet.")
							.start(player);

					break;
				case 5034:
				case ARCHMAGE_SEDRIDOR_11433:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Hello again, " + player.getUsername() +". What can I do for you?").add(DialogueType.PLAYER_STATEMENT)
							.setText("Nothing thanks, I'm just looking around..").add(DialogueType.NPC_STATEMENT)
							.setText("Well, take care. You stand on the ruins of the old", "destroyed Wizards' Tower. Strange and powerful magicks", "lurk here.")
							.start(player);

					break;
				case GENERAL_WARTFACE:
				case GENERAL_BENTNOZE:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("When Chosen Commander come to speak to us again?").add(DialogueType.PLAYER_STATEMENT)
							.setText("I don't know. I don't think she was making", "any progress when she spoke to you before.").add(DialogueType.NPC_STATEMENT)
							.setText("Then she not come here again!")
							.start(player);

					break;
				case ABBOT_LANGLEY:
				case SIGMUND_5322:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Isn't this place built a bit out of the way?").add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("We like it that way actually! We get disturbed less.").add(DialogueType.NPC_STATEMENT)
							.setText("We still get rather a large amount of travellers", "looking for sanctuary and healing here as it is!")
							.start(player);

					break;
				case ARCHER:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Why are you guys hanging around here?").add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("(ahem)...'Guys'?").add(DialogueType.NPC_STATEMENT)
							.setText("Uh... yeah, sorry about that.", "Why are you all standing around out here?")
							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANGRY)
							.setText("Well, that's really none of your business.")
							.start(player);

					break;
				case SHAMUS:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
							.setText("Go away!").start(player);
					break;
				case HISTORIAN_MINAS:
				case HADLEY:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("This is such a memorial island.", "I am looking for a discovery.").add(DialogueType.PLAYER_STATEMENT)
							.setText("Good luck finding one chief!").start(player);
					break;
				case KILLER_7636:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("I haven't killed anybody!").add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANGRY)
							.setText("LeAVE ME AloNE NOWW!").start(player);
					break;
				case HUDON:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("What do you want from me!").add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANGRY)
							.setText("Please leave me alone!").start(player);
					break;
				case JEX:
				new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
						.setText("We are monitoring the staking and duels on here!", "Good luck!").add(DialogueType.PLAYER_STATEMENT)
						.setText("Farewell!").start(player);
				break;
				case MAISA:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Why are you here alone?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("I am just waiting for this minigame to open.", "I have heard it is going to be opening very soon!")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("Okay take care then!")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("You too!")
							.start(player);
					break;
				case HETTY:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("How's your magic coming along?").add(DialogueType.PLAYER_STATEMENT)
							.setText("I'm practicing and slowly getting better.!")
							.add(DialogueType.NPC_STATEMENT)
							.setText("Good, good.")
							.start(player);
					break;
				case HOPS_1108:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Cheers!").add(DialogueType.PLAYER_STATEMENT)
							.setText("Enjoy..").start(player);
					break;
				case GUARD_4335:
				case GUARD_4336:
				case GARV:
				case GUARD_4660:
				case GUARD_4661:
				case GUARD_4662:
				case GUARD_4663:
				case GUARD_4664:
				case GUARD_4665:
				case GUARD_4666:
				case MERCENARY:
				case MERCENARY_4657:
				case MERCENARY_4658:
				case MERCENARY_4659:
				case MERCENARY_CAPTAIN:
				case KHAZARD_GUARD_1209:
				case KHAZARD_GUARD_1210:
				case GENERAL_KHAZARD_3510:
				case GENERAL_KHAZARD:
				case PIRATE_4038:
				case GUARD_3283:
				case WHITE_KNIGHT_4114:
				case GUARD_6056:
				case MEIYERDITCH_CITIZEN_8328:
				case MEIYERDITCH_CITIZEN_8329:
				case MEIYERDITCH_CITIZEN_8330:
				case MEIYERDITCH_CITIZEN_8331:
				case VYREWATCH_8251:
				case VYREWATCH_8252:
				case VYREWATCH_8253:
				case VYREWATCH_8254:
				case VYREWATCH_8255:
				case VYREWATCH_8256:
				case VYREWATCH_8257:
				case VYREWATCH_8258:
				case VYRELORD_8332:
				case VYRELORD_8334:
				case VYRELADY:
				case  VYRELADY_8335:
				case MERCENARY_8213:
				case MERCENARY_8214:
				case MERCENARY_8215:
				case GARTH_8206:
				case KEY_MASTER:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("I am busy guarding! Please leave me alone.").add(DialogueType.PLAYER_STATEMENT)
							.setText("Okay Mr. Guard!").start(player);
					break;
				case GAMER_1012:
					AngelicCapeGamble.exchange(player, 0);
					break;
				case BLACK_KNIGHT_TITAN:
					if (Misc.random(2) == 1) {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("We won't give up this port to the Imperials without", "a fight, don't you worry.").add(DialogueType.PLAYER_STATEMENT)
								.setText("Oh, now you will pay for this treachery...!").setExpression(DialogueExpression.ANGRY).start(player);
					} else {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("You just keep quiet and stay where you are.").add(DialogueType.PLAYER_STATEMENT)
								.setText("Pssst, I will let you pay for this...!").setExpression(DialogueExpression.ANGRY).start(player);
					}
					break;
				case KHAZARD_GUARD:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Hello.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("I don't know you stranger!")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.CRYING_ALMOST)
							.setText("Get of our land!")
							.setPostAction ($ -> {
								npc.getCombat().initiateCombat(player);
								npc.say("You are under attack!");
							}).start(player);
					break;
				case ROAVAR:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("A false friend and a shadow attend only while the sun shines.").add(DialogueType.PLAYER_STATEMENT)
							.setText("Okay I guess.").start(player);
					break;
				case ZOJA:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("If I were as ugly as you I would not dare to show my face in public!").start(player);
					break;
				case MALAK:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Come here, I would like to know what you are", "doing in these lands.").start(player);
					break;
				case JOSHUA:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("I still have some hope to find fish over here.").add(DialogueType.PLAYER_STATEMENT)
							.setText("Best of luck Josh!").start(player);
					break;
				case JAIL_GUARD:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Get away before I put you in one of these cells!").setExpression(DialogueExpression.ANGRY)
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("...").start(player);
					break;
				case SOPHANEM_GUARD:
				case SOPHANEM_GUARD_3883:
					if (Misc.random(5) == 5) {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.CURIOUS)
								.setText("Enjoy your gambling while I keep everything secure.").start(player);
					} else if (Misc.random(5) == 4) {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.EVIL)
								.setText("I'm busy!").start(player);
					} else if (Misc.random(5) == 3) {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.CURIOUS)
								.setText("Do I look like I can talk?").start(player);
					} else if (Misc.random(5) == 2) {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("I cannot get enough rest.").add(DialogueType.PLAYER_STATEMENT)
								.setText("Why not?")
								.add(DialogueType.NPC_STATEMENT)
								.setText("It's because I have to keep watching everyone here.").start(player);
					} else if (Misc.random(5) == 1) {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("I am watching you, " + player.getUsername() +"!")
								.add(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.DISTRESSED)
								.setText("Alright then!").start(player);
					} else {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
						.setText("My job is to keep everyone safe.").start(player);
					}
					break;
				case MURKY_PAT:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Why are you drinking here?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("My story is very sad...")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.CRYING_ALMOST)
							.setText("I used to be one of the richest gamblers on here.", "After gambling all of my wealth I have got nothing else", "left to gamble.")
							.add(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.DISTRESSED)
							.setText("I see...it happens - Why are you still here though?").add(DialogueType.NPC_STATEMENT)
							.setText("I still love watching other players gambling and it also", "reminds me of my great past.").add(DialogueType.PLAYER_STATEMENT)
							.setText("Alright! Enjoy watching then!")
							.start(player);
					break;
				case FIRST_MATE_DAVEYBOY:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Hey! We will soon be able to gamble together.", "Stay tuned.").add(DialogueType.PLAYER_STATEMENT)
							.setText("I'll be waiting for you Dave!").start(player);
					break;
				case LECHEROUS_LEE:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Are you going to win the jackpot today?")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("Perhaps.").start(player);
					break;
				case HAMID:
				case NED:
				case GERRANT_2891:
				case LOWE_8683:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("I am in search of a specific clue scroll treasure.")
							.start(player);
					break;
				case JARAAH:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("I am doing operations to the severely injured people.")
							.start(player);
					break;
				case RADIMUS_ERKLE:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Why are you roaming here?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("I'm trying to find a key to access the dungeon below.").add(DialogueType.PLAYER_STATEMENT)
							.setText("What's in this dungeon?").add(DialogueType.NPC_STATEMENT)
							.setText("I heard legends say that the evil Black knight titan dwells", "below along with The Untouchable.", "I don't recommend you going down there,", "unless you are prepared!").add(DialogueType.PLAYER_STATEMENT)
							.setText("Alright! I will see what I can do, thanks!").add(DialogueType.NPC_STATEMENT)
							.setText("Be safe!")
							.start(player);
					break;
				case CAVE_GOBLIN_6434:
				case CAVE_GOBLIN_6436:
					if (Misc.random(3) == 1) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hello, how are you?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("I'm a bit worried about the increase of humans these", "days.").add(DialogueType.NPC_STATEMENT)
								.setText("Present company excluded, of course!").start(player);
					} else if (Misc.random(2) == 1) {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Where did you come from?")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("From above ground.").add(DialogueType.NPC_STATEMENT)
								.setText("Above ground? Where is that?").add(DialogueType.PLAYER_STATEMENT)
								.setText("You know, out of the caves, in the open air, with", "sunshine and wide open spaces!").add(DialogueType.NPC_STATEMENT)
								.setText("Ick. Sounds horrible.")
								.start(player);
					} else {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Beware of swamp gas! Look out for the warning marks!")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("Um, thanks.")
								.start(player);
					}
					break;

				case GRUBFOOT:
				case GOBLIN:
				case GOBLIN_656:
				case GOBLIN_657:
				case GOBLIN_658:
				case GOBLIN_659:
				case GOBLIN_660:
				case GOBLIN_661:
				case GOBLIN_662:
				case GOBLIN_663:
				case GOBLIN_664:
				case GOBLIN_665:
				case GOBLIN_666:
				case GOBLIN_667:
				case GOBLIN_668:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Hello, how are you?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("I'm a bit worried about the increase of humans these", "days.").add(DialogueType.NPC_STATEMENT)
							.setText("Present company excluded, of course!").start(player);
					break;
				case GYPSY_ARIS:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Hello young one.").add(DialogueType.NPC_STATEMENT)
							.setText("Cross my palm with silver and the future will", "be revealed to you.")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("But how did you know what I would ask?").add(DialogueType.NPC_STATEMENT)
							.setText("Because I can tell the future.").add(DialogueType.PLAYER_STATEMENT)
							.setText("Ah, that's very clever!").add(DialogueType.NPC_STATEMENT)
							.setText("Thanks. Oh and be careful in the Wilderness.", "Tonight is not your night..").add(DialogueType.PLAYER_STATEMENT)
							.setText("Cheers!")
							.start(player);
					break;
				case BARBARIAN_GUARD_7285:
				case BARBARIAN_GUARD_7724: // Vial toggle barbarian guard
					if (!player.unlockedVialCrushing()) { // Locked
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Oi, whaddya want?")
								.add(DialogueType.OPTION).setOptionTitle("Select an Option")
								.firstOption("I want to learn how to crush vials.", player1 -> {
									new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
											.setText("I want to learn how to crush vials.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Barbarians only. Are you a barbarian? You don't look", "like one.").add(DialogueType.OPTION).setOptionTitle("Select an Option")
											.firstOption("Hmm, yep you've got me there.", player2 -> {
												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("Hmm, yep you've got me there.")
														.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
														.setText("Now please, stop wasting my time.", "I am busy on duty guarding!")
														.start(player);
											}).secondOption("I'm ready to serve you if you teach me.", player2 -> {
										new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
												.setText("I'm ready to serve you if you teach me.")
												.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
												.setText("If you want me to teach you then you will need", "to get me the sweet stuff. We barbarians love sweets.")
												.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
												.setText("I have the perfect challenge for you... If you can get me", "a Chocolate bomb, Chocolate saturday, and @gre@65M</col> coins", "I will teach you how to crush vials.")
												.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
												.setText("So, do you have them ready for me?")
												.add(DialogueType.OPTION).setOptionTitle("Select an Option")
												.firstOption("Here you go.", player4 -> {
													if ((player.getInventory().getAmount(995) >= 65_000_000) && player.getInventory().contains(2229)
															&& player.getInventory().contains(2030)) {
														player.getInventory().delete(995, 65_000_000);
														player.getInventory().delete(new Item(2229, 1));
														player.getInventory().delete(new Item(2030, 1));
														PlayerUtil.broadcastMessage("<img=1243> " + PlayerUtil.getImages(player) + "" + player.getUsername() + " has just unlocked the ability to crush vials when empty from the barbarian guard.");
														player.setUnlockedVialCrushing(true);
														new DialogueBuilder(DialogueType.STATEMENT)
																.setText("You hand over all the items requested by the barbarian.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
																.setText("Thanks! I can now have a good break.")
																.add(DialogueType.STATEMENT)
																.setText("The barbarian teaches you barbarian way of crushing vials.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
																.setText("It's all part of drinking like a barbarian! Okay, you will", "now smash your vials as you drink your potions.")
																.start(player);

													} else {
														new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
																.setText("No you don't. Come back again when you them ready.").start(player);
													}
												}).secondOption("Keep it for yourself. I'm good.", player4 -> {
											new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
													.setText("Keep it for yourself. I'm good.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
													.setText("Do not show me your face here again.").start(player);
										}).start(player);
									}).start(player);
									}).secondOption("I want some money.", player3 -> {
										new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
												.setText("I want some money.")
												.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
												.setText("Do I look like a bank to you?").start(player);
									}).start(player);
					} else {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("You can toggle the ability to automatically destroy vials", "upon consuming the last dose, instead of them being empty.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Do you want to toggle smashing vials when they're empty?")
								.add(DialogueType.OPTION).setOptionTitle("Select an Option")
								.firstOption("Yes.", player2 -> {
									if (player.isVialCrushingToggled()) {
										player.setVialCrushingToggled(false);
										new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
												.setText("You're a funny sort of barbarian! But okay, you will", "no longer smash your vials as you drink your potions.")
												.start(player);
									} else {
										player.setVialCrushingToggled(true);
										new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
												.setText("It's all part of drinking like a barbarian! Okay, you will", "now smash your vials as you drink your potions.")
												.start(player);
									}
								}).addCancel("No").start(player);
					}
					break;
				case SOLDIER_6868:
				case MAN:
				case MUBARIZ:
				case PENDA:
				case OCGA:
				case HYGD:
				case CEOLBURG:
				case BREOCA:
				case BERNALD:
				case TOWER_ADVISOR:
				case TOWER_ADVISOR_6062:
				case TOWER_ADVISOR_6063:
				case TOWER_ADVISOR_6064:
				case MAN_1118:
				case WINTER_SOLDIER:
				case MAN_1138:
				case MAN_3014:
				case MAN_3106:
				case MAN_3107:
				case MAN_3108:
				case MAN_3109:
				case MAN_3110:
				case MAN_3261:
				case MAN_3264:
				case MAN_3265:
				case MAN_3298:
				case MAN_3652:
				case MAN_4268:
				case MAN_4269:
				case MAN_4270:
				case MAN_4271:
				case MAN_4272:
				case MAN_6776:
				case MAN_6815:
				case MAN_6818:
				case MAN_6987:
				case MAN_6988:
				case MAN_6989:
				case MAN_7281:
				case MAN_7919:
				case MAN_7920:
				case MAN_8858:
				case MAN_8859:
				case MAN_8860:
				case MAN_8861:
				case MAN_8862:
				case WOMAN:
				case WOMAN_1130:
				case WOMAN_1131:
				case WOMAN_1139:
				case WOMAN_1140:
				case WOMAN_1141:
				case WOMAN_1142:
				case WOMAN_3015:
				case WOMAN_3111:
				case WOMAN_3112:
				case WOMAN_3113:
				case WOMAN_3268:
				case WOMAN_3299:
				case WOMAN_4958:
				case WOMAN_6990:
				case WOMAN_6991:
				case WOMAN_6992:
				case WOMAN_7921:
				case WOMAN_7922:
				case WOMAN_8863:
				case WOMAN_8864:
				case SQUIRE_1770:
				case SQUIRE_1765:
				case SQUIRE_1767:
				case SEAMAN_THRESNOR:
				case KLARENSE:
				case VOID_KNIGHT_1756:
				case VOID_KNIGHT_1757:
				case VOID_KNIGHT_1758:
				case SQUIRE:
				case SQUIRE_1760:
				case SQUIRE_1769:
				case VOID_KNIGHT:
				case DODGY_SQUIRE:
				case CUSTOMS_OFFICER:
				case GRUM:
				case GRUM_2889:
				case BETTY:
				case BETTY_5905:
				case MONK_OF_ENTRANA:
				case MONK_OF_ENTRANA_1166:
				case MONK_OF_ENTRANA_1167:
				case MONK_OF_ENTRANA_1168:
					if (Misc.random(4) == 1) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hello, how's it going?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
						.setText("How can I help you?").add(DialogueType.OPTION).setOptionTitle("Select an Option")
								.firstOption("Do you wish to trade?", player1 -> {
									new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
											.setText("Do you wish to trade?")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("No, I have nothing I wish to get rid of. If you want to", "do some trading, there are plenty of shops and market", "stalls around though.").start(player);
						}).secondOption("I'm in search of a quest.", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("I'm in search of a quest.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("I'm sorry I can't help you there.").start(player);
						}).thirdOption("I'm in search of enemies to kill.", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("I'm in search of enemies to kill.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("I've heard there are many fearsome creatures that", "dwell under the ground.").start(player);
						}).start(player);
					} else if (Misc.random(4) == 2) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hello, how's it going?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("I'm fine, how are you?").add(DialogueType.PLAYER_STATEMENT)
								.setText("Very well thank you.").start(player);
					} else if (Misc.random(4) == 3) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hello, how's it going?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Get out of my way, I'm in a hurry!").setExpression(DialogueExpression.ANGRY)
								.start(player);
					} else if (Misc.random(4) == 4) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hello, how's it going?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Not too bad thanks.").start(player);
					} else if (Misc.random(3) == 2) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hello, how's it going?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("No, I don't want to buy anything!").setExpression(DialogueExpression.ANGRY)
								.start(player);
					} else if (Misc.random(5) == 2) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hello, how's it going?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("I'm busy right now.").start(player);
					} else if (Misc.random(4) == Misc.random(4)) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hello, how's it going?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("I'm a little worried - I've heard there's lots of people", "going about, killing citizens at random.").start(player);
					} else if (Misc.random(6) == 2) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hello, how's it going?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Do I know you? I'm in a hurry.").setExpression(DialogueExpression.EVIL)
								.start(player);
					} else if (Misc.random(6) == 3) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hello, how's it going?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Hello there, nice weather we've been having.").setExpression(DialogueExpression.HAPPY)
								.start(player);
					} else if (Misc.random(7) == 5) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hello, how's it going?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("I think we need a new king. The one we've got isn't", "very good.").start(player);
					} else if (Misc.random(4) == 2) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hello, how's it going?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Not too bad, but I'm little worried about the increase", "of goblins these days.")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("Don't worry, I'll kill them.").start(player);
					} else if (Misc.random(6) == 4) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hello, how's it going?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Who are you?").setExpression(DialogueExpression.DISTRESSED)
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("I'm a bold adventurer.").add(DialogueType.NPC_STATEMENT)
								.setText("Ah, a very notable profession.").setExpression(DialogueExpression.CALM).start(player);
					} else if (Misc.random(6) == 2) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hello, how's it going?")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("Do you wish to trade?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("No, I have nothing I wish to get rid of. If you want to", "do some trading, there are plenty of shops and market", "stalls around though.")
								.start(player);
					} else {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hello, how's it going?")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("I'm in search of enemies to kill.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("I've heard there are many fearsome creatures that", "dwell under the ground.").start(player);
					}
					break;
				case ELITE_VOID_KNIGHT:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Hi there, I need some help.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Absolutely! What are you looking for?").add(DialogueType.OPTION).setOptionTitle("Select an Option")
							.firstOption("I would like to know more about the elite void gear.", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
										.setText("I would like to know more about the elite void gear.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Elite Void Knight equipment is an upgraded version of", "the regular Void Knight equipment.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Like regular Void Knight equipment, it requires 42 Attack,", "Strength, Defence, Magic, Hitpoints, and Ranged,", "as well as 22 Prayer, to wear.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Elite Void Knight equipment offers the same defence bonuses", "as the regular Void Knight equipment and counts as Void Knight", "gear for the purpose of Void Knight equipment's set effects.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("However, it has a different appearance, gives a Prayer bonus", "of +3 per piece (total of +6), also it provides an additional", "damage boost of 2.5% to both the mage and range sets.")
										.add(DialogueType.PLAYER_STATEMENT)
										.setText("Alright cheers thanks for your information.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Anytime " + player.getUsername() +"!")
										.start(player);

							}).secondOption("I would like to know more about gear upgrading.", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
										.setText("I would like to upgrade the trim of my void gear.", "I would like to know more about gear upgrading.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Well, let me explain this to you " + player.getUsername() +"..")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Veteran players can be differentiated by their trim color,", "which shows your <col=800080>Commendation rank</col> and skills.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("The gear will retain its benefits which gives you", "boosted damage and accuracy based on your gear setup.")
										.add(DialogueType.PLAYER_STATEMENT)
										.setText("Okay thanks for the information.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Your welcome.")
										.start(player);


							}).thirdOption("I would like to upgrade the trim of my void gear.", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
										.setText("I would like to upgrade the trim of my void gear.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Okay, now let me know which upgrade you are looking for.")
										.add(DialogueType.OPTION).setOptionTitle("Select an Option")
										.firstOption("<col=1E90FF>Baby Blue (Trim) Equipment Set</col>", player2 -> {

											new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
													.setText("I would like the <col=1E90FF>Baby Blue (Trim) Equipment Set</col>", "Set upgrade.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
													.setText("Good choice " + player.getUsername() +"! To proceed with the upgrade,", "you will need <col=800080>250 Commendation points</col>.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
													.setText("Along with each and every piece of your untrimmed", "Void Knight equipment.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
													.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
													.add(DialogueType.OPTION).setOptionTitle("Select an Option")
													.firstOption("Yes I would like to upgrade it.", player3 -> {


														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("Yes I would like to upgrade it.").setPostAction ($ -> {
																	if (!player.getInventory().containsAny(11665, 11664, 11663, 8839, 8840, 13072, 13073)) { // Doesn't have any of the pieces to upgrade
																		new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																				.setText("You don't seem to have anything for me to upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back later once you have the Void knight equipment", "and <col=800080>250 Commendation points</col> ready.").start(player);
																	} else if (!player.getInventory().containsAll(11665, 11664, 11663, 8839, 8840, 13072, 13073) && player.getInventory().containsAny(11665, 11664, 11663, 8839, 8840, 13072, 13073)) { // Has some pieces to upgrade but not all
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13072, 200)
																				.setText("You hand over the Void Knight equipment.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("You are missing some of the required pieces " + player.getUsername() +".")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("You must have three of the Void knight helmets, top, robe,", "and the Elite void top and robe for the upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back later once you have them ready.").start(player);
																	} else if (player.getInventory().containsAll(11665, 11664, 11663, 8839, 8840, 13072, 13073) && player.getPoints().get(AttributeManager.Points.COMMENDATION) < 250) { // Has all the pieces to upgrade but not enough points
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13072, 200)
																				.setText("You hand over the Void Knight equipment.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																				.setText("Oh dear... you don't have enough <col=800080>Commendation points</col>.", "You need at least 250 points for this tier upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back later once you have saved more points.").start(player);
																	} else if (player.getInventory().containsAll(11665, 11664, 11663, 8839, 8840, 13072, 13073) && player.getPoints().get(AttributeManager.Points.COMMENDATION) >= 250) { // Has all the pieces to upgrade but not enough points
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13072, 200)
																				.setText("You hand over the Void knight equipment.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																				.setText("My pleasure to upgrade your Void knight equipment", "to the next tier " + player.getUsername() +"!")
																				.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15295, 200)
																				.setText("The Elite Void Knight hands you the new equipment.")
																				.setAction(player4 -> {
																					player.getInventory().delete(new Item(11665, 1));
																					player.getInventory().delete(new Item(11664, 1));
																					player.getInventory().delete(new Item(11663, 1));
																					player.getInventory().delete(new Item(8839, 1));
																					player.getInventory().delete(new Item(8840, 1));
																					player.getInventory().delete(new Item(13072, 1));
																					player.getInventory().delete(new Item(13073, 1));
																					player.getPoints().decrease(AttributeManager.Points.COMMENDATION, 250);
																					player.getInventory().add(new Item(15294, 1));
																					player.getInventory().add(new Item(15293, 1));
																					player.getInventory().add(new Item(15292, 1));
																					player.getInventory().add(new Item(15290, 1));
																					player.getInventory().add(new Item(15291, 1));
																					player.getInventory().add(new Item(15295, 1));
																					player.getInventory().add(new Item(15296, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15294, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15293, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15292, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15290, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15291, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15295, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15296, 1));
																					new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																							.setText("Enjoy your new Void Knight equipment!").start(player);
																				}).start(player);
																	}


																}).start(player);

													}).secondOption("I have changed my mind.", player3 -> {
														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("I have changed my mind.").start(player);
														new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
																.setText("Go away then!").start(player);
													}).start(player);
										}).secondOption("<col=27ae60>Radiant Emerald (Trim) Equipment Set</col>", player2 -> {

											new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
													.setText("I would like the <col=27ae60>Radiant Emerald (Trim) Equipment Set</col>", "Set upgrade.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
													.setText("Elegant choice " + player.getUsername() +"! To proceed with the upgrade,", "you will need <col=800080>250 Commendation points</col>.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
													.setText("Along with each and every piece of your trimmed", "<col=1E90FF>Baby Blue (Trim) Equipment Set</col> for this.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
													.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
													.add(DialogueType.OPTION).setOptionTitle("Select an Option")
													.firstOption("Yes I would like to upgrade it.", player3 -> {


														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("Yes I would like to upgrade it.").setPostAction ($ -> {
																	if (!player.getInventory().containsAny(15294, 15293, 15292, 15290, 15291, 15295, 15296)) { // Doesn't have any of the pieces to upgrade
																		new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																				.setText("You don't seem to have anything for me to upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back once you have the Void knight equipment", "and <col=800080>250 Commendation points</col> ready.").start(player);
																	} else if (!player.getInventory().containsAll(15294, 15293, 15292, 15290, 15291, 15295, 15296) && player.getInventory().containsAny(15294, 15293, 15292, 15290, 15291, 15295, 15296)) { // Has some pieces to upgrade but not all
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15290 + Misc.random(6), 200)
																				.setText("You hand over the Void Knight equipment.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("You are missing some of the required pieces " + player.getUsername() +".")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("You must have three of the Void knight helmets, top, robe,", "and the Elite void top and robe for the upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back later once you have them ready.").start(player);
																	} else if (player.getInventory().containsAll(15294, 15293, 15292, 15290, 15291, 15295, 15296) && player.getPoints().get(AttributeManager.Points.COMMENDATION) < 250) { // Has all the pieces to upgrade but not enough points
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15290 + Misc.random(6), 200)
																				.setText("You hand over the Void Knight equipment.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																				.setText("Oh dear... you don't have enough <col=800080>Commendation points</col>.", "You need at least 250 points for this tier upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back later once you have saved more points.").start(player);
																	} else if (player.getInventory().containsAll(15294, 15293, 15292, 15290, 15291, 15295, 15296) && player.getPoints().get(AttributeManager.Points.COMMENDATION) >= 250) { // Has all the pieces to upgrade but not enough points
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15290 + Misc.random(6), 200)
																				.setText("You hand over the Void knight equipment.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																				.setText("My pleasure to upgrade your Void knight equipment", "to the next tier " + player.getUsername() +"!")
																				.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15452, 200)
																				.setText("The Elite Void Knight hands you the new equipment.")
																				.setAction(player4 -> {
																					player.getInventory().delete(new Item(15294, 1));
																					player.getInventory().delete(new Item(15293, 1));
																					player.getInventory().delete(new Item(15292, 1));
																					player.getInventory().delete(new Item(15290, 1));
																					player.getInventory().delete(new Item(15291, 1));
																					player.getInventory().delete(new Item(15295, 1));
																					player.getInventory().delete(new Item(15296, 1));
																					player.getPoints().decrease(AttributeManager.Points.COMMENDATION, 250);
																					player.getInventory().add(new Item(15451, 1));
																					player.getInventory().add(new Item(15450, 1));
																					player.getInventory().add(new Item(15449, 1));
																					player.getInventory().add(new Item(15447, 1));
																					player.getInventory().add(new Item(15448, 1));
																					player.getInventory().add(new Item(15452, 1));
																					player.getInventory().add(new Item(15453, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15451, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15450, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15449, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15447, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15448, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15452, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15453, 1));
																					new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																							.setText("Enjoy your new Void Knight equipment!").start(player);
																				}).start(player);
																	}


																}).start(player);

													}).secondOption("I have changed my mind.", player3 -> {
														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("I have changed my mind.").start(player);
														new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
																.setText("Go away then!").start(player);
													}).start(player);
										}).thirdOption("<col=800080>Fiery Violet (Trim) Equipment Set</col>", player2 -> {

											new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
													.setText("I would like the <col=800080>Fiery Violet (Trim) Equipment Set</col>", "Set upgrade.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
													.setText("Stunning choice " + player.getUsername() +"! To proceed with the upgrade,", "you will need <col=800080>250 Commendation points</col>.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
													.setText("Along with each and every piece of your trimmed", "<col=27ae60>Radiant Emerald (Trim) Equipment Set</col> for this.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
													.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
													.add(DialogueType.OPTION).setOptionTitle("Select an Option")
													.firstOption("Yes I would like to upgrade it.", player3 -> {


														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("Yes I would like to upgrade it.").setPostAction ($ -> {
																	if (!player.getInventory().containsAny(15451, 15450, 15449, 15447, 15448, 15452, 15453)) { // Doesn't have any of the pieces to upgrade
																		new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																				.setText("You don't seem to have anything for me to upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back later once you have the Void knight equipment", "and <col=800080>250 Commendation points</col> ready.").start(player);
																	} else if (!player.getInventory().containsAll(15451, 15450, 15449, 15447, 15448, 15452, 15453) && player.getInventory().containsAny(15451, 15450, 15449, 15447, 15448, 15452, 15453)) { // Has some pieces to upgrade but not all
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15447 + Misc.random(6), 200)
																				.setText("You hand over the Void Knight equipment.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("You are missing some of the required pieces " + player.getUsername() +".")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("You must have three of the Void knight helmets, top, robe,", "and the Elite void top and robe for the upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back later once you have them ready.").start(player);
																	} else if (player.getInventory().containsAll(15451, 15450, 15449, 15447, 15448, 15452, 15453) && player.getPoints().get(AttributeManager.Points.COMMENDATION) < 250) { // Has all the pieces to upgrade but not enough points
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15447 + Misc.random(6), 200)
																				.setText("You hand over the Void Knight equipment.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																				.setText("Oh dear... you don't have enough <col=800080>Commendation points</col>.", "You need at least 250 points for this tier upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back later once you have saved more points.").start(player);
																	} else if (player.getInventory().containsAll(15451, 15450, 15449, 15447, 15448, 15452, 15453) && player.getPoints().get(AttributeManager.Points.COMMENDATION) >= 250) { // Has all the pieces to upgrade but not enough points
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15447 + Misc.random(6), 200)
																				.setText("You hand over the Void knight equipment.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																				.setText("My pleasure to upgrade your Void knight equipment", "to the next tier " + player.getUsername() +"!")
																				.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15459, 200)
																				.setText("The Elite Void Knight hands you the new equipment.")
																				.setAction(player4 -> {
																					player.getInventory().delete(new Item(15451, 1));
																					player.getInventory().delete(new Item(15450, 1));
																					player.getInventory().delete(new Item(15449, 1));
																					player.getInventory().delete(new Item(15447, 1));
																					player.getInventory().delete(new Item(15448, 1));
																					player.getInventory().delete(new Item(15452, 1));
																					player.getInventory().delete(new Item(15453, 1));
																					player.getPoints().decrease(AttributeManager.Points.COMMENDATION, 250);
																					player.getInventory().add(new Item(15458, 1));
																					player.getInventory().add(new Item(15457, 1));
																					player.getInventory().add(new Item(15456, 1));
																					player.getInventory().add(new Item(15454, 1));
																					player.getInventory().add(new Item(15455, 1));
																					player.getInventory().add(new Item(15459, 1));
																					player.getInventory().add(new Item(15460, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15458, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15457, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15456, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15454, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15455, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15459, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15460, 1));
																					new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																							.setText("Enjoy your new Void Knight equipment!").start(player);
																				}).start(player);
																	}


																}).start(player);

													}).secondOption("I have changed my mind.", player3 -> {
														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("I have changed my mind.").start(player);
														new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
																.setText("Go away then!").start(player);
													}).start(player);
										}).fourthOption("<col=900D09>Blaze Scarlet (Trim) Equipment Set</col>", player2 -> {
											new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
													.setText("I would like the <col=900D09>Blaze Scarlet (Trim) Equipment Set</col>", "Set upgrade.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
													.setText("Stunning choice " + player.getUsername() +"! To proceed with the upgrade,", "you will need <col=800080>350 Commendation points</col>.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
													.setText("Along with each and every piece of your trimmed", "<col=800080>Fiery Violet (Trim) Equipment Set</col> for this.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
													.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
													.add(DialogueType.OPTION).setOptionTitle("Select an Option")
													.firstOption("Yes I would like to upgrade it.", player3 -> {


														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("Yes I would like to upgrade it.").setPostAction ($ -> {
																	if (!player.getInventory().containsAny(15458, 15457, 15456, 15454, 15455, 15459, 15460)) { // Doesn't have any of the pieces to upgrade
																		new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																				.setText("You don't seem to have anything for me to upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back later once you have the Void knight equipment", "and <col=800080>350 Commendation points</col> ready.").start(player);
																	} else if (!player.getInventory().containsAll(15458, 15457, 15456, 15454, 15455, 15459, 15460) && player.getInventory().containsAny(15458, 15457, 15456, 15454, 15455, 15459, 15460)) { // Has some pieces to upgrade but not all
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15454 + Misc.random(6), 200)
																				.setText("You hand over the Void Knight equipment.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("You are missing some of the required pieces " + player.getUsername() +".")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("You must have three of the Void knight helmets, top, robe,", "and the Elite void top and robe for the upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back later once you have them ready.").start(player);
																	} else if (player.getInventory().containsAll(15458, 15457, 15456, 15454, 15455, 15459, 15460) && player.getPoints().get(AttributeManager.Points.COMMENDATION) < 350) { // Has all the pieces to upgrade but not enough points
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15454 + Misc.random(6), 200)
																				.setText("You hand over the Void Knight equipment.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																				.setText("Oh dear... you don't have enough <col=800080>Commendation points</col>.", "You need at least 250 points for this tier upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back later once you have saved more points.").start(player);
																	} else if (player.getInventory().containsAll(15458, 15457, 15456, 15454, 15455, 15459, 15460) && player.getPoints().get(AttributeManager.Points.COMMENDATION) >= 350) { // Has all the pieces to upgrade but not enough points
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15454 + Misc.random(6), 200)
																				.setText("You hand over the Void knight equipment.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																				.setText("My pleasure to upgrade your Void knight equipment", "to the next tier " + player.getUsername() +"!")
																				.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15445, 200)
																				.setText("The Elite Void Knight hands you the new equipment.")
																				.setAction(player4 -> {
																					player.getInventory().delete(new Item(15458, 1));
																					player.getInventory().delete(new Item(15457, 1));
																					player.getInventory().delete(new Item(15456, 1));
																					player.getInventory().delete(new Item(15454, 1));
																					player.getInventory().delete(new Item(15455, 1));
																					player.getInventory().delete(new Item(15459, 1));
																					player.getInventory().delete(new Item(15460, 1));
																					player.getPoints().decrease(AttributeManager.Points.COMMENDATION, 350);
																					player.getInventory().add(new Item(15444, 1));
																					player.getInventory().add(new Item(15443, 1));
																					player.getInventory().add(new Item(15442, 1));
																					player.getInventory().add(new Item(15440, 1));
																					player.getInventory().add(new Item(15441, 1));
																					player.getInventory().add(new Item(15445, 1));
																					player.getInventory().add(new Item(15446, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15444, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15443, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15442, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15440, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15441, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15445, 1));
																					player.getCollectionLog().createOrUpdateEntry(player,  "Pest Control", new Item(15446, 1));
																					new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																							.setText("Enjoy your new Void Knight equipment!").start(player);
																				}).start(player);
																	}


																}).start(player);

													}).secondOption("I have changed my mind.", player3 -> {
														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("I have changed my mind.").start(player);
														new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
																.setText("Go away then!").start(player);
													}).start(player);
										}).fifthOption("I am not interested in upgrading.", player2 -> {
											new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
													.setText("I am not interested in upgrading.").start(player);
										}).start(player);


							}).start(player);
					break;
				case ALI_THE_CAMEL_MAN:
					DialogueManager.start(player, 2712);
					break;
				case PILES:
					DialogueManager.start(player, 2711);
					break;
				/*case 1030: // Santa
					DialogueManager.start(player, 2717);
					break;*/
				case IFFIE:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Sorry, dearie, if I stop to chat I'll lose count. Talk to", "my sister instead; she likes to chat. You'll find her", "upstairs in the church.")
							.start(player);
					break;
				case GAMER:
					if (Misc.random(3) == 1) {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("" + player.getUsername() + ", do you want to join the lotto?")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("What do you mean by lotto?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Oh well, lottery! It's very famous term here.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Let me help you, what do you need?")
								.add(DialogueType.OPTION).setOptionTitle("Select an Option")
								.firstOption("What is lottery?", player1 -> {
									new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
											.setText("What is lottery?")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("A lottery is a form of gambling that involves the", "drawing of numbers at random for a prize.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Upon entry you buy tickets with your name on it.", "The more tickets the more likely you are to be a winner.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("If the ticket drawn had your name on it, then you", "are considered to be the winner.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Winners get the total amount of the lottery, so if", "there was 500m lottery, you can win it all.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
											.setText("Do you want to join the lottery now?")
											.add(DialogueType.OPTION).setOptionTitle("Select an Option")
											.firstOption("Yes.", player2 -> {
												Lottery.startGamblerDialogue(player);

											}).secondOption("No.", player2 -> {
												player.getPacketSender().sendInterfaceRemoval();
												}).start(player);
								}).secondOption("I want to enter.", player1 -> {
										Lottery.startGamblerDialogue(player);

						}).thirdOption("How often do you draw a winner?", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("How often do you draw a winner?")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("The server draws and announces a winner once", "every 12 hours.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("You will get notified upon login if you are the winner.")

									.start(player);
						}).fourthOption("Are there any refunds?", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("Are there any refunds?")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Once you've entered the lottery there is no way", "to undo this action.")
									.start(player);
						}).fifthOption("I don't like to gamble.", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.DISTRESSED)
									.setText("I'm not into gambling much.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
									.setText("Okay then! Goodbye!")
									.start(player);

						}).start(player);
					} else if (Misc.random(3) == 2) {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("" + player.getUsername() + ", I'm sure you're up for a click!")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("Hmm, why are you wearing this hat?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("A secret name hides in there for the lottery.")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("Let's see then what you've got?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("What do you want to learn about?")
								.add(DialogueType.OPTION).setOptionTitle("Select an Option")
								.firstOption("What is lottery?", player1 -> {
									new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
											.setText("What is lottery?")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("A lottery is a form of gambling that involves the", "drawing of numbers at random for a prize.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Upon entry you buy tickets with your name on it.", "The more tickets the more likely you are to be a winner.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("If the ticket drawn had your name on it, then you", "are considered to be the winner.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Winners get the total amount of the lottery, so if", "there was 500m lottery, you can win it all.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
											.setText("Do you want to join the lottery now?")
											.add(DialogueType.OPTION).setOptionTitle("Select an Option")
											.firstOption("Yes.", player2 -> {
												Lottery.startGamblerDialogue(player);

											}).secondOption("No.", player2 -> {
										player.getPacketSender().sendInterfaceRemoval();
									}).start(player);
								}).secondOption("I want to enter.", player1 -> {
							Lottery.startGamblerDialogue(player);

						}).thirdOption("How often do you draw a winner?", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("How often do you draw a winner?")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("The server draws and announces a winner once", "every 12 hours.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("You will get notified upon login if you are the winner.")

									.start(player);
						}).fourthOption("Are there any refunds?", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("Are there any refunds?")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Once you've entered the lottery there is no way", "to undo this action.")
									.start(player);
						}).fifthOption("I don't like to gamble.", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.DISTRESSED)
									.setText("I'm not into gambling much.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
									.setText("Okay then! Goodbye!")
									.start(player);

						}).start(player);
					} else {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Did you know the lottery can reach up to 2147m?")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("What does that mean?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("It means you can win it all!")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("How do I do that?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Looks like you're new here, what do you want", "to know about lottery?")
								.add(DialogueType.OPTION).setOptionTitle("Select an Option")
								.firstOption("What is lottery?", player1 -> {
									new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
											.setText("What is lottery?")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("A lottery is a form of gambling that involves the", "drawing of numbers at random for a prize.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Upon entry you buy tickets with your name on it.", "The more tickets the more likely you are to be a winner.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("If the ticket drawn had your name on it, then you", "are considered to be the winner.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Winners get the total amount of the lottery, so if", "there was 500m lottery, you can win it all.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
											.setText("Do you want to join the lottery now?")
											.add(DialogueType.OPTION).setOptionTitle("Select an Option")
											.firstOption("Yes.", player2 -> {
												Lottery.startGamblerDialogue(player);

											}).secondOption("No.", player2 -> {
										player.getPacketSender().sendInterfaceRemoval();
									}).start(player);
								}).secondOption("I want to enter.", player1 -> {
							Lottery.startGamblerDialogue(player);

						}).thirdOption("How often do you draw a winner?", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("How often do you draw a winner?")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("The server draws and announces a winner once", "every 12 hours.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("You will get notified upon login if you are the winner.")

									.start(player);
						}).fourthOption("Are there any refunds?", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("Are there any refunds?")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Once you've entered the lottery there is no way", "to undo this action.")
									.start(player);
						}).fifthOption("I don't like to gamble.", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.DISTRESSED)
									.setText("I'm not into gambling much.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
									.setText("Okay then! Goodbye!")
									.start(player);

						}).start(player);
					}
					break;
				case TURAEL:
				case MAZCHNA:
				case VANNAKA:
				case CHAELDAR:
				case DURADEL:
				case KRYSTILIA:
				case NIEVE:
				case 8623:
					SlayerManager.talkToSlayerMaster(player, npc.getId());
					break;
				case HIGH_PRIEST:
					ShopManager.open(player, ShopIdentifiers.HIGH_PRIEST_STORE);
					break;
				case TICKET_MERCHANT:
					ShopManager.open(player, ShopIdentifiers.RANGING_GUILD_STORE);
					break;
				case CANDLE_MAKER:
					ShopManager.open(player, ShopIdentifiers.CANDLE_STORE);
					break;
				case KILLER:
					ShopManager.open(player, ShopIdentifiers.WILDERNESS_ITEMS_STORE);
					break;
				case GAMER_1014:
					ShopManager.open(player, ShopIdentifiers.GAMBLING_STORE);
					break;
				case LUMBRIDGE_GUIDE:
					WelcomeManager.welcome(player, WelcomeStage.STARTER);
					break;
				case LOWE:
					ShopManager.open(player, ShopIdentifiers.RANGE_GEAR_STORE);
					break;
				case WISTAN:
					ShopManager.open(player, ShopIdentifiers.BURTHORPE_SUPPLIES);
					break;
				case GRACE:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Hey there, Grace.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Hey there " + player.getUsername() +", what are you doing here?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("I don't have much time to talk right now.")
							.add(DialogueType.PLAYER_STATEMENT)
							.setText("I just have a few questions about", "the <col=800080>Marks of grace</col> related.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Let me know what you're looking for?").add(DialogueType.OPTION).setOptionTitle("Select an Option")
							.firstOption("How do I get Marks of grace?", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
										.setText("How to I get Marks of grace?")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("It's quite easy! There are two ways to get it.", "The first way is by tagging the pillars in the", "Brimhaven Agility Arena which rewards you a few points", "on each time you complete the tag.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("The second way is by speaking to Captain Izzy in Brimhaven", "and getting an Agility skill task.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("By completing the task you can choose to get rewarded", "coins or Agility points.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("I prefer to choose the Agility points as you can use it to buy", "extra <col=800080>Marks of grace</col> or bonus Agility XP from tomes.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Now make up your mind I have told you everything.")
										.add(DialogueType.PLAYER_STATEMENT)
										.setText("Sounds good Grace, I will get going.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Goodbye.")
										.start(player);

							}).secondOption("I would like to know about gear upgrade.", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
										.setText("I would like to know about gear upgrade.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Well it's fairly simple and easy " + player.getUsername() +".")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("You can upgrade your Graceful gear set into the upgraded", "variant of it which provides bonus Agility experience", "and a unique attractive color.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("The bonus experience is only provided when wearing the outfit.", "In addition, it also reduces your weight and chance", "to fall from an obstacle.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("The basic set provides 30 % bonus XP and goes up to", "70 % bonus experience on the best set.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Do not forget that Agility hood, Spottier cape, Boots of lightness,", "and Penance gloves also provide 5 % bonus Agility XP.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("The skill cape also provide you with 20 % bonus experience.", "All of the experience can stack together.")
										.add(DialogueType.PLAYER_STATEMENT)
										.setText("That sounds interesting.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Have a good day.")
										.start(player);


							}).thirdOption("I would like to upgrade my Graceful gear.", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
										.setText("I would like to upgrade my Graceful gear.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Sure! The upgraded set offers increased experience", "and benefits.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Let me know which upgrade are you looking for.")
										.add(DialogueType.OPTION).setOptionTitle("Select an Option")
										.firstOption("<col=8A5099>Arceuus Graceful Outfit</col>", player2 -> {

											new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
													.setText("I would like have the <col=8A5099>Arceuus Graceful Outfit</col>", "upgrade.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
													.setText("Great start! To proceed with the upgrade,", "you will need <col=800080>350 Marks of grace</col>.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
													.setText("Along with the regular Graceful outfit in your inventory.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
													.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
													.add(DialogueType.OPTION).setOptionTitle("Select an Option")
													.firstOption("Proceed with the upgrade.", player3 -> {


														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("Proceed with the upgrade.").setPostAction ($ -> {
																	if (!player.getInventory().containsAny(11850, 11852, 11854, 11856, 11858, 11860)) { // Doesn't have any of the pieces to upgrade
																		new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																				.setText("You don't have any Graceful outfit to upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back once you have the Graceful Outfit", "and <col=800080>350 Marks of grace</col> ready.").start(player);
																	} else if (!player.getInventory().containsAll(11850, 11852, 11854, 11856, 11858, 11860) && player.getInventory().containsAny(11850, 11852, 11854, 11856, 11858, 11860)) { // Has some pieces to upgrade but not all
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13072, 200)
																				.setText("You hand over the Graceful Outfit.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("You are missing some of the required pieces " + player.getUsername() +".")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("You must have all of the Graceful Outfit set", "to be able to proceed with this.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back once you have all of them ready with you.").start(player);
																	} else if (player.getInventory().containsAll(11850, 11852, 11854, 11856, 11858, 11860) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 350) { // Has all the pieces to upgrade but not enough points
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(11854, 200)
																				.setText("You hand over the Graceful Outfit.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																				.setText("I'm sorry, you need to have 350 <col=800080>Marks of grace</col>", "for the <col=8A5099>Arceuus Graceful Outfit</col> upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Pass over once you have them all ready.").start(player);
																	} else if (player.getInventory().containsAll(11850, 11852, 11854, 11856, 11858, 11860) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 350) { // Has all the pieces to upgrade but not enough points
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(11854, 200)
																				.setText("You hand over the Graceful Outfit.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																				.setText("I see you have become better in Agility skill already.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																				.setText("You are truly eligible for this upgrade.")
																				.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13583, 200)
																				.setText("Grace hands you over the new <col=8A5099>Arceuus Graceful Outfit</col>.")
																				.setAction(player4 -> {
																					player.getInventory().delete(new Item(11850, 1));
																					player.getInventory().delete(new Item(11852, 1));
																					player.getInventory().delete(new Item(11854, 1));
																					player.getInventory().delete(new Item(11856, 1));
																					player.getInventory().delete(new Item(11858, 1));
																					player.getInventory().delete(new Item(11860, 1));
																					player.getInventory().delete(ItemID.MARK_OF_GRACE, 350);
																					player.getInventory().add(new Item(13579, 1));
																					player.getInventory().add(new Item(13581, 1));
																					player.getInventory().add(new Item(13583, 1));
																					player.getInventory().add(new Item(13585, 1));
																					player.getInventory().add(new Item(13587, 1));
																					player.getInventory().add(new Item(13589, 1));
																					new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																							.setText("Enjoy your new <col=8A5099>Arceuus Graceful Outfit</col>!").start(player);
																				}).start(player);
																	}


																}).start(player);

													}).secondOption("I'am not planning on upgrading.", player3 -> {
														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("I'am not planning on upgrading.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
																.setText("As you wish...").start(player);
													}).start(player);
										}).secondOption("<col=77CBCF>Port Piscarilius Graceful Outfit</col>", player2 -> {

									new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
											.setText("I would like have the <col=77CBCF>Port Piscarilius Graceful Outfit</col>", "upgrade.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
											.setText("Brilliant choice! To proceed with the upgrade,", "you will need <col=800080>500 Marks of grace</col>.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Along with the <col=8A5099>Arceuus Graceful Outfit</col> in your inventory.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
											.add(DialogueType.OPTION).setOptionTitle("Select an Option")
											.firstOption("Proceed with the upgrade.", player3 -> {


												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("Proceed with the upgrade.").setPostAction ($ -> {
															if (!player.getInventory().containsAny(13579, 13581, 13583, 13585, 13587, 13589)) { // Doesn't have any of the pieces to upgrade
																new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																		.setText("You don't have any of the", "<col=8A5099>Arceuus Graceful Outfit</col> to upgrade.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("Come back once you have the <col=8A5099>Arceuus Graceful Outfit</col>", "and <col=800080>500 Marks of grace</col> ready.").start(player);
															} else if (!player.getInventory().containsAll(13579, 13581, 13583, 13585, 13587, 13589) && player.getInventory().containsAny(13579, 13581, 13583, 13585, 13587, 13589)) { // Has some pieces to upgrade but not all
																new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13583, 200)
																		.setText("You hand over the <col=8A5099>Arceuus Graceful Outfit</col>.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("You are missing some of the required pieces " + player.getUsername() +".")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("You must have all of the <col=8A5099>Arceuus Graceful Outfit</col>", "set to be able to proceed with this.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("Come back once you have all of them ready with you.").start(player);
															} else if (player.getInventory().containsAll(13579, 13581, 13583, 13585, 13587, 13589) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 500) { // Has all the pieces to upgrade but not enough points
																new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13583, 200)
																		.setText("You hand over the <col=8A5099>Arceuus Graceful Outfit</col>.")
																		.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																		.setText("I'm sorry, you need to have 500 <col=800080>Marks of grace</col>", "for the <col=77CBCF>Port Piscarilius Graceful Outfit</col> upgrade.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("Pass over once you have them all ready.").start(player);
															} else if (player.getInventory().containsAll(13579, 13581, 13583, 13585, 13587, 13589) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 500) { // Has all the pieces to upgrade but not enough points
																new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13583, 200)
																		.setText("You hand over the <col=8A5099>Arceuus Graceful Outfit</col>.")
																		.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																		.setText("I see you have become even better in Agility skill already.")
																		.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																		.setText("You are surely eligible for this upgrade.")
																		.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13595, 200)
																		.setText("Grace hands you over the new <col=77CBCF>Port Piscarilius Graceful Outfit</col>.")
																		.setAction(player4 -> {
																			player.getInventory().delete(new Item(13579, 1));
																			player.getInventory().delete(new Item(13581, 1));
																			player.getInventory().delete(new Item(13583, 1));
																			player.getInventory().delete(new Item(13585, 1));
																			player.getInventory().delete(new Item(13587, 1));
																			player.getInventory().delete(new Item(13589, 1));
																			player.getInventory().delete(ItemID.MARK_OF_GRACE, 500);
																			player.getInventory().add(new Item(13591, 1));
																			player.getInventory().add(new Item(13593, 1));
																			player.getInventory().add(new Item(13595, 1));
																			player.getInventory().add(new Item(13597, 1));
																			player.getInventory().add(new Item(13599, 1));
																			player.getInventory().add(new Item(13601, 1));
																			new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																					.setText("Enjoy your new <col=77CBCF>Port Piscarilius Graceful Outfit</col>!").start(player);
																		}).start(player);
															}


														}).start(player);

											}).secondOption("I'am not planning on upgrading.", player3 -> {
												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("I'am not planning on upgrading.")
														.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
														.setText("As you wish...").start(player);
											}).start(player);
										}).thirdOption("<col=EACF21>Lovakengj Graceful Outfit</col>", player2 -> {

											new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
													.setText("I would like have the <col=EACF21>Lovakengj Graceful Outfit</col>", "upgrade.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
													.setText("Brilliant choice! To proceed with the upgrade,", "you will need <col=800080>750 Marks of grace</col>.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
													.setText("Along with the <col=77CBCF>Port Piscarilius Graceful Outfit</col> in your inventory.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
													.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
													.add(DialogueType.OPTION).setOptionTitle("Select an Option")
													.firstOption("Proceed with the upgrade.", player3 -> {


														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("Proceed with the upgrade.").setPostAction ($ -> {
																	if (!player.getInventory().containsAny(13591, 13593, 13595, 13597, 13599, 13601)) { // Doesn't have any of the pieces to upgrade
																		new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																				.setText("You don't have any of the", "<col=77CBCF>Port Piscarilius Graceful Outfit</col> to upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back once you have the <col=77CBCF>Port Piscarilius Graceful Outfit</col>", "and <col=800080>750 Marks of grace</col> ready.").start(player);
																	} else if (!player.getInventory().containsAll(13591, 13593, 13595, 13597, 13599, 13601) && player.getInventory().containsAny(13591, 13593, 13595, 13597, 13599, 13601)) { // Has some pieces to upgrade but not all
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13595, 200)
																				.setText("You hand over the <col=77CBCF>Port Piscarilius Graceful Outfit</col>.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("You are missing some of the required pieces " + player.getUsername() +".")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("You must have all of the <col=77CBCF>Port Piscarilius Graceful Outfit</col>", "set to be able to proceed with this.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back once you have all of them ready with you.").start(player);
																	} else if (player.getInventory().containsAll(13591, 13593, 13595, 13597, 13599, 13601) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 750) { // Has all the pieces to upgrade but not enough points
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13595, 200)
																				.setText("You hand over the <col=77CBCF>Port Piscarilius Graceful Outfit</col>.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																				.setText("I'm sorry, you need to have 750 <col=800080>Marks of grace</col>", "for the <col=EACF21>Lovakengj Graceful Outfit</col> upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Pass over once you have them all ready.").start(player);
																	} else if (player.getInventory().containsAll(13591, 13593, 13595, 13597, 13599, 13601) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 750) { // Has all the pieces to upgrade but not enough points
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13595, 200)
																				.setText("You hand over the <col=77CBCF>Port Piscarilius Graceful Outfit</col>.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																				.setText("I see you have become even better in Agility skill already.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																				.setText("You are surely eligible for this upgrade.")
																				.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13607, 200)
																				.setText("Grace hands you over the new <col=EACF21>Lovakengj Graceful Outfit</col>.")
																				.setAction(player4 -> {
																					player.getInventory().delete(new Item(13591, 1));
																					player.getInventory().delete(new Item(13593, 1));
																					player.getInventory().delete(new Item(13595, 1));
																					player.getInventory().delete(new Item(13597, 1));
																					player.getInventory().delete(new Item(13599, 1));
																					player.getInventory().delete(new Item(13601, 1));
																					player.getInventory().delete(ItemID.MARK_OF_GRACE, 750);
																					player.getInventory().add(new Item(13603, 1));
																					player.getInventory().add(new Item(13605, 1));
																					player.getInventory().add(new Item(13607, 1));
																					player.getInventory().add(new Item(13609, 1));
																					player.getInventory().add(new Item(13611, 1));
																					player.getInventory().add(new Item(13613, 1));
																					new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																							.setText("Enjoy your new <col=EACF21>Lovakengj Graceful Outfit</col>!").start(player);
																				}).start(player);
																	}


																}).start(player);

													}).secondOption("I'am not planning on upgrading.", player3 -> {
														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("I'am not planning on upgrading.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
																.setText("As you wish...").start(player);
													}).start(player);
										}).fourthOption("<col=81230D>Shayzien Graceful Outfit</col>", player2 -> {

											new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
													.setText("I would like have the <col=81230D>Shayzien Graceful Outfit</col>", "upgrade.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
													.setText("Brilliant choice! To proceed with the upgrade,", "you will need <col=800080>1000 Marks of grace</col>.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
													.setText("Along with the <col=EACF21>Lovakengj Graceful Outfit</col> in your inventory.")
													.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
													.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
													.add(DialogueType.OPTION).setOptionTitle("Select an Option")
													.firstOption("Proceed with the upgrade.", player3 -> {


														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("Proceed with the upgrade.").setPostAction ($ -> {
																	if (!player.getInventory().containsAny(13603, 13605, 13607, 13609, 13611, 13613)) { // Doesn't have any of the pieces to upgrade
																		new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																				.setText("You don't have any of the", "<col=EACF21>Lovakengj Graceful Outfit</col> to upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back once you have the <col=EACF21>Lovakengj Graceful Outfit</col>", "and <col=800080>1000 Marks of grace</col> ready.").start(player);
																	} else if (!player.getInventory().containsAll(13603, 13605, 13607, 13609, 13611, 13613) && player.getInventory().containsAny(13603, 13605, 13607, 13609, 13611, 13613)) { // Has some pieces to upgrade but not all
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13607, 200)
																				.setText("You hand over the <col=EACF21>Lovakengj Graceful Outfit</col>.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("You are missing some of the required pieces " + player.getUsername() +".")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("You must have all of the <col=EACF21>Lovakengj Graceful Outfit</col>", "set to be able to proceed with this.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Come back once you have all of them ready with you.").start(player);
																	} else if (player.getInventory().containsAll(13603, 13605, 13607, 13609, 13611, 13613) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 1000) { // Has all the pieces to upgrade but not enough points
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13607, 200)
																				.setText("You hand over the <col=EACF21>Lovakengj Graceful Outfit</col>.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																				.setText("I'm sorry, you need to have 1000 <col=800080>Marks of grace</col>", "for the <col=81230D>Shayzien Graceful Outfit</col> upgrade.")
																				.add(DialogueType.NPC_STATEMENT)
																				.setText("Pass over once you have them all ready.").start(player);
																	} else if (player.getInventory().containsAll(13603, 13605, 13607, 13609, 13611, 13613) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 1000) { // Has all the pieces to upgrade but not enough points
																		new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13607, 200)
																				.setText("You hand over the <col=EACF21>Lovakengj Graceful Outfit</col>.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																				.setText("I see you have become even better in Agility skill already.")
																				.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																				.setText("You are surely eligible for this upgrade.")
																				.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13619, 200)
																				.setText("Grace hands you over the new <col=81230D>Shayzien Graceful Outfit</col>.")
																				.setAction(player4 -> {
																					player.getInventory().delete(new Item(13603, 1));
																					player.getInventory().delete(new Item(13605, 1));
																					player.getInventory().delete(new Item(13607, 1));
																					player.getInventory().delete(new Item(13609, 1));
																					player.getInventory().delete(new Item(13611, 1));
																					player.getInventory().delete(new Item(13613, 1));
																					player.getInventory().delete(ItemID.MARK_OF_GRACE, 1000);
																					player.getInventory().add(new Item(13615, 1));
																					player.getInventory().add(new Item(13617, 1));
																					player.getInventory().add(new Item(13619, 1));
																					player.getInventory().add(new Item(13621, 1));
																					player.getInventory().add(new Item(13623, 1));
																					player.getInventory().add(new Item(13625, 1));
																					new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																							.setText("Enjoy your new <col=81230D>Shayzien Graceful Outfit</col>!").start(player);
																				}).start(player);
																	}


																}).start(player);

													}).secondOption("I'am not planning on upgrading.", player3 -> {
														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("I'am not planning on upgrading.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
																.setText("As you wish...").start(player);
													}).start(player);
										}).fifthOption("Next page...", player4 -> {
											new DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
													.firstOption("<col=1DC33E>Hosidius Graceful Outfit</col>", player2 -> {

														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("I would like have the <col=1DC33E>Hosidius Graceful Outfit</col>", "upgrade.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
																.setText("Brilliant choice! To proceed with the upgrade,", "you will need <col=800080>1250 Marks of grace</col>.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
																.setText("Along with the <col=81230D>Shayzien Graceful Outfit</col> in your inventory.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
																.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
																.add(DialogueType.OPTION).setOptionTitle("Select an Option")
																.firstOption("Proceed with the upgrade.", player3 -> {


																	new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																			.setText("Proceed with the upgrade.").setPostAction ($ -> {
																				if (!player.getInventory().containsAny(13615, 13617, 13619, 13621, 13623, 13625)) { // Doesn't have any of the pieces to upgrade
																					new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																							.setText("You don't have any of the", "<col=81230D>Shayzien Graceful Outfit</col> to upgrade.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("Come back once you have the <col=81230D>Shayzien Graceful Outfit</col>", "and <col=800080>1250 Marks of grace</col> ready.").start(player);
																				} else if (!player.getInventory().containsAll(13615, 13617, 13619, 13621, 13623, 13625) && player.getInventory().containsAny(13615, 13617, 13619, 13621, 13623, 13625)) { // Has some pieces to upgrade but not all
																					new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13619, 200)
																							.setText("You hand over the <col=81230D>Shayzien Graceful Outfit</col>.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("You are missing some of the required pieces " + player.getUsername() +".")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("You must have all of the <col=81230D>Shayzien Graceful Outfit</col>", "set to be able to proceed with this.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("Come back once you have all of them ready with you.").start(player);
																				} else if (player.getInventory().containsAll(13615, 13617, 13619, 13621, 13623, 13625) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 1250) { // Has all the pieces to upgrade but not enough points
																					new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13619, 200)
																							.setText("You hand over the <col=81230D>Shayzien Graceful Outfit</col>.")
																							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																							.setText("I'm sorry, you need to have 1250 <col=800080>Marks of grace</col>", "for the <col=81230D>Shayzien Graceful Outfit</col> upgrade.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("Pass over once you have them all ready.").start(player);
																				} else if (player.getInventory().containsAll(13615, 13617, 13619, 13621, 13623, 13625) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 1250) { // Has all the pieces to upgrade but not enough points
																					new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13619, 200)
																							.setText("You hand over the <col=81230D>Shayzien Graceful Outfit</col>.")
																							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																							.setText("I see you have become even better in Agility skill already.")
																							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																							.setText("You are surely eligible for this upgrade.")
																							.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13631, 200)
																							.setText("Grace hands you over the new <col=1DC33E>Hosidius Graceful Outfit</col>.")
																							.setAction(player5 -> {
																								player.getInventory().delete(new Item(13615, 1));
																								player.getInventory().delete(new Item(13617, 1));
																								player.getInventory().delete(new Item(13619, 1));
																								player.getInventory().delete(new Item(13621, 1));
																								player.getInventory().delete(new Item(13623, 1));
																								player.getInventory().delete(new Item(13625, 1));
																								player.getInventory().delete(ItemID.MARK_OF_GRACE, 1250);
																								player.getInventory().add(new Item(13627, 1));
																								player.getInventory().add(new Item(13629, 1));
																								player.getInventory().add(new Item(13631, 1));
																								player.getInventory().add(new Item(13633, 1));
																								player.getInventory().add(new Item(13635, 1));
																								player.getInventory().add(new Item(13637, 1));
																								new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																										.setText("Enjoy your new <col=1DC33E>Hosidius Graceful Outfit</col>!").start(player);
																							}).start(player);
																				}


																			}).start(player);

																}).secondOption("I'am not planning on upgrading.", player3 -> {
																	new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																			.setText("I'am not planning on upgrading.")
																			.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
																			.setText("As you wish...").start(player);
																}).start(player);
													}).secondOption("<col=D2DCD4>Great Kourend Graceful Outfit</col>", player2 -> {

														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("I would like have the <col=D2DCD4>Great Kourend Graceful Outfit</col>", "upgrade.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
																.setText("Brilliant choice! To proceed with the upgrade,", "you will need <col=800080>1500 Marks of grace</col>.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
																.setText("Along with the <col=1DC33E>Hosidius Graceful Outfit</col> in your inventory.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
																.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
																.add(DialogueType.OPTION).setOptionTitle("Select an Option")
																.firstOption("Proceed with the upgrade.", player3 -> {


																	new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																			.setText("Proceed with the upgrade.").setPostAction ($ -> {
																				if (!player.getInventory().containsAny(13627, 13629, 13631, 13633, 13635, 13637)) { // Doesn't have any of the pieces to upgrade
																					new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																							.setText("You don't have any of the", "<col=1DC33E>Hosidius Graceful Outfit</col> to upgrade.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("Come back once you have the <col=1DC33E>Hosidius Graceful Outfit</col>", "and <col=800080>1500 Marks of grace</col> ready.").start(player);
																				} else if (!player.getInventory().containsAll(13627, 13629, 13631, 13633, 13635, 13637) && player.getInventory().containsAny(13627, 13629, 13631, 13633, 13635, 13637)) { // Has some pieces to upgrade but not all
																					new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13631, 200)
																							.setText("You hand over the <col=1DC33E>Hosidius Graceful Outfit</col>.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("You are missing some of the required pieces " + player.getUsername() +".")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("You must have all of the <col=1DC33E>Hosidius Graceful Outfit</col>", "set to be able to proceed with this.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("Come back once you have all of them ready with you.").start(player);
																				} else if (player.getInventory().containsAll(13627, 13629, 13631, 13633, 13635, 13637) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 1500) { // Has all the pieces to upgrade but not enough points
																					new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13631, 200)
																							.setText("You hand over the <col=1DC33E>Hosidius Graceful Outfit</col>.")
																							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																							.setText("I'm sorry, you need to have 1500 <col=800080>Marks of grace</col>", "for the <col=1DC33E>Hosidius Graceful Outfit</col> upgrade.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("Pass over once you have them all ready.").start(player);
																				} else if (player.getInventory().containsAll(13627, 13629, 13631, 13633, 13635, 13637) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 1500) { // Has all the pieces to upgrade but not enough points
																					new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13631, 200)
																							.setText("You hand over the <col=1DC33E>Hosidius Graceful Outfit</col>.")
																							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																							.setText("I see you have become even better in Agility skill already.")
																							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																							.setText("You are surely eligible for this upgrade.")
																							.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13671, 200)
																							.setText("Grace hands you over the new <col=D2DCD4>Great Kourend Graceful Outfit</col>.")
																							.setAction(player5 -> {
																								player.getInventory().delete(new Item(13627, 1));
																								player.getInventory().delete(new Item(13629, 1));
																								player.getInventory().delete(new Item(13631, 1));
																								player.getInventory().delete(new Item(13633, 1));
																								player.getInventory().delete(new Item(13635, 1));
																								player.getInventory().delete(new Item(13637, 1));
																								player.getInventory().delete(ItemID.MARK_OF_GRACE, 1500);
																								player.getInventory().add(new Item(13667, 1));
																								player.getInventory().add(new Item(13669, 1));
																								player.getInventory().add(new Item(13671, 1));
																								player.getInventory().add(new Item(13673, 1));
																								player.getInventory().add(new Item(13675, 1));
																								player.getInventory().add(new Item(13677, 1));
																								new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																										.setText("Enjoy your new <col=D2DCD4>Great Kourend Graceful Outfit</col>!").start(player);
																							}).start(player);
																				}


																			}).start(player);

																}).secondOption("I'am not planning on upgrading.", player3 -> {
																	new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																			.setText("I'am not planning on upgrading.")
																			.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
																			.setText("As you wish...").start(player);
																}).start(player);
													}).thirdOption("<col=163583>Brimhaven Graceful Outfit</col>", player2 -> {

														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("I would like have the <col=163583>Brimhaven Graceful Outfit</col>", "upgrade.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
																.setText("Brilliant choice! To proceed with the upgrade,", "you will need <col=800080>1750 Marks of grace</col>.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
																.setText("Along with the <col=D2DCD4>Great Kourend Graceful Outfit</col> in your inventory.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
																.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
																.add(DialogueType.OPTION).setOptionTitle("Select an Option")
																.firstOption("Proceed with the upgrade.", player3 -> {


																	new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																			.setText("Proceed with the upgrade.").setPostAction ($ -> {
																				if (!player.getInventory().containsAny(13667, 13669, 13671, 13673, 13675, 13677)) { // Doesn't have any of the pieces to upgrade
																					new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																							.setText("You don't have any of the", "<col=D2DCD4>Great Kourend Graceful Outfit</col> to upgrade.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("Come back once you have the <col=D2DCD4>Great Kourend Graceful Outfit</col>", "and <col=800080>1750 Marks of grace</col> ready.").start(player);
																				} else if (!player.getInventory().containsAll(13667, 13669, 13671, 13673, 13675, 13677) && player.getInventory().containsAny(13667, 13669, 13671, 13673, 13675, 13677)) { // Has some pieces to upgrade but not all
																					new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13671, 200)
																							.setText("You hand over the <col=D2DCD4>Great Kourend Graceful Outfit</col>.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("You are missing some of the required pieces " + player.getUsername() +".")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("You must have all of the <col=D2DCD4>Great Kourend Graceful Outfit</col>", "set to be able to proceed with this.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("Come back once you have all of them ready with you.").start(player);
																				} else if (player.getInventory().containsAll(13667, 13669, 13671, 13673, 13675, 13677) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 1750) { // Has all the pieces to upgrade but not enough points
																					new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13671, 200)
																							.setText("You hand over the <col=D2DCD4>Great Kourend Graceful Outfit</col>.")
																							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																							.setText("I'm sorry, you need to have 1750 <col=800080>Marks of grace</col>", "for the <col=D2DCD4>Great Kourend Graceful Outfit</col> upgrade.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("Pass over once you have them all ready.").start(player);
																				} else if (player.getInventory().containsAll(13667, 13669, 13671, 13673, 13675, 13677) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 1750) { // Has all the pieces to upgrade but not enough points
																					new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13671, 200)
																							.setText("You hand over the <col=D2DCD4>Great Kourend Graceful Outfit</col>.")
																							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																							.setText("I see you have become even better in Agility skill already.")
																							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																							.setText("You are surely eligible for this upgrade.")
																							.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(21067, 200)
																							.setText("Grace hands you over the new <col=163583>Brimhaven Graceful Outfit</col>.")
																							.setAction(player5 -> {
																								player.getInventory().delete(new Item(13667, 1));
																								player.getInventory().delete(new Item(13669, 1));
																								player.getInventory().delete(new Item(13671, 1));
																								player.getInventory().delete(new Item(13673, 1));
																								player.getInventory().delete(new Item(13675, 1));
																								player.getInventory().delete(new Item(13677, 1));
																								player.getInventory().delete(ItemID.MARK_OF_GRACE, 1750);
																								player.getInventory().add(new Item(21061, 1));
																								player.getInventory().add(new Item(21064, 1));
																								player.getInventory().add(new Item(21067, 1));
																								player.getInventory().add(new Item(21070, 1));
																								player.getInventory().add(new Item(21073, 1));
																								player.getInventory().add(new Item(21076, 1));
																								new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																										.setText("Enjoy your new <col=163583>Brimhaven Graceful Outfit</col>!").start(player);
																							}).start(player);
																				}


																			}).start(player);

																}).secondOption("I'am not planning on upgrading.", player3 -> {
																	new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																			.setText("I'am not planning on upgrading.")
																			.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
																			.setText("As you wish...").start(player);
																}).start(player);
													}).fourthOption("<col=46474A>Hallowed Graceful Outfit</col>", player2 -> {
														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("I would like have the <col=46474A>Hallowed Graceful Outfit</col>", "upgrade.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
																.setText("Brilliant choice! To proceed with the upgrade,", "you will need <col=800080>1850 Marks of grace</col>.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
																.setText("Along with the <col=163583>Brimhaven Graceful Outfit</col> in your inventory.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
																.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
																.add(DialogueType.OPTION).setOptionTitle("Select an Option")
																.firstOption("Proceed with the upgrade.", player3 -> {


																	new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																			.setText("Proceed with the upgrade.").setPostAction ($ -> {
																				if (!player.getInventory().containsAny(21061, 21064, 21067, 21070, 21073, 21076)) { // Doesn't have any of the pieces to upgrade
																					new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																							.setText("You don't have any of the", "<col=163583>Brimhaven Graceful Outfit</col> to upgrade.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("Come back once you have the <col=163583>Brimhaven Graceful Outfit</col>", "and <col=800080>1850 Marks of grace</col> ready.").start(player);
																				} else if (!player.getInventory().containsAll(21061, 21064, 21067, 21070, 21073, 21076) && player.getInventory().containsAny(21061, 21064, 21067, 21070, 21073, 21076)) { // Has some pieces to upgrade but not all
																					new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13671, 200)
																							.setText("You hand over the <col=163583>Brimhaven Graceful Outfit</col>.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("You are missing some of the required pieces " + player.getUsername() +".")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("You must have all of the <col=163583>Brimhaven Graceful Outfit</col>", "set to be able to proceed with this.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("Come back once you have all of them ready with you.").start(player);
																				} else if (player.getInventory().containsAll(21061, 21064, 21067, 21070, 21073, 21076) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 1850) { // Has all the pieces to upgrade but not enough points
																					new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13671, 200)
																							.setText("You hand over the <col=163583>Brimhaven Graceful Outfit</col>.")
																							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																							.setText("I'm sorry, you need to have 1850 <col=800080>Marks of grace</col>", "for the <col=163583>Brimhaven Graceful Outfit</col> upgrade.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("Pass over once you have them all ready.").start(player);
																				} else if (player.getInventory().containsAll(21061, 21064, 21067, 21070, 21073, 21076) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 1850) { // Has all the pieces to upgrade but not enough points
																					new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13671, 200)
																							.setText("You hand over the <col=163583>Brimhaven Graceful Outfit</col>.")
																							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																							.setText("I see you have become even better in Agility skill already.")
																							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																							.setText("You are surely eligible for this upgrade.")
																							.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(21067, 200)
																							.setText("Grace hands you over the new <col=163583>Brimhaven Graceful Outfit</col>.")
																							.setAction(player5 -> {
																								player.getInventory().delete(new Item(21061, 1));
																								player.getInventory().delete(new Item(21064, 1));
																								player.getInventory().delete(new Item(21067, 1));
																								player.getInventory().delete(new Item(21070, 1));
																								player.getInventory().delete(new Item(21073, 1));
																								player.getInventory().delete(new Item(21076, 1));
																								player.getInventory().delete(ItemID.MARK_OF_GRACE, 1850);
																								player.getInventory().add(new Item(24743, 1));
																								player.getInventory().add(new Item(24746, 1));
																								player.getInventory().add(new Item(24749, 1));
																								player.getInventory().add(new Item(24752, 1));
																								player.getInventory().add(new Item(24755, 1));
																								player.getInventory().add(new Item(24758, 1));
																								new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																										.setText("Enjoy your new <col=46474A>Hallowed Graceful Outfit</col>!").start(player);
																							}).start(player);
																				}


																			}).start(player);

																}).secondOption("I'am not planning on upgrading.", player3 -> {
																	new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																			.setText("I'am not planning on upgrading.")
																			.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
																			.setText("As you wish...").start(player);
																}).start(player);
																}).fifthOption("<col=7E540B>Trailblazer Graceful Outfit</col>", player2 -> {
														new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																.setText("I would like have the <col=7E50B>Trailblazer Graceful Outfit</col>", "upgrade.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
																.setText("Brilliant choice! To proceed with the upgrade,", "you will need <col=800080>2000 Marks of grace</col>.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
																.setText("Along with the <col=46474A>Hallowed Graceful Outfit</col> in your inventory.")
																.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
																.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
																.add(DialogueType.OPTION).setOptionTitle("Select an Option")
																.firstOption("Proceed with the upgrade.", player3 -> {


																	new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																			.setText("Proceed with the upgrade.").setPostAction ($ -> {
																				if (!player.getInventory().containsAny(24743, 24746, 24749, 24752, 24755, 24758)) { // Doesn't have any of the pieces to upgrade
																					new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																							.setText("You don't have any of the", "<col=46474A>Hallowed Graceful Outfit</col> to upgrade.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("Come back once you have the <col=46474A>Hallowed Graceful Outfit</col>", "and <col=800080>2000 Marks of grace</col> ready.").start(player);
																				} else if (!player.getInventory().containsAll(24743, 24746, 24749, 24752, 24755, 24758) && player.getInventory().containsAny(24743, 24746, 24749, 24752, 24755, 24758)) { // Has some pieces to upgrade but not all
																					new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13671, 200)
																							.setText("You hand over the <col=46474A>Hallowed Graceful Outfit</col>.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("You are missing some of the required pieces " + player.getUsername() +".")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("You must have all of the <col=46474A>Hallowed Graceful Outfit</col>", "set to be able to proceed with this.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("Come back once you have all of them ready with you.").start(player);
																				} else if (player.getInventory().containsAll(24743, 24746, 24749, 24752, 24755, 24758) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 2000) { // Has all the pieces to upgrade but not enough points
																					new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13671, 200)
																							.setText("You hand over the <col=46474A>Hallowed Graceful Outfit</col>.")
																							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																							.setText("I'm sorry, you need to have 2000 <col=800080>Marks of grace</col>", "for the <col=46474A>Hallowed Graceful Outfit</col> upgrade.")
																							.add(DialogueType.NPC_STATEMENT)
																							.setText("Pass over once you have them all ready.").start(player);
																				} else if (player.getInventory().containsAll(24743, 24746, 24749, 24752, 24755, 24758) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 2000) { // Has all the pieces to upgrade but not enough points
																					new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13671, 200)
																							.setText("You hand over the <col=46474A>Hallowed Graceful Outfit</col>.")
																							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																							.setText("I see you have become even better in Agility skill already.")
																							.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																							.setText("You are surely eligible for this upgrade.")
																							.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(21067, 200)
																							.setText("Grace hands you over the new <col=46474A>Hallowed Graceful Outfit</col>.")
																							.setAction(player5 -> {
																								player.getInventory().delete(new Item(24743, 1));
																								player.getInventory().delete(new Item(24746, 1));
																								player.getInventory().delete(new Item(24749, 1));
																								player.getInventory().delete(new Item(24752, 1));
																								player.getInventory().delete(new Item(24755, 1));
																								player.getInventory().delete(new Item(24758, 1));
																								player.getInventory().delete(ItemID.MARK_OF_GRACE, 2000);
																								player.getInventory().add(new Item(25069, 1));
																								player.getInventory().add(new Item(25072, 1));
																								player.getInventory().add(new Item(25075, 1));
																								player.getInventory().add(new Item(25078, 1));
																								player.getInventory().add(new Item(25081, 1));
																								player.getInventory().add(new Item(25084, 1));
																								new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																										.setText("Enjoy your new <col=7E50B>Trailblazer Graceful Outfit</col>!").start(player);
																							}).start(player);
																				}


																			}).start(player);

																}).secondOption("I'am not planning on upgrading.", player3 -> {
																	new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																			.setText("I'am not planning on upgrading.")
																			.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
																			.setText("As you wish...").start(player);
																}).start(player);
													}).start(player);
										}).start(player);


							}).start(player);
					break;
				case HAROLD:
				case TOSTIG:
					ShopManager.open(player, ShopIdentifiers.TOAD_AND_CHICKEN);
					break;
				case HURA:
					ShopManager.open(player, ShopIdentifiers.CROSSBOW_SHOP);
					break;
				case DROGO_DWARF:
					ShopManager.open(player, ShopIdentifiers.DROGOS_MINING_EMPORIUM);
					break;
				case NULODION:
					ShopManager.open(player, ShopIdentifiers.MULTICANNON_PARTS_FOR_SALE);
					break;
				case NURMOF:
					ShopManager.open(player, ShopIdentifiers.NURMOFS_PICKAXE_SHOP);
					break;
				case FRINCOS:
					ShopManager.open(player, ShopIdentifiers.FRINCOSS_FABULOUS_HERB_STORE);
					break;
				case CASSIE:
					ShopManager.open(player, ShopIdentifiers.CASSIES_SHIELD_SHOP);
					break;
				case FLYNN:
					ShopManager.open(player, ShopIdentifiers.FLYNNS_MACE_MARKET);
					break;
				case HERQUIN:
					ShopManager.open(player, ShopIdentifiers.HERQUINS_GEMS);
					break;
				case WAYNE:
					ShopManager.open(player, ShopIdentifiers.WAYNES_CHAINS___CHAINMAIL_SPECIALIST);
					break;
/*				case BETTY:
				case BETTY_5905:
					ShopManager.open(player, ShopIdentifiers.BETTYS_MAGIC_EMPORIUM);
					break;
				case GRUM:
				case GRUM_2889:
					ShopManager.open(player, ShopIdentifiers.GRUMS_GOLD_EXCHANGE);
					break;*/
				case JATIX:
					ShopManager.open(player, ShopIdentifiers.JATIXS_HERBLORE_SHOP);
					break;
				case WIZARD_SININA:
					ShopManager.open(player, ShopIdentifiers.WIZARD_SININA_MAGIC_CAPE_STORE);
					break;
				case GAIUS:
					ShopManager.open(player, ShopIdentifiers.GAIUS_TWO_HANDED_SHOP);
					break;
				case SARAH:
					ShopManager.open(player, ShopIdentifiers.SARAHS_FARMING_SHOP);
					break;
				case OGRE_MERCHANT:
					ShopManager.open(player, ShopIdentifiers.GRUDS_HERBLORE_STALL);
					break;
				case UGLUG_NAR:
					ShopManager.open(player, ShopIdentifiers.UGLUGS_STUFFSIES);
					break;
				case JACK:
					ShopManager.open(player, ShopIdentifiers.MYTHICAL_CAPE_STORE);
					break;
				case PRIMULA:
					ShopManager.open(player, ShopIdentifiers.MYTHS_GUILD_HERBALIST);
					break;
				case SHOP_KEEPER_2884:
					ShopManager.open(player, ShopIdentifiers.VARROCK_SWORDSHOP);
					break;
				case ERDAN:
					ShopManager.open(player, ShopIdentifiers.MYTHS_GUILD_ARMOURY);
					break;
				case DIANA:
					ShopManager.open(player, ShopIdentifiers.MYTHS_GUILD_WEAPONRY);
					break;
				case FISHMONGER:
					ShopManager.open(player, ShopIdentifiers.ETCETERIA_FISH);
					break;
				case GREENGROCER:
					ShopManager.open(player, ShopIdentifiers.ISLAND_GREENGROCER);
					break;
				case VANLIGGA_GASTFRIHET:
					ShopManager.open(player, ShopIdentifiers.CONTRABAND_YAK_PRODUCE);
					break;
				case FLOSI_DALKSSON:
					ShopManager.open(player, ShopIdentifiers.FLOSIS_FISHMONGERS);
					break;
				case KEEPA_KETTILON:
					ShopManager.open(player, ShopIdentifiers.KEEPA_KETTILONS_STORE);
					break;
				case SKULI_MYRKA:
					ShopManager.open(player, ShopIdentifiers.WEAPONS_GALORE);
					break;
				case JOSSIK:
				case JOSSIK_4424:
					ShopManager.open(player, ShopIdentifiers.THE_LIGHTHOUSE_STORE);
					break;
				case MELANA_MOONLANDER:
					ShopManager.open(player, ShopIdentifiers.MOON_CLAN_GENERAL_STORE);
					break;
				case BABA_YAGA:
					ShopManager.open(player, ShopIdentifiers.BABA_YAGAS_MAGIC_SHOP);
					break;
				case RIMAE_SIRSALIS:
					ShopManager.open(player, ShopIdentifiers.MOON_CLAN_FINE_CLOTHES);
					break;
				case FINN:
					ShopManager.open(player, ShopIdentifiers.MISCELLANIAN_GENERAL_STORE);
					break;
				case HALLA:
					ShopManager.open(player, ShopIdentifiers.MISCELLANIAN_CLOTHES_SHOP);
					break;
				case OSVALD:
					ShopManager.open(player, ShopIdentifiers.MISCELLANIAN_FOOD_SHOP);
					break;
				case JOFRIDR_MORDSTATTER:
					ShopManager.open(player, ShopIdentifiers.NEITIZNOT_SUPPLIES);
					break;
				case SIGMUND_THE_MERCHANT:
					ShopManager.open(player, ShopIdentifiers.SIGMUND_THE_MERCHANT);
					break;
				case FISH_MONGER:
					ShopManager.open(player, ShopIdentifiers.FREMENNIK_FISH_MONGER);
					break;
				case SKULGRIMEN:
					ShopManager.open(player, ShopIdentifiers.SKULGRIMENS_BATTLE_GEAR);
					break;
				case YRSA:
					ShopManager.open(player, ShopIdentifiers.YRSAS_ACCOUTREMENTS);
					break;
				case VALAINE:
					ShopManager.open(player, ShopIdentifiers.VALAINES_SHOP_OF_CHAMPIONS);
					break;
				case ROMILY_WEAKLAX:
					ShopManager.open(player, ShopIdentifiers.PIE_SHOP);
					break;
				case ROACHEY:
					ShopManager.open(player, ShopIdentifiers.FISHING_GUILD_SHOP);
					break;
				case HELEMOS:
					ShopManager.open(player, ShopIdentifiers.HAPPY_HEROES_HEMPORIUM);
					break;
				case FIONELLA:
					ShopManager.open(player, ShopIdentifiers.LEGENDS_GUILD_GENERAL_STORE);
					break;
				case SIEGFRIED_ERKLE:
					ShopManager.open(player, ShopIdentifiers.LEGENDS_GUILD_SHOP_OF_USEFUL_ITEMS);
					break;
				case YARSUL:
					ShopManager.open(player, ShopIdentifiers.YARSULS_PRODIGIOUS_PICKAXES);
					break;
				case HENDOR:
					ShopManager.open(player, ShopIdentifiers.HENDORS_AWESOME_ORES);
					break;
				case BELONA:
					ShopManager.open(player, ShopIdentifiers.MINING_GUILD_MINERAL_EXCHANGE);
					break;
				case TRIBAL_WEAPON_SALESMAN:
					ShopManager.open(player, ShopIdentifiers.AUTHENTIC_TROWING_WEAPONS);
					break;
				case BOW_AND_ARROW_SALESMAN:
					ShopManager.open(player, ShopIdentifiers.DARGAUDS_BOWS_AND_ARROWS);
					break;
				case ANTON:
					ShopManager.open(player, ShopIdentifiers.WARRIOR_GUILD_ARMOURY);
					break;
				case HICKTON:
					ShopManager.open(player, ShopIdentifiers.FLETCHING_SKILL_MASTER);
					break;
				case VANESSA:
					ShopManager.open(player, ShopIdentifiers.VANESSAS_FARMING_SHOP);
					break;
				case BAKER:
					ShopManager.open(player, ShopIdentifiers.ARDOUGNE_BAKERS_STALL);
					break;
				case GEM_MERCHANT:
					ShopManager.open(player, ShopIdentifiers.ARDOUGNE_GEM_STALL);
					break;
				case SILVER_MERCHANT:
					ShopManager.open(player, ShopIdentifiers.ARDOUGNE_SILVER_STALL);
					break;
				case SPICE_SELLER:
					ShopManager.open(player, ShopIdentifiers.ARDOUGNE_SPICE_STALL);
					break;
				case ZENESHA:
					ShopManager.open(player, ShopIdentifiers.ZENESHAS_PLATE_MAIL_BODY_SHOP);
					break;
				case ARNOLD_LYDSPOR:
					ShopManager.open(player, ShopIdentifiers.ARNOLDS_ECLECTIC_SUPPLIES);
					break;
				case HECKEL_FUNCH:
					ShopManager.open(player, ShopIdentifiers.FUNCHS_FINE_GROCERIES);
					break;
				case GNOME_WAITER:
					ShopManager.open(player, ShopIdentifiers.GIANNES_RESTAURANT);
					break;
				case HUDO:
					ShopManager.open(player, ShopIdentifiers.GRAND_TREE_GROCERIES);
					break;
				case GULLUCK:
					ShopManager.open(player, ShopIdentifiers.GULLUCK_AND_SONS);
					break;
				case ROMETTI:
					ShopManager.open(player, ShopIdentifiers.ROMETTIS_FINE_FASHIONS);
					break;
				case BOLKOY:
					ShopManager.open(player, ShopIdentifiers.BOLKOYS_VILLAGE_SHOP);
					break;
				case CHADWELL:
					ShopManager.open(player, ShopIdentifiers.WEST_ARDOUGNE_GENERAL_STORE);
					break;
				case EZEKIAL_LOVECRAFT:
					ShopManager.open(player, ShopIdentifiers.LOVECRAFTS_TACKLE);
					break;
				case ALECK:
					ShopManager.open(player, ShopIdentifiers.ALECKS_HUNTER_EMPORIUM);
					break;
				case FRENITA:
					ShopManager.open(player, ShopIdentifiers.FRENITAS_COOKERY_SHOP);
					break;
				case RASOLO:
					ShopManager.open(player, ShopIdentifiers.RASOLO_THE_WANDERING_MERCHANT);
					break;
				case DAVON:
					ShopManager.open(player, ShopIdentifiers.DAVONS_AMULET_STORE);
					break;
				case ALFONSE_THE_WAITER:
					ShopManager.open(player, ShopIdentifiers.THE_SHRIMP_AND_PARROT);
					break;
				case ZAMBO:
					ShopManager.open(player, ShopIdentifiers.KARAMJA_WINES_SPIRITS_AND_BEERS);
					break;
				case OBLI:
					ShopManager.open(player, ShopIdentifiers.OBLIS_GENERAL_STORE);
					break;
				case FERNAHEI:
					ShopManager.open(player, ShopIdentifiers.FERNAHEIS_FISHING_HUT);
					break;
				case JIMINUA:
					ShopManager.open(player, ShopIdentifiers.JIMINUAS_JUNGLE_STORE);
					break;
				case GABOOTY:
					ShopManager.open(player, ShopIdentifiers.GABOOTYS_TAI_BWO_WANNAI_COOPERATIVE);
					break;
				case TAMAYU:
					ShopManager.open(player, ShopIdentifiers.TAMAYUS_SPEAR_STALL);
					break;
				case TIADECHE:
					ShopManager.open(player, ShopIdentifiers.TIADECHES_KARAMBWAN_STALL);
					break;
				case ALI_MORRISANE:
					ShopManager.open(player, ShopIdentifiers.ALIS_DISCOUNT_WARES);
					break;
				case DOMMIK:
					ShopManager.open(player, ShopIdentifiers.DOMMIKS_CRAFTING_STORE);
					break;
				case GEM_TRADER:
					ShopManager.open(player, ShopIdentifiers.GEM_TRADER);
					break;
				case LOUIE_LEGS:
					ShopManager.open(player, ShopIdentifiers.LOUIES_ARMOURED_LEGS_BAZAAR);
					break;
				case RANAEL:
					ShopManager.open(player, ShopIdentifiers.RANAELS_SUPER_SKIRT_STORE);
					break;
				case BANDIT_SHOPKEEPER:
					ShopManager.open(player, ShopIdentifiers.BANDIT_BARGAINS);
					break;
				case BEDABIN_NOMAD:
					ShopManager.open(player, ShopIdentifiers.BEDABIN_VILLAGE_BARTERING);
					break;
				case FADLI:
					ShopManager.open(player, ShopIdentifiers.SHOP_OF_DISTASTE);
					break;
				case KAZEMDE:
					ShopManager.open(player, ShopIdentifiers.NARDAH_GENERAL_STORE);
					break;
				case ARTIMEUS:
					ShopManager.open(player, ShopIdentifiers.NARDAH_HUNTER_SHOP);
					break;
				case ROKUH:
					ShopManager.open(player, ShopIdentifiers.ROKS_CHOCS_BOX);
					break;
				case SEDDU:
					ShopManager.open(player, ShopIdentifiers.SEDDUS_ADVENTURERS_STORE);
					break;
				case MARKET_SELLER:
					ShopManager.open(player, ShopIdentifiers.POLLNIVNEACH_GENERAL_STORE);
					break;
				case URBI:
					ShopManager.open(player, ShopIdentifiers.BLADES_BY_URBI);
					break;
				case JAMILA:
					ShopManager.open(player, ShopIdentifiers.JAMILAS_CRAFT_STALL);
					break;
				case NATHIFA:
					ShopManager.open(player, ShopIdentifiers.NATHIFAS_BAKE_STALL);
					break;
				case RAETUL:
					ShopManager.open(player, ShopIdentifiers.RAETUL_AND_COS_CLOTH_STORE);
					break;
				case EMBALMER:
					ShopManager.open(player, ShopIdentifiers.THE_SPICE_IS_RIGHT);
					break;
				case LURGON:
					ShopManager.open(player, ShopIdentifiers.DORGESH_KAAN_GENERAL_SUPPLIES);
					break;
				case NARDOK:
					ShopManager.open(player, ShopIdentifiers.NARDOKS_BONE_WEAPONS);
					break;
				case MILTOG:
					ShopManager.open(player, ShopIdentifiers.MILTOGS_LAMPS);
					break;
				case RELDAK:
					ShopManager.open(player, ShopIdentifiers.RELDAKS_LEATHER_ARMOUR);
					break;
				case AVA:
					ShopManager.open(player, ShopIdentifiers.AVAS_ODDS_AND_ENDS);
					break;
				case DIANGO:
					ShopManager.open(player, ShopIdentifiers.DIANGOS_TOY_STORE);
					break;
				case FORTUNATO:
					ShopManager.open(player, ShopIdentifiers.FORTUNATOS_FINE_WINE);
					break;
				case PEKSA:
					ShopManager.open(player, ShopIdentifiers.HELMET_SHOP);
					break;
				//case AUBURY_11435:
				//	ShopManager.open(player, ShopIdentifiers.AUBURYS_RUNE_SHOP);
				//	break;
//				case AUBURY:
//					ShopManager.open(player, ShopIdentifiers.AUBURYS_RUNE_SHOP);
//					break;
				case SAWMILL_OPERATOR:
					ShopManager.open(player, ShopIdentifiers.CONSTRUCTION_SUPPLIES);
					break;
				case ZAFF:
					ShopManager.open(player, ShopIdentifiers.ZAFFS_SUPERIOR_STAFFS);
					break;
				case AUREL:
					ShopManager.open(player, ShopIdentifiers.AURELS_SUPPLIES);
					break;
				case FIDELIO:
					ShopManager.open(player, ShopIdentifiers.GENERAL_STORE);
					break;
				case BARKER:
					ShopManager.open(player, ShopIdentifiers.BARKERS_HABERDASHERY);
					break;
				case RUFUS:
					ShopManager.open(player, ShopIdentifiers.RUFUS_MEAT_EMPORIUM);
					break;
				case TRADER_SVEN:
					ShopManager.open(player, ShopIdentifiers.TRADER_SVENS_BLACK_MARKET_GOODS);
					break;
				case RAZMIRE_KEELGAN:
					ShopManager.open(player, ShopIdentifiers.RAZMIRE_GENERAL_STORE);
					break;
				case GHOST_SHOPKEEPER:
					ShopManager.open(player, ShopIdentifiers.PORT_PHASMATYS_GENERAL_STORE);
					break;
				case EUDAV:
					ShopManager.open(player, ShopIdentifiers.LLETYA_GENERAL_STORE);
					break;
				case DALLDAV:
					ShopManager.open(player, ShopIdentifiers.LLETYA_ARCHERY_SHOP);
					break;
				case GETHIN:
					ShopManager.open(player, ShopIdentifiers.LLETYA_FOOD_STORE);
					break;
				/*case QUARTERMASTER:
					ShopManager.open(player, ShopIdentifiers.QUARTERMASTERS_STORES);
					break;*/
				case NOLAR:
					ShopManager.open(player, ShopIdentifiers.CAREFREE_CRAFTING_STALL);
					break;
				case HIRKO:
					ShopManager.open(player, ShopIdentifiers.CROSSBOW_SHOP);
					break;
				case HERVI:
					ShopManager.open(player, ShopIdentifiers.GREEN_GEMSTONE_GEMS);
					break;
				case RANDIVOR:
					ShopManager.open(player, ShopIdentifiers.KELDAGRIMS_BEST_BREAD);
					break;
				case KJUT:
					ShopManager.open(player, ShopIdentifiers.KJUTS_KEBABS);
					break;
				case TATI:
					ShopManager.open(player, ShopIdentifiers.PICKAXE_IS_MINE);
					break;
				case GULLDAMAR:
					ShopManager.open(player, ShopIdentifiers.SILVER_COG_SILVER_STALL);
					break;
				case GUNSLIK:
					ShopManager.open(player, ShopIdentifiers.GUNSLIKS_ASSORTED_ITEMS);
					break;
				case AGMUNDI:
					ShopManager.open(player, ShopIdentifiers.AGMUNDI_QUALITY_CLOTHES);
					break;
				case STONEMASON:
					ShopManager.open(player, ShopIdentifiers.KELDAGRIM_STONEMASON);
					break;
				case SARO:
					ShopManager.open(player, ShopIdentifiers.QUALITY_ARMOUR_SHOP);
					break;
				case SANTIRI:
					ShopManager.open(player, ShopIdentifiers.QUALITY_WEAPONS_SHOP);
					break;
				case VIGR:
					ShopManager.open(player, ShopIdentifiers.VIGRS_WARHAMMERS);
					break;
				case FILAMINA:
					ShopManager.open(player, ShopIdentifiers.FILAMINAS_WARES);
					break;
				case REGATH:
					ShopManager.open(player, ShopIdentifiers.REGATHS_WARES);
					break;
				case THYRIA:
					ShopManager.open(player, ShopIdentifiers.THYRIAS_WARES);
					break;
				case HORACE:
					ShopManager.open(player, ShopIdentifiers.LITTLE_SHOP_OF_HORACE);
					break;
				case LOGAVA:
					ShopManager.open(player, ShopIdentifiers.LOGAVA_GRICOLLERS_COOKING_SUPPLIES);
					break;
				case PERRY:
					ShopManager.open(player, ShopIdentifiers.PERRYS_CHOP_CHOP_SHOP);
					break;
				case VANNAH:
					ShopManager.open(player, ShopIdentifiers.VANNAHS_FARM_STORE);
					break;
				case MUNTY:
					ShopManager.open(player, ShopIdentifiers.LITTLE_MUNTYS_LITTLE_SHOP);
					break;
				case TOOTHY:
					ShopManager.open(player, ShopIdentifiers.TOOTHYS_PICKAXES);
					break;
				case THIRUS:
					ShopManager.open(player, ShopIdentifiers.THIRUS_URKARS_FINE_DYNAMITE_STORE);
					break;
				case FRANKIE:
					ShopManager.open(player, ShopIdentifiers.FRANKIES_FISHING_EMPORIUM);
					break;
				case KENELME:
					ShopManager.open(player, ShopIdentifiers.KENELMES_WARES);
					break;
				case LEENZ:
					ShopManager.open(player, ShopIdentifiers.LEENZS_GENERAL_SUPPLIES);
					break;
				case TYNAN:
					ShopManager.open(player, ShopIdentifiers.TYNANS_FISHING_SUPPLIES);
					break;
				case IFABA:
					ShopManager.open(player, ShopIdentifiers.IFABAS_GENERAL_STORE);
					break;
				case DAGA:
					ShopManager.open(player, ShopIdentifiers.DAGAS_SCIMITAR_SMITHY);
					break;
				case HAMAB:
					ShopManager.open(player, ShopIdentifiers.HAMABS_CRAFTING_EMPORIUM);
					break;
				case OOBAPOHK:
					ShopManager.open(player, ShopIdentifiers.OOBAPOHKS_JAVELIN_STORE);
					break;
				case SOLIHIB:
					ShopManager.open(player, ShopIdentifiers.SOLIHIBS_FOOD_STALL);
					break;
				case TUTAB:
					ShopManager.open(player, ShopIdentifiers.MAGIC_STALL);
					break;
				case PETRIFIED_PETE:
					ShopManager.open(player, ShopIdentifiers.PETRIFIED_PETES_ORE_SHOP);
					break;
				case MAIRIN:
					ShopManager.open(player, ShopIdentifiers.MAIRINS_MARKET);
					break;
				case MIKE:
					ShopManager.open(player, ShopIdentifiers.DODGY_MIKES_SECOND_HAND_CLOTHING);
					break;
				case SMITH:
					ShopManager.open(player, ShopIdentifiers.SMITHING_SMITHS_SHOP);
					break;
				case CHARLEY:
					ShopManager.open(player, ShopIdentifiers.TWO_FEET_CHARLEYS_FISH_SHOP);
					break;
/*				case SQUIRE_1765:
					ShopManager.open(player, ShopIdentifiers.VOID_KNIGHT_ARCHERY_STORE);
					break;
				case SQUIRE_1769:
					ShopManager.open(player, ShopIdentifiers.VOID_KNIGHT_MAGIC_STORE);
					break;*/
				case NOTERAZZO:
					ShopManager.open(player, ShopIdentifiers.BANDIT_DUTY_FREE);
					break;
				case DARREN:
					ShopManager.open(player, ShopIdentifiers.DARRENS_WILDERNESS_CAPE_SHOP);
					break;
				case EDWARD:
					ShopManager.open(player, ShopIdentifiers.EDWARDS_WILDERNESS_CAPE_SHOP);
					break;
				case IAN:
					ShopManager.open(player, ShopIdentifiers.IANS_WILDERNESS_CAPE_SHOP);
					break;
				case LUNDAIL:
					ShopManager.open(player, ShopIdentifiers.LUNDAILS_ARENA_SIDE_RUNE_SHOP);
					break;
				case CHAMBER_GUARDIAN:
					ShopManager.open(player, ShopIdentifiers.MAGE_ARENA_STAFFS);
					break;
				case NEIL:
					ShopManager.open(player, ShopIdentifiers.NEILS_WILDERNESS_CAPE_SHOP);
					break;
				case SIMON:
					ShopManager.open(player, ShopIdentifiers.SIMONS_WILDERNESS_CAPE_SHOP);
					break;
				case FAT_TONY:
					ShopManager.open(player, ShopIdentifiers.TONYS_PIZZA_BASES);
					break;
				case WILLIAM:
					ShopManager.open(player, ShopIdentifiers.WILLIAMS_WILDERNESS_CAPE_SHOP);
					break;
				case FAIRY_SHOP_KEEPER:
					ShopManager.open(player, ShopIdentifiers.ZANARIS_GENERAL_STORE);
					break;
				case IRKSOL:
					ShopManager.open(player, ShopIdentifiers.IRKSOL);
					break;
				case JUKAT:
					ShopManager.open(player, ShopIdentifiers.JUKAT);
					break;
				case FAIRY_FIXIT:
					ShopManager.open(player, ShopIdentifiers.FAIRY_FIXITS_FAIRY_ENCHANTMENT);
					break;
				case HOLOY:
					ShopManager.open(player, ShopIdentifiers.CROSSBOW_SHOP_WHITE_WOLF_MOUNTAIN);
					break;
				case SAILOR_3936:
					/*DialogueManager.start(player, 2572);
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player1, int option) {
							switch (option) {
								case 1:
									if (player1.getInventory().getAmount(ItemIdentifiers.COINS) >= 5_000_000) {
										player1.getInventory().delete(995, 5_000_000);
										player1.moveTo(new Position(2516 + Misc.getRandomInclusive(2), 3882 + Misc.getRandomInclusive(1)));
										player1.getPacketSender().sendInterfaceRemoval();
										DialogueManager.sendStatement(player1, "You paid the wizard and then you magically find yourself in the island!");
									} else {
										DialogueManager.start(player1, 2574);
									}
									break;
								case 2:
									player1.getPacketSender().sendInterfaceRemoval();
									break;
							}
						}
					});*/
					WizardSig.handleFirstClickOption(player);

					break;
				case TEA_SELLER:
					ShopManager.open(player, ShopIdentifiers.TEA_SELLER);
					break;
				/*case HUNTING_EXPERT_1504: // Hunter store
					ShopManager.open(player, 38);
					break;*/
				case KAMFREENA:
					break;
				case GARDEN_SUPPLIER:
				case GARDEN_SUPPLIER_3103:
					ShopManager.open(player, ShopIdentifiers.GARDEN_SUPPLIES_STORE);
					break;
				case LESSER_FANATIC:
					DialogueManager.start(player, 2653);
					break;
				case OZIACH:
					DialogueManager.start(player, 2654);
					break;
				case GUARD_1147:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).
							setText("Stop there " + player.getUsername() + "! This area is currently closed", "for maintenance. Check again later.").setExpression(DialogueExpression.HAPPY)
							.add(DialogueType.PLAYER_STATEMENT).setText("Okay, I will be back later.").start(player);
					break;
				case HANS:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Hello. What are you doing here?").add(DialogueType.OPTION).setOptionTitle("Select an Option")
					.firstOption("I'm looking for whoever is in charge of this place.", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setText("I'm looking for whoever is in charge of this place.")
								.add(DialogueType.NPC_STATEMENT).setText("Who, the Duke? He's in his study, on the first floor.")
								.start(player);
					}).secondOption("I have come to kill everyone in this castle.", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("I have come to kill everyone in this castle.").setExpression(DialogueExpression.EVIL)
								.setPostAction ($ -> {
							npc.getMotion().traceTo(new Position(npc.getSpawnPosition().getX() + Random.Default.nextInt(-5, 5), npc.getSpawnPosition().getY() + Random.Default.nextInt(-5, 5)));
							npc.say("Help! Help!");
						}).start(player);

						//new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setText("I have come to kill everyone in this castle.")
						//		.add(DialogueType.NPC_STATEMENT).setText("Help! Help!")
						//		.start(player);
					}).thirdOption("I don't know. I'm lost. Where am I?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setText("I don't know. I'm lost. Where am I?")
								.add(DialogueType.NPC_STATEMENT).setText("You are in Lumbridge Castle.")
								.start(player);
					}).fourthOption("Can you tell me how long I've been here?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setText("Can you tell me how long I've been here?")
								.add(DialogueType.NPC_STATEMENT).setText("Ahh, I see all the newcomers arriving in Lumbridge", "fresh-faced and eager for adventure. I remember you...")
								.add(DialogueType.NPC_STATEMENT).setText("" + PlayerUtil.sendPlayTimeHans(player) +"", "since you arrived.")
								.start(player);
					}).fifthOption("Nothing.", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setText("Nothing.").start(player);
					}).start(player);
					break;
				case JEFF:
					DialogueManager.start(player, 2534);
					break;
				case HAM_MEMBER_2541:
					DialogueManager.start(player, 2606);
					break;
				case TOWN_CRIER:
				case TOWN_CRIER_277:
				case TOWN_CRIER_278:
				case TOWN_CRIER_279:
				case TOWN_CRIER_280:
				case TOWN_CRIER_6823:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setText("Howdy " + player.getUsername() + "!",
							"I hope your having a great time out here! Have fun!").setExpression(DialogueExpression.HAPPY)
							.add(DialogueType.PLAYER_STATEMENT).setText("Thanks you too!").start(player);
					break;
				case GHOST_3975:
				case GHOST_3976:
				case GHOST_3977:
				case GHOST_3978:
				case GHOST_3979:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Woo!!").add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("aawwgo..woooooo...skkoooow!").start(player);
					break;
				case PRIEST:
				case FARMER_3244:
				case PRIEST_4208:
				case PRIEST_4207:
				case PRIEST_5291:
				case FATHER_LAWRENCE:
				case FATHER_URHNEY:
					DialogueManager.start(player, 2566);
					break;
				case TZHAARMEJJAL:
					ExchangeFireCape.exchange(player, 0);
					break;
				case JONNY_THE_BEARD:
					DialogueManager.start(player, 2673);
					break;
				case SIGMUND:
					DialogueManager.start(player, 2608);
					break;
				case KHARID_SCORPION_5229:
				case KHARID_SCORPION:
				case KHARID_SCORPION_5230:
					player.getPacketSender().sendMessage("I better not get my hands bitten by this scorpion..", 1000);
					break;
				case DR_HARLOW:
					DialogueManager.start(player, 2718);
					break;
				case NURSE_SARAH:
					if (EntityExtKt.passedTime(player, Attribute.LAST_REFRESH, 1, TimeUnit.MINUTES, false, true)) {
						// player.getPacketSender().sendMessage("You
						// feel slightly refreshed.");
						player.restoreRegularAttributes();
						DialogueManager.sendStatement(player, "The nurse makes you feel slightly refreshed.");
						player.performGraphic(new Graphic(436));
						player.getPacketSender().sendAreaPlayerSound(Sounds.HEALED_BY_NURSE);
					} else {
						player.getPacketSender()
								.sendMessage("You must wait one minute before being healed again.");
						return;
					}
					break;
				case 7680:
					ShopManager.open(player, ShopIdentifiers.TZHAAR_MEJ_ROHS_RUNE_STORE);
					break;
				case CHARLIE_THE_COOK:
				case APOTHECARY:
					ShopManager.open(player, ShopIdentifiers.CONSUMEABLES_STORE);
					break;
				case MAGIC_INSTRUCTOR:
					ShopManager.open(player, ShopIdentifiers.MAGE_STORE);
					break;
				case COL_ONIALL_4782:
					ShopManager.open(player, ShopIdentifiers.MEMBERS_STORE);
					break;
				case BORAT:
					DialogueManager.start(player, 2612);
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player1, int option) {
							switch (option) {
								case 1:
									DialogueManager.start(player1, 2620);
									break;
								case 2:
									DialogueManager.start(player1, 2618);
									player1.setDialogueContinueAction(new Executable() {
										@Override
										public void execute() {
											ShopManager.open(player1, 42);

										}
									});
									break;
								case 3:
									DialogueManager.start(player1, 2616);
									break;
							}
						}
					});
					break;
				case COMBAT_INSTRUCTOR:
					ShopManager.open(player, ShopIdentifiers.STARTER_SUPPLIES_STORE);
					break;
				case PARTY_PETE:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.HAPPY)
							.setText("Greetings, Pete!")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Hi there " + player.getUsername() +"! What's up?").setExpression(DialogueExpression.HAPPY)
							.add(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.HAPPY)
							.setText("I have questions about store purchases.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.LAUGHING)
							.setText("Absolutely! Let me help you with it!")
							.add(DialogueType.OPTION).setOptionTitle("Select an Option")
							.firstOption("How do I get Premium Points?", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
										.setText("How do I get Premium Points?")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("The primary method to get @cya@premium points</col> is by purchasing", "them from the store.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("You also get a bonus permanent members rank, mystery", "boxes, and participation points when you redeem.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("You can also get 50k @cya@premium points</col> by voting, if lucky.", "There's a @red@1/1000</col> chance.")

										.start(player);
							}).secondOption("How can I redeem my purchase?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("How can I redeem my purchase?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("It's pretty simple, you can type @blu@::claimorder</col> command, or", "you can press on Redeem in your quest tab.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("If you are still having difficulties, you can talk to", "a staff member or post on Discord support line.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Send an email to @red@billing@grinderscape.org</col> and you will", "get a reply within 24 hours.")
								.start(player);

					}).thirdOption("What are the members rank perks?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("What are the members perks?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("There are 6 different groups of member ranks on here.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("<col=873600>$ Bronze member</col> is the first members rank group.", "and it is given upon spending $10.00 or more.", "The primary feature is rank icon and player title.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("@red@$ Ruby member</col> is considered a complete member rank.", "It is given upon spending $50.00 or more.", "You get to access to all basic member perks.", "Keep in mind perks increase as the rank group increases.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("@blu@$ Topaz member</col> is given upon spending $100.00 or more.", "<col=ff00ff>$ Amethyst member</col> $150.00, @yel@$ Legendary member</col> $250.00", "and up to the highest rank @whi@$ Diamond member</col>", "upon $999.00 which is the highest of all.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Members get reduced slayer tasks, Bone crusher charge's", "multiplier, access Yell customizer, rank icon, members' zone,", "The gilded altar, Discord ranks, less kill count for", "Access to The Cursed Vault, and much more!")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Keep in mind perks increase as the rank group increases.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("You can check out our @dre@Wiki</col> page for more information.")
								.start(player);
					}).fourthOption("I want to exchange my points.", player1 -> {
						Store.INSTANCE.openPremiumStore(player);
					}).addCancel("See you later..").start(player);
					break;
				case SQUIRE_1768:
					DialogueManager.start(player, 2710);
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player1, int option) {
							switch (option) {
								case 1:
									ShopManager.open(player1, ShopIdentifiers.GENERAL_STORE);
									break;
								case 2:
									DialogueManager.startSelected(player1, option);
									break;
							}
						}
					});
					break;
				case ZEKE:
					ShopManager.open(player, ShopIdentifiers.ZEKES_SUPERIOR_SCIMITARS);
					break;
				case PORTAL_3086:
				case PORTAL_3088:
					player.sendMessage("This portal is not active at the moment.");
					break;
				case SHOP_ASSISTANT:
				case SHOP_ASSISTANT_2816:
				case SHOP_ASSISTANT_2818:
				case SHOP_ASSISTANT_2820:
				case SHOP_ASSISTANT_2822:
				case SHOP_ASSISTANT_2824:
				case SHOP_ASSISTANT_2826:
				case SHOP_ASSISTANT_2885:
				case FAIRY_SHOP_ASSISTANT:
				case SHOP_KEEPER:
				case SHOP_KEEPER_2815:
				case SHOP_KEEPER_2817:
				case SHOP_KEEPER_2819:
				case SHOP_KEEPER_2821:
				case SHOP_KEEPER_2823:
				case SHOP_KEEPER_2825:
				case SHOP_KEEPER_2888:
				case SHOP_KEEPER_2894:
				case SHOP_KEEPER_7769:
				case SHOP_KEEPER_7913:
				case ARHEIN:
					/*DialogueManager.start(player, 2610);
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player1, int option) {
							switch (option) {
								case 1:
									ShopManager.open(player1, ShopIdentifiers.GENERAL_STORE);
									break;
								case 2:
									DialogueManager.startSelected(player1, option);
									break;
							}
						}
					});*/
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Can I help you at all?").add(DialogueType.OPTION).setOptionTitle("Select an Option")
							.firstOption("Yes please. What are you selling?", player1 -> {
								ShopManager.open(player1, ShopIdentifiers.GENERAL_STORE);
							}).secondOption("No thanks.", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("No thanks.").start(player);
					}).start(player);
					break;
				case BARMAN:
					DialogueManager.start(player, 2682);
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player1, int option) {
							switch (option) {
								case 1:
									DialogueManager.start(player1, 2689);
									break;
								case 2:
									DialogueManager.start(player1, 2690);
									break;
								case 3:
									DialogueManager.start(player1, 2692);
									break;
							}
						}
					});
					break;
				case BANDIT:
				case BANDIT_691:
				case BANDIT_692:
				case BANDIT_693:
				case BANDIT_694:
				case BANDIT_695:
				case BANDIT_734:
					int randomMessage = Misc.random(3);
					if (randomMessage <= 1) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setNpcChatHead(BANDIT)
								.setText("What do you bandits do here?").setExpression(DialogueExpression.ANGRY_2).add(DialogueType.NPC_STATEMENT)
								.setText("It is non of your business you filthy..").setExpression(DialogueExpression.ANGRY_3).add(DialogueType.NPC_STATEMENT)
								.setText("You better leave or I will call the other bandits now!").setExpression(DialogueExpression.ANGRY_3)
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("GOOODBYE!!!")
								.start(player);
					} else if (randomMessage == 2) {
						new DialogueBuilder(DialogueType.NPC_STATEMENT)
								.setNpcChatHead(BANDIT)
								.setText("...").setExpression(DialogueExpression.EVIL)
								.start(player);
					} else if (randomMessage == 3) {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setNpcChatHead(BANDIT)
								.setExpression(DialogueExpression.CALM)
								.setText("I've got waterskins on me, do you want some?").setExpression(DialogueExpression.ANGRY_3).add(DialogueType.NPC_STATEMENT)
								.setText("I don't want anything from you!").setExpression(DialogueExpression.ANGRY_3)
								.start(player);
					}
					break;
				case MAGE_OF_ZAMORAK:
					INSTANCE.startDialogue(player, npc.getId());
					break;
				case RICHARD:
				case RICHARD_2200:
					ShopManager.open(player, ShopIdentifiers.TEAMCAPE_STORE);
					break;
				case ARMOUR_SALESMAN:
					ShopManager.open(player, ShopIdentifiers.RANGE_GEAR_STORE);
					break;
				case FANCY_DRESS_STORE:
					ShopManager.open(player, ShopIdentifiers.FANCY_DRESS_STORE);
					break;
				case BRIAN:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Can I help you at all champion?").add(DialogueType.OPTION).setOptionTitle("Select an Option")
							.firstOption("Yes please. What are you selling?", player1 -> {
								ShopManager.open(player, ShopIdentifiers.BRIANS_BATTLEAXE_BAZAAR);
							}).secondOption("I'm good", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
										.setText("I'm good")
										.add(DialogueType.NPC_STATEMENT)
										.setText("Leave me alone if you're not going to buy anything.")
										.start(player);
							}).start(player);
					break;
				case ADVENTURER_EASY:
				case RICK:
					ShopManager.open(player, ShopIdentifiers.PURE_ITEMS_STORE);
					break;
				case ROMMIK:
					ShopManager.open(player, ShopIdentifiers.CRAFTING_STORE);
					break;
				case BOB_BARTER_HERBS:
					DialogueManager.start(player, 2640);
					// Set dialogue options
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player1, int option) {
							switch (option) {
								case 1:
									DialogueManager.startSelected(player1, option);
									player1.setDialogueContinueAction(new Executable() {
										@Override
										public void execute() {
											ShopManager.open(player1, ShopIdentifiers.HERBLORING_STORE);
										}
									});
									break;
								case 2:
									DialogueManager.startSelected(player1, option);
									player1.setDialogueContinueAction(new Executable() {
										@Override
										public void execute() {
											DialogueManager.start(player1, 2644);
											player1.setDialogueOptions(new DialogueOptions() {
												@Override
												public void handleOption(Player player1, int option) {
													player1.getPacketSender().sendInterfaceRemoval();
													if (option >= 1 && option <= 4) {
														PotionDecanting.decantInventory(player1, option, true);
													}
												}
											});
										}
									});
									break;
								case 3:
									DialogueManager.startSelected(player1, option, DialogueExpression.ANNOYED);
									player1.setDialogueContinueAction(new Executable() {
										@Override
										public void execute() {
											player1.getPacketSender().sendInterfaceRemoval();
										}
									});
									break;
							}
						}
					});
					break;
				case ALI_THE_FARMER:
				case TOOL_LEPRECHAUN:
				case WYSON_THE_GARDENER:
					ShopManager.open(player, ShopIdentifiers.FARMING_STORE);
					break;
				case DUNSTAN:
					ShopManager.open(player, ShopIdentifiers.THIEVING_STORE);
					break;
				case PHANTUWTI_FANSTUWI_FARSIGHT:
					ShopManager.open(player, ShopIdentifiers.SKILLING_STORE);
					break;
				case MINER_MAGNUS:
				case HRING_HRING:
					ShopManager.open(player, ShopIdentifiers.MINING_STORE);
					break;
				case PIRATE_JACKIE_THE_FRUIT:
					ShopManager.open(player, ShopIdentifiers.AGILITY_TICKET_EXCHANGE);
					break;
				case QUARTERMASTER:
					if (Misc.random(3) == 1) {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Hi there, " + player.getUsername() + "!")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("Hello.")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("Can you help me with voting?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("My pleasure! What do you need?")
								.add(DialogueType.OPTION).setOptionTitle("Select an Option")
								.firstOption("How do I vote?", player1 -> {
									new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
											.setText("How do I vote?")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("You can start voting by typing @blu@::vote</col> or", "by clicking Vote in your quest tab.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("After voting on all sites, you can come back in game", "and type @blu@::redeemvote</col>, or click on", "Redeem in your quest tab.")
											.start(player);
								}).secondOption("Any benefits for voting?", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("Any benefits for voting?")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Voting gives a lot of advantages to players who vote daily", "on the server.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Basically, you get @dre@Voting ticket(s)</col> which can be exchanged", "within my store for @dre@Coins</col>, @dre@Blood money</col>, or items.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("You can trade tickets with other players.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Voting gives you @red@25% bonus XP</col> for 1 hour", "and @red@10% bonus drop rate</col>.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("If you vote daily to the server, you will have a", "@red@1/1000 chance</col> to get @cya@50,000 premium points</col>!")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("You can check out our @dre@Wiki</col> for the full list", "and more information.")
									.start(player);

						}).thirdOption("What is voting streak?", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("What is voting streak?")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Streak bonuses can give some extra rewards to those", "who vote daily on the server.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Every time that you vote between an interval of", "12 and 16 hours you will earn 1.5x Vote Streak Points.", "and between 16 and 24 hours you will earn 1", "Vote Streak Point.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("There's also 1/1000 chance to get 50,000 @cya@premium points</col>", "when voting.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("You can check out our @dre@Wiki</col> page for more information.")
									.start(player);
						}).fourthOption("I want to exchange my points.", player1 -> {
							Voting.INSTANCE.openVotePointStore(player);
						}).fifthOption("Nothing.", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.DISTRESSED)
									.setText("Nothing...")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
									.setText("Come back anytime...")
									.start(player);

						}).start(player);
					} else if (Misc.random(3) == 2) {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Did you vote today " + player.getUsername() +"?")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("Ummm I need help with voting.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("What do you need help with?")
								.add(DialogueType.OPTION).setOptionTitle("Select an Option")
								.firstOption("How do I vote?", player1 -> {
									new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
											.setText("How do I vote?")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("You can start voting by typing @blu@::vote</col> or", "by clicking Vote in your quest tab.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("After voting on all sites, you can come back in game", "and type @blu@::redeemvote</col>, or click on", "Redeem in your quest tab.")
											.start(player);
								}).secondOption("Any benefits for voting?", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("Any benefits for voting?")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Voting gives a lot of advantages to players who vote daily", "on the server.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Basically, you get @dre@Voting ticket(s)</col> which can be exchanged", "within my store for @dre@Coins</col>, @dre@Blood money</col>, or items.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("You can trade tickets with other players.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Voting gives you @red@25% bonus XP</col> for 1 hour", "and @red@10% bonus drop rate</col>.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("If you vote daily to the server, you will have a", "@red@1/1000 chance</col> to get @cya@50,000 premium points</col>!")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("You can check out our @dre@Wiki</col> for the full list", "and more information.")
									.start(player);

						}).thirdOption("What is voting streak?", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("What is voting streak?")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Streak bonuses can give some extra rewards to those", "who vote daily on the server.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Every time that you vote between an interval of", "12 and 16 hours you will earn 1.5x Vote Streak Points.", "and between 16 and 24 hours you will earn 1", "Vote Streak Point.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("There's also 1/1000 chance to get 50,000 @cya@premium points</col>", "when voting.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("You can check out our @dre@Wiki</col> page for more information.")
									.start(player);
						}).fourthOption("I want to exchange my points.", player1 -> {
							Voting.INSTANCE.openVotePointStore(player);
						}).fifthOption("Nothing.", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.DISTRESSED)
									.setText("Nothing...")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
									.setText("Come back anytime...")
									.start(player);

						}).start(player);
					} else {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Hi there!")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Hello.")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("Can you help me with voting?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Anytime!")
								.add(DialogueType.OPTION).setOptionTitle("Select an Option")
								.firstOption("How do I vote?", player1 -> {
									new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
											.setText("How do I vote?")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("You can start voting by typing @blu@::vote</col> or", "by clicking Vote in your quest tab.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("After voting on all sites, you can come back in game", "and type @blu@::redeemvote</col>, or click on", "Redeem in your quest tab.")
											.start(player);
								}).secondOption("Any benefits for voting?", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("Any benefits for voting?")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Voting gives a lot of advantages to players who vote daily", "on the server.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Basically, you get @dre@Voting ticket(s)</col> which can be exchanged", "within my store for @dre@Coins</col>, @dre@Blood money</col>, or items.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("You can trade tickets with other players.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Voting gives you @red@25% bonus XP</col> for 1 hour", "and @red@10% bonus drop rate</col>.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("If you vote daily to the server, you will have a", "@red@1/1000 chance</col> to get @cya@50,000 premium points</col>!")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("You can check out our @dre@Wiki</col> for the full list", "and more information.")
									.start(player);

						}).thirdOption("What is voting streak?", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("What is voting streak?")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Streak bonuses can give some extra rewards to those", "who vote daily on the server.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("Every time that you vote between an interval of", "12 and 16 hours you will earn 1.5x Vote Streak Points.", "and between 16 and 24 hours you will earn 1", "Vote Streak Point.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("There's also 1/1000 chance to get 50,000 @cya@premium points</col>", "when voting.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("You can check out our @dre@Wiki</col> page for more information.")
									.start(player);
						}).fourthOption("I want to exchange my points.", player1 -> {
							Voting.INSTANCE.openVotePointStore(player);
						}).fifthOption("Nothing.", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.DISTRESSED)
									.setText("Nothing...")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
									.setText("Come back anytime...")
									.start(player);

						}).start(player);
					}
					break;
				case HORVIK:
					ShopManager.open(player, ShopIdentifiers.MELEE_STORE);
					break;
				case TANNER:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Greetings friend. I am a manufacturer of leath.")
							.add(DialogueType.OPTION).setOptionTitle("Select an Option")
							.firstOption("Can I buy some leather then?", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
										.setText("Can I buy some leather then?")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("I make leather from animal hides. Bring me some", "cowhides and two gold coins per hide, and I'll tan them", "into soft leather for you.")
										.setText("")
								.start(player);
							}).secondOption("Leather is rather weak stuff.", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Leather is rather weak stuff.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Normal leather may be quite weak, but it's very cheap ~", "I make it from cowhides for only 2 coins per hide ~ and", "it's so easy to craft that anyone can work with it.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Alternatively you could try hard leather. It's not so", "easy to craft, but I only charge 5 coins per cowhide to", "prepare it, and it makes much sturdier armour.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("I can also tan snake hides and dragonhides, suitable for", "crafting into the highest quality armour for rangers.")
								.add(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.SLEEPY)
								.setText("Thanks, I'll bear it in mind.")
								.start(player);
					}).start(player);
					break;
				case WISE_OLD_MAN:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("Greetings wise man.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Hi there, " + player.getUsername() +". Good to see you.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("How can I help you?")
					.add(DialogueType.OPTION).setOptionTitle("Select an Option")
						.firstOption("Can I equip a skillcape?", player1 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("Can I equip a skillcape?")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("You should be maxed in the skill of the cape that you", "are going to equip.")
									.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
									.setText("This means you should have a skill level of 99.")
								.start(player);
						}).secondOption("What are the benefits of skillcapes?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("What are the benefits of skillcapes?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("There are many benefits of equipping a skillcape.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Equipping the cape of some skills will increase", "all the experience gained from that skill.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("You also get access to use the unique skillcape emote.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("There are also some perks that comes with the capes.", "Some capes allow players to teleport, and some", "other gives boosted stats.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("You can check the full list of skillcape perks", "the @dre@Wiki</col> for more information.")
								.start(player);
						}).thirdOption("Where can I get a skillcape?", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
										.setText("Where can I get a skillcape?")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("The most common way is from the skill masters.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("You can get skill tasks and redeem them for coins or skill", "points that can be used to buy the skillcape.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("For instance, Ghommal in the Warrior's guild offers", " the combat skill capes.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Feel free to look around and find it through the game.", "Don't forget that the skillcapes provide bonus experience", "and perks that can help you with leveling.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("You can check the full list of skillcape perks", "the @dre@Wiki</col> for more information.")
										.start(player);
						}).fourthOption("I want to claim my 200M skillcapes.", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("I want to claim my 200M experience capes.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("The 200,000,000 experience milestone capes are", "not yet done as they are being made by the", "top specialist to provide great benefits.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("There will be an announcement once they are released.", "Stayed tuned!")
								.start(player);
								//Skillcape.claim200mCapes();
						}).fifthOption("I'll be back later.", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("I'll be back later.")
								.start(player);

						}).start(player);
					break;
				case WOODSMAN_TUTOR:
					ShopManager.open(player, ShopIdentifiers.WOODCUTTING_STORE);
					break;
				case WITCH_4409:
					ShopManager.open(player, ShopIdentifiers.BLOOD_SKILLING_STORE);
					break;
				case TORRCS:
					ShopManager.open(player, ShopIdentifiers.UNTRADEABLES_STORE);
					break;
				case VERMUNDI:
					ShopManager.open(player, ShopIdentifiers.ROBES_STORE);
					break;
				case HARRY:
				case GERRANT:
					ShopManager.open(player, ShopIdentifiers.FISHING_STORE);
					break;
					case SECURITY_GUARD:
					DialogueManager.start(player, 2500);
					// Set dialogue options
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player1, int option) {
							switch (option) {
								case 1:
									if (npc.getId() == SECURITY_GUARD) {
										player1.getBankpin().openBankPinCreation();
									}
									break;
								case 2:
									if (npc.getId() == SECURITY_GUARD) {
										player1.getBankpin().resetPin();
									}
									break;
								case 3:
									if (npc.getId() == SECURITY_GUARD) {
										player1.getBankpin().removePin();
									}
									break;
								case 4:
									// Cancel option
									DialogueManager.start(player1, 2662);
									break;
							}
						}
					});
					break;
				case MELEE_COMBAT_TUTOR:
					if ((player.getGameMode().isIronman() || player.getGameMode().isHardcore() || player.getGameMode().isUltimate())) {
						ShopManager.open(player, ShopIdentifiers.PVP_PREMIUM_STORE_IRONMAN);
					} else {
						ShopManager.open(player, ShopIdentifiers.PVP_PREMIUM_STORE);
					}
				/*if ((player.getGameMode().isIronman() || player.getGameMode().isHardcore() || player.getGameMode().isUltimate())) {
					ShopManager.open(player, ShopIdentifiers.DISCOUNTED_PREMIUM_STORE_IRONMAN);
				} else {
					ShopManager.open(player, ShopIdentifiers.DISCOUNTED_PREMIUM_STORE);
				}*/
					break;
				case DEATH:
					ShopManager.open(player, ShopIdentifiers.OUTLET_BLOOD_STORE);
					break;
				case LEAGUES_TUTOR:
				case LEAGUES_TUTOR_316:
				case LEAGUES_TUTOR_317:
				case EMBLEM_TRADER:
				case EMBLEM_TRADER_7943:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Hello, wanderer. What do you need?")
							.add(DialogueType.OPTION).setOptionTitle("Select an Option")
							.firstOption("How can I get Blood money?", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
										.setText("How can I get Blood money?")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Blood money is the second most popular current after coins,", "there are many ways to get it. First, the easiest", "way is by @red@completing Slayer tasks</col>.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Playing the @red@Weapon Minigame</col> or @red@Battle Royale</col>", "can be massisvely rewarding if you are the winner.", "Players that participated still get a small reward.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("@red@PKing</col> also rewards great amounts specically", "if you or your opponent is on a @red@killstreak</col>. The ", "greater the opponent's streak the bigger the reward", "Killing players with high streak increaes rewards too.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("@red@Completing achievements</col> depending on the task, and", "by @red@Earning participation points</col> by doing in game activities.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Moreover, slaying the @red@Wilderness Spirit</col> once", "it spawns can reward up to 55k Blood money.", "Participating players do get a little reward when the", "spirit is slain.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Some @red@Mystery boxes</col> rewards Blood money", "aswell as @red@Muddy chest</col> which gives a lot more.", "@red@Skilling supplies</col> also does contain Blood money.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Last but not least, you can buy Blood money from the", "@red@Voting store</col>, or by defeating the @red@Wilderness Bots</col>.", "There's a 1/15 chance to get 10k Blood money for", "defeating them.")
										.start(player);

							}).secondOption("What can I do with Blood money?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("What can I do with Blood money?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Quite a lot actually, you can spend Blood money", "in some stores to @red@buy some of the greatest items</col>.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("You can also @red@Repair</col> your broken items", "by speaking with @dre@Perdu</col>, or @red@Imbue Items</col> which", "will imbue your rings for a fee.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Some players prefer to gamble or stake on this currency.", "It's all up to you " + player.getUsername() +"!")
								.start(player);
					}).thirdOption("What are the Wilderness perks?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("What are the Wilderness perks?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("The greatest perk is the Wilderness Resource Area.", "This area allows skillers to train while getting", "50% increased experience excluding Prayer.", "It's very useful area.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("PKing players can reward Blood money, and if you", "or the opponent is on a kill streak, then that", "increaes the rewards considerably.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Slaying any boss in the Wilderness will drop some", "Blood money!")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("You also have the Wilderness agility course which offers", "the highest Agility experience from all courses.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Make sure to stay safe while in the Wilderness!")
								.start(player);
					}).fourthOption("What are Wilderness Bots doing here?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("What are Wilderness Bots doing here?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Wilderness Bots are here for players to train their", "PKing skills.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Defeating those bots will yield a chance of 1/15 to drop", "10k Blood money to the player.")

								.start(player);
					}).fifthOption("Next...", player1 -> {
						new DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
								.firstOption("I want to sell my emblems.", player2 -> {

									player1.setDialogueOptions(new DialogueOptions() {
										@Override
										public void handleOption(Player player1, int option) {
											if (option == 1) {
												if (npc.getId() == EMBLEM_TRADER_7943) {
													int cost = BountyHunterManager.getValueForArtifacts(player1, true);
													player1.getPacketSender().sendMessage("@red@You have received " + cost + " blood money for your artefact(s).");
													DialogueManager.start(player1, 2806);
												} else {
													int cost = BountyHunterManager.getValueForEmblems(player1, true);
													player1.getPacketSender().sendMessage("@red@You have received " + cost + " blood money for your emblem(s).");
													DialogueManager.start(player1, 4);
												}
											} else {
												player1.getPacketSender().sendInterfaceRemoval();
											}
										}
									});
									if (npc.getId() == EMBLEM_TRADER_7943) {
										int value = BountyHunterManager.getValueForArtifacts(player1, false);

										if (value > 0) {
											player1.setDialogue(DialogueManager.getDialogues().get(10));
											DialogueManager.sendStatement(player1, "I will give you " + Misc.format(value) + " blood money for those artefacts. Agree?");
										} else {
											DialogueManager.start(player1, 2637);
										}
									} else {
										int value = BountyHunterManager.getValueForEmblems(player1, false);

										if (value > 0) {
											player1.setDialogue(DialogueManager.getDialogues().get(10));
											DialogueManager.sendStatement(player1, "I will give you " + Misc.format(value) + " blood money for those emblems. Agree?");
										} else {
											DialogueManager.start(player1, 5);
										}
									}
								}).secondOption("Can I have a skull?", player3 -> {
							if (player1.isSkulled()) {
								DialogueManager.start(player1, 3);
							} else {
								DialogueManager.start(player1, 22);
								player1.setDialogueOptions(new DialogueOptions() {
									@Override
									public void handleOption(Player player1, int option) {

										if (option == 1)
											player1.getCombat().skull(SkullType.WHITE_SKULL, 3600);
										else if (option == 2)
											player1.getCombat().skull(SkullType.RED_SKULL, 3600);

										player1.getPacketSender().sendInterfaceRemoval();
									}
								});
							}
						}).thirdOption("What do you have to trade?", player3 -> {
							ShopManager.open(player1, ShopIdentifiers.PVP_STORE);
						}).addCancel("Cancel.").start(player);
					}).start(player);
					break;

				case PERDU:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("What's up?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("I have some broken stuff to fix! What's up?")
							.add(DialogueType.OPTION).setOptionTitle("Select an Option")
							.firstOption("What items can be imbued?", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
										.setText("What items can be imbued?")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("It costs a lot of Blood money to imbue your items.", "Starting from 35,000 Blood money.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Imbued items will upgrade your item into its stronger form.", "This means the stats and effects are also increased.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("There are many items that can be imbued such as:", "@dre@Black mask</col>, @dre@Crystal items</col>, @dre@Berserker Ring</col>,", "@dre@Ring of Suffering</col>, @dre@Slayer helmets</col>, and @dre@Magic shortbow</col>")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Do you have any items that you want to imbue?")
										.add(DialogueType.OPTION).setOptionTitle("Select an Option")
										.firstOption("Yes, please.", player2 -> {
											int cost = ImbuedableItems.getTotalImbueCost(player2);

											if (cost > 0) {
												player2.setDialogue(DialogueManager.getDialogues().get(10)); // Yes
												// /
												// no
												// option
												DialogueManager.sendStatement(player2,
														"It will cost you " + NumberFormat.getInstance().format(cost)
																+ " blood money to imbue all of your items. Agree?");
											} else {
												DialogueManager.start(player2, 2666);
											}
											player2.setDialogueOptions(new DialogueOptions() {
												@Override
												public void handleOption(Player player2, int option) {
													if (option == 1) {
														ImbuedableItems.imbueItems(player2);
													} else {
														player2.getPacketSender().sendInterfaceRemoval();
													}
												}
											});
										}).addCancel("Not at the moment.").start(player);

							}).secondOption("What items can be charged?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("What items can be charged?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("There are many items on Grinderscape that can be", "charged or refilled.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("@dre@Revenant items</col> can be charged using @dre@Revenant ether</col>.", "It requires a minimum of 1,000 ether to activate the weapon.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("@dre@Tome of fire</col> can be charged with burned pages.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("@dre@Bone crusher</col> can be charged with Ecto-tokens.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("@dre@Items with (c)</col> are corrupted items that can't be used", "in the Wilderness and degrade after usage.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("@dre@Dragon shields</col> can be charged with dragon's breath.", "Note: This makes them untradeable.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("@dre@Jewerly items</col> can be charged by using them", "on the Fountain of Rune in the Wilderness.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("@dre@Rune pouch</col> doesn't need charges, but can be", "filled with runes.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.CALM)
								.setText("I can be charged with Blood money.", "Now sort your mind.")
								.start(player);
					}).thirdOption("Why do you charge a lot for repairs?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Why do you charge a lot for repairs?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANGRY_3)
								.setText("What do you mean by that question!")
								.add(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.ANGRY_2)
								.setText("Your prices are very high!")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("It takes a lot of effort and work to repair,", "or imbue your items.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("You can find someone else if you are not happy!")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("I should be calling one of those bots against you!").setExpression(DialogueExpression.ANGRY_3)
								.add(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.LAUGHING)
								.setText("Haha! Good try! Good luck with that!")
								.start(player);
					}).fourthOption("How can I enchant my revenant weapons?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("How can I enchant my revenant weapons?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Revenant weapons can be enchanted or charged by", "using an @dre@Revenant ether</col> on them.", "It requires a minimum of 1,000 ether to activate the weapon.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("You can examine the item for more information, or", "check out our @dre@Wiki</col> for more.")

								.start(player);
					}).fifthOption("Next...", player1 -> {
						new DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
								.firstOption("I want to repair my broken items.", player2 -> {
									int cost = BrokenItems.getRepairCost(player1);

									if (cost > 0) {
										player1.setDialogue(DialogueManager.getDialogues().get(10)); // Yes
										// /
										// no
										// option
										DialogueManager.sendStatement(player1,
												"It will cost you " + NumberFormat.getInstance().format(cost)
														+ " blood money to fix your broken items. Agree?");
									} else {
										DialogueManager.start(player1, 2725);
									}
									player1.setDialogueOptions(new DialogueOptions() {
										@Override
										public void handleOption(Player player1, int option) {
											if (option == 1) {
												BrokenItems.repair(player1);
											} else {
												player1.getPacketSender().sendInterfaceRemoval();
											}
										}
									});
								}).secondOption("I want to repair my barrows items.", player3 -> {
										DialogueManager.start(player1, 2631);
						}).thirdOption("I want to imbue my items.", player3 -> {
							int cost = ImbuedableItems.getTotalImbueCost(player1);

							if (cost > 0) {
								player1.setDialogue(DialogueManager.getDialogues().get(10)); // Yes
								// /
								// no
								// option
								DialogueManager.sendStatement(player1,
										"It will cost you " + NumberFormat.getInstance().format(cost)
												+ " blood money to imbue all of your items. Agree?");
							} else {
								DialogueManager.start(player1, 2666);
							}
							player1.setDialogueOptions(new DialogueOptions() {
								@Override
								public void handleOption(Player player1, int option) {
									if (option == 1) {
										ImbuedableItems.imbueItems(player1);
									} else {
										player1.getPacketSender().sendInterfaceRemoval();
									}
								}
							});

						}).fourthOption("Nothing.", player3 -> {
							new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
									.setText("Nothing.")
									.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANGRY_2)
									.setText("Get yourself straight, and stop bothering me!").start(player);
						}).start(player);
					}).start(player);
					break;
				case ACCOUNT_GUIDE:
					new DialogueBuilder(DialogueType.NPC_STATEMENT)
							.setExpression(DialogueExpression.HAPPY)
							.setText("Hey there "+player.getUsername()+"!",
									"I am here to assist players with their adventure.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("What would you like me to help you with?")
							.add(DialogueType.OPTION)
							.firstOption("How can I make money?", player1 -> {
								new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
										.setText("How can I make money?")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("That's a very vague question " + player.getUsername() +"..")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Let me tell you the basics and it is for you", "to figure out the rest and make your own formula.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("First, the easiest way is by Thieving off the stalls", "Completing skilling tasks can be very rewarding.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Completion of Slayer tasks do reward Blood money", "which is considered as valuable currency.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Skilling can also earn you money depending on", "what skill you train.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Participating in Minigames can reward you generously.", "It varies depending on the minigame.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("PKing can reward you with increased rewards", "when you or your opponent's is on a killstreak.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Slaying bosses can drop rare and epic items", "which can be sold to other players for a lot.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Some players prefer to risk themselves and gamble.", "@red@Remember you are doing this at your own risk.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Completeting achievements is also a good way to earn", "extra items and money, depending on the achievement.")
										.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
										.setText("Finally you can buy items from the store on website, or", "you can do that voting to the server.")
										.start(player);
							}).secondOption("Where do I train skilling?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Where do I train skilling?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("There are various ways of training your skills.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("The easiest way to begin with is to teleport", "to the skilling location of the skill by", "using the teleport interface from @dre@Ancient wizard</col>.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("If you prefer to train in guilds, you can use", "the @dre@Skills necklace</col> to teleport to the guild.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Finally, training in the Wilderness Resource Area can", "train some skills, and grant 50% bonus experience.")
								.start(player);
							}).thirdOption("Where is the Gilded altar?", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Where is the gilded altar?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("The gilded altar can be found in The New Peninsula.", "You can still use the regular bones on regular altar.")
								.start(player);
							}).fourthOption("Can I have a guide book, please?", player1 -> {

						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("Can I have a guide book, please?")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Absolutely! A guide book is very important to help", "with your journey.")
								.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(784, 200)
								.setText("The man hands you a guide book.")
								.setAction(player2 -> ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(784, 1))).start(player);
							}).addCancel("I don't need any help.").start(player);
					break;
				case SQUIRE_2949:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("I want to leave.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Away you go then, the lander will take you back.")
							.setAction(player2 -> {
								PestControl.removePlayerFromGame(player);
								player.moveTo(new Position(2657, 2639, 0));
							}).start(player);
					break;
				default:
					if ((NpcDefinition.forId(npc.npcId()).getName().contains("Fishing"))) {
						return;
					}
					player.getPacketSender()
							.sendMessage("" + (NpcDefinition.forId(npc.npcId()).getName().replaceAll("_", " ")
									+ " is not interested to talk with you."));
					break;
			}
		};

		player.setWalkToTask(new WalkToAction(player, npc, action, WalkToAction.Policy.NO_RESET_ENTITY_INTERACTION_ON_EXECUTION, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
	}

	public void handleSecondClick(Player player, NPC npc) {

		if (player.getRights() == PlayerRights.DEVELOPER) {
			player.getPacketSender().sendMessage(
					"Second click NPC: " + Integer.toString(npc.getId()) + ". " + npc.getPosition().toString());
		}

		DebugManager.debug(player, "npc-option", "2: npc: "+npc.getId()+", pos: "+npc.getPosition().toString());

		// Check if we're thieving..
		if (Pickpocketing.canPickpocket(player, npc)) {
			player.setWalkToTask(new WalkToAction<>(player, npc, 2, 0, () -> {

				if (!npc.isAlive()) {
					resetInteractions(player, npc);
					return;
				}
				if (Pickpocketing.init(player, npc)) {
					return;
				}

			}, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES, WalkToAction.Policy.ALLOW_UNDER));
			return;
		}

		final Executable action = () -> {
			player.subscribe(event -> {

				if (event == PlayerEvents.MOVED || event == PlayerEvents.LOGGED_OUT) {
					npc.resetEntityInteraction();
					return true;
				}

				final Entity playerInteracting = player.getInteractingEntity();
				final Entity npcInteracting = npc.getInteractingEntity();

				if (playerInteracting instanceof NPC && playerInteracting != npc) {
					npc.resetEntityInteraction();
					return true;
				}

				if (npcInteracting instanceof Player && npcInteracting != player) {
					npc.resetEntityInteraction();
					return true;
				}

				if (npc.getPosition().getDistance(player.getPosition()) > 1) {
					npc.debug("player too far away, resetting...");
					npc.resetEntityInteraction();
					return true;
				}
				return false;
			});



			if (!npc.fetchDefinition().getName().toLowerCase().equals("fishing spot")
					&& npc.getId() != PUMPY && npc.getId() != DUMPY && npc.getId() != DUMPY_7387 && npc.getId() != STUMPY
					&& npc.getId() != NUMPTY && npc.getId() != THUMPY) {
				npc.setEntityInteraction(player);
			}

			if(PacketInteractionManager.handleNpcInteraction(player, npc, 2)) {
				return;
			}
			if (NPCActions.INSTANCE.handleClick(player, npc, Type.SECOND_OPTION))
				return;
			if (player.getClueScrollManager().handleNPCAction(2, npc))
				return;

			// Check if we're picking up our pet..
			if (PetHandler.pickup(player, npc)) {
				return;
			}

			switch (npc.getId()) {
				case BARRICADE:
					if (player.getInventory().contains(590)) {
						BarricadeManager.fireBarricade(player, player.getInventory().getSlot(590), npc);
					} else {
						player.sendMessage("You need a tinderbox to do that!");
					}
					break;
				case BARRICADE_5723:
					BarricadeManager.extinguishBarricade(player, npc);
					break;
				case VOID_KNIGHT:
				case VOID_KNIGHT_1757:
				case VOID_KNIGHT_1758:
					ShopManager.open(player, ShopIdentifiers.COMMENDATION_POINTS_EXCHANGE);
					break;
				case ELITE_VOID_KNIGHT:
					ShopManager.open(player, ShopIdentifiers.COMMENDATION_POINTS_EXCHANGE_2);
					break;
				case ROAVAR:
					ShopManager.open(player, ShopIdentifiers.DODGY_MIKES_SECOND_HAND_CLOTHING);
					break;
				case MASTER_FISHER:
					ShopManager.open(player, ShopIdentifiers.FISHING_STORE_MASTER);
					break;
				case SQUIRE_1769:
				case CUSTOMS_OFFICER:
					WizardSig.travel(player, "Port Sarim", new Position(3038 + Misc.getRandomInclusive(2), 3202 + Misc.getRandomInclusive(1)), 171);
					break;
				case SQUIRE_1770:
					WizardSig.travel(player, "Pest Control", Teleporting.TeleportLocation.PEST_CONTROL.getPosition(), 171);
					break;
				case SEAMAN_THRESNOR:
					WizardSig.travel(player, "Karamja", new Position(2956, 3146), 171);
					break;
				case JARAAH:
					if (EntityExtKt.passedTime(player, Attribute.LAST_REFRESH, 1, TimeUnit.MINUTES, false, true)) {
						// player.getPacketSender().sendMessage("You
						// feel slightly refreshed.");
						player.restoreRegularAttributes();
						DialogueManager.sendStatement(player, "You feel slightly refreshed.");
						player.performGraphic(new Graphic(436));
						player.getPacketSender().sendAreaPlayerSound(Sounds.HEALED_BY_NURSE);
					} else {
						player.getPacketSender()
								.sendMessage("You must wait one minute before being healed again.");
						return;
					}
					break;
				case MERCENARY_CAPTAIN:
					DialogueManager.sendStatement(player, "You carefully watch and follow the intimidating captain...but he notices you!");
					npc.say("Hey you filthy! Get off me before I call off the guards!");
					TaskManager.submit(new Task(2) {
						@Override
						public void execute() {
							stop();
							npc.getCombat().initiateCombat(player);
						}
					});
					break;
				case GERRANT:
				case GERRANT_2891:
					ShopManager.open(player, ShopIdentifiers.FISHING_STORE);
					break;
				case GADRIN:
					ShopManager.open(player, ShopIdentifiers.GADRIN_MINING_MASTER);
					break;
				case MASTER_SMITHING_TUTOR:
					ShopManager.open(player, ShopIdentifiers.MASTER_SMITHING_STORE);
					break;
				case IGNATIUS_VULCAN:
					ShopManager.open(player, ShopIdentifiers.MASTER_FIREMAKING_STORE);
					break;
				case HUNTING_EXPERT_1504: // Hunter store
					ShopManager.open(player, 38);
					break;
				case MASTER_CRAFTER:
					ShopManager.open(player, ShopIdentifiers.MASTER_CRAFTING_STORE);
					break;
				case KAQEMEEX:
					ShopManager.open(player, ShopIdentifiers.MASTER_HERBLORE_STORE);
					break;
				case BROTHER_JERED:
					ShopManager.open(player, ShopIdentifiers.MASTER_PRAYER_STORE);
					break;
				case HEAD_CHEF:
					ShopManager.open(player, ShopIdentifiers.MASTER_COOKING_STORE);
					break;
				case AUBURY_11435:
					ShopManager.open(player, ShopIdentifiers.MASTER_RUNECRAFTING_STORE);
					break;
				case CAPN_IZZY_NOBEARD:
					ShopManager.open(player, ShopIdentifiers.MASTER_AGILITY_STORE);
					break;
				case TANNER:
					HideTanData.sendTanningInterface(player);
					break;
/*				case WISE_OLD_MAN:
					ShopManager.open(player, ShopIdentifiers.SKILLCAPE_STORE);
					break;*/
				case PERDU:
					// Set dialogue options
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player1, int option) {
							if (option == 1) {

								int cost = BrokenItems.getRepairCost(player1);

								player1.setDialogueOptions(new DialogueOptions() {
									@Override
									public void handleOption(Player player1, int option) {
										if (option == 1) {
											BrokenItems.repair(player1);
										} else {
											player1.getPacketSender().sendInterfaceRemoval();
										}
									}
								});

								if (cost > 0) {
									player1.setDialogue(DialogueManager.getDialogues().get(10)); // Yes
									// /
									// no
									// option
									DialogueManager.sendStatement(player1,
											"It will cost you " + NumberFormat.getInstance().format(cost)
													+ " coins to fix your broken items. Is this ok?");
								} else {
									DialogueManager.start(player1, 2725);
								}

							} else if (option == 2) {
								DialogueManager.start(player1, 2631);
							} else {
								int cost = ImbuedableItems.getTotalImbueCost(player1);

								player1.setDialogueOptions(new DialogueOptions() {
									@Override
									public void handleOption(Player player1, int option) {
										if (option == 1) {
											ImbuedableItems.imbueItems(player1);
										} else {
											player1.getPacketSender().sendInterfaceRemoval();
										}
									}
								});

								if (cost > 0) {
									player1.setDialogue(DialogueManager.getDialogues().get(10)); // Yes
									// /
									// no
									// option
									DialogueManager.sendStatement(player1,
											"It will cost you " + NumberFormat.getInstance().format(cost)
													+ " blood money to imbue all of your items. Is this ok?");
								} else {
									DialogueManager.start(player1, 2666);
								}
							}
						}
					});

					// Start main dialogue
					DialogueManager.start(player, 19);
					break;
				case ACCOUNT_GUIDE:
					MyCommandsInterface.sendInterface(player);
					break;
				case PARTY_PETE:
					Store.INSTANCE.openPremiumStore(player);
					break;
					/* Start of all OSRS shops */
				case WISTAN:
					ShopManager.open(player, ShopIdentifiers.BURTHORPE_SUPPLIES);
					break;
				case GRACE:
					ShopManager.open(player, ShopIdentifiers.GRACES_GRACEFUL_CLOTHING);
					break;
				case HAROLD:
				case TOSTIG:
					ShopManager.open(player, ShopIdentifiers.TOAD_AND_CHICKEN);
					break;
				case HURA:
					ShopManager.open(player, ShopIdentifiers.CROSSBOW_SHOP);
					break;
				case DROGO_DWARF:
					ShopManager.open(player, ShopIdentifiers.DROGOS_MINING_EMPORIUM);
					break;
				case NULODION:
					ShopManager.open(player, ShopIdentifiers.MULTICANNON_PARTS_FOR_SALE);
					break;
				case NURMOF:
					ShopManager.open(player, ShopIdentifiers.NURMOFS_PICKAXE_SHOP);
					break;
				case FRINCOS:
					ShopManager.open(player, ShopIdentifiers.FRINCOSS_FABULOUS_HERB_STORE);
					break;
				case CASSIE:
					ShopManager.open(player, ShopIdentifiers.CASSIES_SHIELD_SHOP);
					break;
				case GARDEN_SUPPLIER:
					ShopManager.open(player, ShopIdentifiers.GARDEN_CENTRE);
					break;
				case FLYNN:
					ShopManager.open(player, ShopIdentifiers.FLYNNS_MACE_MARKET);
					break;
				case HERQUIN:
					ShopManager.open(player, ShopIdentifiers.HERQUINS_GEMS);
					break;
				case WAYNE:
					ShopManager.open(player, ShopIdentifiers.WAYNES_CHAINS___CHAINMAIL_SPECIALIST);
					break;
/*				case BETTY:
					ShopManager.open(player, ShopIdentifiers.BETTYS_MAGIC_EMPORIUM);
					break;
				case GRUM:
					ShopManager.open(player, ShopIdentifiers.GRUMS_GOLD_EXCHANGE);
					break;*/
				case JATIX:
					ShopManager.open(player, ShopIdentifiers.JATIXS_HERBLORE_SHOP);
					break;
				case WIZARD_SININA:
					ShopManager.open(player, ShopIdentifiers.WIZARD_SININA_MAGIC_CAPE_STORE);
					break;
				case GAIUS:
					ShopManager.open(player, ShopIdentifiers.GAIUS_TWO_HANDED_SHOP);
					break;
				case SARAH:
					ShopManager.open(player, ShopIdentifiers.SARAHS_FARMING_SHOP);
					break;
				case OGRE_MERCHANT:
					ShopManager.open(player, ShopIdentifiers.GRUDS_HERBLORE_STALL);
					break;
				case UGLUG_NAR:
					ShopManager.open(player, ShopIdentifiers.UGLUGS_STUFFSIES);
					break;
				case JACK:
					ShopManager.open(player, ShopIdentifiers.MYTHICAL_CAPE_STORE);
					break;
				case PRIMULA:
					ShopManager.open(player, ShopIdentifiers.MYTHS_GUILD_HERBALIST);
					break;
				case ERDAN:
					ShopManager.open(player, ShopIdentifiers.MYTHS_GUILD_ARMOURY);
					break;
				case DIANA:
					ShopManager.open(player, ShopIdentifiers.MYTHS_GUILD_WEAPONRY);
					break;
				case FISHMONGER:
					ShopManager.open(player, ShopIdentifiers.ETCETERIA_FISH);
					break;
				case GREENGROCER:
					ShopManager.open(player, ShopIdentifiers.ISLAND_GREENGROCER);
					break;
				case VANLIGGA_GASTFRIHET:
					ShopManager.open(player, ShopIdentifiers.CONTRABAND_YAK_PRODUCE);
					break;
				case FLOSI_DALKSSON:
					ShopManager.open(player, ShopIdentifiers.FLOSIS_FISHMONGERS);
					break;
				case KEEPA_KETTILON:
					ShopManager.open(player, ShopIdentifiers.KEEPA_KETTILONS_STORE);
					break;
				case SKULI_MYRKA:
					ShopManager.open(player, ShopIdentifiers.WEAPONS_GALORE);
					break;
				case JOSSIK:
				case JOSSIK_4424:
					ShopManager.open(player, ShopIdentifiers.THE_LIGHTHOUSE_STORE);
					break;
				case MELANA_MOONLANDER:
					ShopManager.open(player, ShopIdentifiers.MOON_CLAN_GENERAL_STORE);
					break;
				case BABA_YAGA:
					ShopManager.open(player, ShopIdentifiers.BABA_YAGAS_MAGIC_SHOP);
					break;
				case RIMAE_SIRSALIS:
					ShopManager.open(player, ShopIdentifiers.MOON_CLAN_FINE_CLOTHES);
					break;
				case FINN:
					ShopManager.open(player, ShopIdentifiers.MISCELLANIAN_GENERAL_STORE);
					break;
				case HALLA:
					ShopManager.open(player, ShopIdentifiers.MISCELLANIAN_CLOTHES_SHOP);
					break;
				case OSVALD:
					ShopManager.open(player, ShopIdentifiers.MISCELLANIAN_FOOD_SHOP);
					break;
				case JOFRIDR_MORDSTATTER:
					ShopManager.open(player, ShopIdentifiers.NEITIZNOT_SUPPLIES);
					break;
				case SIGMUND_THE_MERCHANT:
					ShopManager.open(player, ShopIdentifiers.SIGMUND_THE_MERCHANT);
					break;
				case FISH_MONGER:
					ShopManager.open(player, ShopIdentifiers.FREMENNIK_FISH_MONGER);
					break;
				case SKULGRIMEN:
					ShopManager.open(player, ShopIdentifiers.SKULGRIMENS_BATTLE_GEAR);
					break;
				case YRSA:
					ShopManager.open(player, ShopIdentifiers.YRSAS_ACCOUTREMENTS);
					break;
				case VALAINE:
					ShopManager.open(player, ShopIdentifiers.VALAINES_SHOP_OF_CHAMPIONS);
					break;
				case ROMILY_WEAKLAX:
					ShopManager.open(player, ShopIdentifiers.PIE_SHOP);
					break;
				case ROACHEY:
					ShopManager.open(player, ShopIdentifiers.FISHING_GUILD_SHOP);
					break;
				case HELEMOS:
					ShopManager.open(player, ShopIdentifiers.HAPPY_HEROES_HEMPORIUM);
					break;
				case FIONELLA:
					ShopManager.open(player, ShopIdentifiers.LEGENDS_GUILD_GENERAL_STORE);
					break;
				case SIEGFRIED_ERKLE:
					ShopManager.open(player, ShopIdentifiers.LEGENDS_GUILD_SHOP_OF_USEFUL_ITEMS);
					break;
				case YARSUL:
					ShopManager.open(player, ShopIdentifiers.YARSULS_PRODIGIOUS_PICKAXES);
					break;
				case HENDOR:
					ShopManager.open(player, ShopIdentifiers.HENDORS_AWESOME_ORES);
					break;
				case BELONA:
					ShopManager.open(player, ShopIdentifiers.MINING_GUILD_MINERAL_EXCHANGE);
					break;
				case TRIBAL_WEAPON_SALESMAN:
					ShopManager.open(player, ShopIdentifiers.AUTHENTIC_TROWING_WEAPONS);
					break;
				case BOW_AND_ARROW_SALESMAN:
					ShopManager.open(player, ShopIdentifiers.DARGAUDS_BOWS_AND_ARROWS);
					break;
				case ANTON:
					ShopManager.open(player, ShopIdentifiers.WARRIOR_GUILD_ARMOURY);
					break;
				case HICKTON:
					ShopManager.open(player, ShopIdentifiers.FLETCHING_SKILL_MASTER);
					break;
				case VANESSA:
					ShopManager.open(player, ShopIdentifiers.VANESSAS_FARMING_SHOP);
					break;
				case BAKER:
					ShopManager.open(player, ShopIdentifiers.ARDOUGNE_BAKERS_STALL);
					break;
				case GEM_MERCHANT:
					ShopManager.open(player, ShopIdentifiers.ARDOUGNE_GEM_STALL);
					break;
				case SILVER_MERCHANT:
					ShopManager.open(player, ShopIdentifiers.ARDOUGNE_SILVER_STALL);
					break;
				case SPICE_SELLER:
					ShopManager.open(player, ShopIdentifiers.ARDOUGNE_SPICE_STALL);
					break;
				case ZENESHA:
					ShopManager.open(player, ShopIdentifiers.ZENESHAS_PLATE_MAIL_BODY_SHOP);
					break;
				case ARNOLD_LYDSPOR:
					ShopManager.open(player, ShopIdentifiers.ARNOLDS_ECLECTIC_SUPPLIES);
					break;
				case HECKEL_FUNCH:
					ShopManager.open(player, ShopIdentifiers.FUNCHS_FINE_GROCERIES);
					break;
				case GNOME_WAITER:
					ShopManager.open(player, ShopIdentifiers.GIANNES_RESTAURANT);
					break;
				case HUDO:
					ShopManager.open(player, ShopIdentifiers.GRAND_TREE_GROCERIES);
					break;
				case GULLUCK:
					ShopManager.open(player, ShopIdentifiers.GULLUCK_AND_SONS);
					break;
				case ROMETTI:
					ShopManager.open(player, ShopIdentifiers.ROMETTIS_FINE_FASHIONS);
					break;
				case BOLKOY:
					ShopManager.open(player, ShopIdentifiers.BOLKOYS_VILLAGE_SHOP);
					break;
				case CHADWELL:
					ShopManager.open(player, ShopIdentifiers.WEST_ARDOUGNE_GENERAL_STORE);
					break;
				case EZEKIAL_LOVECRAFT:
					ShopManager.open(player, ShopIdentifiers.LOVECRAFTS_TACKLE);
					break;
				case ALECK:
					ShopManager.open(player, ShopIdentifiers.ALECKS_HUNTER_EMPORIUM);
					break;
				case FRENITA:
					ShopManager.open(player, ShopIdentifiers.FRENITAS_COOKERY_SHOP);
					break;
				case RASOLO:
					ShopManager.open(player, ShopIdentifiers.RASOLO_THE_WANDERING_MERCHANT);
					break;
				case DAVON:
					ShopManager.open(player, ShopIdentifiers.DAVONS_AMULET_STORE);
					break;
				case ALFONSE_THE_WAITER:
					ShopManager.open(player, ShopIdentifiers.THE_SHRIMP_AND_PARROT);
					break;
				case ZAMBO:
					ShopManager.open(player, ShopIdentifiers.KARAMJA_WINES_SPIRITS_AND_BEERS);
					break;
				case OBLI:
					ShopManager.open(player, ShopIdentifiers.OBLIS_GENERAL_STORE);
					break;
				case FERNAHEI:
					ShopManager.open(player, ShopIdentifiers.FERNAHEIS_FISHING_HUT);
					break;
				case JIMINUA:
					ShopManager.open(player, ShopIdentifiers.JIMINUAS_JUNGLE_STORE);
					break;
				case GABOOTY:
					ShopManager.open(player, ShopIdentifiers.GABOOTYS_TAI_BWO_WANNAI_COOPERATIVE);
					break;
				case TAMAYU:
					ShopManager.open(player, ShopIdentifiers.TAMAYUS_SPEAR_STALL);
					break;
				case TIADECHE:
					ShopManager.open(player, ShopIdentifiers.TIADECHES_KARAMBWAN_STALL);
					break;
				case ALI_MORRISANE:
					ShopManager.open(player, ShopIdentifiers.ALIS_DISCOUNT_WARES);
					break;
				case DOMMIK:
					ShopManager.open(player, ShopIdentifiers.DOMMIKS_CRAFTING_STORE);
					break;
				case GEM_TRADER:
					ShopManager.open(player, ShopIdentifiers.GEM_TRADER);
					break;
				case LOUIE_LEGS:
					ShopManager.open(player, ShopIdentifiers.LOUIES_ARMOURED_LEGS_BAZAAR);
					break;
				case RANAEL:
					ShopManager.open(player, ShopIdentifiers.RANAELS_SUPER_SKIRT_STORE);
					break;
				case BANDIT_SHOPKEEPER:
					ShopManager.open(player, ShopIdentifiers.BANDIT_BARGAINS);
					break;
				case BEDABIN_NOMAD:
					ShopManager.open(player, ShopIdentifiers.BEDABIN_VILLAGE_BARTERING);
					break;
				case FADLI:
					ShopManager.open(player, ShopIdentifiers.SHOP_OF_DISTASTE);
					break;
				case KAZEMDE:
					ShopManager.open(player, ShopIdentifiers.NARDAH_GENERAL_STORE);
					break;
				case ARTIMEUS:
					ShopManager.open(player, ShopIdentifiers.NARDAH_HUNTER_SHOP);
					break;
				case ROKUH:
					ShopManager.open(player, ShopIdentifiers.ROKS_CHOCS_BOX);
					break;
				case SEDDU:
					ShopManager.open(player, ShopIdentifiers.SEDDUS_ADVENTURERS_STORE);
					break;
				case MARKET_SELLER:
					ShopManager.open(player, ShopIdentifiers.POLLNIVNEACH_GENERAL_STORE);
					break;
				case URBI:
					ShopManager.open(player, ShopIdentifiers.BLADES_BY_URBI);
					break;
				case JAMILA:
					ShopManager.open(player, ShopIdentifiers.JAMILAS_CRAFT_STALL);
					break;
				case NATHIFA:
					ShopManager.open(player, ShopIdentifiers.NATHIFAS_BAKE_STALL);
					break;
				case RAETUL:
					ShopManager.open(player, ShopIdentifiers.RAETUL_AND_COS_CLOTH_STORE);
					break;
				case EMBALMER:
					ShopManager.open(player, ShopIdentifiers.THE_SPICE_IS_RIGHT);
					break;
				case LURGON:
					ShopManager.open(player, ShopIdentifiers.DORGESH_KAAN_GENERAL_SUPPLIES);
					break;
				case NARDOK:
					ShopManager.open(player, ShopIdentifiers.NARDOKS_BONE_WEAPONS);
					break;
				case MILTOG:
					ShopManager.open(player, ShopIdentifiers.MILTOGS_LAMPS);
					break;
				case RELDAK:
					ShopManager.open(player, ShopIdentifiers.RELDAKS_LEATHER_ARMOUR);
					break;
				case AVA:
					ShopManager.open(player, ShopIdentifiers.AVAS_ODDS_AND_ENDS);
					break;
				case DIANGO:
					ShopManager.open(player, ShopIdentifiers.DIANGOS_TOY_STORE);
					break;
				case FORTUNATO:
					ShopManager.open(player, ShopIdentifiers.FORTUNATOS_FINE_WINE);
					break;
				case OZIACH:
					ShopManager.open(player, ShopIdentifiers.OZIACHS_ARMOUR);
					break;
				case PEKSA:
					ShopManager.open(player, ShopIdentifiers.HELMET_SHOP);
					break;
//				case AUBURY:
//					ShopManager.open(player, ShopIdentifiers.AUBURYS_RUNE_SHOP);
//					break;
				/*case SAWMILL_OPERATOR:
					ShopManager.open(player, ShopIdentifiers.CONSTRUCTION_SUPPLIES);
					break;*/
				case ZAFF:
					ShopManager.open(player, ShopIdentifiers.ZAFFS_SUPERIOR_STAFFS);
					break;
				case TEA_SELLER:
					ShopManager.open(player, ShopIdentifiers.YE_OLDE_TEA_SHOPPE);
					break;
				case AUREL:
					ShopManager.open(player, ShopIdentifiers.AURELS_SUPPLIES);
					break;
				case FIDELIO:
					ShopManager.open(player, ShopIdentifiers.GENERAL_STORE);
					break;
				case BARKER:
					ShopManager.open(player, ShopIdentifiers.BARKERS_HABERDASHERY);
					break;
				case RUFUS:
					ShopManager.open(player, ShopIdentifiers.RUFUS_MEAT_EMPORIUM);
					break;
				case TRADER_SVEN:
					ShopManager.open(player, ShopIdentifiers.TRADER_SVENS_BLACK_MARKET_GOODS);
					break;
				case RAZMIRE_KEELGAN:
					ShopManager.open(player, ShopIdentifiers.RAZMIRE_GENERAL_STORE);
					break;
				case GHOST_SHOPKEEPER:
					ShopManager.open(player, ShopIdentifiers.PORT_PHASMATYS_GENERAL_STORE);
					break;
				case EUDAV:
					ShopManager.open(player, ShopIdentifiers.LLETYA_GENERAL_STORE);
					break;
				case DALLDAV:
					ShopManager.open(player, ShopIdentifiers.LLETYA_ARCHERY_SHOP);
					break;
				case GETHIN:
					ShopManager.open(player, ShopIdentifiers.LLETYA_FOOD_STORE);
					break;
				/*case QUARTERMASTER:
					ShopManager.open(player, ShopIdentifiers.QUARTERMASTERS_STORES);
					break;*/
				case NOLAR:
					ShopManager.open(player, ShopIdentifiers.CAREFREE_CRAFTING_STALL);
					break;
				case HIRKO:
					ShopManager.open(player, ShopIdentifiers.CROSSBOW_SHOP);
					break;
				case HERVI:
					ShopManager.open(player, ShopIdentifiers.GREEN_GEMSTONE_GEMS);
					break;
				case RANDIVOR:
					ShopManager.open(player, ShopIdentifiers.KELDAGRIMS_BEST_BREAD);
					break;
				case KJUT:
					ShopManager.open(player, ShopIdentifiers.KJUTS_KEBABS);
					break;
				case TATI:
					ShopManager.open(player, ShopIdentifiers.PICKAXE_IS_MINE);
					break;
				case GULLDAMAR:
					ShopManager.open(player, ShopIdentifiers.SILVER_COG_SILVER_STALL);
					break;
				case GUNSLIK:
					ShopManager.open(player, ShopIdentifiers.GUNSLIKS_ASSORTED_ITEMS);
					break;
				case AGMUNDI:
					ShopManager.open(player, ShopIdentifiers.AGMUNDI_QUALITY_CLOTHES);
					break;
				case STONEMASON:
					ShopManager.open(player, ShopIdentifiers.KELDAGRIM_STONEMASON);
					break;
				case SARO:
					ShopManager.open(player, ShopIdentifiers.QUALITY_ARMOUR_SHOP);
					break;
				case SANTIRI:
					ShopManager.open(player, ShopIdentifiers.QUALITY_WEAPONS_SHOP);
					break;
				case VIGR:
					ShopManager.open(player, ShopIdentifiers.VIGRS_WARHAMMERS);
					break;
				case FILAMINA:
					ShopManager.open(player, ShopIdentifiers.FILAMINAS_WARES);
					break;
				case REGATH:
					ShopManager.open(player, ShopIdentifiers.REGATHS_WARES);
					break;
				case THYRIA:
					ShopManager.open(player, ShopIdentifiers.THYRIAS_WARES);
					break;
				case HORACE:
					ShopManager.open(player, ShopIdentifiers.LITTLE_SHOP_OF_HORACE);
					break;
				case LOGAVA:
					ShopManager.open(player, ShopIdentifiers.LOGAVA_GRICOLLERS_COOKING_SUPPLIES);
					break;
				case PERRY:
					ShopManager.open(player, ShopIdentifiers.PERRYS_CHOP_CHOP_SHOP);
					break;
				case VANNAH:
					ShopManager.open(player, ShopIdentifiers.VANNAHS_FARM_STORE);
					break;
				case MUNTY:
					ShopManager.open(player, ShopIdentifiers.LITTLE_MUNTYS_LITTLE_SHOP);
					break;
				case TOOTHY:
					ShopManager.open(player, ShopIdentifiers.TOOTHYS_PICKAXES);
					break;
				case THIRUS:
					ShopManager.open(player, ShopIdentifiers.THIRUS_URKARS_FINE_DYNAMITE_STORE);
					break;
				case FRANKIE:
					ShopManager.open(player, ShopIdentifiers.FRANKIES_FISHING_EMPORIUM);
					break;
				case KENELME:
					ShopManager.open(player, ShopIdentifiers.KENELMES_WARES);
					break;
				case LEENZ:
					ShopManager.open(player, ShopIdentifiers.LEENZS_GENERAL_SUPPLIES);
					break;
				case TYNAN:
					ShopManager.open(player, ShopIdentifiers.TYNANS_FISHING_SUPPLIES);
					break;
				case IFABA:
					ShopManager.open(player, ShopIdentifiers.IFABAS_GENERAL_STORE);
					break;
				case DAGA:
					ShopManager.open(player, ShopIdentifiers.DAGAS_SCIMITAR_SMITHY);
					break;
				case HAMAB:
					ShopManager.open(player, ShopIdentifiers.HAMABS_CRAFTING_EMPORIUM);
					break;
				case OOBAPOHK:
					ShopManager.open(player, ShopIdentifiers.OOBAPOHKS_JAVELIN_STORE);
					break;
				case SOLIHIB:
					ShopManager.open(player, ShopIdentifiers.SOLIHIBS_FOOD_STALL);
					break;
				case TUTAB:
					ShopManager.open(player, ShopIdentifiers.MAGIC_STALL);
					break;
				case PETRIFIED_PETE:
					ShopManager.open(player, ShopIdentifiers.PETRIFIED_PETES_ORE_SHOP);
					break;
				case MAIRIN:
					ShopManager.open(player, ShopIdentifiers.MAIRINS_MARKET);
					break;
				case MIKE:
					ShopManager.open(player, ShopIdentifiers.DODGY_MIKES_SECOND_HAND_CLOTHING);
					break;
				case SMITH:
					ShopManager.open(player, ShopIdentifiers.SMITHING_SMITHS_SHOP);
					break;
				case CHARLEY:
					ShopManager.open(player, ShopIdentifiers.TWO_FEET_CHARLEYS_FISH_SHOP);
					break;
				case SQUIRE_1765:
					ShopManager.open(player, ShopIdentifiers.VOID_KNIGHT_ARCHERY_STORE);
					break;
				case SQUIRE_1767:
					ShopManager.open(player, ShopIdentifiers.VOID_KNIGHT_MAGIC_STORE);
					break;
				case BETTY:
				case BETTY_5905:
					ShopManager.open(player, ShopIdentifiers.BETTYS_MAGIC_EMPORIUM);
					break;
				case GRUM:
				case GRUM_2889:
					ShopManager.open(player, ShopIdentifiers.GRUMS_GOLD_EXCHANGE);
					break;
				case NOTERAZZO:
					ShopManager.open(player, ShopIdentifiers.BANDIT_DUTY_FREE);
					break;
				case MAGE_OF_ZAMORAK:
					ShopManager.open(player, ShopIdentifiers.RUNECRAFTING_STORE);
					break;
				case DARREN:
					ShopManager.open(player, ShopIdentifiers.DARRENS_WILDERNESS_CAPE_SHOP);
					break;
				case EDWARD:
					ShopManager.open(player, ShopIdentifiers.EDWARDS_WILDERNESS_CAPE_SHOP);
					break;
				case IAN:
					ShopManager.open(player, ShopIdentifiers.IANS_WILDERNESS_CAPE_SHOP);
					break;
				case LUNDAIL:
					ShopManager.open(player, ShopIdentifiers.LUNDAILS_ARENA_SIDE_RUNE_SHOP);
					break;
				case CHAMBER_GUARDIAN:
					ShopManager.open(player, ShopIdentifiers.MAGE_ARENA_STAFFS);
					break;
				case NEIL:
					ShopManager.open(player, ShopIdentifiers.NEILS_WILDERNESS_CAPE_SHOP);
					break;
				case SIMON:
					ShopManager.open(player, ShopIdentifiers.SIMONS_WILDERNESS_CAPE_SHOP);
					break;
				case FAT_TONY:
					ShopManager.open(player, ShopIdentifiers.TONYS_PIZZA_BASES);
					break;
				case WILLIAM:
					ShopManager.open(player, ShopIdentifiers.WILLIAMS_WILDERNESS_CAPE_SHOP);
					break;
				case FAIRY_SHOP_KEEPER:
					ShopManager.open(player, ShopIdentifiers.ZANARIS_GENERAL_STORE);
					break;
				case IRKSOL:
					ShopManager.open(player, ShopIdentifiers.IRKSOL);
					break;
				case JUKAT:
					ShopManager.open(player, ShopIdentifiers.JUKAT);
					break;
				case FAIRY_FIXIT:
					ShopManager.open(player, ShopIdentifiers.FAIRY_FIXITS_FAIRY_ENCHANTMENT);
					break;
				case SAWMILL_OPERATOR:
					int cost = LogsToPlanks.getTotalConvertCost(player);
					if (cost > 0) {
						player.setDialogue(DialogueManager.getDialogues().get(10));
						DialogueManager.sendStatement(player,
								"It will cost you " + NumberFormat.getInstance().format(cost)
										+ " coins to exchange all of your logs. Agree?");

						player.setDialogueOptions(new DialogueOptions() {
							@Override
							public void handleOption(Player player2, int option) {
								if (option == 1) {
									LogsToPlanks.convertLogsToPlanks(player2);
								} else {
									player2.getPacketSender().sendInterfaceRemoval();
								}
							}
						});
					} else {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("I would like to exchange some planks.")
								.add(DialogueType.NPC_STATEMENT)
								.setText("I am sorry you don't have any logs I can exchange.")
								.add(DialogueType.NPC_STATEMENT)
								.setText("Come back later.")
								.start(player);
					}
						break;
				case HOLOY:
					ShopManager.open(player, ShopIdentifiers.CROSSBOW_SHOP_WHITE_WOLF_MOUNTAIN);
					break;
				case TRADER_STAN:
					ShopManager.open(player, ShopIdentifiers.TRADER_STANS_TRADING_POST);
					break;
					/* END OF OSRS SHOPS */
				case SECURITY_GUARD:
					player.getBankpin().openBankPinCreation();
					break;
				case BARBARIAN_GUARD_7724: // Vial toggle barbarian guard
					// toggle if locked normal
					if (player.unlockedVialCrushing()) {
						if (player.isVialCrushingToggled()) {
							new DialogueBuilder(DialogueType.STATEMENT)
									.setText("Do you want to stop smashing vials when they're empty?").add(DialogueType.OPTION).setOptionTitle("Select an Option")
									.firstOption("Yes.", player2 -> {
										player.setVialCrushingToggled(false);
										new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
												.setText("You're a funny sort of barbarian! But okay, you will", "no longer smash your vials as you drink your potions.")
												.start(player);
									}).addCancel("No").start(player);
						} else {
							new DialogueBuilder(DialogueType.STATEMENT)
									.setText("Do you want to smash vials when they're empty?").add(DialogueType.OPTION).setOptionTitle("Select an Option")
									.firstOption("Yes.", player2 -> {
										player.setVialCrushingToggled(true);
										new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
												.setText("It's all part of drinking like a barbarian! Okay, you will", "now smash your vials as you drink your potions.")
												.start(player);
									}).addCancel("No").start(player);
						}

					} else {
						new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("You better be doing me a favour first before I", "can serve you and teach you some barbarian tricks.").setExpression(DialogueExpression.ANGRY)
								.start(player);
					}

					break;
				case MAKEOVER_MAGE:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Do you want to change your appearance look", "for 50,000 coins?")
							.add(DialogueType.OPTION).setOptionTitle("Select an Option")
							.firstOption("Yes.", player1 -> {
								if (player.getInventory().getAmount(ItemID.COINS) >= 50_000) {
									player.getPacketSender().sendAppearanceConfig(player.getAppearance().isMale() ? 324 : 325, 0);

									player.getPacketSender().sendAppearanceConfig(301, player.getAppearance().getLook()[Appearance.HEAD]);

									if (player.getAppearance().isMale()) {
										player.getPacketSender().sendAppearanceConfig(302, player.getAppearance().getLook()[Appearance.BEARD]);
									}

									player.getPacketSender().sendAppearanceConfig(304, player.getAppearance().getLook()[Appearance.CHEST]);
									player.getPacketSender().sendAppearanceConfig(306, player.getAppearance().getLook()[Appearance.ARMS]);
									player.getPacketSender().sendAppearanceConfig(308, player.getAppearance().getLook()[Appearance.HANDS]);
									player.getPacketSender().sendAppearanceConfig(310, player.getAppearance().getLook()[Appearance.LEGS]);
									player.getPacketSender().sendAppearanceConfig(312, player.getAppearance().getLook()[Appearance.FEET]);
									player.getPacketSender().sendAppearanceConfig(314, player.getAppearance().getLook()[Appearance.HAIR_COLOUR]);
									player.getPacketSender().sendAppearanceConfig(316, player.getAppearance().getLook()[Appearance.TORSO_COLOUR]);
									player.getPacketSender().sendAppearanceConfig(318, player.getAppearance().getLook()[Appearance.LEG_COLOUR]);
									player.getPacketSender().sendAppearanceConfig(322, player.getAppearance().getLook()[Appearance.SKIN_COLOUR]);
									player.getPacketSender().sendInterfaceRemoval().sendInterface(3559);
									player.getAppearance().setCanChangeAppearance(true);
									player.getInventory().delete(995, 50_000);
									player.sendMessage("You have paid 50_000 to the make-over mage to help change your looks.");
								} else {
									new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("OFF! You better pay me 50_000 first to change", "your looks.")
											.start(player);
								}
							}).secondOption("No thanks.", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("No thanks.")
								.start(player);
					}).start(player);
					break;
				case ALI_THE_CAMEL_MAN:
					ShopManager.open(player, ShopIdentifiers.FREMENNIK_STORE);
					break;
				case GAMER:
					Lottery.startGamblerDialogue(player);
					break;
/*				case ASHILD:
					ShopManager.open(player, ShopIdentifiers.BOSS_CONTRACT_STORE);
					break;*/
				case KAMFREENA:
					break;
				case TZHAARHURTEL:
					ShopManager.open(player, ShopIdentifiers.TZHAAR_STORE);
					break;
				case 7688:
					ShopManager.open(player, ShopIdentifiers.TZHAAR_HUR_TELS_EQUIPMENT_STORE);
					break;
				case 7689:
					ShopManager.open(player, ShopIdentifiers.TZHAAR_HUR_LEKS_ORE_AND_GEM_STORE);
					break;
				case BARMAN:
					ShopManager.open(player, ShopIdentifiers.OSRS_TOKENS_STORE);
					break;
				case BORAT:
					ShopManager.open(player, ShopIdentifiers.PARTICIPATION_POINTS_EXCHANGE);
					break;
				case HANS:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("" + PlayerUtil.sendPlayTimeHans(player) +"", "since you arrived.").start(player);
					break;
				case SIGMUND_5322:
					new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
							.setText("Welcome " + player.getUsername() +" to the Lumbridge Castle!").start(player);
					break;
				case CANDLE_MAKER:
					ShopManager.open(player, ShopIdentifiers.CANDLE_STORE);
					break;
				case TURAEL:
				case MAZCHNA:
				case VANNAKA:
				case DURADEL:
				case CHAELDAR:
				case KRYSTILIA:
				case NIEVE:
				case 8623:
					SlayerManager.check(player, npc.getId());
					break;
				case BOB_BARTER_HERBS:
					ShopManager.open(player, ShopIdentifiers.HERBLORING_STORE);
					break;
				case COMBAT_INSTRUCTOR:
					ShopManager.open(player, ShopIdentifiers.STARTER_SUPPLIES_STORE);
					break;
				case TZHAARMEJJAL:
					ExchangeFireCape.exchange(player, 1);
					break;
				case CHARLIE_THE_COOK:
				case APOTHECARY:
					ShopManager.open(player, ShopIdentifiers.CONSUMEABLES_STORE);
					break;
				case HARRY:
					ShopManager.open(player, ShopIdentifiers.FISHING_STORE);
					break;
				case SHOP_ASSISTANT:
				case SHOP_ASSISTANT_2816:
				case SHOP_ASSISTANT_2818:
				case SHOP_ASSISTANT_2820:
				case SHOP_ASSISTANT_2822:
				case SHOP_ASSISTANT_2824:
				case SHOP_ASSISTANT_2826:
				case SHOP_ASSISTANT_2885:
				case FAIRY_SHOP_ASSISTANT:
				case SHOP_KEEPER:
				case SHOP_KEEPER_2815:
				case SHOP_KEEPER_2817:
				case SHOP_KEEPER_2819:
				case SHOP_KEEPER_2821:
				case SHOP_KEEPER_2823:
				case SHOP_KEEPER_2825:
				case SHOP_KEEPER_2888:
				case SHOP_KEEPER_2894:
				case SHOP_KEEPER_7769:
				case SHOP_KEEPER_7913:
				case ARHEIN:
				case SQUIRE_1768:
					ShopManager.open(player, ShopIdentifiers.GENERAL_STORE);
					break;

				case SHOP_KEEPER_2884:
					ShopManager.open(player, ShopIdentifiers.VARROCK_SWORDSHOP);
					break;
				case BANKER_1613:
				case BANKER_1618:
				case BANKER_3090:
				case BANKER_3091:
				case BANKER_3092:
				case BANKER_3093:
				case BANKER_2633:
				case BANKER:
				case BANKER_1479:
				case BANKER_1480:
				case BANKER_2117:
				case BANKER_2119:
				case BANKER_2118:
				case BANKER_2292:
				case BANKER_2293:
				case BANKER_2368:
				case BANKER_2369:
				case BANKER_3094:
				case BANKER_2897:
				case BANKER_2898:
				case BANKER_3318:
				case BANKER_3887:
				case BANKER_3888:
				case BANKER_4054:
				case BANKER_4055:
				case BANKER_1633:
				case BANKER_1634:
				case BANKER_3089:
				case BANKER_6859:
				case BANKER_6860:
				case BANKER_6861:
				case BANKER_6862:
				case BANKER_6863:
				case BANKER_6864:
				case BANKER_6939:
				case BANKER_6940:
				case BANKER_6941:
				case BANKER_6942:
				case BANKER_6969:
				case TZHAARKETZUH:
				case JADE:
				case 7678:
					if (player.busy()) {
						player.getPacketSender().sendMessage("You can't do this right now.", 1000);
						return;
					}
					player.getBankpin().openBank();
					//player.getBank(player.getCurrentBankTab()).open();
					break;
				case ZEKE:
					ShopManager.open(player, ShopIdentifiers.ZEKES_SUPERIOR_SCIMITARS);
					break;
				case RICHARD:
				case RICHARD_2200:
					ShopManager.open(player, ShopIdentifiers.TEAMCAPE_STORE);
					break;
				case DUNSTAN:
					ShopManager.open(player, ShopIdentifiers.THIEVING_STORE);
					break;
				case LOWE:
					ShopManager.open(player, ShopIdentifiers.RANGE_GEAR_STORE);
					break;
				case LOWE_8683:
					ShopManager.open(player, ShopIdentifiers.LOWES_ARCHERY_EMPORIUM);
					break;
				case ARMOUR_SALESMAN:
					ShopManager.open(player, ShopIdentifiers.RANGE_GEAR_STORE);
					break;
				case FANCY_DRESS_STORE:
					ShopManager.open(player, ShopIdentifiers.FANCY_DRESS_STORE);
					break;
				case SAILOR_3936:
					/*if (player.getInventory().getAmount(ItemIdentifiers.COINS) >= 5_000_000) {
						player.getInventory().delete(995, 5_000_000);
						player.moveTo(new Position(2516 + Misc.getRandomInclusive(2), 3882 + Misc.getRandomInclusive(1)));
						player.getPacketSender().sendInterfaceRemoval();
						DialogueManager.sendStatement(player, "You paid the wizard and then you magically find yourself in the island!");
					} else {
						DialogueManager.start(player, 2574);
					}*/
					WizardSig.handleSecondClickOption(player);
					break;
				case GAMER_1012:
					AngelicCapeGamble.exchange(player, 1);
					break;
				case BRIAN:
					ShopManager.open(player, ShopIdentifiers.BRIANS_BATTLEAXE_BAZAAR);
					break;
				case ADVENTURER_EASY:
				case RICK:
					ShopManager.open(player, ShopIdentifiers.PURE_ITEMS_STORE);
					break;
				case ROMMIK:
					ShopManager.open(player, ShopIdentifiers.CRAFTING_STORE);
					break;
				case ALI_THE_FARMER:
				case TOOL_LEPRECHAUN:
					ShopManager.open(player, ShopIdentifiers.FARMING_STORE);
					break;
				case PHANTUWTI_FANSTUWI_FARSIGHT:
					ShopManager.open(player, ShopIdentifiers.SKILLING_STORE);
					break;
				case MINER_MAGNUS:
				case HRING_HRING:
					ShopManager.open(player, ShopIdentifiers.MINING_STORE);
					break;
				case PIRATE_JACKIE_THE_FRUIT:
					ShopManager.open(player, ShopIdentifiers.AGILITY_TICKET_EXCHANGE);
					break;
				case ALRENA_4250:
					ShopManager.open(player, ShopIdentifiers.SKILLING_POINTS_STORE);
					break;
				case QUARTERMASTER:
					Voting.INSTANCE.openVotePointStore(player);
					break;
				case HORVIK:
					break;
				case VERMUNDI:
					ShopManager.open(player, ShopIdentifiers.ROBES_STORE);
					break;
				case WITCH_4409:
					ShopManager.open(player, ShopIdentifiers.BLOOD_SKILLING_STORE);
					break;
				case MARTIN_THWAIT:
					ShopManager.open(player, 33);
					break;
				case WOODSMAN_TUTOR:
					ShopManager.open(player, 18);
					break;
				case LEAGUES_TUTOR:
				case LEAGUES_TUTOR_316:
				case LEAGUES_TUTOR_317:
				case EMBLEM_TRADER:
				case EMBLEM_TRADER_7943:
					ShopManager.open(player, ShopIdentifiers.PVP_STORE);
					break;
				case ARCHMAGE_SEDRIDOR:
				case 5034:
					if (!QuestManager.hasCompletedQuest(player, "Rune Mysteries")) {
						player.sendMessage("You need to complete " +
								"the quest 'Rune Mysteries' quest to use this teleport.");
						return;
					}
					INSTANCE.mineTeleport(player, npc.getId());
					break;
				case MAGIC_INSTRUCTOR:
					ShopManager.open(player, ShopIdentifiers.MAGE_STORE);
					break;
				case ASHUELOT_REIS_11289:
					player.getBankpin().openBank();
					break;
				default:
					Optional<Pet> pet = Pet.getPet(npc.getId());
					if (pet != null && pet.isPresent()) {
						player.getPacketSender().sendMessage("You can't pickup someone else's pet.", 1000);
					} else {
						player.getPacketSender()
								.sendMessage("" + (NpcDefinition.forId(npc.npcId()).getName().replaceAll("_", " ")
										+ " is not selling anything at the moment."), 1000);
					}
					break;
			}
		};

		player.setWalkToTask(new WalkToAction(player, npc, action, WalkToAction.Policy.NO_RESET_ENTITY_INTERACTION_ON_EXECUTION, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
	}

	public void handleThirdClick(Player player, NPC npc) {

		if (player.getRights() == PlayerRights.DEVELOPER) {
			player.getPacketSender().sendMessage(
					"Third click NPC: " + Integer.toString(npc.getId()) + ". " + npc.getPosition().toString(), 1000);
		}

		DebugManager.debug(player, "npc-option", "3: npc: "+npc.getId()+", pos: "+npc.getPosition().toString());


		Optional<NpcDropDefinition> drops = NpcDropDefinition.get(npc.getId());

		if (npc.isAlive() && npc.getHitpoints() > 0 && npc.fetchDefinition().getCombatLevel() > 0 && npc.fetchDefinition().isAttackable()) {
			if (!drops.isPresent()) { // The reason this is here and not in NpcInformation is to fix (so it doesn't walk to
										// NPC you are trying to view drops if it doesn't drop any items).

				// Clear Steps
				player.getMotion().clearSteps();
				player.getPacketSender().sendMinimapFlagRemoval();
				SkillUtil.stopSkillable(player);
				if (npc.fetchDefinition().getCombatLevel() <= 0) {
					return;
				}
				player.sendMessage("This NPC doesn't drop any items.");
				return;
			}
			if (npc.getId() != 3127 && npc.getId() != 7307) { // Skip those NPC's for drop interface
				if (NpcInformation.display(player, npc.getId(), npc.fetchDefinition().getCombatLevel())) {
					return;
				}
			}
		}
		final Executable action = () -> {
			player.subscribe(event -> {

				if (event == PlayerEvents.MOVED || event == PlayerEvents.LOGGED_OUT) {
					npc.resetEntityInteraction();
					return true;
				}

				final Entity playerInteracting = player.getInteractingEntity();
				final Entity npcInteracting = npc.getInteractingEntity();

				if (playerInteracting instanceof NPC && playerInteracting != npc) {
					npc.resetEntityInteraction();
					return true;
				}

				if (npcInteracting instanceof Player && npcInteracting != player) {
					npc.resetEntityInteraction();
					return true;
				}

				if (npc.getPosition().getDistance(player.getPosition()) > 1) {
					npc.debug("player too far away, resetting...");
					npc.resetEntityInteraction();
					return true;
				}
				return false;
			});
			npc.setEntityInteraction(player);

			if(PacketInteractionManager.handleNpcInteraction(player, npc, 3)) {
				return;
			}
			if (NPCActions.INSTANCE.handleClick(player, npc, Type.THIRD_OPTION))
				return;
			if (player.getClueScrollManager().handleNPCAction(3, npc))
				return;

			if (PetHandler.morph(player, npc)) {
				return;
			}
			switch (npc.getId()) {
				case GRACE:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
							.setText("I just have a few questions about", "the <col=800080>Marks of grace</col> related.")
						.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
						.setText("Let me know what you're looking for?").add(DialogueType.OPTION).setOptionTitle("Select an Option")
						.firstOption("I would like to know about gear upgrade.", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("I would like to know about gear upgrade.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Well it's fairly simple and easy " + player.getUsername() +".")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("You can upgrade your Graceful gear set into the upgraded", "variant of it which provides bonus Agility experience", "and a unique attractive color.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("The bonus experience is only provided when wearing the outfit.", "In addition, it also reduces your weight and chance", "to fall from an obstacle.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("The basic set provides 30 % bonus XP and goes up to", "70 % bonus experience on the best set.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Do not forget that Agility hood, Spottier cape, Boots of lightness,", "and Penance gloves also provide 5 % bonus Agility XP.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("The skill cape also provide you with 20 % bonus experience.", "All of the experience can stack together.")
								.add(DialogueType.PLAYER_STATEMENT)
								.setText("That sounds interesting.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Have a good day.")
								.start(player);


					}).secondOption("I would like to upgrade my Graceful gear.", player1 -> {
						new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
								.setText("I would like to upgrade my Graceful gear.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Sure! The upgraded set offers increased experience", "and benefits.")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
								.setText("Let me know which upgrade are you looking for.")
								.add(DialogueType.OPTION).setOptionTitle("Select an Option")
								.firstOption("<col=8A5099>Arceuus Graceful Outfit</col>", player2 -> {

									new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
											.setText("I would like have the <col=8A5099>Arceuus Graceful Outfit</col>", "upgrade.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
											.setText("Great start! To proceed with the upgrade,", "you will need <col=800080>350 Marks of grace</col>.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Along with the regular Graceful outfit in your inventory.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
											.add(DialogueType.OPTION).setOptionTitle("Select an Option")
											.firstOption("Proceed with the upgrade.", player3 -> {


												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("Proceed with the upgrade.").setPostAction ($ -> {
															if (!player.getInventory().containsAny(11850, 11852, 11854, 11856, 11858, 11860)) { // Doesn't have any of the pieces to upgrade
																new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																		.setText("You don't have any Graceful outfit to upgrade.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("Come back once you have the Graceful Outfit", "and <col=800080>350 Marks of grace</col> ready.").start(player);
															} else if (!player.getInventory().containsAll(11850, 11852, 11854, 11856, 11858, 11860) && player.getInventory().containsAny(11850, 11852, 11854, 11856, 11858, 11860)) { // Has some pieces to upgrade but not all
																new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13072, 200)
																		.setText("You hand over the Graceful Outfit.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("You are missing some of the required pieces " + player.getUsername() +".")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("You must have all of the Graceful Outfit set", "to be able to proceed with this.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("Come back once you have all of them ready with you.").start(player);
															} else if (player.getInventory().containsAll(11850, 11852, 11854, 11856, 11858, 11860) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 350) { // Has all the pieces to upgrade but not enough points
																new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(11854, 200)
																		.setText("You hand over the Graceful Outfit.")
																		.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																		.setText("I'm sorry, you need to have 350 <col=800080>Marks of grace</col>", "for the <col=8A5099>Arceuus Graceful Outfit</col> upgrade.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("Pass over once you have them all ready.").start(player);
															} else if (player.getInventory().containsAll(11850, 11852, 11854, 11856, 11858, 11860) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 350) { // Has all the pieces to upgrade but not enough points
																new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(11854, 200)
																		.setText("You hand over the Graceful Outfit.")
																		.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																		.setText("I see you have become better in Agility skill already.")
																		.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																		.setText("You are truly eligible for this upgrade.")
																		.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13585, 200)
																		.setText("Grace hands you over the new <col=8A5099>Arceuus Graceful Outfit</col>.")
																		.setAction(player4 -> {
																			player.getInventory().delete(new Item(11850, 1));
																			player.getInventory().delete(new Item(11852, 1));
																			player.getInventory().delete(new Item(11854, 1));
																			player.getInventory().delete(new Item(11856, 1));
																			player.getInventory().delete(new Item(11858, 1));
																			player.getInventory().delete(new Item(11860, 1));
																			player.getInventory().delete(ItemID.MARK_OF_GRACE, 350);
																			player.getInventory().add(new Item(13579, 1));
																			player.getInventory().add(new Item(13581, 1));
																			player.getInventory().add(new Item(13583, 1));
																			player.getInventory().add(new Item(13585, 1));
																			player.getInventory().add(new Item(13587, 1));
																			player.getInventory().add(new Item(13589, 1));
																			new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																					.setText("Enjoy your new <col=8A5099>Arceuus Graceful Outfit</col>!").start(player);
																		}).start(player);
															}


														}).start(player);

											}).secondOption("I'am not planning on upgrading.", player3 -> {
												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("I'am not planning on upgrading.")
														.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
														.setText("As you wish...").start(player);
											}).start(player);
								}).secondOption("<col=77CBCF>Port Piscarilius Graceful Outfit</col>", player2 -> {

									new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
											.setText("I would like have the <col=77CBCF>Port Piscarilius Graceful Outfit</col>", "upgrade.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
											.setText("Brilliant choice! To proceed with the upgrade,", "you will need <col=800080>500 Marks of grace</col>.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Along with the <col=8A5099>Arceuus Graceful Outfit</col> in your inventory.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
											.add(DialogueType.OPTION).setOptionTitle("Select an Option")
											.firstOption("Proceed with the upgrade.", player3 -> {


												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("Proceed with the upgrade.").setPostAction ($ -> {
															if (!player.getInventory().containsAny(13579, 13581, 13583, 13585, 13587, 13589)) { // Doesn't have any of the pieces to upgrade
																new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																		.setText("You don't have any of the", "<col=8A5099>Arceuus Graceful Outfit</col> to upgrade.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("Come back once you have the <col=8A5099>Arceuus Graceful Outfit</col>", "and <col=800080>500 Marks of grace</col> ready.").start(player);
															} else if (!player.getInventory().containsAll(13579, 13581, 13583, 13585, 13587, 13589) && player.getInventory().containsAny(13579, 13581, 13583, 13585, 13587, 13589)) { // Has some pieces to upgrade but not all
																new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13583, 200)
																		.setText("You hand over the <col=8A5099>Arceuus Graceful Outfit</col>.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("You are missing some of the required pieces " + player.getUsername() +".")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("You must have all of the <col=8A5099>Arceuus Graceful Outfit</col>", "set to be able to proceed with this.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("Come back once you have all of them ready with you.").start(player);
															} else if (player.getInventory().containsAll(13579, 13581, 13583, 13585, 13587, 13589) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 500) { // Has all the pieces to upgrade but not enough points
																new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13583, 200)
																		.setText("You hand over the <col=8A5099>Arceuus Graceful Outfit</col>.")
																		.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																		.setText("I'm sorry, you need to have 500 <col=800080>Marks of grace</col>", "for the <col=77CBCF>Port Piscarilius Graceful Outfit</col> upgrade.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("Pass over once you have them all ready.").start(player);
															} else if (player.getInventory().containsAll(13579, 13581, 13583, 13585, 13587, 13589) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 500) { // Has all the pieces to upgrade but not enough points
																new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13583, 200)
																		.setText("You hand over the <col=8A5099>Arceuus Graceful Outfit</col>.")
																		.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																		.setText("I see you have become even better in Agility skill already.")
																		.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																		.setText("You are surely eligible for this upgrade.")
																		.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13595, 200)
																		.setText("Grace hands you over the new <col=77CBCF>Port Piscarilius Graceful Outfit</col>.")
																		.setAction(player4 -> {
																			player.getInventory().delete(new Item(13579, 1));
																			player.getInventory().delete(new Item(13581, 1));
																			player.getInventory().delete(new Item(13583, 1));
																			player.getInventory().delete(new Item(13585, 1));
																			player.getInventory().delete(new Item(13587, 1));
																			player.getInventory().delete(new Item(13589, 1));
																			player.getInventory().delete(ItemID.MARK_OF_GRACE, 500);
																			player.getInventory().add(new Item(13591, 1));
																			player.getInventory().add(new Item(13593, 1));
																			player.getInventory().add(new Item(13595, 1));
																			player.getInventory().add(new Item(13597, 1));
																			player.getInventory().add(new Item(13599, 1));
																			player.getInventory().add(new Item(13601, 1));
																			new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																					.setText("Enjoy your new <col=77CBCF>Port Piscarilius Graceful Outfit</col>!").start(player);
																		}).start(player);
															}


														}).start(player);

											}).secondOption("I'am not planning on upgrading.", player3 -> {
												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("I'am not planning on upgrading.")
														.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
														.setText("As you wish...").start(player);
											}).start(player);
								}).thirdOption("<col=EACF21>Lovakengj Graceful Outfit</col>", player2 -> {

									new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
											.setText("I would like have the <col=EACF21>Lovakengj Graceful Outfit</col>", "upgrade.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
											.setText("Brilliant choice! To proceed with the upgrade,", "you will need <col=800080>750 Marks of grace</col>.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Along with the <col=77CBCF>Port Piscarilius Graceful Outfit</col> in your inventory.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
											.add(DialogueType.OPTION).setOptionTitle("Select an Option")
											.firstOption("Proceed with the upgrade.", player3 -> {


												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("Proceed with the upgrade.").setPostAction ($ -> {
															if (!player.getInventory().containsAny(13591, 13593, 13595, 13597, 13599, 13601)) { // Doesn't have any of the pieces to upgrade
																new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																		.setText("You don't have any of the", "<col=77CBCF>Port Piscarilius Graceful Outfit</col> to upgrade.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("Come back once you have the <col=77CBCF>Port Piscarilius Graceful Outfit</col>", "and <col=800080>750 Marks of grace</col> ready.").start(player);
															} else if (!player.getInventory().containsAll(13591, 13593, 13595, 13597, 13599, 13601) && player.getInventory().containsAny(13591, 13593, 13595, 13597, 13599, 13601)) { // Has some pieces to upgrade but not all
																new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13595, 200)
																		.setText("You hand over the <col=77CBCF>Port Piscarilius Graceful Outfit</col>.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("You are missing some of the required pieces " + player.getUsername() +".")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("You must have all of the <col=77CBCF>Port Piscarilius Graceful Outfit</col>", "set to be able to proceed with this.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("Come back once you have all of them ready with you.").start(player);
															} else if (player.getInventory().containsAll(13591, 13593, 13595, 13597, 13599, 13601) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 750) { // Has all the pieces to upgrade but not enough points
																new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13595, 200)
																		.setText("You hand over the <col=77CBCF>Port Piscarilius Graceful Outfit</col>.")
																		.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																		.setText("I'm sorry, you need to have 750 <col=800080>Marks of grace</col>", "for the <col=EACF21>Lovakengj Graceful Outfit</col> upgrade.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("Pass over once you have them all ready.").start(player);
															} else if (player.getInventory().containsAll(13591, 13593, 13595, 13597, 13599, 13601) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 750) { // Has all the pieces to upgrade but not enough points
																new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13595, 200)
																		.setText("You hand over the <col=77CBCF>Port Piscarilius Graceful Outfit</col>.")
																		.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																		.setText("I see you have become even better in Agility skill already.")
																		.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																		.setText("You are surely eligible for this upgrade.")
																		.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13607, 200)
																		.setText("Grace hands you over the new <col=EACF21>Lovakengj Graceful Outfit</col>.")
																		.setAction(player4 -> {
																			player.getInventory().delete(new Item(13591, 1));
																			player.getInventory().delete(new Item(13593, 1));
																			player.getInventory().delete(new Item(13595, 1));
																			player.getInventory().delete(new Item(13597, 1));
																			player.getInventory().delete(new Item(13599, 1));
																			player.getInventory().delete(new Item(13601, 1));
																			player.getInventory().delete(ItemID.MARK_OF_GRACE, 750);
																			player.getInventory().add(new Item(13603, 1));
																			player.getInventory().add(new Item(13605, 1));
																			player.getInventory().add(new Item(13607, 1));
																			player.getInventory().add(new Item(13609, 1));
																			player.getInventory().add(new Item(13611, 1));
																			player.getInventory().add(new Item(13613, 1));
																			new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																					.setText("Enjoy your new <col=EACF21>Lovakengj Graceful Outfit</col>!").start(player);
																		}).start(player);
															}


														}).start(player);

											}).secondOption("I'am not planning on upgrading.", player3 -> {
												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("I'am not planning on upgrading.")
														.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
														.setText("As you wish...").start(player);
											}).start(player);
								}).fourthOption("<col=81230D>Shayzien Graceful Outfit</col>", player2 -> {

									new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
											.setText("I would like have the <col=81230D>Shayzien Graceful Outfit</col>", "upgrade.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
											.setText("Brilliant choice! To proceed with the upgrade,", "you will need <col=800080>1000 Marks of grace</col>.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Along with the <col=EACF21>Lovakengj Graceful Outfit</col> in your inventory.")
											.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
											.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
											.add(DialogueType.OPTION).setOptionTitle("Select an Option")
											.firstOption("Proceed with the upgrade.", player3 -> {


												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("Proceed with the upgrade.").setPostAction ($ -> {
															if (!player.getInventory().containsAny(13603, 13605, 13607, 13609, 13611, 13613)) { // Doesn't have any of the pieces to upgrade
																new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																		.setText("You don't have any of the", "<col=EACF21>Lovakengj Graceful Outfit</col> to upgrade.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("Come back once you have the <col=EACF21>Lovakengj Graceful Outfit</col>", "and <col=800080>1000 Marks of grace</col> ready.").start(player);
															} else if (!player.getInventory().containsAll(13603, 13605, 13607, 13609, 13611, 13613) && player.getInventory().containsAny(13603, 13605, 13607, 13609, 13611, 13613)) { // Has some pieces to upgrade but not all
																new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13607, 200)
																		.setText("You hand over the <col=EACF21>Lovakengj Graceful Outfit</col>.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("You are missing some of the required pieces " + player.getUsername() +".")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("You must have all of the <col=EACF21>Lovakengj Graceful Outfit</col>", "set to be able to proceed with this.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("Come back once you have all of them ready with you.").start(player);
															} else if (player.getInventory().containsAll(13603, 13605, 13607, 13609, 13611, 13613) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 1000) { // Has all the pieces to upgrade but not enough points
																new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13607, 200)
																		.setText("You hand over the <col=EACF21>Lovakengj Graceful Outfit</col>.")
																		.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																		.setText("I'm sorry, you need to have 1000 <col=800080>Marks of grace</col>", "for the <col=81230D>Shayzien Graceful Outfit</col> upgrade.")
																		.add(DialogueType.NPC_STATEMENT)
																		.setText("Pass over once you have them all ready.").start(player);
															} else if (player.getInventory().containsAll(13603, 13605, 13607, 13609, 13611, 13613) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 1000) { // Has all the pieces to upgrade but not enough points
																new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13607, 200)
																		.setText("You hand over the <col=EACF21>Lovakengj Graceful Outfit</col>.")
																		.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																		.setText("I see you have become even better in Agility skill already.")
																		.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																		.setText("You are surely eligible for this upgrade.")
																		.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13619, 200)
																		.setText("Grace hands you over the new <col=81230D>Shayzien Graceful Outfit</col>.")
																		.setAction(player4 -> {
																			player.getInventory().delete(new Item(13603, 1));
																			player.getInventory().delete(new Item(13605, 1));
																			player.getInventory().delete(new Item(13607, 1));
																			player.getInventory().delete(new Item(13609, 1));
																			player.getInventory().delete(new Item(13611, 1));
																			player.getInventory().delete(new Item(13613, 1));
																			player.getInventory().delete(ItemID.MARK_OF_GRACE, 1000);
																			player.getInventory().add(new Item(13615, 1));
																			player.getInventory().add(new Item(13617, 1));
																			player.getInventory().add(new Item(13619, 1));
																			player.getInventory().add(new Item(13621, 1));
																			player.getInventory().add(new Item(13623, 1));
																			player.getInventory().add(new Item(13625, 1));
																			new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																					.setText("Enjoy your new <col=81230D>Shayzien Graceful Outfit</col>!").start(player);
																		}).start(player);
															}


														}).start(player);

											}).secondOption("I'am not planning on upgrading.", player3 -> {
												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("I'am not planning on upgrading.")
														.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
														.setText("As you wish...").start(player);
											}).start(player);
								}).fifthOption("Next page...", player4 -> {
									new DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
											.firstOption("<col=1DC33E>Hosidius Graceful Outfit</col>", player2 -> {

												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("I would like have the <col=1DC33E>Hosidius Graceful Outfit</col>", "upgrade.")
														.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
														.setText("Brilliant choice! To proceed with the upgrade,", "you will need <col=800080>1250 Marks of grace</col>.")
														.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
														.setText("Along with the <col=81230D>Shayzien Graceful Outfit</col> in your inventory.")
														.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
														.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
														.add(DialogueType.OPTION).setOptionTitle("Select an Option")
														.firstOption("Proceed with the upgrade.", player3 -> {


															new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																	.setText("Proceed with the upgrade.").setPostAction ($ -> {
																		if (!player.getInventory().containsAny(13615, 13617, 13619, 13621, 13623, 13625)) { // Doesn't have any of the pieces to upgrade
																			new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																					.setText("You don't have any of the", "<col=81230D>Shayzien Graceful Outfit</col> to upgrade.")
																					.add(DialogueType.NPC_STATEMENT)
																					.setText("Come back once you have the <col=81230D>Shayzien Graceful Outfit</col>", "and <col=800080>1250 Marks of grace</col> ready.").start(player);
																		} else if (!player.getInventory().containsAll(13615, 13617, 13619, 13621, 13623, 13625) && player.getInventory().containsAny(13615, 13617, 13619, 13621, 13623, 13625)) { // Has some pieces to upgrade but not all
																			new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13619, 200)
																					.setText("You hand over the <col=81230D>Shayzien Graceful Outfit</col>.")
																					.add(DialogueType.NPC_STATEMENT)
																					.setText("You are missing some of the required pieces " + player.getUsername() +".")
																					.add(DialogueType.NPC_STATEMENT)
																					.setText("You must have all of the <col=81230D>Shayzien Graceful Outfit</col>", "set to be able to proceed with this.")
																					.add(DialogueType.NPC_STATEMENT)
																					.setText("Come back once you have all of them ready with you.").start(player);
																		} else if (player.getInventory().containsAll(13615, 13617, 13619, 13621, 13623, 13625) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 1250) { // Has all the pieces to upgrade but not enough points
																			new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13619, 200)
																					.setText("You hand over the <col=81230D>Shayzien Graceful Outfit</col>.")
																					.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																					.setText("I'm sorry, you need to have 1250 <col=800080>Marks of grace</col>", "for the <col=81230D>Shayzien Graceful Outfit</col> upgrade.")
																					.add(DialogueType.NPC_STATEMENT)
																					.setText("Pass over once you have them all ready.").start(player);
																		} else if (player.getInventory().containsAll(13615, 13617, 13619, 13621, 13623, 13625) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 1250) { // Has all the pieces to upgrade but not enough points
																			new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13619, 200)
																					.setText("You hand over the <col=81230D>Shayzien Graceful Outfit</col>.")
																					.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																					.setText("I see you have become even better in Agility skill already.")
																					.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																					.setText("You are surely eligible for this upgrade.")
																					.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13631, 200)
																					.setText("Grace hands you over the new <col=1DC33E>Hosidius Graceful Outfit</col>.")
																					.setAction(player5 -> {
																						player.getInventory().delete(new Item(13615, 1));
																						player.getInventory().delete(new Item(13617, 1));
																						player.getInventory().delete(new Item(13619, 1));
																						player.getInventory().delete(new Item(13621, 1));
																						player.getInventory().delete(new Item(13623, 1));
																						player.getInventory().delete(new Item(13625, 1));
																						player.getInventory().delete(ItemID.MARK_OF_GRACE, 1250);
																						player.getInventory().add(new Item(13627, 1));
																						player.getInventory().add(new Item(13629, 1));
																						player.getInventory().add(new Item(13631, 1));
																						player.getInventory().add(new Item(13633, 1));
																						player.getInventory().add(new Item(13635, 1));
																						player.getInventory().add(new Item(13637, 1));
																						new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																								.setText("Enjoy your new <col=1DC33E>Hosidius Graceful Outfit</col>!").start(player);
																					}).start(player);
																		}


																	}).start(player);

														}).secondOption("I'am not planning on upgrading.", player3 -> {
															new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																	.setText("I'am not planning on upgrading.")
																	.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
																	.setText("As you wish...").start(player);
														}).start(player);
											}).secondOption("<col=D2DCD4>Great Kourend Graceful Outfit</col>", player2 -> {

												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("I would like have the <col=D2DCD4>Great Kourend Graceful Outfit</col>", "upgrade.")
														.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
														.setText("Brilliant choice! To proceed with the upgrade,", "you will need <col=800080>1500 Marks of grace</col>.")
														.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
														.setText("Along with the <col=1DC33E>Hosidius Graceful Outfit</col> in your inventory.")
														.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
														.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
														.add(DialogueType.OPTION).setOptionTitle("Select an Option")
														.firstOption("Proceed with the upgrade.", player3 -> {


															new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																	.setText("Proceed with the upgrade.").setPostAction ($ -> {
																		if (!player.getInventory().containsAny(13627, 13629, 13631, 13633, 13635, 13637)) { // Doesn't have any of the pieces to upgrade
																			new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																					.setText("You don't have any of the", "<col=1DC33E>Hosidius Graceful Outfit</col> to upgrade.")
																					.add(DialogueType.NPC_STATEMENT)
																					.setText("Come back once you have the <col=1DC33E>Hosidius Graceful Outfit</col>", "and <col=800080>1250 Marks of grace</col> ready.").start(player);
																		} else if (!player.getInventory().containsAll(13627, 13629, 13631, 13633, 13635, 13637) && player.getInventory().containsAny(13627, 13629, 13631, 13633, 13635, 13637)) { // Has some pieces to upgrade but not all
																			new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13631, 200)
																					.setText("You hand over the <col=1DC33E>Hosidius Graceful Outfit</col>.")
																					.add(DialogueType.NPC_STATEMENT)
																					.setText("You are missing some of the required pieces " + player.getUsername() +".")
																					.add(DialogueType.NPC_STATEMENT)
																					.setText("You must have all of the <col=1DC33E>Hosidius Graceful Outfit</col>", "set to be able to proceed with this.")
																					.add(DialogueType.NPC_STATEMENT)
																					.setText("Come back once you have all of them ready with you.").start(player);
																		} else if (player.getInventory().containsAll(13627, 13629, 13631, 13633, 13635, 13637) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 1500) { // Has all the pieces to upgrade but not enough points
																			new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13631, 200)
																					.setText("You hand over the <col=1DC33E>Hosidius Graceful Outfit</col>.")
																					.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																					.setText("I'm sorry, you need to have 1500 <col=800080>Marks of grace</col>", "for the <col=1DC33E>Hosidius Graceful Outfit</col> upgrade.")
																					.add(DialogueType.NPC_STATEMENT)
																					.setText("Pass over once you have them all ready.").start(player);
																		} else if (player.getInventory().containsAll(13627, 13629, 13631, 13633, 13635, 13637) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 1500) { // Has all the pieces to upgrade but not enough points
																			new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13631, 200)
																					.setText("You hand over the <col=1DC33E>Hosidius Graceful Outfit</col>.")
																					.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																					.setText("I see you have become even better in Agility skill already.")
																					.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																					.setText("You are surely eligible for this upgrade.")
																					.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13671, 200)
																					.setText("Grace hands you over the new <col=D2DCD4>Great Kourend Graceful Outfit</col>.")
																					.setAction(player5 -> {
																						player.getInventory().delete(new Item(13627, 1));
																						player.getInventory().delete(new Item(13629, 1));
																						player.getInventory().delete(new Item(13631, 1));
																						player.getInventory().delete(new Item(13633, 1));
																						player.getInventory().delete(new Item(13635, 1));
																						player.getInventory().delete(new Item(13637, 1));
																						player.getInventory().delete(ItemID.MARK_OF_GRACE, 1500);
																						player.getInventory().add(new Item(13667, 1));
																						player.getInventory().add(new Item(13669, 1));
																						player.getInventory().add(new Item(13671, 1));
																						player.getInventory().add(new Item(13673, 1));
																						player.getInventory().add(new Item(13675, 1));
																						player.getInventory().add(new Item(13677, 1));
																						new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																								.setText("Enjoy your new <col=D2DCD4>Great Kourend Graceful Outfit</col>!").start(player);
																					}).start(player);
																		}


																	}).start(player);

														}).secondOption("I'am not planning on upgrading.", player3 -> {
															new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																	.setText("I'am not planning on upgrading.")
																	.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
																	.setText("As you wish...").start(player);
														}).start(player);
											}).thirdOption("<col=163583>Brimhaven Graceful Outfit</col>", player2 -> {

												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("I would like have the <col=163583>Brimhaven Graceful Outfit</col>", "upgrade.")
														.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.HAPPY)
														.setText("Brilliant choice! To proceed with the upgrade,", "you will need <col=800080>1750 Marks of grace</col>.")
														.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
														.setText("Along with the <col=D2DCD4>Great Kourend Graceful Outfit</col> in your inventory.")
														.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId())
														.setText("Are you sure you want to continue with that?").setExpression(DialogueExpression.CALM)
														.add(DialogueType.OPTION).setOptionTitle("Select an Option")
														.firstOption("Proceed with the upgrade.", player3 -> {


															new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																	.setText("Proceed with the upgrade.").setPostAction ($ -> {
																		if (!player.getInventory().containsAny(13667, 13669, 13671, 13673, 13675, 13677)) { // Doesn't have any of the pieces to upgrade
																			new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.ANNOYED)
																					.setText("You don't have any of the", "<col=D2DCD4>Great Kourend Graceful Outfit</col> to upgrade.")
																					.add(DialogueType.NPC_STATEMENT)
																					.setText("Come back once you have the <col=D2DCD4>Great Kourend Graceful Outfit</col>", "and <col=800080>1250 Marks of grace</col> ready.").start(player);
																		} else if (!player.getInventory().containsAll(13667, 13669, 13671, 13673, 13675, 13677) && player.getInventory().containsAny(13667, 13669, 13671, 13673, 13675, 13677)) { // Has some pieces to upgrade but not all
																			new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13671, 200)
																					.setText("You hand over the <col=D2DCD4>Great Kourend Graceful Outfit</col>.")
																					.add(DialogueType.NPC_STATEMENT)
																					.setText("You are missing some of the required pieces " + player.getUsername() +".")
																					.add(DialogueType.NPC_STATEMENT)
																					.setText("You must have all of the <col=D2DCD4>Great Kourend Graceful Outfit</col>", "set to be able to proceed with this.")
																					.add(DialogueType.NPC_STATEMENT)
																					.setText("Come back once you have all of them ready with you.").start(player);
																		} else if (player.getInventory().containsAll(13667, 13669, 13671, 13673, 13675, 13677) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) < 1750) { // Has all the pieces to upgrade but not enough points
																			new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13671, 200)
																					.setText("You hand over the <col=D2DCD4>Great Kourend Graceful Outfit</col>.")
																					.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SAD)
																					.setText("I'm sorry, you need to have 1750 <col=800080>Marks of grace</col>", "for the <col=D2DCD4>Great Kourend Graceful Outfit</col> upgrade.")
																					.add(DialogueType.NPC_STATEMENT)
																					.setText("Pass over once you have them all ready.").start(player);
																		} else if (player.getInventory().containsAll(13667, 13669, 13671, 13673, 13675, 13677) && player.getInventory().getAmount(ItemID.MARK_OF_GRACE) >= 1750) { // Has all the pieces to upgrade but not enough points
																			new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13671, 200)
																					.setText("You hand over the <col=D2DCD4>Great Kourend Graceful Outfit</col>.")
																					.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																					.setText("I see you have become even better in Agility skill already.")
																					.add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.SURPRISED)
																					.setText("You are surely eligible for this upgrade.")
																					.add(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(21067, 200)
																					.setText("Grace hands you over the new <col=163583>Brimhaven Graceful Outfit</col>.")
																					.setAction(player5 -> {
																						player.getInventory().delete(new Item(13667, 1));
																						player.getInventory().delete(new Item(13669, 1));
																						player.getInventory().delete(new Item(13671, 1));
																						player.getInventory().delete(new Item(13673, 1));
																						player.getInventory().delete(new Item(13675, 1));
																						player.getInventory().delete(new Item(13677, 1));
																						player.getInventory().delete(ItemID.MARK_OF_GRACE, 1750);
																						player.getInventory().add(new Item(21061, 1));
																						player.getInventory().add(new Item(21064, 1));
																						player.getInventory().add(new Item(21067, 1));
																						player.getInventory().add(new Item(21070, 1));
																						player.getInventory().add(new Item(21073, 1));
																						player.getInventory().add(new Item(21076, 1));
																						new DialogueBuilder(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
																								.setText("Enjoy your new <col=163583>Brimhaven Graceful Outfit</col>!").start(player);
																					}).start(player);
																		}


																	}).start(player);

														}).secondOption("I'am not planning on upgrading.", player3 -> {
															new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
																	.setText("I'am not planning on upgrading.")
																	.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.ANNOYED)
																	.setText("As you wish...").start(player);
														}).start(player);
											}).fourthOption("<col=46474A>Hallowed Graceful Outfit</col>", player2 -> {
												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("I would like have the <col=46474A>Hallowed Graceful Outfit</col>", "upgrade.")
														.add(DialogueType.NPC_STATEMENT)
														.setText("I am still working on this outfit and it's not done yet.")
														.add(DialogueType.NPC_STATEMENT)
														.setText("Check again later.")
														.start(player);
											}).fifthOption("<col=7E540B>Trailblazer Graceful Outfit</col>", player2 -> {
												new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
														.setText("I would like have the <col=7E50B>Trailblazer Graceful Outfit</col>", "upgrade.")
														.add(DialogueType.NPC_STATEMENT)
														.setText("I am still working on this outfit and it's not done yet.")
														.add(DialogueType.NPC_STATEMENT)
														.setText("Check again later.")
														.start(player);
											}).start(player);
								}).start(player);


					}).start(player);
					break;
				case SAWMILL_OPERATOR:
					ShopManager.open(player, ShopIdentifiers.CONSTRUCTION_SUPPLIES);
					break;
				case CAPN_IZZY_NOBEARD:
					final int entranceFee = 10_000_000;
					if (EntityExtKt.getBoolean(player, Attribute.PAID_BRIMHAVEN_AGILITY_FEE, false)) {
						new DialogueBuilder(DialogueType.NPC_STATEMENT)
								.setNpcChatHead(NpcID.CAPN_IZZY_NOBEARD).setExpression(DialogueExpression.HAPPY)
								.setText("Avast there, you've already paid!")
								.add(DialogueType.NPC_STATEMENT).setNpcChatHead(PARROT_3853)
								.setText("Shiver me timbers, what a ninny!")
								.start(player);
					} else {
						if (!player.getInventory().contains(new Item(ItemID.COINS, entranceFee))) {
							player.sendMessage("You need to have at least 10,000,000 coins to pay the entrance fee.");
							return;
						}
						new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(ItemID.COINS, 200)
								.setText("You give Cap'n Izzy the 10,000,000 coin entrance fee.")
								.start(player);
						player.getInventory().delete(ItemID.COINS, entranceFee);
						EntityExtKt.setBoolean(player, Attribute.PAID_BRIMHAVEN_AGILITY_FEE, true, false);
					}
					break;
				case GAMER:
					ShopManager.open(player, ShopIdentifiers.GAMBLING_STORE);
					break;
				case ACCOUNT_GUIDE:
					PlayerTitles.display(player);
					break;
				case ASHILD:
					MonsterHunting.INSTANCE.assignContract(player);
					break;
				case SECURITY_GUARD:
					if (player.isAccountFlagged()) {
						return;
					}
					player.getPacketSender().sendInterface(51000);
					break;
				case HUNTING_EXPERT_1504:

					new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.HAPPY)
							.setText("I would like to teleport to Hunter skill locations.")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.LAUGHING)
							.setText("Absolutely!", "Please pick the location of your choice!")
							.add(DialogueType.OPTION).setOptionTitle("Select an Option")
							.firstOption("Feldip Hunter.", player1 -> {

								if (TeleportHandler.checkReqs(player, new Position(2557, 2907, 0), true, true, player.getSpellbook().getTeleportType())) {
									TeleportHandler.offerTeleportFromNPC(player, new Position(2557, 2907, 0).randomize(3),
									player.getSpellbook().getTeleportType(), false, true, npc.getId(), "Feldip Hunter");
								}

							}).secondOption("Uzer Hunter.", player1 -> {
								if (TeleportHandler.checkReqs(player, new Position(3408, 3100, 0), true, true, player.getSpellbook().getTeleportType())) {
									TeleportHandler.offerTeleportFromNPC(player, Teleporting.TeleportLocation.UZER_HUNTER_AREA.getPosition().randomize(3),
											player.getSpellbook().getTeleportType(), false, true, npc.getId(), "Uzer Hunter");
								}
							}).thirdOption("Piscatoris Hunter.", player1 -> {
								if (TeleportHandler.checkReqs(player, new Position(2319, 3616, 0), true, true, player.getSpellbook().getTeleportType())) {
									TeleportHandler.offerTeleportFromNPC(player, Teleporting.TeleportLocation.PISCATORIS_HUNTER.getPosition().randomize(3),
											player.getSpellbook().getTeleportType(), false, true, npc.getId(), "Piscatoris Hunter");
								}
							}).fourthOption("Falconry Hunter.", player1 -> {
								if (TeleportHandler.checkReqs(player, new Position(2376, 3596, 0), true, true, player.getSpellbook().getTeleportType())) {
									TeleportHandler.offerTeleportFromNPC(player, Teleporting.TeleportLocation.FALCONRY.getPosition().randomize(5),
											player.getSpellbook().getTeleportType(), false, true, npc.getId(), "Falconry Hunter");
								}
							}).fifthOption("Next...", player1 -> {

							}).fifthOption("Next...", player1 -> {
								new DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
										.firstOption("Relleka Hunter.", player2 -> {
											if (TeleportHandler.checkReqs(player, new Position(2722, 3782, 0), true, true, player.getSpellbook().getTeleportType())) {
												TeleportHandler.offerTeleportFromNPC(player, Teleporting.TeleportLocation.RELLEKKA_HUNTER.getPosition().randomize(3),
														player.getSpellbook().getTeleportType(), false, true, npc.getId(), "Relleka zone");
											}
										}).secondOption("Chinchompa Hill (Wild Level @red@33</col>).", player3 -> {
											if (TeleportHandler.checkReqs(player, new Position(3138, 3783, 0), true, true, player.getSpellbook().getTeleportType())) {
												TeleportHandler.offerTeleportFromNPC(player, Teleporting.TeleportLocation.CHINCHOMPA_HILL.getPosition(),
														player.getSpellbook().getTeleportType(), false, true, npc.getId(), "Chinchompa Hill (Wild Level @red@33</col>)");
											}
										}).thirdOption("Black Chins (Wild Level @red@32</col>).", player3 -> {
											if (TeleportHandler.checkReqs(player, new Position(3138, 3783, 0), true, true, player.getSpellbook().getTeleportType())) {
												TeleportHandler.offerTeleportFromNPC(player, Teleporting.TeleportLocation.BLACK_CHINCHOMPA.getPosition(),
														player.getSpellbook().getTeleportType(), false, true, npc.getId(), "Black Chins (Wild Level @red@32</col>)");
											}
										}).fourthOption("Puro Puro", player3 -> {
											if (TeleportHandler.checkReqs(player, new Position(2594, 4319, 0), true, true, player.getSpellbook().getTeleportType())) {
												TeleportHandler.offerTeleportFromNPC(player, Teleporting.TeleportLocation.PURO_PURO.getPosition(),
														player.getSpellbook().getTeleportType(), false, true, npc.getId(), "Puro Puro");
											}
										}).addCancel().start(player);

							}).start(player);
					break;
				case PIRATE_JACKIE_THE_FRUIT:
					if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.BARBARIAN_OUTPOST.getPosition(), true, true, player.getSpellbook().getTeleportType())) {
						//TeleportHandler.teleport(player, new Position(2552, 3557, 0),
						//		player.getSpellbook().getTeleportType(), false, true);
						TeleportHandler.offerTeleportFromNPC(player, Teleporting.TeleportLocation.BARBARIAN_OUTPOST.getPosition(),
								player.getSpellbook().getTeleportType(), false, true, npc.getId(), "Barbarian Outpost");
					}
					break;
				case MAGE_OF_ZAMORAK:
					MageOfZamorak.INSTANCE.abyssTeleport(player, npc.getId());
					break;
				case AUBURY_11435:
					if (player.getSkillTaskManager().getTask(SkillMasterType.RUNECRAFTING) != null && player.getSkillTaskManager().getTask(SkillMasterType.RUNECRAFTING).getAmount() <= 0) {
						SkillTaskManager.checkCompletion(player, SkillMasterType.RUNECRAFTING);
					} else {
						SkillTaskManager.getTask(player, SkillMasterType.RUNECRAFTING);
					}
					break;
				case HICKTON:
					if (player.getSkillTaskManager().getTask(SkillMasterType.FLETCHING) != null && player.getSkillTaskManager().getTask(SkillMasterType.FLETCHING).getAmount() <= 0) {
						SkillTaskManager.checkCompletion(player, SkillMasterType.FLETCHING);
					} else {
						SkillTaskManager.getTask(player, SkillMasterType.FLETCHING);
					}
					break;
				case MARTIN_THWAIT:
					if (player.getSkillTaskManager().getTask(SkillMasterType.THIEVING) != null && player.getSkillTaskManager().getTask(SkillMasterType.THIEVING).getAmount() <= 0) {
						SkillTaskManager.checkCompletion(player, SkillMasterType.THIEVING);
					} else {
						SkillTaskManager.getTask(player, SkillMasterType.THIEVING);
					}
					break;
				case WOODSMAN_TUTOR:
					if (player.getSkillTaskManager().getTask(SkillMasterType.WOODCUTTING) != null && player.getSkillTaskManager().getTask(SkillMasterType.WOODCUTTING).getAmount() <= 0) {
						SkillTaskManager.checkCompletion(player, SkillMasterType.WOODCUTTING);
					} else {
						SkillTaskManager.getTask(player, SkillMasterType.WOODCUTTING);
					}
					break;
				case GADRIN:
					if (player.getSkillTaskManager().getTask(SkillMasterType.MINING) != null && player.getSkillTaskManager().getTask(SkillMasterType.MINING).getAmount() <= 0) {
						SkillTaskManager.checkCompletion(player, SkillMasterType.MINING);
					} else {
						SkillTaskManager.getTask(player, SkillMasterType.MINING);
					}
					break;
				case MASTER_SMITHING_TUTOR:
					if (player.getSkillTaskManager().getTask(SkillMasterType.SMITHING) != null && player.getSkillTaskManager().getTask(SkillMasterType.SMITHING).getAmount() <= 0) {
						SkillTaskManager.checkCompletion(player, SkillMasterType.SMITHING);
					} else {
						SkillTaskManager.getTask(player, SkillMasterType.SMITHING);
					}
					break;
				case MASTER_FISHER:
					if (player.getSkillTaskManager().getTask(SkillMasterType.FISHING) != null && player.getSkillTaskManager().getTask(SkillMasterType.FISHING).getAmount() <= 0) {
						SkillTaskManager.checkCompletion(player, SkillMasterType.FISHING);
					} else {
						SkillTaskManager.getTask(player, SkillMasterType.FISHING);
					}
					break;
				case MASTER_CRAFTER:
					if (player.getSkillTaskManager().getTask(SkillMasterType.CRAFTING) != null && player.getSkillTaskManager().getTask(SkillMasterType.CRAFTING).getAmount() <= 0) {
						SkillTaskManager.checkCompletion(player, SkillMasterType.CRAFTING);
					} else {
						SkillTaskManager.getTask(player, SkillMasterType.CRAFTING);
					}
					break;
				case IGNATIUS_VULCAN:
					if (player.getSkillTaskManager().getTask(SkillMasterType.FIREMAKING) != null && player.getSkillTaskManager().getTask(SkillMasterType.FIREMAKING).getAmount() <= 0) {
						SkillTaskManager.checkCompletion(player, SkillMasterType.FIREMAKING);
					} else {
						SkillTaskManager.getTask(player, SkillMasterType.FIREMAKING);
					}
					break;
				case KAQEMEEX:
					if (player.getSkillTaskManager().getTask(SkillMasterType.HERBLORE) != null && player.getSkillTaskManager().getTask(SkillMasterType.HERBLORE).getAmount() <= 0) {
						SkillTaskManager.checkCompletion(player, SkillMasterType.HERBLORE);
					} else {
						SkillTaskManager.getTask(player, SkillMasterType.HERBLORE);
					}
					break;
				case BROTHER_JERED:
					if (player.getSkillTaskManager().getTask(SkillMasterType.PRAYER) != null && player.getSkillTaskManager().getTask(SkillMasterType.PRAYER).getAmount() <= 0) {
						SkillTaskManager.checkCompletion(player, SkillMasterType.PRAYER);
					} else {
						SkillTaskManager.getTask(player, SkillMasterType.PRAYER);
					}
					break;
				case HEAD_CHEF:
					if (player.getSkillTaskManager().getTask(SkillMasterType.COOKING) != null && player.getSkillTaskManager().getTask(SkillMasterType.COOKING).getAmount() <= 0) {
						SkillTaskManager.checkCompletion(player, SkillMasterType.COOKING);
					} else {
						SkillTaskManager.getTask(player, SkillMasterType.COOKING);
					}
					break;
				case TZHAARHURTEL:
					if (TeleportHandler.checkReqs(player, new Position(2438, 5171, 0), true, true, player.getSpellbook().getTeleportType())) {
						//TeleportHandler.teleport(player, Teleporting.TeleportLocation.FIGHT_CAVE.getPosition(),
						//		player.getSpellbook().getTeleportType(), false, true);
						TeleportHandler.offerTeleportFromNPC(player, Teleporting.TeleportLocation.FIGHT_CAVE.getPosition(),
								player.getSpellbook().getTeleportType(), false, true, npc.getId(), "Fight caves");
					}
					break;
				case QUARTERMASTER:
					Voting.INSTANCE.requestVoteLookup(player, false);
					break;
				case PARTY_PETE:
					ShopManager.open(player, ShopIdentifiers.LIMITED_SHOP);
					break;
				case BANKER:
				case BANKER_1613:
				case BANKER_1618:
				case BANKER_1479:
				case BANKER_1480:
				case BANKER_1633:
				case BANKER_1634:
				case BANKER_2117:
				case BANKER_2118:
				case BANKER_2119:
				case BANKER_2292:
				case BANKER_2293:
				case BANKER_2368:
				case BANKER_2369:
				case BANKER_2633:
				case BANKER_2897:
				case BANKER_2898:
				case BANKER_3089:
				case BANKER_3090:
				case BANKER_3091:
				case BANKER_3092:
				case BANKER_3093:
				case BANKER_3094:
				case BANKER_3318:
				case BANKER_3887:
				case BANKER_3888:
				case BANKER_4054:
				case BANKER_4055:
				case BANKER_6859:
				case BANKER_6860:
				case BANKER_6861:
				case BANKER_6862:
				case BANKER_6863:
				case BANKER_6864:
				case BANKER_6939:
				case BANKER_6940:
				case BANKER_6941:
				case BANKER_6942:
				case BANKER_6969:
				case BANKER_6970:
				case BANKER_7057:
				case BANKER_7058:
				case BANKER_7059:
				case BANKER_7060:
				case BANKER_7077:
				case BANKER_7078:
				case BANKER_7079:
				case BANKER_7080:
				case BANKER_7081:
				case BANKER_7082:
				case BANKER_8321:
				case BANKER_8322:
				case BANKER_8589:
				case BANKER_8590:
				case BANKER_8666:
				case BANKER_9127:
				case BANKER_9128:
				case BANKER_9129:
				case BANKER_9130:
				case BANKER_9131:
				case BANKER_9132:
				case BANKER_TUTOR:
				case TZHAARKETZUH:
				case JADE:
				case 7678:
				case 11289:
					player.sendMessage("You don't have any items to collect.");
					break;
				case TURAEL:
				case DURADEL:
				case MAZCHNA:
				case VANNAKA:
				case CHAELDAR:
				case KRYSTILIA:
				case NIEVE:
				case 8623:
					ShopManager.open(player, ShopIdentifiers.SLAYER_EQUIPMENTS_STORE);
					break;
				case LANTHUS:
					ShopManager.open(player, ShopIdentifiers.CASTLEWARS_TICKET_EXCHANGE);
					break;
				case BARMAN:
					ShopManager.open(player, ShopIdentifiers.OSRS_TOKENS_ONLY_STORE);
					break;
				case BOB_BARTER_HERBS:
					DialogueManager.start(player, 2838);
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player1, int option) {
							player1.getPacketSender().sendInterfaceRemoval();
							if (option >= 1 && option <= 4) {
								PotionDecanting.decantInventory(player1, option, true);
							}
						}
					});
					break;
				case LEAGUES_TUTOR:
				case LEAGUES_TUTOR_316:
				case LEAGUES_TUTOR_317:
				case EMBLEM_TRADER:
					// Sell emblems option
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player1, int option) {
							if (option == 1) {
								int cost = BountyHunterManager.getValueForEmblems(player1, true);
								player1.getPacketSender().sendMessage(
										"@red@You have received " + Misc.format(cost) + " blood money for your emblem(s).");
								DialogueManager.start(player1, 4);
							} else {
								player1.getPacketSender().sendInterfaceRemoval();
							}
						}
					});
					int value = BountyHunterManager.getValueForEmblems(player, false);
					if (value > 0) {
						player.setDialogue(DialogueManager.getDialogues().get(10)); // Yes
						// /
						// no
						// option
						DialogueManager.sendStatement(player,
								"I will give you " + value + " blood money for those emblems. Agree?");
					} else {
						DialogueManager.start(player, 5);
					}
					break;
				case EMBLEM_TRADER_7943:
					// Sell emblems option
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player1, int option) {
							if (option == 1) {
								int costForArtifacts = BountyHunterManager.getValueForArtifacts(player1, true);
								player1.getPacketSender().sendMessage(
										"@red@You have received " + Misc.format(costForArtifacts) + " blood money for your emblem(s).");
								DialogueManager.start(player1, 2806);
							} else {
								player1.getPacketSender().sendInterfaceRemoval();
							}
						}
					});
					int valueForArtifacts = BountyHunterManager.getValueForArtifacts(player, false);
					if (valueForArtifacts > 0) {
						player.setDialogue(DialogueManager.getDialogues().get(10)); // Yes
						// /
						// no
						// option
						DialogueManager.sendStatement(player,
								"I will give you " + Misc.format(valueForArtifacts) + " blood money for those emblems. Agree?");
					} else {
						DialogueManager.start(player, 2807);
					}
					break;
				case MAGIC_INSTRUCTOR:
					ShopManager.open(player, ShopIdentifiers.MAGE_RUNES_STORE);
					break;
			}
		};

		player.setWalkToTask(new WalkToAction(player, npc, action, WalkToAction.Policy.NO_RESET_ENTITY_INTERACTION_ON_EXECUTION, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
	}

	public void handleFourthClick(Player player, NPC npc) {

		if (player.getRights() == PlayerRights.DEVELOPER) {
			player.getPacketSender().sendMessage(
					"Fourth click NPC: " + Integer.toString(npc.getId()) + ". " + npc.getPosition().toString(), 1000);
		}

		DebugManager.debug(player, "npc-option", "4: npc: "+npc.getId()+", pos: "+npc.getPosition().toString());

		if (npc != null && npc.fetchDefinition().isAttackable()) {
			if (NPCStatsViewer.view(player, npc.getId(), npc)) {
				player.getMotion().clearSteps();
				player.getPacketSender().sendMinimapFlagRemoval();
				return;
			}
		}

		final Executable action = () -> {
			player.subscribe(event -> {

				if (event == PlayerEvents.MOVED || event == PlayerEvents.LOGGED_OUT) {
					npc.resetEntityInteraction();
					return true;
				}

				final Entity playerInteracting = player.getInteractingEntity();
				final Entity npcInteracting = npc.getInteractingEntity();

				if (playerInteracting instanceof NPC && playerInteracting != npc) {
					npc.resetEntityInteraction();
					return true;
				}

				if (npcInteracting instanceof Player && npcInteracting != player) {
					npc.resetEntityInteraction();
					return true;
				}

				if (npc.getPosition().getDistance(player.getPosition()) > 1) {
					npc.debug("player too far away, resetting...");
					npc.resetEntityInteraction();
					return true;
				}
				return false;
			});
			npc.setEntityInteraction(player);
			if(PacketInteractionManager.handleNpcInteraction(player, npc, 4)) {
				return;
			}
			if (NPCActions.INSTANCE.handleClick(player, npc, Type.FOURTH_OPTION))
				return;

			if (player.getClueScrollManager().handleNPCAction(4, npc))
				return;


			switch (npc.getId()) {
				case CAPN_IZZY_NOBEARD:
					if (player.getSkillTaskManager().getTask(SkillMasterType.AGILITY) != null && player.getSkillTaskManager().getTask(SkillMasterType.AGILITY).getAmount() <= 0) {
						SkillTaskManager.checkCompletion(player, SkillMasterType.AGILITY);
					} else {
						SkillTaskManager.getTask(player, SkillMasterType.AGILITY);
					}
					break;
				case PARTY_PETE:
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.HAPPY)
							.setText("May I see the Mystery-Boxes rewards, please?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.LAUGHING)
							.setText("Absolutely!", "Please pick the Mystery-Box you would like to view!")
							.add(DialogueType.OPTION).setOptionTitle("Select an Option")
							.firstOption("<col=A0522D>Barrows mystery box", player1 -> {
								player.getPacketSender().sendInterfaceRemoval();
								SpinMysteryBox.INSTANCE.viewRewards(player, SpinMysteryBoxType.BARROWS_MYSTERY_BOX);
							}).secondOption("<col=5D7DFA>Super mystery box", player1 -> {
						player.getPacketSender().sendInterfaceRemoval();
						SpinMysteryBox.INSTANCE.viewRewards(player, SpinMysteryBoxType.SUPER_MYSTERY_BOX);
							}).thirdOption("<col=EE82EE>Extreme mystery box", player1 -> {
						player.getPacketSender().sendInterfaceRemoval();
						SpinMysteryBox.INSTANCE.viewRewards(player, SpinMysteryBoxType.EXTREME_MYSTERY_BOX);
							}).fourthOption("<col=DF1E44>PVP mystery box", player1 -> {
								player.getPacketSender().sendInterfaceRemoval();
						SpinMysteryBox.INSTANCE.viewRewards(player, SpinMysteryBoxType.PVP_MYSTERY_BOX);
							}).fifthOption("Next...", player1 -> {

					}).fifthOption("Next...", player1 -> {
						new DialogueBuilder(DialogueType.OPTION).setOptionTitle("Select an Option")
								.firstOption("<col=D2691E>Legendary mystery box", player2 -> {
									player.getPacketSender().sendInterfaceRemoval();
									SpinMysteryBox.INSTANCE.viewRewards(player, SpinMysteryBoxType.LEGENDARY_MYSTERY_BOX);
								}).secondOption("<col=FFFF00>Gilded mystery box", player3 -> {
							player.getPacketSender().sendInterfaceRemoval();
							SpinMysteryBox.INSTANCE.viewRewards(player, SpinMysteryBoxType.GILDED_MYSTERY_BOX);
						}).thirdOption("@or1@Sacred Mystery box", player3 -> {
							player.getPacketSender().sendInterfaceRemoval();
							SpinMysteryBox.INSTANCE.viewRewards(player, SpinMysteryBoxType.SACRED_MYSTERY_BOX);
						}).fourthOption("@gre@$50 Mystery Box.", player3 -> {
									player.getPacketSender().sendInterfaceRemoval();
									SpinMysteryBox.INSTANCE.viewRewards(player, SpinMysteryBoxType.VIP_MYSTERY_BOX);
						}).addCancel().start(player);

					}).start(player);
					break;

				case ACCOUNT_GUIDE:
					RankChooser.openInterface(player);
					break;

				case BARMAN: // OSRS Tokens Exchange
					new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setExpression(DialogueExpression.ANNOYED)
							.setText("Can you show me where to exchange my tokens", "for OSRS money please?")
							.add(DialogueType.NPC_STATEMENT).setNpcChatHead(npc.getId()).setExpression(DialogueExpression.THINKING)
							.setText("Certainly! Let me show you where you", "can post on Discord to exchange your tokens.")
							.add(DialogueType.OPTION).setOptionTitle("Select an Option")
							.firstOption("Open Discord Support.", player1 -> {
								player.getPacketSender().sendMessage("@dre@Opening Grinderscape Tokens Support Exchange link..");
								player.getPacketSender().sendURL("https://discord.com/channels/358664434324865024/992202052434403378");
							}).addCancel("Nevermind.").start(player);
					break;
				case PIRATE_JACKIE_THE_FRUIT:
					if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.BRIMHAVEN_ARENA.getPosition(), true, true, player.getSpellbook().getTeleportType())) {
						//TeleportHandler.teleport(player, new Position(2552, 3557, 0),
						//		player.getSpellbook().getTeleportType(), false, true);
						TeleportHandler.offerTeleportFromNPC(player, Teleporting.TeleportLocation.BRIMHAVEN_ARENA.getPosition(),
								player.getSpellbook().getTeleportType(), false, true, npc.getId(), "Brimhaven Agility Arena");
					}
					break;
				case MAGE_OF_ZAMORAK:
				case AUBURY_11435:
					if (!QuestManager.hasCompletedQuest(player, "Rune Mysteries")) {
						player.sendMessage("You need to complete " +
								"the quest 'Rune Mysteries' quest to use this teleport.");
						return;
					}
					INSTANCE.mineTeleport(player, npc.getId());
					break;
				case TURAEL:
				case MAZCHNA:
				case VANNAKA:
				case CHAELDAR:
				case DURADEL:
				case KRYSTILIA:
				case NIEVE:
				case 8623:
					SlayerManager.open(player, SlayerManager.UNLOCK);
					player.getPacketSender().sendSound(73, 5);
					break;
				case MENAPHITE_THUG:
				case MENAPHITE_THUG_3550:
					player.sendMessage("I'm afraid I can't do this, or else all the crew will attack me.");
					break;
				case BOB_BARTER_HERBS:
					// Quick-decant, decant to 4 doses
					PotionDecanting.decantInventory(player, 4, true);
					break;

				case LEAGUES_TUTOR:
				case LEAGUES_TUTOR_316:
				case LEAGUES_TUTOR_317:
				case EMBLEM_TRADER:
				case EMBLEM_TRADER_7943:
					if (player.isSkulled()) {
						DialogueManager.start(player, 3);
					} else {
						DialogueManager.start(player, 22);
						player.setDialogueOptions(new DialogueOptions() {
							@Override
							public void handleOption(Player player1, int option) {

								if (option == 1)
									player1.getCombat().skull(SkullType.WHITE_SKULL, 3600);
								else if (option == 2)
									player1.getCombat().skull(SkullType.RED_SKULL, 3600);

								player1.getPacketSender().sendInterfaceRemoval();
							}
						});
					}
					break;
			}
		};

		player.setWalkToTask(new WalkToAction(player, npc, action, WalkToAction.Policy.NO_RESET_ENTITY_INTERACTION_ON_EXECUTION, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
	}

	private static void attackNPC(Player player, NPC interact) {

		if (interact == null || interact.fetchDefinition() == null) {
			return;
		}

		if (player.getRights() == PlayerRights.DEVELOPER || PlayerRights.OWNER.equals(player.getRights())) {
			player.getPacketSender().sendMessage(
					"Attack NPC: " + Integer.toString(interact.getId()) + ", index=" + Integer.toString(interact.getIndex()) + ", pos=" + interact.getPosition().toString(), 1000);
		}

		if (interact.getHitpoints() <= 0) {
			player.getMotion().clearSteps();
			player.getPacketSender().sendMinimapFlagRemoval();
			player.setEntityInteraction(null);
			return;
		}

		if (Math.abs(player.getPosition().getX() - interact.getPosition().getX()) > 20 || Math.abs(player.getPosition().getY() - interact.getPosition().getY()) > 20) {
			player.getPacketSender().sendMessage("I can't reach that!");
			resetInteractions(player, interact);
			return;
		}

		if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 4084) { // Sled
			player.getPacketSender().sendMessage("You can't attack while your on sled!", 1000);
			resetInteractions(player, interact);
			return;
		}

		if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) { // AFK Status
			player.getMotion().clearSteps();
			player.getPacketSender().sendMinimapFlagRemoval();
			player.setEntityInteraction(null);
			return;
		}
		if (!MorphItems.INSTANCE.notTransformed(player, "attack", true, true)){
			resetInteractions(player, interact);
			return;
		}

		if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 20056) { // Ale of the gods
			player.getPacketSender().sendMessage("You can't attack while holding Ale of the gods!", 1000);
			resetInteractions(player, interact);
			return;
		}

		if (player.BLOCK_ALL_BUT_TALKING || player.isShouldNoClip()) {
			resetInteractions(player, interact);
			return;
		}

		if (NPCActions.INSTANCE.handleClick(player, interact, Type.ATTACK))
			return;

		player.getCombat().initiateCombat(interact, true);
	}

	private static void resetInteractions(Player player, NPC interact) {
		//player.setPositionToFace(interact.getPosition());
		player.getMotion().clearSteps();
		player.getPacketSender().sendMinimapFlagRemoval();
		//player.setEntityInteraction(null);
	}

	private static void mageNpc(Player player, NPC interact, int spellId) {

		if (interact == null || interact.fetchDefinition() == null) {
			return;
		}

		if (player.getRights() == PlayerRights.DEVELOPER) {
			player.getPacketSender().sendMessage(
					"Magic on NPC: " + Integer.toString(interact.getId()) + ". " + interact.getPosition().toString());
		}

		player.setPositionToFace(interact.getPosition());

		final Optional<CombatSpellType> optionalCombatSpellType = CombatSpellType.getCombatSpells(spellId);
		final Optional<InteractiveSpell> nonCombatSpell = InteractiveSpell.forSpellId(spellId);

		int distance = spellId == 12425 || spellId == 12435 || spellId == 12455 ? 8 : 10;
		player.setEntityInteraction(interact);
		player.setWalkToTask(new WalkToAction<>(player, interact, distance, 0, () -> {

			if (!interact.isAlive()) {
				resetInteractions(player, interact);
				return;
			}

			if(optionalCombatSpellType.isEmpty()) {
				player.sendMessage("You cannot use this spell on the NPC.");
				return;
			}

			if (Math.abs(player.getPosition().getX() - interact.getPosition().getX()) > 20 || Math.abs(player.getPosition().getY() - interact.getPosition().getY()) > 20) {
				player.getPacketSender().sendMessage("I can't reach that!");
				resetInteractions(player, interact);
				return;
			}

			if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 4084) { // Sled
				player.getPacketSender().sendMessage("You can't  use this spell while your on sled!", 1000);
				resetInteractions(player, interact);
				return;
			}

			if (!MorphItems.INSTANCE.notTransformed(player, "use this spell", true, true)){
				resetInteractions(player, interact);
				return;
			}

			if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 20056) { // Ale of the gods
				player.getPacketSender().sendMessage("You can't use this spell while holding Ale of the gods!", 1000);
				resetInteractions(player, interact);
				return;
			}

			if(NPCActions.INSTANCE.handleClick(player, interact, Type.CAST_SPELL))
				return;

/*			Item weapon = player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT];
			if(weapon.getId() == ItemID.TRIDENT_OF_THE_SEAS_FULL_ || weapon.getId() == ItemID.TRIDENT_OF_THE_SEAS || weapon.getId() == ItemID.TRIDENT_OF_THE_SWAMP || weapon.getId() == ItemID.UNCHARGED_TRIDENT
					|| weapon.getId() == ItemID.UNCHARGED_TOXIC_TRIDENT || weapon.getId() == 22290 || weapon.getId() == 22292 || weapon.getId() == 22294) {
				player.sendMessage("You cannot cast spells whilst wielding a trident.");
				resetInteractions(player, interact);
				return;
			}*/

			final CombatSpellType spellType = optionalCombatSpellType.get();
			final CombatSpell spell = spellType.getSpell();
			player.getCombat().setCastSpell(spell);
			player.getCombat().setCastSpellType(spellType);
			player.getCombat().initiateCombat(interact);



			if (spellId == 12425 || spellId == 12435 || spellId == 12455) { // Tele-other spells
				player.getMotion().reset();
				player.getPacketSender().sendMinimapFlagRemoval();
				player.getMotion().followTarget(null);
				player.getMotion().clearSteps();
				if (!nonCombatSpell.isPresent()) {
					return;
				}
				if (!nonCombatSpell.get().getSpell().canCast(player, interact, false)) {
					return;
				}
				player.getPacketSender().sendMessage("You can't attack this npc.");
			}
		}, WalkToAction.Policy.EXECUTE_ON_LINE_OF_SIGHT, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES, WalkToAction.Policy.ALLOW_UNDER));
	}
}