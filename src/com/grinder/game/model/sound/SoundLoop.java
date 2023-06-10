package com.grinder.game.model.sound;

public class SoundLoop {

    private final Sound[] sounds;
    private final int loopDelay;
    
    public SoundLoop(Sound sound, int loopDelay) {
    	this(new Sound[] { sound }, loopDelay);
    }

    public SoundLoop(Sound[] sounds, int loopDelay) {
        this.sounds = sounds;
        this.loopDelay = loopDelay;
    }

    public Sound[] getSounds() {
        return sounds;
    }

    public int getLoopDelay() {
        return loopDelay;
    }
}
