package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * Experimenteller integrierter Browser für Casono.
 *
 * <p>Diese Klasse implementiert einen einfachen eingebetteten Webbrowser auf Basis von {@link
 * javafx.scene.web.WebView}. Der Browser dient primär als Hilfswerkzeug innerhalb des Spiels, um
 * externe Inhalte wie Webseiten oder Videos anzuzeigen.
 *
 * <p>Status Der Browser befindet sich derzeit in einer experimentellen Phase. Einige
 * Sicherheitsmechanismen basieren auf experimentellen KI-gestützten Empfehlungen und können sich in
 * zukünftigen Versionen noch ändern.
 *
 * <p>Zweck Der Browser wird aktuell experimentell genutzt, um: Pokerregeln direkt im Spiel zu
 * erklären Hilfeseiten oder Dokumentationen anzuzeigen Videos (z.B. Tutorials oder Erklärungen)
 * über Plattformen wie YouTube abzuspielen
 *
 * <p>Sicherheitsmechanismen Da externe Webseiten geladen werden können, wurden einige grundlegende
 * Schutzmaßnahmen integriert: - HTTPS-Zwang für Webseiten - Whitelist für bekannte Domains -
 * Warnung bei unbekannten Webseiten - JavaScript standardmäßig deaktiviert (man kann es jedoch für
 * Google etc. einschalten) - Popup-Blocker - Automatische Cookie-Löschung beim Schließen
 */
public class CasinoBrowserController {

    /** Standardkonstruktor. Initialisiert den CasinoBrowserController. */
    public CasinoBrowserController() {
        // Intentionally left blank; controller initialization is FXML-driven.
    }
    private static final Set<String> TRUSTED_DOMAINS = new HashSet<>();

