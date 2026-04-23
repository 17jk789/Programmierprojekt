package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.ChatController;
import ch.unibas.dmi.dbis.cs108.casono.client.game.Card;
import ch.unibas.dmi.dbis.cs108.casono.client.game.GameService;
import ch.unibas.dmi.dbis.cs108.casono.client.game.GameState;
import ch.unibas.dmi.dbis.cs108.casono.client.game.Player;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerId;
import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui.ChatBoxController;
import ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents.PlayerStatusController;
import ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents.TaskbarController;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for the casino gaming area.
 *
 * <p>Responsible for: - the display of the poker table and the player interface, - the processing
 * of user input, - the interface to the game engine and the network protocol.
 *
 * <p>Notes: - The `onTableClick()` method currently serves only as test logic. It may no longer be
 * functional and will be replaced by the final game interaction in the future.
 */
public class CasinoGameController {

    private static final Logger LOGGER = Logger.getLogger(CasinoGameController.class.getName());

    @FXML private Label tableText;
    @FXML private VBox casinoTable;
    @FXML private HBox communityCardsBox;
    @FXML private VBox casinoTableInnerBox;
    @FXML private HBox playerCardsBox;
    @FXML private HBox potBox;
    @FXML private PlayerStatusController playerStatusController;
    @FXML private PlayerStatusController player1Controller;
    @FXML private PlayerStatusController player2Controller;
    @FXML private PlayerStatusController player3Controller;
    @FXML private VBox player1;
    @FXML private VBox player2;
    @FXML private VBox player3;
    @FXML private ImageView myDealerIcon;
    @FXML private AnchorPane chatContainer;

    private GameService gameService;
    private PlayerId myPlayerId;
    @FXML private TaskbarController taskbarIncludeController;
    private TaskbarController taskbarController;
    private Image dealerImage;
    private javafx.animation.Timeline timeline;
    private boolean gameStarted = false;
    private java.util.List<String> lastCommunityKeys = java.util.List.of();
    private java.util.List<String> lastMyCardKeys = java.util.List.of();
    private int lastPot = Integer.MIN_VALUE;
    private ChatController chatController;

    private static final int TOTAL_SLOTS = 5;
    private static final int PLAYER_SLOTS = 2;
    private int pot = 0;
    private static final String BACKSIDE = "/images/card-background-3.png";
    private static final String DEALER_IMAGE_PATH = "/images/chip-dealer-blue-3.png";
    private static final double TABLE_OFFSET_Y_FACTOR = 0.09;
    private static final double PLAYER_CARDS_OFFSET_Y_FACTOR = 0.35;
    private static final double MOVE_FROM_X = -40;
    private static final double MOVE_FROM_Y = -10;
    private static final double MOVE_TO_X = 0;
    private static final double MOVE_TO_Y = 0;
    private static final double FADE_FROM = 0;
    private static final double FADE_TO = 1;
    private static final double SCALE_FROM = 0.95;
    private static final double SCALE_TO = 1;
    private static final double SETTLE_FROM_Y = 0;
    private static final double SETTLE_TO_Y = 10;
    private static final boolean SETTLE_AUTO_REVERSE = true;
    private static final int SETTLE_CYCLE_COUNT = 2;
    private static final long ANIMATION_DURATION_MS = 350;
    private static final long SETTLE_DURATION_MS = 150;
    private static final long STAGGER_DELAY_MS = 120;
    private static final double HOVER_SCALE_ON = 1.08;
    private static final double HOVER_SCALE_OFF = 1.0;
    private static final double HOVER_LIFT_ON = -8;
    private static final double HOVER_LIFT_OFF = 0;
    private static final long HOVER_DURATION_MS = 180;
    private static final long UI_UPDATE_INTERVAL_SECONDS = 1;
    private static final int PLAYER_INDEX_0 = 0;
    private static final int PLAYER_INDEX_1 = 1;
    private static final int PLAYER_INDEX_2 = 2;
    private static final int DEALER_INDEX_PLAYER_1 = 0;
    private static final int DEALER_INDEX_PLAYER_2 = 1;
    private static final int DEALER_INDEX_PLAYER_3 = 2;
    private static final int DEALER_PLAYER_1 = 0;
    private static final int DEALER_PLAYER_2 = 1;
    private static final int DEALER_PLAYER_3 = 2;
    private static final int DEALER_MYSELF = 3;
    private static final double CARD_START_OPACITY = 0.0;
    private static final double CARD_START_SCALE = 0.85;
    private static final double COMMUNITY_CARD_HEIGHT_RATIO = 0.22;
    private static final double COMMUNITY_CARD_WIDTH_RATIO = 0.11;
    private static final double PLAYER_CARD_HEIGHT_RATIO = 0.30;
    private static final double PLAYER_CARD_WIDTH_RATIO = 0.15;
    private static final double PLAYER_CARDS_Y_OFFSET_FACTOR = 0.10;
    private static final int[] CHIP_VALUES = {
        100000, 50000, 20000, 10000,
        5000, 2000, 1000, 500,
        200, 100, 50, 20,
        10, 5, 2, 1
    };
    private static final double CHIP_HEIGHT_RATIO = 0.08;
    private static final double CHIP_WIDTH_RATIO = 0.04;
    private static final double CHIP_OPACITY_START = 0.0;
    private static final double CHIP_SCALE_START = 0.6;
    private static final double CHIP_SCALE_END = 1.0;
    private static final double CHIP_RANDOM_X_FACTOR = 20.0;
    private static final double CHIP_RANDOM_X_CENTER = 0.5;
    private static final double CHIP_RANDOM_Y_BASE = -30.0;
    private static final double CHIP_RANDOM_Y_VARIATION = 20.0;
    private static final double CHIP_TRANSLATE_RESET_X = 0.0;
    private static final double CHIP_TRANSLATE_RESET_Y = 0.0;
    private static final double CHIP_FADE_TO = 1.0;
    private static final double CHIP_SCALE_TO = 1.0;
    private static final long CHIP_FADE_DURATION_MS = 200;
    private static final long CHIP_SCALE_DURATION_MS = 220;
    private static final long CHIP_DROP_DURATION_MS = 250;
    private static final long CHIP_STAGGER_DELAY_MULTIPLIER = 35L;
    private static final double CHAT_WIDTH = 400;
    private static final double CHAT_HEIGHT = 600;

