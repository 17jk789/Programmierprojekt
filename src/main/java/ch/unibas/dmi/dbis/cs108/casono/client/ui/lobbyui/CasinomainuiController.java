package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/**
 * Controller for the Casono main UI lobby.
 * Handles UI initialization and user actions.
 */
public class CasinomainuiController {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label titleLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private ImageView logoView;
    @FXML
    private Rectangle greenBox;
    @FXML
    private Button exitbutton;
    @FXML
    private VBox casinoTable;

    private LobbyButtonTranslationManager translationManager;
    private LobbyButtonGridManager gridManager;
    private int nextButtonId = 1;

    public CasinomainuiController() {
        // Default constructor
    }

    /**
     * Initializes the UI components and sets default values.
     */
    @FXML
    public void initialize() {
        titleLabel.setText("Casono");
        subtitleLabel.setText("Texas Hold'em Poker");
        logoView.setImage(new Image(getClass().getResource("/images/logo.png").toExternalForm()));

        translationManager = new LobbyButtonTranslationManager();
        gridManager = new LobbyButtonGridManager(new javafx.scene.layout.GridPane(), translationManager);
        casinoTable.getChildren().clear();
        casinoTable.getChildren().add(gridManager.getGridPane());
        gridManager.renderLobbyButtons();
    }

    /**
     * Handles the exit button action to close the application.
     */
    @FXML
    public void handleexitbutton() {
        Platform.exit();
    }

    /**
     * Handles creation of a new lobby button.
     */
    @FXML
    public void handleCreateLobbyButton() {
        if (translationManager.isFull()) {
            System.out.println("Grid voll! Keine weiteren Lobbys moeglich.");
            return;
        }
        int buttonId = nextButtonId++;
        int lobbyId = gridManager.createLobby();
        try {
            translationManager.addLobbyButton(buttonId, lobbyId);
            System.out.println("ButtonID: " + buttonId + ", LobbyID: " + lobbyId);
            gridManager.renderLobbyButtons();
        } catch (Exception e) {
            System.out.println("Fehler beim Hinzufügen: " + e.getMessage());
        }
    }
}
