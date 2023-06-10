package com.grinder.game.content.miscellaneous;

import com.google.common.collect.ImmutableSet;
import com.grinder.game.World;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.Animation;
import com.grinder.game.model.ButtonActions;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.AttributeManager.Points;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.bank.Bank;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.util.Misc;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * Handles pets. Allows them to be dropped on the ground aswell as picked up.
 * Credits to Lumiere from R-S for the huge {@link Pet} data.
 *
 * @author Professor Oak
 */
public class PetHandler {

	/**
	 * The {@link Animation} for interacting with a pet.
	 */
	private static final Animation INTERACTION_ANIM = new Animation(827);

	static {
		ButtonActions.INSTANCE.onClick(27654, (clickAction -> {
			final Player player = clickAction.getPlayer();
			if (player.getCurrentPet() != null) {
				player.getCurrentPet().moveTo(player.getPosition());
			} else {
				player.sendMessage("You do not have a follower.");
			}
		}));
	}

	/**
	 * Randomly gives player pets when skilling.
	 *
	 * @param player
	 * @param skill
	 */
	public static void onSkill(Player player, Skill skill) {
		Pet pet = Pet.getPetForSkill(skill);
		if (pet == null) {
			return;
		}
		if (pet.getSkill().isPresent() && pet.getSkill().get() == skill) {
			if (Misc.getRandomInclusive(1500) == Misc.getRandomInclusive(1500)) {

				if (alreadyExists(player, pet.getItemId())) {
					// Increase points
					player.getPoints().increase(Points.ALREADY_EXISTING_PET_COUNT, 1); // Increase points

//					// Collection Log Entry
//					player.getCollectionLog().createOrUpdateEntry("Skilling Pets", new Item(pet.getItemId()));

				} else {

					AchievementManager.processFor(AchievementType.PET_COLLECTOR, player);
					PlayerUtil.broadcastMessage("<img=783>@dre@ " + PlayerUtil.getImages(player) + "" + player.getUsername() +" just found a stray " + pet.getName()
							+ " while training " + Misc.formatName(skill.toString().toLowerCase()) + "!");

					// Increase points
					player.getPoints().increase(Points.PETS_RECEIVED, 1); // Increase points

					player.getPacketSender()
							.sendMessage("@dre@You have a weird feeling that you might have been followed.");

					// Collection Log Entry
					player.getCollectionLog().createOrUpdateEntry(player,  "Skilling Pets", new Item(pet.getItemId()));

					drop(player, pet.getItemId(), true);


				}
			}
		}
	}

	public static boolean alreadyExists(Player player, int id) {
		for (Bank banks : player.getBanks()) {
			if (banks == null) {
				continue;
			}
			if (banks.contains(id)) {
				return true;
			}
		}
		return player.getCurrentPet() != null && player.getCurrentPet().getId() == Pet.getPetForItem(id).get().getId() || player.getInventory().contains(id);
	}

