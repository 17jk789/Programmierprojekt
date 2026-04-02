package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

/**
 * Manages the grid for lobby buttons and rendering. Uses LobbyButtonTranslationManager for mapping
 * ButtonID to LobbyID.
 */
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import ch.unibas.dmi.dbis.cs108.casono.client.network.LobbyClient;
import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the grid for lobby buttons and rendering. Uses
 * LobbyButtonTranslationManager for mapping
 * ButtonID to LobbyID.
 */
public class LobbyButtonGridManager {
    private static final double BUTTON_WIDTH_MARGIN = 20.0;
    private static final double BUTTON_MIN_SIZE = 10.0;
    private static final Logger LOGGER = LogManager.getLogger(LobbyButtonGridManager.class);

    /** GridPane for the button grid. */
    private final GridPane gridPane;

    /** Manager for mapping ButtonID to LobbyID. */
    private final LobbyButtonTranslationManager translationManager;

    /** Number of rows in the grid. */
    private static final int ROWS = 2;

    /** Number of columns in the grid. */
    private static final int COLS = 4;

    /** Path to the button image. */
    private static final String BUTTON_IMAGE_PATH = "/images/logo.png";

    /** Image for a lobby in CREATED state. */
    /** Default fallback image. */
    private static final String BUTTON_FALLBACK_IMAGE = "/images/lobbypictures/error.png";

    /**
     * Template for per-button images. Use: button index and status
     * (created|running). Example:
     * /images/lobby_1_created.png
     */
    private static final String BUTTON_IMAGE_TEMPLATE = "/images/lobbypictures/lobby_%d_%s.png";

    /** Cache for loaded Images keyed by resource path. */
    private final ConcurrentHashMap<String, Image> imageCache = new ConcurrentHashMap<>();

    /** Max random lobby id. */
    private static final int MAX_RANDOM_LOBBY_ID = 10000;

    private final LobbyClient lobbyClient;

    /**
     * Constructor for the GridManager.
     *
     * @param gridPane           the GridPane for rendering
     * @param translationManager the manager for mapping ButtonID to LobbyID
     */
    public LobbyButtonGridManager(
            GridPane gridPane, LobbyButtonTranslationManager translationManager) {
        this(gridPane, translationManager, createDefaultLobbyClient());
    }

    /**
     * Constructor that accepts a `LobbyClient` implementation.
     */
    public LobbyButtonGridManager(
            GridPane gridPane, LobbyButtonTranslationManager translationManager, LobbyClient lobbyClient) {
        this.gridPane = gridPane;
        // Always use the singleton
        this.translationManager = LobbyButtonTranslationManager.getInstance();
        this.lobbyClient = lobbyClient;
    }

    private static LobbyClient createDefaultLobbyClient() {
        String host = System.getProperty("casono.server.host", "127.0.0.1");
        int port = 1337;
        try {
            port = Integer.parseInt(System.getProperty("casono.server.port", "1337"));
        } catch (NumberFormatException e) {
            // use default port if parsing fails
        }
        return new LobbyClient(new ClientService(host, port));
    }

    /**
     * Renders all lobby buttons in the grid. Creates a button for each mapping with
     * image and event
     * handler.
     */
    public void renderLobbyButtons() {
        gridPane.getChildren().clear();
        int index = 0;
        Map<Integer, Integer> mapping = translationManager.getButtonIdToLobbyId();
        if (mapping.isEmpty()) {
            // No buttons to render
            return;
        }
        for (Map.Entry<Integer, Integer> entry : mapping.entrySet()) {
            int buttonId = entry.getKey();
            int lobbyId = entry.getValue();
            Button btn = new Button();
            btn.setId("lobbyBtn-" + buttonId);
            // Set image based on lobby status (initial)
            LobbyStatus initialStatus = getLobbyStatus(lobbyId);
            Image image = safeLoadImage(getImagePathForButton(buttonId, initialStatus));
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            // Dynamic width: bind to the cell size
            imageView
                    .fitWidthProperty()
                    .bind(gridPane.widthProperty().divide(COLS).subtract(BUTTON_WIDTH_MARGIN));
            imageView.setSmooth(true);
            btn.setGraphic(imageView);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setMaxHeight(Double.MAX_VALUE);
            btn.setMinWidth(BUTTON_MIN_SIZE);
            btn.setMinHeight(BUTTON_MIN_SIZE);
            GridPane.setHgrow(btn, javafx.scene.layout.Priority.ALWAYS);
            GridPane.setVgrow(btn, javafx.scene.layout.Priority.ALWAYS);
            btn.setOnAction(
                    e -> {
                        Integer targetLobbyId = translationManager.getLobbyIdForButton(buttonId);
                        if (targetLobbyId != null) {
                            joinLobby(targetLobbyId);
                        }
                    });
            int row = index / COLS;
            int col = index % COLS;
            gridPane.add(btn, col, row);
            index++;
        }
    }

    /** Possible lobby statuses. */
    private enum LobbyStatus {
        CREATED,
        RUNNING
    }

    /**
     * Return the current status of a lobby.
     *
     * @param lobbyId the lobby id to query
     * @return the lobby status (mapped from server string; CREATED or RUNNING)
     */
    public LobbyStatus getLobbyStatus(int lobbyId) {
        String serverStatus = lobbyClient.fetchLobbyStatusString(lobbyId);
        LobbyStatus parsed = parseLobbyStatus(serverStatus);
        if (parsed == null) {
            // Defensive fallback
            LOGGER.error("Unrecognized lobby status '{}' for lobby {}. Defaulting to CREATED.", serverStatus, lobbyId);
            return LobbyStatus.CREATED;
        }
        return parsed;
    }