    private String chatUsername;
    private ClientService chatClientService;
    private int chatLobbyId = -1;
    private boolean chatInitialized;

    /** Standard constructor. Used by FXML. */
    public CasinoGameController() {
        // default constructor for FXML
    }

    /**
     * Generate a unique key for a given card based on its suit and value.
     *
     * @param c The card for which to generate the key.
     * @return A string key representing the card, formatted as "suit:value".
     */
    private String cardKey(Card c) {
        if (c == null) {
            return "null";
        }

        String suit = (c.getSuit() == null) ? "" : c.getSuit().toLowerCase();
        String value = (c.getValue() == null) ? "" : c.getValue().toLowerCase();
        return suit + ":" + value;
    }

    /**
     * Generate a list of unique keys for a list of cards.
     *
     * @param cards The list of cards for which to generate keys.
     * @param slots The number of slots to generate keys for.
     * @return A list of string keys representing the cards, with "null" for any empty slots.
     */
    private java.util.List<String> cardKeys(java.util.List<Card> cards, int slots) {
        java.util.List<String> keys = new java.util.ArrayList<>(slots);
        for (int i = 0; i < slots; i++) {
            Card c = (cards != null && i < cards.size()) ? cards.get(i) : null;
            keys.add(cardKey(c));
        }
        return java.util.List.copyOf(keys);
    }

    /**
     * Set the GameService for this controller. This method must be called before starting the UI to
     * ensure that the controller has access to the game logic and can update the interface
     * accordingly.
     *
     * @param gameService The GameService instance that provides access to the game state and logic.
     */
    public void setGameService(GameService gameService) {
        this.gameService = gameService;
        TaskbarController controller = resolveTaskbarController();
        if (controller != null && myPlayerId != null) {
            controller.setGameService(gameService, myPlayerId);
        }
    }

    public void startChat(ChatController chatController) {
        this.chatController = chatController;
        initializeChatIfPossible();
    }

    /** Set the PlayerId of the current player. */
    @FXML
    public void initialize() {
        LOGGER.info("INIT UI");

        taskbarController = resolveTaskbarController();
        if (taskbarController != null && gameService != null && myPlayerId != null) {
            taskbarController.setGameService(gameService, myPlayerId);
        }

        if (communityCardsBox == null) {
            LOGGER.warning("communityCardsBox is NULL");
            return;
        }

        try {

            var url = getClass().getResource(DEALER_IMAGE_PATH);

            if (url != null) {
                dealerImage = new Image(url.toExternalForm(), true);
                myDealerIcon.setImage(dealerImage);
            }

        } catch (Exception e) {
            LOGGER.warning("Dealer icon load failed");
        }

        myDealerIcon.setVisible(false);

        javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
        double screenHeight = screen.getBounds().getHeight();
        casinoTableInnerBox.setTranslateY(-screenHeight * TABLE_OFFSET_Y_FACTOR);
        playerCardsBox.setTranslateY(screenHeight * PLAYER_CARDS_OFFSET_Y_FACTOR);

        // uiTest();

        // or

        // empty display only (optional)
        renderCommunityCards(List.of());
        renderPlayerCards(List.of());

        chatContainer.toFront();
    }

    /**
     * Set the context for the chat functionality by providing the username, ClientService, and
     * lobby ID.
     *
     * @param username The username of the player, used for chat identification. Must not be null or
     *     blank.
     * @param clientService The ClientService instance used for communicating with the server. Must
     *     not be null.
     * @param lobbyId The ID of the lobby associated with the chat, used to determine the chat
     *     context. Must be a non-negative integer.
     */
    public void setChatContext(String username, ClientService clientService, int lobbyId) {
        this.chatUsername = username;
        this.chatClientService = clientService;
        this.chatLobbyId = lobbyId;
        //startChat(chatController);
    }

