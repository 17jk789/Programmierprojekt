package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

/**
 * Manages the grid for lobby buttons and rendering. Uses LobbyButtonTranslationManager for mapping
 * ButtonID to LobbyID.
 */
import java.util.Map;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the grid for lobby buttons and rendering. Uses
 * LobbyButtonTranslationManager for mapping
 * ButtonID to LobbyID.
 */
public class LobbyButtonGridManager {
    private static final Logger LOGGER = LogManager.getLogger(LobbyButtonGridManager.class);

    /** GridPane for the button grid. */
    private final GridPane gridPane;

    /** Manager for mapping ButtonID to LobbyID. */
    private final LobbyButtonTranslationManager translationManager;

    /** Number of rows in the grid. */
    private static final int ROWS = 2;

    /** Number of columns in the grid. */
    private static final int COLS = 4;

    /** Path to the button image. */
    private static final String BUTTON_IMAGE_PATH = "/images/logo.png";

    /** Max random lobby id. */
    private static final int MAX_RANDOM_LOBBY_ID = 10000;

    /**
     * Constructor for the GridManager.
     *
     * @param gridPane           the GridPane for rendering
     * @param translationManager the manager for mapping ButtonID to LobbyID
     */
    public LobbyButtonGridManager(
            GridPane gridPane, LobbyButtonTranslationManager translationManager) {
        this.gridPane = gridPane;
        // Singleton immer verwenden
        this.translationManager = LobbyButtonTranslationManager.getInstance();
    }

    /**
     * Renders all lobby buttons in the grid. Creates a button for each mapping with
     * image and event
     * handler.
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
            btn.setGraphic(
                    new ImageView(new Image(getClass().getResourceAsStream(BUTTON_IMAGE_PATH))));
            btn.setOnAction(
                    e -> {
                        Integer lobbyId = translationManager.getLobbyIdForButton(buttonId);
                        if (lobbyId != null) {
                            joinLobby(lobbyId);
                        }
                    });
            int row = index / COLS;
            int col = index % COLS;
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
        int lobbyId = (int) (Math.random() * MAX_RANDOM_LOBBY_ID + 1);
        LOGGER.info("Lobby created: {}", lobbyId);
        return lobbyId;
    }

    /**
     * Placeholder for joining a lobby.
     *
     * @param lobbyId The lobbyId to join
     */
    public void joinLobby(int lobbyId) {
        // Game-UI starten und Lobby-UI schließen
        LOGGER.info("Joining lobby: {}", lobbyId);
        javafx.application.Platform.runLater(() -> {
            // Lobby-Stage schließen
            javafx.stage.Stage currentStage = (javafx.stage.Stage) gridPane.getScene().getWindow();
            currentStage.close();
            // Game-UI starten
            try {
                new ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.CasinoGameUI().start(new javafx.stage.Stage());
            } catch (Exception e) {
                LOGGER.error("Fehler beim Starten der Game-UI: {}", e.getMessage());
            }
        });
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
