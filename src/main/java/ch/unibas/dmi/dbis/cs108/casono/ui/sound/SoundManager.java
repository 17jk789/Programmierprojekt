package ch.unibas.dmi.dbis.cs108.casono.ui.sound;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages sound effects for the Casono game. Provides methods to play sounds
 * for card reveals and
 * button clicks.
 */
public class SoundManager {
    private static final Logger LOGGER = LogManager.getLogger(SoundManager.class);
    private static final SoundManager INSTANCE = new SoundManager();
    private final Map<String, AudioClip> soundCache = new HashMap<>();
    private static final float MIN_VOLUME = 0.0f;
    private static final float MAX_VOLUME = 1.0f;
    private static final float DEFAULT_VOLUME = 0.5f;
    private float volume = DEFAULT_VOLUME;
    private static final long CARD_REVEAL_STAGGER_MS = 120; // Delay between card reveal sounds

    private SoundManager() {
    }

    /** Returns the singleton instance of SoundManager. */
    public static SoundManager getInstance() {
        return INSTANCE;
    }

    /**
     * Pre-loads critical sounds into cache to avoid delays on first play. Should be
     * called at
     * application startup.
     */
    public void preloadSounds() {
        try {
            getOrLoadSound("button-click");
            getOrLoadSound("card-reveal");
        } catch (Exception e) {
            LOGGER.error("Error preloading sounds", e);
        }
    }

    /** Plays a button click sound. */
    public void playButtonClick() {
        playSound("button-click");
    }

    /** Plays a card reveal sound (for dealt or community cards). */
    public void playCardReveal() {
        playSound("card-reveal");
    }

    /**
     * Plays multiple card reveal sounds in sequence with slight stagger/overlap.
     * Used when multiple
     * cards are revealed at once (e.g., Flop reveals 3 cards, Turn reveals 1 card).
     * Sounds play
     * with a slight delay between them to simulate a person revealing cards one by
     * one.
     *
     * @param count The number of cards being revealed. Each card gets its own sound
     *              with stagger.
     */
    public void playCardRevealMultiple(int count) {
        if (count <= 0) {
            return;
        }
        if (count == 1) {
            playCardReveal();
            return;
        }

        for (int i = 0; i < count; i++) {
            final int cardIndex = i;
            long delayMs = cardIndex * CARD_REVEAL_STAGGER_MS;

            PauseTransition pause = new PauseTransition(Duration.millis(delayMs));
            pause.setOnFinished(
                    event -> Platform.runLater(
                            () -> {
                                try {
                                    playCardReveal();
                                } catch (Exception e) {
                                    LOGGER.error("Error playing card reveal sound", e);
                                }
                            }));
            pause.play();
        }
    }

    /** Plays a card shuffle sound. */
    public void playCardShuffle() {
        playSound("card-shuffle");
    }

    /** Plays a pot/chip sound. */
    public void playChip() {
        playSound("chip");
    }

    /**
     * Plays a generic sound by key. The sound file should exist at:
     * /sounds/{category}/{key}.mp3 or
     * .wav
     */
    public void playSound(String soundKey) {
        try {
            AudioClip audioClip = getOrLoadSound(soundKey);
            if (audioClip != null) {
                audioClip.setVolume(volume);
                audioClip.play();
            }
        } catch (Exception e) {
            LOGGER.error("Error playing sound: {}", soundKey, e);
        }
    }

    /**
     * Gets or loads a sound from cache. Categorizes sounds automatically based on
     * key prefix.
     */
    private AudioClip getOrLoadSound(String soundKey) {
        if (soundCache.containsKey(soundKey)) {
            return soundCache.get(soundKey);
        }

        String category = categorizeSound(soundKey);
        String resourcePath = String.format("/sounds/%s/%s.wav", category, soundKey);

        try {
            URL soundUrl = getClass().getResource(resourcePath);
            if (soundUrl != null) {
                AudioClip clip = new AudioClip(soundUrl.toString());
                soundCache.put(soundKey, clip);
                return clip;
            } else {
                LOGGER.warn("Sound resource not found: {}", resourcePath);
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load sound: {}", resourcePath, e);
            return null;
        }
    }

    /** Categorizes a sound based on its key prefix. */
    private String categorizeSound(String soundKey) {
        if (soundKey.startsWith("button")) {
            return "buttons";
        } else if (soundKey.startsWith("card")) {
            return "cards";
        } else {
            return "game";
        }
    }

    /** Sets the volume for sound effects (0.0 - 1.0). */
    public void setVolume(float volume) {
        this.volume = Math.max(MIN_VOLUME, Math.min(MAX_VOLUME, volume));
    }

    /** Gets the current volume level. */
    public float getVolume() {
        return volume;
    }

    /** Clears the sound cache. */
    public void clearCache() {
        soundCache.clear();
    }

    /** Mutes all sounds by setting volume to 0. */
    public void mute() {
        setVolume(0.0f);
    }

    /** Unmutes sounds by restoring to default volume. */
    public void unmute() {
        setVolume(DEFAULT_VOLUME);
    }
}
