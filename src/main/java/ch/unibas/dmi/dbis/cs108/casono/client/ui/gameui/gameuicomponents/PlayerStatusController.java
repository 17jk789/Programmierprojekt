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

    @FXML Label playerName;
    @FXML Label playerMoney;
    @FXML ImageView dealerIcon;
    @FXML ImageView playerProfileImage;
    @FXML Pane parent;
    @FXML VBox playerStatusBox;
    @FXML HBox statusInnerBoxTop;
    @FXML HBox statusInnerBoxBottom;

    private Image dealerImage;
    // Profile images (loaded from resources/images/profile-picture)
    private Image profileUserImage;
    private Image profileDealerImage;
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

        try {
            var base = "/images/profile-picture/";
            var uUser = getClass().getResource(base + "poker_user_491.png");
            var uDealer = getClass().getResource(base + "poker_dealer_471.png");

            if (uUser != null) {
                profileUserImage = new Image(uUser.toExternalForm(), true);
            }
            if (uDealer != null) {
                profileDealerImage = new Image(uDealer.toExternalForm(), true);
            }

            if (playerProfileImage != null && profileUserImage != null) {
                playerProfileImage.setImage(profileUserImage);
            }

        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Could not load profile images", e);
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
        if (player == null || candidate == null) {
            return false;
        }

        String currentName = normalizeIdentifier(player.getName());
        String candidateName = normalizeIdentifier(candidate.getName());
        if (currentName != null && candidateName != null) {
            return currentName.equals(candidateName);
        }

        return player.getId() != null
                && candidate.getId() != null
                && player.getId().equals(candidate.getId());
    }

    private String normalizeIdentifier(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed.toLowerCase();
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

        if (playerProfileImage != null) {
            if (isDealer && profileDealerImage != null) {
                playerProfileImage.setImage(profileDealerImage);
            } else if (profileUserImage != null) {
                playerProfileImage.setImage(profileUserImage);
            }
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