    /**
     * Initialize the chat UI if all necessary information (username, ClientService, and lobby ID)
     * is available and the chat has not already been initialized.
     */
    private void initializeChatIfPossible() {
        if (chatInitialized || chatClientService == null || chatUsername == null) {
            return;
        }

        try {
            ChatBoxController chatBoxController = new ChatBoxController(chatUsername, chatController);

            chatController.setChatBoxController(chatBoxController);

            URL resource = getClass().getResource("/ui-structure/components/chatui/chatbox.fxml");
            FXMLLoader loader = new FXMLLoader(resource);
            loader.setController(chatBoxController);

            chatBoxController.loadChats();

            Node chatBox = loader.load();

            chatContainer.getChildren().add(chatBox);
            /*
            Scene scene = new Scene(root);
            chatStage.setScene(scene);

            chatStage.setWidth(CHAT_WIDTH);
            chatStage.setHeight(CHAT_HEIGHT);

            chatStage.setOnCloseRequest(event -> chatInitialized = false);

            chatStage.show();
             */

            if (chatLobbyId >= 0) {
                chatController.setLobbyChat(chatLobbyId);
            }

            chatInitialized = true;

        } catch (IOException e) {
            LOGGER.warning("Could not initialize game chat UI: " + e.getMessage());
        }
    }

    // Test method to demonstrate the UI functionality with sample data.
    // @FXML
    // public void uiTest() {
    //     if (communityCardsBox == null) {
    //         LOGGER.info("communityCardsBox is NULL");
    //         return;
    //     }
    //
    //     try {
    //         renderCommunityCards(
    //                 List.of(
    //                         new Card("ace", "hearts"),
    //                         new Card("king", "spades"),
    //                         new Card("10", "diamonds")));
    //
    //         renderPlayerCards(List.of(new Card("10", "spades"), new Card("king", "spades")));
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //
    //     Player mathis = new Player(PlayerId.of("Mathis"), 20000);
    //     mathis.setState(PlayerState.ACTIVE);
    //
    //     Player jona = new Player(PlayerId.of("Jona"), 20000);
    //     jona.setState(PlayerState.FOLDED);
    //
    //     Player lars = new Player(PlayerId.of("Lars"), 20000);
    //     lars.setState(PlayerState.ACTIVE);
    //
    //     player1Controller.setPlayer(mathis);
    //     player2Controller.setPlayer(jona);
    //     player3Controller.setPlayer(lars);
    //
    //     LOGGER.info(
    //             "Player 1: "
    //             + mathis.getName()
    //             + " | $"
    //             + mathis.getChips()
    //             + " | "
    //             + mathis.getState());
    //     LOGGER.info(
    //             "Player 2: "
    //             + jona.getName()
    //             + " | $"
    //             + jona.getChips()
    //             + " | "
    //             + jona.getState());
    //     LOGGER.info(
    //             "Player 3: "
    //             + lars.getName()
    //             + " | $"
    //             + lars.getChips()
    //             + " | "
    //             + lars.getState());
    //
    //     if (playerStatusController != null) {
    //         playerStatusController.setPlayer(mathis);
    //     } else {
    //         LOGGER.warning("PlayerStatusController is NULL");
    //     }
    //
    //     javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
    //     double screenHeight = screen.getBounds().getHeight();
    //
    //     casinoTableInnerBox.setTranslateY(-screenHeight * 0.08);
    //     playerCardsBox.setTranslateY(screenHeight * 0.35);
    //
    //     renderPot(500);
    //
    //     lars.removeChips(500);
    //     LOGGER.info("Lars: $" + lars.getChips());
    //
    //     player3Controller.refresh();
    //
    //     mathis.fall();
    //     player1Controller.refresh();
    //
    //     GameState s = new GameState();
    //     s.dealer = 3;
    //     highlightDealer(s);
    //
    //     s.phase = "FLOP";
    //
    //     updateGameInfo(s);
    //
    //     s.winnerIndex = 1;
    //
    //     updateGameInfo(s);
    // }

    /** Start the UI update loop. */
    public void start() {

        if (gameService == null) {
            throw new IllegalStateException("GameService not set!");
        }

        startLoop();
    }

    /**
     * Start a loop that periodically updates the UI by fetching the latest game state from the
     * GameService.
     */
    private void startLoop() {

        timeline =
                new javafx.animation.Timeline(
                        new javafx.animation.KeyFrame(
                                javafx.util.Duration.seconds(UI_UPDATE_INTERVAL_SECONDS),
                                e -> updateUI()));

        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Stop the UI update loop, which can be called when the game ends or when the user exits the
     * game screen to prevent unnecessary updates and resource usage.
     */
    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
        if (chatController != null) {
            chatController.shutdown();
        }
    }

    /**
     * Triggers an asynchronous UI update by fetching the current game state from the GameService.
     */
    private void updateUI() {

        if (gameService == null) {
            LOGGER.warning("GameService is null, skipping update.");
            return;
        }

        CompletableFuture.supplyAsync(
                        () -> {
                            try {
                                return gameService.refresh();
                            } catch (Exception e) {
                                LOGGER.severe("refresh failed: " + e.getMessage());
                                e.printStackTrace();
                                return null;
                            }
                        })
                .thenAccept(
                        state -> {
                            if (state == null) {
                                LOGGER.info("No state received yet.");
                                return;
                            }

                            javafx.application.Platform.runLater(
                                    () -> {
                                        applyState(state);
                                    });
                        })
                .exceptionally(
                        ex -> {
                            LOGGER.severe("UI update Error: " + ex.getMessage());
                            ex.printStackTrace();
                            return null;
                        });
    }

