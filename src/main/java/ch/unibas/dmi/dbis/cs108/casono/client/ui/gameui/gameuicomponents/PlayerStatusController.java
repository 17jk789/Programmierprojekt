package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents;

import ch.unibas.dmi.dbis.cs108.casono.client.game.Player;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerState;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

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
    @FXML private VBox playerStatusBox;
    @FXML private HBox statusInnerBoxTop;
    @FXML private HBox statusInnerBoxBottom;

    private Image dealerImage;
    private Player player;
    private boolean turnHighlighted;
    private static final String DEALER_IMAGE_PATH = "/images/chip-dealer-blue-5.png";
    private static final double DEALER_ICON_X_FACTOR = 0.8;
    private static final String TURN_HIGHLIGHT_STYLE_CLASS = "player-status-active-turn";

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
            LOGGER.log(Level.SEVERE, "Error loading dealer image", e);
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
        if (player == null) {
            turnHighlighted = false;
        }
        refresh();
        updateTurnHighlightStyle();
    }

    /** Refresh UI safely */
    public void refresh() {

        if (player == null) {
            playerName.setText("-");
            playerMoney.setText("0 $");
            dealerIcon.setVisible(false);
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
     * Highlight or unhighlight this player slot as the currently active turn.
     *
     * @param highlighted true if this slot should be highlighted, false otherwise.
     */
    public void setTurnHighlighted(boolean highlighted) {
        this.turnHighlighted = highlighted;
        updateTurnHighlightStyle();
    }

    /**
     * Check whether the controller is currently bound to the provided player.
     *
     * @param candidate the player to compare against.
     * @return true if both represent the same player.
     */
    public boolean hasPlayer(Player candidate) {
        if (player == null
                || candidate == null
                || player.getId() == null
                || candidate.getId() == null) {
            return false;
        }

        return player.getId().equals(candidate.getId());
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

    /**
     * Update the visual style of the player status box to indicate whether it's currently this
     * player's turn.
     */
    private void updateTurnHighlightStyle() {
        if (statusInnerBoxTop == null || statusInnerBoxBottom == null) {
            return;
        }

        if (turnHighlighted) {
            if (!statusInnerBoxTop.getStyleClass().contains(TURN_HIGHLIGHT_STYLE_CLASS)) {
                statusInnerBoxTop.getStyleClass().add(TURN_HIGHLIGHT_STYLE_CLASS);
            }
            if (!statusInnerBoxBottom.getStyleClass().contains(TURN_HIGHLIGHT_STYLE_CLASS)) {
                statusInnerBoxBottom.getStyleClass().add(TURN_HIGHLIGHT_STYLE_CLASS);
            }
        } else {
            statusInnerBoxTop.getStyleClass().remove(TURN_HIGHLIGHT_STYLE_CLASS);
            statusInnerBoxBottom.getStyleClass().remove(TURN_HIGHLIGHT_STYLE_CLASS);
        }
    }
}