    /**
     * Parse a status string returned by the server into the local enum.
     * Accepts case-insensitive values like "created" / "CREATED" / "running".
     * Returns null if the string is not recognized.
     */
    private LobbyStatus parseLobbyStatus(String statusStr) {
        if (statusStr == null)
            return null;
        try {
            return LobbyStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String getImagePathForStatus(LobbyStatus status) {
        return status == LobbyStatus.CREATED
                ? String.format(BUTTON_IMAGE_TEMPLATE, 0, "created")
                : String.format(BUTTON_IMAGE_TEMPLATE, 0, "running");
    }

    private String getImagePathForButton(int buttonId, LobbyStatus status) {
        String statusStr = status == LobbyStatus.CREATED ? "created" : "running";
        return String.format(BUTTON_IMAGE_TEMPLATE, buttonId, statusStr);
    }

    private Image safeLoadImage(String path) {
        // Return cached image if present
        Image cached = imageCache.get(path);
        if (cached != null) {
            return cached;
        }
        // Attempt to load the requested resource
        java.io.InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            LOGGER.debug(
                    "Image resource not found: {}. Falling back to {}",
                    path,
                    BUTTON_FALLBACK_IMAGE);
            is = getClass().getResourceAsStream(BUTTON_FALLBACK_IMAGE);
        }
        Image loaded = null;
        try {
            if (is != null) {
                loaded = new Image(is);
            } else {
                LOGGER.error(
                        "Both requested image '{}' and fallback '{}' are missing. No image will be set.",
                        path,
                        BUTTON_FALLBACK_IMAGE);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load image '{}'", path, e);
        }
        if (loaded == null) {
            // leave
            // null
        }
        if (loaded != null) {
            imageCache.put(path, loaded);
        }
        return loaded;
    }

    /** Update all lobby buttons' images according to the current lobby statuses. */
    public void updateLobbyButtonImages() {
        // Run on FX thread to be safe
        javafx.application.Platform.runLater(
                () -> {
                    Map<Integer, Integer> mapping = translationManager.getButtonIdToLobbyId();
                    for (Map.Entry<Integer, Integer> entry : mapping.entrySet()) {
                        int buttonId = entry.getKey();
                        int lobbyId = entry.getValue();
                        LobbyStatus status = getLobbyStatus(lobbyId);
                        String path = getImagePathForButton(buttonId, status);
                        for (Node node : gridPane.getChildren()) {
                            if (node instanceof Button
                                    && ("lobbyBtn-" + buttonId).equals(node.getId())) {
                                Button btn = (Button) node;
                                ImageView iv = new ImageView(safeLoadImage(path));
                                iv.setPreserveRatio(true);
                                iv.fitWidthProperty()
                                        .bind(
                                                gridPane.widthProperty()
                                                        .divide(COLS)
                                                        .subtract(BUTTON_WIDTH_MARGIN));
                                iv.setSmooth(true);
                                btn.setGraphic(iv);
                                break;
                            }
                        }
                    }
                });
    }

    /**
     * Creates a new lobby via the LobbyClient.
     *
     * @return The generated lobbyId
     */
    public int createLobby() {
        try {
            int lobbyId = lobbyClient.createLobby();
            LOGGER.info("Lobby created via LobbyClient: {}", lobbyId);
            return lobbyId;
        } catch (Exception e) {
            LOGGER.error("Failed to create lobby via LobbyClient: {}", e.getMessage());
            // Fallback: generate a local id so UI can continue to work in offline mode
            int lobbyId = (int) (Math.random() * MAX_RANDOM_LOBBY_ID + 1);
            LOGGER.warn("Falling back to local generated lobby id: {}", lobbyId);
            return lobbyId;
        }
    }

    /**
     * Placeholder for joining a lobby.
     *
     * @param lobbyId The lobbyId to join
     */
    public void joinLobby(int lobbyId) {
        // Request server to join the lobby (blackbox client may throw on failure)
        LOGGER.info("Joining lobby: {}", lobbyId);
        try {
            lobbyClient.joinLobby(lobbyId);
        } catch (Exception e) {
            LOGGER.error("LobbyClient failed to join lobby {}: {}", lobbyId, e.getMessage());
            return;
        }
        javafx.application.Platform.runLater(
                () -> {
                    // Hide lobby stage (do not close) so we can return later
                    javafx.scene.Scene scene = gridPane.getScene();
                    javafx.stage.Stage currentStage = (javafx.stage.Stage) scene.getWindow();
                    currentStage.hide();
                    // Prepare game stage and set a handler so that when it is closed the lobby is
                    // shown and updated
                    javafx.stage.Stage gameStage = new javafx.stage.Stage();
                    gameStage.setOnHidden(
                            ev -> {
                                try {
                                    currentStage.show();
                                    updateLobbyButtonImages();
                                } catch (Exception ex) {
                                    LOGGER.error(
                                            "Error while returning to lobby: {}", ex.getMessage());
                                }
                            });
                    // Start the Game UI using the prepared stage
                    try {
                        new ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.CasinoGameUI()
                                .start(gameStage);
                    } catch (Exception e) {
                        LOGGER.error("Error starting Game UI: {}", e.getMessage());
                        // If starting fails, show the lobby again
                        currentStage.show();
                    }
                });
    }

    /**
     * Getter for the GridPane.
     *
     * @return The GridPane for the button grid
     */
    public javafx.scene.layout.GridPane getGridPane() {
        return gridPane;
    }

}
