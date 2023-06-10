package com.grinder.game.model.punishment;

import com.google.gson.annotations.Expose;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.util.DiscordBot;
import com.grinder.util.Misc;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.Date;
import java.util.Objects;

/**
 * Represents a punishment that can be applied to a specific player
 * or to some identifying element of a player (ip, mac, serial).
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-02
 */
public class Punishment {

    @Expose private final String initiatorName;
    @Expose private final String identifier;
    @Expose private final String targetName;

    @Expose private final PunishmentType punishmentType;

    @Expose private final boolean permanent;

    @Expose private final Date startDate;
    @Expose private Date endDate;

    @Expose private String reason;

    /**
     * Represents a punishment with no set end date, thus being effectively permanent.
     *
     * @param initiatorName the name of the {@link Player} instantiating the {@link Punishment}.
     * @param targetName the name of the {@link Player} receiving the {@link Punishment}.
     * @param identifier the identifier of the {@link PunishmentLevel} for the target.
     * @param startDate the {@link Date} of when this {@link Punishment} was created.
     * @param expireDate the {@link Date} of when this {@link Punishment} will end.
     * @param punishmentType the {@link PunishmentType} of this {@link Punishment}.
     */
    Punishment(String initiatorName, String targetName, String identifier, Date startDate, Date expireDate, PunishmentType punishmentType) {
        this.initiatorName = initiatorName;
        this.targetName = targetName;
        this.identifier = identifier;
        this.startDate = startDate;
        this.endDate = expireDate;
        this.punishmentType = punishmentType;
        permanent = false;
    }

    /**
     * Represents a punishment with no set end date, thus being effectively permanent.
     *
     * @param initiatorName the name of the {@link Player} instantiating the {@link Punishment}.
     * @param targetName the name of the {@link Player} receiving the {@link Punishment}.
     * @param identifier the name of the {@link Player} receiving the {@link Punishment}.
     * @param startDate the {@link Date} of when this {@link Punishment} was created.
     * @param punishmentType the {@link PunishmentType} of this {@link Punishment}.
     */
    Punishment(String initiatorName, String targetName, String identifier, Date startDate, PunishmentType punishmentType) {
        this.initiatorName = initiatorName;
        this.targetName = targetName;
        this.identifier = identifier;
        this.punishmentType = punishmentType;
        this.startDate = startDate;
        permanent = true;
    }

    /**
     * Creates a message to be send by the {@link DiscordBot}.
     *
     * @return a {@link Message} which can be send to a discord channel.
     */
    public Message toMessage(){

        final MessageBuilder messageBuilder = new MessageBuilder();

        messageBuilder
                .append("**Hash: **").append(String.valueOf(hashCode())).append("\n")
                .append("\t**Type: **").append(String.valueOf(punishmentType)).append("\n")
                .append("\t**Initiator: **").append(initiatorName).append("\n")
                .append("\t**Target: **").append(targetName).append("\n")
                .append("\t**Identifier: **").append(identifier).append("\n")
                .append("\t**Date: **").append(String.valueOf(startDate)).append("\n");

        messageBuilder.append("\t**Duration: **");

        if(permanent){
            messageBuilder.append("permanent");
        } else
            messageBuilder.append(" ").append(Misc.friendlyTimeDiff(endDate.getTime() - new Date().getTime()));

        messageBuilder.append("\n\n");

        return messageBuilder.build();
    }

    /**
     * Check whether this punishment is expired.
     *
     * @return {@code true} is the current {@link Date} is after the {@link #endDate}.
     *          {@code false} if otherwise or if {@link #permanent} is set to {@code true}.
     */
    boolean isExpired(){
        return !permanent && new Date().after(endDate);
    }

    String getIdentifier() {
        return identifier;
    }

    public PunishmentType getPunishmentType() {
        return punishmentType;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    @Override
    public int hashCode() {
        int result = initiatorName.hashCode();
        result = 31 * result + identifier.hashCode();
        result = 31 * result + targetName.hashCode();
        result = 31 * result + punishmentType.hashCode();
        result = 31 * result + (permanent ? 1 : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Punishment that = (Punishment) o;

        if (permanent != that.permanent) return false;
        if (!initiatorName.equals(that.initiatorName)) return false;
        if (!identifier.equals(that.identifier)) return false;
        if (!targetName.equals(that.targetName)) return false;
        if (punishmentType != that.punishmentType) return false;
        if (!Objects.equals(startDate, that.startDate)) return false;
        if (!Objects.equals(endDate, that.endDate)) return false;
        return Objects.equals(reason, that.reason);
    }
}
