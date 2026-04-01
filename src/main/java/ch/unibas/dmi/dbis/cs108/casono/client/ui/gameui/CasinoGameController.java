package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller for the casino gaming area.
 *
 * <p>Responsible for: - the display of the poker table and the player interface, - the
 * processing of user input, - the interface to the game engine and the network protocol.
 *
 * <p>Notes: - The `onTableClick()` method currently serves only as test logic.
 * It may no longer be functional and will be replaced by the final game interaction in the future.
 */
public class CasinoGameController {

    /** Standard constructor. Used by FXML. */
    public CasinoGameController() {
        // default constructor for FXML
    }

    @FXML private Label welcomeText;
    @FXML private VBox casinoTable;

    // TODO: Test logic: will be replaced by real game interactions,
    // once the game engine is finished

    /**
     * Temporary test method that performs a placeholder action when the table is clicked.
     *
     * <p>In the final implementation, this will be replaced by the game logic.
     */
    @FXML
    public void onTableClick() {
        welcomeText.setText("Einsatz akzeptiert!");
    }
}
