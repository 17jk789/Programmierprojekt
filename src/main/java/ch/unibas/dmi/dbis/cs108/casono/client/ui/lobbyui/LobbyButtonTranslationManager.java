package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.JSONObject;

/**
 * Handles JSON translation between ButtonID and LobbyID.
 * <p>
 * Verwaltet das Mapping zwischen Button-IDs und Lobby-IDs, speichert und lädt die Zuordnung aus einer JSON-Datei.
 */
public class LobbyButtonTranslationManager {
        /**
         * Removes all lobbies from the mapping and saves the file.
         */
        public void clearAllLobbies() {
            buttonIdToLobbyId.clear();
            saveTranslation();
        }
    /** Pfad zur JSON-Translationsdatei. */
    private final String translationFilePath = "src/main/resources/ui-structure/lobby_button_translation.json";
    /** Zuordnung ButtonID → LobbyID. */
    private final Map<Integer, Integer> buttonIdToLobbyId = new HashMap<>();
    /** Maximale Anzahl an Buttons im Grid. */
    private final int maxButtons = 8;

    /**
     * Konstruktor: lädt die Zuordnung aus der JSON-Datei.
     */
    public LobbyButtonTranslationManager() {
        loadTranslation();
    }

    /**
     * Lädt die Zuordnung ButtonID → LobbyID aus der JSON-Datei.
     * Falls die Datei nicht existiert, bleibt die Zuordnung leer.
     */
    public void loadTranslation() {
        try {
            Path path = Path.of(translationFilePath);
            if (!Files.exists(path)) return;
            String json = Files.readString(path);
            JSONObject obj = new JSONObject(json);
            buttonIdToLobbyId.clear();
            for (String key : obj.keySet()) {
                buttonIdToLobbyId.put(Integer.parseInt(key), obj.getInt(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Speichert die aktuelle Zuordnung ButtonID → LobbyID in die JSON-Datei.
     */
    public void saveTranslation() {
        try {
            JSONObject obj = new JSONObject();
            for (Map.Entry<Integer, Integer> entry : buttonIdToLobbyId.entrySet()) {
                obj.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            Files.write(Path.of(translationFilePath), obj.toString(2).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prüft, ob das Grid voll ist (maxButtons erreicht).
     * @return true, wenn Grid voll; sonst false
     */
    public boolean isFull() {
        return buttonIdToLobbyId.size() >= maxButtons;
    }

    /**
     * Fügt eine Zuordnung ButtonID → LobbyID hinzu und speichert sie.
     * @param buttonId Die ID des Buttons
     * @param lobbyId Die ID der Lobby
     * @throws Exception wenn das Grid voll ist
     */
    public void addLobbyButton(int buttonId, int lobbyId) throws Exception {
        if (isFull()) throw new Exception("Grid is full!");
        buttonIdToLobbyId.put(buttonId, lobbyId);
        saveTranslation();
    }

    /**
     * Entfernt eine Zuordnung für die gegebene ButtonID und speichert.
     * @param buttonId Die ID des zu entfernenden Buttons
     */
    public void removeLobbyButton(int buttonId) {
        buttonIdToLobbyId.remove(buttonId);
        saveTranslation();
    }

    /**
     * Gibt die LobbyID für eine gegebene ButtonID zurück.
     * @param buttonId Die ButtonID
     * @return Die zugehoerige LobbyID oder null, falls nicht vorhanden
     */
    public Integer getLobbyIdForButton(int buttonId) {
        return buttonIdToLobbyId.get(buttonId);
    }

    /**
     * Gibt die gesamte Zuordnung ButtonID → LobbyID zurück.
     * @return Map aller Zuordnungen
     */
    public Map<Integer, Integer> getButtonIdToLobbyId() {
        return buttonIdToLobbyId;
    }
}
