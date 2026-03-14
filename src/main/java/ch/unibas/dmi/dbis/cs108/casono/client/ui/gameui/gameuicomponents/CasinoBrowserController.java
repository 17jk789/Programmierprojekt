package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Experimenteller integrierter Browser für Casono.
 *
 * Diese Klasse implementiert einen einfachen eingebetteten Webbrowser
 * auf Basis von {@link javafx.scene.web.WebView}. Der Browser dient
 * primär als Hilfswerkzeug innerhalb des Spiels, um externe Inhalte
 * wie Webseiten oder Videos anzuzeigen.
 *
 * Status
 * Der Browser befindet sich derzeit in einer experimentellen Phase.
 * Einige Sicherheitsmechanismen basieren auf experimentellen
 * KI-gestützten Empfehlungen und können sich in zukünftigen
 * Versionen noch ändern.
 *
 * Zweck
 * Der Browser wird aktuell experimentell genutzt, um:
 * Pokerregeln direkt im Spiel zu erklären
 * Hilfeseiten oder Dokumentationen anzuzeigen
 * Videos (z.B. Tutorials oder Erklärungen) über Plattformen wie YouTube abzuspielen
 *
 * Sicherheitsmechanismen
 * Da externe Webseiten geladen werden können, wurden einige
 * grundlegende Schutzmaßnahmen integriert:
 * - HTTPS-Zwang für Webseiten
 * - Whitelist für bekannte Domains
 * - Warnung bei unbekannten Webseiten
 * - JavaScript standardmäßig deaktiviert (man kann es jedoch für Google etc. einschalten)
 * - Popup-Blocker
 * - Automatische Cookie-Löschung beim Schließen
 */
