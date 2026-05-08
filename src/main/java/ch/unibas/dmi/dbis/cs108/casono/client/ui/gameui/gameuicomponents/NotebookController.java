package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller for the movable notebook (Tips) display within the Casono Game UI.
 *
 * <p>Responsible for: - Drag-and-drop movement of the notebook, - Display of general gaming tips, -
 * Toggle visibility of the notebook.
 */
public class NotebookController {

    private static final Logger LOGGER = LogManager.getLogger(NotebookController.class);

    @FXML private VBox notebook;
    @FXML private ScrollPane tipsContent;
    @FXML private VBox tipsList;
    @FXML private Button toggleButton;

    private double xOffset = 0;
    private double yOffset = 0;
    private static final double NOTEBOOK_SCALE = 0.95;
    private static final double SCALE_NORMAL = 1.0;
    private boolean isVisible = true;

    /** Standard constructor. Used by FXML. */
    public NotebookController() {
        // default constructor for FXML
    }

    /** Initialize the notebook with default tips. */
    @FXML
    private void initialize() {
        if (tipsList != null) {
            loadDefaultTips();
        }
    }

    /** Load default poker tips into the notebook. */
    private void loadDefaultTips() {
        tipsList.getChildren().clear();

        String[] tips = {
            "TEXAS HOLD'EM GRUNDLAGEN\n\nJeder Spieler erhält 2 verdeckte Karten. "
                    + "Zusätzlich gibt es 5 Gemeinschaftskarten.\n"
                    + "Ziel: Beste 5-Karten-Hand bilden oder alle Gegner zum Fold bringen.\n"
                    + "Startstack: 20000 Chips ($)",
            "BLINDS & DEALER\n\nSmall Blind: 100 Chips | Big Blind: 200 Chips\n"
                    + "Die Blinds erzeugen den Startpot und "
                    + "müssen vor jeder Runde gezahlt werden.\n"
                    + "Der Dealer-Button bestimmt die Positionen "
                    + "sowie die Reihenfolge der Aktionen.",
            "SPIELABLAUF\n\nPreflop → Flop (3 Karten) → Turn (4. Karte) → River (5. Karte)\n"
                    + "Preflop beginnt links vom Big Blind.\n"
                    + "Nach dem Flop beginnt die Action links vom Dealer (aktive Spieler).",
            "AKTIONEN\n\nFold = aussteigen\nCall = mitgehen\nRaise = erhöhen\n"
                    + "Alle Aktionen müssen in korrekter Reihenfolge (Acting in Turn) erfolgen.",
            "POSITION IST ENTSCHEIDEND\n\nEarly Position: nur starke Hände spielen\n"
                    + "Middle Position: etwas breiter spielen\n"
                    + "Late Position: größter Vorteil durch mehr Informationen",
            "SHOWDOWN\n\nWenn nach der letzten Setzrunde mehrere Spieler übrig sind:\n"
                    + "Beste 5-Karten-Kombination aus Handkarten + Gemeinschaftskarten gewinnt.",
            "STRATEGIE & DENKEN\n\nPoker ist Strategie + Psychologie + Mathematik\n"
                    + "Entscheidungen basieren auf Wahrscheinlichkeiten, "
                    + "Position und Gegnerverhalten.",
            "WICHTIGE REGELN\n\n- Acting in Turn ist Pflicht\n"
                    + "- Jeder Spieler spielt nur seine eigene Hand (One Player One Hand)\n"
                    + "- Ungültige Einsätze werden blockiert oder korrigiert\n"
                    + "- Reihenfolge am Tisch muss immer eingehalten werden",
            "HÄUFIGE FEHLER\n\n❌ Zu viele Hände spielen\n"
                    + "❌ Position ignorieren\n❌ Zu oft callen statt folden\n"
                    + "❌ Stärke der Gegner unterschätzen\n❌ Tilt (emotional spielen)",
            "TOP 10 POKERHÄNDE\n\n"
                    + "1. 👑 Royal Flush\n"
                    + "A♠ K♠ Q♠ J♠ 10♠\n\n"
                    + "2. 🔥 Straight Flush\n"
                    + "9♥ 8♥ 7♥ 6♥ 5♥\n\n"
                    + "3. 💥 Four of a Kind (Poker)\n"
                    + "A♣ A♦ A♥ A♠ K♠\n\n"
                    + "4. 🏠 Full House\n"
                    + "K♣ K♦ K♥ 9♠ 9♦\n\n"
                    + "5. 🌊 Flush\n"
                    + "A♥ J♥ 8♥ 5♥ 2♥\n\n"
                    + "6. ➡ Straight\n"
                    + "10♣ 9♦ 8♠ 7♥ 6♣\n\n"
                    + "7. 🎯 Three of a Kind (Drilling)\n"
                    + "Q♣ Q♦ Q♥ 7♠ 2♦\n\n"
                    + "8. 👥 Two Pair\n"
                    + "J♣ J♦ 4♠ 4♥ A♣\n\n"
                    + "9. 👍 One Pair\n"
                    + "8♣ 8♦ K♠ J♥ 3♣\n\n"
                    + "10. 🃏 High Card\n"
                    + "A♠ J♦ 9♣ 5♥ 2♠"
        };

        for (String tip : tips) {
            TextFlow tipBox = createTipElement(tip);
            tipsList.getChildren().add(tipBox);
        }
    }

