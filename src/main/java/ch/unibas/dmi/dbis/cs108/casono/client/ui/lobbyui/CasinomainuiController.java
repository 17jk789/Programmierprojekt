package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

/**
 * Controller for the Casono main UI lobby.
 * Handles UI initialization and user actions.
 */
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.AnchorPane;

/**
 * Controller for the Casono main UI lobby.
 * Handles UI initialization and user actions.
 * <p>
 * Standardkonstruktor für den Controller.
 */
public class CasinomainuiController {

    /**
     * Standardkonstruktor.
     */
    public CasinomainuiController() {
        // Standardkonstruktor
    }
    /** Root pane of the UI. */
    @FXML
    private AnchorPane rootPane;
    /** Title label for the main UI. */
    @FXML
    private Label titleLabel;
    /** Subtitle label for the main UI. */
    @FXML
    private Label subtitleLabel;
    /** Logo image view. */
    @FXML
    private javafx.scene.image.ImageView logoView;
    /** Green box shape element. */
    @FXML
    private javafx.scene.shape.Rectangle greenBox;
    /** Exit button for closing the application. */
    @FXML
    private Button exitbutton;
    
    


    /**
     * Initializes the UI components and sets default values.
     */
    public void initialize() {
        titleLabel.setText("Casono");
        subtitleLabel.setText("Texas Hold'em Poker");
        // Logo laden
        logoView.setImage(new javafx.scene.image.Image(getClass().getResource("/images/logo.png").toExternalForm()));
    }

    /**
     * Handles the exit button action to close the application.
     */
    @FXML
    public void handleexitbutton() {
        Platform.exit();
    }

}

