package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

import java.util.HashMap;
import java.util.Map;

/**
 * Verwaltet das Mapping zwischen Button-IDs und Lobby-IDs rein im Speicher. Keine Dateioperationen,
 * nur Laufzeitdatenstruktur.
 */
public class LobbyButtonTranslationManager {

    // Singleton-Instanz
    private static LobbyButtonTranslationManager instance;

    // Singleton-Zugriff
    /**
     * Liefert die Singleton-Instanz des Managers.
     *
     * @return die einzige Instanz von {@code LobbyButtonTranslationManager}
     */
    public static LobbyButtonTranslationManager getInstance() {
        if (instance == null) {
            instance = new LobbyButtonTranslationManager();
        }
        return instance;
    }

    /** Maximale Anzahl an Buttons/Lobbys */
    private static final int MAX_BUTTONS = 8;

    /** Zuordnung ButtonID → LobbyID */
    private final Map<Integer, Integer> buttonIdToLobbyId = new HashMap<>();

    /** Privater Konstruktor für Singleton-Pattern */
    private LobbyButtonTranslationManager() {
        // Zuordnung bleibt leer beim Start
    }

    /**
     * Prüft, ob das Grid voll ist (MAX_BUTTONS erreicht).
     *
     * @return true, wenn Grid voll; sonst false
     */
    public boolean isFull() {
        return buttonIdToLobbyId.size() >= MAX_BUTTONS;
    }

    /**
     * Fügt eine Zuordnung ButtonID → LobbyID hinzu.
     *
     * @param buttonId Die ID des Buttons
     * @param lobbyId Die ID der Lobby
     * @throws Exception wenn das Grid voll ist
     */
    public void addLobbyButton(int buttonId, int lobbyId) throws Exception {
        if (isFull()) {
            throw new Exception("Grid is full!");
        }
        buttonIdToLobbyId.put(buttonId, lobbyId);
    }

    /**
     * Entfernt eine Zuordnung für die gegebene ButtonID.
     *
     * @param buttonId Die ID des zu entfernenden Buttons
     */
    public void removeLobbyButton(int buttonId) {
        buttonIdToLobbyId.remove(buttonId);
    }

    /**
     * Gibt die LobbyID für eine gegebene ButtonID zurück.
     *
     * @param buttonId Die ButtonID
     * @return Die zugehoerige LobbyID oder null, falls nicht vorhanden
     */
    public Integer getLobbyIdForButton(int buttonId) {
        return buttonIdToLobbyId.get(buttonId);
    }

    /**
     * Gibt die gesamte Zuordnung ButtonID → LobbyID zurück.
     *
     * @return Map aller Zuordnungen
     */
    public Map<Integer, Integer> getButtonIdToLobbyId() {
        return buttonIdToLobbyId;
    }
}
