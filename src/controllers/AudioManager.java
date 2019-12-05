package controllers;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Handles audio related events.
 */
public class AudioManager {

    private static final AudioManager INSTANCE = new AudioManager();
    /**
     * Set of all currently playing sounds.
     *
     * <p>
     * Use this to keep a reference to the sound until it finishes playing.
     * </p>
     */
    private final Set<MediaPlayer> soundPool = new HashSet<>();
    private boolean enabled = true;
    private boolean counting = false;

    /**
     * Enumeration of known sound resources.
     */
    public enum SoundRes {
        WIN, LOSE, MOVE;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private AudioManager() {
    }

    public static AudioManager getInstance() {
        return INSTANCE;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isCountingDown() {
    	return counting;
    }
    
    public void setCountingDown(boolean counting) {
    	this.counting = counting;
    }
    
    

    /**
     * Plays the sound. If disabled, simply return.
     *
     * <p>
     * Hint:
     * <ul>
     * <li>Use {@link MediaPlayer#play()} and {@link MediaPlayer#dispose()}.</li>
     * <li>When creating a new MediaPlayer object, add it into {@link AudioManager#soundPool} before playing it.</li>
     * <li>Set a callback for when the sound has completed playing. This is to remove it from the soundpool, and
     * dispose the player using a daemon thread.</li>
     * </ul>
     *
     * @param name the name of the sound file to be played, excluding .mp3
     */
    private void playFile(final String name) {
        // TODO
    	if (enabled) {
//        	Media media = new Media(getClass().getResource("resources/assets/audio/" + name + ".mp3").toExternalForm());
//        	MediaPlayer player = new MediaPlayer(media);
//        	player.play();
    	}
    }

    /**
     * Plays a sound.
     *
     * @param name Enumeration of the sound, given by {@link SoundRes}.
     */
    public void playSound(final SoundRes name) {
        playFile(name.toString());
    }
}