	/**
	 * Attempts to drop a pet.
	 *
	 * @param player
	 *            The player to spawn a pet for.
	 * @param id
	 *            The pet-to-spawn's identifier.
	 * @param reward
	 *            Is this pet spawn a reward?
	 * @return
	 */
	public static boolean drop(Player player, int id, boolean reward) {
		Optional<Pet> pet = Pet.getPetForItem(id);
		if (pet.isPresent()) {

			// Check if we already have a pet..
			if (player.getCurrentPet() == null) {

				// Spawn the pet..
				NPC npc = NPCFactory.INSTANCE.create(pet.get().getId(), player.getPosition().clone().add(0, 1));
				npc.setPet(true);
				npc.setOwner(player);
				npc.getMotion().followTarget(player);
				World.getNpcAddQueue().add(npc);

				// Set the player's current pet to this one.
				player.setCurrentPet(npc);
				player.getPoints().set(Points.PET, id);

				npc.setEntityInteraction(player);
				// If this is a reward, congratulate them.
				// Otherwise simply drop it on the ground.
				if (reward) {
					player.getPacketSender()
							.sendMessage("@dre@You suddenly get a weird feeling, as if someone is following you.");
				} else {
					player.getInventory().delete(pet.get().getItemId(), 1);
					player.getPacketSender().sendMessage("You drop your pet..");
					player.performAnimation(INTERACTION_ANIM);
					player.setPositionToFace(npc.getPosition());
				}
			} else {
				// We might have to add to bank if inventory is full!
				if (reward) {
					if (!player.getInventory().isFull()) {
						player.getInventory().add(pet.get().getItemId(), 1);
					} else {
						ItemOnGroundManager.registerNonGlobal(player, new Item(pet.get().getItemId()));
					}
					player.getPacketSender().sendMessage("<img=789>@dre@ You've received a pet!");
				} else {
					player.getPacketSender().sendMessage("You already have a pet following you.", 1000);
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Attempts to pick up a pet.
	 *
	 * @param player
	 *            The player picking up the pet.
	 * @param npc
	 *            The pet to pick up.
	 * @return
	 */
	public static boolean pickup(Player player, NPC npc) {
		if (npc == null || player.getCurrentPet() == null) {
			return false;
		}
		// Make sure npc is a pet..
		Optional<Pet> pet = Pet.getPet(npc.getId());
		if (!pet.isPresent()) {
			return false;
		}
		if (player.getInventory().isFull()) {
			player.getPacketSender().sendMessage("You don't have enough inventory space to pick up your pet.", 1000);
			return true;
		}

		// Make sure we're picking up our pet!
		if (player.getCurrentPet().equals(npc)) {

			// Perform animation..
			player.performAnimation(INTERACTION_ANIM);

			// Remove the npc from the world
			World.getNpcRemoveQueue().add(player.getCurrentPet());

			player.getPoints().set(Points.PET, -1);

			// Add pet to inventory or bank
			if (!player.getInventory().isFull()) {
				player.getInventory().add(pet.get().getItemId(), 1);
			} else {
				BankUtil.addToBank(player, new Item(pet.get().getItemId(), 1));
			}

			// Send message
			player.getPacketSender().sendMessage("You pick up your pet..");

			// Reset pet
			player.setCurrentPet(null);
			return true;
		}
		return false;
	}
	
	public static boolean onLogout(Player player, NPC npc) {
		if (npc == null || player.getCurrentPet() == null) {
			return false;
		}
		// Make sure npc is a pet..
		Optional<Pet> pet = Pet.getPet(npc.getId());
		if (!pet.isPresent()) {
			return false;
		}

		// Make sure we're picking up our pet!
		if (player.getCurrentPet().equals(npc)) {

			// Remove the npc from the world
			World.getNpcRemoveQueue().add(player.getCurrentPet());

			player.getPoints().set(Points.PET, -1);

			// Add pet to inventory or bank
			if (!player.getInventory().isFull()) {
				player.getInventory().add(pet.get().getItemId(), 1);
			} else {
				BankUtil.addToBank(player, new Item(pet.get().getItemId(), 1));
			}

			// Reset pet
			player.setCurrentPet(null);
			return true;
		}
		return false;
	}

	/***
	 * Attempts to morph a pet.
	 *
	 * @param player
	 *            The player morphing a pet.
	 * @param npc
	 *            The pet being morphed.
	 * @return
	 */
	public static boolean morph(Player player, NPC npc) {
		if (npc == null || player.getCurrentPet() == null) {
			return false;
		}

		// Make sure npc is a pet..
		Optional<Pet> pet = Pet.getPet(npc.getId());
		if (!pet.isPresent()) {
			return false;
		}

		// Make sure we're picking up our own pet!
		if (player.getCurrentPet().equals(npc)) {

			// If this pet can morph..
			if (pet.get().canMorph()) {
				npc.setNpcTransformationId(pet.get().getMorphId());
				player.getPacketSender().sendMessage("Your pet endures metamorphosis and transforms.");
			}
			return true;
		}
		return false;
	}

	/**
	 * Attempts to interact with the given pet.
	 *
	 * @param player
	 * @param npc
	 * @return
	 */
	public static boolean interact(Player player, NPC npc) {
		if (npc == null || player.getCurrentPet() == null) {
			return false;
		}

		// Make sure npc is a pet..
		Optional<Pet> pet = Pet.getPet(npc.getId());
		if (!pet.isPresent() || pet.get().getDialogue(player) == -1) {
			return false;
		}

		// Make sure we're interacting with our own pet!
		if (player.getCurrentPet().equals(npc)) {
			if (player.getCurrentPet().getId() == Pet.OLMLET.getId()) {
				DialogueManager.start(player, 298);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player, int option) {
						switch (option) {
						case 1:
							DialogueManager.start(player, 300);
							break;
						case 2:
							DialogueManager.start(player, 303);
							break;
						case 3:
							DialogueManager.start(player, 308);
							break;
						case 4:
							player.getPacketSender().sendInterfaceRemoval();
							break;
						}
					}
				});
			} else {
				DialogueManager.start(player, pet.get().getDialogue(player));
			}
			return true;
		}
		return false;
	}

	/**
	 * Contains all data related to pets.
	 *
	 * @author Lumiere
	 */
	public enum Pet {

		KITTEN(5591, 0, 1555, 100),

		LAZY_CAT(1626, 0, 6549, 2714),
		// BOSS & SLAYER PETS
		DARK_CORE(318, 0, 12816, 123),

		CORP_BEAST(8008, 0, 22318, 2656),

		VENENATIS_SPIDERLING(495, 0, 13177, 126),

		CALLISTO_CUB(11986, 0, 13178, 130),

		HELLPUPPY(964, 0, 13247, 138) {
			@Override
			public int getDialogue(Player player) {
				int[] dialogueIds = new int[] { 138, 143, 145, 150, 154 };
				return dialogueIds[Misc.getRandomInclusive(dialogueIds.length - 1)];
			}
		},
		CHAOS_ELEMENTAL_JR(2055, 0, 11995, 158),

		SNAKELING(2130, 2131, 12921, 162),

		MAGMA_SNAKELING(2131, 2132, 12921, 169),

		TANZANITE_SNAKELING(2132, 2130, 12921, 176),

		VETION_JR(11983, 11984, 13179, 183),

		VETION_JR_REBORN(5537, 5536, 13179, 189),

		SCORPIAS_OFFSPRING(5561, 0, 13181, 195),

		ABYSSAL_ORPHAN(5884, 0, 13262, 202) {
			@Override
			public int getDialogue(Player player) {
				if (!player.getAppearance().isMale()) {
					return 206;
				} else {
					int[] dialogueIds = new int[] { 202, 209 };
					return dialogueIds[Misc.getRandomInclusive(dialogueIds.length - 1)];
				}
			}
		},
		TZREK_JAD(5892, 0, 13225, 212) {
			@Override
			public int getDialogue(Player player) {
				int[] dialogueIds = new int[] { 212, 217 };
				return dialogueIds[Misc.getRandomInclusive(dialogueIds.length - 1)];
			}
		},

/*		MIDNIGHT(7893, 7892, 21750, 2732) {
			@Override
			public int getDialogue(Player player) {
				int[] dialogueIds = new int[] { 2732, 2740, 2755 };
				return dialogueIds[Misc.getRandomInclusive(dialogueIds.length - 1)];
			}
		},
		JAL_NIB_REK(7675, 8009, 21291),
		NOON(7892, 7893, 21748),
		SKOTOS(7671 21273),
		LIL_ZIK(8337, 22473),
		TZREK_ZUK(8009, 7675, 22319),
		SRARACHA(2143, 23495),
		HERBI(7760, 21509),
		PENANCE_QUEEN(6674, 12703),
		BLOODHOUND(7232, 19730),
		CHOMPY_CHICK(4002, 4002),
		PHOENIX(7370, 20693),


		OLMLET(7520, 8201, 20851),
		PUPPADILE(8201, 8202, 22376),
		TEKTINY(8202, 8203, 22378),
		VANGUARD(8203, 8204, 22380),
		VASA(8204, 8205, 22382),
		VESPINE(8205, 7520, 22384),*/
/*		           case ABYSSAL_ORPHAN: {
			return player -> player.dialogue(
					new NPCDialogue(pet.npcId, "You killed my father."),
					new ActionDialogue(() -> {
						if (Random.rollDie(2, 1)) {
							player.dialogue(
									new PlayerDialogue("Yeah, don't take it personally."),
									new NPCDialogue(pet.npcId, "In his dying moment, my father poured his last ounce of strength into my creation. " +
											"My being is formed from his remains."),
									new NPCDialogue(pet.npcId, "When your own body is consumed to nourish the Nexus, and an army of scions arises " +
											"from your corpse, I trust you will not take it personally either."));
						} else {
							if (player.getAppearance().isMale()) {
								player.dialogue(
										new PlayerDialogue("No, I am your father."),
										new NPCDialogue(pet.npcId, "No you're not."));
							} else {
								player.dialogue(
										new PlayerDialogue("No, I am your father."),
										new NPCDialogue(pet.npcId, "Human biology may be unfamiliar to me, but nevertheless I doubt that very much."));
							}
						}
					}));
		}



            case HELLPUPPY: {
			return player -> {
				int random = Random.get(1, 5);
				if (random == 1) {
					player.dialogue(
							new PlayerDialogue("How many souls have you devoured?"),
							new NPCDialogue(pet.npcId, "None."),
							new PlayerDialogue("Aww p-"),
							new NPCDialogue(pet.npcId, "Yet."),
							new PlayerDialogue("Oh.")
					);
				} else if (random == 2) {
					player.dialogue(
							new PlayerDialogue("I wonder if I need to invest in a trowel when I take you out for a walk."),
							new NPCDialogue(pet.npcId, "More like a shovel.")
					);
				} else if (random == 3) {
					player.dialogue(
							new PlayerDialogue("Why are the hot dogs shivering?"),
							new NPCDialogue(pet.npcId, "Grrrrr..."),
							new PlayerDialogue("Because they were served-"),
							new NPCDialogue(pet.npcId, "GRRRRRR..."),
							new PlayerDialogue("-with.. chilli?")
					);
				} else if (random == 4) {
					player.dialogue(
							new PlayerDialogue("Hell yeah! Such a cute puppy."),
							new NPCDialogue(pet.npcId, "Silence mortal! OR I'll eat your soul."),
							new PlayerDialogue("Would that go well with lemon?"),
							new NPCDialogue(pet.npcId, "Grrr...")
					);
				} else {
					player.dialogue(
							new PlayerDialogue("What a cute puppy, how nice to meet you."),
							new NPCDialogue(pet.npcId, "It'd be nice to meat you too..."),
							new PlayerDialogue("Urk... nice doggy."),
							new NPCDialogue(pet.npcId, "Grrr...")
					);
				}
			};
		}

            case BLOODHOUND: {
			return player -> {
				int random = Random.get(1, 5);
				if (random == 1) {
					player.dialogue(
							new PlayerDialogue("How come I can talk to you without an amulet?"),
							new NPCDialogue(pet.npcId, "*Woof woof bark!* Elementary, it's due to the influence of the -SQUIRREL-!")
					);
				} else if (random == 2) {
					player.dialogue(
							new PlayerDialogue("Walkies!"),
							new NPCDialogue(pet.npcId, "...")
					);
				} else if (random == 3) {
					player.dialogue(
							new PlayerDialogue("Can you help me with this clue?"),
							new NPCDialogue(pet.npcId, "*Woof! Bark yip woof!* Sure! Eliminate the impossible first."),
							new PlayerDialogue("And then?"),
							new NPCDialogue(pet.npcId, "*Bark! Woof bark bark.* Whatever is left, however improbable, must be the answer."),
							new PlayerDialogue("So helpful.")
					);
				} else if (random == 4) {
					player.dialogue(
							new PlayerDialogue("I wonder if I could sell you to a vampire to track down dinner."),
							new NPCDialogue(pet.npcId, "*Woof bark bark woof* I have teeth too you know, that joke was not funny.")
					);
				} else {
					player.dialogue(
							new PlayerDialogue("Hey boy, what's up?"),
							new NPCDialogue(pet.npcId, "*Woof! Bark bark woof!* You smell funny."),
							new PlayerDialogue("Err... funny strange or funny ha ha?"),
							new NPCDialogue(pet.npcId, "*Bark bark woof!* You aren't funny.")
					);
				}
			};
		}
            case CHOMPY_CHICK: {
			return player -> player.dialogue(
					new NPCDialogue(pet.npcId, "*Chirp!*")
			);
		}
            case PHOENIX: {
			return player -> {
				int random = Random.get(1, 4);
				if (random == 1) {
					player.dialogue(
							new PlayerDialogue("So... The Pyromancers, they're cool, right?"),
							new NPCDialogue(pet.npcId, "We share a common goal.."),
							new PlayerDialogue("Which is?"),
							new NPCDialogue(pet.npcId, "Keeping the cinders burning and preventing the long night from swallowing us all."),
							new PlayerDialogue("That sounds scary."),
							new NPCDialogue(pet.npcId, "As long as we remain vigilant and praise the Sun, all will be well.")
					);
				} else if (random == 2) {
					player.dialogue(
							new NPCDialogue(pet.npcId, "..."),
							new PlayerDialogue("What are you staring at?"),
							new NPCDialogue(pet.npcId, "The great Sol Supra."),
							new PlayerDialogue("Is that me?"),
							new NPCDialogue(pet.npcId, "No mortal. The Sun, as you would say."),
							new PlayerDialogue("Do you worship it?"),
							new NPCDialogue(pet.npcId, "It is wonderous... If only I could be so grossly incandescent.")
					);
				} else if (random == 3) {
					player.dialogue(
							new PlayerDialogue("Who's a pretty birdy?"),
							new NPCDialogue(pet.npcId, "The Phoenix Gives you a smouldering look.")
					);
				} else {
					player.dialogue(
							new NPCDialogue(pet.npcId, "One day I will burn so hot I'll become Sacred Ash"),
							new PlayerDialogue("Aww, but you're so rare, where would I find another?"),
							new NPCDialogue(pet.npcId, "Do not fret, mortal, I will rise from the Sacred Ash greater than ever before."),
							new PlayerDialogue("So you're immortal?"),
							new NPCDialogue(pet.npcId, "As long as the Sun in the sky gives me strength."),
							new PlayerDialogue("...Sky?")
					);
				}
			};
		}
            case SKOTOS: {
			return player -> {
				int random = Random.get(1, 3);
				if (random == 1) {
					player.dialogue(
							new PlayerDialogue("You look cute."),
							new NPCDialogue(pet.npcId, "I do not thinke thou understand the depths of the darkness you have unleashed upon the world." +
									" To dub it in such a scintillant manner is offensive to mine being."),
							new PlayerDialogue("So why are you following me around?"),
							new NPCDialogue(pet.npcId, "Dark forces of which ye know nought have deemed that this is my geas."),
							new PlayerDialogue("Your goose?"),
							new NPCDialogue(pet.npcId, "*Sighs* Nae. But thine is well and truly cooked.")
					);
				} else if (random == 2) {
					player.dialogue(
							new NPCDialogue(pet.npcId, "I am spawned of darkness. I am filled with darkness. I am darkness incarnate and to darkness I will return."),
							new PlayerDialogue("Sounds pretty... dark."),
							new NPCDialogue(pet.npcId, "Knowest thou not of the cursed place? Knowest thou not about the future yet to befall your puny race?"),
							new PlayerDialogue("Oh yes, I've heard that before."),
							new NPCDialogue(pet.npcId, "Then it is good that ye can laugh in the face of the end."),
							new PlayerDialogue("The end has a face? Which end?"),
							new NPCDialogue(pet.npcId, "*Sighs* The darkness giveth, and the darkness taketh.")
					);
				} else {
					player.dialogue(
							new NPCDialogue(pet.npcId, "Nothing. Ye are already tainted in my sight by the acts of light. However they may be some hope for you if you continue to aid the darkness."),
							new PlayerDialogue("I do have a lantern around here somewhere."),
							new NPCDialogue(pet.npcId, "Do not bring that foul and repellant thing near mine self.")
					);
				}
			};
		}
            case JAL_NIB_REK: {
			return player -> {
				int random = Random.get(1, 3);
				if (random == 1) {
					player.dialogue(
							new PlayerDialogue("Yo Nib, what's going on?"),
							new NPCDialogue(pet.npcId, "Nibnib? Kl-Rek Nib?"),
							new MessageDialogue("Jal-Nib-Rek nips you."),
							new PlayerDialogue("What's you do that for?"),
							new NPCDialogue(pet.npcId, "Heh Nib get you.")
					);
				} else if (random == 2) {
					player.dialogue(
							new PlayerDialogue("What'd you have for dinner?"),
							new NPCDialogue(pet.npcId, "Nibblings!"),
							new PlayerDialogue("Nibblings of what exactly?"),
							new NPCDialogue(pet.npcId, "Nib."),
							new PlayerDialogue("Oh no! That's horrible.")
					);
				} else {
					player.dialogue(
							new PlayerDialogue("Can you speak like a human can Nib?"),
							new NPCDialogue(pet.npcId, "No, I most definitely can not."),
							new PlayerDialogue("Aren't you speaking like a human right now...?"),
							new NPCDialogue(pet.npcId, "Jal-Nib-Rek Nib Kl-Jal, Zuk is mum."),
							new PlayerDialogue("Interesting.")
					);
				}
			};
		}
            case TZREK_ZUK: {
			return player -> {
				int random = Random.get(1, 3);
				if (random == 1) {
					player.dialogue(
							new PlayerDialogue("What's up Zuk?"),
							new NPCDialogue(pet.npcId, "Feeling a bit down to be honest."),
							new PlayerDialogue("Why's that?"),
							new NPCDialogue(pet.npcId, "Well..."),
							new NPCDialogue(pet.npcId, "Not so long ago, I was a big fearsome boss, Now I'm just another pet."),
							new PlayerDialogue("Indeed, and you're going to follow me everywhere I go.")
					);
				} else if (random == 2) {
					player.dialogue(
							new PlayerDialogue("Why have you got lava around your feet?"),
							new NPCDialogue(pet.npcId, "Keeps me cool."),
							new PlayerDialogue("But... lava is hot?"),
							new NPCDialogue(pet.npcId, "No no, I wasn't referring to the temperature."),
							new PlayerDialogue("Ah...")
					);
				} else {
					player.dialogue(
							new PlayerDialogue("You're a lot smaller now, I don't even need a shield."),
							new NPCDialogue(pet.npcId, "Mere mortal, you only survived my challenge because of that convenient pile of rock."),
							new PlayerDialogue("Well, you couldn't even break that pile of rock to get at me!"),
							new NPCDialogue(pet.npcId, "...")
					);
				}
			};
		}
            case MIDNIGHT: {
			return player -> {
				int random = Random.get(1, 3);
				if (random == 1) {
					player.dialogue(
							new PlayerDialogue("Hello little other one."),
							new NPCDialogue(pet.npcId, "Other?"),
							new PlayerDialogue("Yes, don't you have a sister?"),
							new NPCDialogue(pet.npcId, "I don't want to chalk about it.")
					);
				} else if (random == 2) {
					player.dialogue(
							new PlayerDialogue("Sometimes I'm worried you'll attack me whilst my back is turned."),
							new NPCDialogue(pet.npcId, "Are you petrified of my tuffness?"),
							new PlayerDialogue("Not really, but your puns are awful."),
							new NPCDialogue(pet.npcId, "I thought they were clastic.")
					);
				} else {
					player.dialogue(
							new PlayerDialogue("I feel like our relationship is slowly eroding away."),
							new NPCDialogue(pet.npcId, "Geode willing.")
					);
				}
			};
		}
            case NOON: {
			return player -> {
				int random = Random.get(1, 3);
				if (random == 1) {
					player.dialogue(
							new PlayerDialogue("Hello little one."),
							new NPCDialogue(pet.npcId, "I may be small but at least I'm perfectly formed.")
					);
				} else if (random == 2) {
					player.dialogue(
							new PlayerDialogue("What's your favourite rock?"),
							new NPCDialogue(pet.npcId, "You're going tufa with that question. That's personal."),
							new PlayerDialogue("Was just trying to make light conversation, not trying to aggregate you.")
					);
				} else {
					player.dialogue(
							new PlayerDialogue("Metaphorically speaking, do you have a heart of stone?"),
							new NPCDialogue(pet.npcId, "Yes, but you're not having it.")
					);
				}
			};
		}
            case HERBI: {
			return player -> {
				int random = Random.get(1, 5);
				if (random == 1) {
					player.dialogue(
							new PlayerDialogue("Are you hungry?"),
							new NPCDialogue(pet.npcId, "That depends, what have you got?"),
							new PlayerDialogue("I'm sure I could knock you up a decent salad."),
							new NPCDialogue(pet.npcId, "I'm actually a insectivore."),
							new PlayerDialogue("Oh, but your name suggests that-"),
							new NPCDialogue(pet.npcId, "I think you'll find I didn't name myself, you humans and your silly puns."),
							new PlayerDialogue("No need to PUNish us for our incredible wit."),
							new NPCDialogue(pet.npcId, "Please. Stop.")
					);
				} else if (random == 2) {
					player.dialogue(
							new PlayerDialogue("Have your herbs died?"),
							new NPCDialogue(pet.npcId, "These old things? I guess they've dried up... I'm getting old and I need caring for. I've chosen you to do that by the way."),
							new PlayerDialogue("Oh fantastic! I guess I'll go shell out half a million coins to keep you safe then, what superb luck!"),
							new NPCDialogue(pet.npcId, "I could try the next person if you'd prefer?"),
							new PlayerDialogue("I'm just joking you old swine!")
					);
				} else if (random == 3) {
					player.dialogue(
							new PlayerDialogue("So you live in a hole? I would've thought Boars are surface dwelling mammals."),
							new NPCDialogue(pet.npcId, "Well, I'm special! I bore down a little so I'm nice and cosy with my herbs exposed to the sun, it's all very interesting."),
							new PlayerDialogue("Sounds rather... Boring!"),
							new NPCDialogue(pet.npcId, "How very original...")
					);
				} else if (random == 4) {
					player.dialogue(
							new PlayerDialogue("Tell me... do you like Avacado?"),
							new NPCDialogue(pet.npcId, "I'm an insectivore, but even if I wasn't I'd hate Avacado!"),
							new PlayerDialogue("Why ever not? It's delicious!"),
							new NPCDialogue(pet.npcId, "I don't know why people like it so much... it tastes like a ball of chewed up grass."),
							new PlayerDialogue("Sometimes you can be such a bore...")
					);
				} else {
					player.dialogue(
							new NPCDialogue(pet.npcId, "When I was a young HERBIBOAR!!"),
							new PlayerDialogue("I'm standing right next to you, no need to shout..."),
							new NPCDialogue(pet.npcId, "I was trying to sing you a song...")
					);
				}
			};
		}
            case PENANCE_QUEEN: {
			return player -> player.dialogue(
					new PlayerDialogue("Of all the high gamble rewards I could have won, I won you..."),
					new NPCDialogue(pet.npcId, "Keep trying, human. You'll never win that Dragon Chainbody.")
			);
		}
            case OLMLET: {
			return player -> player.dialogue(
					new NPCDialogue(pet.npcId, "Hee hee! What shall we talk about, human?"),
					new OptionsDialogue(
							new Option("You look like a dragon.", () -> player.dialogue(
									new PlayerDialogue("You look like a dragon."),
									new NPCDialogue(pet.npcId, "And humans look like monkeys. Badly shaved monkeys. What's your point, human?"),
									new PlayerDialogue("Are you related to dragons?"),
									new NPCDialogue(pet.npcId, "My sire was an olm. I'm an olm. I don't go around asking you about your parents' species, do I?"),
									new PlayerDialogue("... no, I suppose you don't."),
									new NPCDialogue(pet.npcId, "Hee hee! Let's change the subject before someone gets insulted.")
							)),
							new Option("Where do creatures like you come from?", () -> player.dialogue(
									new PlayerDialogue("Where do creatures like you come from?"),
									new NPCDialogue(pet.npcId, "From eggs, of course! You can't make an olmlet without breaking an egg."),
									new PlayerDialogue("That's... informative. Thank you."),
									new NPCDialogue(pet.npcId, "Hee hee!")
							)),
							new Option("Can you tell me secrets about your home?", () -> player.dialogue(
									new PlayerDialogue("Can you tell me secrets about your home?"),
									new NPCDialogue(pet.npcId, "Ooh, it was lovely. I lived in an eggshell. I was safe in there, dreaming of the life I would lead when I hatched, and the caverns I could rule."),
									new NPCDialogue(pet.npcId, "Then suddenly I felt a trembling of the ground, and my shell shattered."),
									new NPCDialogue(pet.npcId, "Through its cracks I saw the world for the first time, just in time to watch my sire die."),
									new NPCDialogue(pet.npcId, "It was a terrible shock for a newly hatched olmlet, but I try not to let it affect my mood.")
							)),
							new Option("Maybe another time.", () -> player.dialogue(
									new PlayerDialogue("Maybe another time.")
							))
					)
			);
		}
            case LIL_ZIK: {
			int random = Random.get(1, 4);
			if (random == 1) {
				return player -> player.dialogue(
						new PlayerDialogue("Hey Lil' Zik."),
						new NPCDialogue(pet.npcId, "Stop."),
						new NPCDialogue(pet.npcId, "Calling."),
						new NPCDialogue(pet.npcId, "Me."),
						new NPCDialogue(pet.npcId, "Little."),
						new PlayerDialogue("Never!")
				);
			}
			if(random == 2) {
				return player -> player.dialogue(
						new PlayerDialogue("You know... you're not like other spiders."),
						new NPCDialogue(pet.npcId, "You know I hate it when you say that... please leave me alone."),
						new PlayerDialogue("But I earned you fair and square at the Theatre of Blood! You're mine to keep."),
						new NPCDialogue(pet.npcId, "...")
				);
			}
			if(random == 3) {
				return player -> player.dialogue(
						new PlayerDialogue("Incy wincy Verzik climbed up the water spout..."),
						new PlayerDialogue("Down came the rain and washed poor Verzik out..."),
						new NPCDialogue(pet.npcId, "Out came the Vampyre to put an end to this at once. Humans deserve only one fate!"),
						new PlayerDialogue("Wow, calm down. It's just a nursery rhyme."),
						new NPCDialogue(pet.npcId, "I'm not in hte mood.")
				);
			}
			if(random == 4) {
				return player -> player.dialogue(
						new PlayerDialogue("Hi, I'm here for my reward!"),
						new NPCDialogue(pet.npcId, "Not again...")
				);
			}
		}

            case SMOLCANO: {
			return player -> {
				int random = Random.get(1, 2);
				if(random == 1) {
					player.dialogue(new PlayerDialogue("How much did you pay for your ring of stone?"),
							new NPCDialogue(pet.npcId,  "What are you talking about?"),
							new PlayerDialogue("They're so expensive, but so much fun!"),
							new NPCDialogue(pet.npcId, "Right..."));
				}
				if(random == 2) {
					player.dialogue(new PlayerDialogue("So why do they call you Zalcano?"),
							new NPCDialogue(pet.npcId, "Well...."),
							new PlayerDialogue("is it because you’re like a vol-"),
							new NPCDialogue(pet.npcId, "Don’t say it!"));
				}
			};
		}
            case YOUNGLLEF:
				case CORRUPTED_YOUNGLLEF :{
			return player -> {
				int random = Random.get(1, 2);
				if(random == 1) {
					player.dialogue(new PlayerDialogue("I don't get it... Are you real or not?"),
							new NPCDialogue(pet.metaId,  "I'm a crystalline formation, made by the elves."),
							new PlayerDialogue("But, like... Can you feel it if I pinch you?"),
							new NPCDialogue(pet.metaId, "Don't you even think about it."));
				}
				if(random == 2) {
					player.dialogue(new PlayerDialogue("What actually are you? A big wolf or something?"),
							new NPCDialogue(pet.metaId, "I suppose I might look something like that."),
							new PlayerDialogue("That sounds like a no. What are you then?"),
							new NPCDialogue(pet.metaId, "A hunllef."),
							new PlayerDialogue("A what?"),
							new NPCDialogue(pet.metaId, "Nevermind."),
							new PlayerDialogue("You know, you can be a real nightmare sometimes."));
				}
			};
		}
            case SRARACHA: {
			return player -> player.dialogue(new PlayerDialogue("So what kind of spider are you...?"),
					new NPCDialogue(pet.npcId,  "The hive cluster is under attack!"),
					new PlayerDialogue("Erm, I think the attack is over. I have already killed your queen."),
					new NPCDialogue(pet.npcId, "Then we should spawn more overlords!"));
		}
	}*/

		SUPREME_HATCHLING(6628, 0, 12643, 220), PRIME_HATCHLING(6629, 0, 12644, 223),

		REX_HATCHLING(6630, 0, 12645, 231),

		CHICK_ARRA(6631, 0, 12649, 239),

		GENERAL_AWWDOR(6632, 0, 12650, 247),

		COMMANDER_MINIANA(6633, 0, 12651, 250) {
			@Override
			public int getDialogue(Player player) {
				if (player.getEquipment().contains(11806)) {
					return 252;
				} else
					return 250;
			}
		},
		
		VORKI(8029, 0, 21992, 2664),

		NEXLING(11277, 0, 26348, 2881),
		
		KRIL_TINYROTH(6634, 0, 12652, 254),

		BABY_MOLE(6635, 0, 12646, 261),

		PRINCE_BLACK_DRAGON(6636, 0, 12653, 267),

		KALPHITE_PRINCESS(6637, 6638, 12654, 271),

		MORPHED_KALPHITE_PRINCESS(6638, 6637, 12654, 279),

		SMOKE_DEVIL(6639, 0, 12648, 288),

		KRAKEN(6640, 0, 12655, 291),

		PENANCE_PRINCESS(6642, 0, 12703, 296),

		OLMLET(7520, 0, 20851, 298),

		Skotos(425, 0, 21273, 298), // TODO

		HYDRA(8517, 8518, 22746, 2726) {
			@Override
			public int getDialogue(Player player) {
				int[] dialogueIds = new int[] { 2726, 2734, 2743 };
				return dialogueIds[Misc.getRandomInclusive(dialogueIds.length - 1)];
			}
		},
		HYDRA2(8518, 8519, 22748, 2728) {
			@Override
			public int getDialogue(Player player) {
				int[] dialogueIds = new int[] { 2728, 2736, 2747 };
				return dialogueIds[Misc.getRandomInclusive(dialogueIds.length - 1)];
			}
		},
		HYDRA3(8519, 8520, 22750, 2730) {
			@Override
			public int getDialogue(Player player) {
				int[] dialogueIds = new int[] { 2730, 2738, 2751 };
				return dialogueIds[Misc.getRandomInclusive(dialogueIds.length - 1)];
			}
		},
		HYDRA4(8520, 8517, 22752, 2732) {
			@Override
			public int getDialogue(Player player) {
				int[] dialogueIds = new int[] { 2732, 2740, 2755 };
				return dialogueIds[Misc.getRandomInclusive(dialogueIds.length - 1)];
			}
		},

		// SKILL PETS

		HERON(6715, 0, 13320, 2759, Skill.FISHING, 5000),

		BEAVER(6717, 0, 13322, 2764, Skill.WOODCUTTING, 5000),

		GREY_CHINCHOMPA(6719, 6720, 13324, 2761, Skill.HUNTER, 3000),

		RED_CHINCHOMPA(6718, 6719, 13323, 2762, Skill.HUNTER, 4000),

		BLACK_CHINCHOMPA(6720, 6718, 13325, 2763, Skill.HUNTER, 5000),

		ROCK_GOLEM(2182, 0, 13321, 2815, Skill.MINING, 5000),

		GIANT_SQUIRREL(7334, 0, 20659, 2804, Skill.AGILITY, 5000),

		TANGLEROOT(7335, 0, 20661, 2802, Skill.FARMING, 5000),

		ROCKY(7336, 0, 20663, 2799, Skill.THIEVING, 5000),

		PHOENIX(7368, 0, 20693, 2865, Skill.FIREMAKING, 5000),

		HERBI(7760, 0, 21509, 2873, Skill.HERBLORE, 5000),

		// RIFT GUARDIANS (SKILL PETS)
		FIRE_RIFT_GAURDIAN(7337, 7338, 20665, 2766),

		AIR_RIFT_GUARDIAN(7338, 7339, 20667, 2768, Skill.RUNECRAFTING, 8000),

		MIND_RIFT_GUARDIAN(7339, 7340, 20669, 2770),

		WATER_RIFT_GUARDIAN(7340, 7341, 20671, 2772),

		EARTH_RIFT_GUARDIAN(7341, 7342, 20673, 2774),

		BODY_RIFT_GUARDIAN(7342, 7343, 20675, 2776),

		COSMIC_RIFT_GUARDIAN(7343, 7344, 20677, 2778),

		CHAOS_RIFT_GUARDIAN(7344, 7345, 20679, 2780),

		NATURE_RIFT_GUARDIAN(7345, 7346, 20681, 2782),

		LAW_RIFT_GUARDIAN(7346, 7347, 20683, 2784),

		DEATH_RIFT_GUARDIAN(7347, 7348, 20685, 2786),

		SOUL_RIFT_GUARDIAN(7348, 7349, 20687, 2788),

		ASTRAL_RIFT_GUARDIAN(7349, 7350, 20689, 2790),

		BLOOD_RIFT_GUARDIAN(7350, 7337, 20691, 2792),

		BLACK_KNIGHT_TITAN(8100, 0, 15911, 2885),

		GLOD(8101, 0, 15912, 2887),

		LAVA_DRAGON(8102, 0, 15913, 2889),

		SLASH_BASH(8103, 0, 15914, 2891),

		MUTANT_TARN(8104, 0, 15915, 2893),

		THE_INADEQUACY(8105, 0, 15916, 2895),

		THE_UNTOUCHABLE(8106, 0, 15917, 2896),

		SKELETON_GUARD(8107, 0, 8131, 2897),
		
		HELL_CAT_KITTEN(5597, 0, 7583, 2701);

		public static final Set<Pet> SKILLING_PETS = ImmutableSet.of(HERON, BEAVER, GREY_CHINCHOMPA, RED_CHINCHOMPA,
				BLACK_CHINCHOMPA, ROCK_GOLEM, TANGLEROOT, GIANT_SQUIRREL, PHOENIX, HERBI, ROCKY );
		private int petId, morphId, itemId, dialogue;
		public Optional<Skill> skill = Optional.empty();
		private int chance;

		private Pet(int petNpcId, int morphId, int itemId, int dialogue) {
			this.petId = petNpcId;
			this.morphId = morphId;
			this.itemId = itemId;
			this.dialogue = dialogue;
			this.skill = Optional.of(Skill.CONSTRUCTION);
		}

		private Pet(int petNpcId, int morphId, int itemId, int dialogue, Skill skill, int chance) {
			this(petNpcId, morphId, itemId, dialogue);
			this.skill = Optional.of(skill);
			this.chance = chance;
			this.itemId = itemId;
			this.morphId = morphId;
			this.dialogue = dialogue;
		}

		public static Optional<Pet> getPet(int identifier) {
			return Arrays.stream(values()).filter(s -> s.petId == identifier).findFirst();
		}

		public static Optional<Pet> getPetForItem(int identifier) {
			return Arrays.stream(values()).filter(s -> s.itemId == identifier).findFirst();
		}

		public static Pet getPetForSkill(Skill identifier) {
			return Arrays.stream(values()).filter(s -> s.getSkill().get().equals(identifier)).findFirst().orElse(null);
		}

		public int getId() {
			return petId;
		}

		public int getMorphId() {
			return morphId;
		}

		public boolean canMorph() {
			return (morphId != 0);
		}

		public int getItemId() {
			return itemId;
		}

		public int getDialogue(Player player) {
			return dialogue;
		}

		public Optional<Skill> getSkill() {
			return skill;
		}

		public int getChance() {
			return chance;
		} // Unused

		public String getName() {
			String name = name().toLowerCase().replaceAll("_", " ");
			return Misc.capitalizeWords(name);
		}
	}
}
