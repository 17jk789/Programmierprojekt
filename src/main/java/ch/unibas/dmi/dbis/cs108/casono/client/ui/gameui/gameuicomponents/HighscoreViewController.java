package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents;

import ch.unibas.dmi.dbis.cs108.casono.client.network.LobbyClient;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Controller for the highscore popup window. */
public class HighscoreViewController {

    private static final Logger LOGGER =
            LogManager.getLogger(HighscoreViewController.class.getSimpleName());

    @FXML private ListView<String> highscoreList;
    @FXML private Label statusLabel;
    @FXML private Node highscoreRoot;

    private LobbyClient lobbyClient;
    private double dragOffsetX;
    private double dragOffsetY;

    public void setLobbyClient(LobbyClient lobbyClient) {
        this.lobbyClient = lobbyClient;
    }

    /** Starts dragging the window from anywhere in the popup. */
    @FXML
    public void onPopupPressed(MouseEvent event) {
        Stage stage = getStage();
        if (stage == null) {
            return;
        }

        dragOffsetX = stage.getX() - event.getScreenX();
        dragOffsetY = stage.getY() - event.getScreenY();
    }

    /** Moves the popup window while the mouse is dragged anywhere on the popup. */
    @FXML
    public void onPopupDragged(MouseEvent event) {
        Stage stage = getStage();
        if (stage == null) {
            return;
        }

        stage.setX(event.getScreenX() + dragOffsetX);
        stage.setY(event.getScreenY() + dragOffsetY);
    }

    /** Returns the popup stage if the root is already attached to a scene. */
    private Stage getStage() {
        if (highscoreRoot == null || highscoreRoot.getScene() == null) {
            return null;
        }

        return (Stage) highscoreRoot.getScene().getWindow();
    }

    /** Loads current highscores from the server and refreshes the list. */
    @FXML
    public void refreshHighscores() {
        if (highscoreList == null || statusLabel == null) {
            return;
        }

        if (lobbyClient == null) {
            statusLabel.setText("Lobby client not initialized.");
            return;
        }

        try {
            List<String> entries = lobbyClient.getHighscores();
            highscoreList.getItems().setAll(entries);
            statusLabel.setText(
                    entries.isEmpty() ? "No highscores yet." : entries.size() + " entries loaded.");
        } catch (RuntimeException e) {
            LOGGER.error("Failed to load highscores: {}", e.getMessage());
            statusLabel.setText("Failed to load highscores.");
        }
    }

    /** Clears all highscores globally on the server and reloads the list. */
    @FXML
    public void clearHighscores() {
        if (lobbyClient == null || statusLabel == null) {
            return;
        }

        try {
            lobbyClient.clearHighscores();
            refreshHighscores();
            statusLabel.setText("Highscores cleared.");
        } catch (RuntimeException e) {
            LOGGER.error("Failed to clear highscores: {}", e.getMessage());
            statusLabel.setText("Failed to clear highscores.");
        }
    }

    /** Closes the highscore popup window. */
    @FXML
    public void closeWindow() {
        if (highscoreList == null || highscoreList.getScene() == null) {
            return;
        }

        Stage stage = (Stage) highscoreList.getScene().getWindow();
        stage.close();
    }
}
