package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

/**
 * Manages the grid for lobby buttons and rendering.
 * Uses LobbyButtonTranslationManager for mapping ButtonID to LobbyID.
 */

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.util.Map;

/**
 * Manages the grid for lobby buttons and rendering.
 * Uses LobbyButtonTranslationManager for mapping ButtonID to LobbyID.
 */
public class LobbyButtonGridManager {

    /** GridPane for the button grid. */
    private final GridPane gridPane;
    /** Manager for mapping ButtonID to LobbyID. */
    private final LobbyButtonTranslationManager translationManager;
    /** Number of rows in the grid. */
    private final int rows = 2;
    /** Number of columns in the grid. */
    private final int cols = 4;
    /** Path to the button image. */
    private final String buttonImagePath = "/images/logo.png";

    /**
     * Constructor for the GridManager.
     * 
     * @param gridPane           the GridPane for rendering
     * @param translationManager the manager for mapping ButtonID to LobbyID
     */
    public LobbyButtonGridManager(GridPane gridPane, LobbyButtonTranslationManager translationManager) {
        this.gridPane = gridPane;
        this.translationManager = translationManager;
    }

    /**
     * Renders all lobby buttons in the grid.
     * Creates a button for each mapping with image and event handler.
     */
    public void renderLobbyButtons() {
        gridPane.getChildren().clear();
        int index = 0;
        Map<Integer, Integer> mapping = translationManager.getButtonIdToLobbyId();
        if (mapping.isEmpty()) {
            // No buttons to render
            return;
        }
        for (Map.Entry<Integer, Integer> entry : mapping.entrySet()) {
            int buttonId = entry.getKey();
            Button btn = new Button();
            btn.setId("lobbyBtn-" + buttonId);
            btn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(buttonImagePath))));
            btn.setOnAction(e -> {
                Integer lobbyId = translationManager.getLobbyIdForButton(buttonId);
                if (lobbyId != null)
                    joinLobby(lobbyId);
            });
            int row = index / cols;
            int col = index % cols;
            gridPane.add(btn, col, row);
            index++;
        }
    }

    /**
     * Placeholder for lobby creation logic. Returns a generated lobbyId.
     * 
     * @return The generated lobbyId
     */
    public int createLobby() {
        // TODO: Replace with actual lobby creation logic
        int lobbyId = (int) (Math.random() * 10000 + 1);
        System.out.println("Lobby created: " + lobbyId);
        return lobbyId;
    }

    /**
     * Placeholder for joining a lobby.
     * 
     * @param lobbyId The lobbyId to join
     */
    public void joinLobby(int lobbyId) {
        // TODO: Replace with actual join logic
        System.out.println("Joining lobby: " + lobbyId);
    }

    /**
     * Getter for the GridPane.
     * 
     * @return The GridPane for the button grid
     */
    public javafx.scene.layout.GridPane getGridPane() {
        return gridPane;
    }
}
