package ph.edu.dlsu.lbycpob.chessapp.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * BackgroundMusicManager - A utility class for managing background music
 * Supports loading music from resources, playing, pausing, stopping, and volume control
 */
public class BackgroundMusicManager {

    private static BackgroundMusicManager instance;
    private Map<String, MediaPlayer> musicPlayers;
    private MediaPlayer currentPlayer;
    private String currentTrack;
    private double globalVolume = 0.5; // Default volume (0.0 to 1.0)
    private boolean isMuted = false;
    private double volumeBeforeMute;

    // Private constructor for singleton pattern
    private BackgroundMusicManager() {
        musicPlayers = new HashMap<>();
    }

    /**
     * Get the singleton instance of BackgroundMusicManager
     * @return BackgroundMusicManager instance
     */
    public static BackgroundMusicManager getInstance() {
        if (instance == null) {
            instance = new BackgroundMusicManager();
        }
        return instance;
    }

    /**
     * Load a music file from resources
     * @param trackName Unique identifier for the track
     * @param resourcePath Path to the music file in resources (e.g., "/music/background.mp3")
     * @return true if loaded successfully, false otherwise
     */
    public boolean loadMusic(String trackName, String resourcePath) {
        try {
            // Get resource URL
            URL resourceUrl = getClass().getResource(resourcePath);
            if (resourceUrl == null) {
                System.err.println("Music file not found: " + resourcePath);
                return false;
            }

            // Create Media and MediaPlayer
            Media media = new Media(resourceUrl.toExternalForm());
            MediaPlayer player = new MediaPlayer(media);

            // Set default properties
            player.setVolume(globalVolume);
            player.setCycleCount(MediaPlayer.INDEFINITE); // Loop indefinitely

            // Add error handling
            player.setOnError(() -> {
                System.err.println("Error playing music: " + player.getError().getMessage());
            });

            // Store the player
            musicPlayers.put(trackName, player);

            System.out.println("Music loaded successfully: " + trackName);
            return true;

        } catch (Exception e) {
            System.err.println("Failed to load music '" + trackName + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Play a specific track
     * @param trackName Name of the track to play
     * @return true if started successfully, false otherwise
     */
    public boolean playMusic(String trackName) {
        MediaPlayer player = musicPlayers.get(trackName);
        if (player == null) {
            System.err.println("Music track not found: " + trackName);
            return false;
        }

        try {
            // Stop current music if playing
            if (currentPlayer != null && currentPlayer != player) {
                currentPlayer.stop();
            }

            // Set as current player
            currentPlayer = player;
            currentTrack = trackName;

            // Play the music
            player.play();
            System.out.println("Playing music: " + trackName);
            return true;

        } catch (Exception e) {
            System.err.println("Failed to play music '" + trackName + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Pause the currently playing music
     */
    public void pauseMusic() {
        if (currentPlayer != null) {
            currentPlayer.pause();
            System.out.println("Music paused: " + currentTrack);
        }
    }

    /**
     * Resume the currently paused music
     */
    public void resumeMusic() {
        if (currentPlayer != null) {
            currentPlayer.play();
            System.out.println("Music resumed: " + currentTrack);
        }
    }

    /**
     * Stop the currently playing music
     */
    public void stopMusic() {
        if (currentPlayer != null) {
            currentPlayer.stop();
            System.out.println("Music stopped: " + currentTrack);
            currentPlayer = null;
            currentTrack = null;
        }
    }

    /**
     * Stop all loaded music players
     */
    public void stopAllMusic() {
        for (MediaPlayer player : musicPlayers.values()) {
            if (player != null) {
                player.stop();
            }
        }
        currentPlayer = null;
        currentTrack = null;
        System.out.println("All music stopped");
    }

    /**
     * Set the global volume for all music
     * @param volume Volume level (0.0 to 1.0)
     */
    public void setVolume(double volume) {
        if (volume < 0.0) volume = 0.0;
        if (volume > 1.0) volume = 1.0;

        globalVolume = volume;

        // Update volume for all players
        for (MediaPlayer player : musicPlayers.values()) {
            if (player != null) {
                player.setVolume(isMuted ? 0.0 : globalVolume);
            }
        }

        System.out.println("Volume set to: " + (volume * 100) + "%");
    }

    /**
     * Get the current volume level
     * @return Current volume (0.0 to 1.0)
     */
    public double getVolume() {
        return globalVolume;
    }

    /**
     * Mute all music
     */
    public void mute() {
        if (!isMuted) {
            volumeBeforeMute = globalVolume;
            isMuted = true;

            for (MediaPlayer player : musicPlayers.values()) {
                if (player != null) {
                    player.setVolume(0.0);
                }
            }
            System.out.println("Music muted");
        }
    }

    /**
     * Unmute all music
     */
    public void unmute() {
        if (isMuted) {
            isMuted = false;

            for (MediaPlayer player : musicPlayers.values()) {
                if (player != null) {
                    player.setVolume(globalVolume);
                }
            }
            System.out.println("Music unmuted");
        }
    }

    /**
     * Toggle mute/unmute
     */
    public void toggleMute() {
        if (isMuted) {
            unmute();
        } else {
            mute();
        }
    }

    /**
     * Check if music is currently muted
     * @return true if muted, false otherwise
     */
    public boolean isMuted() {
        return isMuted;
    }

    /**
     * Check if any music is currently playing
     * @return true if music is playing, false otherwise
     */
    public boolean isPlaying() {
        return currentPlayer != null &&
                currentPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    /**
     * Get the name of the currently playing track
     * @return Current track name or null if nothing is playing
     */
    public String getCurrentTrack() {
        return currentTrack;
    }

    /**
     * Set whether the current track should loop
     * @param loop true to loop indefinitely, false to play once
     */
    public void setLoop(boolean loop) {
        if (currentPlayer != null) {
            currentPlayer.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1);
        }
    }

    /**
     * Seek to a specific position in the current track
     * @param seconds Position in seconds
     */
    public void seekTo(double seconds) {
        if (currentPlayer != null) {
            currentPlayer.seek(Duration.seconds(seconds));
        }
    }

    /**
     * Get the current playback position in seconds
     * @return Current position in seconds, or 0 if no track is playing
     */
    public double getCurrentTime() {
        if (currentPlayer != null && currentPlayer.getCurrentTime() != null) {
            return currentPlayer.getCurrentTime().toSeconds();
        }
        return 0.0;
    }

    /**
     * Get the total duration of the current track in seconds
     * @return Total duration in seconds, or 0 if no track is loaded
     */
    public double getTotalDuration() {
        if (currentPlayer != null && currentPlayer.getTotalDuration() != null) {
            return currentPlayer.getTotalDuration().toSeconds();
        }
        return 0.0;
    }

    /**
     * Clean up resources - call this when shutting down the application
     */
    public void dispose() {
        stopAllMusic();
        for (MediaPlayer player : musicPlayers.values()) {
            if (player != null) {
                player.dispose();
            }
        }
        musicPlayers.clear();
        currentPlayer = null;
        currentTrack = null;
        System.out.println("BackgroundMusicManager disposed");
    }

    /**
     * Remove a specific track from memory
     * @param trackName Name of the track to remove
     */
    public void removeTrack(String trackName) {
        MediaPlayer player = musicPlayers.get(trackName);
        if (player != null) {
            if (player == currentPlayer) {
                stopMusic();
            }
            player.dispose();
            musicPlayers.remove(trackName);
            System.out.println("Track removed: " + trackName);
        }
    }

    /**
     * Get a list of all loaded track names
     * @return Array of track names
     */
    public String[] getLoadedTracks() {
        return musicPlayers.keySet().toArray(new String[0]);
    }
}