    /**
     * Updates all visual UI components with the data from the provided GameState. This includes
     * community cards, pot, player information, and the taskbar.
     *
     * @param s The current state of the game to be rendered.
     */
    private void applyState(GameState s) {

        if (s == null) {
            return;
        }

        LOGGER.info(
                "applyState -> phase="
                        + s.phase
                        + " pot="
                        + s.pot
                        + " players="
                        + (s.players != null ? s.players.size() : 0));

        List<Player> players = (s.players != null) ? s.players : List.of();
        List<Card> community = (s.communityCards != null) ? s.communityCards : List.of();
        List<Card> myCards = getMyCards(players);

        java.util.List<String> newCommunityKeys = cardKeys(community, TOTAL_SLOTS);
        if (!newCommunityKeys.equals(lastCommunityKeys)) {
            lastCommunityKeys = newCommunityKeys;
            renderCommunityCards(community);
        }

        java.util.List<String> newMyCardKeys = cardKeys(myCards, PLAYER_SLOTS);
        if (!newMyCardKeys.equals(lastMyCardKeys)) {
            lastMyCardKeys = newMyCardKeys;
            renderPlayerCards(myCards);
        }

        if (s.pot != lastPot) {
            lastPot = s.pot;
            renderPot(s.pot);
        }

        updatePlayers(players);
        syncWhisperTargetsFromPlayers(players);
        updateGameInfo(s);
        highlightDealer(s);
        updateTaskbar(s);

        LOGGER.info("myPlayerId=" + myPlayerId);
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            LOGGER.info(
                    "state.players["
                            + i
                            + "] id="
                            + (p != null ? p.getId() : null)
                            + " chips="
                            + (p != null ? p.getChips() : null)
                            + " bet="
                            + (p != null ? p.getBet() : null)
                            + " state="
                            + (p != null ? p.getState() : null));
        }
    }

    /**
     * Synchronize the whisper chat targets with the current list of players.
     *
     * @param players The list of players currently in the game, used to update the whisper chat
     *     targets in the chat controller.
     */
    private void syncWhisperTargetsFromPlayers(List<Player> players) {
        if (chatController == null || players == null || players.isEmpty()) {
            return;
        }
        Set<String> seen = new HashSet<>();
        for (Player player : players) {
            if (player == null) {
                continue;
            }
            String name = player.getName();
            if (name != null && !name.isBlank() && seen.add(name)) {
                chatController.addWhisperUser(name);
            }
        }
    }

    /**
     * Retrieve the hole cards of the current player based on their PlayerId and the list of players
     * in the game state.
     *
     * @param players The list of players currently in the game, used to find the player matching
     *     myPlayerId and retrieve their hole cards.
     * @return A list of Card objects representing the current player's hole cards, or an empty list
     *     if the player is not found or has no cards.
     */
    private List<Card> getMyCards(List<Player> players) {

        if (myPlayerId == null || players == null) {
            return List.of();
        }

        for (Player p : players) {
            if (p.getId() != null && p.getId().equals(myPlayerId)) {
                return p.getCards() != null ? p.getCards() : List.of();
            }
        }

        return List.of();
    }

    /**
     * Update the player's hole cards based on the active player in the game state.
     *
     * @param s The current game state.
     */
    private void updatePlayerCards(GameState s) {

        if (s.players == null || myPlayerId == null) {
            renderPlayerCards(List.of());
            return;
        }

        for (Player p : s.players) {
            if (p.getId().equals(myPlayerId)) {
                renderPlayerCards(p.getCards());
                return;
            }
        }

        // fallback
        renderPlayerCards(List.of());
    }

    /**
     * Update the game information display, such as the current phase and the winner if the hand has
     * ended.
     *
     * @param s The current game state.
     */
    private void updateGameInfo(GameState s) {

        String text = "Phase: " + s.phase;

        if (s.winnerIndex >= 0 && s.winnerIndex < s.players.size()) {
            Player winner = s.players.get(s.winnerIndex);
            text = "Winner: " + winner.getName();
        }

        tableText.setText(text);
    }

    /**
     * Update the taskbar with the current game state and the player's ID.
     *
     * @param s The current game state.
     */
    private void updateTaskbar(GameState s) {
        TaskbarController controller = resolveTaskbarController();
        if (controller == null || myPlayerId == null) {
            return;
        }
        if (gameService != null) {
            controller.setGameService(gameService, myPlayerId);
        }
        controller.update(s, myPlayerId);
    }

    /**
     * Update the opponent player slots based on the list of players in the game state. This method
     * identifies the opponents by comparing their PlayerIds with myPlayerId and updates the
     * corresponding PlayerStatusControllers for each opponent slot.
     *
     * @param players The list of players currently in the game, used to determine which players are
     *     opponents and update their display in the UI accordingly. If the list is null or empty,
     *     all opponent slots will be cleared.
     */
    private void updatePlayers(List<Player> players) {
        if (players == null || players.isEmpty()) {
            clearOpponentSlots();
            return;
        }

        java.util.List<Player> opponents = buildOpponents(players);
        boolean meFound = opponents.size() != players.size();

        LOGGER.info(
                "updatePlayers: total="
                        + players.size()
                        + " myPlayerId="
                        + myPlayerId
                        + " meFound="
                        + meFound
                        + " opponents="
                        + opponents.size());

        setOpponent(player1Controller, opponents, 0);
        setOpponent(player2Controller, opponents, 1);
        setOpponent(player3Controller, opponents, 2);

        safeRefresh(player1Controller);
        safeRefresh(player2Controller);
        safeRefresh(player3Controller);
    }

    /**
     * Build a list of opponent players by filtering out the current player (identified by
     * myPlayerId) from the provided list of players. If the current player is not found in the
     * list, it assumes all players are opponents and returns the original list.
     *
     * @param players The list of players to filter, which may include the current player and their
     *     opponents. This list is typically obtained from the game state and may be null or empty,
     *     in which case an empty list of opponents will be returned.
     * @return A list of Player objects representing the opponents, excluding the current player if
     *     found. If the current player is not found, the original list of players is returned as
     *     opponents.
     */
    private java.util.List<Player> buildOpponents(List<Player> players) {
        java.util.List<Player> opponents = new java.util.ArrayList<>();
        boolean meFound = false;

        for (Player p : players) {
            if (p == null) {
                continue;
            }

            PlayerId pid = p.getId();
            boolean isMe = myPlayerId != null && pid != null && pid.equals(myPlayerId);

            if (isMe) {
                meFound = true;
            } else {
                opponents.add(p);
            }
        }

        if (!meFound) {
            opponents.clear();
            opponents.addAll(players);
        }

        return opponents;
    }

    /**
     * Set the opponent player information in the specified PlayerStatusController based on the
     * provided list of opponents and the index of the opponent to display.
     *
     * @param slot The PlayerStatusController instance representing the opponent slot in the UI,
     *     which will be updated with the player information if available.
     * @param list The list of opponent players to choose from, which is typically built by
     *     filtering the game state's player list to exclude the current player.
     * @param index The index of the opponent in the list to display in the specified slot.
     */
    private void setOpponent(PlayerStatusController slot, java.util.List<Player> list, int index) {
        if (slot == null) {
            return;
        }

        slot.setPlayer(index < list.size() ? list.get(index) : null);
    }

    /**
     * Safely refresh the PlayerStatusController by calling its refresh method, while catching and
     * ignoring any exceptions that may occur during the refresh process.
     *
     * @param c The PlayerStatusController instance to refresh, which may be null.
     */
    private void safeRefresh(PlayerStatusController c) {
        if (c == null) {
            return;
        }

        try {
            c.refresh();
        } catch (Exception ignored) {
        }
    }

    /**
     * Clear all opponent slots in the UI by setting their player information to null and refreshing
     * their display.
     */
    private void clearOpponentSlots() {
        if (player1Controller != null) {
            player1Controller.setPlayer(null);
        }

        if (player2Controller != null) {
            player2Controller.setPlayer(null);
        }

        if (player3Controller != null) {
            player3Controller.setPlayer(null);
        }

        safeRefresh(player1Controller);
        safeRefresh(player2Controller);
        safeRefresh(player3Controller);
    }

    /**
     * Highlight the dealer in the UI based on the dealer index in the game state.
     *
     * @param s The current game state containing the dealer index.
     */
    private void highlightDealer(GameState s) {
        if (player1Controller != null) {
            player1Controller.setDealer(false);
        }

        if (player2Controller != null) {
            player2Controller.setDealer(false);
        }

        if (player3Controller != null) {
            player3Controller.setDealer(false);
        }

        myDealerIcon.setVisible(false);

        if (s == null || s.players == null || s.players.isEmpty()) {
            return;
        }
        if (s.dealer < 0 || s.dealer >= s.players.size()) {
            return;
        }

        Player dealerPlayer = s.players.get(s.dealer);
        if (dealerPlayer == null || dealerPlayer.getId() == null) {
            return;
        }

        if (myPlayerId != null && myPlayerId.equals(dealerPlayer.getId())) {
            myDealerIcon.setVisible(true);
            return;
        }

        java.util.List<Player> opponents = buildOpponents(s.players);
        if (opponents.size() > 0
                && samePlayer(opponents.get(0), dealerPlayer)
                && player1Controller != null) {
            player1Controller.setDealer(true);
        }
        if (opponents.size() > 1
                && samePlayer(opponents.get(1), dealerPlayer)
                && player2Controller != null) {
            player2Controller.setDealer(true);
        }
        if (opponents.size() > 2
                && samePlayer(opponents.get(2), dealerPlayer)
                && player3Controller != null) {
            player3Controller.setDealer(true);
        }
    }

    /**
     * Compare two Player objects to determine if they represent the same player based on their
     * PlayerIds. This method checks for null values and compares the IDs for equality.
     *
     * @param a The first Player object to compare, which may be null.
     * @param b The second Player object to compare, which may be null.
     * @return true if both Player objects are non-null and have the same non-null PlayerId, false
     *     otherwise.
     */
    private boolean samePlayer(Player a, Player b) {
        if (a == null || b == null || a.getId() == null || b.getId() == null) {
            return false;
        }
        return a.getId().equals(b.getId());
    }

    /**
     * Render the community cards on the table based on the list of cards provided.
     *
     * @param cards The list of community cards to display.
     */
    private void renderCommunityCards(List<Card> cards) {

        communityCardsBox.getChildren().clear();

        if (cards == null) {
            cards = List.of();
        }

        for (int i = 0; i < TOTAL_SLOTS; i++) {

            ImageView view = new ImageView();
            styleCommunityCard(view);

            Image image;

            if (i < cards.size() && cards.get(i) != null) {
                image = loadImageSafe(mapCardToImage(cards.get(i)));
            } else {
                image = loadImageSafe(BACKSIDE);
            }

            view.setImage(image);

            view.setOnMouseEntered(e -> animateCardHover(view, true));
            view.setOnMouseExited(e -> animateCardHover(view, false));

            view.setOpacity(CARD_START_OPACITY);

            view.setScaleX(CARD_START_SCALE);
            view.setScaleY(CARD_START_SCALE);

            communityCardsBox.getChildren().add(view);

            animateCardAppear(view, i);
        }
    }

    /**
     * Render the player's hole cards based on the list of cards provided.
     *
     * @param cards The list of hole cards to display for the player.
     */
    private void renderPlayerCards(List<Card> cards) {

        playerCardsBox.getChildren().clear();

        playerCardsBox.getChildren().add(myDealerIcon);

        if (cards == null) {
            cards = List.of();
        }

        for (int i = 0; i < PLAYER_SLOTS; i++) {

            ImageView view = new ImageView();
            stylePlayerCard(view);

            Image image;

            if (i < cards.size() && cards.get(i) != null) {
                image = loadImageSafe(mapCardToImage(cards.get(i)));
            } else {
                image = loadImageSafe(BACKSIDE);
            }

            view.setImage(image);

            view.setOnMouseEntered(e -> animateCardHover(view, true));
            view.setOnMouseExited(e -> animateCardHover(view, false));

            view.setOpacity(CARD_START_OPACITY);

            view.setScaleX(CARD_START_SCALE);
            view.setScaleY(CARD_START_SCALE);

            playerCardsBox.getChildren().add(view);

            animateCardAppear(view, i);
        }
    }

    /**
     * Load an image from the given path safely, falling back to a default backside image if the
     * specified image cannot be found.
     *
     * @param path The path to the image resource to load.
     * @return The loaded Image object.
     */
    private Image loadImageSafe(String path) {

        var stream = getClass().getResourceAsStream(path);

        if (stream == null) {
            LOGGER.warning("IMAGE NOT FOUND: " + path);
            stream = getClass().getResourceAsStream(BACKSIDE);
        }

        return new Image(stream);
    }

    /**
     * Apply styling to the given ImageView for community cards, including CSS classes and size
     * bindings.
     *
     * @param view The ImageView to style as a community card.
     */
    private void styleCommunityCard(ImageView view) {

        view.getStyleClass().add("community-card");

        view.setPreserveRatio(true);
        view.setSmooth(true);

        javafx.scene.Scene scene = communityCardsBox.getScene();

        if (scene != null) {
            bindCommunityCardSize(view, scene);
        } else {
            communityCardsBox
                    .sceneProperty()
                    .addListener(
                            (obs, oldScene, newScene) -> {
                                if (newScene != null) {
                                    bindCommunityCardSize(view, newScene);
                                }
                            });
        }
    }

    /**
     * Apply styling to the given ImageView for player hole cards, including CSS classes and size
     * bindings.
     *
     * @param view The ImageView to style as a player hole card.
     */
    private void stylePlayerCard(ImageView view) {

        view.getStyleClass().add("player-card");

        view.setPreserveRatio(true);
        view.setSmooth(true);

        javafx.scene.Scene scene = playerCardsBox.getScene();

        if (scene != null) {
            bindPlayerCardSize(view, scene);
        } else {
            playerCardsBox
                    .sceneProperty()
                    .addListener(
                            (obs, oldScene, newScene) -> {
                                if (newScene != null) {
                                    bindPlayerCardSize(view, newScene);
                                }
                            });
        }
    }

    /**
     * Bind the size of the given ImageView to the scene dimensions for community cards.
     *
     * @param view The ImageView representing a community card whose size should be bound to the
     *     scene dimensions.
     * @param scene The JavaFX Scene to which the ImageView belongs, used for binding the size
     *     properties.
     */
    private void bindCommunityCardSize(ImageView view, javafx.scene.Scene scene) {

        view.fitHeightProperty().bind(scene.heightProperty().multiply(COMMUNITY_CARD_HEIGHT_RATIO));

        view.fitWidthProperty().bind(scene.widthProperty().multiply(COMMUNITY_CARD_WIDTH_RATIO));
    }

    /**
     * Bind the size of the given ImageView to the scene dimensions for player hole cards.
     *
     * @param view The ImageView representing a player hole card whose size should be bound to the
     *     scene dimensions.
     * @param scene The JavaFX Scene to which the ImageView belongs, used for binding the size
     *     properties.
     */
    private void bindPlayerCardSize(ImageView view, javafx.scene.Scene scene) {

        view.fitHeightProperty().bind(scene.heightProperty().multiply(PLAYER_CARD_HEIGHT_RATIO));

        view.fitWidthProperty().bind(scene.widthProperty().multiply(PLAYER_CARD_WIDTH_RATIO));
    }

    /**
     * Animate the appearance of a card by applying a sequence of transitions, including movement,
     * fading, scaling, and settling.
     *
     * @param view The ImageView representing the card to animate.
     * @param index The index of the card in the display order.
     */
    private void animateCardAppear(ImageView view, int index) {

        javafx.animation.TranslateTransition move =
                new javafx.animation.TranslateTransition(
                        javafx.util.Duration.millis(ANIMATION_DURATION_MS), view);

        move.setFromX(MOVE_FROM_X);
        move.setToX(MOVE_TO_X);

        move.setFromY(MOVE_FROM_Y);
        move.setToY(MOVE_TO_Y);

        move.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        javafx.animation.FadeTransition fade =
                new javafx.animation.FadeTransition(
                        javafx.util.Duration.millis(ANIMATION_DURATION_MS), view);

        fade.setFromValue(FADE_FROM);
        fade.setToValue(FADE_TO);

        javafx.animation.ScaleTransition scale =
                new javafx.animation.ScaleTransition(
                        javafx.util.Duration.millis(ANIMATION_DURATION_MS), view);

        scale.setFromX(SCALE_FROM);
        scale.setFromY(SCALE_FROM);
        scale.setToX(SCALE_TO);
        scale.setToY(SCALE_TO);
        scale.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        javafx.animation.TranslateTransition settle =
                new javafx.animation.TranslateTransition(
                        javafx.util.Duration.millis(SETTLE_DURATION_MS), view);

        settle.setFromY(SETTLE_FROM_Y);
        settle.setToY(SETTLE_TO_Y);
        settle.setAutoReverse(SETTLE_AUTO_REVERSE);
        settle.setCycleCount(SETTLE_CYCLE_COUNT);

        javafx.animation.SequentialTransition st =
                new javafx.animation.SequentialTransition(
                        new javafx.animation.ParallelTransition(move, fade, scale), settle);

        st.setDelay(javafx.util.Duration.millis(index * STAGGER_DELAY_MS));

        st.play();
    }

    /**
     * Animate a card hover effect by scaling and lifting the card when hovered and resetting it
     * when not hovered.
     *
     * @param view The ImageView representing the card to animate on hover.
     * @param hover A boolean indicating whether the card is being hovered (true) or not (false).
     */
    private void animateCardHover(ImageView view, boolean hover) {

        double scale = hover ? HOVER_SCALE_ON : HOVER_SCALE_OFF;
        double lift = hover ? HOVER_LIFT_ON : HOVER_LIFT_OFF;

        javafx.animation.ScaleTransition st =
                new javafx.animation.ScaleTransition(
                        javafx.util.Duration.millis(HOVER_DURATION_MS), view);
        st.setToX(scale);
        st.setToY(scale);

        javafx.animation.TranslateTransition tt =
                new javafx.animation.TranslateTransition(
                        javafx.util.Duration.millis(HOVER_DURATION_MS), view);
        tt.setToY(lift);

        st.play();
        tt.play();
    }

    /**
     * Position the player's hole cards box on the screen based on a predefined offset factor
     * relative to the screen height.
     */
    private void positionPlayerCards() {

        javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
        double screenHeight = screen.getBounds().getHeight();

        playerCardsBox.setTranslateY(screenHeight * PLAYER_CARDS_Y_OFFSET_FACTOR);
    }

    /**
     * Map a Card object to the corresponding image path based on its suit and value.
     *
     * @param card The Card object to be mapped to an image path.
     * @return The string path to the image representing the given card.
     */
    private String mapCardToImage(Card card) {

        if (card == null) {
            return BACKSIDE;
        }

        String rawSuit = card.getSuit();
        String rawValue = card.getValue();
        if (rawSuit == null || rawValue == null) {
            return BACKSIDE;
        }

        String suit =
                switch (rawSuit.trim().toLowerCase()) {
                    case "h", "hearts", "heart" -> "heart";
                    case "d", "diamonds", "diamond" -> "diamond";
                    case "c", "clubs", "club", "cross" -> "cross";
                    case "s", "spades", "spade", "pik" -> "pik";
                    default -> rawSuit.trim().toLowerCase();
                };

        String value =
                switch (rawValue.trim().toLowerCase()) {
                    case "a", "ace" -> "ace";
                    case "k", "king" -> "king";
                    case "q", "queen" -> "queen";
                    case "j", "jack" -> "jack";
                    case "10", "t" -> "10";
                    default -> rawValue.trim().toLowerCase();
                };

        String path = "/images/card-" + suit + "-" + value + "-3.png";

        // LOGGER.info("CARD PATH: " + path);

        return path;
    }

    /**
     * Render the pot by calculating the required chips based on the pot amount and displaying them
     * with animations.
     *
     * @param pot The total amount in the pot that needs to be represented with chips on the UI.
     */
    private void renderPot(int pot) {

        potBox.getChildren().clear();

        int remaining = pot;

        int index = 0;

        for (int chipValue : CHIP_VALUES) {

            while (remaining >= chipValue) {

                ImageView chip =
                        new ImageView(loadImageSafe("/images/chip-" + chipValue + "-5.png"));

                if (chip.getImage() == null) {
                    LOGGER.warning("Missing chip image: " + chipValue);
                    break;
                }

                styleChip(chip);

                potBox.getChildren().add(chip);

                animateChipAppear(chip, index);

                remaining -= chipValue;
                index++;
            }
        }

        // LOGGER.info("POT RENDERED: " + pot + " -> remaining: " + remaining);
    }

    /**
     * Apply styling to the given ImageView for chips, including CSS classes and size bindings.
     *
     * @param view The ImageView to style as a chip in the pot display.
     */
    private void styleChip(ImageView view) {

        view.getStyleClass().add("chips-icon");

        view.setPreserveRatio(true);
        view.setSmooth(true);

        javafx.scene.Scene scene = potBox.getScene();

        if (scene != null) {
            bindChipSize(view, scene);
        } else {
            potBox.sceneProperty()
                    .addListener(
                            (obs, oldScene, newScene) -> {
                                if (newScene != null) {
                                    bindChipSize(view, newScene);
                                }
                            });
        }
    }

    /**
     * Bind the size of the given ImageView to the scene dimensions for chips in the pot display.
     *
     * @param view The ImageView representing a chip whose size should be bound to the scene
     *     dimensions.
     * @param scene The JavaFX Scene to which the ImageView belongs, used for binding the size
     *     properties.
     */
    private void bindChipSize(ImageView view, javafx.scene.Scene scene) {

        view.fitHeightProperty()
                .bind(
                        scene.heightProperty().multiply(CHIP_HEIGHT_RATIO) // kleiner als Karten
                        );

        view.fitWidthProperty().bind(scene.widthProperty().multiply(CHIP_WIDTH_RATIO));
    }

    /**
     * Animate the appearance of a chip by applying a sequence of transitions, including random
     * initial positioning, fading, scaling, and dropping into place.
     *
     * @param view The ImageView representing the chip to animate.
     * @param index The index of the chip in the display order, used to stagger the animation timing
     *     for multiple chips.
     */
    private void animateChipAppear(ImageView view, int index) {

        view.setOpacity(CHIP_OPACITY_START);
        view.setScaleX(CHIP_SCALE_START);
        view.setScaleY(CHIP_SCALE_START);

        double randomX = (Math.random() - CHIP_RANDOM_X_CENTER) * CHIP_RANDOM_X_FACTOR;
        double randomY = CHIP_RANDOM_Y_BASE - Math.random() * CHIP_RANDOM_Y_VARIATION;

        view.setTranslateX(randomX);
        view.setTranslateY(randomY);

        javafx.animation.FadeTransition fade =
                new javafx.animation.FadeTransition(
                        javafx.util.Duration.millis(CHIP_FADE_DURATION_MS), view);
        fade.setToValue(CHIP_FADE_TO);

        javafx.animation.ScaleTransition scale =
                new javafx.animation.ScaleTransition(
                        javafx.util.Duration.millis(CHIP_SCALE_DURATION_MS), view);
        scale.setToX(CHIP_SCALE_TO);
        scale.setToY(CHIP_SCALE_TO);

        javafx.animation.TranslateTransition drop =
                new javafx.animation.TranslateTransition(
                        javafx.util.Duration.millis(CHIP_DROP_DURATION_MS), view);
        drop.setToX(CHIP_TRANSLATE_RESET_X);
        drop.setToY(CHIP_TRANSLATE_RESET_Y);

        javafx.animation.SequentialTransition st =
                new javafx.animation.SequentialTransition(fade, scale, drop);

        st.setDelay(javafx.util.Duration.millis(index * CHIP_STAGGER_DELAY_MULTIPLIER));

        st.play();
    }

    /**
     * Set the PlayerId of the current player, which is used to identify the player's hole cards and
     * update the taskbar with the correct player information.
     *
     * @param id The PlayerId of the current player, which should match the PlayerId in the game
     *     state for the player's own information to be displayed correctly.
     */
    public void setMyPlayerId(PlayerId id) {
        this.myPlayerId = id;
        TaskbarController controller = resolveTaskbarController();
        if (controller != null && gameService != null && myPlayerId != null) {
            controller.setGameService(gameService, myPlayerId);
        }
    }

    /**
     * Resolve the TaskbarController instance to be used for updating the taskbar with game
     * information.
     *
     * @return The TaskbarController instance to use for taskbar updates, which may be the directly
     *     injected taskbarController or the one obtained from the taskbarIncludeController.
     */
    private TaskbarController resolveTaskbarController() {
        if (taskbarController != null) {
            return taskbarController;
        }
        if (taskbarIncludeController != null) {
            taskbarController = taskbarIncludeController;
            return taskbarController;
        }
        return null;
    }

}
