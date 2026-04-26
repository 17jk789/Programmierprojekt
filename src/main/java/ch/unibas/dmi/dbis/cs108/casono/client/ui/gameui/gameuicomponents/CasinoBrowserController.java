package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Experimental embedded browser for Casono.
 *
 * <p>This class implements a simple embedded web browser based on {@link javafx.scene.web.WebView}.
 * The browser primarily serves as a utility within the game to display external content such as web
 * pages or videos.
 *
 * <p>Status The browser is currently in an experimental phase. Some security mechanisms are based
 * on experimental AI-driven recommendations and may change in future versions.
 *
 * <p>Purpose The browser is currently being used experimentally to: Explain poker rules directly
 * within the game Display help pages or documentation Play videos (e.g., tutorials or explanations)
 * via platforms such as YouTube
 *
 * <p>Security Mechanisms Since external websites can be loaded, some basic protective measures have
 * been integrated: - Mandatory HTTPS for websites - Whitelist for known domains - Warning for
 * unknown websites - JavaScript disabled by default (but can be enabled for Google, etc.) - Pop-up
 * blocker - Automatic cookie deletion upon closing
 */
public class CasinoBrowserController {

    /** Default constructor. Initializes the CasinoBrowserController. */
    public CasinoBrowserController() {
        // Intentionally left blank; controller initialization is FXML-driven.
    }

    private static final Set<String> TRUSTED_DOMAINS = new HashSet<>();

