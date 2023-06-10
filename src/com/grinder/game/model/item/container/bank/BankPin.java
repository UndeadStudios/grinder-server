package com.grinder.game.model.item.container.bank;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.item.container.bank.presets.PresetsManager;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.game.model.sound.Sounds;
import com.grinder.util.Logging;
import com.grinder.util.Misc;

import java.util.concurrent.TimeUnit;

/**
 * Handle players bank pin interface and authentication.
 *
 * @author Unknown
 */
public class BankPin {

	private Player player;

	private PostPinValidation postPinInput;
	private Runnable callback;
	private String inputNumbers = "";

	private int[] pinDigits = new int[10];
	private long resetRequestDate;
	private boolean resetingPin = false;

	public boolean pinEntered;

	public enum PostPinValidation {
		OPEN_BANK,
		OPEN_COLLECTS,
		SET_PIN,
		PIN_CONFIGURATION,
		CALL_BACK,
		OPEN_PRESETS
	}

	public BankPin(Player player) {
		this.player = player;
	}

	/**
	 * Request bank pin
	 */
	public void requestPin(Runnable callback) {
		this.postPinInput = PostPinValidation.CALL_BACK;
		this.callback = callback;
		openInterface();
	}

	/**
	 * Attempt to open player bank, Bank PIN
	 * may be requested in case necessary.
	 */
	public void openBank() {
		if (requireBankPin()) {
			postPinInput = PostPinValidation.OPEN_BANK;
			openInterface();
		} else {
			player.getBank(player.getCurrentBankTab()).open();
		}
	}
	/**
	 * Attempt to open player bank presets, Bank PIN
	 * may be requested in case necessary.
	 */
	public void openBankPresets() {
		if (requireBankPin()) {
			postPinInput = PostPinValidation.OPEN_PRESETS;
			openInterface();
		} else {
			PresetsManager.ShowInterface(player);
		}
	}
	/**
	 * Submit a bank PIN creation.
	 */
	public void openBankPinCreation() {
		if (player.getGameMode().isUltimate()) {
			player.getPacketSender().sendMessage("You can't setup a bank PIN as Hardcore Iron Man.", 1000);
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		if (!resetingPin && player.pin() != -1) {
			player.getPacketSender().sendMessage("<img=750> @red@You already have a bank PIN.");
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		postPinInput = PostPinValidation.SET_PIN;
		openInterface();
	}

	public void close() {
		inputNumbers = "";
		player.getPacketSender().sendInterfaceRemoval();
	}

	/**
	 * Open PIN settings interface.
	 */
	public void openPinSettings() {
		inputNumbers = "";
		postPinInput = PostPinValidation.PIN_CONFIGURATION;
		openInterface();

	}
	/**
	 * Request bank pin again
	 */
	public void requestPinAgain() {
		openInterface();
	}
	/**
	 * Handle PIN Digit interface button action.
	 * 
	 * @param actionButtonID: Button Child ID
	 */
	public boolean handleDigitButton(int actionButtonID) {
        if (!player.getClickDelay().elapsed(BankConstants.PIN_ENTER_DELAY)) {
        	return false;
        }
		if (actionButtonID == 14921 && !player.isAccountFlagged()) {
			player.getPacketSender().sendMessage("<img=750> @red@You must speak to the Security Guard in Edgeville to reset your bank PIN!", 1500);
			player.getPacketSender().sendInterfaceRemoval();
			return false;
		}
		if (actionButtonID == 14921) {
			player.getPacketSender().sendMessage("<img=750> @red@You must enter your bank PIN before trying to do any other action.", 1500);
			return false;
		}
		if (actionButtonID == 14922 && !player.isAccountFlagged()) {
			player.getPacketSender().sendInterfaceRemoval();
			return false;
		}
		if (actionButtonID == 14922) {
			player.getPacketSender().sendMessage("<img=750> @red@You must enter your bank PIN before trying to do any other action.", 1500);
			return false;
		}
		if (actionButtonID >= 14873 && actionButtonID <= 14882) {

			if (!(player.getInterfaceId() == BankConstants.BANK_PIN_INTERFACE)) {
				return false;
			}
			
			// Send sound
			player.getPacketSender().sendSound(Sounds.PIN_DIGIT_INPUT);
	
			int digitButtonID = 9 - (14882 - actionButtonID);
			int digitNumber = pinDigits[digitButtonID];
			if (inputNumbers.length() < 4)
				inputNumbers += digitNumber;
			
			if (inputNumbers.length() == 4) {
				updateInterfaceTexts();
				finishInput();
			} else {
				updateInterfaceTexts();
			}
		}
		
        // Reset time click delay
        player.getClickDelay().reset();
		return false;
	}

	/**
	 * Process post PIN enter action.
	 */
	private void finishInput() {
		if (postPinInput == PostPinValidation.OPEN_BANK || postPinInput == PostPinValidation.OPEN_COLLECTS ||  postPinInput == PostPinValidation.OPEN_PRESETS || postPinInput == PostPinValidation.PIN_CONFIGURATION) {
			boolean validPin = checkEnteredPin();
			if (validPin) {
				pinEntered = true;
				player.getPacketSender().sendSound(Sounds.BANK_PIN_SUCESSFULLY);
				player.getPacketSender().sendMessage("<img=750> You have correctly entered your PIN.");
				player.setFailedBankPinTries(0);
				player.setAccountFlagged(false);
				if (!player.getRecentIPS().contains(player.getHostAddress())) {
					player.getRecentIPS().add(player.getHostAddress());
					if (player.getRecentIPS().size() >= 5)
						player.getRecentIPS().remove(0);
				}
				if (postPinInput == PostPinValidation.OPEN_BANK) {
					openBank();
				} else if (postPinInput == PostPinValidation.OPEN_PRESETS) {
					openBankPresets();
				} else {
					removePin();
					pinEntered = false;
					player.getPacketSender().sendInterfaceRemoval();
				}
			} else {
				player.getPacketSender().sendMessage("<img=750> @red@You have entered the wrong PIN.");
				player.getPacketSender().sendSound(Sounds.BANK_PIN_WRONG);
				player.getPacketSender().sendInterfaceRemoval();
				player.setFailedBankPinTries(player.getFailedBankPinTries() + 1);
				player.getPacketSender().sendMessage("You have submitted " + player.getFailedBankPinTries() + "/10 incorrect bank PIN tries!");
				if (player.getFailedBankPinTries() == 7) {
					player.getPacketSender().sendMessage("@red@You have 3 more incorrect bank PIN tries before your account gets locked.");
				}
				if (player.getFailedBankPinTries() == 10) {
					PunishmentManager.submit(player.getUsername(), PunishmentType.LOCK);
					BankUtil.logAccountLock(player.getUsername());
				}
			}
		} else if (postPinInput == PostPinValidation.SET_PIN) {
			int enteredPin = Integer.parseInt(inputNumbers);

			// Disable easy Bank PINs
			if (enteredPin == 0000 || enteredPin == 1234 || enteredPin == 9999 || enteredPin == 1111
			|| enteredPin == 2222 || enteredPin == 3333 || enteredPin == 4444 || enteredPin == 5555 || enteredPin == 6666
			|| enteredPin == 7777 || enteredPin == 8888) {
				player.getPacketSender().sendMessage("<img=750> @red@Please choose a harder to guess Bank PIN.");
				player.getPacketSender().sendSound(Sounds.BANK_PIN_WRONG);
				postPinInput = PostPinValidation.SET_PIN;
				requestPinAgain();
				return;
			}

			int pinHash = BankUtil.hashPin(enteredPin);
			player.setPin(pinHash);
			pinEntered = true;
			player.getPacketSender().sendMessage("<img=750> @red@You successfully set your bank PIN to '" + inputNumbers + "'.");
			Logging.log("setPins", "" + player.getUsername() + " created bank pin from the IP " + player.getHostAddress() + " and MAC: " + player.getMacAddress() + " BankPin: " + inputNumbers +"");
			inputNumbers = "";
			player.getPacketSender().sendSound(Sounds.BANK_PIN_SUCCESSFULLY_OPENS);
			player.getPacketSender().sendInterfaceRemoval();
			AchievementManager.processFor(AchievementType.SELF_SECURE, player);
		} else if (postPinInput == PostPinValidation.CALL_BACK) {
			if (callback != null) {
				callback.run();
			}
		}
		inputNumbers = "";
	}

	/**
	 * Check if entered pin is valid.
	 */
	public boolean checkEnteredPin() {
		int enteredPin = Integer.parseInt(inputNumbers);
		int pinHash = BankUtil.hashPin(enteredPin);
		return pinHash == player.pin();
	}



	/**
	 * Check whether it should require a pin or not.
	 * 
	 * @return true in case PIN input is required.
	 */
	public boolean requireBankPin() {
		if(player.pin == -1)
			return false;
		return !pinEntered;
	}

	/**
	 * Set current interface title.
	 * 
	 * @param title: Title name of current interface.
	 */
	private void setTitle(String title) {
		player.getPacketSender().sendString(title, BankConstants.TEXT_TITLE);
	}

	/**
	 * Submit a PIN reset.
	 */
	public void resetPin() {
		if (pinEntered && player.pin() != -1) {
			resetingPin = true;
			player.getBankpin().openBankPinCreation();
		} else {
			if (player.pin() == -1) {
				DialogueManager.sendStatement(player, "<img=750> @red@You don't have a bank PIN to reset.");
			} else if (!pinEntered) {
				player.getPacketSender().sendMessage("<img=750> @red@You must have your bank PIN entered first!");
				postPinInput = PostPinValidation.PIN_CONFIGURATION;
				openInterface();
			}
		}
	}
	
	/**
	 * Remove a PIN.
	 */
	public void removePin() {
		if (pinEntered && player.pin() != -1) {
			player.sendMessage("<img=750> @red@Your pin has been removed.");
			player.setPin(-1);
			player.getPacketSender().sendInterfaceRemoval();
			Logging.log("removePins", "" + player.getUsername() + " has removed his bank pin from the IP " + player.getHostAddress() + " and MAC: " + player.getMacAddress() + "");
		} else {
			if (player.pin() == -1) {
				DialogueManager.sendStatement(player, "<img=750> @red@You don't have a bank PIN to reset.");
			} else if (!pinEntered) {
				player.getPacketSender().sendMessage("<img=750> @red@You must have your bank PIN entered first before trying to reset it!");
				postPinInput = PostPinValidation.PIN_CONFIGURATION;
				openInterface();
			}
		}
		//player.getPacketSender().sendInterfaceRemoval();
	}

	/**
	 * Get time elapsed since Pin reset request time.
	 * 
	 * @return Time elapsed
	 */
	private int getRequestElapsedTime() {
		return (int) TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - resetRequestDate);
	}

	/**
	 * Open bank PIN interface.
	 */
	private void openInterface() {
		inputNumbers = "";
		updateTitleText();
		updateInterfaceTexts();
		updatePinRemovalText();
		player.getPacketSender().sendInterface(BankConstants.BANK_PIN_INTERFACE);
	}

	/**
	 * Update title text
	 */
	private void updateTitleText() {
		if (postPinInput == PostPinValidation.SET_PIN) {
			setTitle("@red@Set Pin");
		} else if (resetRequestDate != 0) {
			int timeLeft = BankConstants.PIN_RESET_DELAY - getRequestElapsedTime();
			setTitle("@yel@Bank Pin (" + (timeLeft <= 0 ? "You're able to reset your pin already." : "You will be able to reset your pin in " + timeLeft + " hours.") + ")");
		} else {
			setTitle("@yel@Bank Pin");
		}
	}

	/**
	 * Update PIN removal button text.
	 */
	private void updatePinRemovalText() {

		String bankPinMessage;

		if (player.pin() == -1)
			bankPinMessage = "";
		else {
			if (resetRequestDate != 0) {
				if (getRequestElapsedTime() >= BankConstants.PIN_RESET_DELAY) {
					bankPinMessage = "Reset Pin.";
				} else {
					bankPinMessage = "Cancel Pin reset.";
				}
			} else {
				bankPinMessage = "Request Pin reset.";
			}
		}
		player.getPacketSender().sendString(bankPinMessage, BankConstants.TEXT_REMOVE_PIN);
	}

	/**
	 * Update Step Guide text
	 */
	private void updateGuideText() {
		int totalInput = inputNumbers.length();
		if (totalInput != 4) {
			String step = BankConstants.BANK_PIN_INSTRUCTIONS[totalInput];
			player.getPacketSender().sendString(step, BankConstants.TEXT_CURRENT_STEP);
		}
	}

	/**
	 * Update current progress digits.
	 */
	private void updateDigitProgress() {
		int totalInput = inputNumbers.length();
		if (totalInput > 4) {
			totalInput = 4;
		}
		for (int i = 0; i < BankConstants.TEXT_PROGRESS_TRACKERS.length; i++) {
			int childID = BankConstants.TEXT_PROGRESS_TRACKERS[i];
			if (postPinInput == PostPinValidation.SET_PIN) {
				player.getPacketSender().sendString(i >= totalInput ? "?" : String.valueOf(inputNumbers.charAt(i)), childID);
			} else {
				player.getPacketSender().sendString((i >= totalInput ? "?" : "*"), childID);
			}
		}
	}

	/**
	 * Update the interface buttons texts according
	 * their digit number.
	 */
	private void updateInterfaceDigits() {
		for (int i = 0; i < BankConstants.TEXT_DIGIT_NUMBERS.length; i++) {
			int childID = BankConstants.TEXT_DIGIT_NUMBERS[i];
			int digitID = pinDigits[i];
			player.getPacketSender().sendString(String.valueOf(digitID), childID);
		}
	}

	/**
	 * Generate random order of digit numbers *
	 */
	private void randomizeDigits() {
		for (int i = 0; i < pinDigits.length; i++) {
			pinDigits[i] = i;
		}
		Misc.shuffleArray(pinDigits);
	}

	/**
	 * Update all Interface Texts
	 */
	private void updateInterfaceTexts() {
		randomizeDigits();
		updateInterfaceDigits();
		updateDigitProgress();
		updateGuideText();
	}


}
