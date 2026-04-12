package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui;

import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import ch.unibas.dmi.dbis.cs108.casono.client.network.LobbyClient;
import ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.CasinoGameUI;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LobbyButtonGridManager {

    private static final double BTN_WIDTH_MRG = 20.0;
    private static final double BUTTON_MIN_SIZE = 10.0;
    private static final int REFRESH_INTERVAL_SECONDS = 5;
    private static final int INITIAL_DELAY_SECONDS = 5;
    private static final int COLS = 4;
    private static final int MAX_BUTTONS = 8;

    private static final Logger LOGGER = LogManager.getLogger(LobbyButtonGridManager.class);

    private static final String BUTTON_FALLBACK_IMAGE = "/images/lobbypictures/error.png";

    private static final String BUTTON_IMAGE_TEMPLATE = "/images/lobbypictures/lobby_%d_%s.png";

    private final GridPane gridPane;
    private final LobbyButtonTranslationManager translationManager;
    private final LobbyClient lobbyClient;

    private final ConcurrentHashMap<String, Image> imageCache = new ConcurrentHashMap<>();

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public LobbyButtonGridManager(
            GridPane gridPane,
            LobbyButtonTranslationManager translationManager,
            LobbyClient lobbyClient) {

        this.gridPane = gridPane;
        this.translationManager = LobbyButtonTranslationManager.getInstance();
        this.lobbyClient = lobbyClient;

        startPeriodicRefresh(INITIAL_DELAY_SECONDS, REFRESH_INTERVAL_SECONDS);
    }

    public LobbyButtonGridManager(
            GridPane gridPane,
            LobbyButtonTranslationManager translationManager,
            ClientService clientService) {

        this(gridPane, translationManager, new LobbyClient(clientService));

        // Subscribe to server-initiated events so closed lobbies are removed
        // immediately
        clientService.addEventListener(this::handleClientServiceEvent);
    }

    private static record EventInfo(String event, String lobbyId) {}

    private EventInfo extractEventInfo(List<RequestParameter> params) {
        String evt = null;
        String lidStr = null;
        for (RequestParameter p : params) {
            if ("EVENT".equalsIgnoreCase(p.key())) {
                evt = p.value();
            } else if ("LOBBY_ID".equalsIgnoreCase(p.key())) {
                lidStr = p.value();
            }
        }
        return new EventInfo(evt, lidStr);
    }

    private Integer tryParseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void handleLobbyClosed(int lid) {
        LOGGER.info(
                "LOBBY_CLOSED event for lobby {} — mapping before: {}",
                lid,
                translationManager.getButtonIdToLobbyId());
        // find button id(s) for this lobby and remove mapping
        Map<Integer, Integer> mapping = translationManager.getButtonIdToLobbyId();
        Integer toRemove = null;
        for (Map.Entry<Integer, Integer> e : mapping.entrySet()) {
            if (e.getValue() != null && e.getValue().intValue() == lid) {
                toRemove = e.getKey();
                break;
            }
        }
        if (toRemove != null) {
            LOGGER.info(
                    "Removing mapping for button {} -> lobby {} (LOBBY_CLOSED event)",
                    toRemove,
                    lid);
            translationManager.removeLobbyButton(toRemove);
            LOGGER.debug("Mapping after removal: {}", translationManager.getButtonIdToLobbyId());
            javafx.application.Platform.runLater(this::renderLobbyButtons);
        }
    }

    private void handleLobbyCreated(int lid) {
        LOGGER.info(
                "LOBBY_CREATED event for lobby {} — mapping before: {}",
                lid,
                translationManager.getButtonIdToLobbyId());

        Map<Integer, Integer> mapping = translationManager.getButtonIdToLobbyId();

        // If this lobby is already known, nothing to do
        if (mapping.containsValue(lid)) {
            LOGGER.debug("Lobby {} already mapped, skipping", lid);
            return;
        }

        // find next free button id (1..MAX_BUTTONS)
        for (int candidate = 1; candidate <= MAX_BUTTONS; candidate++) {
            if (!mapping.containsKey(candidate)) {
                try {
                    translationManager.addLobbyButton(candidate, lid);
                    LOGGER.info("Mapped new lobby {} to button {}", lid, candidate);
                    LOGGER.debug(
                            "Mapping after add: {}", translationManager.getButtonIdToLobbyId());
                    javafx.application.Platform.runLater(this::renderLobbyButtons);
                    break;
                } catch (Exception ex) {
                    // grid full? try next candidate
                    LOGGER.debug("Could not map created lobby {}: {}", lid, ex.getMessage());
                }
            }
        }
    }

    private void handleClientServiceEvent(List<String> lines) {
        EventInfo info = extractEventInfo(ClientService.convertToRequestParameters(lines));

        if (info.event() == null || info.lobbyId() == null) {
            return;
        }

        Integer lid = tryParseInt(info.lobbyId());
        if (lid == null) {
            return;
        }

        if ("LOBBY_CLOSED".equalsIgnoreCase(info.event())) {
            handleLobbyClosed(lid);
        } else if ("LOBBY_CREATED".equalsIgnoreCase(info.event())) {
            handleLobbyCreated(lid);
        }
    }

    private void startPeriodicRefresh(long initialDelay, long period) {
        scheduler.scheduleAtFixedRate(
                this::refreshMappings, initialDelay, period, TimeUnit.SECONDS);
    }

    private void refreshMappings() {
        Map<Integer, Integer> mapping = translationManager.getButtonIdToLobbyId();

        // Always attempt to sync with server-side lobby list so clients that start
        // after a lobby was created will discover it. This keeps the local mapping
        // in sync with the server without requiring a restart.
        List<LobbyClient.LobbyInfo> serverLobbies = null;
        java.util.Set<Integer> serverIds = new java.util.HashSet<>();
        try {
            serverLobbies = lobbyClient.getLobbyList();
            if (serverLobbies != null) {
                for (var li : serverLobbies) {
                    serverIds.add(li.id);
                }
            }
            reconcileWithServerLobbies(serverLobbies);
        } catch (Exception ex) {
            LOGGER.debug("Could not fetch lobby list: {}", ex.getMessage());
        }

        if (mapping.isEmpty()) {
            // mapping may still be empty after reconciliation
            if (translationManager.getButtonIdToLobbyId().isEmpty()) {
                return;
            }
            mapping = translationManager.getButtonIdToLobbyId();
        }

        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(mapping.entrySet());

        for (Map.Entry<Integer, Integer> e : entries) {
            int buttonId = e.getKey();
            Integer lobbyIdObj = e.getValue();
            if (lobbyIdObj == null) {
                continue;
            }
            int lobbyId = lobbyIdObj.intValue();

            CompletableFuture.supplyAsync(
                            () -> {
                                try {
                                    return lobbyClient.fetchLobbyStatusString(lobbyId);
                                } catch (Exception ex) {
                                    LOGGER.info("Lobby {} missing: {}", lobbyId, ex.getMessage());
                                    return null;
                                }
                            },
                            executor)
                    .thenAccept(
                            status -> {
                                // Do not remove a mapping just because the status fetch
                                // temporarily returned null. Only remove mappings when
                                // the server no longer lists the lobby (reconcileWithServerLobbies
                                // handles removals based on authoritative server list).
                                if (status == null) {
                                    if (!serverIds.contains(lobbyId)) {
                                        translationManager.removeLobbyButton(buttonId);
                                        javafx.application.Platform.runLater(
                                                this::renderLobbyButtons);
                                    } else {
                                        // treat as created/running later; keep mapping
                                        LOGGER.debug(
                                                "Missing status for lobby {} - keeping mapping",
                                                lobbyId);
                                    }
                                }
                            });
        }
    }

    private void reconcileWithServerLobbies(List<LobbyClient.LobbyInfo> serverLobbies) {
        if (serverLobbies == null) {
            return;
        }

        Map<Integer, Integer> mapping = translationManager.getButtonIdToLobbyId();

        // Build set of lobby ids returned by server
        java.util.Set<Integer> serverIds = new java.util.HashSet<>();
        for (var li : serverLobbies) {
            serverIds.add(li.id);
        }

        LOGGER.debug("Reconciling mappings. serverIds={} mapping={}", serverIds, mapping);

        // Remove mappings for lobbies that no longer exist
        java.util.List<Integer> toRemove = new java.util.ArrayList<>();
        for (var e : mapping.entrySet()) {
            Integer lid = e.getValue();
            if (lid == null) {
                continue;
            }
            if (!serverIds.contains(lid)) {
                toRemove.add(e.getKey());
            }
        }
        for (Integer bid : toRemove) {
            LOGGER.info(
                    "Reconciling: removing mapping for button {} (server no longer lists lobby)",
                    bid);
            translationManager.removeLobbyButton(bid);
        }

        // Add server lobbies that are not yet mapped
        for (var li : serverLobbies) {
            if (mapping.containsValue(li.id)) {
                continue;
            }
            // find next free button id (1..MAX_BUTTONS)
            for (int candidate = 1; candidate <= MAX_BUTTONS; candidate++) {
                if (!mapping.containsKey(candidate)) {
                    try {
                        translationManager.addLobbyButton(candidate, li.id);
                        LOGGER.info(
                                "Reconciling: added mapping button {} -> lobby {}",
                                candidate,
                                li.id);
                        break;
                    } catch (Exception ex) {
                        // grid full? try next candidate
                        LOGGER.debug(
                                "Could not add mapping for lobby {}: {}", li.id, ex.getMessage());
                    }
                }
            }
        }

        if (!toRemove.isEmpty() || !serverLobbies.isEmpty()) {
            javafx.application.Platform.runLater(this::renderLobbyButtons);
        }
    }

    public void renderLobbyButtons() {
        gridPane.getChildren().clear();

        Map<Integer, Integer> mapping = translationManager.getButtonIdToLobbyId();

        if (mapping.isEmpty()) {
            return;
        }

        List<Integer> buttonIds = new ArrayList<>(mapping.keySet());
        Collections.sort(buttonIds);

        for (int index = 0; index < buttonIds.size(); index++) {
            Integer buttonId = buttonIds.get(index);
            Integer lobbyIdObj = mapping.get(buttonId);
            if (lobbyIdObj == null) {
                continue;
            }
            int lobbyId = lobbyIdObj.intValue();

            Button btn = createLobbyButton(buttonId, lobbyId);

            int row = index / COLS;
            int col = index % COLS;

            gridPane.add(btn, col, row);
        }
    }

    private Button createLobbyButton(int buttonId, int lobbyId) {
        Button btn = new Button();
        btn.setId("lobbyBtn-" + buttonId);

        ImageView imageView = new ImageView(safeLoadImage(BUTTON_FALLBACK_IMAGE));

        imageView.setPreserveRatio(true);
        imageView
                .fitWidthProperty()
                .bind(gridPane.widthProperty().divide(COLS).subtract(BTN_WIDTH_MRG));

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

        loadLobbyImageAsync(btn, buttonId, lobbyId);

        return btn;
    }

    private void loadLobbyImageAsync(Button btn, int buttonId, int lobbyId) {

        CompletableFuture.supplyAsync(() -> lobbyClient.fetchLobbyStatusString(lobbyId), executor)
                .thenAccept(
                        statusStr -> {
                            LobbyStatus status = parseLobbyStatus(statusStr);

                            String path =
                                    getImagePathForLobby(
                                            lobbyId, status == null ? LobbyStatus.CREATED : status);

                            Image img = safeLoadImage(path);

                            javafx.application.Platform.runLater(
                                    () -> {
                                        ImageView iv = new ImageView(img);
                                        iv.setPreserveRatio(true);
                                        iv.fitWidthProperty()
                                                .bind(
                                                        gridPane.widthProperty()
                                                                .divide(COLS)
                                                                .subtract(BTN_WIDTH_MRG));
                                        btn.setGraphic(iv);
                                    });
                        });
    }

    private enum LobbyStatus {
        CREATED,
        RUNNING
    }

    private LobbyStatus parseLobbyStatus(String statusStr) {
        if (statusStr == null) {
            return null;
        }

        try {
            return LobbyStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String getImagePathForLobby(int lobbyId, LobbyStatus status) {

        String statusStr = status == LobbyStatus.CREATED ? "created" : "running";

        return String.format(BUTTON_IMAGE_TEMPLATE, lobbyId, statusStr);
    }

    private Image safeLoadImage(String path) {
        Image cached = imageCache.get(path);

        if (cached != null) {
            return cached;
        }

        java.io.InputStream is = getClass().getResourceAsStream(path);

        if (is == null) {
            is = getClass().getResourceAsStream(BUTTON_FALLBACK_IMAGE);
        }

        Image loaded = null;

        try {
            if (is != null) {
                loaded = new Image(is);
            }
        } catch (Exception e) {
            LOGGER.error("Image load failed: {}", path, e);
        }

        if (loaded != null) {
            imageCache.put(path, loaded);
        }

        return loaded;
    }

    public void updateLobbyButtonImages() {
        Map<Integer, Integer> mapping = translationManager.getButtonIdToLobbyId();

        if (mapping.isEmpty()) {
            return;
        }

        for (Integer buttonId : mapping.keySet()) {
            Integer lobbyIdObj = mapping.get(buttonId);
            if (lobbyIdObj == null) {
                continue;
            }
            int lobbyId = lobbyIdObj.intValue();

            CompletableFuture.supplyAsync(
                            () -> {
                                String statusStr = lobbyClient.fetchLobbyStatusString(lobbyId);

                                LobbyStatus status = parseLobbyStatus(statusStr);

                                return status == null ? LobbyStatus.CREATED : status;
                            },
                            executor)
                    .thenAccept(
                            status -> {
                                String path = getImagePathForLobby(lobbyId, status);

                                javafx.application.Platform.runLater(
                                        () -> {
                                            for (Node node : gridPane.getChildren()) {

                                                boolean isButton = node instanceof Button;
                                                boolean idMatches =
                                                        ("lobbyBtn-" + buttonId)
                                                                .equals(node.getId());

                                                if (isButton && idMatches) {
                                                    Button btn = (Button) node;

                                                    ImageView iv =
                                                            new ImageView(safeLoadImage(path));

                                                    iv.setPreserveRatio(true);
                                                    iv.fitWidthProperty()
                                                            .bind(
                                                                    gridPane.widthProperty()
                                                                            .divide(COLS)
                                                                            .subtract(
                                                                                    BTN_WIDTH_MRG));

                                                    btn.setGraphic(iv);
                                                    break;
                                                }
                                            }
                                        });
                            });
        }
    }

    public int createLobby() {
        try {
            int lobbyId = lobbyClient.createLobby();

            if (lobbyId <= 0) {
                throw new RuntimeException("Invalid lobby id: " + lobbyId);
            }

            return lobbyId;

        } catch (Exception e) {
            LOGGER.error("Create lobby failed: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void joinLobby(int lobbyId) {
        try {
            lobbyClient.joinLobby(lobbyId);
        } catch (Exception e) {
            LOGGER.error("Join failed: {}", e.getMessage());
            return;
        }

        javafx.application.Platform.runLater(
                () -> {
                    javafx.stage.Stage currentStage =
                            (javafx.stage.Stage) gridPane.getScene().getWindow();

                    currentStage.hide();

                    javafx.stage.Stage gameStage = new javafx.stage.Stage();

                    gameStage.setOnHidden(
                            ev -> {
                                currentStage.show();
                                refreshMappings();
                                updateLobbyButtonImages();
                            });

                    try {
                        var cs = lobbyClient.getClientService();

                        ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.CasinoGameUI.setClientService(cs);
                        ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.CasinoGameUI.setLobbyId(lobbyId);

                        String username = ch.unibas.dmi.dbis.cs108.casono.client.ClientApp.getSharedUsername();
                        if (username == null || username.isBlank()) {
                            username = "Guest-" + java.util.UUID.randomUUID().toString().substring(0, 8);
                        }
                        ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.CasinoGameUI.setUsername(username);

                        new ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.CasinoGameUI().start(gameStage);

                    } catch (Exception e) {
                        LOGGER.error("Game UI failed: {}", e.getMessage());
                        currentStage.show();
                    }
                });
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public LobbyClient getLobbyClient() {
        return lobbyClient;
    }
}
