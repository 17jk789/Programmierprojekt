package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents;

import ch.unibas.dmi.dbis.cs108.casono.client.game.Player;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerState;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Controller for displaying a player's status in the poker game, including their name, chip count
 * and whether they are the dealer.
 */
public class PlayerStatusController {

    private static final Logger LOGGER = Logger.getLogger(PlayerStatusController.class.getName());

    @FXML private Label playerName;
    @FXML private Label playerMoney;
    @FXML private ImageView dealerIcon;
    @FXML private Pane parent;

    private Image dealerImage;
    private Player player;
    private static final String DEALER_IMAGE_PATH = "/images/chip-dealer-blue-3.png";
    private static final double DEALER_ICON_X_FACTOR = 0.8;

    /** Initialize the controller, load dealer image, and set up bindings. */
    @FXML
    public void initialize() {

        try {
            var url = getClass().getResource(DEALER_IMAGE_PATH);

            if (url != null) {
                dealerImage = new Image(url.toExternalForm(), true);
                dealerIcon.setImage(dealerImage);
            } else {
                LOGGER.warning("Dealer image not found in resources!");
            }

        } catch (Exception e) {
            LOGGER.severe("Error: loading dealer image:");
            e.printStackTrace();
        }

        dealerIcon.layoutXProperty().bind(parent.widthProperty().multiply(DEALER_ICON_X_FACTOR));
        dealerIcon.setVisible(false);
    }

    /**
     * Bind player to UI
     *
     * @param player The player whose status is to be displayed.
     */
    public void setPlayer(Player player) {
        this.player = player;
        refresh();
    }

    /** Refresh UI safely */
    public void refresh() {

        if (player == null) {
            return;
        }

        // NAME (safe)
        String name = player.getName();
        playerName.setText(name != null ? name : "-");

        // MONEY
        playerMoney.setText(player.getChips() + " $");

        // DEALER
        boolean isDealer = player.getState() == PlayerState.DEALER;

        dealerIcon.setVisible(isDealer);

        if (isDealer && dealerImage != null) {
            dealerIcon.setImage(dealerImage);
        }
    }

    /**
     * External quick update
     *
     * @param name The player's name to display.
     * @param chips The player's chip count to display.
     */
    public void updatePlayer(String name, int chips) {
        playerName.setText(name != null ? name : "-");
        playerMoney.setText(chips + " $");
    }

    /**
     * Force dealer state
     *
     * @param isDealer Whether the player is the dealer or not.
     */
    public void setDealer(boolean isDealer) {
        dealerIcon.setVisible(isDealer);

        if (isDealer && dealerImage != null) {
            dealerIcon.setImage(dealerImage);
        }
    }
}
