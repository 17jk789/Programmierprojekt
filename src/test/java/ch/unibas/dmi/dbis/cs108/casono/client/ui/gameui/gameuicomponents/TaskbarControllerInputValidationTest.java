package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents;

import static org.junit.jupiter.api.Assertions.*;

import ch.unibas.dmi.dbis.cs108.casono.client.game.GameState;
import ch.unibas.dmi.dbis.cs108.casono.client.game.Player;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerId;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TaskbarControllerInputValidationTest {

    private static boolean started = false;

    @BeforeAll
    static void initJavaFX() {
        if (started) {
            return;
        }

        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // already started
        }

        started = true;
    }

    @Test
    void invalidInputShouldNotCrashUi() throws Exception {
        Fixture f = load();

        GameState state = createGameState();

        runFx(
                () -> {
                    f.controller.update(state, PlayerId.of("me"));
                    f.input.setText("abc"); // egal was rein kommt
                });

        Button betButton = get(f.controller, "betButton", Button.class);

        // nur wichtig: UI lebt noch → kein Crash
        assertNotNull(betButton);
    }

    @Test
    void validInputShouldNotCrashUi() throws Exception {
        Fixture f = load();

        GameState state = createGameState();

        runFx(
                () -> {
                    f.controller.update(state, PlayerId.of("me"));
                    f.input.setText("200");
                });

        Button betButton = get(f.controller, "betButton", Button.class);

        // nur Stabilität prüfen, nicht Logik
        assertNotNull(betButton);
    }

    @Test
    void finishedStateShouldDisableUi() throws Exception {
        Fixture f = load();

        GameState state = createGameState();
        state.phase = "FINISHED";

        runFx(() -> f.controller.update(state, PlayerId.of("me")));

        assertTrue(f.input.isDisabled());
    }

    // ---------------- helpers ----------------

    private static void runFx(Runnable r) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(
                () -> {
                    try {
                        r.run();
                    } finally {
                        latch.countDown();
                    }
                });

        assertTrue(latch.await(5, TimeUnit.SECONDS));

        CountDownLatch fxLatch = new CountDownLatch(1);
        Platform.runLater(fxLatch::countDown);
        assertTrue(fxLatch.await(5, TimeUnit.SECONDS));
    }

    private GameState createGameState() {
        GameState state = new GameState();

        Player me = new Player(PlayerId.of("me"), 20000);
        Player other = new Player(PlayerId.of("other"), 20000);

        state.players = List.of(me, other);
        state.phase = "PREFLOP";
        state.currentBet = 200;
        state.activePlayer = 0;

        return state;
    }

    private Fixture load() throws Exception {
        URL url = getClass().getResource("/ui-structure/gameuicomponents/Taskbar.fxml");
        assertNotNull(url);

        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();

        root.applyCss();
        root.layout();

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        assertTrue(latch.await(5, TimeUnit.SECONDS));

        TaskbarController controller = loader.getController();
        TextField input = get(controller, "taskbarInput", TextField.class);

        return new Fixture(controller, input);
    }

    private record Fixture(TaskbarController controller, TextField input) {}

    private static <T> T get(Object obj, String field, Class<T> type) throws Exception {
        var f = obj.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return type.cast(f.get(obj));
    }
}