public class CasinoBrowserController {
    private static final Set<String> TRUSTED_DOMAINS = new HashSet<>();
    private static final CookieManager COOKIE_MANAGER =
            new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);

    static {
        CookieHandler.setDefault(COOKIE_MANAGER);

        TRUSTED_DOMAINS.add("wikipedia.org");
        TRUSTED_DOMAINS.add("github.com");
        TRUSTED_DOMAINS.add("oracle.com");
        TRUSTED_DOMAINS.add("stackoverflow.com");
        TRUSTED_DOMAINS.add("docs.oracle.com");
        TRUSTED_DOMAINS.add("developer.mozilla.org");
        TRUSTED_DOMAINS.add("maven.apache.org");
        TRUSTED_DOMAINS.add("gradle.org");
        TRUSTED_DOMAINS.add("spring.io");
        TRUSTED_DOMAINS.add("jetbrains.com");
        TRUSTED_DOMAINS.add("google.com");
        TRUSTED_DOMAINS.add("bing.com");
        TRUSTED_DOMAINS.add("duckduckgo.com");
        TRUSTED_DOMAINS.add("metager.de");
        TRUSTED_DOMAINS.add("unibas.ch");
        TRUSTED_DOMAINS.add("mojeek.com");
        TRUSTED_DOMAINS.add("searx.be ");
        TRUSTED_DOMAINS.add("startpage.com");
    }

    private static boolean javascriptEnabled = false;

    /**
     * Löscht alle gespeicherten Cookies der aktuellen Browser-Sitzung.
     */
    private static void clearCookies() {
        try {
            COOKIE_MANAGER.getCookieStore().removeAll();
        } catch (Exception ignored) {}
    }

    /**
     * Öffnet ein neues Browserfenster und lädt eine angegebene Webseite.
     *
     * Falls die Webseite nicht zur Liste vertrauenswürdiger Domains gehört,
     * wird der Benutzer gefragt, ob die Seite dennoch geladen werden soll.
     *
     * @param url die Startadresse der Webseite, die geladen werden soll
     */
    public static void open(String url) {
        Platform.runLater(() -> {
            Stage stage = new Stage();

            WebView webView = new WebView();
            WebEngine engine = webView.getEngine();

            engine.setJavaScriptEnabled(false);

            // Popup Blocker
            engine.setCreatePopupHandler(config -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Popup blockiert");
                alert.setHeaderText(null);
                alert.setContentText("Popup wurde aus Sicherheitsgründen blockiert.");

                try {
                    Image logo =
                            new Image(CasinoBrowserController.class.getResourceAsStream("/images/logoinverted.png"));
                    Image logomain =
                            new Image(CasinoBrowserController.class.getResourceAsStream("/images/logo.png"));

                    if (logo != null) {
                        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                        if (alertStage != null) {
                            alertStage.getIcons().add(logo);
                        }
                    }

                    if (logomain != null) {
                        ImageView logoView = new ImageView(logomain);
                        logoView.setFitHeight(40);
                        logoView.setPreserveRatio(true);
                        alert.setGraphic(logoView);
                    }
                } catch (Exception e) {
                    System.err.println("Logo konnte nicht geladen werden");
                }

                alert.showAndWait();
                return null;
            });

            webView.getStyleClass().add("web-view");

            StackPane webContainer = new StackPane(webView);
            webContainer.getStyleClass().add("browser-web-view");
            VBox.setVgrow(webContainer, Priority.ALWAYS);

            Rectangle clip = new Rectangle();
            clip.setArcWidth(40);
            clip.setArcHeight(40);
            webView.setClip(clip);

            webView.widthProperty().addListener((o,a,b)->clip.setWidth(b.doubleValue()));
            webView.heightProperty().addListener((o,a,b)->clip.setHeight(b.doubleValue()));

            ImageView browserLogo = new ImageView();

            try {
                Image logo =
                        new Image(CasinoBrowserController.class.getResourceAsStream("/images/logoinverted.png"));
                Image logomain =
                        new Image(CasinoBrowserController.class.getResourceAsStream("/images/logo.png"));

                if (logo != null) {
                    stage.getIcons().add(logo);
                }

                if (logomain != null) {
                    browserLogo.setImage(logomain);
                    browserLogo.setFitHeight(30);
                    browserLogo.setPreserveRatio(true);
                }
            } catch (Exception e) {
                System.err.println("Logo konnte nicht geladen werden");
            }

            // URL Feld
            TextField urlField = new TextField(url);
            urlField.getStyleClass().add("gray-input-field");
            HBox.setHgrow(urlField, Priority.ALWAYS);

            Label securityLabel = new Label("SICHER");
            securityLabel.getStyleClass().add("security-label");

            // JS Toggle
            Button jsToggle = new Button("JS EINSCHALTEN");
            jsToggle.getStyleClass().add("red-button");

            jsToggle.setOnAction(e -> {
                javascriptEnabled = !javascriptEnabled;
                engine.setJavaScriptEnabled(javascriptEnabled);

                if (javascriptEnabled) {
                    jsToggle.setText("JS AUSSCHALTEN");
                    jsToggle.getStyleClass().removeAll("red-button");
                    jsToggle.getStyleClass().add("yellow-button");

                } else {
                    jsToggle.setText("JS EINSCHALTEN");
                    jsToggle.getStyleClass().removeAll("yellow-button");
                    jsToggle.getStyleClass().add("red-button");
                }
            });

            // Navigation
            Button backBtn = new Button("<");
            backBtn.getStyleClass().add("gray-button");
            backBtn.setOnAction(e -> {
                if(engine.getHistory().getCurrentIndex() > 0){
                    engine.getHistory().go(-1);
                }
            });

            Button fwdBtn = new Button(">");
            fwdBtn.getStyleClass().add("gray-button");
            fwdBtn.setOnAction(e -> {
                if(engine.getHistory().getCurrentIndex() <
                        engine.getHistory().getEntries().size()-1){
                    engine.getHistory().go(1);
                }
            });

            // Reload Button
            Button reloadBtn = new Button("⟳");
            reloadBtn.getStyleClass().add("gray-button");
            reloadBtn.setOnAction(e -> engine.reload());

            // Close
            Button closeBtn = new Button("X");
            closeBtn.getStyleClass().add("red-button");
            closeBtn.setOnAction(e -> {
                webView.getEngine().load("about:blank");
                clearCookies();
                stage.close();

            });

            // URL Enter
            urlField.setOnAction(e ->
                    loadUrlSafely(engine, urlField.getText(), securityLabel));
            engine.locationProperty().addListener((obs,o,n)->urlField.setText(n));

            // Taskbar
            HBox taskbar = new HBox(
                    10,
                    browserLogo,
                    backBtn,
                    fwdBtn,
                    reloadBtn,
                    urlField,
                    jsToggle,
                    securityLabel,
                    closeBtn
            );

            taskbar.getStyleClass().add("taskbar-browser");
            taskbar.setAlignment(Pos.CENTER_LEFT);

            // Root
            VBox root = new VBox(10, taskbar, webContainer);
            root.getStyleClass().add("browser-root");
            root.setPadding(new Insets(10));
            Scene scene = new Scene(root,1200,800);
            var css = CasinoBrowserController.class.getResource("/ui-structure/casinogameui.css");

            if(css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            // F5 Reload
            scene.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case F5 -> engine.reload();
                }
            });

            stage.setScene(scene);
            stage.setTitle("Casono Browser");
            loadUrlSafely(engine, url, securityLabel);
            stage.show();
        });
    }

    /**
     * Lädt eine URL in den Browser, nachdem grundlegende Sicherheitsprüfungen
     * durchgeführt wurden.
     *
     * Vor dem Laden einer Seite werden folgende Prüfungen durchgeführt:
     * - Überprüfung des Protokolls (nur HTTPS erlaubt)
     * - Überprüfung der Domain gegen eine Whitelist
     * - Schutz vor Domain-Spoofing (Domain-Vortäuschung)
     *
     * Falls eine Domain nicht als vertrauenswürdig eingestuft wird,
     * muss der Benutzer bestätigen, dass die Seite dennoch geöffnet
     * werden darf.
     *
     * @param engine der WebEngine-Renderer des Browsers
     * @param url die zu ladende Webadresse
     * @param securityLabel Label zur Anzeige des aktuellen Sicherheitsstatus
     */
    private static void loadUrlSafely(WebEngine engine, String url, Label securityLabel) {
        try {
            if(!url.startsWith("http")) {
                url = "https://" + url;
            }

            URI uri = new URI(url);

            // HTTPS Pflicht
            if(!"https".equalsIgnoreCase(uri.getScheme())) {
                securityLabel.setText("BLOCKIERT");
                return;
            }

            String host = uri.getHost();
            if(host == null) {
                securityLabel.setText("ERROR");
                return;
            }

            // Sicherer Domain Check
            boolean trusted = TRUSTED_DOMAINS.stream().anyMatch(domain ->
                    host.equals(domain) || host.endsWith("." + domain));

            if(!trusted) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                alert.setTitle("Unbekannte Website");
                alert.setHeaderText("Diese Website ist nicht bekannt");
                alert.setContentText(
                        host + "\n\nDiese Seite ist nicht vom Casono Browser verifiziert.\nMöchten Sie sie trotzdem öffnen?"
                );

                Image logo = new Image(CasinoBrowserController.class.getResourceAsStream("/images/logoinverted.png"));
                if (logo != null) {
                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    if(stage != null) {
                        stage.getIcons().add(logo);
                    }
                    ImageView logomain = new ImageView(logo);
                    logomain.setFitHeight(50);
                    logomain.setPreserveRatio(true);
                    alert.setGraphic(logomain);
                }

                alert.getDialogPane().setPrefSize(600,300);
                Optional<ButtonType> result = alert.showAndWait();

                if(result.isEmpty() || result.get() != ButtonType.OK) {
                    securityLabel.setText("BLOCKIERT");
                    return;
                }

                securityLabel.setText("UNBEKANNT");
            } else {
                securityLabel.setText("SICHER");
            }
            engine.load(uri.toString());
        }

        catch (Exception e) {
            securityLabel.setText("ERROR");
        }
    }
}