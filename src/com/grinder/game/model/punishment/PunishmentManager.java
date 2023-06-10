package com.grinder.game.model.punishment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.grinder.game.World;
import com.grinder.game.entity.agent.player.*;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.service.ServiceManager;
import com.grinder.game.service.tasks.TaskRequest;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.DiscordBot;
import com.grinder.util.Logging;
import com.grinder.util.Misc;
import com.grinder.util.time.TimeUtil;
import kotlin.Unit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Represents a manager that can be used to submit, revoke,
 * and inform about {@link Punishment punishments}.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-02
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class PunishmentManager {

    private static final Logger LOGGER = LogManager.getLogger(PunishmentManager.class.getSimpleName());

    public static final Path PATH = Paths.get("data","saves", "punishments.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    private static final Type TYPE = new TypeToken<HashMap<PunishmentType, ArrayList<Punishment>>>(){}.getType();

    private static final HashMap<PunishmentType, ArrayList<Punishment>> PUNISHMENTS = new HashMap<>();

    static {

        // submits a task to remove expired punishments.
        TaskManager.submit(new Task(TimeUtil.MILLIS.toGameCycles(TimeUnit.SECONDS.toMillis(10L))) {
            @Override
            protected void execute() {
                ServiceManager.INSTANCE.getTaskService().addTaskRequest(new CheckExpiredPunishmentsTask());
            }
        });
    }

    /**
     * Revokes a {@link Punishment punishment} applied to some player.
     *
     * @param player        the {@link Player} revoking the punishment.
     * @param targetName    the name of the {@link Player} to revoke the punishment of.
     * @param type          the {@link PunishmentType} of the punishment to remove.
     */
    public static void revoke(final Player player, String targetName, final PunishmentType type){

        targetName = Misc.formatPlayerName(targetName.toLowerCase());

        if(!PUNISHMENTS.containsKey(type)){
            DialogueManager.sendStatement(player, "There are no punishments registered of type: "+type+"");
            player.sendMessage("There are no punishments registered of type: "+type+".");
            return;
        }

        final PunishmentLevel level = type.getPunishmentLevel();
        final Optional<Player> optionalTarget = World.findPlayerByName(targetName);

        String identifier = targetName;

        if(optionalTarget.isPresent()){

            final Player targetPlayer = optionalTarget.get();

            final Optional<String> optionalIdentifier = level.findPlayerKey(targetPlayer);

            if(optionalIdentifier.isEmpty()){
                DialogueManager.sendStatement(player, "Could not find the right identifier to revoke the punishment for player: @dre@"+targetName+"</col>");
                player.sendMessage("Could not find the right identifier to revoke the punishment for player: "+targetName+".");
                return;
            }

            identifier = optionalIdentifier.get();

            performImmediatePunishmentRevokeAction(targetPlayer, type);

        } else {
            if(!PlayerSaving.playerExists(targetName)){
                DialogueManager.sendStatement(player, "Could not find a player by the name of: @dre@"+targetName+"</col>");
                player.sendMessage("Could not find a player by the name of: "+targetName+".");
                return;
            }

            final Optional<String> optionalPlayerFileKey = level.findPlayerFileKey();

            if(optionalPlayerFileKey.isPresent()){

                final String key = optionalPlayerFileKey.get();

                final Optional<String> optionalIdentifier = PlayerLoading.getString(targetName, key);

                if(optionalIdentifier.isPresent()){

                    identifier = optionalIdentifier.get();

                    player.sendMessage("Attempting to revoke identifier: "+identifier+" for the player: "+targetName+".");
                } else {
                    player.sendMessage("Could not find the identifier in file for the player: "+targetName+".");
                    return;
                }
            } else if(level != PunishmentLevel.ACCOUNT_LEVEL){
                DialogueManager.sendStatement(player, "You can't revoke: "+level+" punishments on offline players!");
                player.sendMessage("You can't revoke: "+level+" punishments on offline players!");
                return;
            }

        }
        revoke(player, targetName, type, identifier);
    }

    /**
     * Revokes a {@link Punishment punishment} applied to some player.
     *
     * @param player        the {@link Player} revoking the punishment.
     * @param targetName    the name of the {@link Player} to revoke the punishment of.
     * @param type          the {@link PunishmentType} of the punishment to remove.
     * @param identifier    some identifier string relevant to the punishment to remove.
     */
    static void revoke(Player player, String targetName, PunishmentType type, String identifier) {

        final Optional<Punishment> optionalPunishment = Stream
                .concat(findPunishmentsForName(targetName).stream(), findPunishmentsForIdentifier(identifier).stream())
                .distinct()
                .filter(punishment -> punishment.getPunishmentType() == type)
                .findAny();

        if(optionalPunishment.isEmpty()) {
            DialogueManager.sendStatement(player, "The player: @dre@"+targetName+"</col> has no punishment of type: "+type+"");
            player.sendMessage("The player: "+targetName+" has no punishment of type: "+type+".");
            return;
        }

        final Punishment punishment = optionalPunishment.get();

        PUNISHMENTS.get(type).remove(punishment);

        save();

        DialogueManager.sendStatement(player, "You successfully revoked the punishment of type: "+type+" for player: @dre@"+targetName+"</col>");
        player.sendMessage("You successfully revoked the punishment of type: "+type+" for player: "+targetName+".");

        log(punishment, "["+punishment.hashCode()+"]: '"+player.getUsername()+"' revoked a punishment of type '" + type + "' for the player: '" + targetName + "'.");
    }

    /**
     * Submits {@link Punishment punishments} for each argued {@link PunishmentType}
     * to the argued target.
     *
     * @param targetName the name of the {@link Player} to apply the punishment to.
     * @param types      array of {@link PunishmentType types} of punishments to submit.
     */
    public static void submit(String targetName, final PunishmentType... types){
        for(final PunishmentType type : types)
            submit(targetName, type);
    }

    /**
     * Submits a {@link Punishment punishment} for the argued {@link PunishmentType}
     * to the argued target.
     *
     * @param targetName the name of the {@link Player} to apply the punishment to.
     * @param type       the {@link PunishmentType types} of punishment to submit.
     */
    public static void submit(String targetName, final PunishmentType type){

        targetName = Misc.formatPlayerName(targetName.toLowerCase());

        final PunishmentLevel level = type.getPunishmentLevel();
        final Optional<Player> optionalTarget = World.findPlayerByName(targetName);

        String identifier = targetName;

        if(optionalTarget.isPresent()){

            final Player targetPlayer = optionalTarget.get();

            if(type.onlyImmediateAction()){
                performImmediatePunishmentAction(targetPlayer, type);
                return;
            }

            final Optional<String> optionalIdentifier = level.findPlayerKey(targetPlayer);

            if(optionalIdentifier.isEmpty()){
                LOGGER.info("Could not find the right identifier to submit the punishment for player: '"+targetName+"'.");
                return;
            }

            identifier = optionalIdentifier.get();

            if(!level.isValid(identifier)){
                LOGGER.info("player '"+targetName+"' does not have a valid identifier '"+identifier+"'.");
                return;
            }

        } else {

            LOGGER.info("player '"+targetName+"' could not be found online.");

            if(!PlayerSaving.playerExists(targetName)){
                LOGGER.info("player '"+targetName+"' has no player file!");
                return;
            }

            if(level != PunishmentLevel.ACCOUNT_LEVEL){
                LOGGER.info("Cannot submit high level punishments on offline players!");
                return;
            }
        }

        if(findPunishmentsForName(identifier).stream().anyMatch(punishment -> punishment.getPunishmentType() == type)) {
            LOGGER.info("player '"+targetName+"' already has a punishment of type: '"+type+"'.");
            return;
        }

        final Punishment punishment = new Punishment("Server", targetName, identifier, new Date(), type);

        PUNISHMENTS.putIfAbsent(type, new ArrayList<>());
        PUNISHMENTS.get(type).add(punishment);

        LOGGER.info("Successfully added a permanent punishment of type '" + type + "' for player: '" + targetName + "'.");

        optionalTarget.ifPresent(targetPlayer -> performImmediatePunishmentAction(targetPlayer, type));

        log(punishment, "["+punishment.hashCode()+"]: 'Server' added a permanent punishment of type '" + type + "' for player: '" + targetName + "'.");

        save();
    }

    /**
     * Submits a {@link Punishment punishment} of the argued {@link PunishmentType}
     * to some target unless the target is in a state where they should not be punished.
     *
     * @param player     the {@link Player} revoking the punishment.
     * @param targetName the name of the {@link Player} to apply the punishment to.
     * @param type       the {@link PunishmentType types} of punishment to submit.
     */
    public static void submit(final Player player, String targetName, final PunishmentType type){

        targetName = Misc.formatPlayerName(targetName.toLowerCase());

        final PunishmentLevel level = type.getPunishmentLevel();
        final Optional<Player> optionalTarget = World.findPlayerByName(targetName);

        String identifier = targetName;

        if(optionalTarget.isPresent()){

            final Player targetPlayer = optionalTarget.get();

            if(cannotPunishPlayer(targetPlayer)){
                DialogueManager.sendStatement(player, "The player: @dre@"+targetName+"</col> cannot be punished!");
                player.sendMessage("The player: "+targetName+" cannot be punished!");
                return;
            }
            if (targetPlayer.isBlockingDisconnect()) {
                player.getPacketSender().sendMessage(targetPlayer.getUsername() + " is in a busy state and cannot be punished right now!");
                return;
            }
            if (targetPlayer.getStatus() == PlayerStatus.DUELING
            || targetPlayer.getStatus() == PlayerStatus.DICING
            || targetPlayer.getStatus() == PlayerStatus.TRADING) {
                player.getPacketSender().sendMessage(targetPlayer.getUsername() + " is in a busy state and cannot be punished right now!");
            }

            if(type.onlyImmediateAction()){

                if(type == PunishmentType.WARN)
                    PunishmentUtil.promptWarning(player, targetName, targetPlayer);
                else
                    performImmediatePunishmentAction(targetPlayer, type);

                return;
            }

            final Optional<String> optionalIdentifier = level.findPlayerKey(targetPlayer);

            if(optionalIdentifier.isEmpty()){
                DialogueManager.sendStatement(player, "Could not find the right identifier to submit the punishment for player @dre@"+targetName+"</col>");
                player.sendMessage("Could not find the right identifier to submit the punishment for player: "+targetName+".");
                return;
            }

            identifier = optionalIdentifier.get();

            if(!level.isValid(identifier)){
                DialogueManager.sendStatement(player, "The player: @dre@"+targetName+"</col> does not have a valid identifier: "+identifier+"");
                player.sendMessage("The player: "+targetName+" does not have a valid identifier: "+identifier+".");
                return;
            }

        } else {

            if(!PlayerSaving.playerExists(targetName)){
                DialogueManager.sendStatement(player, "Could not find a player by the name of: @dre@"+targetName+"</col>");
                player.sendMessage("Could not find a player by the name of: "+targetName+".");
                return;
            }
            DialogueManager.sendStatement(player, "The player: @dre@"+targetName+"</col> could not be found online.");
            player.sendMessage("The player: "+targetName+" could not be found online.");

            if(level != PunishmentLevel.ACCOUNT_LEVEL){
                DialogueManager.sendStatement(player, "You can't submit high level punishments on offline players!");
                player.sendMessage("You can't submit high level punishments on offline players!");
                return;
            }
        }

        if(findPunishmentsForName(identifier).stream().anyMatch(punishment -> punishment.getPunishmentType() == type)) {
            DialogueManager.sendStatement(player, "The player: @dre@"+targetName+"</col> already has a punishment of type: "+type+"");
            player.sendMessage("The player: "+targetName+" already has a punishment of type: "+type+".");
            return;
        }

        final String finalIdentifier = identifier;
        final String finalTargetName = targetName;

        if(type.askConfirmationIfTargetInWild()){

            if(optionalTarget.isPresent()) {

                startConfirmPunishmentDialogue(player, targetName, type, optionalTarget.get(), finalIdentifier, finalTargetName);
                return;
            }
        }

        startPunishmentDialogue(player, type, optionalTarget, finalIdentifier, finalTargetName);
    }

    /**
     * Submits a {@link Punishment punishment} of the argued {@link PunishmentType}
     * to the argued target no matter what they are currently doing.
     *
     * @param player     the {@link Player} revoking the punishment.
     * @param targetName the name of the {@link Player} to apply the punishment to.
     * @param type       the {@link PunishmentType types} of punishment to submit.
     */
    public static void submitForcedPunishment(final Player player, String targetName, final PunishmentType type){

        targetName = Misc.formatPlayerName(targetName.toLowerCase());

        final PunishmentLevel level = type.getPunishmentLevel();
        final Optional<Player> optionalTarget = World.findPlayerByName(targetName);

        String identifier = targetName;

        if(optionalTarget.isPresent()){

            final Player targetPlayer = optionalTarget.get();

            if(type.onlyImmediateAction()){

                if(type == PunishmentType.WARN)
                    PunishmentUtil.promptWarning(player, targetName, targetPlayer);
                else
                    performImmediatePunishmentAction(targetPlayer, type);

                return;
            }

            final Optional<String> optionalIdentifier = level.findPlayerKey(targetPlayer);

            if(optionalIdentifier.isEmpty()){
                DialogueManager.sendStatement(player, "Could not find the right identifier to submit the punishment for the player: @dre@"+targetName+"</col>");
                player.sendMessage("Could not find the right identifier to submit the punishment for the player: "+targetName+".");
                return;
            }

            identifier = optionalIdentifier.get();

            if(!level.isValid(identifier)){
                DialogueManager.sendStatement(player, "The player: @dre@"+targetName+"</col> does not have a valid identifier: "+identifier+"");
                player.sendMessage("The player: "+targetName+" does not have a valid identifier: "+identifier+".");
                return;
            }

        } else {

            if(!PlayerSaving.playerExists(targetName)){
                DialogueManager.sendStatement(player, "Could not find a player by the name of: @dre@"+targetName+"</col>");
                player.sendMessage("Could not find a player by the name of: "+targetName+".");
                return;
            }
            DialogueManager.sendStatement(player, "The player: @dre@"+targetName+"</col> could not be found online.");
            player.sendMessage("The player: "+targetName+" could not be found online.");

        }

        if(findPunishmentsForName(identifier).stream().anyMatch(punishment -> punishment.getPunishmentType() == type)) {
            DialogueManager.sendStatement(player, "The player: @dre@"+targetName+"</col> already has a punishment of type: "+type+"");
            player.sendMessage("The player: "+targetName+" already has a punishment of type: "+type+".");
            return;
        }

        final String finalIdentifier = identifier;
        final String finalTargetName = targetName;

        if(type.askConfirmationIfTargetInWild()){

            if(optionalTarget.isPresent()) {

                startConfirmPunishmentDialogue(player, targetName, type, optionalTarget.get(), finalIdentifier, finalTargetName);
                return;
            }
        }

        startPunishmentDialogue(player, type, optionalTarget, finalIdentifier, finalTargetName);
    }

    /**
     * Sends an {@link DialogueType#OPTION confirmation} dialogue for submitting a {@link Punishment}.
     *
     * @param player            the {@link Player} to send the dialogue to.
     * @param type              the {@link PunishmentType} to create a dialogue for.
     * @param finalIdentifier   the identifier relevant to the punishment.
     * @param finalTargetName   the name of the target to be punished.
     */
    private static void startConfirmPunishmentDialogue(Player player, String targetName, PunishmentType type, Player target, String finalIdentifier, String finalTargetName) {

        final boolean inCombat = target.getCombat().isInCombat();
        final PlayerStatus status = target.getStatus();

        new DialogueBuilder(DialogueType.STATEMENT)
                .setText(
                        "The player: '" + targetName + "' might be busy!",
                        "Status: @red@"+status,
                        "Combat: "+inCombat+" "+(inCombat ? target.getCombat().getTarget() : ""),
                        "Would you like to continue?"
                ).add(DialogueType.OPTION)
                    .setOptionTitle("Continue jailing '"+targetName+"'?")
                    .firstOption("Yes", futurePlayer -> startPunishmentDialogue(futurePlayer, type, Optional.of(target), finalIdentifier, finalTargetName))
                    .addCancel()
                .start(player);
    }

    /**
     * Sends an {@link DialogueType#OPTION option} dialogue for creating a {@link Punishment}.
     *
     * @param player            the {@link Player} to send the dialogue to.
     * @param type              the {@link PunishmentType} to create a dialogue for.
     * @param optionalTarget    an {@link Optional<Player>} that may contain a logged in target.
     * @param finalIdentifier   the identifier relevant to the punishment.
     * @param finalTargetName   the name of the target to be punished.
     */
    private static void startPunishmentDialogue(Player player, PunishmentType type, Optional<Player> optionalTarget, String finalIdentifier, String finalTargetName) {
        final DialogueBuilder builder = new DialogueBuilder(DialogueType.OPTION);

        builder.setOptionTitle("Select a duration")
                .firstOption("Permanent", futurePlayer -> performPermanentPunishment(futurePlayer, finalIdentifier, finalTargetName, type, optionalTarget))
                .secondOption("Duration",
                        new DialogueBuilder(DialogueType.OPTION)
                                .setOptionTitle("Select time unit")
                                .firstOption("Minutes", futurePlayer -> performPunishment(futurePlayer, finalIdentifier, finalTargetName, type, TimeUnit.MINUTES, optionalTarget))
                                .secondOption("Hours", futurePlayer -> performPunishment(futurePlayer, finalIdentifier, finalTargetName, type, TimeUnit.HOURS, optionalTarget))
                                .thirdOption("Days", futurePlayer -> performPunishment(futurePlayer, finalIdentifier, finalTargetName, type, TimeUnit.DAYS, optionalTarget))
                                .addCancel()::start
                ).addCancel()
                .start(player);
    }

    /**
     * Loads all {@link Punishment punishements} to this {@link #PATH}.
     */
    public static void save(){
        ServiceManager.INSTANCE.getTaskService().addTaskRequest(new TaskRequest(() -> {

            final File file = PATH.toFile();

            if(!file.exists()){
                LOGGER.info("save -> did not find file '"+PATH+"', creating...");
                try {
                    if(file.createNewFile()){
                        LOGGER.info("save -> created file at '"+PATH+"'");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }

            try {

                final FileWriter writer = new FileWriter(file);

                GSON.toJson(PUNISHMENTS, writer);

                writer.flush();
                writer.close();

            } catch (IOException e) {
                LOGGER.error( "[PunishmentManager]: could not save the punishments to file '"+PATH+"'.", e);
            }
        }, true));
    }

    /**
     * Loads all {@link Punishment punishements} from this {@link #PATH}.
     */
    public static void load(){

        final File file = PATH.toFile();

        if(!file.exists()){
            LOGGER.info("Did not find file at '"+PATH+"', nothing to load :)");
            return;
        }

        try {

            final FileReader reader = new FileReader(file);

            HashMap<PunishmentType, ArrayList<Punishment>> newPunishments = GSON.fromJson(reader, TYPE);

            if(newPunishments == null)
                newPunishments = new HashMap<>();

            for(PunishmentType type : PunishmentType.values()){
                final ArrayList<Punishment> newList = newPunishments.getOrDefault(type, new ArrayList<>());
                final ArrayList<Punishment> oldList = PUNISHMENTS.getOrDefault(type, new ArrayList<>());
                for(Punishment old : oldList){
                    if(!newList.contains(old)){
                        World.findPlayerByName(old.getTargetName()).ifPresent(player -> performImmediatePunishmentRevokeAction(player, old.getPunishmentType()));
                        log(old, "["+old.hashCode()+"]: 'SERVER' automatically removed expired punishment of type '" + old.getPunishmentType() + "' for player '" + old.getTargetName() + "'.");
                    }
                }
            }

            PUNISHMENTS.clear();
            PUNISHMENTS.putAll(newPunishments);

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Find a {@link Punishment} of which the {@link Punishment#hashCode()}
     * equals the argued hash.
     *
     * @param hash the hash code to look for.
     * @return an {@link Optional<Punishment>} that will contain a {@link Punishment} if found.
     */
    public static Optional<Punishment> findByHash(final long hash){

        for(final ArrayList<Punishment> punishmentArrayList : PUNISHMENTS.values()){

            for(final Punishment punishment : punishmentArrayList){

                if(punishment.hashCode() == hash)
                    return Optional.of(punishment);
            }
        }
        return Optional.empty();
    }

    /**
     * Create a list of {@link Punishment punishments} where the {@link Punishment#getTargetName()}
     * equals the argued name.
     *
     * @param name the target name to look for.
     * @return a {@link List<Punishment>} applied to the target name.
      */
    public static List<Punishment> findPunishmentsForName(String name){

        name = Misc.formatName(name.toLowerCase());

        final ArrayList<Punishment> matches = new ArrayList<>();

        for (final ArrayList<Punishment> punishments : PUNISHMENTS.values()){

            for(final Punishment punishment : punishments){

                if(punishment.getTargetName().equals(name))
                    matches.add(punishment);
            }
        }
        return matches;
    }

    /**
     * Create a list of {@link Punishment punishments} where the {@link Punishment#getIdentifier()} ()}
     * equals the argued identifier.
     *
     * @param identifier the identifier to look for.
     * @return a {@link List<Punishment>} targeting the identifier.
     */
    public static List<Punishment> findPunishmentsForIdentifier(String identifier){

        final ArrayList<Punishment> matches = new ArrayList<>();

        for (final ArrayList<Punishment> punishments : PUNISHMENTS.values()){

            for(final Punishment punishment : punishments){

                if(punishment.getIdentifier().equals(identifier))
                    matches.add(punishment);
            }
        }
        return matches;
    }

    /**
     * Create a list of {@link Punishment punishments} applied to the argued {@link Player}.
     *
     * @param player the {@link Player} to look for.
     * @return a {@link List<Punishment>} applied to the player.
     */
    public static List<Punishment> findPunishments(final Player player){

        final ArrayList<Punishment> playerPunishments = new ArrayList<>();

        for(final ArrayList<Punishment> typedPunishments : PUNISHMENTS.values()){

            for(final Punishment punishment : typedPunishments){

                final PunishmentType type = punishment.getPunishmentType();
                final PunishmentLevel level = type.getPunishmentLevel();

                if (punishment.getPunishmentType() == null) {
                    System.out.println("Punishment type is null, please report!");
                    continue;
                }

                if(level.findPlayerKey(player)
                        .filter(playerKey -> playerKey.equals(punishment.getIdentifier()))
                        .isPresent()){
                    playerPunishments.add(punishment);
                }
            }

        }
        return playerPunishments;
    }

    /**
     * Executes the effect of a punishment targeting some {@link Player}.
     *
     * @param targetPlayer  the {@link Player} to apply the effect to.
     * @param type          the {@link PunishmentType} to apply the effect of.
     */
    private static void performImmediatePunishmentAction(final Player targetPlayer, final PunishmentType type) {

        if(type == PunishmentType.JAIL)
            PunishmentUtil.jailTarget(targetPlayer);

        if(type.isMutePlayer()) {
            targetPlayer.setMuted(true);
            DialogueManager.sendStatement(targetPlayer, "You have been muted!");
            targetPlayer.sendMessage("You have been muted!");
        }

        if(type.isDisconnectPlayer())
            targetPlayer.logout(LogoutPolicy.IMMEDIATE);
    }

    /**
     * Revokes the effect of a punishment for some {@link Player}.
     *
     * @param targetPlayer  the {@link Player} to apply the effect to.
     * @param type          the {@link PunishmentType} to apply the effect of.
     */
    private static void performImmediatePunishmentRevokeAction(final Player targetPlayer, final PunishmentType type) {

        if(type == PunishmentType.JAIL)
            PunishmentUtil.unjailTarget(targetPlayer);

        if(type.isMutePlayer()) {
            targetPlayer.setMuted(false);
            DialogueManager.sendStatement(targetPlayer, "Your mute has been lifted!");
            targetPlayer.sendMessage("Your mute has been lifted!");
        }
    }

    /**
     * Creates a {@link Punishment} that has no expiration date.
     *
     * @param player            the {@link Player} performing the punishment.
     * @param identifier        the identifier used by the punishment.
     * @param targetName        the name of the player targeted by the punishment.
     * @param type              the {@link PunishmentType} of the punishment.
     * @param optionalTarget    the target as an {@link Optional<Player>} (is empty if offline).
     */
    private static void performPermanentPunishment(final Player player, final String identifier, final String targetName, final PunishmentType type, final Optional<Player> optionalTarget) {
        final Punishment punishment = new Punishment(player.getUsername(), targetName, identifier, new Date(), type);

        PUNISHMENTS.putIfAbsent(type, new ArrayList<>());
        PUNISHMENTS.get(type).add(punishment);

        DialogueManager.sendStatement(player, "You successfully added a permanent punishment of type: " + type + " for the player: @dre@" + targetName + "</col>");
        player.sendMessage("You successfully added a permanent punishment of type: " + type + " for the player: " + targetName + ".");
        player.getPacketSender().sendInterfaceRemoval();

        optionalTarget.ifPresent(targetPlayer -> performImmediatePunishmentAction(targetPlayer, type));

        log(punishment, "[" + punishment.hashCode() + "]: '" + player.getUsername() + "' added a permanent punishment of type '" + type + "' for player: '" + targetName + "'.");

        save();
    }

    /**
     * Creates a {@link Punishment} and prompts player to set an expiration date.
     *
     * @param player            the {@link Player} performing the punishment.
     * @param identifier        the identifier used by the punishment.
     * @param targetName        the name of the player targeted by the punishment.
     * @param type              the {@link PunishmentType} of the punishment.
     * @param optionalTarget    the target as an {@link Optional<Player>} (is empty if offline).
     */
    private static void performPunishment(final Player player, final String identifier, final String targetName, final PunishmentType type, final TimeUnit timeUnit, final Optional<Player> optionalTarget){

        player.requestInput(Integer.class, "Enter how many "+timeUnit+"", (input) -> {
            final Calendar calendar = Calendar.getInstance();

            final Date start = calendar.getTime();

            int field = Calendar.DAY_OF_MONTH;

            if(timeUnit == TimeUnit.HOURS)
                field = Calendar.HOUR;
            else if(timeUnit == TimeUnit.MINUTES)
                field = Calendar.MINUTE;

            calendar.add(field, input);

            final Date expire = calendar.getTime();

            final Punishment punishment = new Punishment(player.getUsername(), targetName, identifier, start, expire, type);

            PUNISHMENTS.putIfAbsent(type, new ArrayList<>());
            PUNISHMENTS.get(type).add(punishment);

            DialogueManager.sendStatement(player, "You successfully added a " + input + " "+timeUnit+" punishment of type: " + type + " for the player: @dre@" + targetName + "</col>");
            player.sendMessage("You successfully added a " + input + " "+timeUnit+" punishment of type: " + type + " for the player: " + targetName + ".");
            player.getPacketSender().sendInterfaceRemoval();

            optionalTarget.ifPresent(targetPlayer -> performImmediatePunishmentAction(targetPlayer, type));

            log(punishment, "["+punishment.hashCode()+"]: '"+player.getUsername()+"' added a "+input+" "+timeUnit+" punishment of type '" + type + "' for player: '" + targetName + "'.");

            save();
        });
    }

    /**
     * Logs the argued {@link Punishment} with an additional message.
     *
     * @param punishment    the {@link Punishment} to log.
     * @param message       a message included in the log.
     */
    private static void log(Punishment punishment, String message) {

        final PunishmentType type = punishment.getPunishmentType();

        if(type.writeLogs()) {
            final String action = message.contains("revoked") ? "revoked" : "submitted";
            Logging.log(type.getLogFolderName(), punishment.getInitiatorName()
                    + " has " + action+ " a "+type+" punishment on "+type.getPunishmentLevel()+" for "+punishment.getTargetName()
                    +(type.getPunishmentLevel() == PunishmentLevel.ACCOUNT_LEVEL ? "" : " ["+punishment.getIdentifier()+"]"));
        }

        LOGGER.info(message);

        if(DiscordBot.ENABLED)
            DiscordBot.INSTANCE.sendModMessage(message);
    }

    /**
     * Check whether the argued {@link Player} can be punished.
     *
     * @param player the {@link Player} to check.
     * @return {@code true} if the player can be punished,
     *          {@code false} if not.
     */
    private static boolean cannotPunishPlayer(final Player player){
        return player.getRights().isStaff() && player.getRights().anyMatch(PlayerRights.ADMINISTRATOR, PlayerRights.DEVELOPER, PlayerRights.CO_OWNER, PlayerRights.OWNER);
    }

    /**
     * Sums the total amount of {@link Punishment punishments}.
     *
     * @return an {@link Integer} value representing the sum of all punishments.
     */
    public static int count() {
        return PUNISHMENTS.values().stream().mapToInt(ArrayList::size).sum();
    }

    private static class CheckExpiredPunishmentsTask extends TaskRequest {

        public CheckExpiredPunishmentsTask() {
            super(() -> {

                final HashMap<String, ArrayList<Punishment>> expiredPunishments = new HashMap<>();

                for(ArrayList<Punishment> punishments : PUNISHMENTS.values()){

                    final Iterator<Punishment> punishmentIterator = punishments.iterator();

                    while (punishmentIterator.hasNext()) {

                        final Punishment punishment = punishmentIterator.next();

                        if(punishment.isExpired()){

                            expiredPunishments.putIfAbsent(punishment.getTargetName(), new ArrayList<>());
                            expiredPunishments.get(punishment.getTargetName()).add(punishment);

                            punishmentIterator.remove();
                        }
                    }
                }

                World.submitGameThreadJob(() -> {
                    expiredPunishments.forEach((username, punishments) -> {
                        World.findPlayerByName(username).ifPresent(player ->
                                punishments.forEach(punishment ->
                                        performImmediatePunishmentRevokeAction(player, punishment.getPunishmentType())));
                        save();
                        punishments.forEach(punishment ->
                                log(punishment, "["+punishment.hashCode()+"]: 'SERVER' automatically removed expired punishment of type '"
                                        + punishment.getPunishmentType() + "' for player '"
                                        + punishment.getTargetName() + "'.")
                        );
                    });
                    return Unit.INSTANCE;
                });
            }, true);
        }

        @Override
        public String toString() {
            return "Check Expired Punishments Task";
        }
    }
}
