package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;
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

public class CasinomainuiController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label titleLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private javafx.scene.image.ImageView logoView;
    @FXML
    private javafx.scene.shape.Rectangle greenBox;
    @FXML
    private Button exitbutton;
    
    


    public void initialize() {
        titleLabel.setText("Casono");
        subtitleLabel.setText("Texas Hold'em Poker");
        // Logo laden
        logoView.setImage(new javafx.scene.image.Image(getClass().getResource("/images/logo.png").toExternalForm()));
    }

    @FXML
    public void handleexitbutton() {
        Platform.exit();
    }

}

