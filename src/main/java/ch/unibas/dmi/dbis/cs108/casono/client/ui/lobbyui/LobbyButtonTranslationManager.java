package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the mapping between Button IDs and Lobby IDs in memory only. No file
 * operations,
 * runtime-only data structure.
 */
public class LobbyButtonTranslationManager {

    // Singleton instance
    private static LobbyButtonTranslationManager instance;

    // Singleton access
    /**
     * Returns the singleton instance of the manager.
     *
     * @return the single instance of {@code LobbyButtonTranslationManager}
     */
    public static LobbyButtonTranslationManager getInstance() {
        if (instance == null) {
            instance = new LobbyButtonTranslationManager();
        }
        return instance;
    }

    /** Maximum number of buttons/lobbies */
    private static final int MAX_BUTTONS = 8;

    /** Mapping ButtonID → LobbyID */
    private final Map<Integer, Integer> buttonIdToLobbyId = new HashMap<>();

    /** Private constructor for the singleton pattern */
    private LobbyButtonTranslationManager() {
        // Mapping is empty at startup
    }

    /**
     * Checks whether the grid is full (MAX_BUTTONS reached).
     *
     * @return true if the grid is full; otherwise false
     */
    public boolean isFull() {
        return buttonIdToLobbyId.size() >= MAX_BUTTONS;
    }

    /**
     * Adds a mapping ButtonID → LobbyID.
     *
     * @param buttonId the ID of the button
     * @param lobbyId  the ID of the lobby
     * @throws Exception when the grid is full
     */
    public void addLobbyButton(int buttonId, int lobbyId) throws Exception {
        if (isFull()) {
            throw new Exception("Grid is full!");
        }
        buttonIdToLobbyId.put(buttonId, lobbyId);
    }

    /**
     * Removes the mapping for the given ButtonID.
     *
     * @param buttonId the ID of the button to remove
     */
    public void removeLobbyButton(int buttonId) {
        buttonIdToLobbyId.remove(buttonId);
    }

    /**
     * Returns the LobbyID for a given ButtonID.
     *
     * @param buttonId the ButtonID
     * @return the associated LobbyID or null if not present
     */
    public Integer getLobbyIdForButton(int buttonId) {
        return buttonIdToLobbyId.get(buttonId);
    }

    /**
     * Returns the full mapping ButtonID → LobbyID.
     *
     * @return Map of all mappings
     */
    public Map<Integer, Integer> getButtonIdToLobbyId() {
        return buttonIdToLobbyId;
    }
}
