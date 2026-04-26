package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents;

import static org.junit.jupiter.api.Assertions.*;

import ch.unibas.dmi.dbis.cs108.casono.client.game.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

/**
 * TODO: Refactor UI tests to avoid reflection and test behavior via public APIs or real UI actions
 * (preferably using TestFX) for better maintainability and robustness.
 *
 * <p>These tests verify: - input validation behavior - UI state updates based on game state -
 * correct enabling/disabling of action buttons
 */
class TaskbarControllerInputValidationTest {

    private static boolean fxStarted = false;

    /** Initializes JavaFX once before all tests. */
    @BeforeAll
    static void initJavaFX() throws Exception {
        if (fxStarted) {
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);

        try {
            Platform.startup(
                    () -> {
                        Platform.setImplicitExit(false);
                        latch.countDown();
                    });
        } catch (IllegalStateException ignored) {
            latch.countDown();
        }

        latch.await(10, TimeUnit.SECONDS);
        fxStarted = true;
    }

    /** Ensures invalid input values do not enable the BET button. */
    @Test
    void invalidInputShouldBeRejected() throws Exception {
        Fixture f = load();

        String[] invalidInputs = {"", "abc", "-1", "999999999999"};

        for (String input : invalidInputs) {
            setText(f.input, input);

            invokePrivate(f.controller, "refreshBetInputUi");

            Button bet = get(f.controller, "betButton", Button.class);

            assertFalse(bet.isVisible(), "Invalid input must not enable BET button: " + input);
        }
    }

    /** Ensures valid input enables the BET button. */
    @Test
    void validInputShouldEnableBet() throws Exception {
        Fixture f = load();

        GameState state = createGameState();

        setField(f.controller, "myPlayerId", PlayerId.of("me"));
        setField(f.controller, "lastState", state);
        setField(f.controller, "inputActionAllowed", true);

        f.controller.update(state, PlayerId.of("me"));

        setText(f.input, "200");

        invokePrivate(f.controller, "refreshBetInputUi");

        Button bet = get(f.controller, "betButton", Button.class);

        assertTrue(bet.isVisible(), "BET button should be enabled for valid input");
    }

    /** Ensures all UI actions are disabled when the game is finished. */
    @Test
    void finishedStateShouldDisableUi() throws Exception {
        Fixture f = load();

        GameState state = createGameState();
        state.phase = "FINISHED";

        f.controller.update(state, PlayerId.of("me"));

        assertTrue(f.input.isDisabled());
        assertTrue(get(f.controller, "callButton", Button.class).isDisabled());
        assertTrue(get(f.controller, "foldButton", Button.class).isDisabled());
        assertTrue(get(f.controller, "raiseButton", Button.class).isDisabled());
    }

    /** Creates a minimal game state for testing. */
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

    /** Loads the JavaFX controller and its UI components. */
    private Fixture load() throws Exception {
        URL url = getClass().getResource("/ui-structure/gameuicomponents/Taskbar.fxml");
        assertNotNull(url);

        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();

        root.applyCss();
        root.layout();

        TaskbarController controller = loader.getController();

        TextField input = get(controller, "taskbarInput", TextField.class);
        Button bet = get(controller, "betButton", Button.class);

        return new Fixture(controller, input, bet);
    }

    private record Fixture(TaskbarController controller, TextField input, Button betButton) {}

    private static void setText(TextField field, String value) {
        field.setText(value);
    }

    private static void invokePrivate(Object obj, String method) throws Exception {
        Method m = obj.getClass().getDeclaredMethod(method);
        m.setAccessible(true);
        m.invoke(obj);
    }

    private static <T> T get(Object obj, String field, Class<T> type) throws Exception {
        Field f = obj.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return type.cast(f.get(obj));
    }

    /** Sets a private field value using reflection. */
    private static void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    /** Gets a private field value using reflection. */
    // private static <T> T getField(Object obj, String fieldName, Class<T> type) throws Exception {
    //    Field field = obj.getClass().getDeclaredField(fieldName);
    //    field.setAccessible(true);
    //    return type.cast(field.get(obj));
    // }
}