    private static final CookieManager COOKIE_MANAGER =
            new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER);

    private static final Logger LOGGER = LogManager.getLogger(CasinoBrowserController.class);

    private static final ObservableList<String> URL_SUGGESTIONS =
            FXCollections.observableArrayList(TRUSTED_DOMAINS);

    private static ContextMenu autoCompleteMenu = new ContextMenu();

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
    private static final int MAX_AUTOCOMPLETE_RESULTS = 5;

    static {
        CookieHandler.setDefault(COOKIE_MANAGER);

        TRUSTED_DOMAINS.add("wikipedia.org");
        TRUSTED_DOMAINS.add("youtube.com");
        TRUSTED_DOMAINS.add("github.com");
        TRUSTED_DOMAINS.add("search.brave.com");
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
        TRUSTED_DOMAINS.add("searx.be");
        TRUSTED_DOMAINS.add("startpage.com");
    }

    /** Aliases for common websites. */
    private static final java.util.Map<String, String> ALIASES =
            Map.ofEntries(
                    Map.entry("wiki", "wikipedia.org"),
                    Map.entry("yt", "youtube.com"),
                    Map.entry("bra", "search.brave.com"),
                    Map.entry("bs", "search.brave.com"),
                    Map.entry("gh", "github.com"),
                    Map.entry("google", "google.com"),
                    Map.entry("duck", "duckduckgo.com"),
                    Map.entry("bing", "bing.com"),
                    Map.entry("meta", "metager.de"),
                    Map.entry("mojeek", "mojeek.com"),
                    Map.entry("searx", "searx.be"),
                    Map.entry("startpage", "startpage.com"),
                    Map.entry("stack", "stackoverflow.com"),
                    Map.entry("so", "stackoverflow.com"),
                    Map.entry("oracle", "oracle.com"),
                    Map.entry("docs", "docs.oracle.com"),
                    Map.entry("mdn", "developer.mozilla.org"),
                    Map.entry("maven", "maven.apache.org"),
                    Map.entry("gradle", "gradle.org"),
                    Map.entry("spring", "spring.io"),
                    Map.entry("jetbrains", "jetbrains.com"),
                    Map.entry("uni", "unibas.ch"));

    /**
     * Resolves user input into a valid URL.
     *
     * @param input User input from the URL field, which can be a full URL, a domain name, or an
     *     alias.
     * @return A properly formatted URL string that can be loaded by the browser, or the original
     *     input if it cannot be resolved.
     */
    private static String resolveInputToUrl(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }

        input = input.trim();

        String alias = ALIASES.get(input.toLowerCase());
        if (alias != null) {
            return "https://" + alias;
        }

        if (input.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*")) {
            return input;
        }

        return "https://" + input;
    }

    private static boolean javascriptEnabled = false;

    /** Deletes all cookies stored during the current browser session. */
    private static void clearCookies() {
        try {
            COOKIE_MANAGER.getCookieStore().removeAll();
        } catch (Exception ignored) {
        }
    }

    /**
     * Opens a new browser window and loads a specified webpage.
     *
     * <p>If the webpage is not on the list of trusted domains, the user will be asked whether the
     * page should be loaded anyway.
     *
     * @param url the URL of the webpage to be loaded
     */
    public static void open(String url) {
        Platform.runLater(
                () -> {
                    Stage stage = new Stage();

                    WebView webView = new WebView();
                    WebEngine engine = webView.getEngine();

                    engine.locationProperty()
                            .addListener(
                                    (obs, oldUrl, newUrl) -> {
                                        if (isDownloadUrl(newUrl)) {
                                            LOGGER.warn("Download navigation blocked: " + newUrl);
                                            engine.getLoadWorker().cancel();
                                        }
                                    });

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
     * WebView container
     *
     * @param webView WebView content
     * @return StackPane container
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
     * @param engine Use WebEngine
     */
    public static void configurePopupBlocker(WebEngine engine) {
        engine.setCreatePopupHandler(
                config -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Popup blocked");
                    alert.setHeaderText(null);
                    alert.setContentText("Casono Browser blocked a security threat.");

                    try {
                        var stream = CasinoBrowserController.class.getResourceAsStream(LOGO_PATH);
                        Image logo = new Image(stream);

                        // Variable ‘streamM’ abbreviated to comply with the 100-character
                        // limit (LineLength)
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
                        LOGGER.error("The logo could not be loaded");
                    }

                    alert.showAndWait();
                    return null;
                });
    }

    /**
     * Load logos
     *
     * @param stage Window Stage
     * @return ImageView Logo
     */
    private static ImageView loadLogos(Stage stage) {
        ImageView browserLogo = new ImageView();

        try {
            var stream = CasinoBrowserController.class.getResourceAsStream(LOGO_PATH);
            Image logo = new Image(stream);

            // Variable ‘streamM’ abbreviated to comply with the 100-character limit (LineLength)
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
            LOGGER.error("The logo could not be loaded");
        }

        return browserLogo;
    }

    /**
     * URL field
     *
     * @param url Start URL
     * @return TextField input
     */
    public static TextField createUrlField(String url) {
        TextField urlField = new TextField(url);
        urlField.getStyleClass().add("gray-input-field");
        HBox.setHgrow(urlField, Priority.ALWAYS);

        urlField.textProperty()
                .addListener(
                        (obs, oldText, newText) -> {
                            if (newText == null || newText.isBlank()) {
                                autoCompleteMenu.hide();
                                return;
                            }

                            String input = newText.toLowerCase();

                            String aliasMatch = ALIASES.get(input);
                            if (aliasMatch != null) {
                                autoCompleteMenu.getItems().clear();

                                MenuItem item = new MenuItem(aliasMatch);
                                item.setOnAction(
                                        e -> {
                                            urlField.setText("https://" + aliasMatch);
                                            autoCompleteMenu.hide();
                                        });

                                autoCompleteMenu.getItems().add(item);
                                autoCompleteMenu.show(urlField, javafx.geometry.Side.BOTTOM, 0, 0);
                                return;
                            }

                            updateSuggestions(input, urlField);
                        });

        urlField.focusedProperty()
                .addListener(
                        (obs, oldVal, newVal) -> {
                            if (!newVal) {
                                autoCompleteMenu.hide();
                            }
                        });

        return urlField;
    }

    /**
     * Updates the autocomplete suggestions based on the current input in the URL field. Suggestions
     * are filtered from the list of trusted domains and sorted to prioritize those that start with
     * the input.
     *
     * @param input The current text input from the URL field, used to filter and sort suggestions.
     * @param urlField The TextField for the URL input, used to position the autocomplete menu and
     *     update its content.
     */
    private static void updateSuggestions(String input, TextField urlField) {
        autoCompleteMenu.getItems().clear();

        URL_SUGGESTIONS.stream()
                .filter(
                        domain ->
                                domain.toLowerCase().startsWith(input)
                                        || domain.toLowerCase().contains(input))
                .sorted(
                        (a, b) -> {
                            boolean aStarts = a.startsWith(input);
                            boolean bStarts = b.startsWith(input);
                            return Boolean.compare(!aStarts, !bStarts);
                        })
                .limit(MAX_AUTOCOMPLETE_RESULTS)
                .forEach(
                        domain -> {
                            MenuItem item = new MenuItem(domain);

                            item.setOnAction(
                                    e -> {
                                        urlField.setText("https://" + domain);
                                        autoCompleteMenu.hide();
                                    });

                            autoCompleteMenu.getItems().add(item);
                        });

        if (!autoCompleteMenu.getItems().isEmpty()) {
            autoCompleteMenu.show(urlField, javafx.geometry.Side.BOTTOM, 0, 0);
        } else {
            autoCompleteMenu.hide();
        }
    }

    /**
     * Security Label
     *
     * @return Label display
     */
    public static Label createSecurityLabel() {
        Label securityLabel = new Label("SAFE");
        securityLabel.getStyleClass().add("security-label");

        return securityLabel;
    }

    /**
     * JS Toggle
     *
     * @param engine Use WebEngine
     * @return Toggle button
     */
    public static Button createJsToggle(WebEngine engine) {
        Button jsToggle = new Button("JS TURN ON");
        jsToggle.getStyleClass().add("red-button");

        jsToggle.setOnAction(
                e -> {
                    javascriptEnabled = !javascriptEnabled;
                    engine.setJavaScriptEnabled(javascriptEnabled);

                    if (javascriptEnabled) {
                        jsToggle.setText("JS TURN OFF");
                        jsToggle.getStyleClass().removeAll("red-button");
                        jsToggle.getStyleClass().add("yellow-button");

                    } else {
                        jsToggle.setText("JS TURN ON");
                        jsToggle.getStyleClass().removeAll("yellow-button");
                        jsToggle.getStyleClass().add("red-button");
                    }
                });

        return jsToggle;
    }

    /**
     * Back Button
     *
     * @param engine Use WebEngine
     * @return Back button
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
     * Forward Button
     *
     * @param engine Use WebEngine
     * @return Forward button
     */
    public static Button createForwardButton(WebEngine engine) {
        Button fwdBtn = new Button(">");
        fwdBtn.getStyleClass().add("gray-button");
        fwdBtn.setOnAction(
                e -> {
                    int currentIndex = engine.getHistory().getCurrentIndex();
                    int lastIndex = engine.getHistory().getEntries().size() - 1;
                    if (currentIndex < lastIndex) {
                        engine.getHistory().go(1);
                    }
                });

        return fwdBtn;
    }

    /**
     * Reload Button
     *
     * @param engine Use WebEngine
     * @return Reload button
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
     * @param stage Window stage
     * @param webView WebView content
     * @return Close button
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
     * @param engine Use WebEngine
     * @param urlField URL text field
     * @param securityLabel Security label
     */
    public static void configureUrlEvents(
            WebEngine engine, TextField urlField, Label securityLabel) {
        urlField.setOnKeyPressed(
                e -> {
                    if (e.getCode() == KeyCode.ENTER) {
                        if (!autoCompleteMenu.isShowing()) {
                            String resolved = resolveInputToUrl(urlField.getText());
                            urlField.setText(resolved);
                            loadUrlSafely(engine, resolved, securityLabel);
                        }
                    }
                });
        engine.locationProperty().addListener((obs, o, n) -> urlField.setText(n));
    }

    /**
     * Create a scene
     *
     * @param taskbar Taskbar HBox
     * @param webContainer Web container
     * @return Scene window
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
     * @param scene Scene window
     * @param engine Use WebEngine
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
     * Loads a URL in the browser after performing basic security checks.
     *
     * <p>Before loading a page, the following checks are performed: - Verification of the protocol
     * (only HTTPS allowed) - Verification of the domain against a whitelist - Protection against
     * domain spoofing
     *
     * <p>If a domain is not classified as trustworthy, the user must confirm that the page may
     * still be opened.
     *
     * @param engine the browser's WebEngine renderer
     * @param url the web address to be loaded
     * @param securityLabel label for displaying the current security status
     */
    private static void loadUrlSafely(WebEngine engine, String url, Label securityLabel) {
        try {
            if (!url.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*")) {
                url = "https://" + url;
            }

            URI uri = new URI(url);
            String scheme = uri.getScheme();

            String fullUrl = uri.toString();

            if (isDownloadUrl(fullUrl)) {
                securityLabel.setText("BLOCKED DOWNLOAD");

                LOGGER.warn("Download blocked: " + fullUrl);
                return;
            }

            if ("file".equalsIgnoreCase(scheme)) {
                Path allowed =
                        Paths.get(
                                        System.getProperty("user.dir"),
                                        "documents",
                                        "docs",
                                        "game-engine",
                                        "manual.html")
                                .normalize();

                Path requested = Paths.get(uri).normalize();

                if (requested.equals(allowed)) {
                    securityLabel.setText("LOCAL OK");
                    engine.load(uri.toString());
                } else {
                    securityLabel.setText("BLOCKED");
                }
                return;
            }

            if (!"https".equalsIgnoreCase(scheme)) {
                securityLabel.setText("BLOCKED");
                return;
            }

            String host = uri.getHost();
            if (host == null || host.isBlank()) {
                securityLabel.setText("ERROR");
                return;
            }

            boolean trusted =
                    TRUSTED_DOMAINS.stream()
                            .anyMatch(domain -> host.equals(domain) || host.endsWith("." + domain));

            if (!trusted) {
                if (!showUnknownWebsiteAlert(host)) {
                    securityLabel.setText("BLOCKED");
                    return;
                }
                securityLabel.setText("UNBEKANNT");
            } else {
                securityLabel.setText("SAFE");
            }

            engine.load(uri.toString());

        } catch (Exception e) {
            securityLabel.setText("INVALID");
        }
    }

    /**
     * Displays a warning.
     *
     * @param host Website host
     * @return OK pressed
     */
    private static boolean showUnknownWebsiteAlert(String host) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Unknown website");
        alert.setHeaderText("This website is unknown");
        String content =
                host
                        + "\n\nThis site has not been verified by the Casono Browser.\n"
                        + "Do you still want to open it?";
        alert.setContentText(content);

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

    /**
     * Checks if a given URL points to a downloadable file based on its extension.
     *
     * @param url the URL to check
     * @return true if the URL is likely a download link, false otherwise
     */
    private static boolean isDownloadUrl(String url) {
        if (url == null) {
            return false;
        }

        return url.matches(".*\\.(exe|zip|dmg|msi|apk|jar|pdf)(\\?.*)?$");
    }
}