    private static final CookieManager COOKIE_MANAGER =
            new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);

    private static final org.apache.logging.log4j.Logger LOGGER =
            org.apache.logging.log4j.LogManager.getLogger(CasinoBrowserController.class);

    private static final int LOGO_HEIGHT = 40;
    private static final int CORNER_RADIUS = 40;
    private static final int HBOX_SPACIN = 10;
    private static final int VBOX_SPACING = 10;
    private static final int PADDING_SIZE = 10;
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    private static final int ALERT_WIDTH = 600;
    private static final int ALERT_HEIGHT = 300;
    private static final String LOGO_PATH = "/images/logoinverted.png";
    private static final String LOGO_PATH_MAIN = "/images/logo.png";

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

    /** Löscht alle gespeicherten Cookies der aktuellen Browser-Sitzung. */
    private static void clearCookies() {
        try {
            COOKIE_MANAGER.getCookieStore().removeAll();
        } catch (Exception ignored) {
        }
    }

    /**
     * Öffnet ein neues Browserfenster und lädt eine angegebene Webseite.
     *
     * <p>Falls die Webseite nicht zur Liste vertrauenswürdiger Domains gehört, wird der Benutzer
     * gefragt, ob die Seite dennoch geladen werden soll.
     *
     * @param url die Startadresse der Webseite, die geladen werden soll
     */
    public static void open(String url) {
        Platform.runLater(
                () -> {
                    Stage stage = new Stage();

                    WebView webView = new WebView();
                    WebEngine engine = webView.getEngine();

                    engine.setJavaScriptEnabled(false);

                    configurePopupBlocker(engine);

                    webView.getStyleClass().add("web-view");

                    StackPane webContainer = createWebContainer(webView);

                    Rectangle clip = new Rectangle();
                    clip.setArcWidth(CORNER_RADIUS);
                    clip.setArcHeight(CORNER_RADIUS);
                    webView.setClip(clip);

                    webView.widthProperty()
                            .addListener((o, a, b) -> clip.setWidth(b.doubleValue()));
                    webView.heightProperty()
                            .addListener((o, a, b) -> clip.setHeight(b.doubleValue()));

                    ImageView browserLogo = loadLogos(stage);

                    TextField urlField = createUrlField(url);
                    Label securityLabel = createSecurityLabel();

                    Button jsToggle = createJsToggle(engine);
                    Button backBtn = createBackButton(engine);
                    Button fwdBtn = createForwardButton(engine);
                    Button reloadBtn = createReloadButton(engine);
                    Button closeBtn = createCloseButton(stage, webView);
                    configureUrlEvents(engine, urlField, securityLabel);

                    HBox taskbar =
                            new HBox(
                                    HBOX_SPACIN,
                                    browserLogo,
                                    backBtn,
                                    fwdBtn,
                                    reloadBtn,
                                    urlField,
                                    jsToggle,
                                    securityLabel,
                                    closeBtn);

                    taskbar.getStyleClass().add("taskbar-browser");
                    taskbar.setAlignment(Pos.CENTER_LEFT);

                    Scene scene = createScene(taskbar, webContainer);

                    configureKeyEvents(scene, engine);

                    stage.setScene(scene);
                    stage.setTitle("Casono Browser");
                    loadUrlSafely(engine, url, securityLabel);
                    stage.show();
                });
    }

    /**
     * WebView Container
     *
     * @param webView WebView Inhalt
     * @return StackPane Container
     */
    public static StackPane createWebContainer(WebView webView) {
        StackPane webContainer = new StackPane(webView);
        webContainer.getStyleClass().add("browser-web-view");
        VBox.setVgrow(webContainer, Priority.ALWAYS);

        return webContainer;
    }

    /**
     * Popup Blocker
     *
     * @param engine WebEngine nutzen
     */
    public static void configurePopupBlocker(WebEngine engine) {
        engine.setCreatePopupHandler(
                config -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Popup blockiert");
                    alert.setHeaderText(null);
                    alert.setContentText("Popup wurde aus Sicherheitsgründen blockiert.");

                    try {
                        var stream = CasinoBrowserController.class.getResourceAsStream(LOGO_PATH);
                        Image logo = new Image(stream);

                        // Variable 'streamM' abgekürzt, um das 100-Zeichen-Limit (LineLength)
                        // einzuhalten
                        var streamM =
                                CasinoBrowserController.class.getResourceAsStream(LOGO_PATH_MAIN);
                        Image logomain = new Image(streamM);

                        if (logo != null) {
                            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                            if (alertStage != null) {
                                alertStage.getIcons().add(logo);
                            }
                        }

                        if (logomain != null) {
                            ImageView logoView = new ImageView(logomain);
                            logoView.setFitHeight(LOGO_HEIGHT);
                            logoView.setPreserveRatio(true);
                            alert.setGraphic(logoView);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Logo konnte nicht geladen werden");
                    }

                    alert.showAndWait();
                    return null;
                });
    }

    /**
     * Logos laden
     *
     * @param stage Fenster Stage
     * @return ImageView Logo
     */
    private static ImageView loadLogos(Stage stage) {
        ImageView browserLogo = new ImageView();

        try {
            var stream = CasinoBrowserController.class.getResourceAsStream(LOGO_PATH);
            Image logo = new Image(stream);

            // Variable 'streamM' abgekürzt, um das 100-Zeichen-Limit (LineLength)
            // einzuhalten
            var streamM = CasinoBrowserController.class.getResourceAsStream(LOGO_PATH_MAIN);
            Image logomain = new Image(streamM);

            if (logo != null) {
                stage.getIcons().add(logo);
            }

            if (logomain != null) {
                browserLogo.setImage(logomain);
                browserLogo.setFitHeight(LOGO_HEIGHT);
                browserLogo.setPreserveRatio(true);
            }
        } catch (Exception e) {
            LOGGER.error("Logo konnte nicht geladen werden");
        }

        return browserLogo;
    }

    /**
     * URL Feld
     *
     * @param url Start URL
     * @return TextField Eingabe
     */
    public static TextField createUrlField(String url) {
        TextField urlField = new TextField(url);
        urlField.getStyleClass().add("gray-input-field");
        HBox.setHgrow(urlField, Priority.ALWAYS);

        return urlField;
    }

    /**
     * Sicherheits Label
     *
     * @return Label Anzeige
     */
    public static Label createSecurityLabel() {
        Label securityLabel = new Label("SICHER");
        securityLabel.getStyleClass().add("security-label");

        return securityLabel;
    }

    /**
     * JS Toggle
     *
     * @param engine WebEngine nutzen
     * @return Button Umschalten
     */
    public static Button createJsToggle(WebEngine engine) {
        Button jsToggle = new Button("JS EINSCHALTEN");
        jsToggle.getStyleClass().add("red-button");

        jsToggle.setOnAction(
                e -> {
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

        return jsToggle;
    }

    /**
     * Zurück Button
     *
     * @param engine WebEngine nutzen
     * @return Button Zurück
     */
    public static Button createBackButton(WebEngine engine) {
        Button backBtn = new Button("<");
        backBtn.getStyleClass().add("gray-button");
        backBtn.setOnAction(
                e -> {
                    if (engine.getHistory().getCurrentIndex() > 0) {
                        engine.getHistory().go(-1);
                    }
                });

        return backBtn;
    }

    /**
     * Vorwärts Button
     *
     * @param engine WebEngine nutzen
     * @return Button Vorwärts
     */
    public static Button createForwardButton(WebEngine engine) {
        Button fwdBtn = new Button(">");
        fwdBtn.getStyleClass().add("gray-button");
        fwdBtn.setOnAction(
                e -> {
                    if (engine.getHistory().getCurrentIndex()
                            < engine.getHistory().getEntries().size() - 1) {
                        engine.getHistory().go(1);
                    }
                });

        return fwdBtn;
    }

    /**
     * Reload Button
     *
     * @param engine WebEngine nutzen
     * @return Button Reload
     */
    public static Button createReloadButton(WebEngine engine) {
        Button reloadBtn = new Button("⟳");
        reloadBtn.getStyleClass().add("gray-button");
        reloadBtn.setOnAction(e -> engine.reload());
        return reloadBtn;
    }

    /**
     * Close Button
     *
     * @param stage Fenster Stage
     * @param webView WebView Inhalt
     * @return Button Schließen
     */
    public static Button createCloseButton(Stage stage, WebView webView) {
        Button closeBtn = new Button("X");
        closeBtn.getStyleClass().add("red-button");
        closeBtn.setOnAction(
                e -> {
                    webView.getEngine().load("about:blank");
                    clearCookies();
                    stage.close();
                });

        return closeBtn;
    }

    /**
     * URL Events
     *
     * @param engine WebEngine nutzen
     * @param urlField URL Textfeld
     * @param securityLabel Sicherheits Label
     */
    public static void configureUrlEvents(
            WebEngine engine, TextField urlField, Label securityLabel) {
        urlField.setOnAction(e -> loadUrlSafely(engine, urlField.getText(), securityLabel));
        engine.locationProperty().addListener((obs, o, n) -> urlField.setText(n));
    }

    /**
     * Scene erstellen
     *
     * @param taskbar Taskbar HBox
     * @param webContainer Web Container
     * @return Scene Fenster
     */
    public static Scene createScene(HBox taskbar, StackPane webContainer) {
        VBox root = new VBox(VBOX_SPACING, taskbar, webContainer);
        root.getStyleClass().add("browser-root");
        root.setPadding(new Insets(PADDING_SIZE));

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        var css = CasinoBrowserController.class.getResource("/ui-structure/Casinogameui.css");

        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        return scene;
    }

    /**
     * Key Events
     *
     * @param scene Scene Fenster
     * @param engine WebEngine nutzen
     */
    public static void configureKeyEvents(Scene scene, WebEngine engine) {
        scene.setOnKeyPressed(
                event -> {
                    if (event.getCode() == KeyCode.F5) {
                        engine.reload();
                    }
                });
    }

    /**
     * Lädt eine URL in den Browser, nachdem grundlegende Sicherheitsprüfungen durchgeführt wurden.
     *
     * <p>Vor dem Laden einer Seite werden folgende Prüfungen durchgeführt: - Überprüfung des
     * Protokolls (nur HTTPS erlaubt) - Überprüfung der Domain gegen eine Whitelist - Schutz vor
     * Domain-Spoofing (Domain-Vortäuschung)
     *
     * <p>Falls eine Domain nicht als vertrauenswürdig eingestuft wird, muss der Benutzer
     * bestätigen, dass die Seite dennoch geöffnet werden darf.
     *
     * @param engine der WebEngine-Renderer des Browsers
     * @param url die zu ladende Webadresse
     * @param securityLabel Label zur Anzeige des aktuellen Sicherheitsstatus
     */
    private static void loadUrlSafely(WebEngine engine, String url, Label securityLabel) {
        try {
            if (!url.startsWith("http")) {
                url = "https://" + url;
            }

            URI uri = new URI(url);

            // HTTPS Pflicht
            if (!"https".equalsIgnoreCase(uri.getScheme())) {
                securityLabel.setText("BLOCKIERT");
                return;
            }

            String host = uri.getHost();
            if (host == null) {
                securityLabel.setText("ERROR");
                return;
            }

            // Sicherer Domain Check
            boolean trusted =
                    TRUSTED_DOMAINS.stream()
                            .anyMatch(domain -> host.equals(domain) || host.endsWith("." + domain));

            if (!trusted) {
                if (!showUnknownWebsiteAlert(host)) {
                    securityLabel.setText("BLOCKIERT");
                    return;
                }
                securityLabel.setText("UNBEKANNT");
            } else {
                securityLabel.setText("SICHER");
            }
            engine.load(uri.toString());
        } catch (Exception e) {
            securityLabel.setText("ERROR");
        }
    }

    /**
     * Zeigt Warnung an.
     *
     * @param host Website Host
     * @return OK gedrückt
     */
    private static boolean showUnknownWebsiteAlert(String host) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Unbekannte Website");
        alert.setHeaderText("Diese Website ist nicht bekannt");
        alert.setContentText(
                host
                        + "\n\nDiese Seite ist nicht vom "
                        + "Casono Browser verifiziert.\nMöchten Sie sie trotzdem öffnen?");

        var stream = CasinoBrowserController.class.getResourceAsStream(LOGO_PATH);
        Image logo = new Image(stream);
        if (logo != null) {
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            if (stage != null) {
                stage.getIcons().add(logo);
            }
            ImageView logomain = new ImageView(logo);
            logomain.setFitHeight(LOGO_HEIGHT);
            logomain.setPreserveRatio(true);
            alert.setGraphic(logomain);
        }

        alert.getDialogPane().setPrefSize(ALERT_WIDTH, ALERT_HEIGHT);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return false;
        }

        return true;
    }
}
