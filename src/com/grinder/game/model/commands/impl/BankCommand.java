package com.grinder.game.model.commands.impl;

import com.grinder.game.content.minigame.castlewars.CastleWars;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.InstancedBossArea;
import com.grinder.game.model.areas.UntypedInstancedBossArea;
import com.grinder.game.model.areas.godwars.GodChamberArea;
import com.grinder.game.model.areas.godwars.NexChamber;
import com.grinder.game.model.areas.impl.DuelFightArena;
import com.grinder.game.model.areas.impl.KalphiteLair;
import com.grinder.game.model.areas.impl.PublicMinigameLobby;
import com.grinder.game.model.areas.instanced.*;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerStatus;
import net.runelite.cache.region.Region;

public class BankCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Opens up bank.";
	}

	@Override
	public void execute(Player player, String command, String[] parts) {
		//player.getBank(player.getCurrentBankTab()).open();


		if (player.getGameMode().isSpawn()) {
			if (AreaManager.inWilderness(player)) {
				player.getPacketSender().sendMessage("You can't use this command in the Wilderness!");
				return;
			}
			if (player.getWildernessLevel() > 0) {
				player.getPacketSender().sendMessage("You can't use this command in the Wilderness!");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof DuelFightArena || player.getDueling().inDuel()) {
				player.getPacketSender().sendMessage("You can't use this command in the Duel Arena!");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof CastleWars) {
				player.getPacketSender().sendMessage("You can't use this command in the Castle Wars Minigame!");
				return;
			}
			if (player.busy() || player.getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getStatus() == PlayerStatus.BANKING) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getStatus() == PlayerStatus.TRADING) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getStatus() == PlayerStatus.DUELING) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getStatus() == PlayerStatus.DICING) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getStatus() == PlayerStatus.SHOPPING) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.isInTutorial()) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getCombat().isInCombat()) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getCombat().isUnderAttack()) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getMinigame() != null) {
				player.getPacketSender().sendMessage("You can't use this command while playing Minigames!");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof FightCaveArea) {
				player.getPacketSender().sendMessage("You can't use this command in the Fight Caves!");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof AquaisNeigeArea) {
				player.getPacketSender().sendMessage("You can't use this command in the Aquais Neige!");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof PublicMinigameLobby) {
				player.getPacketSender().sendMessage("You can't use this command here!");
				return;
			}
			if (AreaManager.inWilderness(player) || player.getWildernessLevel() > 0) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			player.getBankpin().openBank();
			player.getPacketSender().sendMessage("@dre@Bank Opened!");
			return;
		}

		if (player.getRights() != PlayerRights.OWNER && player.getRights() != PlayerRights.DEVELOPER && !player.getGameMode().isSpawn()) {
			if (!PlayerUtil.isLegendaryMember(player)) {
				player.getPacketSender().sendMessage("<img=1026> You must be a Legendary member to use this command feature.");
				return;
			}
			if (player.getArea() instanceof UntypedInstancedBossArea || player.getArea() instanceof InstancedBossArea) {
				player.getPacketSender().sendMessage("You can't use this command within instances!", 1000);
				return;
			}
			if (AreaManager.inWilderness(player)) {
				player.getPacketSender().sendMessage("You can't use this command in the Wilderness!");
				return;
			}
			if (player.getWildernessLevel() > 0) {
				player.getPacketSender().sendMessage("You can't use this command in the Wilderness!");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof ZulrahShrine) {
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof KalphiteLair && player.getPosition().getZ() == 0) {
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof VorkathArea) {
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof GodChamberArea) {
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof CerberusArea) {
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(2240, 2302, 2563, 2622))) {
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(2885, 2908, 5255, 5277))) { // Zilyana
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof NexChamber) {
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(2963,3000,4368,4400))) { // Corp Area
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof DuelFightArena || player.getDueling().inDuel()) {
				player.getPacketSender().sendMessage("You can't use this command in the Duel Arena!");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof CastleWars) {
				player.getPacketSender().sendMessage("You can't use this command in the Castle Wars Minigame!");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(3135,3155,4640,4660))) { // Tarn
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(2846,2868,9626,9649))) { // SLASH BASH
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(2804,2897,9894,9983))) { // Ice queen
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(2886,2935,4429,4472))) { // Dagannoth
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(2804,2897,9894,9983))) { // Ice queen
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(1729,1804,5131,5235))) { // Giant mole
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(2614,2688,3980,4022))) { // Kamil
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(2690,2737,9157,9207))) { // Jungle demon
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(2451,2478,4762,4796))) { // Giant sea snake
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(2494,2511,3889,3905))) { // Sea troll queen
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(2240,2301,2562,2622))) { // Legendary boss zone
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(2550,2622,9476,9529))) { // BKT-Untouchable
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (AreaManager.inside(player.getPosition(), new Boundary(3451,3518,9474,9527))) { //KQ
				player.getPacketSender().sendMessage("You can't use this command in this area.");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof HydraArea) {
				player.getPacketSender().sendMessage("You can't use this command here!");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof GodChamberArea) {
				player.getPacketSender().sendMessage("You can't use this command here!");
				return;
			}
			if (player.busy() || player.getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getStatus() == PlayerStatus.BANKING) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getStatus() == PlayerStatus.TRADING) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getStatus() == PlayerStatus.DUELING) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getStatus() == PlayerStatus.DICING) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getStatus() == PlayerStatus.SHOPPING) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.isInTutorial()) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getCombat().isInCombat()) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getCombat().isUnderAttack()) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
			if (player.getMinigame() != null) {
				player.getPacketSender().sendMessage("You can't use this command while playing Minigames!");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof FightCaveArea) {
				player.getPacketSender().sendMessage("You can't use this command in the Fight Caves!");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof AquaisNeigeArea) {
				player.getPacketSender().sendMessage("You can't use this command in the Aquais Neige!");
				return;
			}
			if (player.getArea() != null && player.getArea() instanceof PublicMinigameLobby) {
				player.getPacketSender().sendMessage("You can't use this command here!");
				return;
			}
			if (AreaManager.inWilderness(player) || player.getWildernessLevel() > 0) {
				player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
				return;
			}
		}
		player.getBankpin().openBank();
		player.getPacketSender().sendMessage("@dre@Bank Opened!");
		//player.getPacketSender().sendMessage("You are " + player.isMember());
	}

	@Override
	public boolean canUse(Player player) {
		PlayerRights rights = player.getRights();
		return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.CO_OWNER || rights == PlayerRights.ADMINISTRATOR || rights == PlayerRights.GLOBAL_MODERATOR)
				|| player.getRights().equals(PlayerRights.LEGENDARY_MEMBER) || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 249 || player.getGameMode().isSpawn();
	}

}
