package com.grinder.game.content.clan;

import com.google.gson.annotations.Expose;
import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.util.Misc;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static com.grinder.game.content.clan.ClanChatAction.*;
import static com.grinder.game.content.clan.ClanChatConstants.*;

/**
 * @author Stan van der Bend (original author is unknown)
 */
public class ClanChat {

	@Expose private final int index;
	@Expose private String name;
	@Expose private String ownerName;
	@Expose private boolean lootSharingEnabled;

	@Expose private Map<ClanChatAction, ClanChatRank> actionRequirements = new HashMap<>();
	@Expose private List<ClanChatBan> bannedMembers = new ArrayList<>();
	@Expose private List<ClanMember> memberList = new ArrayList<>();

	private final AtomicBoolean isSaving = new AtomicBoolean(false);
	private boolean requiredUpdate;

	private Player owner;
	private List<Player> activePlayers = new CopyOnWriteArrayList<>();
	private boolean queuedForSaving;

	ClanChat(Player owner, String name, int index) {
		this.owner = owner;
		this.name = name;
		this.index = index;
		this.ownerName = owner.getUsername();
	}

	ClanChat(String ownerName, String name, int index) {
		this.owner = World.findPlayerByName(ownerName).orElse(null);
		this.ownerName = ownerName;
		this.name = name;
		this.index = index;
	}

	/**
	 * Initializes a new {@link ClanChat}.
	 *
	 * This function adds the owner {@link ClanMember} and
	 * sets the {@link ClanChatAction#KICK} rank requirement to {@link ClanChatRank#OWNER}.
	 */
	void init(){
		addMember(new ClanMember(ownerName, ClanChatRank.OWNER));
		setRequirement(KICK, ClanChatRank.OWNER);
		setRequirement(BAN, ClanChatRank.OWNER);
	}

	/**
	 * Adds a {@link ClanMember} to this {@link ClanChat#memberList}.
	 *
	 * @param member the {@link ClanMember} to add to the member list.
	 */
	void addMember(final ClanMember member){
		memberList.add(member);
	}

	/**
	 * Removes a {@link ClanMember} to this {@link ClanChat#memberList}.
	 *
	 * @param member the {@link ClanMember} to add to the member list.
	 */
	void removeMember(final ClanMember member){
		memberList.remove(member);
	}

	/**
	 * Adds an active {@link Player} to this {@link ClanChat#activePlayers}.
	 *
	 * @param player the {@link Player} to add to the active player list.
	 */
	void addActiveMember(final Player player) {
		activePlayers.add(player);
	}

	/**
	 * Removes an active {@link Player} to this {@link ClanChat#activePlayers}.
	 *
	 * @param player the {@link Player} to add to the active player list.
	 */
	void removeActiveMember(final Player player) {
		activePlayers.remove(player);
	}

	/**
	 * Sets the minimum required {@link ClanChatRank} to perform the specified {@link ClanChatAction}.
	 *
	 * @param action the {@link ClanChatAction} to set the minimum rank for.
	 * @param rank the {@link ClanChatRank} that represents the minimum rank.
	 */
	void setRequirement(final ClanChatAction action, final ClanChatRank rank){
		actionRequirements.put(action, rank);
	}

	void messageAll(final String message) {
		final String formattedMessage = BRACKET_COLOR + "[" + CLAN_NAME_COLOR + Misc.capitalizeWords(name) + BRACKET_COLOR + "]:" + CHAT_COLOR + " "+ message;
		activePlayers.forEach(player -> player.getPacketSender().sendSpecialMessage(name, 9, formattedMessage));
	}

	void message(final Player player, final String message) {
		final String formattedMessage = BRACKET_COLOR + "[" + CLAN_NAME_COLOR + Misc.capitalize(name) + BRACKET_COLOR + "]:" + CHAT_COLOR + " "+ message;
		player.getPacketSender().sendSpecialMessage(name, 9, formattedMessage);
	}

	void ban(final ClanMember clanMember){
		ban(clanMember.getName());
	}

	void ban(String name) {
		bannedMembers.add(new ClanChatBan(name, 3600));
	}

	void revokeBan(String name) {
		Iterator<ClanChatBan> it = bannedMembers.iterator();
		while (it.hasNext()) {
			ClanChatBan b = it.next();

			if (b == null) {
				it.remove();
				continue;
			}

			if (b.getName().equals(name)) {
				it.remove();
				return;
			}
		}
	}

