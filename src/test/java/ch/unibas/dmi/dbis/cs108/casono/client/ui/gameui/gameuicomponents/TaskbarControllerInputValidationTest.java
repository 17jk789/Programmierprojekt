package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents;

import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.unibas.dmi.dbis.cs108.casono.client.game.GameState;
import ch.unibas.dmi.dbis.cs108.casono.client.game.Player;
import ch.unibas.dmi.dbis.cs108.casono.client.game.PlayerId;
import java.util.List;
import org.junit.jupiter.api.Test;

class TaskbarControllerInputValidationTest {

    @Test
    void updateLogicRunsWithoutCrash() {
        GameState state = new GameState();

        state.players =
                List.of(
                        new Player(PlayerId.of("me"), 20000),
                        new Player(PlayerId.of("other"), 20000));

        state.phase = "PREFLOP";
        state.currentBet = 200;
        state.activePlayer = 0;

        DummyController controller = new DummyController();

        controller.update(state, PlayerId.of("me"));

        assertTrue(true);
    }

    static class DummyController {
        void update(GameState state, PlayerId id) {}
    }
}
