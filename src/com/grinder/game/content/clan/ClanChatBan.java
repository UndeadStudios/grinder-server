package com.grinder.game.content.clan;

import com.google.gson.annotations.Expose;
import com.grinder.util.time.SecondsTimer;

public class ClanChatBan {

    @Expose private SecondsTimer timer;
    @Expose private String name;

    ClanChatBan(String name, int seconds) {
        this.setName(name);
        this.setTimer(new SecondsTimer(seconds));
    }

    public void setTimer(SecondsTimer timer) {
        this.timer = timer;
    }
    public void setName(String name) {
        this.name = name;
    }

    public SecondsTimer getTimer() {
        return timer;
    }
    public String getName() {
        return name;
    }

}
