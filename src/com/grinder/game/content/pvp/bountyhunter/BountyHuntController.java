package com.grinder.game.content.pvp.bountyhunter;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.util.time.SecondsTimer;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-12
 */
public class BountyHuntController {

    private final List<String> recentKills = new ArrayList<>();
    private final SecondsTimer targetSearchTimer = new SecondsTimer();

    boolean hasRecentlyKilled(Player player){
        return recentKills.contains(player.getMacAddress());
    }

    boolean readyToSearchNewTarget(){
        return targetSearchTimer.finished();
    }

    void restartTargetSearchTimer(){
        targetSearchTimer.start(BountyHunterManager.TARGET_SEARCH_DELAY_SECONDS);
    }

    void restartTargetAbandonTimer(){
        targetSearchTimer.start(BountyHunterManager.TARGET_ABANDON_DELAY_SECONDS);
    }

    void restartTargetLostTimer(){
        targetSearchTimer.start(BountyHunterManager.TARGET_SEARCH_DELAY_SECONDS / 2);
    }

    void onKill(){
        if(recentKills.size() >= 5)
            recentKills.remove(0);
    }

    public void addRecentKill(String hostAddress){
        recentKills.add(hostAddress);
    }

    public void clearRecentKills(){
        recentKills.clear();
    }

    public void startSearchTimer(int seconds) {
        targetSearchTimer.start(seconds);
    }

    public int getRemainingSearchTimerSeconds(){
        return targetSearchTimer.secondsRemaining();
    }

    public List<String> getRecentKills() {
        return recentKills;
    }
}