    private static final int LINE_SPACING = 4;
    private static final int TIP_WIDTH = 280;

    /**
     * Create a styled text element for a single tip.
     *
     * @param tipText The text of the tip to display.
     * @return A TextFlow element containing the formatted tip.
     */
    private TextFlow createTipElement(String tipText) {
        Text text = new Text(tipText);
        text.setStyle("-fx-font-size: 12; -fx-font-family: 'Segoe UI', Arial;");
        TextFlow flow = new TextFlow(text);
        flow.setStyle(
                "-fx-padding: 10; -fx-background-color: #f5f5f5; "
                        + "-fx-border-radius: 5; -fx-margin: 5;");
        flow.setLineSpacing(LINE_SPACING);
        flow.setPrefWidth(TIP_WIDTH);
        return flow;
    }

    /**
     * Called when the notebook header is pressed with the mouse. Saves the relative position for
     * later correct repositioning.
     *
     * @param event The mouse event
     */
    @FXML
    private void onNotebookPressed(MouseEvent event) {
        if (notebook == null || event == null) {
            return;
        }
        xOffset = event.getSceneX() - notebook.getLayoutX();
        yOffset = event.getSceneY() - notebook.getLayoutY();
    }

    /**
     * Called while dragging the notebook with the mouse. Updates the position and slightly scales
     * the notebook for visual feedback.
     *
     * @param event The mouse event
     */
    @FXML
    private void onNotebookDragged(MouseEvent event) {
        if (notebook == null || event == null || notebook.getScene() == null) {
            return;
        }

        notebook.setScaleX(NOTEBOOK_SCALE);
        notebook.setScaleY(NOTEBOOK_SCALE);

        double targetX = event.getSceneX() - xOffset;
        double targetY = event.getSceneY() - yOffset;

        double maxX = Math.max(0, notebook.getScene().getWidth() - scaledNodeWidth());
        double maxY = Math.max(0, notebook.getScene().getHeight() - scaledNodeHeight());

        notebook.setLayoutX(clamp(targetX, 0, maxX));
        notebook.setLayoutY(clamp(targetY, 0, maxY));
    }

    /**
     * Called when the mouse cursor is released over the notebook. Resets the notebook scaling to
     * normal size.
     *
     * @param event The mouse event
     */
    @FXML
    private void onNotebookReleased(MouseEvent event) {
        if (notebook == null) {
            return;
        }
        notebook.setScaleX(SCALE_NORMAL);
        notebook.setScaleY(SCALE_NORMAL);

        if (notebook.getScene() == null) {
            return;
        }

        double maxX = Math.max(0, notebook.getScene().getWidth() - scaledNodeWidth());
        double maxY = Math.max(0, notebook.getScene().getHeight() - scaledNodeHeight());

        notebook.setLayoutX(clamp(notebook.getLayoutX(), 0, maxX));
        notebook.setLayoutY(clamp(notebook.getLayoutY(), 0, maxY));
    }

    /** Close the notebook window from the header minus button. */
    @FXML
    private void onToggleNotebook() {
        if (notebook == null) {
            return;
        }
        isVisible = false;
        notebook.setVisible(false);
        notebook.setManaged(false);
    }

    /** Shows the tips notebook (makes it visible if currently hidden). */
    public void showTips() {
        if (notebook == null) {
            return;
        }

        if (!isVisible) {
            isVisible = true;
            notebook.setVisible(true);
            notebook.setManaged(true);
        }

        if (tipsContent != null) {
            tipsContent.setVisible(true);
            tipsContent.setManaged(true);
        }

        if (toggleButton != null) {
            toggleButton.setText("−");
        }
    }

    /**
     * Calculates the scaled width of the notebook node based on its current bounds and scale
     * factor.
     *
     * @return The scaled width of the notebook node.
     */
    private double scaledNodeWidth() {
        double width = notebook.getBoundsInLocal().getWidth();
        if (width <= 0) {
            width = notebook.prefWidth(-1);
        }
        return Math.max(0, width * notebook.getScaleX());
    }

    /**
     * Calculates the scaled height of the notebook node based on its current bounds and scale
     * factor.
     *
     * @return The scaled height of the notebook node.
     */
    private double scaledNodeHeight() {
        double height = notebook.getBoundsInLocal().getHeight();
        if (height <= 0) {
            height = notebook.prefHeight(-1);
        }
        return Math.max(0, height * notebook.getScaleY());
    }

    /**
     * Clamps a value between a minimum and maximum bound.
     *
     * @param value the value to clamp
     * @param min the lower bound
     * @param max the upper bound
     * @return the clamped value in the range [min, max]
     */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }
}
