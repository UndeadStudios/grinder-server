package com.grinder.util.discord;

public enum DiscordChannelType {

    PUNISHMENT_BOT(577571983013969920L),
    DROPS_CHANNEL(946347133299216414L),
    SERVER_LOGS_CHANNEL(946347215377547294L),
    VOTE_LOGS_CHANNEL(951850656342376498L),
    CHAT(358664434324865025L),
    ;

    private final long channelId;

    DiscordChannelType(long channelId) {
        this.channelId = channelId;
    }

    public long getChannelId() {
        return channelId;
    }
}