	void kickIfNoPermissions(final ClanMember member){
		if(!hasPermission(ENTER, member.getRank())){
			member.ifOnline(activePlayers::remove);
			member.messageIfOnline("Your rank is not high enough to be in this channel.");
			memberList.remove(member);
		}
	}

	void validateAllMembersAccess(final Player initiator){
		final Iterator<ClanMember> iterator = memberList.iterator();
		while (iterator.hasNext()){
			final ClanMember member = iterator.next();
			if(!hasPermission(ENTER, member.getRank())){
				member.ifOnline(this::removeActiveMember);
				member.messageIfOnline("@red@Your rank is no longer sufficient to be in the clan '"+name+"'.");
				initiator.sendMessage("@red@Warning! Changing that setting kicked the player " + member.getName() + " from the chat due to insufficient rights");
				iterator.remove();
			}
		}
	}

	void toggleLootSharing(){
		lootSharingEnabled = !lootSharingEnabled;
		activePlayers.forEach(player -> player.getPacketSender().sendConfig(542, lootSharingEnabled ? 1 : 0));
	}

	boolean isLootSharingEnabled(){
		return lootSharingEnabled;
	}

	boolean isOwner(Player player) {
		return ownerName.equals(player.getUsername());
	}

	boolean isOwner(ClanMember clanMember) {
		return ownerName.equals(clanMember.getName());
	}

	boolean isBanned(ClanMember clanMember){
		return bannedMembers.stream().anyMatch(ban -> ban.getName().equals(clanMember.getName()));
	}

	boolean isBanned(String name) {
		bannedMembers.removeIf(clanChatBan -> clanChatBan.getTimer().finished());
		return bannedMembers.stream().anyMatch(ban -> ban.getName().equals(name));
	}

	public boolean contains(Player player) {
		return activePlayers.contains(player);
	}

	boolean isRequirementSetAs(final ClanChatAction action, final ClanChatRank rank){
		return actionRequirements.getOrDefault(action, ClanChatRank.ANYONE) == rank;
	}

	boolean hasPermission(final ClanChatAction action, final ClanChatRank rank){
		final ClanChatRank requiredRank = actionRequirements.getOrDefault(action, ClanChatRank.ANYONE);
		return rank.compareTo(requiredRank) >= 0;
	}

	Stream<Player> onlineMembers(){
		return activePlayers.stream();
	}

	List<ClanMember> members(){
		return memberList;
	}

	List<ClanChatBan> bannedMembers() {
		return bannedMembers;
	}

	List<ClanMember> rankedMembers(){

		final List<ClanMember> rankedMembers = new ArrayList<>();

		for(final ClanMember member : memberList){
			if(member.getRank() != ClanChatRank.ANYONE)
				rankedMembers.add(member);
		}

		return rankedMembers;
	}

	ClanChatRank getRank(Player player) {
		return findByName(player.getUsername()).map(ClanMember::getRank).orElse(ClanChatRank.ANYONE);
	}

	Optional<ClanChatRank> findRequirement(final ClanChatAction action){
		return Optional.ofNullable(actionRequirements.get(action));
	}

	Optional<ClanMember> findRankedAtIndex(final int index){
		if(index < 0)
			return Optional.empty();
		return Optional.ofNullable(rankedMembers().get(index));
	}
	Optional<ClanMember> findOnlineAtIndex(final int index){
		if(index < 0)
			return Optional.empty();
		return Optional.ofNullable(activePlayers.get(index)).flatMap(this::find);
	}
	Optional<ClanMember> find(final Player player){
		for(ClanMember member: members()){
			if(member.is(player))
				return Optional.of(member);
		}
		return Optional.empty();
	}

	public Optional<ClanMember> findByName(final String name){
		for(ClanMember member: members()){
			if(member.getName().equals(name))
				return Optional.of(member);
		}
		return Optional.empty();
	}

	void setOwnerPlayerInstance(Player owner) {
		this.owner = owner;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setQueuedForSaving(boolean queuedForSaving) {
		this.queuedForSaving = queuedForSaving;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public Player getOwner() {
		return owner;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public boolean isQueuedForSaving() {
		return queuedForSaving;
	}

	public List<Player> players() { return activePlayers; }

	public boolean isRequiredUpdate() {
		return requiredUpdate;
	}

	public void setRequiredUpdate(boolean requiredUpdate) {
		this.requiredUpdate = requiredUpdate;
	}

	public AtomicBoolean getIsSaving() {
		return isSaving;
	}

	@Override
	public String toString() {
		return "ClanChat{" +
				"name='" + name + '\'' +
				", ownerName='" + ownerName + '\'' +
				'}';
	}


}