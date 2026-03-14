package ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controller-Klasse für das Chat-System innerhalb der Spieloberfläche.
 *
 * <p>Verwaltet das Anzeigen von Chatnachrichten, das Eingabefeld für eigene Nachrichten sowie den
 * Senden-Button. Unterstützt drei Arten von Nachrichten: - Player-to-Player (Privat) - Lobby-Chat
 * (Raum) - Globaler Chat (Serverweit)
 *
 * <p>Nachrichten werden in einem {@link VBox}-Container als {@link Label} angezeigt. Eigene
 * Nachrichten werden über {@link #onSendToNetwork(String)} an das Netzwerkprotokoll weitergeleitet,
 * während eingehende Nachrichten über {@link #receiveMessage(String, String)} verarbeitet und
 * angezeigt werden.
 *
 * <p>Hinweis: Einige TODOs stehen in der zugehörigen FXML-Datei
 */
public class ChatController {

    @FXML private VBox chatVBox;

    @FXML private TextField inputField;

    @FXML private Button sendButton;

    @FXML private ScrollPane chatScrollPane;

    private static final int CHAT_PADDING = 20;

    /** Initialisiert den ChatController nach dem Laden der FXML. */
    public void initialize() {
        inputField.setOnAction(event -> sendMessage());
        chatScrollPane.vvalueProperty().bind(chatVBox.heightProperty());
    }

    /**
     * Diese Methode wird vom Senden-Button oder Enter ausgelöst. Sie gibt die eigene Nachricht an
     * das Netzwerkprotokoll weiter.
     */
    @FXML
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            inputField.clear();

            // Hier wird die eigene Nachricht ans Netzwerkprotokoll übergeben
            onSendToNetwork(message);
        }
    }

    /**
     * Diese Funktion muss vom Netzwerkprotokoll aufgerufen werden, wenn eine neue Nachricht von
     * einem anderen Spieler kommt.
     *
     * @param player Name des Spielers
     * @param message Nachricht des Spielers
     */
    public void receiveMessage(String player, String message) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        Label label = new Label("[" + time + "] " + player + ": " + message);
        label.getStyleClass().add("info-text");
        label.setWrapText(true); // Zeilenumbruch aktivieren
        label.maxWidthProperty().bind(chatVBox.widthProperty().subtract(CHAT_PADDING));
        chatVBox.getChildren().add(label);
    }

    /**
     * Schnittstelle zum Netzwerkprotokoll. Diese Funktion wird automatisch aufgerufen, wenn der
     * Benutzer eine eigene Nachricht sendet.
     *
     * @param message Nachricht, die der Benutzer abgeschickt hat
     */
    public void onSendToNetwork(String message) {
        // TODO: Netzwerkcode einfügen
        receiveMessage("Du", message);
    }
}
